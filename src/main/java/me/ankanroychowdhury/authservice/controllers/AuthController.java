package me.ankanroychowdhury.authservice.controllers;

import jakarta.servlet.http.HttpServletResponse;
import me.ankanroychowdhury.authservice.dtos.AuthRequestDto;
import me.ankanroychowdhury.authservice.dtos.AuthResponseDto;
import me.ankanroychowdhury.authservice.dtos.PassengerDto;
import me.ankanroychowdhury.authservice.dtos.PassengerSignupRequestDto;
import me.ankanroychowdhury.authservice.services.AuthService;
import me.ankanroychowdhury.authservice.services.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Value("${jwt.expiry}")
    private int cookieExpiry;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup/passengers")
    public ResponseEntity<PassengerDto> signUpPassenger(@RequestBody PassengerSignupRequestDto passengerSignupRequestDto){
        PassengerDto response = authService.signUpPassenger(passengerSignupRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/signin/passengers")
    public ResponseEntity<?> signIn(@RequestBody AuthRequestDto authRequestDto, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getEmail(), authRequestDto.getPassword()));
        if(authentication.isAuthenticated()) {
            Map<String, Object> claimsPayload = new HashMap<>();
            claimsPayload.put("email", authRequestDto.getEmail());
            String jwtToken = jwtService.createToken(claimsPayload, authentication.getName());
            ResponseCookie cookie = ResponseCookie
                                    .from("jwtToken", jwtToken)
                                    .httpOnly(true)
                                    .secure(false)
                                    .maxAge(cookieExpiry)
                                    .build();
            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return new ResponseEntity<>(AuthResponseDto.builder().success(true).build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("no success", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
