package alexdevyatov.com.tinkofftask.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Алексей on 09.01.2018.
 */

public class Response {

    private String resultCode;
    @SerializedName("payload")
    private List<Payload> payloads;
    private Long trackingId;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<Payload> getPayloads() {
        return payloads;
    }

    public void setPayloads(List<Payload> payloads) {
        this.payloads = payloads;
    }

    public Long getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(Long trackingId) {
        this.trackingId = trackingId;
    }
}
