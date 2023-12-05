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
        Elements titleElements = doc.select(".post__title");
        if (titleElements.isEmpty()) {
            throw new EventPageCheckException(
                    "The page should contain an element with the id \"title\". For instance: <div id=\"title\">Quarkus World Tour</div>.");
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
        return getPart1() + getBuffet() + getPart2();
    }

    public String getBuffet() {
        Elements buffet = doc.select("#buffet");
        if (buffet.isEmpty()) {
            Elements detailh3 = doc.select("#détails");
            if(detailh3.isEmpty()) {
                return "";
            }
            String buffethtml = "";
            Elements elements = detailh3.parents().first().children();
            // for each element, in elements.stream() start at h3 with id contains buffet and append html until next h3
            boolean start = false;
            for(int i = 0; i < elements.size(); i++) {
                if(elements.get(i).tagName().equals("h3") && elements.get(i).id().contains("buffet")) {
                    start = true;
                }
                if(elements.get(i).tagName().equals("h3") && !elements.get(i).id().contains("buffet")) {
                    start = false;
                    continue;
                }
                if(start) {
                    buffethtml += elements.get(i).outerHtml();
                }
            }
            return buffethtml.replaceAll("href=\"/", "href=\"https://www.parisjug.org/");
        }
        return buffet.first().html().replaceAll("href=\"/", "href=\"https://www.parisjug.org/");
    }

    @Override
    public String getPart1() {
        Elements part1 = doc.select("#part1");
        if(part1.isEmpty()) {
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
            return part1html.replaceAll("href=\"/", "href=\"https://www.parisjug.org/");

        }
        return part1.first().html().replaceAll("href=\"/", "href=\"https://www.parisjug.org/");
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
            return part2html.replaceAll("href=\"/", "href=\"https://www.parisjug.org/");
        }
        return part2.first().html().replaceAll("href=\"/", "href=\"https://www.parisjug.org/");
    }

    @Override
    public String getDateTime() {
        Elements dateTimeElement = doc.select("#datetime");
        if (dateTimeElement.isEmpty()) {
            // in the section starting with h2 id="date-et-lieu", get the first ul li element
            Elements elements = doc.select("#date-et-lieu").parents().first().children();
            for(int i = 0; i < elements.size(); i++) {
                if(elements.get(i).tagName().equals("ul")) {
                    Elements lis = elements.get(i).children();
                    for(int j = 0; j < lis.size(); j++) {
                        if(lis.get(j).tagName().equals("li")) {
                            return lis.get(j).text();
                        }
                    }
                }
            }

        }
        return dateTimeElement.first().text();
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
        if (isVirtual()) {
            eventDateTime = eventDateTime.plusMinutes(75);
        } else {
            eventDateTime = eventDateTime.plusMinutes(180);
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
        return "Paris JUG - " + getTitle() + " ("
                + getEventZonedDateTime().format(dtf) + ")";
    }

    @Override
    public String getLocation() {
        Elements locationElement = doc.select("#location a");
        if (locationElement.isEmpty()) {
            // in the section starting with h2 id="date-et-lieu", get the second li element
            Elements elements = doc.select("#date-et-lieu").parents().first().getElementsByTag("li");
            if(elements.size() > 1) {
                locationElement = elements.get(1).getElementsByTag("a");
            }
            else {
                return "";
            }
        }

        String attr = locationElement.first().attr("href");
        if (attr.startsWith("/")) {
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
        return "https://www.google.com/calendar/render?action=TEMPLATE&text=" + title_urlencoded + "&details="
                + details_urlencoded + "&location=" + location_urlencoded + "&dates=" + dates_urlencoded;
    }

    String encode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

    @Override
    public String getIntro() {
        Elements intro = doc.select("#intro");
        if (intro.isEmpty()) {
            return "";
        }
        return intro.first().html().replaceAll("href=\"/", "href=\"https://www.parisjug.org/");
    }

    @Override
    public boolean isVirtual() {
        return getTitle().contains("Soirée Virtuelle");
    }

}
