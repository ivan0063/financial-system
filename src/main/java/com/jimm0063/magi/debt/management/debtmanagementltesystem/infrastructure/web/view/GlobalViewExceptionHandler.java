package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice(basePackages =
        "com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view")
public class GlobalViewExceptionHandler {

    private final ActivityLogHelper activityLogHelper;

    public GlobalViewExceptionHandler(ActivityLogHelper activityLogHelper) {
        this.activityLogHelper = activityLogHelper;
    }

    @ExceptionHandler(Exception.class)
    public String handleViewException(Exception e, HttpSession session, Model model) {
        Map<String, String> errorInfo = new LinkedHashMap<>();
        errorInfo.put("exception", e.getClass().getName());
        errorInfo.put("message", e.getMessage());
        if (e.getCause() != null) {
            errorInfo.put("cause", e.getCause().getClass().getName() + ": " + e.getCause().getMessage());
        }
        activityLogHelper.log(session, "UNHANDLED ERROR", errorInfo);

        model.addAttribute("errorType", e.getClass().getSimpleName());
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("errorCause", e.getCause() != null ? e.getCause().getMessage() : null);
        return "error/view-error";
    }
}
