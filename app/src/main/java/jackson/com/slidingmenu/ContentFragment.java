package jackson.com.slidingmenu;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AndroidRuntimeException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jackson on 2017/3/24.
 * Version : 1
 * Details :
 */
public class ContentFragment extends Fragment {

    public void setBackgroud(int idRes){
        Bundle arguments = getArguments();
        if(arguments==null){
            arguments = new Bundle();
        }
        arguments.putInt("BACKGROUND",idRes);
        setArguments(arguments);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_content, container,false);
        Bundle arguments = getArguments();
        if(arguments!=null){
            int backRes = arguments.getInt("BACKGROUND");
            v.findViewById(R.id.rl).setBackgroundResource(backRes);
        }
        return v;
    }
}
