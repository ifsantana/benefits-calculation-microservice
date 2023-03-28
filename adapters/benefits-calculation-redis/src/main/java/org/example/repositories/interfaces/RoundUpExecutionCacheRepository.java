package org.example.repositories.interfaces;

public interface RoundUpExecutionCacheRepository {

  /**
   * This method returns the cached date of the last processed transactions on the last round up
   * processing to avoid to process the same transactions.
   *
   * @param - userId
   * @return - {@link String} representing the date of the last processed transactions on this format
   * "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'".
   */
  String getLastProcessedTransactionsDayByUserId(String userId);

  /**
   * This method is responsible to store on redis cache, the date of the last processed transactions.
   *
   * @param - userId
   * @param - lastProcessedTransactionDay
   */
  void upsertLastProcessedTransactionDayByUserId(String userId, String lastProcessedTransactionDay);
}
