package cz.vutbr.fit.gja.rssreader.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import cz.vutbr.fit.gja.rssreader.database.dao.MainDAO;
import cz.vutbr.fit.gja.rssreader.database.model.Article;
import cz.vutbr.fit.gja.rssreader.database.model.Source;

/**
 * Adapter pro zobrazeni seznamu clanku s pozadovanou urovni detailu
 */
public class ArticleSimpleAdapter extends ArrayAdapter<Article> {

    /** Reference na DB */
    private MainDAO db = null;

    /** Uroven zobrazovanych detailu o clancich */
    private int detailLevel;

    public ArticleSimpleAdapter(Context context, int resource, List<Article> Articles) {
        super(context, resource, Articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ArticleSimpleView v = (ArticleSimpleView) convertView;

        if (v == null) {
            v = new ArticleSimpleView(getContext());
        }

        Article a = getItem(position);
        if (a != null) {
            v.setTitle(a.getTitle());
            v.setDescription(a.getDescription());

            if (a.getPubDate()==0){
                v.setPubDate("");
            } else {
                SimpleDateFormat ft = new SimpleDateFormat (" dd.MM. hh:mm");
                v.setPubDate(ft.format(new Date(a.getPubDate())));
            }

            String sourceVal = String.valueOf(a.getSourceId());
            
            if (db != null){
                Source s = db.getSource(a.getSourceId());
                if (s != null) {
                    sourceVal = s.getName();
                }
            }
            v.setSource(sourceVal);


            switch (detailLevel) {
                case MainActivity.SHOW_DETAIL_MIN:
                    v.setDefaultSimple();
                    break;

                case MainActivity.SHOW_DETAIL_MID:
                    v.setDefaultMid();
                    break;

                case MainActivity.SHOW_DETAIL_MAX:
                    v.setDefaultMax();
                    break;

                default:
                    v.setDefaultSimple();
                    Log.d("Article adapter", "Unknown detail level");
            }

            if (a.isUnread()) {
                v.markAsUnread();
            } else {
                v.markAsRead();
            }

            if (a.isSaved()){
                v.markAsSaved();
            } else {
                v.markAsUnsaved();
            }
        }

        return v;
    }

    public void setDB(MainDAO db) {
    	this.db = db;
    }

    public void setDetailLevel(int level) {
    	detailLevel = level;
    }

}
