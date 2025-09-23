package DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseEntityObject {

    private boolean status;
    private String message;
    private Object object;
    private Long count;

    public ResponseEntityObject(boolean status, String message, Object object, Long count) {
        this.status = status;
        this.message = message;
        this.object = object;
        this.count = count;
    }
}
