DROP TABLE IF EXISTS tweet;

-- ----------------------------------------------------------------------------
-- tweet
-- ----------------------------------------------------------------------------
create table tweet (
	id bigint,

	-- the content of the tweet
	text text NOT NULL,
	
	-- the preprocessed version of the tweet
	preprocessed text
);

create index id_idx on tweet(id);
create index text_idx on tweet(text);
create index preprocessed_idx on tweet(preprocessed);

-- ----------------------------------------------------------------------------
-- result
-- ----------------------------------------------------------------------------
create table result (
	id serial primary key NOT NULL,

	-- the term searched for
	query text NOT NULL,
	
	-- the score of the result
	score decimal NOT NULL,
	
	-- date when the result has been computed
	computed_at date NOT NULL default CURRENT_DATE	
);

create index res_id_idx on result(id);
create index res_score_idx on result(score);
	