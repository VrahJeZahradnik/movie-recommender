package com.recommender.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CSFDParser extends DefaultHandler{
	
	private static final int UNRATED = -1;
	private static final int RATED_MOVIES = 200;
	public static final String SRC_DIR = "data/";
	private static final String FILE_OUT = SRC_DIR + RATED_MOVIES + ".dat";
	public static final String PARSED_DIR = SRC_DIR + "parsed/";
	public static final String FILE_NAME = "csfd";
	public static final String FILE_IN = PARSED_DIR + FILE_NAME;
	public static final String SUFFIX = ".xml";
	
	private ArrayList<User> users;
	private ArrayList<String> movies;
	private String movie;
	private String userName;
	private int movieID;
	private int rating;
	
	public CSFDParser(){
		parse();
	}

	public void parseFile(String fileName){
		DefaultHandler handler = this;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		
		SAXParser parser;
		
		try {
			parser = factory.newSAXParser();
			parser.parse(fileName, handler);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, 
			Attributes attributes) {
		
		if (qName.equalsIgnoreCase("movie")){
			movie = attributes.getValue(0);
			movies.add(movie);
			movieID++;
		}
		
		if (qName.equalsIgnoreCase("comment")){
			boolean exists = false;
			int userID = 0;
			User newUser;
			
			userName = attributes.getValue(0);
			if (attributes.getQName(1).equals("rating") && attributes.getValue(1).length() != 0)
				try {
					rating = Integer.parseInt(attributes.getValue(1));
				} catch (NumberFormatException e){
					rating = UNRATED;
				}
			else
				rating = UNRATED;
			
			if (rating == UNRATED)
				return;
			
			for (User user : users){
				if (user.getName().equals(userName)){
					exists = true;
					break;
				}
				
				userID++;
			}
			
			if (!exists){
				newUser = new User(userID, userName);
				users.add(newUser);
			} else {
				newUser = users.get(userID);
			}
			
			newUser.addMovieRating(movieID, rating);
		}
	}
	
	public void parse(){
		users = new ArrayList<User>();
		movies = new ArrayList<String>();
		movie = "";
		userName = "";
		movieID = 0;
		rating = 0;
		
		for (int i = 0; i < 11; i++) {
			parseFile(FILE_IN + i + SUFFIX);
			System.out.println("Processed: " + FILE_IN + i + SUFFIX);
			System.out.println("User count: " + users.size());
			System.out.println("Movie count: " + movies.size() + "\n");
		}
		
		File output = new File(FILE_OUT);
		
		try {
			BufferedWriter out  = new BufferedWriter(new FileWriter(output));
			
			ArrayList<Rating> ratings;
			for (User user : users){
				ratings = new ArrayList<Rating>(user.getRatings());
				
				if (ratings.size() < RATED_MOVIES || !user.isValid())
					continue;
				
				for (Rating rating : ratings){
					out.write(user.getUserID() + "," + rating.getMovieID() + "," + (double) rating.getRating() + "\n");
				}
				out.write("\n");
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		new CSFDParser();
	}
}