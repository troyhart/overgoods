package com.example.ogdc;

public class OvergoodsCreatedEvent extends OvergoodsDataCaptureEvent {
  
  private Cause cause;
  
  public OvergoodsCreatedEvent(OvergoodsId id, DCAgent capturedBy, Cause cause) {
    super(id, 1, capturedBy);
    this.cause = cause;
  }
  
  public Cause getCause() {
    return cause;
  }
}
