package jvn.Users.serviceImpl;

import jvn.Users.exceptionHandler.InvalidPermissionDataException;
import jvn.Users.model.Permission;
import jvn.Users.repository.PermissionRepository;
import jvn.Users.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private PermissionRepository permissionRepository;

    @Override
    public List<Permission> getAll() {
        return permissionRepository.findByNameNot("MANAGE_ROLES");
    }

    @Override
    public Permission get(Long id) {
        Permission permission = permissionRepository.findOneById(id);
        if (permission == null) {
            throw new InvalidPermissionDataException("This permission does not exist.", HttpStatus.NOT_FOUND);
        }
        return permission;
    }

    @Autowired
    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
}
