package jackson.com.slidingmenu;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import jackson.com.slidingmenulib.SlidingMenu;


/**
 * Created by Jackson on 2017/3/13.
 * Version : 1
 * Details :
 */
public class Other1Activity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq);
        SlidingMenu sm = (SlidingMenu) findViewById(R.id.sm);
        sm.getBuilder(new ContentFragment(),new MenuFragment(),getFragmentManager(), 870)
                .setContentEndLeft(0)
                .setCover(true)
                .build();
    }
}
