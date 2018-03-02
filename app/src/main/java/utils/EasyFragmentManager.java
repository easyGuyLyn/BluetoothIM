package utils;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by archar on 2018.
 */
public class EasyFragmentManager {

    public static final String TOP_FRGMENT = "top_fragment";

    private Class<? extends android.support.v4.app.Fragment> fromClass;

    private EasyFragmentManager() {
    }


    private static EasyFragmentManager mInstance = new EasyFragmentManager();

    public static EasyFragmentManager getInstance() {
        return mInstance;
    }


    private HashMap<String, android.support.v4.app.Fragment> mFragmentHashMap = new HashMap<>();
    private FragmentManager mFragmentManager;

    public FragmentManager getFragmentManager() {
        return mFragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    public android.support.v4.app.Fragment getFragment(Class<? extends android.support.v4.app.Fragment> clazz) {
        if (clazz == null) {
            return null;
        }
        String key = clazz.getSimpleName();
        android.support.v4.app.Fragment fragment = mFragmentHashMap.get(key);
        if (fragment == null) {
            synchronized (EasyFragmentManager.class) {
                try {
                    fragment = clazz.newInstance();
                    mFragmentHashMap.put(key, fragment);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        return fragment;
    }


    public void clear() {
        clearAllFragmentQuote();
        fromClass = null;
    }

    //清除所有fragment被栈的引用
    //再清除map对fragment的引用  很快就会释放掉所有的fragment
    private void clearAllFragmentQuote() {
        Iterator iterator = mFragmentHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) entry.getValue();
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.remove(fragment);
            transaction.commitAllowingStateLoss();
        }
        mFragmentHashMap.clear();
    }


    public void switchFragment(FragmentManager mFragmentManager, Class<? extends android.support.v4.app.Fragment> clazz) {
        setFragmentManager(mFragmentManager);
        android.support.v4.app.Fragment fromFragment = getFragment(fromClass);
        android.support.v4.app.Fragment toFragment = getFragment(clazz);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
   //     transaction.setCustomAnimations(R.anim.slide_in_bottom_ssc, R.anim.slide_out_top_ssc);
        if (fromFragment != null && !fromFragment.isHidden() && fromFragment != toFragment) {
            if (!toFragment.isAdded()) {
           //     transaction.hide(fromFragment).add(R.id.fragment_content, toFragment);
            } else {
                transaction.hide(fromFragment).show(toFragment);
            }
        } else {
            if (!toFragment.isAdded()) {
            //    transaction.add(R.id.fragment_content, toFragment);
            } else {
                transaction.show(toFragment);
            }
        }
        transaction.commitAllowingStateLoss();
        fromClass = clazz;
       // RxBus.get().post(TOP_FRGMENT, toFragment.getId() + "");
    }
}
