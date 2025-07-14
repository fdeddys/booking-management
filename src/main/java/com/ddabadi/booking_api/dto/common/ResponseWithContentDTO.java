package com.ddabadi.booking_api.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ResponseWithContentDTO extends ResponseNoContent {

    private Object object;
    private String errorCode;
    private String errorDescription;
}
