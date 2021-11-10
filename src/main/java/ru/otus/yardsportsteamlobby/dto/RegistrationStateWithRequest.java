package ru.otus.yardsportsteamlobby.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.otus.yardsportsteamlobby.enums.PlayerRegistrationState;

@Getter
@Setter
@Accessors(chain = true)
public class RegistrationStateWithRequest {

    private PlayerRegistrationState playerRegistrationState;

    private CreatePlayerRequest createPlayerRequest;
}
