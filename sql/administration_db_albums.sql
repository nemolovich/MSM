-- Running file...
-- .=====================================================. --
-- |              CREATION ET REMPLISSAGE                | --
-- |                 DE LA TABLE ALBUMS                  | --
-- '=====================================================' --
-------------------------------------------------------------
-- DATABSE: 		administration_db [root on ROOT]
-- URL:				jdbc:derby://localhost/administration_db
-- AUTHOR:			Maël BARBIN
-- DATE:			2013-06-13 13:27
-------------------------------------------------------------


----- Supprime la table si elle existe, sinon commenter -----
DELETE FROM ROOT.ALBUMS;
ALTER TABLE ROOT.ALBUMS ALTER COLUMN ID RESTART WITH 1;
DROP TABLE ROOT.ALBUMS;

---------------- Cr�ation de la table ALBUMS -----------------
-- TABLE:			ALBUMS
-- DESCRIPTION: 	Albums users table
-- attribute visibility :  0-private 1-public 2-Friends
CREATE TABLE ROOT.ALBUMS (
	id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY
 		(START WITH 1, INCREMENT BY 1),
        id_usr INTEGER NOT NULL,
	name VARCHAR(30) NOT NULL,
        visibility INTEGER NOT NULL
	CONSTRAINT visibility_ck CHECK (visibility IN
		(0,1,2))
	);

------- Permet de ne laisser l'identifiant '1' libre --------
-- Red�marre le compteur � '0', cr�ation d'admin temporaire
--  prenant alors en ID '0', prochaine entr�e � '1'
 ALTER TABLE ROOT.ALBUMS ALTER COLUMN ID RESTART WITH 0;

---------- Cr�ation d'un administrateur par d�faut ----------
-- LOGIN (mail):		admin
-- PASSWORD:			admin
-- /!\ Ne pas oublier de supprimer cette entr�e apr�s  /!\ --
-- /!\ avoir cr�� votre administrateur via l'interface /!\ --
-- /!\ principale                                      /!\ --
INSERT INTO ROOT.ALBUMS
		(ID_USR, "NAME", VISIBILITY) 
	VALUES (0, 'Holidays', 0);