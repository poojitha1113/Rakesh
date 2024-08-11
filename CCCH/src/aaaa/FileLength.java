package aaaa;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileLength {

	public static void main(String[] args) throws IOException {
		
		
		File f = new File("C:\\Users\\Rakesh.Chalamala.ARCSERVE\\Documents\\ABCD.txt");
		
		byte[] b = new byte[(int) f.length()];
		BufferedInputStream bf = new BufferedInputStream(new FileInputStream(f));
		bf.read(b);
		
		
		bf.close();

	}

}
