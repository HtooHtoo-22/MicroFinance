package com.microfinance.code.controller;

import com.microfinance.code.model.Holiday;
import com.microfinance.code.service.HolidayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

    private final HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @GetMapping("/fetch/{year}")
    public String fetchHolidays(@PathVariable int year) {
        holidayService.fetchHolidaysFromAPI(year);
        return "Fetching holidays for year " + year;
    }

    @GetMapping("/all")
    public List<Holiday> getAllHolidays() {
        return holidayService.getAllHolidays();
    }

    // ✅ View holidays for a specific year
    @GetMapping("/{year}")
    public List<Holiday> getHolidaysByYear(@PathVariable int year) {
        return holidayService.getHolidaysByYear(year);
    }
}