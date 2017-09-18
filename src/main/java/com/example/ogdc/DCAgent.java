package com.example.ogdc;

import org.springframework.util.StringUtils;

import com.stormpath.sdk.lang.Assert;

/**
 * A Data Capture Agent.
 * 
 * @author troyh
 *
 */
public class DCAgent {

  private DCAgentId id;
  private String name;

  public DCAgentId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  /**
   * A package local DCAgent builder
   */
  static final class Builder {
    private DCAgent value;

    public Builder() {
      this.value = new DCAgent();
    }

    public DCAgent build() {
      Assert.state(StringUtils.hasText(value.getName()), "Unable to build invalid DCAgent: null/blank name");
      Assert.state(value.getId() != null, "Unable to build invalid DCAgent: null id");
      return value;
    }

    public Builder setId(DCAgentId id) {
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
