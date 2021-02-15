package fr.uge;

import fr.uge.test.TestRunner;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws LoginException, MalformedURLException, ClassNotFoundException {
        if (false) {
            JDA builder = JDABuilder.createDefault("Nzk1NjA4NDAyODAwOTM0OTgz.X_L2EA.XWr5S82moiVmdrF-PvZDBhMX-NI")
                    .setActivity(Activity.playing("WIP"))
                    .addEventListeners(new Listener())
                    .build();
        }
        var t = new TestRunner();
        t.getAsInt();
    }
}
