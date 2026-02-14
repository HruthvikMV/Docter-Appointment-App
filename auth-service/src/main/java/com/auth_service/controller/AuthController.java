package com.auth_service.controller;

import com.auth_service.dto.APIResponse;
import com.auth_service.dto.LoginDto;
import com.auth_service.dto.UserDto;
import com.auth_service.entity.User;
import com.auth_service.repository.UserRepository;
import com.auth_service.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<APIResponse<UserDto>> register(@RequestBody UserDto userDto) {
        APIResponse<UserDto> response = new APIResponse<>();

        if (userRepository.existsByUsername(userDto.getUsername())) {
            response.setMessage("Username already exists");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            response.setMessage("Email already exists");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Default role if not provided
        String role = userDto.getRole() != null ? userDto.getRole() : "ROLE_USER";
        user.setRole(role);

        User saved = userRepository.save(user);

        UserDto result = new UserDto();
        result.setId(saved.getId());
        result.setName(saved.getName());
        result.setUsername(saved.getUsername());
        result.setEmail(saved.getEmail());
        result.setRole(saved.getRole());

        response.setMessage("User registered successfully");
        response.setStatus(HttpStatus.CREATED.value());
        response.setData(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<String>> login(@RequestBody LoginDto loginDto) {
        APIResponse<String> response = new APIResponse<>();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );

        if (!authentication.isAuthenticated()) {
            response.setMessage("Invalid credentials");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User user = userRepository.findByUsername(loginDto.getUsername());
        String token = jwtService.generateToken(user.getUsername(), user.getRole());

        response.setMessage("Login successful");
        response.setStatus(HttpStatus.OK.value());
        response.setData(token);

        return ResponseEntity.ok(response);
    }
}


