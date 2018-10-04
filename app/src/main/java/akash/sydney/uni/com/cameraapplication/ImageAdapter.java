package akash.sydney.uni.com.cameraapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private static final int pad = 4;
    private static final int Image_width = 300;
    private static final int Image_heigh = 300;
    private Context con;
    private List<String> filesList;

    public ImageAdapter(Context c, List<String> ids) {
        con = c;
        this.filesList = ids;
    }

    @Override
    public int getCount() {

        return filesList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;

    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView gridImageView = (ImageView) convertView;


        if (gridImageView == null) {
            gridImageView = new ImageView(con);
            gridImageView.setLayoutParams(new GridView.LayoutParams(Image_width, Image_heigh));
            gridImageView.setPadding(pad, pad, pad, pad);
            gridImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        // decode the image
        final BitmapFactory.Options decodeOption = new BitmapFactory.Options();
        decodeOption.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filesList.get(position), decodeOption);
        decodeOption.inSampleSize = 4;
        decodeOption.inJustDecodeBounds = false;
        Bitmap myBitmap = BitmapFactory.decodeFile(filesList.get(position), decodeOption);

        //setting Image
        gridImageView.setImageBitmap(myBitmap);

        return gridImageView;
    }

}