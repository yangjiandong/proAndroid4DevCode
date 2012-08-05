package com.paad.todolist;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class ToDoList extends Activity {
	
  static final private int ADD_NEW_TODO = Menu.FIRST;
  static final private int REMOVE_TODO = Menu.FIRST + 1;
  
  private static final String TEXT_ENTRY_KEY = "TEXT_ENTRY_KEY";
  private static final String ADDING_ITEM_KEY = "ADDING_ITEM_KEY";
  private static final String SELECTED_INDEX_KEY = "SELECTED_INDEX_KEY";
  
  private boolean addingNew = false;
  private ArrayList<ToDoItem> todoItems;
  private ListView myListView;
  private EditText myEditText;
  private ToDoItemAdapter aa;

  ToDoDBAdapter toDoDBAdapter;
  Cursor toDoListCursor;
  
  /** Called when the activity is first created. */
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);

    myListView = (ListView)findViewById(R.id.myListView);
    myEditText = (EditText)findViewById(R.id.myEditText);

    todoItems = new ArrayList<ToDoItem>();
    int resID = R.layout.todolist_item;
    aa = new ToDoItemAdapter(this, resID, todoItems);
    myListView.setAdapter(aa);

    myEditText.setOnKeyListener(new OnKeyListener() {
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
          if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            ToDoItem newItem = new ToDoItem(myEditText.getText().toString());
            toDoDBAdapter.insertTask(newItem);
            updateArray();
            myEditText.setText("");
            aa.notifyDataSetChanged();
            cancelAdd();
            return true; 
          }
        return false;
      }
    });

    registerForContextMenu(myListView);
    restoreUIState();

    toDoDBAdapter = new ToDoDBAdapter(this);

    // Open or create the database
    toDoDBAdapter.open();

    populateTodoList();
  }
  
  private void populateTodoList() {
    // Get all the todo list items from the database.
    toDoListCursor = toDoDBAdapter. getAllToDoItemsCursor();
    startManagingCursor(toDoListCursor);
      
    // Update the array.
    updateArray();
  }

  private void updateArray() {
	  toDoListCursor.requery();

	  todoItems.clear();
	    
	  if (toDoListCursor.moveToFirst())
	    do { 
	      String task = toDoListCursor.getString(toDoListCursor.getColumnIndex(ToDoDBAdapter.KEY_TASK));
	      long created = toDoListCursor.getLong(toDoListCursor.getColumnIndex(ToDoDBAdapter.KEY_CREATION_DATE));

	      ToDoItem newItem = new ToDoItem(task, new Date(created));
	      todoItems.add(0, newItem);
	    } while(toDoListCursor.moveToNext());
	  
	  aa.notifyDataSetChanged();
	}

  private void restoreUIState() {
    // Get the activity preferences object.
    SharedPreferences settings = getPreferences(0);

    // Read the UI state values, specifying default values.
    String text = settings.getString(TEXT_ENTRY_KEY, "");
    Boolean adding = settings.getBoolean(ADDING_ITEM_KEY, false);

    // Restore the UI to the previous state.
    if (adding) {
      addNewItem();
      myEditText.setText(text);
    }
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putInt(SELECTED_INDEX_KEY, myListView.getSelectedItemPosition());

    super.onSaveInstanceState(outState);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    int pos = -1;

    if (savedInstanceState != null)
      if (savedInstanceState.containsKey(SELECTED_INDEX_KEY))
        pos = savedInstanceState.getInt(SELECTED_INDEX_KEY, -1);

    myListView.setSelection(pos);
  }
  
  @Override
  protected void onPause() {
    super.onPause();
    
    // Get the activity preferences object.
    SharedPreferences uiState = getPreferences(0);
    // Get the preferences editor.
    SharedPreferences.Editor editor = uiState.edit();

    // Add the UI state preference values.
    editor.putString(TEXT_ENTRY_KEY, myEditText.getText().toString());
    editor.putBoolean(ADDING_ITEM_KEY, addingNew);
    // Commit the preferences.
    editor.commit();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    // Create and add new menu items.
    MenuItem itemAdd = menu.add(0, ADD_NEW_TODO, Menu.NONE,
                                R.string.add_new);
    MenuItem itemRem = menu.add(0, REMOVE_TODO, Menu.NONE,
                                R.string.remove);

    // Assign icons
    itemAdd.setIcon(R.drawable.add_new_item);
    itemRem.setIcon(R.drawable.remove_item);

    // Allocate shortcuts to each of them.
    itemAdd.setShortcut('0', 'a');
    itemRem.setShortcut('1', 'r');

    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    int idx = myListView.getSelectedItemPosition();

    String removeTitle = getString(addingNew ? 
                                   R.string.cancel : R.string.remove);

    MenuItem removeItem = menu.findItem(REMOVE_TODO);
    removeItem.setTitle(removeTitle);
    removeItem.setVisible(addingNew || idx > -1);

    return true;
  }
  
  @Override
  public void onCreateContextMenu(ContextMenu menu, 
                                  View v, 
                                  ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);

    menu.setHeaderTitle("Selected To Do Item");
    menu.add(0, REMOVE_TODO, Menu.NONE, R.string.remove);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    int index = myListView.getSelectedItemPosition();

    switch (item.getItemId()) {
      case (REMOVE_TODO): {
        if (addingNew) {
          cancelAdd();
        } 
        else {
          removeItem(index);
        }
        return true;
      }
      case (ADD_NEW_TODO): { 
        addNewItem();
        return true; 
      }
    }

    return false;
  }
  
  @Override
  public boolean onContextItemSelected(MenuItem item) {  
    super.onContextItemSelected(item);
    switch (item.getItemId()) {
      case (REMOVE_TODO): {
        AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int index = menuInfo.position;

        removeItem(index);
        return true;
      }
    }
    return false;
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
      
    // Close the database
    toDoDBAdapter.close();
  }
  
  private void cancelAdd() {
    addingNew = false;
    myEditText.setVisibility(View.GONE);
  }

  private void addNewItem() {
    addingNew = true;
    myEditText.setVisibility(View.VISIBLE);
    myEditText.requestFocus(); 
  }

  private void removeItem(int _index) {
    // Items are added to the listview in reverse order, so invert the index.
    toDoDBAdapter.removeTask(todoItems.size()-_index);
    updateArray();
  }
}