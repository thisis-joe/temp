create table if not exists members (
    id bigint auto_increment primary key,
    email varchar(120) not null unique,
    password varchar(100) not null,
    name varchar(40) not null,
    phone varchar(30),
    address varchar(255),
    role varchar(20) not null default 'USER',
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp on update current_timestamp
);

create table if not exists property_deals (
    id bigint auto_increment primary key,
    deal_type varchar(30) not null,
    lawd_cd varchar(10) not null,
    umd_nm varchar(80),
    house_name varchar(160),
    house_type varchar(40),
    jibun varchar(60),
    road_name varchar(160),
    build_year int,
    exclusive_area decimal(12, 4),
    land_area decimal(12, 4),
    deal_year int,
    deal_month int,
    deal_day int,
    deal_amount bigint,
    deposit bigint,
    monthly_rent bigint,
    floor varchar(20),
    deal_gbn varchar(60),
    raw_xml text,
    created_at timestamp not null default current_timestamp
);

create table if not exists favorites (
    id bigint auto_increment primary key,
    member_id bigint not null,
    sido_nm varchar(80),
    sigungu_nm varchar(80),
    dong_nm varchar(80),
    lawd_cd varchar(10) not null,
    memo varchar(255),
    created_at timestamp not null default current_timestamp,
    constraint fk_favorites_member foreign key (member_id) references members(id) on delete cascade
);

create table if not exists notices (
    id bigint auto_increment primary key,
    title varchar(200) not null,
    content text not null,
    writer_id bigint,
    view_count int not null default 0,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp on update current_timestamp,
    constraint fk_notices_writer foreign key (writer_id) references members(id) on delete set null
);
