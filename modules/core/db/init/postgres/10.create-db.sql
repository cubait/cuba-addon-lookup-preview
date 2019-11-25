-- begin LUPREVIEW_ENTITY_PROPERTY
create table LUPREVIEW_ENTITY_PROPERTY (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    VERSION integer not null,
    --
    USER_ID uuid,
    ENTITY_NAME varchar(255) not null,
    PROPERTY_NAME varchar(255) not null,
    SORT_ORDER integer,
    --
    primary key (ID)
)^
-- end LUPREVIEW_ENTITY_PROPERTY
