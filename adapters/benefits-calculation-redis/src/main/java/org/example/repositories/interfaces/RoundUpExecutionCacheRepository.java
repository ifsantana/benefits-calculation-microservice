package org.example.repositories.interfaces;

public interface RoundUpExecutionCacheRepository {

  /**
   *
   * @param - userId
   * @return
   */
  String getLastProcessedTransactionsDayByUserId(String userId);

  /**
   *
   * @param - userId
   * @param - lastProcessedTransactionDay
   */
  void upsertLastProcessedTransactionDayByUserId(String userId, String lastProcessedTransactionDay);
}
