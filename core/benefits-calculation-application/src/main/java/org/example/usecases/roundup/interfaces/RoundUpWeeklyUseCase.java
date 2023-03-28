package org.example.usecases.roundup.interfaces;

import java.io.IOException;
import java.util.List;
import kotlin.Pair;
import org.example.commands.RoundUpWeeklyCommand;
import org.example.responses.txnfeed.FeedItem;

public interface RoundUpWeeklyUseCase {

  /**
   * This method executes the command responsible for round up the transactions given a date range
   * and add the rounded up difference on a user saving goal.
   *
   * @param - command
   * @return {@link Pair<Boolean,String>} the Boolean representing if the command was executed with
   * success and the String containing some message that will compose the
   * {@link org.example.responses.HttpResponse}.
   * @throws IOException
   */
  Pair<Boolean, String> execute(RoundUpWeeklyCommand command) throws IOException;

  /**
   * This method calculates the round up for a list of transactions given a data range.
   *
   * @param - list
   * @return {@link Integer} that represents the rounded up amount in minor units for a list of
   * transactions in a given date range.
   */
  Integer calculateRoundUpForManyItems(List<FeedItem> list);
}
