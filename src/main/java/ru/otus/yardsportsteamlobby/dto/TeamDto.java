package ru.otus.yardsportsteamlobby.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto {

    private Long teamId;

    private String teamName;

    private List<PlayerDto> lineUp;
}