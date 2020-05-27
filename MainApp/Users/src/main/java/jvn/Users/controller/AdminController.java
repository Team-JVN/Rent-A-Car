package jvn.Users.controller;

import jvn.Users.dto.both.AdminDTO;
import jvn.Users.mapper.AdminDtoMapper;
import jvn.Users.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    private AdminService adminService;

    private AdminDtoMapper adminDtoMapper;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminDTO>create(@Valid @RequestBody AdminDTO adminDTO) throws ParseException {
        return new ResponseEntity<>(adminDtoMapper.toDto(adminService.create(adminDtoMapper.toEntity(adminDTO))), HttpStatus.CREATED);
    }

    @GetMapping(value="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminDTO>get(@PathVariable Long id){
        return new ResponseEntity<>(adminDtoMapper.toDto(adminService.get(id)), HttpStatus.OK);
    }

    @GetMapping(value="/{status}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AdminDTO>>getAll(@PathVariable String status){
        List<AdminDTO> list = adminService.getAll(status).stream().map(adminDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @DeleteMapping(value="/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message="Id must be positive.") Long id){
        adminService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id, @Valid @RequestBody AdminDTO adminDTO) throws ParseException {
        return new ResponseEntity<>(adminDtoMapper.toDto(adminService.edit(id, adminDtoMapper.toEntity(adminDTO))), HttpStatus.OK);
    }

    @Autowired
    public AdminController(AdminService adminService, AdminDtoMapper adminDtoMapper) {
        this.adminService = adminService;
        this.adminDtoMapper = adminDtoMapper;
    }
}
