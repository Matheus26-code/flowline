package com.flowline.flowline.controller;

import com.flowline.flowline.dto.ChatRequestDTO;
import com.flowline.flowline.dto.ChatResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.flowline.flowline.service.AiChatService;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGE')")
    public ResponseEntity<ChatResponseDTO> chat(@RequestBody @Valid ChatRequestDTO request) {
        ChatResponseDTO result = aiChatService.chat(request);
        return ResponseEntity.ok(result);
    }
}
