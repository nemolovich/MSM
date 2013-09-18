-- Running file...
-- .=====================================================. --
-- |              CREATION ET REMPLISSAGE                | --
-- |                 DE LA TABLE USERS                   | --
-- '=====================================================' --
-------------------------------------------------------------
-- DATABSE: 		administration_db [root on ROOT]
-- URL:				jdbc:derby://localhost/administration_db
-- AUTHOR:			Brian GOHIER
-- DATE:			2013-06-13 13:27
-------------------------------------------------------------

--- Supprime les tables si elles existent, sinon commenter --
DELETE FROM ROOT.FRIENDS_RELATION;
ALTER TABLE ROOT.FRIENDS_RELATION DROP CONSTRAINT friends_relation_first_id_fk;
ALTER TABLE ROOT.FRIENDS_RELATION DROP CONSTRAINT friends_relation_second_id_fk;
DROP TABLE ROOT.FRIENDS_RELATION;
DELETE FROM ROOT.USERS;
ALTER TABLE ROOT.USERS ALTER COLUMN ID RESTART WITH 1;
DROP TABLE ROOT.USERS;

---------------- Cr�ation de la table USERS -----------------
-- TABLE:			USERS
-- DESCRIPTION: 	Table concernant les utilisateurs
-- 	du site, ceux qui ont acc�s aux donn�es de la base
-- 	de donn�es.
CREATE TABLE ROOT.USERS (
	id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY
 		(START WITH 1, INCREMENT BY 1),
	mail VARCHAR(64) NOT NULL UNIQUE,
	name VARCHAR(30) NOT NULL,
	firstname VARCHAR(30) NOT NULL,
	rights VARCHAR(32) NOT NULL DEFAULT 'UNKNOWN'
	CONSTRAINT rights_ck CHECK (rights IN
		('UNKNOWN','USER','ADMIN')),
	password CHAR(32) NOT NULL
	);

------- Permet de ne laisser l'identifiant '1' libre --------
-- Red�marre le compteur � '0', cr�ation d'admin temporaire
--  prenant alors en ID '0', prochaine entr�e � '1'
ALTER TABLE ROOT.USERS ALTER COLUMN ID RESTART WITH 0;

---------- Cr�ation d'un administrateur par d�faut ----------
-- LOGIN (mail):		admin
-- PASSWORD:			admin
-- /!\ Ne pas oublier de supprimer cette entr�e apr�s  /!\ --
-- /!\ avoir cr�� votre administrateur via l'interface /!\ --
-- /!\ principale                                      /!\ --
INSERT INTO ROOT.USERS
		(MAIL, "NAME", FIRSTNAME, RIGHTS, PASSWORD) 
	VALUES ('admin', '', '', 'ADMIN',
				'!#/)zW��C�JJ��                ');


CREATE TABLE ROOT.FRIENDS_RELATION (
        first_id INTEGER NOT NULL,
        second_id INTEGER NOT NULL,
        CONSTRAINT friends_relation_first_id_fk
                FOREIGN KEY (first_id)
                REFERENCES ROOT.USERS(id),
        CONSTRAINT friends_relation_second_id_fk
                FOREIGN KEY (second_id)
                REFERENCES ROOT.USERS(id)
        );

-- File successfully loaded!
;