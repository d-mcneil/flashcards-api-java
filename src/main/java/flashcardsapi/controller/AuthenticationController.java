package flashcardsapi.controller;

import flashcardsapi.view.dto.UserLoginDto;
import flashcardsapi.view.dto.UserRegistrationDto;
import flashcardsapi.view.response.LoginResponse;
import flashcardsapi.exception.DaoException;
import flashcardsapi.exception.exceptionmessages.ExceptionMessages;
import flashcardsapi.model.models.User;
import flashcardsapi.model.dao.UserDao;
import flashcardsapi.view.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import flashcardsapi.security.jwt.TokenProvider;

import javax.validation.Valid;

@RestController
public class AuthenticationController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserDao userDao;

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/sign-in", method = RequestMethod.POST)
    public LoginResponse signIn(@Valid @RequestBody UserLoginDto userLoginDto) {
        return handleLogin(userLoginDto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public LoginResponse register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {

        boolean isUsernameAlreadyInUse = userDao.getUserIdByUsername(userRegistrationDto.getUsername()) != -1;
        if (isUsernameAlreadyInUse) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ExceptionMessages.USERNAME_ALREADY_IN_USE_MESSAGE);
        }

        User newUser;
        try {
            newUser = userDao.createUser(
                    mapRegistrationInfoToUser(userRegistrationDto),
                    new BCryptPasswordEncoder().encode(userRegistrationDto.getPassword())
            );
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        if (newUser == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ExceptionMessages.USER_REGISTRATION_FAILED_MESSAGE);
        }

        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername(newUser.getUsername());
        userLoginDto.setPassword(userRegistrationDto.getPassword());

        return handleLogin(userLoginDto);
    }

    private LoginResponse handleLogin(UserLoginDto userLoginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);

        User user = userDao.getUserByUsername(userLoginDto.getUsername());
        UserResponse userResponse = mapUserToResponse(user);

        return new LoginResponse(jwt, userResponse);
    }

    private UserResponse mapUserToResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setJoinedDate(user.getJoinedDate());
        return userResponse;
    }

    private User mapRegistrationInfoToUser(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setUsername(userRegistrationDto.getUsername());
        user.setEmail(userRegistrationDto.getEmail());
        return user;
    }
}
