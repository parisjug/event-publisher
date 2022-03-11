package org.parisjug.eventpublisher.commands;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.parisjug.eventpublisher.eventpage.EventPage;
import org.parisjug.eventpublisher.json.CreateEmailCampaign;
import org.parisjug.eventpublisher.json.Sender;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "campaign")
public class CreateMailCampaignCommand implements Runnable {

    @Parameters(index = "0", description = "Url of the event page where to get the event details. Like https://www.parisjug.org/xwiki/wiki/oldversion/view/Meeting/20201208")
    private String url;

    @Option(names = { "-t", "--template" }, description = "Set the template id to be used (default is 51)")
    private String templateIdOption;

    @ConfigProperty(name = "sendinblue.apikey")
    Optional<String> senditblueApiKey;

    @Override
    public void run() {
        if (senditblueApiKey.isEmpty()) {
            System.err
                    .println("Should set the sendinblue apikey.\nYou can generate new api-key from https://account.sendinblue.com/advanced/api.\nThen pass it as environment variable like `export SENDINBLUE_APIKEY=<your-apikey>` and rerun the command");
                    return;
        }
        long templateIdValue = 51L;

        if (templateIdOption != null) {
            try {
                templateIdValue = Long.parseLong(templateIdOption);
            } catch (NumberFormatException e) {
                System.out.println("Template id " + templateIdOption + " is not a number, using default.");
            }
        }
        System.out.println("Using template id " + templateIdValue);

        try {
            EventPage page = EventPage.fromUrl(url);

            // create campaign
            CreateEmailCampaign campaign = new CreateEmailCampaign( //
                    "Soirée ParisJUG: " + page.getTitle(), //
                    new Sender("La crew du ParisJUG", "crew@parisjug.org"), //
                    "Soirée " + //
                            (page.isVirtual() ? "Virtuelle" : "en présentiel") + //
                            " ParisJUG - {{ params.TITLE }} - {{ params.DATETIME }}");
            campaign.setTemplateId(templateIdValue);

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("TITLE", page.getTitle());
            params.put("INTRO", page.getIntro());
            params.put("LOCATION_URL", page.getLocation());
            params.put("CALENDAR_URL", page.generateGcalLink());
            params.put("DATETIME", page.getDateTime());
            params.put("WEBSITE_URL", "https://www.parisjug.org/xwiki/bin/view/Main/WebHome");
            params.put("CONTENT", page.getDetails());
            params.put("PART1", page.getPart1());
            params.put("PART2", page.getPart2());
            campaign.setParams(params);

            MediaType mediaType = MediaType.parse("application/json");

            Gson gson = new Gson();
            System.out.println("Sending request: \n" + gson.toJson(campaign));
            RequestBody body = RequestBody.create(mediaType, //
                    gson.toJson(campaign));
            Request request2 = new Request.Builder().url("https://api.sendinblue.com/v3/emailCampaigns").post(body)
                    .addHeader("api-key", senditblueApiKey.get()) //
                    .addHeader("Accept", "application/json") //
                    .addHeader("Content-Type", "application/json") //
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request2).execute();
            System.out.println(response.body().string());
        } catch (JsonSyntaxException | IOException e) {
            throw new RuntimeException("an error occured when creating the campains", e);
        }
    }

}