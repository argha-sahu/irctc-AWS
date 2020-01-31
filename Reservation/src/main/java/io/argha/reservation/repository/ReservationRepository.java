package io.argha.reservation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.argha.reservation.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
	boolean existsByPnr(Integer pnr);

	Optional<Reservation> findByPnr(Integer pnr);

	void deleteByPnr(int pnr);
}
