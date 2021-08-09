package eit.linecode;

import eit.linecode.exception.DecodeException;
import eit.linecode.exception.EndOfPacketException;
import eit.linecode.exception.StartOfStreamException;

/**
 * <b>This class provides some simple methods for decode data stream which are
 * encoded in 8B6T coding</b><br>
 * An apparatus and method for transmitting an 8-bit binary format data word as
 * a 6trit ternary code word includes an encoder, a decoder, and a code
 * assignment that produce, for each 8-bit data word value, an unique 6 ternary
 * code word that is particularly optimized for transmission over twisted-pair
 * cable. The logic circuitry of the invention is optimized to accomplish the
 * translation using a small number of combinatorial logic gates. The present
 * invention thus has advantages in size, speed and performance over other
 * possible means for encoding an 8 bit data word to a 6 code word. quoted by:
 * <br>
 * https://www.freepatentsonline.com/5525983.html <br>
 * for more information on coding: visit the following Internet-address:<br>
 * https://www.liveaction.com/resources/glossary/signal-encoding-fast-ethernet-environment-signal-encoding/
 *
 * @author Hamed Nakhei
 *
 */
public class DataDecoder8B6T extends Functions implements CodeInterface {

	private static final int HEXDECIMAL_BASE = 16;
	private static final int SIZE_OF_8B6T = 6;
	private static final int DATASTREAM1 = 1;
	private static final int DATASTREAM2 = 2;
	private static final int DATASTREAM3 = 3;
	private static final int START_OF_NEGATIVE_HEXDECIMAL = 127;
	private static final int TWO_COMPLIMENT = 256;

	/**
	 * this function decodes a string from 8B6T to byte array<br>
	 * First the parts of the 8B6T need to be stored in a string array. afterwards,
	 * the DC balance rules are used to invert accordingly or remain unaffected.<br>
	 * then parts are checked with the 23A 1a 100BASE T4 8B6T code table and the
	 * corresponding value is assigned accordingly.<br>
	 * here we have to pay attention to the 2 compliment.
	 *
	 * @param input is a String which consists of 8B6T codes.<br>
	 *              this String is without Start/end of stream delimiter.we have to
	 *              delete Start/end of stream delimiter before.<br>
	 * @return return decoded parts of the input in the form of bytes array
	 */

	private byte[] decodeLine(String input) {
		// the 8b6t consists of 6 digits, so we have to divide the input by 6 so that we
		// can determine the size of parts
		String[] parts = new String[input.length() / SIZE_OF_8B6T];

		// First the parts of the 8B6T need to be stored in a string array.
		try {
			int i = 0;
			while (!input.isEmpty()) {
				String temp = input.substring(0, SIZE_OF_8B6T);
				parts[i] = temp;
				input = input.substring(SIZE_OF_8B6T);
				i++;
			}
		} catch (DecodeException e) {
			e.printStackTrace();

		}
		// the rules of the DC balance must be used here.
		// DC balance rules are used to invert accordingly or remain unaffected
		int dcBalance = 0;
		int wordWeight = 0;
		for (int i = 0; i < parts.length; i++) {
			wordWeight = cumulativeWeight(parts[i]);
			if (dcBalance == 0 && wordWeight == 0) {
				dcBalance = 0;
				continue;
			}

			if (dcBalance == 0 && wordWeight == 1) {
				dcBalance = 1;
				continue;
			}

			if (dcBalance == 1 && wordWeight == 0) {
				dcBalance = 0;
				continue;
			}
			if (dcBalance == 0 && wordWeight == -1) {
				parts[i] = inverted(parts[i]);
				dcBalance = 0;
				continue;
			}
			if (dcBalance == 1 && wordWeight == -1) {
				parts[i] = inverted(parts[i]);
				dcBalance = 0;
				continue;
			}
			if (dcBalance == 1 && wordWeight == 1) {
				parts[i] = inverted(parts[i]);
				dcBalance = 0;
			}
		}

		byte[] letters = new byte[parts.length];
		//
		// here the party will be compared with the 23A 1a 100BASE T4 8B6T code table
		// and
		// given its worth accordingly
		int tempIntForConvertHexToInt = 0;
		for (int i = 0; i < parts.length; i++) {
			for (int j = 0; j < CODETABLE.length; j++) {
				if (parts[i].equals(CODETABLE[j])) {
					/*
					 * In java, bits from 128 onwards are interpreted as a 2's compliment. So I
					 * subtract 256 to get the positive number.
					 */
					if (j > START_OF_NEGATIVE_HEXDECIMAL) {
						String value = Integer.toHexString(j);
						try {
							// it will convert to hexadecimal
							tempIntForConvertHexToInt = Integer.parseInt(value, HEXDECIMAL_BASE);
						} catch (DecodeException ex) {
							throw new DecodeException("Wrong data size!!!");
						}
						tempIntForConvertHexToInt = tempIntForConvertHexToInt - TWO_COMPLIMENT;
						byte b = (byte) tempIntForConvertHexToInt;
						letters[i] = b;

					} else {
						letters[i] = (byte) j;

					}
				}

			}

		}

		return letters;
	}

	/**
	 * this function is used to decode the entered data stream which are encoded in
	 * 8B6T to byte array.<br>
	 * the data stream are checked for size and correctness. at the end the encoded
	 * inputs are decoded according to the sequence.
	 *
	 * @param data is a string array with 3 string which are
	 *             <li>first data stream</li>
	 *             <li>second data stream</li>
	 *             <li>third data stream</li> and they are encoded in 8B6T.
	 * @return return decoded of the first data stream,second data stream and third
	 *         data stream in the form of bytes array
	 * @throws StartOfStreamException if something is wrong with the start of stream
	 *                                delimiter<br>
	 *                                start of stream delimiter is always the same
	 *                                depending on the data stream one,two or three.
	 *                                An exception is thrown in the event of
	 *                                inconsistencies<br>
	 * @throws EndOfPacketException   if something is wrong with the end of stream
	 *                                delimiter<br>
	 *                                <li>if wrong inverted</li>
	 *                                <li>if end of stream delimiter has not the
	 *                                right size</li>
	 * @throws DecodeException        if something is wrong with the main part,
	 *                                which we want to decode<br>
	 *                                <li>if not divisible by 6</li>
	 *                                <li>wrong inputs</li>
	 *                                <li>if the input is empty</li>
	 *                                <li>if the entry does not contain
	 *                                anything</li>
	 */

	public byte[] decode(String[] data) throws StartOfStreamException, EndOfPacketException, DecodeException {

		String input1 = data[0];
		String input2 = data[1];
		String input3 = data[2];
		// check whether the inputs are empty
		if (data[0].isEmpty() || data[1].isEmpty() || data[2].isEmpty()) {
			throw new DecodeException("Wrong data size!!!");
		}
		// check whether the inputs contain nothing (null)
		if (data[0] == null || data[1] == null || data[2] == null) {
			throw new DecodeException("Wrong data size!!!");
		}

		// Check length of data inputs
		// if the size of data streams are not correct, we have to throw out
		// "DecodeException"
		if (input1.length() < (STARTOFDATASTREAM1.length() + ENDOFDATASTREAM1.length())
				|| input2.length() < (STARTOFDATASTREAM2.length() + ENDOFDATASTREAM2.length())
				|| input3.length() < (STARTOFDATASTREAM3.length() + ENDOFDATASTREAM3.length())) {
			throw new DecodeException("Wrong data size!!!");
		}
		if (input1.length() < (STARTOFDATASTREAM1.length() + ENDOFDATASTREAM1.length()) + SIZE_OF_8B6T) {
			throw new DecodeException("Wrong data size!!!");
		}

		// Check start of data streams delimiter
		// start of data stream delimiter of inputs is checked whether they are the
		// same.
		// if they are not the same, we have to throw out StartOfStreamException
		if (!input1.substring(0, STARTOFDATASTREAM1.length()).equals(STARTOFDATASTREAM1)) {
			throw new StartOfStreamException("Start of Stream 1 is incorrect!!!");
		} else if (!input2.substring(0, STARTOFDATASTREAM2.length()).equals(STARTOFDATASTREAM2)) {
			throw new StartOfStreamException("Start of Stream 2 is incorrect!!!");
		} else if (!input3.substring(0, STARTOFDATASTREAM3.length()).equals(STARTOFDATASTREAM3)) {
			throw new StartOfStreamException("Start of Stream 3 is incorrect!!!");
		}
		// end of data stream delimiter of inputs is controlled with the help of the
		// function "checkEndOfData"
		// if the function return false,we have to throw out EndOfPacketException
		if (checkEndOfData(input1, DATASTREAM1)) {
			input1 = deleteEndOfData(input1.substring(STARTOFDATASTREAM1.length()), 1);
		} else {
			throw new EndOfPacketException("End of Stream 1 is incorrect!!!");
		}
		if (checkEndOfData(input2, DATASTREAM2)) {
			input2 = deleteEndOfData(input2.substring(STARTOFDATASTREAM2.length()), 2);
		} else {
			throw new EndOfPacketException("End of Stream 2 is incorrect!!!");
		}
		if (checkEndOfData(input3, DATASTREAM3)) {
			input3 = deleteEndOfData(input3.substring(STARTOFDATASTREAM3.length()), DATASTREAM3);
		} else {
			throw new EndOfPacketException("End of Stream 3 is incorrect!!!");
		}

		byte[] first = new byte[input1.length()];
		byte[] second = new byte[input1.length()];
		byte[] third = new byte[input1.length()];
		// when the size of data streams are null
		if (first == null || second == null || third == null) {
			throw new DecodeException("Wrong data size!!!");
		}
		// The size of the first data stream must be checked.
		if (checkInputSize(input1)) {
			first = decodeLine(input1);
		} else {
			// if false we have to throw DecodeException
			throw new DecodeException("Wrong data size!!!");
		}
		// The size of the second data stream must be checked.
		if (checkInputSize(input2)) {
			second = decodeLine(input2);

		} else {
			// if false we have to throw DecodeException
			throw new DecodeException("Wrong data size!!!");
		}
		// The size of the third data stream must be checked.
		if (checkInputSize(input3)) {
			third = decodeLine(input3);
		} else {
			// if false we have to throw DecodeException
			throw new DecodeException("Wrong data size!!!");
		}
		// we declare a byte array with the size of 3 data streams
		byte[] letters = new byte[third.length + second.length + first.length];

		/*
		 * here the three byte arrays are mixed together. first index of each array is
		 * taken out and put together. so that we sort the text in the correct order for
		 * example if the input text by encoding was "Hello World!!!": first => HlWl!
		 * second=> eood! third => l r! the finally text will be "Hello World"!!
		 */
		int index = 0; // use for the index by put bytes to letters[]

		// the for loop must be repeated as often as the size of first data stream
		for (int i = 0; i < first.length; i++) {
			letters[index] = first[i];
			index++;
			// if i is bigger or equals the size of second data stream. we are finish with
			// mixing.
			if (i >= second.length) {
				break;
			}
			letters[index] = second[i];
			index++;
			// if i is bigger or equals the size of third data stream. we are finish with
			// mixing.
			if (i >= third.length) {
				break;
			}
			letters[index] = third[i];
			index++;
		}
		// finally we will return the byte array (letter) which decoded the bytes from
		// the entered data stream
		return letters;
	}

}
