package com.example.ogdc;

import org.springframework.util.Assert;

public class OvergoodsFactory {

//  @Autowired
  static final OvergoodsRepository ogrepo = new OvergoodsRepository();

  /**
   * @param cause
   * 
   * @return a new {@link Overgoods} instance
   * 
   * @see Cause.Builder
   */
  Overgoods newOvergood(DCAgent capturedBy, Cause cause) {
    Assert.notNull(cause, "null cause");// stranage error message, but it's consistent!
    return new Overgoods(ogrepo.nextIdentity(), capturedBy, cause);
  }
}
