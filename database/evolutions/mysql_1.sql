create table Song (
    id bigint not null auto_increment,
    artist varchar(40) not null,
    title varchar(60) not null,
    url varchar(160) not null,
    PRIMARY KEY (id),
    UNIQUE (url),
    UNIQUE (artist, title)
);

create table Users (
    id bigint not null auto_increment,
    name varchar(60),
    url varchar(40) not null,
    vkId varchar(20) not null,
    PRIMARY KEY (id),
    UNIQUE (url),
    UNIQUE (vkId)
);

create table Rating (
    id bigint not null auto_increment,
    value int not null,
    date TIMESTAMP not null,
    user_id bigint not null,
    song_id bigint not null,
    PRIMARY KEY (id),
	INDEX (date),
    FOREIGN KEY (user_id)
        REFERENCES Users(id),
    FOREIGN KEY (song_id)
        REFERENCES Song(id)
);

create table Users_Song (
    songs_id bigint not null,
    users_id bigint not null,
	INDEX (songs_id),
	INDEX (users_id),
    FOREIGN KEY (songs_id)
        REFERENCES Song(id),
    FOREIGN KEY (users_id)
        REFERENCES Users(id)
);
