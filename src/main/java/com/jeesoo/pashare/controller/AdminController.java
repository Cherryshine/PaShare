package com.jeesoo.pashare.controller;

import com.jeesoo.pashare.dto.AdminLoginDto;
import com.jeesoo.pashare.dto.TextResponseDto;
import com.jeesoo.pashare.service.AdminService;
import com.jeesoo.pashare.service.TextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final TextService textService;

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @PostMapping("/api/admin/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody AdminLoginDto loginDto) {
        boolean isValid = adminService.validatePassword(loginDto.getPassword());
        if (isValid) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Invalid password");
    }

    @GetMapping("/api/admin/texts")
    @ResponseBody
    public ResponseEntity<List<TextResponseDto>> getAllTexts() {
        return ResponseEntity.ok(textService.getAllTexts());
    }

    @DeleteMapping("/api/admin/texts/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteText(@PathVariable Long id) {
        textService.deleteTextById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/admin/texts")
    @ResponseBody
    public ResponseEntity<?> deleteAllTexts() {
        textService.deleteAllTexts();
        return ResponseEntity.ok().build();
    }
} 