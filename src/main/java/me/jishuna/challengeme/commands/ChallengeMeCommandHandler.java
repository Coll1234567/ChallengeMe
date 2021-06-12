package me.jishuna.challengeme.commands;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.commonlib.commands.ArgumentCommandHandler;

public class ChallengeMeCommandHandler extends ArgumentCommandHandler {
	public ChallengeMeCommandHandler(ChallengeMe plugin) {
		super(plugin.getMessageConfig());

		addArgumenExecutor("reload", new ReloadCommand(plugin));
	}
}
