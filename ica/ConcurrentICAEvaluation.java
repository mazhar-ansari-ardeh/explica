package ica;

import ica.ICACallable;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import problem.Problem;

/**
 * A utility class that uses the multi-threading facility for running ICA optimization
 * on an optimization {@link Problem}. 
 * 
 * @author Mazhar Ansari Ardeh
 *
 */
public class ConcurrentICAEvaluation
{
	/**
	 * Receives a {@link Problem} object and performs an ICA optimization of the 
	 * received problem for a given number of runs. The function uses thread pools
	 * to boost the speed.  
	 * 
	 * @param pr a problem that its minimum will be tried to find the ICA algorithm.
	 * @param noRun number of runs of the ICA algorithm runs that will be performed 
	 * 				on the given problem. 
	 * @param useExplorers if true, the evaluation will use the modified version of
	 * 		  the ICA that exploits explorers.
	 * @return an array of length 4 that will contain the result of test runs. The
	 * first element is the best result that is achieved. The second element is the
	 * mean of all test results and the third element is the standard deviation of
	 * all test results. The last element is the index of the run that gave the 
	 * best result among all runs.
	 */
	public static double[] icaTestOnAFunction(Problem pr, int noRun, boolean useExplorers)
	{
		if(noRun <= 0)
			throw new IllegalArgumentException("Number of runs must be a positive value.");
		if(pr == null)
			throw new IllegalArgumentException("Problem cannot be null.");

		int noth = noRun / 3; // Number Of THreads that thread pool will contain.
		if(noth == 0)
			noth = 1;
		ExecutorService executor = Executors.newFixedThreadPool(noth); 
		ArrayList<Future<Double>> list = new ArrayList<Future<Double>>();
		for(int i = 0; i < noRun; i++)
		{
			String fileName = File.separator + pr.toString() + "-" + (useExplorers ? "EICA" : "ICA") + "Run " + (i + 1);
			ICACallable callable = new ICACallable(pr, fileName, useExplorers);
			Future<Double> future = executor.submit(callable);
			list.add(future);
		}

		double mean = 0;
		double best = Double.MAX_VALUE;
		double std = 0; 

		// There may be better ways to do this but this one is very easy!
		double[] icaResults = new double[noRun]; 
		double bestRunIndex = 0;
		for(int i = 0; i < noRun; i++)
		{
			try
			{
				icaResults[i] = list.get(i).get();
//				System.out.println(icaResults[i]);
				if(Math.abs(icaResults[i]) < Math.abs(best))
				{
					best = icaResults[i];
					bestRunIndex = i;
				}
				mean = mean + icaResults[i];
			} catch (InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
			}
		}
		mean /= noRun;
		std = ICAUtils.getSTD(icaResults, mean);

		double[] retval = {best, mean, std, bestRunIndex};
		executor.shutdown();
		return retval;
	}
	
	/**
	 * Receives a {@link Problem} object and performs an ICA optimization of the 
	 * received problem for a given number of runs. The function uses thread pools
	 * to boost the speed. This method does not create the initial population of 
	 * colonies and empires that the ICA algorithm uses but receives it as an
	 * argument. Hence, the number of empires that should be selected from the 
	 * given initial population is also given as an argument. 
	 * 
	 * @param pr a problem that its minimum will be tried to find the ICA algorithm.
	 * @param noRun number of runs of the ICA algorithm runs that will be performed 
	 * 				on the given problem. 
	 * @param useExplorers if true, the evaluation will use the modified version of
	 * 		  the ICA that exploits explorers.
	 * @param initialPopulation the initial population of colonies and empires that 
	 * the ICA algorithm uses
	 * @param numOfEmpires the number of empires that should be selected from the 
	 * given initial population
	 * @return an array of length 4 that will contain the result of test runs. The
	 * first element is the best result that is achieved. The second element is the
	 * mean of all test results and the third element is the standard deviation of
	 * all test results. The last element is the index of the run that gave the 
	 * best result among all runs. 
	 */
	public static double[] icaTestOnAFunction(Problem pr, int noRun, boolean useExplorers, double[][] initialPopulation, int numOfEmpires)
	{
		if(noRun <= 0)
			throw new IllegalArgumentException("Number of runs must be a positive value.");
		if(pr == null)
			throw new IllegalArgumentException("Problem cannot be null.");
		
//		System.out.println(initialPopulation.length + "\n" + initialPopulation[0].length);
		int noth = noRun / 3; // Number Of THreads that thread pool will contain.
		if(noth == 0)
			noth = 1;
		ExecutorService executor = Executors.newFixedThreadPool(noth); 
		ArrayList<Future<Double>> list = new ArrayList<Future<Double>>();
		for(int i = 0; i < noRun; i++)
		{// PSO-Sphere-Dim5-1
			String fileName = (useExplorers ? "EICA" : "ICA") + "-" + pr.getName() + "-" + "Dim" + pr.getDimension() + "-" + (i + 1);
			ICACallable callable = new ICACallable(pr, null, fileName, useExplorers, initialPopulation, numOfEmpires);
			Future<Double> future = executor.submit(callable);
			list.add(future);
		}

		double mean = 0;
		double best = Double.MAX_VALUE;
		double std = 0; 

		// There may be better ways to do this but this one is very easy!
		double[] icaResults = new double[noRun]; 
		double bestRunIndex = 0;
		for(int i = 0; i < noRun; i++)
		{
			try
			{
				icaResults[i] = list.get(i).get();
//				System.out.println(icaResults[i]);
				if(Math.abs(icaResults[i]) < Math.abs(best))
				{
					best = icaResults[i];
					bestRunIndex = i;
				}
				mean = mean + icaResults[i];
			} catch (InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
			}
		}
		mean /= noRun;
		std = ICAUtils.getSTD(icaResults, mean);

		double[] retval = {best, mean, std, bestRunIndex};
		executor.shutdown();
		return retval;
	}
	
	/**
	 * Receives a {@link Problem} object and performs an ICA optimization of the 
	 * received problem for a given number of runs. The function uses thread pools
	 * to boost the speed. This method does not create the initial population of 
	 * colonies and empires that the ICA algorithm uses but receives it as an
	 * argument. Hence, the number of empires that should be selected from the 
	 * given initial population is also given as an argument. 
	 * 
	 * @param pr a problem that its minimum will be tried to find the ICA algorithm.
	 * @param noRun number of runs of the ICA algorithm runs that will be performed 
	 * 				on the given problem. 
	 * @param useExplorers if true, the evaluation will use the modified version of
	 * 		  the ICA that exploits explorers.
	 * @param initialPopulation the initial population of colonies and empires that 
	 * the ICA algorithm uses
	 * @param numOfEmpires the number of empires that should be selected from the 
	 * given initial population
	 * @param outputDirectory the directory that outputs of the ICA algorithm will
	 * be saved to. 
	 * @return an array of length 4 that will contain the result of test runs. The
	 * first element is the best result that is achieved. The second element is the
	 * mean of all test results and the third element is the standard deviation of
	 * all test results. The last element is the index of the run that gave the 
	 * best result among all runs. 
	 */
	public static double[] icaTestOnAFunction(Problem pr, int noRun, boolean useExplorers, double[][] initialPopulation, 
											  int numOfEmpires, String outputDirectory)
	{
		if(noRun <= 0)
			throw new IllegalArgumentException("Number of runs must be a positive value.");
		if(pr == null)
			throw new IllegalArgumentException("Problem cannot be null.");
		
//		System.out.println(initialPopulation.length + "\n" + initialPopulation[0].length);
		int noth = noRun / 3; // Number Of THreads that thread pool will contain.
		if(noth == 0)
			noth = 1;
		ExecutorService executor = Executors.newFixedThreadPool(noth); 
		ArrayList<Future<Double>> list = new ArrayList<Future<Double>>();
		for(int i = 0; i < noRun; i++)
		{// PSO-Sphere-Dim5-1
			String fileName = (useExplorers ? "EICA" : "ICA") + "-" + pr.getName() + "-" + "Dim" + pr.getDimension() + "-" + (i + 1);
			ICACallable callable = new ICACallable(pr, outputDirectory, fileName, useExplorers, initialPopulation, numOfEmpires);
			Future<Double> future = executor.submit(callable);
			list.add(future);
		}

		double mean = 0;
		double best = Double.MAX_VALUE;
		double std = 0; 

		// There may be better ways to do this but this one is very easy!
		double[] icaResults = new double[noRun]; 
		double bestRunIndex = 0;
		for(int i = 0; i < noRun; i++)
		{
			try
			{
				icaResults[i] = list.get(i).get();
//				System.out.println(icaResults[i]);
				if(Math.abs(icaResults[i]) < Math.abs(best))
				{
					best = icaResults[i];
					bestRunIndex = i;
				}
				mean = mean + icaResults[i];
			} catch (InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
			}
		}
		mean /= noRun;
		std = ICAUtils.getSTD(icaResults, mean);

		double[] retval = {best, mean, std, bestRunIndex};
		executor.shutdown();
		return retval;
	}
}
