package eit.linecode.exception;

/**
 * the exception is called in the event of an error at the Size of stream
 * delimiter
 *
 * @author Hamed Nakhei
 *
 */
public class DecodeException extends RuntimeException {

	/**
	 * is a standard constructor and is used for throwing this exception
	 * <li>if not divisible by 6</li>
	 * <li>wrong inputs</li>
	 * <li>if the input is empty</li>
	 * <li>if the entry does not contain anything</li>
	 *
	 * @param msg the message is issued when an exception is thrown
	 */
	public DecodeException(String msg) {
		super(msg);
	}

}
