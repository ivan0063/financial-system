package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.exceptions;

/**
 * Thrown when a PDF statement is encrypted and cannot be read with the password
 * (if any) supplied so far. {@link #isPasswordWasTried()} distinguishes "no password
 * given yet" from "the given password was wrong", so callers can prompt accordingly.
 */
public class PdfPasswordRequiredException extends RuntimeException {

    private final boolean passwordWasTried;

    public PdfPasswordRequiredException(String message, boolean passwordWasTried) {
        super(message);
        this.passwordWasTried = passwordWasTried;
    }

    public boolean isPasswordWasTried() {
        return passwordWasTried;
    }
}
