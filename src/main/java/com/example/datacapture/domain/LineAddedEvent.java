package com.example.datacapture.domain;

import com.example.datacapture.domain.agent.Agent;

public class LineAddedEvent extends PackageDataCapturedEvent {

  private Line line;

  public LineAddedEvent(PackageId id, long version, Agent capturedBy, Line line) {
    super(id, version, capturedBy);
    this.line = line;
  }

  public Line getLine() {
    return line;
  }
}
