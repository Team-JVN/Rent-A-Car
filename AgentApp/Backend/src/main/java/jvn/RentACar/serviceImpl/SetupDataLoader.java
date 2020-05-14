package jvn.RentACar.serviceImpl;

import jvn.RentACar.model.Agent;
import jvn.RentACar.model.Permission;
import jvn.RentACar.model.Role;
import jvn.RentACar.repository.PermissionRepository;
import jvn.RentACar.repository.RoleRepository;
import jvn.RentACar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
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

        Permission createRentRequest = createPermissionIfNotFound("CREATE_RENT_REQUEST");
        Permission getMyRentRequests = createPermissionIfNotFound("GET_MY_RENT_REQUESTS");
        Permission getReceivedRentRequest = createPermissionIfNotFound("GET_RECEIVED_RENT_REQUEST");
        Permission deleteRentRequest = createPermissionIfNotFound("DELETE_RENT_REQUEST");
        Permission changeRentRequestStatus = createPermissionIfNotFound("CHANGE_RENT_REQUEST_STATUS");

        Set<Permission> agentPermissions = new HashSet<>(Arrays.asList(manageAdvertisements, manageCodeBooks, manageCars, manageClients,
                managePriceLists, manageRentReports, createRentRequest, getReceivedRentRequest, changeRentRequestStatus));
        createRoleIfNotFound("ROLE_AGENT", agentPermissions);

        Set<Permission> clientPermissions = new HashSet<>(Arrays.asList(createRentRequest, getMyRentRequests, deleteRentRequest,
                changeRentRequestStatus));
        createRoleIfNotFound("ROLE_CLIENT", clientPermissions);

        //Rentacar1
        Agent agent = new Agent("Rent-A-Car agency", "rentacar@maildrop.cc", "$2a$10$34ippcUyKNhjxY8o5yDVBO47eOXdtXzC1LqPPO4UlxJgdbdo/lcZe", "Beograd", "11111111");
        if (userRepository.findByEmail(agent.getEmail()) != null) {
            return;
        }

        Role agentRole = this.roleRepository.findByName("ROLE_AGENT");
        agent.setRole(agentRole);
        userRepository.save(agent);

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

    @Autowired
    public SetupDataLoader(RoleRepository roleRepository, PermissionRepository permissionRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }
}
