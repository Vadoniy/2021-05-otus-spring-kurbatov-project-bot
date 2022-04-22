package ru.otus.yardsportsteamlobby.command.processor.player_menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.yardsportsteamlobby.command.processor.AbstractCommonProcessor;
import ru.otus.yardsportsteamlobby.enums.BotState;
import ru.otus.yardsportsteamlobby.service.BotStateService;
import ru.otus.yardsportsteamlobby.service.DeletePlayerRequestByUserIdService;
import ru.otus.yardsportsteamlobby.service.KeyBoardService;
import ru.otus.yardsportsteamlobby.service.LocalizationService;
import ru.otus.yardsportsteamlobby.service.UserRoleService;

@Service
public class RejectDeletePlayerProcessor extends AbstractCommonProcessor {

    private final DeletePlayerRequestByUserIdService deletePlayerRequestByUserIdService;

    private final UserRoleService userRoleService;

    public RejectDeletePlayerProcessor(BotStateService botStateService, KeyBoardService keyBoardService,
                                       LocalizationService localizationService,
                                       DeletePlayerRequestByUserIdService deletePlayerRequestByUserIdService, UserRoleService userRoleService) {
        super(botStateService, keyBoardService, localizationService);
        this.deletePlayerRequestByUserIdService = deletePlayerRequestByUserIdService;
        this.userRoleService = userRoleService;
    }

    @Override
    public SendMessage process(Long chatId, Long userId, String text, String userRole) {
        return super.process(chatId, userId, text, userRole);
    }

    @Override
    protected void fillTheResponse(SendMessage sendMessage, Long chatId, Long userId, String text, String userRole) {
        sendMessage.setText(localizationService.getLocalizedMessage("one-way.message.request-is-deleted", userId));
        deletePlayerRequestByUserIdService.removeDeletePlayerIdRequest(userId);
        sendMessage.setReplyMarkup(keyBoardService.createMainMenuKeyboard(userId, userRoleService.getUserRoleByUserId(userId)));
        botStateService.saveBotStateForUser(userId, BotState.MAIN_MENU);
    }
}
