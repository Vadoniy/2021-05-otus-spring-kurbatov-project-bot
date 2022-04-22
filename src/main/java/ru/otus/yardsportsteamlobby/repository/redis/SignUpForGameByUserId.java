package ru.otus.yardsportsteamlobby.repository.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.otus.yardsportsteamlobby.dto.SignUpForGameRequest;

@RedisHash("sign_up_for_game_by_user_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignUpForGameByUserId {

    @Id
    private String userId;

    private SignUpForGameRequest signUpForGameRequest;
}
