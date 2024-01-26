package unit.tech.qmates.openchat.domain.usecase;

import org.junit.jupiter.api.Test;
import tech.qmates.openchat.domain.entity.RegisteredUser;
import tech.qmates.openchat.domain.repository.UserRepository;
import tech.qmates.openchat.domain.usecase.LoginUseCase;
import tech.qmates.openchat.dto.UserInfo;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginUseCaseTest {
  private final UserRepository userRepository = mock(UserRepository.class);

  @Test
  void whenUserNameDoesNotExist_ThenAnExceptionIsThrown() {

  }

  @Test
  void whenCredentialsAreValid_ThenUserInfoAreReturned() {
    UUID userId = UUID.randomUUID();
    when(userRepository.getUserByCredentials("pippo", "password")).thenReturn(new RegisteredUser(
      userId,
      "pippo",
      "forza roma!"
    ));

    var userInfo = new LoginUseCase().run("pippo", "password");

    assertEquals(new UserInfo(userId, "pippo", "forza roma!"), userInfo);
  }
}
