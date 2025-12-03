CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(30) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'NORMAL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_elder (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    gender VARCHAR(10),
    age INT,
    id_card VARCHAR(50),
    phone VARCHAR(20),
    address VARCHAR(255),
    living_type VARCHAR(50),
    health_level VARCHAR(50),
    emergency_contact_name VARCHAR(50),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relation VARCHAR(30),
    remarks VARCHAR(255),
    responsible_volunteer_id BIGINT,
    responsible_volunteer_name VARCHAR(50),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_health_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    elder_id BIGINT NOT NULL,
    record_date DATETIME,
    blood_pressure VARCHAR(50),
    heart_rate INT,
    temperature DOUBLE,
    blood_sugar DOUBLE,
    health_desc VARCHAR(255),
    created_by BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_visit_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    elder_id BIGINT NOT NULL,
    volunteer_id BIGINT,
    visit_time DATETIME,
    visit_type VARCHAR(50),
    service_content VARCHAR(255),
    elder_status VARCHAR(255),
    next_advice VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_volunteer_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    organization VARCHAR(100),
    remark VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS t_announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    publisher_id BIGINT,
    publish_time DATETIME,
    status VARCHAR(30),
    type VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_dict (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dict_type VARCHAR(50) NOT NULL,
    dict_key VARCHAR(50) NOT NULL,
    dict_value VARCHAR(100) NOT NULL,
    sort INT,
    status VARCHAR(20) DEFAULT 'ENABLED'
);
