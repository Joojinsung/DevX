package com.backend.devx.testContoller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class testHello {
    @GetMapping("/hellotest")
    public String get() {
        return "test";
    }
}
