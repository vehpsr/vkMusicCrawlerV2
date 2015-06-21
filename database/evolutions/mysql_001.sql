create table Song (
    id bigint not null auto_increment,
    artist varchar(50) not null,
    title varchar(70) not null,
    PRIMARY KEY (id),
    UNIQUE (artist, title)
) engine=MyISAM;

create table Users (
    id bigint not null auto_increment,
    name varchar(60),
    url varchar(40) not null,
    vkId varchar(20),
    PRIMARY KEY (id),
    UNIQUE (url),
    index(vkId)
) engine=MyISAM;

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
) engine=MyISAM;

create table Groups (
    id bigint not null auto_increment,
    name varchar(80),
    url varchar(40) not null,
    vkId varchar(20),
    paginationStart int default 0,
    PRIMARY KEY (id),
    UNIQUE (url)
) engine=MyISAM;
