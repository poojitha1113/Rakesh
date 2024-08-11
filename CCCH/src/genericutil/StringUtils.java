package genericutil;

import java.util.UUID;

public class StringUtils {
	public static String repeatString(String s,int count){
	    StringBuilder r = new StringBuilder();
	    for (int i = 0; i < count; i++) {
	        r.append(s);
	    }
	    return r.toString();
	}
	
	/**
	 * Check whether the given String is Empty or Null
	 * @param target the target String to check
	 * @return a boolean value
	 */
	public static boolean isEmptyOrNull(String target){		
		if (target == null || target.equals("") || target.trim().equals(""))
			return true;
		return false;
	}
	
	/**
	 * @author Rakesh.Chalamala
	 * Checks whether the given String is UUID or not
	 * @param uuid
	 * @return
	 */
	public static boolean isUUID(String uuid) {
		try{
		    UUID.fromString(uuid);
		    return true;
		} catch (IllegalArgumentException exception){
			return false;
		}
	}
}
