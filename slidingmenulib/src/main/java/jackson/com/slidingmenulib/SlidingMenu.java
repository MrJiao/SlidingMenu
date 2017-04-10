package jackson.com.slidingmenulib;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jackson.commonutillib.L;

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
            if(menu!=null){
                if(menu.getLeft()>menuStartLeft/2){
                    menu.offsetLeftAndRight(-menu.getLeft());
                    onStateChanged(STATE_END);
                }else {
                    onStateChanged(STATE_START);
                }
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }


    private class CallBack extends ViewDragHelper.Callback {

        long lastTryCaptureView;
        long currentTime;
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            currentTime = System.currentTimeMillis();
            L.e("currentTime-lastTryCaptureView",currentTime-lastTryCaptureView);
            if(currentTime-lastTryCaptureView<210){
                lastTryCaptureView = currentTime;
                return false;
            }
            onStateChanged(STATE_CAPTURE);
            if(child == content)
                L.e("content tryCaptureView", "pointerId", pointerId);
            else
                L.e("menu tryCaptureView", "pointerId", pointerId);
            lastTryCaptureView = currentTime;
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
                if (left <= contentEndLeft) {
                    return left > 0 ? left : 0;
                } else {
                    return contentEndLeft;
                }
            } else if(child == menu){
                left = left - dx + getMenuDxBeforContentChange(dx);
                if(left >=0){
                    return 0;
                }
                L.e("menu clampViewPositionHorizontal","menu.getLeft()",menu.getLeft(), "left", left, "dx", dx);
                return left < 0 ? left : 0;
            }
            return 0;
        }

        private float changePercent;
        float lastPersent;

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
                changePercent = (float) left / (float) contentEndLeft;
                moveMenu(dx);
                onMenuChanged(menu, changePercent);
                onContentChanged(content, changePercent);
                lastPersent = changePercent;
            }
            if (changedView == menu) {
                L.e("menu onViewPositionChanged", "left", left, "top", top, "dx", dx, "dy", dy);
                changePercent = (float) (content.getLeft()+getContentDx(dx)) / (float) contentEndLeft;
                moveContent(dx);
                onMenuChanged(menu, changePercent);
                onContentChanged(content, changePercent);
                lastPersent = changePercent;
            }
        }

        private int getMenuDxBeforContentChange(int pointDx) {
            return -roundInt((float) menuStartLeft / (float) contentEndLeft *  pointDx);
            /*float mDx = (float) menuStartLeft / (float) contentEndLeft * pointDx;
            //差的百分比，原则上chaPersent等于0
            float chaPersent = ((float) content.getLeft() / (float) contentEndLeft) - (((float) menu.getLeft() - (float)menuStartLeft)/ (float) Math.abs(menuStartLeft));
            float chaDx = chaPersent * Math.abs(menuStartLeft);
            L.e("getMenuDxBeforContentChange mDx",mDx,"chaPersent",chaPersent,"chaDx",chaDx);
            return -roundInt(mDx + chaDx);*/
        }

        private int getMenuDxAfterContentChange(int pointDx) {
            return -roundInt((float) menuStartLeft / (float) contentEndLeft *  pointDx);
            /*float mDx = (float) menuStartLeft / (float) contentEndLeft * pointDx;
            //差的百分比，原则上chaPersent等于0
            float chaPersent = ((float) content.getLeft() / (float) contentEndLeft) - (((float) menu.getLeft() - mDx - (float)menuStartLeft)/ (float) Math.abs(menuStartLeft));
            if(chaPersent>0.08){
                return -roundInt(mDx);
            }
            float chaDx = chaPersent * Math.abs(menuStartLeft);
            L.e("getMenuDxAfterContentChange mDx",mDx,"chaPersent",chaPersent,"chaDx",chaDx);
            return -roundInt(mDx + chaDx);*/
        }



        private int getContentDx(int menuDx) {
            return -roundInt((float) contentEndLeft / (float) menuStartLeft *  menuDx);
        }

        private void moveContent(int menuDx) {
            L.e("moveContent", "content.getLeft()", content.getLeft(),"menuDx",menuDx);
            int contentDx = getContentDx(menuDx);
            if (contentDx > 0) {
                if (content.getLeft() + contentDx > contentEndLeft) {
                    content.offsetLeftAndRight(contentEndLeft - content.getLeft());
                    return;
                }
                content.offsetLeftAndRight(getContentDx(menuDx));
            } else if (contentDx < 0) {
                if (content.getLeft() + contentDx < 0) {
                    content.offsetLeftAndRight(- content.getLeft());
                    return;
                }
                content.offsetLeftAndRight(getContentDx(menuDx));
            }
        }

        private int zeroCount=0;
        int menuDx;
        private void moveMenu(int contentDx) {
            menuDx = getMenuDxAfterContentChange(contentDx);
            L.e("moveMenu start", "menu.getLeft()", menu.getLeft(),"menuDx",menuDx);
            if(menuDx>0){
                zeroCount = 0;
                if (menu.getLeft() + menuDx > 0) {
                    menu.offsetLeftAndRight(-menu.getLeft());
                    L.e("moveMenu menu.getLeft() + menuDx", "menu.getLeft()", menu.getLeft(),"menuDx",menuDx);
                    return;
                }
                menu.offsetLeftAndRight(menuDx);
                L.e("moveMenu menuDx>0", "menu.getLeft()", menu.getLeft(),"menuDx",menuDx);
            }else if(menuDx<0){
                zeroCount = 0;
                if (menu.getLeft() + menuDx < menuStartLeft) {
                    menu.offsetLeftAndRight(menuStartLeft-menu.getLeft());
                    L.e("moveMenu menu.getLeft() + menuDx < menuStartLeft", "menu.getLeft()", menu.getLeft(),"menuDx",menuDx);
                    return;
                }
                menu.offsetLeftAndRight(menuDx);
                L.e("moveMenu menuDx<0", "menu.getLeft()", menu.getLeft(),"menuDx",menuDx);
            }/*else {
                if(menu.getLeft()>=0)return;
                zeroCount++;
                if(zeroCount==2){
                    menu.offsetLeftAndRight(menuDx<0?-1:1);
                    zeroCount=0;
                }
                L.e("moveMenu 00000", "menu.getLeft()", menu.getLeft(),"menuDx",menuDx);
            }*/
            L.e("moveMenu end", "menu.getLeft()", menu.getLeft(),"menuDx",menuDx);
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

        public int roundInt(float f) {
            if (f != f) {
                return 0;
            }
            return (int) Math.floor(f + 0.5555f);

        }

        private void smooth2Start() {
            dragHelper.smoothSlideViewTo(content, 0, 0);
            invalidate();
        }

        private void smooth2End() {
            dragHelper.smoothSlideViewTo(content, contentEndLeft, 0);
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
                smooth2End();
                return;
            }
            if (xvel < -600) {
                smooth2Start();
                return;
            }
            if (content.getLeft() > contentEndLeft / 2) {
                smooth2End();
            } else {
                smooth2Start();
            }
        }

    }


    public Builder getBuilder(View content, View menu, int menuWidth, int menuStartLeft, int contentEndLeft) {
        return new Builder(content, menu, menuWidth, menuStartLeft, contentEndLeft);
    }

    public Builder getBuilder(Fragment content, Fragment menu, FragmentManager fragmentManager, int menuWidth, int menuStartLeft, int contentEndLeft) {
        return new Builder(content, menu,fragmentManager, menuWidth, menuStartLeft, contentEndLeft);
    }


    public class Builder {
        View menu;
        View content;
        Fragment contentFragment;
        Fragment menuFragment;

        int menuWidth;
        int menuStartLeft;
        int contentEndLeft;
        OnViewChangedListener onViewChangedListener;
        OnStateChangedListener stateChangedListener;
        private FrameLayout contentLayout;
        private FrameLayout menuLayout;
        private FragmentManager fragmentManager;

        public Builder(View content, View menu, int menuWidth, int menuStartLeft, int contentEndLeft) {
            this.content = content;
            this.menu = menu;
            this.menuWidth = menuWidth;
            this.menuStartLeft = menuStartLeft;
            this.contentEndLeft = contentEndLeft;
        }

        public Builder(Fragment content, Fragment menu, FragmentManager fragmentManager, int menuWidth, int menuStartLeft, int contentEndLeft){
            contentLayout = new FrameLayout(getContext());
            contentLayout.setId(generateId());
            menuLayout = new FrameLayout(getContext());
            menuLayout.setId(generateId());
            this.contentFragment = content;
            this.menuFragment = menu;
            this.menuWidth = menuWidth;
            this.menuStartLeft = menuStartLeft;
            this.contentEndLeft = contentEndLeft;
            this.fragmentManager = fragmentManager;
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
            SlidingMenu.this.onViewChangedListener = onViewChangedListener;
            SlidingMenu.this.stateChangedListener = stateChangedListener;
            if(content!=null && menu!=null){
                addView(menu);
                addView(content);
                SlidingMenu.this.menu = menu;
                SlidingMenu.this.content = content;
            }

            if(contentLayout!=null && menuLayout!=null){
                addView(menuLayout);
                addView(contentLayout);
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
