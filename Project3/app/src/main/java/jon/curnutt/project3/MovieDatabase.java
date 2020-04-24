package jon.curnutt.project3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MovieDatabase extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "movie.db";

    private static MovieDatabase mMovieDb;

    public enum MovieSortOrder { ALPHABETIC, UPDATE_DESC, UPDATE_ASC };

    public static MovieDatabase getInstance(Context context) {
        if (mMovieDb == null) {
            mMovieDb = new MovieDatabase(context);
        }
        return mMovieDb;
    }

    private MovieDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class MovieTable {
        private static final String TABLE = "movies";
        private static final String COL_TEXT = "text";
        private static final String COL_UPDATE_TIME = "updated";
    }

//    private static final class QuestionTable {
//        private static final String TABLE = "movies";
//        private static final String COL_ID = "_id";
//        private static final String COL_TEXT = "text";
//        private static final String COL_PLOT = "plot";
//        private static final String COL_MOVIE = "movie";
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create movies table
        db.execSQL("create table " + MovieTable.TABLE + " (" +
                MovieTable.COL_TEXT + " primary key, " +
                MovieTable.COL_UPDATE_TIME + " int)");

        // Create questions table with foreign key that cascade deletes
//        db.execSQL("create table " + QuestionTable.TABLE + " (" +
//                QuestionTable.COL_ID + " integer primary key autoincrement, " +
//                QuestionTable.COL_TEXT + ", " +
//                QuestionTable.COL_PLOT + ", " +
//                QuestionTable.COL_MOVIE + ", " +
//                "foreign key(" + QuestionTable.COL_MOVIE + ") references " +
//                MovieTable.TABLE + "(" + MovieTable.COL_TEXT + ") on delete cascade)");

        // Add an example movie

        Movie movie = new Movie("Lord of the Rings", "1994", "What is this even", "Dwarves, Elves, Hobbits, and Wizards");
        ContentValues values = new ContentValues();
        values.put(MovieTable.COL_TEXT, movie.getName());
        values.put(MovieTable.COL_UPDATE_TIME, movie.getUpdateTime());
        db.insert(MovieTable.TABLE, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + MovieTable.TABLE);
//        db.execSQL("drop table if exists " + QuestionTable.TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                db.execSQL("pragma foreign_keys = on;");
            } else {
                db.setForeignKeyConstraintsEnabled(true);
            }
        }
    }

    public List<Movie> getMovies(MovieSortOrder order) {
        List<Movie> movies = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String orderBy;
        switch (order) {
            case ALPHABETIC:
                orderBy = MovieTable.COL_TEXT + " collate nocase";
                break;
            case UPDATE_DESC:
                orderBy = MovieTable.COL_UPDATE_TIME + " desc";
                break;
            default:
                orderBy = MovieTable.COL_UPDATE_TIME + " asc";
                break;
        }

        String sql = "select * from " + MovieTable.TABLE + " order by " + orderBy;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setName(cursor.getString(0));
                movie.setUpdateTime(cursor.getLong(1));
                movies.add(movie);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return movies;
    }

    public boolean addMovie(Movie movie) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MovieTable.COL_TEXT, movie.getName());
        values.put(MovieTable.COL_UPDATE_TIME, movie.getUpdateTime());
        long id = db.insert(MovieTable.TABLE, null, values);
        return id != -1;
    }

    public void updateMovie(Movie movie) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MovieTable.COL_TEXT, movie.getName());
        values.put(MovieTable.COL_UPDATE_TIME, movie.getUpdateTime());
        db.update(MovieTable.TABLE, values,
                MovieTable.COL_TEXT + " = ?", new String[] { movie.getName() });
    }

    public void deleteMovie(Movie movie) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MovieTable.TABLE,
                MovieTable.COL_TEXT + " = ?", new String[] { movie.getName() });
    }

//    public List<Question> getQuestions(String movie) {
//        List<Question> questions = new ArrayList<>();
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        String sql = "select * from " + QuestionTable.TABLE +
//                " where " + QuestionTable.COL_MOVIE + " = ?";
//        Cursor cursor = db.rawQuery(sql, new String[] { movie });
//        if (cursor.moveToFirst()) {
//            do {
//                Question question = new Question();
//                question.setId(cursor.getInt(0));
//                question.setText(cursor.getString(1));
//                question.setAnswer(cursor.getString(2));
//                question.setMovie(cursor.getString(3));
//                questions.add(question);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//
//        return questions;
//    }
//
//    public Question getQuestion(long questionId) {
//        Question question = null;
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        String sql = "select * from " + QuestionTable.TABLE +
//                " where " + QuestionTable.COL_ID + " = ?";
//        Cursor cursor = db.rawQuery(sql, new String[] { Float.toString(questionId) });
//
//        if (cursor.moveToFirst()) {
//            question = new Question();
//            question.setId(cursor.getInt(0));
//            question.setText(cursor.getString(1));
//            question.setAnswer(cursor.getString(2));
//            question.setMovie(cursor.getString(3));
//        }
//
//        return question;
//    }
//
//    public void addQuestion(Question question) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(QuestionTable.COL_TEXT, question.getText());
//        values.put(QuestionTable.COL_PLOT, question.getAnswer());
//        values.put(QuestionTable.COL_MOVIE, question.getMovie());
//        long questionId = db.insert(QuestionTable.TABLE, null, values);
//        question.setId(questionId);
//
//        // Change update time in movies table
//        updateMovie(new Movie(question.getMovie()));
//    }
//
//    public void updateQuestion(Question question) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(QuestionTable.COL_ID, question.getId());
//        values.put(QuestionTable.COL_TEXT, question.getText());
//        values.put(QuestionTable.COL_PLOT, question.getAnswer());
//        values.put(QuestionTable.COL_MOVIE, question.getMovie());
//        db.update(QuestionTable.TABLE, values,
//                QuestionTable.COL_ID + " = " + question.getId(), null);
//
//        // Change update time in movies table
//        updateMovie(new Movie(question.getMovie()));
//    }
//
//    public void deleteQuestion(long questionId) {
//        SQLiteDatabase db = getWritableDatabase();
//        db.delete(QuestionTable.TABLE,
//                QuestionTable.COL_ID + " = " + questionId, null);
//    }
}
