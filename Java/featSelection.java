package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class featSelection {

	/* Create an object of featSelection */
	private static featSelection instance = new featSelection();

	/* Make the constructor private so that this class cannot be instantiated */
	private featSelection(){}

	/* Get the only object available */
	public static featSelection getInstance() {
		return instance;
	}

	static final int K = 5;
	static final int topK = 200;
	int numberOfDocs = 0;
	int dictionarySize = 0;
	//int numberOfPostings = 0;
	String[] dictionary = null;
	String[] pmids = null;
	int[] id = null;
	byte[] classes = null;
	byte[][] termMatrix = null;
	Double[] MI = null;
	boolean[] featV = null;

	void readInvIndex(String filename) {
		Scanner in = null;
		try {
			in = new Scanner(new File(filename));
			String[] terms = in.nextLine().split(" ");
			numberOfDocs = Integer.parseInt(terms[0]);
			dictionarySize = Integer.parseInt(terms[1]);
			//numberOfPostings = Integer.parseInt(terms[2]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	void readOutput(String filename) {
		Scanner in = null; int i = 0;
		dictionary = new String[dictionarySize];
		termMatrix = new byte[numberOfDocs][dictionarySize];
		pmids = new String[numberOfDocs];
		id = new int[numberOfDocs];
		classes = new byte[numberOfDocs];
		String[] terms = null;
		try {
			in = new Scanner(new File(filename));
			while (i < dictionarySize) {
				terms = in.nextLine().split(" ");
				dictionary[i++] = terms[0];
			}
			i = 0;
			while (in.hasNext()) {
				terms = in.nextLine().split(",");
				pmids[i] = terms[0];
				id[i] = i;
				for (int j = 0; j < dictionarySize; j++) {
					termMatrix[i][j] = Byte.parseByte(terms[j+1]);
					//System.out.println("this is the value: " + termMatrix[i][j]);
				}
				classes[i++] = Byte.parseByte(terms[dictionarySize+1]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		checkSizes();
	}

	void checkSizes() {
		if (dictionarySize != dictionary.length) System.err.println("Dictionary sizes do not match");
		if (pmids.length != numberOfDocs) System.err.println("Lenght of pmids array does not match number of tweets");
		if (classes.length != numberOfDocs) System.err.println("Class array size does not match number of tweets");
		if (termMatrix.length != numberOfDocs || termMatrix[0].length != dictionarySize)
			System.err.println("Matrix dimensions do not match");
	}


	void mutInfo() {
		double pi = 0.0; double thetajc = 0.0; double thetaj = 0.0;
		MI = new Double[dictionarySize];
		featV = new boolean[dictionarySize];
		for (int i = 0; i < dictionarySize; i++) MI[i] = 0.0;
		for (int a = 0; a < dictionarySize; a++) {	
			for (int k = 0; k < K; k++) {
				pi = 0.0; thetajc = 0.0; thetaj = 0.0;
				for (int i = 0; i < numberOfDocs; i++) {
					if (classes[i] == (byte) k) {
						pi++;
					}
					if (classes[i] == (byte) k && termMatrix[i][a] > (byte) 0) thetajc++;
					if (termMatrix[i][a] > (byte) 0) thetaj++;
				}
				thetajc /= pi; pi /= numberOfDocs; thetaj /= numberOfDocs;
				if (thetajc > 0) 
					MI[a] += thetajc * pi * (Math.log(thetajc * 1.0) - Math.log(thetaj * 1.0)) + ((1 - thetajc) * 1.0) * pi *
					(Math.log((1 - thetajc) * 1.0) - Math.log((1 - thetaj) * 1.0));
				else MI[a] += 0.0;
			}
		}
		Integer[] indexes = rankFeatures(MI);
		featV = selTopFeatures(indexes, featV);
		boolean check = checkFeatArr(featV);
		if (check) System.err.println("Boolean Feature Selection Array Incorrect");
		@SuppressWarnings("unused")
		InvIndSecRnd invIndex2 = null;
		invIndex2 = new InvIndSecRnd(numberOfDocs, dictionarySize, featV, termMatrix, dictionary, classes, pmids);
		//int ind = 0;
		//for (Double mi: MI) System.out.println("Mut Info Results: " + mi + " " + featV[ind++]);
	}

	Integer[] rankFeatures(Double[] mutInfoArr) {
		ArrayIndexComparator comparator = new ArrayIndexComparator(mutInfoArr);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		return indexes;
	}

	boolean[] selTopFeatures(Integer[] indArr, boolean[] featInd) {
		int gap = dictionarySize - topK; int counter = 0;
		if (gap > 0) {
			for (Integer i: indArr) {
				if (counter < gap) featInd[i] = false;
				else featInd[i] = true;
				counter++;
			}
		} else {
			System.err.println("Negative or zero difference between feature size and top selected features");
		}
		return featInd;
	}

	boolean checkFeatArr(boolean[] featVal) {
		boolean a = false; int counter = 0;
		if (featVal.length != dictionarySize) a = true;
		for (boolean i: featVal) if (i == true) counter++;
		if (counter != topK) {
			System.err.println("topK features not selected");
			a = true;
		}
		return a;
	}
}
