package me.ankanroychowdhury.authservice.repositories;

import me.ankanroychowdhury.entityservice.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

}
