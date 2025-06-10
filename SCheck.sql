-- 스터디 앱 데이터베이스 생성
CREATE DATABASE study_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE study_app;

-- 사용자 테이블
CREATE TABLE user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    kakao_id VARCHAR(50) NOT NULL UNIQUE,
    nickname VARCHAR(100) NOT NULL,
    profile_image_url TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 스터디 그룹 테이블
CREATE TABLE study_group (
    group_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_name VARCHAR(100) NOT NULL,
    description TEXT,
    leader_id BIGINT NOT NULL,
    attendance_time TIME,
    attendance_days VARCHAR(20), -- "1,2,3,4,5" 형태로 저장 (월~금)
    is_auto_approve BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (leader_id) REFERENCES user (user_id) ON DELETE CASCADE
);

-- 가입 신청 테이블
CREATE TABLE join_request (
    request_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status ENUM(
        'PENDING',
        'APPROVED',
        'REJECTED'
    ) DEFAULT 'PENDING',
    message TEXT,
    requested_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME NULL,
    processed_by BIGINT NULL,
    reject_reason TEXT,
    FOREIGN KEY (group_id) REFERENCES study_group (group_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user (user_id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES user (user_id) ON DELETE SET NULL,
    UNIQUE KEY unique_pending_request (group_id, user_id, status)
);

-- 그룹 멤버 테이블
CREATE TABLE group_member (
    member_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status ENUM('ACTIVE', 'KICKED', 'LEFT') DEFAULT 'ACTIVE',
    applied_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    approved_at DATETIME NULL,
    approved_by BIGINT NULL,
    reject_reason TEXT,
    FOREIGN KEY (group_id) REFERENCES study_group (group_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user (user_id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES user (user_id) ON DELETE SET NULL,
    UNIQUE KEY unique_active_member (group_id, user_id)
);

-- 출석 테이블
CREATE TABLE attendance (
    attendance_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM(
        'PRESENT',
        'LATE',
        'ABSENT',
        'SUBMITTED'
    ) DEFAULT 'ABSENT',
    checked_at DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES study_group (group_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user (user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_daily_attendance (
        group_id,
        user_id,
        attendance_date
    )
);

-- 게시글 테이블
CREATE TABLE post (
    post_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES study_group (group_id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES user (user_id) ON DELETE CASCADE
);

-- 게시글 첨부파일 테이블
CREATE TABLE post_file (
    file_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    file_path TEXT NOT NULL,
    file_size BIGINT NOT NULL,
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES post (post_id) ON DELETE CASCADE
);

-- 인덱스 생성
CREATE INDEX idx_user_kakao_id ON user (kakao_id);

CREATE INDEX idx_study_group_leader ON study_group (leader_id);

CREATE INDEX idx_join_request_group_user ON join_request (group_id, user_id);

CREATE INDEX idx_join_request_status ON join_request (status);

CREATE INDEX idx_group_member_group ON group_member (group_id);

CREATE INDEX idx_group_member_user ON group_member (user_id);

CREATE INDEX idx_attendance_group_date ON attendance (group_id, attendance_date);

CREATE INDEX idx_attendance_user_date ON attendance (user_id, attendance_date);

CREATE INDEX idx_post_group ON post (group_id);

CREATE INDEX idx_post_author ON post (author_id);

CREATE INDEX idx_post_created ON post (created_at);