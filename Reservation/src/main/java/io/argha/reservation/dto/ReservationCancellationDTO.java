package io.argha.reservation.dto;

public class ReservationCancellationDTO {
	private Integer pnr;
	private String custId;
	private Integer numberOfSeats;

	public ReservationCancellationDTO() {
	}

	public ReservationCancellationDTO(Integer pnr, String custId, Integer numberOfSeats) {
		this.pnr = pnr;
		this.custId = custId;
		this.numberOfSeats = numberOfSeats;
	}

	public Integer getPnr() {
		return pnr;
	}

	public void setPnr(Integer pnr) {
		this.pnr = pnr;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public Integer getNumberOfSeats() {
		return numberOfSeats;
	}

	public void setNumberOfSeats(Integer numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
	}
}
