package org.example;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.example.responses.Amount;
import org.example.responses.savings.CreateSavingGoalResponse;
import org.example.responses.savings.SavingGoal;
import org.example.responses.savings.SavingGoalTransferResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SavingGoalsApiClientIT {

  @Spy
  private SavingGoalsServiceClient savingServiceClient;

  @Test
  void getSavingsByAccountId() throws IOException {
    when(this.savingServiceClient.getSavingsByAccountId(anyString(), anyString())).thenReturn(mockSavingGoal());
  }

  @Test
  void getSavingGoal() throws IOException {
    when(this.savingServiceClient.getSavingGoal(anyString(),anyString(),anyString())).thenReturn(mockSavingGoal());
  }

  @Test
  void createSaving() throws IOException {
    var createSavingGoalResponse = new CreateSavingGoalResponse(UUID.randomUUID().toString(), true);
    when(this.savingServiceClient.createSavingGoal(anyString(), anyString())).thenReturn(createSavingGoalResponse);
  }

  @Test
  void addMoneyToSavingGoal() throws IOException {
    var savingGoalTransfer = new SavingGoalTransferResponse(UUID.randomUUID().toString(), true);
    when(this.savingServiceClient.addMoneyToSavingGoal(anyString(), anyString(), any(), anyInt())).thenReturn(savingGoalTransfer);
  }

  private SavingGoal mockSavingGoal() {
    return new SavingGoal(UUID.randomUUID().toString(), "Dream Trip", new Amount("GBP", 100), new Amount("GBP", 10), 10);
  }

  private List<SavingGoal> mockSavingGoals() {
    return List.of(
//        new SavingGoal(),
//        new SavingGoal(),
//        new SavingGoal(),
    );
  }
}
