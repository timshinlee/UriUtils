package com.example.uriutils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.timshinlee.utils.UriUtils;

public class MainActivity extends AppCompatActivity {

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
        startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), 0);
    }

    public void pick(View view) {
        startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), 0);
    }

    public void openDocument(View view) {
        startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("image/*"), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
//            mText.setText(mText.getText() + "\n" + data.getData());
            mText.setText(mText.getText() + "\n" + UriUtils.getUriPath(this, data.getData()));
            mImage.setImageBitmap(BitmapFactory.decodeFile(UriUtils.getUriPath(this, data.getData())));
//            mImage.setImageBitmap(UriUtils.getBitmapFromUri(this,data.getData()));
        }
    }

    private void getBitmap(String path){
        final Bitmap bitmap = BitmapFactory.decodeFile(path);
    }
}
