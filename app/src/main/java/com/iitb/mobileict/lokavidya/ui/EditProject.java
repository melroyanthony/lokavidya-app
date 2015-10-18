package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.iitb.mobileict.lokavidya.Projectfile;
import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.ui.shotview.GalleryItem;
import com.iitb.mobileict.lokavidya.ui.shotview.ViewShots;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saifur on 16/10/15.
 */

public class EditProject extends Activity {

    String projectName;
    ImageAdapter1 imageadapter;
    Button btnDelete;

    public static int RESIZE_FACTOR = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        projectName = intent.getStringExtra("projectname");
        setContentView(R.layout.activity_edit_project);
        btnDelete=(Button)findViewById(R.id.btnDeleteImg);
        loadImages();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = imageReturnedIntent.getData();
                    System.out.println("IIIMMMAAAGGGEEEE UUURRRIIII...." + imageUri.toString());


                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        bitmap = getResizedBitmap(bitmap, RESIZE_FACTOR);
                        Projectfile f = new Projectfile(getApplicationContext());
                        f.addImage(bitmap, projectName);
                        loadImages();
                    } catch (IOException fe) {
                        toast("Image file not found in the library");
                    }
                }
                break;
            case 2:

                if (resultCode == Activity.RESULT_OK) {
                    Uri takenPhotoUri = getPhotoFileUri("temp.png");

                    Bitmap photo = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                    photo = getResizedBitmap(photo, RESIZE_FACTOR);

                    Projectfile f = new Projectfile(getApplicationContext());
                    f.addImage(photo, projectName);
                    loadImages();
                } else {
                    Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    public void toast(String text) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }

    public void gallery(View v) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void ImageClickCallBack(int position) {
        Intent intent = new Intent(getApplicationContext(), Recording.class);
        intent.putExtra("projectname", projectName);
        Projectfile f = new Projectfile(getApplicationContext());
        List<String> ImageNames = f.getImageNames(projectName);
        String imagefilename = ImageNames.get(position);

        imagefilename = imagefilename.substring(0, imagefilename.length() - 4);

        intent.putExtra("filename", imagefilename);
        startActivity(intent);
    }



    public void takePic(View v) {


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri("temp.png"));

        startActivityForResult(intent, 2);

    }

    public Uri getPhotoFileUri(String fileName) {


        if (isExternalStorageAvailable()) {


            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "lokavidya_images");

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {

            }


            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));


        }
        return null;
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public void proceed(View view) {
        //ImageAdapter1 ia = new ImageAdapter1();
        int check = imageadapter.getCount();
        System.out.println("CCCOOOUUNNNTTT" + check + "");
        if (check > 0) {
            Intent intent1 = new Intent(getApplicationContext(), ViewShots.class);
            intent1.putExtra("projectname", projectName);
            startActivity(intent1);
        } else
            Toast.makeText(EditProject.this, "Empty project!!!", Toast.LENGTH_LONG).show();

    }

    public void loadImages() {
        Projectfile f = new Projectfile(this);
        List<String> ImageNames = f.getImageNames(projectName);

        ArrayList<GalleryItem> galleryItemsList = new ArrayList<GalleryItem>();

        String imagefilename;
        //= ImageNames.get(position);

        File sdCard = Environment.getExternalStorageDirectory();

        Bitmap myBitmap;

        File imgDir = new File (sdCard.getAbsolutePath() + "/lokavidya"+"/"+projectName+"/images");
        File image_file;
        for(int i=0;i<ImageNames.size();i++)
        {
            imagefilename = ImageNames.get(i);
            // image_file=  new File(imgDir, imagefilename);
            //myBitmap = BitmapFactory.decodeFile(image_file.getAbsolutePath());
            //galleryItemsList.add(new GalleryItem(myBitmap,i,false));
            galleryItemsList.add(new GalleryItem(imagefilename,i,false));

        }

        for(int i=0;i<galleryItemsList.size();i++)
            System.out.println("loading image pos------->"+galleryItemsList.get(i).position+"---------"+galleryItemsList.get(i).imgFileName);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        imageadapter = new ImageAdapter1(this,R.layout.galleryitem,galleryItemsList);
        gridview.setAdapter(imageadapter);

    }

    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;

    }

    public void deletePressed(View v){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(EditProject.this);
        builder1.setTitle("Sure?");
        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //  for(int i=imageadapter.getBox().size();i>=0;i--)
                for (GalleryItem p : imageadapter.getBox())
                {
                    // GalleryItem p = imageadapter.getBox().get(i);
                    if(p.box)
                    {
                        System.out.println("image pos----------->" + p.position);

                        imageadapter.remove(p);
                        //  Projectfile f = new Projectfile(getApplicationContext());
                        //List<String> ImageNames = f.getImageNames(projectName);



                    }
                    //loadImages();
                }
                // imageadapter.removeTask();
                imageadapter.notifyDataSetChanged();
                // selectedFileInt.clear();
                loadImages();
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               /* for (int i = 0; i < numberOfImages; i++) {
                                    System.out.println("Arrraaaayyyyy  at  /////" + i + ".......is selected/not" + selectedFileInt.get(i));
                                }*/
                                dialog.cancel();
                            }
                        }

                );
        builder1.create().

                show();


    }


    public class ImageAdapter1 extends ArrayAdapter<GalleryItem> {
        Context ctx;
        LayoutInflater lInflater;
        ArrayList<GalleryItem> objects;
        ViewHolder viewHolder;
    /*ImageAdapter1(Context context, ArrayList<GalleryItem> galleryItemList) {
        ctx = context;
        objects = galleryItemList;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }*/

        public ImageAdapter1(Context context, int resourceId,ArrayList<GalleryItem> galleryItemList) {
            super(context, resourceId, galleryItemList);
            // mSelectedItemsIds = new SparseBooleanArray();
            ctx = context;
            objects = galleryItemList;
            lInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public GalleryItem getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void remove(GalleryItem object) {
            System.out.println("removing image pos----------->"+object.position);
            // int pos=object.position;
//            objects.remove(object);
            String imagefilename = object.imgFileName;
            String audioFilename = imagefilename.replace(".png", ".wav");

            File sdCard = Environment.getExternalStorageDirectory();


            File imgDir = new File(sdCard.getAbsolutePath() + "/lokavidya" + "/" + projectName + "/images");
            File audDir = new File(sdCard.getAbsolutePath() + "/lokavidya" + "/" + projectName + "/audio");

            File image_file = new File(imgDir, imagefilename);
            File audio_file = new File(audDir, audioFilename);


            if (image_file.delete()) {

            }
            if (audio_file.delete()) {

            }
            objects.remove(object);
            notifyDataSetChanged();
            for(int i=0;i<objects.size();i++)
                System.out.println("object postion retain------------------>"+objects.get(i).position+"---------"+objects.get(i).imgFileName);
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = lInflater.inflate(R.layout.galleryitem, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.imageview=((ImageView) view.findViewById(R.id.thumbImage));
                viewHolder.checkbox=(CheckBox) view.findViewById(R.id.itemCheckBox);
                view.setTag(viewHolder);
            }else
            {
                viewHolder = (ViewHolder)view.getTag();
            }

            GalleryItem p = getProduct(position);

            File sdCard = Environment.getExternalStorageDirectory();



            File imgDir = new File (sdCard.getAbsolutePath() + "/lokavidya"+"/"+projectName+"/images");
            File image_file=  new File(imgDir, p.imgFileName);
            Bitmap myBitmap = BitmapFactory.decodeFile(image_file.getAbsolutePath());
            //      ((TextView) view.findViewById(R.id.subgrpname)).setText(p.name);
            viewHolder.imageview.setImageBitmap(myBitmap);

            viewHolder.imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageClickCallBack(position);

                }
            });
            //CheckBox cbBuy = (CheckBox) view.findViewById(R.id.itemCheckBox);
            viewHolder.checkbox.setOnCheckedChangeListener(myCheckChangList);
            viewHolder.checkbox.setTag(position);
            viewHolder.checkbox.setChecked(p.box);
            return view;
        }

        GalleryItem getProduct(int position) {
            return ((GalleryItem) getItem(position));
        }

        ArrayList<GalleryItem> getBox() {
            ArrayList<GalleryItem> box = new ArrayList<GalleryItem>();
            for (GalleryItem p : objects) {
                //if (p.box)
                // {
                System.out.println("details------>"+p.position+"  "+p.box);
                box.add(p);

                //  }

            }
            return box;
        }

        CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                getProduct((Integer) buttonView.getTag()).box = isChecked;
                // if(isChecked==true)
                getProduct((Integer) buttonView.getTag()).position=(Integer) buttonView.getTag();
            }
        };



    }



}
