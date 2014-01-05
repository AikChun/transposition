import java.util.*;
import java.io.*;

/*
 *
 * Author: Sim Aik Chun
 *
 *
 */

public class Transposition {

/**
 * main function
 */
	public static void main (String[] args) {
		if (args.length != 4) {
			System.out.println("Incorrect argument length.");
			System.exit(0);
		}

		String key = args[1];

		// check key validity. exits program if not.
		if (!checkKey(key)) {
			System.out.println("Error with cipher key value.");
			System.exit(0);
		} 

		System.out.println("Key is OK.");

		String inputFilename = args[args.length-2]; 
		String outputFilename = args[args.length-1];

		if (args[0].equals("-e")) {

			String input = readFile(inputFilename);
			
			// invoke encryption
			String output = encryption(input, key);

			// write in into an output file
			if (writeToOutputFile(output, outputFilename)) {
				System.out.println("Text has been encrypted and written on to " + outputFilename);
			}

		} 

		if (args[0].equals("-d")) {
			String input = readFile(inputFilename);

			// invoke decryption
			String output = decryption(input, key);
			System.out.println("Decrypted text: ");

			if (writeToOutputFile(output, outputFilename)) {
				System.out.println("Text has been encrypted and written on to " + outputFilename);
			}
		}

	}
	
/**
 * check for valid key value.
 * @param key String cipher key value.
 * @return boolean returns true when there's no error, false otherwise.
 */
	public static boolean checkKey(String key) {

		int keyInInt = 0;
		try {
			// check key length
			if (!(key.length() < 10)) {
				throw new TranspositionException("Key is too long. Only allow cipher keys with length up to 10.");
			}
			// check key for non-numeric value

			keyInInt = Integer.parseInt(key);

			if (checkForRepeatedDigitInKey(key) && checkForMissingKeyValue(key)) {
				return true;
			}

		} catch (TranspositionException te) {
			System.err.println("TranspositionException: " + te.getMessage());
		} catch (NumberFormatException nfe) {
			System.err.println("NumberFormatException: " + nfe.getMessage());
		}

		return false;
	}

/**
 * check key whether they have missing digits according to their key lengths.
 * Example:  key length: 5, key must contain 0-4
 * @param key String cipher key value
 * @return boolean true if key is valid, false otherwise
 */
	public static boolean checkForMissingKeyValue(String key) {
		int keyLength = key.length();

		for (int i=0; i<keyLength; i++) {
			if(!key.contains(Integer.toString(i))) {
				System.out.println("Each digit of key must be within the range from 0 to the length of the key.");
				return false;
			}
		}
		return true;
	}

/**
 * check key for repeated value
 * @param key String cipher key value
 * @return boolean true if key is valid, false otherwise
 */
	public static boolean checkForRepeatedDigitInKey(String key) {
		// needs to check whether each number in the key is unique.
		String curDigit = "";
		for (int i=0; i<key.length();i++) {
			if (i < key.length()-1) {
				curDigit = key.substring(i, i+1);
				String restOfKey = key.substring(i+1);
				if (restOfKey.contains(curDigit)) {
					System.out.println("Cannot have recurring numbers.");
					return false;
				}
			}
		}
		return true;
	}

/**
 * Extracts text file 
 * @param inputFilename String filename of the input file
 * @return String concatenation of all text in the file
 */
	public static String readFile(String inputFilename) {
		String fileLine = "null";
		String everything = "";

		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(inputFilename));

			fileLine = fileReader.readLine();

			while (fileLine != null) {
				everything = everything + fileLine;

				if ((fileLine = fileReader.readLine()) != null) {
					everything = everything + " ";
				}
			}

		return everything;

		} catch (IOException ioe) {
			System.out.println("Error reading file.");
		}
		return fileLine;
	}
	
/**
 * Does Transposition Cipher with an input string
 * @param input String data that is to be ciphered
 * @param key String cipher key
 * @return String ciphered data
 */
	public static String encryption(String input, String key) {

		int inputLength = input.length();
		int numberOfCols = key.length();
		int numberOfRows = 0;

		if (input.length() % numberOfCols > 0) {
			numberOfRows = (input.length() / numberOfCols) + 1;
		}

		if (input.length() % numberOfCols == 0) {
			numberOfRows = input.length() / numberOfCols;
		}

		char[][] characterMap;
		characterMap = new char[numberOfRows][numberOfCols];

		for (int row=0; row<numberOfRows; row++) {
			for (int col=0; col<numberOfCols; col++) {
				characterMap[row][col] = ' ';
			}
		}
		int rows = 0;
		int cols = 0;

		for (int i=0; i<input.length(); i++) { 
			characterMap[rows][cols] = input.charAt(i);
			cols++;
			if (cols == numberOfCols) {
				rows++;
				cols = 0;
			}
		}

		// need to append back into a string according to the key.
		int curCols = 0;
		String result = "";

		for (int i=0; i<key.length(); i++) {

			if (i == key.length()-1) {
				curCols = Integer.parseInt(key.substring(i));
			} else {
				curCols = Integer.parseInt(key.substring(i,i+1));
			}

			for (int k=0; k<numberOfRows; k++) {
				result = result + characterMap[k][curCols];
			}

		}

		return result;
	}

/**
 *  Write String into an output file.
 *  @param data String that is to be written to output file
 *  @param outputFilename String ouput file name.
 *  @return boolean true when writing to output is successful, false
 *  otherwise.
 */
	public static boolean writeToOutputFile(String data, String outputFilename) {
		
		try {
			PrintWriter pw = new PrintWriter(outputFilename, "UTF-8");
			pw.print(data);
			pw.close();
			return true;
		} catch (IOException ioe) {
			System.out.println("Failed to write to output file.");
		}

		return false;
		
	}

/**
 * deciphers text using key 
 * @param data String the ciphertext to be processed
 * @param key String the cipher key
 * @return String plaintext output of the deciphered text
 */
	public static String decryption(String data, String key) {
		int numberOfCols = key.length();
		int numberOfRows = 0;

		if (data.length() % numberOfCols > 0) {
			numberOfRows = (data.length() / numberOfCols) + 1;
		}

		if (data.length() % numberOfCols == 0) {
			numberOfRows = data.length() / numberOfCols;
		}

		char[][] characterMap = new char[numberOfRows][numberOfCols];

		for (int row=0; row<numberOfRows; row++) {
			for (int col=0; col<numberOfCols; col++) {
				characterMap[row][col] = ' ';
			}
		}

		int curCols = 0;
		int dataIndex = 0;
		String result = "";

		// Fills up the columns of the characterMap by column.
		for (int i=0; i<key.length(); i++) {
			if (i == key.length()-1) {
				curCols = Integer.parseInt(key.substring(i));
			} else {
				curCols = Integer.parseInt(key.substring(i,i+1));
			}

			for (int k=0; k<numberOfRows; k++) {
				if (dataIndex < data.length()) {
					characterMap[k][curCols] = data.charAt(dataIndex);
					dataIndex++;
				}
			}
		}

		// Reads characterMap left to right, from row 0 to last row.
		for (int row=0; row<numberOfRows; row++) {
			for (int col=0; col<numberOfCols; col++) {
				result = result + characterMap[row][col];
			}
		}

		return result;
		
	}
}

class TranspositionException extends Exception {
	public TranspositionException (String errorMessage) {
		super(errorMessage);
	}
}
