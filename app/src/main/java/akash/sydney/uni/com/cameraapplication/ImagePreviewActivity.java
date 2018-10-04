package akash.sydney.uni.com.cameraapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImagePreviewActivity extends AppCompatActivity {
    List<Bitmap> originalImages = new ArrayList<>();
    ImageView imageView;
    String filepath = null;

    public static Bitmap rotateImage(Bitmap myImg, int i) {
        Matrix matrix = new Matrix();
        matrix.postRotate(i);
        Bitmap rotated = Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(),
                matrix, true);
        return rotated;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_priview);

        filepath = getIntent().getStringExtra(MainActivity.FILE_PATH);
        if (filepath != null) {
            imageView = (ImageView) findViewById(R.id.imageView);
            Bitmap bImage = BitmapFactory.decodeFile(filepath);
            imageView.setImageBitmap(bImage);
        }
    }

    public void quitePreview(View view) {
       // setResult(Activity.RESULT_CANCELED);
        finish();

    }


    public void saveNewImage(View view) {
        if (filepath != null)
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(new File(filepath));

                final boolean saved = ((BitmapDrawable) imageView.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                Toast.makeText(this, "Image is Saved Successfully " + filepath, Toast.LENGTH_LONG).show();
                fileOutputStream.close();


            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    public void rotateClockWise(View view) {
        rotateImage(view, 90);
    }

    public void rotateAntiClockWise(View view) {
        rotateImage(view, -90);

    }

    private void rotateImage(View view, int i) {
        Bitmap myImg = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        originalImages.add(myImg);
        Bitmap   newImage = rotateImage(myImg.copy(myImg.getConfig(),true), i);
        imageView.setImageBitmap(newImage);
    }

    public void cropInRectangle(View view) {

        Bitmap myImg = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        originalImages.add(myImg);
        imageView.setImageBitmap(getRectangleBitmap(myImg.copy(myImg.getConfig(), true)));
    }

    public void cropInCircle(View view) {

        Bitmap myImg = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        originalImages.add(myImg);
        imageView.setImageBitmap(getOvalBitmap(myImg.copy(myImg.getConfig(), true)));
    }

    private Bitmap getOvalBitmap(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = Color.RED;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();

        return output;
    }

    private Bitmap getRectangleBitmap(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        int color = Color.RED;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        Rect rectNew = new Rect((int) (bitmap.getWidth() * 0.1), (int) (bitmap.getHeight() * 0.1), (int) (bitmap.getWidth() * 0.95), (int) (bitmap.getWidth() * 0.95));
        paint.setAntiAlias(true);


        canvas.drawRect(rectNew, paint);


        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public void undoChanges(View view) {
        if (originalImages.isEmpty()) {
            Toast.makeText(this, "This is original Image ", Toast.LENGTH_SHORT).show();
        } else {
            Bitmap lastImage = originalImages.get(originalImages.size() - 1);
            originalImages.remove(lastImage);
            imageView.setImageBitmap(lastImage);
        }
    }
}
