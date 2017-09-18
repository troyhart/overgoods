package com.example.ogdc;

// TODO: implement me!!!!!!!!!!

//@Repository
//@Service
public class DCAgentRepository {

  public DCAgent find(DCAgentId id) {
    // a simple "play" implementation
    return new DCAgent.Builder().setId(id).setName("Mr. Big Time Data Capture Agent").build();
  }
}
