package com.example.datacapture.domain.agent;

// TODO: implement me!!!!!!!!!!

// @Repository
// @Service
public class AgentRepository {

  public Agent find(AgentId agentId) {
    // a simple "play" implementation
    return new Agent.Builder().setId(agentId).setName("Mr. Big Time Data Capture Agent").build();
  }
}
