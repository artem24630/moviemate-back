package com.example.filmtinder.parser;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ParserController {

    private final ParserService parserService;

    public ParserController(ParserService parserService) {
        this.parserService = parserService;
    }

    @GetMapping("/api/v1/start_parsing")
    public ResponseEntity<?> startParsing() {
        parserService.startParsing();
        return ResponseEntity.ok("Parsing started");
    }

    @GetMapping("/api/v1/stop_parsing")
    public ResponseEntity<?> stopParsing() {
        parserService.stopParsing();
        return ResponseEntity.ok("Parsing stopped");
    }

    @GetMapping("/api/v1/delete_parsed")
    public ResponseEntity<?> deleteParsed() {
        parserService.clearParsed();
        return ResponseEntity.ok("Deleted parsed");
    }

    @GetMapping("/api/v1/restart_parsing")
    public ResponseEntity<?> restartParsing() {
        deleteParsed();
        startParsing();
        return ResponseEntity.ok("Restarted parsing");
    }
}
