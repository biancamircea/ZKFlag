--
-- PostgreSQL database dump
--

-- Dumped from database version 14.15 (Homebrew)
-- Dumped by pg_dump version 14.15 (Homebrew)

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

--
-- Name: api_token_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.api_token_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.api_token_sequence OWNER TO bianca;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: api_tokens; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.api_tokens (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    expires_at timestamp(6) without time zone,
    name character varying(255) NOT NULL,
    secret character varying(255) NOT NULL,
    environment_id bigint NOT NULL,
    project_id bigint NOT NULL,
    instance_id bigint,
    type bigint
);


ALTER TABLE public.api_tokens OWNER TO bianca;

--
-- Name: applications; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.applications (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    description character varying(255),
    name character varying(255) NOT NULL,
    url character varying(255)
);


ALTER TABLE public.applications OWNER TO bianca;

--
-- Name: applications_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.applications_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.applications_sequence OWNER TO bianca;

--
-- Name: constraint_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.constraint_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.constraint_sequence OWNER TO bianca;

--
-- Name: constraint_value_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.constraint_value_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.constraint_value_sequence OWNER TO bianca;

--
-- Name: constraint_values; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.constraint_values (
    id bigint NOT NULL,
    value character varying(255),
    constraint_id bigint NOT NULL,
    toggle_environment_id bigint
);


ALTER TABLE public.constraint_values OWNER TO bianca;

--
-- Name: constraints; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.constraints (
    id bigint NOT NULL,
    operator character varying(255),
    context_field_id bigint,
    toggle_id bigint NOT NULL,
    isConfidential bigint
);


ALTER TABLE public.constraints OWNER TO bianca;

--
-- Name: context_field_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.context_field_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.context_field_sequence OWNER TO bianca;

--
-- Name: context_fields; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.context_fields (
    id bigint NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL,
    project_id bigint,
    hash_id character varying(255),
    isConfidential bigint
);


ALTER TABLE public.context_fields OWNER TO bianca;

--
-- Name: environments; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.environments (
    id bigint NOT NULL,
    enabled boolean,
    name character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    hash_id character varying(255)
);


ALTER TABLE public.environments OWNER TO bianca;

--
-- Name: environments_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.environments_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.environments_sequence OWNER TO bianca;

--
-- Name: events; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.events (
    id bigint NOT NULL,
    action smallint,
    created_at timestamp(6) without time zone,
    environment_id bigint,
    project_id bigint,
    toggle_id bigint,
    instance_id bigint
);


ALTER TABLE public.events OWNER TO bianca;

--
-- Name: events_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.events_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.events_sequence OWNER TO bianca;

--
-- Name: instance_environment; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.instance_environment (
    instance_id bigint NOT NULL,
    environment_id bigint NOT NULL,
    active boolean DEFAULT false NOT NULL
);


ALTER TABLE public.instance_environment OWNER TO bianca;

--
-- Name: instances; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.instances (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    project_id bigint NOT NULL,
    started_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    hash_id character varying(255)
);


ALTER TABLE public.instances OWNER TO bianca;

--
-- Name: instances_id_seq; Type: SEQUENCE; Schema: public; Owner: bianca
--

ALTER TABLE public.instances ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.instances_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: instances_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.instances_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.instances_sequence OWNER TO bianca;

--
-- Name: project_environment; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.project_environment (
    environment_id bigint NOT NULL,
    project_id bigint NOT NULL,
    active boolean
);


ALTER TABLE public.project_environment OWNER TO bianca;

--
-- Name: project_roles_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.project_roles_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.project_roles_sequence OWNER TO bianca;

--
-- Name: projects; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.projects (
    id bigint NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL,
    hash_id character varying(255)
);


ALTER TABLE public.projects OWNER TO bianca;

--
-- Name: projects_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.projects_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.projects_sequence OWNER TO bianca;

--
-- Name: roles; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.roles (
    id bigint NOT NULL,
    description character varying(255),
    role_type character varying(255)
);


ALTER TABLE public.roles OWNER TO bianca;

--
-- Name: roles_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.roles_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.roles_sequence OWNER TO bianca;

--
-- Name: tags; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.tags (
    id bigint NOT NULL,
    color character varying(255),
    description character varying(255),
    label_name character varying(255) NOT NULL,
    project_id bigint,
    hash_id character varying(255)
);


ALTER TABLE public.tags OWNER TO bianca;

--
-- Name: tags_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.tags_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tags_sequence OWNER TO bianca;

--
-- Name: toggle_environment; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.toggle_environment (
    id bigint NOT NULL,
    disabled_value character varying(255),
    enabled boolean,
    enabled_value character varying(255),
    environment_id bigint,
    toggle_id bigint,
    start_off time without time zone,
    start_on time without time zone,
    end_date date,
    start_date date,
    instance_id bigint
);


ALTER TABLE public.toggle_environment OWNER TO bianca;

--
-- Name: toggle_environment_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.toggle_environment_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.toggle_environment_sequence OWNER TO bianca;

--
-- Name: toggle_tag; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.toggle_tag (
    tag_id bigint NOT NULL,
    toggle_id bigint NOT NULL
);


ALTER TABLE public.toggle_tag OWNER TO bianca;

--
-- Name: toggles; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.toggles (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    description character varying(255),
    name character varying(255) NOT NULL,
    project_id bigint NOT NULL,
    toggle_type integer,
    hash_id character varying(255)
);


ALTER TABLE public.toggles OWNER TO bianca;

--
-- Name: toggles_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.toggles_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.toggles_sequence OWNER TO bianca;

--
-- Name: user_instance; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.user_instance (
    user_id bigint NOT NULL,
    instance_id bigint NOT NULL,
    added_at timestamp without time zone
);


ALTER TABLE public.user_instance OWNER TO bianca;

--
-- Name: user_project; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.user_project (
    project_id bigint NOT NULL,
    user_id bigint NOT NULL,
    added_at timestamp(6) without time zone
);


ALTER TABLE public.user_project OWNER TO bianca;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO bianca;

--
-- Name: users; Type: TABLE; Schema: public; Owner: bianca
--

CREATE TABLE public.users (
    id bigint DEFAULT nextval('public.users_id_seq'::regclass) NOT NULL,
    created_at timestamp(6) without time zone DEFAULT now(),
    email character varying(255) NOT NULL,
    name character varying(255),
    password character varying(255),
    role_id bigint NOT NULL,
    hash_id character varying(255)
);


ALTER TABLE public.users OWNER TO bianca;

--
-- Name: users_sequence; Type: SEQUENCE; Schema: public; Owner: bianca
--

CREATE SEQUENCE public.users_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_sequence OWNER TO bianca;

--
-- Name: api_tokens api_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.api_tokens
    ADD CONSTRAINT api_tokens_pkey PRIMARY KEY (id);


--
-- Name: applications applications_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.applications
    ADD CONSTRAINT applications_pkey PRIMARY KEY (id);


--
-- Name: constraint_values constraint_values_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.constraint_values
    ADD CONSTRAINT constraint_values_pkey PRIMARY KEY (id);


--
-- Name: constraints constraints_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.constraints
    ADD CONSTRAINT constraints_pkey PRIMARY KEY (id);


--
-- Name: context_fields context_fields_hash_id_key; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.context_fields
    ADD CONSTRAINT context_fields_hash_id_key UNIQUE (hash_id);


--
-- Name: context_fields context_fields_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.context_fields
    ADD CONSTRAINT context_fields_pkey PRIMARY KEY (id);


--
-- Name: environments environments_hash_id_key; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.environments
    ADD CONSTRAINT environments_hash_id_key UNIQUE (hash_id);


--
-- Name: environments environments_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.environments
    ADD CONSTRAINT environments_pkey PRIMARY KEY (id);


--
-- Name: events events_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_pkey PRIMARY KEY (id);


--
-- Name: instance_environment instance_environment_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.instance_environment
    ADD CONSTRAINT instance_environment_pkey PRIMARY KEY (instance_id, environment_id);


--
-- Name: instances instances_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.instances
    ADD CONSTRAINT instances_pkey PRIMARY KEY (id);


--
-- Name: project_environment project_environment_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.project_environment
    ADD CONSTRAINT project_environment_pkey PRIMARY KEY (environment_id, project_id);


--
-- Name: projects projects_hash_id_key; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_hash_id_key UNIQUE (hash_id);


--
-- Name: projects projects_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_pkey PRIMARY KEY (id);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: tags tags_hash_id_key; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.tags
    ADD CONSTRAINT tags_hash_id_key UNIQUE (hash_id);


--
-- Name: tags tags_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.tags
    ADD CONSTRAINT tags_pkey PRIMARY KEY (id);


--
-- Name: toggle_environment toggle_environment_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.toggle_environment
    ADD CONSTRAINT toggle_environment_pkey PRIMARY KEY (id);


--
-- Name: toggle_tag toggle_tag_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.toggle_tag
    ADD CONSTRAINT toggle_tag_pkey PRIMARY KEY (tag_id, toggle_id);


--
-- Name: toggles toggles_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.toggles
    ADD CONSTRAINT toggles_pkey PRIMARY KEY (id);


--
-- Name: users uk_6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: roles uk_6kpjgt1lwdofsckw70uo9eo0; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT uk_6kpjgt1lwdofsckw70uo9eo0 UNIQUE (role_type);


--
-- Name: environments uk_egbp7nsmcafke1djorxtxsi3u; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.environments
    ADD CONSTRAINT uk_egbp7nsmcafke1djorxtxsi3u UNIQUE (name);


--
-- Name: user_instance user_instance_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.user_instance
    ADD CONSTRAINT user_instance_pkey PRIMARY KEY (user_id, instance_id);


--
-- Name: user_project user_project_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.user_project
    ADD CONSTRAINT user_project_pkey PRIMARY KEY (project_id, user_id);


--
-- Name: users users_hash_id_key; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_hash_id_key UNIQUE (hash_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: instances_hash_id_key; Type: INDEX; Schema: public; Owner: bianca
--

CREATE UNIQUE INDEX instances_hash_id_key ON public.instances USING btree (hash_id);


--
-- Name: events fk1lob8q4jn7mu787xcpjeeahft; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT fk1lob8q4jn7mu787xcpjeeahft FOREIGN KEY (environment_id) REFERENCES public.environments(id) ON DELETE CASCADE;


--
-- Name: constraints fk37ycwag8qys68si3cjdr0ynl8; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.constraints
    ADD CONSTRAINT fk37ycwag8qys68si3cjdr0ynl8 FOREIGN KEY (context_field_id) REFERENCES public.context_fields(id);


--
-- Name: toggles fk3mnscvrroxtu5wsskiupmk7iv; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.toggles
    ADD CONSTRAINT fk3mnscvrroxtu5wsskiupmk7iv FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


--
-- Name: project_environment fk4eccherw1cm9ecy14eqni6quw; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.project_environment
    ADD CONSTRAINT fk4eccherw1cm9ecy14eqni6quw FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


--
-- Name: toggle_environment fk6cgg2f1nicvupqhtx803ofnv2; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.toggle_environment
    ADD CONSTRAINT fk6cgg2f1nicvupqhtx803ofnv2 FOREIGN KEY (environment_id) REFERENCES public.environments(id) ON DELETE CASCADE;


--
-- Name: project_environment fk6yo777w3ojpcohmry6m74hr5u; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.project_environment
    ADD CONSTRAINT fk6yo777w3ojpcohmry6m74hr5u FOREIGN KEY (environment_id) REFERENCES public.environments(id) ON DELETE CASCADE;


--
-- Name: events fk8gdu3b5mr1ih5x5pvshxccebx; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT fk8gdu3b5mr1ih5x5pvshxccebx FOREIGN KEY (toggle_id) REFERENCES public.toggles(id) ON DELETE CASCADE;


--
-- Name: constraint_values fk_constraint_values_toggle_env; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.constraint_values
    ADD CONSTRAINT fk_constraint_values_toggle_env FOREIGN KEY (toggle_environment_id) REFERENCES public.toggle_environment(id) ON DELETE CASCADE;


--
-- Name: events fk_events_project; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT fk_events_project FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


--
-- Name: user_instance fk_instance; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.user_instance
    ADD CONSTRAINT fk_instance FOREIGN KEY (instance_id) REFERENCES public.instances(id) ON DELETE CASCADE;


--
-- Name: api_tokens fk_instance_api_token; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.api_tokens
    ADD CONSTRAINT fk_instance_api_token FOREIGN KEY (instance_id) REFERENCES public.instances(id) ON DELETE CASCADE;


--
-- Name: events fk_instance_id; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT fk_instance_id FOREIGN KEY (instance_id) REFERENCES public.instances(id) ON DELETE CASCADE;


--
-- Name: toggle_environment fk_instance_toggle_environment; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.toggle_environment
    ADD CONSTRAINT fk_instance_toggle_environment FOREIGN KEY (instance_id) REFERENCES public.instances(id) ON DELETE CASCADE;


--
-- Name: constraints fk_toggle_constraints; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.constraints
    ADD CONSTRAINT fk_toggle_constraints FOREIGN KEY (toggle_id) REFERENCES public.toggles(id) ON DELETE CASCADE;


--
-- Name: user_instance fk_user; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.user_instance
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: api_tokens fkb2dndtacy54iqvdjxqi5w4e88; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.api_tokens
    ADD CONSTRAINT fkb2dndtacy54iqvdjxqi5w4e88 FOREIGN KEY (environment_id) REFERENCES public.environments(id) ON DELETE CASCADE;


--
-- Name: user_project fkc74un5y8u03pxfbvjdvm3kg06; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.user_project
    ADD CONSTRAINT fkc74un5y8u03pxfbvjdvm3kg06 FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


--
-- Name: toggle_tag fkfctn79jda4d9j3ldkef29isqd; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.toggle_tag
    ADD CONSTRAINT fkfctn79jda4d9j3ldkef29isqd FOREIGN KEY (tag_id) REFERENCES public.tags(id);


--
-- Name: toggle_tag fkfun0hqtrt03sovbfxfgghjbth; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.toggle_tag
    ADD CONSTRAINT fkfun0hqtrt03sovbfxfgghjbth FOREIGN KEY (toggle_id) REFERENCES public.toggles(id) ON DELETE CASCADE;


--
-- Name: user_project fkjoreo8pojddvrp3cr4x8b610b; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.user_project
    ADD CONSTRAINT fkjoreo8pojddvrp3cr4x8b610b FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: api_tokens fkmkerw53xdjveove22xn69x8gp; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.api_tokens
    ADD CONSTRAINT fkmkerw53xdjveove22xn69x8gp FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


--
-- Name: toggle_environment fko94d8jrn7x1ti9l3601v2xqe7; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.toggle_environment
    ADD CONSTRAINT fko94d8jrn7x1ti9l3601v2xqe7 FOREIGN KEY (toggle_id) REFERENCES public.toggles(id) ON DELETE CASCADE;


--
-- Name: constraint_values fkoeersfnvre4hc2873aeeagtew; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.constraint_values
    ADD CONSTRAINT fkoeersfnvre4hc2873aeeagtew FOREIGN KEY (constraint_id) REFERENCES public.constraints(id);


--
-- Name: users fkp56c1712k691lhsyewcssf40f; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fkp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- Name: tags fkq3jsgk94g33jvd4src5q2xn4u; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.tags
    ADD CONSTRAINT fkq3jsgk94g33jvd4src5q2xn4u FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


--
-- Name: context_fields fkt2w8y9nhecxbrqr7xp988telb; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.context_fields
    ADD CONSTRAINT fkt2w8y9nhecxbrqr7xp988telb FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


--
-- Name: instance_environment instance_environment_environment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.instance_environment
    ADD CONSTRAINT instance_environment_environment_id_fkey FOREIGN KEY (environment_id) REFERENCES public.environments(id) ON DELETE CASCADE;


--
-- Name: instance_environment instance_environment_instance_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.instance_environment
    ADD CONSTRAINT instance_environment_instance_id_fkey FOREIGN KEY (instance_id) REFERENCES public.instances(id) ON DELETE CASCADE;


--
-- Name: instances instances_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: bianca
--

ALTER TABLE ONLY public.instances
    ADD CONSTRAINT instances_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

