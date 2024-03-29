DROP TABLE IF EXISTS FILM_GENRE,USERS, FILMS, FRIENDS, GENRES, LIKES, RATING, REVIEWS, DIRECTORS, FILM_DIRECTOR;
DROP TABLE IF EXISTS FILM_GENRE, USERS, FILMS, FRIENDS, GENRES, LIKES, RATING, REVIEWS, REVIEWS_LIKES, DIRECTORS, FILM_DIRECTOR, EVENTS;

CREATE TABLE IF NOT EXISTS RATING (
                                      RATING_ID INTEGER NOT NULL AUTO_INCREMENT,
                                      NAME VARCHAR_IGNORECASE NOT NULL,
                                      CONSTRAINT RATING_RATING_ID_PK PRIMARY KEY (RATING_ID)
);

CREATE TABLE IF NOT EXISTS FILMS (
                                     FILM_ID INTEGER NOT NULL AUTO_INCREMENT,
                                     NAME VARCHAR_IGNORECASE NOT NULL,
                                     DESCRIPTION VARCHAR_IGNORECASE(200) NOT NULL,
                                     RELEASE_DATE DATE NOT NULL,
                                     DURATION INTEGER NOT NULL,
                                     RATING_ID INTEGER NOT NULL,
                                     CONSTRAINT FILMS_FILM_ID_PK PRIMARY KEY (FILM_ID),
                                     CONSTRAINT FILMS_RATING_ID_FK FOREIGN KEY (RATING_ID) REFERENCES RATING(RATING_ID)
);


CREATE TABLE IF NOT EXISTS GENRES (
                                      GENRE_ID INTEGER NOT NULL AUTO_INCREMENT,
                                      NAME VARCHAR_IGNORECASE NOT NULL,
                                      CONSTRAINT GENRES_PK PRIMARY KEY (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS FILM_GENRE (
                                          FILM_ID INTEGER NOT NULL,
                                          GENRE_ID INTEGER NOT NULL,
                                          CONSTRAINT FILM_GENRE_FILM_ID_FK FOREIGN KEY (FILM_ID) REFERENCES FILMS(FILM_ID) ON DELETE CASCADE,
                                          CONSTRAINT FILM_GENRE_GENRE_ID_FK FOREIGN KEY (GENRE_ID) REFERENCES GENRES(GENRE_ID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS FILM_GENRE_FILM_ID_FK_INDEX ON FILM_GENRE(FILM_ID);
CREATE INDEX IF NOT EXISTS FILM_GENRE_GENRE_ID_FK_INDEX ON FILM_GENRE(GENRE_ID);

CREATE TABLE IF NOT EXISTS USERS (
                                     USER_ID INTEGER NOT NULL AUTO_INCREMENT,
                                     EMAIL VARCHAR_IGNORECASE NOT NULL,
                                     LOGIN VARCHAR_IGNORECASE NOT NULL,
                                     NAME VARCHAR_IGNORECASE,
                                     BIRTHDAY DATE,
                                     CONSTRAINT USERS_USER_ID_PK PRIMARY KEY (USER_ID)
);


CREATE TABLE IF NOT EXISTS LIKES (
                                     FILM_ID INTEGER NOT NULL,
                                     USER_ID INTEGER NOT NULL,
                                     MARK REAL NOT NULL,
                                     CONSTRAINT LIKES_FILM_ID_USER_ID_PK PRIMARY KEY (FILM_ID,USER_ID),
                                     CONSTRAINT LIKES_FILM_ID_FK FOREIGN KEY (FILM_ID) REFERENCES FILMS(FILM_ID) ON DELETE CASCADE,
                                     CONSTRAINT LIKES_USER_ID_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE

);

CREATE INDEX IF NOT EXISTS LIKES_FILM_ID_FK_INDEX ON LIKES(FILM_ID);
CREATE INDEX IF NOT EXISTS LIKES_USER_ID_FK_INDEX ON LIKES(USER_ID);


CREATE TABLE IF NOT EXISTS FRIENDS (
                                       USER_ID INTEGER NOT NULL,
                                       FRIEND_ID INTEGER NOT NULL,
                                       FRIENDS_STATUS BOOLEAN DEFAULT false NOT NULL,
                                       CONSTRAINT FRIENDS_USER_ID_FRIEND_ID_PK PRIMARY KEY (USER_ID,FRIEND_ID),
                                       CONSTRAINT FRIENDS_USER_ID_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE,
                                       CONSTRAINT FRIENDS_FRIEND_ID_FK FOREIGN KEY (FRIEND_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS FRIENDS_USER_ID_FK_INDEX ON FRIENDS(USER_ID);
CREATE INDEX IF NOT EXISTS FRIENDS_FRIEND_ID_FK_INDEX ON FRIENDS(FRIEND_ID);


CREATE TABLE IF NOT EXISTS EVENTS (
    EVENT_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    TIMESTAMP BIGINT NOT NULL,
    USER_ID INTEGER NOT NULL,
    EVENT_TYPE VARCHAR(11) NOT NULL,
    OPERATION VARCHAR(11) NOT NULL,
    ENTITY_ID INTEGER NOT NULL,
    CONSTRAINT EVENTS_USER_ID_FK FOREIGN KEY(USER_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS EVENTS_USERS_ID_FK_INDEX ON EVENTS(USER_ID);

CREATE TABLE IF NOT EXISTS DIRECTORS (
                                      DIRECTOR_ID INTEGER NOT NULL AUTO_INCREMENT,
                                      NAME VARCHAR_IGNORECASE NOT NULL,
                                      CONSTRAINT DIRECTORS_PK PRIMARY KEY (DIRECTOR_ID)
);

CREATE TABLE IF NOT EXISTS REVIEWS (
	                                   ID INTEGER NOT NULL AUTO_INCREMENT,
	                                   CONTENT VARCHAR_IGNORECASE NOT NULL,
	                                   IS_POSITIVE BOOLEAN NOT NULL,
                                       USER_ID INTEGER NOT NULL,
	                                   FILM_ID INTEGER NOT NULL,
	                                   USEFUL INTEGER DEFAULT 0 NOT NULL,
	                                   CONSTRAINT REVIEWS_ID_PK PRIMARY KEY (ID),
	                                   CONSTRAINT REVIEWS_FILM_ID_FK FOREIGN KEY (FILM_ID) REFERENCES FILMS(FILM_ID) ON DELETE CASCADE,
                                       CONSTRAINT REVIEWS_USER_ID_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REVIEWS_LIKES (
                                     REVIEW_ID INTEGER NOT NULL,
                                     USER_ID INTEGER NOT NULL,
                                     IS_LIKE BOOLEAN NOT NULL,
                                     CONSTRAINT REVIEWS_LIKES_REVIEW_ID_USER_ID_PK PRIMARY KEY (REVIEW_ID,USER_ID,IS_LIKE) ,
                                     CONSTRAINT REVIEWS_LIKES_REVIEW_ID_FK FOREIGN KEY (REVIEW_ID) REFERENCES REVIEWS(ID) ON DELETE CASCADE,
                                     CONSTRAINT REVIEWS_LIKES_USER_ID_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS REVIEWS_LIKES_REVIEW_ID_FK_INDEX ON REVIEWS_LIKES(REVIEW_ID);
CREATE INDEX IF NOT EXISTS REVIEWS_LIKES_USER_ID_FK_INDEX ON REVIEWS_LIKES(USER_ID);


CREATE TABLE IF NOT EXISTS FILM_DIRECTOR (
                                          FILM_ID INTEGER NOT NULL,
                                          DIRECTOR_ID INTEGER NOT NULL,
                                          CONSTRAINT FILM_DIRECTOR_FILM_ID_FK FOREIGN KEY (FILM_ID) REFERENCES FILMS(FILM_ID) ON DELETE CASCADE,
                                          CONSTRAINT FILM_DIRECTOR_DIRECTOR_ID_FK FOREIGN KEY (DIRECTOR_ID) REFERENCES DIRECTORS(DIRECTOR_ID) ON DELETE CASCADE,
                                          CONSTRAINT FILM_ID_DIRECTOR_ID PRIMARY KEY (FILM_ID,DIRECTOR_ID)
);

CREATE INDEX IF NOT EXISTS FILM_DIRECTOR_DIRECTOR_ID_FK_INDEX ON FILM_DIRECTOR(DIRECTOR_ID);
CREATE INDEX IF NOT EXISTS FILM_DIRECTOR_FILM_ID_FK_INDEX ON FILM_DIRECTOR(FILM_ID);
