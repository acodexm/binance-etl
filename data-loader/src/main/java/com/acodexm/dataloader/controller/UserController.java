package com.acodexm.dataloader.controller;

import com.acodexm.dataloader.entity.UserEntity;
import com.acodexm.dataloader.repository.UserRepository;
import com.acodexm.dataloader.service.PortfolioService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;
  private final PortfolioService portfolioService;

  @GetMapping
  public ResponseEntity<List<UserEntity>> getAllUsers() {
    List<UserEntity> users = userRepository.findAll();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserEntity> getUserById(@PathVariable String userId) {
    return userRepository
        .findByUserId(userId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/{userId}/portfolio")
  public ResponseEntity<Map<String, Object>> getUserPortfolio(@PathVariable String userId) {
    return userRepository
        .findByUserId(userId)
        .map(
            user -> {
              Map<String, Object> portfolioSummary = portfolioService.getPortfolioSummary(userId);
              return ResponseEntity.ok(portfolioSummary);
            })
        .orElse(ResponseEntity.notFound().build());
  }
}
