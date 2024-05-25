package me.ankanroychowdhury.authservice.services;

import me.ankanroychowdhury.authservice.dtos.PassengerDto;
import me.ankanroychowdhury.authservice.dtos.PassengerSignupRequestDto;
import me.ankanroychowdhury.authservice.repositories.PassengerRepository;
import me.ankanroychowdhury.entityservice.entities.Passenger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PassengerRepository passengerRepository;
    private final BCryptPasswordEncoder bcryptPasswordEncoder;

    public AuthService(PassengerRepository passengerRepository, BCryptPasswordEncoder bcryptPasswordEncoder) {
        this.passengerRepository = passengerRepository;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
    }

    public PassengerDto signUpPassenger(PassengerSignupRequestDto passengerSignupRequestDto) {
        Passenger passenger = Passenger.builder()
                .email(passengerSignupRequestDto.getEmail())
                .name(passengerSignupRequestDto.getName())
                .password(this.bcryptPasswordEncoder.encode(passengerSignupRequestDto.getPassword()))
                .phoneNumber(passengerSignupRequestDto.getPhoneNumber())
                .build();

        Passenger newPassenger = this.passengerRepository.save(passenger);
        return PassengerDto.from(newPassenger);
    }
}
