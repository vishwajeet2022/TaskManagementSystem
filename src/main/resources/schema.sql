-- Create the table for Authority
CREATE TABLE authority (
    authority_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    authority_code VARCHAR(50) NOT NULL
);

-- Create the table for TMSUser
CREATE TABLE tms_user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

-- Create the table for Task
CREATE TABLE task (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_subject VARCHAR(255) NOT NULL,
    task_description VARCHAR(1000),
    task_priority SMALLINT NOT NULL,
    task_due_date TIMESTAMP NOT NULL,
    task_status VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    created_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    updated_by VARCHAR(255),
    CONSTRAINT fk_user_task FOREIGN KEY (user_id) REFERENCES tms_user(user_id)
);

-- Create the join table for TMSUser and Authority
CREATE TABLE user_authorities_map (
    user_id BIGINT NOT NULL,
    authority_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, authority_id),
    CONSTRAINT fk_user_authority FOREIGN KEY (user_id) REFERENCES tms_user(user_id),
    CONSTRAINT fk_authority_user FOREIGN KEY (authority_id) REFERENCES authority(authority_id)
);