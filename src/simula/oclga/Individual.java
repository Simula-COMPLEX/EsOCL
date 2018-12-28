package simula.oclga;

import java.util.ArrayList;
import java.util.Random;

import simula.ocl.distance.ValueElement4Search;
import simula.oclga.GeneValueScope.EncodedConstraintType;


public class Individual implements Comparable<Individual>
{
	int[] v;    //Array is used to store the value of the constraints, this value is generated according to the array valueofconstraints[][]
	               //This array is also used to represent the chromosome
	
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
		//System.out.println("error");
		fitness_value = problem.getFitness(problem.decoding(this.v));
	}
	
	public ArrayList<GeneValueScope> getEecodedConstraints()
	{
		return this.problem.getGeneConstraints();
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
	
	/*
	 * Random generate the individual according to the problem
	 * Invoke the RandomGenerator function to generate the corresponding number
	 */
	public static Individual getRandomIndividual(Problem p)
	{
		Individual ind  = new Individual(p);
		randomGenerateValue(ind);
		ind.evaluate();
		return ind;
	}
	
	/*
	 */
	public static Individual randomGenerateValue(Individual ind)
	{
		//compute the length of individual
		ind.v = new int[ind.problem.getGeneConstraints().size()];
		
		for(int i=0; i<ind.problem.getGeneConstraints().size(); i++)
		{
			GeneValueScope geneValue = ind.problem.getGeneConstraints().get(i);
			ind.v[i] = randomGenerateValue(geneValue);
		}
		
		return ind;
	}
	
	/*
	0 represent types of int
    1 represent types of boolean and enumeration
    2 represent types of string
    3 represent types of double
    */
	public static int randomGenerateValue(GeneValueScope constraint)
	{
		int value = 0;
		int min = constraint.getMinValue();
		int max = constraint.getMaxValue();
		int type = constraint.getType();
		
		int scople = max - min;
		int k = RandomGenerator.getGenerator().nextInt(scople+1);
		value = min + k;
		
		return value;
	}
	
	
}