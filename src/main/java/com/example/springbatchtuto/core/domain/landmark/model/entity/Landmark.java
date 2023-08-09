package com.example.springbatchtuto.core.domain.landmark.model.entity;

import com.example.springbatchtuto.core.domain.landmark.model.type.Coordinate;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "landmarks",
        indexes = @Index(name = "landmark_geo_index", columnList = "lng, lat")
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Landmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    @Builder.Default
    private Long memberId = 0L;

    @NotNull
    private String name;

    @Embedded // 값 타입을 사용하는 곳에 표시
    @NotNull
    private Coordinate coordinate;

    public void changeOwner(Long memberId) {
        this.memberId = memberId;
    }
}
