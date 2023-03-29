package org.examples.usecases;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.example.responses.Amount;
import org.example.responses.txnfeed.FeedItem;
import org.example.usecases.roundup.RoundUpWeeklyTxnUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoundUpWeeklyTxnUseCaseTest {

  @InjectMocks
  private RoundUpWeeklyTxnUseCase useCase;
  private List<FeedItem> txnFeedItems;

  @BeforeEach
  void setup() {
    this.txnFeedItems = buildTxnFeed();
  }

  /**
   * UseCase: For a customer, take all the transactions in a given week and round them up to the
   * nearest pound. For example with spending of £4.35, £5.20 and £0.87, the round-up would be
   * £1.58.
   */
  @Test
  void shouldCalculateRoundUpCorrectly() {
    var result = this.useCase.calculateRoundUpForManyItems(this.txnFeedItems);
    assertThat(result).isEqualTo(158);
  }

  public List<FeedItem> buildTxnFeed() {
    return List.of(
        new FeedItem.Builder().amount(new Amount("GBP", 435)).build(),
        new FeedItem.Builder().amount(new Amount("GBP", 520)).build(),
        new FeedItem.Builder().amount(new Amount("GBP", 87)).build()
    );
  }
}
