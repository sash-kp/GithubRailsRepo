package adapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import model.ListItem;
import test.github.railsrepo.R;


/**
 * Created by HP on 12-06-2017.
 */

public class ListAdapter extends BaseAdapter {
    private AppCompatActivity context;
    private LayoutInflater inflater;
    private ArrayList<ListItem> feedItems;

    public ListAdapter(AppCompatActivity context, ArrayList<ListItem> feedItems) {
        this.context = context;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clear() {
        feedItems.clear();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.list_item, null);

        ListItem item = feedItems.get(position);

        CircleImageView feedImageView = (CircleImageView) view.findViewById(R.id.profile_image);
        TextView commiterName = (TextView) view.findViewById(R.id.committerName);
        TextView commitNumber = (TextView) view.findViewById(R.id.commitNumber);
        TextView commitMessage = (TextView) view.findViewById(R.id.commitMessage);

        commiterName.setText(item.getCommitterName());
        commitNumber.setText(item.getCommitNumber());
        commitMessage.setText(item.getCommitMessage());

        Picasso.with(context)
                .load(item.getProfileImage())
                .noFade()
                .into(feedImageView);

        return view;
    }


}
