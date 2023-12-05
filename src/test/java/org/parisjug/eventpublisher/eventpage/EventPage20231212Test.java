package org.parisjug.eventpublisher.eventpage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class EventPage20231212Test {

        @Test
        public void test_inRealLifeEventPage() {
                File file = new File(this.getClass().getResource("parisjug-20231212.html").getFile());

                EventPage page = EventPage.fromHtmlLocalFile(file);
                assertNotNull(page, "should be able to load from a HUGO html file");

                // title
                assertEquals("Soirée Panama (GL)", page.getTitle(), "Title from page");

                // assert page.getPart1 should contains "19h30 : Panama - Foreign Function &
                // Memory"
                assertTrue(page.getPart1().contains("19h30 : Panama - Foreign Function"),
                                "part1 should contains 19h30 : Panama - Foreign Function");
                assertFalse(page.getPart1().contains("21h00 : Panama - Panama GL n jyz3D"),
                                "part1 should not contains 21h00 : Panama - Panama GL n jyz3D");
                assertFalse(page.getPart1().contains("20h30 : Buffet"), "part1 should not contains 20h30 : Buffet");

                // assert page.getPart2 should contains "21h00 : Panama - Panama GL n jyz3D"
                assertTrue(page.getPart2().contains("21h00 : Panama - Panama GL n jyz3D"),
                                "part2 should contains 21h00 : Panama - Panama GL n jyz3D");
                assertFalse(page.getPart2().contains("19h30 : Panama - Foreign Function"),
                                "part2 should not contains 19h30 : Panama - Foreign Function");
                assertFalse(page.getPart2().contains("figure"),
                                "part2 should not contains figure (part of buffet or sponsors)");
                assertFalse(page.getPart2().contains("Code de Conduite"), "part2 should not contains Code de Conduite");

                // details
                String details = page.getDetails();
                assertTrue(details.contains("19h : Accueil"));
                assertTrue(details.contains("20h30 : Buffet"), "details should contains 20h30 : Buffet");
                assertFalse(page.getDetails().contains("Code de Conduite"));
                assertTrue(
                                page.getDetails()
                                                .contains("Martin Pernollet"),
                                "should contain speaker Martin Pernollet");

                // date time
                assertEquals("Mardi 12 Décembre 2023 à 19h00", page.getDateTime(), "Date and time");

                // is virtual ?
                assertFalse(page.isVirtual(), "should be in Real life");

                // start time
                assertEquals("20231212T180000Z", page.getStartTime(), "start time");

                // end time
                assertEquals("20231212T210000Z", page.getEndTime(), "end time");

                // long title
                assertEquals("Paris JUG - Soirée Panama (GL) (2023/12/12)",
                                page.getLongTitle(), "Long title");

                // location
                assertEquals("https://www.parisjug.org/location/mirakl", page.getLocation(), "location");

        }
}