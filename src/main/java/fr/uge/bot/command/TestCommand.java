package fr.uge.bot.command;

import fr.uge.UnitTestBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
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
                channel.sendMessage(":x: Must be a Java file :coffee:").queue();
                return;
            }
            // TODO
            // Not downloading to disk at all
            CompletableFuture<File> attachment = attachments.get(0)
                    .downloadToFile("src/main/resources/" + fileName);
            attachment.exceptionally(error -> {
                error.printStackTrace();
                return null;
            });
            compileAndTest(attachment, event);
        } else {
            channel.sendMessage(":x: Please attach a Java file :coffee:").queue();
        }
    }

    @Override
    public String getCommand() {
        return "!test";
    }

    private void compileAndTest(CompletableFuture<File> fileToCompileAndTest, MessageReceivedEvent event) {
        Objects.requireNonNull(fileToCompileAndTest);
        try {
            UnitTestBot.getInstance().compileAndTest(fileToCompileAndTest.get(), event);
        } catch (ExecutionException | IOException e) {
            throw new AssertionError(e);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
