package Base64;
import Base64.Base64Coder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
/*
 * BhardwajGhadiam
 * Decoding the Base64Code
 */
public class EncodingBase64 {

	public String DecodeSiteRegistrationKey(String base) {
	      try {
	       
	        String decoded = URLDecoder.decode( base.trim(), "UTF-8" );
	        Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode( decoded );
			String[] parts = decrypted.split( "\\n", -2 );
			String registration_key=parts[1];
			return registration_key;
	    }  catch(UnsupportedEncodingException e ){
	    	System.out.println("The value of the error Message :"+e.getMessage());
	    	return null;
	    }

   }
}
