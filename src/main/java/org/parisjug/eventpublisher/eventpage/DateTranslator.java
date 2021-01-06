package org.parisjug.eventpublisher.eventpage;

import java.util.HashMap;
import java.util.Map;

/**
 * quick and dirty translator that would translate a date string in french to
 * english. Did not have time to find the class that wasn't loaded by GraalVM to
 * do that properly ... so here it is ...
 */
public class DateTranslator {

    Map<String, String> map = new HashMap<String, String>();

    public DateTranslator() {


        map.put("janvier", "01");
        map.put("février", "02");
        map.put("mars", "03");
        map.put("avril", "04");
        map.put("mai", "05");
        map.put("juin", "06");
        map.put("juillet", "07");
        map.put("août", "08");
        map.put("septembre", "09");
        map.put("octobre", "10");
        map.put("novembre", "11");
        map.put("décembre", "12");
    }

    public String translate(String dateToTranslate) {
        dateToTranslate = dateToTranslate.toLowerCase();
        for (String key : map.keySet()) {
            dateToTranslate = dateToTranslate.replace(key, map.get(key));
        }

        return dateToTranslate;
    }
}