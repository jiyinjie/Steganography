import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

public class Steganography {
	private String filename;
	private String extension;
	
	
	public static void main(String args[]){
		Steganography sten = new Steganography();
		if (! sten.cmdline_valid(args)){
			System.err.println("Please enter valid commands");
			return;
		}
		if (args[0].equals("-D")){
			//decrypt
			sten.filename = args[2];
			try{					
				File v_file = new File(sten.filename);
				FileOutputStream fos = new FileOutputStream(v_file);
				PrintStream out = new PrintStream(fos);
				System.setOut(out);
				sten.decrypt(args[1], args[2]);
				
			} catch(IOException e){
				System.err.println("Writing Error");
			}
			
		}
		else
		{
			sten.filename = args[1].substring(0, args[1].indexOf('.'));
			sten.extension = args[1].substring(args[1].indexOf('.')+1, args[1].length());
			try{
//				File v_file = new File("input.txt");
//				FileOutputStream fos = new FileOutputStream(v_file);
//				PrintStream out = new PrintStream(fos);
//				System.setOut(out);
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
        int column = 0;
        int row = 0;
        int[] dimensions = {row, column};
        while(row < img.getHeight())
	       	{
                /*bitarray will contain the bitstring representation for an
                ASCII character*/
	        	String[] bitarray = read_3Bytes(img, row, column);

		   	/*Converts the 3 bytes to characters that are added to output string*/
		   		for(int count = 0; count < 3; count++)
		   		{
                    //val contains the ASCII character's decimal code
		   			int val = Integer.parseInt(bitarray[count],2);

                    //If the character is the NULL character, then end
		   			if(val != 0)
		   			{
		   				output+=(char)val;
		   			}
		   			else
		   			{
		   				System.out.println(output);
		   				return output;
		   			}
		   		}

                //3 bytes in the image is 24 bits. Move columns over by 
                //8 pixels so we move 24 bits because each pixel contains 
                //3 bits of encrypted info
                dimensions=pixelArithmetic(row, column, img.getHeight(), img.getWidth(), 8);
                row = dimensions[0];
                column = dimensions[1];
	   		}
	   	//System.out.println(output);
		return output;
		
	}
	private int[] pixelArithmetic(int row, int column, int height, int width, int pixels)
    {
        int[] rc = {row, column};
        if(column+pixels >= width)
        {
            column+=pixels%width;
            row+=pixels/width;

            if(column >= width)
            {
                column -= width;
                row++;
            }
            rc[0] = row;
            rc[1] = column;
        }
        else
        {
            column+=pixels;
            rc[0] = row;
            rc[1] = column;
        }
        return rc;
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
	    
        //Need at least 3 pixels to store one character
        if(amountPixel < 3)
        {
        	System.out.println("File too small");
        	return;
        }
	    
        FileInputStream fstream = new FileInputStream(msg);
	    DataInputStream in = new DataInputStream(fstream);
	    br = new BufferedReader(new InputStreamReader(in));


    	int[] msgChars = new int[3]; //Holds 3 chars by their ASCII values
    	boolean end_of_msg = false;
    	int row = 0;
    	int col = 0; 
    	while(!end_of_msg)
    	{
    		//Read 3 characters from message and converts it to ASCII value
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
    			//Convert ASCII value to a string of the binary representation
    			String bin = Integer.toBinaryString(msgChars[k]);
    			
    			//Pad 0's on the left
    			bitarray += ("00000000"+bin).substring(bin.length());

    		}
    		
            //System.out.println("Bitarray: "+bitarray);
   			
    		for(int k = 0; k < 8; k++)
    		{
    			int[] color;

    			//If we run out of space...
    			if(col >= (width-1) && row >= (height-1))
    			{
    				//Exceeded capacity must set last 2.66 pixels (8 bits) to 0
    				//Third bit to be changed: row= height-1; col=width-1;
    				row = height - 1;
    				col = width - 1;
    				color = convertRGB(img.getRGB(col,row));
    				img.setRGB(col, row, calc_RGB(color,0,0,0));

    				//Second bit
    				col--;
    				if(col<0)
    				{
    					row--;
    					col=0;
    				}
    				color = convertRGB(img.getRGB(col,row));
    				img.setRGB(col, row, calc_RGB(color,0,0,0));

    				//First bit
    				col--;
    				if(col<0)
    				{
    					row--;
    					col=0;
    				}
    				color = convertRGB(img.getRGB(col,row));
    				img.setRGB(col, row, calc_RGB(color,color[0],0,0));
    				br.close();
    				System.err.println("message has been truncated");
    				String outputName = filename+"-steg";
    		    	File output_file = new File(outputName+"."+extension);
    		    	ImageIO.write(img,extension,output_file);
    				return;
    			}

    			//Get the R,G,B values at the current pixel
    			color = convertRGB(img.getRGB(col,row));
    			
    			//System.out.println("Row: "+row+" Col: "+col);
//    			System.out.println("Old Red: "+color[0]);
//     			System.out.println("Old Green: "+color[1]);
//    			System.out.println("Old Blue: "+color[2]);
               // System.out.println("<---------------------------->");
  
    			//Add the value in the bitstring to either r,g, or b
    			//r,g,b will either be a 0 or 1
    			int r = Integer.parseInt(""+bitarray.charAt(k*3+0));
    			int g = Integer.parseInt(""+bitarray.charAt(k*3+1));
    			int b = Integer.parseInt(""+bitarray.charAt(k*3+2));
    			

    			//Store the new r,g,b values into the pixel rgb
    			int new_rgb_value = calc_RGB(color,r,g,b);
    			img.setRGB(col, row, new_rgb_value);
//    			System.out.println("New Red: "+ color[0]);
//    			System.out.println("New Green: "+color[1]);
//    			System.out.println("New Blue: "+ color[2]);
    			int[] dimensions = pixelArithmetic(row,col,height,width,1);
                row = dimensions[0];
                col = dimensions[1];
    		}

    	}
    	in.close();
    	String outputName = filename+"-steg";
    	File output_file = new File(outputName+"."+extension);
    	ImageIO.write(img,extension,output_file);
	}

	private int calc_RGB(int[] rgb, int r, int g, int b)
	{
		rgb[0]=calc_color(rgb[0],r);
		rgb[1]=calc_color(rgb[1],g);
		rgb[2]=calc_color(rgb[2],b);   						
   								
        String bits0 = Integer.toBinaryString(rgb[0]);
        String bits1 = Integer.toBinaryString(rgb[1]);
        String bits2 = Integer.toBinaryString(rgb[2]);
        String str_rgb = "00000000";
        str_rgb += ("00000000"+bits2).substring(bits2.length());
        str_rgb += ("00000000"+bits1).substring(bits1.length());
        str_rgb += ("00000000"+bits0).substring(bits0.length());

   		
   		int new_rgb_value = Integer.parseInt(str_rgb,2);
   		/*System.out.println("RGBvalue: "+Integer.toBinaryString(rgb[0]).charAt(7)+
            Integer.toBinaryString(rgb[1]).charAt(7)+
            Integer.toBinaryString(rgb[2]).charAt(7));*/
   		
        return new_rgb_value;
	}
	private int calc_color(int old_color, int value){
		if(old_color%2 != value%2)
		{
			if(old_color == 255)
				old_color-= 1;
			else
				old_color+= 1;
			//System.out.println(old_color + " bit value: "+value);
		}
		
		return old_color;
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
	     	long rgb=0;
	     	if(column < img.getWidth() && row < img.getHeight())
	     	{
	     		rgb = img.getRGB(column, row); //Three bits from rgb
	     	}
	     	else
	     	{
	     		if(bitstrings[0].equals(""))
	     			bitstrings[0]="00000000";
	     		else if(bitstrings[1].equals(""))
	     			bitstrings[1]="00000000";
	     		else
	     			bitstrings[2]="00000000";	     		
	     		break;
	     	}

	     	int[] color = convertRGB(rgb);

    		for(int k = 0; k <3; k++)
   			{
   				if(color[k] % 2 == 0)
   					bitstrings[(count*3 + k)/8]+='0';
   				else
   					bitstrings[(count*3 + k)/8]+='1';
   			}
   		

        int[] dimensions = pixelArithmetic(row, column, img.getHeight(), img.getWidth(), 1);
   		row = dimensions[0];
        column = dimensions[1];

   		}
   		

/*  	System.out.println(bitstrings[0] + " "+ (char)Integer.parseInt(bitstrings[0], 2));
   		System.out.println(bitstrings[1] + " "+ (char)Integer.parseInt(bitstrings[1], 2));
   		System.out.println(bitstrings[2] + " "+ (char)Integer.parseInt(bitstrings[2], 2));
   		System.out.println();*/

//   		System.out.print(bitstrings[0] +"\t");
//   		System.out.print(bitstrings[1] +"\t");
//   		
//   		System.out.print(bitstrings[2] + "\n");
   		//System.out.println();

   		return bitstrings;
	}
}
