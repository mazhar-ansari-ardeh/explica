package problem;

import com.mathworks.toolbox.javabuilder.MWException;

public class TestProblems
{

	/**
	 * @param args
	 * @throws MWException 
	 */
	public static void main(String[] args) throws MWException
	{
		Problem t = new Whitley(10, -100, 100);
		double [] input = {1, 1, 1, 1, 1,1, 1, 1, 1, 1};
		double retval = t.valueAt(input);
		System.out.println(retval);
		
		t = new PropellerProblem();
		retval = t.valueAt(new double[]{7, 5, 1.4, 1, 75});
		System.out.println(retval);
	} 

}
