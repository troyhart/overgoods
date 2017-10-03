package com.example.datacapture.domain.pkg;

import java.util.Date;
import java.util.UUID;

import org.springframework.util.Assert;

import com.example.common.domain.Event;
import com.example.common.domain.EventId;
import com.example.datacapture.domain.agent.Agent;


/**
 * base type for all events in this pkg module
 * 
 * @author troyh
 *
 */
public class PackageEvent implements Event {

  private EventId eventId;
  private PackageId packageId;
  private long version;
  private Date capturedOn;
  private Agent capturedBy;

  public PackageEvent(PackageId packageId, long version, Agent capturedBy) {
    Assert.notNull(packageId, "null packageId");
    Assert.isTrue(version > 0, "Invalid version: " + version + "! Must be greater than zero.");
    Assert.notNull(capturedBy, "null capturedBy");

    this.eventId = new EventId(UUID.randomUUID().toString().toUpperCase());

    this.packageId = packageId;
    this.version = version;
    this.capturedBy = capturedBy;
    this.capturedOn = new Date();
  }

  @Override
  public EventId getEventId() {
    return eventId;
  }

  /**
   * An alias for {@link #getAggregateId()}
   * 
   * @return the {@link #getAggregateId() aggregateId}
   */
  public PackageId getPackageId() {
    return packageId;
  }

  @Override
  public PackageId getAggregateId() {
    return getPackageId();
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

  @Override
  public String toString() {
    return "{\"eventId\": " + eventId + ", \"packageId\": " + packageId + ", \"version\": " + version
        + ", \"capturedOn\": \"" + capturedOn + "\", \"capturedBy\": " + capturedBy + "}";
  }


}
