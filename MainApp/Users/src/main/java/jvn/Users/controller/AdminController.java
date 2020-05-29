package jvn.Users.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Users.dto.both.AdminDTO;
import jvn.Users.dto.response.UserDTO;
import jvn.Users.mapper.AdminDtoMapper;
import jvn.Users.service.AdminService;
import jvn.Users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.text.ParseException;
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
    public ResponseEntity<AdminDTO>create(@Valid @RequestBody AdminDTO adminDTO) throws ParseException {
        return new ResponseEntity<>(adminDtoMapper.toDto(adminService.create(adminDtoMapper.toEntity(adminDTO))), HttpStatus.CREATED);
    }

    @GetMapping(value="/all/{status}")
    public ResponseEntity<List<AdminDTO>>getAll(@PathVariable(value = "status") String status){
        List<AdminDTO> list = adminService.getAll(status,userService.getLoginUser().getId()).stream().map(adminDtoMapper::toDto).
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
    public AdminController(AdminService adminService, AdminDtoMapper adminDtoMapper,UserService userService) {
        this.adminService = adminService;
        this.adminDtoMapper = adminDtoMapper;
        this.userService = userService;
    }
}
