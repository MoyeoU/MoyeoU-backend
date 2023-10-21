insert into category(id, name) VALUES (1, '어학') ON DUPLICATE KEY UPDATE id = id;
insert into category(id, name) VALUES (2, '프로그래밍') ON DUPLICATE KEY UPDATE id = id;
insert into category(id, name) VALUES (3, '팀프로젝트') ON DUPLICATE KEY UPDATE id = id;
insert into category(id, name) VALUES (4, '자격증') ON DUPLICATE KEY UPDATE id = id;
insert into category(id, name) VALUES (5, '취미/교양') ON DUPLICATE KEY UPDATE id = id;
insert into category(id, name) VALUES (6, '고시/공무원') ON DUPLICATE KEY UPDATE id = id;
insert into category(id, name) VALUES (7, '기타') ON DUPLICATE KEY UPDATE id = id;

insert into hashtag (id, name, category_id) values (1, '토익', 1) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (2, '토익 스피킹', 1) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (3, '토플', 1) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (4, '일본어', 1) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (5, '중국어', 1) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (6, '한자', 1) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (7, '영어회화', 1) ON DUPLICATE KEY UPDATE id = id;

insert into hashtag (id, name, category_id) values (8, '프론트엔드', 2) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (9, '백엔드', 2) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (10, '코딩테스트', 2) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (11, '모바일', 2) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (12, '보안/네트워크', 2) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (13, '게임', 2) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (14, '하드웨어', 2) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (15, '데이터/AI', 2) ON DUPLICATE KEY UPDATE id = id;

insert into hashtag (id, name, category_id) values (16, '공과대학', 3) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (17, '사회과학대학', 3) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (18, '인문대학', 3) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (19, '자연과학대학', 3) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (20, '경상대학', 3) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (21, '약학대학', 3) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (22, '농업생명과학대학', 3) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (23, '간호대학', 3) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (24, '사범대학', 3) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (25, '생활과학대학', 3) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (26, '예술대학', 3) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (27, '수의과대학', 3) ON DUPLICATE KEY UPDATE id = id;

insert into hashtag (id, name, category_id) values (28, '컴활', 4) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (29, '정보처리기사', 4) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (30, '전기기사', 4) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (31, '건축기사', 4) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (32, '조리기능사', 4) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (33, '한능검', 4) ON DUPLICATE KEY UPDATE id = id;

insert into hashtag (id, name, category_id) values (34, '독서', 5) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (35, '음악', 5) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (36, '그림', 5) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (37, '운동', 5) ON DUPLICATE KEY UPDATE id = id;

insert into hashtag (id, name, category_id) values (38, '임용고시', 6) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (39, '간호사', 6) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (40, '의사', 6) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (41, '행정고시', 6) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (42, '외무고시', 6) ON DUPLICATE KEY UPDATE id = id;
insert into hashtag (id, name, category_id) values (43, '공무원', 6) ON DUPLICATE KEY UPDATE id = id;
