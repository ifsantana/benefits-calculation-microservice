package org.example.factories.interfaces;

import java.util.HashMap;
import kotlin.Pair;
import org.example.commands.RoundUpWeeklyCommand;

public interface RoundUpWeeklyCommandFactory extends Factory<RoundUpWeeklyCommand, Pair<String, HashMap<String, String>>> {
}
