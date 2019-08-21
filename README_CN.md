SlideBack
=========

[![Download](https://api.bintray.com/packages/phantomtvk/SlideBack/SlideBack/images/download.svg?version=0.1.5)](https://bintray.com/phantomtvk/SlideBack/SlideBack/0.1.5/link) [![中文README](https://img.shields.io/badge/Readme-%E4%B8%AD%E6%96%87-orange)](https://github.com/phantomVK/SlideBack/blob/master/README_CN.md) [![README in English](https://img.shields.io/badge/Readme-English-yellow)](https://github.com/phantomVK/SlideBack/blob/master/README.md) [![license](https://img.shields.io/badge/License-Apache2.0-brightgreen)](https://github.com/phantomVK/SlideBack/blob/master/LICENSE)

用于Android上协助完成滑动关闭界面的开源库

![MP4](static/record.mp4)

下载试用
----------
请到这里下载最新试用安装包：[更新历史](https://github.com/phantomVK/SlideBack/releases)。

注意：试用包因包含ActionBar，而暂不支持从顶部下拉关闭页面。请自行下载源码更换为 __NoActionBar__ 主题并编译安装。

依赖
-----------
可通过 __Gradle__ 从 __JCenter__ 下载依赖

```gradle
repositories {
    google()
    jcenter()
}

dependencies {
    implementation 'com.phantomvk.slideback:slideback:0.1.5'
}
```
或 Maven:
```xml
<dependency>
  <groupId>com.phantomvk.slideback</groupId>
  <artifactId>slideback</artifactId>
  <version>0.1.5</version>
  <type>pom</type>
</dependency>
```

使用方法
-------

继承名为 __SlideActivity__ 的父类，调用 __setContentView(View)__ 完成后指定触摸边缘，即方法 __onContentChanged()__

```java
public class MainActivity extends SlideActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // 可选步骤，在setContentView()之后指定触摸边缘，默认为左边缘
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mManager.getSlideLayout().setTrackingEdge(ViewDragHelper.EDGE_RIGHT);
    }

    @Override
    public void onBackPressed() {
        SlideLayout m = mManager.getSlideLayout();
        if (m != null)
            m.slideExit();
        else {
            super.onBackPressed();
        }
    }
}
```

彻底关闭滑动操作，关闭后 __SlideManager__ 不会进行任何初始化，也不能在后续重新初始化

```java
public class MainActivity extends SlideActivity {
    ....

    @Override
    public boolean slideBackDisable() {
        return true;
    }
}
```

以下方法暂时关闭功能，可在需要时再次启用。

变量 __mManager__ 来自父类 __SlideActivity__。当滑动操作被彻底关闭后，__mManager.getSlideLayout()__ 返回值为空，详情请看上文名为 __slideBackDisable()__ 方法的指南

```java
mManager.getSlideLayout().setEnable(false);
```
兼容
-------------

 * **最低 Android SDK**: SlideBack 最低支持到 API15
 * **编译 Android SDK**: SlideBack 要求使用 API 28 或更新版本进行编译

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