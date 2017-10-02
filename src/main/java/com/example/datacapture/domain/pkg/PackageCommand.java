package com.example.datacapture.domain.pkg;

import org.springframework.util.StringUtils;

import com.example.common.domain.Command;
import com.example.datacapture.domain.agent.AgentId;

public abstract class PackageCommand implements Command {

  private PackageId packageId;
  private AgentId agentId;

  public String getRawPackageId() {
    return packageId == null ? null : packageId.getRawId();
  }

  public final PackageCommand setRawPackageId(String rawPackageId) {
    if (StringUtils.hasText(rawPackageId)) {
      this.packageId = new PackageId(rawPackageId);
    }
    else {
      this.packageId = null;
    }
    return this;
  }

  public PackageId getPackageId() {
    return this.packageId;
  }

  public PackageCommand setPackageId(PackageId packageId) {
    this.packageId = packageId;
    return this;
  }

  public String getRawAgentId() {
    return agentId == null ? null : agentId.getRawId();
  }

  public final PackageCommand setRawAgentId(String rawAgentId) {
    if (StringUtils.hasText(rawAgentId)) {
      this.agentId = new AgentId(rawAgentId);
    }
    else {
      this.agentId = null;
    }
    return this;
  }

  public AgentId getAgentId() {
    return this.agentId;
  }

  public PackageCommand setAgentId(AgentId agentId) {
    this.agentId = agentId;
    return this;
  }
}
