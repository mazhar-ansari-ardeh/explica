package problem;

import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import Propeller.Propeller;

public final class PropellerProblem extends Problem
{
	private static final long serialVersionUID = -7031636674065625147L;

	@Override
	public String toString()
	{
		return "Propeller function. " + super.toString();
	}
	
	Propeller propeller = null;
	
	public PropellerProblem() throws MWException
	{
		// In case of this particular problem, the higher and lower bounds are fixed 
		super("Propeller problem", new double[]{3, 2, 0.5, 1, 55}, new double[] {7, 5, 1.4, 1.5, 75});
		propeller = new Propeller();
	}
	
	public static double functionValueAt(double[] input)
	{
		Double[] Input = {new Double(input[0]), new Double(input[1]), new Double(input[2]), new Double(input[3]), new Double(input[4])};
		Object[] ret = null;
		try
		{
			ret = new Propeller().Cost_Propeller(1, (Object)Input);
		} catch (MWException e)
		{
			throw new RuntimeException("MatlabRuntime has thrown an exception.", e);
		}
		
		return ((MWNumericArray)ret[0]).getDouble(0);
	}

	@Override
	public double valueAt(double[] input)
	{
		if(input.length != 5)
			throw new IllegalArgumentException("The PropellerProblem's space dimension is 5");
		
		Double[] Input = {new Double(input[0]), new Double(input[1]), new Double(input[2]), new Double(input[3]), new Double(input[4])};
		Object[] ret = null;
		try
		{
			ret = propeller.Cost_Propeller(1, (Object)Input);
		} catch (MWException e)
		{
			throw new RuntimeException("MatlabRuntime has thrown an exception.", e);
		}
		
		return ((MWNumericArray)ret[0]).getDouble();
	}
}
