package com.jw.flickrfeed;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AppTest {

    @BeforeClass
    public static void beforeClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    }

    @AfterClass
    public static void afterClass() {
        RxAndroidPlugins.reset();
    }
}
