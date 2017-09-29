package com.example.datacapture.domain;

import com.example.datacapture.domain.agent.Agent;

public class LineRemovedEvent extends PackageDataCapturedEvent {

  public LineRemovedEvent(PackageId id, long version, Agent capturedBy) {
    super(id, version, capturedBy);
  }

}
