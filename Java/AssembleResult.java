package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class AssembleResult {
	
	/* Create an object of Assemble Result */
	private static AssembleResult instance = new AssembleResult();

	/* Make the constructor private so that this class cannot be instantiated */
	private AssembleResult(){}

	/* Get the only object available */
	public static AssembleResult getInstance() {
		return instance;
	}
	
	String[] unorderedDictionary;
	int dictionarySize = 0;
	int numberOfDocs = 0;
	int numberOfPostings = 0;
	String[] resultsArray = null;
	String[] pmids = null;
	
	/* Read Unordered Dictionary and Put into Array */
	void readNewDictionary(String filename, String filename1) {
		Scanner in = null;
		Scanner in1 = null;
		try {
			in = new Scanner(new File(filename));
			in1 = new Scanner(new File(filename1));
			String[] terms1 = in1.nextLine().split(" ");
			numberOfDocs = Integer.parseInt(terms1[0]);
			dictionarySize = Integer.parseInt(terms1[1]);
			numberOfPostings = Integer.parseInt(terms1[2]);
			unorderedDictionary = new String[dictionarySize];
			pmids = new String[numberOfDocs];
			
			int i = 0;
			while (in.hasNext()) {
				String[] terms = in.nextLine().split(" ");
				unorderedDictionary[i++] = terms[0];
				//System.out.println("New Dictionary: " + unorderedDictionary[i-1] + " " + i + " " + terms[0]);
			}
			if (i != dictionarySize) System.err.println("Number of Terms does not match Number of Dictionary Size");
			i = 0;
			while (i < numberOfDocs) {
				terms1 = in1.nextLine().split(" ");
				pmids[i++] = terms1[0];
			}
			if (i != numberOfDocs) System.err.println("Number of PMIDs does not match Number of Tweets");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	void readResults(String filename) {
		resultsArray = new String[numberOfDocs];
		Scanner in = null; int i = 0;
		try {
			in = new Scanner(new File(filename));
			while (in.hasNext()) {
				String[] terms = in.nextLine().split(" ");
				resultsArray[i++] = terms[0];
			}
			if (i != numberOfDocs) System.err.println("Number of Results does not match Number of Tweets");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void readTermMatrix(String filename, String output) {
		Scanner in = null;
		try {
			in = new Scanner(new File(filename));
			FileWriter writer = new FileWriter(output);
			for (String n: unorderedDictionary) writer.append(n + '\n');
			int i = 0;
			while (i < numberOfDocs) {
				String[] terms = in.nextLine().split(",");
				if (dictionarySize != terms.length-1) System.err.println("Number of terms does not match dictionary size");
				writer.append(pmids[i] + ",");
				for (int j = 1; j < dictionarySize+1; j++) {
					writer.append(terms[j] + ",");
				}
				writer.append(resultsArray[i++] + '\n');
			}
			if (i != numberOfDocs) System.err.println("Number of Results does not match Number of Tweets");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void rePrintOutput(String vector, String input, String invIndex, String output) {
		Scanner in = null;
		Scanner in1 = null;
		Scanner in2 = null;
		int i = 0;
		try {
			in = new Scanner(new File(vector));
			in1 = new Scanner(new File(invIndex));
			in2 = new Scanner(new File(input));
			String[] terms = in1.nextLine().split(" ");
			String[] terms1;
			int numDocs = Integer.parseInt(terms[0]);
			int dictSize = Integer.parseInt(terms[1]);
			FileWriter writer = new FileWriter(output);
			while (i < dictSize) {
				terms = in2.nextLine().split(" ");
				writer.append(terms[0] + '\n');
				i++;
			}
			i = 0;
			while (i < numDocs) {
				terms = in2.nextLine().split(" ");
				terms1 = in.nextLine().split(" ");
				terms[terms.length - 1] = terms1[0];
				for (String s: terms) writer.append(s + " ");
				writer.append('\n');
				i++;
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
