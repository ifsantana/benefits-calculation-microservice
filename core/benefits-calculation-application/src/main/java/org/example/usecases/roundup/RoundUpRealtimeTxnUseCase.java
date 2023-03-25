package org.example.usecases.roundup;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.example.events.AccountHolderWebhookDispatchFeedItem;
import org.example.responses.txnfeed.FeedItem;
import org.example.usecases.roundup.interfaces.RoundUpRealtimeUseCase;

public class RoundUpRealtimeTxnUseCase implements RoundUpRealtimeUseCase {

  @Override
  public void execute(AccountHolderWebhookDispatchFeedItem feedItem) {

  }

  @Override
  public Integer calculateRoundUp(FeedItem feedTxn) {
    BigDecimal initValue1;
    initValue1 = new BigDecimal(feedTxn.amount().minorUnits().toString()).movePointLeft(2);
    var roundup1 = initValue1.round(new MathContext(initValue1.scale(), RoundingMode.UP));
    var roundUpResult = roundup1.subtract(initValue1);
    Double result = roundUpResult.doubleValue()*100;
    return  result.intValue();
  }
}
