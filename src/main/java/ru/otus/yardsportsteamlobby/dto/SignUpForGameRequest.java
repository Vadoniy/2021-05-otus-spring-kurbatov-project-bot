package ru.otus.yardsportsteamlobby.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class SignUpForGameRequest {

    @JsonIgnore
    private List<GameDto> lastGames;

    private Long gameId;

    private Long teamId;

    private Long userId;
}
