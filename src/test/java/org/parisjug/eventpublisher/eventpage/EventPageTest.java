package org.parisjug.eventpublisher.eventpage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class EventPageTest {

    @Test
    void test_virtual_event_page() {
        File file = new File(this.getClass().getResource("newsite_parisjug-20201208.html").getFile());

        EventPage page = EventPage.fromHtmlLocalFile(file);
        assertNotNull(page, "should be able to load from a HUGO html file");

        // title
        assertEquals("Soirée Virtuelle : Le Java nouveau est arrivé : Java SE 15", page.getTitle(), "Title from page");

        String details = page.getDetails();

        // details
        assertTrue(details.contains("18h45 à 19h00 : Accueil</h3>"));
        assertTrue(details.contains("Jean-Michel"));
        assertFalse(details.contains("Replays"));
        assertTrue(
                page.getDetails()
                        .contains("https://www.parisjug.org/speakers/jean-michel-doudoux"),
                "should contain link https://www.parisjug.org/speakers/jean-michel-doudoux");

        // is virtual ?
        assertTrue(page.isVirtual(), "should be virtual");

        // date time
        assertEquals("Mardi 8 décembre 2020 à 19h00", page.getDateTime(), "Date and time");

        // start time
        assertEquals("20201208T174500Z", page.getStartTime(), "start time");

        // end time
        assertEquals("20201208T191500Z", page.getEndTime(), "end time");

        // long title
        assertEquals("Paris JUG - Soirée Virtuelle : Le Java nouveau est arrivé : Java SE 15 (2020/12/08)",
                page.getLongTitle(), "Long title");

        // location
        assertEquals("https://www.twitch.tv/parisjug", page.getLocation(), "location");
    }

    @Test
    public void test_inRealLifeEvent_with_div_ids() {
        File file = new File(this.getClass().getResource("parisjug-iRL-div-id-20231114.html").getFile());

        EventPage page = EventPage.fromHtmlLocalFile(file);
        assertNotNull(page, "should be able to load from a HUGO html file");

        // title
        assertEquals("Soirée Loom", page.getTitle(), "Title from page");

        // details
        String details = page.getDetails();
        assertTrue(details.contains("19h00 : Accueil"));
        assertFalse(page.getDetails().contains("Code de Conduite"));
        assertTrue(
                page.getDetails()
                        .contains("https://www.parisjug.org/speakers/david-pequegnot"),
                "should contain link https://www.parisjug.org/speakers/david-pequegnot");

        // date time
        assertEquals("Mardi 14 Novembre 2023 à 19h00", page.getDateTime(), "Date and time");

        // is virtual ?
        assertFalse(page.isVirtual(), "should be in Real life");

        // start time
        assertEquals("20231114T180000Z", page.getStartTime(), "start time");

        // end time
        assertEquals("20231114T210000Z", page.getEndTime(), "end time");

        // long title
        assertEquals("Paris JUG - Soirée Loom (2023/11/14)",
                page.getLongTitle(), "Long title");

        // location
        assertEquals("https://www.parisjug.org/location/sfeir", page.getLocation(), "location");

    }

    @Test
    public void test_inRealLifeEvent() {
        File file = new File(this.getClass().getResource("parisjug-iRL-20230110-yb.html").getFile());

        EventPage page = EventPage.fromHtmlLocalFile(file);
        assertNotNull(page, "should be able to load from a HUGO html file");

        // title
        assertEquals("Soirée Young Blood X", page.getTitle(), "Title from page");

        // assert page.getPart1 should contains "19h30 : 204VS404 le duel du bon code http" but not "21h20 : Tech Lead REX"
        assertTrue(page.getPart1().contains("19h30 : 204VS404 le duel du bon code http"), "part1 should contains 19h30 : 204VS404 le duel du bon code http");
        assertFalse(page.getPart1().contains("21h20 : Tech Lead REX"), "part1 should not contains 21h20 : Tech Lead REX");
        assertFalse(page.getPart1().contains("20h30 à 21h00 : Buffet"), "part1 should not contains 20h30 à 21h00 : Buffet");

        // assert page.getPart2 should contains "21h20 : Tech Lead REX" but not "19h30 : 204VS404 le duel du bon code http"
        assertTrue(page.getPart2().contains("21h20 : Tech Lead REX"), "part2 should contains 21h20 : Tech Lead REX");
        assertFalse(page.getPart2().contains("19h30 : 204VS404 le duel du bon code http"), "part2 should not contains 19h30 : 204VS404 le duel du bon code http");
        assertFalse(page.getPart2().contains("figure"), "part2 should not contains figure (part of buffet)");


        // details
        String details = page.getDetails();
        assertTrue(details.contains("18h45 à 19h00: Accueil"));
        assertTrue(details.contains("20h30 à 21h00 : Buffet"), "details should contains 20h30 à 21h00 : Buffet");
        assertFalse(page.getDetails().contains("Code de Conduite"));
        assertTrue(
                page.getDetails()
                        .contains("Pierre Cheucle"),
                "should contain speaker Pierre Cheucle");

        // date time
        assertEquals("Mardi 10 janvier 2023 à 19h00", page.getDateTime(), "Date and time");

        // is virtual ?
        assertFalse(page.isVirtual(), "should be in Real life");

        // start time
        assertEquals("20230110T180000Z", page.getStartTime(), "start time");

        // end time
        assertEquals("20230110T210000Z", page.getEndTime(), "end time");

        // long title
        assertEquals("Paris JUG - Soirée Young Blood X (2023/01/10)",
                page.getLongTitle(), "Long title");

        // location
        assertEquals("https://www.parisjug.org/location/agorapulse/", page.getLocation(), "location");





    }
}