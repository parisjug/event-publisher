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


        map.put("janvier", "1");
        map.put("février", "2");
        map.put("mars", "3");
        map.put("avril", "4");
        map.put("mai", "5");
        map.put("juin", "6");
        map.put("juillet", "7");
        map.put("août", "8");
        map.put("septembre", "9");
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