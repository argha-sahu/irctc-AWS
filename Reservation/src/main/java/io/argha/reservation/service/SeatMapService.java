package io.argha.reservation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.argha.reservation.entity.SeatMap;
import io.argha.reservation.repository.SeatMapRepository;

@Service
public class SeatMapService {
	@Autowired
	private SeatMapRepository seatMapRepository;

	public int seatsLeft(String trainId, String date) {
		Optional<SeatMap> optionalSeatMap = seatMapRepository.findByTrainIdAndDate(trainId, date);
		if (!optionalSeatMap.isPresent())
			return -1;
		String seats = optionalSeatMap.get().getMySeatMap();
		int res = (seats.equals("")) ? 0 : seats.split(",").length;
		return res;
	}

	public void addSeatMap(SeatMap seatMap) {
		seatMapRepository.save(seatMap);
	}

	public SeatMap getSeatMap(String trainId, String date) {
		Optional<SeatMap> optionalSeatMap = seatMapRepository.findByTrainIdAndDate(trainId, date);
		SeatMap seatMap = (optionalSeatMap.isPresent()) ? optionalSeatMap.get() : null;
		return seatMap;
	}

	public void update(SeatMap sm) {
		seatMapRepository.save(sm);
	}
}
