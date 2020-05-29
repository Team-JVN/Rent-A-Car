package jvn.Users.serviceImpl;

import jvn.Users.common.RandomPasswordGenerator;
import jvn.Users.exceptionHandler.InvalidAgentDataException;
import org.springframework.core.env.Environment;
import jvn.Users.dto.both.AgentDTO;
import jvn.Users.enumeration.AgentStatus;
import jvn.Users.exceptionHandler.InvalidClientDataException;
import jvn.Users.model.Agent;
import jvn.Users.model.Role;
import jvn.Users.repository.AgentRepository;
import jvn.Users.service.AgentService;
import jvn.Users.service.EmailNotificationService;
import jvn.Users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class AgentServiceImpl implements AgentService {

    private AgentRepository agentRepository;

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    private EmailNotificationService emailNotificationService;

    private Environment environment;

    @Override
    public Agent create(Agent agent) {
        if (agentRepository.findByPhoneNumber(agent.getPhoneNumber()) != null) {
            throw new InvalidAgentDataException("Agent with same phone number already exists.",
                    HttpStatus.BAD_REQUEST);
        }
        if (userService.findByEmail(agent.getEmail()) != null) {
            throw new InvalidAgentDataException("User with same email address already exists.",
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
        Agent agent = agentRepository.findOneById(id);
        if (agent == null) {
            throw new InvalidAgentDataException("Requested agent does not exist.", HttpStatus.NOT_FOUND);
        }
        return agent;
    }

    @Override
    public List<Agent> getAll(String status) {
        List<Agent> agents = null;
        if (status.equals("inactive")) {
            agents = agentRepository.findAllByStatus(AgentStatus.INACTIVE);
        } else {
            agents = agentRepository.findAllByStatus(AgentStatus.ACTIVE);
        }
        return agents;
    }

    @Override
    public void delete(Long id) {
        Agent agent = get(id);
        if (agent.getStatus() == AgentStatus.INACTIVE) {
            throw new InvalidAgentDataException("This agent is in use and therefore can not be deleted.", HttpStatus.BAD_REQUEST);
        }
        agent.setStatus(AgentStatus.INACTIVE);
        agentRepository.save(admin);
    }

    @Override
    public Agent edit(Long id, Agent agent) {
        Agent dbAgent = get(agent.getId());
        dbAgent.setName(agent.getName());
        dbAgent.setAddress(agent.getAddress());
        dbAgent.setPhoneNumber(agent.getPhoneNumber());
        dbAgent.setTaxId(agent.getTaxId());
        return agentRepository.save(dbAgent);
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

    @Autowired
    public AgentServiceImpl(AgentRepository agentRepository, UserService userService,PasswordEncoder passwordEncoder,
                            EmailNotificationService emailNotificationService, Environment environment) {
        this.agentRepository = agentRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailNotificationService = emailNotificationService;
        this.environment = environment;
    }
}
