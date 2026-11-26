package com.athome.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.athome.application.dto.AddUserRequest;
import com.athome.application.model.Household;
import com.athome.application.model.User;
import com.athome.application.repo.HouseholdRepository;
import com.athome.application.repo.UserRepository;
import com.athome.application.services.UserService;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/households")
public class HouseholdController {

    private final HouseholdRepository householdRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public HouseholdController(HouseholdRepository householdRepository, UserRepository userRepository,
            UserService userService) {
        this.householdRepository = householdRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<Household> createHousehold(Authentication authentication, @RequestBody Household household) {

        User currentUser = userService.getAuthenticatedUser(authentication);

        householdRepository.save(household);

        currentUser.setHousehold(household);
        userRepository.save(currentUser);
        return ResponseEntity.ok(household);

    }

    @PostMapping("/invite")
    public ResponseEntity<Void> addUsersToHousehold(
            @RequestBody AddUserRequest request,
            Authentication auth) {

        User currentUser = userService.getAuthenticatedUser(auth);

        Household household = currentUser.getHousehold();

        if (household == null) {
            return ResponseEntity.notFound().build();
        }

        User newUser = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        newUser.setHousehold(household);

        // userRepository.save(newUser);

        return ResponseEntity.noContent().build();
    }

    // READ ONE
    @GetMapping("/myhome")
    public ResponseEntity<Household> getMyHousehold(Authentication auth) {

        User user = userService.getAuthenticatedUser(auth);

        Household household = user.getHousehold();

        if (household == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(household);
    }

    // UPDATE

    // DELETE

}