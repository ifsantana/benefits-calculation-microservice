package org.example;

import java.io.IOException;
import org.example.responses.accounts.Account;

public interface AccountsServiceClient {

  /**
   *
   * @param - token
   * @return {@link Account}
   * @throws IOException
   */
  Account getAccount(String token) throws IOException;
}
