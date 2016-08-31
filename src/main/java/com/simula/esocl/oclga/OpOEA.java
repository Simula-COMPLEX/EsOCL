package com.simula.esocl.oclga;

public class OpOEA extends SSGA 
{
	public OpOEA()
	{}
	
	@Override
	public int[] getSolution(Problem problem)
	{	
		Individual current = Individual.getRandomIndividual(problem);
		current.evaluate();
		this.increaseIteration();

		if(current.fitness_value == 0d)
			return current.v;

		Individual tmp = Individual.getRandomIndividual(problem);
		
		//init first generation
		while(!this.isStoppingCriterionFulfilled())
		{
			tmp.copyDataFrom(current);
			mutateAndEvaluateOffspring(tmp);

			if(tmp.fitness_value == 0d)
				return tmp.v;
						
			if(tmp.fitness_value <= current.fitness_value)
			{
				current.copyDataFrom(tmp);
				
				if(tmp.fitness_value < current.fitness_value)
					reportImprovement();				
			}
		}
			
		return current.v;		
	}
	
	@Override 
	public String getShortName()
	{
		return "OpOEA";
	}
}
