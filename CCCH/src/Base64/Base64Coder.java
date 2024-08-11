package Base64;
/**
 * BharadwajGhadiam
 * This call is used to decode the Encoded base64code
 */
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;

public class Base64Coder {

	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static final String ALGORITHM = "AES";
	private final static int KEY_LENGTH = 128;

	private BASE64Decoder base64decoder = new BASE64Decoder();

	private byte[] key = new byte[] { -52, 115, 52, -27, 78, 109, 38, 27, 89, 67, -100, 91, -20, 5, 75, -127 };
	private byte[] iv = new byte[] { 1, 73, -41, 117, -16, -34, -95, -103, 29, -33, 117, 92, 8, -53, -106, 40 };

	private SecretKey secretKey = null;

	public Base64Coder()
	{
		try
		{
			secretKey = new SecretKeySpec( key, ALGORITHM );
		}
		catch (Exception e)
		{
			System.out.println("The value of the error message:"+e.getMessage());
			//logger.error( "Error initializing encoder.", e );
		}
	}

	public String decode( String string )
	{
		try
		{
			byte[] base64decoded = this.base64decoder.decodeBuffer( string );
			Cipher cipher = Cipher.getInstance( TRANSFORMATION );
			cipher.init( Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec( iv ) );
			byte[] decoded = cipher.doFinal( base64decoded );
			return new String( decoded );
		}
		catch (Exception e)
		{
			System.out.println( "Error decoding string."+ e.getMessage() );
			return null;
		}
	}

}
