package com.backend.devx.global;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class) //이벤트를 처리하는 리스너 생명주기 이벤트(예: 생성, 수정, 삭제)
@MappedSuperclass //공통 매핑정보를 제공하기 위해 사용되는 JPA 애노테이션, 부모 클래스에 매핑정보를 정의, 이를 하위 클래스에서 상속받아 재사용
@Getter
public class BaseEntity {
    @CreatedDate //엔티티 생성될 때 시간
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @LastModifiedDate //엔티티가 수정될 때 시간
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

//    예를 들어, 사용자가 새로운 항목을 생성하면 CreatedBy 필드는 해당 사용자의 ID로 설정
//    나중에 항목이 수정될 때 LastModifiedBy 필드는 수정을 수행한 사용자의 ID로 업데이트
    @CreatedBy //생성한 사람 추적 / 엔티티 생성한 사용자를 추적
    @Column(name = "created_by")
    private Long createdBy;

    @LastModifiedBy //엔티티 마지막으로 수정된 사용자를 추적
    @Column(name = "updated_by")
    private Long updateBy;


}
