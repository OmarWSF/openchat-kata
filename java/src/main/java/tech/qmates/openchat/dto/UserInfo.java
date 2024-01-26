package tech.qmates.openchat.dto;

import java.util.UUID;

public record UserInfo(UUID userId, String username, String about) {}
