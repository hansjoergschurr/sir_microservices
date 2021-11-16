package com.example.AuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
public class APIController {
    private final Map<Long, NewUser> users = new HashMap<Long, NewUser>();
    private final Map<String, Token> tokens = new HashMap<String, Token>();
    private final Map<Long, Set<String>> user_to_token = new HashMap<Long, Set<String>>();

    private final Logger logger = LoggerFactory.getLogger(APIController.class);

    @PutMapping(path = "/AS/users")
    public long create_user(@RequestBody @Valid NewUser newUser) {
        if (users.containsKey(newUser.getId()))
            throw new UserExistsException(newUser.getId());
        users.put(newUser.getId(), newUser);
        logger.info(String.format("User created: [%d].", newUser.getId()));
        return newUser.getId();
    }

    @GetMapping("/AS/users/{id}")
    public long user(@PathVariable(value = "id") Long id) {
        if (!users.containsKey(id))
            throw new UserNotFoundException(id);
        return id;
    }

    private void removeToken(Token token) {
        String tokenString = token.getToken();
        long userId = token.getUserId();
        this.tokens.remove(tokenString);
        this.user_to_token.get(userId).remove(tokenString);
    }

    @DeleteMapping("/AS/users/{id}")
    public void delete_user(@PathVariable(value = "id") Long id,
                            @RequestHeader(value = "X-Token") String token) {
        if (!tokens.containsKey(token))
            throw new TokenNotValidException(token);
        Token t = tokens.get(token);
        if (!t.isValid()) {
            this.removeToken(t);
            throw new TokenNotValidException(token);
        }
        if (t.getUserId() != id)
            throw new TokenNotValidException(token);
        if (!users.containsKey(id))
            throw new UserNotFoundException(id);
        users.remove(id);
        Set<String> userTokens = user_to_token.get(id);
        for (String tkn : userTokens)
            this.tokens.remove(tkn);
    }

    @PostMapping("/AS/users/{id}/password")
    public long change_password(@PathVariable(value = "id") Long id, @RequestHeader(value = "X-Token") String token, @RequestBody String newPassword) {
        if (!tokens.containsKey(token))
            throw new TokenNotValidException(token);
        Token t = tokens.get(token);
        if (t.getUserId() != id)
            throw new TokenNotValidException(token);
        if (!t.isValid()) {
            this.removeToken(t);
            throw new TokenNotValidException(token);
        }
        if (!users.containsKey(id))
            throw new UserNotFoundException(id);
        NewUser user = users.get(id);
        user.setPassword(newPassword);
        return id;
    }

    @PostMapping("/AS/users/{id}/token")
    public String login(@PathVariable(value = "id") Long id, @RequestBody String password) {
        if (!users.containsKey(id))
            throw new UserNotFoundException(id);
        if (!users.get(id).checkPassword(password))
            throw new PasswordIncorrectException(id);

        Token token = new Token(id);
        this.tokens.put(token.getToken(), token);

        if(!this.user_to_token.containsKey(id))
            this.user_to_token.put(id, new HashSet());
        this.user_to_token.get(id).add(token.getToken());
        return token.getToken();
    }

    @DeleteMapping("/AS/users/{id}/token")
    public long logout(@PathVariable(value = "id") Long id,
                       @RequestHeader(value = "X-Token") String token) {
        if (!tokens.containsKey(token))
            throw new TokenNotValidException(token);
        Token t = tokens.get(token);
        if (t.getUserId() != id)
            throw new TokenNotValidException(token);
        if (!t.isValid()) {
            this.removeToken(t);
            throw new TokenNotValidException(token);
        }
        if (!users.containsKey(id))
            throw new UserNotFoundException(id);
        Set<String> userTokens = user_to_token.get(id);
        for (String tkn : userTokens) {
            Token ts = this.tokens.get(tkn);
            this.removeToken(ts);
        }
        return id;
    }

    @GetMapping("/AS/token")
    public long check_token(@RequestHeader(value = "X-Token") String token) {
        if (!tokens.containsKey(token))
            throw new TokenNotValidException(token);
        Token t = tokens.get(token);
        if (!t.isValid()) {
            this.removeToken(t);
            throw new TokenNotValidException(token);
        }
        return t.getUserId();
    }

}

