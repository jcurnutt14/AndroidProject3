package jon.curnutt.project3;

public class Movie {

    private String mMovieName;
    private String mMovieYear;
    private String mMovieTitleYear;
    private String mMoviePlot;
    private long mUpdateTime;

    public Movie() {}

    public Movie(String name, String year, String titleYear, String plot) {
        mMovieName = name;
        mMovieYear = year;
        mMovieTitleYear = titleYear;
        mMoviePlot = plot;
        mUpdateTime = System.currentTimeMillis();
    }

    public String getName() {
        return mMovieName;
    }

    public void setName(String name) {
        mMovieName = name;
    }

    public long getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(long updateTime) {
        mUpdateTime = updateTime;
    }
}
