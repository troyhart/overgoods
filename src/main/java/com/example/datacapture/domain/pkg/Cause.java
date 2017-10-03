package com.example.datacapture.domain.pkg;

public class Cause {

  private String description;

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "{\"description\": \"" + description + "\"}";
  }

  /**
   * A package local Cause builder
   */
  static final class Builder {
    private Cause value;

    public Builder() {
      this.value = new Cause();
    }

    public Cause build() {
      return value;
    }

    Builder setDescription(String description) {
      value.description = description;
      return this;
    }
  }
}
