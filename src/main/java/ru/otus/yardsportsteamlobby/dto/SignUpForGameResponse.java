package ru.otus.yardsportsteamlobby.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpForGameResponse {

    private Integer errorCode;

    private GameDto gameDto;
}
