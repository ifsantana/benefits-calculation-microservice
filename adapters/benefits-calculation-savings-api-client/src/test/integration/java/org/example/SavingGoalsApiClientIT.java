package org.example;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SavingGoalsApiClientIT {

  @Spy
  private SavingGoalsServiceClient savingServiceClient;

  @Test
  void getSavingsByAccountId() {

  }

  @Test
  void getSavingGoal() {

  }

  @Test
  void createSaving() {

  }

  @Test
  void addMoneyToSavingGoal() {

  }
}
