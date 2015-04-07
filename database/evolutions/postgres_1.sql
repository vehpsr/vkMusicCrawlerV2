create table Song (
    id serial,
    artist varchar(40) not null,
    title varchar(60) not null,
    url varchar(60) not null,
    PRIMARY KEY (id),
    UNIQUE (url),
    UNIQUE (artist, title)
);

create table Users (
    id serial,
    name varchar(60),
    url varchar(40) not null,
    vkId varchar(20) not null,
    PRIMARY KEY (id),
    UNIQUE (url),
    UNIQUE (vkId)
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

create table Users_Song (
    songs_id bigint not null,
    users_id bigint not null,
    FOREIGN KEY (songs_id)
        REFERENCES Song(id),
    FOREIGN KEY (users_id)
        REFERENCES Users(id)
);

create index on Users_Song (songs_id);
create index on Users_Song (users_id);
