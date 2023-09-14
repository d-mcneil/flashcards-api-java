package flashcardsapi.model.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import flashcardsapi.security.Authority;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User {

    private int userId;
// TODO: pretty sure these validation annotations weren't doing anything, because the info the user sends is actually UserRegistrationDto

//    @NotNull
//    @Length(max = 63)
    private String username;

//    @NotNull
//    @Length(max = 63)
    private String firstName;

//    @NotNull
//    @Length(max = 63)
    private String lastName;

//    @NotNull
//    @Length(max = 127)
    private String email;

    private Timestamp joinedDate;

//    @NotNull
    private boolean isUserActive;

    private Set<Authority> authorities = new HashSet<>();

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

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public void setAuthorities(String authorities) {
        String[] roles = authorities.split(",");
        for(String role : roles) {
            this.authorities.add(new Authority("ROLE_" + role));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId &&
                isUserActive == user.isUserActive &&
                Objects.equals(username, user.username) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(email, user.email) &&
                Objects.equals(joinedDate, user.joinedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, firstName, lastName, email, joinedDate, isUserActive);
    }
}
