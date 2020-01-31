package io.argha.reservation.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import io.argha.reservation.dto.ReservationCancellationDTO;
import io.argha.reservation.dto.ReservationDTO;
import io.argha.reservation.dto.ReservationMakingDTO;
import io.argha.reservation.entity.Reservation;
import io.argha.reservation.entity.SeatMap;
import io.argha.reservation.exception.BookingNotOpenedException;
import io.argha.reservation.exception.PnrNotFoundException;
import io.argha.reservation.exception.SourceOrDestinationNotFoundException;
import io.argha.reservation.exception.TrainNotFoundException;
import io.argha.reservation.repository.ReservationRepository;

@Service
public class ReservationService {

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private SeatMapService seatMapService;

	public boolean checkPnrAvailability(Integer pnr) {
		return reservationRepository.existsByPnr(pnr);
	}

	public void addReservation(Reservation res) {
		reservationRepository.save(res);
	}

	public Reservation getReservationStatus(Integer pnr) throws PnrNotFoundException {
		if (checkPnrAvailability(pnr))
			return reservationRepository.findByPnr(pnr).get();
		else
			throw new PnrNotFoundException(pnr);
	}

	public static String convertDateToDay(String customDate) {
		Date date = null;
		try {
			date = new SimpleDateFormat("dd-MM-yyyy").parse(customDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("E"); // the day of the week abbreviated
		return simpleDateformat.format(date);
	}

	public ReservationDTO doReservation(ReservationMakingDTO temp, RestTemplate restTemplate)
			throws IllegalArgumentException, BookingNotOpenedException, TrainNotFoundException,
			SourceOrDestinationNotFoundException {
		String trainId = temp.getTrainId();
		String from = temp.getFrom();
		String to = temp.getTo();
		String date = temp.getDate();
		Integer numberOfSeats = temp.getNumberOfSeats();
		String custId = temp.getCustId();
		String day = convertDateToDay(date).substring(0, 2);

		String uri = "http://localhost:8082/train-service/irctc/train/" + trainId;
		Boolean isTrainValid = restTemplate.getForObject(uri, Boolean.class);
		if (!isTrainValid) {
			throw new TrainNotFoundException("Train with ID = " + trainId + " doesn't exist");
		}
		uri = "http://localhost:8082/lookup-service/lookup/verify?trainId=" + trainId + "&from=" + from + "&to=" + to
				+ "&day=" + day;
		Boolean isValid = false;
		try {
			isValid = restTemplate.getForObject(uri, Boolean.class);
		} catch (HttpClientErrorException.NotAcceptable exception) {
			throw new SourceOrDestinationNotFoundException(from, to);
		}
		if (!isValid) {
			throw new IllegalArgumentException("ERRORR!! Requested train is not available on " + date + "/ Route is not valid for the train");
		}
		SeatMap mySeatMap = seatMapService.getSeatMap(trainId, date);
		if (mySeatMap == null)
			throw new BookingNotOpenedException(trainId, date);
		String seats[] = mySeatMap.getMySeatMap().split(",");
		int seatsAvailable = seats.length;

		if (seatsAvailable < numberOfSeats || seats[0].equals("")) {
			throw new IllegalArgumentException("ERRORR!! Requested number of seats are not available");
		}
		// Create Reservation
		String reservedSeats = seats[0];
		for (int i = 1; i < numberOfSeats; ++i)
			reservedSeats += "," + seats[i];
		Random random = new Random();
		int pnr = 0;
		while (true) {
			pnr = random.nextInt(888888888) + 111111111;
			if (!checkPnrAvailability(pnr))
				break;
		}
		Reservation res = new Reservation(pnr, trainId, custId, from, to, date, reservedSeats);
		addReservation(res);

		// Update SeatMap
		String leftSeats = "";
		if (seatsAvailable > numberOfSeats) {
			leftSeats = seats[numberOfSeats];
			for (int i = numberOfSeats + 1; i < seats.length; ++i)
				leftSeats += "," + seats[i];
		}
		mySeatMap.setMySeatMap(leftSeats);
		seatMapService.update(mySeatMap);
		return new ReservationDTO(pnr, "Tickets booked successfully");
	}

	@Transactional
	public ReservationDTO cancelReservation(ReservationCancellationDTO cancel)
			throws IllegalArgumentException, PnrNotFoundException {
		int pnr = cancel.getPnr();
		int numberOfSeats = cancel.getNumberOfSeats();
		if (numberOfSeats <= 0) {
			throw new IllegalArgumentException("ERROR!! Number of seats to be cancelled must be geater or equals to 1");
		}
		if (!reservationRepository.existsByPnr(pnr)) {
			throw new PnrNotFoundException(pnr);
		}
		Reservation res = reservationRepository.findByPnr(pnr).get();
		String reservedSeats[] = res.getSeats().split(",");
		if (numberOfSeats > reservedSeats.length) {
			throw new IllegalArgumentException("ERROR!! Too many seats to be cancelled");
		}
		String seatsReturned = reservedSeats[0];
		for (int i = 1; i < numberOfSeats; ++i)
			seatsReturned += "," + reservedSeats[i];
		if (numberOfSeats == reservedSeats.length) {
			reservationRepository.deleteByPnr(pnr);
		} else {
			String temp = reservedSeats[numberOfSeats];
			for (int i = numberOfSeats + 1; i < reservedSeats.length; ++i)
				temp += "," + reservedSeats[i];
			res.setSeats(temp);
			addReservation(res);
		}
		SeatMap seatMap = seatMapService.getSeatMap(res.getTrainId(), res.getDate());
		if (seatMap.getMySeatMap().equals(""))
			seatMap.setMySeatMap(seatsReturned);
		else
			seatMap.setMySeatMap(seatMap.getMySeatMap() + "," + seatsReturned);
		seatMapService.addSeatMap(seatMap);
		return new ReservationDTO(pnr, "Success!! Reservation successfully cancelled");
	}
}
