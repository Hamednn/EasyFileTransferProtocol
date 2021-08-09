
package eit.cli;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

import edu.fra.uas.oop.Terminal;
import eit.application.EasyFileTransferProtocol;
import eit.linecode.DataDecoder8B6T;
import eit.linecode.DataEncoder8B6T;
import eit.linecode.exception.DecodeException;
import eit.linecode.exception.EndOfPacketException;
import eit.linecode.exception.StartOfStreamException;
import eit.medium.Cable;

/**
 * the main class of the program
 *
 * @author hamed,Nakhei Nejad
 */
public class Main {
	private static final int SIZE_OF_DATA_LINE_TO_DECODE = 3;
	private static final int FIRST_CHARAKTER_TO_ENCODE = 7;
	private static final int BEGIN_OF_TEXT_FOR_TRANSMIT = 9;
	private static final int BEGIN_OF_FILENAME_FOR_TRANSMIT = 13;

	/**
	 * is the main function to run the program
	 * The following functionalities are made available in this program.
	 * <li>encoding in 8B6T</li>
	 * <li>decoding from 8B6T</li>
	 * <li>transmit and receive strings and characters</li>
	 * <li>transmit and receive files</li>
	 * the functions mentioned are described in detail in each case.<br>
	 * @param args an array of command-line arguments for the application
	 * @Param input the inputText from user
	 */
	public static void main(String[] args){

		String input = "";
		Cable<String[]> cable = new Cable<String[]>();
		DataEncoder8B6T encoder = new DataEncoder8B6T();
		DataDecoder8B6T decoder = new DataDecoder8B6T();
		//eftp => Easy File Transfer Protocol
		EasyFileTransferProtocol eftp = new EasyFileTransferProtocol(encoder, decoder, cable);

		while (true) {
			input = Terminal.readLine();

			if (input.contains("encode")) { // control if input is right

				input = input.substring(FIRST_CHARAKTER_TO_ENCODE); // Delete "encode " from input

				byte[] inputToByte = input.getBytes(); // convert input from String to bytes

				DataEncoder8B6T encodeClass = new DataEncoder8B6T();
				String[] encodeText = encodeClass.encode(inputToByte); // and encode input to 3 DataStreams

				Terminal.printLine("DataStream 1: " + encodeText[0]);
				Terminal.printLine("DataStream 2: " + encodeText[1]);
				Terminal.printLine("DataStream 3: " + encodeText[2]);

				continue;
			} else if (input.equals("decode")) {

				String[] dataLines = new String[SIZE_OF_DATA_LINE_TO_DECODE];
				DataDecoder8B6T decoderClass = new DataDecoder8B6T();

				Terminal.printLine("Please enter first encoded data stream:");
				dataLines[0] = Terminal.readLine();

				Terminal.printLine("Please enter second encoded data stream:");
				dataLines[1] = Terminal.readLine();

				Terminal.printLine("Please enter third encoded data stream:");
				dataLines[2] = Terminal.readLine();

				try {
					byte[] letter = decoderClass.decode(dataLines);

					String outputText = new String(letter, StandardCharsets.UTF_8);
					Terminal.printLine(outputText);
				} catch (StartOfStreamException e) {
					e.printStackTrace();
				} catch (EndOfPacketException e) {
					e.printStackTrace();
				} catch (DecodeException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// throw new DecodeException("Wrong data size!!!");
					Terminal.printLine(e);
				}

				continue;

			} else if (input.contains("transmit ")) {
				if (input.substring(0, BEGIN_OF_TEXT_FOR_TRANSMIT).equals("transmit ")) {
					input = input.substring(BEGIN_OF_TEXT_FOR_TRANSMIT);
					byte[] inputToByte = input.getBytes(); // convert input from String to bytes

					String[] encodedData = encoder.encode(inputToByte);

					cable.transmit(encodedData);
				}
				continue;
			} else if (input.equals("receive")) {
				try {
					String[] receive = cable.receive();
					byte[] data;
					if (decoder.decode(receive) != null) {
						data = decoder.decode(receive);
						String outputText = new String(data, StandardCharsets.UTF_8);
						Terminal.printLine(outputText);
					}
				} catch (NoSuchElementException e) {
					Terminal.printLine("No data on line!!!");
				} catch (Exception e) {

				}
				continue;
			} else if (input.contains("transmitFile ")) {
				if (input.substring(0, BEGIN_OF_FILENAME_FOR_TRANSMIT).equals("transmitFile ")) {
					input = input.substring(BEGIN_OF_FILENAME_FOR_TRANSMIT);

					File file = new File(input);
					//Terminal.printLine(file.length());
					eftp.transmitFile(file);
					cable = eftp.cable;
					Terminal.printLine("send file: " + file.getName());

				}

				continue;
			} else if (input.equals("receiveFile")) {
				File newfile = eftp.receiveFile();
				if (newfile == null) {
					Terminal.printLine("No data on line!!!");
				} else {

					Terminal.printLine("received file: " + newfile.getName() + " with size: " + newfile.length());

				}

				continue;
			}
			if (input.equals("quit")) { // if the input equals exit, the program is terminated

				break;
			}

			Terminal.printError("unknown command");
		}

	}

}
