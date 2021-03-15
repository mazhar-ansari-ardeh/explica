package problem;

// Not helpful
public class StyblinskiProblem extends Problem
{
	private static final long serialVersionUID = -3376709988428236028L;

	public StyblinskiProblem()
	{
		super();
	}
	
	public StyblinskiProblem(int dimension, double lowerBound, double upperBound)
	{
		super("Styblinski", dimension, lowerBound, upperBound);
	}
	
	@Override
	public double valueAt(double[] x)
	{
		double retval = 0;
		for(int i = 0; i < x.length; i++)
		{
			retval = retval + Math.pow(x[i], 4) - (16 * Math.pow(x[i], 2)) + (5 * x[i]); 
		}
		
		return retval * 0.5;
	}
}
