package org.example.backend.web.model.enums;

import lombok.Getter;

@Getter
public enum CommonStatus {

    ENABLE(0, "正常"),
    DISABLE(1, "停用"),
    DELETED(2, "删除");

    private final int value;
    private final String label;

    CommonStatus(int value, String label) {
        this.value = value;
        this.label = label;
    }

}

