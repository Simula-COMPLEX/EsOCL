package simula.oclga;

public class FakeProblem implements Problem 
{

	@Override
	public int[][] getConstraints() 
	{
		int[][] m = new int[][]{
				new int[]{-1000,1000,0},
				new int[]{-1000,1000,0},
				new int[]{0,10,1},
				new int[]{0,20,1}
		};
		
		return m;
	}

	@Override
	public double getFitness(int[] v) 
	{
		double sum = 0;
		
		//v[0] == c0;
		int c0 = -47;
		if(v[0]!= c0)
			sum += Math.abs(v[0]-c0) + 1;
		
		//v[1]>900
		int c1 = 900;
		if(v[1] <= c1)
			sum += (c1 - v[1]) + 1; 
		
		if(v[2] != 4)
			sum += 1;
		
		if(v[3] != 17)
			sum += 1;
		
		return sum;
	}

}
