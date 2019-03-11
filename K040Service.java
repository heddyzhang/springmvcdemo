package jp.co.toshiba.traces.service.k;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.co.toshiba.traces.entity.k.K040GaibuSenryoListItem;
import jp.co.toshiba.traces.entity.k.K040HiSenryoListItem;
import jp.co.toshiba.traces.entity.k.K040KenshinListItem;
import jp.co.toshiba.traces.entity.k.K040KyoikuListItem;
import jp.co.toshiba.traces.entity.k.K040NaibuSenryoListItem;
import jp.co.toshiba.traces.entity.k.K040NyutaiListItem;
import jp.co.toshiba.traces.entity.k.User;
import jp.co.toshiba.traces.form.k.K040Form;
import jp.co.toshiba.traces.libs.LibEnv;
import jp.co.toshiba.traces.libs.LibStringUtil;
import jp.co.toshiba.traces.libs.LibXSSFWorkbook;
import jp.co.toshiba.traces.service.common.CommonService;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ResponseUtil;
/**
 * ���샍�O�Ɖ���Service
 * @author NPC
 * @version 1.0
 */
public class K040Service extends CommonService {

	/** �e���v���[�g�� */
	private static final String SHEET_NAME_TEMP = "K040.xlsx";
	/** �V�[�g���i���N�f�f�j*/
	private static final String SHEET_NAME_KENSHIN = "���N�f�f";
	/** ���y�[�W�\���f�[�^���i���N�f�f�j*/
	private static final int PAGE_SIZE_KENSHIN = 6;
	/** ���y�[�W�\���s���i���N�f�f�j*/
	private static final int PAGE_ROWS_KENSHIN = 42;
	/** �o�͊J�n�s�i���N�f�f�j*/
	private static final int 	START_ROW_IDX_KENSHIN = 4;
	/** �o�͊J�n��i���N�f�f�j*/
	private static final int 	START_COL_IDX_KENSHIN = 11;
	/** �J�����̃}�[�W�����i���N�f�f�j*/
	private static final int 	COL_MERGE_LEN_KENSHIN = 8;

	/** �V�[�g���i����j*/
	private static final String SHEET_NAME_KYOIKU = "����";
	/** �o�͊J�n�s�i����j*/
	private static final int 	START_ROW_IDX_KYOIKU = 5;
	/** �o�͊J�n��i����j*/
	private static final int 	START_COL_IDX_KYOIKU = 0;

	/** �V�[�g���i�����E�ޏ��j*/
	private static final String SHEET_NAME_NYUTAI = "�����E�ޏ�";
	/** �o�͊J�n�s�i�����E�ޏ��j*/
	private static final int 	START_ROW_IDX_NYUTAI = 5;
	/** �o�͊J�n��i�����E�ޏ��j*/
	private static final int 	START_COL_IDX_NYUTAI = 0;

	/** �V�[�g���i�����ʁj*/
	private static final String SHEET_NAME_HI_SENRYO = "������";
	/** �f�[�^�J�n�s�i�����ʁj*/
	private static final int 	START_ROW_IDX_HI_SENRYO = 4;
	/** �f�[�^�J�n��i�����ʁj*/
	private static final int 	START_COL_IDX_HI_SENRYO = 0;
	/** ���y�[�W�\���f�[�^���i�����ʁj*/
	private static final int PAGE_SIZE_HI_SENRYO = 27;

	/** �V�[�g���i�O�����ʁj*/
	private static final String SHEET_NAME_GAIBU_SENRYO = "�O������";
	/** �f�[�^�J�n�s�i�O�����ʁj*/
	private static final int 	START_ROW_IDX_GAIBU_SENRYO = 3;
	/** �f�[�^�J�n��i�O�����ʁj*/
	private static final int 	START_COL_IDX_GAIBU_SENRYO = 0;
	/** ���y�[�W�\���f�[�^���i�O�����ʁj*/
	private static final int PAGE_SIZE_GAIBU_SENRYO = 5;

	/** �V�[�g���i�������ʁj*/
	private static final String SHEET_NAME_NAIBU_SENRYO = "��������";
	/** �f�[�^�J�n�s�i�������ʁj*/
	private static final int 	START_ROW_IDX_NAIBU_SENRYO = 3;
	/** �f�[�^�J�n��i�������ʁj*/
	private static final int 	START_COL_IDX_NAIBU_SENRYO = 0;
	/** ���y�[�W�\���f�[�^���i�������ʁj*/
	private static final int PAGE_SIZE_NAIBU_SENRYO = 5;

	/** �`�F�b�N�{�b�N�X�̒萔�l�i���N�f�f�j*/
	private static final String CHK_VALUE_KENSIN = "1";
	/** �`�F�b�N�{�b�N�X�̒萔�l�i����j*/
	private static final String CHK_VALUE_KYOIKU = "2";
	/** �`�F�b�N�{�b�N�X�̒萔�l�i�����ޏ� �j */
	private static final String CHK_VALUE_NYUTAI = "3";
	/** �`�F�b�N�{�b�N�X�̒萔�l�i������ �j */
	private static final String CHK_VALUE_HI_SENRYO = "4";
	/** �`�F�b�N�{�b�N�X�̒萔�l�i�O������ �j */
	private static final String CHK_VALUE_GAIBU_SENRYO = "5";
	/** �`�F�b�N�{�b�N�X�̒萔�l�i�������� �j */
	private static final String CHK_VALUE_NAIBU_SENRYO = "6";

	/**
	 * �����\��
	 * @throws Exception
	 *
	 */
    public void init(K040Form k040Form) throws Exception {

		// S2Container������
		SingletonS2ContainerFactory.init();

		/**********************************
		 * ��ʏ�����
		 **********************************/
		// �f�[�^���͎҂̏�����
		List<User> userList = new ArrayList<User>();
		// �ŏ��̓��͗��ɂs�q�`�b�d�r���O�C���҂�ݒ�B
		userList.add(new User(k040Form.cmnLoginID));
		userList.add(new User());
		userList.add(new User());
		userList.add(new User());
		userList.add(new User());
		userList.add(new User());
		userList.add(new User());
		userList.add(new User());
		userList.add(new User());
		userList.add(new User());

		k040Form.userList = userList;

		// �o�͍��ڂ̏����l
		List<String> outputList = new ArrayList<String>();
		// ���N�f�f
		outputList.add(CHK_VALUE_KENSIN);
		// ����
		outputList.add(CHK_VALUE_KYOIKU);
		// �����ޏ�
		outputList.add(CHK_VALUE_NYUTAI);
		// ������
		outputList.add(CHK_VALUE_HI_SENRYO);
		// �O������
		outputList.add(CHK_VALUE_GAIBU_SENRYO);
		// ��������
		outputList.add(CHK_VALUE_NAIBU_SENRYO);

		k040Form.outputItems = (String[]) outputList.toArray(new String[0]);
	}

    /**
     * Excel�o��
     * @param form
     * @param fileName
     * @throws Exception
     */
	public void outputExcel(K040Form form, String fileName) throws Exception {

		// �����������쐬
		Map<String, Object> param = new HashMap<String, Object>();
		List<String> userIdList = new ArrayList<String>();

		// ���͓��iFROM�j
		param.put("dateFrom", form.dateFrom);
		// ���͓��i�s�n�j
		param.put("dateTo", form.dateTo);

		// ���͎�ID��List���쐬
		for (User user: form.userList) {
			// ���͎�ID����͂����ꍇ
			if (!StringUtils.isEmpty(user.getUserId())) {
				userIdList.add(user.getUserId());
			}

		}
		// ���͎�ID
		param.put("userIds", userIdList);

    	try {

			LibEnv env = new LibEnv();
			// Excel�e���v���[�g�p�X�̎擾
			String strExcelDirPath = env.getExcelPath();
			// EXCEL�e���v���[�g�t�@�C���ǂݍ���
			LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();
			XSSFWorkbook workbook = libWorkbook.openXSSFWorkbook(strExcelDirPath + "/"+ SHEET_NAME_TEMP);

			// poi-ooxml-3.10-FINAL.jar�̃o�b�O start�H�H�H
			// download���ꂽ�t�@�C�����J���ƁA���܂ɁA�폜���ꂽ���R�[�h: /xl/workbook.xml �p�[�c���̃r���[ (�u�b�N)�v�̃G���[���b�Z�[�W���o����,
			// �Ƃ肠�����A���L�ݒ�őΉ�
			workbook.setActiveSheet(0);
			// poi-ooxml-3.10-FINAL.jar�̃o�b�O end�H�H�H

			// �o�̓��X�g
			List<String> outputlist = Arrays.asList(form.outputItems);

			// ���N�f�f���o�͂����ꍇ
			if (outputlist.contains(CHK_VALUE_KENSIN)) {
				// ���N�f�f�̃f�[�^���X�g�擾
				List<K040KenshinListItem>  k040KenshinList = jdbcManager.selectBySqlFile(K040KenshinListItem.class,
						"data/k040Kenshin.sql", param).getResultList();

				// ���N�f�f�V�[�g�̓��e���o��
				outputKenshin(workbook.getSheet(SHEET_NAME_KENSHIN), k040KenshinList, form);
			} else {
            	// �Y���V�[�g���\��
				libWorkbook.deleteSheetByName(workbook, SHEET_NAME_KENSHIN);
			}

			// ������o�͂����ꍇ
			if (outputlist.contains(CHK_VALUE_KYOIKU)) {
				// ����̃f�[�^���X�g�擾
				List<K040KyoikuListItem>  k040KyoikuList = jdbcManager.selectBySqlFile(K040KyoikuListItem.class,
						"data/k040Kyoiku.sql", param).getResultList();

				// ����V�[�g�̓��e���o��
				outputKyoiku(workbook.getSheet(SHEET_NAME_KYOIKU), k040KyoikuList, form);
			} else {
            	// �Y���V�[�g���\��
				libWorkbook.deleteSheetByName(workbook, SHEET_NAME_KYOIKU);
			}

			// �����E�ޏ����o�͂����ꍇ
			if (outputlist.contains(CHK_VALUE_NYUTAI)) {
				// �����E�ޏ��̃f�[�^���X�g�擾
				List<K040NyutaiListItem>  k040NyutaiList = jdbcManager.selectBySqlFile(K040NyutaiListItem.class,
						"data/k040Nyutai.sql", param).getResultList();

				// �����E�ޏ��V�[�g�̓��e���o��
				outputNyutai(workbook.getSheet(SHEET_NAME_NYUTAI), k040NyutaiList, form);
			} else {
            	// �Y���V�[�g���\��
				libWorkbook.deleteSheetByName(workbook, SHEET_NAME_NYUTAI);
			}

			// �����ʂ��o�͂����ꍇ
			if (outputlist.contains(CHK_VALUE_HI_SENRYO)) {
				// �����ʂ̃f�[�^���X�g�擾
				List<K040HiSenryoListItem>  k040HiSenryoList = jdbcManager.selectBySqlFile(K040HiSenryoListItem.class,
						"data/k040HiSenryo.sql", param).getResultList();

				// �����ʃV�[�g�̓��e���o��
				outputHiSenryo(workbook.getSheet(SHEET_NAME_HI_SENRYO), k040HiSenryoList, form);
			} else {
            	// �Y���V�[�g���\��
				libWorkbook.deleteSheetByName(workbook, SHEET_NAME_HI_SENRYO);
			}

			// �O�����ʂ��o�͂����ꍇ
			if (outputlist.contains(CHK_VALUE_GAIBU_SENRYO)) {
				// �O�����ʂ̃f�[�^���X�g�擾
				List<K040GaibuSenryoListItem>  k040GaibuSenryoList = jdbcManager.selectBySqlFile(
						K040GaibuSenryoListItem.class, "data/k040GaibuSenryo.sql", param).getResultList();

				// �O�����ʃV�[�g�̓��e���o��
				outputGaibuSenryo(workbook.getSheet(SHEET_NAME_GAIBU_SENRYO), k040GaibuSenryoList, form);
			} else {
            	// �Y���V�[�g���\��
				libWorkbook.deleteSheetByName(workbook, SHEET_NAME_GAIBU_SENRYO);
			}

			// �������ʂ��o�͂����ꍇ
			if (outputlist.contains(CHK_VALUE_NAIBU_SENRYO)) {
				// �������ʂ̃f�[�^���X�g�擾
				List<K040NaibuSenryoListItem>  k040NaibuSenryoList = jdbcManager.selectBySqlFile(
						K040NaibuSenryoListItem.class, "data/k040NaibuSenryo.sql", param).getResultList();

				// �������ʃV�[�g�̓��e���o��
				outputNaibuSenryo(workbook.getSheet(SHEET_NAME_NAIBU_SENRYO), k040NaibuSenryoList, form);
			} else {
            	// �Y���V�[�g���\��
				libWorkbook.deleteSheetByName(workbook, SHEET_NAME_NAIBU_SENRYO);
			}

			// Excel�t�@�C�������X�|���X�ɏo��
			libWorkbook.outputExcel(ResponseUtil.getResponse(), workbook, fileName);

		} catch (Exception e) {
			throw e;
		}

    }

	/**
	 * �f�[�^���͎҂����݂��邩���`�F�b�N
	 * @param form
	 * @return
	 */
	public boolean checkRiyoshaID(K040Form form, List<String> errorList) {

		Map<String, Object> param = new HashMap<String, Object>();
		String userId = "";

		// ���͎�ID��List���쐬
		for (User user: form.userList) {

			userId = user.getUserId();

			// ���͎�ID����͂����ꍇ
			if (!StringUtils.isEmpty(userId)) {

				// ���͎�ID
				param.put("userId", userId);
				// ���p�҂h�c�e�[�u��������
		        long count = jdbcManager.selectBySqlFile(Long.class, "data/k040CheckRiyoshaID.sql", param)
		        		.getSingleResult();

		        // �f�[�^�����݂��Ȃ��ꍇ
		        if (count == 0) {
		        	errorList.add(userId);
		        }
			}

		}

		// ���݂��Ȃ����͎�ID���������ꍇ
		if (errorList.size() > 0 ) {
			return false;
		}

        return true;
	}

	/**
	 * ���N�f�f���o��
	 * @param sheet
	 * @param k040KenshinList
	 * @param form
	 * @throws Exception
	 */
	private void outputKenshin(XSSFSheet sheet, List<K040KenshinListItem> k040KenshinList, K040Form form)
			throws Exception {

		// �ϐ��̏�����
		int pageSum= 0;
		int pPositioin= 0;
		int nowPage = 0;
		int rowIdx = 0;
		int colIdx = 0;
		LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();

        // ���y�[�W�����v�Z
		pageSum = (k040KenshinList.size() + PAGE_SIZE_KENSHIN - 1) / PAGE_SIZE_KENSHIN;
		// �w�b�_��ݒ�
		setHeader(sheet, form);

		// �y�[�W�����J��Ԃ�
		for (int i = 2; i <= pageSum; i++) {

			// ���y�[�W��̊J�n�s�ʒu���v�Z
			pPositioin = (i - 1) * PAGE_ROWS_KENSHIN;
			// ���ڃ^�C�g�����ƃR�s�[
			copyRows(0, PAGE_ROWS_KENSHIN - 1, pPositioin, sheet);
			// �w��s�ʒu�ȍ~���y�[�W�����ݒ�
			sheet.setRowBreak(pPositioin - 1);
		}

		// ���݂̃y�[�W
		nowPage = 1;
		// �o�͊J�n�s
		rowIdx = START_ROW_IDX_KENSHIN;
		// �o�͊J�n��
		colIdx = START_COL_IDX_KENSHIN;

		// ���N�f�f�f�[�^�̏�񕪂��J��Ԃ�
		for (K040KenshinListItem k040Kenshin : k040KenshinList) {

			// ���y�[�W�̏ꍇ
			if (colIdx == START_COL_IDX_KENSHIN + PAGE_SIZE_KENSHIN * COL_MERGE_LEN_KENSHIN) {
				// ���݂̃y�[�W��
				nowPage++;
				// �o�͊J�n�s���v�Z
				rowIdx = START_ROW_IDX_KENSHIN + PAGE_ROWS_KENSHIN * (nowPage - 1);
				// �o�͊J�n���������
				colIdx = START_COL_IDX_KENSHIN;
			}

			// �Ǘ��ԍ��i���o�ԍ��j
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.chutoNo);
			// ����
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.fullname);
			// ���f�敪
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kenshinKbn);
			// ��f�敪
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.jukenKbn);
			// �d�����N�f�f�N����
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kenshinDate);
			// ��������
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.hakekyuSu);
			// �����p��
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.rinpa);
			// �P��
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.tankyu);
			// �ٌ`�����p��
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.sonota);
			// �D��������j
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kanjo);
			// �D�������t�j
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.bunyo);
			// �D�������v
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kochukyukei);
			// �D�_��
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kosan);
			// �D���
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.koenki);
			// �Ԍ�����
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.sekekyu);
			// ���F�f��
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.shikiso);
			// �w�}�g�N���b�g
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.hemato);
			// ���̑�
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.ketsuta);
			// �����̂̍���
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coMe);
			// ����
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coHaseki);
			// �������͏c����
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coKanso);
			// ���
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coKaiyo);
			// �܂ُ̈�
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coTsume);
			// ���̑��̌���
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coSonota);
			// �S�g�I����
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coZenshin);
			// ���o�I�i��
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coJikaku);
			// �Q�l����
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coSanko);
			// ��t�̐f�f
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coShindan);
			// �a�@�E�����@�֖�
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kensaKikanMei);
			// �f�f���s������t��
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.shindanIshi);
			// ��t�̈ӌ�
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coIken);
			// �a�@�E�����@�֖�
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.ikenKensaKikanMei);
			// �ӌ����q�ׂ���t��
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.ikenIshi);
			// ����
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.hanteiKbn);
			// �J��񍐁i�T�C�g�j
			libWorkbook.setCellValue(sheet, rowIdx++, colIdx, k040Kenshin.rokiSiteNo);
			// �J��񍐁i�����j
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.shozokuCd);
			// ���́i�X�V�j����
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kosinDate);
			// ���͎�
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kosinName);

			// ���̏o�͊J�n�s���v�Z
			rowIdx = START_ROW_IDX_KENSHIN + PAGE_ROWS_KENSHIN * (nowPage - 1);

			// ���̏o�͊J�n����v�Z
			colIdx = colIdx + COL_MERGE_LEN_KENSHIN;
		}
    }

	/**
	 * ������o��
	 * @param sheet
	 * @param k040KyoikuList
	 * @param form
	 * @throws Exception
	 */
    private void outputKyoiku(XSSFSheet sheet, List<K040KyoikuListItem> k040KyoikuList, K040Form form)
    		throws Exception {

		// �ϐ��̏�����
		int rowIdx = 0;
		int colIdx = 0;
		LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();

		// �w�b�_��ݒ�
		setHeader(sheet, form);

		// �o�͊J�n�s
		rowIdx = START_ROW_IDX_KYOIKU;
		// �o�͊J�n��
		colIdx = START_COL_IDX_KYOIKU;

		// �e���v���[�g�s���擾
		XSSFRow sourceRow = sheet.getRow(START_ROW_IDX_KYOIKU);

		// ����f�[�^�̏�񕪂��J��Ԃ�
		for(K040KyoikuListItem data: k040KyoikuList){

			// ���{�N����
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.kyoikuDate, sourceRow.getCell(0).getCellStyle());
			// �Ǘ��ԍ��i���o�j
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.chutoNo, sourceRow.getCell(1).getCellStyle());
			// ����
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.fullname, sourceRow.getCell(2).getCellStyle());
			// ���{�Җ�
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.jishiMei, sourceRow.getCell(3).getCellStyle());
			// �Ȗږ�
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.kamokuMei, sourceRow.getCell(4).getCellStyle());
			// �Ə�
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.menjoFlg, sourceRow.getCell(5).getCellStyle());
			// �{�ݖ�
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.shisetsuMei, sourceRow.getCell(6).getCellStyle());
			// d/E����I����
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.eKyoikuDate, sourceRow.getCell(7).getCellStyle());
			// d/E�������
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.eKyoikuSu, sourceRow.getCell(8).getCellStyle());
			// ����(�X�V�j����
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.kosinDate, sourceRow.getCell(9).getCellStyle());
			// ���͎�
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.kosinName, sourceRow.getCell(10).getCellStyle());

			// ���ݍs�̍�����ݒ�
			sheet.getRow(rowIdx).setHeight(sourceRow.getHeight());

			// ���̍s�֍s��
			rowIdx++;
			// �o�͗��������
			colIdx = START_COL_IDX_KYOIKU;
		}

    }

    /**
     * �����E�ޏ����o��
     * @param sheet
     * @param k040NyutaiList
     * @param form
     * @throws Exception
     */
    private void outputNyutai(XSSFSheet sheet, List<K040NyutaiListItem> k040NyutaiList, K040Form form)
    		throws Exception {

		// �ϐ��̏�����
		int rowIdx = 0;
		int colIdx = 0;
		LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();

		// �w�b�_��ݒ�
		setHeader(sheet, form);

		// �o�͊J�n�s
		rowIdx = START_ROW_IDX_NYUTAI;
		// �o�͊J�n��
		colIdx = START_COL_IDX_NYUTAI;

		// �e���v���[�g�s���擾
		XSSFRow sourceRow = sheet.getRow(START_ROW_IDX_NYUTAI);

		// �����E�ޏ��f�[�^�̏�񕪂��J��Ԃ�
		for(K040NyutaiListItem data: k040NyutaiList){

			// �O���[�v
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.groupNo, sourceRow.getCell(0).getCellStyle());
			// �T�C�g
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.siteNo, sourceRow.getCell(1).getCellStyle());
			// �Ǘ��ԍ��i���o�j
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.chutoNo, sourceRow.getCell(2).getCellStyle());
			// ����
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.fullname, sourceRow.getCell(3).getCellStyle());
			// �]���ғo�^�N����
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.jujiTorokuDate, sourceRow.getCell(4).getCellStyle());
			// �]���҉����N����
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.jujiKaijoDate, sourceRow.getCell(5).getCellStyle());
			// ���l
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.biko, sourceRow.getCell(6).getCellStyle());
			// ����(�X�V�j����
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.kosinDate, sourceRow.getCell(7).getCellStyle());
			// ���͎�
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.kosinName, sourceRow.getCell(8).getCellStyle());

			// ���ݍs�̍�����ݒ�
			sheet.getRow(rowIdx).setHeight(sourceRow.getHeight());

			// ���̍s�֍s��
			rowIdx++;
			// �o�͗��������
			colIdx = START_COL_IDX_NYUTAI;
		}
    }

    /**
     * �����ʂ��o��
     * @param sheet
     * @param k040HiSenryoList
     * @param form
     * @throws Exception
     */
    private void outputHiSenryo(XSSFSheet sheet, List<K040HiSenryoListItem> k040HiSenryoList, K040Form form) throws Exception {

    	// �ϐ��̏�����
		int rowIdx = 0;
		int colIdx = 0;
		String compareKey = "";
		LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();

		// �w�b�_��ݒ�
		setHeader(sheet, form);

		// �f�[�^�J�n�s
		rowIdx = START_ROW_IDX_HI_SENRYO;

		// �f�[�^�����݂����ꍇ
		if (k040HiSenryoList.size() > 0) {
			// ���y�[�W�̔�r�p�L�[���擾
			compareKey = k040HiSenryoList.get(0).groupNo + k040HiSenryoList.get(0).siteNo
					+ LibStringUtil.Nvl(k040HiSenryoList.get(0).chutoNo)
					+ LibStringUtil.Nvl(k040HiSenryoList.get(0).sagyoshaNo)
					+ LibStringUtil.Nvl(k040HiSenryoList.get(0).fullname)
					+ LibStringUtil.Nvl(k040HiSenryoList.get(0).kosinName);
		}

    	// ����L�[�̃f�[�^����
    	int dataCnt = 0;

    	for (K040HiSenryoListItem k040HiSenryo : k040HiSenryoList) {

    		// �o�͌�����݉�
    		dataCnt++;
    		// ���y�[�W�̔�r�p�L�[���擾
    		String currentKey = k040HiSenryo.groupNo + k040HiSenryo.siteNo + LibStringUtil.Nvl(k040HiSenryo.chutoNo)
					+ LibStringUtil.Nvl(k040HiSenryo.sagyoshaNo) + LibStringUtil.Nvl(k040HiSenryo.fullname)
					+ LibStringUtil.Nvl(k040HiSenryo.kosinName);

    		// �L�[���ς��ꍇ
    		if (!compareKey.equals(currentKey)) {

    			// ���̃y�[�W�̍s�֍s��
				rowIdx = rowIdx + (PAGE_SIZE_HI_SENRYO - (dataCnt - 1));
    			// ���ڃ^�C�g�����ƃR�s�[
				copyRows(START_ROW_IDX_HI_SENRYO, START_ROW_IDX_HI_SENRYO + 2 + PAGE_SIZE_HI_SENRYO, rowIdx,
						sheet);
				// ���׍s���e���N���A
				clearRowsContent(sheet, rowIdx + 3, rowIdx + 2 + PAGE_SIZE_HI_SENRYO);
				// ���y�[�W�����ݒ�
				sheet.setRowBreak(rowIdx - 1);
    			// �o�͌�����������
				dataCnt = 1;
				// ��r�p�̃L�[���X�V
				compareKey = currentKey;
    		}

    		// ����L�[���o�͌��������y�[�W�̍ő�\�����𒴂���ꍇ
    		if (dataCnt > PAGE_SIZE_HI_SENRYO) {

    			// ���׍s�^�C�g�����ƃR�s�[
				copyRows(START_ROW_IDX_HI_SENRYO + 2 , START_ROW_IDX_HI_SENRYO + 2 + PAGE_SIZE_HI_SENRYO,
						rowIdx, sheet);
				// ���׍s���e���N���A
				clearRowsContent(sheet, rowIdx + 1, rowIdx + PAGE_SIZE_HI_SENRYO);
				// ���y�[�W�����ݒ�
				sheet.setRowBreak(rowIdx - 1);

    			// ���y�[�W�̏o�͊J�n�s�֍s��
				rowIdx = rowIdx + 1;

				// �o�͌�����������
				dataCnt = 1;

			// ���L�[�̈�Ԗڂ̃f�[�^�̏ꍇ
    		} else if (dataCnt == 1) {

    			// ���ڃ^�C�g���̏o�͍s�֍s��
				rowIdx = rowIdx + 1;

				// �O���[�v
				libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.groupNo);
				// �T�C�g
				libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.siteNo);
				// �Ǘ��ԍ��i���o�j
				libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.chutoNo);
				// ���
				colIdx++;
				// �Ǘ��ԍ��i���d��Əؔԍ��j
				libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.sagyoshaNo);
				// ���
				colIdx++;
				// ����
				libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.fullname);
				// ���
				colIdx++;
				// ���́i�X�V�j����
				libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.kosinDate);
				// ���
				colIdx++;
				// ���͎�
				libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.kosinName);

        		// ���׏o�͂̊J�n�s�֍s��
        		rowIdx = rowIdx + 2;

        		// ���̏o�͊J�n���������
    			colIdx = START_COL_IDX_HI_SENRYO;
    		}

    		// ���׍s���o��
			// ���{�N��
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.jishiYm);
			// ���t
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.jishiDay);
			// ���C���m��
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.lineNo);
			// ���@
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.goki);
			// ������
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.senryo);
			// ��
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.senryoGanma);
			// ��
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.senryoBeta);
			// ��
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.senryoN);
			// �H������
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.kojiMei);
			// ���掞��
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.nyuikiJikan);
			// �ވ掞��
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.taiikiJikan);
			// ����
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, toDiffTime(k040HiSenryo.nyuikiJi,
					k040HiSenryo.nyuikiHun, k040HiSenryo.taiikiJi, k040HiSenryo.taiikiHun));
			// ���L����
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.tokiJiko);

			// ���̏o�͍s�֍s��
			rowIdx++;
    		// ���̏o�͊J�n���������
			colIdx = START_COL_IDX_HI_SENRYO;
    	}
    }

    /**
     * �O�����ʂ��o��
     * @param sheet
     * @param k040GaibuSenryoList
     * @param form
     * @throws Exception
     */
    private void outputGaibuSenryo(XSSFSheet sheet, List<K040GaibuSenryoListItem> k040GaibuSenryoList, K040Form form)
    		throws Exception {

		// �ϐ��̏�����
		int rowIdx = 0;
		int colIdx = 0;
		int dataCnt = 0;
		LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();

		// �w�b�_��ݒ�
		setHeader(sheet, form);

		// �f�[�^�J�n�s
		rowIdx = START_ROW_IDX_GAIBU_SENRYO;
		// �f�[�^�J�n��
		colIdx = START_COL_IDX_GAIBU_SENRYO;

		// �O�����ʃf�[�^�̏�񕪂��J��Ԃ�
		for(K040GaibuSenryoListItem data: k040GaibuSenryoList){

			// �o�͌������v�Z
			dataCnt++;

			// �e���v���[�g�s�ȍ~�̏ꍇ
			if (dataCnt > PAGE_SIZE_GAIBU_SENRYO) {
				// �e���v���[�g�s���Ƃ��R�s�[
				copyRows(START_ROW_IDX_GAIBU_SENRYO, START_ROW_IDX_GAIBU_SENRYO + 4, rowIdx, sheet);

				// �Q�y�[�W�ڈȍ~�����y�[�W�̈�Ԗڂ̃f�[�^�̏ꍇ
				if (dataCnt % PAGE_SIZE_GAIBU_SENRYO == 1) {
					// �w��s�ʒu�ȍ~���y�[�W�����ݒ�
					sheet.setRowBreak(rowIdx - 1);
				}
			}

			// �P�s�ڃf�[�^�o��
			// �o�͍s�ԍ��v�Z
			rowIdx = rowIdx + 2;
			// �O���[�v
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.groupNo);
			// �Ǘ��ԍ��i���o�j
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.chutoNo);
			// ����
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.fullname);
			// �������
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sokuteiDate);
			// ���
			colIdx = colIdx + 2;

			// ���i���茋�ʂP�����j
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.xganma1Cm1, data.xganma1Cm2));
			// ���i���j
			colIdx++;
			// �m�����i���茋�ʂP�����j
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.nth1Cm1, data.nth1Cm2));
			// �m���i���茋�ʂP�����j
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.nf1Cm1, data.nf1Cm2));

			// ���i���茋�ʂV�O�ʂ��j
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.xganma70Myu1, data.xganma70Myu2));
			// ���i���茋�ʂV�O�ʂ��j
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.beta70Myu1, data.beta70Myu2));
			// �m�����i���茋�ʂV�O�ʂ��j
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.nth70Myu1, data.nth70Myu2));
			// �m���i���茋�ʂV�O�ʂ��j
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.nf70Myu1, data.nf70Myu2));

			// ����(�X�V�j����
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.kosinDate);
			// ���
			colIdx++;
			// ���͎�
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.kosinName);

			// �Q�s�ڃf�[�^�o��
			// �o�͍s�ԍ����v�Z
			rowIdx = rowIdx + 2;
			// �o�͗�ԍ���������
			colIdx = START_COL_IDX_GAIBU_SENRYO;
			// �T�C�g
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.siteNo);
			// �Ǘ��ԍ��i���d��Əؔԍ��j
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sagyoshaNo);
			// ���
			colIdx++;

			// ����
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.bui);
			// ����
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.maisu);
			// ���j�^���
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.montorSbt);

			// ��������
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.jikkoSenryo1, data.jikkoSenryo2));
			// ���
			colIdx++;
			// �����̓�������
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.suishotai1, data.suishotai2));
			// ���
			colIdx++;
			// �畆��������
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.hifu1, data.hifu2));
			// ���
			colIdx++;
			// �������̑�
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.teashi1, data.teashi2));
			// ���
			colIdx++;

			// ���l
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.biko);
			// ���
			colIdx++;
			// ���L����
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.tokjiko);

			// ���̃f�[�^�s�֍s��
			rowIdx++;
			// ��ԍ���������
			colIdx = START_COL_IDX_NYUTAI;
		}
    }

    /**
     * �������ʂ��o��
     * @param sheet
     * @param k040NaibuSenryoList
     * @param form
     * @throws Exception
     */
    private void outputNaibuSenryo(XSSFSheet sheet, List<K040NaibuSenryoListItem> k040NaibuSenryoList, K040Form form)
    		throws Exception {

		// �ϐ��̏�����
		int rowIdx = 0;
		int colIdx = 0;
		int dataCnt = 0;
		LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();

		// �w�b�_��ݒ�
		setHeader(sheet, form);

		// �f�[�^�J�n�s
		rowIdx = START_ROW_IDX_NAIBU_SENRYO;
		// �f�[�^�J�n��
		colIdx = START_COL_IDX_NAIBU_SENRYO;

		// �������ʃf�[�^�̏�񕪂��J��Ԃ�
		for(K040NaibuSenryoListItem data: k040NaibuSenryoList){

			// �o�͌������v�Z
			dataCnt++;

			// �e���v���[�g�s�ȍ~�̏ꍇ
			if (dataCnt > PAGE_SIZE_NAIBU_SENRYO) {
				// �e���v���[�g�s���Ƃ��R�s�[
				copyRows(START_ROW_IDX_NAIBU_SENRYO, START_ROW_IDX_NAIBU_SENRYO + 3, rowIdx, sheet);

				// �Q�y�[�W�ڈȍ~�����y�[�W�̈�Ԗڂ̃f�[�^�̏ꍇ
				if (dataCnt % PAGE_SIZE_NAIBU_SENRYO == 1) {
					// �w��s�ʒu�ȍ~���y�[�W�����ݒ�
					sheet.setRowBreak(rowIdx - 1);
				}
			}

			// �P�s�ڃf�[�^�o��
			// �o�͍s�ԍ��v�Z
			rowIdx = rowIdx + 1;
			// �O���[�v
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.groupNo);
			// �Ǘ��ԍ��i���o�j
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.chutoNo);
			// ����
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.fullname);
			// ����N����
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sokuteiDate);
			// ����敪
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sokuteiKbn);
			// ��
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sokuteiSu);
			// �Ǘ��敪
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.kanriKbn);
			// ������@
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sokuteiHoho);
			// ���莞��
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, convDispTime(data.sokuteiJikoku));
			// ���莞��
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, convDispTime(data.sokuteiJikan));
			// ����@��
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sokuteiKishu);
			// ���
			colIdx++;
			// ����(�X�V�j����
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.kosinDate);

			// �Q�s�ڃf�[�^�o��
			// �o�͍s�ԍ����v�Z
			rowIdx = rowIdx + 2;
			// �o�͗�ԍ���������
			colIdx = START_COL_IDX_NAIBU_SENRYO;
			// �T�C�g
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.siteNo);
			// �Ǘ��ԍ��i���d��Əؔԍ��j
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sagyoshaNo);
			// ���
			colIdx++;
			// �v���l��
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.keisuA);
			// �v���l��
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.keisuB);
			// �v���l�i�m�d�s�j
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.keisuNet);
			// �]���Ώۓ�
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.hyokaDate);
			// �Z�茋��
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.santeiKeka);
			// ����
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.naibuBui);
			// �ΏۃT�C�g
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.taishoSite);
			// ���l
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.biko);
			// ���L����
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.tokjiko);
			// ���͎�
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.kosinName);

			// ���̃f�[�^�s�֍s��
			rowIdx++;
			// ��ԍ���������
			colIdx = START_COL_IDX_NYUTAI;
		}
    }

    /**
     * �w�b�_���o��
     * @param sheet
     * @param form
     * @throws Exception
     */
    private void setHeader(XSSFSheet sheet, K040Form form) throws Exception {

		// �o�͓������쐬
		SimpleDateFormat d1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String outputDate = d1.format(new Date());

		LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();
		// �o�͓���/�o�͎҂�ݒ�
		libWorkbook.setCellValue(sheet, 0, 0, outputDate + " / " + form.cmnLoginName);
		// �f�[�^���͓���ݒ�
		libWorkbook.setCellValue(sheet, 2, 0, "�f�[�^���͓�          "
				+ form.dateFrom + " �` " + form.dateTo);
    }

    /**
     * ���ԍ����v�Z
     * @param startHour
     * @param startMinute
     * @param endHour
     * @param endMinute
     */
	private String toDiffTime(String startHour, String startMinute, String endHour, String endMinute) {

		// ���掞�������͑ވ掞��null�̏ꍇ
		if (StringUtils.isEmpty(startHour)|| StringUtils.isEmpty(endHour)) {
			return "";
		}

		Calendar calStart = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();

		// �J�n���Ԃ��Z�b�g
		calStart.set( Calendar.HOUR_OF_DAY, Integer.parseInt(startHour));
		calStart.set( Calendar.MINUTE, Integer.parseInt(startMinute));
		calStart.set( Calendar.SECOND, 00);
		// �I�����Ԃ��Z�b�g
		calEnd.set( Calendar.HOUR_OF_DAY, Integer.parseInt(endHour));
		calEnd.set( Calendar.MINUTE, Integer.parseInt(endMinute));
		calEnd.set( Calendar.SECOND, 00);

		// �J�n�����ƏI���������珈�����Ԃ�Ԃ�
        String diffTime = DurationFormatUtils.formatPeriod(calStart.getTimeInMillis(), calEnd.getTimeInMillis(),
        		"HH:mm");

        return diffTime;
	}

	/**
	 * ������(HHMMSS)���玞��������(HH:MM:SS)�ɕϊ�
	 * @param value
	 * @return
	 */
	private String convDispTime(String value) {

		// �l��Null�̏ꍇ�A�󔒂̕������߂�
		if (StringUtil.isEmpty(value)) {
			return "";
		}

		return value.substring(0, 2) + ":" + value.substring(2, 4) + ":" + value.substring(4, 6);
	}

    /**
     * �o�͗p�̐��ʒl���擾
     * @param value1
     * @param value2
     * @return
     */
    private String getDispSenryoVaule(Double value1, Integer value2) {

    	// �����l�������Ȃ������ꍇ
    	if (value1 == null && value2 == null) {
    		return "";
    	}

    	// �����l���������ꍇ
    	if (value1 != null && value2 != null) {
    		return String.format("%1$.2f", value1) + "�{" + String.valueOf(value2) + "��";
    	}

    	// �l�P���󔒂̏ꍇ
    	if (value1 == null) {
    		return String.valueOf(value2) + "��";
    	}

    	// �l�Q���󔒂̏ꍇ
    	if (value2 == null) {
    		return String.format("%1$.2f", value1);
    	}

    	return "";
    }

    /**
     * �s���R�s�[
     * @param pStartRow
     * @param pEndRow
     * @param pPosition
     * @param sheet
     */
	private void copyRows(int pStartRow, int pEndRow, int pPosition, XSSFSheet sheet) {

		int targetRowFrom;
		int targetRowTo;
		int columnCount;
		CellRangeAddress region = null;
		int i;
		int j;
		if (pStartRow == -1 || pEndRow == -1) {
			return;
		}
		//
		for (i = 0; i < sheet.getNumMergedRegions(); i++) {
			region = sheet.getMergedRegion(i);
			if ((region.getFirstRow() >= pStartRow)
					&& (region.getLastRow() <= pEndRow)) {
				targetRowFrom = region.getFirstRow() - pStartRow + pPosition;
				targetRowTo = region.getLastRow() - pStartRow + pPosition;
				CellRangeAddress newRegion = region.copy();
				newRegion.setFirstRow(targetRowFrom);
				newRegion.setFirstColumn(region.getFirstColumn());
				newRegion.setLastRow(targetRowTo);
				newRegion.setLastColumn(region.getLastColumn());
				sheet.addMergedRegion(newRegion);
			}
		}

		for (i = pStartRow; i <= pEndRow; i++) {
			XSSFRow sourceRow = sheet.getRow(i);
			columnCount = sourceRow.getLastCellNum();
			if (sourceRow != null) {
				XSSFRow newRow = sheet.createRow(pPosition - pStartRow + i);
				newRow.setHeight(sourceRow.getHeight());
				for (j = 0; j < columnCount; j++) {
					XSSFCell templateCell = sourceRow.getCell(j);
					if (templateCell != null) {
						XSSFCell newCell = newRow.createCell(j);
						copyCell(templateCell, newCell);
					}
				}
			}
		}
	}

	/**
	 * �Z�[�����R�s�|
	 * @param srcCell
	 * @param distCell
	 */
	private void copyCell(XSSFCell srcCell, XSSFCell distCell) {
		distCell.setCellStyle(srcCell.getCellStyle());
		if (srcCell.getCellComment() != null) {
			distCell.setCellComment(srcCell.getCellComment());
		}
		int srcCellType = srcCell.getCellType();
		distCell.setCellType(srcCellType);
		if (srcCellType == XSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(srcCell)) {
				distCell.setCellValue(srcCell.getDateCellValue());
			} else {
				distCell.setCellValue(srcCell.getNumericCellValue());
			}
		} else if (srcCellType == XSSFCell.CELL_TYPE_STRING) {
			distCell.setCellValue(srcCell.getRichStringCellValue());
		} else if (srcCellType == XSSFCell.CELL_TYPE_BLANK) {
			// nothing
		} else if (srcCellType == XSSFCell.CELL_TYPE_BOOLEAN) {
			distCell.setCellValue(srcCell.getBooleanCellValue());
		} else if (srcCellType == XSSFCell.CELL_TYPE_ERROR) {
			distCell.setCellErrorValue(srcCell.getErrorCellValue());
		} else if (srcCellType == XSSFCell.CELL_TYPE_FORMULA) {
			distCell.setCellFormula(srcCell.getCellFormula());
		} else { // nothing

		}
	}

	/**
	 * �w�肵���s���e���󔒂ɂȂ�
	 * @param sheet
	 * @param pStartRow
	 * @param pEndRow
	 */
	private void clearRowsContent(XSSFSheet sheet, int pStartRow, int pEndRow) {

		for (int i = pStartRow; i <= pEndRow; i++) {
			XSSFRow currentRow = sheet.getRow(i);

			Iterator<Cell> cellIterator = currentRow.iterator();

	        while (cellIterator.hasNext()) {
	            Cell currentCell = cellIterator.next();
	            currentCell.setCellValue("");
	        }
		}
	}

}
