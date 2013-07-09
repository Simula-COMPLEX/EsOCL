package simula.oclga;


public class Individual implements Comparable<Individual>
{
	//package visibility
	int[] v;
	double fitness_value; 
	Problem problem;
	
	public Individual(Problem p)
	{
		problem = p;
	}
	
	public int compareTo(Individual other)
	{
		if(this.fitness_value == other.fitness_value)
			return 0;
		else if(this.fitness_value > other.fitness_value)
			return 1;
		else
			return -1;
	}
	
	public void evaluate()
	{
		fitness_value = problem.getFitness(v);
	}
	
	public Individual getCopy()
	{
		Individual copy = new Individual(problem);
		copy.v = v.clone();
		copy.fitness_value = fitness_value;
		
		return copy;
	}
	
	public void copyDataFrom(Individual other)
	{
		this.problem = other.problem;

		for(int i=0; i<v.length;i++)
			this.v[i] = other.v[i];

		this.fitness_value = other.fitness_value;
	}
	
	public static Individual getRandomIndividual(Problem p)
	{
		Individual ind  = new Individual(p);

		int[][] constraints = p.getConstraints();
		ind.v = new int[constraints.length];
		
		for(int i=0; i<ind.v.length; i++)
		{
			int min = constraints[i][0];
			int max = constraints[i][1];
			int type = constraints[i][2];
			//in this case, type does not matter
			
			int dif = max - min;
			int k = RandomGenerator.getGenerator().nextInt(dif+1);
			ind.v[i] = min + k;
		}
		
		ind.evaluate();
		return ind;
	}
}