package org.projects.dupligonebackend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "api/session")
public class SessionController {

    @PostMapping("/start")
    public ResponseEntity<UUID> startSession(){
        UUID sessionId = UUID.randomUUID();
        return ResponseEntity.ok(sessionId);
    }

}
