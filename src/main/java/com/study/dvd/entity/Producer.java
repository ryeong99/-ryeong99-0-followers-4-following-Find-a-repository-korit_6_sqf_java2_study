package com.study.dvd.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Producer {
    private int producerId;
    private String producerName;

}
