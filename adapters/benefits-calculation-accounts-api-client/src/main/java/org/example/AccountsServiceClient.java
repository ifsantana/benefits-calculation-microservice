package org.example;

import static org.example.constants.StarlingApiConstants.ACCOUNT_COLLECTION_SEGMENT;
import static org.example.constants.StarlingApiConstants.API_PREFIX;
import static org.example.constants.StarlingApiConstants.API_V2;
import static org.example.constants.StarlingApiConstants.HOST;
import static org.example.constants.StarlingApiConstants.SCHEME;

import java.io.IOException;
import okhttp3.HttpUrl;
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

  /**
   * This method builds the URL used to get accounts from accounts api.
   * @return {@link HttpUrl} representing the request URL for accounts api
   */
  default HttpUrl buildGetAccountUrl() {
    return new HttpUrl
        .Builder()
        .scheme(SCHEME)
        .host(HOST)
        .addPathSegment(API_PREFIX)
        .addPathSegment(API_V2)
        .addPathSegment(ACCOUNT_COLLECTION_SEGMENT)
        .build();
  }
}
