-- Running file...
-- .=====================================================. --
-- |              CREATION ET REMPLISSAGE                | --
-- |                 DE LA TABLE USERS                   | --
-- '=====================================================' --
-------------------------------------------------------------
-- DATABSE: 		msmdb [root on ROOT]
-- URL:				jdbc:derby://localhost:1527/msmdb
-- AUTHOR:			Brian GOHIER
-- DATE:			2013-08-18 13:27
-------------------------------------------------------------


----- Supprime la table si elle existe, sinon commenter -----
ALTER TABLE ROOT.FRIENDS_RELATION DROP CONSTRAINT FRIENDS_RELATION_FIRST_ID_FK;
ALTER TABLE ROOT.FRIENDS_RELATION DROP CONSTRAINT FRIENDS_RELATION_SECOND_ID_FK;
DELETE FROM ROOT.FRIENDS_RELATION;
ALTER TABLE ROOT.FRIENDS_RELATION ALTER COLUMN ID RESTART WITH 1;
DELETE FROM ROOT.USERS;
ALTER TABLE ROOT.USERS ALTER COLUMN ID RESTART WITH 1;

----------------- Cr�ation des utilisateurs -----------------
INSERT INTO ROOT.USERS
		(MAIL, "NAME", FIRSTNAME, RIGHTS, PASSWORD) 
	VALUES ('user1', 'user', '1', 'USER',
				'!#/)zW��C�JJ��                ');

INSERT INTO ROOT.USERS
		(MAIL, "NAME", FIRSTNAME, RIGHTS, PASSWORD) 
	VALUES ('user2', 'user', '2', 'USER',
				'!#/)zW��C�JJ��                ');

INSERT INTO ROOT.USERS
		(MAIL, "NAME", FIRSTNAME, RIGHTS, PASSWORD) 
	VALUES ('user3', 'user', '3', 'USER',
				'!#/)zW��C�JJ��                ');

INSERT INTO ROOT.USERS
		(MAIL, "NAME", FIRSTNAME, RIGHTS, PASSWORD) 
	VALUES ('user4', 'user', '4', 'USER',
				'!#/)zW��C�JJ��                ');

INSERT INTO ROOT.USERS
		(MAIL, "NAME", FIRSTNAME, RIGHTS, PASSWORD) 
	VALUES ('user5', 'user', '5', 'USER',
				'!#/)zW��C�JJ��                ');

INSERT INTO ROOT.USERS
		(MAIL, "NAME", FIRSTNAME, RIGHTS, PASSWORD) 
	VALUES ('user6', 'user', '6', 'USER',
				'!#/)zW��C�JJ��                ');

INSERT INTO ROOT.USERS
		(MAIL, "NAME", FIRSTNAME, RIGHTS, PASSWORD) 
	VALUES ('user7', 'user', '7', 'USER',
				'!#/)zW��C�JJ��                ');

----------------- Cr�ation des relations ------------------

INSERT INTO ROOT.FRIENDS_RELATION (FIRST_ID, SECOND_ID)
	VALUES (1,2);
	
INSERT INTO ROOT.FRIENDS_RELATION (FIRST_ID, SECOND_ID)
	VALUES (1,3);

INSERT INTO ROOT.FRIENDS_RELATION (FIRST_ID, SECOND_ID)
	VALUES (2,3);
	
INSERT INTO ROOT.FRIENDS_RELATION (FIRST_ID, SECOND_ID)
	VALUES (2,4);

INSERT INTO ROOT.FRIENDS_RELATION (FIRST_ID, SECOND_ID)
	VALUES (3,4);
	
INSERT INTO ROOT.FRIENDS_RELATION (FIRST_ID, SECOND_ID)
	VALUES (3,5);

INSERT INTO ROOT.FRIENDS_RELATION (FIRST_ID, SECOND_ID)
	VALUES (4,5);
	
INSERT INTO ROOT.FRIENDS_RELATION (FIRST_ID, SECOND_ID)
	VALUES (4,6);

INSERT INTO ROOT.FRIENDS_RELATION (FIRST_ID, SECOND_ID)
	VALUES (5,6);
	
INSERT INTO ROOT.FRIENDS_RELATION (FIRST_ID, SECOND_ID)
	VALUES (5,7);

INSERT INTO ROOT.FRIENDS_RELATION (FIRST_ID, SECOND_ID)
	VALUES (6,7);
	
INSERT INTO ROOT.FRIENDS_RELATION (FIRST_ID, SECOND_ID)
	VALUES (6,1);

INSERT INTO ROOT.FRIENDS_RELATION (FIRST_ID, SECOND_ID)
	VALUES (7,1);
	
INSERT INTO ROOT.FRIENDS_RELATION (FIRST_ID, SECOND_ID)
	VALUES (7,2);

-- File successfully loaded!
;