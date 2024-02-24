package org.parisjug.eventpublisher;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.parisjug.eventpublisher.commands.CreateMailCampaignCommand;
import org.parisjug.eventpublisher.commands.GenerateGoogleCalendarCommand;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;


@QuarkusMain
@TopCommand
@CommandLine.Command(subcommands = { //
        GenerateGoogleCalendarCommand.class, //
        CreateMailCampaignCommand.class //
})
public class EventPublisher implements Runnable, QuarkusApplication {
    @Inject
    CommandLine.IFactory factory;
    @Spec
    CommandSpec spec;

    @Override
    public void run() {
        throw new CommandLine.ParameterException(spec.commandLine(),
                "No command argument specified. Please provide a command argument.");
    }

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory).execute(args);
    }
}
