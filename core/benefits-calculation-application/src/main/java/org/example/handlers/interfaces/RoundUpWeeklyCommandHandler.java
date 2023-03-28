package org.example.handlers.interfaces;

import kotlin.Pair;
import org.example.commands.RoundUpWeeklyCommand;
import org.example.handlers.CommandHandler;

public interface RoundUpWeeklyCommandHandler extends CommandHandler<RoundUpWeeklyCommand, Pair<Boolean, String>> {
}
