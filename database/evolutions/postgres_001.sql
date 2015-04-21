create table Song (
    id serial,
    artist varchar(50) not null,
    title varchar(70) not null,
    PRIMARY KEY (id),
    UNIQUE (artist, title)
);

create table Users (
    id serial,
    name varchar(60),
    url varchar(40) not null,
    vkId varchar(20) not null,
    PRIMARY KEY (id),
    UNIQUE (url)
);

create table Rating (
    id serial,
    value int not null,
    date TIMESTAMP not null,
    user_id bigint not null,
    song_id bigint not null,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES Users(id),
    FOREIGN KEY (song_id)
        REFERENCES Song(id)
);

create index on Rating (date);
