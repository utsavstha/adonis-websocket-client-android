# adonis-websocket-client-android
A library created for android to work with WebSockets in Adonis JS.


[Adonis WebSockets](https://adonisjs.com/docs/4.1/websocket)

The Status of the lib: 
[![Release](https://jitpack.io/v/utsavstha/adonis-websocket-client-android.svg)](https://jitpack.io/#utsavstha/adonis-websocket-client-android/1.1)

How to use this lib in your project:
```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

Add to your app module build.gradle
```gradle
dependencies {
        compile 'com.github.zurche:my-cool-lib:v0.1'
}
```

What you can do with this lib:
```javascript
Point buenosAiresObeliscoPoint = new Point((float) -34.6037389, (float) -58.3815704);
        
Point nycStatueOfLibertyPoint = new Point((float) 40.6892494, (float) -74.0445004);

float distanceBetweenPoints = LatLonDistanceCalculator.calculateDistance(
    buenosAiresObeliscoPoint, 
    nycStatueOfLibertyPoint);
```

<p align="center">
  <img src="https://github.com/zurche/my-cool-lib/blob/master/device-2016-06-03-131119.png" alt="Example"/>
</p>

License
--------

    Copyright 2016 Alejandro Zürcher

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
