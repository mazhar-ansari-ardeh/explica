package problem;

public class Schwefels2_22Problem extends Problem
{
	private static final long serialVersionUID = -4560247042777416845L;

	public Schwefels2_22Problem(int dimension, double lowerBound, double upperBound)
	{
		super("Schwefel2.22", dimension, lowerBound, upperBound);
	}
	
	@Override
	public double valueAt(double[] x)
	{
		if(x == null || x.length == 0)
			throw new IllegalArgumentException("Value cannot be null or empty");
		if(x.length != getDimension())
			throw new IllegalArgumentException("Dimension of the given value does not match the dimension of this problem");
		
		double sumFactor = 0;
		double productFactor = 0;
		
		for(int i = 0; i < x.length; i++)
		{
			sumFactor = sumFactor + Math.abs(x[i]);
			productFactor = productFactor * Math.abs(x[i]);
		}
		
		return sumFactor + productFactor;
	}

}
