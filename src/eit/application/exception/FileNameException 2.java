package eit.application.exception;

/**
 * the exception is called in the event of an error at the Size of stream
 * delimiter
 *
 * @author Hamed Nakhei
 *
 */
public class FileNameException extends RuntimeException {

	/**
	 * is a standard constructor and is used for throwing this exception<br>
	 * if length of file name bigger then 256 byte.<br>
	 * if length of file name less then 5 byte
	 * @param msg the message is issued when an exception is thrown
	 */
	public FileNameException(String msg) {
		super(msg);
	}

}
