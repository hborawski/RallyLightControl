package com.borawski.rallylightcontrol;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    SimpleCursorAdapter mAdapter;
    PresetContract.PresetDbHelper mDbHelper;
    int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice chosenDevice;
    OutputStream out;
    Cursor c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LinearLayout layout = new LinearLayout(getApplicationContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText name = new EditText(MainActivity.this);
                name.setHint("Preset Name");
                final EditText pitch = new EditText(MainActivity.this);
                pitch.setInputType(InputType.TYPE_CLASS_NUMBER);
                pitch.setHint("Pitch");
                final EditText yaw = new EditText(MainActivity.this);
                yaw.setInputType(InputType.TYPE_CLASS_NUMBER);
                yaw.setHint("Yaw");
                layout.addView(name);
                layout.addView(pitch);
                layout.addView(yaw);
                builder.setTitle("New Preset");
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addEntry(name.getText().toString(), Integer.parseInt(pitch.getText().toString()), Integer.parseInt(yaw.getText().toString()));
                        loadPresets();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setView(layout);
                builder.show();
            }
        });

        ListView list = (ListView) findViewById(R.id.listView);
        registerForContextMenu(list);
        mDbHelper = new PresetContract.PresetDbHelper(getApplicationContext());
        loadPresets();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    public void deleteEntry(int id) {
        c.moveToPosition(id);
        int sqlId = c.getInt(0);
        String selection = PresetContract.PresetEntry.COLUMN_NAME_ID + " = " + sqlId;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(PresetContract.PresetEntry.TABLE_NAME, selection, null);
        loadPresets();
    }
    public void addEntry(String name, int pitch, int yaw) {
        SQLiteDatabase db =mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PresetContract.PresetEntry.COLUMN_NAME_NAME, name);
        values.put(PresetContract.PresetEntry.COLUMN_NAME_PITCH, String.valueOf(pitch));
        values.put(PresetContract.PresetEntry.COLUMN_NAME_YAW, String.valueOf(yaw));

        long newRowId;
        newRowId = db.insert(PresetContract.PresetEntry.TABLE_NAME,
                null,
                values);
        loadPresets();
    };
    public void loadPresets() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = PresetContract.getProjection();
        String sortOrder = PresetContract.PresetEntry.COLUMN_NAME_ID + " DESC";

        c = db.query(
                PresetContract.PresetEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        mAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                c,
                new String[] { "name", "pitch" },
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        ListView list = (ListView) findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("TEST", String.valueOf(id));
                c.moveToPosition((int) position);
                int index = c.getColumnIndex(PresetContract.PresetEntry.COLUMN_NAME_PITCH);
                String pitch = c.getString(index);
                Log.i("PITCH", pitch);
                index = c.getColumnIndex(PresetContract.PresetEntry.COLUMN_NAME_YAW);
                String yaw = c.getString(index);
                Log.i("YAW", yaw);
                index = c.getColumnIndex(PresetContract.PresetEntry.COLUMN_NAME_NAME);
                String name = c.getString(index);
                Log.i("SEND", name);
                sendData(Integer.parseInt(pitch), Integer.parseInt(yaw));
            }
        });
        list.setAdapter(mAdapter);
    }

    public void showBluetoothMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Device");
        final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        CharSequence[] devices = new CharSequence[10];
        final ArrayList<BluetoothDevice> realdevices = new ArrayList<>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
        if (pairedDevices.size() > 0) {
            int count = 0;
            for (BluetoothDevice device : pairedDevices) {
                realdevices.add(device);
                arrayAdapter.add(device.getName());
            }
            builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    chosenDevice = realdevices.get(which);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    public void connectDevice() {
        final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        BluetoothSocket socket = null;
        try {
            socket = chosenDevice.createRfcommSocketToServiceRecord(SERIAL_UUID);
        } catch (IOException e) {

        }

        try {
            socket.connect();
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(int pitch, int yaw) {
        byte[] data = {'m', (byte) pitch, (byte) yaw};
        try {
            out.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_bluetooth) {
            showBluetoothMenu();
        } else if (id == R.id.action_connect) {
            connectDevice();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listView) {
            menu.add(Menu.NONE, 0, 0, "Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        if ( menuItemIndex == 0) {
            deleteEntry(info.position);
        }
        return true;
    }
}
