CREATE TABLE IF NOT EXISTS `albums` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `title` varchar(120) NOT NULL COMMENT '앨범 이름',
    `created_by` varchar(40) NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_by` varchar(40) NOT NULL,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='앨범';

CREATE TABLE IF NOT EXISTS `songs` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `title` varchar(120) NOT NULL COMMENT '노래 제목',
    `album_id` bigint(20) NOT NULL,
    `created_by` varchar(40) NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_by` varchar(40) NOT NULL,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='노래';
