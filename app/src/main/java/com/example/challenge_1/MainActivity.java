package com.example.challenge_1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_TAKE_PICTURE = 123;
    private Button takeImage, loadImage;
    private ImageView camera_image;
    private EditText editText;
    private SQLiteOpenHelper DBHelper;
    private SQLiteDatabase database;



    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if (requestCode ==REQ_CODE_TAKE_PICTURE
                && resultCode == RESULT_OK)
        {
            Bitmap bmp= (Bitmap)intent.getExtras().get("data");
            camera_image.setImageBitmap(bmp);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 0, stream);
            database = DBHelper.getWritableDatabase();
            ContentValues pictureValues = new ContentValues();
            pictureValues.put("IMAGE", stream.toByteArray());
            long value = database.insert("PICTURES", null, pictureValues);
            database.close();
            Toast toast = Toast.makeText(this, "Enter ID " + String.valueOf(value) + " to view again.", Toast.LENGTH_LONG);
            toast.show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper = new DBHelper(this);

        takeImage = findViewById(R.id.image);
        loadImage = findViewById(R.id.loadImage);
        editText = findViewById(R.id.editText);
        camera_image = findViewById(R.id.camera_image);

        takeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(picIntent, REQ_CODE_TAKE_PICTURE);
            }
        });

        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long value = Long.parseLong(editText.getText().toString());

                if(value != 0) {
                    database = DBHelper.getWritableDatabase();
                    Cursor c = database.rawQuery("SELECT IMAGE FROM PICTURES WHERE ID = " + value, null);
                    if(c.moveToFirst()) {
                        byte[] blob = c.getBlob(c.getColumnIndex("IMAGE"));
                        c.close();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                        camera_image.setImageBitmap(bitmap);
                    }
                    c.close();
                    database.close();
                }
            }
        });
    }
}
