package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.ActivityEntry;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ActivityLogHelper {

    private static final int MAX_ENTRIES = 20;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ObjectMapper objectMapper;

    public ActivityLogHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void log(HttpSession session, String action, Object response) {
        List<ActivityEntry> log = new ArrayList<>();
        if (session.getAttribute("activityLog") instanceof List<?> existing) {
            existing.stream()
                    .filter(ActivityEntry.class::isInstance)
                    .map(ActivityEntry.class::cast)
                    .forEach(log::add);
        }

        String json;
        try {
            json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        } catch (JsonProcessingException e) {
            json = String.valueOf(response);
        }

        log.add(0, new ActivityEntry(LocalDateTime.now().format(FMT), action, json));
        if (log.size() > MAX_ENTRIES) log = new ArrayList<>(log.subList(0, MAX_ENTRIES));
        session.setAttribute("activityLog", log);
    }
}
