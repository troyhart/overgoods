package com.example.datacapture.domain.pkg;

import org.springframework.util.Assert;

import com.example.datacapture.domain.agent.Agent;
import com.example.datacapture.domain.agent.AgentId;
import com.example.datacapture.domain.agent.AgentRepository;

public class PackageFactory {

  private static final PackageFactory INSTANCE = new PackageFactory();

  public static final PackageFactory instance() {
    return INSTANCE;
  }

  private PackageFactory() {
  }

  /**
   * @param cause
   * 
   * @return a new {@link Package} instance
   * 
   * @see Cause.Builder
   */
  public Package newPackage(PackageRepository packageRepository, AgentRepository agentRepository, String description,
      AgentId describedBy) {
    Assert.notNull(packageRepository, "null packageRepository");
    Assert.notNull(agentRepository, "null agentRepository");
    Assert.notNull(describedBy, "null describedBy");
    Assert.notNull(description, "null description");

    Agent agent = agentRepository.find(describedBy);
    Cause cause = new Cause.Builder().setDescription(description).build();

    return new Package(packageRepository.nextIdentity(), agent, cause);
  }
}
