package cz.vutbr.fit.gja.rssreader.ui;

import cz.vutbr.fit.gja.rssreader.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Polozka clanku v seznamu s pozadovanou urovni detailu
 */
public class ArticleSimpleView extends LinearLayout {

    // Prvky GUI

    private LinearLayout topBtnsLayout;
    private LinearLayout titleLayout;
    private LinearLayout infoLayout;

    private ImageButton saveButton;
    private ImageButton unreadButton;
    private ImageButton goToPageButton;
    private ImageButton closeButton2;
    private View unreadView;
    private TextView titleView;
    private TextView descView;
    private TextView source;
    private TextView pubDate;
    private ImageButton closeButton;

    public ArticleSimpleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ArticleSimpleView,
                0, 0);
        String titleText = "";
        String descText = "";
        String sourceText = "";
        String pubDateText = "";
        try {
            titleText = a.getString(R.styleable.ArticleSimpleView_titleText);
            descText = a.getString(R.styleable.ArticleSimpleView_descText);
            sourceText = a.getString(R.styleable.ArticleSimpleView_sourceText);
            pubDateText = a.getString(R.styleable.ArticleSimpleView_pubDateText);
        } finally {
            a.recycle();
        }

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.article_simple_view, this, true);
        topBtnsLayout = (LinearLayout) getChildAt(0);

        saveButton = (ImageButton) topBtnsLayout.getChildAt(0);
        unreadButton = (ImageButton) topBtnsLayout.getChildAt(1);
        goToPageButton = (ImageButton) topBtnsLayout.getChildAt(2);
        closeButton2 = (ImageButton) topBtnsLayout.getChildAt(3);

        titleLayout = (LinearLayout) getChildAt(1);

        unreadView = (View) titleLayout.getChildAt(0);
        titleView = (TextView) titleLayout.getChildAt(1);
        titleView.setText(titleText);
        closeButton = (ImageButton) titleLayout.getChildAt(2);

        infoLayout = (LinearLayout) getChildAt(2);
        source = (TextView) infoLayout.getChildAt(0);
        source.setText(sourceText);
        pubDate = (TextView) infoLayout.getChildAt(1);
        pubDate.setText(pubDateText);

        descView = (TextView) getChildAt(3);
        descView.setText(descText);
    }

    public ArticleSimpleView(Context context) {
        this(context, null);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setDescription(String description) {
        descView.setText(description);
    }

    public void setPubDate(String date) {
        pubDate.setText(date);
    }

    public void setSource(String source) {
       this.source.setText(source);
    }
    
    /**
     * Nastaveni pro nejmensi uroven detailu
     */
    public void setDefaultSimple() {
        this.setBackgroundColor(Color.TRANSPARENT);
        descView.setVisibility(GONE);
        closeButton.setVisibility(VISIBLE);
        topBtnsLayout.setVisibility(GONE);
        infoLayout.setVisibility(GONE);
        titleView.setClickable(true);
    }

    /**
     * Nastaveni pro stredni uroven detailu
     */
    public void setDefaultMid() {
        this.setBackgroundColor(Color.TRANSPARENT);
        descView.setVisibility(VISIBLE);
        descView.setMaxLines(3);
        closeButton.setVisibility(VISIBLE);
        topBtnsLayout.setVisibility(GONE);
        infoLayout.setVisibility(VISIBLE);
        titleView.setClickable(true);
    }
    
    /**
     * Nastaveni pro nejvyssi uroven detailu
     */
    public void setDefaultMax() {
        this.setBackgroundColor(Color.TRANSPARENT);
        descView.setVisibility(VISIBLE);
        descView.setMaxLines(40);
        closeButton.setVisibility(GONE);
        topBtnsLayout.setVisibility(VISIBLE);
        infoLayout.setVisibility(VISIBLE);
        titleView.setClickable(true);
    }

    public void showDetails() {
        setDefaultMax();
        this.setBackgroundColor(Color.WHITE);
    }

    public void markAsRead() {
    	titleView.setTextColor(Color.DKGRAY);
    	unreadButton.setImageResource(R.drawable.ic_action_article_unread);
    	unreadView.setBackgroundColor(Color.TRANSPARENT);
    }

    public void markAsUnread() {
    	titleView.setTextColor(Color.DKGRAY);
    	unreadButton.setImageResource(R.drawable.ic_action_article_unread2);
    	unreadView.setBackgroundColor(Color.argb(255, 0xff, 0xb8, 0));
    }

    public void markAsSaved() {
        saveButton.setImageResource(R.drawable.ic_action_article_saved_in);
        closeButton.setImageResource(R.drawable.ic_action_article_saved_in);
        closeButton.setClickable(false);
        saveButton.setEnabled(false);
        closeButton2.setImageResource(R.drawable.ic_action_trash);
    }

    public void markAsUnsaved() {
        saveButton.setImageResource(R.drawable.ic_action_article_save);
        closeButton.setImageResource(R.drawable.ic_action_article_delete);
        closeButton.setClickable(true);
        saveButton.setEnabled(true);
        closeButton2.setImageResource(R.drawable.ic_action_article_delete);
    }

}
