package problem;

public class TridProblem extends Problem
{
	private static final long serialVersionUID = 1148599695779771761L;
	
	public TridProblem()
	{
		
	}

	public TridProblem(int dimension, double lowerBound, double upperBound)
	{
		super("Trid", dimension, lowerBound, upperBound);
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
		double sum1 = 0f;
		double sum2 = 0f;
		for (int i = 0; i < x.length; i++)
		{
			double d = x[i];
			sum1 = sum1 + Math.pow(d - 1, 2);
			if(i == 0)
				continue;
			sum2 = sum2 + (d * x[i - 1]);
		}
		retval = sum1 - sum2;
		return retval;
	}

}
