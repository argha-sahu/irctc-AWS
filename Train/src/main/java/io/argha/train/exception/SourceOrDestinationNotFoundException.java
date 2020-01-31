package io.argha.train.exception;

public class SourceOrDestinationNotFoundException extends Exception {
	private static final long serialVersionUID = -3332292346834265371L;

	public SourceOrDestinationNotFoundException(String source, String destination) {
		super("Source : " + source + " or destination " + destination + " doesn't exist");
	}
}
