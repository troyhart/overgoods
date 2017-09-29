package com.example.datacapture.domain;

import java.util.Date;

import org.springframework.util.Assert;

import com.example.common.domain.Event;
import com.example.common.domain.EventId;
import com.example.datacapture.domain.agent.Agent;

public class PackageDataCapturedEvent implements Event {

  private PackageId id;
  private long version;
  private Date capturedOn;
  private Agent capturedBy;

  public PackageDataCapturedEvent(PackageId id, long version, Agent capturedBy) {
    Assert.notNull(id, "null id");
    Assert.isTrue(version > 0, "Invalid version: " + version + "! Must be greater than zero.");
    Assert.notNull(capturedBy, "null capturedBy");

    this.id = id;
    this.version = version;
    this.capturedBy = capturedBy;
    this.capturedOn = new Date();
  }

  @Override
  public EventId getEventId() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PackageId getAggregateId() {
    return id;
  }

  @Override
  public long getVersion() {
    return version;
  }

  @Override
  public Date getOccurredOn() {
    return capturedOn;
  }

  public Agent capturedBy() {
    return capturedBy;
  }
}
