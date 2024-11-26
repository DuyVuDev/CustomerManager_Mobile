package controller;

import java.util.List;

import model.dto.requests.RequestCustomerDTO;
import model.dto.responses.ResponseCustomerDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.DELETE;
import retrofit2.http.Query;

public interface CustomerService {

    // Lấy danh sách khách hàng
    @GET("customers")
    Call<List<ResponseCustomerDTO>> getAllCustomers();

    @GET("customers/filter")
    Call<List<ResponseCustomerDTO>> filterAndSortCustomers(
            @Query("rankName") String rankName,
            @Query("discount") String discount,
            @Query("sortBy") String sortBy,
            @Query("sortOrder") String sortOrder);

    @GET("customers/{id}")
    Call<ResponseCustomerDTO> getCustomerById(@Path("id") String id);

    // Thêm khách hàng mới
    @POST("customers")
    Call<Void> createCustomer(@Body RequestCustomerDTO requestCustomerDTO);

    // Cập nhật thông tin khách hàng
    @PUT("customers/{id}")
    Call<Void> updateCustomer(@Path("id") String id, @Body RequestCustomerDTO requestCustomerDTO);

    // Xóa khách hàng
    @DELETE("customers/{id}")
    Call<Void> deleteCustomer(@Path("id") String id);

    @HTTP(method = "DELETE", path = "/customers/deleteMultiple", hasBody = true)
    Call<Void> deleteCustomersByIds(@Body List<String> selectedCustomerIds);
}
