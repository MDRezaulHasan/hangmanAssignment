package se.hangman.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public final class ReadData {

	private String fileName; 
	private ExecutorService exe = null;
	public  ReadData(String str) {
		this.fileName = str;
		exe = Executors.newFixedThreadPool(1);
	}
 
	public  List<String> getData() {
		
		CompletableFuture<List<String>> f = CompletableFuture.supplyAsync(() -> {
		    return loadFile();
		}, exe);
		
		try {
			return f.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	
	private List<String> loadFile()  {
		List<String> listOfWords = new ArrayList<String>();
		
//		try {
//			URI uri = Main.class.getResource(this.fileName).toURI();
//			File file = new File(uri);
//			FileReader fileReader = new FileReader(file);
//			BufferedReader bufferedFileReader = new BufferedReader(fileReader);
//			String line = bufferedFileReader.readLine();
//			while(line != null) {
//				listOfWords.add(line.toLowerCase());
//				line = bufferedFileReader.readLine();
//			}
//			bufferedFileReader.close();
//			fileReader.close();
//		} catch (Exception e) {
//			System.out.println("Cound not read file");
//		}
		
		
		
		
		try {
			InputStream inputStream = Main.class.getResourceAsStream(this.fileName);
			InputStreamReader in = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(in);
			String line;
			while ((line = br.readLine()) != null) {
				listOfWords.add(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listOfWords;
	}
}
