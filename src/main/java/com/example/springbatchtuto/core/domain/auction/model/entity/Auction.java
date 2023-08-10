package com.example.springbatchtuto.core.domain.auction.model.entity;

import com.example.springbatchtuto.core.domain.landmark.model.entity.Landmark;
import com.example.springbatchtuto.core.domain.type.AuctionId;
import com.sun.istack.NotNull;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "auctions",
        indexes = @Index(name = "auction_finished_index", columnList = "finished")
)
@EntityListeners(AuditingEntityListener.class)
@IdClass(AuctionId.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Auction {

    @Id
    @CreatedDate
    @Column(name = "created_date", columnDefinition = "DATE", updatable = false)
    private LocalDate createdDate;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landmark_id", updatable = false)
    private Landmark landmark;

    @NotNull
    @Builder.Default
    private Boolean finished = Boolean.FALSE;

    @Column(name = "last_log_id")
    private Long lastLogId;

    public void changeLastLogId(Long lastLogId) {
        this.lastLogId = lastLogId;
    }

}
