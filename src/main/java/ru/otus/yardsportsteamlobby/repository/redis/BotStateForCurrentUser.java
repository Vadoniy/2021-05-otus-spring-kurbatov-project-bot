package ru.otus.yardsportsteamlobby.repository.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.otus.yardsportsteamlobby.enums.BotState;

@RedisHash("bot_state")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BotStateForCurrentUser {

    @Id
    private String userId;

    private BotState botState;
}