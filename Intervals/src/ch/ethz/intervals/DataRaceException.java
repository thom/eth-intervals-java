package ch.ethz.intervals;

/** Indicates that a data race was detected on a {@link DynamicGuard} */
public class DataRaceException extends RuntimeException {
	private static final long serialVersionUID = -4846309821441326261L;

	enum Role { READ, WRITE, LOCK };
	
	public final Lock lock;
	public final Role beforeRole;
	public final Point before;
	public final Role afterRole;
	public final Point after;
	
	public DataRaceException(
			Lock lock,
			Role beforeRole,
			Point before,
			Role afterRole,
			Point after)
	{
		this.lock = lock;
		this.beforeRole = beforeRole;
		this.before = before;
		this.afterRole = afterRole;
		this.after = after;
	}
	
}
