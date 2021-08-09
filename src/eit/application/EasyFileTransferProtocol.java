package eit.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import eit.linecode.ControlFrame;
import eit.linecode.DataDecoder8B6T;
import eit.linecode.DataEncoder8B6T;
import eit.medium.Cable;

/**
 * The Easy File Transfer Protocol (EFTP) is a standard communication protocol
 * used for the transfer of computer files.<br>
 * the files transmit and receive methods are declared here.<br>
 * using these methods, we will be able to transport or receive a file.<br>
 * 
 * * for more in-depth information on this topic, please visit this source:<br>
 * https://en.wikipedia.org/wiki/File_Transfer_Protocol
 * @author Hamed Nakhei
 */

public class EasyFileTransferProtocol {
	DataEncoder8B6T encoder;
	DataDecoder8B6T decoder;
	public Cable<String[]> cable;
	private static final int MAX_DATA_FRAME_SIZE = 2048;
	private static final int START_OF_DATA_FROM_DATA_FRAME_INDEX = 4;
	private static final int LENGTH_OF_INTEGER_IN_BYTE = 4;
	private static final int FIRST_INDEX_FROM_FILELENGHT = 1;
	private static final int SECOND_INDEX_FROM_FILELENGHT = 2;
	private static final int THIRD_INDEX_FROM_FILELENGHT = 3;

	/**is the standard constructor.<br>
	 *the following instances are passed on here
	 * @param enc   an instance from DataEncoder8B6T
	 * @param dec   an instance from DataDecoder8B6T
	 * @param cable an instance from Cable class
	 */

	public EasyFileTransferProtocol(DataEncoder8B6T enc, DataDecoder8B6T dec, Cable<String[]> cable) {
		this.encoder = enc;
		this.decoder = dec;
		this.cable = cable;
	}

	/**
	 * With the help of the transmitFile() method, any file can be transferred via
	 * the Cable class<br>
	 * In this method, we transport a file encoded in cable. the coding is done by
	 * 8B6T coding<br>
	 * First a data frame is created which contains the information from the
	 * file<br>
	 * Data frame is also coded and put into cable then the file is divided into
	 * 2048 data frame segments and coded in 8B6T coding.<br>
	 * at the end the segments are packed in cables.<br>
	 * @param file which we want to transmit <br>
	 */

	public void transmitFile(File file) {
		// file is converted to byte
		byte[] fileInByteArray = readFileToByteArray(file);
		ControlFrame control = new ControlFrame();
		byte[] controlFrame = control.getBytes(file);

		String[] controlFrameEncode = encoder.encode(controlFrame);
		// Put the control frame in the cable
		cable.transmit(controlFrameEncode);

		// if file is larger than MAX_DATA_FRAME_SIZE

		if (file.length() > MAX_DATA_FRAME_SIZE) {
			List<byte[]> dataFrames = new ArrayList<byte[]>();
			
			double d = (double) fileInByteArray.length / MAX_DATA_FRAME_SIZE;
			int numberOfSegments = (int) Math.ceil(d);

			// Calculate remaining bytes if the file is larger than MAX_DATA_FRAME_SIZE byte

			double restByte = ((double) file.length() / MAX_DATA_FRAME_SIZE
					- ((int) (file.length() / MAX_DATA_FRAME_SIZE))) * MAX_DATA_FRAME_SIZE;
			int rest = (int) restByte;
			byte[] dataFrame = new byte[MAX_DATA_FRAME_SIZE + 1];
			int index = 0;
			// DataFrame first position => int seqNr in byte
			// the rest is filled with MAX_DATA_FRAME_SIZE bytes up to the point
			// MAX_DATA_FRAME_SIZE
			for (int i = 0; i < numberOfSegments - 1; i++) {
				dataFrame[0] = (byte) (numberOfSegments - i - 1);
				for (int j = 1; j < MAX_DATA_FRAME_SIZE + 1; j++) {
					dataFrame[j] = fileInByteArray[index];
					index++;
				}
				dataFrames.add(dataFrame.clone());
			}
			byte[] restOfBytes = new byte[rest + 1];

			restOfBytes[0] = (byte) 0;
			for (int j = 1; j < rest; j++) {
				restOfBytes[j] = fileInByteArray[index];
				index++;
			}
			dataFrames.add(restOfBytes.clone());
			// encode the list and then put it in cable
			for (int i = 0; i < dataFrames.size(); i++) {
				String[] dataEncode = encoder.encode(dataFrames.get(i));
				cable.transmit(dataEncode);
			}
		} else {
			// if file is smaller than MAX_DATA_FRAME_SIZE
			byte[] dataFrame = new byte[(int) (1 + file.length())];
			dataFrame[0] = (byte) 0;
			System.arraycopy(fileInByteArray, 0, dataFrame, 0, fileInByteArray.length);
			String[] dataEncode =encoder.encode(dataFrame);
			cable.transmit(dataEncode);
		}
	}

	/**
	 * In this function, a file is broken down into a byte array<br>
	 * Here is the code to read a file into a byte array using FileInputStream
	 * class<br>
	 *
	 * @param file which we want to convert into byte array<br>
	 * @return bytes from file in a byte array<br>
	 */

	private static byte[] readFileToByteArray(File file) {
		FileInputStream fis = null;
		// Creating a byte array using the length of the file
		// file.length returns long which is cast to int
		byte[] bArray = new byte[(int) file.length()];
		try {
			fis = new FileInputStream(file);
			fis.read(bArray);
			fis.close();
		} catch (IOException ioExp) {
			ioExp.printStackTrace();
		}
		return bArray;
	}

	/**
	 * the data on the cable is converted into a file and returned.<br>
	 * first the dataframe is received and analyzed.<br>
	 * accordingly, the following information is determined: <br>
	 * <li>File name length</li>
	 * <li>File length</li>
	 * <li>File name</li>
	 * <li>Number of segments</li> then the remaining datas are received and put
	 * together. At the end, a file is generated and saved with received datas<br>
	 *
	 * @return the received files which we collect by collecting segments.
	 */
	public File receiveFile() {
		byte[] decodedFile;
		try {
			decodedFile = decoder.decode(this.cable.receive());
		} catch (Exception e) {
			return null;
		}
		
		String fileName = "";
		for (int i = START_OF_DATA_FROM_DATA_FRAME_INDEX; i < decodedFile.length; i++) {
			fileName = fileName + (char) decodedFile[i];
		}

		byte[] fileLenght = new byte[LENGTH_OF_INTEGER_IN_BYTE];
		fileLenght[0] = 0;
		fileLenght[FIRST_INDEX_FROM_FILELENGHT] = decodedFile[1];
		fileLenght[SECOND_INDEX_FROM_FILELENGHT] = decodedFile[2];
		fileLenght[THIRD_INDEX_FROM_FILELENGHT] = decodedFile[THIRD_INDEX_FROM_FILELENGHT];
		int fileLenghtInInteger = ByteBuffer.wrap(fileLenght).getInt();
		double segments = fileLenghtInInteger / MAX_DATA_FRAME_SIZE;
		int numberOfSegments = (int) Math.ceil(segments);
		File file = new File("rcvd-" + fileName);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			int i = 0;
			for (i = 0; i < numberOfSegments + 1; i++) {
				byte[] dataFrame = decoder.decode(this.cable.receive());

				fos.write(dataFrame, 1, dataFrame.length - 1);
			}
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;

	}
}
