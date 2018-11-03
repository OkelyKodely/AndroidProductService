package service.androidproductservice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PActivity extends AppCompatActivity {

    private void getProduct(String productName)
    {
        try
        {
            Connection con = getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "select * from products2 where productName = '" + productName + "'"
            );
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                p = new Product();
                p.productID = rs.getString("productID");
                p.image = rs.getBytes("image");
                p.productName = rs.getString("productName");
                p.stockQty = rs.getString("stockQty");
                p.category = rs.getString("category");
                p.description = rs.getString("description");
                p.price = rs.getString("price");
                System.out.println("P R I C E  " + p.price);
            }
            rs.close();
            ps.close();

            con.close();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private String pName = "";

    private String category = "";

    private File imageFile = null;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Product p = null;

    class Product {
        private String productID;
        private String productName;
        private String price;
        private byte[] image;
        private String description;
        private String stockQty;
        private String category;
    }

    public Connection getConnection()
    {
        Connection conn = null;

        String hostName = "ec2-54-163-240-54.compute-1.amazonaws.com";
        String dbName = "d89l9begjikklj";
        String userName = "isscllglmxgeln";
        String password = "334f696049572d4bc9c3b6b78c3410301e24dd3b5fd2b96dc15bf4c1c6fed113";

        try
        {
            DriverManager.registerDriver(new org.postgresql.Driver());
            //Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://" + hostName + "/" + dbName + "?user=" + userName + "&password=" + password + "&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
            conn = DriverManager.getConnection(url);
            System.out.println("connected");
            return conn;
        }
        catch(Exception e)
        {
            e.printStackTrace();

            return null;
        }
    }

    private byte[] readFileToByteArray(File file){
        FileInputStream fis = null;
        // Creating a byte array using the length of the file
        // file.length returns long which is cast to int
        byte[] bArray = new byte[(int) file.length()];
        try{
            fis = new FileInputStream(file);
            fis.read(bArray);
            fis.close();

        }catch(Exception ioExp){
            ioExp.printStackTrace();
        }
        return bArray;
    }

    private void update(PActivity.Product product)
    {
        try
        {
            Connection con = getConnection();
            String sqlStr = "";
            if(imageFile != null)
                sqlStr = "UPDATE products2 set productID = '"+product.productID+"', productName = '"+product.productName+"', price = "+product.price+", image = ?, description = '"+product.description+"', stockQty = "+product.stockQty+", category = '"+product.category+"' where productName = '" + pName + "'";
            else
                sqlStr = "UPDATE products2 set productID = '"+product.productID+"', productName = '"+product.productName+"', price = "+product.price+", description = '"+product.description+"', stockQty = "+product.stockQty+", category = '"+product.category+"' where productName = '" + pName + "'";
            PreparedStatement ps = con.prepareStatement(sqlStr);
            if(imageFile != null) {
                byte[] b = readFileToByteArray(imageFile);
                ps.setBytes(1, b);
            }
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void del(PActivity.Product product)
    {
        try
        {
            Connection con = getConnection();
            String sqlStr = "DELETE from products2 where productName = '" + pName + "'";
            PreparedStatement ps = con.prepareStatement(sqlStr);
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    class WriteTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            PActivity.Product p = new PActivity.Product();
            p.productID = ((EditText) findViewById(R.id.e_Id)).getText().toString();
            ;
            p.category = category;
            p.description = ((EditText) findViewById(R.id.ds_c)).getText().toString();
            p.price = ((TextView) findViewById(R.id.prce_2)).getText().toString();
            p.productName = ((TextView) findViewById(R.id.e_Name)).getText().toString();
            p.stockQty = ((TextView) findViewById(R.id.qtt_y)).getText().toString();
            update(p);
            Intent myIntent = new Intent(PActivity.this, ProductActivity.class);
            myIntent.putExtra("category", category);
            PActivity.this.startActivity(myIntent);
            return "";
        }

        @Override
        protected void onPostExecute(String text) {
            super.onPostExecute(text);
        }

    };

    class DelTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            PActivity.Product p = new PActivity.Product();
            p.productID = ((EditText) findViewById(R.id.e_Id)).getText().toString();
            ;
            p.category = category;
            p.description = ((EditText) findViewById(R.id.ds_c)).getText().toString();
            p.price = ((TextView) findViewById(R.id.prce_2)).getText().toString();
            p.productName = ((TextView) findViewById(R.id.e_Name)).getText().toString();
            p.stockQty = ((TextView) findViewById(R.id.qtt_y)).getText().toString();
            del(p);
            Intent myIntent = new Intent(PActivity.this, ProductActivity.class);
            myIntent.putExtra("category", category);
            PActivity.this.startActivity(myIntent);
            return "";
        }

        @Override
        protected void onPostExecute(String text) {
            super.onPostExecute(text);
        }

    };

    public Bitmap getImageDataInBitmap() {
        if (p.image != null) {
            //turn byte[] to bitmap
            Bitmap b = BitmapFactory.decodeByteArray(p.image, 0, p.image.length);
            return b;
        }
        return null;
    }

    class SimpleTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            getProduct(pName);
            return "";
        }

        @Override
        protected void onPostExecute(String text) {
            super.onPostExecute(text);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        pName = intent.getStringExtra("productName");
        category = intent.getStringExtra("category");

        TextView tv = (TextView) findViewById(R.id.ctt_e);
        tv.setText(category);

        Button update = (Button) findViewById(R.id.updaButton);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PActivity.WriteTask wt = new PActivity.WriteTask();
                wt.execute();
            }
        });

        Button del = (Button) findViewById(R.id.delBtn);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PActivity.DelTask wt = new PActivity.DelTask();
                wt.execute();
            }
        });

        ImageView iv = (ImageView) findViewById(R.id.img_View);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if we have write permission
                int permission = ActivityCompat.checkSelfPermission(PActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            PActivity.this,
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                }

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }
        });

        SimpleTask s = new SimpleTask();
        s.execute();

        while (true) {
            if (p == null)
                continue;
            else {
                if(p.description == null || p.productID == null || p.productName == null || p.category == null || p.stockQty == null || p.price == null)
                    continue;
            }
            EditText p_name = (EditText) findViewById(R.id.e_Name);
            EditText e_id = (EditText) findViewById(R.id.e_Id);
            EditText prce2 = (EditText) findViewById(R.id.prce_2);
            EditText qtty = (EditText) findViewById(R.id.qtt_y);
            EditText dsc = (EditText) findViewById(R.id.ds_c);
            ImageView ivv = (ImageView) findViewById(R.id.img_View);
            p_name.setText(p.productName);
            System.out.println("pppname :::: " + p.productName);
            System.out.println("pppeid :::: " + p.productID);
            System.out.println("pppprice :::: " + p.price);
            System.out.println("pppqty :::: " + p.stockQty);
            System.out.println("pppdsc :::: " + p.description);
            System.out.println("pppimv :::: " + p.image);
            e_id.setText(p.productID);
            prce2.setText(p.price);
            qtty.setText(p.stockQty);
            dsc.setText(p.description);
            Bitmap b = getImageDataInBitmap();
            ivv.setImageBitmap(b);
            break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    ((ImageView) findViewById(R.id.img_View)).setImageURI(selectedImage);
                    String imagepath = getPath(selectedImage);
                    imageFile = new File(imagepath);
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    ((ImageView) findViewById(R.id.img_View)).setImageURI(selectedImage);
                    String imagepath = getPath(selectedImage);
                    imageFile = new File(imagepath);
                }
                break;
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
}