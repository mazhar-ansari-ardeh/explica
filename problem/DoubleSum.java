package problem;

public class DoubleSum extends Problem
{
	private static final long serialVersionUID = -4486651886913063L;

	public DoubleSum(int dimension, double lowerBound, double upperBound)
	{
		super("DoubleSum", dimension, lowerBound, upperBound);
	}

	@Override
	public double valueAt(double[] x)
	{
		if(x == null || x.length == 0)
			throw new IllegalArgumentException("Value cannot be null or empty");
		if(x.length != getDimension())
			throw new IllegalArgumentException("Dimension of the given value does not match the dimension of this problem");
		double retval = 0;
		for(int i = 0; i < x.length; i++)
		{
			double innerSum = 0;
			for(int j = 0; j <= i; j++)
			{
				innerSum = innerSum + Math.pow(x[j] - (j + 1), 2);				 
			}
			
			retval = retval + innerSum;
		}
		
		return retval;
	}

}
