package com.example.common.infrastructure;

import com.example.common.domain.ChainedHandler;
import com.example.common.domain.Command;
import com.example.common.domain.Handler;

public class TransactionHandler<T extends Command> extends ChainedHandler<T> {

  public TransactionHandler(Handler<T> next) {
    super(next);
  }

  @Override
  public void handle(T command) {
    // TODO: handle transaction
    System.out.println("[transaction-begin]");
    next().handle(command);
    System.out.println("[transaction-end]");
  }

}
