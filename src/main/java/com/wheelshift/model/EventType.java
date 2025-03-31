package com.wheelshift.model;

public enum EventType {
    TEST_DRIVE("test_drive"),
    INSPECTION("inspection"),
    DELIVERY("delivery"),
    NEGOTIATION("negotiation"),
    MAINTENANCE("maintenance");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventType fromString(String text) {
        for (EventType type : EventType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with value " + text + " found");
    }
}