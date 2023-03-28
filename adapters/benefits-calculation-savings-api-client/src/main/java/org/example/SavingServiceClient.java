package org.example;

import java.io.IOException;
import org.example.responses.savings.CreateSavingGoalResponse;
import org.example.responses.savings.SavingGoal;
import org.example.responses.savings.SavingGoalTransferResponse;

public interface SavingServiceClient {

  /**
   * This method consumes saving goals api and get all saving goals by account id.
   *
   * @param - token
   * @param - accountUid
   * @return {@link SavingGoal}
   * @throws IOException
   */
  SavingGoal getSavingsByAccountId(String token, String accountUid) throws IOException;

  /**
   * This method consumes saving goals api and get an specific saving goal by account id and saving
   * goal id.
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
   * This method consumes saving goals api and performs a saving goal creation for a specific
   * account id.
   *
   * @param - token
   * @param - accountUid
   * @return {@link CreateSavingGoalResponse}
   * @throws IOException
   */
  CreateSavingGoalResponse createSaving(String token, String accountUid) throws IOException;

  /**
   * This method consumes saving goals api and add money for an specific saving goal.
   *
   * @param - token
   * @param - accountResponse
   * @param - savingGoal
   * @param - amountToAdd
   * @return {@link SavingGoalTransferResponse}
   * @throws IOException
   */
  SavingGoalTransferResponse addMoneyToSaving(String token, String accountUid,
      SavingGoal savingGoal, Integer amountToAdd) throws IOException;
}
