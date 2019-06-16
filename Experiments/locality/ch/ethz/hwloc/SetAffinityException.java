package ch.ethz.hwloc;

public class SetAffinityException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public SetAffinityException() {
		super();
	}
	
	public SetAffinityException(String message) {
		super(message);
	}
}
