package fr.uge.bot;

import fr.uge.database.TestResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class BotUtility {
    // TODO
    // WIP
    public static void sendEmbedTestResult(MessageReceivedEvent event, List<TestResult> testResults, String testedFile) {
        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Test result");
        eb.setColor(42069);
        eb.setDescription("Result for test");
        testResults.forEach(testResult -> eb.addField(testResult.getQuestion() + " " + testResult.getTest(), String.valueOf(testResult.getResult()), true));
        eb.addBlankField(false);
        eb.setAuthor(event.getAuthor().getAsTag(), null, null);
        eb.setFooter("Aller les zouz", null);

        event.getChannel().sendMessage(eb.build()).queue();
    }

    public static void sendCompilationErrorMessage(MessageReceivedEvent event, String fileName) {
        event.getChannel().sendMessage("<@" + event.getAuthor().getId() + "> your file: " + fileName + " compile pas zebi").queue();
    }

    public static void sendNoAvailableTestForNowMessage(MessageReceivedEvent event, String fileName) {
        event.getChannel().sendMessage("<@" + event.getAuthor().getId() + "> your file: " + fileName + " a pas de test corerspond").queue();
    }

    public static void sendErrorDuringTestMessage(MessageReceivedEvent event, String testFileName) {
        event.getChannel().sendMessage("<@" + event.getAuthor().getId() + ">  Error trying to run your test on "+ testFileName  +"verify if goo dpalcal").queue();
    }
}
