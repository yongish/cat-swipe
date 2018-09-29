package com.zhiyong.cat_swipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    int windowwidth;
    int screenCenter;
    int x_cord, y_cord, x, y;
    float oldX, newX, deltaX;
    int Likes = 0;
    public RelativeLayout parentView;
    float alphaValue = 0;
    private Context context;

    AsyncHttpClient client = new AsyncHttpClient();
    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        parentView = findViewById(R.id.main_layoutview);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        windowwidth = displaymetrics.widthPixels;

        screenCenter = windowwidth / 2;

        sharingIntent.setType("text/plain");
        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_TEXT,
                "Swipe on cute cats: https://play.google.com/store/apps/details?id=com.zhiyong.cat_swipe");

        List<String> queue = fillQueue(new LinkedList<String>());
        loadACard(queue, windowwidth);
        loadACard(queue, windowwidth);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void share(MenuItem mi) {
        startActivity(Intent.createChooser(sharingIntent, "Let's swipe on cute cats!"));
    }

    private static Bitmap createSquaredBitmap(Bitmap srcBmp) {
        int dim = Math.max(srcBmp.getWidth(), srcBmp.getHeight());
        Bitmap dstBmp = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(dstBmp);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(srcBmp, (dim - srcBmp.getWidth()) / 2, (dim - srcBmp.getHeight()) / 2, null);

        return dstBmp;
    }

    private List<String> fillQueue(List<String> queue) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("paths.txt")));
            String path;

            // Read paths into an queue.
            while ((path = reader.readLine()) != null) {
                queue.add(path);
            }
        } catch (IOException e) {
            System.err.println("Error in loading paths.");
        }
        Collections.shuffle(queue);
        return queue;
    }

    private void loadACard(final List<String> queue, final int windowwidth) {
        // Dequeue a path. TODO: Queue can run out.
        if (queue.isEmpty()) {
            fillQueue(queue);
        }
        String path = queue.remove(0);

        Log.d("path", path);
        LayoutInflater inflate =
                (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View containerView = inflate.inflate(R.layout.custom_tinder_layout, null);

        final ImageView userIMG = containerView.findViewById(R.id.userIMG);
        final RelativeLayout relativeLayoutContainer = containerView.findViewById(R.id.relative_container);


        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        containerView.setLayoutParams(layoutParams);

        client.get(path, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                Bitmap bitmap = BitmapFactory.decodeByteArray(response,0, response.length - 1);

                bitmap = createSquaredBitmap(bitmap);

                BitmapDrawable background = new BitmapDrawable(getResources(), bitmap);
                userIMG.setBackground(background);

                LayoutParams layoutTvParams = new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);


                // ADD dynamically like TextView on image.
                final TextView tvLike = new TextView(context);
                tvLike.setLayoutParams(layoutTvParams);
                tvLike.setPadding(10, 10, 10, 10);
                tvLike.setBackground(getResources().getDrawable(R.drawable.btnlikeback));
                tvLike.setText(R.string.cute);
                tvLike.setGravity(Gravity.CENTER);
                tvLike.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tvLike.setTextSize(25);
                tvLike.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                tvLike.setX(20);
                tvLike.setY(100);
                tvLike.setRotation(-50);
                tvLike.setAlpha(alphaValue);
                relativeLayoutContainer.addView(tvLike);


                // ADD dynamically dislike TextView on image.
                final TextView tvUnLike = new TextView(context);
                tvUnLike.setLayoutParams(layoutTvParams);
                tvUnLike.setPadding(10, 10, 10, 10);
                tvUnLike.setBackground(getResources().getDrawable(R.drawable.btnlikeback));
                tvUnLike.setText(R.string.not_cute);
                tvUnLike.setGravity(Gravity.CENTER);
                tvUnLike.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tvUnLike.setTextSize(25);
                tvUnLike.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                tvUnLike.setX(windowwidth-200);
                tvUnLike.setY(150);
                tvUnLike.setRotation(50);
                tvUnLike.setAlpha(alphaValue);
                relativeLayoutContainer.addView(tvUnLike);

                final ImageView ivPaw = new ImageView(context);
                ivPaw.setBackground(getResources().getDrawable(R.drawable.pink_paw));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
                ivPaw.setLayoutParams(layoutParams);
                ivPaw.setVisibility(View.INVISIBLE);
                relativeLayoutContainer.addView(ivPaw);


                // Touch listener on the image layout to swipe image right or left.
                relativeLayoutContainer.setOnTouchListener(new OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        x_cord = (int) event.getRawX();
                        y_cord = (int) event.getRawY();

                        containerView.setX(0);
                        containerView.setY(0);

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:

                                x = (int) event.getX();
                                y = (int) event.getY();

                                oldX = event.getRawX();

                                Log.v("On touch", x + " " + y);
                                break;
                            case MotionEvent.ACTION_MOVE:

                                newX = event.getRawX();
                                deltaX = newX - oldX;
                                x_cord = (int) event.getRawX();
                                // smoother animation.
                                y_cord = (int) event.getRawY();

                                containerView.setX(x_cord - x);
                                containerView.setY(100);

                                ivPaw.setVisibility(View.VISIBLE);
                                ivPaw.setX(x - 100);
                                ivPaw.setY(y - 300);

                                Likes = 0;
                                tvLike.setAlpha(0);
                                tvUnLike.setAlpha(0);
                                if (deltaX > 8) {
                                    containerView.setRotation((float) ((screenCenter - x_cord) * (Math.PI / 256)));
                                    tvLike.setAlpha(1);
                                    tvUnLike.setAlpha(0);
                                    Likes = 2;
                                } else if (deltaX < -8){
                                    // rotate image while moving
                                    containerView.setRotation((float) ((screenCenter - x_cord) * (Math.PI / 256)));
                                    tvUnLike.setAlpha(1);
                                    tvLike.setAlpha(0);
                                    Likes = 1;
                                }

                                break;
                            case MotionEvent.ACTION_UP:
                                x_cord = (int) event.getRawX();
                                y_cord = (int) event.getRawY();

                                Log.e("X Point", "" + x_cord + " , Y " + y_cord);
                                tvUnLike.setAlpha(0);
                                tvLike.setAlpha(0);

                                ivPaw.setVisibility(View.INVISIBLE);

                                if (Likes == 0) {
                                    Toast.makeText(context, "NOTHING", Toast.LENGTH_SHORT).show();
                                    Log.e("Event_Status :-> ", "Nothing");
                                    containerView.setX(0);
                                    containerView.setY(0);
                                    containerView.setRotation(0);
                                } else if (Likes == 1) {
                                    Toast.makeText(context, R.string.not_cute, Toast.LENGTH_SHORT).show();
                                    Log.e("Event_Status :-> ", "UNLIKE");
                                    parentView.removeView(containerView);

                                    // Load the next picture.
                                    loadACard(queue, windowwidth);

                                } else if (Likes == 2) {
                                    Toast.makeText(context, R.string.cute, Toast.LENGTH_SHORT).show();
                                    Log.e("Event_Status :-> ", "Liked");
                                    parentView.removeView(containerView);

                                    // Load the next picture.
                                    loadACard(queue, windowwidth);
                                }
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

                parentView.addView(containerView, 0);
            }

            // Todo: Try to load a card again if load fails.

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });

    }
}
