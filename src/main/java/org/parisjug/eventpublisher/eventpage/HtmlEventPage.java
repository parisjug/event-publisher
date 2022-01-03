package org.parisjug.eventpublisher.eventpage;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HtmlEventPage implements EventPage {
    Document doc;

    @Override
    public String getTitle() {
        Elements titleElements = doc.select("#title");
        if(titleElements.isEmpty()){
            throw new EventPageCheckException("The page should contain an element with the id \"title\". For instance: <div id=\"title\">Quarkus World Tour</div>.");
        }
        return titleElements.first().text();
    }

    protected void loadFromLocalHtmlFile(File htmlFile) {
        try {
            doc = Jsoup.parse(htmlFile, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load file with jsoup", e);
        }
    }

    protected void loadFromUrl(String url) {
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load url " + url + " with jsoup", e);
        }
    }

    @Override
    public String getDetails() {
        return doc.select("#details").first().html().replaceAll("href=\"/", "href=\"https://www.parisjug.org/" );
    }

    @Override
    public String getPart1() {
        return doc.select("#part1").first().html().replaceAll("href=\"/", "href=\"https://www.parisjug.org/" );
    }

    @Override
    public String getPart2() {
        return doc.select("#part2").first().html().replaceAll("href=\"/", "href=\"https://www.parisjug.org/" );
    }

    @Override
    public String getDateTime() {
        return doc.select("#datetime").first().text();
    }

    @Override
    public String getStartTime() {
        ZonedDateTime eventDateTime = getEventZonedDateTime();
        if (isVirtual()) {
            eventDateTime = eventDateTime.minusMinutes(15);
        }
        return eventDateTime.format(DateTimeFormatter.ISO_INSTANT).replace(":", "").replace("-", "");
    }

    private ZonedDateTime getEventZonedDateTime() {
        return parseDateTime(getDateTime());
    }

    @Override
    public String getEndTime() {
        ZonedDateTime eventDateTime = getEventZonedDateTime();
        if(isVirtual()){
            eventDateTime = eventDateTime.plusMinutes(75);
        }else {
            eventDateTime = eventDateTime.plusMinutes(165);
        }
        return eventDateTime.format(DateTimeFormatter.ISO_INSTANT).replace(":", "").replace("-", "");
    }

    ZonedDateTime parseDateTime(String datetimeInput) {
        datetimeInput = datetimeInput.split(" ", 2)[1];
        String datetimeInputTZ = new DateTranslator().translate(datetimeInput).toLowerCase() + " Europe/Paris";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d MM yyyy 'à' HH'h'mm zzzz", Locale.getDefault());
        return ZonedDateTime.parse(datetimeInputTZ, dtf);
    }

    @Override
    public String getLongTitle() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy'/'MM'/'dd");
        String virtualOrInRealLife = "en présentiel";
        if (isVirtual()) {
            virtualOrInRealLife = "Virtuelle";
        }
        return "Paris JUG - Soirée " + virtualOrInRealLife + " : " + getTitle() + " ("
                + getEventZonedDateTime().format(dtf) + ")";
    }

    @Override
    public String getLocation() {
        String attr = doc.select("#location a").first().attr("href");
        if(attr.startsWith("/")) {
            attr = "https://www.parisjug.org" + attr;
        }
        return attr;
    }

    @Override
    public String generateGcalLink() {
        String title_urlencoded = encode(getLongTitle());
        String details_urlencoded = encode(getDetails());
        String dates_urlencoded = encode(getStartTime() + "/" + getEndTime());
        String location_urlencoded = encode(getLocation());
        return "https://www.google.com/calendar/render?action=TEMPLATE&text=" + title_urlencoded + "&details=" + details_urlencoded
                + "&location=" + location_urlencoded + "&dates=" + dates_urlencoded;
    }

    String encode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

	@Override
	public String getIntro() {
        Elements intro = doc.select("#intro");
		if(intro.isEmpty()){
            return "";
        }
        return intro.first().html().replaceAll("href=\"/", "href=\"https://www.parisjug.org/" );
	}

    @Override
    public boolean isVirtual() {
        if(doc.select("#location").first().text().contains("Dans les locaux de notre chaîne")){
            return true;
        };

        return false;
    }

    @Override
    public String generateAddEventCalLink(String addEventToken) {

        try {

            // create calendarevent
            // CalendarEvent calendarEvent = new CalendarEvent(getLongTitle(), getDetails(),
            // getStartTime(), getEndTime(), getLocation());

            // MediaType mediaType = MediaType.parse("application/json");

            // Gson gson = new Gson();
            // String json = gson.toJson(calendarEvent);
            // System.out.println("Sending request: \n" + json);
            // RequestBody body = RequestBody.create(mediaType, //
            // json);

            HttpUrl url = HttpUrl //
                    .parse("https://www.addevent.com/api/v1/oe/events/create/") //
                    .newBuilder() //
                    .addQueryParameter("title", getLongTitle()) //
                    .addQueryParameter("description", getDetails()) //
                    .addQueryParameter("location", getLongTitle()) //
                    .addQueryParameter("organizer", "Paris JUG") //
                    .addQueryParameter("organizer_email", "crew@parisjug.org") //
                    .addQueryParameter("start_date", getStartTime()) //
                    .addQueryParameter("end_date", getEndTime()) //
                    .addQueryParameter("token", addEventToken)
                    .build();

            Request request = new Request.Builder().url(url).build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("an error occured when create an addevent url", e);
        }
    }

}
