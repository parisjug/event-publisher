package org.parisjug.eventpublisher;

import org.parisjug.eventpublisher.commands.CreateMailCampaignCommand;
import org.parisjug.eventpublisher.commands.GenerateGoogleCalendarCommand;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;

@TopCommand
@CommandLine.Command(subcommands = { //
        GenerateGoogleCalendarCommand.class, //
        CreateMailCampaignCommand.class //
})
public class EventPublisher implements Runnable {
    @Override
    public void run() {
        System.out.println("hello ");
    }
}
