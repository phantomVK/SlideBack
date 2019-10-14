package com.phantomvk.slideback.support.utility;

/**
 * For more details, see interface #TranslucentConversionListener in {@link android.app.Activity}.
 */
@FunctionalInterface
public interface TranslucentConversionListener {

    void onTranslucentConversionComplete(boolean drawComplete);
}
