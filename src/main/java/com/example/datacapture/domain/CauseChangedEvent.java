package com.example.datacapture.domain;

import com.example.datacapture.domain.agent.Agent;

public class CauseChangedEvent extends PackageDataCapturedEvent {

  private Cause newCause;

  public CauseChangedEvent(PackageId id, long version, Agent capturedBy, Cause newCause) {
    super(id, version, capturedBy);

    this.newCause = newCause;
  }

  public Cause getNewCause() {
    return newCause;
  }
}
