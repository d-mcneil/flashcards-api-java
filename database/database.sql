DROP TABLE IF EXISTS users_deck_practice_settings, card, deck, users CASCADE;

CREATE TABLE users (
	user_id SERIAL,
	username VARCHAR(63) NOT NULL,
	hashed_password CHAR(60) NOT NULL,
	first_name VARCHAR(63) NOT NULL,
	last_name VARCHAR(63) NOT NULL,
	email VARCHAR(127) NOT NULL,
	joined_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	is_user_active BOOLEAN NOT NULL DEFAULT true,
	CONSTRAINT PK_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

CREATE TABLE deck (
	deck_id BIGSERIAL,
	owner_user_id INTEGER NOT NULL,
	-- owner_user_id INTEGER, -- If I want decks that belong to a delete account to persist, then I need to use this line instead.
	deck_name VARCHAR(511) NOT NULL,
	deck_description TEXT NOT NULL DEFAULT '',
	deck_created_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	is_deck_public BOOLEAN NOT NULL DEFAULT false,
	is_deck_deleted BOOLEAN NOT NULL DEFAULT false,
	CONSTRAINT PK_deck PRIMARY KEY (deck_id),
	CONSTRAINT FK_deck_user FOREIGN KEY (owner_user_id) REFERENCES users (user_id) ON DELETE CASCADE
	-- CONSTRAINT FK_deck_user FOREIGN KEY (owner_user_id) REFERENCES user ON DELETE SET NULL (user_id) -- If I want decks that belong to a delete account to persist, then I need to use this line instead.
);

CREATE TABLE card (
	card_id BIGSERIAL,
	deck_id BIGINT NOT NULL,
	card_term VARCHAR(511) NOT NULL,
	card_definition TEXT NOT NULL DEFAULT '',
	card_score INTEGER NOT NULL DEFAULT 0,
	card_created_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	is_card_deleted BOOLEAN NOT NULL DEFAULT false,
	CONSTRAINT PK_card PRIMARY KEY (card_id),
	CONSTRAINT FK_card_deck FOREIGN KEY (deck_id) REFERENCES deck (deck_id) ON DELETE CASCADE
);

CREATE TABLE users_deck_practice_settings (
	-- Users can add decks that they aren't owners of to their collection in order to practice them.
	-- Each user can choose their own settings for practicing a deck whether or not they are the owner of the deck.
	user_id INTEGER,
	deck_id BIGINT,
	is_definition_first BOOLEAN NOT NULL DEFAULT FALSE,
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
	CONSTRAINT PK_users_deck_practice_settings PRIMARY KEY (user_id, deck_id),
	CONSTRAINT FK_users_deck_practice_settings_deck FOREIGN KEY (deck_id) REFERENCES deck (deck_id) ON DELETE CASCADE,
	CONSTRAINT FK_users_deck_practice_settings_users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
	CONSTRAINT CHK_practice_deck_percentage CHECK (practice_deck_percentage BETWEEN 1 AND 100),
	CONSTRAINT CHK_term_language_code CHECK (term_language_code ~* '[a-z]{2}-[A-Z]{2}'),
	CONSTRAINT CHK_definition_language_code CHECK (definition_language_code ~* '[a-z]{2}-[A-Z]{2}')
);

-- **************************************************************************************************************************************************************
-- I ended up realizing that this table wasn't really necessary, because it was all covered in the practice settings table, but I wanted to keep it just in case.
-- **************************************************************************************************************************************************************
-- CREATE TABLE deck_users ( -- ASSOCIATIVE TABLE
-- 	-- For relationships between decks and users when A USER THAT IS NOT THE OWNER OF A DECK has added that deck to their collection.
-- 	-- The use case for this is if a user wants to practice somebody else's deck and keep up with the changes they make to that deck.
-- 	-- They would do this as an alternative to making themself a copy of the deck and becoming the owner of the new, copied deck.
-- 	-- A user that is not the owner of a deck cannot make changes to the deck.
-- 	deck_id BIGINT,
-- 	non_owner_user_id INTEGER,
-- 	CONSTRAINT PK_deck_users PRIMARY KEY (deck_id, non_owner_user_id),
-- 	CONSTRAINT FK_deck_users_deck FOREIGN KEY (deck_id) REFERENCES deck (deck_id) ON DELETE CASCADE,
-- 	CONSTRAINT FK_deck_users_user FOREIGN KEY (non_owner_user_id) REFERENCES users (user_id) ON DELETE CASCADE
-- );
