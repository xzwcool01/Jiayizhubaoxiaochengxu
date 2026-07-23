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

-- [购物车] 用户购物车表
CREATE TABLE IF NOT EXISTS ums_user_cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID(对应ums_user.id)',
    product_id BIGINT NOT NULL COMMENT '商品ID(对应pms_product.id)',
    quantity INT DEFAULT 1 COMMENT '数量',
    selected TINYINT(1) DEFAULT 1 COMMENT '是否选中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- [地址] 用户收货地址
CREATE TABLE IF NOT EXISTS ums_user_address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(50) NOT NULL COMMENT '收件人',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    province VARCHAR(50) DEFAULT '' COMMENT '省',
    city VARCHAR(50) DEFAULT '' COMMENT '市',
    district VARCHAR(50) DEFAULT '' COMMENT '区',
    detail VARCHAR(200) NOT NULL COMMENT '详细地址',
    is_default TINYINT(1) DEFAULT 0 COMMENT '是否默认',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- [优惠券] 优惠券定义
CREATE TABLE IF NOT EXISTS sms_coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '券名称',
    type TINYINT NOT NULL COMMENT '0-满减 1-折扣',
    value DECIMAL(10,2) NOT NULL COMMENT '满减=减金额 折扣=折扣率%%',
    min_amount DECIMAL(10,2) DEFAULT 0 COMMENT '最低订单金额 0=无门槛',
    max_amount DECIMAL(10,2) DEFAULT 0 COMMENT '最高抵扣 0=不限',
    start_time DATETIME NOT NULL COMMENT '有效期开始',
    end_time DATETIME NOT NULL COMMENT '有效期结束',
    total_count INT DEFAULT 0 COMMENT '发行总量',
    per_user_limit INT DEFAULT 1 COMMENT '每用户限领',
    used_count INT DEFAULT 0 COMMENT '已使用',
    issued_count INT DEFAULT 0 COMMENT '已发放数量',
    status TINYINT DEFAULT 1 COMMENT '0-下架 1-上架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- [优惠券-商品] 优惠券可用商品（空=全部可用）
CREATE TABLE IF NOT EXISTS sms_coupon_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    coupon_id BIGINT NOT NULL,
    product_id BIGINT DEFAULT NULL COMMENT '空=全部商品可用',
    UNIQUE KEY uk_coupon_product (coupon_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- [用户优惠券] 用户领券记录
CREATE TABLE IF NOT EXISTS sms_user_coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    used TINYINT(1) DEFAULT 0 COMMENT '0-未用 1-已用',
    order_id BIGINT DEFAULT NULL COMMENT '使用订单ID',
    used_time DATETIME DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_coupon (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- [积分规则] 多少积分抵多少钱
CREATE TABLE IF NOT EXISTS sms_points_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    points INT NOT NULL COMMENT '所需积分数',
    amount DECIMAL(10,2) NOT NULL COMMENT '抵扣金额',
    type TINYINT DEFAULT 0 COMMENT '0-固定抵扣(固定积分换固定金额) 1-全积分抵扣(所有积分按比例换)',
    status TINYINT DEFAULT 1 COMMENT '0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE sms_points_rule ADD COLUMN IF NOT EXISTS type TINYINT DEFAULT 0 COMMENT '0-固定抵扣 1-全积分抵扣' after amount;

-- [积分适用商品] 空=全部商品可用
CREATE TABLE IF NOT EXISTS sms_points_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_id BIGINT NOT NULL,
    product_id BIGINT DEFAULT NULL COMMENT '空=全部商品可用',
    UNIQUE KEY uk_rule_product (rule_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- [订单] 主订单
CREATE TABLE IF NOT EXISTS oms_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_sn VARCHAR(64) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL COMMENT '商品总额',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    coupon_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠券抵扣',
    points_amount DECIMAL(10,2) DEFAULT 0 COMMENT '积分抵扣',
    points_deduct INT DEFAULT 0 COMMENT '消耗积分数',
    freight_amount DECIMAL(10,2) DEFAULT 0 COMMENT '运费',
    payment_method TINYINT DEFAULT 0 COMMENT '0-未支付 1-微信支付',
    status TINYINT DEFAULT 0 COMMENT '0-待付款 1-已付款 2-已发货 3-已完成 4-已取消',
    address_snapshot JSON COMMENT '地址快照',
    note VARCHAR(500) DEFAULT '' COMMENT '买家备注',
    paid_at DATETIME DEFAULT NULL COMMENT '支付时间',
    coupon_id BIGINT DEFAULT NULL COMMENT '使用的优惠券ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_sn (order_sn),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- [订单商品] 订单商品快照
CREATE TABLE IF NOT EXISTS oms_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    product_specs TEXT COMMENT '规格快照',
    product_image VARCHAR(500) COMMENT '商品主图',
    price DECIMAL(10,2) NOT NULL COMMENT '下单时单价',
    quantity INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ums_member_signin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    signin_date DATE NOT NULL,
    points_earned INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_date (user_id, signin_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 发货表
CREATE TABLE IF NOT EXISTS oms_order_delivery (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    express_company VARCHAR(50),
    tracking_no VARCHAR(50),
    waybill_data TEXT COMMENT '顺丰创建订单返回的原始数据(JSON)',
    status TINYINT DEFAULT 0 COMMENT '0-已发货 1-已签收',
    shipped_at DATETIME,
    received_at DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评价表
CREATE TABLE IF NOT EXISTS oms_order_review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '关联订单',
    product_id BIGINT NOT NULL COMMENT '关联商品',
    user_id BIGINT NOT NULL COMMENT '关联用户',
    rating TINYINT NOT NULL COMMENT '评分1-5',
    content VARCHAR(500) DEFAULT '' COMMENT '评价内容',
    images TEXT COMMENT '图片JSON',
    is_anonymous TINYINT DEFAULT 0 COMMENT '0实名1匿名',
    is_top TINYINT DEFAULT 0 COMMENT '0不置顶1置顶',
    status TINYINT DEFAULT 1 COMMENT '0隐藏1显示',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 行为积分规则表
CREATE TABLE IF NOT EXISTS ums_action_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action_key VARCHAR(50) NOT NULL UNIQUE COMMENT '行为标识',
    action_name VARCHAR(100) NOT NULL COMMENT '行为名称',
    points INT NOT NULL DEFAULT 0 COMMENT '获得积分数',
    status TINYINT DEFAULT 1 COMMENT '0禁用1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 积分变动记录表
CREATE TABLE IF NOT EXISTS ums_points_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    action_key VARCHAR(50) NOT NULL COMMENT '行为标识',
    action_name VARCHAR(100) NOT NULL COMMENT '行为名称',
    points INT NOT NULL COMMENT '变动积分数(正增负减)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO ums_action_points (action_key, action_name, points) VALUES
('sign_in', '每日签到', 10),
('review', '商品评价', 20),
('register', '注册赠送', 1000),
('order', '下单赠送', 10),
('share', '分享商品', 20);

ALTER TABLE oms_order ADD COLUMN reviewed TINYINT DEFAULT 0 COMMENT '0未评价1已评价';
ALTER TABLE ums_level ADD COLUMN discount_rate DECIMAL(5,2) DEFAULT 10.00 COMMENT '折扣率';
ALTER TABLE ums_level ADD COLUMN color VARCHAR(20) DEFAULT '#775836' COMMENT '显示颜色';
ALTER TABLE ums_level ADD COLUMN perks TEXT COMMENT '权益列表JSON数组';
ALTER TABLE ums_level ADD COLUMN icon VARCHAR(500) COMMENT '图标';
ALTER TABLE ums_level ADD COLUMN ai_wear_limit INT DEFAULT 10 COMMENT 'AI试戴每日限额';

-- AI穿戴记录表
CREATE TABLE IF NOT EXISTS ai_wear_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    user_photo VARCHAR(500) NOT NULL COMMENT '用户照片URL',
    result_url VARCHAR(500) COMMENT 'AI生成结果URL',
    style VARCHAR(50) COMMENT '选择的风格',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_date (user_id, create_time),
    INDEX idx_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- AI穿戴提示词模板表
CREATE TABLE IF NOT EXISTS ai_wear_prompt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL COMMENT '商品分类ID(关联pms_category)',
    prompt_template TEXT NOT NULL COMMENT '提示词模板，{name}商品名 {desc}描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE ai_wear_record ADD COLUMN category_id BIGINT COMMENT '商品分类ID' AFTER product_id;
ALTER TABLE ai_wear_record ADD COLUMN show_on_discovery TINYINT DEFAULT 0 COMMENT '是否在发现页展示' AFTER style;
