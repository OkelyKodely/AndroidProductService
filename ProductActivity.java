package service.androidproductservice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    private String category = null;

    private String[] fruits = null;

    private ArrayList<Product> products = new ArrayList<Product>();

    private CustomAdapter adapter = null;

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.customlayout, null);

            ImageView iv = (ImageView) convertView.findViewById(R.id.imageView);
            TextView tv1 = (TextView) convertView.findViewById(R.id.product_name);
            TextView tv2 = (TextView) convertView.findViewById(R.id.product_price);

            Bitmap bmp = BitmapFactory.decodeByteArray(products.get(position).image, 0, products.get(position).image.length);

            iv.setImageBitmap(bmp);
            tv1.setText(products.get(position).productName);
            tv2.setText("  $" + products.get(position).price);

            return convertView;
        }
    }

    class Product {
        private byte[] image;
        private String productName;
        private String price;
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
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://" + hostName + "/" + dbName + "?user=" + userName + "&password=" + password + "&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
            conn = DriverManager.getConnection(url);
            return conn;
        }
        catch(Exception e)
        {
            return null;
        }
    }

    private void getProducts(String category)
    {
        try
        {
            Connection con = getConnection();

            PreparedStatement ps = null;
            if(category.equals("all"))
                ps = con.prepareStatement(
                        "select * from products2 order by productID asc"
                );
            else
                ps = con.prepareStatement(
                        "select * from products2 where category = '" + category + "' order by productID asc"
                );
            ResultSet rs = ps.executeQuery();
            products.clear();
            while(rs.next())
            {
                Product p = new Product();
                p.image = rs.getBytes("image");
                p.productName = rs.getString("productName");
                p.price = rs.getString("price");
                products.add(p);
            }
            rs.close();
            ps.close();

            con.close();

        }
        catch(Exception e)
        {
        }
    }

    class SimpleTask extends AsyncTask<Void, Void, String> {

        // CAST THE LINEARLAYOUT HOLDING THE MAIN PROGRESS (SPINNER)
        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            linlaHeaderProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            getProducts(category);
            adapter = new CustomAdapter();
            return "";
        }

        @Override
        protected void onPostExecute(String text) {
            super.onPostExecute(text);
            // SET THE ADAPTER TO THE LISTVIEW
            ListView lv = (ListView) findViewById(R.id.products);
            lv.setAdapter(adapter);

            // CHANGE THE LOADINGMORE STATUS TO PERMIT FETCHING MORE DATA
            //loadingMore = false;

            // HIDE THE SPINNER AFTER LOADING FEEDS
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");

        Button insert = (Button) findViewById(R.id.inProduct);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ProductActivity.this, PNewActivity.class);
                myIntent.putExtra("category", category);
                ProductActivity.this.startActivity(myIntent);
            }
        });

        ListView lv = (ListView) findViewById(R.id.products);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(ProductActivity.this, PActivity.class);
                myIntent.putExtra("productName", ((TextView)view.findViewById(R.id.product_name)).getText().toString());
                myIntent.putExtra("category", category);
                ProductActivity.this.startActivity(myIntent);
            }
        });

        SimpleTask simple = new SimpleTask();
        simple.execute();
    }
}