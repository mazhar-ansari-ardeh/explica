package ica;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

import problem.Problem;
import static ica.ICAUtils.*;


/**
 * This class contains the ICA algorithm methods
 */
public class ICAlgorithm
{

	private Problem problem;
	
	/**
	 *  Number of initial countries
	 */
	int numOfCountries = 80;               		
	
	/**
	 * Number of initial imperialists
	 */
	int numOfInitialImperialists = 8;      		
	
	/**
	 * Number of decades (generations)
	 */
	int numOfDecades = 2500;					
	
	/**
	 * In the original paper assimilation coefficient is shown by "beta"
	 */
	double assimilationCoefficient = 2;        	
	
	/**
	 * In the original paper assimilation angle coefficient is shown by "gama"
	 */
	double assimilationAngleCoefficient = .5; 	
	
	/**
	 * Determines the percent to which the cost of the colonies can affect the cost of the entire empire as in:
	 * Total Cost of Empire = Cost of Imperialist + Zeta * mean(Cost of All Colonies)
	 */
	double zeta = 0.02;
	
	/* It is used to update the revolution rate.*/
	
	/**
	 * Use "true" to stop the algorithm when just one empire is remaining. Use "false" to continue the algorithm
	 */
	boolean stopIfJustOneEmpire = false;
	
	/**
	 * The percent of search space size, which enables the uniting process of two empires
	 */
	double unitingThreshold = 0.02;

	/**
	 * Logs steps of ICA.  
	 */
	private PrintWriter logger;

	Random r = new Random(System.currentTimeMillis());
	
	/**
	 * List of Empires of this instance of the ICA algorithm
	 */
	Empire[] empiresList = new Empire[numOfInitialImperialists];

	/**
	 * The best found position for each decade
	 */
	double[] bestDecadePosition; 				
	
	/**
	 * The overall minimum cost of all decades
	 */
	double minimumCost;
	
	/**
	 * The search space size (between the min and max bounds), an interval: searchSpaceSize[i] = maxBounds[i] - minBounds[i]
	 */
	double[] searchSpaceSize;

	/**
	 * Creates an instance of the ICAlgorithm to solve a give problem. Initial population of 
	 * colonies will be created randomly. 
	 * @param pr a Problem object that this instance of ICAlgorithm will be applied to. 
	 * @param logFilePath the path to the location on which log file will be saved. If given null
	 * or empty string, current directory will be used. 
	 * @param logFileName the name of log file. If given null or empty string, name of the Problem 
	 * object and its dimension will be used.  
	 */
	public ICAlgorithm(Problem pr, String logFilePath, String logFileName) 
	{
		this.problem = pr;
		int problemDimension = this.problem.getDimension();
		this.bestDecadePosition = new double[problemDimension];
		this.searchSpaceSize = new double[problemDimension];
		double[] maxBounds = problem.getUpperBounds(), minBounds = problem.getLowerBounds();
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

		if(logFilePath == null || logFilePath.trim().isEmpty())
			logFilePath = ".";
		File logDirectory = new File(logFilePath);
		if(!logDirectory.exists())
			logDirectory.mkdirs();
		try 
		{
			if(logFileName == null || logFileName.trim().isEmpty())
				logger = new PrintWriter(new FileOutputStream(logFilePath + File.separator + pr.getName() + "-" + pr.getDimension() + ".log"));
			else
			{
				logger = new PrintWriter(new FileOutputStream(logFilePath + File.separator + logFileName + ".log"));
			}
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates an instance of the ICAlgorithm to solve a give problem. Initial population of 
	 * colonies are received as a parameter. 
	 * @param pr a Problem object that this instance of ICAlgorithm will be applied to. 
	 * @param logFilePath the path to the location on which log file will be saved. If given null
	 * or empty string, current directory will be used. 
	 * @param logFileName the name of log file. If given null or empty string, name of the Problem 
	 * object and its dimension will be used.  
	 * @param initialPopulation the initial population of colonies. 
	 * @param numOfEmpires the number of initial empires that should be selected from the initial 
	 * population of colonies.  
	 */
	public ICAlgorithm(Problem pr, String logFilePath, String logFileName, double [][] initialPopulation, int numOfEmpires) 
	{
		if(initialPopulation == null || initialPopulation.length == 0)
			throw new IllegalArgumentException("Initial population cannot be null.");
		if(numOfEmpires <= 0)
			throw new IllegalArgumentException("Number of empires cannot be negative or zero");
		
		this.problem = pr;
		int problemDimension = this.problem.getDimension();
		this.bestDecadePosition = new double[problemDimension];
		this.searchSpaceSize = new double[problemDimension];
		double[] maxBounds = problem.getUpperBounds(), minBounds = problem.getLowerBounds();
		for(int i=0; i<problemDimension; i++)
		{
			searchSpaceSize[i] = maxBounds[i] - minBounds[i];
		}

		this.numOfCountries = initialPopulation.length;
		this.numOfInitialImperialists = numOfEmpires;
		// Create an initial population of individuals (countries)
		// double[][] initialCountries = generateNewCountries(numOfCountries);
						
		// Compute the cost of each country: the lesser the cost, the more powerful the country is
		double[] initialCosts = getCountriesCosts(initialPopulation);
		minimumCost = initialCosts[0];

		// Sort the costs and the corresponding countries in ascending order. The best countries will be in higher places.
		sortArray(initialCosts, initialPopulation);
						
		createInitialEmpires(initialPopulation, initialCosts);

		if(logFilePath == null || logFilePath.trim().isEmpty())
			logFilePath = ".";
		File logDirectory = new File(logFilePath);
		if(!logDirectory.exists())
			logDirectory.mkdirs();
		try 
		{
			if(logFileName == null || logFileName.trim().isEmpty())
				logger = new PrintWriter(new FileOutputStream(logFilePath + File.separator + pr.getName() + "-" + pr.getDimension() + ".csv"));
			else
			{
				logger = new PrintWriter(new FileOutputStream(logFilePath + File.separator + logFileName + ".csv"));
			}
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
		for(int decade=0; decade < numOfDecades; decade++)
		{
			for (int i=0; i < empiresList.length; i++)
			{
				empiresList[i].asssimilateColonies(assimilationCoefficient, r);
				empiresList[i].revolveColonies(r);
				empiresList[i].updateTotalCost();				
				//empiresList[i].dispatchExplorers6(computePower(i), decade, r);
				possesEmpire(empiresList[i]);
				
				empiresList[i].updateTotalCost();
			}

			uniteSimilarEmpires();
			
			imperialisticCompetition(decade);
			
			// If the user wants it, the algorithm can stop when only one empire remains
			if (empiresList.length == 1 && stopIfJustOneEmpire)
			{
				System.out.println("Finished at decade = " + decade);
				break;
			}

			updateInternalStates(decade);
//			logger.append(decade + ",	" + Arrays.toString(this.bestDecadePosition));
			logger.append(this.minimumCost + "\n");
			logger.flush();
		}

//		System.out.println("Best solution: " + Arrays.toString(bestDecadePosition));
//		System.out.println("Best fitness: " + minimumCost + "\nNumber of empires: " + empiresList.length);
//		System.out.println("Value: " + this.minimumCost);
		
		logger.flush();
		return bestDecadePosition;
	}
	
	/**
	 * Runs the modified version of the algorithm and saves the results of each iteration of
	 * the algorithm to the PrintWriter object that is given to the constructor.
	 * @return the best optimum solution that is found.
	 */
	protected double[] runEICA()
	{
		for(int decade=0; decade < numOfDecades; decade++)
		{
			for (int i=0; i < empiresList.length; i++)
			{
				empiresList[i].asssimilateColonies(assimilationCoefficient, r);
				empiresList[i].revolveColonies(r);
				empiresList[i].updateTotalCost();				
				empiresList[i].dispatchExplorers6(computePower(i), decade, r);
				possesEmpire(empiresList[i]);
				
				empiresList[i].updateTotalCost();
			}

			uniteSimilarEmpires();
			
			imperialisticCompetition(decade);
			
			// If the user wants it, the algorithm can stop when only one empire remains
			if (empiresList.length == 1 && stopIfJustOneEmpire)
			{
				System.out.println("Finished at decade = " + decade);
				break;
			}

			updateInternalStates(decade);
//			logger.append(decade + ",	" + Arrays.toString(this.bestDecadePosition));
			logger.append( this.minimumCost + "\n");
		}

//		System.out.println("Best solution: " + Arrays.toString(bestDecadePosition));
//		System.out.println("Best fitness: " + minimumCost + "\nNumber of empires: " + empiresList.length);
//		System.out.println("Value: " + this.minimumCost);
		
		logger.flush();
		return bestDecadePosition;
	}
	
	double computePower(int index)
	{
		double maxCost = empiresList[0].getTotalCost();
		double sumOfPower = 0;
		
		for(int i = 0; i < empiresList.length; i++)
		{
			if(maxCost < empiresList[i].getTotalCost())
				maxCost = empiresList[i].getTotalCost();
		}
		double[] colonyPowers = new double[empiresList.length];
		
		if(maxCost > 0)
			for(int i = 0; i < empiresList.length; i++)
			{
				colonyPowers[i] = 1.3 * maxCost - empiresList[i].getTotalCost();
				sumOfPower += colonyPowers[i];
			}
		else
			for(int i = 0; i < empiresList.length; i++)
			{
				colonyPowers[i] = 0.7 * maxCost - empiresList[i].getTotalCost();
				sumOfPower += colonyPowers[i];
			}
		
		return (colonyPowers[index] / sumOfPower);
	}

	private void updateInternalStates(int decade)
	{
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
		for(int i = 0; i < countriesArray.length; i++)
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
	 * Generates the initial empires
	 */
	private void createInitialEmpires(double[][] initialCountries, double[] initialCosts)
	{
		int numOfAllColonies = numOfCountries - numOfInitialImperialists;

		// Extract the best countries to create empires
		double[][] allImperialistsPosition = extractArrayRange(initialCountries, 0, numOfInitialImperialists);

		// Extract their costs
		double[] allImperialistsCost = new double[numOfInitialImperialists];
		System.arraycopy(initialCosts, 0, allImperialistsCost, 0, numOfInitialImperialists);

		// Extract the rest to create colonies
		double[][] allColoniesPosition = extractArrayRange(initialCountries, numOfInitialImperialists, initialCountries.length);

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
			//System.out.println(allImperialistsPower[i]);
			allImperialistNumOfColonies[i] = (int) Math.round(allImperialistsPower[i] / getSum(allImperialistsPower) * numOfAllColonies);
			if(allImperialistNumOfColonies[i] < 0)
				allImperialistNumOfColonies[i] = 0;
		}
		allImperialistNumOfColonies[allImperialistNumOfColonies.length-1] = 
			Math.max(
					numOfAllColonies - 
					getSum(Arrays.copyOfRange(allImperialistNumOfColonies, 0, allImperialistNumOfColonies.length-1)), 0);

		// Initialize the empires
		for(int i=0; i<numOfInitialImperialists; i++)
		{
			empiresList[i] = new Empire(zeta, problem);
		}
		
		// Create a random permutation of integers
		int[] randomIndex = randperm(numOfAllColonies, r);
		
		// Create the empires and attribute them their colonies
		for(int i = 0; i < numOfInitialImperialists; i++)
		{
			int[] R = Arrays.copyOfRange(randomIndex, 0, allImperialistNumOfColonies[i]);
			//empiresList[i].init(R.length);
			//System.out.println(this.numOfCountries);
			//System.out.println(allImperialistNumOfColonies[i] + " " + randomIndex.length);
			randomIndex = Arrays.copyOfRange(randomIndex, allImperialistNumOfColonies[i], randomIndex.length);
			
			empiresList[i].setImperialistPosition(allImperialistsPosition[i]);
			empiresList[i].setColoniesPosition(extractGivenArrayParts(allColoniesPosition, R));
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
	private void uniteSimilarEmpires()
	{
		// Get the threshold distance between two empires
		double thresholdDistance = unitingThreshold * getNorm(searchSpaceSize);
		
		// Get the number of empires
		int numOfEmpires = empiresList.length;

		// Compare each empire with the other ones
		for(int i=0; i < (numOfEmpires-1); i++)
		{
			for(int j=i+1; j<numOfEmpires; j++)
			{
				// Compute the distance between the two empires i and j
				double[] distanceVector = new double[empiresList[i].getImperialistPosition().length];
				for(int k=0; k<empiresList[i].getImperialistPosition().length; k++)
				{
					distanceVector[k] = empiresList[i].getImperialistPosition()[k] - empiresList[j].getImperialistPosition()[k];
				}
				double distance = getNorm(distanceVector);

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
					empiresList[i].updateTotalCost();

					deleteAnEmpire(worseEmpireInd);

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
	
		for(int i2=i+1; i2<newSize; i2++)
		{
			newColoniesPosition[i2] = empiresList[worseEmpireInd].getColoniesPosition()[i2-empiresList[betterEmpireInd].getColoniesPosition().length-1];
		}

		// Return the array with the updated positions
		return newColoniesPosition;
	}

	/**
	 * Runs the competition between empires
	 */
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
			possessionProbability[i] = totalPowers[i] / getSum(totalPowers);
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
			//System.out.println("An empire deleted at decade = " + decade);
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
		
		// Subtract to each element of this vector the corresponding 
		// value of the probability vector
		double[] dVector = new double[probability.length];
		for(int i=0; i<probability.length; i++)
		{
			dVector[i] = probability[i] - randVector[i];
		}
		
		// Return the index of the maximum value of the vector
		return getMaxIndex(dVector);
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
