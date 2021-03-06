CREATE TABLE `seckill_user` (
`id` BIGINT ( 20 ) NOT NULL COMMENT '用户ID,手机号',
`nickname` VARCHAR ( 255 ) NOT NULL,
`password` VARCHAR ( 32 ) DEFAULT NULL COMMENT 'MD5(MD5(pass明文+固定salt) + salt)',
`salt` VARCHAR ( 10 ) DEFAULT NULL,
`head` VARCHAR ( 128 ) DEFAULT NULL COMMENT '头像，云存储时的ID',
`register_date` datetime DEFAULT NULL COMMENT '注册时间',
`last_login_date` datetime DEFAULT NULL COMMENT '上次登录时间',
`login_count` INT ( 11 ) DEFAULT '0' COMMENT '上次登录时间',
PRIMARY KEY ( `id` )
) ENGINE = INNODB DEFAULT charset = utf8mb4;