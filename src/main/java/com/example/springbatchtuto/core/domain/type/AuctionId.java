package com.example.springbatchtuto.core.domain.type;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class AuctionId implements Serializable {

    private LocalDate createdDate;

    private Long landmark;

}
