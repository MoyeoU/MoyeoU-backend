CREATE TABLE IF NOT EXISTS answer
(
    id               BIGINT AUTO_INCREMENT,
    answer           VARCHAR(255),
    item_id          BIGINT,
    participation_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS category
(
    id          BIGINT AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comment
(
    id         BIGINT AUTO_INCREMENT,
    content    VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    member_id  BIGINT,
    post_id    BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS evaluation
(
    id            BIGINT AUTO_INCREMENT,
    content       VARCHAR(255),
    evaluated     BOOLEAN,
    point         DOUBLE,
    evaluatee_id  BIGINT,
    evaluator_id  BIGINT,
    post_id       BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS hashtag
(
    id          BIGINT AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    category_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS item
(
    id      BIGINT AUTO_INCREMENT,
    name    VARCHAR(255),
    post_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS member
(
    id               BIGINT AUTO_INCREMENT,
    department       VARCHAR(255) NOT NULL,
    email            VARCHAR(255) NOT NULL,
    evaluation_count INTEGER,
    image_path       VARCHAR(255),
    introduction     VARCHAR(255),
    nickname         VARCHAR(255) NOT NULL,
    password         VARCHAR(255) NOT NULL,
    point DOUBLE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS member_hashtag
(
    id                BIGINT AUTO_INCREMENT,
    hashtag_id        BIGINT,
    member_id         BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS notification
(
    id              BIGINT AUTO_INCREMENT,
    receiver_id     BIGINT       NOT NULL,
    type            VARCHAR(255) NOT NULL,
    member_id       BIGINT,
    post_id         BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS participation
(
    id               BIGINT AUTO_INCREMENT,
    participated_at  TIMESTAMP    NOT NULL,
    status           VARCHAR(255) NOT NULL,
    member_id        BIGINT,
    post_id          BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS post
(
    id                 BIGINT AUTO_INCREMENT,
    content            VARCHAR(255) NOT NULL,
    created_at         DATE         NOT NULL,
    current_count      INTEGER      NOT NULL,
    estimated_duration VARCHAR(255) NOT NULL,
    expected_date      VARCHAR(255) NOT NULL,
    head_count         INTEGER      NOT NULL,
    operation_way      VARCHAR(255) NOT NULL,
    status             VARCHAR(255) NOT NULL,
    title              VARCHAR(255) NOT NULL,
    member_id          BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS post_hashtag
(
    id              BIGINT AUTO_INCREMENT,
    hashtag_id      BIGINT,
    post_id         BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS refresh_token
(
    id            BIGINT AUTO_INCREMENT,
    refresh_token VARCHAR(255),
    PRIMARY KEY (id)
);
