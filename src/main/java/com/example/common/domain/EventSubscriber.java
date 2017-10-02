package com.example.common.domain;

public interface EventSubscriber<T extends Event> {

  void handleEvent(T domainEvent);

  Class<T> subscribedToEventType();
}
