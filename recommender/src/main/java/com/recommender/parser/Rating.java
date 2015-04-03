package com.recommender.parser;

public class Rating {
	
	private int movieID;
	private int rating;
	
	public Rating(int movieID, int rating){
		this.setMovieID(movieID);
		this.setRating(rating);
	}
	
	public int getMovieID() {
		return movieID;
	}
	
	public void setMovieID(int movieID) {
		this.movieID = movieID;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

}
