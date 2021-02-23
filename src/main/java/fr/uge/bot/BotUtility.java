package fr.uge.bot;

import fr.uge.database.TestResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Comparator;
import java.util.List;

public class BotUtility {
    private BotUtility() {
        throw new IllegalStateException("Utility class");
    }

    private static String printMessageAuthor(MessageReceivedEvent event) {
        return "<@" + event.getAuthor().getId() + ">";
    }

    public static void sendEmbedTestResult(MessageReceivedEvent event, List<TestResult> testResults, String testedFile) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Test result for `" + testedFile + "` :robot:");
        testResults.sort(Comparator.comparing(TestResult::getQuestion).thenComparing(TestResult::getTest));
        // TODO
        // Embed limited to 25 fields...
        testResults.forEach(testResult ->
                eb.addField(testResult.toString(), testResultEmote(testResult.getResult()), true));
        eb.setColor(2438306);
        eb.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private static String testResultEmote(boolean result) {
        if (result) {
            return ":white_check_mark:";
        } else {
            return ":x:";
        }
    }

    public static void sendCompilationErrorMessage(MessageReceivedEvent event) {
        event.getChannel().sendMessage(":x: " + printMessageAuthor(event) +
                " Your file does not compile :rofl:").queue();
    }

    public static void sendNoAvailableTestMessage(MessageReceivedEvent event, String fileName) {
        event.getChannel().sendMessage(":x: No test available for **" + fileName + "**").queue();
    }

    public static void sendErrorDuringTestMessage(MessageReceivedEvent event) {
        event.getChannel().sendMessage(":x: " + printMessageAuthor(event) +
                " **ERROR** Please verify if your file is correct :rotating_light:").queue();
    }

    public static void sendErrorTestFileNotCorrectMessage(MessageReceivedEvent event, String testFileName) {
        event.getChannel().sendMessage(":x: **ERROR** Something is wrong with **"
                + testFileName + "** :rotating_light:").queue();
    }

    public static void sendNewTestNotification(MessageReceivedEvent event, String testName, int lifetime) {
        event.getChannel().sendMessage(":mega: **" + testName
                + "** is available for **" + lifetime + "** seconds").queue();
    }

    public static void sendSuccessfullyRemovedTest(MessageReceivedEvent event, String removedTest) {
        event.getChannel().sendMessage(":white_check_mark: **"
                + removedTest + "** has been successfully removed").queue();
    }

    public static void sendFailToRemoveTestMessage(MessageReceivedEvent event, String testToRemove) {
        event.getChannel().sendMessage(":x: **FAIL** to remove **" + testToRemove + "**").queue();
    }

    public static void sendRedefiningLifetimeMessage(String name, MessageReceivedEvent event, int newLifetime) {
        event.getChannel().sendMessage(":white_check_mark: lifetime for **"
                + name + "** has been set to **" + newLifetime + "** seconds").queue();
    }

    public static void sendTestAlreadyRemovedMessage(String name, MessageReceivedEvent event) {
        event.getChannel().sendMessage(":x: **" + name + "** has already been removed").queue();
    }
}
