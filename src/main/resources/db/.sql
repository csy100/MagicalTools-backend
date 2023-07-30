USE MAGICAL_TOOLS;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`
(
    `id`          bigint(20) UNSIGNED                                           NOT NULL AUTO_INCREMENT COMMENT '主键',
    `email`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号码',
    `password`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT '' COMMENT '密码，加密存储',
    `nick_name`   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT '' COMMENT '昵称，默认是用户id',
    `icon`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT '' COMMENT '人物头像',
    `create_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `unique_key_email` (`email`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1010
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Compact;

INSERT INTO `tb_user` (`id`, `email`, `password`, `nick_name`, `icon`, `create_time`, `update_time`)
VALUES (1010, 'example@example.com', 'password123', 'JohnDoe', 'avatar.jpg', NOW(), NOW());

-- ----------------------------
-- Table structure for tb_session
-- ----------------------------
DROP TABLE IF EXISTS `tb_session`;
CREATE TABLE `tb_session`
(
    `id`            bigint(20) UNSIGNED                                           NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`       bigint(20) UNSIGNED                                           NOT NULL COMMENT '用户索引',
    `session_id`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话索引',
    `session_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话标题',
    `create_time`   timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `unique_key_session_id` (`session_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Compact;

INSERT INTO `tb_session` (`user_id`, `session_id`, `session_title`, `create_time`, `update_time`)
VALUES ('1010', '1010', 'test', NOW(), NOW());

INSERT INTO `tb_session` (`user_id`, `session_id`, `session_title`, `create_time`, `update_time`)
VALUES ('1010', '1010', 'session 1', NOW(), NOW());

INSERT INTO `tb_session` (`user_id`, `session_id`, `session_title`, `create_time`, `update_time`)
VALUES ('1010', '1010', 'session 2', NOW(), NOW());

INSERT INTO `tb_session` (`user_id`, `session_id`, `session_title`, `create_time`, `update_time`)
VALUES ('1010', '1010', 'session 3', NOW(), NOW());


-- ----------------------------
-- Table structure for tb_chat
-- ----------------------------
DROP TABLE IF EXISTS `tb_chat`;
CREATE TABLE `tb_chat`
(
    `id`           bigint(20) UNSIGNED                                           NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`      bigint(20) UNSIGNED                                           NOT NULL COMMENT '用户索引',
    `session_id`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话索引',
    `chat_type`    tinyint(5)                                                    NOT NULL COMMENT '回答类型,用户(0),gpt(1)',
    `chat_content` text                                                          NOT NULL COMMENT '聊天内容',
    `create_time`  timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `unique_key_id` (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Compact;

INSERT INTO tb_chat (user_id, session_id, chat_type, chat_content)
VALUES (1010, '1010', 0, '用户发言1'),
       (1010, '1010', 1, 'GPT回答1'),
       (1010, '1010', 0, '用户发言2'),
       (1010, '1010', 1, 'GPT回答2'),
       (1010, '1010', 0, '用户发言1'),
       (1010, '1010', 1, 'GPT回答1'),
       (1010, '1010', 0, '用户发言2'),
       (1010, '1010', 1, 'GPT回答2');