package ru.otus.yardsportsteamlobby.command.processor.player_menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.PlayerMenuProcessor;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.PlayerRegistrationState;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

@Component
@RequiredArgsConstructor
public class EmptyNameProcessor implements PlayerMenuProcessor {

    private final LocalizationService localizationService;

    @Override
    public SendMessage process(RegistrationStateWithRequest userData, Long chatId, String text, Long userId) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        userData.getCreatePlayerRequest().setName(text);
        response.setText(localizationService.getLocalizedMessage("one-way.message.enter-your-phone", userId));
        userData.setPlayerRegistrationState(PlayerRegistrationState.EMPTY_PHONE);
        return response;
    }
}
