package fr.uge.bot;

import fr.uge.UnitTestBot;
import fr.uge.bot.command.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Listener extends ListenerAdapter {
    private final CommandManager commandManager;

    public Listener(UnitTestBot unitTestBot) {
        Objects.requireNonNull(unitTestBot);
        commandManager = new CommandManager(unitTestBot);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        commandManager.run(event);
    }
}
