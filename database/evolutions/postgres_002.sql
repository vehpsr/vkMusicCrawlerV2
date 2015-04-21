alter table Users alter column vkId drop not null;

create index on Users (vkId);

create table Groups (
    id serial,
    name varchar(80),
    url varchar(40) not null,
    vkId varchar(20),
    paginationStart int default 0,
    PRIMARY KEY (id)
);
