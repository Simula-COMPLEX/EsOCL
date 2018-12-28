package simula.oclga;

import simula.ocl.distance.ValueElement4Search;

public abstract class Search 
{
	private long time_threshold;
	private long start_time;
	private long delta;
	private long last_improvement;
	
	private int max_iterations;
	private int current_iteration; 
	
	public abstract String[] getSolution(Problem problem);
	public  abstract String getShortName();
	
	/*
	 * Two stage search,
	 * The first stage, search the optimization value of multiplicity
	 * The second stage, search the optimization value of attributes 
	 */
	public String[] search(Problem problem)
	{
		start_time = System.currentTimeMillis();
		last_improvement = start_time;
	    
		if(!hasStoppingCriterion())
			throw new RuntimeException("No stopping criterion defined for "+this.getShortName());
		
		//this.max_iterations = max_iterations;
		current_iteration = 0;
		String[]  solution = getSolution(problem);
		
		return solution;
	}
	
	public static void validateConstraints(Problem p)
	{
		ValueElement4Search[] c = p.getConstraints();
		if(!areConstraintsValid(c))
			throw new RuntimeException("This Problem has invalid constraints");
	}

	public static boolean areConstraintsValid(ValueElement4Search[] constraints) 
	{
		boolean invalid = false;
		
		if(constraints==null || constraints.length==0)
			invalid = true;
		else
		{
			for(int i=0; i<constraints.length;i++)
			{
				if(constraints[i]==null || 
						String.valueOf(constraints[i].getMinValue()).length()==0||
						String.valueOf(constraints[i].getMaxValue()).length()==0||
						String.valueOf(constraints[i].getType()).length()==0)
				{
					invalid = true;
					break;
				}
				
				//currently, there are only three types of constraints, so, the type must lower that 3
				if(constraints[i].getMinValue()>constraints[i].getMaxValue() 
						|| (constraints[i].getType() > 3) 
						|| constraints[i].getType() < 0)
				{
					invalid = true;
					break;
				}
			}
		}
		
		return !invalid;
	}
	
	
	protected boolean isStoppingCriterionFulfilled()
	{
		if(max_iterations > 0) //using iterations
		{
			if(current_iteration<max_iterations)
				return false;
		}
		
		if(usingTime() && !isTimeExpired())
			return false;
		
		if(shouldKeepGoingBasedOnDelta())
			return false;
		
		return true;
	}
	
	/*
	protected boolean isStoppingCriterionFulfilled(int populationsize)
	{
		if(current_iteration >= max_iterations)
			if(max_iterations < populationsize)
				return false;
			else 
				return true;
		
		if(max_iterations > 0) //using iterations
		{
			if(current_iteration<max_iterations)
				return false;
		}
		
		if(usingTime() && !isTimeExpired())
			return false;
		
		if(shouldKeepGoingBasedOnDelta())
			return false;
		
		return true;
	}
	*/
	
	protected void increaseIteration()
	{
		current_iteration++;
	}
	
	public int getIteration()
	{
		return current_iteration;
	}
	
	public boolean hasStoppingCriterion()
	{
		if(max_iterations<=0  && time_threshold<=0 && delta<=0)
			return false;
		return true;
	}
	
	public void setMaxIterations(int maxIterations) 
	{
		max_iterations = maxIterations;
	}

	
	protected boolean usingTime()
	{
		return time_threshold > 0;
	}
	
	protected boolean isTimeExpired()
	{
		long elapsed = System.currentTimeMillis() - start_time;
		return elapsed > time_threshold;
	}


	
	
	public double normalise(double x)
	{
		if(x<0)
			throw new RuntimeException("Cannot normalise negative value "+x);
		
		return x / (x +1d);
	}
	
	public  void setStopAfterMilliseconds(long ms)
	{
		time_threshold = ms;
	}
	
	public void setKeepGoingIfBetterResults(long delta)
	{
		this.delta = delta;
	}
	
	protected void reportImprovement()
	{
		last_improvement = System.currentTimeMillis();
	}
	
	protected boolean shouldKeepGoingBasedOnDelta()
	{
		if(delta <=0)
			return false;
		
		long elapsed = System.currentTimeMillis() - last_improvement;
		
		return elapsed < delta;
	}
}
