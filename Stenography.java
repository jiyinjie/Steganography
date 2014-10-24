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
	
	private String decrypt(String encrypted)
	{
		BufferedImage img = null;
        try {
            img = ImageIO.read(new File(image));
        } catch (IOException e) {
        	System.err.println("image could not be opened");
        }

        
		//byte[] input = new byte[8];
		String bitarray="";
		String output="";
		char newest = 0;
		do{
			for(int filled=0; filled <8; filled++) //need to read 8 bits
			{
				for(int k=0; k<encrypted.length(); k+=32) //every int (4 bytes or 32 bits) read is one bit
				{
					int input = Integer.parseInt(encrypted.substring(k,k+31),2);
					if(input%2 == 0)
					{
						bitarray+="0";
					}
					else
					{
						bitarray+="1";
					}
				}

			}
			newest=(char)Integer.parseInt(bitarray,2);
			output+=newest;

		}while(newest!= 0);
		System.out.println(output);
		return output;
		
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
    					int rgb = Math.abs(img.getRGB(j, i));
    					int red = rgb & 0xFF;
    					int green = (rgb>>8) & 0xFF;
    					int blue = (rgb>>16) & 0xFF;
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
