package com.example.uriutils;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.timshinlee.utils.UriUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView mText;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = (TextView) findViewById(R.id.text);
        mImage = (ImageView) findViewById(R.id.image);
    }

    public void getContent(View view) {
        startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), 0); // ES文件管理器返回的uri是mediaprovider，需要ReadExternal权限
    }

    public void pick(View view) {
        startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), 0);
    }

    public void openDocument(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("image/*"), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            Log.e(TAG, "onActivityResult: " + data.getData());
            mText.setText(mText.getText() + "\n" + UriUtils.getUriPath(this, data.getData()));
            mImage.setImageBitmap(BitmapFactory.decodeFile(UriUtils.getUriPath(this, data.getData())));
//            mImage.setImageBitmap(UriUtils.getBitmapFromUri(this,data.getData()));
        }
    }
}
