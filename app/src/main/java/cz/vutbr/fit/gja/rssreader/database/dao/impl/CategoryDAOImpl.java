package cz.vutbr.fit.gja.rssreader.database.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cz.vutbr.fit.gja.rssreader.database.dao.DatabaseHelper;
import cz.vutbr.fit.gja.rssreader.database.dao.api.CategoryDAO;
import cz.vutbr.fit.gja.rssreader.database.model.Category;

public class CategoryDAOImpl implements CategoryDAO {

    private SQLiteDatabase db;

    @Override
    public void open(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public Category getCategory(long categoryId) {
        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORY, null,
                DatabaseHelper.COL_ID + " = ?",
                new String[] { String.valueOf(categoryId) }, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Category cat = cursorToCategory(cursor);
            cursor.close();
            return cat;
        } else
            return null;
    }
    
    @Override
    public void createCategory(Category c) {
        createCategory(c.getName());
    }

    @Override
    public void createCategory(String name) {
        if (name == null)
            throw new NullPointerException();

        ContentValues cValues = new ContentValues();
        cValues.put(DatabaseHelper.COL_CAT_NAME, name);

        db.insert(DatabaseHelper.TABLE_CATEGORY, null, cValues);
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CATEGORY + 
                " ORDER BY " + DatabaseHelper.COL_CAT_NAME, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                categories.add(cursorToCategory(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return categories;
    }

    @Override
    public boolean updateCategory(Category c) {
        ContentValues cValues = new ContentValues();
        cValues.put(DatabaseHelper.COL_CAT_NAME, c.getName());

        int ecode = db.update(DatabaseHelper.TABLE_CATEGORY, cValues,
                DatabaseHelper.COL_ID + " = ?",
                new String[] { String.valueOf(c.getId()) });

        if (ecode == 1)
            return true;
        else
            return false;
    }

    @Override
    public boolean deleteCategory(long categoryId) {
        int ecode = db.delete(DatabaseHelper.TABLE_CATEGORY, DatabaseHelper.COL_ID + " = ?",
                new String[] { String.valueOf(categoryId) });

        if (ecode == 1)
            return true;
        else
            return false;
    }

    private Category cursorToCategory(Cursor cursor) {
        return new Category(cursor.getLong(0), cursor.getString(1));
    }

}
