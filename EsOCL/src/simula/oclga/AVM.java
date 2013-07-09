package simula.oclga;

public class AVM extends Search 
{
	@Override
	public int[] getSolution(Problem problem)
	{	
		Individual best = Individual.getRandomIndividual(problem);
		best.evaluate();
		this.increaseIteration();

		if(best.fitness_value == 0d)
			return best.v;

		//init first generation
		while(!this.isStoppingCriterionFulfilled())
		{
			Individual current = Individual.getRandomIndividual(problem);
			current.evaluate();
			this.increaseIteration();

			if(current.fitness_value == 0d)
				return current.v;

			applyAVMSearch(current);

			if(current.fitness_value == 0d)
				return current.v;

			if(current.fitness_value < best.fitness_value)
			{
				best.copyDataFrom(current);
				reportImprovement();				
			}
		}

		return best.v;		
	}

	protected void applyAVMSearch(Individual ind)
	{
		int[][] cons = ind.problem.getConstraints();
		final int[] directions = new int[]{-1,+1};

		int last_improvement_index = -1;

		while(!this.isStoppingCriterionFulfilled())
		{
			loop_on_variables : for(int i=0; i<ind.v.length; i++)
			{
				int index = (last_improvement_index+1+i)  %  ind.v.length; 

				//----------------------------------------------
				if(cons[index][2]==Problem.CATEGORICAL_TYPE)
				{
					//try all the other values

					int current_value = ind.v[index];
					double current_fitness = ind.fitness_value;

					boolean improved = false;

					loop_on_values : for(int value=cons[index][0]; value<=cons[index][1]; value++)
					{
						if(value == current_value)
							continue;

						ind.v[index] = value;

						ind.evaluate();
						this.increaseIteration();

						if(ind.fitness_value == 0d)
							return;

						//is it better?
								if(ind.fitness_value < current_fitness)
								{
									improved = true;
									break loop_on_values;
								}
								else
								{
									//undo the change
									ind.fitness_value = current_fitness;
									ind.v[index] = current_value;
								}
					}


					//it was possible to improve fitness by changing the value at v[index]
					if(improved)
					{						
						last_improvement_index = index;
						break loop_on_variables;
					}
				}
				//----------------------------------------------
				else //Numerical
				{
					boolean improved = true;

					while(improved)
					{
						improved = false;
						
						for(int d : directions)
						{
							//exploratory search
							double current_fitness = ind.fitness_value;

							ind.v[index] = ind.v[index] + d;

							ind.evaluate();
							this.increaseIteration();

							if(ind.fitness_value == 0d)
								return;

							//is it better?
							if(ind.fitness_value < current_fitness)
							{
								current_fitness = ind.fitness_value;
								improved = true;
							}
							else
							{
								//undo change
								ind.v[index] = ind.v[index] - d;
								ind.fitness_value = current_fitness;
							}

							if(improved)
							{
								//exploration in one direction was successful

								int delta = 2;

								while(!this.isStoppingCriterionFulfilled())
								{
									current_fitness = ind.fitness_value;

									ind.v[index] = ind.v[index] + (d * delta);

									ind.evaluate();
									this.increaseIteration();

									if(ind.fitness_value == 0d)
										return;

									//is it better?
									if(ind.fitness_value < current_fitness)
									{
										delta = delta * 2;
									}
									else
									{
										//undo change
										ind.v[index] = ind.v[index] - (d*delta);
										ind.fitness_value = current_fitness;
										break;
									}
								}
							}	
						}
					}
				}
			}
		}
	}


	@Override 
	public String getShortName()
	{
		return "AVM";
	}
}
