package com.pro.firebasepro.nearbyplace;

import retrofit2.http.GET;
import retrofit2.http.Url;
import retrofit2.Call;

public interface NearByPlaceService {
    @GET
    Call<NearByPlacesResponse>getNearByPlacesResponse(@Url String endUrl);
}
