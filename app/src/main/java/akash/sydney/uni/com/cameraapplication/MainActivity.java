package akash.sydney.uni.com.cameraapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static String FILE_PATH = "ABSOLUTE IMAGE FILE PATH";
    ArrayList<String> listOfAllImages;

    ImageAdapter imageAdapter;
    int CAMERA_SERVICE = 100;
    int GALARY_SERVICE = 100;
    String APP_TAG = "AKASH CAMERA APP";
    Intent imagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        imagePreview = new Intent(this, ImagePreviewActivity.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listOfAllImages = new ArrayList<String>();

        getAllTheImages();
        GridView gridview = (GridView) findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(this, listOfAllImages);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                imagePreview.putExtra(MainActivity.FILE_PATH, listOfAllImages.get(position));
                startActivityForResult(imagePreview, GALARY_SERVICE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNewCreatedPhoto();
        imageAdapter.notifyDataSetChanged();
    }

    public void getAllTheImages() {
        Cursor cursor;
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        cursor = this.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        addImagesToList(cursor);
    }

    public void onTakePhotoClick(View v) {

        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, CAMERA_SERVICE);


    }


    private void addImagesToList(Cursor cursor) {
        String imagePath = null;
        int column_index_data, column_index_folder_name;
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        while (cursor.moveToNext()) {

            imagePath = cursor.getString(column_index_data);
            listOfAllImages.add(imagePath);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        getNewCreatedPhoto();
        imageAdapter.notifyDataSetChanged();
    }

    private void getNewCreatedPhoto() {

        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "/Images/");
        if(!directory.exists())
        {
            return;
        }
        File[] allPhotos = directory.listFiles();
        if (allPhotos != null) {
            for (File photo : allPhotos) {
                String filePath = photo.getAbsolutePath();
                if (!listOfAllImages.contains(filePath)) {
                    listOfAllImages.add(0, filePath);
                }
            }
        }
    }
}
