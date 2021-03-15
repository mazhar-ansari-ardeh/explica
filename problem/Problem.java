package problem;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 * @author Mazhar Ansari Ardeh
 *
 */
public abstract class Problem implements Serializable
{
	private static final long serialVersionUID = 5630605003860735298L;
	private int dimension;
	private double[] lowerBound = null;
	private double[] upperBound = null;
	private Boolean uniformBounds;
	private double uniformLowerBound;
	private double uniformUpperBound;
	protected String name = "";

	public Problem()
	{
		
	}
	
	public Problem(String name, int dimension, double lowerBound, double upperBound)
	{
		if(dimension <= 0)
			throw new IllegalArgumentException("Dimension cannot be negative or zero");

		this.name = name != null ? name : "";
		this.dimension = dimension;
		this.uniformBounds = true;
		this.uniformLowerBound = lowerBound;
		this.uniformUpperBound = upperBound;
	}

	public Problem(String name, double[] lowerBounds, double[] upperBounds)
	{
		if(lowerBounds.length != upperBounds.length)
			throw new IllegalArgumentException("Lower bound and upper bound inputs must have equal size");
		if(lowerBounds.length == 0)
			throw new IllegalArgumentException("Dimension cannot be negative or zero");

		this.name = name != null ? name : "";
		this.uniformBounds = false;
		this.dimension = lowerBounds.length;
		this.lowerBound = lowerBounds;
		this.upperBound = upperBounds;
	}

	/**
	 * Returns the name of this problem instance. 
	 * @return the name of this problem instance. 
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the dimension of the space on which this problem is defined.
	 * @return the dimension of the problem space.
	 */
	public int getDimension()
	{
		return dimension;
	}

	/**
	 * Gets the lower bound of the space that problem is defined on.
	 * @param index Specifies a variable of the space.
	 * @return The lower bound of the variable specified by <code>index</code> of the space on which the problem is defined.
	 */
	public double getLowerBound(int index)
	{
		if(this.uniformBounds)
			return this.uniformLowerBound;
		else
			return this.lowerBound[index];
	}

	/**
	 * Checks a given data value against the upper bound of this problem instance to see 
	 * if it is within the space on which this problem is defined. 
	 * @param data a data value that will be checked. 
	 * @param index Specifies the component of the problem space.
	 * @return <code>true</code> if the given data value is within the problem space and
	 * <code>false</code> otherwise. 
	 */
	public boolean checkUpperBound(double data, int index)
	{
		if(data <= getUpperBound(index))
			return true;

		return false;
	}

	public double[] getUpperBounds()
	{
		synchronized (uniformBounds)
		{
			if(isUniformBounded())
			{
				this.upperBound = new double[this.dimension];
				Arrays.fill(this.upperBound, this.uniformUpperBound);

				this.lowerBound = new double[this.dimension];
				Arrays.fill(this.lowerBound, this.uniformLowerBound);
				this.uniformBounds = false;
			}
		}
		return this.upperBound;
	}
	
	public double[] getLowerBounds()
	{
		synchronized (uniformBounds)
		{
			if(isUniformBounded())
			{
				this.upperBound = new double[this.dimension];
				Arrays.fill(this.upperBound, this.uniformUpperBound);

				this.lowerBound = new double[this.dimension];
				Arrays.fill(this.lowerBound, this.uniformLowerBound);
				this.uniformBounds = false;
			}
			return this.lowerBound;
		}

	}

	/**
	 * Checks whether the given data is inside the cube that this problem is defined on or not.
	 * @param x the data to be checked to see  if it is inside the the problem space. For the <code>null</code> value
	 * the return value will be <code>false</code>.
	 * @return <code>true</code> if the input data <code>x</code> resides within the space on which the problem is defined,
	 * <code>false</code> otherwise.
	 */
	public boolean checkBounds(double[] x)
	{
		if(x == null)
			return false; 
		if(x.length != this.getDimension())
			throw(new IllegalArgumentException("The input does not match the problem dimension"));

		for(int i = 0; i < x.length; i++)
			if(!checkLowerBound(x[i], i) || !checkUpperBound(x[i], i))
				return false;

		return true;
	}

	/**
	 * Checks a given data value against the lower bound of this problem instance to see 
	 * if it is within the space on which this problem is defined. 
	 * @param data a data value that will be checked. 
	 * @param index Specifies the component of the problem space.
	 * @return <code>true</code> if the given data value is within the problem space and
	 * <code>false</code> otherwise. 
	 */
	public boolean checkLowerBound(double data, int dim)
	{
		if(data >= getLowerBound(dim))
			return true;

		return false;
	}

	public double getUpperBound(int index)
	{
		if(this.uniformBounds)
			return this.uniformUpperBound;
		else
			return this.upperBound[index];
	}

	public boolean isUniformBounded()
	{
		return this.uniformBounds;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder(this.getName());
		result.append("D").append(this.getDimension()); //.append(".\n Lower bounds:");
		//		StringBuilder lb = new StringBuilder();
		//		StringBuilder ub = new StringBuilder();
		//		
		//		for(int i = 0; i < this.getDimension(); i++)
		//		{
		//			lb.append(" [").append(i).append("]: ").append(getLowerBound(i));
		//			ub.append(" [").append(i).append("]: ").append(getUpperBound(i));
		//		}
		//		result.append(lb).append("\nUpper bounds: ").append(ub);

		return result.toString();
	}

	/**
	 * Computes the function value at a given point and return it. Derived classes
	 * that implement this method must check the given point against the bounds of
	 * this problem and choose an appropriate action if it is not within the bounds.
	 * @param input a point on the problem space that value of this function
	 * will be computed at. This parameter cannot be null.
	 * @return function value at a given point in space. 
	 */
	public abstract double valueAt(double[] input);
}