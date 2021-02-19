package fr.uge.bot;

import fr.uge.database.TestResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class BotService {

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
}
