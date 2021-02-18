package fr.uge;

import fr.uge.database.Database;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.jdbi.v3.core.Jdbi;

import javax.security.auth.login.LoginException;
import java.net.MalformedURLException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws LoginException, MalformedURLException, ClassNotFoundException, SQLException {
//        JDA builder = JDABuilder.createDefault("Nzk1NjA4NDAyODAwOTM0OTgz.X_L2EA.XWr5S82moiVmdrF-PvZDBhMX-NI")
//                .setActivity(Activity.playing("WIP"))
//                .addEventListeners(new Listener())
//                .build();

//        Jdbi jdbi = Jdbi.create("jdbc:sqlite::memory:");
        var database = new Database();
        database.createTables();
        database.testInstert();
    }
}
