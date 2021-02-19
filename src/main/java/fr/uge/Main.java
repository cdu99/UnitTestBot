package fr.uge;

import fr.uge.database.Database;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.log4j.BasicConfigurator;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException {
        var database = new Database();
        database.createTable();

        BasicConfigurator.configure();

        JDABuilder.createDefault("Nzk1NjA4NDAyODAwOTM0OTgz.X_L2EA.XWr5S82moiVmdrF-PvZDBhMX-NI")
                .setActivity(Activity.playing("WIP"))
                .addEventListeners(new Listener())
                .build();
    }
}
