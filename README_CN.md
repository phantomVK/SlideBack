SlideBack
=========

[![](https://jitpack.io/v/phantomVK/SlideBack.svg)](https://jitpack.io/#phantomVK/SlideBack) [![license](https://img.shields.io/badge/License-Apache2.0-brightgreen)](https://github.com/phantomVK/SlideBack/blob/master/LICENSE)

[README in English](./README.md)

用于Android上协助完成滑动关闭界面的开源库，在开启很多 __Activity__ 后也不卡顿

<img src="https://j.gifs.com/xn8gqB.gif" alt="gif" width="288" height="512" style="display: inline;"/>



预览
----------
请从 [更新历史](https://github.com/phantomVK/SlideBack/releases) 获得最新体验用安装包。



下载
-----------
可通过 __Gradle__ 从 __JitPack__ 下载依赖：

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation "com.github.phantomVK.SlideBack:slideback:latest.release"
}
```


用法
-------

在 __Activity__ 的主题 __styles.xml__ 添加 `<item name="android:windowIsTranslucent">true</item>` 配置，例如

```java
<resources>
    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <!-- Customize your theme here. -->
        ....
        <item name="android:windowIsTranslucent">true</item>
    </style>
</resources>
```

用 __Activity__ 继承名为 __SlideActivity__ 的父类，在调用 __setContentView(View)__ 完成后设置目标滑动边缘，建议在 __onContentChanged()__ 内完成

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

    // 可选步骤，在setContentView()之后指定触摸边缘
    // 默认为 ViewDragHelper.EDGE_LEFT
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

    // 可选步骤，提供自定义SlideManager实例
    @NonNull
    @Override
    protected SlideManager slideManagerProvider() {
        return new SlideManager(this, new CustomAdapter(this));
    }
}
```

彻底关闭滑动操作，设置后 __SlideManager__ 内部不会初始化，也不能在后续运行重新初始化

```java
public class MainActivity extends SlideActivity implements SlideManager.Conductor{

    @Override
    public boolean slideBackDisable() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }
}
```

以下方法暂时关闭功能，可在需要时再次启用。

变量 __slideManager__ 来自父类 __SlideActivity__。当滑动操作被彻底关闭后，__slideManager.getSlideLayout()__ 返回值为空，详情请看上文名为 __slideBackDisable()__ 方法的指南

```java
public class MainActivity extends SlideActivity {

    @Override
    protected void onResume() {
        super.onResume();
        slideManager.getSlideLayout().setEnable(false);
    }
}
```



兼容性
-------------

* **最低 Android SDK**: SlideBack 最低支持 API14；
* **编译 Android SDK**: SlideBack 要求使用 API28 或更新版本进行编译；
* **通过不同依赖分别兼容 AndroidX** 和 **Android Support**；



许可证
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