package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeSet;
/**
 * Methods to parse the *.arff file and print a term-incidence matrix text file and an inverted index text file.
 * @author James D'Amato & Britney Gill
 *
 */
public class Parser {

	/* Create an object of Parser */
	private static Parser instance = new Parser();

	/* Make the constructor private so that this class cannot be instantiated */
	private Parser(){}

	/* Get the only object available */
	public static Parser getInstance() {
		return instance;
	}

	/* Global variables */
	int dictionarySize = 387; /* Number of terms */
	int newDictSize = 0; /* New number of terms after filtering */
	int classSize = 5; /* Number of classes */
	int numberOfDocs = 26834; /* Number of original Tweets */
	int newNumberOfDocs = 0;  /* Number of Tweets after filtering */
	int numberOfPostings = 0; /* Number of postings in inverted index */
	String[] dictionary = null;
	String[] location = null;
	String[] pmids = null;
	String[] dictionary1 = null;
	int[] docs = null;
	boolean[] termV = null;
	boolean[] docV = null;
	int[][] termMatrix = null;
	int[] pointersToPostings = null;

	/* Terms that provide no mathematical meaning */
	static final String[] stopList = new String[]{
		"a", "aint", "all", "also", "among", "an", "and", "are", "as", "at", "be", "been", 
		"between", "both", "bout", "but", "by", "can", "da", "damn", "dat", "ers", "for", "from", "get", "goin", "got", "had", "has", "have",
		"haha", "hahaha", "he", "her", "here", "how", "however", "httpbitlyyadyk", "httpgawkercom", "httptinychatcom", "i", "id", "ii", "im", "ima", "in", "into", "is", "it", "its", "jus", "just", "lol",
		"lmao", "lmaoo", "may", "me", "more", "most", "my", "n", "ng", "no", "non", "not", "of", "off", "on", "one", "only", "or",
		"other", "our", "rt", "so", "such", "th", "tha", "than", "that", "the", "their", "them", "then",
		"there", "therefore", "these", "they", "this", "those", "three", "thus", "to", 
		"two", "u", "ur", "via", "was", "we", "were", "when", "whether", "which", "while", "who",
		"will", "wit", "with", "xo", "xox", "xoxo", "ya", "yea","yo", "you", "yp"};

	HashSet<String> stopwords = new HashSet<String>(25);

	TreeSet<Incidence> incidences = new TreeSet<Incidence>();

	/**
	 * Method which makes a hashset of the stop words
	 */
	void makeStopSet(){
		for (String s: stopList) stopwords.add(s);
	}

	boolean checker(String s) {
		if (s.length() > 1 && !stopwords.contains(s) && s.charAt(0) > '9') {
			newDictSize++;
			return true;
		} else return false;
	}

	/**
	 * Reads in the *.arff file and creates data structures
	 * @param filename
	 */
	void readArff(String filename)	{
		Scanner in = null;
		try {
			in = new Scanner(new File(filename));
		} catch (FileNotFoundException e)	{
			System.err.println(filename + "not found");
			System.exit(1);
		}
		dictionary = new String[dictionarySize];
		location = new String[classSize];
		pmids = new String[numberOfDocs];
		docs = new int[dictionarySize];

		String[] terms = in.nextLine().split(" ");
		termV = new boolean[dictionarySize];
		/* Put all of the terms into the dictionary array */
		for (int i = 0; i < dictionarySize; i++)	{
			terms = in.nextLine().split(" ");
			dictionary[i] = terms[1];
			termV[i] = checker(terms[1]);
		}
		newDictSize++;
		termMatrix = new int[numberOfDocs][dictionarySize];
		terms = in.nextLine().split(" ");
		location[0] = terms[2];
		terms = location[0].split(",");

		/* All location classes in the location array */
		for (int i = 0; i < classSize; i++) {
			location[i] = terms[i];
		}

		/* Delete unnecessary braces on class strings */
		StringBuilder sb = new StringBuilder(location[0]);
		sb.deleteCharAt(0);
		location[0] = sb.toString();
		StringBuilder sb1 = new StringBuilder(location[4]);
		sb1.deleteCharAt(2);
		location[4] = sb1.toString();
		terms = in.nextLine().split(" ");

		/* Iterate through tweets and create array of pmids and the term matrix with the number of incidences
		 * for that specific row/column, where the row represents the pmid index and the column is the term 
		 * index. 
		 */
		int j = 0; int k = 1;
		//int i = 1;
		//for (Incidence i: incidences)
		while (in.hasNext())	{
			terms = in.nextLine().split(",");
			pmids[j] = terms[0];
			termMatrix[j][0] = j;
			k = 1;
			for (int i = 0; i < dictionarySize; i++)	{
				if (i > 0) {
					termMatrix[j][i] = Integer.parseInt(terms[i]);
					//System.out.println("terms0: " + terms[i]);
					//System.out.println("termV: " + dictionary[i] + "  terms: " + terms[i]);
					k++;
					//System.out.println("this is k: " + k);
				}
			}
			//System.out.println();
			//System.out.println();
			j++;
		}
		in.close();
		//System.out.println("new dict size: " + newDictSize);
	}

	void printTMatrix(String filename) {
		newNumberOfDocs = 0;
		docV = new boolean[numberOfDocs]; int counter = 0;
		dictionary1 = new String[dictionarySize];
		int i = 0; int flag = 0; int b = 0;
		try {
			FileWriter writer = new FileWriter(filename);
			while (i < numberOfDocs) {
				flag = 0;
				for (int j = 0; j < dictionarySize; j++) {
					if (termMatrix[i][j] > 0 && termV[j] && j > 0) { 
						flag = 1; 
					}
				}
				if (flag > 0) {
					//writer.append(b + ","); counter = 0;
					for (int k = 0; k < dictionarySize; k++) {
						if (termV[k] && k > 0) {
							//writer.append(Integer.toString(termMatrix[i][k]));
							//writer.append(",");
							//System.out.println("term: " + dictionary[k]);
							dictionary1[k] = dictionary[k]; counter++;
						}
						//System.out.println("new dictionary: " + dictionary1[k]);
						//System.out.println("this is k: " + counter);
					}


					//writer.append(Integer.toString(termMatrix[i][newDictSize - 1]));	
					
				}
				if (flag > 0) {
					docV[i] = true;
					newNumberOfDocs++;
					termMatrix[i][0] = b++;;
					//writer.append('\n');
				} else { docV[i] = false; }
				i++; 
			}

			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("new # of docs: " + newNumberOfDocs);
	}

	void invertIndex(String filename, String output, String output1) {
		Scanner in = null; newDictSize = 221; numberOfPostings = 8590; newNumberOfDocs = 7005;
		String[] newDictionary = null; int d = 0; //newNumberOfDocs = 0; 
		newDictionary = new String[dictionarySize];
		try {
			in = new Scanner(new File(filename));

			while (d < numberOfDocs) {
				if (docV[d]) {
				//terms = in.nextLine().split(","); 
				for (int i = 0; i < dictionarySize; i++) {
					//System.out.println(terms.length);
					if (i > 0 && termMatrix[d][i] > 0 && termV[i]) {
						//System.out.println("this is i: " + i);
						incidences.add(new Incidence(dictionary[i], termMatrix[d][0], termMatrix[d][i]));
						newDictionary[i] = dictionary[i]; 
						//System.out.println("this is counter: " + counter);
					}
				}
				}
				//newNumberOfDocs++;
				d++;
			}
			in.close();
			FileWriter writer = new FileWriter(output);
			FileWriter writer1 = new FileWriter(output1);
			String curTerm = "";
			writer.append(newNumberOfDocs + " " + newDictSize + " " + numberOfPostings + '\n');
			for (int a = 0; a < numberOfDocs; a++) {
				if (docV[a]) writer.append(pmids[a] + '\n');
			}
			for (Incidence i: incidences) {
				if (!i.term.equals(curTerm)) {
					if (!curTerm.equals("")) writer.append('\n');
					writer.append(i.term); newDictSize++;
					curTerm = i.term;
				}
				writer.append(" " + Integer.toString(i.docNum)); numberOfPostings++;
				writer.append(" " + Integer.toString(i.termNum));
			}
			System.out.println("postings: " + numberOfPostings);
			writer.append('\n');
			writer.flush();
			writer.close();
			for (int a = 0; a < dictionarySize; a++) if (newDictionary[a] != null) {
				writer1.append(newDictionary[a] + '\n');
			}
			writer1.flush();
			writer1.close();
			FileWriter writer2 = new FileWriter("term-inc-matrix.txt");
			int i = 0; int b = 0;
			while (i < numberOfDocs) {
				if (docV[i]) {
					writer2.append(b + ",");
					for (int k = 0; k < dictionarySize; k++) {
						//System.out.println("dictionary1: " + dictionary1[k]);
						if (newDictionary[k] != null && k > 0) {
							writer2.append(Integer.toString(termMatrix[i][k]));
							writer2.append(",");
						}
					}
					b++;
					writer2.append('\n');
				}
				i++;
			}
			writer2.flush();
			writer2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
