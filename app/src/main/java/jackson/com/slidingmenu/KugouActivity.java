package jackson.com.slidingmenu;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;


import jackson.com.slidingmenulib.L;
import jackson.com.slidingmenulib.SlidingMenu;


/**
 * Created by Jackson on 2017/3/13.
 * Version : 1
 * Details :
 */
public class KugouActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kugou);
        SlidingMenu sm = (SlidingMenu) findViewById(R.id.sm);
        sm.getBuilder(new ContentFragment(), new MenuFragment(), getFragmentManager(), 850)
        .setMenuStartLeft(-350)
        .setOnViewChangedListener(new ScaleChange())
        .setOnStateChangedListener(new StateListener())
        .build();
    }


    private class ScaleChange implements SlidingMenu.OnViewChangedListener {

        private boolean first=true;

        @Override
        public void onContentChanged(View content, float percent) {
            if(first){
                content.setPivotX(0);
                content.setPivotY(content.getHeight()/2);
                first = false;
            }
            content.setScaleX(1 - .3f * percent);
            content.setScaleY(1 - .3f * percent);
            content.setAlpha(1 - .5f * percent);
        }

        @Override
        public void onMenuChanged(View menu, float percent) {
            menu.setScaleX(.7f + .3f * percent);
            menu.setScaleY(.7f + .3f * percent);
            menu.setAlpha(.2f+.8f * percent);
        }
    }


    private class StateListener implements SlidingMenu.OnStateChangedListener {
        @Override
        public void onState(int state) {
            String s = "";
            switch (state) {
                case SlidingMenu.STATE_START:
                    s = "STATE_START";
                    break;
                case SlidingMenu.STATE_MOVE:
                    s = "STATE_MOVE";
                    break;
                case SlidingMenu.STATE_END:
                    s = "STATE_END";
                    break;
                case SlidingMenu.STATE_CAPTURE:
                    s = "STATE_CAPTURE";
                    break;
            }
            L.e("onState", s);
        }

    }


}
