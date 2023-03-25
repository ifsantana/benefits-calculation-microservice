package org.example;

import java.io.IOException;
import org.example.responses.savings.CreateSavingGoalResponse;
import org.example.responses.savings.SavingGoal;
import org.example.responses.savings.SavingGoalTransferResponse;

public interface SavingServiceClient {

  /**
   *
   * @param - token
   * @param - accountUid
   * @return {@link SavingGoal}
   * @throws IOException
   */
  SavingGoal getSavingsByAccountId(String token, String accountUid) throws IOException;

  /**
   *
   * @param - token
   * @param - accountUid
   * @param - savingGoalUid
   * @return {@link SavingGoal}
   * @throws IOException
   */
  SavingGoal getSavingGoal(String token, String accountUid, String savingGoalUid)
      throws IOException;

  /**
   *
   * @param - token
   * @param - accountUid
   * @return {@link CreateSavingGoalResponse}
   * @throws IOException
   */
  CreateSavingGoalResponse createSaving(String token, String accountUid) throws IOException;

  /**
   *
   * @param - token
   * @param - accountResponse
   * @param - savingGoal
   * @param - amountToAdd
   * @return {@link SavingGoalTransferResponse}
   * @throws IOException
   */
  SavingGoalTransferResponse addMoneyToSaving(String token, String accountUid, SavingGoal savingGoal, Integer amountToAdd) throws IOException;
}
