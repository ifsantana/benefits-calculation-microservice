package org.example.usecases.roundup;

import com.google.inject.Inject;
import java.io.IOException;
import org.example.AccountsServiceClient;
import org.example.usecases.roundup.interfaces.RoundUpWeeklyUseCase;

public class RoundUpWeeklyTxnUseCase implements RoundUpWeeklyUseCase {

  private final AccountsServiceClient accountsServiceClient;

  @Inject
  public RoundUpWeeklyTxnUseCase(AccountsServiceClient accountsServiceClient) {
    this.accountsServiceClient = accountsServiceClient;
  }

  @Override
  public void execute(String token) throws IOException {
    var account = this.accountsServiceClient.getAccount(token);
  }
}
