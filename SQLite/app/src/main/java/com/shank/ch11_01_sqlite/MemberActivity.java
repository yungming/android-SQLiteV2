package com.shank.ch11_01_sqlite;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.shank.ch11_01_sqlite.sqlite.SQLiteHandler;

import java.util.HashMap;

public class MemberActivity extends Activity{
    private SQLiteHandler db;

    private int recid;
    private String[] list = new String[3];
    private String img;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mem);
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        imageView=(ImageView)findViewById(R.id.imagehead);
        Intent it = getIntent();
        recid = it.getIntExtra("id", 0);

        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails(recid);
        list[0]="名子："+user.get("name");
        list[1]="電話："+user.get("phone");
        list[2]="信箱："+user.get("email");
        img=user.get("img");
        setTitle(user.get("name"));
        image();



        ListView listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        Intent it = new Intent(Intent.ACTION_DIAL);
                        it.setData(Uri.parse("tel:" + list[1]));
                        startActivity(it);
                        break;
                    case 2:
                        Intent it2 = new Intent(Intent.ACTION_VIEW);
                        it2.setData(Uri.parse("mailto:" + list[2]));
                        it2.putExtra(Intent.EXTRA_SUBJECT, "資料送出");
                        it2.putExtra(Intent.EXTRA_TEXT, list[0] + "您好!");
                        startActivity(it2);
                        break;
                }
            }
        });
    }

    public void image()
    {
        Bitmap bmplist;
        byte[] bytes = Base64.decode(img, Base64.DEFAULT);
        bmplist = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        //轉換Bitmap為Drawable
        Drawable drawable = new BitmapDrawable(bmplist);
        imageView.setImageDrawable(drawable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                about();
                return true;
            case R.id.action_edit:
                Intent it1 = new Intent(MemberActivity.this, EditActivity.class);
                it1.putExtra("id", recid);
                startActivityForResult(it1, 100);
                this.finish();
                return true;
            case R.id.action_delete:
                    dialog();
                return true;
            case android.R.id.home://返回
                Intent it = new Intent(MemberActivity.this,MainActivity.class);
                startActivity(it);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    protected void dialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_action_delete)
                .setTitle("刪除")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.delete(recid);
                        Intent it2 = new Intent(MemberActivity.this, MainActivity.class);
                        startActivity(it2);
                        finish();
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
