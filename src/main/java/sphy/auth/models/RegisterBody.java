package sphy.auth.models;

public class RegisterBody {
    private String token;
    private User newUser;

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
    public User getNewUser() {
        return newUser;
    }
}
