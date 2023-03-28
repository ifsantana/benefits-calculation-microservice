package org.example.repositories;

import java.text.MessageFormat;
import org.example.repositories.interfaces.RoundUpExecutionCacheRepository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RoundUpExecutionCacheRepositoryImp implements RoundUpExecutionCacheRepository {

  private static final String LAST_ROUND_UP_EXECUTION_DATE = "benefitsCalculation:roundUp:lastExecutionDate:userId:";
  private final JedisPool jedisPool;

  public RoundUpExecutionCacheRepositoryImp() {
    this.jedisPool = new JedisPool("localhost", 6379);
  }

  @Override
  public String getLastProcessedTransactionsDayByUserId(String userId) {
    var key = MessageFormat.format("{0}{1}", LAST_ROUND_UP_EXECUTION_DATE, userId);
    String value;
    try (Jedis jedis = this.jedisPool.getResource()) {
      value = jedis.get(key);
    }
    return value;
  }

  @Override
  public void upsertLastProcessedTransactionDayByUserId(String userId,
      String lastProcessedTransactionDay) {
    var key = MessageFormat.format("{0}{1}", LAST_ROUND_UP_EXECUTION_DATE, userId);
    try (Jedis jedis = this.jedisPool.getResource()) {
      jedis.set(key, lastProcessedTransactionDay);
    }
  }
}
