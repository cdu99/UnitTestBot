package fr.uge.bot.command;

import fr.uge.UnitTestBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ResultCommand implements Command {
    private final UnitTestBot unitTestBot;

    public ResultCommand(UnitTestBot unitTestBot) {
        this.unitTestBot = unitTestBot;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        String testName = event.getMessage().getContentRaw().split(" ", 2)[1];
        // TODO
        // Construire XLS et renvoyer
    }

    @Override
    public String getCommand() {
        return "!result";
    }
}
