package model;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import java.sql.Timestamp;

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

    private Timestamp joinedDate;

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

    public Timestamp getJoinedDate() {
        return joinedDate;
    }

    public boolean isUserActive() {
        return isUserActive;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setJoinedDate(Timestamp joinedDate) {
        this.joinedDate = joinedDate;
    }

    public void setUserActive(boolean userActive) {
        isUserActive = userActive;
    }
}
