SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;
SET default_tablespace = '';
SET default_table_access_method = heap;

CREATE TABLE public.admins (
    id bigint NOT NULL,
    password character varying(255) NOT NULL,
    username character varying(255) NOT NULL
);

CREATE SEQUENCE public.admins_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE public.blacklisted_jwt (
    id character varying(255) NOT NULL
);


CREATE TABLE public.restaurant_employee (
    id bigint NOT NULL,
    restaurant_id bigint NOT NULL,
    password character varying(255) NOT NULL,
    username character varying(255) NOT NULL
);



CREATE SEQUENCE public.restaurant_employee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.restaurant_employee_id_seq OWNED BY public.restaurant_employee.id;


CREATE TABLE public.restaurants (
    id bigint NOT NULL,
    name character varying(255) NOT NULL
);

CREATE SEQUENCE public.restaurants_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.users (
    id bigint NOT NULL,
    password character varying(255) NOT NULL,
    username character varying(255) NOT NULL
);


CREATE SEQUENCE public.users_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY public.restaurant_employee ALTER COLUMN id SET DEFAULT nextval('public.restaurant_employee_id_seq'::regclass);

SELECT pg_catalog.setval('public.admins_seq', 1, false);
SELECT pg_catalog.setval('public.restaurant_employee_id_seq', 1, false);
SELECT pg_catalog.setval('public.restaurants_seq', 1, false);
SELECT pg_catalog.setval('public.users_seq', 1, false);


ALTER TABLE ONLY public.admins
    ADD CONSTRAINT admins_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.blacklisted_jwt
    ADD CONSTRAINT blacklisted_jwt_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.restaurant_employee
    ADD CONSTRAINT restaurant_employee_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.restaurants
    ADD CONSTRAINT restaurants_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.restaurant_employee
    ADD CONSTRAINT fkaed7wlscdbtxc0gbr7hga35o4 FOREIGN KEY (restaurant_id) REFERENCES public.restaurants(id);


