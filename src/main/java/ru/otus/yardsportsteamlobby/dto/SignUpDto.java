package ru.otus.yardsportsteamlobby.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class SignUpDto {

    private List<GameDto> lastGames;

    private Long selectedGameId;

    private Long selectedTeamId;
}
