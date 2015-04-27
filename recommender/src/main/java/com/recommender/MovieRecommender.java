package com.recommender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import com.google.common.io.ByteStreams;

@SuppressWarnings("serial")
public class MovieRecommender extends HttpServlet {

	private static final String DATA_FILE = "data";
	private static final String RES_FILE = "/WEB-INF/classes/data/20.dat";
	private static final String MOVIES_FILE = "/WEB-INF/classes/data/movies.dat";
	private static final String USERS_FILE = "/WEB-INF/classes/data/users.dat";
	private static final int REFRESH_TRESHOLD = 20;

	private DataModel model;
	private Recommender recommender;
	private Map<Long, String> movies, users;
	private int counter;
	
	public MovieRecommender() throws TasteException, IOException {
		super();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		counter = 0;
		try {
			model = new FileDataModel(loadResourcesToFile(RES_FILE));
			UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
			UserNeighborhood nbh = new ThresholdUserNeighborhood(0.8, similarity, model);
			recommender = new GenericUserBasedRecommender(model, nbh, similarity);
			
			movies = new HashMap<Long, String>(getDataFromFile((MOVIES_FILE)));
			users = new HashMap<Long, String>(getDataFromFile((USERS_FILE)));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TasteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		long userID = 0;
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		try {
			if (Boolean.parseBoolean(request.getParameter("getusers"))) {
				sendMessage(response, users, "users");
			} else if (Boolean.parseBoolean(request.getParameter("getmovies"))) {
				sendMessage(response, movies, "movies");
			} else if (Boolean.parseBoolean(request.getParameter("refresh"))) {
				counter += REFRESH_TRESHOLD;
				checkForRefresh();
			}
			
			String userIDString = request.getParameter("userID");
			if (userIDString != null) {
//				throw new ServletException("userID was not specified");
				userID = Long.parseLong(userIDString);
				String howManyString = request.getParameter("howMany");
				String itemID = request.getParameter("itemID");
				String value = request.getParameter("value");
				if (howManyString != null) {
					List<RecommendedItem> items = recommender.recommend(userID, Integer.parseInt(howManyString));
					sendRecommended(response, items);
				} else if (itemID != null && value != null) {
					sendRatingAck(response, userID, Long.parseLong(itemID), Float.parseFloat(value));
				} else {
					sendMovies(response, userID);
				}
			}
		} catch (TasteException te) {
			throw new ServletException(te);
		} catch (IOException ioe) {
			throw new ServletException(ioe);
		}

	}

	private void sendRatingAck(HttpServletResponse response, long userID, long itemID, float value) {
		PrintWriter writer = null, pw;
		try {
			writer = response.getWriter();
			File file;
			file = new File(System.getProperty("java.io.tmpdir") + File.separator + DATA_FILE + ".1.tmp");
			file.deleteOnExit();
	//			file.createNewFile();
			pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
			pw.println(userID + "," + itemID + "," + value);
			pw.close();
			checkForRefresh();
			writer.println("ok");
		} catch (IOException e) {
			writer.println("fail");
		}
	}
	
	private void checkForRefresh() {
		if (++counter >= REFRESH_TRESHOLD) {
			model.refresh(null);
			counter = 0;
		}
	}

	private void sendMessage(HttpServletResponse response, Map<Long, String> data, String name) throws IOException {
		PrintWriter writer = response.getWriter();
		writer.print("{\"" + name + "\":[");
		int i = 0;
		for (Map.Entry<Long, String> entry : data.entrySet()) {
			if (i > 0) {
				writer.print(',');
			}
            writer.print("{\"name\":\"" + entry.getValue() + "\",\"id\":\"" + entry.getKey() + "\"}");
            i++;
        }
		writer.println("]}");
	}
	
	private void sendMovies(HttpServletResponse response, Long userID) throws IOException {
		PrintWriter writer = response.getWriter();
		PreferenceArray prefs;
		try {
			prefs = model.getPreferencesFromUser(userID);
			writer.print("{\"items\":[");
			for (int i = 0; i < prefs.length(); i++) {
				if (i > 0) {
					writer.print(',');
				}
					writer.print("{\"value\":\"" + prefs.getValue(i) + "\",\"id\":\"" + prefs.getItemID(i) + "\"}");
			}
		} catch (TasteException e) {
			writer.print("{\"empty\":[");
		}
		writer.println("]}");
	}
	
	private void sendRecommended(HttpServletResponse response, Iterable<RecommendedItem> items) throws IOException {
		PrintWriter writer = response.getWriter();
		writer.print("{\"items\":[");
		boolean first = true;
		for (RecommendedItem recommendedItem : items) {
			if (first) {
				first = false;
			} else {
				writer.print(',');
			}
			writer.print("{\"value\":\"");
			writer.print(recommendedItem.getValue());
			writer.print("\",\"id\":\"");
			writer.print(recommendedItem.getItemID());
			writer.print("\"}");
		}
		writer.println("]}");
	}

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
	
	private Map<Long, String> getDataFromFile(String file) throws IOException {
		InputStream stream = this.getServletContext().getResourceAsStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		Map<Long, String> data = new HashMap<Long, String>();
		String line;
		int i = 0;
		while ((line = reader.readLine()) != null) {
			data.put(new Long(i++), line);
        }
		reader.close();
		return data;
	}
	
	private File loadResourcesToFile(String resource) {
		InputStream stream = this.getServletContext().getResourceAsStream(resource);
		File tempFile = null;
		try {
			tempFile = new File(System.getProperty("java.io.tmpdir") + File.separator + DATA_FILE + ".tmp");
			tempFile.deleteOnExit();
			tempFile.createNewFile();
			
			FileOutputStream out = new FileOutputStream(tempFile);
			ByteStreams.copy(stream, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tempFile;
	}
	
	@Override
	public void destroy() {
		
	}
}