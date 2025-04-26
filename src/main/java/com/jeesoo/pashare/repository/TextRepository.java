package com.jeesoo.pashare.repository;

import com.jeesoo.pashare.entity.Text;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;


public interface TextRepository extends JpaRepository<Text, Long> {
    Optional<Text> findByPasscode(String passcode);
    void deleteByPasscode(String passcode);
    
    Optional<Text> findByShareId(String shareId);
    void deleteByExpiryTimeBefore(LocalDateTime dateTime);
}