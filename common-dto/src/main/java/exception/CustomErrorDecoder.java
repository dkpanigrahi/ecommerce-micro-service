package exception;

import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 400:
                return new IllegalArgumentException("Bad Request from " + methodKey);
            case 404:
                return new RuntimeException("Resource not found from " + methodKey);
            case 500:
                return new RuntimeException("Internal server error from " + methodKey);
            default:
                return defaultDecoder.decode(methodKey, response);
        }
    }
}
