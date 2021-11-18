package cc.ibooker.test1;

import android.util.Log;

import cc.ibooker.sprovider_annotation.SProvider;
import cc.ibooker.testcommon.ITest;

@SProvider(alias = "TestProvider1")
public class TestProvider implements ITest {

    public void print(String msg) {
        Log.i("TestProvider: ", msg);
    }
}
