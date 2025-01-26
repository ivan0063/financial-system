package mx.magi.jimm0063.financial.system.debt.application.component;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class DebtHashComponent {
    public String hashId(Double monthAmount, Integer monthsPaid, Integer monthsFinanced) {
        try {
            String input = monthAmount + String.valueOf(monthsPaid) + monthsFinanced;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash); // URL-safe string
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }
}
