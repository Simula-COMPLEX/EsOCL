package simula.oclga;

import java.util.Random;

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
	
	public static  String getRandomString(int length) 
	{ 
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";  
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < length; i++)
	    {   
	        int number = random.nextInt(base.length());   
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString();   
	 }
	
	/*
	 *  Random generate next double
	 */
	public static Double nextDouble(int scope)
	{
		return random.nextDouble()%scope;
	}
	
    public static double getDouble(byte[] b) {
	    long l;
		l = b[0];
		l &= 0xff;
		l |= ((long) b[1] << 8);
		l &= 0xffff;
		l |= ((long) b[2] << 16);
	    l &= 0xffffff;
		l |= ((long) b[3] << 24);
		l &= 0xffffffffl;
		l |= ((long) b[4] << 32);
		l &= 0xffffffffffl;
	    l |= ((long) b[5] << 40);
		l &= 0xffffffffffffl;
		l |= ((long) b[6] << 48);
		l &= 0xffffffffffffffl;
		l |= ((long) b[7] << 56);
		  return Double.longBitsToDouble(l);
		}
}
