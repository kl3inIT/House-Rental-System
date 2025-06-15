package com.rental.houserental.controller.landlord;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class LandlordDashboardController {

    @GetMapping("/landlord/dashboard")
    public String dashboard() {
        return "landlord/dashboard";
    }
}