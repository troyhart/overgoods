package com.example.common.domain;

import java.util.List;

public class OptimisticConcurrencyException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  
  private List<? extends Event> missedEvents;

  public OptimisticConcurrencyException(List<? extends Event> missedEvents) {
    this.missedEvents = missedEvents;
  }

  public List<? extends Event> getMissedEvents() {
    return missedEvents;
  }
}
