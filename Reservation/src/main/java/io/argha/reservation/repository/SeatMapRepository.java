package io.argha.reservation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.argha.reservation.entity.SeatMap;

public interface SeatMapRepository extends JpaRepository<SeatMap, Integer>{
	Optional<SeatMap> findByTrainIdAndDate(String trainId, String date);
}
