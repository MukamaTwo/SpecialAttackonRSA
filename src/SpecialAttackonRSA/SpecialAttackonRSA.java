package SpecialAttackonRSA;
import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;

public class SpecialAttackonRSA {

	public static void main(String[] args) {
		String filename = "/home/jo/input.txt";
		BigInteger[] N = new BigInteger[3];
		BigInteger[] e = new BigInteger[3];
		BigInteger[] c = new BigInteger[3];
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			for (int i = 0; i < 3; i++) {
				String line = br.readLine();
				String[] elem = line.split(",");
				N[i] = new BigInteger(elem[0].split("=")[1]);
				e[i] = new BigInteger(elem[1].split("=")[1]);
				c[i] = new BigInteger(elem[2].split("=")[1]);
			}
			br.close();
		} catch (Exception err) {
			System.err.println("Error handling file.");
			err.printStackTrace();
		}
		BigInteger m = recoverMessage(N, e, c);
		System.out.println("Recovered message: " + m);
		System.out.println("Decoded text: " + decodeMessage(m));

	}

	public static String decodeMessage(BigInteger m) {
		return new String(m.toByteArray());
	}

	
	/**
	 * Returns an array "result" with the values "result[0] = gcd",
	 * "result[1] = s" and "result[2] = t" such that "gcd" is the greatest
	 * common divisor of "a" and "b", and "gcd = a * s + b * t".
	 **/
	public static BigInteger[] EEABigInt(BigInteger a, BigInteger b) {
		// Note: as you can see in the test suite,
		// your function should work for any (positive) value of a and b.
		BigInteger gcd = BigInteger.ZERO;
		BigInteger s = BigInteger.ZERO;
		BigInteger t = BigInteger.ZERO;
		BigInteger[] result = new BigInteger[3];
		BigInteger r0 = BigInteger.ZERO, r1 = BigInteger.ZERO, s0 = BigInteger.ZERO, s1 = BigInteger.ZERO, t0 = BigInteger.ZERO, t1 = BigInteger.ZERO;
		BigInteger r_prev= BigInteger.ZERO, r_curr= BigInteger.ZERO, r_next= BigInteger.ZERO, q= BigInteger.ZERO;   //for the equation
		BigInteger s_prev= BigInteger.ZERO,  s_next= BigInteger.ZERO ;
		BigInteger t_prev= BigInteger.ZERO,  t_next= BigInteger.ZERO ;
		
		//ensure that the first term is bigger
		r0=(a.compareTo(b))>0?a:b;
		r1=(a.compareTo(b))>0?b:a;
		q=r0.divide(r1);         //integer division
				
		//needed to enter the loop
		r_next=r0.subtract((q.multiply(r1)));
		r_curr=r1;
		
		//s values
		s0=(a.compareTo(b)>0)?BigInteger.ONE:BigInteger.ZERO;
		s1=(a.compareTo(b)>0)?BigInteger.ZERO:BigInteger.ONE;
		
		s_prev=s0; 
		s=s1;
		s_next=s_prev.subtract((q.multiply(s)));
		
		//t values
		t0=(a.compareTo(b)>0)?BigInteger.ZERO:BigInteger.ONE;
		t1=(a.compareTo(b)>0)?BigInteger.ONE:BigInteger.ZERO;	

		t_prev=t0; 
		t=t1;
		t_next=t_prev.subtract((q.multiply(t)));
		
		while(r_next.compareTo(BigInteger.ZERO)>0 && r_next.compareTo(r_curr.abs()) < 0){				
			r_prev=r_curr;
			r_curr=r_next;
			q=r_prev.divide(r_curr);
			r_next=r_prev.subtract((q.multiply(r_curr)));	
			
			s_prev=s;
			s=s_next;
			s_next=s_prev.subtract((q.multiply(s)));
			
			t_prev=t;
			t=t_next;
			t_next=t_prev.subtract((q.multiply(t)));
		}
	
		gcd=r_curr;
		
		result[0] = gcd;
		result[1] = s;
		result[2] = t;
		return result;		
		
	}
	
	/**
	 * Tries to recover the message based on the three intercepted cipher texts.
	 * In each array the same index refers to same receiver. I.e. receiver 0 has
	 * modulus N[0], public key e[0] and received message c[0], etc.
	 * 
	 * @param N
	 *            The modulus of each receiver.
	 * @param e
	 *            The public key of each receiver (should all be 3).
	 * @param c
	 *            The cipher text received by each receiver.
	 * @return The same message that was sent to each receiver.
	 */	
	private static BigInteger recoverMessage(BigInteger[] N, BigInteger[] e,
			BigInteger[] c) {
		// TODO Solve assignment.		

		BigInteger m_cubed =BigInteger.ZERO, pq_product = N[0];
		BigInteger n0 = N[0], n1 = N[1], c0 = c[0], c1 = c[1];
		BigInteger s =BigInteger.ZERO, r =BigInteger.ZERO;
		BigInteger[] bezoutResult = new BigInteger[3];	

		System.out.println("Using the Chinese Reminder Theorem");
		
	    for(int i=0; i<N.length-1;i++ ){
			pq_product=pq_product.multiply(N[i+1]); 
			bezoutResult = EEABigInt(n0, n1);
			s=bezoutResult[1];
			r=bezoutResult[2];
			
			m_cubed=(c0.multiply(r).multiply(n1).add(c1.multiply(s).multiply(n0))).mod(pq_product);
						
			n0=N[0].multiply(N[1]);
			c0=m_cubed;
			n1=N[2];			
			c1=c[2];	    	
	    }

	    return CubeRoot.cbrt(m_cubed);
	}

}
