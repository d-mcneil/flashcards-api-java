BEGIN TRANSACTION;

CREATE EXTENSION IF NOT EXISTS citext;

DROP TABLE IF EXISTS deck_users, practice_settings, card, deck, login, users CASCADE;
DROP SEQUENCE IF EXISTS seq_user_id, seq_deck_id, seq_card_id, seq_settings_id;

CREATE SEQUENCE seq_user_id
    INCREMENT BY 1
    START WITH 1001
    NO MAXVALUE;

CREATE SEQUENCE seq_deck_id
    INCREMENT BY 1
    START WITH 2001
    NO MAXVALUE;

CREATE SEQUENCE seq_card_id
    INCREMENT BY 1
    START WITH 3001
    NO MAXVALUE;

CREATE SEQUENCE seq_settings_id
    INCREMENT BY 1
    START WITH 4001
    NO MAXVALUE;

CREATE TABLE users (
	user_id INTEGER NOT NULL DEFAULT nextval('seq_user_id'),
	username citext NOT NULL,
	first_name VARCHAR(63) NOT NULL,
	last_name VARCHAR(63) NOT NULL,
	email VARCHAR(127) NOT NULL,
	joined_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	is_user_active BOOLEAN NOT NULL DEFAULT true,
	CONSTRAINT PK_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username),
	CONSTRAINT CHK_username CHECK (LENGTH(username) > 0 AND LENGTH(username) <= 63),
	CONSTRAINT CHK_first_name CHECK (LENGTH(first_name) > 0),
	CONSTRAINT CHK_last_name CHECK (LENGTH(last_name) > 0),
	CONSTRAINT CHK_email CHECK (
	    LENGTH(email) >= 5 AND
	    LENGTH(email) <= 127 AND
	    email LIKE '%@%.%'
	)
);

CREATE TABLE login (
    user_id INTEGER,
	hashed_password CHAR(60) NOT NULL,
	CONSTRAINT PK_login PRIMARY KEY (user_id),
	CONSTRAINT FK_login_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
	CONSTRAINT CHK_hashed_password CHECK (LENGTH(hashed_password) = 60)
);

CREATE TABLE deck (
	deck_id BIGINT NOT NULL DEFAULT nextval('seq_deck_id'),
	owner_user_id INTEGER NOT NULL,
	-- owner_user_id INTEGER, -- If I want decks that belong to a delete account to persist, then I need to use this line instead.
	deck_name VARCHAR(511) NOT NULL,
	deck_description TEXT NOT NULL DEFAULT '',
	deck_created_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	is_deck_public BOOLEAN NOT NULL DEFAULT false,
	is_deck_deleted BOOLEAN NOT NULL DEFAULT false,
	CONSTRAINT PK_deck PRIMARY KEY (deck_id),
	CONSTRAINT FK_deck_user FOREIGN KEY (owner_user_id) REFERENCES users (user_id) ON DELETE CASCADE
	-- CONSTRAINT FK_deck_user FOREIGN KEY (owner_user_id) REFERENCES user ON DELETE SET NULL (user_id) -- If I want decks that belong to a deleted account to persist, then I need to use this line instead.
);

CREATE TABLE card (
	card_id BIGINT NOT NULL DEFAULT nextval('seq_card_id'),
	deck_id BIGINT NOT NULL,
	card_term VARCHAR(511) NOT NULL,
	card_definition TEXT NOT NULL DEFAULT '',
	card_score INTEGER NOT NULL DEFAULT 0,
	card_created_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	is_card_deleted BOOLEAN NOT NULL DEFAULT false,
	CONSTRAINT PK_card PRIMARY KEY (card_id),
	CONSTRAINT FK_card_deck FOREIGN KEY (deck_id) REFERENCES deck (deck_id) ON DELETE CASCADE
);

-- Each user can choose their own settings for practicing a deck whether or not they are the owner of the deck.
CREATE TABLE practice_settings (
    settings_id BIGINT NOT NULL DEFAULT nextval('seq_settings_id'),
	is_definition_first BOOLEAN NOT NULL DEFAULT false,
	practice_deck_percentage SMALLINT NOT NULL DEFAULT 100,
	term_language_code CHAR(5) NOT NULL DEFAULT 'en-US',
	definition_language_code CHAR(5) NOT NULL DEFAULT 'en-US',
	term_language_name VARCHAR(255) NOT NULL DEFAULT 'Google US English',
	definition_language_name VARCHAR(255) NOT NULL DEFAULT 'Google US English',
    -- How choosing the language/voice works across browsers.
        -- If the language/voice name is present, use it.
        -- If the language/voice name isn't present, use the language code.
        -- If the language code isn't present, default to US English.
	should_read_out_on_flip BOOLEAN NOT NULL DEFAULT FALSE,
	should_read_out_on_next_card BOOLEAN NOT NULL DEFAULT FALSE,
	CONSTRAINT PK_practice_settings PRIMARY KEY (settings_id),
	CONSTRAINT CHK_practice_deck_percentage CHECK (practice_deck_percentage BETWEEN 1 AND 100),
	CONSTRAINT CHK_term_language_code CHECK (term_language_code ~* '[a-z]{2}-[A-Z]{2}'),
	CONSTRAINT CHK_definition_language_code CHECK (definition_language_code ~* '[a-z]{2}-[A-Z]{2}')
);

CREATE TABLE deck_users (
    -- For relationships between decks and users
        -- when a user is the owner of a deck
        -- OR
        -- when A USER THAT IS NOT THE OWNER OF A DECK has added that deck to their collection
            -- The use case for this is if a user wants to practice somebody else's deck and keep up with the changes they make to that deck.
            -- They would do this as an alternative to making themself a copy of the deck and becoming the owner of the new, copied deck.
            -- A user that is not the owner of a deck cannot make changes to the deck, but they do have their own practice settings for the deck.
    deck_id BIGINT,
    user_id INTEGER,
    settings_id BIGINT,
    CONSTRAINT PK_deck_users PRIMARY KEY (deck_id, user_id),
    CONSTRAINT FK_deck_users_deck FOREIGN KEY (deck_id) REFERENCES deck (deck_id) ON DELETE CASCADE,
    CONSTRAINT FK_deck_users_users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT FK_deck_users_practice_settings FOREIGN KEY (settings_id) REFERENCES practice_settings (settings_id) ON DELETE CASCADE
);


-- *********************** FUNCTIONS AND TRIGGERS ***************************************************************
--
-- **************************************************************************************************************
-- After a new deck is inserted, a new entry should be put into deck_users that links the new deck and its owner.
-- This is not redundant data, because users can practice decks that they don't own themselves.
-- This is kept track of in the deck_users table, and that table also associates practice settings to that
-- user/deck combination. But, because users (obvviously) can practice decks they own, that link needs to be made
-- in the deck_users table as well, because that is what is used by the application to grab the list of decks
-- that are available to practice.
-- **************************************************************************************************************
--CREATE OR REPLACE FUNCTION function_after_insert_on_deck_insert_into_deck_users()
--RETURNS TRIGGER
--LANGUAGE plpgsql AS
--$$
--	BEGIN
--		INSERT INTO deck_users (deck_id, user_id)
--		VALUES (NEW.deck_id, NEW.owner_user_id);
--		RETURN NEW;
--	END;
--$$;
--
--CREATE TRIGGER trigger_after_insert_on_deck
--AFTER INSERT ON deck
--FOR EACH ROW
--EXECUTE FUNCTION function_after_insert_on_deck_insert_into_deck_users();


-- **************************************************************************************************************
-- After a record is entered into deck_users (i.e., there is a practice pairing made between a deck and a user, 
-- whether or not the user is the owner of the deck), a new record should be inserted into the practice_settings
-- table with the default settings, and then that settings_id needs to be input into the corresponding row in the
-- deck_users table.
-- **************************************************************************************************************
--CREATE OR REPLACE FUNCTION function_insert_default_practice_settings()
--RETURNS BIGINT
--LANGUAGE plpgsql AS
--$$
--	DECLARE
--		new_settings_id BIGINT;
--	BEGIN
--		INSERT INTO practice_settings
--		DEFAULT VALUES
--		RETURNING settings_id
--		INTO new_settings_id;
--
--		RETURN new_settings_id;
--	END;
--$$;
--
--CREATE OR REPLACE FUNCTION
--function_after_insert_on_deck_users_insert_default_practice_settings_and_update_deck_users_with_settings_id()
--RETURNS TRIGGER
--LANGUAGE plpgsql AS
--$$
--	BEGIN
--		NEW.settings_id := function_insert_default_practice_settings();
--		RETURN NEW;
--	END;
--$$;
--
--CREATE TRIGGER trigger_after_insert_on_deck_users
--BEFORE INSERT ON deck_users
--FOR EACH ROW
--EXECUTE FUNCTION
--function_after_insert_on_deck_users_insert_default_practice_settings_and_update_deck_users_with_settings_id();


-- **************************************************************************************************************
-- When an association between a user and a deck is removed (whether it be from deleting a user, deleting a deck,
-- of a user removing another user's deck from their collection), the practice settings that
-- correspond to that relation need to be deleted also. Otherwise, there would be data no longer
-- being used that would persist in the practice_settings table.
-- **************************************************************************************************************
--CREATE OR REPLACE FUNCTION
--function_before_delete_deck_users_delete_practice_settings()
--RETURNS TRIGGER
--LANGUAGE plpgsql AS
--$$
--	BEGIN
--		DELETE FROM practice_settings
--		WHERE practice_settings.settings_id = OLD.settings_id;
--		RETURN OLD;
--	END;
--$$;
--
--CREATE TRIGGER trigger_before_delete_deck_users
--AFTER DELETE ON deck_users
--FOR EACH ROW
--EXECUTE FUNCTION
--function_before_delete_deck_users_delete_practice_settings();


COMMIT TRANSACTION;
