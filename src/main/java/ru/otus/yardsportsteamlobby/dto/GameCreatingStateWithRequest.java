package ru.otus.yardsportsteamlobby.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.otus.yardsportsteamlobby.enums.CreateGameState;

@Getter
@Setter
@Accessors(chain = true)
public class GameCreatingStateWithRequest {

    private CreateGameState createGameState;

    private CreateGameRequest createGameRequest;
}
