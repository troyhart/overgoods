package com.example.common.domain;

public interface Handler<T extends Command> {
  void handle(T command);
}
