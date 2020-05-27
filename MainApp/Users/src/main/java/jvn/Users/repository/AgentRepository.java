package jvn.Users.repository;

import jvn.Users.enumeration.AgentStatus;
import jvn.Users.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    Agent findByPhoneNumber(String phoneNumber);
    Agent findOneById(Long id);
    List<Agent> findAllByStatus(AgentStatus agentStatus);
}
