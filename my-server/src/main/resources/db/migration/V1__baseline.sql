-- Flyway 도입 시점의 베이스라인. 로컬 개발 DB(myserver_dev)가 비어 있는
-- 상태에서 이 스크립트로 직접 스키마를 생성했고, Hibernate ddl-auto=validate로
-- 엔티티와 스키마가 일치함을 확인했다 (posts 테이블의 금액 컬럼 precision,
-- article.url UNIQUE 인덱스 prefix length 포함).

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    login_id VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_login_id (login_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE posts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    username VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    last_modified_at DATETIME(6),
    file_path VARCHAR(255),
    user_id BIGINT,
    type VARCHAR(255) NOT NULL,
    ticker VARCHAR(255),
    position VARCHAR(255),
    entry_price DECIMAL(19,2),
    exit_price DECIMAL(19,2),
    profit_rate DECIMAL(19,2),
    exchange VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE article (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    url VARCHAR(2000) NOT NULL,
    source VARCHAR(255),
    summary VARCHAR(500),
    korean_summary VARCHAR(2000),
    published_at DATETIME(6),
    fetched_at DATETIME(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_article_url (url(768))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE holding (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    coin VARCHAR(20) NOT NULL,
    avg_price DECIMAL(20,8) NOT NULL,
    quantity DECIMAL(20,8) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_holding_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
