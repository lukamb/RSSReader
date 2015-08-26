package cz.vutbr.fit.gja.rssreader.database.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cz.vutbr.fit.gja.rssreader.database.dao.DatabaseHelper;
import cz.vutbr.fit.gja.rssreader.database.dao.Utillity;
import cz.vutbr.fit.gja.rssreader.database.dao.api.ArticleDAO;
import cz.vutbr.fit.gja.rssreader.database.model.Article;

public class ArticleDAOImpl implements ArticleDAO {

    private SQLiteDatabase db;

    @Override
    public void open(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public Article getArticle(long articleId) {
        Cursor cursor = db.query(DatabaseHelper.TABLE_ARTICLE, null,
                DatabaseHelper.COL_ID + " = ?",
                new String[] { String.valueOf(articleId) }, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Article art = cursorToArticle(cursor);
            cursor.close();
            return art;
        } else
            return null;
    }

    @Override
    public void createArticle(Article a) {
        createArticle(a.getTitle(), a.getLink(), a.getDescription(),
                a.getPubDate(), a.getInsertDate(), a.isDeleted(), a.isUnread(), a.isSaved(),
                a.getSourceId(), a.getCategoryId());
    }

    @Override
    public void createArticle(String title, String link, String description, long pubDate, 
            long insertDate, boolean deleted, boolean unread, 
            boolean saved, long sourceId, long categoryId) {
        ContentValues cValues = articleToContentValues(title, link, description, pubDate, insertDate, 
                deleted, unread, saved, sourceId, categoryId);

        db.insert(DatabaseHelper.TABLE_ARTICLE, null, cValues);
    }

    @Override
    public List<Article> getAllArticles(boolean showReadedArticles) {
        List<Article> articles = new ArrayList<>();

        Cursor cursor;
        if (showReadedArticles)
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ARTICLE
                    + " ORDER BY " + DatabaseHelper.COL_ART_PUB_DATE + " DESC", null);
        else
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ARTICLE
                    + " WHERE " + DatabaseHelper.COL_ART_UNREAD + " = " + Utillity.TRUE
                    + " ORDER BY " + DatabaseHelper.COL_ART_PUB_DATE + " DESC", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                articles.add(cursorToArticle(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return articles;
    }

    @Override
    public boolean updateArticle(Article a) {
        ContentValues cValues = articleToContentValues(a);

        int ecode = db.update(DatabaseHelper.TABLE_ARTICLE, cValues,
                DatabaseHelper.COL_ID + " = ?",
                new String[] { String.valueOf(a.getId()) });

        if (ecode == 1)
            return true;
        else
            return false;
    }

    @Override
    public void changeArticleCategoryBySource(long sourceId, long categoryId) {
        ContentValues cValues = new ContentValues();
        cValues.put(DatabaseHelper.COL_ART_CATEGORY_ID, categoryId);

        db.update(DatabaseHelper.TABLE_ARTICLE, cValues,
                DatabaseHelper.COL_ART_SOURCE_ID + " = ?",
                new String[] { String.valueOf(sourceId) });
    }

    @Override
    public boolean deleteArticle(long articleId) {
        int ecode = db.delete(DatabaseHelper.TABLE_ARTICLE, DatabaseHelper.COL_ID + " = ?",
                new String[] { String.valueOf(articleId) });

        if (ecode == 1)
            return true;
        else
            return false;
    }

    @Override
    public List<Article> getArticlesByCategory(long category, boolean showReadedArticles) {
        List<Article> articles = new ArrayList<>();
        Cursor cursor;
        if (showReadedArticles)
            cursor = db.query(DatabaseHelper.TABLE_ARTICLE, null,
                    DatabaseHelper.COL_ART_CATEGORY_ID + " = ?",
                    new String[] { String.valueOf(category) }, null, null, null);
        else
            cursor = db.query(DatabaseHelper.TABLE_ARTICLE, null,
                    DatabaseHelper.COL_ART_CATEGORY_ID + " = ?"
                            + " AND " + DatabaseHelper.COL_ART_UNREAD + " = " + Utillity.TRUE,
                    new String[] { String.valueOf(category) }, null, null, 
                    DatabaseHelper.COL_ART_PUB_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                articles.add(cursorToArticle(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return articles;
    }

    @Override
    public List<Article> getUncategorizedArticles(boolean showReadedArticles) {
        List<Article> articles = new ArrayList<>();

        Cursor cursor;
        if (showReadedArticles)
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ARTICLE
                    + " WHERE " + DatabaseHelper.COL_ART_CATEGORY_ID + " = 0"
                    + " ORDER BY " + DatabaseHelper.COL_ART_PUB_DATE + " DESC", null);
        else
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ARTICLE
                    + " WHERE " + DatabaseHelper.COL_ART_CATEGORY_ID + " = 0"
                    + " AND " + DatabaseHelper.COL_ART_UNREAD + " = " + Utillity.TRUE
                    + " ORDER BY " + DatabaseHelper.COL_ART_PUB_DATE + " DESC", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                articles.add(cursorToArticle(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return articles;
    }

    @Override
    public List<Article> getSavedArticles(boolean showReadedArticles) {
        List<Article> articles = new ArrayList<>();

        Cursor cursor;
        if (showReadedArticles)
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ARTICLE
                    + " WHERE " + DatabaseHelper.COL_ART_SAVED + " = " + Utillity.TRUE
                    + " ORDER BY " + DatabaseHelper.COL_ART_PUB_DATE + " DESC", null);
        else
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ARTICLE
                    + " WHERE " + DatabaseHelper.COL_ART_SAVED + " = " + Utillity.TRUE
                    + " AND " + DatabaseHelper.COL_ART_UNREAD + " = " + Utillity.TRUE
                    + " ORDER BY " + DatabaseHelper.COL_ART_PUB_DATE + " DESC", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                articles.add(cursorToArticle(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return articles;
    }

    @Override
    public List<Article> getArticleBySource(long sourceId) {
        List<Article> articles = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ARTICLE, null,
                DatabaseHelper.COL_ART_SOURCE_ID + " = ?",
                new String[] { String.valueOf(sourceId) }, null, null, 
                DatabaseHelper.COL_ART_PUB_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                articles.add(cursorToArticle(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return articles;
    }
    

    @Override
    public void removeArticleCategoryByCategoryId(long categoryId) {
        ContentValues cValues = new ContentValues();
        cValues.put(DatabaseHelper.COL_ART_CATEGORY_ID, 0L);

        db.update(DatabaseHelper.TABLE_ARTICLE, cValues,
                DatabaseHelper.COL_ART_CATEGORY_ID + " = ?",
                new String[] { String.valueOf(categoryId) });
    }

    private ContentValues articleToContentValues(String title, String link, String description,
            long pubDate, long insertDate, boolean deleted, boolean unread, boolean saved, long sourceId,
            long categoryId) {
        ContentValues cValues = new ContentValues();
        if (title != null)
            cValues.put(DatabaseHelper.COL_ART_TITLE, title);

        if (link != null)
            cValues.put(DatabaseHelper.COL_ART_LINK, link);

        if (description != null)
            cValues.put(DatabaseHelper.COL_ART_DESCRIPTION, description);

        cValues.put(DatabaseHelper.COL_ART_PUB_DATE, pubDate);
        cValues.put(DatabaseHelper.COL_ART_INSERT_DATE, insertDate);
        cValues.put(DatabaseHelper.COL_ART_DELETED, Utillity.booleanToLong(deleted));
        cValues.put(DatabaseHelper.COL_ART_UNREAD, Utillity.booleanToLong(unread));
        cValues.put(DatabaseHelper.COL_ART_SAVED, Utillity.booleanToLong(saved));
        cValues.put(DatabaseHelper.COL_ART_SOURCE_ID, sourceId);
        cValues.put(DatabaseHelper.COL_ART_CATEGORY_ID, categoryId);
        return cValues;
    }

    private ContentValues articleToContentValues(Article a) {
        return articleToContentValues(a.getTitle(), a.getLink(), a.getDescription(),
                a.getPubDate(), a.getInsertDate(),
                a.isDeleted(), a.isUnread(), a.isSaved(), a.getSourceId(), a.getCategoryId());
    }

    private Article cursorToArticle(Cursor cursor) {
        return new Article(cursor.getLong(0), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getLong(4),
                cursor.getLong(5),
                Utillity.longToBoolean(cursor.getLong(6)),
                Utillity.longToBoolean(cursor.getLong(7)),
                Utillity.longToBoolean(cursor.getLong(8)), cursor.getLong(9),
                cursor.getLong(10));
    }

}
