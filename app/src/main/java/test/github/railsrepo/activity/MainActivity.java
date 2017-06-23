package test.github.railsrepo.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import model.ListItem;
import test.github.railsrepo.R;
import util.AppController;
import util.Constants;

public class MainActivity extends AppCompatActivity {

    private static final int TIMEOUT_MS = 30000;
    private ListView listView;
    private adapter.ListAdapter listAdapter;
    private ArrayList<ListItem> feedItems;
    MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupLayoutElements();
    }

    private void setupLayoutElements() {
        listView = (ListView) findViewById(R.id.lv_rails_repo_commits);
        feedItems = new ArrayList<>();
        listAdapter = new adapter.ListAdapter(this,feedItems);
        listView.setAdapter(listAdapter);
        fetchRailsRepoCommits(Constants.URL_TO_FETCH_COMMITS);
    }

    private void showProgressDialog() {
        materialDialog = new MaterialDialog.Builder(this)
                .title("Please Wait")
                .content("Fetching Commits...")
                .progress(true, 0)
                .show();
    }

    private void dismissProgressDialog(){
        if(materialDialog!=null && materialDialog.isShowing()){
            materialDialog.dismiss();
        }
    }

    private void fetchRailsRepoCommits(String urlToFetchRailsRepoCommits) {
        showProgressDialog();
        JsonArrayRequest jsonReq = new JsonArrayRequest(Request.Method.GET,
                urlToFetchRailsRepoCommits, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                parseJsonFeed(response);
                dismissProgressDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(listView,"Please try again later",Snackbar.LENGTH_SHORT).show();
                dismissProgressDialog();
            }
        }) {
            //Set a retry policy in case of SocketTimeout & ConnectionTimeout Exceptions.
            //Volley does retry for you if you have specified the policy.
            @Override
            public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
                retryPolicy = new DefaultRetryPolicy(
                        TIMEOUT_MS,
                        2,
                        2.0F);
                return super.setRetryPolicy(retryPolicy);
            }
        };
        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    private void parseJsonFeed(JSONArray response) {

        try {

            for (int i = 0; i < response.length(); i++) {
                JSONObject feedObj = (JSONObject) response.get(i);
                ListItem item = new ListItem();
                item.setCommitNumber("commit: "+feedObj.getString("sha"));
                JSONObject commitObject = feedObj.getJSONObject("commit");
                item.setCommitMessage("commit: "+commitObject.getString("message"));
                item.setCommitterName(commitObject.getJSONObject("author").getString("name"));
                item.setProfileImage(feedObj.getJSONObject("author").getString("avatar_url"));
                feedItems.add(item);
            }
            Log.d("CheckHereAgain","YES");
            listAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
