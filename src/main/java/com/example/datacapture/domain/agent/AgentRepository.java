package com.example.datacapture.domain.agent;

// TODO: implement me!!!!!!!!!!

// @Repository
// @Service
public class AgentRepository {

  public Agent find(String id) {
    // a simple "play" implementation
    return new Agent.Builder().setId(new AgentId(id)).setName("Mr. Big Time Data Capture Agent").build();
  }
}
