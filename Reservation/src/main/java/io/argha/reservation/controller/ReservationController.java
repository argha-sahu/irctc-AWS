package io.argha.reservation.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.argha.reservation.dto.ExceptionJSONInfo;
import io.argha.reservation.dto.ReservationCancellationDTO;
import io.argha.reservation.dto.ReservationDTO;
import io.argha.reservation.dto.ReservationMakingDTO;
import io.argha.reservation.entity.Reservation;
import io.argha.reservation.exception.BookingNotOpenedException;
import io.argha.reservation.exception.PnrNotFoundException;
import io.argha.reservation.exception.SourceOrDestinationNotFoundException;
import io.argha.reservation.exception.TrainNotFoundException;
import io.argha.reservation.service.ReservationService;
import io.swagger.annotations.ApiOperation;

@RestController
public class ReservationController {
	@Autowired
	private ReservationService reservationService;

	@ApiOperation(value = "This method does train reservation")
	@RequestMapping(method = RequestMethod.POST, value = "/irctc/reservation")
	public ResponseEntity<ReservationDTO> doReservation(@RequestBody ReservationMakingDTO temp)
			throws IllegalArgumentException, BookingNotOpenedException, TrainNotFoundException,
			SourceOrDestinationNotFoundException {
		ReservationDTO reservationDTO = reservationService.doReservation(temp, new RestTemplate());
		ResponseEntity<ReservationDTO> responseEntity = new ResponseEntity<>(reservationDTO, HttpStatus.CREATED);
		return responseEntity;
	}

	@ApiOperation(value = "This method cancels a reservation")
	@RequestMapping(method = RequestMethod.DELETE, value = "/irctc/reservation")
	public ResponseEntity<ReservationDTO> cancelReservation(@RequestBody ReservationCancellationDTO cancel)
			throws IllegalArgumentException, PnrNotFoundException {
		ReservationDTO reservationDTO = reservationService.cancelReservation(cancel);
		ResponseEntity<ReservationDTO> responseEntity = new ResponseEntity<>(reservationDTO, HttpStatus.OK);
		return responseEntity;
	}

	@ApiOperation(value = "This method checks the PNR status")
	@RequestMapping(method = RequestMethod.GET, value = "/irctc/reservation/{pnr}")
	public ResponseEntity<Reservation> getReservation(@PathVariable Integer pnr) throws PnrNotFoundException {
		Reservation reservation = reservationService.getReservationStatus(pnr);
		ResponseEntity<Reservation> responseEntity = new ResponseEntity<>(reservation, HttpStatus.OK);
		return responseEntity;
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ExceptionJSONInfo> handleIllegalArgumentException(HttpServletRequest request, Exception ex) {
		ExceptionJSONInfo response = new ExceptionJSONInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setMessage(ex.getMessage());

		return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler({ PnrNotFoundException.class, BookingNotOpenedException.class, TrainNotFoundException.class,
			SourceOrDestinationNotFoundException.class })
	public ResponseEntity<ExceptionJSONInfo> handlePnrNotFoundException(HttpServletRequest request, Exception ex) {
		ExceptionJSONInfo response = new ExceptionJSONInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setMessage(ex.getMessage());

		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
}
