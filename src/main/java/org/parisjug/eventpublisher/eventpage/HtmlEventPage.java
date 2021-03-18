package org.parisjug.eventpublisher.eventpage;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
    public String getDateTime() {
        return doc.select("#datetime").first().text();
    }

    @Override
    public String getStartTime() {
        ZonedDateTime eventDateTime = getEventZonedDateTime();
        ZonedDateTime startDateTime = eventDateTime.minusMinutes(15);
        return startDateTime.format(DateTimeFormatter.ISO_INSTANT).replace(":", "").replace("-", "");
    }

    private ZonedDateTime getEventZonedDateTime() {
        return parseDateTime(getDateTime());
    }

    @Override
    public String getEndTime() {
        ZonedDateTime eventDateTime = getEventZonedDateTime();
        ZonedDateTime endDateTime = eventDateTime.plusMinutes(75);
        return endDateTime.format(DateTimeFormatter.ISO_INSTANT).replace(":", "").replace("-", "");
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
        return "Paris JUG - Soirée Virtuelle: " + getTitle() + " (" + getEventZonedDateTime().format(dtf) + ")";
    }

    @Override
    public String getLocation() {
        return doc.select("#location a").first().attr("href");
    }

    @Override
    public String generateGcalLink() {
        String title_url = encode(getLongTitle());
        String details_url = encode(getDetails());
        String dates_url = encode(getStartTime() + "/" + getEndTime());
        String location_url = encode(getLocation());
        return "https://www.google.com/calendar/render?action=TEMPLATE&text=" + title_url + "&details=" + details_url
                + "&location=" + location_url + "&dates=" + dates_url;
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

    

}
