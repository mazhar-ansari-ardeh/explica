/*	
 * Copyright 2011, Robin Roche
 * This file is part of jica.

    jica is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jica is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with jica.  If not, see <http://www.gnu.org/licenses/>.
*/
package ica;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import problem.Problem;


/**
 * This class contains the ICA algorithm methods
 * @author Robin Roche
 */
public class OriginalICAlgorithm
{

	private Problem problem;
	int numOfCountries = 80;               		// Number of initial countries
	int numOfInitialImperialists = 8;      		// Number of initial imperialists
	int numOfDecades = 2500;					// Number of decades (generations)
	double revolutionRate = 0.1;               	// Revolution is the process in which the socio-political characteristics of a country change suddenly
	double assimilationCoefficient = 2;        	// In the original paper assimilation coefficient is shown by "beta"
	double assimilationAngleCoefficient = .5; 	// In the original paper assimilation angle coefficient is shown by "gama"
	/**
	 * Determines the percent to which the cost of the colonies can affect the cost of the entire empire as in:
	 * Total Cost of Empire = Cost of Imperialist + Zeta * mean(Cost of All Colonies)
	 */
	double zeta = 0.02;
	/**
	 * It is used to update the revolution rate.
	 */
	double dampRatio = 0.99;					// The damp ratio
	boolean stopIfJustOneEmpire = false;        // Use "true" to stop the algorithm when just one empire is remaining. Use "false" to continue the algorithm
	double unitingThreshold = 0.02;           	// The percent of search space size, which enables the uniting process of two empires

	static PrintWriter pw;

	Random r = new Random(System.currentTimeMillis());
	Empire[] empiresList = new Empire[numOfInitialImperialists];	// List of Empires
	// The initial countries with their positions
	//private double[] initialCosts;						// The costs of the initial countries
	double[] bestDecadePosition; 				// The best found position for each decade
	/**
	 * The overall minimum cost of all decades
	 */
	double minimumCost;	// The overall minimum cost
	/**
	 * The mean cost of each decade
	 */
	//private double[] meanCost = new double[numOfDecades];		// The mean cost of each decade
	/**
	 * The search space size (between the min and max bounds), an interval: searchSpaceSize[i] = maxBounds[i] - minBounds[i]
	 */
	double[] searchSpaceSize;					// The search space size (between the min and max bounds)
	ICAUtils utils = new ICAUtils();			// A class with useful methods for array operations

	/**
	 * Constructor of the class
	 * @param pr the problem to be optimized
	 */
	public OriginalICAlgorithm(Problem pr) 
	{
		this.problem = pr;
		init();
		try {
			pw = new PrintWriter(new FileOutputStream("log.txt"));
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * Runs the ICA algorithm
	 * @return the best found solution
	 */
	protected double[] runICA()
	{
		// While no stopping condition is met
		for(int decade=0; decade<numOfDecades; decade++)
		{
			// Update the revolution rate
			revolutionRate = dampRatio * revolutionRate;
			
			// For each empire
			for (int i=0; i<empiresList.length; i++)
			{
				empiresList[i].asssimilateColonies(assimilationCoefficient, r);

				empiresList[i].revolveColonies(r);
				
				empiresList[i].updateTotalCost();
				
				//empiresList[i].dispatchExplorers6(computePower(i), decade, r);

				possesEmpire(empiresList[i]);
				
				// Update the empire's total cost
				//empiresList[i].setTotalCost( empiresList[i].getImperialistCost() + zeta * utils.getMean(empiresList[i].getColoniesCost()) );
				empiresList[i].updateTotalCost();
				
				//System.out.println("Empire's power: " + computePower(i));
			}

			//System.out.println();
			uniteSimilarEmpires();
			
			imperialisticCompetition(decade);
			
			// If the user wants it, the algorithm can stop when only one empire remains
			if (empiresList.length == 1 && stopIfJustOneEmpire)
			{
				System.out.println("Finished at decade = " + decade);
				break;
			}

			// Extract and save results of the round
			saveResults(decade);
		}
		pw.flush();

		// Return results of the optimization
		System.out.println("Best solution: " + Arrays.toString(bestDecadePosition));
		System.out.println("Best fitness: " + minimumCost + "\nNumber of empires: " + empiresList.length);
		
		return bestDecadePosition;
	}
	
//	double computePower(int index)
//	{
//		double maxCost = empiresList[0].getTotalCost();
//		double sumOfPower = 0;
//		
//		for(int i = 0; i < empiresList.length; i++)
//		{
//			if(maxCost < empiresList[i].getTotalCost())
//				maxCost = empiresList[i].getTotalCost();
//		}
//		double[] colonyPowers = new double[empiresList.length];
//		
//		if(maxCost > 0)
//			for(int i = 0; i < empiresList.length; i++)
//			{
//				colonyPowers[i] = 1.3 * maxCost - empiresList[i].getTotalCost();
//				sumOfPower += colonyPowers[i];
//			}
//		else
//			for(int i = 0; i < empiresList.length; i++)
//			{
//				colonyPowers[i] = 0.7 * maxCost - empiresList[i].getTotalCost();
//				sumOfPower += colonyPowers[i];
//			}
//		
//		return (colonyPowers[index] / sumOfPower);
//	}

	private void saveResults(int decade)
	{
		// Extract and save results of the round
		double[] imperialistCosts = new double[empiresList.length];
		for(int i=0; i<empiresList.length ; i++)
		{
			imperialistCosts[i] = empiresList[i].getImperialistCost();
		}
		
		int minIndex = ICAUtils.getMinIndex(imperialistCosts);
		if(minimumCost > imperialistCosts[minIndex])
		{
			minimumCost = imperialistCosts[minIndex];
			bestDecadePosition = empiresList[minIndex].getImperialistPosition();
		}
		
		//meanCost[decade] = utils.getMean(imperialistCosts);
	}


	/** Generates new countries
	 * @param numberOfCountries the number of countries to generate
	 * @param dimension the dimension of each country
	 * @param minVector the minimum values for each dimension 
	 * @param maxVector the maximum values for each dimension
	 * @param rand a random number generator
	 * @return a matrix that each row contains a point which represents a new country, generated randomly.
	 */
	private double[][] generateNewCountries(int numberOfCountries) 
	{
		int problemDimension = this.problem.getDimension();
		double[][] countriesArray = new double[numberOfCountries][problemDimension];  
		for(int i=0; i<numberOfCountries; i++)
		{
			for(int j=0; j<problemDimension; j++)
			{
				countriesArray[i][j] = (this.problem.getUpperBounds()[j] - this.problem.getLowerBounds()[j]) * r.nextDouble() + this.problem.getLowerBounds()[j];
			}
		}
		return countriesArray;
	}


	/**
	 * Returns a vector with the cost of all countries computed according to their position
	 * @param numberOfCountries the number of countries
	 * @param countriesArray
	 * @return a vector with the costs of all countries
	 */
	private double[] getCountriesCosts(double[][] countriesArray) 
	{
		double[] costsVector = new double[countriesArray.length];
		for(int i=0; i<countriesArray.length; i++)
		{
			costsVector[i] = getCountryCost(countriesArray[i]);
		}
		return costsVector;
	}

	

	/**
	 * Returns the cost of one country
	 * @param country the country
	 * @return the cost
	 */
	private double getCountryCost(double[] country) 
	{
		return problem.valueAt(country);
	}


	/**
	 * Sorts an array according to its values and sorts another array in the same order
	 * The lowest value is put first
	 * @param arrayToSort the array to sort according to its values
	 * @param matchingArray the array that should be sorted according to the first one
	 */
	private void sortArray(final double[] arrayToSort, double[][] matchingArray) 
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

	private void init()
	{
		// Initialize variables
				int problemDimension = this.problem.getDimension();
				bestDecadePosition = new double[problemDimension];
				searchSpaceSize = new double[problemDimension];
				
				double[] maxBounds = problem.getUpperBounds(), minBounds = problem.getLowerBounds();
				// Compute the problem search space, between the min and max bounds
				for(int i=0; i<problemDimension; i++)
				{
					searchSpaceSize[i] = maxBounds[i] - minBounds[i];
				}

				// Create an initial population of individuals (countries)
				double[][] initialCountries = generateNewCountries(numOfCountries);
				
				// Compute the cost of each country: the lesser the cost, the more powerful the country is
				double[] initialCosts = getCountriesCosts(initialCountries);
				minimumCost = initialCosts[0];

				// Sort the costs and the corresponding countries in ascending order. The best countries will be in higher places.
				sortArray(initialCosts, initialCountries);
				
				createInitialEmpires(initialCountries, initialCosts);
	}
	
	/**
	 * Generates the initial empires
	 */
	@SuppressWarnings("static-access")
	private void createInitialEmpires(double[][] initialCountries, double[] initialCosts)
	{
		int numOfAllColonies = numOfCountries - numOfInitialImperialists;

		// Extract the best countries to create empires
		double[][] allImperialistsPosition = utils.extractArrayRange(initialCountries, 0, numOfInitialImperialists);

		// Extract their costs
		double[] allImperialistsCost = new double[numOfInitialImperialists];
		System.arraycopy(initialCosts, 0, allImperialistsCost, 0, numOfInitialImperialists);

		// Extract the rest to create colonies
		double[][] allColoniesPosition = utils.extractArrayRange(initialCountries, numOfInitialImperialists, initialCountries.length);

		// Extract their costs
		double[] allColoniesCost = new double[initialCosts.length-numOfInitialImperialists]; 
		System.arraycopy(initialCosts, numOfInitialImperialists, allColoniesCost, 0, initialCosts.length-numOfInitialImperialists);	

		// Compute the power of imperialists
		double[] allImperialistsPower = new double[numOfInitialImperialists];
		if(allImperialistsCost[ICAUtils.getMaxIndex(allImperialistsCost)]>0)
		{
			for(int i=0; i<allImperialistsCost.length; i++)
			{
				allImperialistsPower[i] = 1.3 * allImperialistsCost[ICAUtils.getMaxIndex(allImperialistsCost)] - allImperialistsCost[i];
			}
		}
		else
		{
			for(int i=0; i<allImperialistsCost.length; i++)
			{
				allImperialistsPower[i] = 0.7 * allImperialistsCost[ICAUtils.getMaxIndex(allImperialistsCost)] - allImperialistsCost[i];
			}
		}
		
		// Set the number of colonies for the imperialists 
		int[] allImperialistNumOfColonies = new int[numOfInitialImperialists];
		for(int i=0; i<allImperialistsPower.length; i++)
		{
			allImperialistNumOfColonies[i] = (int) Math.round(allImperialistsPower[i]/utils.getSum(allImperialistsPower) * numOfAllColonies);
		}
		allImperialistNumOfColonies[allImperialistNumOfColonies.length-1] = 
			Math.max(
					numOfAllColonies - 
					utils.getSum(Arrays.copyOfRange(allImperialistNumOfColonies, 0, allImperialistNumOfColonies.length-1)),
					0
					);

		// Initialize the empires
		for(int i=0; i<numOfInitialImperialists; i++)
		{
			empiresList[i] = new Empire(zeta, problem);
		}
		
		// Create a random permutation of integers
		int[] randomIndex = utils.randperm(numOfAllColonies, r);
		
		// Create the empires and attribute them their colonies
		for(int i=0; i<numOfInitialImperialists; i++)
		{
			int[] R = Arrays.copyOfRange(randomIndex, 0, allImperialistNumOfColonies[i]);
			randomIndex = Arrays.copyOfRange(randomIndex, allImperialistNumOfColonies[i], randomIndex.length);
			
			empiresList[i].setImperialistPosition(allImperialistsPosition[i]);
			empiresList[i].setColoniesPosition(utils.extractGivenArrayParts(allColoniesPosition, R));
			//empiresList[i].setColoniesCost(utils.extractGivenArrayParts(allColoniesCost, R));
			//empiresList[i].setTotalCost(empiresList[i].getImperialistCost() + zeta * utils.getMean(empiresList[i].getColoniesCost()));
			empiresList[i].updateTotalCost();
		}

		// If an empire has no colony, give it one
		for(int i=0; i<empiresList.length; i++)
		{
			if(empiresList[i].getColoniesPosition().length == 0)
			{
				empiresList[i].setColoniesPosition(generateNewCountries(1));
				empiresList[i].updateTotalCost();
				//empiresList[i].setColoniesCost(getCountriesCosts(empiresList[i].getColoniesPosition()));
			}
		}

	}

	/**
	 * Can make a colony become the imperialist 
	 * if it is more powerful than the imperialist.
	 * @param theEmpire
	 */
	private void possesEmpire(Empire theEmpire)
	{
		// Get the costs of the colonies
		double[] coloniesCost = theEmpire.getColoniesCost();

		// Get the cost of the best colony (the lowest cost)
		int bestColonyInd = ICAUtils.getMinIndex(coloniesCost);
		double minColoniesCost = coloniesCost[bestColonyInd]; 

		// If this cost is lower than the one of the imperialist
		if(minColoniesCost < theEmpire.getImperialistCost())
		{
			// Backup the position and cost of the former imperialist
			double[] oldImperialistPosition = theEmpire.getImperialistPosition();

			// Update the position and cost of the imperialist with the ones of the colony
			theEmpire.setImperialistPosition(theEmpire.getColoniesPosition()[bestColonyInd]);

			// Update the position and cost of the former colony with the ones of the former imperialist
			theEmpire.setColonyPosition(bestColonyInd, oldImperialistPosition);
			theEmpire.updateTotalCost();
		}
	}



	/**
	 * Unites imperialists that are close to each other
	 */
	@SuppressWarnings("static-access")
	private void uniteSimilarEmpires()
	{
		// Get the threshold distance between two empires
		double thresholdDistance = unitingThreshold * utils.getNorm(searchSpaceSize);
		
		// Get the number of empires
		int numOfEmpires = empiresList.length;

		// Compare each empire with the other ones
		for(int i=0; i<(numOfEmpires-1); i++)
		{
			for(int j=i+1; j<numOfEmpires; j++)
			{
				// Compute the distance between the two empires i and j
				double[] distanceVector = new double[empiresList[i].getImperialistPosition().length];
				for(int k=0; k<empiresList[i].getImperialistPosition().length; k++)
				{
					distanceVector[k] = empiresList[i].getImperialistPosition()[k] - empiresList[j].getImperialistPosition()[k];
				}
				double distance = utils.getNorm(distanceVector);

				// If the empires are too close
				if(distance<=thresholdDistance)
				{
					// Get the best and worst empires of the two
					int betterEmpireInd;
					int worseEmpireInd;
					if(empiresList[i].getImperialistCost() < empiresList[j].getImperialistCost())
					{
						betterEmpireInd=i;
						worseEmpireInd=j;
					}
					else
					{
						betterEmpireInd=j;
						worseEmpireInd=i;
					}

					// Update the positions
					double[][] newColoniesPosition = getColonyPositionsOfUnitedEmpire(betterEmpireInd, worseEmpireInd);
					empiresList[betterEmpireInd].setColoniesPosition(newColoniesPosition);

					// Update the total cost of the united empire                                     
//					empiresList[betterEmpireInd].setTotalCost(
//							empiresList[betterEmpireInd].getImperialistCost() +
//							zeta * utils.getMean(empiresList[betterEmpireInd].getColoniesCost())
//					);
					
					empiresList[i].updateTotalCost();

					// Update the empires list
					deleteAnEmpire(worseEmpireInd);
					//System.out.println("New number of empires: " + empiresList.length);

					return;
				}
			}
		}  
	}

	/**
	 * Returns the positions of the colonies of the united empire (after two empires merge) 
	 * @param betterEmpireInd the best empire
	 * @param worseEmpireInd the worst empire
	 * @return the corresponding colony positions
	 */
	private double[][] getColonyPositionsOfUnitedEmpire(int betterEmpireInd, int worseEmpireInd) 
	{
		int problemDimension = this.problem.getDimension();
		// Get the new number of colonies of the united empire
		int newSize = 
			empiresList[betterEmpireInd].getColoniesPosition().length + 
			1 + 
			empiresList[worseEmpireInd].getColoniesPosition().length;
		
		// Create a new array to store the positions of the colonies
		double[][] newColoniesPosition = new double[newSize][problemDimension];

		// At first, store the positions of the colonies of the best empire in the array
		int i;
		for(i=0; i<empiresList[betterEmpireInd].getColoniesPosition().length; i++)
		{
			newColoniesPosition[i] = empiresList[betterEmpireInd].getColoniesPosition()[i];
		}
		
		// Then add the position of the former worst imperialist
		newColoniesPosition[i] = empiresList[worseEmpireInd].getImperialistPosition();
		
		// Finally, add the costs of the colonies of the worst empire
		int i2;	
		for(i2=i+1; i2<newSize; i2++)
		{
			newColoniesPosition[i2] = empiresList[worseEmpireInd].getColoniesPosition()[i2-empiresList[betterEmpireInd].getColoniesPosition().length-1];
		}

		// Return the array with the updated positions
		return newColoniesPosition;
	}



	/**
	 * Runs the competition between empires
	 */
	@SuppressWarnings("static-access")
	private void imperialisticCompetition(int decade)
	{
		
		// Generate a random number, and return if this number is too high
		double rand = r.nextDouble();
		if(rand > .11)
		{
			return;
		}
		
		// Idem if their is only one empire
		if(empiresList.length<=1)
		{
			return;
		}

		// Get the total cost of each empire
		double[] totalCosts = new double[empiresList.length]; 
		for(int i=0; i<empiresList.length; i++)
		{
			totalCosts[i] = empiresList[i].getTotalCost();
		}
		
		// Get the weakest empire (the one with the highest cost) and its cost
		int weakestEmpireInd = ICAUtils.getMaxIndex(totalCosts);
		double maxTotalCost = totalCosts[weakestEmpireInd]; 
		
		// Get the power of each empire
		double[] totalPowers = new double[empiresList.length];
		for(int i=0; i<empiresList.length; i++)
		{
			totalPowers[i] = maxTotalCost - totalCosts[i];
		}

		// Get the possession probability of each empire
		double[] possessionProbability = new double[empiresList.length];
		for(int i=0; i<empiresList.length; i++)
		{
			possessionProbability[i] = totalPowers[i] / utils.getSum(totalPowers);
		}
		
		// Select an empire according to their probabilities
		int selectedEmpireInd = selectAnEmpire(possessionProbability);
		
		// Generate a random integer
		int numOfColoniesOfWeakestEmpire = empiresList[weakestEmpireInd].getNumberOfColonies();
		int indexOfSelectedColony = r.nextInt(numOfColoniesOfWeakestEmpire);

		// Update the positions of the colonies of the selected empire 
		// by adding the position of the randomly selected colony of the weakest empire
		empiresList[selectedEmpireInd].setColoniesPosition(	
				concatenatePositions(
						empiresList[selectedEmpireInd].getColoniesPosition(), 
						empiresList[weakestEmpireInd].getColoniesPosition()[indexOfSelectedColony]
				)
		);
		empiresList[selectedEmpireInd].updateTotalCost();

		// Update the positions of the colonies of the weakest empire 
		// by removing the position of the randomly selected colony of the empire
		empiresList[weakestEmpireInd].removeColony(indexOfSelectedColony);

		// Get the number of colonies of the weakest empire
		numOfColoniesOfWeakestEmpire = empiresList[weakestEmpireInd].getNumberOfColonies();
		
		// If it has not more than 1 colony, then make it disappear/collapse
		// It is then absorbed by the selected empire
		if(numOfColoniesOfWeakestEmpire<=1)
		{
			// Update the positions of the colonies by adding the collapsed imperialist
			empiresList[selectedEmpireInd].setColoniesPosition( 
					concatenatePositions(
							empiresList[selectedEmpireInd].getColoniesPosition(), 
							empiresList[weakestEmpireInd].getImperialistPosition()
					) 
			);
			
			// Erase the collapsed empire from the empires list
			deleteAnEmpire(weakestEmpireInd);
			System.out.println("An empire deleted at decade = " + decade);
		}
		else
			empiresList[weakestEmpireInd].updateTotalCost();

	}

	/**
	 * Concatenates the positions of an empire with an additional one
	 * @param positions1 the positions array of the empire
	 * @param position2 the position to add
	 * @return the updated positions
	 */
	private double[][] concatenatePositions(double[][] positions1, double[] position2)
	{
		// Create a new array to store the updated positions 
		double[][] newPositions = new double[positions1.length+1][positions1[0].length];

		// Add the positions of the existing empire in the array
		int i;
		for(i=0; i<positions1.length; i++)
		{
			newPositions[i] = positions1[i];
		}

		// Then add the position to add at the end
		newPositions[i] = position2;

		// Return the updated positions
		return newPositions;
	}

	
	/**
	 * Deletes an empire from the empires list
	 * @param indexToDelete
	 */
	private void deleteAnEmpire(int indexToDelete)
	{
		// Split the empires list into two sub lists, before and after the empire to remove
		Empire[] empiresList1 = Arrays.copyOfRange(empiresList, 0, indexToDelete);
		Empire[] empiresList2 = Arrays.copyOfRange(empiresList, indexToDelete+1, empiresList.length);

		// Create a new list with the updated size
		empiresList = new Empire[empiresList1.length+empiresList2.length];
		
		// Copy the empires of the sub lists into the new one
		for(int n=0; n<(empiresList1.length + empiresList2.length); n++)
		{
			if(n<empiresList1.length)
			{
				empiresList[n] = empiresList1[n];
			}

			if(n>= empiresList1.length)
			{
				empiresList[n] = empiresList2[n-empiresList1.length];
			}
		}
	}


	/**
	 * Selects an empire according to their probabilities 
	 * @param probability the probability vector
	 * @return the selected empire index
	 */
	private int selectAnEmpire(double[] probability)
	{
		// Create a vector of random numbers
		double[] randVector = new double[probability.length];
		for(int i=0; i<probability.length; i++)
		{
			randVector[i] = r.nextDouble();
		}
		
		// Substract to each element of this vector the corresponding 
		// value of the probability vector
		double[] dVector = new double[probability.length];
		for(int i=0; i<probability.length; i++)
		{
			dVector[i] = probability[i] - randVector[i];
		}
		
		// Return the index of the maximum value of the vector
		return ICAUtils.getMaxIndex(dVector);
	}

	/**
	 * Returns a string with information about the algorithm
	 * @return the string
	 */
	public String getDetails()
	{
		return "Imperialist Competitive Algorithm (ICA): " + 
		"as described in: Atashpaz-Gargari, E. and Lucas, C., Imperialist Competitive Algorithm: An Algorithm for Optimization Inspired by Imperialistic Competition, IEEE Congress on Evolutionary Computation, 2007, pp. 4661-4667." +
		" Adapted from: http://www.mathworks.com/matlabcentral/fileexchange/22046-imperialist-competitive-algorithm-ica";
	}

	/**
	 * Returns a string with the name of the algorithm
	 * @return the string
	 */
	public String getName() 
	{
		return "Imperialist Competitive Algorithm";
	}
}
