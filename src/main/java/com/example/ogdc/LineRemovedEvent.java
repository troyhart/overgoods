package com.example.ogdc;

public class LineRemovedEvent extends OvergoodsDataCaptureEvent {

  public LineRemovedEvent(OvergoodsId id, long version, DCAgent capturedBy) {
    super(id, version, capturedBy);
  }

}
