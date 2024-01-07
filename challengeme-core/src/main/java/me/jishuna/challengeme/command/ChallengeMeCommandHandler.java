package me.jishuna.challengeme.command;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.jishlib.command.ArgumentCommandHandler;

public class ChallengeMeCommandHandler extends ArgumentCommandHandler {

    public ChallengeMeCommandHandler(ChallengeMe plugin) {
        super("challengeme.command", () -> "", () -> "");

        ChallengeGUICommand guiCommand = new ChallengeGUICommand();
        setDefault(guiCommand);

        addArgumentExecutor("gui", guiCommand);
        addArgumentExecutor("reload", new ReloadCommand(plugin));
    }
}
