package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/user")
    @PreAuthorize("@permissionServiceImpl.hasPermission('helloUser')")
    public String hello() {
        return "hello user !";
    }

    @GetMapping("/admin")
    @PreAuthorize("@permissionServiceImpl.hasPermission('helloAdmin')")
    public String admin() {
        return "hello admin !";
    }
}
