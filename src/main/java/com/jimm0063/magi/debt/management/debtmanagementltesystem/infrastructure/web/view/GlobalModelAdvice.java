package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.ActivityEntry;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("activityLog")
    public List<ActivityEntry> activityLog(HttpSession session) {
        Object log = session.getAttribute("activityLog");
        if (log instanceof List<?> list) {
            return list.stream()
                    .filter(ActivityEntry.class::isInstance)
                    .map(ActivityEntry.class::cast)
                    .toList();
        }
        return List.of();
    }
}
