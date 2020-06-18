package jvn.Cars.serviceImpl;

import jvn.Cars.service.FuelTypeService;
import jvn.Cars.service.GearboxTypeService;
import jvn.Cars.service.MakeService;
import jvn.Cars.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
public class SetupDataLoader{

}
/*
@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    boolean alreadySetup = false;

    @Autowired
    private MakeService makeService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private FuelTypeService fuelTypeService;

    @Autowired
    private GearboxTypeService gearboxTypeService;


    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup) {
            return;
        }

        Permission manageUsers = createPermissionIfNotFound("MANAGE_USERS");
        Permission adminEditProfile = createPermissionIfNotFound("ADMIN_EDIT_PROFILE");
        Permission agentEditProfile = createPermissionIfNotFound("AGENT_EDIT_PROFILE");
        Permission clientEditProfile = createPermissionIfNotFound("CLIENT_EDIT_PROFILE");
        Permission manageRoles = createPermissionIfNotFound("MANAGE_ROLES");
        Permission manageCodeBooks = createPermissionIfNotFound("MANAGE_CODE_BOOKS");
        Permission manageCars = createPermissionIfNotFound("MANAGE_CARS");
        Permission getStatistics = createPermissionIfNotFound("GET_STATISTICS");
        Permission managePriceLists = createPermissionIfNotFound("MANAGE_PRICE_LISTS");
        Permission manageAdvertisements = createPermissionIfNotFound("MANAGE_ADVERTISEMENTS");
        Permission myRentRequests = createPermissionIfNotFound("MY_RENT_REQUESTS");

        Set<Permission> adminPermissions = new HashSet<>(Arrays.asList(manageUsers, adminEditProfile, manageRoles, manageCodeBooks));
        createRoleIfNotFound("ROLE_ADMIN", adminPermissions);

        Set<Permission> clientPermissions = new HashSet<>(Arrays.asList(clientEditProfile, manageCars, managePriceLists, manageAdvertisements,
                myRentRequests));
        createRoleIfNotFound("ROLE_CLIENT", clientPermissions);

        Set<Permission> agentPermissions = new HashSet<>(Arrays.asList(agentEditProfile, manageCars, managePriceLists,
                manageAdvertisements, getStatistics));
        createRoleIfNotFound("ROLE_AGENT", agentPermissions);

        RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
        String generatedPassword = randomPasswordGenerator.generatePassword();

        Admin admin = new Admin("Rent a Car Admin", "rentacaradmin@maildrop.cc", passwordEncoder.encode(generatedPassword), this.roleRepository.findByName("ROLE_ADMIN"));
        if (userRepository.findByEmail(admin.getEmail()) == null) {
            userRepository.save(admin);
            composeAndSendEmailToChangePassword(admin.getEmail(), generatedPassword);
        }

        generatedPassword = randomPasswordGenerator.generatePassword();
        Agent agent = new Agent("Rent a Car Agency", "rentacar@maildrop.cc",
                passwordEncoder.encode(generatedPassword), this.roleRepository.findByName("ROLE_AGENT"), "Beograd", "0627564136", "500000014");
        if (userRepository.findByEmail(agent.getEmail()) == null) {
            userRepository.save(agent);
            composeAndSendEmailToChangePassword(agent.getEmail(), generatedPassword);
        }

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

}
*/