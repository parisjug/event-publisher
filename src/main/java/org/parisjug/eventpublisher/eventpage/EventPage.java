package org.parisjug.eventpublisher.eventpage;

import java.io.File;

public interface EventPage {

    static EventPage fromHtmlLocalFile(File htmlFile) {
        HtmlEventPage page = new HtmlEventPage();
        page.loadFromLocalHtmlFile(htmlFile);
        return page;
    }

    static EventPage fromUrl(String url) {
        HtmlEventPage page = new HtmlEventPage();
        page.loadFromUrl(url);
        return page;
    }

    String getTitle();

    String getDetails();

    String getPart1();

    String getPart2();

    String getStartTime();

    String getDateTime();

    String getEndTime();

    String getLongTitle();

    String getLocation();

    String generateGcalLink();

	String getIntro();

    boolean isVirtual();

}
