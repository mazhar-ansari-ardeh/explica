package problem;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.sin;

public class GeneralizedSchafferProblem extends Problem 
{

	private static final long serialVersionUID = -4007208030846354581L;
	
	public GeneralizedSchafferProblem()
	{
		
	}

	public GeneralizedSchafferProblem(int dimension, double lowerBound, double upperBound) 
	{
		super("Generalized Schaffer", dimension, lowerBound, upperBound);
	}
	
	public GeneralizedSchafferProblem(double[] lowerBounds, double[] upperBounds) 
	{
		super("GeneralizedSchaffer", lowerBounds, upperBounds);
	}
	
	@Override
	public double valueAt(double[] x) 
	{
		if(x.length != this.getDimension())
			throw(new IllegalArgumentException("The input does not match the problem dimension"));
		
		double result = 0;
		double sumOfSquares = 0;
		for(int i = 0; i < x.length; i++)
			sumOfSquares += pow(x[i], 2);
		
		result = .5 + (pow(sin(sqrt(sumOfSquares)), 2) - .5) / pow((1 + .001 * sumOfSquares), 2);	
		
		return result;
	}
	
	@Override
	public String toString()
	{
		return "Generalized Schaffer. " + super.toString();
	}
}
