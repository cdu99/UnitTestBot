package fr.uge;

import fr.uge.test.MyCustomTestDescriptor;
import fr.uge.test.MyCustomTestEngine;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.discovery.EngineDiscoveryRequestResolver;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

import javax.security.auth.login.LoginException;

import java.util.List;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public class Main {
    public static void main(String[] args) throws LoginException {
        if (false) {
            JDA builder = JDABuilder.createDefault("Nzk1NjA4NDAyODAwOTM0OTgz.X_L2EA.XWr5S82moiVmdrF-PvZDBhMX-NI")
                    .setActivity(Activity.playing("WIP"))
                    .addEventListeners(new Listener())
                    .build();
        }

        MyCustomTestEngine testEngine = new MyCustomTestEngine();


        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .filters(
                        includeClassNamePatterns(".*Test")
                )
                .build();

        testEngine.discover(request, UniqueId.forEngine("test"));
        System.out.println(testEngine.getClass());
    }
}
