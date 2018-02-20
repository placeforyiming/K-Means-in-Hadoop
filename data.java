import java.io.*;
import java.util.Random;


class WriteAFile{
	public static void main(String[] args){
		try{
			BufferedWriter writer = null;
			FileWriter II = new FileWriter("Location.csv");
			writer = new BufferedWriter(II);
			Random rand = new Random();

			
			for (int i=0; i<10000000;i++){
				int  n = rand.nextInt(10000) ;
				String X = String.valueOf(n);
				writer.write(X);

				writer.write(",");
				int  m = rand.nextInt(10000) ;
				String Y = String.valueOf(m);
				writer.write(Y);
				writer.newLine();
			}
			writer.close();
		} catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
}


