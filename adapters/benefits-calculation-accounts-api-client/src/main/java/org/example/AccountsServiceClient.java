package org.example;

import java.io.IOException;
import org.example.responses.accounts.Account;

public interface AccountsServiceClient {

  /**
   * This method consumes the accounts api and return account data based on the implicit token
   * information.
   *
   * @param - token
   * @return {@link Account}
   * @throws IOException
   */
  Account getAccount(String token) throws IOException;
}
