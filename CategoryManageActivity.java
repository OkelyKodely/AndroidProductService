package service.androidproductservice;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryManageActivity extends AppCompatActivity {

    private String category = "";

    private String writeTask = "update";

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
            return conn;
        }
        catch(Exception e)
        {
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
        }
    }

    private void insert()
    {
        if(((TextView)findViewById(R.id.editText)).getText().toString().equals("all"))
            return;
        try
        {
            Connection con = getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "insert into categories select '" + ((TextView)findViewById(R.id.editText)).getText().toString() + "'"
            );
            ps.execute();

            con.close();

        }
        catch(Exception e)
        {
        }
    }

    private void update()
    {
        if(((TextView)findViewById(R.id.editText)).getText().toString().equals("all"))
            return;
        try
        {
            Connection con = getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "update categories set category = '" + ((TextView)findViewById(R.id.editText)).getText().toString() + "' where category = '" + category + "'"
            );
            ps.execute();

            con.close();

        }
        catch(Exception e)
        {
        }
    }

    private void delete()
    {
        if(((TextView)findViewById(R.id.editText)).getText().toString().equals("all"))
            return;
        try
        {
            Connection con = getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "delete from categories where category = '" + ((TextView)findViewById(R.id.editText)).getText().toString() + "'"
            );
            ps.execute();

            con.close();

        }
        catch(Exception e)
        {
        }
    }

    class WriteTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            switch (writeTask) {
                case "insert" :
                    insert();
                    break;
                case "update" :
                    update();
                    break;
                case "delete" :
                    delete();
                    break;
                default:
                    break;
            }
            Intent myIntent = new Intent(CategoryManageActivity.this, CategoryActivity.class);
            CategoryManageActivity.this.startActivity(myIntent);
            return "";
        }

        @Override
        protected void onPostExecute(String text) {
            super.onPostExecute(text);
        }
    };

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
            getCategories();

            // Create a List from String Array elements
            final List<String> fruits_list = categories;

            // Create an ArrayAdapter from List
            arrayAdapter = new ArrayAdapter<String>
                    (CategoryManageActivity.this, android.R.layout.simple_list_item_1, fruits_list);

            return "";
        }

        @Override
        protected void onPostExecute(String text) {
            super.onPostExecute(text);
            // SET THE ADAPTER TO THE LISTVIEW
            ListView lv = (ListView) findViewById(R.id.catManageListView);
            lv.setAdapter(arrayAdapter);

            // CHANGE THE LOADINGMORE STATUS TO PERMIT FETCHING MORE DATA
            //loadingMore = false;

            // HIDE THE SPINNER AFTER LOADING FEEDS
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SimpleTask s = new SimpleTask();
        s.execute();

        Button insert = (Button) findViewById(R.id.insertButton);
        Button update = (Button) findViewById(R.id.updateButton);
        Button delete = (Button) findViewById(R.id.deleteButton);

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeTask = "insert";
                WriteTask wt = new WriteTask();
                wt.execute();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeTask = "update";
                WriteTask wt = new WriteTask();
                wt.execute();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeTask = "delete";
                WriteTask wt = new WriteTask();
                wt.execute();
            }
        });

        ListView lv = (ListView) findViewById(R.id.catManageListView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                category = ((TextView)view).getText().toString();
                EditText et = (EditText) findViewById(R.id.editText);
                et.setText(((TextView)view).getText().toString());
            }
        });
    }
}