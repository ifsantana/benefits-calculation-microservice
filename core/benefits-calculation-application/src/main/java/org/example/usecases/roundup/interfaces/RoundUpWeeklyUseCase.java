package org.example.usecases.roundup.interfaces;

import java.io.IOException;
import java.util.List;
import kotlin.Pair;
import org.example.commands.RoundUpWeeklyCommand;
import org.example.responses.txnfeed.FeedItem;

public interface RoundUpWeeklyUseCase {
  Pair<Boolean, String> execute(RoundUpWeeklyCommand command) throws IOException;

  /**
   *
   * @param - list
   * @return {@link Integer} that represents the rounded up amount in minor units.
   */
  Integer calculateRoundUpForManyItems(List<FeedItem> list);
}
