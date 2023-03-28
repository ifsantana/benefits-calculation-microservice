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
    BigDecimal txnValue;
    txnValue = new BigDecimal(feedTxn.amount().minorUnits().toString()).movePointLeft(2);
    var roundedUpDiffValue = txnValue.round(new MathContext(txnValue.scale(), RoundingMode.UP));
    var roundUpResult = roundedUpDiffValue.subtract(txnValue);
    Double result = roundUpResult.doubleValue()*100;
    return  result.intValue();
  }
}
