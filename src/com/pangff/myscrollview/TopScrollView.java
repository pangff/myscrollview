package com.pangff.myscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class TopScrollView extends ScrollView{

  public TopScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }
  
  public boolean canMyScrollVertically(int direction) {
    final int offset = computeVerticalScrollOffset();
    final int range = computeVerticalScrollRange() - computeVerticalScrollExtent();
    if (range == 0) return false;
    if (direction < 0) {
      return offset > 0;
    } else {
      return offset < range - 1;
    }
  }
  
  public void scrollToMax(){
    final int range = computeVerticalScrollRange() - computeVerticalScrollExtent();
    scrollTo(0,range);
  }

}
