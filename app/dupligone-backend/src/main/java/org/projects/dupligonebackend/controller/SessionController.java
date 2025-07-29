package org.projects.dupligonebackend.controller;


import org.projects.dupligonebackend.model.PhotoSession;
import org.projects.dupligonebackend.service.PhotoSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "api/session")
public class SessionController {

    private final PhotoSessionService service;

    public SessionController(PhotoSessionService service){
        this.service = service;
    }

    @PostMapping("/start")
    public ResponseEntity<UUID> startSession(){
        PhotoSession session = service.createSession();
        return ResponseEntity.ok(session.getId());
    }

}
