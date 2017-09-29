package com.example.datacapture.domain;

import com.example.datacapture.domain.agent.Agent;

public class LineSerialNumberChangedEvent extends PackageDataCapturedEvent {

  private String newSerialNumber;
  private String oldSerialNumber;

  public LineSerialNumberChangedEvent(PackageId id, long version, Agent capturedBy, String oldSerialNumber,
      String newSerialNumber) {
    super(id, version, capturedBy);

    this.oldSerialNumber = oldSerialNumber;
    this.newSerialNumber = newSerialNumber;
  }

  public String getOldSerialNumber() {
    return oldSerialNumber;
  }

  public String getNewSerialNumber() {
    return newSerialNumber;
  }
}
