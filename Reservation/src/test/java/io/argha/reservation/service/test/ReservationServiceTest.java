package io.argha.reservation.service.test;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import io.argha.reservation.service.ReservationService;
import io.argha.reservation.service.SeatMapService;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureMockRestServiceServer
@RestClientTest(ReservationService.class)
public class ReservationServiceTest {
	@Autowired
	RestTemplate restTemplate;

	@Mock
	private SeatMapService seatMapService;

	@Mock
	private ReservationRepository reservationRepository;

	@InjectMocks
	private ReservationService reservationService = null;

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private ObjectMapper objectMapper = new ObjectMapper();

	@Before
	public void setUp() throws Exception {
		restTemplate = new RestTemplate();
		server = MockRestServiceServer.createServer(restTemplate);
	}

	@Test
	public void doReservation_successful_case() throws JsonProcessingException, IllegalArgumentException,
			BookingNotOpenedException, TrainNotFoundException, SourceOrDestinationNotFoundException {
		
		String booleanString2 = objectMapper.writeValueAsString(new Boolean(true));
		String uri2 = "http://localhost:8082/train-service/irctc/train/12348";
		this.server.expect(requestTo(uri2)).andRespond(withSuccess(booleanString2, MediaType.APPLICATION_JSON));

		String booleanString1 = objectMapper.writeValueAsString(new Boolean(true));
		String uri1 = "http://localhost:8082/lookup-service/lookup/verify?trainId=12348&from=Hyderabad&to=Roorkee&day=Th";
		this.server.expect(requestTo(uri1)).andRespond(withSuccess(booleanString1, MediaType.APPLICATION_JSON));

		Reservation reservation = new Reservation();
		when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

		SeatMap seatMap = new SeatMap();
		seatMap.setTrainId("12348");
		seatMap.setMySeatMap("20,21,22,23,24,25");
		seatMap.setDate("08-08-2019");
		when(seatMapService.getSeatMap("12348", "08-08-2019")).thenReturn(seatMap);
		doNothing().when(seatMapService).update(any(SeatMap.class));

		ReservationMakingDTO rmdto = new ReservationMakingDTO();
		rmdto.setTrainId("12348");
		rmdto.setFrom("Hyderabad");
		rmdto.setTo("Roorkee");
		rmdto.setDate("08-08-2019");
		rmdto.setCustId("cs6478");
		rmdto.setNumberOfSeats(3);

		ReservationDTO rdto = reservationService.doReservation(rmdto, restTemplate);

		assertThat(rdto.getPnr()).isGreaterThanOrEqualTo(111111111);
		assertThat(rdto.getStatus()).isEqualTo("Tickets booked successfully");
	}

	@Test(expected = IllegalArgumentException.class)
	public void doReservation_booking_more_number_of_seats_than_available_case()
			throws JsonProcessingException, IllegalArgumentException, BookingNotOpenedException, TrainNotFoundException,
			SourceOrDestinationNotFoundException {
		
		String booleanString2 = objectMapper.writeValueAsString(new Boolean(true));
		String uri2 = "http://localhost:8082/train-service/irctc/train/12348";
		this.server.expect(requestTo(uri2)).andRespond(withSuccess(booleanString2, MediaType.APPLICATION_JSON));

		String booleanString1 = objectMapper.writeValueAsString(new Boolean(true));
		String uri1 = "http://localhost:8082/lookup-service/lookup/verify?trainId=12348&from=Hyderabad&to=Roorkee&day=Th";
		this.server.expect(requestTo(uri1)).andRespond(withSuccess(booleanString1, MediaType.APPLICATION_JSON));

		SeatMap seatMap = new SeatMap();
		seatMap.setTrainId("12348");
		seatMap.setMySeatMap("20,21");
		seatMap.setDate("08-08-2019");
		when(seatMapService.getSeatMap("12348", "08-08-2019")).thenReturn(seatMap);

		ReservationMakingDTO rmdto = new ReservationMakingDTO();
		rmdto.setTrainId("12348");
		rmdto.setFrom("Hyderabad");
		rmdto.setTo("Roorkee");
		rmdto.setDate("08-08-2019");
		rmdto.setCustId("cs6478");
		rmdto.setNumberOfSeats(3);

		reservationService.doReservation(rmdto, restTemplate);
	}

	@Test(expected = IllegalArgumentException.class)
	public void doReservation_train_or_route_invalid_case() throws JsonProcessingException, IllegalArgumentException,
			BookingNotOpenedException, TrainNotFoundException, SourceOrDestinationNotFoundException {

		String booleanString2 = objectMapper.writeValueAsString(new Boolean(true));
		String uri2 = "http://localhost:8082/train-service/irctc/train/12348";
		this.server.expect(requestTo(uri2)).andRespond(withSuccess(booleanString2, MediaType.APPLICATION_JSON));

		String booleanString1 = objectMapper.writeValueAsString(new Boolean(false));
		String uri1 = "http://localhost:8082/lookup-service/lookup/verify?trainId=12348&from=Hyderabad&to=Roorkee&day=Th";
		this.server.expect(requestTo(uri1)).andRespond(withSuccess(booleanString1, MediaType.APPLICATION_JSON));
		
		ReservationMakingDTO rmdto = new ReservationMakingDTO();
		rmdto.setTrainId("12348");
		rmdto.setFrom("Hyderabad");
		rmdto.setTo("Roorkee");
		rmdto.setDate("08-08-2019");
		rmdto.setCustId("cs6478");
		rmdto.setNumberOfSeats(3);

		reservationService.doReservation(rmdto, restTemplate);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cancelReservation_invalid_cancelled_seats_case() throws IllegalArgumentException, PnrNotFoundException {
		ReservationCancellationDTO reservationCancellationDTO = new ReservationCancellationDTO(123456789, "cs1234", 0);
		reservationService.cancelReservation(reservationCancellationDTO);
	}

	@Test(expected = PnrNotFoundException.class)
	public void cancelReservation_pnr_does_not_exist_case() throws IllegalArgumentException, PnrNotFoundException {
		when(reservationRepository.existsByPnr(any(Integer.class))).thenReturn(false);
		ReservationCancellationDTO reservationCancellationDTO = new ReservationCancellationDTO(123456789, "cs1234", 2);
		reservationService.cancelReservation(reservationCancellationDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cancelReservation_cancel_more_seats_than_reserved_case()
			throws IllegalArgumentException, PnrNotFoundException {
		when(reservationRepository.existsByPnr(123456789)).thenReturn(true);

		Reservation reservation = new Reservation();
		reservation.setPnr(123456789);
		reservation.setSeats("20,21,22");
		when(reservationRepository.findByPnr(123456789)).thenReturn(Optional.of(reservation));

		ReservationCancellationDTO reservationCancellationDTO = new ReservationCancellationDTO(123456789, "cs1234", 4);
		reservationService.cancelReservation(reservationCancellationDTO);
	}

	@Test
	public void cancelReservation_successful_case() throws IllegalArgumentException, PnrNotFoundException {
		when(reservationRepository.existsByPnr(123456789)).thenReturn(true);

		Reservation reservation = new Reservation();
		reservation.setPnr(123456789);
		reservation.setSeats("20,21,22,23");
		reservation.setTrainId("575883");
		reservation.setDate("08-08-2019");
		when(reservationRepository.findByPnr(123456789)).thenReturn(Optional.of(reservation));
		when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

		SeatMap seatMap = new SeatMap();
		seatMap.setDate("08-08-2019");
		seatMap.setMySeatMap("10,11,12");
		seatMap.setTrainId("575883");
		when(seatMapService.getSeatMap("575883", "08-08-2019")).thenReturn(seatMap);

		ReservationCancellationDTO reservationCancellationDTO = new ReservationCancellationDTO(123456789, "cs1234", 2);
		ReservationDTO reservationDTO = reservationService.cancelReservation(reservationCancellationDTO);

		assertThat(reservationDTO.getPnr()).isEqualTo(123456789);
		assertThat(reservationDTO.getStatus()).isEqualTo("Success!! Reservation successfully cancelled");
	}
}
