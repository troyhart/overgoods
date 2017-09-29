package com.example.common.domain;

import java.util.Date;

public interface Event {

  EventId getEventId();

  EntityId getAggregateId();

  long getVersion();

  Date getOccurredOn();
}
