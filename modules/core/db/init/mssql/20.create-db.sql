-- begin LUPREVIEW_ENTITY_PROPERTY
alter table LUPREVIEW_ENTITY_PROPERTY add constraint FK_LUPREVIEW_ENTITY_PROPERTY_ON_USER foreign key (USER_ID) references SEC_USER(ID)^
create index IDX_LUPREVIEW_ENTITY_PROPERTY_ON_USER on LUPREVIEW_ENTITY_PROPERTY (USER_ID)^
-- end LUPREVIEW_ENTITY_PROPERTY
