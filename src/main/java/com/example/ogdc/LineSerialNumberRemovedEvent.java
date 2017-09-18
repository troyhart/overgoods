package com.example.ogdc;

public class LineSerialNumberRemovedEvent extends OvergoodsDataCaptureEvent {
  
  private String[] serialNumber;

  public LineSerialNumberRemovedEvent(OvergoodsId id, long version, DCAgent capturedBy, String...serialNumber) {
    super(id, version, capturedBy);

    this.serialNumber = serialNumber;
  }

  public String[] getSerialNumber() {
    return serialNumber;
  }
}
