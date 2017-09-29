package com.example.datacapture.domain;

import com.example.datacapture.domain.agent.Agent;

public class LineSerialNumberRemovedEvent extends PackageDataCapturedEvent {
  
  private String[] serialNumber;

  public LineSerialNumberRemovedEvent(PackageId id, long version, Agent capturedBy, String...serialNumber) {
    super(id, version, capturedBy);

    this.serialNumber = serialNumber;
  }

  public String[] getSerialNumber() {
    return serialNumber;
  }
}
