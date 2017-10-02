package com.example.common.domain;

public class EntityNotFoundException extends RuntimeException {

  private EntityId entityId;
  private Class<?> entityType;

  public EntityNotFoundException(Class<?> type, EntityId id) {
    this.entityType = type;
    this.entityId = id;
  }

  public EntityId getEntityId() {
    return entityId;
  }

  public Class<?> getEntityType() {
    return entityType;
  }

  @Override
  public String getMessage() {
    return String.format("Unable to find: %s.id={%s}", getEntityType().getSimpleName(), getEntityId());
  }
}
