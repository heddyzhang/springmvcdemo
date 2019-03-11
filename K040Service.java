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
 * 操作ログ照会画面Service
 * @author NPC
 * @version 1.0
 */
public class K040Service extends CommonService {

	/** テンプレート名 */
	private static final String SHEET_NAME_TEMP = "K040.xlsx";
	/** シート名（健康診断）*/
	private static final String SHEET_NAME_KENSHIN = "健康診断";
	/** 毎ページ表示データ数（健康診断）*/
	private static final int PAGE_SIZE_KENSHIN = 6;
	/** 毎ページ表示行数（健康診断）*/
	private static final int PAGE_ROWS_KENSHIN = 42;
	/** 出力開始行（健康診断）*/
	private static final int 	START_ROW_IDX_KENSHIN = 4;
	/** 出力開始列（健康診断）*/
	private static final int 	START_COL_IDX_KENSHIN = 11;
	/** カラムのマージ長さ（健康診断）*/
	private static final int 	COL_MERGE_LEN_KENSHIN = 8;

	/** シート名（教育）*/
	private static final String SHEET_NAME_KYOIKU = "教育";
	/** 出力開始行（教育）*/
	private static final int 	START_ROW_IDX_KYOIKU = 5;
	/** 出力開始列（教育）*/
	private static final int 	START_COL_IDX_KYOIKU = 0;

	/** シート名（入所・退所）*/
	private static final String SHEET_NAME_NYUTAI = "入所・退所";
	/** 出力開始行（入所・退所）*/
	private static final int 	START_ROW_IDX_NYUTAI = 5;
	/** 出力開始列（入所・退所）*/
	private static final int 	START_COL_IDX_NYUTAI = 0;

	/** シート名（日線量）*/
	private static final String SHEET_NAME_HI_SENRYO = "日線量";
	/** データ開始行（日線量）*/
	private static final int 	START_ROW_IDX_HI_SENRYO = 4;
	/** データ開始列（日線量）*/
	private static final int 	START_COL_IDX_HI_SENRYO = 0;
	/** 毎ページ表示データ数（日線量）*/
	private static final int PAGE_SIZE_HI_SENRYO = 27;

	/** シート名（外部線量）*/
	private static final String SHEET_NAME_GAIBU_SENRYO = "外部線量";
	/** データ開始行（外部線量）*/
	private static final int 	START_ROW_IDX_GAIBU_SENRYO = 3;
	/** データ開始列（外部線量）*/
	private static final int 	START_COL_IDX_GAIBU_SENRYO = 0;
	/** 毎ページ表示データ数（外部線量）*/
	private static final int PAGE_SIZE_GAIBU_SENRYO = 5;

	/** シート名（内部線量）*/
	private static final String SHEET_NAME_NAIBU_SENRYO = "内部線量";
	/** データ開始行（内部線量）*/
	private static final int 	START_ROW_IDX_NAIBU_SENRYO = 3;
	/** データ開始列（内部線量）*/
	private static final int 	START_COL_IDX_NAIBU_SENRYO = 0;
	/** 毎ページ表示データ数（内部線量）*/
	private static final int PAGE_SIZE_NAIBU_SENRYO = 5;

	/** チェックボックスの定数値（健康診断）*/
	private static final String CHK_VALUE_KENSIN = "1";
	/** チェックボックスの定数値（教育）*/
	private static final String CHK_VALUE_KYOIKU = "2";
	/** チェックボックスの定数値（入所退所 ） */
	private static final String CHK_VALUE_NYUTAI = "3";
	/** チェックボックスの定数値（日線量 ） */
	private static final String CHK_VALUE_HI_SENRYO = "4";
	/** チェックボックスの定数値（外部線量 ） */
	private static final String CHK_VALUE_GAIBU_SENRYO = "5";
	/** チェックボックスの定数値（内部線量 ） */
	private static final String CHK_VALUE_NAIBU_SENRYO = "6";

	/**
	 * 初期表示
	 * @throws Exception
	 *
	 */
    public void init(K040Form k040Form) throws Exception {

		// S2Container初期化
		SingletonS2ContainerFactory.init();

		/**********************************
		 * 画面初期化
		 **********************************/
		// データ入力者の初期化
		List<User> userList = new ArrayList<User>();
		// 最初の入力欄にＴＲＡＣＥＳログイン者を設定。
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

		// 出力項目の初期値
		List<String> outputList = new ArrayList<String>();
		// 健康診断
		outputList.add(CHK_VALUE_KENSIN);
		// 教育
		outputList.add(CHK_VALUE_KYOIKU);
		// 入所退所
		outputList.add(CHK_VALUE_NYUTAI);
		// 日線量
		outputList.add(CHK_VALUE_HI_SENRYO);
		// 外部線量
		outputList.add(CHK_VALUE_GAIBU_SENRYO);
		// 内部線量
		outputList.add(CHK_VALUE_NAIBU_SENRYO);

		k040Form.outputItems = (String[]) outputList.toArray(new String[0]);
	}

    /**
     * Excel出力
     * @param form
     * @param fileName
     * @throws Exception
     */
	public void outputExcel(K040Form form, String fileName) throws Exception {

		// 検索条件を作成
		Map<String, Object> param = new HashMap<String, Object>();
		List<String> userIdList = new ArrayList<String>();

		// 入力日（FROM）
		param.put("dateFrom", form.dateFrom);
		// 入力日（ＴＯ）
		param.put("dateTo", form.dateTo);

		// 入力者IDのListを作成
		for (User user: form.userList) {
			// 入力者IDを入力した場合
			if (!StringUtils.isEmpty(user.getUserId())) {
				userIdList.add(user.getUserId());
			}

		}
		// 入力者ID
		param.put("userIds", userIdList);

    	try {

			LibEnv env = new LibEnv();
			// Excelテンプレートパスの取得
			String strExcelDirPath = env.getExcelPath();
			// EXCELテンプレートファイル読み込み
			LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();
			XSSFWorkbook workbook = libWorkbook.openXSSFWorkbook(strExcelDirPath + "/"+ SHEET_NAME_TEMP);

			// poi-ooxml-3.10-FINAL.jarのバッグ start？？？
			// downloadされたファイルを開くと、たまに、削除されたレコード: /xl/workbook.xml パーツ内のビュー (ブック)」のエラーメッセージを出って,
			// とりあえず、下記設定で対応
			workbook.setActiveSheet(0);
			// poi-ooxml-3.10-FINAL.jarのバッグ end？？？

			// 出力リスト
			List<String> outputlist = Arrays.asList(form.outputItems);

			// 健康診断を出力した場合
			if (outputlist.contains(CHK_VALUE_KENSIN)) {
				// 健康診断のデータリスト取得
				List<K040KenshinListItem>  k040KenshinList = jdbcManager.selectBySqlFile(K040KenshinListItem.class,
						"data/k040Kenshin.sql", param).getResultList();

				// 健康診断シートの内容を出力
				outputKenshin(workbook.getSheet(SHEET_NAME_KENSHIN), k040KenshinList, form);
			} else {
            	// 該当シートを非表示
				libWorkbook.deleteSheetByName(workbook, SHEET_NAME_KENSHIN);
			}

			// 教育を出力した場合
			if (outputlist.contains(CHK_VALUE_KYOIKU)) {
				// 教育のデータリスト取得
				List<K040KyoikuListItem>  k040KyoikuList = jdbcManager.selectBySqlFile(K040KyoikuListItem.class,
						"data/k040Kyoiku.sql", param).getResultList();

				// 教育シートの内容を出力
				outputKyoiku(workbook.getSheet(SHEET_NAME_KYOIKU), k040KyoikuList, form);
			} else {
            	// 該当シートを非表示
				libWorkbook.deleteSheetByName(workbook, SHEET_NAME_KYOIKU);
			}

			// 入所・退所を出力した場合
			if (outputlist.contains(CHK_VALUE_NYUTAI)) {
				// 入所・退所のデータリスト取得
				List<K040NyutaiListItem>  k040NyutaiList = jdbcManager.selectBySqlFile(K040NyutaiListItem.class,
						"data/k040Nyutai.sql", param).getResultList();

				// 入所・退所シートの内容を出力
				outputNyutai(workbook.getSheet(SHEET_NAME_NYUTAI), k040NyutaiList, form);
			} else {
            	// 該当シートを非表示
				libWorkbook.deleteSheetByName(workbook, SHEET_NAME_NYUTAI);
			}

			// 日線量を出力した場合
			if (outputlist.contains(CHK_VALUE_HI_SENRYO)) {
				// 日線量のデータリスト取得
				List<K040HiSenryoListItem>  k040HiSenryoList = jdbcManager.selectBySqlFile(K040HiSenryoListItem.class,
						"data/k040HiSenryo.sql", param).getResultList();

				// 日線量シートの内容を出力
				outputHiSenryo(workbook.getSheet(SHEET_NAME_HI_SENRYO), k040HiSenryoList, form);
			} else {
            	// 該当シートを非表示
				libWorkbook.deleteSheetByName(workbook, SHEET_NAME_HI_SENRYO);
			}

			// 外部線量を出力した場合
			if (outputlist.contains(CHK_VALUE_GAIBU_SENRYO)) {
				// 外部線量のデータリスト取得
				List<K040GaibuSenryoListItem>  k040GaibuSenryoList = jdbcManager.selectBySqlFile(
						K040GaibuSenryoListItem.class, "data/k040GaibuSenryo.sql", param).getResultList();

				// 外部線量シートの内容を出力
				outputGaibuSenryo(workbook.getSheet(SHEET_NAME_GAIBU_SENRYO), k040GaibuSenryoList, form);
			} else {
            	// 該当シートを非表示
				libWorkbook.deleteSheetByName(workbook, SHEET_NAME_GAIBU_SENRYO);
			}

			// 内部線量を出力した場合
			if (outputlist.contains(CHK_VALUE_NAIBU_SENRYO)) {
				// 内部線量のデータリスト取得
				List<K040NaibuSenryoListItem>  k040NaibuSenryoList = jdbcManager.selectBySqlFile(
						K040NaibuSenryoListItem.class, "data/k040NaibuSenryo.sql", param).getResultList();

				// 内部線量シートの内容を出力
				outputNaibuSenryo(workbook.getSheet(SHEET_NAME_NAIBU_SENRYO), k040NaibuSenryoList, form);
			} else {
            	// 該当シートを非表示
				libWorkbook.deleteSheetByName(workbook, SHEET_NAME_NAIBU_SENRYO);
			}

			// Excelファイルをレスポンスに出力
			libWorkbook.outputExcel(ResponseUtil.getResponse(), workbook, fileName);

		} catch (Exception e) {
			throw e;
		}

    }

	/**
	 * データ入力者が存在するかをチェック
	 * @param form
	 * @return
	 */
	public boolean checkRiyoshaID(K040Form form, List<String> errorList) {

		Map<String, Object> param = new HashMap<String, Object>();
		String userId = "";

		// 入力者IDのListを作成
		for (User user: form.userList) {

			userId = user.getUserId();

			// 入力者IDを入力した場合
			if (!StringUtils.isEmpty(userId)) {

				// 入力者ID
				param.put("userId", userId);
				// 利用者ＩＤテーブルを検索
		        long count = jdbcManager.selectBySqlFile(Long.class, "data/k040CheckRiyoshaID.sql", param)
		        		.getSingleResult();

		        // データが存在しない場合
		        if (count == 0) {
		        	errorList.add(userId);
		        }
			}

		}

		// 存在しない入力者IDがあった場合
		if (errorList.size() > 0 ) {
			return false;
		}

        return true;
	}

	/**
	 * 健康診断を出力
	 * @param sheet
	 * @param k040KenshinList
	 * @param form
	 * @throws Exception
	 */
	private void outputKenshin(XSSFSheet sheet, List<K040KenshinListItem> k040KenshinList, K040Form form)
			throws Exception {

		// 変数の初期化
		int pageSum= 0;
		int pPositioin= 0;
		int nowPage = 0;
		int rowIdx = 0;
		int colIdx = 0;
		LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();

        // 総ページ数を計算
		pageSum = (k040KenshinList.size() + PAGE_SIZE_KENSHIN - 1) / PAGE_SIZE_KENSHIN;
		// ヘッダを設定
		setHeader(sheet, form);

		// ページ数を繰り返し
		for (int i = 2; i <= pageSum; i++) {

			// 改ページ後の開始行位置を計算
			pPositioin = (i - 1) * PAGE_ROWS_KENSHIN;
			// 項目タイトルごとコピー
			copyRows(0, PAGE_ROWS_KENSHIN - 1, pPositioin, sheet);
			// 指定行位置以降改ページ印刷を設定
			sheet.setRowBreak(pPositioin - 1);
		}

		// 現在のページ
		nowPage = 1;
		// 出力開始行
		rowIdx = START_ROW_IDX_KENSHIN;
		// 出力開始列
		colIdx = START_COL_IDX_KENSHIN;

		// 健康診断データの情報分を繰り返し
		for (K040KenshinListItem k040Kenshin : k040KenshinList) {

			// 改ページの場合
			if (colIdx == START_COL_IDX_KENSHIN + PAGE_SIZE_KENSHIN * COL_MERGE_LEN_KENSHIN) {
				// 現在のページ数
				nowPage++;
				// 出力開始行を計算
				rowIdx = START_ROW_IDX_KENSHIN + PAGE_ROWS_KENSHIN * (nowPage - 1);
				// 出力開始列を初期化
				colIdx = START_COL_IDX_KENSHIN;
			}

			// 管理番号（中登番号）
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.chutoNo);
			// 氏名
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.fullname);
			// 健診区分
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kenshinKbn);
			// 受診区分
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.jukenKbn);
			// 電離健康診断年月日
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kenshinDate);
			// 白血球数
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.hakekyuSu);
			// リンパ球
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.rinpa);
			// 単球
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.tankyu);
			// 異形リンパ球
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.sonota);
			// 好中球桿状核
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kanjo);
			// 好中球分葉核
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.bunyo);
			// 好中球合計
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kochukyukei);
			// 好酸球
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kosan);
			// 好塩基球
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.koenki);
			// 赤血球数
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.sekekyu);
			// 血色素量
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.shikiso);
			// ヘマトクリット
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.hemato);
			// その他
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.ketsuta);
			// 水晶体の混濁
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coMe);
			// 発赤
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coHaseki);
			// 乾燥又は縦じわ
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coKanso);
			// 潰瘍
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coKaiyo);
			// 爪の異常
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coTsume);
			// その他の検査
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coSonota);
			// 全身的所見
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coZenshin);
			// 自覚的訴え
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coJikaku);
			// 参考事項
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coSanko);
			// 医師の診断
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coShindan);
			// 病院・検査機関名
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kensaKikanMei);
			// 診断を行った医師名
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.shindanIshi);
			// 医師の意見
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.coIken);
			// 病院・検査機関名
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.ikenKensaKikanMei);
			// 意見を述べた医師名
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.ikenIshi);
			// 判定
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.hanteiKbn);
			// 労基報告（サイト）
			libWorkbook.setCellValue(sheet, rowIdx++, colIdx, k040Kenshin.rokiSiteNo);
			// 労基報告（所属）
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.shozokuCd);
			// 入力（更新）日時
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kosinDate);
			// 入力者
			libWorkbook.setCellDataValue(sheet, rowIdx++, colIdx, k040Kenshin.kosinName);

			// 次の出力開始行を計算
			rowIdx = START_ROW_IDX_KENSHIN + PAGE_ROWS_KENSHIN * (nowPage - 1);

			// 次の出力開始列を計算
			colIdx = colIdx + COL_MERGE_LEN_KENSHIN;
		}
    }

	/**
	 * 教育を出力
	 * @param sheet
	 * @param k040KyoikuList
	 * @param form
	 * @throws Exception
	 */
    private void outputKyoiku(XSSFSheet sheet, List<K040KyoikuListItem> k040KyoikuList, K040Form form)
    		throws Exception {

		// 変数の初期化
		int rowIdx = 0;
		int colIdx = 0;
		LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();

		// ヘッダを設定
		setHeader(sheet, form);

		// 出力開始行
		rowIdx = START_ROW_IDX_KYOIKU;
		// 出力開始列
		colIdx = START_COL_IDX_KYOIKU;

		// テンプレート行を取得
		XSSFRow sourceRow = sheet.getRow(START_ROW_IDX_KYOIKU);

		// 教育データの情報分を繰り返し
		for(K040KyoikuListItem data: k040KyoikuList){

			// 実施年月日
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.kyoikuDate, sourceRow.getCell(0).getCellStyle());
			// 管理番号（中登）
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.chutoNo, sourceRow.getCell(1).getCellStyle());
			// 氏名
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.fullname, sourceRow.getCell(2).getCellStyle());
			// 実施者名
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.jishiMei, sourceRow.getCell(3).getCellStyle());
			// 科目名
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.kamokuMei, sourceRow.getCell(4).getCellStyle());
			// 免除
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.menjoFlg, sourceRow.getCell(5).getCellStyle());
			// 施設名
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.shisetsuMei, sourceRow.getCell(6).getCellStyle());
			// d/E教育終了日
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.eKyoikuDate, sourceRow.getCell(7).getCellStyle());
			// d/E教育日数
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.eKyoikuSu, sourceRow.getCell(8).getCellStyle());
			// 入力(更新）日時
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.kosinDate, sourceRow.getCell(9).getCellStyle());
			// 入力者
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.kosinName, sourceRow.getCell(10).getCellStyle());

			// 現在行の高さを設定
			sheet.getRow(rowIdx).setHeight(sourceRow.getHeight());

			// 次の行へ行く
			rowIdx++;
			// 出力列を初期化
			colIdx = START_COL_IDX_KYOIKU;
		}

    }

    /**
     * 入所・退所を出力
     * @param sheet
     * @param k040NyutaiList
     * @param form
     * @throws Exception
     */
    private void outputNyutai(XSSFSheet sheet, List<K040NyutaiListItem> k040NyutaiList, K040Form form)
    		throws Exception {

		// 変数の初期化
		int rowIdx = 0;
		int colIdx = 0;
		LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();

		// ヘッダを設定
		setHeader(sheet, form);

		// 出力開始行
		rowIdx = START_ROW_IDX_NYUTAI;
		// 出力開始列
		colIdx = START_COL_IDX_NYUTAI;

		// テンプレート行を取得
		XSSFRow sourceRow = sheet.getRow(START_ROW_IDX_NYUTAI);

		// 入所・退所データの情報分を繰り返し
		for(K040NyutaiListItem data: k040NyutaiList){

			// グループ
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.groupNo, sourceRow.getCell(0).getCellStyle());
			// サイト
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.siteNo, sourceRow.getCell(1).getCellStyle());
			// 管理番号（中登）
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.chutoNo, sourceRow.getCell(2).getCellStyle());
			// 氏名
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.fullname, sourceRow.getCell(3).getCellStyle());
			// 従事者登録年月日
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.jujiTorokuDate, sourceRow.getCell(4).getCellStyle());
			// 従事者解除年月日
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.jujiKaijoDate, sourceRow.getCell(5).getCellStyle());
			// 備考
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.biko, sourceRow.getCell(6).getCellStyle());
			// 入力(更新）日時
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.kosinDate, sourceRow.getCell(7).getCellStyle());
			// 入力者
			libWorkbook.setCellValue(sheet, rowIdx, colIdx++, data.kosinName, sourceRow.getCell(8).getCellStyle());

			// 現在行の高さを設定
			sheet.getRow(rowIdx).setHeight(sourceRow.getHeight());

			// 次の行へ行く
			rowIdx++;
			// 出力列を初期化
			colIdx = START_COL_IDX_NYUTAI;
		}
    }

    /**
     * 日線量を出力
     * @param sheet
     * @param k040HiSenryoList
     * @param form
     * @throws Exception
     */
    private void outputHiSenryo(XSSFSheet sheet, List<K040HiSenryoListItem> k040HiSenryoList, K040Form form) throws Exception {

    	// 変数の初期化
		int rowIdx = 0;
		int colIdx = 0;
		String compareKey = "";
		LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();

		// ヘッダを設定
		setHeader(sheet, form);

		// データ開始行
		rowIdx = START_ROW_IDX_HI_SENRYO;

		// データが存在した場合
		if (k040HiSenryoList.size() > 0) {
			// 改ページの比較用キーを取得
			compareKey = k040HiSenryoList.get(0).groupNo + k040HiSenryoList.get(0).siteNo
					+ LibStringUtil.Nvl(k040HiSenryoList.get(0).chutoNo)
					+ LibStringUtil.Nvl(k040HiSenryoList.get(0).sagyoshaNo)
					+ LibStringUtil.Nvl(k040HiSenryoList.get(0).fullname)
					+ LibStringUtil.Nvl(k040HiSenryoList.get(0).kosinName);
		}

    	// 同一キーのデータ件数
    	int dataCnt = 0;

    	for (K040HiSenryoListItem k040HiSenryo : k040HiSenryoList) {

    		// 出力件数を累加
    		dataCnt++;
    		// 改ページの比較用キーを取得
    		String currentKey = k040HiSenryo.groupNo + k040HiSenryo.siteNo + LibStringUtil.Nvl(k040HiSenryo.chutoNo)
					+ LibStringUtil.Nvl(k040HiSenryo.sagyoshaNo) + LibStringUtil.Nvl(k040HiSenryo.fullname)
					+ LibStringUtil.Nvl(k040HiSenryo.kosinName);

    		// キーが変わる場合
    		if (!compareKey.equals(currentKey)) {

    			// 次のページの行へ行く
				rowIdx = rowIdx + (PAGE_SIZE_HI_SENRYO - (dataCnt - 1));
    			// 項目タイトルごとコピー
				copyRows(START_ROW_IDX_HI_SENRYO, START_ROW_IDX_HI_SENRYO + 2 + PAGE_SIZE_HI_SENRYO, rowIdx,
						sheet);
				// 明細行内容をクリア
				clearRowsContent(sheet, rowIdx + 3, rowIdx + 2 + PAGE_SIZE_HI_SENRYO);
				// 改ページ印刷を設定
				sheet.setRowBreak(rowIdx - 1);
    			// 出力件数を初期化
				dataCnt = 1;
				// 比較用のキーを更新
				compareKey = currentKey;
    		}

    		// 同一キー且つ出力件数が毎ページの最大表示数を超える場合
    		if (dataCnt > PAGE_SIZE_HI_SENRYO) {

    			// 明細行タイトルごとコピー
				copyRows(START_ROW_IDX_HI_SENRYO + 2 , START_ROW_IDX_HI_SENRYO + 2 + PAGE_SIZE_HI_SENRYO,
						rowIdx, sheet);
				// 明細行内容をクリア
				clearRowsContent(sheet, rowIdx + 1, rowIdx + PAGE_SIZE_HI_SENRYO);
				// 改ページ印刷を設定
				sheet.setRowBreak(rowIdx - 1);

    			// 改ページの出力開始行へ行く
				rowIdx = rowIdx + 1;

				// 出力件数を初期化
				dataCnt = 1;

			// 毎キーの一番目のデータの場合
    		} else if (dataCnt == 1) {

    			// 項目タイトルの出力行へ行く
				rowIdx = rowIdx + 1;

				// グループ
				libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.groupNo);
				// サイト
				libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.siteNo);
				// 管理番号（中登）
				libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.chutoNo);
				// 空列
				colIdx++;
				// 管理番号（東電作業証番号）
				libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.sagyoshaNo);
				// 空列
				colIdx++;
				// 氏名
				libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.fullname);
				// 空列
				colIdx++;
				// 入力（更新）日時
				libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.kosinDate);
				// 空列
				colIdx++;
				// 入力者
				libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.kosinName);

        		// 明細出力の開始行へ行く
        		rowIdx = rowIdx + 2;

        		// 次の出力開始列を初期化
    			colIdx = START_COL_IDX_HI_SENRYO;
    		}

    		// 明細行を出力
			// 実施年月
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.jishiYm);
			// 日付
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.jishiDay);
			// ラインＮｏ
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.lineNo);
			// 号機
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.goki);
			// 日線量
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.senryo);
			// γ
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.senryoGanma);
			// β
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.senryoBeta);
			// ｎ
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.senryoN);
			// 工事件名
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.kojiMei);
			// 入域時間
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.nyuikiJikan);
			// 退域時間
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.taiikiJikan);
			// 時間
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, toDiffTime(k040HiSenryo.nyuikiJi,
					k040HiSenryo.nyuikiHun, k040HiSenryo.taiikiJi, k040HiSenryo.taiikiHun));
			// 特記事項
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, k040HiSenryo.tokiJiko);

			// 次の出力行へ行く
			rowIdx++;
    		// 次の出力開始列を初期化
			colIdx = START_COL_IDX_HI_SENRYO;
    	}
    }

    /**
     * 外部線量を出力
     * @param sheet
     * @param k040GaibuSenryoList
     * @param form
     * @throws Exception
     */
    private void outputGaibuSenryo(XSSFSheet sheet, List<K040GaibuSenryoListItem> k040GaibuSenryoList, K040Form form)
    		throws Exception {

		// 変数の初期化
		int rowIdx = 0;
		int colIdx = 0;
		int dataCnt = 0;
		LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();

		// ヘッダを設定
		setHeader(sheet, form);

		// データ開始行
		rowIdx = START_ROW_IDX_GAIBU_SENRYO;
		// データ開始列
		colIdx = START_COL_IDX_GAIBU_SENRYO;

		// 外部線量データの情報分を繰り返し
		for(K040GaibuSenryoListItem data: k040GaibuSenryoList){

			// 出力件数を計算
			dataCnt++;

			// テンプレート行以降の場合
			if (dataCnt > PAGE_SIZE_GAIBU_SENRYO) {
				// テンプレート行ごとをコピー
				copyRows(START_ROW_IDX_GAIBU_SENRYO, START_ROW_IDX_GAIBU_SENRYO + 4, rowIdx, sheet);

				// ２ページ目以降且毎ページの一番目のデータの場合
				if (dataCnt % PAGE_SIZE_GAIBU_SENRYO == 1) {
					// 指定行位置以降改ページ印刷を設定
					sheet.setRowBreak(rowIdx - 1);
				}
			}

			// １行目データ出力
			// 出力行番号計算
			rowIdx = rowIdx + 2;
			// グループ
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.groupNo);
			// 管理番号（中登）
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.chutoNo);
			// 氏名
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.fullname);
			// 測定期間
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sokuteiDate);
			// 空列
			colIdx = colIdx + 2;

			// γ（測定結果１ｃｍ）
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.xganma1Cm1, data.xganma1Cm2));
			// β（空列）
			colIdx++;
			// Ｎｔｈ（測定結果１ｃｍ）
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.nth1Cm1, data.nth1Cm2));
			// Ｎｆ（測定結果１ｃｍ）
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.nf1Cm1, data.nf1Cm2));

			// γ（測定結果７０μｍ）
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.xganma70Myu1, data.xganma70Myu2));
			// β（測定結果７０μｍ）
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.beta70Myu1, data.beta70Myu2));
			// Ｎｔｈ（測定結果７０μｍ）
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.nth70Myu1, data.nth70Myu2));
			// Ｎｆ（測定結果７０μｍ）
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.nf70Myu1, data.nf70Myu2));

			// 入力(更新）日時
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.kosinDate);
			// 空列
			colIdx++;
			// 入力者
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.kosinName);

			// ２行目データ出力
			// 出力行番号を計算
			rowIdx = rowIdx + 2;
			// 出力列番号を初期化
			colIdx = START_COL_IDX_GAIBU_SENRYO;
			// サイト
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.siteNo);
			// 管理番号（東電作業証番号）
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sagyoshaNo);
			// 空列
			colIdx++;

			// 部位
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.bui);
			// 枚数
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.maisu);
			// モニタ種別
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.montorSbt);

			// 実効線量
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.jikkoSenryo1, data.jikkoSenryo2));
			// 空列
			colIdx++;
			// 水晶体等価線量
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.suishotai1, data.suishotai2));
			// 空列
			colIdx++;
			// 皮膚等価線量
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.hifu1, data.hifu2));
			// 空列
			colIdx++;
			// 等価その他
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, getDispSenryoVaule(data.teashi1, data.teashi2));
			// 空列
			colIdx++;

			// 備考
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.biko);
			// 空列
			colIdx++;
			// 特記事項
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.tokjiko);

			// 次のデータ行へ行く
			rowIdx++;
			// 列番号を初期化
			colIdx = START_COL_IDX_NYUTAI;
		}
    }

    /**
     * 内部線量を出力
     * @param sheet
     * @param k040NaibuSenryoList
     * @param form
     * @throws Exception
     */
    private void outputNaibuSenryo(XSSFSheet sheet, List<K040NaibuSenryoListItem> k040NaibuSenryoList, K040Form form)
    		throws Exception {

		// 変数の初期化
		int rowIdx = 0;
		int colIdx = 0;
		int dataCnt = 0;
		LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();

		// ヘッダを設定
		setHeader(sheet, form);

		// データ開始行
		rowIdx = START_ROW_IDX_NAIBU_SENRYO;
		// データ開始列
		colIdx = START_COL_IDX_NAIBU_SENRYO;

		// 内部線量データの情報分を繰り返し
		for(K040NaibuSenryoListItem data: k040NaibuSenryoList){

			// 出力件数を計算
			dataCnt++;

			// テンプレート行以降の場合
			if (dataCnt > PAGE_SIZE_NAIBU_SENRYO) {
				// テンプレート行ごとをコピー
				copyRows(START_ROW_IDX_NAIBU_SENRYO, START_ROW_IDX_NAIBU_SENRYO + 3, rowIdx, sheet);

				// ２ページ目以降且毎ページの一番目のデータの場合
				if (dataCnt % PAGE_SIZE_NAIBU_SENRYO == 1) {
					// 指定行位置以降改ページ印刷を設定
					sheet.setRowBreak(rowIdx - 1);
				}
			}

			// １行目データ出力
			// 出力行番号計算
			rowIdx = rowIdx + 1;
			// グループ
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.groupNo);
			// 管理番号（中登）
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.chutoNo);
			// 氏名
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.fullname);
			// 測定年月日
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sokuteiDate);
			// 測定区分
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sokuteiKbn);
			// 回数
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sokuteiSu);
			// 管理区分
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.kanriKbn);
			// 測定方法
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sokuteiHoho);
			// 測定時刻
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, convDispTime(data.sokuteiJikoku));
			// 測定時間
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, convDispTime(data.sokuteiJikan));
			// 測定機種
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sokuteiKishu);
			// 空列
			colIdx++;
			// 入力(更新）日時
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.kosinDate);

			// ２行目データ出力
			// 出力行番号を計算
			rowIdx = rowIdx + 2;
			// 出力列番号を初期化
			colIdx = START_COL_IDX_NAIBU_SENRYO;
			// サイト
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.siteNo);
			// 管理番号（東電作業証番号）
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.sagyoshaNo);
			// 空列
			colIdx++;
			// 計測値ａ
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.keisuA);
			// 計測値ｂ
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.keisuB);
			// 計測値（ＮＥＴ）
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.keisuNet);
			// 評価対象日
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.hyokaDate);
			// 算定結果
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.santeiKeka);
			// 部位
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.naibuBui);
			// 対象サイト
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.taishoSite);
			// 備考
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.biko);
			// 特記事項
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.tokjiko);
			// 入力者
			libWorkbook.setCellDataValue(sheet, rowIdx, colIdx++, data.kosinName);

			// 次のデータ行へ行く
			rowIdx++;
			// 列番号を初期化
			colIdx = START_COL_IDX_NYUTAI;
		}
    }

    /**
     * ヘッダを出力
     * @param sheet
     * @param form
     * @throws Exception
     */
    private void setHeader(XSSFSheet sheet, K040Form form) throws Exception {

		// 出力日時を作成
		SimpleDateFormat d1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String outputDate = d1.format(new Date());

		LibXSSFWorkbook libWorkbook = new LibXSSFWorkbook();
		// 出力日時/出力者を設定
		libWorkbook.setCellValue(sheet, 0, 0, outputDate + " / " + form.cmnLoginName);
		// データ入力日を設定
		libWorkbook.setCellValue(sheet, 2, 0, "データ入力日          "
				+ form.dateFrom + " ～ " + form.dateTo);
    }

    /**
     * 時間差を計算
     * @param startHour
     * @param startMinute
     * @param endHour
     * @param endMinute
     */
	private String toDiffTime(String startHour, String startMinute, String endHour, String endMinute) {

		// 入域時もしくは退域時がnullの場合
		if (StringUtils.isEmpty(startHour)|| StringUtils.isEmpty(endHour)) {
			return "";
		}

		Calendar calStart = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();

		// 開始時間をセット
		calStart.set( Calendar.HOUR_OF_DAY, Integer.parseInt(startHour));
		calStart.set( Calendar.MINUTE, Integer.parseInt(startMinute));
		calStart.set( Calendar.SECOND, 00);
		// 終了時間をセット
		calEnd.set( Calendar.HOUR_OF_DAY, Integer.parseInt(endHour));
		calEnd.set( Calendar.MINUTE, Integer.parseInt(endMinute));
		calEnd.set( Calendar.SECOND, 00);

		// 開始時刻と終了時刻から処理時間を返す
        String diffTime = DurationFormatUtils.formatPeriod(calStart.getTimeInMillis(), calEnd.getTimeInMillis(),
        		"HH:mm");

        return diffTime;
	}

	/**
	 * 文字列(HHMMSS)から時刻文字列(HH:MM:SS)に変換
	 * @param value
	 * @return
	 */
	private String convDispTime(String value) {

		// 値がNullの場合、空白の文字列を戻る
		if (StringUtil.isEmpty(value)) {
			return "";
		}

		return value.substring(0, 2) + ":" + value.substring(2, 4) + ":" + value.substring(4, 6);
	}

    /**
     * 出力用の線量値を取得
     * @param value1
     * @param value2
     * @return
     */
    private String getDispSenryoVaule(Double value1, Integer value2) {

    	// 両方値が入っなかった場合
    	if (value1 == null && value2 == null) {
    		return "";
    	}

    	// 両方値が入った場合
    	if (value1 != null && value2 != null) {
    		return String.format("%1$.2f", value1) + "＋" + String.valueOf(value2) + "ｘ";
    	}

    	// 値１が空白の場合
    	if (value1 == null) {
    		return String.valueOf(value2) + "ｘ";
    	}

    	// 値２が空白の場合
    	if (value2 == null) {
    		return String.format("%1$.2f", value1);
    	}

    	return "";
    }

    /**
     * 行をコピー
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
	 * セールをコピ－
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
	 * 指定した行内容を空白になる
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
