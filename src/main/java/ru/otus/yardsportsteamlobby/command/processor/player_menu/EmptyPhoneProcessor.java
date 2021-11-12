package ru.otus.yardsportsteamlobby.command.processor.player_menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.PlayerMenuProcessor;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.PlayerRegistrationState;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class EmptyPhoneProcessor implements PlayerMenuProcessor {

    private final LocalizationService localizationService;

    @Override
    public SendMessage process(RegistrationStateWithRequest userData, Long chatId, String text, Long userId) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        if (Pattern.matches("\\d{10,11}", text)) {
            userData.getCreatePlayerRequest().setPhone(text);
            response.setText(localizationService.getLocalizedMessage("one-way.message.enter-number"));
            userData.setPlayerRegistrationState(PlayerRegistrationState.EMPTY_NUMBER);
        } else {
            response.setText(localizationService.getLocalizedMessage("one-way.message.wrong-phone"));
        }
        return response;
    }
}
