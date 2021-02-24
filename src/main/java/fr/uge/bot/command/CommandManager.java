package fr.uge.bot.command;

import fr.uge.UnitTestBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    public CommandManager(UnitTestBot unitTestBot) {
        addCommand(new UnitTestCommand(unitTestBot));
        addCommand(new TestCommand(unitTestBot));
        addCommand(new RemoveCommand(unitTestBot));
        addCommand(new LifetimeCommand(unitTestBot));
        addCommand(new ResultCommand(unitTestBot));
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