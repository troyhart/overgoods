package com.example.ogdc;

public class LineAddedEvent extends OvergoodsDataCaptureEvent {

  private Line line;

  public LineAddedEvent(OvergoodsId id, long version, DCAgent capturedBy, Line line) {
    super(id, version, capturedBy);
    this.line = line;
  }

  public Line getLine() {
    return line;
  }
}
