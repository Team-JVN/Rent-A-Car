package jvn.RentACar.serviceImpl;

import jvn.RentACar.common.RandomPasswordGenerator;
import jvn.RentACar.model.Agent;
import jvn.RentACar.model.Permission;
import jvn.RentACar.model.Role;
import jvn.RentACar.repository.PermissionRepository;
import jvn.RentACar.repository.RoleRepository;
import jvn.RentACar.repository.UserRepository;
import jvn.RentACar.service.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    private RoleRepository roleRepository;

    private PermissionRepository permissionRepository;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private EmailNotificationService emailNotificationService;

    private Environment environment;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup) {
            return;
        }

        Permission manageAdvertisements = createPermissionIfNotFound("MANAGE_ADVERTISEMENTS");
        Permission manageCodeBooks = createPermissionIfNotFound("MANAGE_CODE_BOOKS");
        Permission manageCars = createPermissionIfNotFound("MANAGE_CARS");
        Permission manageClients = createPermissionIfNotFound("MANAGE_CLIENTS");
        Permission managePriceLists = createPermissionIfNotFound("MANAGE_PRICE_LISTS");
        Permission manageRentReports = createPermissionIfNotFound("MANAGE_RENT_REPORTS");
        Permission manageRoles = createPermissionIfNotFound("MANAGE_ROLES");
        Permission getStatistics = createPermissionIfNotFound("GET_STATISTICS");

        Permission myRentRequests = createPermissionIfNotFound("MY_RENT_REQUESTS");

        Permission agentEditProfile = createPermissionIfNotFound("AGENT_EDIT_PROFILE");
        Permission clientEditProfile = createPermissionIfNotFound("CLIENT_EDIT_PROFILE");

        Set<Permission> agentPermissions = new HashSet<>(Arrays.asList(manageAdvertisements, manageCodeBooks, manageCars, manageClients,
                managePriceLists, manageRentReports, manageRoles,agentEditProfile,getStatistics));
        createRoleIfNotFound("ROLE_AGENT", agentPermissions);

        Set<Permission> clientPermissions = new HashSet<>(Arrays.asList(clientEditProfile, myRentRequests,manageClients));
        createRoleIfNotFound("ROLE_CLIENT", clientPermissions);

        RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
        String generatedPassword = randomPasswordGenerator.generatePassword();

        Agent agent = new Agent("Rent a Car Agency", "rentacar@maildrop.cc",
                passwordEncoder.encode(generatedPassword), "Beograd", "500000014","0627564136");
        if (userRepository.findByEmail(agent.getEmail()) != null) {
            return;
        }

        Role agentRole = this.roleRepository.findByName("ROLE_AGENT");
        agent.setRole(agentRole);
        userRepository.save(agent);
        composeAndSendEmailToChangePassword(agent.getEmail(), generatedPassword);

        alreadySetup = true;
    }

    @Transactional
    public Permission createPermissionIfNotFound(String name) {
        Permission permission = permissionRepository.findByName(name);
        if (permission == null) {
            permission = new Permission();
            permission.setName(name);
            return permissionRepository.save(permission);
        }
        return permission;
    }

    @Transactional
    public void createRoleIfNotFound(String name, Set<Permission> permissions) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role();
            role.setName(name);
            role.setPermissions(permissions);
            roleRepository.save(role);
        }
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
        sb.append("Because of the security protocol, you will have to change this given password the first time you log in.");
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

    @Autowired
    public SetupDataLoader(RoleRepository roleRepository, PermissionRepository permissionRepository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder, EmailNotificationService emailNotificationService,Environment environment) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailNotificationService = emailNotificationService;
        this.environment=environment;
    }
}
