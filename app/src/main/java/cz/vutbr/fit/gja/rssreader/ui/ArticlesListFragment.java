package cz.vutbr.fit.gja.rssreader.ui;

import cz.vutbr.fit.gja.rssreader.R;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ArticlesListFragment extends ListFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_articles_list, container, false);
    }

}
