package cz.vutbr.fit.gja.rssreader.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static final int DB_VERSION = 10;
	
	public static final String DB_NAME = "rssReader";
	
	// Nazvy tabulek
	public static final String TABLE_CATEGORY = "category";
	public static final String TABLE_SOURCE = "source";
	public static final String TABLE_ARTICLE = "article";
	
	// ID
	public static final String COL_ID = "_id";
	
	// Sloupce - kategorie
	public static final String COL_CAT_NAME = "name";
	
	// Sloupce - zdroje
	public static final String COL_SRC_LINK = "link";
	public static final String COL_SRC_NAME = "name";
	public static final String COL_SRC_CATEGORY_ID = "categoryId";
	
	// Sloupce - clanky
	public static final String COL_ART_SOURCE_ID = "sourceId";
	public static final String COL_ART_CATEGORY_ID = "categoryId";
	public static final String COL_ART_TITLE = "title";
	public static final String COL_ART_LINK = "link";
	public static final String COL_ART_DESCRIPTION = "desc";
	public static final String COL_ART_PUB_DATE = "pubDate";
	public static final String COL_ART_INSERT_DATE = "insertDate";
	public static final String COL_ART_DELETED = "deleted";
	public static final String COL_ART_UNREAD = "unread";
	public static final String COL_ART_SAVED = "saved";
	
	// Vytvoreni tabulek
	private static final String CREATE_TABLE_CAT = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_CATEGORY + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_CAT_NAME + " TEXT)";
	
	private static final String CREATE_TABLE_SRC = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_SOURCE + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_SRC_LINK + " TEXT, " + COL_SRC_NAME + " TEXT, "
		    + COL_SRC_CATEGORY_ID + " INTEGER, "
			+ "FOREIGN KEY(" + COL_SRC_CATEGORY_ID 
				+ ") REFERENCES " + TABLE_CATEGORY + "(" + COL_ID + "))";
	
	private static final String CREATE_TABLE_ART = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_ARTICLE + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_ART_TITLE + " TEXT, " + COL_ART_LINK + " TEXT, " 
			+ COL_ART_DESCRIPTION + " TEXT, " + COL_ART_PUB_DATE + " INTEGER, "
			+ COL_ART_INSERT_DATE + " INTEGER, "
			+ COL_ART_DELETED + " INTEGER, " + COL_ART_UNREAD + " INTEGER, "
			+ COL_ART_SAVED + " INTEGER, "  
		    + COL_ART_SOURCE_ID + " INTEGER, "
			+ COL_ART_CATEGORY_ID + " INTEGER, "
            + "FOREIGN KEY(" + COL_ART_SOURCE_ID 
                + ") REFERENCES " + TABLE_SOURCE + "(" + COL_ID + "), "
			+ "FOREIGN KEY(" + COL_ART_CATEGORY_ID 
				+ ") REFERENCES " + TABLE_CATEGORY + "(" + COL_ID + "))";
	
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_CAT);
		db.execSQL(CREATE_TABLE_SRC);
		db.execSQL(CREATE_TABLE_ART);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    removeAllData(db);
	}
	
	public void removeAllData(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOURCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLE);
        
        onCreate(db);
	}

}
