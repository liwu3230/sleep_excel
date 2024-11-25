package org.example.backend.web.model.enums;

import lombok.Getter;

@Getter
public enum SwitchStatus {

    ENABLE(0, "正常"),
    DISABLE(1, "停用");

    private final String label;
    private final int value;

    SwitchStatus(int value, String label) {
        this.value = value;
        this.label = label;
    }
}

