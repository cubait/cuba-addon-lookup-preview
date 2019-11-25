-- begin LUPREVIEW_ENTITY_PROPERTY
create table LUPREVIEW_ENTITY_PROPERTY (
    ID uniqueidentifier,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    VERSION integer not null,
    --
    USER_ID uniqueidentifier,
    ENTITY_NAME varchar(255) not null,
    PROPERTY_NAME varchar(255) not null,
    SORT_ORDER integer,
    --
    primary key nonclustered (ID)
)^
-- end LUPREVIEW_ENTITY_PROPERTY
