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


----- Supprime la table si elle existe, sinon commenter -----
DELETE FROM ROOT.USERS;
ALTER TABLE ROOT.USERS ALTER COLUMN ID RESTART WITH 1;
DROP TABLE ROOT.USERS;

---------------- Création de la table USERS -----------------
-- TABLE:			USERS
-- DESCRIPTION: 	Table concernant les utilisateurs
-- 	du site, ceux qui ont accès aux données de la base
-- 	de données.
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
-- Redémarre le compteur à '0', création d'admin temporaire
--  prenant alors en ID '0', prochaine entrée à '1'
ALTER TABLE ROOT.USERS ALTER COLUMN ID RESTART WITH 0;

---------- Création d'un administrateur par défaut ----------
-- LOGIN (mail):		admin
-- PASSWORD:			admin
-- /!\ Ne pas oublier de supprimer cette entrée après  /!\ --
-- /!\ avoir créé votre administrateur via l'interface /!\ --
-- /!\ principale                                      /!\ --
INSERT INTO ROOT.USERS
		(MAIL, "NAME", FIRSTNAME, RIGHTS, PASSWORD) 
	VALUES ('admin', '', '', 'ADMIN',
				'!#/)zW¥§C‰JJ€Ã                ');