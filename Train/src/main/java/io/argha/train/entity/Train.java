package io.argha.train.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Train {
	@Id
	private String id;
	private String name;
	private String route;

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
}
