package fr.uge.main;

import fr.uge.command.TestClassCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException {
        JDA builder = JDABuilder.createDefault("Nzk1NjA4NDAyODAwOTM0OTgz.X_L2EA.XWr5S82moiVmdrF-PvZDBhMX-NI")
                .setActivity(Activity.playing("WIP"))
                .addEventListeners(new TestClassCommand())
                .build();
    }
}
