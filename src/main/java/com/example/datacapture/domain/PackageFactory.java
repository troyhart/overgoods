package com.example.datacapture.domain;

import org.springframework.util.Assert;

import com.example.datacapture.domain.agent.Agent;

public class PackageFactory {

//  @Autowired
  static final PackageRepository packageRepository = new PackageRepository();

  /**
   * @param cause
   * 
   * @return a new {@link Package} instance
   * 
   * @see Cause.Builder
   */
  public Package newOvergood(Agent capturedBy, Cause cause) {
    Assert.notNull(cause, "null cause");
    return new Package(packageRepository.nextIdentity(), capturedBy, cause);
  }
}
