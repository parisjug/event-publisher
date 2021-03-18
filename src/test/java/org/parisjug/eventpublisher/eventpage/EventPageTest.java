package org.parisjug.eventpublisher.eventpage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class EventPageTest {

    @Test
    public void test_working_page() {
        File file = new File(this.getClass().getResource("parisjug-20201208.html").getFile());

        EventPage page = EventPage.fromHtmlLocalFile(file);
        // EventPage page =
        // EventPage.fromUrl("https://www.parisjug.org/xwiki/wiki/oldversion/view/Meeting/20201208");
        assertNotNull(page, "should be able to load from a xwiki html file");

        // title
        assertEquals("Le Java nouveau est arrivé : Java SE 15", page.getTitle(), "Title from page");

        // details
        assertTrue(page.getDetails().contains("<strong>18h45 à 19h00 : Accueil</strong>"));
        assertFalse(page.getDetails().contains("Code de Conduite"));
        assertTrue(
                page.getDetails()
                        .contains("https://www.parisjug.org/xwiki/wiki/oldversion/view/Speaker/DoudouxJeanMichel"),
                "should contain link https://www.parisjug.org/xwiki/wiki/oldversion/view/Speaker/DoudouxJeanMichel");

        // date time
        assertEquals("Mardi 8 décembre 2020 à 19h00", page.getDateTime(), "Date and time");

        // start time
        assertEquals("20201208T174500Z", page.getStartTime(), "start time");

        // end time
        assertEquals("20201208T191500Z", page.getEndTime(), "end time");

        // long title
        assertEquals("Paris JUG - Soirée Virtuelle: Le Java nouveau est arrivé : Java SE 15 (2020/12/08)",
                page.getLongTitle(), "Long title");

        // location
        assertEquals("https://www.twitch.tv/parisjug", page.getLocation(), "location");
    }

    @Test
    public void should_provide_clear_error_if_missing_title() {
        File file = new File(this.getClass().getResource("parisjug-20201208_missing_title.html").getFile());
        EventPage page = EventPage.fromHtmlLocalFile(file);

        EventPageCheckException ex = Assertions.assertThrows(EventPageCheckException.class, () -> {
            page.getTitle();
        });
        assertEquals(
                "The page should contain an element with the id \"title\". For instance: <div id=\"title\">Quarkus World Tour</div>.",
                ex.getMessage());

    }
}