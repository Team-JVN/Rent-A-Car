package jvn.RentACar.controller;

import jvn.RentACar.dto.both.RoleDTO;
import jvn.RentACar.mapper.RoleDtoMapper;
import jvn.RentACar.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/api/role", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleController {

    private RoleService roleService;

    private RoleDtoMapper roleDtoMapper;

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAll() {
        List<RoleDTO> list = roleService.getAll().stream().map(roleDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id, @Valid @RequestBody RoleDTO roleDTO) {
        return new ResponseEntity<>(roleDtoMapper.toDto(roleService.editPermissions(id, roleDTO)), HttpStatus.OK);
    }

    @Autowired
    public RoleController(RoleService roleService, RoleDtoMapper roleDtoMapper) {
        this.roleService = roleService;
        this.roleDtoMapper = roleDtoMapper;
    }
}
