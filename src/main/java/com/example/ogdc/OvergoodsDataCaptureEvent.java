package com.example.ogdc;

import java.util.Date;

import org.springframework.util.Assert;

import com.example.infrastructure.DomainEvent;

public class OvergoodsDataCaptureEvent implements DomainEvent {

  private OvergoodsId id;
  private long version;
  private Date capturedOn;
  private DCAgent capturedBy;

  public OvergoodsDataCaptureEvent(OvergoodsId id, long version, DCAgent capturedBy) {
    Assert.notNull(id, "null id");
    Assert.isTrue(version > 0, "Invalid version: " + version + "! Must be greater than zero.");
    Assert.notNull(capturedBy, "null capturedBy");

    this.id = id;
    this.version = version;
    this.capturedBy = capturedBy;
    this.capturedOn = new Date();
  }

  @Override
  public OvergoodsId id() {
    return id;
  }

  @Override
  public long version() {
    return version;
  }

  @Override
  public Date occurredOn() {
    return capturedOn;
  }

  public DCAgent capturedBy() {
    return capturedBy;
  }
}
