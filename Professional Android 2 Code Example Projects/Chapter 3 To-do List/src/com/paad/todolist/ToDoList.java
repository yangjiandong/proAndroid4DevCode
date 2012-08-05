package com.paad.todolist;

import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ToDoList extends Activity {

  /** Called when the activity is first created. */
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Inflate your view
    setContentView(R.layout.main);
      
    // Get references to UI widgets
    ListView myListView = (ListView)findViewById(R.id.myListView);
    final EditText myEditText = (EditText)findViewById(R.id.myEditText);
    
    // Create the array list of to do items
    final ArrayList<String> todoItems = new ArrayList<String>();
    // Create the array adapter to bind the array to the listview
    final ArrayAdapter<String> aa;
    aa = new ArrayAdapter<String>(this,
                                  android.R.layout.simple_list_item_1,
                                  todoItems);
    // Bind the array adapter to the listview.
    myListView.setAdapter(aa);
    
    myEditText.setOnKeyListener(new OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
          if (event.getAction() == KeyEvent.ACTION_DOWN)
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
            {
              todoItems.add(0, myEditText.getText().toString());
              aa.notifyDataSetChanged();
              myEditText.setText("");
              return true;
            }
          return false;
        }
      });
  }
}