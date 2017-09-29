package com.example.datacapture.domain.agent;

import org.springframework.util.StringUtils;

import com.stormpath.sdk.lang.Assert;

/**
 * A Data Capture Agent.
 * 
 * @author troyh
 *
 */
public class Agent {

  private AgentId id;
  private String name;

  public AgentId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  /**
   * A package local Agent builder
   */
  static final class Builder {
    private Agent value;

    public Builder() {
      this.value = new Agent();
    }

    public Agent build() {
      Assert.state(StringUtils.hasText(value.getName()), "Unable to build invalid Agent: null/blank name");
      Assert.state(value.getId() != null, "Unable to build invalid Agent: null id");
      return value;
    }

    public Builder setId(AgentId id) {
      value.id = id;
      return this;
    }

    public Builder setName(String name) {
      value.name = name;
      return this;
    }
  }

  @Override
  public String toString() {
    return getName() + " [" + getId() + "]";
  }
}
