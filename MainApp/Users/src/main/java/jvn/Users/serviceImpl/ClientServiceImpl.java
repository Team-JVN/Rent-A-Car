package jvn.Users.serviceImpl;

import jvn.Users.common.RandomPasswordGenerator;
import jvn.Users.enumeration.ClientStatus;
import jvn.Users.exceptionHandler.InvalidClientDataException;
import jvn.Users.exceptionHandler.InvalidTokenException;
import jvn.Users.model.Client;
import jvn.Users.model.Role;
import jvn.Users.model.VerificationToken;
import jvn.Users.repository.ClientRepository;
import jvn.Users.repository.VerificationTokenRepository;
import jvn.Users.service.ClientService;
import jvn.Users.service.EmailNotificationService;
import jvn.Users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.core.env.Environment;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private ClientRepository clientRepository;

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    private EmailNotificationService emailNotificationService;

    private Environment environment;
    private VerificationTokenRepository verificationTokenRepository;

    @Override
    public Client create(Client client) throws NoSuchAlgorithmException {
        if (clientRepository.findByPhoneNumber(client.getPhoneNumber()) != null) {
            throw new InvalidClientDataException("Client with same phone number already exists.",
                    HttpStatus.BAD_REQUEST);
        }
        if (userService.findByEmail(client.getEmail()) != null) {
            throw new InvalidClientDataException("Client with same email address already exists.",
                    HttpStatus.BAD_REQUEST);
        }

        Role role = userService.findRoleByName("ROLE_CLIENT");
        client.setRole(role);
        client.setCanceledReservationCounter(0);
        client.setRejectedCommentsCounter(0);
        if (client.getPassword() == null) {
            RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
            String generatedPassword = randomPasswordGenerator.generatePassword();
            client.setPassword(passwordEncoder.encode(generatedPassword));
            client.setStatus(ClientStatus.NEVER_LOGGED_IN);

            composeAndSendEmailToChangePassword(client.getEmail(), generatedPassword);
            return clientRepository.save(client);
        } else {
            client.setPassword(passwordEncoder.encode(client.getPassword()));
            client.setStatus(ClientStatus.AWAITING);
            Client savedClient = clientRepository.save(client);
            return savedClient;
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

//    @Override
//    public List<Client> get() {
//        return clientRepository.findAll();
//    }
//
//    @Override
//    public Client edit(Long id, Client client) {
//        Client dbClient = get(client.getId());
//        if (clientRepository.findByPhoneNumberAndIdNot(client.getPhoneNumber(), id) != null) {
//            throw new InvalidClientDataException("Client with same phone number already exists.",
//                    HttpStatus.BAD_REQUEST);
//        }
//        dbClient.setPhoneNumber(client.getPhoneNumber());
//        dbClient.setName(client.getName());
//        dbClient.setAddress(client.getAddress());
//        return clientRepository.save(dbClient);
//    }
//
//    @Override
//    public void delete(Long id) {
//        Client dbClient = get(id);
//        if (!dbClient.getClientRentRequests().isEmpty()) {
//            throw new InvalidClientDataException(
//                    "This client has at least one request so you can not delete this client.", HttpStatus.BAD_REQUEST);
//        }
//        dbClient.setRole(null);
//        clientRepository.deleteById(id);
//    }
//
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

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, UserService userService,
                             PasswordEncoder passwordEncoder, EmailNotificationService emailNotificationService, Environment environment,
                             VerificationTokenRepository verificationTokenRepository) {
        this.clientRepository = clientRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailNotificationService = emailNotificationService;
        this.environment = environment;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    private String getLocalhostURL() {
        return environment.getProperty("LOCALHOST_URL");

    }
}