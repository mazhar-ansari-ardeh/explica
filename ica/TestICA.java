
package ica;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import com.mathworks.toolbox.javabuilder.MWException;

import problem.*;

public class TestICA 
{
	static double[][] rand(int m, int n)
	{
		double retval [][] = new double[m][];
		for(int i = 0; i < m; i++)
		{
			retval[i] = new double[n];
		}
		for(int i = 0; i < m; i++)
			for(int j = 0; j < n; j++)
				retval[i][j] = Math.random();
		
		return retval;
	}
	
	/**
	 * Reads initial population from a data file and returns a two-dimensional array.
	 * It assumes that the file contains an integer value for the number of rows in 
	 * the array, an integer value for the number of columns of array and double
	 * values of the array.
	 * @param file the location of data file. 
	 * @return a two-dimensional array as an initial population. 
	 * @throws IOException if the file does not exist or cannot be accessed. 
	 */
	public static double[][] readPopFromFile(String file) throws IOException
	{
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		
		int rows = dis.readInt();
		int cols = dis.readInt();
		
		double[][] retval = new double[rows][cols];
		
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				retval[i][j] = dis.readDouble();
		
		dis.close();
		return retval;
	}
	
	public static void main(String[] args) throws MWException, IOException
	{
		//Problem pr = new SphereProblem(5, -14, 14);
		Problem pr = new PropellerProblem();
		double[][] in = readPopFromFile("D:\\initpop.data");
		
		double[] retval = ConcurrentICAEvaluation.icaTestOnAFunction(pr, 1, false, in, 8);
		//new ICAlgorithm(pr, ".", "s", in, 8);
		System.out.println(retval[0] + ", " + retval[1] + ", " + retval[2]);
		retval = ConcurrentICAEvaluation.icaTestOnAFunction(pr, 1, true);
		System.out.println(retval[0] + ", " + retval[1] + ", " + retval[2]);
		
		
		
//		PrintWriter pw = new PrintWriter(new FileOutputStream("log2.txt"));
//		fun(pr, pw);
//		
//		Problem[] benchFuncs = new Problem[] {
//				//new TridProble(5, -25, 25), new TridProblem(30, -900, 900), new TridProblem(60, -3600, 3600), new TridProblem(100, -10000, 10000)
//				//, new ZakharovProblem(5, -100, 100), new ZakharovProblem(30, -100, 100), new ZakharovProblem(60, -100, 100), new ZakharovProblem(100, -100, 100)
//				//, new SumSquares(5, -100, 100), new SumSquares(30, -100, 100), new SumSquares(60, -100, 100), new SumSquares(100, -100, 100)
//				//, new SchwefelProblem(5, -100, 100), new SchwefelProblem(30, -100, 100), new SchwefelProblem(60, -100, 100), new SchwefelProblem(100, -100, 100)
//				//, new SumOfDifferentPowers(5, -100, 100), new SumOfDifferentPowers(30, -100, 100), new SumOfDifferentPowers(60, -100, 100), new SumOfDifferentPowers(100, -100, 100)
//				new DixonPriceProblem(60, -4, 5), new PowellProblem(30, -4, 5), new PowellProblem(60, -4, 5), new PowellProblem(100, -4, 5)
//		};
//		
//		for(Problem pr2 : benchFuncs)
//		{
//			pw.println("Test results for " + pr2.getName() + " function. Dimension: " + pr2.getDimension());
//			System.out.println("Test results for " + pr2.getName() + " function. Dimension: " + pr2.getDimension());
//			fun(pr2, pw);
//			pw.flush();
//		}
	}
	
	/**
	 * This function receives a problem object and a PrintWriter object and 
	 * runs both the original ICA and the modified ICA for the given problem
	 * for 50 times.
	 * @param pr the Problem object that will be passed to ICA
	 * @param pw the PrintWriter that the overall progress of the function
	 * will be logged into.
	 */
	public static void fun(Problem pr, PrintWriter pw)
	{
		double mean = 0;
		double best = Double.MAX_VALUE;
		double std = 0; 
		int numOfRuns = 2;
		double results1[] = new double[numOfRuns];
		
		for(int i = 0; i < numOfRuns; i++)
		{
			ICAlgorithm ica = new ICAlgorithm(pr, pr.getName(), File.separator + pr.toString() + "-Run " + (i + 1));
			results1[i] = pr.valueAt(ica.runICA());
			System.out.println("Run: " + i + ". Result: " + results1[i]);
			pw.println("Run: " + i + ". Result: " + results1[i]);
			//System.out.println(Arrays.toString(ica.bestDecadePosition));
			if(results1[i] < best)
				best = results1[i];
			mean += results1[i];
		}
		mean /= numOfRuns;
		std = ICAUtils.getSTD(results1, mean);
		System.out.println("Original ICA. Mean of " + numOfRuns + "runs: " + mean + "\nSTD: " + std +"\nBest result: " + best);
		System.out.println();
		pw.println("Original ICA. Mean of " + numOfRuns + "runs: " + mean + "\nSTD: " + std +"\nBest result: " + best);
		pw.println();
		
		mean = 0;
		best = Double.MAX_VALUE; 
		std = 0;
		double[] results2 = new double[numOfRuns];
		
		for(int i = 0; i < numOfRuns; i++)
		{
			ICAlgorithm ica = new ICAlgorithm(pr, pr.getName(), pr.toString() + "-EICARun " + (i + 1));
			results2[i] = pr.valueAt(ica.runEICA());
			System.out.println("Run: " + i + ". Result: " + results2[i]);
			pw.println("Run: " + i + ". Result: " + results2[i]);
			//System.out.println(Arrays.toString(ica.bestDecadePosition));
			if(results2[i] < best)
				best = results2[i];
			mean += results2[i];
		}
		mean /= numOfRuns;
		std = ICAUtils.getSTD(results2, mean);
		System.out.print("Modified ICA. Mean of " + numOfRuns + "runs: " + mean + "\nSTD: " + std +"\nBest result: " + best);
		System.out.println("\n");
		pw.println("Modified ICA. Mean of " + numOfRuns + "runs: " + mean + "\nSTD: " + std +"\nBest result: " + best);
		pw.println();
	}
}
