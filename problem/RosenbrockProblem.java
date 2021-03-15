package problem;
import static java.lang.Math.pow;

/**
 * @author Mazhar
 *
 */
public class RosenbrockProblem extends Problem 
{

	private static final long serialVersionUID = -8501721896525964733L;
	
	public RosenbrockProblem()
	{
		
	}

	/**Generates a new Rosenbrock function. All dimensions of the space will have the same lower and upper bounds 
	 * @param dimension the dimension of the space on which the function is defined. 
	 * @param lowerBound the lower bound of any dimension
	 * @param upperBound the lower bound of any dimension
	 */
	public RosenbrockProblem(int dimension, double lowerBound, double upperBound)
	{
		super("Rosenbrock", dimension, lowerBound, upperBound);
	}

	/**Generates a new Rosenbrock function
	 * @param lowerBound the lower bound of each dimension
	 * @param upperBound the lower bound of each dimension
	 */
	public RosenbrockProblem(double[] lowerBounds, double[] upperBounds) 
	{
		super("Rosenbrock", lowerBounds, upperBounds);
	}

	/* 
	 * @see Problem#calculateFunction(double[])
	 */
	@Override
	public final double valueAt(double[] x) 
	{
		if(x == null || x.length != getDimension())
			throw new IllegalArgumentException("The length of input does not match with problem dimension");
		
		return functionValueAt(x);
	}
	
	public static double functionValueAt(double[] x)
	{
		if(x == null)
			throw new IllegalArgumentException("The input value cannot be null.");
		double result = 0;
		for(int i = 0; i < x.length - 1; i++)
			result += 100 * pow(x[i + 1] - pow(x[i], 2) ,2) + pow(x[i] - 1, 2);
		
		return result;
	}
	
	@Override
	public String toString()
	{
		return "Rosenbrock function. " + super.toString();
	}

}
