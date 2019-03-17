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
    implementation 'com.github.utsavstha:adonis-websocket-client-android:1.1'
}
```

Usage:

1: Establish connection:
```java
String url = (This url is usually ws://(your ip or domain)/adonis-ws)
Socket socket = Socket.Builder.with(url).build();
socket.connect();
```

2: Socket Default Events:
```java
socket.onEvent(Socket.EVENT_OPEN, new Socket.OnEventListener() {
    @Override
    public void onMessage(String event) {
	output("sonnected");
    }
});
```

3: Socket Custom Events:
```java
socket.onEventResponse("chat", new Socket.OnEventResponseListener() {
    @Override
    public void onMessage(String event, String data) {
        System.out.println(data);
    }
});
```

4: Join Topic:
```java
socket.join("topic_name");
```

5: Leave Topic:
```java
socket.leave("topic_name");
```

6: Send Message: 
```java
JSONObject jsonObject = new JSONObject();
jsonObject.put("username", username);
socket.send("topic_name", jsonObject.toString()); //Yes this has to be json.
```

License
--------

    Copyright 2019 Utsav Shrestha

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
