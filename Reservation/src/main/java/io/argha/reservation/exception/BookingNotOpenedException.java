package io.argha.reservation.exception;

public class BookingNotOpenedException extends Exception{
private static final long serialVersionUID = -3332292346834265371L;
	
	public BookingNotOpenedException(String trainId, String date) {
		super("Reservation of train " + trainId + " on " + date + " has not been started yet");
	}
}
