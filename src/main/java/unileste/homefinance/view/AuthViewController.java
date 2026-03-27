package unileste.homefinance.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    @GetMapping("/public/email-confirmed")
    public String emailConfirmedPage() {
        return "email-confirmed";
    }
}