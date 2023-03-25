package org.example.usecases.roundup;

import com.google.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import org.example.AccountsServiceClient;
import org.example.SavingServiceClient;
import org.example.TxnFeedServiceClient;
import org.example.responses.txnfeed.FeedItem;
import org.example.usecases.roundup.interfaces.RoundUpWeeklyUseCase;

public class RoundUpWeeklyTxnUseCase implements RoundUpWeeklyUseCase {
  private final AccountsServiceClient accountsServiceClient;
  private final TxnFeedServiceClient txnFeedServiceClient;
  private final SavingServiceClient savingServiceClient;

  @Inject
  public RoundUpWeeklyTxnUseCase(AccountsServiceClient accountsServiceClient,
      TxnFeedServiceClient txnFeedServiceClient, SavingServiceClient savingServiceClient) {
    this.accountsServiceClient = accountsServiceClient;
    this.txnFeedServiceClient = txnFeedServiceClient;
    this.savingServiceClient = savingServiceClient;
  }

  @Override
  public Boolean execute(String token) throws IOException {
    var account = this.accountsServiceClient.getAccount(token);
    var feedItems = this.txnFeedServiceClient.getTxnFeedItemsByAccountId(token, account.accountUid());
    var savingGoal = this.savingServiceClient.getSavingsByAccountId(token, account.accountUid());
    var roundUpTotal = this.calculateRoundUp(feedItems.feedItems());
    var transactionResult = this.savingServiceClient.addMoneyToSaving(token, account.accountUid(), savingGoal, roundUpTotal);
    return transactionResult.success();
  }

  private Integer calculateRoundUp(List<FeedItem> txnList) {
    Double total = Double.MIN_VALUE;
    for(FeedItem item : txnList) {
      BigDecimal initValue1;
      initValue1 = new BigDecimal(item.amount().minorUnits().toString()).movePointLeft(2);
      var roundup1 = initValue1.round(new MathContext(initValue1.scale(), RoundingMode.UP));
      var roundUpResult = roundup1.subtract(initValue1);
      total += roundUpResult.doubleValue();
    }
    Double result = total*100;
    return  result.intValue();
  }
}
