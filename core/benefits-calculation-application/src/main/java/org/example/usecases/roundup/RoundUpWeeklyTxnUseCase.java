package org.example.usecases.roundup;

import com.google.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.example.AccountsServiceClient;
import org.example.SavingServiceClient;
import org.example.TxnFeedServiceClient;
import org.example.commands.RoundUpWeeklyCommand;
import org.example.repositories.interfaces.RoundUpExecutionCacheRepository;
import org.example.responses.txnfeed.FeedItem;
import org.example.usecases.roundup.interfaces.RoundUpWeeklyUseCase;

public class RoundUpWeeklyTxnUseCase implements RoundUpWeeklyUseCase {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
  private final AccountsServiceClient accountsServiceClient;
  private final TxnFeedServiceClient txnFeedServiceClient;
  private final SavingServiceClient savingServiceClient;
  private final RoundUpExecutionCacheRepository cacheRepository;

  @Inject
  public RoundUpWeeklyTxnUseCase(AccountsServiceClient accountsServiceClient,
      TxnFeedServiceClient txnFeedServiceClient, SavingServiceClient savingServiceClient,
      RoundUpExecutionCacheRepository cacheRepository) {
    this.accountsServiceClient = accountsServiceClient;
    this.txnFeedServiceClient = txnFeedServiceClient;
    this.savingServiceClient = savingServiceClient;
    this.cacheRepository = cacheRepository;
  }

  @Override
  public Boolean execute(RoundUpWeeklyCommand command) throws IOException {
    var account = this.accountsServiceClient.getAccount(command.token());

    if(!isDateRangeValidToProceed(account.accountUid(), command.minTransactionTimestamp(), command.maxTransactionTimestamp()))
      return false;

    var feedItems = this.txnFeedServiceClient.getTxnFeedItemsByAccountId(command.token(), account.accountUid(),
        command.minTransactionTimestamp(), command.maxTransactionTimestamp());
    var savingGoal = this.savingServiceClient.getSavingsByAccountId(command.token(), account.accountUid());
    var roundUpTotal = this.calculateRoundUpForManyItems(feedItems.feedItems());
    var transactionResult = this.savingServiceClient.addMoneyToSaving(command.token(), account.accountUid(), savingGoal, roundUpTotal);
    this.cacheRepository.upsertLastProcessedTransactionDayByUserId(account.accountUid(),
        command.maxTransactionTimestamp());
    return transactionResult.success();
  }

  @Override
  public Integer calculateRoundUpForManyItems(List<FeedItem> txnList) {
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

  private boolean isDateRangeValidToProceed(String accountId, String minTransactionTimestamp, String maxTransactionTimestamp) {
    try {
      var startDateRange = LocalDateTime.parse(minTransactionTimestamp, formatter);
      var endDateRange = LocalDateTime.parse(maxTransactionTimestamp, formatter);

      var days = Duration.between(startDateRange, endDateRange).toDays();

      if (days > 7)
        return false;

      var lastExecutionCache =  this.cacheRepository.getLastProcessedTransactionsDayByUserId(accountId);

      if(lastExecutionCache == null)
        return true;

      var lastExecution = LocalDateTime.parse(lastExecutionCache, formatter);

      return lastExecution.isBefore(startDateRange) && lastExecution.isBefore(endDateRange);
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
