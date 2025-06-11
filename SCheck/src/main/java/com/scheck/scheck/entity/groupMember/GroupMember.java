package com.scheck.scheck.entity.groupMember;

import com.scheck.scheck.entity.studyGroup.StudyGroup;
import com.scheck.scheck.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_member",
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup studyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "applied_at", updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    @Builder
    public GroupMember(StudyGroup studyGroup, User user, User approvedBy) {
        this.studyGroup = studyGroup;
        this.user = user;
        this.approvedBy = approvedBy;
        this.status = MemberStatus.ACTIVE;
        this.approvedAt = LocalDateTime.now();
    }

    // 비즈니스 메서드
    public void kickOut(String reason) {
        this.status = MemberStatus.KICKED;
        this.rejectReason = reason;
    }

    public void leave() {
        this.status = MemberStatus.LEFT;
    }

    public void reactivate() {
        if (this.status == MemberStatus.LEFT) {
            this.status = MemberStatus.ACTIVE;
        } else {
            throw new IllegalStateException("탈퇴한 멤버만 재활성화할 수 있습니다.");
        }
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }

    public boolean isLeader() {
        return this.studyGroup.getLeader().getUserId().equals(this.user.getUserId());
    }

    public enum MemberStatus {
        ACTIVE, KICKED, LEFT
    }
}