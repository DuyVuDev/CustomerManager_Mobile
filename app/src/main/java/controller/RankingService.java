package controller;

import java.util.List;

import model.dto.requests.RequestRankingDTO;
import model.dto.responses.ResponseRankingDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RankingService {
    @GET("ranks")
    Call<List<ResponseRankingDTO>> getAllRankings();

    @GET("ranks/{id}")
    Call<ResponseRankingDTO> getRankingById(@Path("id") Long id);

    @POST("ranks")
    Call<Void> createRanking(@Body RequestRankingDTO requestRankingDTO);

    @PUT("ranks/{id}")
    Call<Void> updateRanking(@Path("id") Long id,@Body RequestRankingDTO requestRankingDTO);

    @DELETE("ranks/{id}")
    Call<Void> deleteRankingById(@Path("id") Long id);

    @HTTP(method = "DELETE", path = "/ranks/deleteMultiple", hasBody = true)
    Call<Void> deleteAllRankingsByIds(@Body List<Long> ids);
}
