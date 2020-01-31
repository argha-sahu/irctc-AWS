package io.argha.reservation.exception;

public class TrainNotFoundException extends Exception {
	private static final long serialVersionUID = -3332292346834265371L;

	public TrainNotFoundException(String message) {
		super(message);
	}
}