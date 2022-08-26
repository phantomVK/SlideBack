SlideBack
=========

[![](https://jitpack.io/v/phantomVK/SlideBack.svg)](https://jitpack.io/#phantomVK/SlideBack) [![license](https://img.shields.io/badge/License-Apache2.0-brightgreen)](https://github.com/phantomVK/SlideBack/blob/master/LICENSE)

[中文版README](./README_CN.md)

An android library that helps you to finish activity with slide gesture, never stuck with a bunch of activities openning.

<img src="https://j.gifs.com/xn8gqB.gif" alt="gif" width="288" height="512" style="display: inline;"/>

Preview
----------
Please download the latest release apk via [Download APK](./static/SlideBack_release.apk).

Download
-----------
You can download the dependency from __JitPack__ using __Gradle__ for __AndroidX__.

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation "com.github.phantomVK:SlideBack:latest.release"
}
```

Usage
-------

Add `<item name="android:windowIsTranslucent">true</item>`  to your __ActivityTheme__ in __styles.xml__, such as

```xml
<resources>
    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <!-- Customize your theme here. -->
        ....
        <item name="android:windowIsTranslucent">true</item>
    </style>
</resources>
```

Extends the class named __SlideActivity__ using your __Activity__, set the edge which is going to track after __setContentView(View)__.

```java
public class MainActivity extends SlideActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!slideManager.isSlideDisable()) {
            overridePendingTransition(R.anim.slide_in_right, 0);
        }
    }

    // Optional, set tracking edge after content changed.
    // Default edge is ViewDragHelper.EDGE_LEFT.
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        slideManager.getSlideLayout().setTrackingEdge(ViewDragHelper.EDGE_RIGHT);
    }
    
    @Override
    public void finish() {
        super.finish();
        if (!slideManager.isSlideDisable()) {
            overridePendingTransition(0, R.anim.slide_out_right);
        }
    }

    // Optional, provide a custom SlideManager instance to SlideActivity.
    @NonNull
    @Override
    protected SlideManager slideManagerProvider() {
        return new SlideManager(this, new CustomAdapter(this));
    }
}
```

Totally disable the slide function, nothing inside __SlideManager__ will be initialized, re-initialize is illegal either.

```java
public class MainActivity extends SlideActivity implements SlideManager.Conductor{

    @Override
    public boolean slideBackDisable() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }
}
```

To temporarily disable the slide action, or enable again whenever you want, see the method below.

__slideManager__ comes from __SlideActivity__ the super class you extended. What __slideManager.getSlideLayout()__ returns is null if slide action has been totally disabled, see method mentioned above named __slideBackDisable()__ for more details.

```java
public class MainActivity extends SlideActivity {

    @Override
    protected void onResume() {
        super.onResume();
        slideManager.getSlideLayout().setEnable(false);
    }
}
```

Compatibility
-------------

 * **Minimum Android SDK**: SlideBack requires a minimum API level of 14.
 * **Compile Android SDK**: SlideBack requires you to compile against API 28 or later.
 * Both **AndroidX** and **Android Support** are supported by using different dependencies.

License
--------

```
Copyright 2019 WenKang Tan(phantomVK)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```