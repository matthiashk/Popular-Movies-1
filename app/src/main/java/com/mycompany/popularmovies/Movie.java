package com.mycompany.popularmovies;

// custom arraylist class for storing movie data
public class Movie {

    String title;
    String posterPath;
    String plot;
    String userRating;
    String releaseDate;

    public Movie(String title, String posterPath, String plot, String userRating, String releaseDate)
    {
        this.title = title;
        this.posterPath = posterPath;
        this.plot = plot;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }
}
