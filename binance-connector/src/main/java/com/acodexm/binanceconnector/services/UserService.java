package com.acodexm.binanceconnector.services;

import com.acodexm.binanceconnector.domain.User;
import java.util.Optional;

public interface UserService {

  /**
   * Gets the current active user
   *
   * @return the active user
   */
  User getCurrentUser();

  /**
   * Finds a user by ID
   *
   * @param userId the user ID
   * @return optional containing the user if found
   */
  Optional<User> findByUserId(String userId);
}
