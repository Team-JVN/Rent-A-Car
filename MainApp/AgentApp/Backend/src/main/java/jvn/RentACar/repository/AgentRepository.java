package jvn.RentACar.repository;
import jvn.RentACar.enumeration.AgentStatus;
import jvn.RentACar.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    Agent findOneByIdAndStatusNot(Long id, AgentStatus agentStatus);
}