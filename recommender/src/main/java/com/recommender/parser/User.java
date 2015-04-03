package com.recommender.parser;

import java.util.ArrayList;

public class User {

	private int userID;
	private String name;
	private ArrayList<Rating> ratings;
	private boolean isValid;
	private Rating lastRating;
	
	public User(int userID, String name){
		this.setUserID(userID);
		this.setName(name);
		setValid(false);
		setRatings(new ArrayList<Rating>());
	}
	
	public void addMovieRating(int movie, int rating){
		Rating newRating = new Rating(movie, rating);
		if (ratings.size() == 0) {
			lastRating = newRating;
		} else if (lastRating != newRating) {
			setValid(true);
		}
		ratings.add(newRating);
	}

	public ArrayList<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(ArrayList<Rating> ratings) {
		this.ratings = ratings;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

}
