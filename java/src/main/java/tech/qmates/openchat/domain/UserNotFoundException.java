package tech.qmates.openchat.domain;

import java.util.UUID;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(UUID uuid) {
        super("User with uuid [" + uuid + "] not found!");
    }
    public UserNotFoundException(String username) {
        super("User with username [" + username + "] not found!");
    }
}
