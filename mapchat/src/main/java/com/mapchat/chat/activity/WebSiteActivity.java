package com.mapchat.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetCallback;
import com.avoscloud.chat.R;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.squareup.picasso.Picasso;

public class WebSiteActivity extends AppCompatActivity {

    private MapView mMapview = null;
    private BaiduMap mBaiduMap;
    private ImageButton mButton;
    private TextView mName;
    private TextView mDescription;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_site);
        mMapview = (MapView)findViewById(R.id.mymapview);
        mButton = (ImageButton) findViewById(R.id.mybutton2);
        mBaiduMap = mMapview.getMap();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WebSiteActivity.this,EnjoyActivity.class);
                startActivity(intent);
            }
        });
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(getString(R.string.detail));

        mName = (TextView) findViewById(R.id.name_detail);
        mDescription = (TextView) findViewById(R.id.description_detail);
        mImage = (ImageView) findViewById(R.id.image_detail);

        String goodsObjectId = getIntent().getStringExtra("goodsObjectId");
        AVObject avObject = AVObject.createWithoutData("Product", goodsObjectId);
        avObject.fetchInBackground("owner", new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                mName.setText(avObject.getAVUser("owner") == null ? "" : avObject.getAVUser("owner").getUsername());
                mDescription.setText(avObject.getString("description"));
                Picasso.with(WebSiteActivity.this).load(avObject.getAVFile("image") == null ? "www" : avObject.getAVFile("image").getUrl()).into(mImage);
            }
        });
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//        }
//        return super.onOptionsItemSelected(item);
//    }

}
