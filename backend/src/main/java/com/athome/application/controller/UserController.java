package com.athome.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.athome.application.dto.UserDTO;
import com.athome.application.model.User;
import com.athome.application.repo.UserRepository;
import com.athome.application.services.JwtService;
import com.athome.application.services.UserService;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public UserController(UserRepository userRepo, AuthenticationManager authenticationManager, JwtService jwtService,
            PasswordEncoder passwordEncoder, UserService userService) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    //get user info from jwt token
    @GetMapping("/user")
    public ResponseEntity<User> getUser(Authentication authentication) {
        User user = userService.getAuthenticatedUser(authentication);
        if(user == null) {
            return ResponseEntity.status(201).build();
        }
        user.setPassword(null); // bad to send user password so just null it
        return ResponseEntity.ok(user);
        
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        System.out.println(hashedPassword);

        user.setPassword(hashedPassword);

        return userRepo.save(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDTO.getUsername(),
                        userDTO.getPassword()));

        System.out.println(userDTO.getUsername() + " " + userDTO.getPassword());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(Map.of("token", token));
    }

}
