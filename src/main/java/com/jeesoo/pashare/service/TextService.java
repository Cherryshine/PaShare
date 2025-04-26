package com.jeesoo.pashare.service;

import com.jeesoo.pashare.dto.TextRequestDto;
import com.jeesoo.pashare.dto.TextResponseDto;
import com.jeesoo.pashare.entity.Text;
import com.jeesoo.pashare.repository.TextRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TextService {

    private final TextRepository textRepository;

    // 패스코드가 이미 사용 중인지 확인
    @Transactional(readOnly = true)
    public boolean isPasscodeInUse(String passcode) {
        return textRepository.findByPasscode(passcode).isPresent();
    }

    // 텍스트 저장
    @Transactional
    public TextResponseDto saveText(TextRequestDto requestDto) {
        Optional<Text> existingText = textRepository.findByPasscode(requestDto.getPasscode());
        if (existingText.isPresent()) {
            return new TextResponseDto("이미 사용 중인 패스코드입니다 (already in use)", null);
        }

        Text textEntity = new Text();
        textEntity.setText(requestDto.getText());
        textEntity.setPasscode(requestDto.getPasscode());
        
        // 만료 시간 설정 (현재 시간 + 24시간)
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(24);
        textEntity.setExpiryTime(expiryTime);
        
        textRepository.save(textEntity);

        return new TextResponseDto("텍스트가 성공적으로 저장되었습니다!", null);
    }
    
    // 링크로 텍스트 공유 (24시간 유효)
    @Transactional
    public TextResponseDto shareTextViaLink(String text) {
        // 랜덤 링크 ID 생성 (UUID의 첫 8자리 사용)
        String shareId = UUID.randomUUID().toString().substring(0, 8);
        
        // 만료 시간 설정 (현재 시간 + 24시간)
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(24);
        
        Text textEntity = new Text();
        textEntity.setText(text);
        textEntity.setShareId(shareId);
        textEntity.setExpiryTime(expiryTime);
        textEntity.setLinkShared(true);
        textRepository.save(textEntity);
        
        return new TextResponseDto("텍스트가 링크로 공유되었습니다!", shareId);
    }
    
    // 링크로 공유된 텍스트 조회
    @Transactional
    public TextResponseDto getTextByShareId(String shareId) {
        Optional<Text> textEntity = textRepository.findByShareId(shareId);
        
        if (textEntity.isPresent()) {
            Text text = textEntity.get();
            
            // 만료 여부 확인
            if (text.getExpiryTime().isBefore(LocalDateTime.now())) {
                textRepository.delete(text);
                return new TextResponseDto("링크가 만료되었습니다.", null);
            }
            
            return new TextResponseDto("링크로 공유된 텍스트입니다.", text.getText(), text.getExpiryTime());
        } else {
            return new TextResponseDto("해당 링크로 공유된 텍스트가 없습니다.", null);
        }
    }

    // 텍스트 조회 및 해당 엔티티 삭제
    @Transactional
    public TextResponseDto getText(String passcode) {
        Optional<Text> textEntity = textRepository.findByPasscode(passcode);

        if (textEntity.isPresent()) {
            Text text = textEntity.get();
            
            String retrievedText = text.getText();
            textRepository.deleteByPasscode(passcode); // 해당 엔티티 삭제
            return new TextResponseDto("텍스트를 성공적으로 불러왔습니다!", retrievedText);
        } else {
            return new TextResponseDto("해당 패스코드로 저장된 텍스트가 없습니다.", null);
        }
    }

    // 데이터베이스 전체 삭제
    @Transactional
    public void deleteAllTexts() {
        textRepository.deleteAll();
    }
    
    // 만료된 텍스트 정기 삭제 (매 시간 실행)
    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    @Transactional
    public void deleteExpiredTexts() {
        LocalDateTime now = LocalDateTime.now();
        textRepository.deleteByExpiryTimeBefore(now);
    }

    @Transactional(readOnly = true)
    public List<TextResponseDto> getAllTexts() {
        return textRepository.findAll().stream()
                .map(text -> new TextResponseDto("", text.getText()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTextById(Long id) {
        textRepository.deleteById(id);
    }
}