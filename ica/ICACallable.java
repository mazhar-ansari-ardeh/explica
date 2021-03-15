package ica;

import java.util.concurrent.Callable;

import problem.Problem;

/**
 * An optimization task that will use the {@link ICAlgorithm} for finding the minimum of 
 * a {@link Problem}. 
 * @author Mazhar Ansari Ardeh
 *
 */
class ICACallable implements Callable<Double>
{
	private Problem pr = null;
	private String logFileName = null;
	private boolean useExplorers = false;
	private double[][] initialPopulation;
	private int numOfEmpires;
	private String path;
	
	/**
	 * Creates an instance of {@link ICACallable}. This constructor will use the original 
	 * ICA without explorers for optimizing.
	 * @param pr a {@link Problem} object that this object will run ICA optimization on it. 
	 * @param logFileName the name of the log file that the {@link ICAlgorithm} object will
	 * use for logging.
	 */
	public ICACallable(Problem pr, String logFileName)
	{
		this(pr, logFileName, false);
	}
	
	/**
	 * Creates an instance of {@link ICACallable}.
	 * @param pr a {@link Problem} object that this object will run ICA optimization on it. 
	 * @param logFileName the name of the log file that the {@link ICAlgorithm} object will
	 * use for logging.
	 * @param useExplorers if <code>true</code> the object will use the modified ICA and exploits 
	 * explorers for optimization and otherwise, ordinary the original ICA will be used.
	 */
	public ICACallable(Problem pr, String logFileName, boolean useExplorers)
	{
		if(pr == null || logFileName == null || logFileName.trim().length() == 0)
			throw new IllegalArgumentException("The given parameter cannot be null or empty.");
		
		this.pr = pr;
		this.logFileName = logFileName;
		this.useExplorers = useExplorers;
	}

	/**
	 * Creates an instance of {@link ICACallable} which receives an initial population of 
	 * colonies. 
	 * @param pr a {@link Problem} object that this object will run ICA optimization on it. 
	 * @param the location that log files will be saved to. If null or an empty string is 
	 * passed to it, a default value will be used. 
	 * @param logFileName the name of the log file that the {@link ICAlgorithm} object will
	 * use for logging.
	 * @param useExplorers if <code>true</code> the object will use the modified ICA and exploits 
	 * explorers for optimization and otherwise, ordinary the original ICA will be used.
	 * @param initialPopulation the initial population of colonies for the ICA algorithm. 
	 * @param numOfEmpires the number of empires that the ICA algorithm should select from the
	 * given initial population of colonies. 
	 */
	public ICACallable(Problem pr, String path, String fileName, boolean useExplorers, double[][] initialPopulation, int numOfEmpires)
	{
		if(pr == null || fileName == null || fileName.trim().length() == 0)
			throw new IllegalArgumentException("The given parameter cannot be null or empty.");
		if(path == null || path.trim().equals(""))
			path = (useExplorers) ? "EICASavedResults" : "ICASavedResults";
		
		this.pr = pr;
		this.logFileName = fileName;
		this.useExplorers = useExplorers;
		this.initialPopulation = ICAUtils.copyMatrix(initialPopulation);
		this.numOfEmpires = numOfEmpires;
		this.path = path;
	}

	/**
	 * Computes ICA optimization and returns the minimum value found. 
	 */
	@Override
	public Double call() throws Exception
	{
		ICAlgorithm ica;
		if(initialPopulation == null)
			ica = new ICAlgorithm(pr, path, logFileName);
		else
			ica = new ICAlgorithm(pr, path, logFileName, initialPopulation, numOfEmpires);
		if(useExplorers)
			return pr.valueAt(ica.runEICA());
		else
			return pr.valueAt(ica.runICA());
	}
}