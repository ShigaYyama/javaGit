package com.jmc.loginTest.controller;				
				
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;				
				
@Controller				
public class MenuController {				
    @GetMapping("/menu")				
    String menu() {				
        return "menu";				
    }				
}				
