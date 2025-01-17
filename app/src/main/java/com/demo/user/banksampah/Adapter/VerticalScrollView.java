package com.demo.user.banksampah.Adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class VerticalScrollView extends ScrollView{

    public VerticalScrollView(Context context){
        super(context);
    }

    public VerticalScrollView(Context context, AttributeSet attrs){
        super (context, attrs);
    }

    public VerticalScrollView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        final int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                Log.i("VerticalScrollView", "onInterceptTouchEvent: DOWN super false");
                super.onTouchEvent(ev);
                break;

                case MotionEvent.ACTION_MOVE:
                    return false;

                    case MotionEvent.ACTION_CANCEL:
                        Log.i("VerticalScrollview", "onInterceptTouchEvent: CANCEL super false" );
                        super.onTouchEvent(ev);
                        break;

                        case MotionEvent.ACTION_UP:
                            Log.i("VerticalScrollview", "onInterceptTouchEvent: UP super false" );
                            return false;

            default: Log.i("VerticalScrollview", "onInterceptTouchEvent: " + action ); break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        super.onTouchEvent(ev);
        Log.i("VerticalScrollview", "onTouchEvent. action: " + ev.getAction() );
        return true;
    }

}
