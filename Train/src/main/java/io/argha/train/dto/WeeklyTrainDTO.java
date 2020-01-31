package io.argha.train.dto;

import io.argha.train.entity.Train;

public class WeeklyTrainDTO {
	private String id;
	private String name;
	private String route;
	private String days;
	
	public WeeklyTrainDTO(Train train, String days) {
		this.id = train.getId();
		this.name = train.getName();
		this.route = train.getRoute();
		this.days = days;
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

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}
}
