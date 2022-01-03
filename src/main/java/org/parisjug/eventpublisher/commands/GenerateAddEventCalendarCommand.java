package org.parisjug.eventpublisher.commands;

import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.parisjug.eventpublisher.eventpage.EventPage;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "addevent")
public class GenerateAddEventCalendarCommand implements Runnable {

    @Parameters(index = "0", description = "Url of the event page where to get the event details. Like https://www.parisjug.org/xwiki/wiki/oldversion/view/Meeting/20201208")
    private String url;

    @ConfigProperty(name = "addevent.token")
    Optional<String> addEventToken;

    private String getAddEventToken() {
        return addEventToken.orElseThrow(() -> new RuntimeException(
                "Should set the addevent token. for instance with the env variable ADDEVENT_TOKEN"));
    }

    @Override
    public void run() {
        EventPage page = EventPage.fromUrl(url);
        System.out.println(page.generateAddEventCalLink(getAddEventToken()));
    }
}