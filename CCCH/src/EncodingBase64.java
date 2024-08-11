import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import Base64.Base64Coder;

public class EncodingBase64 {

	 public static void main(String[] args)  {  
	      try {
	        String eStr1="9h16K6XJ2HK%2Fn6ABS0%2BLlgFpq%2BUMCyjuYg9U6U1JxXh37Dj7Omw7lUYJwkjTyINKu7yiVlgCulch%0AtiVXCGH2Epid%2BFEFxyYidNLuLbnWRv6jsl0ei0ETyj1bwuRBFtPJ7HZmQ%2Bp7NlOqWS3dYUM6wNId%0AY0C3bvj74LVuLOv7ZoGe5paWUYr1qhMRACC%2Bg5iQ";
	        String decoded = URLDecoder.decode( eStr1.trim(), "UTF-8" );
	        Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode( decoded );
			String[] parts = decrypted.split( "\\n", -2 );
			System.out.println("Decoded message: "+parts[1]);   
			System.out.println("Decoded message:"+parts[0]);
			System.out.println ("Decoded Message:"+parts[2]);
			System.out.println("Decoded Message:"+parts[3]);
	      
	    }  catch(UnsupportedEncodingException e ){
	    	System.out.println("The value of the error Message :"+e.getMessage());
	    }

     }//fc7d6617-631a-4a7e-8c40-4931e0219a72
}
