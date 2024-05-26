package me.ankanroychowdhury.authservice.repositories;

import me.ankanroychowdhury.entityservice.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    Optional<Passenger> findPassengerByEmail(String email);

}
