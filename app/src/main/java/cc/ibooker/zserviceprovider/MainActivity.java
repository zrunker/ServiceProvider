package cc.ibooker.zserviceprovider;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import cc.ibooker.sprovider_api.ISProvider;
import cc.ibooker.sprovider_api.ServiceProvider;
import cc.ibooker.testcommon.ITest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ISProvider isProvider = ServiceProvider.load("TestProvider1");
        if (isProvider != null) {
            ITest iTest = (ITest) isProvider;
            iTest.print("--测试");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ISProvider isProvider = ServiceProvider.load("TestProvider2");
                if (isProvider != null) {
                    ITest iTest = (ITest) isProvider;
                    iTest.print("测试---");
                }
            }
        }, 1000);

    }
}