package eit.application.exception;

/**
 * the exception is called in the event of an error at the Size of stream
 * delimiter
 *
 * @author Hamed Nakhei
 *
 */
public class FileLengthException extends RuntimeException {

	/**
	 * is a standard constructor and is used for throwing this exception
	 * the file size must not be larger than 2 ^ 24-1
	 * @param msg the message is issued when an exception is thrown
	 */
	public FileLengthException(String msg) {
		super(msg);
	}

}
