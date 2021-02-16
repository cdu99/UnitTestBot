package fr.uge.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    public CommandManager() {
        addCommand(new UnitTestCommand());
        addCommand(new TestCommand());
    }

    private void addCommand(Command c) {
        if (!commands.containsKey(c.getCommand())) {
            commands.put(c.getCommand(), c);
        }
    }

    public void run(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().toLowerCase();

        if (!message.startsWith("!")) {
            return;
        }
        if (commands.containsKey(message)) {
            commands.get(message).execute(event);
        }
    }
}