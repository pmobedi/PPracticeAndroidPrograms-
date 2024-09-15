package com.example.practiceandroidprograms;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("posts/{id}")
    Observable<Post> getPost(@Path("id") int postId);
}
