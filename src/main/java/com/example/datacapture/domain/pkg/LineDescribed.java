package com.example.datacapture.domain.pkg;

import com.example.datacapture.domain.agent.Agent;

public class LineDescribed extends PackageEvent {

  private long lineId;
  private Line line;

  public LineDescribed(PackageId id, long version, Agent capturedBy, Line line, long lineId) {
    super(id, version, capturedBy);
    this.line = line;
    this.lineId = lineId;
  }


  public Line getLine() {
    return line;
  }

  public long getLineId() {
    return lineId;
  }

  @Override
  public String toString() {
    return super.toString() + ", \"lineId\": " + lineId + ", \"line\": {" + line + "}";
  }
}
