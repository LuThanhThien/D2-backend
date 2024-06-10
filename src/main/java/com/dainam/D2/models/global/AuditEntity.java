package com.dainam.D2.models.global;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@RequiredArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditEntity {

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(
            name = "created_datetime",
            nullable = false,
            updatable = false
    )
    protected LocalDateTime createdDatetime = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(
            name = "last_modified_datetime",
            insertable = false
    )
    protected LocalDateTime lastModifiedDatetime;


    @CreatedBy
    @Column(
            name = "created_by",
            updatable = false
    )
    protected String createdBy;

    @LastModifiedBy
    @Column(
            name = "last_modified_by",
            insertable = false
    )
    protected String lastModifiedBy;

    @Column(name = "data_status")
    @Builder.Default
    protected DataStatus dataStatus = DataStatus.ACTIVE;
}
