package jvn.RentACar.controller;

import jvn.RentACar.dto.both.PermissionDTO;
import jvn.RentACar.mapper.PermissionDtoMapper;
import jvn.RentACar.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/api/permission", produces = MediaType.APPLICATION_JSON_VALUE)
public class PermissionController {

    private PermissionService permissionService;

    private PermissionDtoMapper permissionDtoMapper;

    @GetMapping
    public ResponseEntity<List<PermissionDTO>> getAll() {
        List<PermissionDTO> list = permissionService.getAll().stream().map(permissionDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Autowired
    public PermissionController(PermissionService permissionService, PermissionDtoMapper permissionDtoMapper) {
        this.permissionService = permissionService;
        this.permissionDtoMapper = permissionDtoMapper;
    }
}
