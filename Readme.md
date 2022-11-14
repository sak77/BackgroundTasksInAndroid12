Android 12 introduced more restrictions when it comes to background tasks. It affects the 
behavior of services and also how to work with these restrictions. 

The purpose of this app is to study these changes and also how it impacts services, BR, 
Workmanager and Activities.

Here an Activity starts a service and immediately calls finish() [To go into background]. 
The started service registers a BR and later broadcasts an intent to it. In BR's onReceive 
callback, I try to perform different operations:
- start an Activity
- start service
- bind to a service
- start foreground service
- enqueue work request
- enqueue expedited work request (which can be a long running task)

When the service is connected, it calls some function in it.

BR - static and dynamic registered receivers 
- differences in setup (manifest vs context.registerReceiver) 
- sending a broadcast (component name required in intent for manifest registered receiver)
- Context registered receiver can bind to a service. But same is difficult if done for
Manifest registered receiver.
https://medium.com/@debuggingisfun/android-o-work-around-background-service-limitation-e697b2192bc3
- Manifest registered BR cannot bind to a service since the system does not allow to perform
long running background work (more than 10 secs) in onReceive of a BR:
  https://developer.android.com/guide/components/broadcasts#effects-process-state
  https://developer.android.com/develop/ui/views/appwidgets/advanced#broadcastreceiver-considerations
- BR can start a regular service.
- BR which receives system broadcasts intents with following actions, can launch activity from 
background -
  ACTION_BOOT_COMPLETED, ACTION_LOCKED_BOOT_COMPLETED, or ACTION_MY_PACKAGE_REPLACED
  ACTION_TIMEZONE_CHANGED, ACTION_TIME_CHANGED, or ACTION_LOCALE_CHANGED

Coroutines - 
- launch can only be invoked on coroutineScope instance and not as a static method. 
- cancel is cooperative. Cancel scope and job. Use isAlive to track if coroutine is cancelled.  
- LifecycleAware coroutines- LifecycleScope is provided by LifecycleService. The coroutine 
is automatically cancelled when the service is destroyed.
  https://developer.android.com/topic/libraries/architecture/coroutines

Activity - 
- onDestroy is not guaranteed to be called. So do all closing work in onPause or onStop 
- To start an Activity outside an Activity requires to set Intent.FLAG_ACTIVITY_NEW_TASK on 
the intent.
- It is not possible to start an Activity from a background task. However, there are some 
exceptions to this rule. For eg. Background task which launches the activity was called from 
an app which recently had a visible foreground Activity, or was recently closed by calling 
the finish() method. In this app, it is observed that for a delay of 5 secs after finishing 
the MainActivity, if the BR tries to start an Activity from background, it works. However 
if the delay is more, like 50 secs. Then system does not allow to start the Activity.

StartService - Generally start service or other background components cannot be used to launch 
activity:
ActivityTaskManager     system_process                       W  Background activity start 
[callingPackage: com.saket.mysampleservice; callingUid: 1010166; appSwitchAllowed: true; 
isCallingUidForeground: false; callingUidHasAnyVisibleWindow: false; callingUidProcState: SERVICE; 
isCallingUidPersistentSystemProcess: false; realCallingUid: 1010166; 
isRealCallingUidForeground: false; realCallingUidHasAnyVisibleWindow: false; 
realCallingUidProcState: SERVICE; isRealCallingUidPersistentSystemProcess: false; 
originatingPendingIntent: null; allowBackgroundActivityStart: false; 
intent: Intent { flg=0x10000000 cmp=com.saket.mysampleservice/.DummyActivity }; 
callerApp: ProcessRecord{76d3eaa 13655:com.saket.mysampleservice/u10a166}; 
inVisibleTask: false]
ActivityTaskManager     system_process                       E  Abort background activity starts 
from 1010166

However, there are some exceptions to this rule-
https://developer.android.com/guide/components/activities/background-starts

StartService - setup in onCreate, do work in onStartCommand, tear-down in onDestroy
By default Service runs on the main thread. So one can use Coroutines to perform long running tasks on another thread.
StartService can register a BR and also send broadcasts.

BoundService - a service which returns Binder instance from onBind method. 
It has higher priority than started service when Android System tries to kill background service. 
So it is less likely to get killed by the system compared to regular service... 
unbind should be called on service to invoke its onunbound callback...

Workmanagers -
WorkManager is a scheduling wrapper around other APIs.  
Under the hood, APIS like AlarmManager or FGS do the actual work.
Its purpose is to guarantee execution of the task when it is best suited for the system. 
One can also provide constraints to help system to schedule the task. 
One can also use setExpedited to ask system that the task be executed asap. 
Each app has a quota of time it can execute foreground work. So calling setExpedited 
will work until the app has not used up its quota. Or system can terminate the work or 
enqueue it as a regular work. For more details refer -
https://developer.android.com/topic/libraries/architecture/workmanager/how-to/define-work#quotas

By default WorkManager can execute background tasks for up to 10 mins..before system 
can reduce its priority. 
For long running background task, one has to use CoroutineWorker to define work and invoke 
setForegound() and pass it notification which it uses to launch a FGS. 
So trying to start a long running Work from background task gives same FGS exception. As 
under the hood WorkManager uses FGS.
https://developer.android.com/topic/libraries/architecture/workmanager/advanced/long-running

Exceptions to launch activity/FGS from background task...
In addition to what is mentioned above, when i add this permission in manifest -
<uses-permission android:name="android.permission.START_ACTIVITIES_FROM_BACKGROUND"/>
The app is able to launch FGS from background. But the doc states that this is a privileged 
permission only for System apps. So maybe it should be used sparingly.  
For more details -
https://developer.android.com/guide/components/foreground-services#background-start-restrictions
https://developer.android.com/guide/components/activities/background-starts