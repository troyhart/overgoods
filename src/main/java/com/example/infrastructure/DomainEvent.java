package com.example.infrastructure;

import java.io.Serializable;
import java.util.Date;

public interface DomainEvent {
  Serializable id();
  long version();
  Date occurredOn();
}
