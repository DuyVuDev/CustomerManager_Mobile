package model.dto.responses;

public class ResponseVoucherDTO {
    private Long id;
    private Float discount;

    public ResponseVoucherDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }
}
