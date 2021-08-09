package eit.linecode;

/**
 *
 * This class implements the encoder functionality of the 8B/6T encoder It is
 * multilevel line coding scheme used to encode Datastreams into pattern of
 * signal elements. The 8B6T converts 8 bits of data into 6 signal elements the
 * value of the data byte is compared to the values in the 23A 1a 100BASE T4
 * 8B6T code table Every possible byte has a unique 6T code. using three levels
 * of signal viz. +ve, -ve and zero.<br>
 * This code is used in 100Base-4T cable. for more information on coding: visit
 * the following Internet-address:<br>
 * https://www.liveaction.com/resources/glossary/signal-encoding-fast-ethernet-environment-signal-encoding/
 * <br>
 *
 * @author Hamed Nakhei
 */
public class DataEncoder8B6T extends Functions implements CodeInterface {

	private static int dcBalance1 = 0, dcBalance2 = 0, dcBalance3 = 0;
	private static final int TWO_COMPLIMENT = 256;
	private static final int MODULO_THREE = 3;
	private static final int THREE_DATASTREAMS = 3;

	/**
	 * This function encode the text to the 8B/6T<br>
	 * if the user need to encode text to the 8B/6T with 3 data stream can use this
	 * function.
	 *
	 * @param data the input text is from data type byte[], which the user want to
	 *             encode to 8B/6T
	 * @return is from data type String[] with 3 elements
	 *         {<b>DataStream1</b>,<b>DataStream2</b>,<b>DataStream3</b>} data
	 *         streams consists of a start of stream delimiter, main segment and end
	 *         of stream delimiter.<br>
	 *         ---------------------<br>
	 *         <b>Start of stream delimiter:</b><br>
	 *         <br>
	 *         data stream one starts with P4|SOSa|SOSb<br>
	 *         data stream two starts with SOSa|SOSa|SOSb<br>
	 *         data stream three starts with P3|SOSa|SOSa|SOSb<br>
	 *         ---------------------<br>
	 *         <b>main segment:</b><br>
	 *         <br>
	 *         the input parameter from user is encoded according to the
	 *         sequence<br>
	 *         ---------------------<br>
	 *         <b>End of stream delimiter:</b><br>
	 *         <br>
	 *         end segment of data stream one starts with EOP_1|EOP_4 or inverted of
	 *         EOP_1|EOP_4<br>
	 *         end segment of data stream two starts with EOP_2|EOP_5 or inverted of
	 *         EOP_2|EOP_5<br>
	 *         end segment of data stream two starts with EOP_3 or inverted of
	 *         EOP_3<br>
	 */

	public String[] encode(byte[] data) {
		// at the beginning the variables are set to 0 if the function is called several
		// times
		dcBalance1 = 0;
		dcBalance2 = 0;
		dcBalance3 = 0;

		// 3 temporary string variables are declared and initialized directly with start
		// values
		String temp1 = STARTOFDATASTREAM1;
		String temp2 = STARTOFDATASTREAM2;
		String temp3 = STARTOFDATASTREAM3;

		for (int i = 0; i < data.length; i++) {
			/*
			 * In java, bits from 128 onwards are interpreted as a 2's compliment. So I add
			 * 256 to get the positive number. or you could just get the positive with By
			 * Byte.toUnsignedInt(0)
			 */
			int inputByteToInteger = Integer.parseInt(String.valueOf(data[i]));
			
			if (inputByteToInteger < 0) {
				inputByteToInteger = inputByteToInteger + TWO_COMPLIMENT;
			}

			// here we will calculate the wordBalancet
			// we have to following DC balance encoding rules
			int wordBalance = cumulativeWeight(CODETABLE[inputByteToInteger]);
			if (i % MODULO_THREE == 0) {
				// a) If the 6T code group weight is 0, do not change the cumulative weight.
				if (wordBalance == 0) {
					temp1 = temp1 + CODETABLE[inputByteToInteger];
					continue;
				}
				// b) If the 6T code group weight is 1, and the cumulative weight bit is 0,
				// set the cumulative weight bit to 1.
				if (wordBalance == 1 && dcBalance1 == 0) {
					dcBalance1 = 1;
					temp1 = temp1 + CODETABLE[inputByteToInteger];
					continue;
				}
				// c) If the 6T code group weight is 1, and the cumulative weight bit is also 1,
				// set the cumulative weight
				// bit to 0, and then algebraically negate all the ternary symbol values in the
				// 6T code group.
				if (wordBalance == 1 && dcBalance1 == 1) {
					dcBalance1 = 0;
					temp1 = temp1 + inverted(CODETABLE[inputByteToInteger]);
					continue;
				}
			}
			if (i % MODULO_THREE == 1) {
				// a) If the 6T code group weight is 0, do not change the cumulative weight.
				if (wordBalance == 0) {
					temp2 = temp2 + CODETABLE[inputByteToInteger];
					continue;

				}
				// b) If the 6T code group weight is 1, and the cumulative weight bit is 0,
				// set the cumulative weight bit to 1.
				if (wordBalance == 1 && dcBalance2 == 0) {
					dcBalance2 = 1;
					temp2 = temp2 + CODETABLE[inputByteToInteger];
					continue;
				}
				// c) If the 6T code group weight is 1, and the cumulative weight bit is also 1,
				// set the cumulative weight
				// bit to 0, and then algebraically negate all the ternary symbol values in the
				// 6T code group.
				if (wordBalance == 1 && dcBalance2 == 1) {
					dcBalance2 = 0;
					temp2 = temp2 + inverted(CODETABLE[inputByteToInteger]);
					continue;
				}
			}
			if (i % MODULO_THREE == 2) {
				// a) If the 6T code group weight is 0, do not change the cumulative weight.
				if (wordBalance == 0) {
					temp3 = temp3 + CODETABLE[inputByteToInteger];
					continue;

				}
				// b) If the 6T code group weight is 1, and the cumulative weight bit is 0,
				// set the cumulative weight bit to 1.
				if (wordBalance == 1 && dcBalance3 == 0) {
					dcBalance3 = 1;
					temp3 = temp3 + CODETABLE[inputByteToInteger];
					continue;
				}
				// c) If the 6T code group weight is 1, and the cumulative weight bit is also 1,
				// set the cumulative weight
				// bit to 0, and then algebraically negate all the ternary symbol values in the
				// 6T code group.
				if (wordBalance == 1 && dcBalance3 == 1) {
					dcBalance3 = 0;
					temp3 = temp3 + inverted(CODETABLE[inputByteToInteger]);
					continue;
				}
			}
		}
		String[] text = new String[THREE_DATASTREAMS];
		// After encoding any of the constants eop1-5, update the cumulative weight bit
		// for
		// the affected pair according to rules e) and f):
		// e) If the cumulative weight is 0, do not change the cumulative weight;
		// algebraically negate all the ter- nary symbol values in eop1-5.
		// f) If the cumulative weight is 1, do not change the cumulative weight.
		if (dcBalance1 == 0) {
			text[0] = temp1 + inverted(ENDOFDATASTREAM1);
		} else {
			text[0] = temp1 + ENDOFDATASTREAM1;
		}
		if (dcBalance2 == 0) {
			text[1] = temp2 + inverted(ENDOFDATASTREAM2);
		} else {
			text[1] = temp2 + ENDOFDATASTREAM2;
		}

		if (dcBalance3 == 0) {
			text[2] = temp3 + inverted(ENDOFDATASTREAM3);
		} else {
			text[2] = temp3 + ENDOFDATASTREAM3;
		}
		return text;
	}

}