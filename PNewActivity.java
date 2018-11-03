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
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PNewActivity extends AppCompatActivity {

    private String category = "";

    private File imageFile = null;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    class Product {
        private String productID;
        private String productName;
        private String price;
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

    private void insert(Product product)
    {
        try
        {
            Connection con = getConnection();
            String sqlStr = "INSERT INTO products2 (productID, productName, price, image, description, stockQty, inputdate, category) VALUES ('"+product.productID+"', '"+product.productName+"', "+product.price+", ?, '"+product.description+"', "+product.stockQty+", current_timestamp, '"+product.category+"')";
            PreparedStatement ps = con.prepareStatement(sqlStr);
            byte[] b = readFileToByteArray(imageFile);
            ps.setBytes(1, b);
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
            Product p = new Product();
            p.productID = ((EditText) findViewById(R.id.eId)).getText().toString();;
            p.category = category;
            p.description = ((EditText) findViewById(R.id.dsc)).getText().toString();
            p.price = ((TextView) findViewById(R.id.prce2)).getText().toString();
            p.productName = ((TextView) findViewById(R.id.e_Name)).getText().toString();
            p.stockQty = ((TextView) findViewById(R.id.qtty)).getText().toString();
            insert(p);
            Intent myIntent = new Intent(PNewActivity.this, ProductActivity.class);
            myIntent.putExtra("category", category);
            PNewActivity.this.startActivity(myIntent);
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
        setContentView(R.layout.activity_pnew);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");

        TextView tv = (TextView) findViewById(R.id.ctte);
        tv.setText(category);

        Button insert = (Button) findViewById(R.id.insrtButton);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteTask wt = new WriteTask();
                wt.execute();
            }
        });

        ImageView iv = (ImageView) findViewById(R.id.imgView);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if we have write permission
                int permission = ActivityCompat.checkSelfPermission(PNewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            PNewActivity.this,
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                }

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    ((ImageView) findViewById(R.id.imgView)).setImageURI(selectedImage);
                    String imagepath = getPath(selectedImage);
                    imageFile = new File(imagepath);
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    ((ImageView) findViewById(R.id.imgView)).setImageURI(selectedImage);
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