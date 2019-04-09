package net.meticScore.adapter;

import android.app.Activity;
import android.support.annotation.Keep;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.IgnoreExtraProperties;

import net.meticScore.R;

import java.util.ArrayList;

@IgnoreExtraProperties
@Keep
public class ListViewAdapter extends BaseAdapter
{
  public Activity context;
    public ArrayList<String> title;
    public ArrayList<String> description;

    public ListViewAdapter(Activity context, ArrayList<String> title, ArrayList<String> description) {
        this.context = context;
        this.title = title;
        this.description = description;
    }

    public ListViewAdapter() {
    }

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public ArrayList<String> getTitle() {
        return title;
    }

    public void setTitle(ArrayList<String> title) {
        this.title = title;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return title.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class ViewHolder {
        TextView txtViewTitle;
        TextView txtViewDescription;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.listitem_row, null);
            holder = new ViewHolder();
            holder.txtViewTitle = (TextView) convertView.findViewById(R.id.textView1);
            holder.txtViewDescription = (TextView) convertView.findViewById(R.id.textView2);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtViewTitle.setText(title.get(position));
        holder.txtViewDescription.setText(description.get(position));

        return convertView;
    }
}