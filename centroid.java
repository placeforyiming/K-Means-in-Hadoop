import java.io.*;
import java.util.Random;


class WriteCentroid{
	public static void main(String[] args){
		try{
			BufferedWriter writer = null;
			FileWriter II = new FileWriter("centroid.csv");
			writer = new BufferedWriter(II);
			Random rand = new Random();
			int K = Integer.valueOf(args[0]) ;
			
			for (int i=0; i<K;i++){
				String Index = String.valueOf(i);
				writer.write(Index);
				writer.write("\t");

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


