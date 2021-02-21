package fr.uge;

import fr.uge.bot.Listener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.log4j.BasicConfigurator;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException {
        BasicConfigurator.configure();

        JDABuilder.createDefault("Nzk1NjA4NDAyODAwOTM0OTgz.X_L2EA.XWr5S82moiVmdrF-PvZDBhMX-NI")
                .setActivity(Activity.streaming("League of Legends", "https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
                .addEventListeners(new Listener())
                .build();
    }
}
