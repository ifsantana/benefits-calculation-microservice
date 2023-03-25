package org.example.handlers;


import java.io.IOException;

public interface CommandHandler<TCommand, TResult> {
  TResult handle(TCommand param) throws IOException;
}
