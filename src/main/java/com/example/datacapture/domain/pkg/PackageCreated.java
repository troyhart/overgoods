package com.example.datacapture.domain.pkg;

import com.example.datacapture.domain.agent.Agent;

public class PackageCreated extends PackageEvent {
  
  private Cause cause;
  
  public PackageCreated(PackageId id, Agent capturedBy, Cause cause) {
    super(id, 1, capturedBy);
    this.cause = cause;
  }
  
  public Cause getCause() {
    return cause;
  }
}
