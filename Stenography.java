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
		if (args[0].equals("-D")){
			//decrypt
			sten.decrypt(args[1], args[2]);
		}
		else
		{
			try{
				sten.encrypt(args[1],args[2]);
			}catch(IOException e)
			{
				System.out.println("Error Encrypting: "+e.toString());
			}

		}
	}
	
	private boolean cmdline_valid(String[] args){
		if (args.length != 3)
			return false;
		
		if (!args[0].equals("-E") && !args[0].equals("-D"))
			return false;
		
		return true;				
	}

	private String decrypt(String encrypted_image, String output_file)
	{
		BufferedImage img = null;
        try {
            img = ImageIO.read(new File(encrypted_image));
        } catch (IOException e) {
        	System.err.println("image could not be opened");
        }

        String output="";
        for(int row = 0; row < img.getHeight(); row++)
        {
        	for(int column = 0; column < img.getWidth(); column++)
	        {
	        	String[] bitarray = read_3Bytes(img, row, column);

		   	/*Converts the 3 bytes to characters that are added to output string*/
		   		for(int count = 0; count < 3; count++)
		   		{
		   			int val = Integer.parseInt(bitarray[count],2);
		   			if(val != 0)
		   				output+=(char)val;
		   			else
		   			{
		   				System.out.println(output);
		   				return output;
		   			}
		   		}
	   		}
	   	}
		System.out.println();
		return output;
		
	}
	
	private void encrypt(String image, String msg) throws IOException
	{
		BufferedReader br;
		BufferedImage img = null;
        try {
            img = ImageIO.read(new File(image));
        } catch (IOException e) {
        	System.err.println("image could not be opened");
        }
        int height = img.getHeight();
        int width = img.getWidth();
        long amountPixel = height * width;

		// This prints the image height and width and a specific pixel. 
        System.out.println("filename: " +image +"\nnumber of pixels: "+ amountPixel+ "\nheight: "+height  + "\nwidth: " +  width);
	    

	    
	    		FileInputStream fstream = new FileInputStream(msg);
	    		DataInputStream in = new DataInputStream(fstream);
	    		br = new BufferedReader(new InputStreamReader(in));

    	int[] msgChars = new int[3]; //Holds 3 chars
    	boolean end_of_msg = false;
    	int row = 0;
    	int col = 0; 
    	
    	while(!end_of_msg)
    	{
    		//Read 3 characters from message
    		for(int count = 0; count < 3; count++)
    		{
    			msgChars[count] = br.read();
    			
    			if(msgChars[count] == -1)
    			{
    				end_of_msg = true;
    				msgChars[count] = 0;
    				while(count<3)
    				{
    					msgChars[count] = 0;
    					count++;
    				}
    				break;
    			}
    		}

    		/*Write 3 characters to image. Note that 3 characters is 24 bits
    		so we must change 24 integer values which is 8 rgb values or pixels*/
    		String bitarray="";

    		for(int k = 0; k < 3; k++)
    		{
    			String bin = Integer.toBinaryString(msgChars[k]);
    			//Pad 0's on the left
    			bitarray+= ("00000000"+bin).substring(bin.length());
    		}
   			System.out.println(bitarray);
    		for(int k = 0; k < 8; k++)
    		{
    			//Get the RGB value at the current pixel
    			int[] color = convertRGB(img.getRGB(row,col++));
    			if(col==width)
    			{
    				col=0;
    				row++;
    			}
    			//Add the value in the bitstring to either r,g, or b
    			for(int color_count=0; color_count < 3; color_count++)
    			{
    				color[color_count]+=Integer.parseInt(""+bitarray.charAt(k*3+color_count));
    				color[color_count] = color[color_count] % 256;	
    			}
    			//Store the new r,g,b values into the pixel rgb
    			String str_rgb = "00000000"+Integer.toBinaryString(color[0])+
    								Integer.toBinaryString(color[1])+
    									Integer.toBinaryString(color[2]);
    			img.setRGB(col, row, Integer.parseInt(str_rgb,2));
    		}

    	}
	}

	private int[] convertRGB(long rgb)
	{
	    int[] color = new int[3];				
 		color[0] = (int) rgb & 0xFF;
	   	color[1] = (int) (rgb>>8) & 0xFF;
	    color[2] = (int) (rgb>>16) & 0xFF;
	    return color;
	}
	
	private String[] read_3Bytes(BufferedImage img, int row, int column)
	{ //Need to read 24 bits = 24 integer vals
		String[] bitstrings = {"","",""}; //each string should be 8 bits
	//3 bits per rgb * 8 times = 24 bits
	    for(int count=0; count < 8; count++)
	     {
	     	long rgb = img.getRGB(row, column++); //Three bits from rgb
	     	
	     	if(column == img.getWidth())
	     	{
	     		column = 0;
	     		row++;
	     	}

	     	int[] color = convertRGB(rgb);

    		for(int k = 0; k <2; k++)
   			{
   				if(color[k] % 2 == 0)
   					bitstrings[(count*3 + k)/8]+='0';
   				else
   					bitstrings[(count*3 + k)/8]+='1';
   			}
   		}
   		return bitstrings;
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
        long amountPixel = height * width;

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
    			if (bytes[0].equals("00000000"))
    				break;
    			int index = 0;
    			for (int i = 0; i < height; i++){
    				for (int j = 0; j< width; j++){    	
    					if (count != 0 && count %8 == 0){
    						index ++;
    					}
    					long rgb = img.getRGB(j, i);
    					int red = (int) rgb & 0xFF;
    					int green = (int) (rgb>>8) & 0xFF;
    					int blue = (int) (rgb>>16) & 0xFF;
    					System.out.println(red + "; "+green+"; "+ blue);
    					if(bytes[index].charAt(count%8) == '0'){
    						if (count%3 == 1){
    							red = calcPixel(red, 0);
    						}
    						else if (count%3 == 2){
    							green = calcPixel(green, 0);
    						}
    						else if (count%3 == 0){
    							blue = calcPixel(blue,0);				
    						}  				
    						
    					}
    					else if (bytes[index].charAt(count%8) == '1'){
    						if (count%3 == 1){
    							red = calcPixel(red, 1);
    						}
    						else if (count%3 == 2){
    							green = calcPixel(green, 1);
    						}
    						else if (count%3 == 0){
    							blue = calcPixel(blue, 1);				
    						}  
    					}
    					count ++;
    					System.out.println("red: "+red+"green: "+green+"blue: " + blue);
    					if (index == bytes.length-1)
    						break;
    				}
    				if (index == bytes.length-1)
						break;
    			}
    			System.out.println(count);
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
	
	private int calcPixel(int val, int mod){
		if (mod == 0 && val%2 == 1){
			if(val == 255)
				return val --;
			return val +1;
		}
		else if (mod == 1 && val%2 == 0){
			if(val == 255)
				return val --;
			return val +1;
		}
		else return val;
			
	}
}
