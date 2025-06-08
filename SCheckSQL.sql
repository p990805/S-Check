-- 사용자 테이블
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255),
                       name VARCHAR(50) NOT NULL,
                       phone VARCHAR(20),
                       auth_type ENUM('email', 'oauth') DEFAULT 'email',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OAuth 계정 테이블
CREATE TABLE oauth_accounts (
                                oauth_id INT AUTO_INCREMENT PRIMARY KEY,
                                user_id INT NOT NULL,
                                provider VARCHAR(50) NOT NULL,
                                provider_id VARCHAR(255) NOT NULL,
                                access_token TEXT,
                                refresh_token TEXT,
                                token_expires_at TIMESTAMP NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                UNIQUE KEY unique_provider_id (provider, provider_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 스터디 그룹 테이블
CREATE TABLE study_groups (
                              group_id INT AUTO_INCREMENT PRIMARY KEY,
                              group_name VARCHAR(100) NOT NULL,
                              description TEXT,
                              leader_id INT NOT NULL,
                              invite_code VARCHAR(20) UNIQUE NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              is_active BOOLEAN DEFAULT TRUE,
                              FOREIGN KEY (leader_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 그룹 멤버 테이블
CREATE TABLE group_members (
                               member_id INT AUTO_INCREMENT PRIMARY KEY,
                               group_id INT NOT NULL,
                               user_id INT NOT NULL,
                               role ENUM('leader', 'member') DEFAULT 'member',
                               joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               is_active BOOLEAN DEFAULT TRUE,
                               FOREIGN KEY (group_id) REFERENCES study_groups(group_id) ON DELETE CASCADE,
                               FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                               UNIQUE KEY unique_group_user (group_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 과제 테이블
CREATE TABLE assignments (
                             assignment_id INT AUTO_INCREMENT PRIMARY KEY,
                             group_id INT NOT NULL,
                             created_by INT NOT NULL,
                             title VARCHAR(200) NOT NULL,
                             description TEXT,
                             due_date TIMESTAMP NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             is_active BOOLEAN DEFAULT TRUE,
                             FOREIGN KEY (group_id) REFERENCES study_groups(group_id) ON DELETE CASCADE,
                             FOREIGN KEY (created_by) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 과제 제출 테이블
CREATE TABLE assignment_submissions (
                                        submission_id INT AUTO_INCREMENT PRIMARY KEY,
                                        assignment_id INT NOT NULL,
                                        user_id INT NOT NULL,
                                        submission_content TEXT,
                                        submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        status ENUM('submitted', 'late', 'approved', 'rejected') DEFAULT 'submitted',
                                        FOREIGN KEY (assignment_id) REFERENCES assignments(assignment_id) ON DELETE CASCADE,
                                        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                        UNIQUE KEY unique_assignment_user (assignment_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 출석 테이블
CREATE TABLE attendance (
                            attendance_id INT AUTO_INCREMENT PRIMARY KEY,
                            group_id INT NOT NULL,
                            user_id INT NOT NULL,
                            attendance_date DATE NOT NULL,
                            status ENUM('present', 'absent', 'late', 'excused') DEFAULT 'present',
                            recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            notes TEXT,
                            FOREIGN KEY (group_id) REFERENCES study_groups(group_id) ON DELETE CASCADE,
                            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                            UNIQUE KEY unique_group_user_date (group_id, user_id, attendance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 공지사항 테이블
CREATE TABLE announcements (
                               announcement_id INT AUTO_INCREMENT PRIMARY KEY,
                               group_id INT NOT NULL,
                               created_by INT NOT NULL,
                               title VARCHAR(200) NOT NULL,
                               content TEXT NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               is_pinned BOOLEAN DEFAULT FALSE,
                               FOREIGN KEY (group_id) REFERENCES study_groups(group_id) ON DELETE CASCADE,
                               FOREIGN KEY (created_by) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 공유 파일 테이블
CREATE TABLE shared_files (
                              file_id INT AUTO_INCREMENT PRIMARY KEY,
                              group_id INT NOT NULL,
                              uploaded_by INT NOT NULL,
                              file_name VARCHAR(255) NOT NULL,
                              file_path VARCHAR(500) NOT NULL,
                              file_type VARCHAR(50),
                              file_size INT,
                              uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (group_id) REFERENCES study_groups(group_id) ON DELETE CASCADE,
                              FOREIGN KEY (uploaded_by) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 캘린더 이벤트 테이블
CREATE TABLE calendar_events (
                                 event_id INT AUTO_INCREMENT PRIMARY KEY,
                                 group_id INT NOT NULL,
                                 created_by INT NOT NULL,
                                 title VARCHAR(200) NOT NULL,
                                 description TEXT,
                                 start_time TIMESTAMP NOT NULL,
                                 end_time TIMESTAMP NOT NULL,
                                 event_type ENUM('meeting', 'assignment', 'exam', 'other') DEFAULT 'meeting',
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 FOREIGN KEY (group_id) REFERENCES study_groups(group_id) ON DELETE CASCADE,
                                 FOREIGN KEY (created_by) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 사용자 알림 테이블
CREATE TABLE user_notifications (
                                    notification_id INT AUTO_INCREMENT PRIMARY KEY,
                                    user_id INT NOT NULL,
                                    title VARCHAR(200) NOT NULL,
                                    message TEXT NOT NULL,
                                    type ENUM('general', 'assignment', 'attendance', 'announcement') DEFAULT 'general',
                                    is_read BOOLEAN DEFAULT FALSE,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 생성
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_auth_type ON users(auth_type);
CREATE INDEX idx_oauth_accounts_provider ON oauth_accounts(provider, provider_id);
CREATE INDEX idx_study_groups_invite_code ON study_groups(invite_code);
CREATE INDEX idx_study_groups_leader ON study_groups(leader_id);
CREATE INDEX idx_group_members_group_user ON group_members(group_id, user_id);
CREATE INDEX idx_assignments_group ON assignments(group_id);
CREATE INDEX idx_assignments_due_date ON assignments(due_date);
CREATE INDEX idx_assignment_submissions_assignment ON assignment_submissions(assignment_id);
CREATE INDEX idx_attendance_group_date ON attendance(group_id, attendance_date);
CREATE INDEX idx_attendance_user_date ON attendance(user_id, attendance_date);
CREATE INDEX idx_announcements_group ON announcements(group_id);
CREATE INDEX idx_shared_files_group ON shared_files(group_id);
CREATE INDEX idx_calendar_events_group ON calendar_events(group_id);
CREATE INDEX idx_calendar_events_time ON calendar_events(start_time, end_time);
CREATE INDEX idx_user_notifications_user ON user_notifications(user_id);
CREATE INDEX idx_user_notifications_is_read ON user_notifications(is_read);
