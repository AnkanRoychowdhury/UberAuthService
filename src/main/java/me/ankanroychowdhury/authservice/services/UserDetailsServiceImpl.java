package me.ankanroychowdhury.authservice.services;

import me.ankanroychowdhury.authservice.helpers.AuthPassengerDetails;
import me.ankanroychowdhury.authservice.repositories.PassengerRepository;
import me.ankanroychowdhury.entityservice.entities.Passenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This class is responsible to load the user by email in the form of UserDetails object for auth
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PassengerRepository passengerRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Passenger> passenger = this.passengerRepository.findPassengerByEmail(username);
        if(passenger.isEmpty()) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new AuthPassengerDetails(passenger.get());
    }
}
