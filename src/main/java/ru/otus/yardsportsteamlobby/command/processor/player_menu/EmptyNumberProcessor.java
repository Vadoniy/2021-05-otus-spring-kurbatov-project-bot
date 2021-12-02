package ru.otus.yardsportsteamlobby.command.processor.player_menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.PlayerMenuProcessor;
import ru.otus.yardsportsteamlobby.dto.RegistrationStateWithRequest;
import ru.otus.yardsportsteamlobby.enums.PlayerRegistrationState;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;

@Component
@RequiredArgsConstructor
public class EmptyNumberProcessor implements PlayerMenuProcessor {

    private final KeyBoardService keyBoardService;

    private final LocalizationService localizationService;

    @Override
    public SendMessage process(RegistrationStateWithRequest userData, Long chatId, String text, Long userId) {
        final var response = new SendMessage();
        response.setChatId(chatId.toString());
        try {
            userData.getCreatePlayerRequest().setNumber(Integer.parseInt(text));
            response.setText(localizationService.getLocalizedMessage("one-way.message.select-position", userId));
            response.setReplyMarkup(keyBoardService.createSelectPositionMarkup(userId));
            userData.setPlayerRegistrationState(PlayerRegistrationState.EMPTY_POSITION);
        } catch (NumberFormatException nfe) {
            response.setText(localizationService.getLocalizedMessage("one-way.message.wrong-number", userId));
        }
        return response;
    }
}
