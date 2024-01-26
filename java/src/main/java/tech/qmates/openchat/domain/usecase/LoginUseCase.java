package tech.qmates.openchat.domain.usecase;

import tech.qmates.openchat.domain.repository.UserRepository;
import tech.qmates.openchat.dto.UserInfo;

public class LoginUseCase {
  private final UserRepository userRepository;

  public LoginUseCase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserInfo run(String username, String password) {
    var user = userRepository.getUserByCredentials(username, password);
    var userInfo = new UserInfo(user.uuid(), user.username(), user.about());
    return userInfo;
  }
}
