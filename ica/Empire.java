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

import java.util.Arrays;
import java.util.Random;

import problem.Problem;

import static ica.ICAUtils.*;

/**
 * Class creating the empire type, with its imperialists, colonies 
 * and their respective positions and costs
 * @author Robin Roche
 */
class Empire
{
	// Empire variables
	private int problemDimension;
	private double[] imperialistPosition = new double[problemDimension];
	private double imperialistCost;
	private double[][] coloniesPosition;
	private double[] coloniesCost;
	private double totalCost;
	private double zeta;
	private int inoc = -1;
	private double dampRatio = 0.99;
	private double[] lowerBounds;
	private double[] upperBounds;
	private Problem problem;
	private double revolutionRate = 0.1;
	
	/**
	 * Constructor
	 * @param problemDimension
	 */
	public Empire(double zeta, Problem problem)
	{
		this.problem = problem;
		this.problemDimension = this.problem.getDimension();
		this.lowerBounds = problem.getLowerBounds();
		this.upperBounds = problem.getUpperBounds();
		this.zeta = zeta;
	}
	
	public Empire(double zeta, Problem problem, double revolutionRate, double dampRatio)
	{
		this(zeta, problem);
		this.revolutionRate = revolutionRate;
		this.dampRatio = dampRatio;
	}
	
	// Best answer so far
	public void dispatchExplorers6(double power, int decade, Random R)
	{
		int noe = (int)(power * problem.getDimension());
		if(noe == 0)
			noe = 1;
		
		double[] newSite, bestSite = findNewSiteImperialist();
		double bestSiteCost = problem.valueAt(bestSite);
		
		for(;noe > 1; noe--)
		{
			newSite = findNewSiteImperialist();
			if(problem.valueAt(newSite) < bestSiteCost)
			{
				bestSite = newSite;
				bestSiteCost = problem.valueAt(newSite);
			}
		}
		
		if(bestSiteCost < imperialistCost)
		{
			double r = ( (double)Math.random()*32767 / ((double)(32767)+(double)(1)) );
		    int ctbr = (int)(r * getNumberOfColonies()); // colony to be replaced
		    double[] x = coloniesPosition[ctbr];
			setColonyPosition(ctbr, imperialistPosition);
			imperialistPosition = bestSite;
			imperialistCost = bestSiteCost;
			r = Math.random();
			if( r < power * Math.pow(inoc / (double)(getNumberOfColonies()), 2))/*Math.random() < Math.exp(-1 * power * getNumberOfColonies() ) && Math.random() < .03)*/
			{	
				coloniesPosition = ICAUtils.add(coloniesPosition, x);
				coloniesCost = ICAUtils.add(coloniesCost, problem.valueAt(x));
			}
			//System.out.println("Decade: " + decade + ". Best site.");
			
			updateTotalCost();
		}
	}

	private double[] findNewSiteImperialist()
	{
		double r = ((double) Math.random()*32767 / ((double)(32767)+(double)(1)) );
		int param2change = (int)(r * problemDimension);
		
		r = (   (double)Math.random()*32767 / ((double)(32767)+(double)(1)) );
	    int neighbour=(int)(r * getNumberOfColonies());
	     
	    double[] site = new double[this.problemDimension];
	    System.arraycopy(imperialistPosition, 0, site, 0, problemDimension);
	    
	    r = (   (double)Math.random()*32767 / ((double)(32767)+(double)(1)) );
	    
	    site[param2change] = site[param2change] + (r - .5) * 2 * (site[param2change] - coloniesPosition[neighbour][param2change]);
	    if(site[param2change] > problem.getUpperBound(param2change))
	    	site[param2change] = problem.getUpperBound(param2change);
	    else if(site[param2change] < problem.getLowerBound(param2change))
	    	site[param2change] = problem.getLowerBound(param2change);
	    
	    return site;
	}
	
	public double[] getImperialistPosition() 
	{
		return imperialistPosition;
	}

	public void setImperialistPosition(double[] imperialistPosition) 
	{
		this.imperialistPosition = imperialistPosition;
		this.imperialistCost = problem.valueAt(imperialistPosition);
	}

	
	public double getImperialistCost() 
	{
		return imperialistCost;
	}
	
	public double[][] getColoniesPosition() 
	{
		return coloniesPosition;
	}

	public void setColoniesPosition(double[][] coloniesPosition) 
	{
		this.coloniesPosition = coloniesPosition;
		updateColoniesCost();
		if(inoc == -1)
			inoc = coloniesPosition.length;
	}

	public double[] getColoniesCost() 
	{
		return coloniesCost;
	}

	private void updateColoniesCost() 
	{
		if((coloniesCost == null) || (coloniesCost.length != coloniesPosition.length))
			coloniesCost = new double[coloniesPosition.length];
		
		for(int i = 0; i < coloniesCost.length; i++)
			this.coloniesCost[i] = problem.valueAt(coloniesPosition[i]);
	}

	public double getTotalCost() 
	{
		return totalCost;
	}
	
	public void updateTotalCost()
	{
		double mean = ICAUtils.getMean(coloniesCost);
		this.totalCost = imperialistCost + zeta * mean;
	}

	public void setColonyPosition(int colonyIndex, double[] position) 
	{
		this.coloniesPosition[colonyIndex] = position;
		this.coloniesCost[colonyIndex] = problem.valueAt(position);
	}

	public int getNumberOfColonies()
	{
		return this.coloniesCost.length;
	}
		
	public void asssimilateColonies(double assimilationCoefficient, Random r)
	{
		Empire theEmpire = this;

		int numOfColonies = theEmpire.getNumberOfColonies();

		// Create an array containing the distance between the imperialist and the colonies
		double[][] repmatArray = repmat(theEmpire.getImperialistPosition(), numOfColonies);
		double[][] array = new double[numOfColonies][problemDimension];
		for(int i=0; i<numOfColonies; i++)
		{
			for(int j=0; j<problemDimension; j++)
			{
				// Calculate the distance of each colony from the empire
				array[i][j] = repmatArray[i][j] - theEmpire.getColoniesPosition()[i][j];
			}
		}

		// Create a new array to store the updated colonies positions
		double[][] coloniesPosition = new double[numOfColonies][problemDimension];

		// Fill the array
		for(int i=0; i<array.length; i++)
		{
			for(int j=0; j<array[0].length; j++)
			{
				coloniesPosition[i][j] = theEmpire.getColoniesPosition()[i][j] + 2 * assimilationCoefficient * r.nextDouble() * array[i][j];
				if(coloniesPosition[i][j] < this.lowerBounds[j])
					coloniesPosition[i][j] = this.lowerBounds[j];
				if(coloniesPosition[i][j] > this.upperBounds[j])
					coloniesPosition[i][j] = this.upperBounds[j];
			}
		}
		setColoniesPosition(coloniesPosition);
	}
	
	/**
	 * Removes a position from the colony costs vector
	 * @param colonyCosts
	 * @param indexToRemove
	 * @return the updated costs vector
	 */
	private double[] removeColonyCost(int indexToRemove)
	{
		// Create a new vector to store the updated costs
		double[] newColonyCosts = new double[coloniesCost.length-1];

		// Copy in it the costs before the colony to remove
		for(int i=0; i<indexToRemove; i++)
		{
			newColonyCosts[i] = coloniesCost[i];
		}

		// Then copy the rest of the costs, without including to colony to remove
		for(int j = indexToRemove; j < newColonyCosts.length; j++)
		{
			newColonyCosts[j] = coloniesCost[j+1];
		}

		coloniesCost = newColonyCosts;
		// Return the updated costs
		return newColonyCosts;
	}
	
	/**
	 * Removes a position from the colony positions array
	 * @param colonyPositions
	 * @param indexToRemove
	 * @return the updated positions
	 */
	private double[][] removeColonyPosition(int indexToRemove)
	{
		double[][] newColonyPositions = new double[coloniesPosition.length-1][coloniesPosition[0].length];

		for(int i=0; i<indexToRemove; i++)
		{
			newColonyPositions[i] = coloniesPosition[i];
		}

		for(int j=indexToRemove; j<newColonyPositions.length; j++)
		{
			newColonyPositions[j] = coloniesPosition[j+1];
		}

		coloniesPosition = newColonyPositions;
		return newColonyPositions;
	}
	
	public void removeColony(int indexToRemove)
	{
		removeColonyPosition(indexToRemove);
		removeColonyCost(indexToRemove);
	}
	
	/**
	 * Make colonies of an empire revolve.
	 * This is equivalent to a perturbation which avoid getting stuck 
	 * into local minima.
	 * @param theEmpire to revolve
	 */
	public void revolveColonies(Random r)
	{
		revolutionRate = dampRatio * revolutionRate;
		int numOfRevolvingColonies = (int) Math.round((revolutionRate * getNumberOfColonies()));

		// Create a new array with new random positions for the revolving colonies
		double[][] revolvedPosition = this.generateNewCountries(numOfRevolvingColonies, r);

		// Generate a vector with integer values in a random order
		int[] R = randperm(getNumberOfColonies(), r);
		R = Arrays.copyOfRange(R, 0, numOfRevolvingColonies);

		// Update the positions of the revolved colonies of the empire
		for(int i=0; i<R.length; i++)
		{
			setColonyPosition(R[i], revolvedPosition[i]);
		}
	}

	private double[][] generateNewCountries(int numberOfCountries, Random r) 
	{
		double[][] countriesArray = new double[numberOfCountries][problemDimension];  
		for(int i=0; i<numberOfCountries; i++)
		{
			for(int j=0; j<problemDimension; j++)
			{
				countriesArray[i][j] = (this.upperBounds[j] - this.lowerBounds[j]) * r.nextDouble() + this.lowerBounds[j];
			}
		}
		return countriesArray;
	}

	/**
	 * Returns the total number of colonies in the empires list
	 * @param empiresList
	 * @return the number of colonies
	 */
	public static int getTotalColoniesCount(Empire[] empiresList) 
	{
		int currentNumOfColonies = 0;
		for(int i=0; i<empiresList.length; i++)
		{
			currentNumOfColonies += empiresList[i].getNumberOfColonies();
		}
		
		return currentNumOfColonies;
	}
}
