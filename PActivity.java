package service.androidproductservice;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PActivity extends AppCompatActivity {

    private String productName = null;

    private Product p = new Product();

    class Product {
        private String productName;
        private String qty;
        private String category;
        private String desc;
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
            System.out.println("connected");
            return conn;
        }
        catch(Exception e)
        {
            e.printStackTrace();

            return null;
        }
    }

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
                p.productName = rs.getString("productName");
                p.qty = rs.getString("stockQty");
                p.category = rs.getString("category");
                p.desc = rs.getString("description");
                p.price = rs.getString("price");
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
            getProduct(productName);
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
        productName = intent.getStringExtra("productName");

        SimpleTask simple = new SimpleTask();
        simple.execute();

        TextView prodName = (TextView) findViewById(R.id.productName);
        TextView qty = (TextView) findViewById(R.id.qty);
        TextView categ = (TextView) findViewById(R.id.categ);
        EditText desc = (EditText) findViewById(R.id.description);
        TextView price = (TextView) findViewById(R.id.price);

        while(true) {
            if(p.productName == null)
                continue;
            prodName.setText(p.productName);
            qty.setText(p.qty);
            categ.setText(p.category);
            desc.setText(p.desc);
            price.setText(p.price);
            break;
        }
    }
}