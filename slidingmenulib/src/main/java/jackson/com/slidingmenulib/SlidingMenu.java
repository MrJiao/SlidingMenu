package jackson.com.slidingmenulib;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by Jackson on 2017/3/20.
 * Version : 1
 * Details :
 */
public class SlidingMenu extends ViewGroup {

    private MyViewDragHelper dragHelper;


    public SlidingMenu(Context context) {
        super(context);
        init(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        dragHelper = MyViewDragHelper.create(this,11f, new CallBack());
        dragHelper.setDuration(330);
    }

    private View menu;
    private View content;
    private int contentEndLeft;
    private int menuStartLeft;
    private int menuWidth;
    private float contentVel;//contentView的横坐标变化率
    private float menuVel;//menuView的横坐标变化率

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        initLayoutMenu(changed);
        initLayoutContent(changed);
    }

    private void initLayoutContent(boolean changed) {
        L.e("initLayoutContent changed", changed);
        if (changed && content != null) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            L.e("initLayoutContent", "changed", changed, "measuredWidth", measuredWidth, "measuredHeight", measuredHeight);
            content.layout(0, 0, measuredWidth, measuredHeight);
            if(onViewChangedListener!=null)
            onViewChangedListener.onContentChanged(content,0);
        }
    }

    private void initLayoutMenu(boolean changed) {
        L.e("initLayoutMenu changed", changed);
        if (changed && menu != null) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            L.e("initLayoutMenu", "measuredWidth", measuredWidth, "measuredHeight", measuredHeight, "menuStartLeft", menuStartLeft);
            menu.layout(menuStartLeft, 0, menuWidth + menuStartLeft, measuredHeight);
            if(onViewChangedListener!=null)
            onViewChangedListener.onMenuChanged(menu,0);
        }
    }

    int mWidth;
    int mHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        L.e("onMeasure");

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        }

        for (int i = 0; i < getChildCount(); i++) {
            int widthSpec ;
            int heightSpec ;
            View v = getChildAt(i);
            if (mWidth == 0) {
                widthSpec = MeasureSpec.makeMeasureSpec(v == content ? widthSize : menuWidth, MeasureSpec.AT_MOST);
            } else {
                widthSpec = MeasureSpec.makeMeasureSpec(v == content ? mWidth : menuWidth, MeasureSpec.EXACTLY);
            }

            if (mHeight == 0) {
                heightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);
            } else {
                heightSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
            }

            v.measure(widthSpec, heightSpec);
        }
        L.e("mWidth",mWidth,"mHeight",mHeight);
        setMeasuredDimension(mWidth, mHeight);
    }
    
    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            invalidate();
        }else {
            if(contentEndLeft!=0){
                if(content.getLeft()<4){
                    onStateChanged(STATE_START);
                }else {
                    onStateChanged(STATE_END);
                }
            }else {
                if(menu.getLeft()<menuStartLeft+4){
                    onStateChanged(STATE_START);
                }else {
                    onStateChanged(STATE_END);
                }
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
    }


    private float lastX;
    private float currentX;
    private float moveDx;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (MotionEventCompat.getActionMasked(event)){
            case MotionEvent.ACTION_DOWN:
                lastX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = event.getRawX();
                moveDx = currentX - lastX;
                lastX = currentX;
                L.e("onTouchEvent","moveDx",moveDx);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        dragHelper.processTouchEvent(event);
        return true;
    }


    private class CallBack extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            onStateChanged(STATE_CAPTURE);
            if(child == content){
                L.e("content tryCaptureView", "pointerId", pointerId);
            }else {

                L.e("menu tryCaptureView", "pointerId", pointerId);
            }
            return true;
        }

        /**
         * @param child 触摸到的view
         * @param left  下次要移动到的位置
         * @param dx    这次移动的大小
         * @return 下次移动到的位置
         * left-dx = 当前位置。 如果return left-dx 相当于不滑动
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == content) {
                L.e("content clampViewPositionHorizontal", "left", left, "dx", dx);
                left = getContentNewLeft(left-dx);

                if (left <= contentEndLeft) {
                    return left > 0 ? left : 0;
                } else {
                    return contentEndLeft;
                }
            } else if(child == menu){
                left = getMenuNewLeft(left - dx);
                if(left >=0){
                    return 0;
                }
                L.e("menu clampViewPositionHorizontal","menu.getLeft()",menu.getLeft(), "left", left, "dx", dx);
                return left < 0 ? left : 0;
            }
            return 0;
        }

        private int getContentNewLeft(int oldLeft){
            final int left = (int) (oldLeft + contentVel * moveDx);
            if(left>contentEndLeft)
                return contentEndLeft;
            L.e("getContentNewLeft","oldLeft",oldLeft,"contentVel",contentVel,"moveDx",moveDx,"newLeft",left);
            return left;
        }

        private int getMenuNewLeft(int oldLeft){
            final int left = (int) (oldLeft+menuVel*moveDx);
            if(left>0){
                return 0;
            }
            L.e("getMenuNewLeft","oldLeft",oldLeft,"menuVel",menuVel,"moveDx",moveDx,"menuVel*moveDx",menuVel*moveDx,"newLeft",left);
            return left;
        }

        private float changePercent;

        /**
         * 被拖拽时调用
         *
         * @param changedView 触摸的view
         * @param left        新x
         * @param top         新y
         * @param dx          变化的x
         * @param dy          变化的y
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

            if (changedView == content) {
                L.e("content onViewPositionChanged", "left", left, "top", top, "dx", dx, "dy", dy);
                if(contentEndLeft==0){
                    int menuNewLeft = getMenuNewLeft(menu.getLeft());
                    changePercent = (float) (menuStartLeft-menuNewLeft) / (float) menuStartLeft;
                }else {
                    changePercent = (float) left / (float) contentEndLeft;
                }
                moveMenu(changePercent);
                onMenuChanged(menu, changePercent);
                onContentChanged(content, changePercent);
            }
            if (changedView == menu) {
                L.e("menu onViewPositionChanged", "left", left, "top", top, "dx", dx, "dy", dy);

                if(menuStartLeft==0){
                    int contentNewLeft = getContentNewLeft(content.getLeft());
                    changePercent = (float) contentNewLeft / (float) contentEndLeft;
                }else {
                    changePercent = (float) (menuStartLeft-left) / (float) menuStartLeft;
                }
                moveContent(changePercent);
                onMenuChanged(menu, changePercent);
                onContentChanged(content, changePercent);
            }
        }

        private void moveMenu(float changePercent){
            if(changePercent==0 || changePercent>1 || changePercent<-1)return;
            final int left = menu.getLeft();
            final float targetLeft = -menuStartLeft*changePercent+menuStartLeft ;
            L.e("moveMenu","changePercent",changePercent, "menu.getLeft()", left,"targetLeft",targetLeft);
            if(targetLeft>0){
                menu.offsetLeftAndRight(-left);
                return;
            }
            if(targetLeft<menuStartLeft){
                menu.offsetLeftAndRight(menuStartLeft-left);
                return;
            }
            menu.offsetLeftAndRight((int) (targetLeft-left));
        }

        private void moveContent(float changePercent){
            if(changePercent==0 || changePercent>1 || changePercent<-1)return;
            final int left = content.getLeft();
            final float targetLeft = contentEndLeft*changePercent ;
            L.e("moveContent","changePercent",changePercent, "content.getLeft()", left,"targetLeft",targetLeft);
            if(targetLeft>contentEndLeft){
                content.offsetLeftAndRight(contentEndLeft-left);
                return;
            }
            if(targetLeft<0){
                content.offsetLeftAndRight(-left);
                return;
            }
            content.offsetLeftAndRight((int) (targetLeft-left));
        }

        private void onContentChanged(View content, float percent) {
            if(onViewChangedListener !=null){
                onViewChangedListener.onContentChanged(content,percent);
            }
        }

        private void onMenuChanged(View menu, float percent) {
            if(onViewChangedListener !=null){
                onViewChangedListener.onMenuChanged(menu,percent);
            }

        }


        private void smooth2Start(boolean isMoveContent) {
            if(isMoveContent)
                dragHelper.smoothSlideViewTo(content, 0, 0);
            else
                dragHelper.smoothSlideViewTo(menu, menuStartLeft, 0);
            invalidate();
        }

        private void smooth2End(boolean isMoveContent) {
            if(isMoveContent)
                dragHelper.smoothSlideViewTo(content, contentEndLeft, 0);
            else
                dragHelper.smoothSlideViewTo(menu, 0, 0);
            invalidate();
        }

        /**
         * 释放view时的回调，和ACTIION_UP 类似，但是可以知道释放时的速度
         * releasedChild
         * xvel x方向速度
         * yvel y 方向速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            onStateChanged(STATE_MOVE);
            L.e("onViewReleased", "xvel", xvel, "yvel", yvel);
            if (xvel > 600) {
                smooth2End(contentEndLeft!=0);
                return;
            }
            if (xvel < -600) {
                smooth2Start(contentEndLeft!=0);
                return;
            }
            if(contentEndLeft==0){
                smooth2StartOrEnd(menu.getLeft(),menuStartLeft,false);
            }else {
                smooth2StartOrEnd(content.getLeft(),contentEndLeft,true);
            }
        }

        private void smooth2StartOrEnd(int currentLeft,int endLeft,boolean isMoveContent){
            if (currentLeft > endLeft / 2) {
                smooth2End(isMoveContent);
            } else {
                smooth2Start(isMoveContent);
            }
        }

    }

    public Builder getBuilder(View content, View menu, int menuWidth) {
        return new Builder(content, menu, menuWidth);
    }

    public Builder getBuilder(Fragment content, Fragment menu, FragmentManager fragmentManager, int menuWidth) {
        return new Builder(content, menu,fragmentManager, menuWidth);
    }

    public class Builder {
        View menu;
        View content;
        Fragment contentFragment;
        Fragment menuFragment;

        int menuWidth;
        int menuStartLeft;
        int contentEndLeft;
        int slideWidth;
        boolean isCover;
        OnViewChangedListener onViewChangedListener;
        OnStateChangedListener stateChangedListener;
        private FrameLayout contentLayout;
        private FrameLayout menuLayout;
        private FragmentManager fragmentManager;

        public Builder(View content, View menu, int menuWidth) {
            this.content = content;
            this.menu = menu;
            this.menuWidth = menuWidth;
            this.menuStartLeft = -menuWidth;
            this.contentEndLeft = menuWidth;
            this.slideWidth = menuWidth;
        }

        public Builder(Fragment content, Fragment menu, FragmentManager fragmentManager, int menuWidth){
            contentLayout = new FrameLayout(getContext());
            contentLayout.setId(generateId());
            menuLayout = new FrameLayout(getContext());
            menuLayout.setId(generateId());
            this.contentFragment = content;
            this.menuFragment = menu;
            this.menuWidth = menuWidth;
            this.menuStartLeft = -menuWidth;
            this.contentEndLeft = menuWidth;
            this.fragmentManager = fragmentManager;
            this.slideWidth = menuWidth;
            this.isCover = false;
        }

        public Builder setMenuStartLeft(int menuStartLeft){
            this.menuStartLeft = menuStartLeft;
            return this;
        }

        public Builder setContentEndLeft(int contentEndLeft){
            this.contentEndLeft = contentEndLeft;
            return this;
        }

        public Builder setCover(boolean isCover){
            this.isCover = isCover;
            return this;
        }


        public Builder setOnViewChangedListener(OnViewChangedListener onViewChangedListener){
            this.onViewChangedListener=onViewChangedListener;
            return this;
        }

        public Builder setOnStateChangedListener(OnStateChangedListener stateChangedListener){
            this.stateChangedListener = stateChangedListener;
            return this;
        }


        public void build() {

            SlidingMenu.this.menuWidth = menuWidth;
            SlidingMenu.this.menuStartLeft = menuStartLeft;
            SlidingMenu.this.contentEndLeft = contentEndLeft;
            SlidingMenu.this.contentVel = (float)contentEndLeft/(float)slideWidth;
            SlidingMenu.this.menuVel = (float)Math.abs(menuStartLeft)/(float)slideWidth;
            SlidingMenu.this.onViewChangedListener = onViewChangedListener;
            SlidingMenu.this.stateChangedListener = stateChangedListener;

            if(menuStartLeft==0 && contentEndLeft==0 ){
                throw new RuntimeException("不能同时设置 menuStartLeft、contentEndLeft为0");
            }

            if(content!=null && menu!=null){
                addView(menu);
                addView(content);
                SlidingMenu.this.menu = menu;
                SlidingMenu.this.content = content;
            }

            if(contentLayout!=null && menuLayout!=null){
                if(isCover){
                    addView(contentLayout);
                    addView(menuLayout);
                }else {
                    addView(menuLayout);
                    addView(contentLayout);
                }

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(contentLayout.getId(),contentFragment,"content");
                fragmentTransaction.add(menuLayout.getId(),menuFragment,"menu");
                fragmentTransaction.commit();
                SlidingMenu.this.menu = menuLayout;
                SlidingMenu.this.content = contentLayout;
            }
            SlidingMenu.this.invalidate();

        }

    }
    private OnViewChangedListener onViewChangedListener;
    private OnStateChangedListener stateChangedListener;
    public interface OnViewChangedListener {
        /**
         *
         * @param content 变化的view
         * @param percent 变化的百分比
         */
        void onContentChanged(View content, float percent);
        /**
         *
         * @param menu 变化的view
         * @param percent 变化的百分比
         */
        void onMenuChanged(View menu, float percent);
    }

    public static final int STATE_START=0;
    public static final int STATE_MOVE=1;
    public static final int STATE_END=2;
    public static final int STATE_CAPTURE=3;
    public interface OnStateChangedListener {
        void onState(int state);
    }

    private void onStateChanged(int state){
        if(stateChangedListener!=null){
            stateChangedListener.onState(state);
        }
    }

    private final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private  int generateId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}
