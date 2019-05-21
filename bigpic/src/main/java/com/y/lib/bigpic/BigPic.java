package com.y.lib.bigpic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.io.InputStream;

/**
 * 坑1：图片源格式要求 未确定
 * 找到一个链接：https://blog.csdn.net/hahajluzxb/article/details/8158852
 */
public class BigPic extends View {
    public BigPic(Context context) {
        this(context,null,-1);
    }

    public BigPic(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public BigPic(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private Context mContext;
    private Scroller mScroller;
    private GestureDetector mGestureDetector;
    private BitmapRegionDecoder mBitmapRegionDecoder;
    private BitmapFactory.Options options;
    private int mPicWidth,mPicHeight,mViewWidth,mViewHeight;
    private Bitmap mBitmap;

    //保存view宽高
    private Rect mRect = new Rect();
    private Matrix mMatrix = new Matrix();
    private float mScale = 4;

    private void init() {
        mScroller = new Scroller(mContext);
        mGestureDetector = new GestureDetector(mContext,new BigGestureListener());
        mMatrix.setScale(mScale,mScale);
    }

    public void setPic(InputStream is){
        try {
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is,null,options);
            mPicWidth = options.outWidth;
            mPicHeight = options.outHeight;
            options.inMutable = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inJustDecodeBounds = false;
            mBitmapRegionDecoder = BitmapRegionDecoder.newInstance(is,false);
            mBitmap = mBitmapRegionDecoder.decodeRegion(mRect,options);
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRect.left = mRect.top = 0;
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        mRect.right = (int) (mViewWidth / mScale);
        mRect.bottom = (int) (mViewHeight / mScale);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mBitmapRegionDecoder == null) return;
        options.inBitmap = mBitmap;
        mBitmap = mBitmapRegionDecoder.decodeRegion(mRect,options);
        canvas.drawBitmap(mBitmap,mMatrix,null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.isFinished()) return;
        if(mScroller.computeScrollOffset()){
            int dx = (int) ((mScroller.getCurrX() - mRect.left) / mScale);
            int dy = (int) ((mScroller.getCurrY() - mRect.top) / mScale);
            mRect.offset(dx,dy);
            invalidate();
        }
    }

    private class BigGestureListener implements GestureDetector.OnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
            if(!mScroller.isFinished()){
                mScroller.forceFinished(true);
            }
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        /**
         * @param e1   接下
         * @param e2   移动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mRect.offset((int)(distanceX / mScale),(int)(distanceY / mScale));
            if(mRect.left < 0){
                mRect.offset(-mRect.left,0);
            }
            if(mRect.top < 0){
                mRect.offset(0,-mRect.top);
            }
            if(mRect.right > mPicWidth){
                mRect.offset(mPicWidth - mRect.right,0);
            }
            if(mRect.bottom > mPicHeight){
                mRect.offset(0,mPicHeight - mRect.bottom);
            }
            invalidate();
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.fling(mRect.left,mRect.top,(int)-velocityX,(int)-velocityY,
                    0,mPicWidth - (int)(mViewWidth / mScale),
                    0,mPicHeight - (int)(mViewHeight / mScale));
            return false;
        }
    }

}
