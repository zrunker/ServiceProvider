package cc.ibooker.test2;

import android.util.Log;

import cc.ibooker.sprovider_annotation.SProvider;
import cc.ibooker.testcommon.ITest;

@SProvider(alias = "TestProvider2")
public class TestProvider implements ITest {

    public void print(String msg) {
        Log.i("TestProvider: ", msg);
    }
}
