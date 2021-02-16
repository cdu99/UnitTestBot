package fr.uge.command;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TestClassCommand implements Command {

    @Override
    public void execute(MessageReceivedEvent event) {
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        MessageChannel channel = event.getChannel();

        if (!attachments.isEmpty()) {
            String fileName = attachments.get(0).getFileName();
            if (!fileName.endsWith(".class")) {
                channel.sendMessage("The test must be a .class file !!")
                        .queue();
                return;
            }
            CompletableFuture<File> attachment = attachments.get(0)
                    .downloadToFile("src/main/java/fr/uge/test/" + fileName);
            attachment.exceptionally(error -> {
                error.printStackTrace();
                return null;
            });
        } else {
            channel.sendMessage("Please attach a test file !!")
                    .queue();
        }
    }

    @Override
    public String getCommand() {
        return "!testclass";
    }
}