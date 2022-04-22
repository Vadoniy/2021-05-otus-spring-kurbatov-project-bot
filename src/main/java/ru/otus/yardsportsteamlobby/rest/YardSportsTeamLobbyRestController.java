package ru.otus.yardsportsteamlobby.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.yardsportsteamlobby.bot.YardSportsTeamLobbyWebhookBot;

@RestController
@RequiredArgsConstructor
@Slf4j
public class YardSportsTeamLobbyRestController {

    private final YardSportsTeamLobbyWebhookBot yardSportsTeamLobbyWebhookBot;

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return yardSportsTeamLobbyWebhookBot.onWebhookUpdateReceived(update);
    }
}
