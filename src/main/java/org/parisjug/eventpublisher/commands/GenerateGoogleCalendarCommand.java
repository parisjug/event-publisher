package org.parisjug.eventpublisher.commands;

import org.parisjug.eventpublisher.eventpage.EventPage;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "gcal")
public class GenerateGoogleCalendarCommand implements Runnable {

    @Parameters(index = "0", description = "Url of the event page where to get the event details. Like https://www.parisjug.org/xwiki/wiki/oldversion/view/Meeting/20201208")
    private String url;

    @Override
    public void run() {
        EventPage page = EventPage.fromUrl(url);
        System.out.println(page.generateGcalLink());
    }
}