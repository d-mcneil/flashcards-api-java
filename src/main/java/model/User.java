package model;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class User {
    private int userId;
    @NotNull
    @Length(max = 63)
    private String username;
    @NotNull
    @Length(max = 63)
    private String firstName;
    @NotNull
    @Length(max = 63)
    private String lastName;
    @NotNull
    @Length(max = 127)
    private String email;
    @NotNull
    private LocalDateTime joinedDate;
    @NotNull
    private boolean isUserActive;

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
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

    public LocalDateTime getJoinedDate() {
        return joinedDate;
    }

    public boolean isUserActive() {
        return isUserActive;
    }
}
