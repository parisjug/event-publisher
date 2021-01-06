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
import picocli.CommandLine.Parameters;

@Command(name = "campaign")
public class CreateMailCampaignCommand implements Runnable {

    @Parameters(index = "0", description = "Url of the event page where to get the event details. Like https://www.parisjug.org/xwiki/wiki/oldversion/view/Meeting/20201208")
    private String url;

    @ConfigProperty(name = "sendinblue.apikey")
    Optional<String> senditblueApiKey;

    @Override
    public void run() {

        try {
            EventPage page = EventPage.fromUrl(url);

            // create campaign
            CreateEmailCampaign campaign = new CreateEmailCampaign( //
                    "Soirée ParisJUG: " + page.getTitle(), //
                    new Sender("La crew du ParisJUG", "crew@parisjug.org"), //
                    "Soirée Virtuelle ParisJUG - {{ params.TITLE }} - {{ params.DATETIME }}");
            campaign.setTemplateId(51L);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("TITLE", page.getTitle());
            params.put("INTRO", page.getIntro());
            params.put("LOCATION_URL", page.getLocation());
            params.put("CALENDAR_URL", page.generateGcalLink());
            params.put("DATETIME", page.getDateTime());
            params.put("WEBSITE_URL", "https://www.parisjug.org/xwiki/bin/view/Main/WebHome");
            params.put("CONTENT", page.getDetails());
            campaign.setParams(params);

            MediaType mediaType = MediaType.parse("application/json");

            Gson gson = new Gson();
            System.out.println("Sending request: \n" + gson.toJson(campaign));
            RequestBody body = RequestBody.create(mediaType, //
                    gson.toJson(campaign));
            Request request2 = new Request.Builder().url("https://api.sendinblue.com/v3/emailCampaigns").post(body)
                    .addHeader("api-key", senditblueApiKey.orElseThrow(() -> new RuntimeException(
                            "Should set the sendinblue apikey. for instance with the env variable SENDINBLUE_APIKEY"))) //
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