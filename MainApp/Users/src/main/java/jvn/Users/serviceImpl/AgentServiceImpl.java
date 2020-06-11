package jvn.Users.serviceImpl;

import jvn.Users.common.RandomPasswordGenerator;
import jvn.Users.dto.message.OwnerMessageDTO;
import jvn.Users.enumeration.AgentStatus;
import jvn.Users.exceptionHandler.InvalidAgentDataException;
import jvn.Users.model.Agent;
import jvn.Users.model.Role;
import jvn.Users.producer.UserProducer;
import jvn.Users.repository.AgentRepository;
import jvn.Users.service.AgentService;
import jvn.Users.service.EmailNotificationService;
import jvn.Users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentServiceImpl implements AgentService {

    private AgentRepository agentRepository;

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    private EmailNotificationService emailNotificationService;

    private Environment environment;

    private UserProducer userProducer;

    @Override
    public Agent create(Agent agent) {
        if (agentRepository.findByPhoneNumber(agent.getPhoneNumber()) != null) {
            throw new InvalidAgentDataException("Agent with same phone number already exists.",
                    HttpStatus.BAD_REQUEST);
        }
        if (userService.findByEmail(agent.getEmail()) != null) {
            throw new InvalidAgentDataException("Agent with same email address already exists.",
                    HttpStatus.BAD_REQUEST);
        }

        Role role = userService.findRoleByName("ROLE_AGENT");
        agent.setRole(role);
        agent.setStatus(AgentStatus.INACTIVE);

        RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
        String generatedPassword = randomPasswordGenerator.generatePassword();
        agent.setPassword(passwordEncoder.encode(generatedPassword));

        composeAndSendEmailToChangePassword(agent.getEmail(), generatedPassword);
        return agentRepository.save(agent);

    }

    @Override
    public Agent get(Long id) {
        Agent agent = agentRepository.findOneByIdAndStatusNot(id, AgentStatus.DELETED);
        if (agent == null) {
            throw new InvalidAgentDataException("Requested agent does not exist.", HttpStatus.NOT_FOUND);
        }
        return agent;
    }

    @Override
    public List<Agent> getAll(String status, Long id) {
        List<Agent> agents;
        if (status.equals("all")) {
            agents = agentRepository.findByStatusNotAndIdNot(AgentStatus.DELETED, id);
        } else {
            agents = agentRepository.findByStatusAndIdNot(getAgentStatus(status), id);
        }
        return agents;
    }

    @Override
    public void delete(Long id) {
        Agent agent = agentRepository.findOneByIdAndStatusNot(id, AgentStatus.DELETED);
        if (agent == null) {
            throw new InvalidAgentDataException("This agent is already deleted.", HttpStatus.NOT_FOUND);
        }
        agent.setStatus(AgentStatus.DELETED);
        agentRepository.save(agent);
    }

    @Override
    public Agent edit(Long id, Agent agent) {
        Agent dbAgent = get(id);
        dbAgent.setName(agent.getName());
        dbAgent.setAddress(agent.getAddress());
        dbAgent.setPhoneNumber(agent.getPhoneNumber());
        dbAgent.setTaxIdNumber(agent.getTaxIdNumber());
        dbAgent = agentRepository.save(dbAgent);
        sendMessageForSearch(new OwnerMessageDTO(id, dbAgent.getName(), dbAgent.getEmail()));
        return dbAgent;
    }

    @Async
    public void sendMessageForSearch(OwnerMessageDTO ownerMessageDTO) {
        userProducer.sendMessageForSearch(ownerMessageDTO);
    }

    private void composeAndSendEmailToChangePassword(String recipientEmail, String generatedPassword) {
        String subject = "Activate your account";
        StringBuilder sb = new StringBuilder();
        sb.append("An account for you on Rent-a-Car website has been created.");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("You can log in using your email address and the following password: ");
        sb.append(generatedPassword);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(
                "Because of the security protocol, you will have to change this given password the first time you log in.");
        sb.append(System.lineSeparator());
        sb.append("To do that click the following link:");
        sb.append(System.lineSeparator());
        sb.append(getLocalhostURL());
        sb.append("change-password");
        String text = sb.toString();

        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    private String getLocalhostURL() {
        return environment.getProperty("LOCALHOST_URL");
    }

    private AgentStatus getAgentStatus(String status) {
        try {
            return AgentStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new InvalidAgentDataException("Please choose valid agent's status", HttpStatus.NOT_FOUND);
        }
    }

    @Autowired
    public AgentServiceImpl(AgentRepository agentRepository, UserService userService, PasswordEncoder passwordEncoder,
                            EmailNotificationService emailNotificationService, Environment environment, UserProducer userProducer) {
        this.agentRepository = agentRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailNotificationService = emailNotificationService;
        this.environment = environment;
        this.userProducer = userProducer;
    }
}
