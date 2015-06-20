package com.shank.ch11_01_sqlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.shank.ch11_01_sqlite.sqlite.SQLiteHandler;


public class MainActivity extends Activity {
    private SQLiteHandler db;
    private String [] from = {"img","name"};
    private int [] to = {R.id.imghead,R.id.name};
    private SimpleCursorAdapter adp;
    private Cursor cur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("通訊錄");
        db = new SQLiteHandler(getApplicationContext());
        cur=db.getall();
        ListView lv = (ListView) findViewById(R.id.lv2);
        //建立Adapter物件
        adp = new SimpleCursorAdapter(MainActivity.this, R.layout.item, cur, from, to, 0);

        adp.setViewBinder(new SimpleCursorAdapter.ViewBinder(){
            /** Binds the Cursor column defined by the specified index to the specified view */
            public boolean setViewValue(View view, Cursor cursor, int columnIndex){
                if(view.getId() == R.id.imghead){
                    Bitmap bmplist;
                    String byt=cur.getString(cur.getColumnIndex("img"));
                    byte[] bytes = Base64.decode(byt, Base64.DEFAULT);
                    bmplist = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    //轉換Bitmap為Drawable
                    Drawable drawable = new BitmapDrawable(bmplist);
                    //存入Drawable陣列中
                    ((ImageView)view).setImageDrawable(drawable);
                    return true; //true because the data was bound to the view
                }
                return false;
            }
        });
        lv.setAdapter(adp);
        lv.setOnItemClickListener(lvOnItemClickListener);
        requery();

    }



    private void requery()
    {
        cur = db.getall();
        adp.changeCursor(cur);
    }

    AdapterView.OnItemClickListener lvOnItemClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> parent, View v, int pos, long id)
        {
            cur.moveToPosition(pos);  //移動Cursor至使用者選取的項目
            Intent it1 = new Intent(MainActivity.this, MemberActivity.class);
            it1.putExtra("id", cur.getInt(cur.getColumnIndex("_id")));
            startActivityForResult(it1, 100);
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent it)
    {
        if(resultCode == RESULT_OK)
            requery();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //this.menu = menu;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    cur=db.getsome(query);
                    adp.changeCursor(cur);
                    return true;

                }

            });

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_add:
                Intent it = new Intent(MainActivity.this, AddActivity.class);
                startActivity(it);
                this.finish();
                return true;
            case R.id.action_settings:
                about();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void about() {
         new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_action_about)
                .setTitle("About")
                .setMessage("作者：李元銘")
                .setPositiveButton("github", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("https://github.com/yungming/android.git");
                        Intent i = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(i);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
