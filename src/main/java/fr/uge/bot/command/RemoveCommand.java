package fr.uge.bot.command;

import fr.uge.UnitTestBot;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveCommand implements Command {

    @Override
    public void execute(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        try {
            String testToRemove = event.getMessage().getContentRaw().split(" ", 2)[1];
            UnitTestBot.getInstance().removeUnitTest(testToRemove, event);
        } catch (ArrayIndexOutOfBoundsException e) {
            channel.sendMessage(":x: **!remove** must be use like so: `!remove <test_to_remove>`").queue();
            throw new AssertionError(e);
        }
    }

    @Override
    public String getCommand() {
        return "!remove";
    }
}
