# AIDL Example

This is a basic example of the Android Interface Definition Language, a component of the Android framework
that allows to separate apps (processes) to communicate with each other using a "contract" (interface).

![Screenshot](https://github.com/xiaogegexiao/AIDL_Example/blob/master/screenshots/screenshot_client1.png)
![Screenshot](https://github.com/xiaogegexiao/AIDL_Example/blob/master/screenshots/screenshot_client2.png)
![Screenshot](https://github.com/xiaogegexiao/AIDL_Example/blob/master/screenshots/screenshot_client3.png)
![Screenshot](https://github.com/xiaogegexiao/AIDL_Example/blob/master/screenshots/screenshot_client4.png)
![Screenshot](https://github.com/xiaogegexiao/AIDL_Example/blob/master/screenshots/screenshot_receiver.png)

### Using this example

To observe this example project work, you must first install the `receiver` module on your device
(it doesn't show any UI, it's just a `Service`). Once it's installed, install and run the `app` module
on your device. The `app` module will display UI that starts the service, binds with the service,
and uses methods declared in the service.

Note it also tells you how long it took to receive the entire response from the Service, AIDL is *very* fast
compared to other forms of IPC.

## About RxAIDLObservable

To use RxAIDLObservable
