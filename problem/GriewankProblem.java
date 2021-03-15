package problem;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.cos;

/**
 * This class provides the functionality of the Griewank function. This function has a minimum at <b>100</b>.
 * @author Mazhar
 *
 */
public final class GriewankProblem extends Problem 
{

	private static final long serialVersionUID = 7673090472381109534L;

	public GriewankProblem()
	{
		
	}
	
	/**
	 * @param name
	 * @param dimension
	 * @param lowerBound
	 * @param upperBound
	 */
	public GriewankProblem(int dimension, double lowerBound, double upperBound)
	{
		super("Griewank", dimension, lowerBound, upperBound);
	}

	/**
	 * @param name
	 * @param lowerBounds
	 * @param upperBounds
	 */
	public GriewankProblem(double[] lowerBounds, double[] upperBounds)
	{
		super("Griewank", lowerBounds, upperBounds);
	}

	@Override
	public double valueAt(double[] x) 
	{
		if(x.length != this.getDimension())
			throw(new IllegalArgumentException("The input does not match the problem dimension"));
		if(!checkBounds(x))
			throw(new IllegalArgumentException("The input does not reside within the problem space"));
		
		return functionValueAt(x);
	}
	
	public static double functionValueAt(double[] x)
	{
		double result = 0;
		double sos = 0; // sum of squares
		double poc = 1; // product of cosines
		
		for(int i = 0; i < x.length; i++)
		{
			sos += pow(x[i] - 100, 2);
			poc *= cos( (x[i] - 100) / sqrt(i + 1) );
		}
		
		result = (1 / 4000.0) * sos - poc + 1;
		return result;
	}
	
	@Override
	public String toString()
	{
		return "Griewank function. " + super.toString();
	}

}
