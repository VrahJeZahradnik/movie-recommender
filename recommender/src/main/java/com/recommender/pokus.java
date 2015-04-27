package com.recommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import com.google.common.io.ByteStreams;

public class pokus {
	
	DataModel model;
	Recommender recommender = null;
	List<String> movies, users;
	Map<Integer, List<String>> ratings = null;
	
	public pokus() {
		try {
			model = new FileDataModel(new File("src/main/resources/data/20.dat"));
			UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
			UserNeighborhood nbh = new ThresholdUserNeighborhood(0.8, similarity, model);
			recommender = new GenericUserBasedRecommender(model, nbh, similarity);
			
			getRatingsFromFile("src/main/resources/data/20.dat");
//			movies = new ArrayList<String>(getDataFromFile("src/main/resources/data/movies.dat"));
//			users = new ArrayList<String>(getDataFromFile("src/main/resources/data/users.dat"));
			
			System.out.println(recommender.recommend(100000, 10));
//			System.out.println(recommender.recommend(1, 30));
//			System.out.println(ratings.get(20200));
//			sendMessage(ratings.get(0), "movies");
		} catch (IOException e) {
			e.printStackTrace();
		} 
		catch (TasteException e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessage(List<String> data, String name) throws IOException {
		System.out.print("{\"" + name + "\":[");
		for (int i = 0; i < data.size(); i++) {
			if (i > 0) {
				System.out.print(',');
			}
			System.out.print("{\"name\":\"" + data.get(i) + "\",\"id\":\"" + i + "\"}");
        }
		System.out.println("]}");
	}
	
	private void getRatingsFromFile(String file) throws IOException {
		ratings = new HashMap<Integer, List<String>>();
		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
		List<String> user = new ArrayList<String>();
		String[] values;
		String line;
		int id = 0;
		while ((line = reader.readLine()) != null) {
			if (line.length() == 0) {
				ratings.put(id, user);
				user = new ArrayList<String>();
			} else {
				values = line.split(",");
				id = Integer.parseInt(values[0]);
				user.add(values[1] + " " + values[2]);
			}
        }
		reader.close();
	}
	
	private ArrayList<String> getDataFromFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
		ArrayList<String> data = new ArrayList<String>();
		String line;
		while ((line = reader.readLine()) != null) {
			data.add(line);
        }
		reader.close();
		return data;
	}
	
	public static void main(String[] args) throws TasteException, IOException {
		new pokus();
	}
}
