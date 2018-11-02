package service.androidproductservice;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private String category = null;

    private String[] fruits = null;

    private ArrayList<String> products = new ArrayList<String>();

    private ArrayAdapter<String> arrayAdapter = null;

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
            System.out.println("connected");
            return conn;
        }
        catch(Exception e)
        {
            e.printStackTrace();

            return null;
        }
    }

    private void getProducts(String category)
    {
        try
        {
            Connection con = getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "select * from products2 where category = '" + category + "' order by productID asc"
            );
            ResultSet rs = ps.executeQuery();
            products.clear();
            while(rs.next())
            {
                products.add(rs.getString("productName"));
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

    class SimpleTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            getProducts(category);

            // Create a List from String Array elements
            final List<String> fruits_list = products;

            // Create an ArrayAdapter from List
            arrayAdapter = new ArrayAdapter<String>
                    (ProductActivity.this, android.R.layout.simple_list_item_1, fruits_list);

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
        setContentView(R.layout.activity_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");

        ListView lv = (ListView) findViewById(R.id.products);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(((TextView)view).getText().toString());
                Intent myIntent = new Intent(ProductActivity.this, PActivity.class);
                myIntent.putExtra("productName", ((TextView)view).getText().toString());
                ProductActivity.this.startActivity(myIntent);
            }
        });
        SimpleTask simple = new SimpleTask();
        simple.execute();
        while(true) {
            if(arrayAdapter == null)
                continue;
            lv.setAdapter(arrayAdapter);
            break;
        }
    }
}