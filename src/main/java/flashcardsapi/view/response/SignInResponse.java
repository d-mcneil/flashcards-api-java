package flashcardsapi.view.response;

public class SignInResponse {
    private final String token;
    private final UserResponse userResponse;

    public SignInResponse(String token, UserResponse userResponse) {
        this.token = token;
        this.userResponse = userResponse;
    }

    public String getToken() {
        return token;
    }

    public UserResponse getUserResponse() {
        return userResponse;
    }
}


