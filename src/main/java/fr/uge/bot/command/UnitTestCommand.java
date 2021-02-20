package fr.uge.bot.command;

import fr.uge.JavaFileTesting;
import fr.uge.compiler.JavaByteObject;
import fr.uge.compiler.TestFiles;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class UnitTestCommand implements Command {


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
//            CompletableFuture<File> attachment = attachments.get(0)
//                    .downloadToFile("test-sources/" + fileName);
            CompletableFuture<InputStream> attachment = attachments.get(0).retrieveInputStream();
            try {
//                InputStream is = attachment.get();
//                String className = JavaFileTesting.getClassName(is);
//                System.out.println(className);
                byte[] classFile = attachment.get().readAllBytes();
                var tf = new TestFiles();
                tf.addTestFile(fileName.split("\\.")[0], classFile);
                tf.run(fileName.split("\\.")[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
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
        return "!unittest";
    }

}