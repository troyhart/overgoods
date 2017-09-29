package com.example.datacapture.domain;

import com.example.datacapture.domain.agent.Agent;

public class PackageCreatedEvent extends PackageDataCapturedEvent {
  
  private Cause cause;
  
  public PackageCreatedEvent(PackageId id, Agent capturedBy, Cause cause) {
    super(id, 1, capturedBy);
    this.cause = cause;
  }
  
  public Cause getCause() {
    return cause;
  }
}
