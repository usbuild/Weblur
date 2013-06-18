package com.lecoding.activity;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;
import com.lecoding.data.ContentProviderTraits;

/**
 * Created by usbuild on 13-6-18.
 */
public abstract class AbstractListViewActivity extends SherlockActivity {

    private ListView listView;
    private CursorAdapter adapter;
    Handler handler;

    ContentProviderTraits traits;

    public AbstractListViewActivity(ContentProviderTraits traits) {
        this.traits = traits;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.block);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("屏蔽设置");

        listView = (ListView) findViewById(R.id.listView);
        registerForContextMenu(listView);


        Cursor cursor = getContentResolver().query(traits.getContentUri(), null, null, null, null);
        adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.simple_list_item_1, cursor,
                new String[]{traits.getNAME()}, new int[]{R.id.text1});
        listView.setAdapter(adapter);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Cursor cursor = getContentResolver().query(traits.getContentUri(), null, null, null, null);
                adapter.changeCursor(cursor);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        getContentResolver().registerContentObserver(traits.getContentUri(), true, new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                handler.sendEmptyMessage(0);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.menu_add:
                showEditDialog(0, "");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.block, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.block_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        Cursor data = (Cursor) adapter.getItem(position);
        String keyword = data.getString(data.getColumnIndex(traits.getNAME()));
        int id = data.getInt(data.getColumnIndex(traits.get_ID()));
        switch (item.getItemId()) {
            case R.id.menu_edit:
                showEditDialog(id, keyword);
                break;
            case R.id.menu_remove:
                getContentResolver().delete(ContentUris.withAppendedId(traits.getContentUri(), id), null, null);
                break;
        }
        return true;
    }

    protected abstract boolean isTaken(String data);

    public void showEditDialog(final int id, final String str) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("设置关键字");

        final EditText input = new EditText(this);
        input.setText(str);
        alert.setView(input);

        alert.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String text = input.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(getApplicationContext(), "输入的内容不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                if (isTaken(text)) {
                    Toast.makeText(getApplicationContext(), "已包含该关键词", Toast.LENGTH_LONG).show();
                    return;
                }

                ContentValues values = new ContentValues();
                values.put(traits.getNAME(), text);
                if (TextUtils.isEmpty(str)) {
                    getContentResolver().insert(traits.getContentUri(), values);
                } else {
                    getContentResolver().update(ContentUris.withAppendedId(traits.getContentUri(), id), values, null, null);
                }
                adapter.notifyDataSetChanged();
            }
        });

        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }
}
