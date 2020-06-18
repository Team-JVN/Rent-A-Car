package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.PermissionDTO;
import jvn.RentACar.dto.both.RoleDTO;
import jvn.RentACar.exceptionHandler.InvalidRoleDataException;
import jvn.RentACar.model.Log;
import jvn.RentACar.model.Permission;
import jvn.RentACar.model.Role;
import jvn.RentACar.repository.RoleRepository;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.PermissionService;
import jvn.RentACar.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private RoleRepository roleRepository;

    private PermissionService permissionService;

    private UserServiceImpl userService;

    private LogService logService;

    @Override
    public Role editPermissions(Long id, RoleDTO roleDTO) {
        Role role = get(id);

        StringBuilder sb = new StringBuilder("Before: ");
        for (Permission permission : role.getPermissions()) {
            sb.append(permission.getId());
            sb.append(", ");
        }
        sb.append("Now: ");

        Set<Permission> permissions = new HashSet<>();
        for (PermissionDTO permissionDTO : roleDTO.getPermissions()) {
            permissions.add(permissionService.get(permissionDTO.getId()));
            sb.append(permissionDTO.getId());
            sb.append(", ");
        }
        role.setPermissions(permissions);

        Long loggedInUserId = userService.getLoginUser().getId();
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EPM", String.format("User %s successfully changed permissions for role %s [%s]", loggedInUserId, role.getId(), sb.toString())));
        return roleRepository.save(role);
    }

    @Override
    public List<Role> getAll() {
        return roleRepository.findByNameNot(userService.getLoginUser().getRole().getName());
    }

    @Override
    public Role get(Long id) {
        Role role = roleRepository.findOneById(id);
        if (role == null) {
            throw new InvalidRoleDataException("This role does not exist.", HttpStatus.NOT_FOUND);
        }
        return role;
    }

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, PermissionService permissionService,
                           UserServiceImpl userService, LogService logService) {
        this.roleRepository = roleRepository;
        this.permissionService = permissionService;
        this.userService = userService;
        this.logService = logService;
    }
}
