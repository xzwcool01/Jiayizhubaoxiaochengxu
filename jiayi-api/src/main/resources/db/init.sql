-- 嘉怡珠宝商城 - 初始化建表脚本

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    avatar VARCHAR(500),
    role VARCHAR(20) DEFAULT 'admin',
    deleted TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ums_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    openid VARCHAR(100),
    unionid VARCHAR(100),
    nickname VARCHAR(50),
    avatar VARCHAR(500),
    phone VARCHAR(20),
    gender TINYINT DEFAULT 0,
    level_id INT DEFAULT 1,
    points INT DEFAULT 0,
    last_signin_time DATETIME,
    deleted TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ums_member_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    min_points INT DEFAULT 0,
    max_points INT DEFAULT 999999,
    discount_rate DECIMAL(5,2) DEFAULT 10.00,
    icon VARCHAR(500),
    deleted TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS pms_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    name VARCHAR(50) NOT NULL,
    sort INT DEFAULT 0,
    icon VARCHAR(500),
    deleted TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS pms_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    product_type TINYINT DEFAULT 0 COMMENT '0-普通 1-限时抢购 2-限时活动 3-限时拍卖 4-积分商城 5-会员专享 6-新品',
    name VARCHAR(200) NOT NULL,
    subtitle VARCHAR(500),
    main_image VARCHAR(500),
    images TEXT COMMENT 'JSON 数组，多张图片URL',
    description TEXT,
    description_text TEXT COMMENT '商品介绍（纯文本，不含HTML）',
    specs TEXT COMMENT '规格参数 JSON [{label,value}]',
    price DECIMAL(10,2) NOT NULL COMMENT '当前售价',
    original_price DECIMAL(10,2) COMMENT '划线原价',
    points_price INT DEFAULT 0 COMMENT '积分价（积分商城专用）',
    stock INT DEFAULT 0,
    sales INT DEFAULT 0,
    flash_stock INT DEFAULT 0 COMMENT '限时抢购独立库存',
    flash_sold INT DEFAULT 0 COMMENT '限时抢购已售',
    sale_start DATETIME COMMENT '抢购/活动/拍卖开始时间',
    sale_end DATETIME COMMENT '抢购/活动/拍卖结束时间',
    member_level TINYINT DEFAULT 0 COMMENT '会员专享最低等级',
    is_new TINYINT DEFAULT 0,
    is_recommend TINYINT DEFAULT 0,
    sort_order INT DEFAULT 0,
    weight INT DEFAULT 0 COMMENT '全局权重，越大越靠前',
    status TINYINT DEFAULT 0 COMMENT '0-下架 1-上架',
    extra_attrs TEXT COMMENT 'JSON 扩展属性',
    video_url VARCHAR(500),
    deleted TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO pms_category (id, parent_id, name, sort) VALUES
(1, 0, '戒指', 1),
(2, 0, '项链', 2),
(3, 0, '耳饰', 3),
(4, 0, '手链', 4),
(5, 0, '手镯', 5),
(6, 0, '套装', 6),
(7, 0, '其他', 7);

CREATE TABLE IF NOT EXISTS pms_product_page_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    ai_enabled TINYINT(1) DEFAULT 0 COMMENT 'AI穿戴模块开关',
    video_enabled TINYINT(1) DEFAULT 0 COMMENT '抖音视频模块开关',
    video_cover VARCHAR(500) COMMENT '视频封面图',
    video_url VARCHAR(500) COMMENT '抖音跳转链接',
    gallery_enabled TINYINT(1) DEFAULT 0 COMMENT '商品大图模块开关',
    gallery_images TEXT COMMENT 'JSON数组，多张图片URL',
    disclaimer_enabled TINYINT(1) DEFAULT 0 COMMENT '免责声明开关',
    disclaimer_text VARCHAR(500) COMMENT '免责声明文字',
    disclaimer_color VARCHAR(20) DEFAULT '#605E5A' COMMENT '文字颜色',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- [收藏] 用户收藏表（新增，不修改原有表）
CREATE TABLE IF NOT EXISTS ums_user_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID(对应ums_user.id)',
    product_id BIGINT NOT NULL COMMENT '商品ID(对应pms_product.id)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ums_member_signin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    signin_date DATE NOT NULL,
    points_earned INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_date (user_id, signin_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
