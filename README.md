SlideBack
=========

[![Download](https://api.bintray.com/packages/phantomtvk/SlideBack/slideback/images/download.svg?version=0.2.4)](https://bintray.com/phantomtvk/SlideBack/slideback/0.2.4/link) [![中文README](https://img.shields.io/badge/Readme-%E4%B8%AD%E6%96%87-orange)](https://github.com/phantomVK/SlideBack/blob/master/README_CN.md) [![README in English](https://img.shields.io/badge/Readme-English-yellow)](https://github.com/phantomVK/SlideBack/blob/master/README.md) [![license](https://img.shields.io/badge/License-Apache2.0-brightgreen)](https://github.com/phantomVK/SlideBack/blob/master/LICENSE)

[中文版README](./README_CN.md)

An android library that helps you to finish activity with slide gesture, never stuck with a bunch of activities openning.

![GIF](https://j.gifs.com/71OyLj.gif)

Preview
----------
Please download the latest release apk via [Release history](https://github.com/phantomVK/SlideBack/releases).

Download
-----------
You can download the dependency from __JCenter__ using __Gradle__ for __AndroidX__.

```groovy
repositories {
    google()
    jcenter()
}

dependencies {
    implementation 'com.phantomvk.slideback:slideback:0.2.4'
}
```
Or Maven:
```xml
<dependency>
  <groupId>com.phantomvk.slideback</groupId>
  <artifactId>slideback</artifactId>
  <version>0.2.4</version>
  <type>pom</type>
</dependency>
```

If your are no using __AndroidX__, why not try this for __Android.Support__?

```groovy
repositories {
    google()
    jcenter()
}

dependencies {
    implementation 'com.phantomvk.slideback:slideback-support:0.2.4'
}
```

Or Maven:

```xml
<dependency>
	<groupId>com.phantomvk.slideback</groupId>
	<artifactId>slideback-support</artifactId>
	<version>0.2.4</version>
	<type>pom</type>
</dependency>
```

They both sharing the same version with different __artifactId__, you just need one of then.

Usage
-------

First, extends the class named __SlideActivity__ using your __Activity__, set the edge which is going to track after __setContentView(View)__.

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

    // called by super.onBackPressed(); to finish activity with transition.
    @Override
    public void finishAfterTransition() {
        SlideLayout l = mManager.getSlideLayout();
        if (l != null) {
            l.slideExit();
        } else {
            super.finishAfterTransition();
        }
    }
}
```

Totally disable the slide function, nothing inside __SlideManager__ will be initialized, re-initialize is illegal either.

```java
public class MainActivity extends SlideActivity {

    @Override
    public boolean slideBackDisable() {
        return true;
    }
}
```

To temporarily disable the slide action, enable again whenever you want, see the method below.

__mManager__ comes from __SlideActivity__ the super class you extended. What __mManager.getSlideLayout()__ returns is null if slide action has been totally disabled, see method mentioned above named __slideBackDisable()__ for more details.

```java
mManager.getSlideLayout().setEnable(false);
```

Compatibility
-------------

 * **Minimum Android SDK**: SlideBack requires a minimum API level of 15.
 * **Compile Android SDK**: SlideBack requires you to compile against API 28 or later.
 * Both **AndroidX** and **Android Support** are supported using different dependencies.

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