package com.shank.ch11_01_sqlite;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.shank.ch11_01_sqlite.sqlite.SQLiteHandler;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class EditActivity extends Activity {

	private SQLiteHandler db;
	
	Button btnUpdate, btnCan;
	EditText edtUDName, edtUDTel, edtUDEmail;
    Intent it1 ;
    private String img;
    private ImageView imageView;

	int recid; //記住目前正在更正的資料是第id的紀錄
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        imageView=(ImageView)findViewById(R.id.imagehead);
		
		Intent it = getIntent();
		recid = it.getIntExtra("id", 0);
        it1 = new Intent(EditActivity.this, MemberActivity.class);
        it1.putExtra("id", recid);

		db = new SQLiteHandler(getApplicationContext());
		
		btnUpdate = (Button)findViewById(R.id.btnUpdate);
		btnCan = (Button)findViewById(R.id.btnCan);
		
		edtUDName = (EditText)findViewById(R.id.edtUDName);
		edtUDTel = (EditText)findViewById(R.id.edtUDTel);
		edtUDEmail = (EditText)findViewById(R.id.edtUDEmail);


		HashMap<String, String> user = db.getUserDetails(recid);
		    edtUDName.setText(user.get("name"));
		    edtUDTel.setText(user.get("phone"));
			edtUDEmail.setText(user.get("email"));
            img=user.get("img");
            image();

		
		btnUpdate.setOnClickListener(btnUpdateOnClickListener);
		btnCan.setOnClickListener(btnCanOnClickListener);

		
	}
	
	OnClickListener btnUpdateOnClickListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			String tepName = edtUDName.getText().toString().trim();
			String tepTel = edtUDTel.getText().toString().trim();
			String tepEmail = edtUDEmail.getText().toString().trim();
			
			db.update(recid,tepName,tepTel,tepEmail,changeimage());

            startActivityForResult(it1, 100);
            finish();
		}
	};

    public void image()
    {
        Bitmap bmplist;
        byte[] bytes = Base64.decode(img, Base64.DEFAULT);
        bmplist = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        //轉換Bitmap為Drawable
        Drawable drawable = new BitmapDrawable(bmplist);
        imageView.setImageDrawable(drawable);
    }

    public String changeimage()
    {
        //imageView轉Bitmap
        imageView.setDrawingCacheEnabled(true);
        //建立圖片的緩存，圖片的緩存本身就是一個Bitmap
        imageView.buildDrawingCache();
        //取得緩存圖片的Bitmap檔
        Bitmap bmp=imageView.getDrawingCache();
        //轉換為圖片指定大小
        //獲得圖片的寬高
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        // 設置想要的大小
        int newWidth = 150;
        int newHeight = 150;
        // 計算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix參數
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的圖片
        Bitmap newbm = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix,true);
        // 先把 bitmap 轉成 byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        newbm.compress(Bitmap.CompressFormat.JPEG, 100, stream );
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        // 把byte變成base64
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

	
	OnClickListener btnCanOnClickListener = new OnClickListener()
	{
		public void onClick(View v)
		{
            startActivityForResult(it1, 100);
            finish();
		}
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.other, menu);
        return true;
    }

    public void editpciture(View v)
    {
        final CharSequence[] items = { "相簿", "拍照" };

        AlertDialog dlg = new AlertDialog.Builder(EditActivity.this).setTitle("選擇照片").setItems(items,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //這裡item是根據選擇的方式，   在items數據裡面定義了兩種方式，拍照的下標為1所以就調用拍照方法
                        if(which==1){
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
                            intent.putExtra("crop", "true");
                            intent.putExtra("aspectX", 1);
                            intent.putExtra("aspectY", 1);

                            try {
                                intent.putExtra("return-data", true);
                                startActivityForResult(intent, 1);
                            } catch (ActivityNotFoundException e) {
                            }
                        }else{
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.putExtra("crop", "true");
                            intent.putExtra("aspectX", 1);
                            intent.putExtra("aspectY", 1);
                            try {
                                intent.putExtra("return-data", true);
                                startActivityForResult(Intent.createChooser(intent,
                                        "選擇照片"), 0);
                            } catch (ActivityNotFoundException e) {
                            }
                        }

                    }
                }).create();
        dlg.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null) {
            if (requestCode == 0) {
                Bundle extras2 = data.getExtras();
                if (extras2 != null) {
                    Bitmap photo = extras2.getParcelable("data");
                    imageView.setImageBitmap(photo);
                }
            } else if (requestCode == 1) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    imageView.setImageBitmap(photo);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                about();
                return true;
            case android.R.id.home:
                Intent it = new Intent(EditActivity.this,MainActivity.class);
                startActivity(it);
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
