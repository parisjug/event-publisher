package org.parisjug.eventpublisher;

import org.parisjug.eventpublisher.commands.CreateMailCampaignCommand;
import org.parisjug.eventpublisher.commands.GenerateGoogleCalendarCommand;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

@TopCommand
@CommandLine.Command(subcommands = { //
        GenerateGoogleCalendarCommand.class, //
        CreateMailCampaignCommand.class //
})
public class EventPublisher implements Runnable {
    @Spec
    CommandSpec spec;

    @Override
    public void run() {
        throw new CommandLine.ParameterException(spec.commandLine(), "No command argument specified. Please provide a command argument.");
    }
}
