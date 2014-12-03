package project;

import java.io.FileWriter;
import java.io.IOException;

public abstract class InvertIndex {
	
	/* Gloabl Class Variables */
	private InvIndexType round = null;
	private int numberOfDocs;
	private int dictionarySize;
	private boolean[] featV = null;
	private byte[][] termMatrix = null;
	private String[] dictionary = null;
	private byte[] classes = null;
	private String[] pmid = null;

	/* Pass variables to class via constructor */
	public InvertIndex(InvIndexType round, int numDocs, int dictSize, boolean[] featValid, 
			byte[][] tMatrix, String[] dict, byte[] clses, String[] pmIDs) {
		this.round  = round;
		this.numberOfDocs = numDocs;
		this.dictionarySize = dictSize;
		this.featV = featValid;
		this.termMatrix = tMatrix;
		this.dictionary = dict;
		this.classes = clses;
		this.pmid = pmIDs;
	}
	
	public InvIndexType getRound() {
		return round;
	}
	
	public int getNumDocs() {
		return numberOfDocs;
	}
	
	public int getDictionarySize() {
		return dictionarySize;
	}
	
	public boolean[] getFeatureValidVector() {
		return featV;
	}
	
	public byte[][] getTermMatrix() {
		return termMatrix;
	}
	
	public String[] getDictionary() {
		return dictionary;
	}
	
	public byte[] getClassVector() {
		return classes;
	}
	
	public String[] getPmidVector() {
		return pmid;
	}
	
	protected void setVars(InvIndexType round, int numDocs, int dictSize, boolean[] featValid, 
			byte[][] tMatrix, String[] dict, byte[] clses, String[] pmIDs) {
		this.round = round;
		this.numberOfDocs = numDocs;
		this.dictionarySize = dictSize;
		this.featV = featValid;
		this.termMatrix = tMatrix;
		this.dictionary = dict;
		this.classes = clses;
		this.pmid = pmIDs;
	}
	

	protected boolean[] validateDocs(boolean[] ftVaRR, int dictSize, int numDocs, byte[][] tMatrix) {
		boolean[] docV = null;
		docV = new boolean[numDocs]; int i = 0;
		while (i < numDocs) {
			for (int j = 0; j < dictSize; j++) {
				if (ftVaRR[j] && tMatrix[i][j] > (byte) 0) docV[i] = true;
			}
			i++;
		}

		return docV;
	}
	
	protected int computeNewDictSize(boolean[] ftVaRR, int oldDictSize) {
		int newDictSize = 0;
		for (boolean a: ftVaRR) if (a) newDictSize++;
		return newDictSize;
	}

	protected int computeNewNoDocs(boolean[] ftVaRR, boolean[] docV, int oldDictSize, int oldNumDocs, byte[][] tMatrix) {
		int newNumDocs = 0; int i = 0; boolean flag = false;
		while (i < oldNumDocs) {
			flag = false;
			if (docV[i]) {
				for (int j = 0; j < oldDictSize; j++) {
					if (ftVaRR[j] && tMatrix[i][j] > (byte) 0) flag = true;
				}
			}
			i++;
			if (flag) newNumDocs++;
		}
		return newNumDocs;
	}
	
	protected byte[] adjustClassVector(byte[] clses, boolean[] docV, int oldNumDocs, int newNumDocs) {
		byte[] newClassVector = null;
		newClassVector = new byte[newNumDocs];
		int k = 0;
		for (int i = 0; i < oldNumDocs; i++) if (docV[i]) {
			newClassVector[k++] = clses[i];
		}
		return newClassVector;
	}
	
	protected String[] adjustPMIDVector(boolean[] docV, int oldNumDocs, int newNumDocs, String[] IDs) {
		String[] pmID = null;
		pmID = new String[newNumDocs];
		int k = 0;
		for (int i = 0; i < oldNumDocs; i++) if (docV[i]) {
			pmID[k++] = IDs[i];
		}
		return pmID;
	}

	protected byte[][] adjustTMatrix(boolean[] ftVaRR, boolean[] docV, int dictSize, int numDocs, byte[][] tMatrix, int newDictSize, int newNumDocs) {
		byte[][] reducedTMatrix = null;
		reducedTMatrix = new byte[newNumDocs][newDictSize];
		System.out.println("new num docs: " + newNumDocs + " " + newDictSize);
		int i = 0; int m = 0; int n = 0;
		while (i < numDocs) {
			if (docV[i]) {
				n = 0;
				for (int j = 0; j < dictSize; j++) {
					if (ftVaRR[j]) reducedTMatrix[m][n++] = tMatrix[i][j];
				}
				m++;
			}
			i++;
		}
		return reducedTMatrix;
	}
	
	protected String[] adjustDictionary(boolean[] ftVaRR, int dictSize, String[] dict, int newDictSize) {
		String[] newDictionary = null;
		newDictionary = new String[newDictSize];
		int k = 0;
		for (int i = 0; i < dictSize; i++) if (ftVaRR[i]) newDictionary[k++] = dict[i];
		return newDictionary;
	}
	
	int computeNoPostings(int dictSize, int numDocs, byte[][] tMatrix) {
		int numberOfPostings = 0; 
		for (int j = 0; j < dictSize; j++) {
			int i = 0;
			while (i < numDocs) {
				if (tMatrix[i][j] > (byte) 0) {
					numberOfPostings++;
					System.out.println("value: " + tMatrix[i][j] + " postings: " + numberOfPostings);
				}
				i++;
			}
		}
		return numberOfPostings;
	}

	protected void printTermIncMatrix(String output, int dictSize, int numDocs, String[] dict, byte[][] tMatrix, String[] ids, byte[] cls) {
		try {
			int j = 0;
			FileWriter writer = new FileWriter(output);
			for (int i = 0; i < dictSize; i++) writer.append(dict[i] + '\n');
			while (j < numDocs) {
				writer.append(ids[j] + " ");
				for (int k = 0; k < dictSize; k++) writer.append(tMatrix[j][k] + " ");
				writer.append(Byte.toString(cls[j++]) + '\n');
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void printInvIndex(String output, int dictSize, int numDocs, int numPostings, String[] dict, byte[][] tMatrix, String[] ids) {
		try {
			FileWriter writer = new FileWriter(output);
			writer.append(numDocs + " " + dictSize + " " + numPostings + '\n');
			for (int j = 0; j < numDocs; j++) writer.append(ids[j] + '\n');
			for (int j = 0; j < dictSize; j++) {
				int i = 0;
				writer.append(dict[j] + " ");
				while (i < numDocs) {
					if (tMatrix[i][j] > (byte) 0) writer.append(i + " " + tMatrix[i][j] + " ");
					i++;
				}
				writer.append('\n');
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void printMatlabDataset(String output, int dictSize, int numDocs, byte[][] tMatrix, byte[] cls) {
		try {
			FileWriter writer = new FileWriter(output);
			int i = 0;
			while (i < numDocs) {
				for (int j = 0; j < dictSize; j++) {
					writer.append(tMatrix[i][j] + " ");
				}
				writer.append(Byte.toString(cls[i++]) + '\n');
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
