package simula.oclga;

//This algorithm is based on genetic algorithm

public class RandomSearch extends Search 
{
	@Override
	public String[] getSolution(Problem problem)
	{	
		Individual best = Individual.getRandomIndividual(problem);
		best.evaluate();
		this.increaseIteration();

		if(best.fitness_value == 0d)
			return best.problem.decoding(best.v);
		
		
		//init first generation
		while(!this.isStoppingCriterionFulfilled())
		{
			Individual current = Individual.getRandomIndividual(problem);
			current.evaluate();
			this.increaseIteration();

			if(current.fitness_value == 0d)
				return current.problem.decoding(current.v);
						
			if(current.fitness_value < best.fitness_value)
			{
				best.copyDataFrom(current);
				reportImprovement();				
			}
		}
			
		return best.problem.decoding(best.v);		
	}
	
	@Override 
	public String getShortName()
	{
		return "RS";
	}
}
