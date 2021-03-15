package problem;

public class SumOfDifferentPowersProblem extends Problem
{
	private static final long serialVersionUID = -6853233676794349064L;

	public SumOfDifferentPowersProblem()
	{
		
	}
	
	public SumOfDifferentPowersProblem(int dimension, double lowerBound, double upperBound)
	{
		super("SumOfDifferentPowers", dimension, lowerBound, upperBound);
	}
	
	@Override
	public double valueAt(double[] input)
	{
		if(input.length != this.getDimension())
			throw (new IllegalArgumentException("The input variable does not comply with the defined space dimension"));
		
		return functionValueAt(input);
	}
	
	public static double functionValueAt(double[] x)
	{
		double retval = 0;
		for (int i = 0; i < x.length; i++)
		{
			retval = retval + Math.pow(Math.abs(x[i]), i + 2);
		}
		
		return retval;
	}
}
