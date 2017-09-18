package com.example.ogdc;

import com.example.infrastructure.DomainEntityId;

public class DCAgentId extends DomainEntityId {

  private static final long serialVersionUID = 1L;

  DCAgentId(String rawId) {
    super(rawId);
  }
}
