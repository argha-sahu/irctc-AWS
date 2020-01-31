package io.argha.train.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;

import io.argha.train.dto.ExceptionJSONInfo;
import io.argha.train.dto.TrainDTO;
import io.argha.train.dto.WeeklyTrainDTO;
import io.argha.train.entity.Train;
import io.argha.train.exception.SourceOrDestinationNotFoundException;
import io.argha.train.service.TrainService;
import io.swagger.annotations.ApiOperation;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class TrainController {
	@Autowired
	private TrainService trainService;

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

	@ApiOperation(value = "This method adds new train objects from JSON file")
	@RequestMapping(method = RequestMethod.POST, value = "/irctc/train/insert/{file}")
	public void addTrainFromFile(@PathVariable String file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File("/home/argha/data/" + file)));
			String str;
			Gson gson = new Gson();
			while ((str = br.readLine()) != null) {
				Train train = gson.fromJson(str, Train.class);
				trainService.addTrain(train);
				System.out.println(train);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@ApiOperation(value = "This method adds a new train object")
	@RequestMapping(method = RequestMethod.POST, value = "/irctc/train")
	public void addTrain(@RequestBody Train train) {
		trainService.addTrain(train);
	}
	
	@ApiOperation(value = "Verify, whether train exists or not")
	@RequestMapping(method = RequestMethod.GET, value = "/irctc/train/{trainId}")
	public boolean verifyExistenceOfTrain(@PathVariable Integer trainId) {
		return trainService.verifyExistenceOfTrain(trainId);
	}

	@ApiOperation(value = "Given source, destination and date, this method finds all the train details satisfying all the constraints")
	@RequestMapping(method = RequestMethod.GET, value = "/irctc/train/lookup")
	public List<WeeklyTrainDTO> getTrainsMatchingRoute(@RequestParam Map<String, String> queryMap)
			throws JsonParseException, JsonMappingException, IOException, SourceOrDestinationNotFoundException {

		String from = queryMap.get("from"), to = queryMap.get("to");
		return trainService.getTrainsMatchingRoute(from, to);
	}

	@ApiOperation(value = "Given source and destination, this method finds all the train details satisfying all the constraints")
	@RequestMapping(method = RequestMethod.GET, value = "/irctc/train/lookup/{date}")
	public List<TrainDTO> getTrainsMatchingRouteAndDate(@RequestParam Map<String, String> queryMap,
			@PathVariable String date) throws JsonParseException, JsonMappingException, SourceOrDestinationNotFoundException, IOException {
		String from = queryMap.get("from"), to = queryMap.get("to");
		return trainService.getTrainsMatchingRouteAndDate(from, to, date);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ExceptionJSONInfo> handleIllegalArgumentException(HttpServletRequest request, Exception ex) {
		ExceptionJSONInfo response = new ExceptionJSONInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setMessage(ex.getMessage());

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(SourceOrDestinationNotFoundException.class)
	public ResponseEntity<ExceptionJSONInfo> handleSourceOrDestinationNotFoundException(HttpServletRequest request, Exception ex) {
		ExceptionJSONInfo response = new ExceptionJSONInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setMessage(ex.getMessage());

		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
}
