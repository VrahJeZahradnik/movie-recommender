package com.recommender.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PreParser {
	
	public PreParser(){
		parse();
	}

	public void parseFile(String fileName, String fileOName) throws IOException{
		
		File file = new File(fileName + CSFDParser.SUFFIX);
		File fileOut = new File(fileOName + CSFDParser.SUFFIX);
		
		BufferedReader in  = new BufferedReader(new FileReader(file));
		BufferedWriter out  = new BufferedWriter(new FileWriter(fileOut));
		
		String line = null;
		line = in.readLine();

		while (line != null) {
			line = line.replaceAll("[\u0000-\u0008]|[\u000e-\u001f]", "");
//			line = line.replaceAll("(?=\\p{Cc})(?=[^\r\n\t]+)", "");
			out.write(line + "\n");
			line = in.readLine();
			
		}

		in.close();
		out.close();
	}
	
	public void parse(){
		String name = CSFDParser.SRC_DIR + CSFDParser.FILE_NAME;
		String out = CSFDParser.PARSED_DIR + CSFDParser.FILE_NAME ;
		
		for (int i = 0; i < 11; i++) {
			try {
				parseFile(name + i, out + i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		new PreParser();
	}
}
