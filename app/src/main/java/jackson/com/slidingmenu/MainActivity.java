package jackson.com.slidingmenu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_kugou).setOnClickListener(this);
        findViewById(R.id.btn_qq).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_kugou:
                startActivity(new Intent(this,KugouActivity.class));
                break;
            case R.id.btn_qq:
                startActivity(new Intent(this,QQActivity.class));
                break;
        }
    }
}
