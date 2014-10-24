import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

public class Stenography {
	
	public static void main(String args[]){
		Stenography sten = new Stenography();
		if (! sten.cmdline_valid(args)){
			System.err.println("Please enter valid commands");
			return;
		}
		sten.readFiles(args[1],args[2]);
	}
	
	private boolean cmdline_valid(String[] args){
		if (args.length != 3)
			return false;
		
		if (!args[0].equals("-E") && !args[0].equals("-D"))
			return false;
		
		return true;				
	}
	
	
	private void readFiles(String image, String msg){
		BufferedImage img = null;
        try {
            img = ImageIO.read(new File(image));
        } catch (IOException e) {
        	System.err.println("image could not be opened");
        }
        int height = img.getHeight();
        int width = img.getWidth();
        
        int amountPixel = height * width;

	// This prints the image height and width and a specific pixel. 

        System.out.println("filename: " +image +"\nnumber of pixels: "+ amountPixel+ "\nheight: "+height  + "\nwidth: " +  width);
        
        try{
    		FileInputStream fstream = new FileInputStream(msg);
    		DataInputStream in = new DataInputStream(fstream);
    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		String strLine;
    		int count = 0;
    		do{
    			strLine = br.readLine();
    			String[]bytes = getLineByte(strLine);
    			int index = 0;
    			for (int i = 0; i < height; i++){
    				for (int j = 0; j< width; j++){
    					if(bytes[index].charAt(count%8) == '0'){
    						if (count%3 == 1){
    							
    						}
    						else if (count%3 == 2){
    							
    						}
    						else if (count%3 == 0){
    							
    						}
    							
    					}
    						
    				}
    			}
    		}while (strLine != null);
    		in.close();
    		}catch (IOException e){
    			System.err.println("Error: "+ e.toString());
    		}
    	}
	
	private String[] getLineByte(String line){
		
		if (line == null){
			String[] binary = new String[1];
			binary[0] = "00000000";
			System.out.println(binary[0]);
			return binary;
		}
		
		byte[] bytes = line.getBytes();
		String[] binary = new String[bytes.length];
		String pad = String.format("%0" + 8 + 'd', 0);
		
		for (int i = 0; i < bytes.length; i++){
			
			String s = Integer.toBinaryString(bytes[i]);
			s = pad.substring(s.length()) + s;
			binary[i] = s;
			System.out.println(s);
		}
		return binary;
		
	}
}
