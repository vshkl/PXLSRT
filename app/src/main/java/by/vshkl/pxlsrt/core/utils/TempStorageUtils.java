package by.vshkl.pxlsrt.core.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class TempStorageUtils {

    public static Observable<String> saveTempBitmap(final FileOutputStream fos, final String fname, final byte[] data) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getWidth());
                storeFile(fos, bitmap)
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    emitter.onNext(fname);
                                }
                            }
                        });
            }
        });
    }

    public static Observable<String> saveTempBitmap(final FileOutputStream fos, final String fname, final Uri uri) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getWidth());
                storeFile(fos, bitmap)
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    emitter.onNext(fname);
                                }
                            }
                        });
            }
        });
    }

    public static Observable<Boolean> saveResultPicture(final FileOutputStream fos, final Bitmap bitmap) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> emitter) throws Exception {
                storeFile(fos, bitmap)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(new Function<Throwable, Boolean>() {
                            @Override
                            public Boolean apply(Throwable throwable) throws Exception {
                                emitter.onNext(false);
                                return null;
                            }
                        })
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    emitter.onNext(true);
                                }
                            }
                        });
            }
        });
    }

    public static Observable<Bitmap> getTempBitmap(final FileInputStream fis) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                try {
                    fis.close();
                    emitter.onNext(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        });
    }

    static Observable<Boolean> storeFile(final FileOutputStream fos, final Bitmap bitmap) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                bitmap.recycle();
                try {
                    fos.flush();
                    fos.close();
                    emitter.onNext(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        });
    }
}
