CREATE TABLE profiles (
    id VARCHAR(255) PRIMARY KEY,
    full_name VARCHAR(255),
    title VARCHAR(255),
    location VARCHAR(255),
    summary TEXT,
    image VARCHAR(1024),
    active BOOLEAN
);
