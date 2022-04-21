package ru.otus.yardsportsteamlobby.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@ToString
public class CreateGameRequest {

    @JsonFormat(pattern = GameDto.GAME_DATE_TIME_FORMAT)
    private LocalDateTime gameDateTime;

    private Integer teamCapacity;

    private String teamNameA;

    private String teamNameB;
}
