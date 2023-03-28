package org.example.handlers;


import java.io.IOException;

public interface CommandHandler<TCommand, TResult> {

  /**
   *
   * @param - param
   * @return
   * @throws IOException
   */
  TResult handle(TCommand param) throws IOException;
}
