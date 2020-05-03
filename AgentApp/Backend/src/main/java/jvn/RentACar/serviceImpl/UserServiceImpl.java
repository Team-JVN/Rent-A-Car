package jvn.RentACar.serviceImpl;

import jvn.RentACar.model.Agent;
import jvn.RentACar.model.User;
import jvn.RentACar.repository.UserRepository;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = false)
    @EventListener(ApplicationReadyEvent.class)
    public void insertAfterStartup() {
        Agent agent = new Agent("Rent-A-Car agency", "rentacar@maildrop.cc", "Rent-A-Car agency", "Beograd", "11111111");
        if (userRepository.findByEmail(agent.getEmail()) != null) {
            return;
        }

        userRepository.save(agent);
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
