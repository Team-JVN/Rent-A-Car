package jvn.Users.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Users.dto.message.Log;
import jvn.Users.dto.response.UserDTO;
import jvn.Users.dto.response.UserInfoDTO;
import jvn.Users.dto.response.SignedMessageDTO;
import jvn.Users.mapper.UserDtoMapper;
import jvn.Users.producer.LogProducer;
import jvn.Users.service.DigitalSignatureService;
import jvn.Users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/verify", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private UserService userService;

    private UserDtoMapper userDtoMapper;

    private DigitalSignatureService digitalSignatureService;

    private ObjectMapper objectMapper;

    private LogProducer logProducer;

    @GetMapping
    public ResponseEntity<SignedMessageDTO> verify() {
        UserDTO userDTO = userDtoMapper.toDto(this.userService.verify());
        byte[] messageBytes = convertToBytes(userDTO);
        byte[] digitalSignature = digitalSignatureService.encrypt(messageBytes);
        SignedMessageDTO signedMessageDTO = new SignedMessageDTO(messageBytes, digitalSignature);

        return new ResponseEntity<>(signedMessageDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/user")
    public ResponseEntity<UserInfoDTO> get(@RequestParam String email) {
        return new ResponseEntity<>(userService.getByEmail(email), HttpStatus.OK);
    }

    private byte[] convertToBytes(UserDTO obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to byte array failed", UserDTO.class.getSimpleName())));
            return null;
        }
    }

    @Autowired
    public AuthController(UserService userService, UserDtoMapper userDtoMapper, ObjectMapper objectMapper,
                          LogProducer logProducer, DigitalSignatureService digitalSignatureService) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
        this.objectMapper = objectMapper;
        this.logProducer = logProducer;
        this.digitalSignatureService = digitalSignatureService;
    }
}
