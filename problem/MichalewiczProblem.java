package problem;

public class MichalewiczProblem extends Problem
{
	private static final long serialVersionUID = -2519181088324161421L;
	
	private double m;
	
	public MichalewiczProblem()
	{
		
	}
	
	public MichalewiczProblem(int dimension, double lowerBound, double upperBound, double m)
	{
		super("Michalewicz", dimension, lowerBound, upperBound);
		this.m = m;
	}
	
	public MichalewiczProblem(int dimension, double lowerBound, double upperBound)
	{
		super("Michalewicz", dimension, lowerBound, upperBound);
		this.m = 10; // This is the recommended value.
	}

	@Override
	public double valueAt(double[] input)
	{
		if(input.length != this.getDimension())
			throw (new IllegalArgumentException("The input variable does not comply with the defined space dimension"));
		
		return functionValueAt(input, m);
	}
	
	public static double functionValueAt(double[] input, double mValue)
	{
		double retval = 0f;
		for(int i = 0; i < input.length; i++)
		{
			double x = input[i];
			double ix2_pi = (i + 1) * Math.pow(x, 2);
			ix2_pi = ix2_pi / Math.PI;
			double sin2m = Math.pow(Math.sin(ix2_pi), 2 * mValue);
			
			retval = retval + (Math.sin(x) * sin2m);
		}
		return  - retval;
	}

}
