package com.microfinance.code.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.code.model.Holiday;
import com.microfinance.code.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HolidayService {

    @Value("${holiday.api.key}")
    private String apiKey;

    private final HolidayRepository holidayRepository;

    public HolidayService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public void fetchHolidaysFromAPI(int year) {
        String url = UriComponentsBuilder.fromHttpUrl("https://calendarific.com/api/v2/holidays")
                .queryParam("api_key", apiKey)
                .queryParam("country", "MM")
                .queryParam("year", year)
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());

                List<Holiday> holidaysToSave = new ArrayList<>();
                JsonNode holidaysArray = root.path("response").path("holidays");

                for (JsonNode holidayNode : holidaysArray) {
                    String name = holidayNode.path("name").asText();
                    String dateStr = holidayNode.path("date").path("iso").asText();
                    // Extract only the date (yyyy-MM-dd) part from the full DateTime string
                    String dateOnly = dateStr.substring(0, 10);
                    LocalDate date = LocalDate.parse(dateOnly);


                    // Check if the holiday already exists in the database
                    if (!holidayRepository.existsByHolidayDateAndName(date, name)) {
                        holidaysToSave.add(new Holiday(date, name));
                    }
                }

                // Save only new holidays
                if (!holidaysToSave.isEmpty()) {
                    holidayRepository.saveAll(holidaysToSave);
                    System.out.println("New holidays saved to the database!");
                } else {
                    System.out.println("No new holidays to save.");
                }

            } catch (Exception e) {
                System.out.println("Error parsing API response: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to fetch holidays");
        }
    }

    public List<Holiday> getAllHolidays() {
        return holidayRepository.findAll();
    }

    // ✅ Fetch holidays by year
    public List<Holiday> getHolidaysByYear(int year) {
        return holidayRepository.findAll().stream()
                .filter(holiday -> holiday.getHolidayDate().getYear() == year)
                .collect(Collectors.toList());
    }

}
