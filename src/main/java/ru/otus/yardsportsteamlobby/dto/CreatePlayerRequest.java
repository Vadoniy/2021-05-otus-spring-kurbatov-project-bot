package ru.otus.yardsportsteamlobby.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.otus.yardsportsteamlobby.enums.CallbackQuerySelect;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Accessors(chain = true)
public class CreatePlayerRequest {

    private long userId;

    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "\\d{11}")
    private String phone;

    private CallbackQuerySelect position;

    private Integer number;
}