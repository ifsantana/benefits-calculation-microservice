package org.example.factories;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import kotlin.Pair;
import org.example.commands.RoundUpWeeklyCommand;
import org.example.factories.interfaces.RoundUpWeeklyCommandFactory;

public class RoundUpWeeklyCommandFactoryImp implements RoundUpWeeklyCommandFactory {
  private static final String MIN_TXN_TIMESTAMP = "minTransactionTimestamp";
  private static final String MAX_TXN_TIMESTAMP = "maxTransactionTimestamp";

  @Override
  public RoundUpWeeklyCommand create(Pair<String, HashMap<String, String>> params)
      throws IOException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    var minTimestamp = params.component2().get(MIN_TXN_TIMESTAMP);
    var maxTimestamp = params.component2().get(MAX_TXN_TIMESTAMP);
    return new RoundUpWeeklyCommand(params.component1(), minTimestamp, maxTimestamp);
  }
}
