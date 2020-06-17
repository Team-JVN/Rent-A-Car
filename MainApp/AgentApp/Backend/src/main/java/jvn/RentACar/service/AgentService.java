package jvn.RentACar.service;

import jvn.RentACar.model.Agent;

public interface AgentService {
    Agent edit(Long id, Agent agent);

    Agent get(Long id);
}
