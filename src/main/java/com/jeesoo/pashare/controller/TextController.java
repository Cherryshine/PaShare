package com.jeesoo.pashare.controller;

import com.jeesoo.pashare.dto.TextRequestDto;
import com.jeesoo.pashare.dto.TextResponseDto;
import com.jeesoo.pashare.service.TextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class TextController {

    private final TextService textService;

    // 패스코드 사용 여부 확인 엔드포인트
    @GetMapping("/api/check-passcode")
    public ResponseEntity<Map<String, Boolean>> checkPasscode(@RequestParam String code) {
        boolean inUse = textService.isPasscodeInUse(code);
        Map<String, Boolean> response = new HashMap<>();
        response.put("inUse", inUse);
        return ResponseEntity.ok(response);
    }

    // 텍스트 저장 엔드포인트
    @PostMapping("/api/save-text")
    public ResponseEntity<TextResponseDto> saveText(@Valid @RequestBody TextRequestDto requestDto) {
        TextResponseDto response = textService.saveText(requestDto);
        return ResponseEntity.ok(response);
    }

    // 텍스트 조회 엔드포인트
    @GetMapping("/api/get-text")
    public ResponseEntity<TextResponseDto> getText(@RequestParam String code) {
        TextResponseDto response = textService.getText(code);
        return ResponseEntity.ok(response);
    }
    
    // 링크로 텍스트 공유 엔드포인트
    @PostMapping("/api/share-via-link")
    public ResponseEntity<TextResponseDto> shareViaLink(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new TextResponseDto("텍스트를 입력해주세요.", null));
        }
        TextResponseDto response = textService.shareTextViaLink(text);
        return ResponseEntity.ok(response);
    }
    
    // 공유 링크 페이지 렌더링
    @GetMapping("/share/{shareId}")
    public String getSharedTextPage(@PathVariable String shareId, Model model) {
        TextResponseDto response = textService.getTextByShareId(shareId);
        model.addAttribute("sharedText", response.getText());
        model.addAttribute("message", response.getMessage());
        model.addAttribute("shareId", shareId);
        model.addAttribute("isSharedLink", true);
        model.addAttribute("expiryTime", response.getExpiryTime());
        return "shared-text";
    }

    // admin 접근
    @GetMapping("/api/admin")
    public String admin(){
        return "admin";
    }
    
    // 어드민용 전체 텍스트 삭제 엔드포인트
    @DeleteMapping("/api/admin/delete-all")
    public ResponseEntity<String> deleteAllTexts() {
        textService.deleteAllTexts();
        return ResponseEntity.ok("All texts have been deleted successfully.");
    }
}