package project;

@SuppressWarnings("rawtypes")
public class Incidence implements Comparable{

	//Class variables term and doc
	String term; int docNum; int termNum;

	/**
	 * Superclass for Incidence
	 */
	public Incidence(String t, int d, int tN){
		term = t; docNum = d; termNum = tN;
	}

	/* 
	 * Incidence implements comparable interface, containing the compareTo method.
	 * compareTo will order the objects lexicographically.
	 */
	public int compareTo(Object obj){
		Incidence i = (Incidence)obj;
		int diff = term.compareTo(i.term);
		if (diff == 0) diff = docNum - i.docNum;
		return diff;
	}
}
