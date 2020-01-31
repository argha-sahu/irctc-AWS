package io.argha.reservation.exception;

public class PnrNotFoundException extends Exception{
	private static final long serialVersionUID = -3332292346834265371L;
	
	public PnrNotFoundException(Integer pnr) {
		super("PNR : " + pnr + " doesn't exist!");
	}
}
