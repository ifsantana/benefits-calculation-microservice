package org.example.usecases.roundup.interfaces;

import java.io.IOException;
import java.util.List;
import org.example.responses.txnfeed.FeedItem;

public interface RoundUpWeeklyUseCase {
  Boolean execute(String token) throws IOException;

  /**
   *
   * @param - list
   * @return {@link Integer} that represents the rounded up amount in minor units.
   */
  Integer calculateRoundUpForManyItems(List<FeedItem> list);
}
