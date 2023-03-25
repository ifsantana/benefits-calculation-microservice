package org.example.handlers;

import com.google.inject.Inject;
import java.io.IOException;
import org.example.handlers.interfaces.RoundUpWeeklyCommandHandler;
import org.example.usecases.roundup.interfaces.RoundUpWeeklyUseCase;

public class RoundUpWeeklyTxnCommandHandler implements RoundUpWeeklyCommandHandler {

  private final RoundUpWeeklyUseCase useCase;

  @Inject
  public RoundUpWeeklyTxnCommandHandler(RoundUpWeeklyUseCase useCase) {
    this.useCase = useCase;
  }

  @Override
  public Void handle(String param) throws IOException {
     this.useCase.execute(param);
     return null;
  }
}
