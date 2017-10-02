package com.example.common.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

public class EventPublisher {

  @SuppressWarnings("rawtypes")
  private static final ThreadLocal<List> subscribers = new ThreadLocal<>();

  private static final ThreadLocal<Boolean> publishing = new ThreadLocal<Boolean>() {
    @Override
    protected Boolean initialValue() {
      return false;
    }
  };

  public static EventPublisher instance() {
    return new EventPublisher();
  }

  public EventPublisher() {
    super();
  }

  public <T extends Event> void publish(final T domainEvent) {
    Assert.state(!publishing.get(), "already publishing");

    try {
      publishing.set(true);
      @SuppressWarnings("unchecked")
      List<EventSubscriber<T>> registeredSubscribers = subscribers.get();
      if (registeredSubscribers != null) {
        Class<?> eventType = domainEvent.getClass();
        for (EventSubscriber<T> eventSubscriber : registeredSubscribers) {
          Class<?> subscribedTo = eventSubscriber.subscribedToEventType();
          if (subscribedTo.isAssignableFrom(eventType)) {
            eventSubscriber.handleEvent(domainEvent);
          }
        }
      }
    }
    finally {
      publishing.set(false);
    }
  }

  public EventPublisher reset() {
    Assert.state(!publishing.get(), "unable to reset while publishing");

    subscribers.set(null);
    return this;
  }

  public <T extends Event> void subscribe(EventSubscriber<T> subscriber) {
    Assert.state(!publishing.get(), "unable to subscribe while publishing");

    @SuppressWarnings("unchecked")
    List<EventSubscriber<T>> registeredSubscribers = subscribers.get();
    if (registeredSubscribers == null) {
      registeredSubscribers = new ArrayList<>();
      subscribers.set(registeredSubscribers);
    }
    registeredSubscribers.add(subscriber);
  }
}
