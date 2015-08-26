package cz.vutbr.fit.gja.rssreader.rssparser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.xml.sax.SAXException;

import cz.vutbr.fit.gja.rssreader.database.model.Source;
import cz.vutbr.fit.gja.rssreader.database.model.Article;
import cz.vutbr.fit.gja.rssreader.database.dao.MainDAO;
import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssReader;
import nl.matshofman.saxrssreader.RssItem;
import android.util.Log;

/**
 * Nacteni dat z RSS kanalu
 */
public class RSSParser {

    private static final String TAG = "RSSParser";
    private static final long secInDay = 3600 * 24;
    private MainDAO db;
    private int dayslimit;
    private boolean removeUnread;

    // Regularni vyrazy pro odstraneni nezadoucich sekvenci/znaku
    private Pattern patternbr = Pattern.compile("<br[ \t\n\r\f]*/?>");
    private Pattern patterntags = Pattern.compile("<.*?>");
    private Pattern pattern_rmwhite1 = Pattern.compile("^[ \t\n\r\f]+");
    private Pattern pattern_rmwhite2 = Pattern.compile("[ \t\n\r\f]+$");

    public RSSParser(MainDAO db, int daysLimit, boolean removeUnread) {
        this.db = db;
        this.dayslimit = daysLimit;
        this.removeUnread = removeUnread;
    }

    /**
     * Vyber pozadovanych clanku pro dany zdroj (nove, drive nesmazane apod.)
     */
    private List<Article> selectNewArticles(List<Article> articles, Source s) {
        // Vyber pouze novych clanku (kontrola podle odkazu na clanek)
        List<Article> result = new ArrayList<Article>();
        List<Article> in_db = db.getArticleBySource(s.getId());
        Map <String, Article> map_in_db = new HashMap<String, Article>();

        for (Article a : in_db) {
            map_in_db.put(a.getLink(), a);
        }
        for (Article a : articles) {
            if (map_in_db.containsKey(a.getLink())) {
                // Clanek jiz existuje
                map_in_db.remove(a.getLink());
            } else {
                // Novy clanek
                result.add(a);
            }
        }

        // Vyber clanku, ktere nebyly drive smazany, nebo nejsou starsi nez nastaveny pocet dni
        long time_now = Calendar.getInstance().getTime().getTime();
        long timeLimit = time_now - dayslimit * secInDay;
        for (Article a : map_in_db.values()) {
            if (a.isSaved()) continue;
            if (a.isDeleted()) {
                db.deleteArticle(a.getId());
                Log.d(TAG, "Remove article from DB (deleted)" + a.toString());
            } else if (a.getInsertDate() < timeLimit) {
                if (a.isUnread() && removeUnread) {
                    a.setDeleted(true);
                }
                db.deleteArticle(a.getId());
                Log.d(TAG, "Remove article from DB (outdated)" + a.toString());
            }
        }

        return result;
    }

    /**
     * Nacteni clanku z RSS zdroje
     */
    public List<Article> getArticlesFromSource(Source source, boolean new_only) {
        List<Article> articles = new ArrayList<>();

        try {
            URL url = new URL(source.getLink());
            RssFeed feed = RssReader.read(url);
            ArrayList<RssItem> rssItems = feed.getRssItems();
            
            long pubDate;
            for (RssItem rssItem : rssItems) {
                pubDate = Calendar.getInstance().getTime().getTime();
                if (rssItem.getPubDate() != null)
                    pubDate = rssItem.getPubDate().getTime();
                
                articles.add(new Article(rssItem.getTitle(), 
                                         rssItem.getLink(),
                                         removeTags(rssItem.getDescription()),
                                         pubDate,
                                         Calendar.getInstance().getTime().getTime(),
                                         false, true, false,
                                         source.getId(),
                                         source.getCategoryId()));
            }
        } catch (SAXException | IOException e) {
            Log.e(TAG, e.getMessage());
        }

        if (new_only) {
            return this.selectNewArticles(articles, source);
        }

        return articles;
    }

    /**
     * Odstraneni nezadoucich sekvenci a znaku z popisu clanku
     */
    private String removeTags(String input) {
        String tmp = patternbr.matcher(input).replaceAll("\n");
        tmp = patterntags.matcher(tmp).replaceAll("");
        tmp = pattern_rmwhite1.matcher(tmp).replaceAll("");
        tmp = pattern_rmwhite2.matcher(tmp).replaceAll("");
        return tmp;
    }

}
