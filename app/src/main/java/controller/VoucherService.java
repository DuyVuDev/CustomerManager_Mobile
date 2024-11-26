package controller;

import java.util.List;

import model.dto.requests.RequestVoucherDTO;
import model.dto.responses.ResponseRankingDTO;
import model.dto.responses.ResponseVoucherDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface VoucherService {

    @GET("vouchers")
    Call<List<ResponseVoucherDTO>> getAllVouchers();

    @POST("customer_voucher/publish")
    Call<Void> publishVoucher(@Body List<RequestVoucherDTO> requestVoucherDTOs);

    @PUT("customers/randomScore")
    Call<Void> generateScore();
}
