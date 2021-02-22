package fr.uge.bot.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    public CommandManager() {
        addCommand(new UnitTestCommand());
        addCommand(new TestCommand());
        addCommand(new RemoveCommand());
        addCommand(new LifetimeCommand());
    }

    private void addCommand(Command c) {
        if (!commands.containsKey(c.getCommand())) {
            commands.put(c.getCommand(), c);
        }
    }

    public void run(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().toLowerCase().split(" ", 2)[0];

        if (!message.startsWith("!")) {
            return;
        }
        if (commands.containsKey(message)) {
            commands.get(message).execute(event);
        } else {
            event.getChannel().sendMessage(":x: Command not found :face_with_monocle:").queue();
        }
    }
}