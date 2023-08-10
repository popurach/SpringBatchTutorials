package com.example.springbatchtuto.core.domain.type;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class BaseDateTime {

    @CreatedDate
    @Column(name = "created_date_time", columnDefinition = "DATETIME", updatable = false)
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @Column(name = "modified_date_time", columnDefinition = "DATETIME")
    private LocalDateTime modifiedDateTime;

}
