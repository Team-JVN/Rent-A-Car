package jvn.Users.repository;

import jvn.Users.enumeration.AgentStatus;
import jvn.Users.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    Agent findByPhoneNumber(String phoneNumber);

    Agent findOneByIdAndStatusNot(Long id,AgentStatus agentStatus);

    List<Agent> findByStatusNotAndIdNot(AgentStatus agentStatus,Long id);

    List<Agent> findByStatusAndIdNot(AgentStatus agentStatus,Long id);
}
