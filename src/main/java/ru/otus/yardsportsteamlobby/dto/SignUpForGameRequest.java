package ru.otus.yardsportsteamlobby.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SignUpForGameRequest {

    private Long gameId;

    private Long teamId;

    private Long userId;
}
