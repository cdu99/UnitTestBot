package fr.uge.bot.command;

import fr.uge.UnitTestBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class UnitTestCommand implements Command {
    private final UnitTestBot unitTestBot;

    public UnitTestCommand(UnitTestBot unitTestBot) {
        this.unitTestBot = unitTestBot;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        MessageChannel channel = event.getChannel();

        if (!attachments.isEmpty()) {
            String fileName = attachments.get(0).getFileName();
            if (!fileName.endsWith(".class")) {
                channel.sendMessage(":x: Must be a compiled JUnit test file :gear:").queue();
                return;
            }
            CompletableFuture<InputStream> attachment = attachments.get(0).retrieveInputStream();
            attachment.exceptionally(error -> {
                error.printStackTrace();
                return null;
            });
            addTest(attachment, fileName, event);
        } else {
            channel.sendMessage(":x: Please attach a compiled JUnit test file :gear:").queue();
        }
    }

    @Override
    public String getCommand() {
        return "!unittest";
    }

    private void addTest(CompletableFuture<InputStream> compiledTestData, String fileName, MessageReceivedEvent event) {
        Objects.requireNonNull(compiledTestData);
        try {
            byte[] testData = compiledTestData.get().readAllBytes();
            unitTestBot.addTest(fileName.split("\\.")[0], testData, event);
        } catch (ExecutionException | IOException e) {
            throw new AssertionError(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}