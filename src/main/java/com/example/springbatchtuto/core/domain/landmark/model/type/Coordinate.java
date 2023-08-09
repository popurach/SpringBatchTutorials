package com.example.springbatchtuto.core.domain.landmark.model.type;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable // 값 타입을 정의하는 곳에 표시 (하나의 객체로 묶어 객체지향적으로 설계)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Coordinate {

    @NotNull
    private Double lng; // 경도

    @NotNull
    private Double lat; // 위도

}
