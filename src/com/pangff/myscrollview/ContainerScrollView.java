package com.pangff.myscrollview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Scroller;

@SuppressLint("NewApi")
public class ContainerScrollView extends ViewGroup implements OnGestureListener {

  TopScrollView  webview;
  ListView listViewBottom;

  private Scroller mScroller;
  int downX;// 按下的x坐标
  int downY;// 按下的x坐标


  private float mLastMotionY;
  private GestureDetector detector;
  int move = 0;
  int up_excess_move = 0;
  int down_excess_move = 0;

  @SuppressWarnings("deprecation")
  public ContainerScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mScroller = new Scroller(getContext());
    detector = new GestureDetector(this);
    setWillNotDraw(false);
    this.setVerticalScrollBarEnabled(false);
  }
  
  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    Log.e("ddddd", "oldh-h:"+(oldh-h));
    super.onSizeChanged(w, h, oldw, oldh);
    if(oldh!=0){
      this.postDelayed(new Runnable() {
        
        @Override
        public void run() {
          // TODO Auto-generated method stub
          //listViewBottom.setSelectionFromTop(0, 0);
          smoothScrollToComment();
          requestLayout();
          
        }
      }, 200);
    }
   
    
  }


  public void setSysView(TopScrollView  webview) {
    this.webview = webview;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    webview = (TopScrollView )getChildAt(0);
    listViewBottom = (ListView) getChildAt(1);
    this.measureChildren(widthMeasureSpec, heightMeasureSpec);
    webview.measure(widthMeasureSpec, 0);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  boolean disable = false;

  public void disableTouchEvent(boolean disable) {
    this.disable = disable;
  }


  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if(disable){
      return true;
    }
    int location_listview[] = {0, 0};
    int location_container[] = {0, 0};
    int location_listview_top[] = {0, 0};
    this.getLocationInWindow(location_container);
    listViewBottom.getChildAt(0).getLocationInWindow(location_listview_top);
    listViewBottom.getLocationInWindow(location_listview);

    int listview_top = location_listview[1];
    int container_top = location_container[1];
    int listtopitem_top = location_listview_top[1];

    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
      if (!mScroller.isFinished()) {
        mScroller.forceFinished(true);
      }
      move = mScroller.getFinalY();
      mLastMotionY = ev.getY();
    } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
      if (mLastMotionY - ev.getY() > 0 && Math.abs(mLastMotionY - ev.getY())>PhoneUtils.dipToPixels(this.getContext(), 4)) {// 上拉加载下方数据
        if (listview_top == container_top + this.getHeight()) {
            if (!webview.canMyScrollVertically(1)) {// 不能向下滑动了
              return true;
            }
        } else if (listview_top > container_top) {
          return true;
        }
      } else if (mLastMotionY - ev.getY() < 0 && Math.abs(mLastMotionY - ev.getY())>PhoneUtils.dipToPixels(this.getContext(), 4)) {// 下拉加载上方数据
        if (listview_top == container_top) {
          if (listViewBottom.getFirstVisiblePosition() == 0 && listtopitem_top == container_top) {
            return true;
          }
        } else if (listview_top < container_top + this.getHeight()) {
          return true;
        }
      }
    }
    return super.onInterceptTouchEvent(ev);
  }



  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if(disable){
      return true;
    }
    final float y = ev.getY();
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        break;
      case MotionEvent.ACTION_MOVE:
        if (ev.getPointerCount() == 1) {
          if(webview.canMyScrollVertically(1)){
            webview.scrollToMax();
          }
          int deltaY = 0;
          deltaY = (int) (mLastMotionY - y);
          mLastMotionY = y;
          Log.d("move", "" + move);
          if (deltaY < 0) {
            if (up_excess_move == 0) {
              if (move > 0) {
                int move_this = Math.max(-move, deltaY);
                move = move + move_this;
                scrollBy(0, move_this);
              }
            } else if (up_excess_move > 0) {
              if (up_excess_move >= (-deltaY)) {
                up_excess_move = up_excess_move + deltaY;
                scrollBy(0, deltaY);
              } else {
                up_excess_move = 0;
                scrollBy(0, -up_excess_move);
              }
            }
          } else if (deltaY > 0) {
            if (down_excess_move == 0) {
              if (getMaxScrollY() - move > 0) {
                int move_this = Math.min(getMaxScrollY() - move, deltaY);
                move = move + move_this;
                scrollBy(0, move_this);
              }
            } else if (down_excess_move > 0) {
              if (down_excess_move >= deltaY) {
                down_excess_move = down_excess_move - deltaY;
                scrollBy(0, deltaY);
              } else {
                down_excess_move = 0;
                scrollBy(0, down_excess_move);
              }
            }
          }
        }
        break;
      case MotionEvent.ACTION_UP:
        if (up_excess_move > 0) {
          scrollBy(0, -up_excess_move);
          invalidate();
          up_excess_move = 0;
        }
        if (down_excess_move > 0) {
          scrollBy(0, down_excess_move);
          invalidate();
          down_excess_move = 0;
        }
        break;
    }
    return this.detector.onTouchEvent(ev);
  }

  // boolean isScrolling = false;
  //
  // @Override
  // public void computeScroll() {
  // isScrolling = true;
  // if (!mScroller.isFinished()) {
  // if (mScroller.computeScrollOffset()) {
  // int oldX = this.getScrollX();
  // int oldY = this.getScrollY();
  // int x = mScroller.getCurrX();
  // int y = mScroller.getCurrY();
  // if (oldX != x || oldY != y) {
  // this.scrollTo(x, y);
  // }
  // invalidate();
  // }
  // } else {
  // isScrolling = false;
  // }
  // }

  @Override
  public void computeScroll() {
    if (mScroller.computeScrollOffset()) {
      scrollTo(0, mScroller.getCurrY());
      postInvalidate();
    }
  }

  public void smoothScrollTo(int dy) {
    int duration = 500;
    int oldScrollY = getScrollY();
    mScroller.startScroll(0, oldScrollY, 0, dy, duration);
    invalidate();
  }



  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
  }

  /**
   * 获取最大的滑动距离
   * 
   * @return
   */
  public int getMaxScrollY() {
//    if(listViewBottom.getVisibility()==View.GONE){
//      return height;
//    }else{
//      return 0;
//    }
    return height;
  }

  int height = 0;

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    height = webview.getMeasuredHeight();
    if (height > (b - t)) {
      height = b - t;
    }
    webview.layout(l, t, r, height);
    listViewBottom.layout(l, height, r, height+(b-t));
  }

  public void smoothScrollToComment() {
    webview.scrollTo(0, webview.getMeasuredHeight() - this.getHeight());
    listViewBottom.setSelectionFromTop(0, 0);
    this.smoothScrollTo(getMaxScrollY() - getScrollY());
    mScroller.setFinalY(getMaxScrollY());
  }


  public void reset(boolean hideList) {
    smoothScrollTo(-this.getScrollY());
    webview.scrollTo(0, 0);
    if (hideList) {
      listViewBottom.setVisibility(View.GONE);
    }
  }


  @Override
  public boolean onDown(MotionEvent e) {
    // TODO Auto-generated method stub
    return false;
  }


  @Override
  public void onShowPress(MotionEvent e) {
    // TODO Auto-generated method stub

  }


  @Override
  public boolean onSingleTapUp(MotionEvent e) {
    // TODO Auto-generated method stub
    return false;
  }


  @Override
  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    // TODO Auto-generated method stub
    return false;
  }


  @Override
  public void onLongPress(MotionEvent e) {
    // TODO Auto-generated method stub

  }


  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    // TODO Auto-generated method stub
    Log.d("onFling", "onFling");
    if (up_excess_move == 0 && down_excess_move == 0) {

      int slow = -(int) velocityY * 3 / 4;
      mScroller.fling(0, move, 0, slow, 0, 0, 0, getMaxScrollY());
      move = mScroller.getFinalY();
      computeScroll();
    }
    return false;
  }



}
