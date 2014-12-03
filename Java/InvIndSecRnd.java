package project;

public class InvIndSecRnd extends InvertIndex {
	
	private static int numberOfDocs;
	private static int dictionarySize;
	private static boolean[] featV = null;
	private static byte[][] termMatrix = null;
	private static String[] dictionary = null;
	private static byte[] classes = null;
	private static String[] pmid = null;

	InvIndSecRnd(int numDocs, int dictSize, boolean[] featValid, byte[][] tMatrix,
			String[] dict, byte[] clses, String[] pmIDs) {
		super(InvIndexType.SECOND, numDocs, dictSize, featValid, tMatrix, dict, clses, pmIDs);
		numberOfDocs = super.getNumDocs();
		dictionarySize = super.getDictionarySize();
		featV = super.getFeatureValidVector();
		termMatrix = super.getTermMatrix();
		dictionary = super.getDictionary();
		classes = super.getClassVector();
		pmid = super.getPmidVector();
		boolean[] docValid = validateDocs(featV, dictionarySize, numberOfDocs, termMatrix);
		int newDictSize= computeNewDictSize(featV, dictionarySize);
		int newNoDocs = computeNewNoDocs(featV, docValid, dictionarySize, numberOfDocs, termMatrix);
		byte[][] newTMatrix = adjustTMatrix(featV, docValid, dictionarySize, numberOfDocs, termMatrix, newDictSize, newNoDocs);
		String[] newDictionary = adjustDictionary(featV, dictionarySize, dictionary, newDictSize);
		int numPostings = computeNoPostings(newDictSize, newNoDocs, newTMatrix);
		byte[] newClasses = adjustClassVector(classes, docValid, numberOfDocs, newNoDocs);
		String[] newPMIDs = adjustPMIDVector(docValid, numberOfDocs, newNoDocs, pmid);
		printTermIncMatrix("output1.txt", newDictSize, newNoDocs, newDictionary, newTMatrix, newPMIDs, newClasses);
		printInvIndex("invertedIndex1.txt", newDictSize, newNoDocs, numPostings, newDictionary, newTMatrix, newPMIDs);
		printMatlabDataset("matlabInput.txt", newDictSize, newNoDocs, newTMatrix, newClasses);
	}
}
