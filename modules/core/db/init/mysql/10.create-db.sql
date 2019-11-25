-- begin LUPREVIEW_ENTITY_PROPERTY
create table LUPREVIEW_ENTITY_PROPERTY (
    ID varchar(32),
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    VERSION integer not null,
    --
    USER_ID varchar(32),
    ENTITY_NAME varchar(255) not null,
    PROPERTY_NAME varchar(255) not null,
    SORT_ORDER integer,
    --
    primary key (ID)
)^
-- end LUPREVIEW_ENTITY_PROPERTY
