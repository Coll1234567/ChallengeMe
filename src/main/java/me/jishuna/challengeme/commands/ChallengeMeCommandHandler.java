package me.jishuna.challengeme.commands;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.commonlib.commands.ArgumentCommandHandler;
import me.jishuna.commonlib.commands.SimpleCommandHandler;

public class ChallengeMeCommandHandler extends ArgumentCommandHandler {
	public ChallengeMeCommandHandler(ChallengeMe plugin) {
		super(plugin.getMessageConfig());

		SimpleCommandHandler menuHandler = new ChallengeCommand(plugin);

		setDefault(menuHandler);
		addArgumentExecutor("challenges", menuHandler);
		addArgumentExecutor("reload", new ReloadCommand(plugin));
	}
}
