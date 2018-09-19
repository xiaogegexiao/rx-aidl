package test.xiao.rxaidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

public class RxAIDLObservable<SERVICE extends IInterface, STUB extends Binder & IInterface> extends Observable<SERVICE> {

    private SERVICE mService;
    private Class<SERVICE> serviceClazz;
    private Class<STUB> stubClazz;
    private ContextWrapper contextWrapper;
    private Intent serviceIntent;
    private boolean serviceBound = false;
    public RxAIDLObservable(
            @NonNull ContextWrapper contextWrapper,
            @NonNull Intent intent,
            @NonNull Class<SERVICE> serviceClass,
            @NonNull Class<STUB> stubClass) {
        this.contextWrapper = contextWrapper;
        serviceIntent = intent;
        serviceClazz = serviceClass;
        stubClazz = stubClass;
    }

    @Override
    protected void subscribeActual(Observer<? super SERVICE> observer) {
        AIDLListener listener = new AIDLListener(observer);
        serviceBound = contextWrapper.bindService(serviceIntent, listener, Context.BIND_AUTO_CREATE);
        observer.onSubscribe(listener);
        if (!serviceBound) {
            observer.onError(new ServiceBoundFailureException("Service bound failure"));
        }
    }

    private final class AIDLListener extends MainThreadDisposable implements ServiceConnection {
        private final Observer<? super SERVICE> observer;
        public AIDLListener(Observer<? super SERVICE> observer) {
            this.observer = observer;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("AIDL", "Service binded!\n");
            // Obtain the class object if we know the name of the class
            Exception exception;
            try {
                Method asInterfaceMethod = stubClazz.getMethod("asInterface", IBinder.class);
                mService = (SERVICE) asInterfaceMethod.invoke(null, service);
                if (!isDisposed()) {
                    observer.onNext(mService);
                }
                return;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                exception = e;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                exception = e;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                exception = e;
            }
            unBind();

            observer.onError(exception);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // This method is only invoked when the service quits from the other end or gets killed
            Log.d("AIDL", "Service disconnected.\n");
            unBind();
            if (isDisposed()) {
                observer.onComplete();
            }
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Log.d("AIDL", "Binding died.\n");
            unBind();
            if (isDisposed()) {
                observer.onComplete();
            }
        }

        @Override
        protected void onDispose() {
            Log.d("AIDL", "Binding disposed.\n");
            unBind();
        }

        private void unBind() {
            if (serviceBound) {
                contextWrapper.unbindService(this);
                serviceBound = false;
            }
            mService = null;
        }
    }
}
