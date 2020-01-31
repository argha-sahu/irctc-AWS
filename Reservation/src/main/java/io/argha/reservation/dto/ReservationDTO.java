package io.argha.reservation.dto;

public class ReservationDTO {
	private Integer pnr;
	private String status;
	public ReservationDTO() {}
	public ReservationDTO(Integer pnr, String status)
	{
		this.pnr = pnr;
		this.status = status;
	}
	public Integer getPnr() {
		return pnr;
	}
	public void setPnr(Integer pnr) {
		this.pnr = pnr;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
