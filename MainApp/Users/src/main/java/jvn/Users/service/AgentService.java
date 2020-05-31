package jvn.Users.service;

import jvn.Users.dto.both.AgentDTO;
import jvn.Users.model.Agent;

import java.util.BitSet;
import java.util.List;

public interface AgentService {

    Agent create(Agent agent);

    Agent get(Long id);

    List<Agent> getAll(String status,Long id);

    void delete(Long id);

    Agent edit(Long id, Agent agent);
}
