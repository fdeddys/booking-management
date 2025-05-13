package com.ddabadi.booking_api.enums;

public enum Status {
    READY("READY"),
    BOOKED("BOOKED"),
    NOT_READY("NOT_READY");

    private String data;

    Status(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
