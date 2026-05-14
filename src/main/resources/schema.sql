-- 회원 계정과 세션 로그인 기준 테이블입니다. favorites/notices가 members.id를 참조합니다.
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

-- data.go.kr에서 수집한 아파트/연립다세대 매매·전월세 실거래 통합 저장소입니다.
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
    created_at timestamp not null default current_timestamp,
    index idx_property_deals_region_month (lawd_cd, deal_year, deal_month),
    index idx_property_deals_type_month (deal_type, lawd_cd, deal_year, deal_month)
);

-- 법정동 코드 기준 테이블입니다. 지역 선택, 단지 검색 지역명 표시, 관심지역 코드 입력에 사용합니다.
create table if not exists dongcodes (
    dong_code varchar(10) not null primary key,
    sido_name varchar(30),
    gugun_name varchar(30),
    dong_name varchar(30)
);

-- 아파트 단지 기본정보입니다. housedeals와 apt_seq로 연결됩니다.
create table if not exists houseinfos (
    apt_seq varchar(20) not null primary key,
    sgg_cd varchar(5),
    umd_cd varchar(5),
    umd_nm varchar(20),
    jibun varchar(10),
    road_nm_sgg_cd varchar(5),
    road_nm varchar(20),
    road_nm_bonbun varchar(10),
    road_nm_bubun varchar(10),
    apt_nm varchar(40),
    build_year int,
    latitude varchar(45),
    longitude varchar(45)
);

-- 단지별 거래 이력입니다. houseinfos.apt_seq를 참조하며 단지 상세 화면의 거래 내역에 사용됩니다.
create table if not exists housedeals (
    no int auto_increment primary key,
    apt_seq varchar(20),
    apt_dong varchar(40),
    floor varchar(3),
    deal_year int,
    deal_month int,
    deal_day int,
    exclu_use_ar decimal(7, 2),
    deal_amount varchar(10),
    index idx_housedeals_apt_seq (apt_seq),
    constraint fk_housedeals_houseinfos foreign key (apt_seq) references houseinfos(apt_seq)
);

-- 회원별 관심지역입니다. member_id 기준으로 소유권을 제한하고 회원 삭제 시 함께 삭제됩니다.
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

-- 공지사항입니다. writer_id는 회원 삭제 시 null 처리되어 공지 내용은 유지됩니다.
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
