package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Clustering {

	/* Create an object of Clustering */
	private static Clustering instance = new Clustering();

	/* Make the constructor private so that this class cannot be instantiated */
	private Clustering(){}

	/* Get the only object available */
	public static Clustering getInstance() {
		return instance;
	}	

	static final int K = 5;
	int numberOfDocs = 0;
	int dictionarySize = 0;
	int numberOfPostings = 0;
	double log10NumberOfDocs = 0;
	short[] postings = null;
	int[] pointersToPostings = null;
	double[] docLengths = null;
	byte[] clusterAssignment = null;
	double[][] centroids = null;
	short[] tfs = null;
	LargeDoubleMatrix cosSim = null;	
	float[][] cosSim1 = null;
	int[] medoids = new int[K];
	int[] clusterSizes = new int[K];
	String[] pmids = null;

	void readIndex(String filename) {
		Scanner in = null;
		try {
			in = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			System.err.println(filename + "not found");
			System.exit(1);
		}
		String[] terms = null;
		terms = in.nextLine().split(" ");
		numberOfDocs = (Integer.parseInt(terms[0]));
		dictionarySize = Integer.parseInt(terms[1]);
		numberOfPostings = Integer.parseInt(terms[2]);
		log10NumberOfDocs = Math.log(1.0 * numberOfDocs);
		postings = new short[numberOfPostings];
		pointersToPostings = new int[dictionarySize + 1];
		docLengths = new double[15000];
		clusterAssignment = new byte[numberOfDocs];
		centroids = new double[K][dictionarySize];
		tfs = new short[numberOfPostings];
		cosSim1 = new float[numberOfDocs][numberOfDocs];
		pmids = new String[numberOfDocs];
		for (int i = 0; i < numberOfDocs; i++) {
			terms = in.nextLine().split(" ");
			pmids[i] = terms[0];
		}
		System.out.println("pmids: " + pmids[numberOfDocs - 1]);
		int k = 0;
		for (int i = 0; i < dictionarySize; i++) {
			pointersToPostings[i] = k;
			terms = in.nextLine().split(" ");
			//System.out.println(terms[0] + " ");
			int len = terms.length / 2;
			//System.out.println(len + " ");
			for (int j = 0; j < len; j++) {

				postings[k] = Short.parseShort(terms[2 * j + 1]);
				tfs[k++] = Short.parseShort(terms[2 * j + 2]);
			}
		}
		//System.out.println("this is out" + " " + i);
		in.close();
		pointersToPostings[dictionarySize] = k;
	}

	void precompute(){  // precompute docLengths for all docs
		for (int i = 0; i < numberOfDocs; i++) docLengths[i] = 0; 
		for (int j = 0; j < dictionarySize; j++) {
			int lo = pointersToPostings[j];
			int hi = pointersToPostings[j + 1];
			double idf = log10NumberOfDocs - Math.log10((hi - lo) * 1.0);
			for (int k = lo; k < hi; k++) {
				double w = (1.0 + Math.log10(tfs[k])) * idf;
				//System.out.println("these are postings: " + postings[k]);
				docLengths[postings[k]] += w * w;
			}
		}
		for (int i = 0; i < numberOfDocs; i++) docLengths[i] = Math.sqrt(docLengths[i]);
	}

	void pairwise() {
		double idf = 0.0;
		//cosSim = new LargeDoubleMatrix("ldm.test", numberOfDocs, numberOfDocs);
		for (int i = 0; i < numberOfDocs; i++)
			for (int j = 0; j < numberOfDocs; j++) cosSim1[i][j] = (float) 0.0;
		int lo = 0; int hi = 0;
		for (int i = 0; i < dictionarySize; i++) {
			lo = hi; hi = pointersToPostings[i + 1];
			int df = hi - lo;
			if (df > 0) idf = log10NumberOfDocs - Math.log10(df * 1.0);
			else idf = 0.0;
			double idf2 = idf * idf;
			for (int j = lo; j < hi; j++) {
				double wp = (1 + Math.log10(tfs[j]));
				for (int k = j + 1; k < hi; k++) {
					cosSim1[postings[j]][postings[k]] += (float) (wp * (1 + Math.log10(tfs[k])) * idf2);
				}
			}
		}
		//cosSim.close();
		for (int i = 0; i < numberOfDocs; i++){
			cosSim1[i][i] = (float) 1.0;
			for (int j = i + 1; j < numberOfDocs; j++){
				cosSim1[i][j] /= (docLengths[i] * docLengths[j]);
				cosSim1[j][i] = cosSim1[i][j];
			}
		}
		//System.out.println("cosSim1: " + cosSim1[numberOfDocs-1][numberOfDocs-1]);
	}


	void initialSeeds(){
		Random random = new Random();
		for (byte k = 0; k < K; k++) medoids[k] = random.nextInt(numberOfDocs);
	}

	boolean partition(){  // returns true if different from existing cluster assignment
		for (byte k = 0; k < K; k++) clusterSizes[k] = 0;
		boolean changed = false;
		for (int i = 0; i < numberOfDocs; i++){
			double maxSim = cosSim1[i][medoids[clusterAssignment[i]]];  
			for (byte k = 0; k < K; k++) if (cosSim1[i][medoids[k]] > maxSim){
				changed = true;
				maxSim = cosSim1[i][medoids[k]];
				clusterAssignment[i] = k;
			}
			clusterSizes[clusterAssignment[i]]++;
		}
		return changed;
	}

	void computeCentroids(){
		for (byte k = 0; k < K; k++)
			for (int j = 0; j < dictionarySize; j++) centroids[k][j] = 0;
		int lo = 0; int hi = 0; 
		for (int i = 0; i < dictionarySize; i++){
			lo = hi; hi = pointersToPostings[i + 1];
			int df = hi - lo;
			double idf = log10NumberOfDocs - Math.log10(df * 1.0);
			for (int j = lo; j < hi; j++)
				centroids[clusterAssignment[postings[j]]][i] += 1 + Math.log10(tfs[j]) * idf;
		}
		for (byte k = 0; k < K; k++)
			for (int j = 0; j < dictionarySize; j++) centroids[k][j] /= clusterSizes[k];
	}

	boolean kMeansPartition() {  // returns true if different from existing cluster assignment
		for (byte k = 0; k < K; k++) clusterSizes[k] = 0;
		double[][] sim2Centroids = new double[numberOfDocs][K];
		for (int i = 0; i < numberOfDocs; i++)
			for (byte k = 0; k < K; k++) sim2Centroids[i][k] = 0;
		int lo = 0; int hi = 0; 
		for (int i = 0; i < dictionarySize; i++){
			lo = hi; hi = pointersToPostings[i + 1];
			int df = hi - lo;
			double idf = log10NumberOfDocs - Math.log10(df * 1.0);
			for (int j = lo; j < hi; j++) {
				for (byte k = 0; k < K; k++) {
					sim2Centroids[postings[j]][k] += 
					centroids[k][i] * (1 + Math.log10(tfs[j]) * idf);
					//System.out.print(sim2Centroids[postings[j]][k] + " ");
					//System.out.print(k + " ");
				}
			//System.out.println();
			}
		}
		boolean changed = false;
		for (int i = 0; i < numberOfDocs; i++){
			double maxSim = sim2Centroids[i][clusterAssignment[i]];
			for (byte k = 0; k < K; k++) 
				if (sim2Centroids[i][k] > maxSim){
					maxSim = sim2Centroids[i][k];
					clusterAssignment[i] = k;
					changed = true;
					//System.out.println("This is maxSim: " + maxSim);
				}
			clusterSizes[clusterAssignment[i]]++;
		}
		return changed;
	}

	void kmeans(){  // k-means clustering
		initialSeeds();
		partition();
		computeCentroids();
		while (kMeansPartition()){
			computeCentroids();
			for (byte k = 0; k < K; k++) System.err.print(clusterSizes[k] + " ");
			System.err.println();  // reporting cluster sizes to stderr
		}
	}

	void outputClusterAssignment(String output){  // write cluster assignment to output file
		try {
			FileWriter writer = new FileWriter(output);
			for (int i = 0; i < numberOfDocs; i++) {
				//System.out.print(clusterAssignment[i] + " ");
				writer.append(clusterAssignment[i] + " ");
				writer.append('\n');
			}
			//System.out.println();
			writer.flush();
			writer.close();
		}	catch (IOException e){
			e.printStackTrace();
		}
	}
}
