package com.example.mehak.movies;


public class Movie {
    private String movie_name;
    private String genre;
    private String actress;
    private String actor;
    private String year_of_release;
    private String description;
    private String language;
    private int video_id = NO_VIDEO_PROVIDED;
    private int image_id= NO_IMAGE_PROVIDED;

    private static final int NO_IMAGE_PROVIDED = -1;
    private static final int NO_VIDEO_PROVIDED = -1;

    public Movie(String movie_name,String genre,  String year_of_release,String language, int image_id) {
        this.movie_name = movie_name;
        this.genre = genre;
        this.year_of_release = year_of_release;
        this.language = language;
        this.image_id = image_id;

    }

    public Movie(String movie_name,String genre, String actress, String actor, String year_of_release,String description,String language, int video_id) {
        this.movie_name = movie_name;
        this.genre = genre;
        this.actress = actress;
        this.actor = actor;
        this.year_of_release = year_of_release;
        this.description = description;
        this.language = language;
        this.video_id = video_id;
    }



    public String getMovie_name() {
        return movie_name;
    }

    public String getGenre() {
        return genre;
    }

    public String getActress() {
        return actress;
    }

    public String getActor() {
        return actor;
    }

    public String getYear_of_release() {
        return year_of_release;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public int getImage_id(){
        return image_id;
    }

    public int getVideo_id(){
        return video_id;
    }

    public boolean checkImage(){
        return image_id != NO_IMAGE_PROVIDED;

    }


    public boolean checkVideo(){
        return video_id != NO_VIDEO_PROVIDED;

    }
}
