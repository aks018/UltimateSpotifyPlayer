package aviee.develop.music.myultimatemusicexperienceforspotify.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.Song;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "UltimateSpotifyPlayer.db";
    public static final String SONG_TABLE_NAME = "Song";
    public static final String SUPPORTING_ARTIST_TABLE_NAME = "SupportingArtist";

    public static final String SONG_URI = "uri";
    public static final String SONG_ARTIST = "artist";
    public static final String SONG_TRACK_NAME = "track_name";
    public static final String SONG_ALBUM = "album";
    public static final String SONG_RELEASE_DATE = "release_date";
    public static final String SONG_TOTAL_ARTIST_TRACKS = "total_artist_tracks";
    public static final String SONG_SONG_LENGTH = "song_length";
    public static final String SONG_ALBUM_ID = "album_id";
    public static final String SONG_ALBUM_IMAGE = "album_image";


    public static final String SUPPORTING_ARTIST_SONG_URI = "song_uri";
    public static final String SUPPORTING_ARTIST_URI = "artist_uri";

    private HashMap hp;

    String TAG = "DBHelper";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME , null, 9);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL(
                "create table Song " +
                        "(uri text primary key, artist text, track_name text, " +
                        "album text, release_date text, " +
                        "total_artist_tracks text, song_length text, album_id text, album_image text)"

        );

        db.execSQL(
                "create table SupportingArtist " +
                        "(song_uri text primary key, artist_uri text)"

        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + SONG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SUPPORTING_ARTIST_TABLE_NAME);

        onCreate(db);
    }

    public boolean insertSong (Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SONG_URI, song.getTrackValue());
        contentValues.put(SONG_ARTIST, song.getArtist());
        contentValues.put(SONG_TRACK_NAME, song.getTrackName());
        contentValues.put(SONG_ALBUM, song.getAlbum());
        contentValues.put(SONG_RELEASE_DATE, song.getReleaseDate());
        contentValues.put(SONG_TOTAL_ARTIST_TRACKS, song.getGetTotalArtistTracks());
        contentValues.put(SONG_SONG_LENGTH, song.getSongLength());
        contentValues.put(SONG_ALBUM_ID, song.getAlbumID());
        contentValues.put(SONG_ALBUM_IMAGE, song.getAlbumImage());


        ContentValues artistUrisValues = new ContentValues();
        artistUrisValues.put(SUPPORTING_ARTIST_SONG_URI, song.getTrackValue());
        for(String value : song.getArtistUri()){
            artistUrisValues.put(SUPPORTING_ARTIST_URI, value);
        }

        db.insert(SONG_TABLE_NAME, null, contentValues);
        db.insert(SUPPORTING_ARTIST_TABLE_NAME, null, artistUrisValues);

        return true;
    }

    public Cursor getSong(String songUri) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + SONG_TABLE_NAME + "  where " + SONG_URI + "="+songUri+"";
        Log.i(TAG, query);
        Cursor res =  db.rawQuery( query, null );
        return res;
    }

    public boolean updateSong (Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SONG_ARTIST, song.getArtist());
        contentValues.put(SONG_TRACK_NAME, song.getTrackName());
        contentValues.put(SONG_ALBUM, song.getAlbum());
        contentValues.put(SONG_RELEASE_DATE, song.getReleaseDate());
        contentValues.put(SONG_TOTAL_ARTIST_TRACKS, song.getGetTotalArtistTracks());
        contentValues.put(SONG_SONG_LENGTH, song.getSongLength());
        contentValues.put(SONG_ALBUM_ID, song.getAlbumID());
        contentValues.put(SONG_ALBUM_IMAGE, song.getAlbumImage());
        db.update(SONG_TABLE_NAME, contentValues,  SONG_URI + " = ? ", new String[] { song.getTrackValue() } );
        return true;
    }

    public Integer deleteSong (Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(SONG_TABLE_NAME,
                SONG_URI + " = ? ",
                new String[] { song.getTrackValue() });
    }

    public ArrayList<Song> getAllSongs() {
        ArrayList<Song> songs = new ArrayList<Song>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorSongs =  db.rawQuery( "select * from " + SONG_TABLE_NAME, null );

        if (cursorSongs.moveToFirst()) {
            //looping through all the records
            do {
                //pushing each record in the employee list
                Cursor cursorArtists =  db.rawQuery( "select * from " + SUPPORTING_ARTIST_TABLE_NAME + " where " + SUPPORTING_ARTIST_SONG_URI + " =?", new String[]{cursorSongs.getString(0)});
                List<String> songArtists = new ArrayList<>();

                if (cursorArtists.moveToFirst()) {
                    do {
                        Log.i(TAG, Integer.toString(cursorArtists.getColumnCount()));
                        String artistValue = cursorArtists.getString(cursorArtists.getColumnIndex("artist_uri"));
                        songArtists.add(artistValue);
                    } while (cursorArtists.moveToNext());
                    cursorArtists.close();
                }

                songs.add(new Song(
                        cursorSongs.getString(0),
                        cursorSongs.getString(1),
                        cursorSongs.getString(2),
                        cursorSongs.getString(3),
                        cursorSongs.getString(4),
                        cursorSongs.getString(5),
                        cursorSongs.getString(6),
                        cursorSongs.getString(7),
                        cursorSongs.getString(8),
                        songArtists));
            } while (cursorSongs.moveToNext());
        }
        //closing the cursor
        cursorSongs.close();

        return songs;
    }

    public void restoreDB(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + SONG_TABLE_NAME);
        db.execSQL("delete from " + SUPPORTING_ARTIST_TABLE_NAME);
    }

    public void printAllSongs(){
        ArrayList<Song> allSongs = new ArrayList<>();

        for(Song song : allSongs){
            Log.i(TAG, song.toString());
        }
    }
}
