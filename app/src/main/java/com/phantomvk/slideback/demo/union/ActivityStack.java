package com.phantomvk.slideback.demo.union;

import android.app.Activity;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public final class ActivityStack {
    private static final List<WeakReference<View>> DECORS = new ArrayList<>(8);

    public static void add(Activity activity) {
        DECORS.add(new WeakReference<>(activity.getWindow().getDecorView()));
    }

    public static WeakReference<View> peek() {
        int index = DECORS.size() - 2;
        return index < 0 ? null : DECORS.get(index);
    }

    /**
     * The activity passed to this method should not be null.
     * <p>
     * In normal case, we will always find the reference saved in the list which the activity has
     * been added.
     *
     * @param activity the activity to remove, should not be null.
     */
    public static void remove(Activity activity) {
        ListIterator<WeakReference<View>> iterator = DECORS.listIterator();
        View decor = activity.getWindow().getDecorView();

        while (iterator.hasNext()) {
            View decorRef = iterator.next().get();
            if (decorRef == null) {
                iterator.remove();
                continue;
            }

            // Found the target decorView, remove it right now.
            if (decorRef == decor) {
                iterator.remove();
                return;
            }
        }
    }
}
