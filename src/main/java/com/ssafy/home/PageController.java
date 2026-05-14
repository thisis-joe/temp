package com.ssafy.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/deals")
    public String deals() {
        return "deals";
    }

    @GetMapping("/houses")
    public String houses() {
        return "houses";
    }

    @GetMapping("/members")
    public String members() {
        return "members";
    }

    @GetMapping("/favorites")
    public String favorites() {
        return "favorites";
    }

    @GetMapping("/notices")
    public String notices() {
        return "notices";
    }

    @GetMapping("/regions")
    public String regions() {
        return "regions";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
}
