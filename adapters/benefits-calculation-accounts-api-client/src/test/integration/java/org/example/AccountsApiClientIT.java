package org.example;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.example.responses.accounts.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountsApiClientIT {
  @Spy
  private AccountsServiceClient accountsServiceClient;

  @Test
  void testGetAccounts() throws IOException {
    var testAccount = buildAccount();
    when(this.accountsServiceClient.getAccount("mocked.token")).thenReturn(testAccount);
    var result = this.accountsServiceClient.getAccount("mocked.token");
    assertThat(result).isEqualTo(testAccount);
  }

  @Test
  void testGetAccountsUrlBuilder() {
    var result = this.accountsServiceClient.buildGetAccountUrl();
    assertThat(result.url().toString()).isEqualTo("https://api-sandbox.starlingbank.com/api/v2/accounts");
  }

  public Account buildAccount(){
    return new Account("02348f0f-1a63-4e74-bed1-80f8fb1d9ed9", "PRIMARY", "0933523e-a58b-4e64-bf9b-76f4ae079d6f", "GBP", "2023-03-26T11:45:08.739Z", "Personal");
  }
}
