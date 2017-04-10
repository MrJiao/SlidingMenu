package jackson.com.slidingmenu;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;


import jackson.com.slidingmenulib.SlidingMenu;


/**
 * Created by Jackson on 2017/3/13.
 * Version : 1
 * Details :
 */
public class XuanZhuanActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kugou);
        SlidingMenu sm = (SlidingMenu) findViewById(R.id.sm);
        sm.getBuilder(new ContentFragment(), new MenuFragment(), getFragmentManager(), 850)
        .setMenuStartLeft(0)
        .setOnViewChangedListener(new ScaleChange())
        .build();

    }


    class ScaleChange implements SlidingMenu.OnViewChangedListener {

        private boolean contentfirst=true;
        private boolean menufirst=true;

        @Override
        public void onContentChanged(View content, float percent) {
            if(contentfirst){
                content.setPivotX(0);
                contentfirst = false;
            }
            content.setScaleX(1 - .3f * percent);
            content.setScaleY(1 - .3f * percent);
            content.setAlpha(1 - .5f * percent);
        }

        @Override
        public void onMenuChanged(View menu, float percent) {
            if(menufirst){
                menu.setPivotX(0);
                menufirst = false;
            }
            menu.setRotationY(90*(1-percent));
            menu.setAlpha(.2f+.8f * percent);
        }
    }

}
