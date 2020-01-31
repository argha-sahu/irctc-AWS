package io.argha.reservation.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Reservation {
	@Id
	private Integer pnr;
	private String trainId;
	private String custId;
	private String source;
	private String destination;
	private String date;
	private String seats;

	public Reservation() {
	}

	public Reservation(Integer pnr, String trainId, String custId, String source, String destination, String date,
			String seats) {
		this.pnr = pnr;
		this.trainId = trainId;
		this.custId = custId;
		this.source = source;
		this.destination = destination;
		this.date = date;
		this.seats = seats;
	}

	public Integer getPnr() {
		return pnr;
	}

	public void setPnr(Integer pnr) {
		this.pnr = pnr;
	}

	public String getTrainId() {
		return trainId;
	}

	public void setTrainId(String trainId) {
		this.trainId = trainId;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSeats() {
		return seats;
	}

	public void setSeats(String seats) {
		this.seats = seats;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
}