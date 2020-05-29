package jvn.Users.serviceImpl;

import jvn.Users.common.RandomPasswordGenerator;
import jvn.Users.enumeration.AdminStatus;
import jvn.Users.exceptionHandler.InvalidAdminDataException;
import jvn.Users.exceptionHandler.InvalidClientDataException;
import jvn.Users.model.Admin;
import jvn.Users.model.Role;
import jvn.Users.repository.AdminRepository;
import jvn.Users.service.AdminService;
import jvn.Users.service.EmailNotificationService;
import jvn.Users.service.UserService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class AdminServiceImpl implements AdminService {

    private AdminRepository adminRepository;

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    private EmailNotificationService emailNotificationService;

    private Environment environment;


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
        return adminRepository.save(admin);
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
    public List<Admin> getAll(String status) {
        List<Admin> admins = null;
        if (status.equals("inactive")) {
            admins = adminRepository.findAllByStatus(AdminStatus.INACTIVE);
        } else {
            admins = adminRepository.findAllByStatus(AdminStatus.ACTIVE);
        }
        return admins;
    }

    @Override
    public void delete(Long id) {
        Admin admin = get(id);
        admin.setRole(null);
        adminRepository.deleteById(id);
    }

    @Override
    public Admin edit(Long id, Admin admin) {
        Admin dbAdmin = get(admin.getId());
        dbAdmin.setName(admin.getName());
        return adminRepository.save(dbAdmin);

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

    public AdminServiceImpl(AdminRepository adminRepository, UserService userService, PasswordEncoder passwordEncoder, EmailNotificationService emailNotificationService, Environment environment) {
        this.adminRepository = adminRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailNotificationService = emailNotificationService;
        this.environment = environment;
    }
}
