DROP TABLE IF EXISTS tweet;
DROP TABLE IF EXISTS result;

-- ----------------------------------------------------------------------------
-- tweet
-- ----------------------------------------------------------------------------
create table tweet (
	id bigint,

	-- the content of the tweet
	text text NOT NULL,
	
	-- the text optimized for text searches. This field is automatically filled with the trigger 'tsvectorupdate'
	text_tsvector tsvector,
	
	-- the preprocessed version of the tweet
	preprocessed text,
	
	-- the date when the tweet has been tweeted
	created_at date NOT NULL default CURRENT_DATE
);

create index tweet_id_idx on tweet(id);
create index tweet_text_tsvector_gin_idx ON tweet USING GIN(text_tsvector);
create index tweet_date_idx on tweet(created_at);
create index preprocessed_idx on tweet(preprocessed);

-- create a trigger to automatically fill the ts-vector
create trigger tsvectorupdate BEFORE INSERT OR UPDATE ON tweet FOR EACH ROW EXECUTE PROCEDURE tsvector_update_trigger('text_tsvector', 'pg_catalog.english', 'text');

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
	