package eit.linecode.exception;

/**
 * the exception is called in the event of an error at the end of stream
 * delimiter
 *
 * @author Hamed Nakhei
 *
 */
public class EndOfPacketException extends RuntimeException {

	/**
	 * is a standard constructor and is used for throwing this exception
	 * <li>if wrong inverted</li>
	 * <li>if end of stream delimiter has not the right size</li>
	 *
	 * @param msg the message is issued when an exception is thrown
	 */
	public EndOfPacketException(String msg) {
		super(msg);
	}

}
