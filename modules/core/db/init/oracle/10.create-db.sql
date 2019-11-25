-- begin LUPREVIEW_ENTITY_PROPERTY
create table LUPREVIEW_ENTITY_PROPERTY (
    ID varchar2(32),
    CREATE_TS timestamp,
    CREATED_BY varchar2(50 char),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50 char),
    VERSION number(10) not null,
    --
    USER_ID varchar2(32),
    ENTITY_NAME varchar2(255 char) not null,
    PROPERTY_NAME varchar2(255 char) not null,
    SORT_ORDER number(10),
    --
    primary key (ID)
)^
-- end LUPREVIEW_ENTITY_PROPERTY
