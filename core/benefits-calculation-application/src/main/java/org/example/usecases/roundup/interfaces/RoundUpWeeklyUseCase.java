package org.example.usecases.roundup.interfaces;

import java.io.IOException;

public interface RoundUpWeeklyUseCase {
  Boolean execute(String token) throws IOException;
}
