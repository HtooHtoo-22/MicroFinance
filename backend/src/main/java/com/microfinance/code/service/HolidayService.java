//package com.microfinance.code.service;
//
//import com.microfinance.code.model.Holiday;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Service
//public class HolidayService {
//
//    @Value("${holiday.api.key}")
//    private String apiKey;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    public List<Holiday> getMyanmarHoliday(){
//        String url = UriComponentsBuilder.fromHttpUrl("https://holidayapi.com/v1/holidays")
//                .queryParam("key", apiKey)
//                .queryParam("country", "MM")
//                .queryParam("year", "2023")
//                .queryParam("pretty", true)
//                .toUriString();
//        Holiday[] holidays = restTemplate.getForObject(url, Holiday[].class);
//        return Arrays.asList(holidays != null ? holidays : new Holiday[0]);
//    }
//
//}
