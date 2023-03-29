package org.example;

import com.google.inject.AbstractModule;
import org.example.factories.RoundUpWeeklyCommandFactoryImp;
import org.example.factories.interfaces.RoundUpWeeklyCommandFactory;
import org.example.handlers.RoundUpRealtimeTxnCommandHandler;
import org.example.handlers.RoundUpWeeklyTxnCommandHandler;
import org.example.handlers.interfaces.RoundUpRealtimeCommandHandler;
import org.example.handlers.interfaces.RoundUpWeeklyCommandHandler;
import org.example.repositories.RoundUpExecutionCacheRepositoryImp;
import org.example.repositories.interfaces.RoundUpExecutionCacheRepository;
import org.example.usecases.roundup.RoundUpRealtimeTxnUseCase;
import org.example.usecases.roundup.RoundUpWeeklyTxnUseCase;
import org.example.usecases.roundup.interfaces.RoundUpRealtimeUseCase;
import org.example.usecases.roundup.interfaces.RoundUpWeeklyUseCase;

public class InjectionManager extends AbstractModule {
  @Override
  protected void configure() {
    this.configureAdapters();
    this.configureCore();
    this.configureCommon();
  }

  private void configureCommon() {
    bind(RoundUpWeeklyCommandFactory.class).to(RoundUpWeeklyCommandFactoryImp.class);
  }

  private void configureAdapters() {
    bind(AccountsServiceClient.class).to(AccountApiClient.class);
    bind(SavingGoalsServiceClient.class).to(SavingGoalsApiClient.class);
    bind(TxnFeedServiceClient.class).to(TxnFeedApiClient.class);
    bind(RoundUpExecutionCacheRepository.class).to(RoundUpExecutionCacheRepositoryImp.class);
  }

  private void configureCore() {
    bind(RoundUpWeeklyCommandHandler.class).to(RoundUpWeeklyTxnCommandHandler.class);
    bind(RoundUpRealtimeCommandHandler.class).to(RoundUpRealtimeTxnCommandHandler.class);
    bind(RoundUpWeeklyUseCase.class).to(RoundUpWeeklyTxnUseCase.class);
    bind(RoundUpRealtimeUseCase.class).to(RoundUpRealtimeTxnUseCase.class);
  }
}
