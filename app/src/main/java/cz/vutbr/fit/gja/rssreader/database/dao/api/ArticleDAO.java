package cz.vutbr.fit.gja.rssreader.database.dao.api;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import cz.vutbr.fit.gja.rssreader.database.model.Article;

public interface ArticleDAO {

    /**
     * Otevreni pozadovane DB
     * @param db Pozadovana DB
     */
    public void open(SQLiteDatabase db);

    /**
     * Vyhledani clanku
     * @param id ID clanku
     * @return Pozadovany clanek nebo null, pokud neexistuje
     */
    public Article getArticle(long id);

    /**
     * Ulozeni zadaneho clanku
     * @param a Clanek pro ulozeni
     */
    public void createArticle(Article a);

    /**
     * Ulozeni clanku se zadanymi udaji do DB
     */
    public void createArticle(String title, String link, String description, long pubDate,
            long insertDate,
            boolean deleted, boolean unread, boolean saved, long sourceId, long categoryId);

    /**
     * Vrati vsechny ulozene clanky
     * @param showReadedArticles Priznak, zda maji byt vraceny i prectene clanky
     * @return Seznam clanku
     */
    public List<Article> getAllArticles(boolean showReadedArticles);

    /**
     * Aktualizace clanku
     * @param a Clanek s novymi udaji
     * @return True v pripade uspechu
     */
    public boolean updateArticle(Article a);

    /**
     * Odstrani clanek s danym ID
     * @param articleId ID clanku
     * @return True v pripade uspechu
     */
    public boolean deleteArticle(long articleId);

    /**
     * Vrati clanky v zadane kategorii
     * @param showReadedArticles Priznak, zda maji byt vraceny i prectene clanky
     * @return Seznam clanku
     */
    List<Article> getArticlesByCategory(long category, boolean showReadedArticles);

    /**
     * Vrati clanky nezarazene do kategorii
     * @param showReadedArticles Priznak, zda maji byt vraceny i prectene clanky
     * @return Seznam clanku
     */
    List<Article> getUncategorizedArticles(boolean showReadedArticles);

    /**
     * Vrati seznam ulozenych clanku
     * @param showReadedArticles Priznak, zda maji byt vraceny i prectene clanky
     * @return Seznam clanku
     */
    List<Article> getSavedArticles(boolean showReadedArticles);

    /**
     * Vrati clanky podle zadaneho zdroje
     * @param sourceId ID zdroje
     * @return Seznam clanku
     */
    List<Article> getArticleBySource(long sourceId);

    /**
     * Zmeni kategorii clanku daneho zdroje
     * @param sourceId ID zdroje
     * @param categoryId ID nove kategorie
     */
    public void changeArticleCategoryBySource(long sourceId, long categoryId);

    /**
     * Zrusi zarazeni clanku do dane kategorie
     * @param categoryId ID kategorie k odebrani clanku
     */
    public void removeArticleCategoryByCategoryId(long categoryId);

}
