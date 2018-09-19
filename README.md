# Reactive AIDL with example

This is a reactive implementation of AIDL with examples of how to use the lib

## What is AIDL
AIDL is Android Interface Definition Language, a component of the Android framework
that allows to separate apps (processes) to communicate with each other using a "contract" (interface).

![Screenshot](https://github.com/xiaogegexiao/rxaidl/blob/master/screenshots/screenshot_client1.png)
![Screenshot](https://github.com/xiaogegexiao/rxaidl/blob/master/screenshots/screenshot_client2.png)
![Screenshot](https://github.com/xiaogegexiao/rxaidl/blob/master/screenshots/screenshot_client3.png)
![Screenshot](https://github.com/xiaogegexiao/rxaidl/blob/master/screenshots/screenshot_client4.png)
![Screenshot](https://github.com/xiaogegexiao/rxaidl/blob/master/screenshots/screenshot_receiver.png)

## Using this example

To observe this example project work, you must first install the `receiver` module on your device
(it doesn't show any UI, it's just a `Service`). Once it's installed, install and run the `app` module
on your device. The `app` module will display UI that starts the service, binds with the service,
and uses methods declared in the service.

Note it also tells you how long it took to receive the entire response from the Service, AIDL is *very* fast
compared to other forms of IPC.

# Importing the RxAIDLObservable library
## Gradle
Add the ```JitPack``` repository to the root ```build.gradle``` file of your project
```groovy
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

Import the library in your app's module ```build.gradle```
```groovy
dependencies {
    ...
    implementation 'com.github.xiaogegexiao:rx-aidl:1.2'
}
```

##AIDL
```aidl
// IRandomNumberService.aidl
package com.xiao.aidlexamplereceiver;

// Declare any non-default types here with import statements

interface IRandomNumberService {
    int getRandomNumber(int range);
}

```

## Kotlin
To bind to a service thro AIDL 
```kotlin
private var mDisposable: Disposable? = null
val serviceIntent = Intent().setComponent(ComponentName(
                            "com.xiao.aidlexamplereceiver",
                            "com.xiao.aidlexamplereceiver.RandomNumberService"))
mDisposable?.dispose()
mDisposable = RxAIDLObservable<IRandomNumberService, IRandomNumberService.Stub>(this, serviceIntent, IRandomNumberService::class.java, IRandomNumberService.Stub::class.java)
        .compose(bindUntilEvent(ActivityEvent.PAUSE))
        .subscribe({
            log.append("onNext.\n")
            mRandomNumberService = it
            getRandomNumber()
        }, {
            log.append("onError ${it.message}.\n")
        }, {
            log.append("onComplete.\n")
        })
```

To unbind to a service 
```kotlin
mDisposable?.dispose()
```