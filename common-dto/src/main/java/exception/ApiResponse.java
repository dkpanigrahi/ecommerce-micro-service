package exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {

    private boolean status;          // true / false
    private String message;          // success or error message
    private T data;                  // generic object (object, list, etc.)
    private Long totalItems;         // for pagination (optional)

}
