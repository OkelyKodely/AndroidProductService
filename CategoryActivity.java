package service.androidproductservice;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private String[] fruits = null;

    private ArrayList<String> categories = new ArrayList<String>();

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

    private void getCategories()
    {
        try
        {
            Connection con = getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "select category from categories order by category asc"
            );
            ResultSet rs = ps.executeQuery();
            categories.clear();
            while(rs.next())
            {
                categories.add(rs.getString("category"));
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
            getCategories();

            // Create a List from String Array elements
            final List<String> fruits_list = categories;

            // Create an ArrayAdapter from List
            arrayAdapter = new ArrayAdapter<String>
                    (CategoryActivity.this, android.R.layout.simple_list_item_1, fruits_list);

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
        setContentView(R.layout.activity_category);

        SimpleTask s = new SimpleTask();
        s.execute();

        Button catManage = (Button) findViewById(R.id.catManage);
        catManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CategoryActivity.this, CategoryManageActivity.class);
                CategoryActivity.this.startActivity(myIntent);
            }
        });

        ListView lv = (ListView) findViewById(R.id.list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(CategoryActivity.this, ProductActivity.class);
                myIntent.putExtra("category", ((TextView)view).getText().toString());
                CategoryActivity.this.startActivity(myIntent);
            }
        });
        while(true) {
            if(arrayAdapter == null)
                continue;
            lv.setAdapter(arrayAdapter);
            break;
        }
    }
}