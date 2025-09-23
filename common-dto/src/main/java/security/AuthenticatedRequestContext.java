package security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticatedRequestContext {

    private  String userUuid;
    private  String phoneNumber;
    private  String role;
    private  Boolean isVerified;
}
