package com.example.artbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<String> nameArray;
    ArrayList<Integer> idArray;
    ArrayAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=findViewById(R.id.listView);
        nameArray= new ArrayList();
        idArray=new ArrayList();

        aa = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,nameArray);
        lv.setAdapter(aa);


        getData();
    }

    public void getData() {
        try{

            //Arts adında sql database varsa aç, yoksa oluştur (ki halihazırda var)
            SQLiteDatabase db  = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            db.execSQL("CREATE TABLE IF NOT EXISTS eserler (id INTEGER PRIMARY KEY, eseradi VARCHAR, paintername VARCHAR, year VARCHAR, image BLOB)");

            Cursor c = db.rawQuery("SELECT * FROM eserler",null);
            int nameIndex = c.getColumnIndex("eseradi");
            int idIndex = c.getColumnIndex("id");

            while(c.moveToNext()){

                nameArray.add(c.getString(nameIndex));
                idArray.add(c.getInt(idIndex));
                System.out.println("ID: " + c.getInt(idIndex));
                System.out.println("Name: " + c.getString(nameIndex));
            }

            aa.notifyDataSetChanged();

            c.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.add_art,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //add art item'a tıklıyorsa diğer aktiviteye geç
        if(item.getItemId()==R.id.add_art_item){
            Intent i1 = new Intent(MainActivity.this,Main2Activity.class);
            startActivity(i1);

        }

        return super.onOptionsItemSelected(item);
    }
}
