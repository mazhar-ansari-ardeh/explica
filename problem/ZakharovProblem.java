package problem;

public class ZakharovProblem extends Problem
{
	
	private static final long serialVersionUID = 3767803616577368149L;
	
	public ZakharovProblem()
	{
		
	}

	public ZakharovProblem(int dimension, double lowerBound, double upperBound)
	{
		super("Zakharov", dimension, lowerBound, upperBound);
	}

	@Override
	public double valueAt(double[] x)
	{
		if(x.length != this.getDimension())
			throw (new IllegalArgumentException("The input variable does not comply with the defined space dimension"));
//		if(checkBounds(input))
//			throw new IllegalArgumentException("The input value does not reside within the problem space");
		
		return functionValueAt(x);
	}
	
	public static double functionValueAt(double[] x)
	{
		double result = 0f;
		int dim = x.length;
		double p1 = 0f; // sum of (x_i^2)
		double p2 = 0f; // sum of (0.5ix_i)
		for(int i = 0; i < dim; i++)
		{
			p1 += x[i] * x[i];
			p2 += (0.5 * i * x[i]);
		}
		
		result = p1 + Math.pow(p2, 2) + Math.pow(p2, 4);
		return result;
	}

}
