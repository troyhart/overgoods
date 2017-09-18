package com.example.ogdc;

public class LineSerialNumberAddedEvent extends OvergoodsDataCaptureEvent {

  private String[] serialNumber;

  public LineSerialNumberAddedEvent(OvergoodsId id, long version, DCAgent capturedBy, String...serialNumber) {
    super(id, version, capturedBy);
    this.serialNumber = serialNumber;
  }

  public String[] getSerialNumber() {
    return serialNumber;
  }
}
