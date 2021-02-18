package fr.uge;

import fr.uge.database.Database;
import fr.uge.database.TestResult;
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

        var database = new Database();
        database.createTable();
        var tr = new TestResult();
        tr.setStudent("EREN");
        tr.setResult(true);
        var tr2 = new TestResult();
        tr2.setStudent("MIKASA");
        database.insertTestResultBean(tr);
        var dbd = new Database();

        dbd.insertTestResultBean(tr2);


    }
}
