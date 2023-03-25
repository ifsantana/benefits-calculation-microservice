package org.example.usecases.roundup.interfaces;

import java.io.IOException;

public interface RoundUpWeeklyUseCase {
  void execute(String token) throws IOException;
}
