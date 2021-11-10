package ru.otus.yardsportsteamlobby.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameDto {

    public static final String GAME_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm";

    private Long gameId;

    @JsonFormat(pattern = GAME_DATE_TIME_FORMAT)
    private LocalDateTime gameDateTime;

    private int teamCapacity;

    private TeamDto teamA;

    private TeamDto teamB;
}