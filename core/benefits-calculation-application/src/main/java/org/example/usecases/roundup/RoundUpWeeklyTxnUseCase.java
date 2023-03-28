package org.example.usecases.roundup;

import com.google.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import kotlin.Pair;
import org.example.AccountsServiceClient;
import org.example.SavingServiceClient;
import org.example.TxnFeedServiceClient;
import org.example.commands.RoundUpWeeklyCommand;
import org.example.repositories.interfaces.RoundUpExecutionCacheRepository;
import org.example.responses.HttpResponse;
import org.example.responses.txnfeed.FeedItem;
import org.example.usecases.roundup.interfaces.RoundUpWeeklyUseCase;

public class RoundUpWeeklyTxnUseCase implements RoundUpWeeklyUseCase {

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
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
  public Pair<Boolean, String> execute(RoundUpWeeklyCommand command) throws IOException {
    var account = this.accountsServiceClient.getAccount(command.token());
    var lastExecutionCache = this.cacheRepository.getLastProcessedTransactionsDayByUserId(
        account.accountUid());

    if (!isDateRangeValidToProceed(lastExecutionCache, command.minTransactionTimestamp(),
        command.maxTransactionTimestamp())) {
      return new Pair<>(false, MessageFormat.format(
          "Invalid input parameters. You must select a date range after the last execution date. Last Execution Date: {0}",
          lastExecutionCache));
    }

    var feedItems = this.txnFeedServiceClient.getTxnFeedItemsByAccountId(command.token(),
        account.accountUid(),
        command.minTransactionTimestamp(), command.maxTransactionTimestamp());
    var savingGoal = this.savingServiceClient.getSavingsByAccountId(command.token(),
        account.accountUid());
    var roundUpTotal = this.calculateRoundUpForManyItems(feedItems.feedItems());
    var transactionResult = this.savingServiceClient.addMoneyToSaving(command.token(),
        account.accountUid(), savingGoal, roundUpTotal);
    this.cacheRepository.upsertLastProcessedTransactionDayByUserId(account.accountUid(),
        command.maxTransactionTimestamp());

    return new Pair<>(transactionResult.success(), "Success.");
  }

  @Override
  public Integer calculateRoundUpForManyItems(List<FeedItem> txnList) {
    Double total = Double.MIN_VALUE;
    for (FeedItem item : txnList) {
      BigDecimal txnValue;
      txnValue = new BigDecimal(item.amount().minorUnits().toString()).movePointLeft(2);
      var roundedUpDiffValue = txnValue.round(new MathContext(txnValue.scale(), RoundingMode.UP));
      var roundUpResult = roundedUpDiffValue.subtract(txnValue);
      total += roundUpResult.doubleValue();
    }
    Double result = total * 100;
    return result.intValue();
  }

  private boolean isDateRangeValidToProceed(String lastExecutionCache,
      String minTransactionTimestamp, String maxTransactionTimestamp) {
    try {
      if (lastExecutionCache == null) {
        return true;
      }

      var startDateRange = LocalDateTime.parse(minTransactionTimestamp, formatter);
      var endDateRange = LocalDateTime.parse(maxTransactionTimestamp, formatter);
      var lastExecution = LocalDateTime.parse(lastExecutionCache, formatter);

      return lastExecution.isBefore(startDateRange) && lastExecution.isBefore(endDateRange);
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
