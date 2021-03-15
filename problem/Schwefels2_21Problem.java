package problem;

public class Schwefels2_21Problem extends Problem
{
	private static final long serialVersionUID = -4560247042777416845L;

	public Schwefels2_21Problem(int dimension, double lowerBound, double upperBound)
	{
		super("Schwefel2.21", dimension, lowerBound, upperBound);
	}
	
	@Override
	public double valueAt(double[] x)
	{
		if(x == null || x.length == 0)
			throw new IllegalArgumentException("Value cannot be null or empty");
		if(x.length != getDimension())
			throw new IllegalArgumentException("Dimension of the given value does not match the dimension of this problem");
		
		double retval = 0;
		for(double xi : x)
		{
			double absx = Math.abs(xi);
			if(absx > retval)
				retval = absx;
		}
		
		return retval;
	}

}
