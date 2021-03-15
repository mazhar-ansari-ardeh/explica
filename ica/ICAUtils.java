package ica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.System.arraycopy;

/**
 * Contains several methods on arrays used by the ICA algorithm
 * @author Robin Roche
 */
public class ICAUtils 
{

	/**
	 * Returns the index of the max value contained in the vector
	 * @param vector values
	 * @return the index of the max value
	 */
	public static int getMaxIndex(double[] vector)
	{
		double max = Double.MIN_VALUE;
		int i;
		int bestIndex = 0;
		for(i=0; i<vector.length; i++) 
		{
			if(vector[i] > max)
			{
				max = vector[i];
				bestIndex = i;
			}
		}
		return bestIndex;
	}
	
	public static double[][] copyMatrix(double[][] matrix)
	{
		if(matrix == null)
			return null;
		
		double[][] retval = new double[matrix.length][];
		for(int i = 0; i < matrix.length; i++)
		{
			retval[i] = Arrays.copyOf(matrix[i], matrix[i].length);
		}
		
		return retval;
	}

	/**
	 * Returns the index of the min value contained in the vector
	 * @param vector values
	 * @return the index of the min value
	 */
	public static int getMinIndex(double[] vector)
	{
		double min = Double.MAX_VALUE;
		int i;
		int bestIndex = 0;
		for(i=0; i<vector.length; i++) 
		{
			if(vector[i] < min)
			{
				min = vector[i];
				bestIndex = i;
			}
		}
		return bestIndex;
	}

	/**
	 * Returns the mean value of a vector
	 * @param vector the vector
	 * @return the mean value
	 */
	public static double getMean(double[] vector) 
	{
		double sum = 0;
		for (int i = 0; i < vector.length; i++) 
			sum += vector[i];

		return sum / vector.length;
	}
	
	public static double getSTD(double[] vector, double mean)
	{
		if(vector == null || vector.length == 0)
			throw new IllegalArgumentException("Vector array cannot be null or empty");
		
		double result = 0;
		for(double x : vector)
			result = result + pow(x - mean, 2);
		
		int dividor = (vector.length == 1) ? 1 : vector.length;
		return result / dividor;
	}

	/**
	 * Returns the norm (root of sum of squares) of a vector
	 * @param vector the vector
	 * @return the norm
	 */
	public static double getNorm(double[] vector) 
	{
		double sum = 0;		
		for(int i=0; i<vector.length; i++)
			sum += pow(vector[i],2);

		return sqrt(sum);
	}
	
	public static double[][] add(double[][] destination, double[] data)
	{
		double retVal[][] = new double[destination.length + 1][destination[0].length];
		arraycopy(destination, 0, retVal, 0, destination.length);
		retVal[destination.length] = data;
		
		return retVal;
	}
	
	public static double[] add(double[] destination, double data)
	{
		double retVal[] = new double[destination.length + 1];
		arraycopy(destination, 0, retVal, 0, destination.length);
		retVal[destination.length] = data;
		
		return retVal;
	}

	/**
	 * Returns the sum of the elements on a vector
	 * @param vector the vector
	 * @return the sum
	 */
	public static double getSum(double[] vector)
	{
		double sum = 0;
		for (double i : vector) 
			sum += i;
		
		return sum;
	}

	/**
	 * Returns the sum of the elements on a vector
	 * @param vector the vector
	 * @return the sum
	 */
	public static int getSum(int[] vector)
	{
		int sum = 0;
		for (int i : vector) 
			sum += i;

		return sum;
	}

	/**
	 * Returns a vector with the n integers (from 1 to n, each appearing once) in a random order
	 * This is equivalent to the Matlab function randperm()
	 * @param n the number of values
	 * @param r the random generator
	 * @return the vector of integers
	 */
	public static int[] randperm(int n, Random r) 
	{
		ArrayList<Integer> nVector = new ArrayList<Integer>();
		for(int i=0; i<n; i++)
			nVector.add(i);

		int[] outputVector = new int[n];
		int outputIndex = 0;

		while(nVector.size() > 0)
		{
			int position = r.nextInt(nVector.size());
			outputVector[outputIndex] = nVector.get(position);
			outputIndex++;
			nVector.remove(position);
		}

		return outputVector;
	}

	/**
	 * Returns an array with a copy of a pattern, done n times
	 * This is equivalent to the Matlab function repmat()
	 * @param pattern the pattern to to repeat
	 * @param n the number of times
	 * @return the array with the copies (a n-by-length(pattern) matrix containing n copies of the pattern)
	 */
	public static double[][] repmat(double[] pattern, int n) 
	{
		double[][] outputArray = new double[n][pattern.length];
		for(int i = 0; i < n; i++)
			arraycopy(pattern, 0, outputArray[i], 0, pattern.length); //outputArray[i] = pattern;
		
		return outputArray;
	}

	/**
	 * Returns an extract of an array between two indexes (i.e. a range of it)
	 * @param array the array
	 * @param startIndex the index where to start
	 * @param endIndex the index where to stop
	 * @return the array extract (a (endIndex-startIndex)-by-(array[0].length) matrix)
	 */
	public static double[][] extractArrayRange(double[][] array, int startIndex, int endIndex)
	{
		double[][] arrayExtract = new double[endIndex-startIndex][array[0].length];
		int newIndex = 0;
		for(int i=startIndex; i<endIndex ; i++)
		{
			arrayExtract[newIndex] = array[i];
			newIndex++;
		}
		return arrayExtract;
	}

	/**
	 * Returns an extract of an array, with only selected indexes extracted
	 * @param array the array to be extracted from
	 * @param selectedIndexes the indexes to extract data from
	 * @return the array with the extracted data
	 */
	public static double[][] extractGivenArrayParts(double[][] array, int[] selectedIndexes) 
	{
		double[][] arrayExtract = new double[selectedIndexes.length][array[0].length];
		int index;
		for(int i=0; i<selectedIndexes.length; i++)
		{
			index = selectedIndexes[i];
			arrayExtract[i] = array[index];
		}
		return arrayExtract;
	}

	/**
	 * Returns an extract of an array, with only selected indexes extracted
	 * @param array the array to be extracted from
	 * @param selectedIndexes the indexes to extract data from
	 * @return the array with the extracted data
	 */
	public static double[] extractGivenArrayParts(double[] array, int[] selectedIndexes) 
	{
		double[] arrayExtract = new double[selectedIndexes.length];
		int index;
		for(int i=0; i<selectedIndexes.length; i++)
		{
			index = selectedIndexes[i];
			arrayExtract[i] = array[index];
		}
		return arrayExtract;
	}

	/**
	 * Prints the values of an array
	 * @param arrayName
	 * @param array
	 */
	public static void printArray(String arrayName, double[][] array)
	{
		for(int i=0; i<array.length; i++)
			System.out.println(arrayName + "[" + i + "]: " + Arrays.toString(array[i]));

	}

	/**
	 * Prints the characteristics of an empire
	 * @param empire
	 */
	public static void printEmpire(Empire empire, int empireIndex) 
	{
		System.out.println("Empire " + empireIndex);
		System.out.println("Number of colonies: " + empire.getNumberOfColonies());
		System.out.println("imperialistPosition: " + Arrays.toString(empire.getImperialistPosition()));
		System.out.println("imperialistCost: " + empire.getImperialistCost());
		printArray("coloniesPosition", empire.getColoniesPosition());
		System.out.println("coloniesCost: " + Arrays.toString(empire.getColoniesCost()));
		System.out.println("totalCost: " + empire.getTotalCost());
	}
	
	/**
	 * Sorts an array according to its values and sorts another array in the same order
	 * The lowest value is put first
	 * @param arrayToSort the array to sort according to its values
	 * @param matchingArray the array that should be sorted according to the first one
	 */
	public static void sortArray(final double[] arrayToSort, double[][] matchingArray) 
	{
		// Create an index array
		Integer[] sortOrder = new Integer[arrayToSort.length];
		for(int i=0; i<sortOrder.length; i++)
		{
			sortOrder[i] = i;
		}
	
		// Sort the array using the index array, thanks to a custom comparator
		Arrays.sort(sortOrder,new Comparator<Integer>() 
			{   
				public int compare(Integer a, Integer b)
				{
					double delta = arrayToSort[b]-arrayToSort[a];
					if(delta < 0) return 1;
					if(delta > 0) return -1;
					return 0;
				}
			}
		);
		
		// Create copies of the arrays
		double[] arrayToSortCopy = arrayToSort.clone();
	    double[][] matchingArrayCopy = matchingArray.clone();
	        
	    // Output the values using the sorted indexes
		for(int i=0;i<sortOrder.length;i++)
		{
			arrayToSort[i] = arrayToSortCopy[sortOrder[i]];
			matchingArray[i] = matchingArrayCopy[sortOrder[i]];
	    }
	}
}
