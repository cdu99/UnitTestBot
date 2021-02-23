package fr.uge.bot.command;

import fr.uge.UnitTestBot;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LifetimeCommand implements Command {
    private final UnitTestBot unitTestBot;

    public LifetimeCommand(UnitTestBot unitTestBot) {
        this.unitTestBot = unitTestBot;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        try {
            String[] format = event.getMessage().getContentRaw().split(" ", 3);
            unitTestBot.redefineLifetime(format[1], Integer.parseInt(format[2]), event);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            channel.sendMessage(":x: **!lifetime** must be use like so: `!remove <test> <new_lifetime>`").queue();
            throw new AssertionError(e);
        }
    }

    @Override
    public String getCommand() {
        return "!lifetime";
    }
}
