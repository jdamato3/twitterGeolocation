package project;

public class Project {
	
	public static void main(String[] args)	{
		
		long start = System.nanoTime();
		final long used0 = usedMemory();
		
		
		/*Parser parseObject = Parser.getInstance();
		parseObject.makeStopSet();
		parseObject.readArff("test-reduced.arff");
		parseObject.printTMatrix("term-inc-matrix.txt");
		//parseObject.printInvertedIndex("invertedIndex.txt");
		parseObject.invertIndex("term-inc-matrix.txt", "invertedIndex.txt", "newDictionary.txt");*/
		
	    /*Clustering clustObject = Clustering.getInstance();
		clustObject.readIndex("invertedIndex.txt");
		clustObject.precompute();
		clustObject.pairwise();
		clustObject.kmeans();
		clustObject.outputClusterAssignment("isr4.txt");*/
		
		/*AssembleResult rsltObj = AssembleResult.getInstance();
		rsltObj.readNewDictionary("newDictionary.txt", "invertedIndex.txt");
		rsltObj.readResults("isr4.txt");
		rsltObj.readTermMatrix("term-inc-matrix.txt", "output.txt");*/
		
		featSelection fSlctnObj = featSelection.getInstance();
		fSlctnObj.readInvIndex("invertedIndex.txt");
		fSlctnObj.readOutput("output.txt");
		fSlctnObj.mutInfo();
		
		Clustering clustObject = Clustering.getInstance();
		clustObject.readIndex("invertedIndex1.txt");
		clustObject.precompute();
		clustObject.pairwise();
		clustObject.kmeans();
		clustObject.outputClusterAssignment("reClassifiedVector.txt");
		
		AssembleResult rsltObj = AssembleResult.getInstance();
		rsltObj.rePrintOutput("reClassifiedVector.txt", "output1.txt", "invertedIndex1.txt", "output2.txt");
		
		long time = System.nanoTime() - start;
		final long used = usedMemory() - used0;
		
		if (used == 0) {
			System.err.println("You need to use -XX:-UsedTLAB to see small changes in memory usage.");
		} else {
			System.out.printf("Execution time was %,d ms, Heap used is %,d KB%n", time / 1000 / 1000, used / 1024);
		}
	}
	
	private static long usedMemory () {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

}
