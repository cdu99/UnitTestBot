package fr.uge.bot.command;

import fr.uge.UnitTestBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

public class ResultCommand implements Command {
    private final UnitTestBot unitTestBot;

    public ResultCommand(UnitTestBot unitTestBot) {
        this.unitTestBot = unitTestBot;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        String testName = event.getMessage().getContentRaw().split(" ", 2)[1];
        try {
            byte[] xls = unitTestBot.createTestResultXLS(testName);
            if (xls == null) {
                event.getChannel().sendMessage(":x: No test result found for: **" + testName + "**").queue();
                return;
            }
            event.getChannel().sendMessage(":clipboard: Test results for: **" + testName + "**")
                    .addFile(xls, testName + "Result.xls").queue();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public String getCommand() {
        return "!result";
    }
}
