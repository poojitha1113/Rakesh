package InvokerServer;

public class ServerResponseCode {
	public static final int Succes_Login  	               = 200;
	public static final int Success_Get                    = 200;
	public static final int Success_Put                    = 200;
	public static final int Success_Delete                 = 200;
	public static final int Bad_Request                    = 400;
	public static final int Success_Post                   = 201;
	public static final int Not_login                      = 401;
	public static final int Insufficient_Permissions       = 403;
	public static final int Resource_Does_Not_Exist        = 404;
	public static final int Method_Not_Allowed_On_Resource = 405;
	public static final int Server_Internal_Error          = 500;
}
