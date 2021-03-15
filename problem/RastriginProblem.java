package problem;
import static java.lang.Math.pow;
import static java.lang.Math.cos;
import static java.lang.Math.PI;

/**This class provides the functionality of the Rastrigin function. The minimum solution of the 
 * Rastrigin function is at <b>zero</b>.
 * @author mazhar
 *
 */
public class RastriginProblem extends Problem 
{
	private static final long serialVersionUID = 1144221964542443955L;

	public RastriginProblem()
	{
		
	}
	
	/**
	 * @param dimension
	 * @param lowerBound the lower bound of any dimension
	 * @param upperBound the lower bound of any dimension
	 */
	public RastriginProblem(int dimension, double lowerBound,	double upperBound) 
	{
		super("Rastrigin", dimension, lowerBound, upperBound);
	}

	/**
	 * @param lowerBound the lower bound of each dimension
	 * @param upperBound the lower bound of each dimension
	 */
	public RastriginProblem(double[] lowerBounds, double[] upperBounds) 
	{
		super("Rastrigin", lowerBounds, upperBounds);
	}

	/* (non-Javadoc)
	 * @see Problem#calculateFunction(double[])
	 */
	@Override
	public double valueAt(double[] x) 
	{
		if(x.length != this.getDimension())
			throw(new IllegalArgumentException("The input does not match the problem dimension"));
		
		return functionValueAt(x);
	}
	
	public static double functionValueAt(double[] x) 
	{
		double result = 0;
		double a, b;
		for(int i = 0; i < x.length; i++)
		{
			a = pow(x[i], 2);
			b = -10 * cos( 2 * PI * x[i]) + 10;
			result = result + a + b;// pow(x[i], 2) + (- 10 * cos( 2 * PI * x[i]) + 10);
		}
		
		return result;
	}
	
	@Override
	public String toString()
	{
		return "Rastrigin function. " + super.toString();
	}

}
