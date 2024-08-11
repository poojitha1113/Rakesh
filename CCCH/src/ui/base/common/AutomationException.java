/**
 * 
 */
package ui.base.common;

/**
 * @author Administrator
 *
 */
public class  AutomationException extends RuntimeException{
    private String errorMsg = "";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8969406900744447928L;

	public AutomationException(String errorMessage){	
		super(errorMessage);
		
		errorMsg = errorMessage;
	}
	
	public String getErrorMsg() {
	    return this.errorMsg;
	}
}
