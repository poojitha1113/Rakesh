package genericutil;
import java.util.Properties;
import java.util.Enumeration; 
import java.io.*;

public class propertyloader 
{     
	private Properties prop;
	private File prop_file = null;
	private boolean debug = false;
       
    public propertyloader ( File propFile )
    {
		prop = new Properties(); 
		this.prop_file = propFile ;
		loadProperties ();
		if (debug) printProperties ();       
    }

	public propertyloader ( )
	{
	}
    private void printProperties() 
    {
        System.out.println();
        System.out.println("========================================");
        System.out.println("|         Properties        |");
        System.out.println("========================================");
        prop.list(System.out);
        System.out.println();
    }
  
    private void loadProperties () 
    {
        InputStream propsFile;        
        try
		{
            propsFile = new FileInputStream ( prop_file );
            prop.load ( propsFile );
            propsFile.close( );
        } 
        catch ( IOException ioe ) 
        {
            System.out.println("I/O Exception.");
            ioe.printStackTrace();
            System.exit(0);
        }
    }
 	public String getProperty( String property_name )
	{
		 String value = prop.getProperty( property_name );
		 return value;
	}
	public void writeProperty( String property_name, String property_val )
	{
		//System.out.println( "property_name:" + property_name );
		//System.out.println( "property_val:" + property_val );
		try{
			FileOutputStream op_file = new FileOutputStream( prop_file );
			prop.setProperty( property_name, property_val );
			prop.store( op_file, "......Debug File Information....." );
		}
		catch( Exception e )
		{
		  System.out.println( "propertyloader:writeProperty" + e);
		}
	} 
	public void printUsage()
	{
		System.out.println("java propertyloader ");
		System.out.println(" -setproperty <prop_file> <prop_name> <prop_val> ");
		System.out.println(" -getproperty <prop_file> <prop_name>");
	}   
	public static void main (String  [] args) 
	{             
		   File prop_file = null;
		   propertyloader pl = new propertyloader(); 
		   try   
		   {
				if( args.length < 1 )
					pl.printUsage( );
				if( args[ 0 ].equalsIgnoreCase( "-setproperty" ) )
				{
					String propfile = args[1];
					prop_file = new File ( propfile );					 
                    if( !prop_file.exists() )
                    {
					 boolean create_file = prop_file.createNewFile();
 					 if( !create_file )
						System.out.println("Error in creating the file");
					}
					pl  = new propertyloader ( prop_file );
					String prop_name = args[2];
					String prop_val = args[3];
					pl.writeProperty( prop_name, prop_val );
				}
				else if( args[0].equalsIgnoreCase( "-getproperty" ) )
				{
					String propfile = args[1];
					prop_file = new File ( propfile );
					pl  = new propertyloader ( prop_file );  
					String prop_name = args[2];
					//String prop_val = args[3];
					String value = pl.getProperty( prop_name );
					System.out.print(value);
				}
			}                
			catch ( Exception e ) 
			{
				System.out.println( "propertyLoader: main: " + e );
			} 
	}

}
