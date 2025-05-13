package com.ddabadi.booking_api.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ResponseNoContent {
    private String errorCode;
    private String errorDescription;
}
