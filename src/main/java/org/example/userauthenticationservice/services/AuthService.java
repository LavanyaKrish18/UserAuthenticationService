package org.example.userauthenticationservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthenticationservice.exceptions.InvalidCredentialsException;
import org.example.userauthenticationservice.exceptions.UserAlreadyExistsException;
import org.example.userauthenticationservice.models.Session;
import org.example.userauthenticationservice.models.State;
import org.example.userauthenticationservice.models.User;
import org.example.userauthenticationservice.repos.SessionRepo;
import org.example.userauthenticationservice.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpHeaders;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private SecretKey secretKey;

    @Override
    public User signup(String email, String password) throws UserAlreadyExistsException {
        Optional<User> userOptional = userRepo.findUserByEmail(email);
        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException("Email already Registered !!");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setState(State.ACTIVE);
        userRepo.save(user);
        return user;
    }

    @Override
    public Pair<User, MultiValueMap<String, String>> login(String email, String password) throws InvalidCredentialsException {
        Optional<User> userOptional = userRepo.findUserByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
                throw new InvalidCredentialsException("Please provide correct password..");
            }

//            String message = "{\n" +
//                "   \"email\": \"anurag@scaler.com\",\n" +
//                "   \"roles\": [\n" +
//                "      \"instructor\",\n" +
//                "      \"buddy\"\n" +
//                "   ],\n" +
//                "   \"expirationDate\": \"25thSept2024\"\n" +
//                "}";
//
//            byte[] content = message.getBytes(StandardCharsets.UTF_8);
            //String token = Jwts.builder().content(content).compact();

            Map<String, Object> claims = new HashMap<>();
            claims.put("user_id__", user.getId());
            claims.put("user_email", user.getEmail());
            claims.put("roles", user.getRoles());
            long timeInMillis = System.currentTimeMillis();
            claims.put("iat", timeInMillis);
            claims.put("exp", timeInMillis + 86400000);
            claims.put("iss", "LavanyaKrishnan");

//            MacAlgorithm algorithm = Jwts.SIG.HS256;
//            SecretKey secretKey = algorithm.key().build();

            //sign my token with key
            //String token = Jwts.builder().content(content).signWith(secretKey).compact();
            String token = Jwts.builder().claims(claims).signWith(secretKey).compact();

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add(HttpHeaders.SET_COOKIE, token);

            Session session = new Session();
            session.setToken(token);
            session.setUser(user);
            session.setState(State.ACTIVE);
            sessionRepo.save(session);

            Pair<User, MultiValueMap<String, String>> p = new Pair<>(user, headers);
            return p;
        }
        return null;
    }

    @Override
    public Boolean validateToken(String token, Long userId){
        //Authenticate
        Optional<Session> sessionOptional = sessionRepo.findByTokenAndUser_Id(token, userId);
        if (!sessionOptional.isPresent()) {
            return false;
        }

        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        Long expiry = (Long) claims.get("exp");
        Long currentTimeInMillis = System.currentTimeMillis();

        System.out.println(expiry);
        System.out.println(currentTimeInMillis);

        if (currentTimeInMillis > expiry) {
            System.out.println("TOKEN EXPIRED");
            //1. clear expired token from DB async with help of kAFKA
            //2. YOU can also trigger login API
            //3. Use same api in order service to validate user before getting order info.
            return false;
        }

        //Authorize
        //List<Role> roles = (Long)claims.get("roles");
        //if(roles.contains())

        return true;
    }

    @Override
    public User logout(String email) {
        return null;
    }
}
