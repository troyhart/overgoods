package com.example.ogdc;

public class CauseChangedEvent extends OvergoodsDataCaptureEvent {

  private Cause newCause;

  public CauseChangedEvent(OvergoodsId id, long version, DCAgent capturedBy, Cause newCause) {
    super(id, version, capturedBy);

    this.newCause = newCause;
  }

  public Cause getNewCause() {
    return newCause;
  }
}
