package eit.linecode.exception;

/**
 * the exception is called in the event of an error at the start of stream
 * delimiter
 *
 * @author Hamed Nakhei
 *
 */
public class StartOfStreamException extends RuntimeException {

	/**
	 * is a standard constructor and is used for throwing this exception start of
	 * stream delimiter is always the same depending on the data stream one,two or
	 * three. An exception is thrown in the event of inconsistencies<br>
	 *
	 * @param msg the message is issued when an exception is thrown
	 */
	public StartOfStreamException(String msg) {
		super(msg);
	}

}
