SlideBack
=========

[![Download](https://api.bintray.com/packages/phantomtvk/SlideBack/SlideBack/images/download.svg?version=0.2.0)](https://bintray.com/phantomtvk/SlideBack/SlideBack/0.2.0/link) [![中文README](https://img.shields.io/badge/Readme-%E4%B8%AD%E6%96%87-orange)](https://github.com/phantomVK/SlideBack/blob/master/README_CN.md) [![README in English](https://img.shields.io/badge/Readme-English-yellow)](https://github.com/phantomVK/SlideBack/blob/master/README.md) [![license](https://img.shields.io/badge/License-Apache2.0-brightgreen)](https://github.com/phantomVK/SlideBack/blob/master/LICENSE)

An android library that helps you to finish activity with slide gesture.

![GIF](https://j.gifs.com/lxDMW6.gif)

Preview
----------
Please download latest release apk via: [Release history](https://github.com/phantomVK/SlideBack/releases)

Download
-----------
You can download from __JCenter__ using __Gradle__.

```gradle
repositories {
    google()
    jcenter()
}

dependencies {
    implementation 'com.phantomvk.slideback:slideback:0.1.5'
}
```
Or Maven:
```xml
<dependency>
  <groupId>com.phantomvk.slideback</groupId>
  <artifactId>slideback</artifactId>
  <version>0.1.5</version>
  <type>pom</type>
</dependency>
```

Usage
-------

Extends the class named __SlideActivity__ in your __Activity__, set the edge which is going to track after __setContentView(View)__.

```java
public class MainActivity extends SlideActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Optional, set tracking edge after content changed.
    // Default edge is ViewDragHelper.EDGE_LEFT.
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mManager.getSlideLayout().setTrackingEdge(ViewDragHelper.EDGE_RIGHT);
    }

    @Override
    public void onBackPressed() {
        // Optional.
        SlideLayout m = mManager.getSlideLayout();
        if (m != null) {
            m.slideExit();
        } else {
            super.onBackPressed();
        }
    }
}
```

Totally disable the slide action, nothing inside __SlideManager__ will be initialized, re-initialize is illegal either.

```java
public class MainActivity extends SlideActivity {

    @Override
    public boolean slideBackDisable() {
        return true;
    }
}
```

Temporarily disable slide action, enable again whenever you want.

__mManager__ comes from __SlideActivity__ the super class you extended. What __mManager.getSlideLayout()__ returns is null if slide action has been totally disabled, see method mentioned above called __slideBackDisable()__ for more details.

```java
mManager.getSlideLayout().setEnable(false);
```

Compatibility
-------------

 * **Minimum Android SDK**: SlideBack requires a minimum API level of 15.
 * **Compile Android SDK**: SlideBack requires you to compile against API 28 or later.

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