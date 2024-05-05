create table patient (
    id bigint not null,
    description varchar(255),
    first_name varchar(255),
    last_name varchar(255),
    middle_name varchar(255),
    phone varchar(255),
    primary key (id)
)
create table visit (
    id bigint not null,
    anamnesis varchar(255),
    complaints varchar(255),
    dominant varchar(255),
    glob_bio integer,
    glob_neiro_postural integer,
    glob_neiro_psihosomat integer,
    glob_right_breath integer,
    glob_rithm_kardio integer,
    glob_rithm_karnial integer,
    local_disfunction varchar(255),
    recommendations varchar(255),
    region_brest_struct integer,
    region_brest_vistz integer,
    regionc1c3som integer,
    regionc1c3vistz integer,
    regionc4c6som integer,
    regionc4c6vistz integer,
    regionc7th1som integer,
    regionc7th1vistz integer,
    region_cr integer,
    region_dura_mater_struct integer,
    region_hands_struct integer,
    region_head_struct integer,
    regionl2l5som integer,
    regionl2l5vistz integer,
    region_legs_struct integer,
    region_lower_back_struct integer,
    region_lower_back_vistz integer,
    region_neck_struct integer,
    region_neck_vistz integer,
    region_pelvic_struct integer,
    region_pelvic_vistz integer,
    region_th10l1som integer,
    region_th10l1vistz integer,
    region_th2th5som integer,
    region_th2th5vistz integer,
    region_th6th9som integer,
    region_th6th9vistz integer,
    specialists varchar(255),
    treatment_plan varchar(255),
    visit_date varchar(255),
    visit_id bigint,
    primary key (id)
)
create sequence patient_seq start with 1 increment by 50
create sequence visit_seq start with 1 increment by 50
alter table if exists visit
   add constraint FKk33uqa3qd4lj5gsps1bbv3s13
   foreign key (visit_id)
   references patient