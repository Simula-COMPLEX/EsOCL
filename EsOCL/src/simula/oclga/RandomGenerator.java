package simula.oclga;

public class RandomGenerator
{
	private static java.util.Random random = new MersenneTwister(System.currentTimeMillis());
	
	
	public static java.util.Random  getGenerator()
	{
		return random;
	} 
	
	
	public static void setSeed(long seed)
	{
		random.setSeed(seed);
	}

	public static String nextString(int max_length)
	{
		int l = random.nextInt(max_length);
		
		byte[] buffer = new byte[l];
		
		random.nextBytes(buffer);
		
		return new String(buffer);
	}
}
