package jvn.RentACar.serviceImpl;

import jvn.RentACar.exceptionHandler.InvalidPermissionDataException;
import jvn.RentACar.model.Permission;
import jvn.RentACar.repository.PermissionRepository;
import jvn.RentACar.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    PermissionRepository permissionRepository;

    @Override
    public List<Permission> getAll() {
        return permissionRepository.findAll();
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
