package fr.uge.bot.command;

import fr.uge.JavaFileTesting;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
                    .downloadToFile("src/main/java/fr/uge/test/" + fileName);
            attachment.exceptionally(error -> {
                error.printStackTrace();
                return null;
            });
            try {
                var testing = new JavaFileTesting();
                testing.compileAndTest(attachment.get(), event);
            } catch (InterruptedException | ExecutionException | IOException e) {
                throw new AssertionError(e);
            }
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
