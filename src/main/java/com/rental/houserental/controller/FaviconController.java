package com.rental.houserental.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FaviconController {
    @RequestMapping("favicon.ico")
    @ResponseBody
    public ResponseEntity<ClassPathResource> favicon() {
        return ResponseEntity.ok(new ClassPathResource("static/favicon.ico"));
    }
}
