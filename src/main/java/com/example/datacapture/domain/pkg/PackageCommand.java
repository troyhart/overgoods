package com.example.datacapture.domain.pkg;

import org.springframework.util.StringUtils;

import com.example.common.domain.Command;
import com.example.datacapture.domain.agent.Agent;

public abstract class PackageCommand implements Command {

  private PackageId packageId;
  private Agent agent;

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

  public Agent getAgent() {
    return agent;
  }

  public final PackageCommand setAgent(Agent agent) {
    this.agent = agent;
    return this;
  }
}
