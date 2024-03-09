package org.parisjug.eventpublisher.eventpage;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlEventPage implements EventPage {
    private static final String ABSOLUTE_URL = "https://www.parisjug.org";

    Document doc;

    @Override
    public String getTitle() {
        Element titleElement = this.doc.selectFirst(".post__title");
        if (titleElement == null) {
            throw new EventPageCheckException(
                    "The page should contain an element with the id \"title\". For instance: <div id=\"title\">Quarkus World Tour</div>.");
        }
        return titleElement.text();
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
        return convertToAbsoluteLinks(getPart1() + getBuffet() + getPart2());
    }

    public String getBuffet() {
        Element buffet = doc.selectFirst("#buffet");
        if (buffet != null) {
            return buffet.html();
        }
        Element buffetElement = doc.selectFirst("h3[id*='buffet']");
        if (buffetElement == null) {
            return "";
        }
        String buffetHtml = buffetElement.outerHtml();
        while ((buffetElement = buffetElement.nextElementSibling()) != null &&
            !"h3".equals(buffetElement.tagName())) {
            buffetHtml+= buffetElement.outerHtml();
        }
        return buffetHtml;
    }

    @Override
    public String getPart1() {
        Element part1 = doc.selectFirst("#part1");
        if (part1 != null) {
            return part1.html();
        }
        Elements detailh3 = doc.select("#détails");
        if(detailh3.isEmpty()) {
            return "";
        }
        String part1html = "";
        Elements elements = detailh3.parents().first().children();
        // for each element, in elements.stream() start at h3 with id détail and append html until next h3
        boolean start = false;
        for(int i = 0; i < elements.size(); i++) {
            if(elements.get(i).tagName().equals("h2") && elements.get(i).id().equals("détails")) {
                start = true;
                continue;
            }
            if(elements.get(i).tagName().equals("h2") && !elements.get(i).id().equals("détails")) {
                start = false;
                continue;
            }
            if(elements.get(i).tagName().equals("h3") && elements.get(i).id().contains("buffet")) {
                start = false;
                continue;
            }
            if(start) {
                part1html += elements.get(i).outerHtml();
            }
        }
        return part1html;
    }

    @Override
    public String getPart2() {
        Elements part2 = doc.select("#part2");
        if (part2.isEmpty()) {
            Elements detailh3 = doc.select("#détails");
            if(detailh3.isEmpty()) {
                return "";
            }
            String part2html = "";
            Elements elements = detailh3.parents().first().children();
            // for each element, in elements.stream() start at h3 with id contains buffet and append html until next h3 with id contains "3ème-mi-temps"
            boolean start = false;
            boolean buffet = false;
            for(int i = 0; i < elements.size(); i++) {
                if(elements.get(i).tagName().equals("h3") && buffet) {
                    start = true;
                }
                if(elements.get(i).tagName().equals("h3") && elements.get(i).id().contains("buffet")) {
                    buffet = true;
                    continue;
                }
                if(elements.get(i).tagName().equals("h3") && elements.get(i).id().contains("3ème-mi-temps")) {
                    start = false;
                    break;
                }
                if(start) {
                    part2html += elements.get(i).outerHtml();
                }
            }
            return part2html;
        }
        return part2.first().html();
    }

    @Override
    public String getDateTime() {
        Element dateTimeElement = doc.selectFirst("#datetime");
        if (dateTimeElement != null) {
            return dateTimeElement.text();
        }
        // in the section starting with h2 id="date-et-lieu", get the first ul li element
        Element dateEtLieuElement = doc.selectFirst("#date-et-lieu + ul > li");
        if (dateEtLieuElement == null) {
            throw new EventPageCheckException(
                "The page should contain an element with the id \"date-et-lieu\"."
            );
        }
        return dateEtLieuElement.text();
    }

    @Override
    public String getStartTime() {
        ZonedDateTime eventDateTime = getEventZonedDateTime();
        if (isVirtual()) {
            eventDateTime = eventDateTime.minusMinutes(15);
        }
        return eventDateTime.format(ISO_INSTANT).replace(":", "").replace("-", "");
    }

    private ZonedDateTime getEventZonedDateTime() {
        return parseDateTime(getDateTime());
    }

    @Override
    public String getEndTime() {
        ZonedDateTime eventDateTime = getEventZonedDateTime();
        if (isVirtual()) {
            eventDateTime = eventDateTime.plusMinutes(75);
        } else {
            eventDateTime = eventDateTime.plusMinutes(180);
        }
        return eventDateTime.format(ISO_INSTANT).replace(":", "").replace("-", "");
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
        return "Paris JUG - " + getTitle() + " ("
                + getEventZonedDateTime().format(dtf) + ")";
    }

    @Override
    public String getLocation() {
        Element locationElement = doc.selectFirst("#location a");
        if (locationElement == null) {
            // in the section starting with h2 id="date-et-lieu", get the second li element
            locationElement = doc.selectFirst("#date-et-lieu + ul > li:eq(1) > a");
        }
        if (locationElement == null) {
            return "";
        }
        String location = locationElement.attr("href");
        if (location.startsWith("/")) {
            location = ABSOLUTE_URL + location;
        }
        return location;
    }

    @Override
    public String generateGcalLink() {
        String title_urlencoded = encode(getLongTitle());
        String details_urlencoded = encode(getDetails());
        String dates_urlencoded = encode(getStartTime() + "/" + getEndTime());
        String location_urlencoded = encode(getLocation());
        return "https://www.google.com/calendar/render?action=TEMPLATE&text=" + title_urlencoded + "&details="
                + details_urlencoded + "&location=" + location_urlencoded + "&dates=" + dates_urlencoded;
    }

    String encode(String str) {
        return URLEncoder.encode(str, UTF_8);
    }

    @Override
    public String getIntro() {
        Element intro = this.doc.selectFirst("#intro");
        if (intro == null) {
            return "";
        }
        return convertToAbsoluteLinks(intro.html());
    }

    @Override
    public boolean isVirtual() {
        return getTitle().contains("Soirée Virtuelle");
    }

    private static String convertToAbsoluteLinks(String html) {
        return html.replaceAll("href=\"/", "href=\"" + ABSOLUTE_URL + "/");
    }
}
