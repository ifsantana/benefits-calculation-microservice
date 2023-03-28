package org.example.handlers;

import com.google.inject.Inject;
import java.io.IOException;
import kotlin.Pair;
import org.example.commands.RoundUpWeeklyCommand;
import org.example.handlers.interfaces.RoundUpWeeklyCommandHandler;
import org.example.usecases.roundup.interfaces.RoundUpWeeklyUseCase;

public class RoundUpWeeklyTxnCommandHandler implements RoundUpWeeklyCommandHandler {

  private final RoundUpWeeklyUseCase useCase;

  @Inject
  public RoundUpWeeklyTxnCommandHandler(RoundUpWeeklyUseCase useCase) {
    this.useCase = useCase;
  }

  @Override
  public Pair<Boolean, String> handle(RoundUpWeeklyCommand command) throws IOException {
      return this.useCase.execute(command);
  }
}
