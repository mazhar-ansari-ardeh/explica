package problem;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.PI;
import static java.lang.Math.E;

/**
 * This class provides the functionality of the Ackley function. This function has a minimum at <b>zero</b>.
 * @author Mazhar
 *
 */
public final class AckleyProblem extends Problem {

	private static final long serialVersionUID = 374989071783381134L;
	
	public AckleyProblem()
	{
		
	}

	/**
	 * @param name
	 * @param dimension
	 * @param lowerBound
	 * @param upperBound
	 */
	public AckleyProblem(int dimension, double lowerBound, double upperBound)
	{
		super("Ackley", dimension, lowerBound, upperBound);
	}

	/**
	 * @param name
	 * @param lowerBounds
	 * @param upperBounds
	 */
	public AckleyProblem(double[] lowerBounds, double[] upperBounds)
	{
		super("The Ackley function", lowerBounds, upperBounds);
	}

	/* (non-Javadoc)
	 * @see Problem#calculateFunction(double[])
	 */
	@Override
	public double valueAt(double[] x) 
	{
		if(x == null)
			throw new IllegalArgumentException("The input variable cannot be null");
		if(x.length != this.getDimension())
			throw(new IllegalArgumentException("The input does not match the problem dimension"));
		
		return functionValueAt(x);
	}
	
	public static double functionValueAt(double[] x)
	{
		if(x == null || x.length == 0)
			throw new IllegalArgumentException("The input variable cannot be null");
		
		double result = 0;
		int dim = x.length;
		double sos = 0; // sum of squares
		double soc = 0; // sum of cosines
		
		for(int i = 0; i < x.length; i++)
		{
			sos += pow(x[i], 2);
			soc += cos( 2 * PI * x[i]);
		}
		
		sos = sos / dim;
		soc = soc / dim;
		
		result = 20 + E - 20 * exp(-.2 * sqrt(sos)) - exp( soc );
		
		return result;
	}
	
	@Override
	public String toString()
	{
		return "Ackley function. " + super.toString();
	}

}
