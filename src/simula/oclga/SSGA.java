package simula.oclga;

import java.util.*;


public class SSGA  extends Search
{
	protected int population_size;
	protected double xover_rate;
	
	public SSGA()
	{
		population_size = 100;
		xover_rate = 0.75;
	}
	
	public SSGA(int population_size, double xover_rate)
	{
		this.population_size = population_size;
		this.xover_rate = xover_rate;	
	}
	
	
	public String[] getSolution(Problem problem)
	{	
		double best = Double.MAX_VALUE;
		
		Individual[] population = new Individual[population_size];
		
		//init first generation
		for(int i=0; i<population.length && !this.isStoppingCriterionFulfilled(); i++)
		{
			population[i] = Individual.getRandomIndividual(problem); 
			this.increaseIteration();
			
			if(population[i].fitness_value < best)
			{
				best = population[i].fitness_value;
				reportImprovement();
				
				if(best == 0d)
					return population[i].problem.decoding(population[i].v);
			}
		}
			
				
		while(!this.isStoppingCriterionFulfilled())
		{	
			//for rank selection, I need the population sorted
			Arrays.sort(population);
			
			int x = rankSelectionIndex(1.5,population.length);
			int y = rankSelectionIndex(1.5,population.length);
			
			//avoid to pick up same parents
			if(y==x)
			{
				if(y==population.length-1)
					y--;
				else
					y++;
			}
			
			Individual father = population[x];
			Individual mother = population[y];
			
			//generate and mutate offspring
			

		//	System.out.println("**************** Father: "+ father );
			
			for (int i=0; i<father.v.length; i++)
			{
				//System.out.print(", "+father.v[i]);
			}
			
		//	System.out.println("**************** Mother: "+ mother );
			for (int i=0; i<father.v.length; i++)
			{
				//System.out.print(", "+mother.v[i]);
			}
			
			Individual[] offspring = xover(father, mother);
			
			mutateAndEvaluateOffspring(offspring[0]);
			if(offspring[0].fitness_value == 0d)
				return offspring[0].problem.decoding(offspring[0].v);

			mutateAndEvaluateOffspring(offspring[1]);
			if(offspring[1].fitness_value == 0d)
				return offspring[1].problem.decoding(offspring[1].v);
			
			double off_min = Math.min(offspring[0].fitness_value,offspring[1].fitness_value);		
			double par_min =  Math.min(father.fitness_value,mother.fitness_value);
			
			//accept new offspring if one of them is better/equal than  the best parent
			if(off_min <= par_min)
			{	
				population[x] = offspring[0];
				population[y] = offspring[1];
			}
			
			if(off_min < best)
			{
				best = off_min;
				reportImprovement();
			}
		}	
		
		//return the best, ie minimal similarity
		Arrays.sort(population);
		return population[0].problem.decoding(population[0].v);
	}
	
	public void mutateAndEvaluateOffspring(Individual ind)
	{
		ArrayList<GeneValueScope> cons = ind.getEecodedConstraints();
		
		final double p = 1d / (double) ind.v.length;  
		
		for(int i=0; i<ind.v.length; i++)
		{
			if(p >= RandomGenerator.getGenerator().nextDouble())
			{
				final int min = cons.get(i).getMinValue();
				final int max = cons.get(i).getMaxValue();
				final int dif = max-min;
				
				if(cons.get(i).getType()==Problem.CATEGORICAL_TYPE)
				{
					ind.v[i] = min + RandomGenerator.getGenerator().nextInt(dif+1);
				}
				else 
				{
					final double step = (double )dif / 100d ; // 1%
					int sign = RandomGenerator.getGenerator().nextBoolean() ? -1 : 1;
					
					final int delta = sign * ( 1 + (int)(step * RandomGenerator.getGenerator().nextDouble()));
					
					int k = ind.v[i] + delta;
					
					if(k > max )
						k = max;
					if(k < min)
						k = min;
					
					ind.v[i] = k;
				}
			}
		}
		
		ind.evaluate();
		this.increaseIteration();
	}
	
	protected Individual[] xover(Individual a, Individual b)
	{
		Individual[] offspring = new Individual[]{new Individual(a.problem),new Individual(b.problem)};
		
		offspring[0].v = new int[a.v.length];
		offspring[1].v = new int[b.v.length];
		
		//k is the splitting point. it cannot be the first, because otherwise the offspring
		//d be copies of the parents
		int k;
		
		//shall we apply xover?
	//	System.out.print("************"+offspring[0].v.length);
		
		if(RandomGenerator.getGenerator().nextDouble() < xover_rate)
			k = 1 + RandomGenerator.getGenerator().nextInt(offspring[0].v.length);
		else
			k = offspring[0].v.length; // that means no splitting point
		
		for(int i=0; i<k; i++)
		{
			offspring[0].v[i] = a.v[i];
			offspring[1].v[i] = b.v[i];
		}
		
		for(int i=k; i<offspring[0].v.length; i++)
		{
			//note the inverted order
			offspring[0].v[i] = b.v[i];
			offspring[1].v[i] = a.v[i];
		}
		
				
		return offspring;
	}
	
	
	public static int rankSelectionIndex(double rank_bias, int length)
	{
		//see Whitley
		//http://citeseer.ist.psu.edu/whitley89genitor.html
		
		double r = RandomGenerator.getGenerator().nextDouble();
		double d = rank_bias - Math.sqrt( (rank_bias*rank_bias) -  (4.0*(rank_bias-1.0)*r) );
		d = d /2.0 / (rank_bias - 1.0) ;
		
		//d = 1.0 - d; // to do that if we want to have Maximisation
		
		int index = (int) (length * d);
		return index;
	}

	@Override
	public String getShortName() 
	{
		return "SSGA";
	}
	
}