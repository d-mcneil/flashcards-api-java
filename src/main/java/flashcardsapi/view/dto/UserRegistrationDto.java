package flashcardsapi.view.dto;

import flashcardsapi.model.models.User;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UserRegistrationDto {
    @NotBlank
    @Length(max = 63)
    private String username;

    @NotBlank
    @Length(max = 63) // TODO: make custom validation to make maximum 72 bytes (that's the biggest input for bcrypt)
    private String password;

    @Length(max = 63)
    @NotBlank
    private String firstName;

    @NotBlank
    @Length(max = 63)
    private String lastName;

    @NotBlank
    @Email
    @Length(min = 5, max = 127)
    private String email;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public String getEmail() {
        return email;
    }

}
