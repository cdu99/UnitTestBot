package fr.uge.command;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TestCommand implements Command {

    @Override
    public void execute(MessageReceivedEvent event) {
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        MessageChannel channel = event.getChannel();

        if (!attachments.isEmpty()) {
            String fileName = attachments.get(0).getFileName();

            if (!fileName.endsWith(".java")) {
                channel.sendMessage("This must be a java file !!")
                        .queue();
                return;
            }

            CompletableFuture<File> attachment = attachments.get(0)
                    .downloadToFile("src/main/resources/" + fileName);
            attachment.exceptionally(error -> {
                error.printStackTrace();
                return null;
            });

        } else {
            channel.sendMessage("Please attach a java file !!")
                    .queue();
        }
    }

    @Override
    public String getCommand() {
        return "!test";
    }
}
