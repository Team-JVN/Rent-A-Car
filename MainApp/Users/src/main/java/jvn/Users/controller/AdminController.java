package jvn.Users.controller;

import jvn.Users.dto.both.AdminDTO;
import jvn.Users.exceptionHandler.InvalidAdminDataException;
import jvn.Users.mapper.AdminDtoMapper;
import jvn.Users.model.Admin;
import jvn.Users.model.User;
import jvn.Users.service.AdminService;
import jvn.Users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/api/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    private AdminService adminService;

    private AdminDtoMapper adminDtoMapper;

    private UserService userService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminDTO> create(@Valid @RequestBody AdminDTO adminDTO) {
        return new ResponseEntity<>(adminDtoMapper.toDto(adminService.create(adminDtoMapper.toEntity(adminDTO))), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AdminDTO> get(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        return new ResponseEntity<>(adminDtoMapper.toDto(adminService.get(id)), HttpStatus.CREATED);
    }

    @GetMapping(value = "/all/{status}")
    public ResponseEntity<List<AdminDTO>> getAll(@PathVariable(value = "status") @Pattern(regexp = "(?i)(all|active|inactive)$", message = "Status is not valid.") String status) {
        List<AdminDTO> list = adminService.getAll(status, userService.getLoginUser().getId()).stream().map(adminDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        adminService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminDTO> edit(@Valid @RequestBody AdminDTO adminDTO) {
        User user = userService.getLoginUser();
        if (user instanceof Admin) {
            return new ResponseEntity<>(adminDtoMapper.toDto(adminService.edit(userService.getLoginUser().getId(), adminDtoMapper.toEntity(adminDTO))), HttpStatus.OK);
        }
        throw new InvalidAdminDataException("As a non-authorized user, you are not allowed to enter this page.", HttpStatus.FORBIDDEN);
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<AdminDTO> get() {
        User user = userService.getLoginUser();
        if (user instanceof Admin) {
            return new ResponseEntity<>(adminDtoMapper.toDto((Admin) user), HttpStatus.OK);
        }
        throw new InvalidAdminDataException("As a non-authorized user, you are not allowed to enter this page.", HttpStatus.FORBIDDEN);
    }

    @Autowired
    public AdminController(AdminService adminService, AdminDtoMapper adminDtoMapper, UserService userService) {
        this.adminService = adminService;
        this.adminDtoMapper = adminDtoMapper;
        this.userService = userService;
    }
}
