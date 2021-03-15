package problem;

public class SchwefelProblem extends Problem
{
	private static final long serialVersionUID = 5953720296755140409L;

	public SchwefelProblem()
	{
		
	}
	
	public SchwefelProblem(int dimension, double lowerBound, double upperBound)
	{
		super("Schwefel", dimension, lowerBound, upperBound);
	}

	@Override
	public double valueAt(double[] x)
	{
		if(x.length != this.getDimension())
			throw(new IllegalArgumentException("The input does not match the problem dimension"));
		
		return functionValueAt(x);
	}
	
	public double functionValueAt(double[] x)
	{
		double retval = 0f;
		for(int i = 0; i < getDimension(); i++)
		{
			double abs = Math.abs(x[i]);
			double sqrt = Math.sqrt(abs);
			double sin = Math.sin(sqrt);
			double fa = x[i] * sin;
			retval = retval + fa;
		}
		retval = (418.9829 * getDimension()) - retval;
		return retval;
	}
}
