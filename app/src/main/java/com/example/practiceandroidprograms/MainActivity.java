package com.example.practiceandroidprograms;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ApiService apiService;
    private Disposable disposable;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // پیدا کردن ویو TextView
        textView = findViewById(R.id.textView);

        // لاگ شروع اکتیویتی
        Log.d(TAG, "onCreate: Activity started");

        // تنظیم Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // اضافه کردن RxJava3CallAdapterFactory
                .build();

        // ایجاد ApiService
        apiService = retrofit.create(ApiService.class);

        // استفاده از RxJava برای فراخوانی API
        Observable<Post> postObservable = apiService.getPost(1)
                .subscribeOn(Schedulers.io()) // اجرا در نخ IO
                .observeOn(AndroidSchedulers.mainThread()); // نتیجه در نخ اصلی

        // ایجاد Observer
        disposable = postObservable.subscribeWith(new DisposableObserver<Post>() {
            @Override
            public void onNext(Post post) {
                // نمایش داده‌ها در TextView و لاگ‌گذاری عنوان پست
                Log.d(TAG, "onNext: Post title: " + post.getTitle());
                textView.setText(post.getTitle()); // نمایش عنوان پست در TextView
            }

            @Override
            public void onError(Throwable e) {
                // در صورت بروز خطا
                Log.e(TAG, "onError: Error occurred", e);
            }

            @Override
            public void onComplete() {
                // زمانی که عملیات تمام شد
                Log.d(TAG, "onComplete: Request completed");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // آزادسازی منابع برای جلوگیری از Memory Leak
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            Log.d(TAG, "onDestroy: Disposable disposed");
        }
    }
}
