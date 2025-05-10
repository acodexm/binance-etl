package com.acodexm.binanceconnector.controller;

import com.acodexm.binanceconnector.domain.User;
import com.acodexm.binanceconnector.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/current")
  public ResponseEntity<User> getCurrentUser() {
    User currentUser = userService.getCurrentUser();
    log.debug("Returning current user: {}", currentUser);
    return ResponseEntity.ok(currentUser);
  }
}
