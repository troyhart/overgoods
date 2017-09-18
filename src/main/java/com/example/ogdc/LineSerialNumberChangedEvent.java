package com.example.ogdc;

public class LineSerialNumberChangedEvent extends OvergoodsDataCaptureEvent {

  private String newSerialNumber;
  private String oldSerialNumber;

  public LineSerialNumberChangedEvent(OvergoodsId id, long version, DCAgent capturedBy, String oldSerialNumber,
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
