package eit.linecode;

import java.io.File;
import eit.application.exception.FileLengthException;
import eit.application.exception.FileNameException;

/**
 * so that the control frame is set up correctly, this class is very useful<br>
 * this class builds a byte array from information that we get from the file.<br>
 * with this control frame all possible information for a transmission is communicated<br>
 * At the beginning of a transmission, the control frame is sent.<br>
 * then all data frames are communicated according to the segment numbers.<br>
 * The control frame contains the following information: <br>
 * <li>Length of the file name - 8 bits are available</li>
 * <li>Length of the file in bytes - 3 bytes are available</li>
 * <li>Name of the file - max. 256 bytes are available</li>
 * 
 * for more in-depth information on this topic, please visit this source:<br>
 * https://en.wikipedia.org/wiki/File_Transfer_Protocol
 * @author Hamed Nakhei
 */

public class ControlFrame {
	private static final int MAX_SIZE_TO_TRANSMIT=16777215;
	private static final int MAX_SIZE_OF_FILENAME=256;
	private static final int MIN_SIZE_OF_FILENAME=4;
	private static final int MIN_LENGTH_OF_BINARY=24;
	private static final int LENGTH_OF_ONE_BYTE_IN_BIT=8;
	//we have to convert file length in 3 bytes
	private static final int SIZE_OF_FILE_LENGTH=3;
	//Index 1-3 is for file length
	private static final int THIRD_INDEX_OF_RESULT=3;
	//from the 4th begin with data name in bytes
	private static final int FOURTH_INDEX_OF_RESULT=4;
	public byte fileNameLength;
	public byte[] fileLength = new byte[SIZE_OF_FILE_LENGTH];
	public byte[] filename;
	
	/** With this method, the control frame is compiled from file information
	 * @param file the file from which we want to create a control frame.
	 * @return a control frame in byte array.
	 */
	
	public byte[] getBytes(File file) {

		// <==control FILE NAME LENGTH ==>>
		if (file.getName().length() > MAX_SIZE_OF_FILENAME) {
			throw new FileNameException("File name is too big!!!");
		}
		if (file.getName().length() < MIN_SIZE_OF_FILENAME) {
			throw new FileNameException("File name is too short!!!");
		}
		// <==End of control FILE NAME LENGTH ==>>
		this.fileNameLength = (byte) file.getName().length();

		
		// <==control FILE LENGTH ==>>
		if (fileLength.length > MAX_SIZE_TO_TRANSMIT) {
			throw new FileLengthException("File is too big!!!");
		}
		// <==End of control FILE LENGTH ==>>
		
		int sizeOfFileInInteger = (int) file.length();

		// convert in binary
		String binary = "";
		while (sizeOfFileInInteger != 0) {
			binary = (sizeOfFileInInteger % 2) + binary;
			sizeOfFileInInteger = sizeOfFileInInteger / 2;
		}
		// bring it to 24 bit if is smaller
		if (binary.length() < MIN_LENGTH_OF_BINARY) {
			int lower = MIN_LENGTH_OF_BINARY - binary.length();
			for (int i = 0; i < lower; i++) {
				binary = "0" + binary;
			}
		}
		// Distribute binary representation in bytes pack in file lengh
		for (int i = 0; i < fileLength.length; i++) {
			String temp = binary.substring(0, LENGTH_OF_ONE_BYTE_IN_BIT);
			binary = binary.substring(LENGTH_OF_ONE_BYTE_IN_BIT);

			int s = binaryInDecimal(temp);
			fileLength[i] = (byte) s;
		}
		
		this.filename = file.getName().getBytes();

		byte[] result = new byte[1 + fileLength.length + this.filename.length];

		result[0] = fileNameLength;
		result[1] = fileLength[0];
		result[2] = fileLength[1];
		result[THIRD_INDEX_OF_RESULT] = fileLength[2];
		System.arraycopy(filename, 0, result, FOURTH_INDEX_OF_RESULT, filename.length);

		return result;
	}

	
	/** this function converts binary representation into decimal
	 * @param binary binary representation
	 * @return Integer number that came about by converting binary 
	 */
	public static int binaryInDecimal(String binary) {
		int result = 0;

		for (int i = binary.length() - 1; i >= 0; i--) {
			if (binary.charAt(i) == '1') {
				result += Math.pow(2, binary.length() - 1 - i);
			}
		}

		return result;
	}
}
