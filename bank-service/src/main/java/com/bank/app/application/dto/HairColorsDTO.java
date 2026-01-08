package com.bank.app.application.dto;

import com.bank.app.data.entities.HairColors;

public enum HairColorsDTO {
    BLACK,
    WHITE,
    GRAY;

    public static HairColors toDomain(HairColorsDTO dto) {
        if (dto == null) {
            return null;
        }

        return switch (dto) {
            case WHITE -> HairColors.WHITE;
            case BLACK -> HairColors.BLACK;
            case GRAY -> HairColors.GRAY;
        };
    }
}
