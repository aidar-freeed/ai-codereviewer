package com.adins.mss.base.about;


import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.mikepenz.aboutlibraries.entity.Library;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gigin.ginanjar
 */
public class AboutArrayAdapter extends ArrayAdapter<Library> {
    private Context mContext;
    private List<Library> libraries;

    /**
     * Inisialisasi About Array Adapter
     *
     * @param context   - Context
     * @param libraries - List of Libary
     */
    public AboutArrayAdapter(Context context, List<Library> libraries) {
        super(context, R.layout.new_about_library_log, libraries);

        this.mContext = context;
        if (libraries != null)
            this.libraries = libraries;
        else
            this.libraries = new ArrayList<Library>();
    }

    @Override
    public int getCount() {
        return libraries.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.new_about_library_log, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.libraryname)).setText(libraries.get(position).getLibraryName());
        ((TextView) convertView.findViewById(R.id.librarycreator)).setText(libraries.get(position).getAuthor());
        ((TextView) convertView.findViewById(R.id.libraryversion)).setText(libraries.get(position).getLibraryVersion());
//		((TextView)convertView.findViewById(R.id.librarylicense)).setText(libraries.get(position).getLicense().getLicenseName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ((TextView) convertView.findViewById(R.id.description)).setText(Html.fromHtml(libraries.get(position).getLibraryDescription(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            ((TextView) convertView.findViewById(R.id.description)).setText(Html.fromHtml(libraries.get(position).getLibraryDescription()));
        }

        return convertView;
    }

}
