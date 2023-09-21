package flashcardsapi.view.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public class UserSignInDto {
    @NotBlank
    @Length(max = 63)
    private String username;

    @NotBlank
    @Length(max = 63) // TODO: make custom validation to make maximum 72 bytes (that's the biggest input for bcrypt)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
