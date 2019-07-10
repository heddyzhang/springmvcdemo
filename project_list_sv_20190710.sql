--------------------------------------------------------
--  �t�@�C�����쐬���܂��� - ���j��-7��-10-2019   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package Body PROJECT_LIST_SV
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE PACKAGE BODY "RMS"."PROJECT_LIST_SV" 

-- ====================================================
-- PACKAGE PROJECT_LIST_SV
-- PROJECT_LIST_PAN_XXX�\�f�[�^��PROJECT_LIST�\�ɔ��f����
-- �O������F�e_XXX�e�[�u������łȂ�����
--   PROJECT_LIST_INTERFACE.INTERFACE_STATUS_CODE='ACTIVE'�̃f�[�^���Ȃ�����

-- ====================================================
IS


-- ====================================================
-- PROCEDURE EXEC_PA2
-- PROJECT_LIST_PA2_XXX��PROJECT_LIST�̃v���W�F�N�g�f�[�^��r��
-- �����������PROJECT_LIST_INTERFACE�ɓo�^����Bproject_list.data_upd_type in (0,1,2)�̂ݑΏہB
-- KKPA�̃f�[�^�́APJ#�̖�����'_KK'������
--
-- ���̓p�����[�^�FIN_EXEC_USER_ID
--   ���s���[�UID
-- ���^�[���R�[�h�FOUT_RESUT_CODE
--   ����I��=200
--   �ُ�I��=�G���[�R�[�h
-- ��O�����F
--   ERR_TABLE�Ƀ��O���o�͂��ăG���[�R�[�h��Ԃ�
-- ====================================================
PROCEDURE EXEC_PA2
(
	IN_EXEC_USER_ID	IN	NUMBER,
	OUT_RESULT_CODE	OUT	VARCHAR2
)
IS

N_ERR_CODE		ERR_TABLE.ERR_CODE%TYPE;
V_ERR_MSG		ERR_TABLE.ERR_MSG%TYPE;

BEGIN

	-- INTERFACE�\�ɕύX�Ώۂ�o�^�AERROR_MSG�ɂ͕ύX�O�f�[�^���i�[
	-- �X�V�Ώۂ�project_list��project_list_pa2_xxx�����ɑ��݂���f�[�^
	-- data_upd_type=2�̏ꍇ��proj_start,end�͍X�V���Ȃ�
	INSERT INTO PROJECT_LIST_INTERFACE(
		PROJECT_INTERFACE_ID,
		INTERFACE_STATUS_CODE,
		ERROR_MSG,
		MODE_CODE,
		CREATION_DATE,
		LAST_UPDATE_DATE,
		CREATED_BY,
		LAST_UPDATED_BY,
		LAST_UPDATE_LOGIN,
		DEFAULT_TYPE_CODE,
		PROJ_CODE,
		GSI_PROJ_CODE,
		PROJ_NAME,
		PROJ_START,
		PROJ_END,
		CONTRACT_ID,
		PA_CC_ID,
        COST_CENTER_FULL_NAME
	)
	SELECT
		TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')||'_'||P.PROJ_CODE,
		'ACTIVE',
		SUBSTRB('proj_start='||P.PROJ_START||',proj_end='||P.PROJ_END||',contract_id='||P.CONTRACT_ID||',proj_name='||P.PROJ_NAME,1,4000),
		'UPDATE',
		SYSDATE,
		SYSDATE,
		IN_EXEC_USER_ID,
		IN_EXEC_USER_ID,
		'EXEC_PA2',
		'OFFICIAL',
		P.PROJ_CODE,
		P.GSI_PROJ_CODE,
		NVL(PA2.PROJ_NAME,' '),
		CASE WHEN P.DATA_UPD_TYPE=2 AND P.BOOK_STATUS!=5 THEN P.PROJ_START
			ELSE NVL(PA2.PROJ_START,sysdate) END,
		CASE WHEN P.DATA_UPD_TYPE=2 AND P.BOOK_STATUS!=5 THEN P.PROJ_END
			ELSE NVL(PA2.PROJ_END,sysdate) END,
		DECODE(PA2.CONT_TYPE,'TM',1,'FP',2,'ICR',3,'ITM',4,'IRT',6,'XRT',7,'XCR',8,'I',15,'ITML',11,'ITMFC',3,'ITMFR',11,9),
        SUBSTR(PA2.PROJ_ORG,1,INSTR(PA2.PROJ_ORG,' ')-1),
        PA2.PROJ_ORG
	FROM PROJECT_LIST P, PROJECT_LIST_PA2_XXX PA2
	WHERE PA2.PROJ_STATUS != 'Rejected'
	AND   PA2.PROJ_STATUS != 'Closed'
	AND   PA2.GSI_PROJ_CODE = SUBSTR(P.GSI_PROJ_CODE,1,9)
	AND   P.DATA_UPD_TYPE IN (0,1,2)
	AND  ( CASE WHEN P.DATA_UPD_TYPE=2 AND P.BOOK_STATUS!=5 THEN NVL(P.PROJ_START,TO_DATE('100001','YYYYMM'))
		ELSE NVL(PA2.PROJ_START,TO_DATE('100001','YYYYMM')) END != NVL(P.PROJ_START,TO_DATE('100001','YYYYMM'))
	OR	CASE WHEN P.DATA_UPD_TYPE=2 AND P.BOOK_STATUS!=5 THEN NVL(P.PROJ_END,TO_DATE('100001','YYYYMM'))
		ELSE NVL(PA2.PROJ_END,TO_DATE('100001','YYYYMM')) END  != NVL(P.PROJ_END,TO_DATE('100001','YYYYMM'))
	OR    NVL(PA2.PROJ_NAME,' ') != NVL(P.PROJ_NAME,' ')
	OR    (NVL(SUBSTR( PA2.PROJ_ORG,1,INSTR( PA2.PROJ_ORG, ' ', 1,1 ) -1),' ') != NVL(P.PA_CC_ID,' ')
        AND NVL(SUBSTR( PA2.PROJ_ORG,1,INSTR( PA2.PROJ_ORG, ' ', 1,1 ) -1),' ') != NVL(P.OLD_PA_CC_ID,' '))
	OR    DECODE(PA2.CONT_TYPE,'TM',1,'FP',2,'ICR',3,'ITM',4,'IRT',6,'XRT',7,'XCR',8,'I',15,'ITML',11,'ITMFC',3,'ITMFR',11,9)!= P.CONTRACT_ID
		)
	;

	-- INTERFACE�\�ɐV�K�o�^�Ώۂ�o�^
	INSERT INTO PROJECT_LIST_INTERFACE(
		PROJECT_INTERFACE_ID,
		INTERFACE_STATUS_CODE,
		ERROR_MSG,
		MODE_CODE,
		CREATION_DATE,
		LAST_UPDATE_DATE,
		CREATED_BY,
		LAST_UPDATED_BY,
		LAST_UPDATE_LOGIN,
		DEFAULT_TYPE_CODE,
		PROJ_CODE,
		GSI_PROJ_CODE,
		PROJ_NAME,
		PROJ_START,
		PROJ_END,
		CONTRACT_ID,
		CONS_START,
		CONS_END,
		UNIT_ID,
		PA_CC_ID,
		PROJ_STATUS,
		WORK_STATUS
		,ADMI
        ,COST_CENTER_FULL_NAME
	)
	WITH VIEW_PA5 AS
	(SELECT PA5.gsi_proj_code,PA5.proj_role,e.emp_id,PA5.proj_status,PA5.start_date,PA5.end_date
	  FROM PROJECT_LIST_PA5_XXX PA5,EMP_LIST e
	 WHERE LOWER(PA5.ATTRIBUTE1) = e.MAIL_ADDRESS
	   AND E.del_flg = 0)
	SELECT
		TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')||'_'||PA2.GSI_PROJ_CODE,
		'ACTIVE',
		NULL,
		'INSERT',
		SYSDATE,
		SYSDATE,
		IN_EXEC_USER_ID,
		IN_EXEC_USER_ID,
		'EXEC_PA2',
		'OFFICIAL',
		'TEMP'||TO_CHAR(PROJ_CODE_SEQ.NEXTVAL),
		-- KKPA�̃f�[�^��PJ#�̌���_KK������
		DECODE(PA2.ATTRIBUTE1,'Oracle Information Systems (Japan) G.K.',PA2.GSI_PROJ_CODE||'_KK',PA2.GSI_PROJ_CODE),
		PA2.PROJ_NAME,
		PA2.PROJ_START,
		NVL(PA2.PROJ_END, ADD_MONTHS(PA2.PROJ_START,3)),
		DECODE(PA2.CONT_TYPE,'TM',1,'FP',2,'ICR',3,'ITM',4,'IRT',6,'XRT',7,'XCR',8,'I',15,'ITML',11,'ITMFC',3,'ITMFR',11,9),
		PA2.PROJ_START,
		PA2.PROJ_END,
		-- �_��`�Ԃ�ICR�̏ꍇ��PM�̃R�X�g�Z���^���B����ȊO��PJ�̑g�D��ݒ�
		NVL(DECODE(PA2.CONT_TYPE,'ICR', PM_CC.COST_CENTER_ID,'ITMFC', PM_CC.COST_CENTER_ID, V_CC_ORG_ID_P||SUBSTR(PA2.PROJ_ORG,1,6)),V_CC_ORG_ID),
		SUBSTR(PA2.PROJ_ORG,1,INSTR(PA2.PROJ_ORG,' ')-1),
		-- �ʏ��BEST�œo�^�BCONT_TYPE=ITM��I��������BACKLOG
		DECODE(PA2.CONT_TYPE,'ITM',N_BACKLOG,'I',N_BACKLOG,N_BEST),
		-- �ʏ��NOTWORK�œo�^�BCONT_TYPE=ITM��I��������CONTRACT�ɂ���
		DECODE(PA2.CONT_TYPE,'ITM',N_CONTRACT,'I',N_CONTRACT,N_NOTWORK)
		,DECODE(PA2.CONT_TYPE,'ICR',POADMI.ADMI,'ITMFC',POADMI.ADMI)
        ,PA2.PROJ_ORG
	FROM PROJECT_LIST_PA2_XXX PA2
		-- PM�̃R�X�g�Z���^�ꗗ
        	,(SELECT PM.GSI_PROJ_CODE, E.COST_CENTER_ID, E.EMP_ID
		FROM EMP_LIST E,
		(SELECT PA5.GSI_PROJ_CODE, MIN(PA5.EMP_ID) EMP_ID
		FROM VIEW_PA5 PA5
		WHERE PA5.PROJ_ROLE = 'Project Manager'
		AND   PA5.START_DATE = (SELECT MAX(START_DATE)
			FROM VIEW_PA5
			WHERE GSI_PROJ_CODE = PA5.GSI_PROJ_CODE
			AND   PROJ_ROLE = PA5.PROJ_ROLE)
                GROUP BY PA5.GSI_PROJ_CODE
		) PM
		WHERE E.EMP_ID = PM.EMP_ID
		) PM_CC
		,PROJ_PO_ADMI_LIST POADMI
	-- DATA_UPD_TYPE = 0��3 �ɑΏۂ�GSI_PROJ_CODE���Ȃ�������o�^
	WHERE NOT EXISTS(SELECT 1 FROM PROJECT_LIST WHERE SUBSTR(GSI_PROJ_CODE,1,9) >= '0' AND (DATA_UPD_TYPE = 0 OR DATA_UPD_TYPE = 3) AND SUBSTR(GSI_PROJ_CODE,1,9) = SUBSTR(PA2.GSI_PROJ_CODE,1,9))
	AND PA2.GSI_PROJ_CODE = PM_CC.GSI_PROJ_CODE(+)
	AND PA2.PROJ_STATUS != 'Rejected'
	AND PA2.PROJ_STATUS != 'Closed'
	AND PM_CC.EMP_ID = POADMI.PO_EMP_ID(+)
	-- pa2.proj_org�����ݗL���ȃR���T���g�D�̂��� �� ICR�̂���
	AND(  (SUBSTR(PA2.PROJ_ORG,1,6) IN
		(SELECT SUBSTR(CC.ORG_ID,-6)
		FROM COST_CENTER_LIST CC, CONST_LIST C
		WHERE C.NAME = 'CONSUL_ORG_ID'
		AND   SYSDATE BETWEEN C.START_DATE AND NVL(C.END_DATE,SYSDATE+1)
		AND   C.VALUE = CC.LEVEL2
		AND   CC.ORG_END_DATE IS NULL
		)
    OR   (SUBSTR(PA2.PROJ_ORG,1,INSTR(PA2.PROJ_ORG,' ')-1) IN 
    (SELECT CM.NEW_CC_ID
		FROM CC_ID_MAP CM))
	OR   PA2.CONT_TYPE = 'ICR'
    OR   PA2.CONT_TYPE = 'ITMFC'
		)
        )
	;

    --PA_CC_ID��4���̂Ƃ�6���ɏC�����s��
    UPDATE PROJECT_LIST_INTERFACE p
    set p.PA_CC_ID = (select cc.NEW_CC_ID from CC_ID_MAP cc where cc.OLD_CC_ID = p.PA_CC_ID)
    where LENGTH(p.PA_CC_ID) = 4
    and p.LAST_UPDATE_DATE > TO_DATE(TO_CHAR(SYSDATE,'YYYY/MM/DD') || '00:00','YYYY/MM/DD HH24:MI')
    and (select cc.NEW_CC_ID from CC_ID_MAP cc where cc.OLD_CC_ID = p.PA_CC_ID) is not null;

	OUT_RESULT_CODE := '200';
	COMMIT;

EXCEPTION
	WHEN OTHERS THEN
		N_ERR_CODE := SQLCODE;
		V_ERR_MSG := SUBSTR(SQLERRM,1,100);
		INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
		VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.EXEC_PA2 �\�����Ȃ��G���[');
		COMMIT;
		OUT_RESULT_CODE := '500 �\�����Ȃ��G���[���������܂��� EXEC_PA2';

END EXEC_PA2;


-- ====================================================
-- PROCEDURE EXEC_PA1
-- PROJECT_LIST_PA1_XXX��PROJECT_LIST�̃v���W�F�N�g�f�[�^��r��
-- �����������PROJECT_LIST_INTERFACE�ɓo�^����Bproject_list.data_upd_type in (0)�̂ݑΏہB
--
-- ���̓p�����[�^�FIN_EXEC_USER_ID
--   ���s���[�UID
-- ���^�[���R�[�h�FOUT_RESUT_CODE
--   ����I��=200
--   �ُ�I��=�G���[�R�[�h
-- ��O�����F
--   ERR_TABLE�Ƀ��O���o�͂��ăG���[�R�[�h��Ԃ�
-- ====================================================
PROCEDURE EXEC_PA1
(
	IN_EXEC_USER_ID	IN	NUMBER,
	OUT_RESULT_CODE	OUT	VARCHAR2
)
IS

N_ERR_CODE		ERR_TABLE.ERR_CODE%TYPE;
V_ERR_MSG		ERR_TABLE.ERR_MSG%TYPE;

BEGIN

	-- INTERFACE�\�ɕύX�Ώۂ�o�^�AERROR_MSG�ɂ͕ύX�O�f�[�^���i�[
	INSERT INTO PROJECT_LIST_INTERFACE(
		PROJECT_INTERFACE_ID,
		INTERFACE_STATUS_CODE,
		ERROR_MSG,
		MODE_CODE,
		CREATION_DATE,
		LAST_UPDATE_DATE,
		CREATED_BY,
		LAST_UPDATED_BY,
		LAST_UPDATE_LOGIN,
		DEFAULT_TYPE_CODE,
		PROJ_CODE,
		CONT_AMOUNT,
		CONTRACT_DATE,
		CONTRACT_CREATION_DATE
	)
	SELECT
		TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')||'_'||P.PROJ_CODE,
		'ACTIVE',
		SUBSTRB('cont_amount='||P.CONT_AMOUNT||',contract_date='||P.CONTRACT_DATE||',contract_creation_date='||P.CONTRACT_CREATION_DATE,1,4000),
		'UPDATE',
		SYSDATE,
		SYSDATE,
		IN_EXEC_USER_ID,
		IN_EXEC_USER_ID,
		'EXEC_PA1',
		'OFFICIAL',
		P.PROJ_CODE,
		NVL(PA1.CONT_AMOUNT,NULL_NUM),
		NVL(PA1.CONTRACT_DATE,TO_DATE('100001','YYYYMM')),
		NVL(PA1.CONTRACT_CREATION_DATE,TO_DATE('100001','YYYYMM'))
	FROM PROJECT_LIST P, (SELECT PA.GSI_PROJ_CODE, PA.PROJ_STATUS, SUM(PA.CONT_AMOUNT) CONT_AMOUNT, MIN(PA.CONTRACT_DATE) CONTRACT_DATE, MIN(PA.CONTRACT_CREATION_DATE) CONTRACT_CREATION_DATE
                                FROM (SELECT GSI_PROJ_CODE, PROJ_STATUS, CONT_AMOUNT, CONTRACT_DATE, CONTRACT_CREATION_DATE
                                        FROM PROJECT_LIST_PA1_XXX
                                       WHERE ATTRIBUTE1 = 'CORRECTION'
                                       UNION
                                      SELECT PA1_XXX.GSI_PROJ_CODE, PA1_XXX.PROJ_STATUS, PA1_XXX.CONT_AMOUNT, PA1_XXX.CONTRACT_DATE, PA1_XXX.CONTRACT_CREATION_DATE
                                        FROM PROJECT_LIST_PA1_XXX PA1_XXX, (SELECT GSI_PROJ_CODE, MIN(CONTRACT_DATE) CONTRACT_DATE
                                                                              FROM PROJECT_LIST_PA1_XXX
                                                                             WHERE ATTRIBUTE1 = 'ORIGINAL'
                                                                             GROUP BY GSI_PROJ_CODE) PA1_TMP
                                       WHERE PA1_XXX.GSI_PROJ_CODE = PA1_TMP.GSI_PROJ_CODE
                                         AND PA1_XXX.CONTRACT_DATE = PA1_TMP.CONTRACT_DATE
                                         AND PA1_XXX.ATTRIBUTE1 = 'ORIGINAL') PA
                               GROUP BY PA.GSI_PROJ_CODE, PA.PROJ_STATUS) PA1
	WHERE PA1.PROJ_STATUS != 'Rejected'
	AND   PA1.PROJ_STATUS != 'Closed'
	AND   PA1.GSI_PROJ_CODE = SUBSTR(P.GSI_PROJ_CODE,1,9)
	AND  (NVL(PA1.CONT_AMOUNT,NULL_NUM) != NVL(P.CONT_AMOUNT,NULL_NUM)
	OR    NVL(PA1.CONTRACT_DATE,TO_DATE('100001','YYYYMM'))  != NVL(P.CONTRACT_DATE,TO_DATE('100001','YYYYMM'))
	OR    NVL(PA1.CONTRACT_CREATION_DATE,TO_DATE('100001','YYYYMM'))  != NVL(P.CONTRACT_CREATION_DATE,TO_DATE('100001','YYYYMM'))
		)
	-- ���z�ɂ��ẮA�^�C�v0�̂��̂̂ݍX�V
	AND P.DATA_UPD_TYPE = 0
	;

	-- ���̋@�\���s�v�Ȃ񂶂�Ȃ����ȁB
	-- project_list.est_amount(��ʂ���C�����ꂽ�ꍇ�̒l������)
	-- update project_list set est_amount = null, est_contract_date = null;

	OUT_RESULT_CODE := '200';
	COMMIT;

EXCEPTION
	WHEN OTHERS THEN
		N_ERR_CODE := SQLCODE;
		V_ERR_MSG := SUBSTR(SQLERRM,1,100);
		INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
		VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.EXEC_PA1 �\�����Ȃ��G���[');
		COMMIT;
		OUT_RESULT_CODE := '500 �\�����Ȃ��G���[���������܂��� EXEC_PA1';

END EXEC_PA1;

-- ====================================================
-- PROCEDURE EXEC_PA1_SENDMAIL_LIST
-- PROJECT_LIST_PA1_XXX����ATTRIBUTE1 = 'CORRECTION'�̃f�[�^���擾���A
-- PROJECT_PA1_SENDMAIL_LIST�ɂȂ����SENDMAIL_FLG=1�Ƃ��ēo�^����B
--
-- ���̓p�����[�^�FIN_EXEC_USER_ID
--   ���s���[�UID
-- ���^�[���R�[�h�FOUT_RESUT_CODE
--   ����I��=200
--   �ُ�I��=�G���[�R�[�h
-- ��O�����F
--   ERR_TABLE�Ƀ��O���o�͂��ăG���[�R�[�h��Ԃ�
-- ====================================================
PROCEDURE EXEC_PA1_SENDMAIL_LIST
(
	IN_EXEC_USER_ID	IN	NUMBER,
	OUT_RESULT_CODE	OUT	VARCHAR2
)
IS

N_ERR_CODE		ERR_TABLE.ERR_CODE%TYPE;
V_ERR_MSG		ERR_TABLE.ERR_MSG%TYPE;

BEGIN

	-- INTERFACE�\�ɕύX�Ώۂ�o�^
	INSERT INTO PROJECT_PA1_SENDMAIL_LIST(
		PROJ_CODE,
		CONT_AMOUNT,
		CONTRACT_DATE,
		CONTRACT_CREATION_DATE,
		SENDMAIL_FLG,
		CREATION_DATE,
		LAST_UPDATE_DATE,
		CREATED_BY,
		LAST_UPDATED_BY
	)
	SELECT
		PA1.GSI_PROJ_CODE,
		PA1.CONT_AMOUNT,
		PA1.CONTRACT_DATE,
		PA1.CONTRACT_CREATION_DATE,
		1,
		SYSDATE,
		SYSDATE,
		IN_EXEC_USER_ID,
		IN_EXEC_USER_ID
      FROM (SELECT *
              FROM PROJECT_LIST_PA1_XXX
             WHERE ATTRIBUTE1 = 'CORRECTION') PA1
   WHERE NOT EXISTS (SELECT * FROM PROJECT_PA1_SENDMAIL_LIST M
                      WHERE PA1.GSI_PROJ_CODE = M.PROJ_CODE
                        AND PA1.CONT_AMOUNT = M.CONT_AMOUNT
                        AND PA1.CONTRACT_DATE = M.CONTRACT_DATE
                        AND PA1.CONTRACT_CREATION_DATE = M.CONTRACT_CREATION_DATE);

	OUT_RESULT_CODE := '200';
	COMMIT;

EXCEPTION
	WHEN OTHERS THEN
		N_ERR_CODE := SQLCODE;
		V_ERR_MSG := SUBSTR(SQLERRM,1,100);
		INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
		VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.EXEC_PA1_SENDMAIL_LIST �\�����Ȃ��G���[');
		COMMIT;
		OUT_RESULT_CODE := '500 �\�����Ȃ��G���[���������܂��� EXEC_PA1_SENDMAIL_LIST';

END EXEC_PA1_SENDMAIL_LIST;

-- ====================================================
-- PROCEDURE EXEC_PA5
-- PROJECT_LIST_PA5_XXX��PROJECT_LIST�̃v���W�F�N�g�f�[�^��r��
-- �����������PROJECT_LIST_INTERFACE�ɓo�^����Bproject_list.data_upd_type in (0,1,2)�̂ݑΏہB
--
-- PO��sysdate���_��PO
-- ��������ꍇ��CreateDate����ԐV�����l��I��
--
-- ���̓p�����[�^�FIN_EXEC_USER_ID
--   ���s���[�UID
-- ���^�[���R�[�h�FOUT_RESUT_CODE
--   ����I��=200
--   �ُ�I��=�G���[�R�[�h
-- ��O�����F
--   ERR_TABLE�Ƀ��O���o�͂��ăG���[�R�[�h��Ԃ�
-- ====================================================
PROCEDURE EXEC_PA5
(
	IN_EXEC_USER_ID	IN	NUMBER,
	OUT_RESULT_CODE	OUT	VARCHAR2
)
IS

N_ERR_CODE		ERR_TABLE.ERR_CODE%TYPE;
V_ERR_MSG		ERR_TABLE.ERR_MSG%TYPE;

BEGIN

	-- INTERFACE�\�ɕύX�Ώۂ�o�^�AERROR_MSG�ɂ͕ύX�O�f�[�^���i�[
	INSERT INTO PROJECT_LIST_INTERFACE(
		PROJECT_INTERFACE_ID,
		INTERFACE_STATUS_CODE,
		ERROR_MSG,
		MODE_CODE,
		CREATION_DATE,
		LAST_UPDATE_DATE,
		CREATED_BY,
		LAST_UPDATED_BY,
		LAST_UPDATE_LOGIN,
		DEFAULT_TYPE_CODE,
		PROJ_CODE,
		PROJ_OWNER_ID,
		PROJ_MGR_ID,
        SEC_PM
	)
	WITH VIEW_PA5 AS
	(SELECT PA5.gsi_proj_code,PA5.proj_role,e.emp_id,PA5.proj_status,PA5.start_date,PA5.end_date
	  FROM PROJECT_LIST_PA5_XXX PA5,EMP_LIST e
	 WHERE LOWER(PA5.ATTRIBUTE1) = e.MAIL_ADDRESS)
	SELECT
		TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')||'_'||P.PROJ_CODE,
		'ACTIVE',
		SUBSTRB('proj_owner_id='||P.PROJ_OWNER_ID ||',proj_mgr_id='|| P.PROJ_MGR_ID,1,4000),
		'UPDATE',
		SYSDATE,
		SYSDATE,
		IN_EXEC_USER_ID,
		IN_EXEC_USER_ID,
		'EXEC_PA5',
		'OFFICIAL',
		P.PROJ_CODE,
		NVL(PA5.PO_ID,P.PROJ_OWNER_ID),
		NVL(PA5.PM_ID,P.PROJ_MGR_ID),
        NVL(PA5.SPM_ID,P.SEC_PM)
	FROM PROJECT_LIST P,
		(select pa5.gsi_proj_code, pa5.proj_status, nvl(po.po_id,prgdpo.po_id) po_id, pm.pm_id, spm.spm_id
		from
			(select distinct gsi_proj_code, proj_status from VIEW_PA5) pa5,
			-- FY09�܂ł�ProgramDirector��PO���o�^����Ă���
			(select latestPrgdPo.gsi_proj_code, nvl(prsntPrgdPo.po_id,latestPrgdPo.po_id) po_id
			from
			    (select gsi_proj_code, min(emp_id) po_id from VIEW_PA5 inpa5
			    where proj_role = 'Program Director'
			    and sysdate between start_date and nvl(end_date, sysdate + 1)
			    group by gsi_proj_code) prsntPrgdPo,
			    (select gsi_proj_code, min(emp_id) po_id from VIEW_PA5 inpa5
			    where proj_role = 'Program Director'
			    and   start_date = (select max(start_date) from VIEW_PA5
			    where inpa5.gsi_proj_code = gsi_proj_code
			    and   inpa5.proj_role = proj_role
			    group by gsi_proj_code, proj_role)
			    group by gsi_proj_code) latestPrgdPo
			where latestPrgdPo.gsi_proj_code = prsntPrgdPo.gsi_proj_code(+)
			)prgdpo,
			-- FY10�ȍ~��ProgramManager��PO���o�^����Ă���B�������D��Ŏ擾����
			(select latestPo.gsi_proj_code, nvl(prsntPo.po_id,latestPo.po_id) po_id
			from
			    (select gsi_proj_code, min(emp_id) po_id from VIEW_PA5 inpa5
			    where proj_role = 'Program Manager'
			    and sysdate between start_date and nvl(end_date, sysdate + 1)
			    group by gsi_proj_code) prsntPo,
			    (select gsi_proj_code, min(emp_id) po_id from VIEW_PA5 inpa5
			    where proj_role = 'Program Manager'
			    and   start_date = (select max(start_date) from VIEW_PA5
			    where inpa5.gsi_proj_code = gsi_proj_code
			    and   inpa5.proj_role = proj_role
			    group by gsi_proj_code, proj_role)
			    group by gsi_proj_code) latestPo
			where latestPo.gsi_proj_code = prsntPo.gsi_proj_code(+)
			)po,
			(select latestPm.gsi_proj_code, nvl(prsntPm.pm_id,latestPm.pm_id) pm_id
			from
			    (select gsi_proj_code, emp_id pm_id from VIEW_PA5 inpa5
			    where proj_role = 'Project Manager'
			    and sysdate between start_date and nvl(end_date, sysdate + 1)
			    ) prsntPm,
			    (select gsi_proj_code, emp_id pm_id from VIEW_PA5 inpa5
			    where proj_role = 'Project Manager'
			    and   start_date = (select max(start_date) from VIEW_PA5
			    where inpa5.gsi_proj_code = gsi_proj_code
			    and   inpa5.proj_role = proj_role
			    group by gsi_proj_code, proj_role)
                            ) latestPm
			where latestPm.gsi_proj_code = prsntPm.gsi_proj_code(+)
			)pm,
            			(select latestPm.gsi_proj_code, nvl(prsntSpm.spm_id,latestPm.spm_id) spm_id
			from
			    (select gsi_proj_code, emp_id spm_id from VIEW_PA5 inpa5
			    where proj_role = 'Secondary Project Manager'
			    and sysdate between start_date and nvl(end_date, sysdate + 1)
			    ) prsntSpm,
			    (select gsi_proj_code, emp_id spm_id from VIEW_PA5 inpa5
			    where proj_role = 'Secondary Project Manager'
			    and   start_date = (select max(start_date) from VIEW_PA5
			    where inpa5.gsi_proj_code = gsi_proj_code
			    and   inpa5.proj_role = proj_role
			    group by gsi_proj_code, proj_role)
                            ) latestPm
			where latestPm.gsi_proj_code = prsntSpm.gsi_proj_code(+)
			) spm
		where pa5.gsi_proj_code = prgdpo.gsi_proj_code(+)
		and   pa5.gsi_proj_code = po.gsi_proj_code(+)
		and   pa5.gsi_proj_code = pm.gsi_proj_code(+)
        and   pa5.gsi_proj_code = spm.gsi_proj_code(+)
		) PA5
	WHERE PA5.PROJ_STATUS != 'Rejected'
	AND   PA5.PROJ_STATUS != 'Closed'
	AND   PA5.GSI_PROJ_CODE = SUBSTR(P.GSI_PROJ_CODE,1,9)
	AND   P.DATA_UPD_TYPE IN (0,1,2)
	AND  ( (NVL(PA5.PO_ID, 0) != NVL(P.PROJ_OWNER_ID, 0) AND PA5.PO_ID IS NOT NULL)
		OR (NVL(PA5.PM_ID, 0) != NVL(P.PROJ_MGR_ID, 0) AND PA5.PM_ID IS NOT NULL) 
        OR (NVL(PA5.SPM_ID, 0) != NVL(P.SEC_PM, 0) AND PA5.SPM_ID IS NOT NULL) )
	;

	OUT_RESULT_CODE := '200';
	COMMIT;

EXCEPTION
	WHEN OTHERS THEN
		N_ERR_CODE := SQLCODE;
		V_ERR_MSG := SUBSTR(SQLERRM,1,100);
		INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
		VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.EXEC_PA5 �\�����Ȃ��G���[');
		COMMIT;
		OUT_RESULT_CODE := '500 �\�����Ȃ��G���[���������܂��� EXEC_PA5';

END EXEC_PA5;


-- ====================================================
-- PROCEDURE EXEC_PA6
-- PROJECT_LIST_PA6_XXX��PROJECT_LIST�̃v���W�F�N�g�f�[�^��r��
-- �����������PROJECT_LIST_INTERFACE�ɓo�^����Bproject_list.data_upd_type in (0,1,2)�̂ݑΏہB
--
-- CLASS_CATEGORY=Automate FP Revenue�̂��̂����X�V
--
-- ���̓p�����[�^�FIN_EXEC_USER_ID
--   ���s���[�UID
-- ���^�[���R�[�h�FOUT_RESUT_CODE
--   ����I��=200
--   �ُ�I��=�G���[�R�[�h
-- ��O�����F
--   ERR_TABLE�Ƀ��O���o�͂��ăG���[�R�[�h��Ԃ�
-- ====================================================
PROCEDURE EXEC_PA6
(
	IN_EXEC_USER_ID	IN	NUMBER,
	OUT_RESULT_CODE	OUT	VARCHAR2
)
IS

N_ERR_CODE		ERR_TABLE.ERR_CODE%TYPE;
V_ERR_MSG		ERR_TABLE.ERR_MSG%TYPE;

BEGIN

	-- INTERFACE�\�ɕύX�Ώۂ�o�^�AERROR_MSG�ɂ͕ύX�O�f�[�^���i�[
	INSERT INTO PROJECT_LIST_INTERFACE(
		PROJECT_INTERFACE_ID,
		INTERFACE_STATUS_CODE,
		ERROR_MSG,
		MODE_CODE,
		CREATION_DATE,
		LAST_UPDATE_DATE,
		CREATED_BY,
		LAST_UPDATED_BY,
		LAST_UPDATE_LOGIN,
		DEFAULT_TYPE_CODE,
		PROJ_CODE,
		FPAUTO
	)
	SELECT
		TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')||'_'||P.PROJ_CODE,
		'ACTIVE',
		SUBSTRB('fpauto='||P.FPAUTO,1,4000),
		'UPDATE',
		SYSDATE,
		SYSDATE,
		IN_EXEC_USER_ID,
		IN_EXEC_USER_ID,
		'EXEC_PA6',
		'OFFICIAL',
		P.PROJ_CODE,
		NVL(PA6.CLASS_CODE,' ')
	FROM PROJECT_LIST P,
		PROJECT_LIST_PA6_XXX PA6
	WHERE PA6.PROJ_STATUS != 'Rejected'
	AND   PA6.PROJ_STATUS != 'Closed'
	AND   PA6.CLASS_CATEGORY = 'Automate FP Revenue'
	AND   PA6.GSI_PROJ_CODE = SUBSTR(P.GSI_PROJ_CODE,1,9)
	AND   P.DATA_UPD_TYPE IN (0,1,2)
	AND   NVL(PA6.CLASS_CODE, ' ') != NVL(P.FPAUTO, ' ')
	;

	-- SaaS
	INSERT INTO PROJECT_LIST_INTERFACE(
		PROJECT_INTERFACE_ID,
		INTERFACE_STATUS_CODE,
		ERROR_MSG,
		MODE_CODE,
		CREATION_DATE,
		LAST_UPDATE_DATE,
		CREATED_BY,
		LAST_UPDATED_BY,
		LAST_UPDATE_LOGIN,
		DEFAULT_TYPE_CODE,
		PROJ_CODE,
		CLASS_CODE
	)
	SELECT
		TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')||'_'||P.PROJ_CODE,
		'ACTIVE',
		SUBSTRB('class_code='||P.CLASS_CODE,1,4000),
		'UPDATE',
		SYSDATE,
		SYSDATE,
		IN_EXEC_USER_ID,
		IN_EXEC_USER_ID,
		'EXEC_PA6',
		'OFFICIAL',
		P.PROJ_CODE,
		PA6.CLASS_CODE
	FROM PROJECT_LIST P,
		PROJECT_LIST_PA6_XXX PA6
	WHERE PA6.PROJ_STATUS != 'Rejected'
	AND   PA6.PROJ_STATUS != 'Closed'
	AND   PA6.CLASS_CATEGORY = 'Cloud/On Premise Deal'
	AND   PA6.GSI_PROJ_CODE = SUBSTR(P.GSI_PROJ_CODE,1,9)
	AND   P.DATA_UPD_TYPE IN (0,1,2)
	AND   NVL(PA6.CLASS_CODE, ' ') != NVL(P.CLASS_CODE, ' ')
	;

	-- Allow LOB Invoice Entry
	INSERT INTO PROJECT_LIST_INTERFACE(
		PROJECT_INTERFACE_ID,
		INTERFACE_STATUS_CODE,
		ERROR_MSG,
		MODE_CODE,
		CREATION_DATE,
		LAST_UPDATE_DATE,
		CREATED_BY,
		LAST_UPDATED_BY,
		LAST_UPDATE_LOGIN,
		DEFAULT_TYPE_CODE,
		PROJ_CODE,
		ALLOW_LOB_INVOICE
	)
	SELECT
		TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')||'_'||P.PROJ_CODE,
		'ACTIVE',
		SUBSTRB('class_code='||P.ALLOW_LOB_INVOICE,1,4000),
		'UPDATE',
		SYSDATE,
		SYSDATE,
		IN_EXEC_USER_ID,
		IN_EXEC_USER_ID,
		'EXEC_PA6',
		'OFFICIAL',
		P.PROJ_CODE,
		PA6.CLASS_CODE
	FROM PROJECT_LIST P,
		PROJECT_LIST_PA6_XXX PA6
	WHERE PA6.PROJ_STATUS != 'Rejected'
	AND   PA6.PROJ_STATUS != 'Closed'
	AND   PA6.CLASS_CATEGORY = 'Allow LOB Invoice Entry'
	AND   PA6.GSI_PROJ_CODE = SUBSTR(P.GSI_PROJ_CODE,1,9)
	AND   P.DATA_UPD_TYPE IN (0,1,2)
	AND   NVL(PA6.CLASS_CODE, ' ') != NVL(P.ALLOW_LOB_INVOICE, ' ')
	;

    	-- CONTRACT#
	INSERT INTO PROJECT_LIST_INTERFACE(
		PROJECT_INTERFACE_ID,
		INTERFACE_STATUS_CODE,
		ERROR_MSG,
		MODE_CODE,
		CREATION_DATE,
		LAST_UPDATE_DATE,
		CREATED_BY,
		LAST_UPDATED_BY,
		LAST_UPDATE_LOGIN,
		DEFAULT_TYPE_CODE,
		PROJ_CODE,
		CONTRACT_NUMBER
	)
	SELECT
		TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')||'_'||P.PROJ_CODE,
		'ACTIVE',
		SUBSTRB('CONTRACT_NUMBER='||P.CONTRACT_NUMBER,1,4000),
		'UPDATE',
		SYSDATE,
		SYSDATE,
		IN_EXEC_USER_ID,
		IN_EXEC_USER_ID,
		'EXEC_PA6',
		'OFFICIAL',
		P.PROJ_CODE,
		PA6.CLASS_CODE
	FROM PROJECT_LIST P,
		PROJECT_LIST_PA6_XXX PA6
	WHERE PA6.PROJ_STATUS != 'Rejected'
	AND   PA6.PROJ_STATUS != 'Closed'
	AND   PA6.CLASS_CATEGORY = 'CONTRACT NUMBER'
	AND   PA6.GSI_PROJ_CODE = SUBSTR(P.GSI_PROJ_CODE,1,9)
	AND   P.DATA_UPD_TYPE IN (0,1,2)
	AND   NVL(PA6.CLASS_CODE, ' ') != NVL(P.CONTRACT_NUMBER, ' ')
	;

	OUT_RESULT_CODE := '200';
	COMMIT;

EXCEPTION
	WHEN OTHERS THEN
		N_ERR_CODE := SQLCODE;
		V_ERR_MSG := SUBSTR(SQLERRM,1,100);
		INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
		VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.EXEC_PA6 �\�����Ȃ��G���[');
		COMMIT;
		OUT_RESULT_CODE := '500 �\�����Ȃ��G���[���������܂��� EXEC_PA6';

END EXEC_PA6;


-- ====================================================
-- PROCEDURE EXEC_PA7
-- PROJECT_LIST_PA7_XXX��PROJECT_LIST�̃v���W�F�N�g�f�[�^��r��
-- �����������PROJECT_LIST_INTERFACE�ɓo�^����Bproject_list.data_upd_type in (0,1,2)�̂ݑΏہB
--
-- Primary=�_���ASecondary=EndUser�BSecondary���Ȃ��ꍇEndUser��null�B
--
-- ���̓p�����[�^�FIN_EXEC_USER_ID
--   ���s���[�UID
-- ���^�[���R�[�h�FOUT_RESUT_CODE
--   ����I��=200
--   �ُ�I��=�G���[�R�[�h
-- ��O�����F
--   ERR_TABLE�Ƀ��O���o�͂��ăG���[�R�[�h��Ԃ�
-- ====================================================
PROCEDURE EXEC_PA7
(
	IN_EXEC_USER_ID	IN	NUMBER,
	OUT_RESULT_CODE	OUT	VARCHAR2
)
IS

N_ERR_CODE		ERR_TABLE.ERR_CODE%TYPE;
V_ERR_MSG		ERR_TABLE.ERR_MSG%TYPE;

BEGIN

	-- INTERFACE�\�ɕύX�Ώۂ�o�^�AERROR_MSG�ɂ͕ύX�O�f�[�^���i�[
	INSERT INTO PROJECT_LIST_INTERFACE(
		PROJECT_INTERFACE_ID,
		INTERFACE_STATUS_CODE,
		ERROR_MSG,
		MODE_CODE,
		CREATION_DATE,
		LAST_UPDATE_DATE,
		CREATED_BY,
		LAST_UPDATED_BY,
		LAST_UPDATE_LOGIN,
		DEFAULT_TYPE_CODE,
		PROJ_CODE,
		CLIENT_NAME,
		END_USER_NAME
	)
	SELECT
		TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')||'_'||P.PROJ_CODE,
		'ACTIVE',
		SUBSTRB('client_name='||P.CLIENT_NAME ||',end_user_name=' || P.END_USER_NAME,1,4000),
		'UPDATE',
		SYSDATE,
		SYSDATE,
		IN_EXEC_USER_ID,
		IN_EXEC_USER_ID,
		'EXEC_PA7',
		'OFFICIAL',
		P.PROJ_CODE,
		NVL(PA7.CLIENT_NAME,' '),
		NVL(PA7.END_USER_NAME,' ')
	FROM PROJECT_LIST P,
		(SELECT PA7.GSI_PROJ_CODE, PA7.PROJ_STATUS, CLIENT.CUSTOMER_NAME CLIENT_NAME, EU.CUSTOMER_NAME END_USER_NAME
		FROM
			(SELECT DISTINCT GSI_PROJ_CODE, PROJ_STATUS FROM PROJECT_LIST_PA7_XXX) PA7,
			(SELECT GSI_PROJ_CODE, CUSTOMER_NAME FROM PROJECT_LIST_PA7_XXX
			WHERE CUST_RELATION = 'Primary'
			GROUP BY GSI_PROJ_CODE, CUSTOMER_NAME
			)CLIENT,
			(SELECT GSI_PROJ_CODE, CUSTOMER_NAME FROM PROJECT_LIST_PA7_XXX
			WHERE CUST_RELATION = 'Secondary'
			)EU
		where PA7.GSI_PROJ_CODE = CLIENT.GSI_PROJ_CODE(+)
		and   PA7.GSI_PROJ_CODE = EU.GSI_PROJ_CODE(+)
		) PA7
	WHERE PA7.PROJ_STATUS != 'Rejected'
	AND   PA7.PROJ_STATUS != 'Closed'
	AND   PA7.GSI_PROJ_CODE = SUBSTR(P.GSI_PROJ_CODE,1,9)
	AND   P.DATA_UPD_TYPE IN (0,1,2)
	AND  ( NVL(PA7.CLIENT_NAME, ' ') != NVL(P.CLIENT_NAME, ' ')
		OR NVL(PA7.END_USER_NAME, ' ') != NVL(P.END_USER_NAME, ' ') )
	;

	OUT_RESULT_CODE := '200';
	COMMIT;

EXCEPTION
	WHEN OTHERS THEN
		N_ERR_CODE := SQLCODE;
		V_ERR_MSG := SUBSTR(SQLERRM,1,100);
		INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
		VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.EXEC_PA7 �\�����Ȃ��G���[');
		COMMIT;
		OUT_RESULT_CODE := '500 �\�����Ȃ��G���[���������܂��� EXEC_PA7';

END EXEC_PA7;

-- ====================================================
-- PROCEDURE EXEC_PROJECT_STATUS
-- PROJECT_LIST_PA2_XXX��PROJECT_LIST��PROJECT_STATUS��r��
-- �����������PROJECT_LIST_INTERFACE�ɓo�^����Bproject_list.data_upd_type in (0,1,2)�̂ݑΏہB
--
-- ���̓p�����[�^�FIN_EXEC_USER_ID
--   ���s���[�UID
-- ���^�[���R�[�h�FOUT_RESUT_CODE
--   ����I��=200
--   �ُ�I��=�G���[�R�[�h
-- ��O�����F
--   ERR_TABLE�Ƀ��O���o�͂��ăG���[�R�[�h��Ԃ�
-- ====================================================
PROCEDURE EXEC_PROJECT_STATUS
(
	IN_EXEC_USER_ID	IN	NUMBER,
	OUT_RESULT_CODE	OUT	VARCHAR2
)
IS

N_ERR_CODE		ERR_TABLE.ERR_CODE%TYPE;
V_ERR_MSG		ERR_TABLE.ERR_MSG%TYPE;

BEGIN

	-- INTERFACE�\�ɕύX�Ώۂ�o�^�AERROR_MSG�ɂ͕ύX�O�f�[�^���i�[
	INSERT INTO PROJECT_LIST_INTERFACE(
		PROJECT_INTERFACE_ID,
		INTERFACE_STATUS_CODE,
		ERROR_MSG,
		MODE_CODE,
		CREATION_DATE,
		LAST_UPDATE_DATE,
		CREATED_BY,
		LAST_UPDATED_BY,
		LAST_UPDATE_LOGIN,
		DEFAULT_TYPE_CODE,
		PROJ_CODE,
		WORK_STATUS
	)
	SELECT
		TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')||'_'||P.PROJ_CODE,
		'ACTIVE',
		SUBSTRB('project_status_name:('||P.PROJECT_STATUS_NAME||'=>'||PA2.PROJECT_STATUS_NAME||'),work_status = '||P.WORK_STATUS,1,4000),
		'UPDATE',
		SYSDATE,
		SYSDATE,
		IN_EXEC_USER_ID,
		IN_EXEC_USER_ID,
		'EXEC_PROJECT_STATUS',
		'OFFICIAL',
		P.PROJ_CODE,
		DECODE(PA2.PROJECT_STATUS_NAME,'Approved',0,'Bid',0,'WAR Expired',0
        ,'At Risk',1,'Pre-WAR',1
        ,'Post-WAR',2,'Active',2
        ,'Accepted',3)
	FROM PROJECT_LIST P, (SELECT PA2_XXX.GSI_PROJ_CODE, PA2_XXX.ATTRIBUTE2 PROJECT_STATUS_NAME
                               FROM PROJECT_LIST_PA2_XXX PA2_XXX
                               GROUP BY PA2_XXX.GSI_PROJ_CODE, PA2_XXX.ATTRIBUTE2) PA2
	WHERE P.WORK_STATUS != 3
    AND   P.DATA_UPD_TYPE IN (0,1,2)
	AND   PA2.GSI_PROJ_CODE = P.GSI_PROJ_CODE
	AND  (NVL(PA2.PROJECT_STATUS_NAME,'NULL') != NVL(P.PROJECT_STATUS_NAME,'NULL')
		)
	-- PROJECT_STATUS_NAME�ɒl�������Ă�����̂̂݊Ǘ�
	AND P.PROJECT_STATUS_NAME is not null
	;

    	-- INTERFACE�\�ɕύX�Ώۂ�o�^�A��荞�܂Ȃ��Ȃ����v���W�F�N�g�̍X�V
	INSERT INTO PROJECT_LIST_INTERFACE(
		PROJECT_INTERFACE_ID,
		INTERFACE_STATUS_CODE,
		ERROR_MSG,
		MODE_CODE,
		CREATION_DATE,
		LAST_UPDATE_DATE,
		CREATED_BY,
		LAST_UPDATED_BY,
		LAST_UPDATE_LOGIN,
		DEFAULT_TYPE_CODE,
		PROJ_CODE,
		WORK_STATUS
	)
	SELECT
		TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')||'_'||P.PROJ_CODE,
		'ACTIVE',
		SUBSTRB('project_status_name:('||P.PROJECT_STATUS_NAME||'=> Close),work_status = '||P.WORK_STATUS,1,4000),
		'UPDATE',
		SYSDATE,
		SYSDATE,
		IN_EXEC_USER_ID,
		IN_EXEC_USER_ID,
		'EXEC_PROJECT_STATUS',
		'OFFICIAL',
		P.PROJ_CODE,
		3
	FROM PROJECT_LIST P
	WHERE P.WORK_STATUS != 3
    AND   P.DATA_UPD_TYPE IN (0,1,2)
	AND   SUBSTR(P.GSI_PROJ_CODE,1,9) not in (SELECT PA2_XXX.GSI_PROJ_CODE
                               FROM PROJECT_LIST_PA2_XXX PA2_XXX
                               GROUP BY PA2_XXX.GSI_PROJ_CODE)
	-- PROJECT_STATUS_NAME�ɒl�������Ă�����̂̂݊Ǘ�
	AND P.PROJECT_STATUS_NAME is not null
	;


    --PROJECT_STATUS_NAME�̍X�V
    UPDATE PROJECT_LIST P 
    set p.PROJECT_STATUS_NAME = 
    (SELECT PA2.PROJECT_STATUS_NAME 
        FROM (SELECT PA2_XXX.GSI_PROJ_CODE, PA2_XXX.ATTRIBUTE2 PROJECT_STATUS_NAME
        FROM PROJECT_LIST_PA2_XXX PA2_XXX
        GROUP BY PA2_XXX.GSI_PROJ_CODE, PA2_XXX.ATTRIBUTE2) PA2 
        WHERE PA2.GSI_PROJ_CODE = SUBSTR(P.GSI_PROJ_CODE,1,9))
    WHERE (SELECT COUNT(*) 
        FROM (SELECT PA2_XXX.GSI_PROJ_CODE, PA2_XXX.ATTRIBUTE2 PROJECT_STATUS_NAME
        FROM PROJECT_LIST_PA2_XXX PA2_XXX
        GROUP BY PA2_XXX.GSI_PROJ_CODE, PA2_XXX.ATTRIBUTE2) PA2 
        WHERE PA2.GSI_PROJ_CODE = SUBSTR(P.GSI_PROJ_CODE,1,9)) > 0;


	OUT_RESULT_CODE := '200';
	COMMIT;

EXCEPTION
	WHEN OTHERS THEN
		N_ERR_CODE := SQLCODE;
		V_ERR_MSG := SUBSTR(SQLERRM,1,100);
		INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
		VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.EXEC_PROJECT_STATUS �\�����Ȃ��G���[');
		COMMIT;
		OUT_RESULT_CODE := '500 �\�����Ȃ��G���[���������܂��� EXEC_PROJECT_STATUS';

END EXEC_PROJECT_STATUS;


-- ====================================================
-- PROCEDURE SEED_PROJ_NO
-- PROJ_OPPO_NO_XXX�Ƀf�[�^�����݂��APROJECT_LIST.gsi_proj_code���Ȃ����̂�
-- PROJECT_LIST�ɔ��f�A
-- PROJECT_LIST_INTERFACE����INSERT�Ńf�[�^��o�^���ACOMPLETE�ɂ��Ă���
-- (���[�����M�������邽�߁B)
-- KKPA�V�K�f�[�^�́APJ#�̖�����'_KK'������
--
-- ���^�[���R�[�h�FOUT_RESUT_CODE
--   ����I��=200
--   �ُ�I��=�G���[�R�[�h
-- ��O�����F
--   ERR_TABLE�Ƀ��O���o�͂��Ď��̃f�[�^�֑��s
-- ====================================================
PROCEDURE SEED_PROJ_NO
(
	IN_EXEC_USER_ID	IN	NUMBER,
	OUT_RESULT_CODE	OUT	VARCHAR2
)
IS

N_ERR_CODE		ERR_TABLE.ERR_CODE%TYPE;
V_ERR_MSG		ERR_TABLE.ERR_MSG%TYPE;

--�X�V�p�J�[�\��
CURSOR i_line IS
	WITH VIEW_PA5 AS
	(SELECT PA5.gsi_proj_code,PA5.proj_role,e.emp_id,PA5.proj_status,PA5.start_date,PA5.end_date
	  FROM PROJECT_LIST_PA5_XXX PA5,EMP_LIST e
	 WHERE LOWER(PA5.ATTRIBUTE1) = e.MAIL_ADDRESS)
	SELECT
		P.PROJ_CODE,
		DECODE(PA2.ATTRIBUTE1,'Oracle Information Systems (Japan) G.K.',NX.GSI_PROJ_CODE||'_KK',NX.GSI_PROJ_CODE) GSI_PROJ_CODE,
		PA2.PROJ_NAME,
		PA2.PROJ_START,
		PA2.PROJ_END,
		-- �_��`�Ԃ�ICR�̏ꍇ��PO�̃R�X�g�Z���^���B����ȊO��PJ�̑g�D��ݒ�
		NVL(DECODE(PA2.CONT_TYPE,'ICR', PM_CC.COST_CENTER_ID,'ITMFC', PM_CC.COST_CENTER_ID, V_CC_ORG_ID_P||SUBSTR(PA2.PROJ_ORG,1,6)), V_CC_ORG_ID ) UNIT_ID,
		DECODE(PA2.CONT_TYPE,'TM',1,'FP',2,'ICR',3,'ITM',4,'IRT',6,'XRT',7,'XCR',8,'I',15,'ITMFC',3,'ITMFR',11,9) CONTRACT_ID
	FROM PROJECT_LIST P, PROJ_OPPO_NO_XXX NX, PROJECT_LIST_PA2_XXX PA2
		-- PO�̃R�X�g�Z���^�ꗗ
        	,(SELECT PM.GSI_PROJ_CODE, E.COST_CENTER_ID
		FROM EMP_LIST E,
		(SELECT PA5.GSI_PROJ_CODE, MIN(PA5.EMP_ID) EMP_ID
		FROM VIEW_PA5 PA5
		WHERE PA5.PROJ_ROLE = 'Project Manager'
		AND   PA5.START_DATE = (SELECT MAX(START_DATE)
			FROM VIEW_PA5
			WHERE GSI_PROJ_CODE = PA5.GSI_PROJ_CODE
			AND   PROJ_ROLE = PA5.PROJ_ROLE)
                GROUP BY PA5.GSI_PROJ_CODE
		) PM
		WHERE E.EMP_ID = PM.EMP_ID
		) PM_CC
	WHERE NX.OPPO_NUMBER = P.OPPO_NUMBER
	AND   NX.GSI_PROJ_CODE = PA2.GSI_PROJ_CODE
	AND   NX.GSI_PROJ_CODE = PM_CC.GSI_PROJ_CODE(+)
	AND   P.GSI_PROJ_CODE IS NULL
	AND   P.OPPO_NUMBER IS NOT NULL
	AND   P.DATA_UPD_TYPE IN (0,1,2);
i_rec  i_line%ROWTYPE;


BEGIN

	OPEN i_line;
	LOOP
		FETCH i_line INTO i_rec;
		EXIT WHEN i_line%NOTFOUND;

		BEGIN
			-- gsi_proj_code,proj_name,proj_start,proj_end,cons_start,cons_end,proj_status,unit_id,contract_id�ɒl������
			UPDATE PROJECT_LIST P
			SET GSI_PROJ_CODE = i_rec.GSI_PROJ_CODE
			, PROJ_NAME = i_rec.PROJ_NAME
			, PROJ_START = i_rec.PROJ_START
			, PROJ_END = i_rec.PROJ_END
			, CONS_START = i_rec.PROJ_START
			, CONS_END = i_rec.PROJ_END
--			, PROJ_STATUS = DECODE(P.PROJ_STATUS, 99, 99, N_C_30)
--			, UNIT_ID = i_rec.UNIT_ID
			, CONTRACT_ID = i_rec.CONTRACT_ID
			WHERE PROJ_CODE = i_rec.PROJ_CODE;

			-- �g�D����proj_org_list���A�b�v�f�[�g
--			UPDATE PROJ_ORG_LIST
--			SET CC_ORG_ID = i_rec.UNIT_ID
--			WHERE PROJ_CODE = i_rec.PROJ_CODE
--			AND   END_DATE IS NULL;

			-- ���[�����M�̂��߂�INTERFACE�ɓo�^.status='COMPLETE'
            -- ��ق�UNIT_ID�͍폜�\��
			INSERT INTO PROJECT_LIST_INTERFACE(
				PROJECT_INTERFACE_ID, INTERFACE_STATUS_CODE, MODE_CODE, CREATION_DATE, LAST_UPDATE_DATE, CREATED_BY, LAST_UPDATED_BY, LAST_UPDATE_LOGIN,
				DEFAULT_TYPE_CODE, PROJ_CODE, GSI_PROJ_CODE, PROJ_NAME, PROJ_START, PROJ_END, CONS_START, CONS_END, PROJ_STATUS, UNIT_ID, CONTRACT_ID
			)VALUES(
				TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')||'_'||i_rec.PROJ_CODE, 'COMPLETE', 'INSERT', SYSDATE, SYSDATE, IN_EXEC_USER_ID, IN_EXEC_USER_ID, 'SEED_PROJ_NO',
				'OFFICIAL', i_rec.PROJ_CODE, i_rec.GSI_PROJ_CODE, i_rec.PROJ_NAME, i_rec.PROJ_START, i_rec.PROJ_END, i_rec.PROJ_START, i_rec.PROJ_END, NULL, i_rec.UNIT_ID, i_rec.CONTRACT_ID
			);
		EXCEPTION
			WHEN OTHERS THEN
				ROLLBACK;
				N_ERR_CODE := SQLCODE;
				V_ERR_MSG := SUBSTR(SQLERRM,1,100);
				INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
				VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.SEED_PROJ_NO �\�����Ȃ��G���[ proj_code='||i_rec.PROJ_CODE);
			COMMIT;
		END;
	END LOOP;
	CLOSE i_line;

	COMMIT;

	OUT_RESULT_CODE := '200';

EXCEPTION
	WHEN OTHERS THEN
		N_ERR_CODE := SQLCODE;
		V_ERR_MSG := SUBSTR(SQLERRM,1,100);
		INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
		VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.SEED_PROJ_NO �\�����Ȃ��G���[');
		COMMIT;
		OUT_RESULT_CODE := '500 �\�����Ȃ��G���[���������܂��� SEED_PROJ_NO';

END SEED_PROJ_NO;


-- ====================================================
-- PROCEDURE SEED_PROJECT
-- PROJECT_LIST_INTERFACE�ɓo�^����Ă���f�[�^����������
--
-- ���̓p�����[�^�FIN_EXEC_USER_ID
--   ���s���[�UID
-- ���^�[���R�[�h�FOUT_RESUT_CODE
--   ����I��=200
--   �ُ�I��=�G���[�R�[�h
-- ��O�����F
--   ERR_TABLE�Ƀ��O���o�͂��Ď��̃f�[�^�֑��s
-- ====================================================
PROCEDURE SEED_PROJECT
(
	IN_EXEC_USER_ID	IN	NUMBER,
	OUT_RESULT_CODE	OUT	VARCHAR2
)
IS

--=================================================
-- PROJECT_LIST_INTERFACE �\���烌�R�[�h���擾����J�[�\��
--=================================================
FY_START_DATE   DATE;
MAP_PROD_ORG_ID VARCHAR2(12);
V_MODE_CODE	PROJECT_LIST_INTERFACE.MODE_CODE%TYPE;
CURSOR i_line IS
	SELECT *
	FROM PROJECT_LIST_INTERFACE
	WHERE (INTERFACE_STATUS_CODE = 'ACTIVE'
	OR INTERFACE_STATUS_CODE IS NULL)
	AND (MODE_CODE = 'INSERT'
	OR MODE_CODE = 'UPDATE');
i_rec  i_line%ROWTYPE;

BEGIN
	OPEN i_line;
	LOOP
		FETCH i_line INTO i_rec;
		EXIT WHEN i_line%NOTFOUND;

		BEGIN
			--=================================================
			-- �����Ώۂ̃��R�[�h���X�e�[�^�X�ύX
			--=================================================
			UPDATE PROJECT_LIST_INTERFACE
			SET
				INTERFACE_STATUS_CODE = 'PROGRESS'
			WHERE PROJECT_INTERFACE_ID = i_rec.PROJECT_INTERFACE_ID;
			--================================================
			-- �n���ꂽ�p�����[�^�����Ƃɓo�^���s��
			--================================================
			V_MODE_CODE := i_rec.MODE_CODE;

			IF V_MODE_CODE = 'INSERT' THEN

				INSERT INTO PROJECT_LIST
				(
					PROJ_CODE
					,GSI_PROJ_CODE
					,PROJ_NAME
					,WORK_STATUS
					,PROJ_STATUS
--					,UNIT_ID
					,PA_CC_ID
					,CONTRACT_ID
					,CONS_START
					,CONS_END
					,PROJ_START
					,PROJ_END
					,UPDATE_PERSON
					,UPDATE_DATE
					,DEL_FLG
					,BOOK_STATUS
					,REV_STATUS
					,ADMI
                    ,CONTRACT_NUMBER
                    ,COST_CENTER_FULL_NAME
				)
				VALUES
				(
					i_rec.PROJ_CODE
					,i_rec.GSI_PROJ_CODE
					,i_rec.PROJ_NAME
					,i_rec.WORK_STATUS
					,i_rec.PROJ_STATUS
--					,i_rec.UNIT_ID
					,i_rec.PA_CC_ID
					,i_rec.CONTRACT_ID
					,i_rec.PROJ_START
					,i_rec.PROJ_END
					,DECODE(i_rec.PROJ_START,NULL_DATE,NULL,i_rec.PROJ_START)
					,DECODE(i_rec.PROJ_END,NULL_DATE,NULL,i_rec.PROJ_END)
					,IN_EXEC_USER_ID
					,SYSDATE
					,0
					,i_rec.PROJ_STATUS		-- ��UBEST�œ����
					,i_rec.PROJ_STATUS		-- ��UBEST�œ����
					,i_rec.ADMI
                    ,i_rec.CONTRACT_NUMBER
                    ,i_rec.COST_CENTER_FULL_NAME
				);

				-- �g�D���͂�����
				SELECT TO_DATE(value,'YYYY/MM/DD') INTO FY_START_DATE
				  FROM CONST_LIST
				 WHERE name = 'FY_START_DATE';

				SELECT MIN(OM.PROD_ORG_ID) INTO MAP_PROD_ORG_ID
				  FROM PROD_CC_ORG_MAP OM,PRODUCT_ORG_LIST OL
				 WHERE CC_ORG_ID = i_rec.UNIT_ID
			      AND OM.PROD_ORG_ID = OL.ORG_ID
			      AND OL.ORG_END_DATE IS NULL;

				INSERT INTO PROJ_ORG_LIST(PROJ_CODE, PROD_ORG_ID, CC_ORG_ID, START_DATE, END_DATE)
				VALUES(i_rec.PROJ_CODE, NVL(MAP_PROD_ORG_ID,'P1000381'), DECODE(i_rec.UNIT_ID,'CC00501636','CC00506245','CC00508835','CC00506245',i_rec.UNIT_ID), FY_START_DATE, NULL);

			ELSIF V_MODE_CODE = 'UPDATE' THEN

				UPDATE PROJECT_LIST
				SET
					PROJ_NAME = DECODE(i_rec.PROJ_NAME,null,PROJ_NAME,' ',null,i_rec.PROJ_NAME),
					PROJ_START = CASE WHEN i_rec.PROJ_START IS NULL THEN PROJ_START
						WHEN i_rec.PROJ_START = NULL_DATE THEN NULL
						ELSE i_rec.PROJ_START END
					,PROJ_END = CASE WHEN i_rec.PROJ_END IS NULL THEN PROJ_END
						WHEN i_rec.PROJ_END = NULL_DATE THEN NULL
						ELSE i_rec.PROJ_END END
					,CONTRACT_DATE = CASE WHEN i_rec.CONTRACT_DATE IS NULL THEN CONTRACT_DATE
						WHEN i_rec.CONTRACT_DATE = NULL_DATE THEN NULL
						ELSE i_rec.CONTRACT_DATE END
					,CONTRACT_CREATION_DATE = CASE WHEN i_rec.CONTRACT_CREATION_DATE IS NULL THEN CONTRACT_CREATION_DATE
						WHEN i_rec.CONTRACT_CREATION_DATE = NULL_DATE THEN NULL
						ELSE i_rec.CONTRACT_CREATION_DATE END
					,END_USER_NAME = DECODE(i_rec.END_USER_NAME,null,END_USER_NAME,' ',null,i_rec.END_USER_NAME)
					,CLIENT_NAME = DECODE(i_rec.CLIENT_NAME,null,CLIENT_NAME,' ',null,i_rec.CLIENT_NAME)
					,SALES_DEPT_ID = NVL(i_rec.SALES_DEPT_ID,SALES_DEPT_ID)
					,SALES_ID = NVL(TO_CHAR(i_rec.SALES_ID),SALES_ID)
					,PROJ_OWNER_ID = NVL(TO_CHAR(i_rec.PROJ_OWNER_ID),PROJ_OWNER_ID)
					,PROJ_MGR_ID = NVL(TO_CHAR(i_rec.PROJ_MGR_ID),PROJ_MGR_ID)
                    ,SEC_PM = NVL(TO_CHAR(i_rec.SEC_PM),SEC_PM)
					,CONTRACT_ID = NVL(TO_CHAR(i_rec.CONTRACT_ID),CONTRACT_ID)
					,PA_CC_ID = NVL(i_rec.PA_CC_ID,PA_CC_ID)
					,FPAUTO = NVL(i_rec.FPAUTO,FPAUTO)
					,CONT_AMOUNT = CASE WHEN i_rec.CONT_AMOUNT IS NULL THEN CONT_AMOUNT
						WHEN i_rec.CONT_AMOUNT = NULL_NUM THEN NULL
						ELSE i_rec.CONT_AMOUNT END
					,CLASS_CODE = NVL(i_rec.CLASS_CODE,CLASS_CODE)
					,ALLOW_LOB_INVOICE = NVL(i_rec.ALLOW_LOB_INVOICE,NVL(ALLOW_LOB_INVOICE,'No'))
                    ,WORK_STATUS = NVL(i_rec.WORK_STATUS,WORK_STATUS)
                    ,CONTRACT_NUMBER = NVL(i_rec.CONTRACT_NUMBER,CONTRACT_NUMBER)
					,UPDATE_PERSON = IN_EXEC_USER_ID
					,UPDATE_DATE = SYSDATE
                    ,COST_CENTER_FULL_NAME = NVL(i_rec.COST_CENTER_FULL_NAME,COST_CENTER_FULL_NAME)
				WHERE PROJ_CODE = i_rec.PROJ_CODE;

			END IF;

			--=================================================
			-- �����������������R�[�h�̃X�e�[�^�X�ύX
			--=================================================
			UPDATE PROJECT_LIST_INTERFACE
			SET INTERFACE_STATUS_CODE = 'COMPLETE'
			WHERE PROJECT_INTERFACE_ID = i_rec.PROJECT_INTERFACE_ID;
			COMMIT;

		--===============================================
		-- ��O����
		-- �J�[�\�����̃G���[�̓X�e�[�^�X��ERROR�ɕύX���Ď��֐i��
		--===============================================
		EXCEPTION
			WHEN OTHERS THEN
				NULL;
		END;

	END LOOP;
	CLOSE i_line;

	COMMIT;
	OUT_RESULT_CODE := '200';
EXCEPTION
	WHEN OTHERS THEN
		NULL;



END SEED_PROJECT;


-- ====================================================
-- PROCEDURE SEND_INSERTED_PROJECT_MAIL
-- PROJECT_LIST_INTERFACE�\���m�F���A
-- ���̓��ɐV�K�o�^���ꂽ���̂����[���ő��M����
-- ��1����2�x���s�����1�x�ڂ̂��̂ɂ����M�����̂Œ���
--
-- ���̓p�����[�^�FIN_EXEC_USER_ID
--   ���s���[�UID
-- ���^�[���R�[�h�FOUT_RESUT_CODE
--   ����I��=200
--   �ُ�I��=�G���[�R�[�h
-- ��O�����F
--   ERR_TABLE�Ƀ��O���o�͂��ăG���[�R�[�h��Ԃ�
-- ====================================================
PROCEDURE SEND_INSERTED_PROJECT_MAIL

IS

N_IS_HONBAN	NUMBER;		--�{�Ԋ����ǂ���
--MAIL_TO		MAIL_ADDRESSES;
V_MAIL_TO	VARCHAR2(10000);
N_MAIL_TO_CNT	NUMBER;
V_FROM_ADDRESS	EMP_LIST.MAIL_ADDRESS%TYPE;
V_MAIL_DOMAIN	VARCHAR2(100) := COMMON_PACK.GETCONST('MAIL_DOMAIN');
V_CONTENTS	VARCHAR2(4000);
V_TITLE		VARCHAR2(100);
V_PROJ_URL 	VARCHAR2(100);

N_ERR_CODE	ERR_TABLE.ERR_CODE%TYPE;
V_ERR_MSG	ERR_TABLE.ERR_MSG%TYPE;

-- �V�K�o�^���̃f�[�^�J�[�\��
CURSOR p_line IS
	SELECT DISTINCT
		P.PROJ_CODE
		,P.OPPO_NUMBER
		,P.GSI_PROJ_CODE
		,P.PROJ_NAME
		,P.CLIENT_NAME
		,C.ORG_NAME CC_NAME
		,PO.EMP_NAME PO
		,PO.MAIL_ADDRESS PO_ADDRESS
		,PM.EMP_NAME PM
		,PM.MAIL_ADDRESS PM_ADDRESS
		,CONT.CONTRACT_NAME
	FROM
		(SELECT GSI_PROJ_CODE,PROJ_CODE
		FROM PROJECT_LIST_INTERFACE
		WHERE INTERFACE_STATUS_CODE  ='COMPLETE'
		AND MODE_CODE = 'INSERT'
		AND TO_CHAR(CREATION_DATE,'YYYYMMDD') = TO_CHAR(SYSDATE,'YYYYMMDD')) t
		,PROJECT_LIST P
		,PROJ_ORG_LIST PORG
		,EMP_LIST PO
		,EMP_LIST PM
		,COST_CENTER_LIST C
		,CONTRACT_LIST CONT
	WHERE T.PROJ_CODE = P.PROJ_CODE
	AND   P.PROJ_MGR_ID = PM.EMP_ID(+)
	AND   P.PROJ_OWNER_ID = PO.EMP_ID(+)
	AND   P.PROJ_CODE = PORG.PROJ_CODE
	AND   PORG.END_DATE IS NULL
	AND   PORG.CC_ORG_ID = C.ORG_ID(+)
	AND   P.CONTRACT_ID = CONT.CONTRACT_ID(+)
	AND   C.ORG_START_DATE = (SELECT MAX(ORG_START_DATE) FROM COST_CENTER_LIST WHERE ORG_ID = C.ORG_ID)
--�@OMC�Ή�(2018/10/02 �P��)
--    AND   P.PA_CC_ID != '506175'
	;

p_rec  p_line%ROWTYPE;

BEGIN

	--�{�Ԃƃe�X�g�Ń��[�����M�����ς�����悤�ɂ��Ă���
	SELECT COUNT(*) INTO N_IS_HONBAN FROM CONST_LIST WHERE NAME = '��' AND VALUE = '�{��';

	-- URL
	SELECT VALUE
	INTO V_PROJ_URL
	FROM CONST_LIST
	WHERE NAME = 'INSERT_PROJ_URL';

	-- FROM
	SELECT (E.MAIL_ADDRESS||'@'||V_MAIL_DOMAIN)
	INTO V_FROM_ADDRESS
	FROM MAIL_CASE_LIST MC, MAIL_RECIPIENT_LIST MR, EMP_LIST E
	WHERE MC.MAIL_CASE_ID = MR.MAIL_CASE_ID
	AND   MR.EMP_ID = E.EMP_ID
	AND   MC.MAIL_CASE_NAME = '�V�X�e���Ǘ���'
	;
	-- TO
	N_MAIL_TO_CNT :=1;
	IF N_IS_HONBAN = 1 THEN
		--�{��
--		MAIL_TO := MAIL_ADDRESSES(null,null,null,null,null,null,null,null,null,null);
		FOR ADD_REC IN
			(SELECT ROWNUM NUM,(E.MAIL_ADDRESS||'@'||V_MAIL_DOMAIN) MAIL
			FROM EMP_LIST E,MAIL_RECIPIENT_LIST R, MAIL_CASE_LIST C
			WHERE C.MAIL_CASE_NAME = '�v���W�F�N�g�o�^�^�X�V'
			AND   R.MAIL_CASE_ID = C.MAIL_CASE_ID
			AND   E.EMP_ID = R.EMP_ID
			-- �ސE�҂͏������AML�͑ΏۂƂ���
			AND   (E.DEL_FLG = 0 OR E.EMP_ID >= 9000000)
		) LOOP
			IF N_MAIL_TO_CNT = 1 THEN
				V_MAIL_TO := ADD_REC.MAIL;
			ELSE
				V_MAIL_TO := V_MAIL_TO || ',' || ADD_REC.MAIL;
			END IF;
			N_MAIL_TO_CNT := N_MAIL_TO_CNT + 1;
--		MAIL_TO(ADD_REC.NUM) := ADD_REC.MAIL;
		END LOOP;
	ELSE
		--�e�X�g
--		MAIL_TO := MAIL_ADDRESSES(null,null,null,null,null,null,null,null,null,null);
		FOR ADD_REC IN
			(SELECT ROWNUM NUM,(E.MAIL_ADDRESS||'@'||V_MAIL_DOMAIN) MAIL
			FROM MAIL_CASE_LIST MC, MAIL_RECIPIENT_LIST MR, EMP_LIST E
			WHERE MC.MAIL_CASE_ID = MR.MAIL_CASE_ID
			AND   MR.EMP_ID = E.EMP_ID
			AND   MC.MAIL_CASE_NAME = '�e�X�g�p���M��'
		) LOOP
			IF N_MAIL_TO_CNT = 1 THEN
				V_MAIL_TO := ADD_REC.MAIL;
			ELSE
				V_MAIL_TO := V_MAIL_TO || ',' || ADD_REC.MAIL;
			END IF;
			N_MAIL_TO_CNT := N_MAIL_TO_CNT + 1;

--			MAIL_TO(ADD_REC.NUM) := ADD_REC.MAIL;
		END LOOP;
	END IF;

	OPEN p_line;
	LOOP
		FETCH p_line INTO p_rec;
		EXIT WHEN p_line%NOTFOUND;

		BEGIN
			-- TITLE
			IF N_IS_HONBAN = 1 THEN
				V_TITLE := 'Project Registered!(PA/'||p_rec.GSI_PROJ_CODE||')';
			ELSE
				V_TITLE := 'Project Registered!(PA/'||p_rec.GSI_PROJ_CODE||') TEST��';
			END IF;

			-- CONTENTS
			V_CONTENTS := '�V�K�Č��o�^��'||chr(13)||chr(10)

				||chr(13)||chr(10)
				||'Temp Number       = '||p_rec.PROJ_CODE||chr(13)||chr(10)
				||'Oppo Number       = '||p_rec.OPPO_NUMBER||chr(13)||chr(10)
				||'Project Number    = '||p_rec.GSI_PROJ_CODE||chr(13)||chr(10)
				||'Project Name(����)= '||p_rec.PROJ_NAME||chr(13)||chr(10)
				||'CLIENT            = '||p_rec.CLIENT_NAME||chr(13)||chr(10)
				||'�g�D��            = '||p_rec.CC_NAME||chr(13)||chr(10)
                ||'PO                = '||p_rec.PO||chr(13)||chr(10)
				||'PM                = '||p_rec.PM||chr(13)||chr(10)
				||'�_��`��          = '||p_rec.CONTRACT_NAME||chr(13)||chr(10)
				||chr(13)||chr(10)
				||V_PROJ_URL||'&'||'c_proj_code='||p_rec.PROJ_CODE;

			-- ====================================================
			-- ���[�����M
			-- ====================================================
			SENDMAIL(V_FROM_ADDRESS,V_MAIL_TO,V_TITLE,V_CONTENTS);

		EXCEPTION
			WHEN OTHERS THEN
				N_ERR_CODE := SQLCODE;
				V_ERR_MSG := SUBSTR(SQLERRM,1,100);
				INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
				VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.SEND_INSERTED_PROJECT_MAIL �\�����Ȃ��G���[');
				COMMIT;
		END;

	END LOOP;
	CLOSE p_line;

EXCEPTION
	WHEN OTHERS THEN
		N_ERR_CODE := SQLCODE;
		V_ERR_MSG := SUBSTR(SQLERRM,1,100);
		INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
		VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.SEND_INSERTED_PROJECT_MAIL �\�����Ȃ��G���[');
		COMMIT;

END SEND_INSERTED_PROJECT_MAIL;

-- ====================================================
-- PROCEDURE SEND_PA1_MAIL
-- PROJECT_PA1_SENDMAIL_LIST�\���m�F���A
-- SENDMAIL_FLG=1�̂��̂����[�����M����B
-- ��O�����F
--   ERR_TABLE�Ƀ��O���o�͂��ăG���[�R�[�h��Ԃ�
-- ====================================================
PROCEDURE SEND_PA1_MAIL

IS

N_IS_HONBAN	NUMBER;		--�{�Ԋ����ǂ���
V_MAIL_TO	VARCHAR2(10000);
N_MAIL_TO_CNT	NUMBER;
V_FROM_ADDRESS	EMP_LIST.MAIL_ADDRESS%TYPE;
V_MAIL_DOMAIN	VARCHAR2(100) := COMMON_PACK.GETCONST('MAIL_DOMAIN');
V_CONTENTS	VARCHAR2(4000);
V_TITLE		VARCHAR2(100);
V_PROJECT	VARCHAR2(4000);

N_ERR_CODE	ERR_TABLE.ERR_CODE%TYPE;
V_ERR_MSG	ERR_TABLE.ERR_MSG%TYPE;

-- �V�K�o�^���̃f�[�^�J�[�\��
CURSOR p_line IS
	SELECT DISTINCT
		M.PROJ_CODE
		,M.CONT_AMOUNT
		,M.CONTRACT_DATE
		,M.CONTRACT_CREATION_DATE
	 FROM PROJECT_PA1_SENDMAIL_LIST M
	WHERE SENDMAIL_FLG = 1;

p_rec  p_line%ROWTYPE;

BEGIN

	OPEN p_line;
	LOOP
		FETCH p_line INTO p_rec;
		EXIT WHEN p_line%NOTFOUND;

			V_PROJECT := V_PROJECT ||'Project Number    = '||p_rec.PROJ_CODE||chr(13)||chr(10) ||'���z              = '||p_rec.CONT_AMOUNT||chr(13)||chr(10)||'--' || chr(13)||chr(10);

	END LOOP;
	CLOSE p_line;

	IF V_PROJECT IS NOT NULL THEN

		--�{�Ԃƃe�X�g�Ń��[�����M�����ς�����悤�ɂ��Ă���
		SELECT COUNT(*) INTO N_IS_HONBAN FROM CONST_LIST WHERE NAME = '��' AND VALUE = '�{��';

		-- FROM
		SELECT (E.MAIL_ADDRESS||'@'||V_MAIL_DOMAIN)
		INTO V_FROM_ADDRESS
		FROM MAIL_CASE_LIST MC, MAIL_RECIPIENT_LIST MR, EMP_LIST E
		WHERE MC.MAIL_CASE_ID = MR.MAIL_CASE_ID
		AND   MR.EMP_ID = E.EMP_ID
		AND   MC.MAIL_CASE_NAME = '�V�X�e���Ǘ���'
		;
		-- TO
		N_MAIL_TO_CNT :=1;
		IF N_IS_HONBAN = 1 THEN
			--�{��
	--		MAIL_TO := MAIL_ADDRESSES(null,null,null,null,null,null,null,null,null,null);
			FOR ADD_REC IN
				(SELECT ROWNUM NUM,(E.MAIL_ADDRESS||'@'||V_MAIL_DOMAIN) MAIL
				FROM EMP_LIST E,MAIL_RECIPIENT_LIST R, MAIL_CASE_LIST C
				WHERE C.MAIL_CASE_NAME = 'CORRECTION�f�[�^�o�^�ʒm'
				AND   R.MAIL_CASE_ID = C.MAIL_CASE_ID
				AND   E.EMP_ID = R.EMP_ID
				AND   E.DEL_FLG = 0
			) LOOP
				IF N_MAIL_TO_CNT = 1 THEN
					V_MAIL_TO := ADD_REC.MAIL;
				ELSE
					V_MAIL_TO := V_MAIL_TO || ',' || ADD_REC.MAIL;
				END IF;
				N_MAIL_TO_CNT := N_MAIL_TO_CNT + 1;
	--		MAIL_TO(ADD_REC.NUM) := ADD_REC.MAIL;
			END LOOP;
			V_TITLE := 'CORRECTION�f�[�^�o�^�ʒm';
		ELSE
			--�e�X�g
	--		MAIL_TO := MAIL_ADDRESSES(null,null,null,null,null,null,null,null,null,null);
			FOR ADD_REC IN
				(SELECT ROWNUM NUM,(E.MAIL_ADDRESS||'@'||V_MAIL_DOMAIN) MAIL
				FROM MAIL_CASE_LIST MC, MAIL_RECIPIENT_LIST MR, EMP_LIST E
				WHERE MC.MAIL_CASE_ID = MR.MAIL_CASE_ID
				AND   MR.EMP_ID = E.EMP_ID
				AND   MC.MAIL_CASE_NAME = '�e�X�g�p���M��'
			) LOOP
				IF N_MAIL_TO_CNT = 1 THEN
					V_MAIL_TO := ADD_REC.MAIL;
				ELSE
					V_MAIL_TO := V_MAIL_TO || ',' || ADD_REC.MAIL;
				END IF;
				N_MAIL_TO_CNT := N_MAIL_TO_CNT + 1;

	--			MAIL_TO(ADD_REC.NUM) := ADD_REC.MAIL;
			END LOOP;
			V_TITLE := 'CORRECTION�f�[�^�o�^�ʒm TEST��';
		END IF;

		-- CONTENTS
		V_CONTENTS := 'CORRECTION�f�[�^�o�^�ʒm'||chr(13)||chr(10)
			|| '�ȉ��̃f�[�^���V�K�ɓo�^����܂����̂ŁA���m�F���������B'|| chr(13)||chr(10)
			|| '============================================'|| chr(13)||chr(10)
			|| V_PROJECT
			|| '============================================'|| chr(13)||chr(10) || chr(13)||chr(10)
			|| '�ȏ�';

		-- ====================================================
		-- ���[�����M
		-- ====================================================
		SENDMAIL(V_FROM_ADDRESS,V_MAIL_TO,V_TITLE,V_CONTENTS);

		UPDATE PROJECT_PA1_SENDMAIL_LIST
		   SET SENDMAIL_FLG = 0
		 WHERE SENDMAIL_FLG = 1;
		COMMIT;
	END IF;

EXCEPTION
	WHEN OTHERS THEN
		N_ERR_CODE := SQLCODE;
		V_ERR_MSG := SUBSTR(SQLERRM,1,100);
		INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
		VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.SEND_PA1_MAIL �\�����Ȃ��G���[');
		COMMIT;

END SEND_PA1_MAIL;

-- ====================================================
-- PROCEDURE INIT_CHECK
-- ���s�O�ɕK�v�ȃe�[�u���Ƀf�[�^�������������Ă��邩�`�F�b�N
--   project_list_interface.interface_status_code='ACTIVE'������ꍇ�̓G���[
--   project_list_pa2_xxx,project_list_pa7_xxx�̒l�������������Ă�����G���[
-- �萔�\����l���擾���ăO���[�o���萔���`

-- ���^�[���R�[�h�FOUT_RESUT_CODE
--   ����I��=0
--   �ُ�I��=�G���[���b�Z�[�W
-- ====================================================
PROCEDURE INIT_CHECK
(
	IN_EXEC_USER_ID	IN	NUMBER,
	OUT_RESULT_CODE	OUT	VARCHAR2
)
IS

N_CNT		NUMBER;
N_NOX_CNT	NUMBER;
-- N_PA1X_CNT	NUMBER;
N_PA2X_CNT	NUMBER;
N_PA5X_CNT	NUMBER;
N_PA7X_CNT	NUMBER;
INT_NOT_EMPTY		EXCEPTION;
NO_XXX_DATA_EXCEPTION	EXCEPTION;
PA2_GARBLE_EXCEPTION	EXCEPTION;
PA7_GARBLE_EXCEPTION	EXCEPTION;

BEGIN
	-- ====================================================
	-- ���s�O�̃`�F�b�N
	-- ====================================================
	-- �������̂��̂�����΃G���[��Ԃ�
	SELECT COUNT(*) INTO N_CNT FROM PROJECT_LIST_INTERFACE WHERE INTERFACE_STATUS_CODE = 'ACTIVE';
	IF N_CNT > 0 THEN
		RAISE INT_NOT_EMPTY;
	END IF;
	-- �g�p����e�[�u������Ȃ�G���[��Ԃ�
	SELECT COUNT(*) INTO N_NOX_CNT FROM PROJ_OPPO_NO_XXX;
	-- SELECT COUNT(*) INTO N_PA1X_CNT FROM PROJECT_LIST_PA1_XXX;
	SELECT COUNT(*) INTO N_PA2X_CNT FROM PROJECT_LIST_PA2_XXX;
	SELECT COUNT(*) INTO N_PA5X_CNT FROM PROJECT_LIST_PA5_XXX;
	SELECT COUNT(*) INTO N_PA7X_CNT FROM PROJECT_LIST_PA7_XXX;
	IF (N_NOX_CNT=0 OR N_PA2X_CNT=0 OR N_PA5X_CNT=0 OR N_PA7X_CNT=0) THEN
		RAISE NO_XXX_DATA_EXCEPTION;
	END IF;
	-- ���������f�[�^�ɑΉ�
	SELECT COUNT(*) INTO N_CNT FROM PROJECT_LIST_PA2_XXX
	WHERE PROJ_NAME LIKE '%���%';
	IF N_CNT > 0 THEN
		RAISE PA2_GARBLE_EXCEPTION;
	END IF;
	SELECT COUNT(*) INTO N_CNT FROM PROJECT_LIST_PA7_XXX
	WHERE CUSTOMER_NAME LIKE '%���%';
	IF N_CNT > 0 THEN
		RAISE PA7_GARBLE_EXCEPTION;
	END IF;

	-- ====================================================
	-- �O���[�o���萔�ɒl�������
	-- ====================================================
	-- V_CC_ORG_ID_P(�R���T���g�DID�̐ړ���)�ɒl������
	SELECT VALUE INTO V_CC_ORG_ID_P
	FROM CONST_LIST
	WHERE NAME='COST_CENTER_ORG'
	AND SYSDATE BETWEEN START_DATE AND NVL(END_DATE,SYSDATE+1)
	;
	-- V_CC_ORG_ID(�R���T��TOP�g�DID)�ɒl������
	SELECT VALUE INTO V_CC_ORG_ID
	FROM CONST_LIST
	WHERE NAME='CONSUL_ORG_ID'
	AND SYSDATE BETWEEN START_DATE AND NVL(END_DATE,SYSDATE+1)
	;
	OUT_RESULT_CODE := '0';

-- 2012/6/14 �ꎞ�Ή�
--insert into proj_oppo_no_xxx
--(creation_date,last_update_date,created_by,last_updated_by
--,last_update_login,oppo_number,gsi_proj_code)
--values(sysdate,sysdate,999999,999999,999999,'3CCT','300361265');
--commit;

EXCEPTION
	WHEN INT_NOT_EMPTY THEN
		OUT_RESULT_CODE := 'PROJECT_LIST_INTERFACE��ACTIVE�f�[�^������܂�';
	WHEN NO_XXX_DATA_EXCEPTION THEN
		OUT_RESULT_CODE := 'XXX�e�[�u���̃f�[�^����̂��̂�����܂�';
	WHEN PA2_GARBLE_EXCEPTION THEN
		OUT_RESULT_CODE := 'PROJECT_LIST_PA2_XXX�̃f�[�^�������������Ă��܂�';
	WHEN PA7_GARBLE_EXCEPTION THEN
		OUT_RESULT_CODE := 'PROJECT_LIST_PA7_XXX�̃f�[�^�������������Ă��܂�';

END INIT_CHECK;


-- ====================================================
-- EXEC_ALL
-- ====================================================

PROCEDURE EXEC_ALL
(
	IN_EXEC_USER_ID	IN	NUMBER,
	OUT_RESULT_CODE	OUT	VARCHAR2
)
IS

N_CNT1		NUMBER;
N_CNT2		NUMBER;
V_OUT_RESULT	VARCHAR2(1000);
N_ERR_CODE		ERR_TABLE.ERR_CODE%TYPE;
V_ERR_MSG		ERR_TABLE.ERR_MSG%TYPE;

INIT_CHECK_EXCEPTION	EXCEPTION;
SEED_PROJ_NO_EXCEPTION	EXCEPTION;
PA2_EXCEPTION		EXCEPTION;
--PA1_EXCEPTION		EXCEPTION;
PA5_EXCEPTION		EXCEPTION;
PA6_EXCEPTION		EXCEPTION;
PA7_EXCEPTION		EXCEPTION;
SEED_PROJECT_EXCEPTION	EXCEPTION;
PA2_GARBLE_EXCEPTION	EXCEPTION;
PA7_GARBLE_EXCEPTION	EXCEPTION;
PROJECT_STATUS_EXCEPTION	EXCEPTION;

BEGIN

	--================================
	-- �萔�l�ݒ�Ǝ��s�O�̃`�F�b�N
	INIT_CHECK(IN_EXEC_USER_ID,V_OUT_RESULT);
	OUT_RESULT_CODE := V_OUT_RESULT;
	IF OUT_RESULT_CODE != '0' THEN
		RAISE INIT_CHECK_EXCEPTION;
	END IF;

-- �C���M�����[���� �Ԉ����OPPO��PJ��R�Â��ēo�^���Ă��܂������̂��폜
DELETE FROM PROJ_OPPO_NO_XXX WHERE GSI_PROJ_CODE = '300356493' AND OPPO_NUMBER = '3-299WK0K';
DELETE FROM PROJ_OPPO_NO_XXX WHERE gsi_proj_code = '300362453' AND OPPO_NUMBER = '3-2UIU1AF';
DELETE FROM PROJ_OPPO_NO_XXX WHERE GSI_PROJ_CODE = '300401565' AND OPPO_NUMBER = 'Y8JQ';
DELETE FROM PROJ_OPPO_NO_XXX WHERE GSI_PROJ_CODE = '300406966' AND OPPO_NUMBER = '39P57';
DELETE FROM PROJ_OPPO_NO_XXX WHERE GSI_PROJ_CODE = '300421039' AND OPPO_NUMBER = '3N98S';
DELETE FROM PROJ_OPPO_NO_XXX WHERE GSI_PROJ_CODE = '300426244' AND OPPO_NUMBER = '3LYTF';
DELETE FROM PROJ_OPPO_NO_XXX WHERE GSI_PROJ_CODE = '300452035_3' AND OPPO_NUMBER = '57T3T';
DELETE FROM PROJ_OPPO_NO_XXX WHERE GSI_PROJ_CODE = '300487181' AND OPPO_NUMBER = '5C2KY';
DELETE FROM PROJ_OPPO_NO_XXX WHERE GSI_PROJ_CODE = '300487420' AND OPPO_NUMBER = '5CBYP';
DELETE FROM PROJ_OPPO_NO_XXX WHERE GSI_PROJ_CODE = '300563444' AND OPPO_NUMBER = '7243G';
DELETE FROM PROJ_OPPO_NO_XXX WHERE GSI_PROJ_CODE = '300572882' AND OPPO_NUMBER = '68DN3';

COMMIT;

	--================================
	SEED_PROJ_NO(IN_EXEC_USER_ID,V_OUT_RESULT);

	OUT_RESULT_CODE := V_OUT_RESULT;
	IF OUT_RESULT_CODE != '200' THEN
		RAISE SEED_PROJ_NO_EXCEPTION;
	END IF;
	--================================
	EXEC_PA2(IN_EXEC_USER_ID,V_OUT_RESULT);

	OUT_RESULT_CODE := V_OUT_RESULT;
	IF OUT_RESULT_CODE != '200' THEN
		RAISE PA2_EXCEPTION;
	END IF;
	--================================
	-- PA2���o�^����Ă��Ȃ��Ƒ��̃e�[�u���̔�r���ł��Ȃ�
	SEED_PROJECT(IN_EXEC_USER_ID,V_OUT_RESULT);

	OUT_RESULT_CODE := V_OUT_RESULT;
	IF OUT_RESULT_CODE != '200' THEN
		RAISE SEED_PROJECT_EXCEPTION;
	END IF;
	--================================
--	EXEC_PA1(IN_EXEC_USER_ID,V_OUT_RESULT);

--	OUT_RESULT_CODE := V_OUT_RESULT;
--	IF OUT_RESULT_CODE != '200' THEN
--		RAISE PA1_EXCEPTION;
--	END IF;
	--================================
	--================================
--	EXEC_PA1_SENDMAIL_LIST(IN_EXEC_USER_ID,V_OUT_RESULT);

--	OUT_RESULT_CODE := V_OUT_RESULT;
--	IF OUT_RESULT_CODE != '200' THEN
--		RAISE PA1_EXCEPTION;
--	END IF;
	--================================
	EXEC_PA5(IN_EXEC_USER_ID,V_OUT_RESULT);

	OUT_RESULT_CODE := V_OUT_RESULT;
	IF OUT_RESULT_CODE != '200' THEN
		RAISE PA5_EXCEPTION;
	END IF;
	--================================
	EXEC_PA6(IN_EXEC_USER_ID,V_OUT_RESULT);

	OUT_RESULT_CODE := V_OUT_RESULT;
	IF OUT_RESULT_CODE != '200' THEN
		RAISE PA6_EXCEPTION;
	END IF;
	--================================
	EXEC_PA7(IN_EXEC_USER_ID,V_OUT_RESULT);

	OUT_RESULT_CODE := V_OUT_RESULT;
	IF OUT_RESULT_CODE != '200' THEN
		RAISE PA7_EXCEPTION;
	END IF;
    --================================
    EXEC_PROJECT_STATUS(IN_EXEC_USER_ID,V_OUT_RESULT);

	OUT_RESULT_CODE := V_OUT_RESULT;
	IF OUT_RESULT_CODE != '200' THEN
		RAISE PROJECT_STATUS_EXCEPTION;
	END IF;
	--================================
	SEED_PROJECT(IN_EXEC_USER_ID,V_OUT_RESULT);

	OUT_RESULT_CODE := V_OUT_RESULT;
	IF OUT_RESULT_CODE != '200' THEN
		RAISE SEED_PROJECT_EXCEPTION;
	END IF;
    --================================

	-- BOOK_STATUS���X�V����
	UPDATE PROJECT_LIST P
	SET BOOK_STATUS = (SELECT FS.STATUS_ID
		FROM FCST_STATUS_LIST FS, STATUS_MAP_LIST SM, PROJECT_LIST_V PV
		WHERE FS.STATUS_ID = SM.STATUS_ID
		AND PV.WIN_PROB BETWEEN SM.MIN_WIN_PROB AND SM.MAX_WIN_PROB
		AND P.PROJ_CODE = PV.PROJ_CODE
	)
	WHERE P.PROJ_CODE IN (SELECT PROJ_CODE
		FROM FCST_STATUS_LIST FS, STATUS_MAP_LIST SM, PROJECT_LIST_V PV
		WHERE FS.STATUS_ID = SM.STATUS_ID
		AND PV.WIN_PROB BETWEEN SM.MIN_WIN_PROB AND MAX_WIN_PROB
		AND FS.STATUS_ID != NVL(PV.BOOK_STATUS,NULL_NUM)
		AND PV.CONTRACT_DATE >= TO_DATE('200903','YYYYMM')
		AND PV.STATUS NOT IN (V_VOID_STATUS, V_VOID_STATUS2,V_VOID_STATUS3)
	);

	-- REV_STATUS���X�V����
	-- REV_STATUS��NO_UPD_REV_STATUS��1�̂��͍̂X�V���Ȃ�
	UPDATE PROJECT_LIST P
	SET REV_STATUS = (SELECT FS.STATUS_ID
		FROM FCST_STATUS_LIST FS, STATUS_MAP_LIST SM, PROJECT_LIST_V PV
		WHERE FS.STATUS_ID = SM.STATUS_ID
		AND PV.WIN_PROB BETWEEN SM.MIN_WIN_PROB AND MAX_WIN_PROB
		AND P.PROJ_CODE = PV.PROJ_CODE
	)
	WHERE P.PROJ_CODE IN (SELECT PROJ_CODE
		FROM FCST_STATUS_LIST FS, STATUS_MAP_LIST SM, PROJECT_LIST_V PV
		WHERE FS.STATUS_ID = SM.STATUS_ID
		AND PV.WIN_PROB BETWEEN SM.MIN_WIN_PROB AND MAX_WIN_PROB
		AND FS.STATUS_ID != NVL(PV.REV_STATUS,NULL_NUM)
		AND PV.NO_UPD_REV_STATUS != 1
		AND PV.CONTRACT_DATE >= TO_DATE('200903','YYYYMM')
		AND PV.STATUS NOT IN (V_VOID_STATUS, V_VOID_STATUS2,V_VOID_STATUS3)
	);

	-- �R���T���I�������_��������̃f�[�^���C��
	UPDATE PROJECT_LIST P
	 SET (CONS_START,CONS_END) = 
	(SELECT PV.CONTRACT_DATE
	,CONTRACT_DATE + (CONS_END - CONS_START)
	 FROM PROJECT_LIST_V PV
	 WHERE PV.CONTRACT_DATE > PV.CONS_END
	   AND P.PROJ_CODE = PV.PROJ_CODE
	)
	WHERE PROJ_CODE IN
	(SELECT PROJ_CODE FROM PROJECT_LIST_V
	 WHERE CONTRACT_DATE > CONS_END);

     -- OLD_CC_PA_ID�̍X�V
     -- 4���̏ꍇ
     update project_list 
        set old_pa_cc_id = pa_cc_id 
         where LENGTH(pa_cc_id) = 4 and old_pa_cc_id != pa_cc_id;
    update project_list p
        set pa_cc_id = (select NEW_CC_ID from CC_ID_MAP m where p.pa_cc_id = m.OLD_CC_ID)
         where LENGTH(pa_cc_id) = 4;
     -- 6���̏ꍇ(FY19�ɂȂ�����폜����)
     update project_list p
        set old_pa_cc_id = (select OLD_CC_ID from CC_ID_MAP m where p.pa_cc_id = m.NEW_CC_ID)
        where LENGTH(pa_cc_id) = 6 
        and
        (p.old_pa_cc_id is null 
        or old_pa_cc_id != (select OLD_CC_ID from CC_ID_MAP m where p.pa_cc_id = m.NEW_CC_ID));


	--PROJ_STATUS���X�V���� ��ɍ폜������
update project_list set proj_status = 6 where no_upd_rev_status = 0 and book_status = 5 and proj_status != 6;
update project_list set proj_status = 4 where no_upd_rev_status = 0 and book_status = 4 and proj_status not in  (4,3);
update project_list set proj_status = 3 where no_upd_rev_status = 0 and book_status = 3 and proj_status != 3;
update project_list set proj_status = 2 where no_upd_rev_status = 0 and book_status = 2 and proj_status != 2;
update project_list set proj_status = 1 where no_upd_rev_status = 0 and book_status = 1 and proj_status not in(1,0);

update project_list set proj_status = 6 where no_upd_rev_status = 1 and rev_status = 5 and proj_status != 6;
update project_list set proj_status = 4 where no_upd_rev_status = 1 and rev_status = 4 and proj_status not in  (4,3);
update project_list set proj_status = 3 where no_upd_rev_status = 1 and rev_status = 3 and proj_status != 3;
update project_list set proj_status = 2 where no_upd_rev_status = 1 and rev_status = 2 and proj_status != 2;
update project_list set proj_status = 1 where no_upd_rev_status = 1 and rev_status = 1 and proj_status not in(1,0);

	--================================ ��������C���M�����[�Ή�
-- ��O�Ή�ADDITIONAL��CORRECTION���v�Z����Ă��܂����̂ŏC���Bcontract_date�ŕ������������̂ŁA�ʍs��-40M��o�^���Ă���B
update project_list set cont_amount = 175000000 where proj_code = 'TEMP18064';
-- ��O�Ή� ADDITIONAL��CORRECTION���v�Z����Ă��܂����̂ŏC��
update project_list set contract_date = to_date('20100528','YYYYMMDD') where gsi_proj_code = '300289876';
update project_list set cont_amount = 17000000 where gsi_proj_code = '300273533' and proj_code = 'TEMP16733';
update project_list set cont_amount = 20620000 where proj_code = 'TEMP19350';
update project_list set contract_date = to_date('20091007','YYYYMMDD') where gsi_proj_code = '300267784_KK';
update project_list set cont_amount = 36001200 where gsi_proj_code = '300309260' and proj_code = 'TEMP19485';
-- ���΂��񂩂�˗�
update project_list set proj_name = '�V��v��Փ����v���W�F�N�g�i1-6���j_�J����' where proj_code = 'TEMP21350';
update project_list set proj_name = '�V��v��Փ����v���W�F�N�g�i1-6 ���j_WebInq' where proj_code = 'TEMP21612';
update project_list set proj_name = '�V��v��Փ����v���W�F�N�g�i1-6���j_�x����' where proj_code = 'TEMP21613';

update project_list set proj_name = (
	select replace(proj_name, '- Evaluate','Evaluate')
	from project_list where gsi_proj_code = '300272618' and proj_code = 'TEMP17198'
	)
where gsi_proj_code like '%300272618%'
;

-- contract_creation_date�̑���BQ1�ɂ��ꂽ��
update project_list set contract_creation_date = to_date('20090903','YYYYMMDD') where proj_code = 'TEMP16360';
update project_list set contract_creation_date = to_date('20090903','YYYYMMDD') where proj_code = 'TEMP16025';

-- contract_creation_date�̑���BQ2�ɂ��ꂽ��
update project_list set contract_creation_date = to_date('20091203','YYYYMMDD') where proj_code = 'TEMP17237';

-- 2012/5/23 ���肳����˗� ITML�_����z�ύX
update project_list set cont_amount = 4035870 where gsi_proj_code like '300358157' and del_flg = 0;
-- 2012/6/22 �Ж��ύX�̂���PJ#�Ď擾�AFunding����蒼�����̂ōĎ�荞�݂��Ȃ��悤�ɂ���B
update project_list set cont_amount = 9500000, contract_date=to_date('20120316','YYYYMMDD'), contract_creation_date=to_date('20120322','YYYYMMDD') where gsi_proj_code = '300361207' and data_upd_type=0;
update project_list set cont_amount = 10580000, contract_date=to_date('20120312','YYYYMMDD'), contract_creation_date=to_date('20120314','YYYYMMDD') where gsi_proj_code = '300361859' and data_upd_type=0;
update project_list set cont_amount = 5200000, contract_date=to_date('20120312','YYYYMMDD'), contract_creation_date=to_date('20120314','YYYYMMDD') where gsi_proj_code = '300361862' and data_upd_type=0;
-- 2012/8/24 �_���ύX�̂���PJ#�Ď擾�AFunding����蒼�����̂ōĎ捞���Ȃ��悤�ɂ���B
update project_list set cont_amount = 6900000, contract_date=to_date('20120316','YYYYMMDD'), contract_creation_date=to_date('20120320','YYYYMMDD') where gsi_proj_code = '300366949' and data_upd_type=0;
update project_list set cont_amount = 7750000 where gsi_proj_code = '300367609' and data_upd_type=0;

-- 2012/8/16 USD��Funding����邽�ߍX�V���Ȃ�,���т��������񍐂����삳��Ȃǂ��炫����A����update���Ŏ��т���͂���悤�ύX����
update project_list set cont_amount = null where gsi_proj_code = '300367043';
update project_list set cont_amount = null where gsi_proj_code = '300364193';

-- 2013/2/25 ���X�؂�����˗�
update project_list set cont_amount = 90000000 where gsi_proj_code = '300383991';

-- 2013/4/2 ���肳����˗� RightNow��Funding���C��
update project_list set cont_amount = 396000 where proj_code = 'TEMP23803';
update project_list set cont_amount = 493496 where proj_code = 'TEMP23757';
update project_list set cont_amount = 691395 where proj_code = 'TEMP23805';
update project_list set cont_amount = 744000 where proj_code = 'TEMP23812';
update project_list set cont_amount = 2685000 where proj_code = 'TEMP23754';
update project_list set cont_amount = 3612900 where proj_code = 'TEMP23816';
update project_list set cont_amount = 4233000 where proj_code = 'TEMP23811';
update project_list set cont_amount = 4284900 where proj_code = 'TEMP23817';
update project_list set cont_amount = 4620000 where proj_code = 'TEMP23806';
update project_list set cont_amount = 5058900 where proj_code = 'TEMP23814';
update project_list set cont_amount = 5379800 where proj_code = 'TEMP23813';
update project_list set cont_amount = 6539800 where proj_code = 'TEMP23809';
update project_list set cont_amount = 7500000 where proj_code = 'TEMP23807';
update project_list set cont_amount = 8121099 where proj_code = 'TEMP23804';
update project_list set cont_amount = 9816000 where proj_code = 'TEMP23808';
update project_list set cont_amount = 11270000 where proj_code = 'TEMP23758';
update project_list set cont_amount = 15444000 where proj_code = 'TEMP23802';
update project_list set cont_amount = 22944000 where proj_code = 'TEMP23759';

-- 2013/4/10 ���X�؂�����˗�
update project_list set cont_amount = 15500000 where gsi_proj_code = '300386313';

-- 2013/4/25 ���X�؂�����˗�
update project_list set cont_amount = 23904000 where gsi_proj_code = '300371099';
update project_list set cont_amount = 5910720 where gsi_proj_code = '300378485';

-- 2013/5/10 ���肳����˗�
update project_list set gsi_proj_code = '300376705_1X_R' where proj_code = 'TEMP23965';

-- 2013/5/13 ���X�؂�����˗�
update project_list set cont_amount = 1842400 where proj_code = 'TEMP23596';
-- 2013/5/15 ���X�؂�����˗�
update project_list set book_status = 99 where proj_code = 'TEMP23762';
-- 2013/05/22 ���X�؂�����˗�
update project_list set cont_amount = 3576087, contract_date=to_date('20121128','YYYYMMDD') where proj_code = 'TEMP23188';
-- 2013/05/28 ���c������˗�
update project_list set cont_amount = '15000000' where proj_code = 'TEMP23086';
-- 2013/05/31 ���X�؂�����˗�
update project_list set cont_amount = 24992920 where proj_code = 'TEMP23130';
update project_list set cont_amount = 5900000 where proj_code = 'TEMP22434';
-- 2013/06/27 ���X�؂�����˗�
update project_list set cont_amount = '-70400',contract_date = '2012/08/30' where proj_code = 'TEMP22922';
-- 2013/07/23 ���X�؂�����˗�
update project_list set cont_amount = 25000000 where proj_code = 'TEMP23428';

-- 2013/07/30 �G���h���[�U���������蓮�ōX�V(�b��Ή�)
update project_list set eu_account_id = 2145 where oppo_number = 'YJ64';
-- 2013/08/08 �G���h���[�U���������蓮�ōX�V(�b��Ή�)
update project_list set eu_account_id = 2145 where oppo_number = '33MPB';

-- 2013/08/15 ���X�؂�����˗�
update project_list set end_user_name = '�y�V�������', eu_account_id = 1288 where proj_code = 'TEMP23757';
update project_list set end_user_name = '������ЃJ�v�R��', eu_account_id = 2114 where proj_code = 'TEMP23754';
update project_list set end_user_name = '���t�[�������', eu_account_id = 424 where proj_code = 'TEMP23759';
update project_list set end_user_name = '������ЃR�i�~�f�W�^���G���^�e�C�������g', eu_account_id = 652 where proj_code = 'TEMP23764';
update project_list set end_user_name = '�m�s�s�R�~���j�P�[�V�����Y�������', eu_account_id = 65 where proj_code = 'TEMP23767';
update project_list set end_user_name = '�O���[�������', eu_account_id = 1885 where proj_code = 'TEMP23770';
update project_list set end_user_name = '������Еx�m�f�[�^�V�X�e��', eu_account_id = 2121 where proj_code = 'TEMP23773';
update project_list set end_user_name = '�S���{��A�������', eu_account_id = 1 where proj_code = 'TEMP24011';
update project_list set end_user_name = '���t�[�������', eu_account_id = 424 where proj_code = 'TEMP24379';
update project_list set end_user_name = '�A���p�C���}�[�P�e�B���O�������', eu_account_id = 2113 where proj_code = 'TEMP24420';

-- 2013/08/29 ���肳����˗�
update project_list set cont_amount = 9075516 where proj_code = 'TEMP24679';
-- 2013/08/30 ���X�؂�����˗�
update project_list set cont_amount = 55690000 where proj_code = 'TEMP22103';
-- 2013/09/06 ���X�؂�����˗�
update project_list set cont_amount = 14400000 where proj_code = 'TEMP24460';
update project_list set cont_amount = 2346240 where proj_code = 'TEMP24632';

-- 2013/09/19 ���c������˗�
update project_list set end_user_name = '�L�b�R�[�}���������', eu_account_id = 603 where proj_code = 'TEMP23939';
-- 2013/10/22 ���肳����˗�
update project_list set cont_amount = 3800000 where proj_code = 'TEMP24867';
-- 2013/11/01 ���X�؂�����˗�
update project_list set cont_amount = 38000000 where proj_code = 'TEMP24320';
-- 2013/11/07 ���肳����˗�
update project_list set cont_amount = 792040 where proj_code = 'TEMP24783';
-- 2013/11/11 ���X�؂�����˗�
update project_list set contract_id = 1 where proj_code = 'TEMP23353';
-- 2014/01/07 ���X�؂�����˗�
update project_list set cont_amount = 21400000 where proj_code = 'TEMP24007';
update project_list set cont_amount = 23200000 where proj_code = 'TEMP23750';
update project_list set cont_amount = 2500000 where proj_code = 'TEMP24291';
-- 2014/01/14 ���肳����˗�
update project_list set cont_amount = 2654078 where proj_code = 'TEMP25042';
-- 2014/01/29 ���肳����˗�
update project_list set cont_amount = 3576087 where proj_code = 'TEMP25357';
-- 2014/02/19 ���X�؂�����˗�
update project_list set contract_date = '2014/01/30', cont_amount = 2068000 where proj_code = 'TEMP25232';
-- 2014/04/10 ���X�؂�����˗�
update project_list set cont_amount = 14000000 where proj_code = 'TEMP25598';
-- 2014/04/11 ���X�؂�����˗�
update project_list set cont_amount = 90393680 where proj_code = 'TEMP21852';
-- 2014/04/17 ���X�؂�����˗�
update project_list set cont_amount = 3800000 where proj_code = 'TEMP25467';
-- 2014/05/09 ���肳����˗�
update project_list set cont_amount = 6000000 where proj_code = 'TEMP25016';
-- 2014/06/19 ���肳����˗�
update project_list set proj_name = 'Project Reston 7-9���x��' where proj_code = 'TEMP26177';
-- 2014/08/01 ����������˗�
update project_list set proj_name = 'GSCM�����ڍs�i�����E�k�āEHQ�j ���i�x���i14/4-6�j' where proj_code = 'TEMP25463';

update project_list set book_status = 5 where proj_code = 'TEMP24652';
-- 2015/03/19
update project_list set contract_id = 3 where proj_code = 'TEMP27493';
update project_list set contract_id = 3 where proj_code = 'TEMP27494';
-- 2015/04/01 ���Ƃ�����˗�
update project_list set cont_amount = 38021120 where proj_code = 'TEMP26496';
update project_list set contract_id = 3 where proj_code = 'TEMP27594';
-- 2015/06/11 ���X�؂�����˗�
update project_list set contract_id = 3 where proj_code = 'TEMP27954';
update project_list set contract_id = 3 where proj_code = 'TEMP27955';
update project_list set contract_id = 3 where proj_code = 'TEMP27956';
update project_list set contract_id = 3 where proj_code = 'TEMP27980';
-- 2015/07/06 ���X�؂�����˗�
update project_list set contract_id = 3 where proj_code = 'TEMP28160';
update project_list set contract_id = 3 where proj_code = 'TEMP28161';

update project_list set book_status = 99, rev_status = 99 where proj_code = 'TEMP27951';

update project_list set end_user_name = '�g���^������', eu_account_id = 118 where proj_code = 'TEMP28961';
-- 2016/01/13 �R�c������˗�
update project_list set contract_id = 3, proj_name = '�yCL�zTEPSYS EBS On-site Task Support Tech�x����' where proj_code = 'TEMP29135';
update project_list set contract_id = 3 where proj_code = 'TEMP30050';
-- 2016/08/16 ���Ƃ�����˗�
update project_list set pa_cc_id = '5067',PROJ_OWNER_ID = '4586',PROJ_MGR_ID = '4755',cont_amount = 8437500 where proj_code = 'TEMP30152';
update project_list set pa_cc_id = '5028',PROJ_OWNER_ID = '1387',PROJ_MGR_ID = '3348',cont_amount = 15562500 where proj_code = 'TEMP30263';

update project_list set cont_amount = '2220000' where proj_code = 'TEMP31663';
update project_list set cont_amount = '9900000' where proj_code = 'TEMP31554';
update project_list set cont_amount = '720000' where proj_code = 'TEMP31664';

-- 2017/08/23 ���Ƃ�����˗�
update project_list set cont_amount = '41000000' where proj_code = 'TEMP31676';
update project_list set cont_amount = '13000000' where proj_code = 'TEMP33162';

-- 2017/11/02 ���X�؂�����˗� Migration�Ή�
update project_list set end_user_name = '������Ѓ��N�T', eu_account_id = 3394 where proj_code = 'TEMP33970';
update project_list set end_user_name = '�V���t�H�j�[�}�[�P�e�B���O�������', eu_account_id = 3401 where proj_code = 'TEMP33977';
update project_list set end_user_name = '������Ѓ��N�T', eu_account_id = 3394 where proj_code = 'TEMP33978';
update project_list set end_user_name = '������Ѓi�m�E���j�o�[�X', eu_account_id = 3403 where proj_code = 'TEMP33983';
update project_list set end_user_name = '���{�I���N���������', eu_account_id = 337 where proj_code = 'TEMP34057';
update project_list set end_user_name = '������ЃN���X�E�R�~���j�P�[�V����', eu_account_id = 3400 where proj_code = 'TEMP33985';

-- 2017/01/24
update project_list set CLIENT_NAME = '���ŃC���t�H���[�V�����V�X�e���Y�������' where proj_code = 'TEMP33254';
update project_list set CLIENT_NAME = '���ŃC���t�H���[�V�����V�X�e���Y�������' where proj_code = 'TEMP34066';

-- 2018/03/06 ���Ƃ���˗�
update project_list set cont_amount = '33000000' where proj_code = 'TEMP32933';

--����Paas,Iaas�@PA�R�X�g�Z���^�C���˗�
update project_list p set p.pa_cc_id = '501636' , p.old_pa_cc_id = 'D1M1'
where proj_code in ('TEMP36199','TEMP36200','TEMP36203','TEMP36205',
'TEMP36559','TEMP36560','TEMP36567','TEMP36572','TEMP36576','TEMP37107',
'TEMP37128','TEMP37959','TEMP37958','TEMP38067','TEMP38415','TEMP37764',
'TEMP37765','TEMP38725','TEMP39224','TEMP39495','TEMP39896','TEMP39898',
'TEMP40651');

update project_list p set p.pa_cc_id = '506205' , p.old_pa_cc_id = '5059'
where proj_code in ('TEMP36564','TEMP36565','TEMP38726');

update project_list p set p.pa_cc_id = '506215' , p.old_pa_cc_id = '5072'
where proj_code in ('TEMP36566');

update project_list p set p.pa_cc_id = '506325' , p.old_pa_cc_id = '5069'
where proj_code in ('TEMP38721','TEMP38723','TEMP40652');

update project_list p set p.pa_cc_id = '506295' , p.old_pa_cc_id = '5061'
where proj_code in ('TEMP37132','TEMP36737','TEMP37796','TEMP36737','TEMP39225'
,'TEMP39899','TEMP40649','TEMP40650');

update project_list p set p.pa_cc_id = '506305' , p.old_pa_cc_id = '5067'
where proj_code in ('TEMP36569','TEMP36573','TEMP37131','TEMP36737');

update project_list p set p.pa_cc_id = '506315' , p.old_pa_cc_id = '5068'
where proj_code in ('TEMP36563','TEMP37129','TEMP37456','TEMP38414','TEMP38416',
'TEMP38417','TEMP38724','TEMP40229','TEMP40230');

update project_list p set p.pa_cc_id = '506355' , p.old_pa_cc_id = 'AE24'
where proj_code in ('TEMP37130');

update project_list p set p.pa_cc_id = '506225' , p.old_pa_cc_id = '5074'
where proj_code in ('TEMP37763','TEMP39895','TEMP39897');

update project_list p set p.pa_cc_id = '508835' , p.old_pa_cc_id = '1YK1'
where proj_code in ('TEMP36202','TEMP36204','TEMP36201','TEMP36558','TEMP36561',
'TEMP36562','TEMP36568','TEMP36570','TEMP36571','TEMP36574','TEMP36575',
'TEMP36577','TEMP37761','TEMP37762','TEMP38722','TEMP39503','TEMP39900'
,'TEMP40648');

update project_list p set p.pa_cc_id = '506335' , p.old_pa_cc_id = '5070'
where proj_code in ('TEMP39496','TEMP39502','TEMP39504');

update project_list p set p.pa_cc_id = '506245' , p.old_pa_cc_id = '5000'
where proj_code in ('TEMP39494');

--�ЂȂ݂���˗�
update project_list p set p.PROJ_MGR_ID = 1009168 where proj_code = 'TEMP34200' ;

--��������˗�
update project_list p set p.CLIENT_NAME = null where gsi_proj_code like '400009039%';

--�R�c����_�~�[�v���W�F�N�g
update project_list p set p.proj_name = '�y�R�X�g�Ǘ��pDummy_IP�zP4SConstruction' 
,p.contract_id = 2 where proj_code = 'TEMP36032';

update project_list set sec_pm = 702494 where proj_code = 'TEMP30148';

--ITMTT�_��`�ԍX�V
UPDATE PROJECT_LIST set CONTRACT_ID = 14 where PROJ_CODE in 
('TEMP38246','TEMP38457','TEMP38291','TEMP38341','TEMP35996',
'TEMP35997','TEMP36198','TEMP37832','TEMP36197','TEMP36196');

-- 2019/07/10 ���Ƃ��񂩂烁�[��Maxymiser�̌_��`��ITMTT�_��`�ԍX�V�Ή�
update project_list set contract_id = '14'
WHERE proj_code IN (
        'TEMP38246',
        'TEMP38291',
        'TEMP38341',
        'TEMP37832'
    );
    
commit;
    --================================
    --          ���[�����t
    --================================
    SEND_INSERTED_PROJECT_MAIL;
    --================================
    --        SEND_PA1_MAIL;
    --================================
    UPD_END_USER_ACCOUNT_ID(V_OUT_RESULT);
    ACCOUNT_NAYOSE_MAIL(V_OUT_RESULT);
    --================================


EXCEPTION
	WHEN INIT_CHECK_EXCEPTION THEN
		OUT_RESULT_CODE := 'INIT_CHECK�G���[�F'||OUT_RESULT_CODE;
	WHEN PA2_EXCEPTION THEN
		OUT_RESULT_CODE := 'PA2�G���[�F'||OUT_RESULT_CODE;
--	WHEN PA1_EXCEPTION THEN
--		OUT_RESULT_CODE := 'PA1�G���[�F'||OUT_RESULT_CODE;
	WHEN PA5_EXCEPTION THEN
		OUT_RESULT_CODE := 'PA5�G���[�F'||OUT_RESULT_CODE;
	WHEN PA6_EXCEPTION THEN
		OUT_RESULT_CODE := 'PA6�G���[�F'||OUT_RESULT_CODE;
	WHEN PA7_EXCEPTION THEN
		OUT_RESULT_CODE := 'PA7�G���[�F'||OUT_RESULT_CODE;
    WHEN PROJECT_STATUS_EXCEPTION THEN
        OUT_RESULT_CODE := 'PROJECT_STATUS�G���[�F'||OUT_RESULT_CODE;
	WHEN OTHERS THEN
		N_ERR_CODE := SQLCODE;
		V_ERR_MSG := SUBSTR(SQLERRM,1,100);
		INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
		VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.EXEC_ALL �\�����Ȃ��G���[');
		COMMIT;
		OUT_RESULT_CODE := '500 �\�����Ȃ��G���[���������܂��� EXEC_ALL';

END EXEC_ALL;


-- ====================================================
-- PROCEDURE UPD_END_USER_ACCOUNT_ID
-- PROJECT_LIST.EU_ACCOUNT_ID���APROJECT_LIST.END_USER_NAME�ɉ��������̏o�Ȃ��ꍇ�͍X�V
--
-- ���^�[���R�[�h�FOUT_RESUT_CODE
--   ����I��=200
--   �ُ�I��=�G���[�R�[�h
-- ��O�����F
--   ERR_TABLE�Ƀ��O���o�͂��ďI��
-- ====================================================
PROCEDURE UPD_END_USER_ACCOUNT_ID
(
	OUT_RESULT_CODE	OUT	VARCHAR2
)
IS

N_ERR_CODE		ERR_TABLE.ERR_CODE%TYPE;
V_ERR_MSG		ERR_TABLE.ERR_MSG%TYPE;

-- �X�V�f�[�^�̃J�[�\��
CURSOR upd_line IS
	SELECT P.PROJ_CODE, P.EU_ACCOUNT_ID MAE_EU_ACCOUNT_ID, ACNT_M.ACCOUNT_ID ATO_EU_ACCOUNT_ID
	FROM  PROJECT_LIST P
		,(SELECT ACNT.ACCOUNT_NAME, AL.ACCOUNT_ID
		FROM ACCOUNT ACNT, ACCOUNT_LIST AL
		WHERE ACNT.ACCOUNT_ID = AL.ACCOUNT_ID
		) ACNT_M
	WHERE TRIM_ACCOUNT_NAME(P.END_USER_NAME) = TRIM_ACCOUNT_NAME(ACNT_M.ACCOUNT_NAME(+))
	AND   NVL(P.EU_ACCOUNT_ID,-1) != NVL(ACNT_M.ACCOUNT_ID,-1)
	;

upd_rec  upd_line%ROWTYPE;

BEGIN
	-- ================================================
	-- EU_ACCOUNT_ID�ɕύX���K�v��������ύX����
	-- ================================================
	OPEN upd_line;
	LOOP
		FETCH upd_line INTO upd_rec;
		EXIT WHEN upd_line%NOTFOUND;

		BEGIN
			UPDATE PROJECT_LIST
			SET EU_ACCOUNT_ID = DECODE(upd_rec.ATO_EU_ACCOUNT_ID,-1,NULL,upd_rec.ATO_EU_ACCOUNT_ID)
			WHERE PROJ_CODE = upd_rec.PROJ_CODE
			;

			COMMIT;

		EXCEPTION
		WHEN OTHERS THEN
			N_ERR_CODE := SQLCODE;
			V_ERR_MSG := SUBSTR(SQLERRM,1,100);
			INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
			VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.UPD_END_USER_ACCOUNT_ID �\�����Ȃ��G���[ '||upd_rec.PROJ_CODE);
			COMMIT;

		END;
	END LOOP;
	CLOSE upd_line;
	OUT_RESULT_CODE := '200';

EXCEPTION
	WHEN OTHERS THEN
			N_ERR_CODE := SQLCODE;
			V_ERR_MSG := SUBSTR(SQLERRM,1,100);
			INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
			VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'RMS.PROJECT_LIST_SV.UPD_END_USER_ACCOUNT_ID �\�����Ȃ��G���[');
			COMMIT;
			OUT_RESULT_CODE := '500 �\�����Ȃ��G���[���������܂��� UPD_END_USER_ACCOUNT_ID';

END UPD_END_USER_ACCOUNT_ID;


-- ====================================================
-- PROCEDURE ACCOUNT_NAYOSE_MAIL
-- PROJECT_LIST_V�ɂ���f�[�^�̂����A20090601�ȍ~�̃f�[�^�ŁA
--   ACCOUNT�e�[�u���Ŗ��񂹂���Ă��Ȃ��f�[�^�𒊏o���A���[���ő��t����
--   ERR_TABLE�Ɏ��s���O�o��
--   ���[����M��A�蓮��ACCOUNT,ACCOUNT_LIST�Ƀf�[�^���쐬�A�������͎捞���f�[�^�̕ύX���s���ׂ��B
--
-- ���^�[���R�[�h�FOUT_RESUT_CODE
--   ����I��=200
--   �ُ�I��=�G���[�R�[�h
-- ��O�����F
--   ERR_TABLE�Ƀ��O���o�͂��ďI��
-- ====================================================
PROCEDURE ACCOUNT_NAYOSE_MAIL
(
	OUT_RESULT_CODE	OUT	VARCHAR2
)
IS


N_ERR_CODE		ERR_TABLE.ERR_CODE%TYPE;
V_ERR_MSG		ERR_TABLE.ERR_MSG%TYPE;
V_ACCOUNT		VARCHAR2(4000);
V_MAIL_FROM		EMP_LIST.MAIL_ADDRESS%TYPE;
V_MAIL_TO	VARCHAR2(4000);
V_MAIL_DOMAIN	VARCHAR2(100) := COMMON_PACK.GETCONST('MAIL_DOMAIN');
V_CONTENT		VARCHAR2(4000);
V_TITLE			VARCHAR2(100);
N_IS_HONBAN		NUMBER;
N_MAIL_TO_CNT		NUMBER;

--=================================================
-- PROJECT_LIST_V �\���疼�񂹂���Ă��Ȃ����R�[�h���擾����J�[�\��
--=================================================
CURSOR account_line IS
	SELECT ACCOUNT_NAME, TRIM_ACCOUNT_NAME,proj_code
	FROM
	(
		-- END_USER_NAME�Ŗ��񂹂���Ă��Ȃ��f�[�^
		SELECT distinct END_USER_NAME ACCOUNT_NAME, TRIM_ACCOUNT_NAME(END_USER_NAME) TRIM_ACCOUNT_NAME,proj_code
		FROM PROJECT_LIST_V P
		WHERE  NOT EXISTS(SELECT 1 FROM ACCOUNT ACNT
		    WHERE TRIM_ACCOUNT_NAME(P.END_USER_NAME) = ACNT.ACCOUNT_NAME
		    )
		AND  GREATEST(P.CONS_START,NVL(P.CONS_END,P.CONS_START)) >= TO_DATE('20150601','YYYYMMDD')
		AND  P.END_USER_NAME IS NOT NULL
        --���O
		and  p.proj_code not in 
        ('TEMP28321','TEMP28866','TEMP28620','TEMP28867','TEMP28320','TEMP37550'
        ,'TEMP30801','TEMP30791','TEMP31022','TEMP31024','TEMP31025','TEMP30060'
        ,'TEMP37787','TEMP28937','TEMP25482','TEMP27751','TEMP31300','TEMP30063'
        ,'TEMP32806','TEMP29917','TEMP40876')
	)
	ORDER BY TRIM_ACCOUNT_NAME
;

account_rec  account_line%ROWTYPE;

BEGIN
	OPEN account_line;
	LOOP
		FETCH account_line INTO account_rec;
		EXIT WHEN account_line%NOTFOUND;

			V_ACCOUNT := V_ACCOUNT || account_rec.ACCOUNT_NAME || chr(13)||chr(10) || account_rec.TRIM_ACCOUNT_NAME || chr(13)||chr(10)||'--' || chr(13)||chr(10) ;

	END LOOP;
	CLOSE account_line;

	IF V_ACCOUNT IS NOT NULL THEN

		-- ====================================================
		-- ���[���ݒ�
		-- ====================================================
		--�{�Ԃƃe�X�g�Ń��[�����M�����ς�����悤�ɂ��Ă���
		SELECT COUNT(*) INTO N_IS_HONBAN FROM CONST_LIST WHERE NAME = '��' AND VALUE = '�{��';

		-- FROM
		SELECT (E.MAIL_ADDRESS||'@'||V_MAIL_DOMAIN)
		INTO V_MAIL_FROM
		FROM MAIL_CASE_LIST MC, MAIL_RECIPIENT_LIST MR, EMP_LIST E
		WHERE MC.MAIL_CASE_ID = MR.MAIL_CASE_ID
		AND   MR.EMP_ID = E.EMP_ID
		AND   MC.MAIL_CASE_NAME = '�V�X�e���Ǘ���'
		;

		-- TO
		N_MAIL_TO_CNT :=1;
		IF N_IS_HONBAN = 1 THEN
			--�{��
			FOR ADD_REC IN
				(SELECT ROWNUM NUM,(E.MAIL_ADDRESS||'@'||V_MAIL_DOMAIN) MAIL
				FROM EMP_LIST E,MAIL_RECIPIENT_LIST R, MAIL_CASE_LIST C
				WHERE C.MAIL_CASE_NAME = 'MP���񂹃`�F�b�N'
				AND   R.MAIL_CASE_ID = C.MAIL_CASE_ID
				AND   E.EMP_ID = R.EMP_ID
				AND   E.DEL_FLG = 0
			) LOOP
				IF N_MAIL_TO_CNT = 1 THEN
					V_MAIL_TO := ADD_REC.MAIL;
				ELSE
					V_MAIL_TO := V_MAIL_TO || ',' || ADD_REC.MAIL;
				END IF;
				N_MAIL_TO_CNT := N_MAIL_TO_CNT + 1;
			END LOOP;
			V_TITLE := '[MP]�ڋq�����񂹃`�F�b�N';
		ELSE
			--�e�X�g
			FOR ADD_REC IN
				(SELECT ROWNUM NUM,(E.MAIL_ADDRESS||'@'||V_MAIL_DOMAIN) MAIL
				FROM MAIL_CASE_LIST MC, MAIL_RECIPIENT_LIST MR, EMP_LIST E
				WHERE MC.MAIL_CASE_ID = MR.MAIL_CASE_ID
				AND   MR.EMP_ID = E.EMP_ID
				AND   MC.MAIL_CASE_NAME = '�e�X�g�p���M��'
			) LOOP
				IF N_MAIL_TO_CNT = 1 THEN
					V_MAIL_TO := ADD_REC.MAIL;
				ELSE
					V_MAIL_TO := V_MAIL_TO || ',' || ADD_REC.MAIL;
				END IF;
				N_MAIL_TO_CNT := N_MAIL_TO_CNT + 1;
				V_TITLE := '[MP]�ڋq�����񂹃`�F�b�N TEST';
			END LOOP;
		END IF;

		V_CONTENT := chr(13)||chr(10)||chr(13)||chr(10)||'���L�̌ڋq���͖��񂹂���Ă��܂���B' || chr(13)||chr(10)
			|| 'ACCOUNT, ACCOUNT_LIST�\�ɓo�^���邩�A���f�[�^���C�����Ă��������B' || chr(13)||chr(10)
			|| '============================================'|| chr(13)||chr(10)
			|| '�ڋq��' || chr(13)||chr(10)
			||'ACCOUNT�\�ɓ���镶����'|| chr(13)||chr(10)
			|| '--------------------'|| chr(13)||chr(10)
			|| V_ACCOUNT
			|| '============================================' || chr(13)||chr(10) || chr(13)||chr(10)
			|| '�ȏ�'
		;

		-- ====================================================
		-- ���[�����M
		-- ====================================================
		SENDMAIL(V_MAIL_FROM,V_MAIL_TO,V_TITLE,V_CONTENT);
		OUT_RESULT_CODE := '200';

		-- ����I�����O
		INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
		VALUES(SYSDATE,NULL,'�ڋq�����񂹃`�F�b�N���[���𑗐M���܂���','PROJECT_LIST_SV.ACCOUNT_NAYOSE_MAIL')
		;
	ELSE
		-- ����I�����O
		INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
		VALUES(SYSDATE,NULL,'�ڋq�����񂹃`�F�b�N�Ώۂ�0���ł���','PROJECT_LIST_SV.ACCOUNT_NAYOSE_MAIL')
		;

	END IF;

	OUT_RESULT_CODE := '200';

	COMMIT;

EXCEPTION
	WHEN OTHERS THEN
		N_ERR_CODE := SQLCODE;
		V_ERR_MSG := SUBSTR(SQLERRM,1,100);
		OUT_RESULT_CODE := '300 '||N_ERR_CODE || ' ' || V_ERR_MSG;
		INSERT INTO ERR_TABLE(ERR_DATE,ERR_CODE,ERR_MSG,ERR_DETAIL)
		VALUES(SYSDATE,N_ERR_CODE,V_ERR_MSG,'PROJECT_LIST_SV.ACCOUNT_NAYOSE_MAIL');

		COMMIT;

END ACCOUNT_NAYOSE_MAIL;


END;

/
