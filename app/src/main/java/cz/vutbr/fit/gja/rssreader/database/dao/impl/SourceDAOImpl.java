package cz.vutbr.fit.gja.rssreader.database.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cz.vutbr.fit.gja.rssreader.database.dao.DatabaseHelper;
import cz.vutbr.fit.gja.rssreader.database.dao.api.SourceDAO;
import cz.vutbr.fit.gja.rssreader.database.model.Source;

public class SourceDAOImpl implements SourceDAO {

    private SQLiteDatabase db;

    public void open(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public Source getSource(long sourceId) {
        Cursor cursor = db.query(DatabaseHelper.TABLE_SOURCE, null,
                DatabaseHelper.COL_ID + " = ?",
                new String[] { String.valueOf(sourceId) }, null, null, null);
        if (cursor != null && cursor.moveToFirst() ) {
            Source s =  cursorToSource(cursor);
            cursor.close();
            return s;
        } else
            return null;
    }

    @Override
    public void createSource(Source s) {
        createSource(s.getLink(), s.getName(), s.getCategoryId());
    }

    @Override
    public void createSource(String link, String name, long categoryId) {
        if (name == null || link == null)
            throw new NullPointerException();

        ContentValues cValues = sourceToContentValues(link, name, categoryId);

        db.insert(DatabaseHelper.TABLE_SOURCE, null, cValues);
    }

    @Override
    public List<Source> getAllSources() {
        List<Source> sources = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_SOURCE
                    + " ORDER BY " + DatabaseHelper.COL_SRC_NAME, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                sources.add(cursorToSource(cursor));
            } while (cursor.moveToNext());
            
            cursor.close();
        }

        return sources;
    }

    @Override
    public boolean updateSource(Source s) {
        ContentValues cValues = sourceToContentValues(s);

        int ecode = db.update(DatabaseHelper.TABLE_SOURCE, cValues,
                DatabaseHelper.COL_ID + " = ?",
                new String[] { String.valueOf(s.getId()) });

        if (ecode == 1)
            return true;
        else
            return false;
    }

    @Override
    public boolean deleteSource(long sourceId) {
        db.delete(DatabaseHelper.TABLE_ARTICLE, DatabaseHelper.COL_ART_SOURCE_ID + " = ?", 
                new String[] {String.valueOf(sourceId)});
        int ecode = db.delete(DatabaseHelper.TABLE_SOURCE, DatabaseHelper.COL_ID + " = ?",
                new String[] { String.valueOf(sourceId) });

        if (ecode == 1)
            return true;
        else
            return false;
    }

    @Override
    public void removeSourceCategoryByCategoryId(long categoryId) {
        ContentValues cValues = new ContentValues();
        cValues.put(DatabaseHelper.COL_SRC_CATEGORY_ID, 0L);

        db.update(DatabaseHelper.TABLE_SOURCE, cValues,
                DatabaseHelper.COL_SRC_CATEGORY_ID + " = ?",
                new String[] { String.valueOf(categoryId) });
    }
    
    private Source cursorToSource(Cursor cursor) {
        return new Source(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                cursor.getLong(3));
    }

    private ContentValues sourceToContentValues(Source s) {
        return sourceToContentValues(s.getLink(), s.getName(), s.getCategoryId());
    }

    private ContentValues sourceToContentValues(String link, String name, long categoryId) {
        ContentValues cValues = new ContentValues();
        cValues.put(DatabaseHelper.COL_SRC_LINK, link);
        cValues.put(DatabaseHelper.COL_SRC_NAME, name);
        cValues.put(DatabaseHelper.COL_SRC_CATEGORY_ID, categoryId);
        return cValues;
    }

}
