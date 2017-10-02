package com.example.common.domain;

public abstract class ChainedHandler<T extends Command> implements Handler<T> {

  private Handler<T> next;

  public ChainedHandler(Handler<T> next) {
    this.next = next;
  }

  protected Handler<T> next() {
    return next;
  }
}
