package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.PermissionDTO;
import jvn.RentACar.dto.both.RoleDTO;
import jvn.RentACar.exceptionHandler.InvalidRoleDataException;
import jvn.RentACar.model.Permission;
import jvn.RentACar.model.Role;
import jvn.RentACar.repository.RoleRepository;
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

    private RoleRepository roleRepository;

    private PermissionService permissionService;

    private UserServiceImpl userService;

    @Override
    public Role editPermissions(Long id, RoleDTO roleDTO) {
        Role role = get(id);
        Set<Permission> permissions = new HashSet<>();
        for (PermissionDTO permissionDTO : roleDTO.getPermissions()) {
            permissions.add(permissionService.get(permissionDTO.getId()));
        }
        role.setPermissions(permissions);
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
                           UserServiceImpl userService) {
        this.roleRepository = roleRepository;
        this.permissionService = permissionService;
        this.userService = userService;
    }
}
