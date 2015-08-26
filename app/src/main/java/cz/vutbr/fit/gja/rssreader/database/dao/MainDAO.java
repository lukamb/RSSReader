package cz.vutbr.fit.gja.rssreader.database.dao;

import java.io.Closeable;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import cz.vutbr.fit.gja.rssreader.database.dao.api.ArticleDAO;
import cz.vutbr.fit.gja.rssreader.database.dao.api.CategoryDAO;
import cz.vutbr.fit.gja.rssreader.database.dao.api.SourceDAO;
import cz.vutbr.fit.gja.rssreader.database.dao.impl.ArticleDAOImpl;
import cz.vutbr.fit.gja.rssreader.database.dao.impl.CategoryDAOImpl;
import cz.vutbr.fit.gja.rssreader.database.dao.impl.SourceDAOImpl;
import cz.vutbr.fit.gja.rssreader.database.model.Article;
import cz.vutbr.fit.gja.rssreader.database.model.Category;
import cz.vutbr.fit.gja.rssreader.database.model.Source;

/**
 * Trida poskytujici pristup k DB
 */
public class MainDAO implements Closeable, ArticleDAO, CategoryDAO, SourceDAO {

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;

    private ArticleDAO articleDAO = new ArticleDAOImpl();

    private CategoryDAO categoryDAO = new CategoryDAOImpl();

    private SourceDAO sourceDAO = new SourceDAOImpl();

    public MainDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Priprava DAO pro pristup k datum
     */
    public void open() {
        db = dbHelper.getWritableDatabase();
        articleDAO.open(db);
        categoryDAO.open(db);
        sourceDAO.open(db);
    }
    
    /**
     * Odstraneni vsech dat z DB
     */
    public void removeAllData() {
        dbHelper.removeAllData(db);
    }

    @Override
    public void close() {
        dbHelper.close();
    }

    // CLANKY

    @Override
    public Article getArticle(long id) {
        return articleDAO.getArticle(id);
    }

    @Override
    public List<Article> getAllArticles(boolean showReadedArticles) {
        return articleDAO.getAllArticles(showReadedArticles);
    }

    @Override
    public void createArticle(Article a) {
        articleDAO.createArticle(a);
    }

    @Override
    public void createArticle(String title, String link, String description, long pubDate,
            long insertDate, 
            boolean deleted, boolean unread, boolean saved, long sourceId, long categoryId) {
        articleDAO.createArticle(title, link, description, pubDate, insertDate, deleted,
                unread, saved, sourceId, categoryId);
    }

    @Override
    public boolean updateArticle(Article a) {
        return articleDAO.updateArticle(a);
    }

    @Override
    public boolean deleteArticle(long articleId) {
        return articleDAO.deleteArticle(articleId);
    }

    @Override
    public List<Article> getArticlesByCategory(long category, boolean showReadedArticles) {
        return articleDAO.getArticlesByCategory(category, showReadedArticles);
    }

    @Override
    public List<Article> getArticleBySource(long sourceId){
        return articleDAO.getArticleBySource(sourceId);
    }

    @Override
    public List<Article> getUncategorizedArticles(boolean showReadedArticles) {
        return articleDAO.getUncategorizedArticles(showReadedArticles);
    }

    @Override
    public List<Article> getSavedArticles(boolean showReadedArticles) {
        return articleDAO.getSavedArticles(showReadedArticles);
    }
    
    @Override
    public void changeArticleCategoryBySource(long sourceId, long categoryId) {
        articleDAO.changeArticleCategoryBySource(sourceId, categoryId);
    }

    @Override
    public void removeArticleCategoryByCategoryId(long categoryId) {
        articleDAO.removeArticleCategoryByCategoryId(categoryId);
    }

    // KATEGORIE

    @Override
    public Category getCategory(long categoryId) {
        return categoryDAO.getCategory(categoryId);
    }

    @Override
    public void createCategory(Category c) {
        categoryDAO.createCategory(c);
    }

    @Override
    public void createCategory(String name) {
        categoryDAO.createCategory(name);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

    @Override
    public boolean updateCategory(Category c) {
        return categoryDAO.updateCategory(c);
    }

    @Override
    public boolean deleteCategory(long categoryId) {
        articleDAO.removeArticleCategoryByCategoryId(categoryId);
        sourceDAO.removeSourceCategoryByCategoryId(categoryId);
        return categoryDAO.deleteCategory(categoryId);
    }

    // ZDROJE

    @Override
    public Source getSource(long sourceId) {
        return sourceDAO.getSource(sourceId);
    }

    @Override
    public void createSource(Source s) {
        sourceDAO.createSource(s);
    }

    @Override
    public void createSource(String link, String name, long categoryId) {
        sourceDAO.createSource(link, name, categoryId);
    }

    @Override
    public List<Source> getAllSources() {
        return sourceDAO.getAllSources();
    }

    @Override
    public boolean updateSource(Source s) {
        Source dbSource = getSource(s.getId());
        if (dbSource.getCategoryId() != s.getCategoryId()) {
            // Pri zmene kategorie nutno zmenit kategorii u vsech clanku
            articleDAO.changeArticleCategoryBySource(s.getId(), s.getCategoryId());
        }
        
        return sourceDAO.updateSource(s);
    }

    @Override
    public boolean deleteSource(long sourceId) {
        return sourceDAO.deleteSource(sourceId);
    }
    

    @Override
    public void removeSourceCategoryByCategoryId(long categoryId) {
        sourceDAO.removeSourceCategoryByCategoryId(categoryId);
    }


    /**
     * @deprecated Use open method without parameters
     */
    @Override
    public void open(SQLiteDatabase db) {
    }

}
