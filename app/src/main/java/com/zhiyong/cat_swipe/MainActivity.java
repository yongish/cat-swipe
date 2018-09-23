package com.zhiyong.cat_swipe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {


    int windowwidth;
    int screenCenter;
    int x_cord, y_cord, x, y;
    int Likes = 0;
    public RelativeLayout parentView;
    float alphaValue = 0;
    private Context context;

    ArrayList<UserDataModel> userDataModelArrayList;
    AsyncHttpClient client = new AsyncHttpClient();

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        parentView = (RelativeLayout) findViewById(R.id.main_layoutview);

        windowwidth = getWindowManager().getDefaultDisplay().getWidth();

        screenCenter = windowwidth / 2;

        userDataModelArrayList = new ArrayList<>();

//        getArrayData();

        BufferedReader reader;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("paths.txt")));
            String path;
            while ((path = reader.readLine()) != null) {

                Log.d("path", path);
                UserDataModel model = new UserDataModel();
                LayoutInflater inflate =
                        (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                final View containerView = inflate.inflate(R.layout.custom_tinder_layout, null);

                final ImageView userIMG = (ImageView) containerView.findViewById(R.id.userIMG);
                final RelativeLayout relativeLayoutContainer = (RelativeLayout) containerView.findViewById(R.id.relative_container);


                LayoutParams layoutParams = new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

                containerView.setLayoutParams(layoutParams);

//                containerView.setTag(i);
//            String imageUri = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ28xTemgDxAUY0baMY5VeOsLp9jJfWLyYyztCJCN-tEn_H-1-0_A";
//                String imageUri = "http://i.imgur.com/DvpvklR.png";
//            userIMG.setImageURI(null);
//            userIMG.setImageURI(Uri.parse(imageUri));
//            userIMG.setBackgroundResource(userDataModelArrayList.get(i).getPhoto());

                client.get(path, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        // called when response HTTP status is "200 OK"
                        Bitmap bitmap = BitmapFactory.decodeByteArray(response,0, response.length - 1);

//                        int originalHeight = bitmap.getHeight();
//                        int originalWidth = bitmap.getWidth();
                        // Set width to 500. Scale height by same ratio.
//                        int width = 500;
//                        int height = bitmap.getHeight() * 500 / bitmap.getWidth();

                        bitmap = createSquaredBitmap(bitmap);

                        BitmapDrawable background = new BitmapDrawable(getResources(), bitmap);
                        userIMG.setBackgroundDrawable(background);


//            ImageView ivBasicImage = (ImageView) findViewById(R.id.ivBasicImage);

//            m_view.setRotation(i);
//            containerView.setPadding(0, i, 0, 0);

                        LayoutParams layoutTvParams = new LayoutParams(
                                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);


                        // ADD dynamically like TextView on image.
                        final TextView tvLike = new TextView(context);
                        tvLike.setLayoutParams(layoutTvParams);
                        tvLike.setPadding(10, 10, 10, 10);
                        tvLike.setBackgroundDrawable(getResources().getDrawable(R.drawable.btnlikeback));
                        tvLike.setText("LIKE");
                        tvLike.setGravity(Gravity.CENTER);
                        tvLike.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        tvLike.setTextSize(25);
                        tvLike.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                        tvLike.setX(20);
                        tvLike.setY(100);
                        tvLike.setRotation(-50);
                        tvLike.setAlpha(alphaValue);
                        relativeLayoutContainer.addView(tvLike);


//            ADD dynamically dislike TextView on image.
                        final TextView tvUnLike = new TextView(context);
                        tvUnLike.setLayoutParams(layoutTvParams);
                        tvUnLike.setPadding(10, 10, 10, 10);
                        tvUnLike.setBackgroundDrawable(getResources().getDrawable(R.drawable.btnlikeback));
                        tvUnLike.setText("UNLIKE");
                        tvUnLike.setGravity(Gravity.CENTER);
                        tvUnLike.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        tvUnLike.setTextSize(25);
                        tvUnLike.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                        tvUnLike.setX(500);
                        tvUnLike.setY(150);
                        tvUnLike.setRotation(50);
                        tvUnLike.setAlpha(alphaValue);
                        relativeLayoutContainer.addView(tvUnLike);


//                TextView tvName = (TextView) containerView.findViewById(R.id.tvName);
//                TextView tvTotLikes = (TextView) containerView.findViewById(R.id.tvTotalLikes);


//                tvName.setText(userDataModelArrayList.get(i).getName());
//                tvTotLikes.setText(userDataModelArrayList.get(i).getTotalLikes());

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


                                        Log.v("On touch", x + " " + y);
                                        break;
                                    case MotionEvent.ACTION_MOVE:

                                        x_cord = (int) event.getRawX();
                                        // smoother animation.
                                        y_cord = (int) event.getRawY();

                                        containerView.setX(x_cord - x);
                                        containerView.setY(y_cord - y);


                                        if (x_cord >= screenCenter) {
                                            containerView.setRotation((float) ((screenCenter - x_cord) * (Math.PI / 256)));
                                            if (x_cord > (screenCenter + (screenCenter / 2))) {
                                                tvLike.setAlpha(1);
                                                if (x_cord > (windowwidth - (screenCenter / 4))) {
                                                    Likes = 2;
                                                } else {
                                                    Likes = 0;
                                                }
                                            } else {
                                                Likes = 0;
                                                tvLike.setAlpha(0);
                                            }
                                            tvUnLike.setAlpha(0);
                                        } else {
                                            // rotate image while moving
                                            containerView.setRotation((float) ((screenCenter - x_cord) * (Math.PI / 256)));
                                            if (x_cord < (screenCenter / 2)) {
                                                tvUnLike.setAlpha(1);
                                                if (x_cord < screenCenter / 4) {
                                                    Likes = 1;
                                                } else {
                                                    Likes = 0;
                                                }
                                            } else {
                                                Likes = 0;
                                                tvUnLike.setAlpha(0);
                                            }
                                            tvLike.setAlpha(0);
                                        }

                                        break;
                                    case MotionEvent.ACTION_UP:

                                        x_cord = (int) event.getRawX();
                                        y_cord = (int) event.getRawY();

                                        Log.e("X Point", "" + x_cord + " , Y " + y_cord);
                                        tvUnLike.setAlpha(0);
                                        tvLike.setAlpha(0);

                                        if (Likes == 0) {
                                            Toast.makeText(context, "NOTHING", Toast.LENGTH_SHORT).show();
                                            Log.e("Event_Status :-> ", "Nothing");
                                            containerView.setX(0);
                                            containerView.setY(0);
                                            containerView.setRotation(0);
                                        } else if (Likes == 1) {
                                            Toast.makeText(context, "UNLIKE", Toast.LENGTH_SHORT).show();
                                            Log.e("Event_Status :-> ", "UNLIKE");
                                            parentView.removeView(containerView);
                                        } else if (Likes == 2) {
                                            Toast.makeText(context, "LIKED", Toast.LENGTH_SHORT).show();
                                            Log.e("Event_Status :-> ", "Liked");
                                            parentView.removeView(containerView);
                                        }
                                        break;
                                    default:
                                        break;
                                }
                                return true;
                            }
                        });

                        parentView.addView(containerView);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }

                    @Override
                    public void onRetry(int retryNo) {
                        // called when request is retried
                    }
                });


                // Load picture.
            }

        } catch (IOException e) {
            System.err.println("Error in loading paths.");
        }




    }
    private static Bitmap createSquaredBitmap(Bitmap srcBmp) {
        int dim = Math.max(srcBmp.getWidth(), srcBmp.getHeight());
        Bitmap dstBmp = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(dstBmp);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(srcBmp, (dim - srcBmp.getWidth()) / 2, (dim - srcBmp.getHeight()) / 2, null);

        return dstBmp;
    }


    private void getArrayData() {

        UserDataModel model = new UserDataModel();
        model.setName("Card 1 ");
        model.setTotalLikes("3 M");
        model.setPhoto(com.zhiyong.cat_swipe.R.drawable.image1);
        userDataModelArrayList.add(model);


        UserDataModel model2 = new UserDataModel();
        model2.setName("Card 2 ");
        model2.setTotalLikes("2 M");
        model2.setPhoto(com.zhiyong.cat_swipe.R.drawable.image2);
        userDataModelArrayList.add(model2);

        UserDataModel model3 = new UserDataModel();
        model3.setName("Card 3 ");
        model3.setTotalLikes("3 M");
        model3.setPhoto(com.zhiyong.cat_swipe.R.drawable.image3);
        userDataModelArrayList.add(model3);


        UserDataModel model4 = new UserDataModel();
        model4.setName("Card 4 ");
        model4.setTotalLikes("4 M");
        model4.setPhoto(com.zhiyong.cat_swipe.R.drawable.image1);
        userDataModelArrayList.add(model4);


        UserDataModel model5 = new UserDataModel();
        model5.setName("Card 5 ");
        model5.setTotalLikes("5 M");
        model5.setPhoto(com.zhiyong.cat_swipe.R.drawable.image2);
        userDataModelArrayList.add(model5);

        UserDataModel model6 = new UserDataModel();
        model6.setName("Card 6 ");
        model6.setTotalLikes("6 M");
        model6.setPhoto(com.zhiyong.cat_swipe.R.drawable.image3);
        userDataModelArrayList.add(model6);


        UserDataModel model7 = new UserDataModel();
        model7.setName("Card 7 ");
        model7.setTotalLikes("7 M");
        model7.setPhoto(com.zhiyong.cat_swipe.R.drawable.image1);
        userDataModelArrayList.add(model7);


        UserDataModel model8 = new UserDataModel();
        model8.setName("Card 8 ");
        model8.setTotalLikes("8 M");
        model8.setPhoto(com.zhiyong.cat_swipe.R.drawable.image2);
        userDataModelArrayList.add(model8);

        UserDataModel model9 = new UserDataModel();
        model9.setName("Card 9 ");
        model9.setTotalLikes("9 M");
        model9.setPhoto(com.zhiyong.cat_swipe.R.drawable.image3);
        userDataModelArrayList.add(model9);

        Collections.reverse(userDataModelArrayList);

    }
}
