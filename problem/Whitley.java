package problem;

public class Whitley extends Problem
{

	private static final long serialVersionUID = 5875360392346899283L;

	public Whitley(int dimension, double lowerBound, double upperBound)
	{
		super("Whitley", dimension, lowerBound, upperBound);
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
			for(int j = 0; j < x.length; j++)
			{
				double temp = 100*(Math.pow(x[i], 2)-x[j]) + Math.pow(1-x[j], 2);
				retval += ((temp * temp)/4000.0) - Math.cos(temp) + 1;
			}
		
		return retval;
	}

}
