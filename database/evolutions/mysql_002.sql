alter table Users modify column vkId varchar(20);

alter table Users add index vkId (vkId);

create table Groups (
    id bigint not null auto_increment,
    name varchar(80),
    url varchar(40) not null,
    vkId varchar(20),
    paginationStart int default 0,
    PRIMARY KEY (id),
    UNIQUE (url)
);
