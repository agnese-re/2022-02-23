package it.polito.tdp.yelp.model;

public class ReviewWithEdges {

	public Review review;
	public int outgoingEdges;
	
	public ReviewWithEdges(Review review, int outgoingEdges) {
		this.review = review;
		this.outgoingEdges = outgoingEdges;
	}

	public Review getReview() {
		return review;
	}

	public void setReview(Review review) {
		this.review = review;
	}

	public int getOutgoingEdges() {
		return outgoingEdges;
	}

	public void setOutgoingEdges(int outgoingEdges) {
		this.outgoingEdges = outgoingEdges;
	}
	
}
