package com.wpfeedreader.android;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.wpfeedreader.android.Models.Post;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private List<Post> postsList;
    private ListView postList;
    private RelativeLayout layoutNoPosts;
    CircleProgressBar progress1;
    private AdView mAdView;
    private DrawerView drawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer = (DrawerView) findViewById(R.id.drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.color_primary_dark));
        drawerLayout.addDrawerListener(drawerToggle);
        drawerLayout.closeDrawer(drawer);

        drawer.addItem(new DrawerItem()
                        .setImage(ContextCompat.getDrawable(this, R.drawable.ic_email))
                        .setTextPrimary("Home")
                //  .setTextSecondary(getString(R.string.lorem_ipsum_long))
        );

        drawer.addDivider();

        drawer.addItem(new DrawerItem()
                        .setRoundedImage((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.cat_1))
                        .setTextPrimary("Kanal")
                //   .setTextSecondary(getString(R.string.lorem_ipsum_long))
        );

        drawer.addDivider();
        //  drawer.addItem(new DrawerHeaderItem().setTitle(getString(R.string.lorem_ipsum_short)));
        drawer.addItem(new DrawerItem()
                        .setRoundedImage((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.cat_1))
                        .setTextPrimary("kanel")
                //   .setTextSecondary(getString(R.string.lorem_ipsum_long))
        );
        drawer.addDivider();
        drawer.addItem(new DrawerItem()
                        .setRoundedImage((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.cat_2), DrawerItem.SMALL_AVATAR)
                        .setTextPrimary("kanul")
                //  .setTextSecondary(getString(R.string.lorem_ipsum_long), DrawerItem.THREE_LINE)
        );

        // drawer.selectItem(1);
        drawer.setOnItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem item, long id, int position) {
                drawer.selectItem(position);

                if(position == 0)
                {
                    Toast.makeText(MainActivity.this, "satuu item #" + position, Toast.LENGTH_SHORT).show();

                }

                if(position == 1)
                {
                    Toast.makeText(MainActivity.this, "duaaa item #" + position, Toast.LENGTH_SHORT).show();

                }
                if(position == 2)
                {
                    Toast.makeText(MainActivity.this, "tigaa item #" + position, Toast.LENGTH_SHORT).show();

                }
                if(position == 5)
                {
                    Toast.makeText(MainActivity.this, "four item #" + position, Toast.LENGTH_SHORT).show();

                }
                if(position == 7)
                {
                    Toast.makeText(MainActivity.this, "five item #" + position, Toast.LENGTH_SHORT).show();

                }






            }
        });


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);



        progress1 = (CircleProgressBar) findViewById(R.id.progressBar);
        progress1.setShowArrow(true);

        postList = (ListView)findViewById(R.id.postList);
        layoutNoPosts = (RelativeLayout) findViewById(R.id.layoutNoPosts);
        layoutNoPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initContent();
            }
        });

        initContent();
    }

    private void initContent(){

       // progressDialog = new ProgressDialog(MainActivity.this);
       // progressDialog.setMessage(getString(R.string.loading));
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressDialog.show();
        progress1.setVisibility(View.VISIBLE);
        // Request

        StringRequest request = new StringRequest(Request.Method.GET, Config.URL_POSTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String source) {
                JsonParser parser = new JsonParser();
                JsonArray postsArray = (JsonArray)parser.parse(source);
                postsList = new ArrayList<Post>();

                for(int i = 0; i < postsArray.size(); i ++){
                    JsonObject jsonPost = (JsonObject) postsArray.get(i);
                    Post post = new Post();
                    post.setID(jsonPost.get("id").getAsInt());
                    post.setTitle(jsonPost.get("title").getAsJsonObject().get("rendered").getAsString());
                    post.setContent(jsonPost.get("content").getAsJsonObject().get("rendered").getAsString());

                    // Thumb
                    String thumbUrl = jsonPost.get("_embedded").getAsJsonObject()
                            .get("wp:featuredmedia").getAsJsonArray().get(0).getAsJsonObject().get("media_details").getAsJsonObject()
                            .get("sizes").getAsJsonObject().get("thumbnail").getAsJsonObject().get("source_url").getAsString();
                    post.setThumbUrl(thumbUrl);

                    // Image
                    String imageUrl = jsonPost.get("_embedded").getAsJsonObject()
                            .get("wp:featuredmedia").getAsJsonArray().get(0).getAsJsonObject().get("media_details").getAsJsonObject()
                            .get("sizes").getAsJsonObject().get("full").getAsJsonObject().get("source_url").getAsString();
                    post.setImageUrl(imageUrl);

                    post.setShareUrl(jsonPost.get("link").getAsString());

                    // Author
                    String author = jsonPost.get("_embedded").getAsJsonObject()
                            .get("author").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
                    post.setAuthor(author);

                    postsList.add(post);
                }

                postList.setAdapter(new PostsListAdapter(MainActivity.this, postsList));
                //progressDialog.dismiss();
                progress1.setVisibility(View.INVISIBLE);
                showList();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
              //  progressDialog.dismiss();
                progress1.setVisibility(View.INVISIBLE);
                showTextMessage();
                Toast.makeText(MainActivity.this, "Kesalahan saat mendownload artikel", Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);

        postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post = postsList.get(position);

                Intent intent = new Intent(getApplicationContext(),PostDetails.class);
                intent.putExtra("postObject", post);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                overridePendingTransition(0,0);
                startActivity(intent);
            }
        });







    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_contact:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"gurnari.dev@gmail.com"});
                try {
                    startActivity(Intent.createChooser(intent, getString(R.string.contact)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, getString(R.string.contact_error), Toast.LENGTH_SHORT).show();
                }
                return true;*/

            case R.id.action_favorite:
                startActivity(new Intent(getApplicationContext(),Favourites.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showList() {
        layoutNoPosts.setVisibility(View.GONE);
        postList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


    private void showTextMessage() {
        layoutNoPosts.setVisibility(View.VISIBLE);
        postList.setVisibility(View.GONE);
    }
}
