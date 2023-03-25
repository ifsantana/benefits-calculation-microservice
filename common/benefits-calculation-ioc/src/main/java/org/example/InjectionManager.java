package org.example;

import com.google.inject.AbstractModule;
import org.example.handlers.RoundUpWeeklyTxnCommandHandler;
import org.example.handlers.interfaces.RoundUpWeeklyCommandHandler;
import org.example.usecases.roundup.RoundUpRealtimeTxnUseCase;
import org.example.usecases.roundup.RoundUpWeeklyTxnUseCase;
import org.example.usecases.roundup.interfaces.RoundUpRealtimeUseCase;
import org.example.usecases.roundup.interfaces.RoundUpWeeklyUseCase;

public class InjectionManager extends AbstractModule {
  @Override
  protected void configure() {
    this.configureAdapters();
    this.configureCore();
  }

  private void configureAdapters() {
    bind(AccountsServiceClient.class).to(AccountApiClient.class);
    bind(SavingClient.class).to(SavingApiClient.class);
    bind(TxnFeedClient.class).to(TxnFeedApiClient.class);
  }

  private void configureCore() {
    bind(RoundUpWeeklyCommandHandler.class).to(RoundUpWeeklyTxnCommandHandler.class);
    bind(RoundUpWeeklyUseCase.class).to(RoundUpWeeklyTxnUseCase.class);
    bind(RoundUpRealtimeUseCase.class).to(RoundUpRealtimeTxnUseCase.class);
  }
}