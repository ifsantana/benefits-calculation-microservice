package org.example.handlers;


import java.io.IOException;

public interface CommandHandler<TCommand, TResult> {

  /**
   * This method is an abstraction of command handlers to allow a custom implementations
   * depending on the necessities allowing a generic command and result types to be defined
   * on the concrete implementation.
   * @param - param
   * @return - result of the command execution.
   * @throws IOException
   */
  TResult handle(TCommand param) throws IOException;
}
