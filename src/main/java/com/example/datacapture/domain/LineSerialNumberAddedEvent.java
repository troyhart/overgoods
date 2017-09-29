package com.example.datacapture.domain;

import com.example.datacapture.domain.agent.Agent;

public class LineSerialNumberAddedEvent extends PackageDataCapturedEvent {

  private String[] serialNumber;

  public LineSerialNumberAddedEvent(PackageId id, long version, Agent capturedBy, String...serialNumber) {
    super(id, version, capturedBy);
    this.serialNumber = serialNumber;
  }

  public String[] getSerialNumber() {
    return serialNumber;
  }
}
