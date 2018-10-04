package akash.sydney.uni.com.cameraapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {
    Camera camera;
    FrameLayout frameLayout;
    DisplayCamera displayCamera;
    Intent imagePreview;
    Context context;
    ImageView lastImage;
    String latestFile= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
        lastImage=findViewById(R.id.lastImageView);
        imagePreview = new Intent(this, ImagePreviewActivity.class);
        frameLayout = (FrameLayout) findViewById(R.id.cameraView);

         getLastImage();

    }
    @Override
    protected void onResume() {
        super.onResume();
        camera = Camera.open();
        displayCamera = new DisplayCamera(this, camera);
        frameLayout.addView(displayCamera);

    }

    private void getLastImage() {
        File last = null;
        boolean firstFile = true;
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "/Images/");
        if (!directory.exists()) {
            return;
        }
        File[] allPhotos = directory.listFiles();
        if (allPhotos != null) {
            for (File photo : allPhotos) {
                if (firstFile) {
                    firstFile = false;
                    last = photo;
                }

                if (photo.lastModified() > last.lastModified()) {
                    last = photo;
                }
            }
        }
        if(last!=null)
        {
            Bitmap bImage = BitmapFactory.decodeFile(last.getAbsolutePath());
            lastImage.setImageBitmap(bImage);
            latestFile=last.getAbsolutePath();
        }
    }

    /*Take Image
     * */
    public void captureImage(View view) {
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                File newFile = createNewImageFile();
                if (newFile != null)
                    try {

                        FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                        Bitmap bitmap = ImagePreviewActivity.rotateImage(BitmapFactory.decodeByteArray(data, 0, data.length), 90);
                        final boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        Toast.makeText(context, "Image is Saved Successfully " + newFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        fileOutputStream.close();
                        lastImage.setImageBitmap(bitmap);
                        latestFile=newFile.getAbsolutePath();
                        imagePreview.putExtra(MainActivity.FILE_PATH, newFile.getAbsolutePath());
                        releaseCamera();
                        startActivity(imagePreview);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        });
    }
    public void previewLastImage(View view) {
        if(latestFile==null)
        {
            Toast.makeText(context,"Please take new picture",Toast.LENGTH_SHORT).show();
        }
        else
        {
            imagePreview.putExtra(MainActivity.FILE_PATH, latestFile);
            startActivity(imagePreview);
        }
        releaseCamera();
    }

    private void releaseCamera() {
        frameLayout.removeView(displayCamera);
        camera.stopPreview();
    }

    public void quiteCamera(View view) {
        setResult(Activity.RESULT_CANCELED);
        releaseCamera();
        finish();

    }

    private File createNewImageFile() {
        Date newDate = new Date();
        SimpleDateFormat spf = new SimpleDateFormat("ddmmyyyyhhmmss");
        String date = spf.format(newDate);
        String fileName = "Img_" + date + ".jpg";
        File file = null;
        try {
            String folderName = "/Images/";


            File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                    folderName);


            if (!directory.exists() && !directory.mkdirs()) {
                directory.createNewFile();
            }


            file = new File(directory.getPath() + File.separator + fileName);
            file.createNewFile();


        } catch (Exception ex) {
            Log.d("File Creation Ex", ex.getStackTrace().toString());
        }
        return file;
    }

}
