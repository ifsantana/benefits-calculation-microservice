package org.example.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.repositories.interfaces.RoundUpExecutionCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class RoundUpExecutionCacheRepositoryIT {
  private RoundUpExecutionCacheRepository cacheRepository;

  // container {
  @Container
  public GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
      .withExposedPorts(6379);

  @BeforeEach
  public void setUp() {
    cacheRepository = new RoundUpExecutionCacheRepositoryImp();
    this.cacheRepository.upsertLastProcessedTransactionDayByUserId("123", "2023-03-10T12:48:00.000Z");
  }

  @Test
  void shouldReturnCachedRoundUpDate() {
    var result = this.cacheRepository.getLastProcessedTransactionsDayByUserId("123");
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("2023-03-10T12:48:00.000Z");
  }

  @Test
  void shouldNotReturnCachedRoundUpDate() {
    var result = this.cacheRepository.getLastProcessedTransactionsDayByUserId("1");
    assertThat(result).isNullOrEmpty();
  }

  @Test
  void shouldReturnUpsertRoundUpDate() {
    this.cacheRepository.upsertLastProcessedTransactionDayByUserId("12345", "2023-03-15T12:48:00.000Z");
    var result = this.cacheRepository.getLastProcessedTransactionsDayByUserId("12345");
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("2023-03-15T12:48:00.000Z");
    this.cacheRepository.upsertLastProcessedTransactionDayByUserId("12345", "2023-03-16T12:48:00.000Z");
    var updatedResult = this.cacheRepository.getLastProcessedTransactionsDayByUserId("12345");
    assertThat(updatedResult).isNotNull();
    assertThat(updatedResult).isEqualTo("2023-03-16T12:48:00.000Z");
  }
}
