package problem;

import java.io.Serializable;

public final class SphereProblem extends Problem implements Serializable
{
	private static final long serialVersionUID = -1036185614359152256L;

	public SphereProblem()
	{
		super();
	}
	public SphereProblem(int dimension, double lowerBound, double upperBound)
	{
		super("Sphere", dimension, lowerBound, upperBound);
	}
	
	public SphereProblem(double[] lowerBounds, double[] upperBounds)
	{
		super("Sphere", lowerBounds, upperBounds);
	}

	@Override
	public double valueAt(double[] input)
	{
		if(input == null)
			throw new IllegalArgumentException("The input variable cannot be null");
		if(input.length != this.getDimension())
			throw (new IllegalArgumentException("The input variable does not comply with the defined space dimension. Dimension: " + getDimension() + ", input length: " + input.length));
		
		return functionValueAt(input);
	}
	
	public static double functionValueAt(double[] x)
	{
		if(x == null)
			throw new IllegalArgumentException("The input variable cannot be null");
		double result = 0;
		for(int i = 0; i < x.length; i++)
			result += Math.pow(x[i], 2);
		
		return result;
	}
	
	@Override
	public String toString()
	{
		return "Sphere function. " + super.toString();
	}
}