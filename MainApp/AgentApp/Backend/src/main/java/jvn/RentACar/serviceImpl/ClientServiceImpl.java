package jvn.RentACar.serviceImpl;

import jvn.RentACar.client.ClientClient;
import jvn.RentACar.common.RandomPasswordGenerator;
import jvn.RentACar.dto.soap.client.*;
import jvn.RentACar.enumeration.ClientStatus;
import jvn.RentACar.exceptionHandler.InvalidClientDataException;
import jvn.RentACar.exceptionHandler.InvalidTokenException;
import jvn.RentACar.exceptionHandler.InvalidUserDataException;
import jvn.RentACar.mapper.ClientDetailsMapper;
import jvn.RentACar.model.*;
import jvn.RentACar.repository.ClientRepository;
import jvn.RentACar.repository.VerificationTokenRepository;
import jvn.RentACar.service.ClientService;
import jvn.RentACar.service.EmailNotificationService;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private ClientRepository clientRepository;

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    private EmailNotificationService emailNotificationService;

    private Environment environment;

    private VerificationTokenRepository verificationTokenRepository;

    private ClientClient clientClient;

    private ClientDetailsMapper clientDetailsMapper;

    private LogService logService;

    @Override
    public Client create(Client client) throws NoSuchAlgorithmException {
        CheckClientPersonalInfoResponse response = clientClient.checkClientPersonalInfo(client.getEmail(),
                client.getPhoneNumber());
        String dataValid = response.getDataValid();
        if (dataValid.equals("EMAIL_NOT_VALID")) {
            throw new InvalidClientDataException("Client with same email address already exists.",
                    HttpStatus.BAD_REQUEST);
        }
        if (dataValid.equals("PHONE_NUMBER_NOT_VALID")) {
            throw new InvalidClientDataException("Client with same phone number already exists.",
                    HttpStatus.BAD_REQUEST);
        }

        Role role = userService.findRoleByName("ROLE_CLIENT");
        client.setRole(role);

        if (client.getPassword() == null) {
            RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
            String generatedPassword = randomPasswordGenerator.generatePassword();
            client.setPassword(passwordEncoder.encode(generatedPassword));
            client.setStatus(ClientStatus.NEVER_LOGGED_IN);

            composeAndSendEmailToChangePassword(client.getEmail(), generatedPassword);
            Client dbClient = clientRepository.save(client);
            String password = dbClient.getPassword();
            dbClient.setPassword(null);
            CreateOrEditClientResponse responseSave = clientClient.createOrEdit(dbClient);
            dbClient.setPassword(password);
            dbClient.setMainAppId(responseSave.getClientDetails().getId());
            return clientRepository.save(dbClient);
        } else {
            client.setPassword(passwordEncoder.encode(client.getPassword()));
            client.setStatus(ClientStatus.APPROVED);
            Client savedClient = clientRepository.save(client);

            VerificationToken verificationToken = new VerificationToken(savedClient);
            String nonHashedToken = verificationToken.getToken();
            verificationToken.setToken(getTokenHash(nonHashedToken));

            VerificationToken dbToken = verificationTokenRepository.findByToken(verificationToken.getToken());
            while (dbToken != null) {
                verificationToken = new VerificationToken(savedClient);
                dbToken = verificationTokenRepository.findByToken(verificationToken.getToken());
            }
            verificationTokenRepository.save(verificationToken);

            composeAndSendEmailToActivate(client.getEmail(), nonHashedToken);
            CreateOrEditClientResponse responseSave = clientClient.createOrEdit(savedClient);
            savedClient.setMainAppId(responseSave.getClientDetails().getId());
            return clientRepository.save(savedClient);
        }
    }

    @Override
    public Client get(Long id) {
        Client client = clientRepository.findOneById(id);
        if (client == null) {
            throw new InvalidClientDataException("This client doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return client;
    }

    @Override
    public Client getByMainAppId(Long mainAppId) {
        Client client = clientRepository.findByMainAppId(mainAppId);
        if (client == null) {
            throw new InvalidClientDataException("This client doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return client;
    }

    @Override
    public List<Client> get() {
        synchronize();
        return clientRepository.findAllByStatusNot(ClientStatus.DELETED);
    }

    @Override
    public Client edit(Long id, Client client) {
        synchronize();
        Client dbClient = get(id);
        if (!dbClient.getPhoneNumber().equals(client.getPhoneNumber())) {
            CheckClientPersonalInfoResponse response = clientClient.checkClientPersonalInfo(client.getEmail(),
                    client.getPhoneNumber());
            String dataValid = response.getDataValid();
            if (dataValid.equals("PHONE_NUMBER_NOT_VALID")) {
                throw new InvalidClientDataException("Client with same phone number already exists.",
                        HttpStatus.BAD_REQUEST);
            }
        }
        if (clientRepository.findByPhoneNumberAndIdNot(client.getPhoneNumber(), id) != null) {
            throw new InvalidClientDataException("Client with same phone number already exists.",
                    HttpStatus.BAD_REQUEST);
        }
        dbClient.setPhoneNumber(client.getPhoneNumber());
        dbClient.setName(client.getName());
        dbClient.setAddress(client.getAddress());
        dbClient = clientRepository.save(dbClient);
        clientClient.createOrEdit(dbClient);
        return dbClient;
    }

    @Override
    public void delete(Long id) {
        Client dbClient = get(id);
        DeleteClientDetailsResponse response = clientClient.checkAndDeleteIfCan(dbClient);
        if (response == null || !response.isCanDelete()) {
            throw new InvalidClientDataException("You can not delete this client.", HttpStatus.BAD_REQUEST);
        }
        dbClient.setStatus(ClientStatus.DELETED);
        clientRepository.save(dbClient);
    }

    @Override
    public Client activateAccount(String token) throws NoSuchAlgorithmException {
        VerificationToken verificationToken = verificationTokenRepository
                .findByTokenAndExpiryDateTimeAfter(getTokenHash(token), LocalDateTime.now());
        if (verificationToken == null) {
            throw new InvalidTokenException("This activation link is invalid or expired.", HttpStatus.BAD_REQUEST);
        }
        Client client = get(verificationToken.getClient().getId());
        client.setStatus(ClientStatus.ACTIVE);
        verificationTokenRepository.deleteById(verificationToken.getId());

        return clientRepository.save(client);
    }

    private String getTokenHash(String token) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.reset();
        digest.update(token.getBytes());
        return String.format("%040x", new BigInteger(1, digest.digest()));
    }

    private void composeAndSendEmailToActivate(String recipientEmail, String token) {
        String subject = "Activate your account";
        StringBuilder sb = new StringBuilder();
        sb.append("You have successfully registered to the Rent-a-Car website.");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("To activate your account click the following link:");
        sb.append(System.lineSeparator());
        sb.append(getLocalhostURL());
        sb.append("activate-account?t=" + token);
        String text = sb.toString();

        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    private void composeAndSendEmailToChangePassword(String recipientEmail, String generatedPassword) {
        String subject = "Activate your account";
        StringBuilder sb = new StringBuilder();
        sb.append("An account for you on Rent-a-Car website has been created.");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("You can log in using your email address and the following password: ");
        sb.append(generatedPassword);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(
                "Because of the security protocol, you will have to change this given password the first time you log in.");
        sb.append(System.lineSeparator());
        sb.append("To do that click the following link:");
        sb.append(System.lineSeparator());
        sb.append(getLocalhostURL());
        sb.append("change-password");
        String text = sb.toString();

        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    @Scheduled(cron = "0 20 0/3 * * ?")
    public void synchronize() {
        try {
            GetAllClientDetailsResponse response = clientClient.getAll();
            if (response == null) {
                return;
            }
            List<ClientDetails> clientDetails = response.getClientDetails();
            if (clientDetails == null || clientDetails.isEmpty()) {
                return;
            }

            for (ClientDetails current : clientDetails) {
                Client currentClient = clientDetailsMapper.toEntity(current);

                try {
                    User dbUser = userService.getByMainAppId(currentClient.getMainAppId());
                    if (dbUser == null) {
                        createSynchronize(currentClient);
                    } else {
                        if (dbUser instanceof Client) {
                            Client dbClient = (Client) dbUser;
                            editSynchronize(currentClient, dbClient);
                        }
                    }
                } catch (InvalidUserDataException e) {
                    createSynchronize(currentClient);
                }
            }
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SYN",
                    "[SOAP] Clients are successfully synchronized"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSynchronize(Client client) {
        Role role = userService.findRoleByName("ROLE_CLIENT");
        client.setRole(role);
        clientRepository.saveAndFlush(client);
    }

    private void editSynchronize(Client client, Client dbClient) {
        dbClient.setStatus(client.getStatus());
        dbClient.setPhoneNumber(client.getPhoneNumber());
        dbClient.setName(client.getName());
        dbClient.setAddress(client.getAddress());
        clientRepository.save(dbClient);
    }

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, UserService userService,
                             PasswordEncoder passwordEncoder, EmailNotificationService emailNotificationService, Environment environment,
                             VerificationTokenRepository verificationTokenRepository, ClientClient clientClient,
                             ClientDetailsMapper clientDetailsMapper, LogService logService) {
        this.clientRepository = clientRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailNotificationService = emailNotificationService;
        this.environment = environment;
        this.verificationTokenRepository = verificationTokenRepository;
        this.clientClient = clientClient;
        this.clientDetailsMapper = clientDetailsMapper;
        this.logService = logService;
    }

    private String getLocalhostURL() {
        return environment.getProperty("LOCALHOST_URL");

    }
}
