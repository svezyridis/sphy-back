package sphy.auth.models;

public class LoginResponse {
    private final String status;
    private final String token;
    private final String message;

    public LoginResponse(String status, String token, String message) {
        this.status = status;
        this.token = token;
        this.message=message;
    }

    public String getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}
