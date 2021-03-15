package problem;

public class PowellProblem extends Problem
{

	private static final long serialVersionUID = 8993882855594412127L;
	
	public PowellProblem()
	{
		
	}

	public PowellProblem(int dimension, double lowerBound, double upperBound)
	{
		super("Powell", dimension, lowerBound, upperBound);
	}

	@Override
	public double valueAt(double[] x)
	{
		if(x.length != this.getDimension())
			throw (new IllegalArgumentException("The input variable does not comply with the defined space dimension"));
		
		return functionValueAt(x);
	}
	
	public static double functionValueAt(double[] x)
	{
		double retval = 0f;
		for(int i = 0; i < x.length / 4; i++)
		{
			double term1 = x[(4 * (i + 1)) - 4] + (10 * x[(4 * (i + 1)) - 3]);
			term1 = Math.pow(term1, 2);
			
			double term2 = x[(4 * (i + 1)) - 2] - x[4 * (i + 1) - 1];
			term2 = 5 * Math.pow(term2, 2);
			
			double term3 = x[(4 * (i + 1)) - 3] - (2 * x[(4 * (i + 1)) - 2]); 
			term3 = Math.pow(term3, 4);
			
			double term4 = x[(4 * (i + 1)) - 4] - x[4 * (i + 1) - 1];
			term4 = 10 * Math.pow(term4, 4);
			
			retval = retval + term1 + term2 + term3 + term4;
		}
		
		return retval;
	}

}
