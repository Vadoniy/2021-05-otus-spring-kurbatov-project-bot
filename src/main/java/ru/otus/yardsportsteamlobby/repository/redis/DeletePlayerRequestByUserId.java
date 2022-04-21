package ru.otus.yardsportsteamlobby.repository.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("delete_player_by_user_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DeletePlayerRequestByUserId {

    @Id
    private String userId;
}
