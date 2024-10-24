create table patient (
    id  serial primary key,
    description TEXT,
    first_name TEXT,
    last_name TEXT,
    middle_name TEXT,
    phone TEXT,
    birth_date TEXT,
    next_visit timestamp(6)
);
create table visit (
     id  serial primary key,
     visit_date timestamp(6),
     visit_id bigint,
     anamnesis TEXT,
     complaints TEXT,
     glob_bio integer,
     glob_neiro_postural integer,
     glob_neiro_psihosomat integer,
     glob_right_breath integer,
     glob_rithm_kardio integer,
     glob_rithm_karnial integer,
     region_brest_struct integer,
     region_brest_vistz integer,
     region_cr integer,
     region_dura_mater_struct integer,
     region_hands_struct integer,
     region_head_struct integer,
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
     regionc1c3som integer,
     regionc1c3vistz integer,
     regionc4c6som integer,
     regionc4c6vistz integer,
     regionc7th1som integer,
     regionc7th1vistz integer,
     regionl2l5som integer,
     regionl2l5vistz integer,
     dominant TEXT,
     local_disfunction TEXT,
     recommendations TEXT,
     specialists TEXT,
     treatment_plan TEXT
 );
alter table if exists visit
   add constraint FKk33uqa3qd4lj5gsps1bbv3s13
   foreign key (visit_id)
   references patient;

create table audit (
    id  serial primary key,
    tm timestamp,
    rec_type TEXT,
    patient_id bigint
);
