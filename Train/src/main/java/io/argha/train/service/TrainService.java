package io.argha.train.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.argha.train.dto.TrainDTO;
import io.argha.train.dto.WeeklyTrainDTO;
import io.argha.train.entity.Train;
import io.argha.train.exception.SourceOrDestinationNotFoundException;
import io.argha.train.repository.TrainRepository;

@Service
public class TrainService {
	@Autowired
	TrainRepository trainRepository;

	public void addTrain(Train train) {
		trainRepository.save(train);
	}

	public List<Train> getTrainDetails(Set<Integer> tIds) {
		ArrayList<Train> trains = new ArrayList<Train>();
		for (Integer tid : tIds)
			trains.add(trainRepository.findById(tid.toString()).get());
		return trains;
	}
	
	public boolean verifyExistenceOfTrain(@PathVariable Integer trainId) {
		return trainRepository.existsById(trainId.toString());
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

	public List<WeeklyTrainDTO> getTrainsMatchingRoute(String from, String to)
			throws JsonParseException, JsonMappingException, IOException, SourceOrDestinationNotFoundException {
		if (from == null || to == null || from.equals("") || to.equals(""))
			throw new IllegalArgumentException("Invalid Source or Destination");

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = null;

		try {
			response = restTemplate.getForEntity(
					"http://localhost:8082/lookup-service/lookup?from=" + from + "&to=" + to, String.class);
		} catch (HttpClientErrorException.NotAcceptable exception) {
			throw new SourceOrDestinationNotFoundException(from, to);
		}
		String trainsMatchingRouteString = response.getBody();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> trainsMatchingRoute = mapper.readValue(trainsMatchingRouteString, Map.class);
		if (trainsMatchingRoute.isEmpty())
			return new ArrayList<WeeklyTrainDTO>();
		HashSet<Integer> trainIds = new HashSet<Integer>();
		for (String tId : trainsMatchingRoute.keySet())
			trainIds.add(Integer.parseInt(tId));
		List<Train> trainDetails = getTrainDetails(trainIds);
		ArrayList<WeeklyTrainDTO> result = new ArrayList<WeeklyTrainDTO>();
		for (Train train : trainDetails) {
			result.add(new WeeklyTrainDTO(train, trainsMatchingRoute.get(train.getId())));
		}
		return result;
	}

	public List<TrainDTO> getTrainsMatchingRouteAndDate(String from, String to, String date)
			throws SourceOrDestinationNotFoundException, JsonParseException, JsonMappingException, IOException {
		if (from == null || to == null || from.equals("") || to.equals(""))
			throw new IllegalArgumentException("Invalid Source or Destination");
		String day = convertDateToDay(date).substring(0, 2);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.getForEntity(
					"http://localhost:8082/lookup-service/lookup/" + day + "?from=" + from + "&to=" + to, String.class);
		} catch (HttpClientErrorException.NotAcceptable exception) {
			throw new SourceOrDestinationNotFoundException(from, to);
		}
		ObjectMapper mapper = new ObjectMapper();
		Set<Integer> trainsMatchingRoute = mapper.readValue(response.getBody(), Set.class);
		List<Train> trainDetails = getTrainDetails(trainsMatchingRoute);
		ArrayList<TrainDTO> result = new ArrayList<TrainDTO>();
		for (Train train : trainDetails) {
			Integer seatsLeft = restTemplate.getForObject(
					"http://localhost:8082/reservation-service/irctc/seats?trainId=" + train.getId() + "&date=" + date,
					Integer.class);
			String seatsLeftString = (seatsLeft == -1) ? "Booking has not been opened yet" : seatsLeft.toString();
			result.add(new TrainDTO(train, date, seatsLeftString));
		}
		return result;
	}
}
