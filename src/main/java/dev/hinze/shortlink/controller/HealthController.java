package dev.hinze.shortlink.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Void> getHealth() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
