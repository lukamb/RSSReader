package cz.vutbr.fit.gja.rssreader.database.dao.api;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import cz.vutbr.fit.gja.rssreader.database.model.Category;

public interface CategoryDAO {

    /**
     * Otevreni pozadovane DB
     * @param db Pozadovana DB
     */
    public void open(SQLiteDatabase db);

    /**
     * Vyhledani kategorie
     * @param categoryId ID kategorie
     * @return Objekt dane kategorie
     */
    public Category getCategory(long categoryId);
    
    /**
     * Ulozi kategorii do DB
     * @param c Kategorie pro ulozeni
     */
    public void createCategory(Category c);

    /**
     * Ulozi kategorii se zadanym nazvem do DB
     * @param name Nazev kategorie (!= null)
     */
    public void createCategory(String name);

    /**
     * Vrati vsechny kategorie
     * @return Seznam kategorii
     */
    public List<Category> getAllCategories();

    /**
     * Aktualizuje kategorii
     * @param c Kategorie s novymi hodnotami
     * @return True v pripade uspechu
     */
    public boolean updateCategory(Category c);

    /**
     * Odstrani zadanou kategorii. Clanky a zdroje pod touto kategorii budou nezarazeny (ID == 0).
     * @param categoryId ID kategorie k odstraneni
     * @return True v pripade uspechu
     */
    public boolean deleteCategory(long categoryId);

}
