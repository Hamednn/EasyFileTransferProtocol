package eit.linecode;

import eit.linecode.exception.DecodeException;

/**
 * This class provides some simple methods for decode/encode data stream which
 * are encoded/decoded in 8B6T coding<br>
 *
 * @author Hamed Nakhei
 *
 */
public class Functions implements CodeInterface {

	private static final int SIZE_OF_END_DATA_STREAM_1_AND_2 = 12;
	private static final int SIZE_OF_END_DATA_STREAM3_3 = 6;
	private static final int MODULO_SIX = 6;
	private static final int THREE_DATASTREAMS = 3;

	/**
	 * cumulative weight of the code will be calculated
	 *
	 * @param code complete code <br />
	 * @return cumulative weight from the last part of code
	 */
	public int cumulativeWeight(String code) {

		if (code == null) {
			throw new DecodeException("Wrong data size!!!");
		}
		int weight = 0;
		char[] codeToArray = code.toCharArray();
		for (int i = 0; i < codeToArray.length; i++) {
			if (codeToArray[i] == '+') {
				weight++;
			}
			if (codeToArray[i] == '-') {
				weight--;
			}
		}
		if (weight > 0){
			weight = 1;
		}
		if (weight < 0){
			weight = -1;
		}
		return weight;
	}

	/**
	 * the value of the input will inverted
	 *
	 * @param code the text we want to invert
	 * @return inverted String value from the input-value
	 */

	public static String inverted(String code) {
		char[] codeChar = code.toCharArray();
		for (int i = 0; i < codeChar.length; i++) {
			if (codeChar[i] == '+') {
				codeChar[i] = '-';
				continue;
			}
			if (codeChar[i] == '-') {
				codeChar[i] = '+';
				continue;
			}
		}
		code = String.valueOf(codeChar);
		return code;
	}

	/**
	 * with this function we will remove the end of stream delimiter.<br>
	 * Since we always have a certain size at the end of stream delimiter,<br>
	 * we can remove the end of stream delimiter depending on the data stream.
	 *
	 * @param data is from data type String, which we want to remove the End of
	 *             stream Delimiter
	 * @param i    i=1 for the data Stream1 i=2 for the data Stream1 i=3 for the
	 *             data Stream1
	 * @return data without end of stream delimiter
	 */

	public String deleteEndOfData(String data, int i) {
		// i=1 for the data Stream1
		// i=2 for the data Stream1
		// i=3 for the data Stream1
		if (i == 1 || i == 2) {
			return data.substring(0, data.length() - SIZE_OF_END_DATA_STREAM_1_AND_2);
		}
		if (i == THREE_DATASTREAMS) {
			return data.substring(0, data.length() - SIZE_OF_END_DATA_STREAM3_3);
		}
		return data;

	}

	/**
	 * with this function we will contour the last part<br>
	 * we check whether the end of delimiter has the correct size.<br>
	 * if the last part is the same end of stream delimiter or the inverted of end
	 * of stream delimiter.<br>
	 *
	 * @param data is the the data stream, which we want to control the end of
	 *             delimiter
	 * @param i    i=1 => for the data Stream1 i=2 => for the data Stream1 i=3 =>
	 *             for the data Stream1
	 * @return true if the end of stream delimiter has the correct size otherwise
	 *         return false.<br>
	 */

	public boolean checkEndOfData(String data, int i) {

		switch (i) {
		case 1: {
			if (data.substring(data.length() - ENDOFDATASTREAM1.length()).equals(ENDOFDATASTREAM1)
					|| data.substring(data.length() - ENDOFDATASTREAM1.length()).equals(inverted(ENDOFDATASTREAM1))) {
				return true;
			}
			if (data.substring(data.length() - ENDOFDATASTREAM1.length()).equals(ENDOFDATASTREAM1)
					|| data.substring(data.length() - ENDOFDATASTREAM1.length()).equals(inverted(ENDOFDATASTREAM1))) {
				return true;
			}
			break;
		}
		case 2: {
			if (data.substring(data.length() - ENDOFDATASTREAM2.length()).equals(ENDOFDATASTREAM2)
					|| data.substring(data.length() - ENDOFDATASTREAM2.length()).equals(inverted(ENDOFDATASTREAM2))) {
				return true;
			}
			break;
		}
		case THREE_DATASTREAMS: {
			if (data.substring(data.length() - ENDOFDATASTREAM3.length()).equals(ENDOFDATASTREAM3)
					|| data.substring(data.length() - ENDOFDATASTREAM3.length()).equals(inverted(ENDOFDATASTREAM3))) {
				return true;
			}
			break;
		}
		default:
		}

		return false;
	}

	/**
	 * with this function we will contour the size of input. we check whether the
	 * entered text is divisible by 6.<br>
	 * this way we can determine if the size of input is correct or incorrect.<br>
	 *
	 * @param input is only the main part of input data stream without <b>start of
	 *              stream delimiter</b> and <b>end of stream delimiter</b><br>
	 * @return true if the input is divisible by 6 otherwise @return false.<br>
	 */
	public boolean checkInputSize(String input) {
		if (input.length() % MODULO_SIX == 0) {
			return true;
		}
		return false;
	}

}
