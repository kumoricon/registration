package org.kumoricon.registration.model.order;

import java.time.OffsetDateTime;

public class OrderHandOffDTO {
    private final int orderId;
    private final int fromUserId;
    private final String fromUserName;
    private final OffsetDateTime timestamp;
    private final String stage;

    public OrderHandOffDTO(int orderId, int fromUserId, String fromUserName, OffsetDateTime timestamp, String stage) {
        this.orderId = orderId;
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
        this.timestamp = timestamp;
        this.stage = stage;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public String getStage() { return stage; }
}
