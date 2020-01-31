package io.argha.train.dto;

import io.argha.train.entity.Train;

public class TrainDTO {
	private String id;
	private String name;
	private String route;
	private String date;
	private String seatsLeft;
	public TrainDTO(Train train, String date, String seatsLeft)
	{
		this.id = train.getId();
		this.name = train.getName();
		this.route = train.getRoute();
		this.date = date;
		this.seatsLeft = seatsLeft;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSeatsLeft() {
		return seatsLeft;
	}
	public void setSeatsLeft(String seatsLeft) {
		this.seatsLeft = seatsLeft;
	}
}