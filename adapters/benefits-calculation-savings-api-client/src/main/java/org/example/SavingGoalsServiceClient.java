package org.example;

import static org.example.constants.StarlingApiConstants.ACCOUNT_RESOURCE_SEGMENT;
import static org.example.constants.StarlingApiConstants.API_PREFIX;
import static org.example.constants.StarlingApiConstants.API_V2;
import static org.example.constants.StarlingApiConstants.HOST;
import static org.example.constants.StarlingApiConstants.SAVINGS_ADD_MONEY_SEGMENT;
import static org.example.constants.StarlingApiConstants.SAVINGS_RESOURCE_SEGMENT;
import static org.example.constants.StarlingApiConstants.SCHEME;

import java.io.IOException;
import java.util.UUID;
import okhttp3.HttpUrl;
import org.example.responses.savings.CreateSavingGoalResponse;
import org.example.responses.savings.SavingGoal;
import org.example.responses.savings.SavingGoalTransferResponse;

public interface SavingGoalsServiceClient {

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
   * During the round process if there's no existing saving goal, it creates a default saving goal
   * to save money to the user's dream trip.
   *
   * @param - token
   * @param - accountUid
   * @return {@link CreateSavingGoalResponse}
   * @throws IOException
   */
  CreateSavingGoalResponse createSavingGoal(String token, String accountUid) throws IOException;

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
  SavingGoalTransferResponse addMoneyToSavingGoal(String token, String accountUid,
      SavingGoal savingGoal, Integer amountToAdd) throws IOException;


  default HttpUrl buildSavingGoalsUrl(String accountUid) {
    return new HttpUrl
        .Builder()
        .scheme(SCHEME)
        .host(HOST)
        .addPathSegment(API_PREFIX)
        .addPathSegment(API_V2)
        .addPathSegment(ACCOUNT_RESOURCE_SEGMENT)
        .addPathSegment(accountUid)
        .addPathSegment(SAVINGS_RESOURCE_SEGMENT)
        .build();
  }

  default HttpUrl buildSavingGoalByIdUrl(String accountUid, String savingGoalUid) {
    return new HttpUrl
        .Builder()
        .scheme(SCHEME)
        .host(HOST)
        .addPathSegment(API_PREFIX)
        .addPathSegment(API_V2)
        .addPathSegment(ACCOUNT_RESOURCE_SEGMENT)
        .addPathSegment(accountUid)
        .addPathSegment(SAVINGS_RESOURCE_SEGMENT)
        .addPathSegment(savingGoalUid)
        .build();
  }

  default HttpUrl buildTopUpSavingUrl(String accountUid, String savingGoalUid) {
    return new HttpUrl
        .Builder()
        .scheme(SCHEME)
        .host(HOST)
        .addPathSegment(API_PREFIX)
        .addPathSegment(API_V2)
        .addPathSegment(ACCOUNT_RESOURCE_SEGMENT)
        .addPathSegment(accountUid)
        .addPathSegment(SAVINGS_RESOURCE_SEGMENT)
        .addPathSegment(savingGoalUid)
        .addPathSegment(SAVINGS_ADD_MONEY_SEGMENT)
        .addPathSegment(UUID.randomUUID().toString())
        .build();
  }
}
