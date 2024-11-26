package model.dto.requests;

public class RequestVoucherDTO {
    Long rank_id;
    Float discount;

    public RequestVoucherDTO() {
    }

    public Long getRank_id() {
        return rank_id;
    }

    public void setRank_id(Long rank_id) {
        this.rank_id = rank_id;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }
}
