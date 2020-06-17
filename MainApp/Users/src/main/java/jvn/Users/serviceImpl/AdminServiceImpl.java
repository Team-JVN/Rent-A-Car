package jvn.Users.serviceImpl;

import jvn.Users.common.RandomPasswordGenerator;
import jvn.Users.dto.message.Log;
import jvn.Users.enumeration.AdminStatus;
import jvn.Users.exceptionHandler.InvalidAdminDataException;
import jvn.Users.model.Admin;
import jvn.Users.model.Role;
import jvn.Users.producer.LogProducer;
import jvn.Users.repository.AdminRepository;
import jvn.Users.service.AdminService;
import jvn.Users.service.EmailNotificationService;
import jvn.Users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private AdminRepository adminRepository;

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    private EmailNotificationService emailNotificationService;

    private Environment environment;

    private LogProducer logProducer;

    @Override
    public Admin create(Admin admin) {

        if (userService.findByEmail(admin.getEmail()) != null) {
            throw new InvalidAdminDataException("User with same email address already exists.",
                    HttpStatus.BAD_REQUEST);
        }

        Role role = userService.findRoleByName("ROLE_ADMIN");
        admin.setRole(role);
        admin.setStatus(AdminStatus.INACTIVE);

        RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
        String generatedPassword = randomPasswordGenerator.generatePassword();
        admin.setPassword(passwordEncoder.encode(generatedPassword));

        composeAndSendEmailToChangePassword(admin.getEmail(), generatedPassword);
        Admin dbAdmin = adminRepository.save(admin);

        Long loggedInUserId = userService.getLoginUser().getId();
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CAM", String.format("User %s successfully created agent %s", loggedInUserId, dbAdmin.getId())));
        return dbAdmin;
    }

    @Override
    public Admin get(Long id) {
        Admin admin = adminRepository.findOneById(id);
        if (admin == null) {
            throw new InvalidAdminDataException("Requested admin does not exist.", HttpStatus.NOT_FOUND);
        }
        return admin;
    }

    @Override
    public List<Admin> getAll(String status, Long id) {
        List<Admin> admins;
        if (status.equals("all")) {
            admins = adminRepository.findAllByIdNot(id);
        } else {
            admins = adminRepository.findByStatusAndIdNot(getAdminStatus(status), id);
        }
        return admins;
    }

    @Override
    public void delete(Long id) {
        Admin admin = get(id);
        admin.setRole(null);

        Long loggedInUserId = userService.getLoginUser().getId();
        adminRepository.deleteById(id);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DAM", String.format("User %s successfully deleted admin %s", loggedInUserId, admin.getId())));
    }

    @Override
    public Admin edit(Long id, Admin admin) {
        Admin dbAdmin = get(id);
        dbAdmin.setName(admin.getName());

        Admin savedAdmin = adminRepository.save(dbAdmin);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EAM", String.format("User %s successfully edited profile", savedAdmin.getId())));
        return savedAdmin;
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

    private String getLocalhostURL() {
        return environment.getProperty("LOCALHOST_URL");
    }

    private AdminStatus getAdminStatus(String status) {
        try {
            return AdminStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new InvalidAdminDataException("Please choose valid admin's status", HttpStatus.NOT_FOUND);
        }
    }

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository, UserService userService, PasswordEncoder passwordEncoder,
                            EmailNotificationService emailNotificationService, Environment environment, LogProducer logProducer) {
        this.adminRepository = adminRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailNotificationService = emailNotificationService;
        this.environment = environment;
        this.logProducer = logProducer;
    }
}
