package com.pangff.myscrollview;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

  ListView listview;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    listview = (ListView) findViewById(R.id.listview);
    listview.setAdapter(new TestAdapter());
  }
  
  public class TestAdapter extends BaseAdapter{

    @Override
    public int getCount() {
      return 50;
    }

    @Override
    public Object getItem(int position) {
      return null;
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if(convertView==null){
        convertView = new TextView(MainActivity.this);
      }
      ((TextView)convertView).setText("第"+position+"个");
      return convertView;
    }
    
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

}
