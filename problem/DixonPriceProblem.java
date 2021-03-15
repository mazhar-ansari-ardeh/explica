package problem;

public class DixonPriceProblem extends Problem
{

	private static final long serialVersionUID = 2773125082853203702L;

	public DixonPriceProblem()
	{
		
	}
	
	public DixonPriceProblem(int dimension, double lowerBound, double upperBound)
	{
		super("DixonPrice", dimension, lowerBound, upperBound);
	}

	@Override
	public double valueAt(double[] x)
	{
		if(x.length != this.getDimension())
			throw(new IllegalArgumentException("The input does not match the problem dimension"));
		
		return functionValueAt(x);
	}
	
	public static double functionValueAt(double[] x)
	{
		double retval = Math.pow(x[0] - 1, 2);
		
		for(int i = 1; i < x.length; i++)
		{
			retval = retval + ((i + 1) * Math.pow(2 * Math.pow(x[i], 2) - x[i - 1], 2));
		}
		
		return retval;
	}

}
