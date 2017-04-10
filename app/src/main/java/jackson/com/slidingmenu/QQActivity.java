package jackson.com.slidingmenu;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.jackson.commonutillib.L;

import jackson.com.slidingmenulib.SlidingMenu;


/**
 * Created by Jackson on 2017/3/13.
 * Version : 1
 * Details :
 */
public class QQActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq);
        SlidingMenu sm = (SlidingMenu) findViewById(R.id.sm);
        SlidingMenu.Builder builder = sm.getBuilder(
                new ContentFragment(),
                new MenuFragment(),
                getFragmentManager(), 800);
        builder.setContentEndLeft(800)
                .setMenuStartLeft(-500)
                .build();
    }
}
