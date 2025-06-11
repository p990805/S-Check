package com.scheck.scheck.entity.studyGroup;

import com.scheck.scheck.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "study_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "group_name", nullable = false, length =100)
    private String groupName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id",nullable = false)
    private User leader;

    @Column(name = "attendance_time")
    private LocalTime attendanceTime;

    @Column(name = "attendance_days", length = 20)
    private String attendanceDays;

    @Column(name = "is_auto_approve", nullable = false)
    private Boolean isAutoApprove = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public StudyGroup(String groupName, String description, User leader,
                      LocalTime attendanceTime, String attendanceDays, Boolean isAutoApprove) {
        this.groupName = groupName;
        this.description = description;
        this.leader = leader;
        this.attendanceTime = attendanceTime;
        this.attendanceDays = attendanceDays;
        this.isAutoApprove = isAutoApprove != null ? isAutoApprove : false;
    }

    public void updateGroupInfo(String groupName, String description,
                                LocalTime attendanceTime, String attendanceDays) {
        if (groupName != null && !groupName.trim().isEmpty()) {
            this.groupName = groupName;
        }
        if (description != null) {
            this.description = description;
        }
        if (attendanceTime != null) {
            this.attendanceTime = attendanceTime;
        }
        if (attendanceDays != null) {
            this.attendanceDays = attendanceDays;
        }

    }
    public void changeAutoApprove(Boolean isAutoApprove) {
        this.isAutoApprove = isAutoApprove;
    }

    public boolean isLeader(User user) {
        return this.leader.getUserId().equals(user.getUserId());
    }

}
