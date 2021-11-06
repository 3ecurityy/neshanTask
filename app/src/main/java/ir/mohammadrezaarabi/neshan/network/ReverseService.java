package ir.mohammadrezaarabi.neshan.network;


import ir.mohammadrezaarabi.neshan.model.NeshanAddress;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ReverseService {
    @Headers("Api-Key: service.kREahwU7lND32ygT9ZgPFXbwjzzKukdObRZsnUAJ")
    @GET("/v2/reverse")
    Call<NeshanAddress> getReverse(@Query("lat") Double lat, @Query("lng") Double lng);
}
