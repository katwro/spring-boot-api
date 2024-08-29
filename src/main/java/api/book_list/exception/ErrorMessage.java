package api.book_list.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ErrorMessage {

    private int code;
    private String message;
    private LocalDateTime timestamp;

}
