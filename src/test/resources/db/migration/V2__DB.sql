CREATE TABLE public.spam_checks (
    id bigint NOT NULL,
    "timestamp" timestamp(6) without time zone,
    ip character varying(255) COLLATE pg_catalog."default"
);


CREATE SEQUENCE public.spam_checks_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.spam_checks_seq OWNED BY public.spam_checks.id;

ALTER TABLE ONLY public.spam_checks
    ADD CONSTRAINT spam_checks_pkey PRIMARY KEY (id);


SELECT pg_catalog.setval('public.users_seq', 1, false);