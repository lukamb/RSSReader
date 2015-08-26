package cz.vutbr.fit.gja.rssreader.database.dao.api;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import cz.vutbr.fit.gja.rssreader.database.model.Source;

public interface SourceDAO {

    /**
     * Otevreni pozadovane DB
     * @param db Pozadovana DB
     */
    public void open(SQLiteDatabase db);
    
    /**
     * Vyhledani zdroje
     * @param sourceId ID zdroje
     * @return Objekt zdroje
     */
    public Source getSource(long sourceId);

    /**
     * Ulozi zadany zdroj do DB
     * @param s Zdroj k ulozeni
     */
    public void createSource(Source s);

    /**
     * Ulozi zdroj se zadanymi udaji do DB
     */
    public void createSource(String link, String name, long categoryId);

    /**
     * Vrati vsechny zdroje
     * @return Seznam zdroju
     */
    public List<Source> getAllSources();

    /**
     * Aktualizace zdroje vcetne pripadne zmeny kategorie souvisejicich clanku
     * @param s Zdroj s novymi hodnotami
     * @return True v pripade uspechu
     */
    public boolean updateSource(Source s);

    /**
     * Odstrani zadany zdroj vcetne souvisejicich clanku
     * @param sourceId ID zdroje
     * @return True v pripade uspechu
     */
    public boolean deleteSource(long sourceId);

    /**
     * Odebere zdroje ze zadane kategorie
     * @param categoryId ID kategorie
     */
    public void removeSourceCategoryByCategoryId(long categoryId);
    
}
