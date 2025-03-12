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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
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

    public void addWeekendHolidays(int year) {
        List<Holiday> weekendHolidays = new ArrayList<>();


        for (int month = 1; month <= 12; month++) {
            int daysInMonth = YearMonth.of(year, month).lengthOfMonth(); // Get correct days for each month

            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = LocalDate.of(year, month, day);
                if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    String name = (date.getDayOfWeek() == DayOfWeek.SATURDAY) ? "Saturday Holiday" : "Sunday Holiday";


                    if (!holidayRepository.existsByHolidayDateAndName(date, name)) {
                        weekendHolidays.add(new Holiday(date, name));
                    }
                }
            }
        }


        if (!weekendHolidays.isEmpty()) {
            holidayRepository.saveAll(weekendHolidays);
            System.out.println("Weekend holidays added for year " + year);
        } else {
            System.out.println("Weekend holidays already exist for year " + year);
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
