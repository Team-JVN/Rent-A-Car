package jvn.Users.serviceImpl;

import jvn.Users.common.RandomPasswordGenerator;
import jvn.Users.dto.message.Log;
import jvn.Users.dto.message.OwnerMessageDTO;
import jvn.Users.dto.response.UserDTO;
import jvn.Users.enumeration.ClientStatus;
import jvn.Users.exceptionHandler.InvalidClientDataException;
import jvn.Users.exceptionHandler.InvalidTokenException;
import jvn.Users.model.Client;
import jvn.Users.model.Role;
import jvn.Users.model.VerificationToken;
import jvn.Users.producer.LogProducer;
import jvn.Users.producer.UserProducer;
import jvn.Users.repository.ClientRepository;
import jvn.Users.repository.VerificationTokenRepository;
import jvn.Users.service.ClientService;
import jvn.Users.service.EmailNotificationService;
import jvn.Users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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

    private UserProducer userProducer;

    private LogProducer logProducer;

    @Override
    public Client create(Client client, boolean fromAgentApp, Long user) throws NoSuchAlgorithmException {


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
        client.setCanCreateRentRequests(true);
        client.setCanCreateComments(true);
        if (client.getPassword() == null) {
            RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
            String generatedPassword = randomPasswordGenerator.generatePassword();
            client.setPassword(passwordEncoder.encode(generatedPassword));
            client.setStatus(ClientStatus.NEVER_LOGGED_IN);
            composeAndSendEmailToChangePassword(client.getEmail(), generatedPassword);
            Client dbClient = clientRepository.save(client);

            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CCL", String.format("User %s successfully created client %s", user, dbClient.getId())));
            return dbClient;
        } else {

            if (fromAgentApp) {
                client.setPassword(client.getPassword());
                client.setStatus(client.getStatus());
                client = clientRepository.save(client);
                VerificationToken verificationToken = new VerificationToken(client);
                String nonHashedToken = verificationToken.getToken();
                verificationToken.setToken(getTokenHash(nonHashedToken));

                VerificationToken dbToken = verificationTokenRepository.findByToken(verificationToken.getToken());
                while (dbToken != null) {
                    verificationToken = new VerificationToken(client);
                    dbToken = verificationTokenRepository.findByToken(verificationToken.getToken());
                }
                verificationTokenRepository.save(verificationToken);
                composeAndSendEmailToActivate(client.getEmail(), nonHashedToken);
            } else {
                client.setPassword(passwordEncoder.encode(client.getPassword()));
                client.setStatus(ClientStatus.AWAITING);
            }
            Client savedClient = clientRepository.save(client);
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "REG", String.format("User %s successfully registered", savedClient.getId())));
            return savedClient;
        }
    }

    @Override
    public Client get(Long id, ClientStatus status) {
        Client client = clientRepository.findOneByIdAndStatus(id, status);
        if (client == null) {
            throw new InvalidClientDataException("This client doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return client;
    }

    @Override
    public boolean verify(Long id) {
        Collection<ClientStatus> statuses = new ArrayList<>();
        statuses.add(ClientStatus.NEVER_LOGGED_IN);
        statuses.add(ClientStatus.ACTIVE);
        statuses.add(ClientStatus.APPROVED);
        Client client = clientRepository.findOneByIdAndStatusIn(id, statuses);
        if (client == null) {
            throw new InvalidClientDataException("This client doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return true;
    }

    @Override
    public List<Client> get(String status, Long id) {
        List<Client> clients;
        if (status.equals("all")) {
            clients = clientRepository.findByStatusNotAndIdNot(ClientStatus.DELETED, id);
        } else {
            clients = clientRepository.findByStatusAndIdNot(getClientStatus(status), id);
        }
        return clients;
    }

    @Override
    public List<Client> getForRentRequest(Long loggedInClientId) {
        Collection<ClientStatus> statuses = new ArrayList<>();
        statuses.add(ClientStatus.NEVER_LOGGED_IN);
        statuses.add(ClientStatus.ACTIVE);
        statuses.add(ClientStatus.APPROVED);
        return clientRepository.findByStatusIn(statuses);
    }

    @Override
    public Client edit(Long id, Client client) {
        Client dbClient = get(id, ClientStatus.ACTIVE);
        if (clientRepository.findByPhoneNumberAndIdNot(client.getPhoneNumber(), id) != null) {
            throw new InvalidClientDataException("Client with same phone number already exists.",
                    HttpStatus.BAD_REQUEST);
        }
        dbClient.setPhoneNumber(client.getPhoneNumber());
        dbClient.setName(client.getName());
        dbClient.setAddress(client.getAddress());
        dbClient = clientRepository.save(dbClient);
        sendMessageForSearch(new OwnerMessageDTO(id, dbClient.getName(), dbClient.getEmail()));

        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "ECL", String.format("User %s successfully edited profile", id)));
        return dbClient;
    }


    @Override
    public void delete(Long id) {
        Client client = clientRepository.findOneByIdAndStatusNot(id, ClientStatus.DELETED);
        if (client == null) {
            throw new InvalidClientDataException("This client is already deleted.", HttpStatus.NOT_FOUND);
        }
        client.setStatus(ClientStatus.DELETED);
        clientRepository.save(client);

        Long loggedInUserId = userService.getLoginUser().getId();
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DCL", String.format("User %s successfully deleted client %s", loggedInUserId, client.getId())));
    }

    @Override
    public Client activateAccount(String token) throws NoSuchAlgorithmException {
        VerificationToken verificationToken = verificationTokenRepository
                .findByTokenAndExpiryDateTimeAfter(getTokenHash(token), LocalDateTime.now());
        if (verificationToken == null) {
            throw new InvalidTokenException("This activation link is invalid or expired.", HttpStatus.BAD_REQUEST);
        }
        Client client = get(verificationToken.getClient().getId(), ClientStatus.APPROVED);
        client.setStatus(ClientStatus.ACTIVE);
        verificationTokenRepository.deleteById(verificationToken.getId());

        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "ACT", String.format("User %s successfully activated account", client.getId())));
        return clientRepository.save(client);
    }

    @Override
    public Client approveRequestToRegister(Long id) throws NoSuchAlgorithmException {
        Client client = get(id, ClientStatus.AWAITING);
        if (client.getStatus().equals(ClientStatus.AWAITING)) {
            client.setStatus(ClientStatus.APPROVED);
            client = clientRepository.save(client);
            VerificationToken verificationToken = new VerificationToken(client);
            String nonHashedToken = verificationToken.getToken();
            verificationToken.setToken(getTokenHash(nonHashedToken));

            VerificationToken dbToken = verificationTokenRepository.findByToken(verificationToken.getToken());
            while (dbToken != null) {
                verificationToken = new VerificationToken(client);
                dbToken = verificationTokenRepository.findByToken(verificationToken.getToken());
            }
            verificationTokenRepository.save(verificationToken);

            composeAndSendEmailToActivate(client.getEmail(), nonHashedToken);

            Long loggedInUserId = userService.getLoginUser().getId();
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "APR", String.format("User %s approved request made by client %s", loggedInUserId, client.getId())));
            return client;
        }
        throw new InvalidClientDataException("This client is already approved/rejected.", HttpStatus.BAD_REQUEST);
    }

    @Override
    public Client rejectRequestToRegister(Long id, String reason) {
        Client client = get(id, ClientStatus.AWAITING);

        client.setRole(null);
        clientRepository.deleteById(id);
        composeAndSendRejectionEmail(client.getEmail(), reason);

        Long loggedInUserId = userService.getLoginUser().getId();
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "RJT", String.format("User %s rejected request made by client %s", loggedInUserId, client.getId())));
        return client;
    }

    @Override
    public Client block(Long id) {
        Client dbClient = get(id, ClientStatus.ACTIVE);
        dbClient.setStatus(ClientStatus.BLOCKED);
        Client client = clientRepository.save(dbClient);
        composeAndSendBlockEmail(client.getEmail());

        Long loggedInUserId = userService.getLoginUser().getId();
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "BLC", String.format("User %s blocked client %s", loggedInUserId, client.getId())));
        return client;
    }

    @Override
    public Client unblock(Long id) {
        Client dbClient = get(id, ClientStatus.BLOCKED);
        dbClient.setStatus(ClientStatus.ACTIVE);
        Client client = clientRepository.save(dbClient);
        composeAndSendUnblockEmail(client.getEmail());

        Long loggedInUserId = userService.getLoginUser().getId();
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "BLC", String.format("User %s unblocked client %s", loggedInUserId, client.getId())));
        return client;
    }

    @Override
    public Client createRentRequests(Long id, String status) {
        Long loggedInUserId = userService.getLoginUser().getId();

        Client dbClient = get(id, ClientStatus.ACTIVE);
        if (status.equals("disable")) {
            dbClient.setCanCreateRentRequests(false);
            dbClient.setCanceledReservationCounter(0);
            composeAndSendDisableCreatingRentRequests(dbClient.getEmail());
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CRR", String.format("User %s disabled client %s", loggedInUserId, dbClient.getId())));
        } else if (status.equals("enable")) {
            dbClient.setCanCreateRentRequests(true);
            composeAndSendEnableCreatingRentRequests(dbClient.getEmail());
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CRR", String.format("User %s enabled client %s", loggedInUserId, dbClient.getId())));
        } else {
            throw new InvalidTokenException("Status is not valid. Please try again.", HttpStatus.BAD_REQUEST);
        }

        return clientRepository.save(dbClient);
    }

    @Override
    public Client createComments(Long id, String status) {
        Long loggedInUserId = userService.getLoginUser().getId();

        Client dbClient = get(id, ClientStatus.ACTIVE);
        if (status.equals("disable")) {
            dbClient.setCanCreateComments(false);
            dbClient.setRejectedCommentsCounter(0);
            composeAndSendDisableCreatingComments(dbClient.getEmail());
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CCM", String.format("User %s disabled client %s", loggedInUserId, dbClient.getId())));
        } else if (status.equals("enable")) {
            dbClient.setCanCreateComments(true);
            composeAndSendEnableCreatingComments(dbClient.getEmail());
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CCM", String.format("User %s enabled client %s", loggedInUserId, dbClient.getId())));
        } else {
            throw new InvalidTokenException("Status is not valid. Please try again.", HttpStatus.BAD_REQUEST);
        }
        return clientRepository.save(dbClient);
    }

    @Override
    public List<Client> getClientsById(List<Long> clients) {
        return clientRepository.findByIdIn(clients);
    }

    @Override
    public String checkClientPersonalInfo(String email, String phoneNumber) {
        if (clientRepository.findByPhoneNumber(phoneNumber) != null) {
            return "PHONE_NUMBER_NOT_VALID";
        }
        if (!email.isEmpty()) {
            if (userService.findByEmail(email) != null) {
                return "EMAIL_NOT_VALID";
            }
        }
        return "ALL";
    }

    @Override
    public boolean checkIfCanDeleteAndDelete(Long id, Long loggedInUser) {
        Client client = clientRepository.findOneByIdAndStatusNot(id, ClientStatus.DELETED);
        if (client == null) {
            return false;
        }
        client.setStatus(ClientStatus.DELETED);
        clientRepository.save(client);

//        Long loggedInUserId = userService.getLoginUser().getId();
        return true;
    }

    @Override
    public List<Client> getAll(Long id) {
        Collection<ClientStatus> statuses = new ArrayList<>();
        statuses.add(ClientStatus.NEVER_LOGGED_IN);
        statuses.add(ClientStatus.ACTIVE);
        statuses.add(ClientStatus.APPROVED);
        statuses.add(ClientStatus.DELETED);
        return clientRepository.findAllByStatusIn(statuses);
    }

    @Async
    public void sendMessageForSearch(OwnerMessageDTO ownerMessageDTO) {
        userProducer.sendMessageForSearch(ownerMessageDTO);
    }

    private void composeAndSendDisableCreatingRentRequests(String recipientEmail) {
        String subject = "Forbidden creating rent requests";
        StringBuilder sb = new StringBuilder();
        sb.append("From now on, you are not allowed to create rent requests because you canceled your reservations many times.");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("You will receive an email when an administrator returns you that permission.");
        sb.append(System.lineSeparator());
        String text = sb.toString();
        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    private void composeAndSendEnableCreatingRentRequests(String recipientEmail) {
        String subject = "Allowed creating rent requests";
        StringBuilder sb = new StringBuilder();
        sb.append("From now on, you are allowed to create rent requests again.");
        sb.append(System.lineSeparator());
        String text = sb.toString();
        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    private void composeAndSendDisableCreatingComments(String recipientEmail) {
        String subject = "Forbidden creating comments";
        StringBuilder sb = new StringBuilder();
        sb.append("From now on, you are not allowed to create comments because your comments were rejected many times by an administrator.");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("You will receive an email when an administrator returns you that permission.");
        sb.append(System.lineSeparator());
        String text = sb.toString();
        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    private void composeAndSendEnableCreatingComments(String recipientEmail) {
        String subject = "Allowed creating comments";
        StringBuilder sb = new StringBuilder();
        sb.append("From now on, you are allowed to create comments again.");
        sb.append(System.lineSeparator());
        String text = sb.toString();
        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    private void composeAndSendRejectionEmail(String recipientEmail, String reason) {
        String subject = "Request to register rejected";
        StringBuilder sb = new StringBuilder();
        sb.append("Your request to register as a client is rejected by a rent a car administrator.");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Explanation:");
        sb.append(System.lineSeparator());
        sb.append(reason);
        String text = sb.toString();
        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    private void composeAndSendBlockEmail(String recipientEmail) {
        String subject = "Blocked account";
        StringBuilder sb = new StringBuilder();
        sb.append("Your account is blocked by a rent a car administrator.");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Administrator will send you email when unblock you.");
        sb.append(System.lineSeparator());
        String text = sb.toString();
        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    private void composeAndSendUnblockEmail(String recipientEmail) {
        String subject = "Unblocked account";
        StringBuilder sb = new StringBuilder();
        sb.append("Your account is unblocked by a rent a car administrator.");
        sb.append(System.lineSeparator());
        String text = sb.toString();
        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    private ClientStatus getClientStatus(String status) {
        try {
            return ClientStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new InvalidClientDataException("Please choose valid client's status", HttpStatus.NOT_FOUND);
        }
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
                             VerificationTokenRepository verificationTokenRepository, UserProducer userProducer, LogProducer logProducer) {
        this.clientRepository = clientRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailNotificationService = emailNotificationService;
        this.environment = environment;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userProducer = userProducer;
        this.logProducer = logProducer;
    }

    private String getLocalhostURL() {
        return environment.getProperty("LOCALHOST_URL");

    }
}
