package ru.otus.yardsportsteamlobby.repository.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.otus.yardsportsteamlobby.dto.CreatePlayerRequest;

@RedisHash("create_player_by_user_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class CreatePlayerRequestByUserId {

    @Id
    private String userId;

    private CreatePlayerRequest createPlayerRequest;
}
