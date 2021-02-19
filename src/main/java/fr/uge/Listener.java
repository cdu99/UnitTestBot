package fr.uge;

import fr.uge.bot.command.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Listener extends ListenerAdapter {
    private final CommandManager commandManager = new CommandManager();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        commandManager.run(event);
    }
}
