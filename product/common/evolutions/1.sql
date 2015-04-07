create table Song (
    id bigint not null auto_increment,
    artist varchar(40) not null,
    title varchar(60) not null,
    url varchar(60) not null,
    PRIMARY KEY (id),
    UNIQUE (url),
    UNIQUE (artist, title)
);