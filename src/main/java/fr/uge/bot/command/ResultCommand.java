package fr.uge.bot.command;

import fr.uge.UnitTestBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

public class ResultCommand implements Command {
    private final UnitTestBot unitTestBot;

    public ResultCommand(UnitTestBot unitTestBot) {
        this.unitTestBot = unitTestBot;
    }

    // TODO WIP
    @Override
    public void execute(MessageReceivedEvent event) {
        String testName = event.getMessage().getContentRaw().split(" ", 2)[1];
        try {
            byte[] xls = unitTestBot.createTestResultXLS(testName);
            event.getChannel().sendMessage("result").addFile(xls, "result.xls").queue();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public String getCommand() {
        return "!result";
    }
}
