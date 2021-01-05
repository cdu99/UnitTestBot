package fr.uge.command;


import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TestClassCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        if (msg.getContentRaw().equals("!testclass")) {
            List<Message.Attachment> attachments = event.getMessage().getAttachments();
            MessageChannel channel = event.getChannel();
            if (attachments.isEmpty()) {
                channel.sendMessage("Please attach the test file !!")
                        .queue();
                return;
            }

            CompletableFuture<File> attachment = attachments.get(0)
                    .downloadToFile(attachments.get(0).getFileName());
            attachment.exceptionally(error -> {
                error.printStackTrace();
                return null;
            });
        }
    }
}