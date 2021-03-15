package problem;

public class SumSquaresProblem extends Problem
{
	private static final long serialVersionUID = 619928153921778633L;
	
	public SumSquaresProblem()
	{
		
	}

	public SumSquaresProblem(int dimension, double lowerBound, double upperBound)
	{
		super("SumSquare", dimension, lowerBound, upperBound);
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
		double retval = 0f;
		int dim = x.length;
		for(int i = 0; i < dim; i++)
		{
			retval = retval + i * Math.pow(x[i], 2);
		}
		
		return retval;
	}

}
