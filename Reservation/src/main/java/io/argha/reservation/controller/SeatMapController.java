package io.argha.reservation.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import io.argha.reservation.entity.SeatMap;
import io.argha.reservation.service.SeatMapService;
import io.swagger.annotations.ApiOperation;

@RestController
public class SeatMapController {
	@Autowired
	private SeatMapService seatMapService;

	@RequestMapping(method = RequestMethod.POST, value = "/irctc/stations/insert/{file}")
	public void addTrainFromFile(@PathVariable String file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File("/home/argha/data/" + file)));
			String str;
			Gson gson = new Gson();
			while ((str = br.readLine()) != null) {
				SeatMap seatMap = gson.fromJson(str, SeatMap.class);
				seatMapService.addSeatMap(seatMap);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@ApiOperation(value = "This method queries the number of seats left of a given train and on a given date")
	@RequestMapping("/irctc/seats")
	public int seatsLeft(@RequestParam Map<String, String> queryMap) {
		String trainId = queryMap.get("trainId");
		String date = queryMap.get("date");
		return seatMapService.seatsLeft(trainId, date);
	}

	@ApiOperation(value = "This method is used to add a new SeatMap object")
	@RequestMapping(method = RequestMethod.POST, value = "/irctc/seats")
	public void addSeatMap(@RequestBody SeatMap seatMap) {
		seatMapService.addSeatMap(seatMap);
	}
}
