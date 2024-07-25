package com.adins.dao.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MssDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(13, "com.adins.mss.dao");
        general(schema);
        new DaoGenerator().generateAll(schema, "mssbase/src/main/java");
    }

    private static void general(Schema schema) {

        //TR_COLLECTIONACTIVITY
        Entity collectionActivity = schema.addEntity("CollectionActivity").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");

        collectionActivity.setTableName("TR_COLLECTIONACTIVITY");

        collectionActivity.addStringProperty("uuid_collection_activity").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_collection_activity\")");
        collectionActivity.addStringProperty("uuid_task_h").codeBeforeField("@SerializedName(\"uuid_task_h\")");
        collectionActivity.addStringProperty("agreement_no").codeBeforeField("@SerializedName(\"agreement_no\")");
        collectionActivity.addStringProperty("branch_code").codeBeforeField("@SerializedName(\"branch_code\")");
        collectionActivity.addStringProperty("collector_name").codeBeforeField("@SerializedName(\"collector_name\")");
        collectionActivity.addStringProperty("activity").codeBeforeField("@SerializedName(\"activity\")");
        collectionActivity.addStringProperty("result").codeBeforeField("@SerializedName(\"result\")");
        collectionActivity.addStringProperty("notes").codeBeforeField("@SerializedName(\"notes\")");
        collectionActivity.addStringProperty("overdue_days").codeBeforeField("@SerializedName(\"overdue_days\")");
        collectionActivity.addDateProperty("activity_date").codeBeforeField("@SerializedName(\"activity_date\")");
        collectionActivity.addDateProperty("ptp_date").codeBeforeField("@SerializedName(\"ptp_date\")");
        collectionActivity.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        collectionActivity.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        collectionActivity.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        collectionActivity.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        collectionActivity.addDateProperty("next_plan_date").codeBeforeField("@SerializedName(\"next_plan_date\")");
        collectionActivity.addStringProperty("next_plan_action").codeBeforeField("@SerializedName(\"next_plan_action\")");

        //InstallmentSchedule TR_INSTALLMENTSCHEDULE
        Entity installmentSchedule = schema.addEntity("InstallmentSchedule").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        installmentSchedule.setTableName("TR_INSTALLMENTSCHEDULE");

        installmentSchedule.addStringProperty("uuid_installment_schedule").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_installment_schedule\")");
        installmentSchedule.addStringProperty("uuid_task_h").codeBeforeField("@SerializedName(\"uuid_task_h\")");
        installmentSchedule.addStringProperty("agreement_no").codeBeforeField("@SerializedName(\"agreement_no\")");
        installmentSchedule.addStringProperty("branch_code").codeBeforeField("@SerializedName(\"branch_code\")");
        installmentSchedule.addStringProperty("installment_no").codeBeforeField("@SerializedName(\"installment_no\")");
        installmentSchedule.addStringProperty("installment_amount").codeBeforeField("@SerializedName(\"installment_amount\")");
        installmentSchedule.addStringProperty("installment_paid_amount").codeBeforeField("@SerializedName(\"installment_paid_amount\")");
        installmentSchedule.addStringProperty("lc_instl_amount").codeBeforeField("@SerializedName(\"lc_instl_amount\")");
        installmentSchedule.addStringProperty("lc_instl_paid").codeBeforeField("@SerializedName(\"lc_instl_paid\")");
        installmentSchedule.addStringProperty("lc_instl_waived").codeBeforeField("@SerializedName(\"lc_instl_waived\")");
        installmentSchedule.addStringProperty("principal_amount").codeBeforeField("@SerializedName(\"principal_amount\")");
        installmentSchedule.addStringProperty("interest_amount").codeBeforeField("@SerializedName(\"interest_amount\")");
        installmentSchedule.addStringProperty("os_principal_amount").codeBeforeField("@SerializedName(\"os_principal_amount\")");
        installmentSchedule.addStringProperty("os_interest_amount").codeBeforeField("@SerializedName(\"os_interest_amount\")");
        installmentSchedule.addStringProperty("lc_days").codeBeforeField("@SerializedName(\"lc_days\")");
        installmentSchedule.addStringProperty("lc_admin_fee").codeBeforeField("@SerializedName(\"lc_admin_fee\")");
        installmentSchedule.addStringProperty("lc_admin_fee_paid").codeBeforeField("@SerializedName(\"lc_admin_fee_paid\")");
        installmentSchedule.addStringProperty("lc_admin_fee_waive").codeBeforeField("@SerializedName(\"lc_admin_fee_waive\")");
        installmentSchedule.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        installmentSchedule.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        installmentSchedule.addDateProperty("due_date").codeBeforeField("@SerializedName(\"due_date\")");
        installmentSchedule.addDateProperty("instl_paid_date").codeBeforeField("@SerializedName(\"instl_paid_date\")");

        //MS_MOBILEDATAFILES
        Entity mobileDataFiles = schema.addEntity("MobileDataFile").addImport("com.google.gson.annotations.Since").addImport("com.google.gson.annotations.SerializedName");
        mobileDataFiles.setTableName("MS_MOBILEDATAFILES");
        mobileDataFiles.addLongProperty("id_datafile").notNull().primaryKey().codeBeforeField("@SerializedName(\"idDatafile\")");
        mobileDataFiles.addStringProperty("is_active").codeBeforeField("@SerializedName(\"isActive\")");
        mobileDataFiles.addDateProperty("max_timestamp").codeBeforeField("@SerializedName(\"maxTimestamp\")");
        mobileDataFiles.addStringProperty("file_url").codeBeforeField("@SerializedName(\"fileUrl\")");
        mobileDataFiles.addStringProperty("alternate_file_url").codeBeforeField("@SerializedName(\"alternateFileUrl\")");
        mobileDataFiles.addStringProperty("hash_sha1").codeBeforeField("@SerializedName(\"hashSha1\")");
        mobileDataFiles.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usrCrt\")");
        mobileDataFiles.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtmCrt\")");
        mobileDataFiles.addStringProperty("usr_upd");
        mobileDataFiles.addDateProperty("dtm_upd");
        mobileDataFiles.addStringProperty("downloaded_file_path");
        mobileDataFiles.addBooleanProperty("import_flag");

        //MS_GENERALPARAMETER
        Entity generalParameter = schema.addEntity("GeneralParameter").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        generalParameter.setTableName("MS_GENERALPARAMETER");

        generalParameter.addStringProperty("uuid_general_parameter").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_general_parameter\")");
        generalParameter.addStringProperty("gs_value").codeBeforeField("@SerializedName(\"gs_value\")");
        generalParameter.addStringProperty("gs_code").codeBeforeField("@SerializedName(\"gs_code\")");
        generalParameter.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        generalParameter.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        generalParameter.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        generalParameter.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        Property fk_generalParameter_uuid_user = generalParameter.addStringProperty("uuid_user").notNull()
                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();


        //MS_GROUPUSER
        Entity groupUser = schema.addEntity("GroupUser").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        groupUser.setTableName("MS_GROUPUSER");

        groupUser.addStringProperty("uuid_group_user").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_group_user\")");
        groupUser.addStringProperty("group_id").codeBeforeField("@SerializedName(\"group_id\")");
        groupUser.addStringProperty("is_admin").codeBeforeField("@SerializedName(\"is_admin\")");
        groupUser.addStringProperty("group_name").codeBeforeField("@SerializedName(\"group_name\")");
        groupUser.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        groupUser.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        groupUser.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        groupUser.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        Property fk_groupUser_uuid_user = groupUser.addStringProperty("uuid_user")
                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();


        //MS_LOOKUP
        Entity lookup = schema.addEntity("Lookup").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        lookup.setTableName("MS_LOOKUP");

        Property uuid_lookup = lookup.addStringProperty("uuid_lookup").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_lookup\")").getProperty();
        lookup.addStringProperty("option_id").codeBeforeField("@SerializedName(\"option_id\")");
        Property code = lookup.addStringProperty("code").codeBeforeField("@SerializedName(\"code\")").getProperty();
        lookup.addStringProperty("value").codeBeforeField("@SerializedName(\"value\")");
        Property filter1 = lookup.addStringProperty("filter1").codeBeforeField("@SerializedName(\"filter1\")").getProperty();
        Property filter2 = lookup.addStringProperty("filter2").codeBeforeField("@SerializedName(\"filter2\")").getProperty();
        Property filter3 = lookup.addStringProperty("filter3").codeBeforeField("@SerializedName(\"filter3\")").getProperty();
        Property filter4 = lookup.addStringProperty("filter4").codeBeforeField("@SerializedName(\"filter4\")").getProperty();
        Property filter5 = lookup.addStringProperty("filter5").codeBeforeField("@SerializedName(\"filter5\")").getProperty();

        Property sequence = lookup.addIntProperty("sequence").codeBeforeField("@SerializedName(\"sequence\")").getProperty();
        //lookup.addStringProperty("lookup_id");
        lookup.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        lookup.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        lookup.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        lookup.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        Property fk_lookup_uuid_question_set = lookup.addStringProperty("uuid_question_set").codeBeforeField("@SerializedName(\"uuid_question_set\")").getProperty();
        //lookup.addStringProperty("group_name");
        Property lov_group =  lookup.addStringProperty("lov_group").codeBeforeField("@SerializedName(\"lov_group\")").getProperty();
        Property is_active = lookup.addStringProperty("is_active").codeBeforeField("@SerializedName(\"is_active\")").getProperty();
        Property is_deleted = lookup.addStringProperty("is_deleted").codeBeforeField("@SerializedName(\"is_deleted\")").getProperty();
        lookup.addStringProperty("flag_hardsync").codeBeforeField("@SerializedName(\"flag\")");

        Index indexLookup = new Index();
        indexLookup.addProperty(lov_group);
        indexLookup.addProperty(filter1);
        indexLookup.addProperty(filter2);
        indexLookup.addProperty(filter3);
        indexLookup.addProperty(filter4);
        indexLookup.addProperty(filter5);
        indexLookup.addProperty(is_active);
        indexLookup.addProperty(is_deleted);

        lookup.addIndex(indexLookup);

        Index indexLookup2 = new Index();
        indexLookup2.addProperty(code);
        indexLookup2.addProperty(lov_group);
        indexLookup2.addProperty(is_active);
        indexLookup2.addProperty(is_deleted);

        lookup.addIndex(indexLookup2);

        Index indexLookup3 = new Index();
        indexLookup3.addProperty(lov_group);
        indexLookup3.addProperty(filter1);
        indexLookup3.addProperty(filter2);
        indexLookup3.addProperty(filter3);
        indexLookup3.addProperty(filter4);
        indexLookup3.addProperty(is_active);
        indexLookup3.addProperty(is_deleted);

        lookup.addIndex(indexLookup3);

        Index indexLookup4 = new Index();
        indexLookup4.addProperty(lov_group);
        indexLookup4.addProperty(filter1);
        indexLookup4.addProperty(filter2);
        indexLookup4.addProperty(filter3);
        indexLookup4.addProperty(is_active);
        indexLookup4.addProperty(is_deleted);

        lookup.addIndex(indexLookup4);

        Index indexLookup5 = new Index();
        indexLookup5.addProperty(lov_group);
        indexLookup5.addProperty(filter1);
        indexLookup5.addProperty(filter2);
        indexLookup5.addProperty(is_active);
        indexLookup5.addProperty(is_deleted);

        lookup.addIndex(indexLookup5);

        Index indexLookup6 = new Index();
        indexLookup6.addProperty(lov_group);
        indexLookup6.addProperty(filter1);
        indexLookup6.addProperty(is_active);
        indexLookup6.addProperty(is_deleted);

        lookup.addIndex(indexLookup6);

        Index indexLookup7 = new Index();
        indexLookup7.addProperty(lov_group);
        indexLookup7.addProperty(is_active);
        indexLookup7.addProperty(is_deleted);

        lookup.addIndex(indexLookup7);

        //MS_SYNC
        Entity sync = schema.addEntity("Sync").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        sync.setTableName("MS_SYNC");

        sync.addStringProperty("uuid_sync").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_sync\")");
        sync.addStringProperty("tabel_name").codeBeforeField("@SerializedName(\"tabel_name\")");
        sync.addStringProperty("lov_group").codeBeforeField("@SerializedName(\"lov_group\")");
        sync.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        sync.addIntProperty("flag").codeBeforeField("@SerializedName(\"flag\")");
        sync.addStringProperty("flag_hardsync").codeBeforeField("@SerializedName(\"flag_hardsync\")");


        //MS_MENU
        Entity menu = schema.addEntity("Menu").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        menu.setTableName("MS_MENU");

        menu.addStringProperty("uuid_menu").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_menu\")");
        menu.addStringProperty("menu_id").codeBeforeField("@SerializedName(\"menu_id\")");
        menu.addStringProperty("flag_job").codeBeforeField("@SerializedName(\"flag_job\")");
        menu.addStringProperty("is_visible").codeBeforeField("@SerializedName(\"is_visible\")");
        menu.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        menu.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        menu.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        menu.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        Property fk_menu_uuid_user = menu.addStringProperty("uuid_user")
                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();

        //MS_PRINTITEM
        Entity printItem = schema.addEntity("PrintItem").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        printItem.setTableName("MS_PRINTITEM");

        printItem.addStringProperty("uuid_print_item").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_print_item\")");
        printItem.addStringProperty("print_type_id").codeBeforeField("@SerializedName(\"print_type_id\")");
        printItem.addStringProperty("print_item_label").codeBeforeField("@SerializedName(\"print_item_label\")");
        printItem.addStringProperty("question_group_id").codeBeforeField("@SerializedName(\"question_group_id\")");
        printItem.addStringProperty("question_id").codeBeforeField("@SerializedName(\"question_id\")");
        printItem.addIntProperty("print_item_order").codeBeforeField("@SerializedName(\"print_item_order\")");
        printItem.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        printItem.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        printItem.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        printItem.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        Property fk_printItem_uuid_scheme = printItem.addStringProperty("uuid_scheme")
                .codeBeforeField("@SerializedName(\"uuid_scheme\")").getProperty();


        //MS_QUESTIONSET
        Entity questionSet = schema.addEntity("QuestionSet").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        questionSet.setTableName("MS_QUESTIONSET");

        questionSet.addStringProperty("uuid_question_set").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_question_set\")");
        questionSet.addStringProperty("question_group_id").codeBeforeField("@SerializedName(\"question_group_id\")");
        questionSet.addStringProperty("question_group_name").codeBeforeField("@SerializedName(\"question_group_name\")");
        questionSet.addIntProperty("question_group_order").codeBeforeField("@SerializedName(\"question_group_order\")");
        questionSet.addStringProperty("question_id").codeBeforeField("@SerializedName(\"question_id\")");
        questionSet.addStringProperty("question_label").codeBeforeField("@SerializedName(\"question_label\")");
        questionSet.addIntProperty("question_order").codeBeforeField("@SerializedName(\"question_order\")");
        questionSet.addStringProperty("answer_type").codeBeforeField("@SerializedName(\"answer_type\")");
        questionSet.addStringProperty("option_answers").codeBeforeField("@SerializedName(\"option_answers\")");
        questionSet.addStringProperty("choice_filter").codeBeforeField("@SerializedName(\"choice_filter\")");
        questionSet.addStringProperty("is_mandatory").codeBeforeField("@SerializedName(\"is_mandatory\")");
        questionSet.addIntProperty("max_length").codeBeforeField("@SerializedName(\"max_length\")");
        questionSet.addStringProperty("is_visible").codeBeforeField("@SerializedName(\"is_visible\")");
        questionSet.addStringProperty("is_readonly").codeBeforeField("@SerializedName(\"is_readonly\")");
        questionSet.addStringProperty("regex").codeBeforeField("@SerializedName(\"regex\")");
        questionSet.addStringProperty("relevant_question").codeBeforeField("@SerializedName(\"relevant_question\")");
        questionSet.addStringProperty("calculate").codeBeforeField("@SerializedName(\"calculate\")");
        questionSet.addStringProperty("constraint_message").codeBeforeField("@SerializedName(\"constraint_message\")");
        questionSet.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        questionSet.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        questionSet.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        questionSet.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        questionSet.addStringProperty("identifier_name").codeBeforeField("@SerializedName(\"identifier_name\")");
        Property fk_QuestionSet_uuid_scheme = questionSet.addStringProperty("uuid_scheme")
                .codeBeforeField("@SerializedName(\"uuid_scheme\")").getProperty();
        questionSet.addStringProperty("lov_group").codeBeforeField("@SerializedName(\"lov_group\")");
        questionSet.addStringProperty("tag").codeBeforeField("@SerializedName(\"tag\")");
        questionSet.addStringProperty("is_holiday_allowed").codeBeforeField("@SerializedName(\"is_holiday_allowed\")");
        questionSet.addStringProperty("img_quality").codeBeforeField("@SerializedName(\"img_quality\")");
        questionSet.addStringProperty("question_validation").codeBeforeField("@SerializedName(\"question_validation\")");
        questionSet.addStringProperty("question_value").codeBeforeField("@SerializedName(\"question_value\")");
        questionSet.addStringProperty("validate_err_message").codeBeforeField("@SerializedName(\"validate_err_message\")");
        questionSet.addStringProperty("form_version").codeBeforeField("@SerializedName(\"form_version\")");
        questionSet.addStringProperty("relevant_mandatory").codeBeforeField("@SerializedName(\"relevant_mandatory\")");;


        //MS_SCHEME
        Entity scheme = schema.addEntity("Scheme").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        scheme.setTableName("MS_SCHEME");

        scheme.addStringProperty("uuid_scheme").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_scheme\")");
        scheme.addStringProperty("scheme_description").codeBeforeField("@SerializedName(\"scheme_description\")");
        scheme.addDateProperty("scheme_last_update").codeBeforeField("@SerializedName(\"scheme_last_update\")");
        scheme.addStringProperty("is_printable").codeBeforeField("@SerializedName(\"is_printable\")");
        scheme.addStringProperty("form_id").codeBeforeField("@SerializedName(\"form_id\")");

        scheme.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        scheme.addStringProperty("is_preview_server").codeBeforeField("@SerializedName(\"is_preview_server\")");
        scheme.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        scheme.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        scheme.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        scheme.addStringProperty("form_type").codeBeforeField("@SerializedName(\"form_type\")");
        scheme.addStringProperty("is_active").codeBeforeField("@SerializedName(\"is_active\")");
        scheme.addStringProperty("form_version").codeBeforeField("@SerializedName(\"form_version\")");


        //MS_TIMELINETYPE
        Entity timelineType = schema.addEntity("TimelineType").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        timelineType.setTableName("MS_TIMELINETYPE");

        timelineType.addStringProperty("uuid_timeline_type").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_timeline_type\")");
        timelineType.addStringProperty("timeline_description").codeBeforeField("@SerializedName(\"timeline_description\")");
        timelineType.addStringProperty("timeline_type").codeBeforeField("@SerializedName(\"timeline_type\")");
        timelineType.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        timelineType.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        timelineType.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        timelineType.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");


        //MS_USER
        Entity user = schema.addEntity("User").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        user.setTableName("MS_USER");

        user.addStringProperty("uuid_user").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_user\")");
        user.addStringProperty("flag_job").codeBeforeField("@SerializedName(\"flag_job\")");
        user.addByteArrayProperty("image_profile").codeBeforeField("@ExcludeFromGson \n\t @SerializedName(\"image_profile\")");
        user.addStringProperty("fullname").codeBeforeField("@SerializedName(\"fullname\")");
        user.addStringProperty("branch_id").codeBeforeField("@SerializedName(\"branch_id\")");
        user.addStringProperty("branch_name").codeBeforeField("@SerializedName(\"branch_name\")");
        user.addStringProperty("is_branch").codeBeforeField("@SerializedName(\"is_branch\")");
        user.addStringProperty("password").codeBeforeField("@SerializedName(\"password\")");
        user.addIntProperty("task_seq").codeBeforeField("@SerializedName(\"task_seq\")");
        user.addStringProperty("google_id").codeBeforeField("@SerializedName(\"google_id\")");
        user.addStringProperty("facebook_id").codeBeforeField("@SerializedName(\"facebook_id\")");
        user.addStringProperty("login_id").codeBeforeField("@SerializedName(\"login_id\")");
        user.addIntProperty("fail_count").codeBeforeField("@SerializedName(\"fail_count\")");
        user.addDateProperty("last_sync").codeBeforeField("@SerializedName(\"last_sync\")");
        user.addStringProperty("branch_address").codeBeforeField("@SerializedName(\"branch_address\")");
        user.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        user.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        user.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        user.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        user.addByteArrayProperty("image_cover").codeBeforeField("@ExcludeFromGson \n" +
                "\t @SerializedName(\"image_cover\")");
        user.addStringProperty("chg_pwd").codeBeforeField("@SerializedName(\"chg_pwd\")");
        user.addStringProperty("job_description").codeBeforeField("@SerializedName(\"job_description\")");
        user.addStringProperty("pwd_exp").codeBeforeField("@SerializedName(\"pwd_exp\")");
        user.addStringProperty("dealer_name").codeBeforeField("@SerializedName(\"dealer_name\")");
        user.addStringProperty("cash_limit").codeBeforeField("@SerializedName(\"cash_limit\")");
        user.addStringProperty("cash_on_hand").codeBeforeField("@SerializedName(\"cash_on_hand\")");
        user.addStringProperty("uuid_branch").codeBeforeField("@SerializedName(\"uuid_branch\")");
        user.addStringProperty("uuid_group").codeBeforeField("@SerializedName(\"uuid_group\")");
        user.addStringProperty("uuid_dealer").codeBeforeField("@SerializedName(\"uuid_dealer\")");
        user.addStringProperty("start_time").codeBeforeField("@SerializedName(\"start_time\")");
        user.addStringProperty("end_time").codeBeforeField("@SerializedName(\"end_time\")");
        user.addStringProperty("is_tracking").codeBeforeField("@SerializedName(\"is_tracking\")");
        user.addStringProperty("tracking_days").codeBeforeField("@SerializedName(\"tracking_days\")");
        user.addStringProperty("token_id_fcm").codeBeforeField("@SerializedName(\"token_id_fcm\")");
        user.addStringProperty("is_emergency").codeBeforeField("@SerializedName(\"is_emergency\")");
        user.addStringProperty("membershipProgramCode").codeBeforeField("@SerializedName(\"membershipProgramCode\")");
        user.addStringProperty("membershipProgramName").codeBeforeField("@SerializedName(\"membershipProgramName\")");
        user.addStringProperty("membershipProgramPriorityCode").codeBeforeField("@SerializedName(\"membershipProgramPriorityCode\")");
        user.addStringProperty("membershipProgramStatus").codeBeforeField("@SerializedName(\"membershipProgramStatus\")");
        user.addStringProperty("membershipProgramExpiredDate").codeBeforeField("@SerializedName(\"membershipProgramExpiredDate\")");
        user.addStringProperty("membershipProgramStartDate").codeBeforeField("@SerializedName(\"membershipProgramStartDate\")");
        user.addStringProperty("gracePeriode").codeBeforeField("@SerializedName(\"gracePeriode\")");
        user.addStringProperty("listKompetisi").codeBeforeField("@SerializedName(\"listKompetisi\")");
        user.addStringProperty("piloting_branch").codeBeforeField("@SerializedName(\"piloting_branch\")");

        //LIST KOMPETISI
        Entity kompetisi = schema.addEntity("Kompetisi").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        kompetisi.setTableName("MS_KOMPETISI");

        kompetisi.addStringProperty("uuid_kompetisi").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_kompetisi\")");
        kompetisi.addStringProperty("membershipProgramCode").codeBeforeField("@SerializedName(\"MEMBERSHIP_PROGRAM_CODE\")");
        kompetisi.addStringProperty("membershipProgramName").codeBeforeField("@SerializedName(\"MEMBERSHIP_PROGRAM_NAME\")");
        kompetisi.addStringProperty("membershipProgramPriorityCode").codeBeforeField("@SerializedName(\"MEMBERSHIP_PROGRAM_PRIORITY\")");
        kompetisi.addStringProperty("membershipProgramStatus").codeBeforeField("@SerializedName(\"MEMBERSHIP_PROGRAM_STATUS\")");
        kompetisi.addStringProperty("membershipProgramExpiredDate").codeBeforeField("@SerializedName(\"MEMBERSHIP_PROGRAM_EXPIRED_DATE\")");
        kompetisi.addStringProperty("membershipProgramStartDate").codeBeforeField("@SerializedName(\"MEMBERSHIP_PROGRAM_START_DATE\")");
        kompetisi.addStringProperty("gracePeriode").codeBeforeField("@SerializedName(\"GRACE_PERIODE\")");
        kompetisi.addStringProperty("membershipProgramLogo").codeBeforeField("@SerializedName(\"LOGO\")");
        Property fk_listkompetisi_uuid_user = kompetisi.addStringProperty("uuid_user")
                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();

        //TR_APPLICATIONLOG
        Entity logger = schema.addEntity("Logger").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        logger.setTableName("TR_APPLICATION_LOG");

        logger.addStringProperty("uuid_log").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_log\")");
        logger.addStringProperty("screen").codeBeforeField("@SerializedName(\"screen\")");
        logger.addDateProperty("timestamp").codeBeforeField("@SerializedName(\"timestamp\")");
        logger.addStringProperty("detail").codeBeforeField("@SerializedName(\"detail\")");
        Property fk_logger_uuid_user = logger.addStringProperty("uuid_user")
                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();


        //TR_COLLECTIONHISTORY
        Entity collectionHistory = schema.addEntity("CollectionHistory").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        collectionHistory.setTableName("TR_COLLECTIONHISTORY");

        collectionHistory.addStringProperty("uuid_collection_history").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"uuid_collection_history\")");
        collectionHistory.addDateProperty("last_update").codeBeforeField("@SerializedName(\"last_update\")");
        collectionHistory.addStringProperty("description").codeBeforeField("@SerializedName(\"description\")");
        collectionHistory.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        collectionHistory.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        Property fk_collectionHistory_uuid_user = collectionHistory.addStringProperty("uuid_user")
                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();


        //TR_COMMENT
        Entity comment = schema.addEntity("Comment").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        comment.setTableName("TR_COMMENT");

        comment.addStringProperty("uuid_comment").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"uuid_comment\")");
        comment.addStringProperty("comment").codeBeforeField("@SerializedName(\"comment\")");
        comment.addDateProperty("dtm_crt_server").codeBeforeField("@SerializedName(\"dtm_crt_server\")");
        comment.addStringProperty("sender_id").codeBeforeField("@SerializedName(\"sender_id\")");
        comment.addStringProperty("sender_name").codeBeforeField("@SerializedName(\"sender_name\")");
        comment.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        comment.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        comment.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        comment.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        Property fk_comment_uuid_timeline = comment.addStringProperty("uuid_timeline")
                .codeBeforeField("@SerializedName(\"uuid_timeline\")").getProperty();


        //TR_DEPOSITREPORT_D
        Entity depositReportD = schema.addEntity("DepositReportD").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        depositReportD.setTableName("TR_DEPOSITREPORT_D");

        depositReportD.addStringProperty("uuid_deposit_report_d").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"uuid_deposit_report_d\")");
        depositReportD.addStringProperty("uuid_task_h").codeBeforeField("@SerializedName(\"uuid_task_h\")");
        depositReportD.addStringProperty("deposit_amt").codeBeforeField("@SerializedName(\"deposit_amt\")");
        depositReportD.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        depositReportD.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        Property fk_depositReportD_uuid_deposit_report_h = depositReportD.addStringProperty("uuid_deposit_report_h")
                .codeBeforeField("@SerializedName(\"uuid_deposit_report_h\")").getProperty();
        depositReportD.addStringProperty("is_sent").codeBeforeField("@SerializedName(\"is_sent\")");
        depositReportD.addStringProperty("agreement_no").codeBeforeField("@ExcludeFromGson \n\t @SerializedName(\"agreement_no\")");

        //TR_DEPOSITREPORT_H
        Entity depositReportH = schema.addEntity("DepositReportH").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        depositReportH.setTableName("TR_DEPOSITREPORT_H");

        depositReportH.addStringProperty("uuid_deposit_report_h").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"uuid_deposit_report_h\")");
        depositReportH.addDateProperty("last_update").codeBeforeField("@SerializedName(\"last_update\")");
        depositReportH.addStringProperty("batch_id").codeBeforeField("@SerializedName(\"batch_id\")");
        depositReportH.addStringProperty("bank_account").codeBeforeField("@SerializedName(\"bank_account\")");
        depositReportH.addStringProperty("bank_name").codeBeforeField("@SerializedName(\"bank_name\")");
        depositReportH.addStringProperty("cashier_name").codeBeforeField("@SerializedName(\"cashier_name\")");
        depositReportH.addDateProperty("transfered_date").codeBeforeField("@SerializedName(\"transfered_date\")");
        depositReportH.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        depositReportH.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        depositReportH.addByteArrayProperty("image").codeBeforeField("@SerializedName(\"image\")");
        Property fk_depositReportH_uuid_user = depositReportH.addStringProperty("uuid_user")
                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();
        depositReportH.addStringProperty("flag").codeBeforeField("@SerializedName(\"flag\")");
        depositReportH.addStringProperty("branch_payment").codeBeforeField("@SerializedName(\"branch_payment\")");
        depositReportH.addStringProperty("code_channel").codeBeforeField("@SerializedName(\"code_channel\")");
        depositReportH.addStringProperty("no_transaction").codeBeforeField("@SerializedName(\"no_transaction\")");


        //TR_IMAGERESULT
        Entity imageResult = schema.addEntity("ImageResult").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        imageResult.setTableName("TR_IMAGERESULT");

        imageResult.addStringProperty("uuid_image_result").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"uuid_image_result\")");
        imageResult.addStringProperty("question_id").codeBeforeField("@SerializedName(\"question_id\")");
        imageResult.addStringProperty("submit_duration").codeBeforeField("@SerializedName(\"submit_duration\")");
        imageResult.addStringProperty("submit_size").codeBeforeField("@SerializedName(\"submit_size\")");
        imageResult.addIntProperty("total_image").codeBeforeField("@SerializedName(\"total_image\")");
        imageResult.addIntProperty("count_image").codeBeforeField("@SerializedName(\"count_image\")");
        imageResult.addDateProperty("submit_date").codeBeforeField("@SerializedName(\"submit_date\")");
        imageResult.addStringProperty("submit_result").codeBeforeField("@SerializedName(\"submit_result\")");
        imageResult.addStringProperty("question_group_id").codeBeforeField("@SerializedName(\"question_group_id\")");
        imageResult.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        imageResult.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        Property fk_imageResult_uuid_task_h = imageResult.addStringProperty("uuid_task_h")
                .codeBeforeField("@SerializedName(\"uuid_task_h\")").getProperty();

        //TR_LOCATIONTRACKING
        Entity locationTracking = schema.addEntity("LocationInfo").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        locationTracking.setTableName("TR_LOCATION");

        locationTracking.addStringProperty("uuid_location_info").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"uuid_location_info\")");
        locationTracking.addStringProperty("latitude").codeBeforeField("@SerializedName(\"latitude\")");
        locationTracking.addStringProperty("longitude").codeBeforeField("@SerializedName(\"longitude\")");
        locationTracking.addStringProperty("mcc").codeBeforeField("@SerializedName(\"mcc\")");
        locationTracking.addStringProperty("mnc").codeBeforeField("@SerializedName(\"mnc\")");
        locationTracking.addStringProperty("lac").codeBeforeField("@SerializedName(\"lac\")");
        locationTracking.addStringProperty("cid").codeBeforeField("@SerializedName(\"cid\")");
        locationTracking.addDateProperty("handset_time").codeBeforeField("@SerializedName(\"handset_time\")");
        locationTracking.addStringProperty("mode").codeBeforeField("@SerializedName(\"mode\")");
        locationTracking.addIntProperty("accuracy").codeBeforeField("@SerializedName(\"accuracy\")");
        locationTracking.addDateProperty("gps_time").codeBeforeField("@SerializedName(\"gps_time\")");
        locationTracking.addStringProperty("is_gps_time").codeBeforeField("@SerializedName(\"is_gps_time\")");
        locationTracking.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        locationTracking.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        locationTracking.addStringProperty("location_type").codeBeforeField("@SerializedName(\"location_type\")");
        Property fk_locationTracking_uuid_user = locationTracking.addStringProperty("uuid_user")
                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();


        //TR_MESSAGE
        Entity message = schema.addEntity("Message").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        message.setTableName("TR_MESSAGE");

        message.addStringProperty("uuid_message").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"uuid_message\")");
        message.addStringProperty("message").codeBeforeField("@SerializedName(\"message\")");
        message.addStringProperty("sender_id").codeBeforeField("@SerializedName(\"sender_id\")");
        message.addStringProperty("sender_name").codeBeforeField("@SerializedName(\"sender_name\")");
        message.addDateProperty("dtm_crt_server").codeBeforeField("@SerializedName(\"dtm_crt_server\")");
        message.addDateProperty("time_read").codeBeforeField("@SerializedName(\"time_read\")");
        message.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        message.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        Property fk_message_uuid_user = message.addStringProperty("uuid_user")
                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();


        //TR_PAYMENTHISTORY_D
        Entity paymentHistoryD = schema.addEntity("PaymentHistoryD").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        paymentHistoryD.setTableName("TR_PAYMENTHISTORY_D");

        paymentHistoryD.addStringProperty("uuid_payment_history_d").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"uuid_payment_history_d\")");
        paymentHistoryD.addStringProperty("uuid_task_h").codeBeforeField("@SerializedName(\"uuid_task_h\")");

        paymentHistoryD.addStringProperty("transaction_type").codeBeforeField("@SerializedName(\"transactiontype\")");
        paymentHistoryD.addStringProperty("receipt_no").codeBeforeField("@SerializedName(\"receipt_no\")");
        paymentHistoryD.addDateProperty("value_date").codeBeforeField("@SerializedName(\"value_date\")");
        paymentHistoryD.addDateProperty("posting_date").codeBeforeField("@SerializedName(\"posting_date\")");
        paymentHistoryD.addStringProperty("payment_amount").codeBeforeField("@SerializedName(\"payment_amount\")");
        paymentHistoryD.addStringProperty("installment_amount").codeBeforeField("@SerializedName(\"installment_amount\")");
        paymentHistoryD.addStringProperty("installment_number").codeBeforeField("@SerializedName(\"installment_number\")");
        paymentHistoryD.addStringProperty("wop_code").codeBeforeField("@SerializedName(\"wop_code\")");

        paymentHistoryD.addStringProperty("payment_allocation_name").codeBeforeField("@SerializedName(\"payment_allocation_name\")");
        paymentHistoryD.addStringProperty("os_amount_od").codeBeforeField("@SerializedName(\"os_amount_od\")");
        paymentHistoryD.addStringProperty("receive_amount").codeBeforeField("@SerializedName(\"receive_amount\")");
        paymentHistoryD.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        paymentHistoryD.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        paymentHistoryD.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        paymentHistoryD.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        paymentHistoryD.addStringProperty("uuid_payment_history_h").codeBeforeField("@SerializedName(\"uuid_payment_history_h\")");

        //TR_PAYMENTHISTORY_H
        Entity paymentHistoryH = schema.addEntity("PaymentHistoryH").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        paymentHistoryH.setTableName("TR_PAYMENTHISTORY_H");

        paymentHistoryH.addStringProperty("uuid_payment_history_h").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"uuid_payment_history_h\")");
        paymentHistoryH.addStringProperty("uuid_task_h").codeBeforeField("@SerializedName(\"uuid_task_h\")");
        paymentHistoryH.addStringProperty("agreement_no").codeBeforeField("@SerializedName(\"agreement_no\")");
        paymentHistoryH.addStringProperty("branch_code").codeBeforeField("@SerializedName(\"branch_code\")");
        paymentHistoryH.addDateProperty("value_date").codeBeforeField("@SerializedName(\"value_date\")");
        paymentHistoryH.addStringProperty("payment_amount").codeBeforeField("@SerializedName(\"payment_amount\")");
        paymentHistoryH.addStringProperty("installment_amount").codeBeforeField("@SerializedName(\"installment_amount\")");
        paymentHistoryH.addStringProperty("installment_number").codeBeforeField("@SerializedName(\"installment_number\")");
        paymentHistoryH.addStringProperty("transaction_type").codeBeforeField("@SerializedName(\"transaction_type\")");
        paymentHistoryH.addStringProperty("wop_code").codeBeforeField("@SerializedName(\"wop_code\")");
        paymentHistoryH.addStringProperty("receipt_no").codeBeforeField("@SerializedName(\"receipt_no\")");

        paymentHistoryH.addDateProperty("post_date").codeBeforeField("@SerializedName(\"post_date\")");
        paymentHistoryH.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        paymentHistoryH.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        paymentHistoryH.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        paymentHistoryH.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");

        //MS_PAYMENTCHANNEL
        Entity paymentChannel = schema.addEntity("PaymentChannel").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        paymentChannel.setTableName("MS_PAYMENTCHANNEL");

        paymentChannel.addStringProperty("uuid_paymentchannel").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_paymentchannel\")");
        paymentChannel.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        paymentChannel.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        paymentChannel.addStringProperty("is_active").codeBeforeField("@SerializedName(\"is_active\")");
        paymentChannel.addStringProperty("code").codeBeforeField("@SerializedName(\"code\")");
        paymentChannel.addStringProperty("description").codeBeforeField("@SerializedName(\"description\")");
        paymentChannel.addDoubleProperty("payment_limit").codeBeforeField("@SerializedName(\"payment_limit\")");


        //TR_PRINTRESULT
        Entity printResult = schema.addEntity("PrintResult").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        printResult.setTableName("TR_PRINTRESULT");

        printResult.addStringProperty("uuid_print_result").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"uuid_print_result\")");
        printResult.addDateProperty("dtm_crt_server").codeBeforeField("@SerializedName(\"dtm_crt_server\")");
        printResult.addStringProperty("label").codeBeforeField("@SerializedName(\"label\")");
        printResult.addStringProperty("value").codeBeforeField("@SerializedName(\"value\")");
        printResult.addStringProperty("print_type_id").codeBeforeField("@SerializedName(\"print_type_id\")");
        printResult.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        printResult.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        Property fk_printResult_user = printResult.addStringProperty("uuid_task_h")
                .codeBeforeField("@SerializedName(\"uuid_task_h\")").getProperty();


        // TR_RECEIPTVOUCHER
        Entity receiptVoucher = schema.addEntity("ReceiptVoucher").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        receiptVoucher.setTableName("TR_RECEIPTVOUCHER");

        receiptVoucher.addStringProperty("uuid_receipt_voucher").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"uuid_rv_number\")");
        receiptVoucher.addStringProperty("rv_status").codeBeforeField("@SerializedName(\"status_rv\")");
        receiptVoucher.addStringProperty("rv_number").codeBeforeField("@SerializedName(\"rv_number\")");
        receiptVoucher.addStringProperty("flag_sources").codeBeforeField("@SerializedName(\"flag_sources\")");
        receiptVoucher.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        receiptVoucher.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        receiptVoucher.addDateProperty("dtm_use").codeBeforeField("@SerializedName(\"dtm_use\")");
        Property fk_receiptVoucher_uuid_user = receiptVoucher.addStringProperty("uuid_user")
                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();

        Property fk_receiptVoucher_uuid_task_h = receiptVoucher.addStringProperty("uuid_task_h")
                .codeBeforeField("@SerializedName(\"uuid_task_h\")").getProperty();


        // TR_TASK_SUMMARY
        Entity taskSummary = schema.addEntity("TaskSummary").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        taskSummary.setTableName("TR_TASK_SUMMARY");

        taskSummary.addStringProperty("uuid_task_summary").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_task_summary\")");
        taskSummary.addStringProperty("uuid_task_h").codeBeforeField("@SerializedName(\"uuid_task_h\")");
        taskSummary.addStringProperty("uuid_user").codeBeforeField("@SerializedName(\"uuid_user\")");
        taskSummary.addStringProperty("task_status").codeBeforeField("@SerializedName(\"task_status\")");
        taskSummary.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");


        // TR_TASK_D
        Entity taskD = schema.addEntity("TaskD").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        taskD.setTableName("TR_TASK_D");

        taskD.addStringProperty("uuid_task_d").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_task_d\")");
        taskD.addStringProperty("question_group_id").codeBeforeField("@SerializedName(\"question_group_id\")");
        taskD.addStringProperty("question_id").codeBeforeField("@SerializedName(\"question_id\")");
        taskD.addStringProperty("option_answer_id").codeBeforeField("@SerializedName(\"option_answer_id\")");
        taskD.addStringProperty("text_answer").codeBeforeField("@SerializedName(\"text_answer\")");
        taskD.addByteArrayProperty("image").codeBeforeField("@SerializedName(\"image\")");
        taskD.addStringProperty("is_final").codeBeforeField("@SerializedName(\"is_final\")");
        taskD.addStringProperty("is_sent").codeBeforeField("@SerializedName(\"is_sent\")");
        taskD.addStringProperty("lov").codeBeforeField("@SerializedName(\"lov\")");
        taskD.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        taskD.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        Property fk_taskD_uuid_task_h = taskD.addStringProperty("uuid_task_h")
                .codeBeforeField("@SerializedName(\"uuid_task_h\")").getProperty();
        //21 jan 2015 add new field
        taskD.addStringProperty("question_label").codeBeforeField("@SerializedName(\"question_label\")");
        taskD.addStringProperty("latitude").codeBeforeField("@SerializedName(\"latitude\")");
        taskD.addStringProperty("longitude").codeBeforeField("@SerializedName(\"longitude\")");
        taskD.addStringProperty("mcc").codeBeforeField("@SerializedName(\"mcc\")");
        taskD.addStringProperty("mnc").codeBeforeField("@SerializedName(\"mnc\")");
        taskD.addStringProperty("lac").codeBeforeField("@SerializedName(\"lac\")");
        taskD.addStringProperty("cid").codeBeforeField("@SerializedName(\"cid\")");
        taskD.addDateProperty("gps_time").codeBeforeField("@SerializedName(\"gps_time\")");
        taskD.addIntProperty("accuracy").codeBeforeField("@SerializedName(\"accuracy\")");
        taskD.addStringProperty("regex").codeBeforeField("@SerializedName(\"regex\")");
        taskD.addStringProperty("is_readonly").codeBeforeField("@SerializedName(\"is_readonly\")");
        taskD.addByteArrayProperty("location_image").codeBeforeField("@ExcludeFromGson \n\t @SerializedName(\"location_image\")");
        taskD.addStringProperty("is_visible").codeBeforeField("@SerializedName(\"is_visible\")");
        Property fk_taskD_uuid_lookup =taskD.addStringProperty("uuid_lookup").codeBeforeField("@SerializedName(\"uuid_lookup\")").getProperty();
        taskD.addStringProperty("tag").codeBeforeField("@SerializedName(\"tag\")");
        taskD.addStringProperty("count").codeBeforeField("@SerializedName(\"count\")");
        //add image timestamp field
        taskD.addDateProperty("image_timestamp").codeBeforeField("@SerializedName(\"image_timestamp\")");
        taskD.addStringProperty("data_dukcapil").codeBeforeField("@SerializedName(\"data_dukcapil\")");

        // TR_TASK_H
        Entity taskH = schema.addEntity("TaskH").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        taskH.setTableName("TR_TASK_H");

        taskH.addStringProperty("uuid_task_h").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_task_h\")");
        taskH.addStringProperty("task_id").codeBeforeField("@SerializedName(\"task_id\")");
        taskH.addStringProperty("status").codeBeforeField("@SerializedName(\"status\")");
        taskH.addStringProperty("is_printable").codeBeforeField("@SerializedName(\"is_printable\")");
        taskH.addStringProperty("customer_name").codeBeforeField("@SerializedName(\"customer_name\")");
        taskH.addStringProperty("customer_phone").codeBeforeField("@SerializedName(\"customer_phone\")");
        taskH.addStringProperty("customer_address").codeBeforeField("@SerializedName(\"customer_address\")");
        taskH.addStringProperty("notes").codeBeforeField("@SerializedName(\"notes\")");
        taskH.addDateProperty("submit_date").codeBeforeField("@SerializedName(\"submit_date\")");
        taskH.addStringProperty("submit_duration").codeBeforeField("@SerializedName(\"submit_duration\")");
        taskH.addStringProperty("submit_size").codeBeforeField("@SerializedName(\"submit_size\")");
        taskH.addStringProperty("submit_result").codeBeforeField("@SerializedName(\"submit_result\")");
        taskH.addDateProperty("assignment_date").codeBeforeField("@SerializedName(\"assignment_date\")");
        taskH.addIntProperty("print_count").codeBeforeField("@SerializedName(\"print_count\")");
        taskH.addDateProperty("draft_date").codeBeforeField("@SerializedName(\"draft_date\")");
        taskH.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        taskH.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        taskH.addStringProperty("priority").codeBeforeField("@SerializedName(\"priority\")");
        taskH.addStringProperty("latitude").codeBeforeField("@SerializedName(\"latitude\")");
        taskH.addStringProperty("longitude").codeBeforeField("@SerializedName(\"longitude\")");
        taskH.addDateProperty("scheme_last_update").codeBeforeField("@SerializedName(\"scheme_last_update\")");
        taskH.addStringProperty("is_verification").codeBeforeField("@SerializedName(\"is_verification\")");
        taskH.addStringProperty("is_preview_server").codeBeforeField("@SerializedName(\"is_preview_server\")");
        Property fk_taskH_uuid_user = taskH.addStringProperty("uuid_user")
                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();
        taskH.addByteArrayProperty("voice_note").codeBeforeField("@SerializedName(\"voice_note\")");
        Property fk_taskH_uuid_scheme = taskH.addStringProperty("uuid_scheme")
                .codeBeforeField("@SerializedName(\"uuid_scheme\")").getProperty();
        taskH.addStringProperty("zip_code").codeBeforeField("@SerializedName(\"zip_code\")");
        taskH.addDateProperty("start_date").codeBeforeField("@SerializedName(\"start_date\")");
        taskH.addDateProperty("open_date").codeBeforeField("@SerializedName(\"open_date\")");
        taskH.addStringProperty("appl_no").codeBeforeField("@SerializedName(\"appl_no\")");
        taskH.addStringProperty("is_prepocessed").codeBeforeField("@ExcludeFromGson \n\t @SerializedName(\"is_prepocessed\")");
        taskH.addIntProperty("last_saved_question").codeBeforeField("@SerializedName(\"last_saved_question\")");
        taskH.addStringProperty("is_reconciled").codeBeforeField("@SerializedName(\"is_reconciled\")");
        taskH.addDateProperty("pts_date").codeBeforeField("@SerializedName(\"pts_date\")");
        taskH.addStringProperty("access_mode").codeBeforeField("@ExcludeFromGson \n\t @SerializedName(\"access_mode\")");
        taskH.addStringProperty("rv_number").codeBeforeField("@SerializedName(\"rv_number\")");
        taskH.addStringProperty("status_rv").codeBeforeField("@SerializedName(\"status_rv\")");
        taskH.addStringProperty("no_rangka").codeBeforeField("@SerializedName(\"no_rangka\")");
        taskH.addStringProperty("no_plat").codeBeforeField("@SerializedName(\"no_plat\")");
        taskH.addStringProperty("no_mesin").codeBeforeField("@SerializedName(\"no_mesin\")");
        taskH.addStringProperty("flag").codeBeforeField("@SerializedName(\"flag\")");
        taskH.addStringProperty("message").codeBeforeField("@ExcludeFromGson \n\t @SerializedName(\"message\")");
        taskH.addStringProperty("form_version").codeBeforeField("@SerializedName(\"form_version\")");
        taskH.addStringProperty("flag_survey").codeBeforeField("@SerializedName(\"flag_survey\")");
        taskH.addStringProperty("uuid_resurvey_user").codeBeforeField("@SerializedName(\"uuid_resurvey_user\")");
        taskH.addStringProperty("resurvey_suggested").codeBeforeField("@SerializedName(\"resurvey_suggested\")");
        taskH.addStringProperty("verification_notes").codeBeforeField("@SerializedName(\"verification_notes\")");
        taskH.addStringProperty("od").codeBeforeField("@SerializedName(\"od\")");
        taskH.addStringProperty("amt_due").codeBeforeField("@SerializedName(\"amt_due\")");
        taskH.addStringProperty("inst_no").codeBeforeField("@SerializedName(\"inst_no\")");
        taskH.addStringProperty("status_code").codeBeforeField("@SerializedName(\"status_code\")");
        taskH.addStringProperty("uuid_account").codeBeforeField("@SerializedName(\"uuid_account\")");
        taskH.addStringProperty("uuid_product").codeBeforeField("@SerializedName(\"uuid_product\")");
        taskH.addStringProperty("reference_number").codeBeforeField("@SerializedName(\"reference_number\")");
        taskH.addStringProperty("data_dukcapil").codeBeforeField("@SerializedName(\"data_dukcapil\")");
        taskH.addIntProperty("seq_no").codeBeforeField("@SerializedName(\"seq_no\")");
        taskH.addStringProperty("batch_id").codeBeforeField("@SerializedName(\"batch_id\")");
        taskH.addStringProperty("survey_location").codeBeforeField("@SerializedName(\"survey_location\")");

        // TR_TASK_H_SEQUENCE
        Entity taskHSequence = schema.addEntity("TaskHSequence").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        taskHSequence.setTableName("TR_TASK_H_SEQUENCE");

        taskHSequence.addIntProperty("sequence").codeBeforeField("@SerializedName(\"sequence\")").notNull();
        Property fk_uuid_task_h_seq = taskHSequence.addStringProperty("uuid_task_h")
                .codeBeforeField("@SerializedName(\"uuid_task_h\")").getProperty();

        //TR_PLAN_TASK
        Entity planTask = schema.addEntity("PlanTask").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        planTask.setTableName("TR_PLAN_TASK");
        planTask.addStringProperty("uuid_plan_task").codeBeforeField("@SerializedName(\"uuid_plan_task\")").notNull().primaryKey();
        planTask.addIntProperty("sequence").codeBeforeField("@SerializedName(\"sequence\")").notNull();
        planTask.addStringProperty("plan_status").codeBeforeField("@SerializedName(\"plan_status\")").notNull();
        planTask.addDateProperty("plan_start_date").codeBeforeField("@SerializedName(\"plan_start_date\")");
        planTask.addDateProperty("plan_crt_date").codeBeforeField("@SerializedName(\"plan_crt_date\")");
        planTask.addIntProperty("view_sequence").codeBeforeField("@SerializedName(\"view_sequence\")");
        Property fk_user_plan = planTask.addStringProperty("uuid_user").codeBeforeField("@SerializedName(\"uuid_user\")").notNull().getProperty();
        Property fk_taskh_plan = planTask.addStringProperty("uuid_task_h")
                .codeBeforeField("@SerializedName(\"uuid_task_h\")").notNull().getProperty();

        // TR_TIMELINE
        Entity timeline = schema.addEntity("Timeline").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        timeline.setTableName("TR_TIMELINE");

        timeline.addStringProperty("uuid_timeline").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_timeline\")");
        timeline.addStringProperty("description").codeBeforeField("@SerializedName(\"description\")");
        timeline.addStringProperty("latitude").codeBeforeField("@SerializedName(\"latitude\")");
        timeline.addStringProperty("longitude").codeBeforeField("@SerializedName(\"longitude\")");
        timeline.addDateProperty("dtm_crt_server").codeBeforeField("@SerializedName(\"dtm_crt_server\")");

        timeline.addStringProperty("name").codeBeforeField("@SerializedName(\"name\")");
        timeline.addStringProperty("address").codeBeforeField("@SerializedName(\"address\")");
        timeline.addStringProperty("agreement_no").codeBeforeField("@SerializedName(\"agreement_no\")");
        timeline.addStringProperty("amount_due").codeBeforeField("@SerializedName(\"amount_due\")");
        timeline.addStringProperty("overdue").codeBeforeField("@SerializedName(\"overdue\")");
        timeline.addStringProperty("installment_no").codeBeforeField("@SerializedName(\"installment_no\")");
        timeline.addStringProperty("attd_address").codeBeforeField("@SerializedName(\"attd_address\")");
        timeline.addStringProperty("priority").codeBeforeField("@SerializedName(\"priority\")");
        timeline.addStringProperty("isVerificationTask").codeBeforeField("@SerializedName(\"isVerificationTask\")");
        timeline.addStringProperty("collResult").codeBeforeField("@SerializedName(\"collResult\")");
        timeline.addStringProperty("account_name").codeBeforeField("@SerializedName(\"account_name\")");
        timeline.addStringProperty("product_name").codeBeforeField("@SerializedName(\"product_name\")");
        timeline.addStringProperty("status_code").codeBeforeField("@SerializedName(\"status_code\")");


        timeline.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        timeline.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        Property fk_timeline_uuid_task = timeline.addStringProperty("uuid_task_h")
                .codeBeforeField("@SerializedName(\"uuid_task_h\")").getProperty();
        Property fk_timeline_uuid_user = timeline.addStringProperty("uuid_user")
                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();
        Property fk_timeline_uuid_timeline_type = timeline.addStringProperty("uuid_timeline_type")
                .codeBeforeField("@SerializedName(\"uuid_timeline_type\")").getProperty();
        Property fk_timeline_uuid_message = timeline.addStringProperty("uuid_message")
                .codeBeforeField("@SerializedName(\"uuid_message\")").getProperty();
        timeline.addByteArrayProperty("byte_image").codeBeforeField("@SerializedName(\"byte_image\")");

        // TR_MOBILECONTENT_D
        Entity mobileContentD = schema.addEntity("MobileContentD").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        mobileContentD.setTableName("TR_MOBILECONTENT_D");

        mobileContentD.addStringProperty("uuid_mobile_content_d").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"uuid_mobile_content_d\")");
        mobileContentD.addStringProperty("menu_id").codeBeforeField("@SerializedName(\"menu_id\")");
        mobileContentD.addByteArrayProperty("content").codeBeforeField("@SerializedName(\"content\")");
        mobileContentD.addStringProperty("content_type").codeBeforeField("@SerializedName(\"content_type\")");
        mobileContentD.addIntProperty("sequence").codeBeforeField("@SerializedName(\"sequence\")");
        mobileContentD.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        mobileContentD.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        mobileContentD.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        Property fk_mobileContentD_uuid_mobile_content_h = mobileContentD.addStringProperty("uuid_mobile_content_h")
                .codeBeforeField("@SerializedName(\"uuid_mobile_content_h\")").getProperty();
        mobileContentD.addDateProperty("start_date").codeBeforeField("@SerializedName(\"start_date\")");
        mobileContentD.addDateProperty("end_date").codeBeforeField("@SerializedName(\"end_date\")");

        // TR_MOBILECONTENT_H
        Entity mobileContentH = schema.addEntity("MobileContentH").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        mobileContentH.setTableName("TR_MOBILECONTENT_H");

        mobileContentH.addStringProperty("uuid_mobile_content_h").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"uuid_mobile_content_h\")");
        mobileContentH.addStringProperty("content_name").codeBeforeField("@SerializedName(\"content_name\")");
        mobileContentH.addDateProperty("last_update").codeBeforeField("@SerializedName(\"last_update\")");
        mobileContentH.addStringProperty("content_description").codeBeforeField("@SerializedName(\"content_description\")");
        mobileContentH.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        mobileContentH.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        mobileContentH.addStringProperty("usr_upd").codeBeforeField("@SerializedName(\"usr_upd\")");
        mobileContentH.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        Property fk_mobileContentH_uuid_user = mobileContentH.addStringProperty("uuid_user")
                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();
        Property fk_mobileContentH_uuid_mobile_content_h = mobileContentH.addStringProperty("uuid_parent_content")
                .codeBeforeField("@SerializedName(\"uuid_parent_content\")").getProperty();
        mobileContentH.addDateProperty("start_date").codeBeforeField("@SerializedName(\"start_date\")");
        mobileContentH.addDateProperty("end_date").codeBeforeField("@SerializedName(\"end_date\")");


        // MS_HOLIDAY
        Entity holiday = schema.addEntity("Holiday").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        holiday.setTableName("MS_HOLIDAY");

        holiday.addStringProperty("uuid_holiday").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"uuid_holiday\")");
        holiday.addDateProperty("h_date").codeBeforeField("@SerializedName(\"h_date\")");
        holiday.addStringProperty("h_desc").codeBeforeField("@SerializedName(\"h_desc\")");
        holiday.addStringProperty("flag_holiday").codeBeforeField("@SerializedName(\"flag_holiday\")");
//					holiday.addStringProperty("usr_crt");
//					holiday.addDateProperty("dtm_crt");
//					holiday.addStringProperty("usr_upd");
        holiday.addDateProperty("dtm_upd").codeBeforeField("@SerializedName(\"dtm_upd\")");
        holiday.addStringProperty("flag_day").codeBeforeField("@SerializedName(\"flag_day\")");
        holiday.addStringProperty("branch").codeBeforeField("@SerializedName(\"branch\")");

        // TR_PRINTDATE
        Entity submitPrint = schema.addEntity("PrintDate").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        submitPrint.setTableName("TR_PRINTDATE");

        submitPrint.addDateProperty("dtm_print").notNull().primaryKey()
                .codeBeforeField("@SerializedName(\"dtm_print\")");
        submitPrint.addStringProperty("uuid_task_h").codeBeforeField("@SerializedName(\"uuid_task_h\")");

        // TR_ERROR_LOG
        Entity errorLog = schema.addEntity("ErrorLog").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        errorLog.setTableName("TR_ERROR_LOG");

        errorLog.addStringProperty("uuid_error_log").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_error_log\")");
        errorLog.addStringProperty("error_description").codeBeforeField("@SerializedName(\"description\")");
        errorLog.addStringProperty("device_name").codeBeforeField("@SerializedName(\"device_name\")");
        errorLog.addDateProperty("dtm_activity").codeBeforeField("@SerializedName(\"dtm_activity\")");
        Property fk_errorLog_uuid_user = errorLog.addStringProperty("uuid_user")
                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();
        Property fk_errorLog_task_id = errorLog.addStringProperty("task_id")
                .codeBeforeField("@SerializedName(\"task_id\")").getProperty();

        //MO
        //MS_ACCOUNT
        Entity account = schema.addEntity("Account").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        account.setTableName("MS_ACCOUNT");

        account.addStringProperty("uuid_account").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_account\")");
        account.addStringProperty("account_name").codeBeforeField("@SerializedName(\"account_name\")");
        account.addStringProperty("account_address").codeBeforeField("@SerializedName(\"account_address\")");
        account.addStringProperty("account_phone_1").codeBeforeField("@SerializedName(\"account_phone_1\")");
        account.addStringProperty("account_phone_2").codeBeforeField("@SerializedName(\"account_phone_2\")");
        account.addStringProperty("account_latitude").codeBeforeField("@SerializedName(\"account_latitude\")");
        account.addStringProperty("account_longitude").codeBeforeField("@SerializedName(\"account_longitude\")");
        account.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        account.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");

        //MS_PRODUCT
        Entity product = schema.addEntity("Product").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        product.setTableName("MS_PRODUCT");

        product.addStringProperty("uuid_product").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_product\")");
        product.addStringProperty("product_code").codeBeforeField("@SerializedName(\"product_code\")");
        product.addStringProperty("product_name").codeBeforeField("@SerializedName(\"product_name\")");
        product.addStringProperty("product_desc").codeBeforeField("@SerializedName(\"product_desc\")");
        product.addIntProperty("product_value").codeBeforeField("@SerializedName(\"product_value\")");
        product.addIntProperty("product_inctv_prctg").codeBeforeField("@SerializedName(\"product_inctv_prctg\")");
        product.addByteArrayProperty("lob_image").codeBeforeField("@SerializedName(\"lob_image\")");
        product.addStringProperty("usr_crt").codeBeforeField("@SerializedName(\"usr_crt\")");
        product.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");
        product.addStringProperty("is_active").codeBeforeField("@SerializedName(\"is_active\")");
        product.addStringProperty("brand").codeBeforeField("@SerializedName(\"brand\")");
        product.addStringProperty("type").codeBeforeField("@SerializedName(\"type\")");
        product.addStringProperty("model").codeBeforeField("@SerializedName(\"model\")");
        product.addStringProperty("product_file").codeBeforeField("@SerializedName(\"product_file\")");

        //MS_CONTACT
        Entity contact = schema.addEntity("Contact").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        contact.setTableName("MS_CONTACT");

        contact.addStringProperty("uuid_contact").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_contact\")");
        contact.addStringProperty("contact_name").codeBeforeField("@SerializedName(\"contact_name\")");
        contact.addStringProperty("contact_dept").codeBeforeField("@SerializedName(\"contact_dept\")");
        contact.addStringProperty("contact_phone").codeBeforeField("@SerializedName(\"contact_phone\")");
        contact.addStringProperty("contact_email").codeBeforeField("@SerializedName(\"contact_email\")");
        contact.addStringProperty("uuid_account").codeBeforeField("@SerializedName(\"uuid_account\")");
        contact.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");

        //MS_GROUPTASK
        Entity grouptask = schema.addEntity("GroupTask").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        grouptask.setTableName("MS_GROUPTASK");

        grouptask.addStringProperty("uuid_group_task").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_group_task\")");
        grouptask.addStringProperty("group_task_id").codeBeforeField("@SerializedName(\"group_task_id\")");
        grouptask.addStringProperty("uuid_account").codeBeforeField("@SerializedName(\"uuid_account\")");
        grouptask.addStringProperty("last_status").codeBeforeField("@SerializedName(\"status_code\")");
        grouptask.addStringProperty("product_name").codeBeforeField("@SerializedName(\"product_name\")");
        grouptask.addIntProperty("project_nett").codeBeforeField("@SerializedName(\"project_nett\")");
        grouptask.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");

        //MS_CATALOGUE
        Entity catalogue = schema.addEntity("Catalogue").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        catalogue.setTableName("MS_CATALOGUE");

        catalogue.addStringProperty("uuid_mkt_catalogue").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_mkt_catalogue\")");
        catalogue.addStringProperty("catalogue_name").codeBeforeField("@SerializedName(\"catalogue_name\")");
        catalogue.addStringProperty("catalogue_desc").codeBeforeField("@SerializedName(\"catalogue_desc\")");
        catalogue.addStringProperty("catalogue_file").codeBeforeField("@SerializedName(\"catalogue_file\")");
        catalogue.addDateProperty("dtm_crt").codeBeforeField("@SerializedName(\"dtm_crt\")");

        // TR_THEME_ITEM
        Entity themeItem = schema.addEntity("ThemeItem").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        themeItem.setTableName("TR_THEME_ITEM");
        themeItem.addStringProperty("uuid_theme_item").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_theme_item\")");
        themeItem.addStringProperty("theme_item").notNull().codeBeforeField("@SerializedName(\"theme_item\")");
        themeItem.addStringProperty("value").codeBeforeField("@SerializedName(\"value\")");
        Property fk_dynamictheme_uuid = themeItem.addStringProperty("uuid_theme")
                .codeBeforeField("@SerializedName(\"uuid_theme\")").getProperty();

        //TR_THEME
        Entity dynamictheme = schema.addEntity("Theme").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        dynamictheme.setTableName("TR_THEME");
        dynamictheme.addStringProperty("uuid_theme").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_theme\")");
        dynamictheme.addStringProperty("application_type").notNull().codeBeforeField("@SerializedName(\"application_type\")");
        dynamictheme.addStringProperty("version").codeBeforeField("@SerializedName(\"version\")");

        //TR_LOGO_PRINT
        Entity logoPrint = schema.addEntity("LogoPrint").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        logoPrint.setTableName("TR_LOGO_PRINT");
        logoPrint.addStringProperty("uuid_logo_print").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_logo_print\")");
        logoPrint.addStringProperty("tenant").notNull().codeBeforeField("@SerializedName(\"tenant\")");
        logoPrint.addByteArrayProperty("image_bitmap").codeBeforeField("@SerializedName(\"image_bitmap\")");

        //TR_EMERGENCY
        Entity emergency = schema.addEntity("Emergency").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        emergency.setTableName("TR_EMERGENCY");
        emergency.addLongProperty("uuid_emergency").notNull().primaryKey().autoincrement();
        Property fk_user_emergency = emergency.addStringProperty("uuid_user").notNull().codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();
        emergency.addStringProperty("longitude").codeBeforeField("@SerializedName(\"longitude\")");
        emergency.addStringProperty("latitude").codeBeforeField("@SerializedName(\"latitude\")");
        emergency.addDateProperty("dtm_emergency").codeBeforeField("@SerializedName(\"dtm_emergency\")");

        emergency.addToOne(user,fk_user_emergency);

        //TR_LASTSYNC
        Entity lastSync = schema.addEntity("LastSync").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        lastSync.setTableName("TR_LASTSYNC");
        lastSync.addStringProperty("uuid_last_sync").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_last_sync\")");
        lastSync.addDateProperty("dtm_lastsync").codeBeforeField("@SerializedName(\"dtm_lastsync\")");
        lastSync.addStringProperty("dtm_req").codeBeforeField("@SerializedName(\"dtm_req\")");
        lastSync.addStringProperty("data").codeBeforeField("@SerializedName(\"data\")");
        lastSync.addStringProperty("listOfLOV").codeBeforeField("@SerializedName(\"listOfLOV\")");
        lastSync.addStringProperty("flag").codeBeforeField("@SerializedName(\"flag\")");
        lastSync.addIntProperty("is_send").codeBeforeField("@SerializedName(\"is_send\")");
        //        Property fk_lastSync_uuid_user = lastSync.addStringProperty("uuid_user")
        ////                .codeBeforeField("@SerializedName(\"uuid_user\")").getProperty();

        //MS_BANKACCOUNTOFBRANCH
        Entity bankAccountOfBranch = schema.addEntity("BankAccountOfBranch").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        bankAccountOfBranch.setTableName("MS_BANKACCOUNTOFBRANCH");

        bankAccountOfBranch.addStringProperty("uuid_bankaccountofbranch").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_bankaccountofbranch\")");
        bankAccountOfBranch.addStringProperty("bank_account_id").codeBeforeField("@SerializedName(\"bank_account_id\")");
        bankAccountOfBranch.addStringProperty("bank_account_no").codeBeforeField("@SerializedName(\"bank_account_no\")");
        bankAccountOfBranch.addStringProperty("bank_account_name").codeBeforeField("@SerializedName(\"bank_account_name\")");
        bankAccountOfBranch.addStringProperty("branch_code").codeBeforeField("@SerializedName(\"branch_code\")");

        // TR_BROADCAST
        Entity notification = schema.addEntity("Broadcast").addImport("com.adins.mss.base.util.ExcludeFromGson");
        notification.setTableName("TR_BROADCAST");

        notification.addStringProperty("uuid_broadcast").notNull().primaryKey();
        notification.addStringProperty("title");
        notification.addStringProperty("message");
        notification.addBooleanProperty("is_shown");
        notification.addDateProperty("dtm_crt");

        // TR_RECEIPTHISTORY
        Entity receiptHistory = schema.addEntity("ReceiptHistory").addImport("com.adins.mss.base.util.ExcludeFromGson").addImport("com.google.gson.annotations.SerializedName");
        receiptHistory.setTableName("TR_RECEIPTHISTORY");

        receiptHistory.addStringProperty("uuid_receipt_history").notNull().primaryKey().codeBeforeField("@SerializedName(\"uuid_receipt_history\")");
        receiptHistory.addStringProperty("uuid_task_h").codeBeforeField("@SerializedName(\"uuid_task_h\")");
        receiptHistory.addStringProperty("agreement_no").codeBeforeField("@SerializedName(\"agreementNo\")");
        receiptHistory.addStringProperty("receipt_no").codeBeforeField("@SerializedName(\"invoiceNo\")");
        receiptHistory.addStringProperty("payment_date").codeBeforeField("@SerializedName(\"valueDt\")");

        //SET RELATION  GENERAL PARAMETER
        generalParameter.addToOne(user, fk_generalParameter_uuid_user);
        user.addToMany(generalParameter, fk_QuestionSet_uuid_scheme);


        //SET RELATION GROUP USER
        groupUser.addToOne(user, fk_groupUser_uuid_user);
        user.addToMany(groupUser, fk_groupUser_uuid_user);

        //SET RELATION for menu
        menu.addToOne(user, fk_menu_uuid_user);
        user.addToMany(menu, fk_menu_uuid_user);

        printItem.addToOne(scheme, fk_printItem_uuid_scheme);
        scheme.addToMany(printItem, fk_printItem_uuid_scheme);


        //SET RELATION ms_questionset
        questionSet.addToOne(scheme, fk_QuestionSet_uuid_scheme);
        scheme.addToMany(questionSet, fk_QuestionSet_uuid_scheme);

        //SET RELATION log
        logger.addToOne(user, fk_logger_uuid_user);
        user.addToMany(logger, fk_logger_uuid_user);


        //SET RELATION collection history
        collectionHistory.addToOne(user, fk_collectionHistory_uuid_user);
        user.addToMany(collectionHistory, fk_collectionHistory_uuid_user);

        //SET RELATION List Kompetisi
        kompetisi.addToOne(user, fk_listkompetisi_uuid_user);
        user.addToMany(kompetisi,fk_listkompetisi_uuid_user);

        //SET RELATION comment
        comment.addToOne(timeline, fk_comment_uuid_timeline);
        timeline.addToMany(comment, fk_comment_uuid_timeline);


        //SET RELATION deposit report d
        depositReportD.addToOne(depositReportH, fk_depositReportD_uuid_deposit_report_h);
        depositReportH.addToMany(depositReportD, fk_depositReportD_uuid_deposit_report_h);

        //SET RELATION deposit report h
        depositReportH.addToOne(user, fk_depositReportH_uuid_user);
        user.addToMany(depositReportH, fk_depositReportH_uuid_user);

        //SET RELATION image result
        imageResult.addToOne(taskH, fk_imageResult_uuid_task_h);
        taskH.addToMany(imageResult, fk_imageResult_uuid_task_h);

        //SET RELATION installmentSchedule
//					installmentSchedule.addToOne(user, fk_installmentSchedule_uuid_user);
//					user.addToMany(installmentSchedule, fk_installmentSchedule_uuid_user);


        //SET RELATION location tracking
        locationTracking.addToOne(user, fk_locationTracking_uuid_user);
        user.addToMany(locationTracking, fk_locationTracking_uuid_user);


        //SET RELATION message
        message.addToOne(user, fk_message_uuid_user);
        user.addToMany(message, fk_message_uuid_user);

        //SET RELATION mobileContentD
        mobileContentD.addToOne(mobileContentH, fk_mobileContentD_uuid_mobile_content_h);
        mobileContentH.addToMany(mobileContentD, fk_mobileContentD_uuid_mobile_content_h);


        //SET RELATION mobileContentH
        mobileContentH.addToOne(mobileContentH, fk_mobileContentH_uuid_mobile_content_h);
        mobileContentH.addToMany(mobileContentH, fk_mobileContentH_uuid_mobile_content_h);

        mobileContentH.addToOne(user, fk_mobileContentH_uuid_user);
        user.addToMany(mobileContentH, fk_mobileContentH_uuid_user);


        //SET RELATION payment history
//					paymentHistory.addToOne(user, fk_paymentHistory_user);
//					user.addToMany(paymentHistory, fk_paymentHistory_user);

        //SET RELATION print result
        printResult.addToOne(user, fk_printResult_user);
        user.addToMany(printResult, fk_printResult_user);

        //SET RELATION RV
        receiptVoucher.addToOne(user, fk_receiptVoucher_uuid_user);
        user.addToMany(receiptVoucher, fk_receiptVoucher_uuid_user);

        receiptVoucher.addToOne(taskH, fk_receiptVoucher_uuid_task_h);
        taskH.addToMany(receiptVoucher, fk_receiptVoucher_uuid_task_h);

        //SET RELATION task d
        taskD.addToOne(taskH, fk_taskD_uuid_task_h);
        taskH.addToMany(taskD, fk_taskD_uuid_task_h);
        taskD.addToOne(lookup, fk_taskD_uuid_lookup);

        //SET RELATION task h
        taskH.addToOne(user, fk_taskH_uuid_user);
        user.addToMany(taskH, fk_taskH_uuid_user);

        taskH.addToOne(scheme, fk_taskH_uuid_scheme);
        scheme.addToMany(taskH, fk_taskH_uuid_scheme);

        //SET RELATION error log
        errorLog.addToOne(user, fk_errorLog_uuid_user);
        user.addToMany(errorLog, fk_errorLog_uuid_user);

        errorLog.addToOne(taskH, fk_errorLog_task_id);
        taskH.addToMany(errorLog, fk_errorLog_task_id);

        //SET RELATION timeline
        timeline.addToOne(user, fk_timeline_uuid_user);
        user.addToMany(timeline, fk_timeline_uuid_user);

        timeline.addToOne(timelineType, fk_timeline_uuid_timeline_type);
        timelineType.addToMany(timeline, fk_timeline_uuid_timeline_type);

        timeline.addToOne(message, fk_timeline_uuid_message);
        message.addToMany(timeline, fk_timeline_uuid_message);

        timeline.addToOne(taskH, fk_timeline_uuid_task);
        taskH.addToMany(timeline, fk_timeline_uuid_task);

        taskHSequence.addToOne(taskH, fk_uuid_task_h_seq);
        taskH.addToMany(taskHSequence, fk_uuid_task_h_seq);

        planTask.addToOne(taskH,fk_taskh_plan);
        planTask.addToOne(user,fk_user_plan);
        user.addToMany(planTask,fk_user_plan);

        //SET RELATION theme_item
        themeItem.addToOne(dynamictheme,fk_dynamictheme_uuid);
        dynamictheme.addToMany(themeItem,fk_dynamictheme_uuid);
    }
}