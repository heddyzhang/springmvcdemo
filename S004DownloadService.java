package jp.co.toshiba.hby.pspromis.syuueki.service;


import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import jp.co.toshiba.hby.pspromis.common.util.StringUtil;
import jp.co.toshiba.hby.pspromis.syuueki.bean.S004Bean;
import jp.co.toshiba.hby.pspromis.syuueki.entity.Cost;
import jp.co.toshiba.hby.pspromis.syuueki.entity.OperationLog;
import jp.co.toshiba.hby.pspromis.syuueki.entity.S004DownloadEntity;
import jp.co.toshiba.hby.pspromis.syuueki.entity.S004DownloadKaisyuEntity;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuGeBukkenInfoTbl;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuKiLossTbl;
import jp.co.toshiba.hby.pspromis.syuueki.enums.Env;
import jp.co.toshiba.hby.pspromis.syuueki.enums.Label;
//import jp.co.toshiba.hby.pspromis.syuueki.facade.KanjyoMstFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.S004DownloadFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SysdateEntityFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiLossTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.interceptor.TranceInterceptor;
import jp.co.toshiba.hby.pspromis.syuueki.pages.DetailHeader;
import jp.co.toshiba.hby.pspromis.syuueki.pages.DivisonComponentPage;
import jp.co.toshiba.hby.pspromis.syuueki.util.ConstantString;
import jp.co.toshiba.hby.pspromis.syuueki.util.LoginUserInfo;
import jp.co.toshiba.hby.pspromis.syuueki.util.PoiUtil;
import jp.co.toshiba.hby.pspromis.syuueki.util.SyuuekiUtils;
import jp.co.toshiba.hby.pspromis.syuueki.util.Utils;
//import jp.co.toshiba.hby.pspromis.syuueki.util.ConstantString;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PS-Promis収益管理システム
 * 期間損益(進行基準) Service
 * @author (NPC)Y.Kitajima
 */
@Stateless
@Interceptors({TranceInterceptor.class})
public class S004DownloadService {

    private final String SAVE_ONLINE = "SAVE_ONLINE";
    private final String DL_TPL = "DL_TPL";
    private final String UP_TPL = "UP_TPL";
    private final String ADD_CHOUSEI = "ADD_CHOUSEI";
    private final String COPY_SABUN = "COPY_SABUN";
    private final String DL_PDF = "DL_PDF";

    private final String SAVE_ONLINE_ID = "20";
    private final String DL_TPL_ID = "10";
    private final String UP_TPL_ID = "15";
    private final String ADD_CHOUSEI_ID = "20";
    private final String COPY_SABUN_ID = "20";
    private final String DL_PDF_ID = "10";

    private final String jpUnit = "JPY";
    
    private final String labelKawase = Label.getValue(Label.kawaseDiff); //"為替差調整"
    
    // 左部項目タイトル開始行
    //private final int DATA_START_ROW = 15;
    private final int DATA_START_ROW = 5;
    // 左部項目タイトル開始列
    private final int DATA_START_COL = 5;
    
    // 年月項目タイトル行
    private final int YM_START_ROW = 3;
    // 年月項目タイトル開始列
    private final int YM_START_COL = 5;
    
    
    // セルスタイル格納位置
    private final int JISSEKI_STYLE_COL = 5;
    private final int MIKOMI_STYLE_COL = 6;
    private final int QUARTER_STYLE_COL = 7;
    
    // 回収管理 年月表示の加算分
    //private final int ADD_YM = 3;
    // 年月項目タイトル回収管理用
    private final String YM_TITLE_K = "_K";
    
    /**
     * ロガー
     */
    public static final Logger logger = LoggerFactory.getLogger(S004DownloadService.class);

    /**
     * パラメータ格納クラスをinjection(CDI)<br>
     * InjectアノテーションよりAPサーバー(Glassfish)側で自動的にインスタンス作成(new)される。<br>
     */
    @Inject
    private S004Bean s004Bean;

    /**
     * Injection DetailHeader
     */
    @Inject
    private DetailHeader dateilHeader;

    /**
     * Injection loginUserInfo
     * (ユーザー情報(ユーザーid,名称,所属部課名)を格納したオブジェクト)
     */
    @Inject
    private LoginUserInfo loginUserInfo;

    /**operationLogService
     * Injection syuuekiUtils
     */
    @Inject
    private SyuuekiUtils syuuekiUtils;

    /**
     * Injection OperationLogService
     */
    @Inject
    private OperationLogService operationLogService;

    //@Inject
    //private KanjyoMstFacade kanjyoMstFacade;

    @Inject
    private SysdateEntityFacade sysdateFacade;
    
    @Inject
    private S004DownloadFacade downloadFacade;

    @Inject
    private SyuKiLossTblFacade syuKiLossTblFacade;
    
    @Inject
    private DivisonComponentPage divisionComponentPage;
    
    /**
     * 通貨種類
     */
    private List<S004DownloadEntity> currencyList;
    
    /**
     * 売上原価カテゴリ種類
     */
    private List<Cost> cateTitleList;
    
    /**
     * 各項目開始位置
     */
    private Map<String, Object> headStartMap;
    
    /**
     * 各項目開始位置
     */
    private Map<String, List<Integer>> mergedRegionMap;
    
    /**
     * 見込初月フラグ
     */
    private Integer firstMikomiFlg;
    
    /**
     * 完売月フラグ
     */
    private Integer kanbaiFlg;
    
    /**
     * 実績の範囲
     */
    private Integer jissekiCol;
    
    /**
     * 前月セル(四半期終了月)の位置補正用
     */
    private Integer colBefMonth;
    
    /**
     * 四半期３ヵ月分SUM用
     * 四半期開始月の位置補正用
     */
    private Integer cntQuarterSum;
    
    /**
     * 期６ヵ月分SUM用
     * 列番号を保持
     */
    private Integer colQuarterSum1;
    private Integer colQuarterSum2;
    private Integer colQuarterSum3;
    private Integer colQuarterSum4;
    
    /**
     * 最終列の位置
     */
    private Integer lastCol;
    
    /**
     * 勘定月
     */
    private String kanjoYm;
    
    /**
     * 削除予定の非表示行
     */
    private Integer delTagetRow;
    
    /**
     * 事業部コード 
     */
    private String divisionCode;
    
    /**
     * 回収管理　年月開始位置
     */
    private Integer ymStartRow;
    
    /**
     * 画面表示　ビジネスロジック
     * @throws Exception
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void indexExecute() throws Exception {
        logger.info("S004ServiceDownload#indexExecute");
        logger.info("jpyUnit=" + s004Bean.getJpyUnit());
 
    }

    
    /**
     * 操作ログの登録
     * @param operationCode
     * @throws Exception
     */
    public void registOperationLog(String operationCode) throws Exception{
        OperationLog operationLog = this.operationLogService.getOperationLog();

        operationLog.setOperationCode(operationCode);
        operationLog.setObjectId((new Integer(this.getObjectId(operationCode))));
        operationLog.setObjectType("KIKAN_S");
        operationLog.setRemarks(s004Bean.getAnkenId());

        operationLogService.insertOperationLogSearch(operationLog);
    }

    /**
     * 操作対象IDの取得
     * @param operationCode
     * @return
     */
    private String getObjectId(String operationCode){
        if(this.ADD_CHOUSEI.equals(operationCode)){
            return this.ADD_CHOUSEI_ID;
        }else if(this.COPY_SABUN.equals(operationCode)){
            return this.COPY_SABUN_ID;
        }else if(this.DL_PDF.equals(operationCode)){
            return this.DL_PDF_ID;
        }else if(this.DL_TPL.equals(operationCode)){
            return this.DL_TPL_ID;
        }else if(this.SAVE_ONLINE.equals(operationCode)){
            return this.SAVE_ONLINE_ID;
        }else if(this.UP_TPL.equals(operationCode)){
            return this.UP_TPL_ID;
        }
        return null;
    }

    /**
     * インスタンス変数を全て初期化
     */
    private void initInstanceValue() {
        this.currencyList = null;
        this.cateTitleList = null;
        this.headStartMap = null;
        this.mergedRegionMap = null;
        this.firstMikomiFlg = null;
        this.kanbaiFlg = null;
        this.jissekiCol = null;
        this.colBefMonth = null;
        this.cntQuarterSum = null;
        this.colQuarterSum1 = null;
        this.colQuarterSum2 = null;
        this.colQuarterSum3 = null;
        this.colQuarterSum4 = null;
        this.lastCol = null;
        this.kanjoYm = null;
        this.delTagetRow = null;
        this.divisionCode = null;
        this.ymStartRow = YM_START_ROW;

        logger.info("all instance clear");
    }

    /**
     * Excelダウンロードのデータ埋め込み
     * @param workbook
     * @throws Exception 
     */
    public void outputDownloadExcel(Workbook workbook) throws Exception {
        // インスタンス変数を全て初期化
        initInstanceValue();
        
        // シート取得
        //Sheet sheet = workbook.getSheetAt(0);
        Sheet sheet = workbook.getSheet("kikanS_list");
        
        // スタイルコピー用シートを取得
        //Sheet styleSheet = workbook.getSheetAt(1);
        Sheet styleSheet = workbook.getSheet("kikanS_style");
        
        // 案件の基本情報を取得
        takeAnkenData();
        
        // ヘッダ情報をセット
        setHeadData(sheet);
        
        // 左部項目をセット
        setLeftData(sheet, styleSheet);
        
        // セルの色情報を保持
        //getCellColor(sheet, styleSheet);
        
        // 年月をセット
        setDitailData(sheet, styleSheet);

        // セルに色情報を設定
        setCellColor(sheet, styleSheet, "");
        
        // スタイルコピー用シートを削除
        //workbook.removeSheetAt(1);

        // 非表示行の削除　削除後タイトルのセル結合が解除されるため削除前に範囲指定を保持する。
        if (delTagetRow != null && delTagetRow != 0) {
            PoiUtil.delRow(sheet, delTagetRow, 1);
            PoiUtil.delRow(styleSheet, delTagetRow, 1);
            List<Integer> mergedRegionList;
            for (Map.Entry<String, List<Integer>> e : mergedRegionMap.entrySet()) {
                mergedRegionList = e.getValue();
                sheet.addMergedRegion(new CellRangeAddress(mergedRegionList.get(0), mergedRegionList.get(1) - 1, mergedRegionList.get(2), mergedRegionList.get(2)));
            }
        }
        
        // 回収管理　年月開始行数の取得(最終行から-4した位置を回収管理行開始となるようにテンプレートを設定している)
        ymStartRow = sheet.getLastRowNum()-4;
        
        // 取扱店を書き込み
        String toriatsuNm = syuuekiUtils.getToriatsuName(dateilHeader.getAnkenEntity().getToriatsuNm());
        // 販売ルートを取得
        String salesRouteNm = StringUtil.isEmpty(dateilHeader.getAnkenEntity().getSalesRouteNm()) ? ""
                : dateilHeader.getAnkenEntity().getSalesRouteNm();
        // 取扱店 + SPACE(1) + 販売ルートを設定
        PoiUtil.setCellValue(PoiUtil.getCell(sheet, ymStartRow, 2), toriatsuNm + " " + salesRouteNm);

        // 回収管理　左部項目をセット
        setLeftDataKaisyu(sheet, styleSheet);
        
        // 回収管理　年月をセット
        setDitailDataKaisyu(sheet, styleSheet);
     
        // セルに色情報を設定
        setCellColor(sheet, styleSheet, "K");
        
        // 再計算
        sheet.setForceFormulaRecalculation(true);
        
        // シート名の設定
        String sheetName = StringUtils.defaultString(s004Bean.getOrderNo()) + "_" + Label.getValue(Label.excelSheetKikan);
        workbook.setSheetName(workbook.getSheetIndex(sheet), sheetName);
        
        // 操作ログへの書き込み
        // (案件一覧の一括出力処理から出力した場合はここでのログ書き込みは行わない)
        if (!s004Bean.isIsIkkatsuFlg()) {
            registOperationLog(DL_TPL);
        }
    }

    /**
     * 案件基本情報の取得
     */
    private void takeAnkenData() {
        dateilHeader.setAnkenId(s004Bean.getAnkenId());
        dateilHeader.setRirekiId(s004Bean.getRirekiId());
        dateilHeader.setRirekiFlg(s004Bean.getRirekiFlg());
        dateilHeader.findAnkenPk();
    }
    
    /**
     * ヘッダ情報のデータ埋め込み
     */
    private void setHeadData(Sheet sheet) throws Exception {
        
        Cell cell;
        String cellValueString;
        
        String orderNo = ""; 
        
        //// 出力日時取得
        Date now = sysdateFacade.getSysdate();
        //// 案件情報を取得
        SyuGeBukkenInfoTbl ankenEntity = dateilHeader.getAnkenEntity();
        
        ////// ヘッダ情報セット
        //// ログイン者名
        PoiUtil.setCellValue(sheet, 0, 2, loginUserInfo.getUserName());
        //// 現在日時
        PoiUtil.setCellValue(sheet, 0, 4, now);
        
        //2018/03/05 原価回収基準対応 ADD
        //// 売上基準
        PoiUtil.setCellValue(sheet, 0, 5, syuuekiUtils.getSalesClassLabelGenFull(dateilHeader.getAnkenEntity().getSalesClass(),dateilHeader.getAnkenEntity().getSalesClassGenka()));

        //// 案件番号
        PoiUtil.setCellValue(sheet, 1, 1, s004Bean.getAnkenId());
        //// 強制フラグ
        PoiUtil.setCellValue(sheet, 1, 4, "OFF");
        
        //// 注番
        cell = PoiUtil.getCell(sheet, 2, 0);
        cellValueString = (String)(PoiUtil.getCellValue(cell));
        
        if ("1".equals(ankenEntity.getAnkenFlg())) {
            orderNo = ankenEntity.getMainOrderNo();
        } else {
            orderNo = ankenEntity.getOrderNo();
        }
        
        // シート名の設定用にORDER_NOを保持
        s004Bean.setOrderNo(ankenEntity.getOrderNo());
        
        // 事業部を取得
        this.divisionCode = ankenEntity.getDivisionCode();
        
        cellValueString = StringUtils.replace(cellValueString, "#ORDER_NO#", orderNo);
        PoiUtil.setCellValue(cell, cellValueString);

        //// 案件名称
        cell = PoiUtil.getCell(sheet, 2, 3);
        cellValueString = (String)(PoiUtil.getCellValue(cell));
        cellValueString = StringUtils.replace(cellValueString, "#ANKEN_NAME#", ankenEntity.getAnkenName());
        PoiUtil.setCellValue(cell, cellValueString);
    
    }
    
    /**
     * ロスコン対象か？
     * @return true:ロスコン対象 false:ロスコン対象外
     */
    private boolean isLossconData() {
        return dateilHeader.isLossControlFlag();
    }
    
    /**
     * 左部項目のデータ埋め込み
     */
    private void setLeftData(Sheet sheet, Sheet styleSheet) throws Exception {

        int row;      
        
        //int wkRow = DATA_START_COL;
        int startRow = DATA_START_ROW;
        int wkRow = startRow;

        int startRow1 = 0;
        int startRow2 = 0;
        
        // 初期化
        headStartMap = new HashMap<>();
        mergedRegionMap = new HashMap<>();

        // (2018A)円貨(通貨JPY)のみの場合、契約・売上・回収の各通貨は小数点を表示しないようにする
        // そのために整数用フォーマットを用意する
        DataFormat intCellformat = styleSheet.getWorkbook().createDataFormat();

        // 検索条件セット
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("rirekiFlg", (s004Bean.getRirekiFlg()));
        // 通貨種類の取得        
        currencyList = downloadFacade.selectCurrencyList(paramMap);
        
        // 売上原価種類の取得
        cateTitleList = downloadFacade.selectCateTitleList(paramMap);
        
        //// (2018B)受注レートの通貨を埋め込んで、行を確保
        wkRow = setJyuchuCurrencyCode(sheet, wkRow, currencyList, styleSheet, intCellformat);
        // 余分な行を削除(1行分)
        PoiUtil.delRow(sheet, wkRow, 1);
        PoiUtil.delRow(styleSheet, wkRow, 1);

        //// (2018B)受注SPの通貨を埋め込んで、行を確保
        wkRow = setJyuchuCurrencyCode(sheet, wkRow, currencyList, styleSheet, intCellformat);
        // 余分な行を削除(1行分)
        PoiUtil.delRow(sheet, wkRow, 1);
        PoiUtil.delRow(styleSheet, wkRow, 1);
        
         //// (2018B)[受注SP]カテゴリのセル結合
        sheet.addMergedRegion(new CellRangeAddress(startRow, wkRow, 0, 1));
        
        // (2018B)契約金額の行に移動(受注SPの最終行からの加算行数を指定しています)
        wkRow = wkRow + 6;

        

        //// 契約金額：建値額
        // 罫線を描画（上部）
        headStartMap.put("keiyakuTatene", wkRow);
        row = wkRow;
        startRow1 = wkRow;
        //wkRow = setCurrencyCode2(sheet, wkRow, currencyList, "補正", styleSheet);
        wkRow = setCurrencyCode2(sheet, wkRow, currencyList, "補正", styleSheet, intCellformat);
        // 余分な行を削除(2行分)
        PoiUtil.delRow(sheet, wkRow, 2);
        PoiUtil.delRow(styleSheet, wkRow, 2);   
        // セルの結合 ※削除行を含むため最後に結合する
//      sheet.addMergedRegion(new CellRangeAddress(row, wkRow-1, 2, 2));
        mergedRegionMap.put("keiyakuTateneMergedRegion", Arrays.asList(row, wkRow-1, 2));

        //// 契約金額：契約為替レート
        headStartMap.put("keiyakuKawase", wkRow);
        row = wkRow;
        //wkRow = setCurrencyCode(sheet, wkRow, currencyList, styleSheet);
        wkRow = setCurrencyCode(sheet, wkRow, currencyList, styleSheet, null);
        // 余分な行を削除(1行分)
        PoiUtil.delRow(sheet, wkRow, 1);
        PoiUtil.delRow(styleSheet, wkRow, 1);   
        // セルの結合
        sheet.addMergedRegion(new CellRangeAddress(row, wkRow-1, 2, 2));
        
        
        //// 契約金額：円価
        headStartMap.put("keiyakuEnka", wkRow);
        row = wkRow;
        //wkRow = setCurrencyCode(sheet, wkRow, currencyList, styleSheet);
        wkRow = setCurrencyCode(sheet, wkRow, currencyList, styleSheet, null);
        // 余分な行を削除(1行分)
        PoiUtil.delRow(sheet, wkRow, 1); 
        PoiUtil.delRow(styleSheet, wkRow, 1);   
        // セルの結合
        sheet.addMergedRegion(new CellRangeAddress(row, wkRow-1, 2, 2));
        
        
        //// 契約金額：合計　
        headStartMap.put("keiyakuSum", wkRow);
        wkRow++; //行加算
        
        //// 契約金額セルの結合
        sheet.addMergedRegion(new CellRangeAddress(startRow1, wkRow-1, 0, 1));
        
        
        //// 見積総原価
        headStartMap.put("mitsumoriGenka", wkRow);
        
        //// 見積総原価：未発番ＮＥＴ＆製番損益
        // セルに名前を設定
        PoiUtil.setCellName(sheet, "_HAT_NET" , wkRow, 2 );
        PoiUtil.setCellName(sheet, "_MI_HAT_NET" , wkRow+1, 2 );
        PoiUtil.setCellName(sheet, "_SEIBAN_SONEKI" , wkRow+2, 2 );
        PoiUtil.setCellName(sheet, "_KAWASE_EIKYO" , wkRow+3, 2 );
        
        wkRow+=4; //行加算
        //// 見積総原価：合計　
        headStartMap.put("mitsumoriSum", wkRow);
        wkRow++; //行加算
        //// 契約/見積総原価
        headStartMap.put("keiyakuMitsumoriSum", wkRow);
        wkRow++; //行加算

        //// 売上高：今回：建値額
        headStartMap.put("uriKonkai", wkRow);
        row = wkRow;
        startRow1 = wkRow;
        //wkRow = setCurrencyCode(sheet, wkRow, currencyList, styleSheet);
        wkRow = setCurrencyCode(sheet, wkRow, currencyList, styleSheet, intCellformat);
        // 余分な行を削除(1行分)
        PoiUtil.delRow(sheet, wkRow, 1);   
        PoiUtil.delRow(styleSheet, wkRow, 1);   
        // セルの結合
        sheet.addMergedRegion(new CellRangeAddress(row, wkRow-1, 2, 2));
        
        
        //// 売上高：今回：売上為替レート
        row = wkRow;
        //wkRow = setCurrencyCode(sheet, wkRow, currencyList, styleSheet);
        wkRow = setCurrencyCode(sheet, wkRow, currencyList, styleSheet, null);
        // 余分な行を削除(1行分)
        PoiUtil.delRow(sheet, wkRow, 1);
        PoiUtil.delRow(styleSheet, wkRow, 1);   
        // セルの結合
        sheet.addMergedRegion(new CellRangeAddress(row, wkRow-1, 2, 2));
        
        //// 売上高：今回：円価
        row = wkRow;
        //wkRow = setCurrencyCode2(sheet, wkRow, currencyList, labelKawase, styleSheet);
        wkRow = setCurrencyCode2(sheet, wkRow, currencyList, labelKawase, styleSheet, null);
        // 余分な行を削除(2行分)
        PoiUtil.delRow(sheet, wkRow, 2);
        PoiUtil.delRow(styleSheet, wkRow, 2);   
        // セルの結合 ※削除行を含むため最後に結合する
        mergedRegionMap.put("uriKonkaiMergedRegion", Arrays.asList(row, wkRow-1, 2));
        
        
        //// 売上高：今回：合計
        headStartMap.put("uriKonkaiSum", wkRow);
        wkRow++; //行加算

        //// 売上高：今回セルの結合 ※削除行を含むため最後に結合する
        mergedRegionMap.put("uriKonkaiSumMergedRegion", Arrays.asList(startRow1, wkRow-1, 1));
        
        //// 売上高：累計：建値額
        headStartMap.put("uriRuikei", wkRow);
        row = wkRow;
        startRow2 = wkRow;
//wkRow = setCurrencyCode(sheet, wkRow, currencyList, styleSheet);
wkRow = setCurrencyCode(sheet, wkRow, currencyList, styleSheet, intCellformat);
        // 余分な行を削除(1行分)
        PoiUtil.delRow(sheet, wkRow, 1);
        PoiUtil.delRow(styleSheet, wkRow, 1);   
        // セルの結合
        sheet.addMergedRegion(new CellRangeAddress(row, wkRow-1, 2, 2));
        
        
        //// 売上高：累計：円価
        row = wkRow;
//wkRow = setCurrencyCode(sheet, wkRow, currencyList, styleSheet);
wkRow = setCurrencyCode(sheet, wkRow, currencyList, styleSheet, null);
        // 余分な行を削除(1行分)
        PoiUtil.delRow(sheet, wkRow, 1);
        PoiUtil.delRow(styleSheet, wkRow, 1);   
        // セルの結合
        sheet.addMergedRegion(new CellRangeAddress(row, wkRow-1, 2, 2));
        
        
        //// 売上高：累計：合計
        headStartMap.put("uriRuikeiSum", wkRow);
        wkRow++; //行加算

        //// 売上高：累計セルの結合
        sheet.addMergedRegion(new CellRangeAddress(startRow2, wkRow-1, 1, 1));
        
        //// 売上高セルの結合　※削除行を含むため最後に結合する
        mergedRegionMap.put("uriagetakaMergedRegion", Arrays.asList(startRow1, wkRow-1, 0));
        
        //// 売上原価：今回：カテゴリ
        headStartMap.put("uriGenka", wkRow);
        row = wkRow;
        startRow1 = wkRow;
        wkRow = setCateTitle(sheet, wkRow, cateTitleList, styleSheet);
        // 余分な行を削除(1行分)
        PoiUtil.delRow(sheet, wkRow, 1);
        PoiUtil.delRow(styleSheet, wkRow, 1);   
        // セルの結合
        sheet.addMergedRegion(new CellRangeAddress(row, wkRow-1, 1, 1));
        
        //// 売上原価：今回：合計
        headStartMap.put("uriGenkaKonkai", wkRow);
        wkRow++; //行加算
        
        //// 売上原価：累計：合計
        headStartMap.put("uriGenkaRuikei", wkRow);
        wkRow++; //行加算

        //// 売上原価セルの結合
        sheet.addMergedRegion(new CellRangeAddress(startRow1, wkRow-1, 0, 0));

        // 粗利
        headStartMap.put("arari", wkRow);
        wkRow+=2; //行加算
        
        // M率
        headStartMap.put("mritsu", wkRow);
        wkRow+=2; //行加算
    
        //// (2018A)ロスコン引当欄
        // ロスコン対象外の場合、ロスコン引当に対応する7行分を削除する。
        if (!isLossconData()) {
            PoiUtil.delRow(sheet, wkRow, 7);
            PoiUtil.delRow(styleSheet, wkRow, 7);
        }
    }
    
    
    /**
     * 実績・見込・四半期の色情報を別シートに保持
     * @param sheet
     * @param styleSheet
     * @throws Exception 
     */
    /*
    private void getCellColor(Sheet sheet, Sheet styleSheet) throws Exception {
        
        // 列指定でコピー　値と計算式は含まない
        PoiUtil.copyColumnStyleValue(styleSheet, JISSEKI_STYLE_COL, sheet, JISSEKI_STYLE_COL, 0);
        PoiUtil.copyColumnStyleValue(styleSheet, MIKOMI_STYLE_COL, sheet, MIKOMI_STYLE_COL, 0);
        PoiUtil.copyColumnStyleValue(styleSheet, QUARTER_STYLE_COL, sheet, QUARTER_STYLE_COL, 0);
        
    }
    */
     
    /**
     * セル色の設定
     * @param sheet
     * @param styleSheet
     * @throws Exception 
     */
    private void setCellColor(Sheet sheet, Sheet styleSheet, String kFlg) throws Exception {
        
        // 初期設定として実績のスタイル位置を指定
        int col = JISSEKI_STYLE_COL;
        
        //for (int colIdx=jissekiCol+4; colIdx<lastCol; colIdx++) {
        for (int colIdx=5; colIdx<lastCol; colIdx++) {
                      
            // 対象列の年月を取得
            Cell cellYm = PoiUtil.getCell(sheet, ymStartRow, colIdx);
            String strYm = (String)(PoiUtil.getCellValue(cellYm));
            
            // 末尾１文字が文字の場合は"四半期"or"期"
            boolean quarterFlg = false;
            if (StringUtils.isNotEmpty(strYm)) {
                quarterFlg = !Utils.isNumeric(strYm.substring(strYm.length()-1));
            }
                
            // 勘定月から見込のスタイルを使用
            // 勘定月以前は四半期でも実績のスタイルを使用
            if (kanjoYm.compareTo(strYm.replaceAll("/", "")) == 0) {
                col = MIKOMI_STYLE_COL;
            // 勘定月以上で四半期フラグがtrueの場合は、四半期のスタイルを使用
            } else if (col != JISSEKI_STYLE_COL && quarterFlg) {
                col = QUARTER_STYLE_COL;
            // 勘定月以上で四半期フラグがfalseの場合は、見込のスタイルを使用    
            } else if (col != JISSEKI_STYLE_COL && !quarterFlg) {
                col = MIKOMI_STYLE_COL;
            }
            
            // 列指定でコピー
            if(kFlg.equals("")){
                PoiUtil.copyColumnStyleValue(sheet, colIdx, styleSheet, col, 0);
            }else{
                // 回収管理用に追加
                PoiUtil.copyColumnStyleValue(sheet, colIdx, styleSheet, col, ymStartRow, 0);
            }
            
        }
    }

    /**
     * (2018A)通貨が円貨(JPY)しかない場合、各通貨の契約金額・売上金額・回収金額の小数点2桁表記を解除する
     * @param cell
     * @param intCellformat 
     */
    private void setCurrencyEnkaMoneyFormat(Cell cell, DataFormat intCellformat) {
        if (intCellformat != null) {
            CellStyle cellStyle = cell.getCellStyle();
            cellStyle.setDataFormat(intCellformat.getFormat("#,##0"));
            PoiUtil.setCellValue(cell, null, cellStyle);
        }
    }

    /**
     * (2018A)通貨が円貨(JPY)しかない場合、各通貨の契約金額・売上金額・回収金額の小数点2桁表記を解除する(回収金額用)
     * @param cell
     * @param intCellformat 
     */
    private void setCurrencyKaisyuEnkaMoneyFormat(Cell cell, DataFormat intCellformat) {
        if (intCellformat != null) {
            // 回収金額は、契約SPや売上SPとは独立して円貨表記を行うため、スタイルオブジェクトを新規作成したうえで行う(そうしないと契約SPや売上SPのセルにも影響を及ぼしてしまうため)
            CellStyle newStyle = cell.getSheet().getWorkbook().createCellStyle();
            newStyle.cloneStyleFrom(cell.getCellStyle());
            newStyle.setDataFormat(intCellformat.getFormat("#,##0"));
            PoiUtil.setCellValue(cell, null, newStyle);
        }
    }

    /**
     * 契約/売上SPが円貨(JPY)通貨のみであるかをチェック
     */
    private boolean isEnkaOnly(List<S004DownloadEntity> currencyList) {
        boolean isEnka = true;
        for (int idx=0; idx<currencyList.size(); idx++ ) {
            String currencyCode =  currencyList.get(idx).getCurrencyCode();
            if (!ConstantString.currencyCodeEn.equals(currencyCode)) {
                isEnka = false;
                break;
            }
        }
        return isEnka;
    }

    /**
     * 受注情報/通貨コードの埋め込み
     * 仮ロジック
     */
    private int setJyuchuCurrencyCode(Sheet sheet, int wkRow, List<S004DownloadEntity> currencyList, Sheet styleSheet, DataFormat intCellformat) {
        Cell cell;
        if (currencyList == null || currencyList.isEmpty()) {
            wkRow+=1;
        } else {
            // 円貨(JPY)通貨のみであるかをチェック
            boolean isEnka = isEnkaOnly(currencyList);
            
            for (int idx=0; idx<currencyList.size(); idx++ ) {
                String currencyCode =  currencyList.get(idx).getCurrencyCode();
                // 2行目以降は行追加（Excel計算式保持のため）
                if (idx!=0) {
                    sheet.shiftRows(wkRow, sheet.getLastRowNum(), 1);
                    sheet.createRow(wkRow);
                    styleSheet.shiftRows(wkRow, styleSheet.getLastRowNum(), 1);
                    styleSheet.createRow(wkRow);
                    
                    // 行のコピー
                    PoiUtil.copyRowStyleValue(sheet.getRow(wkRow), sheet.getRow(wkRow+1), true);
                    PoiUtil.copyRowStyleValue(styleSheet.getRow(wkRow), styleSheet.getRow(wkRow+1), true);
                }
                // 通貨コードをセット
                cell = PoiUtil.getCell(sheet, wkRow, 3);
                PoiUtil.setCellValue(cell, currencyCode);
                
                // (2018A)通貨が円貨(JPY)しか存在しない場合、小数点2桁を表示しないようにセルの書式設定を変更する
                // (スタイル用シート[kikanS_style]のF,G,H列にそれぞれ実績,見込,期(Q)合計要のテンプレートを用意しているため、この列に対してセットする)
                // 5列目:実績月用列 6列目:見込月用列 7列目:4半期/期合計要列
                if (isEnka) {
                    for (int i=5; i<=7; i++) {
                        cell = PoiUtil.getCell(styleSheet, wkRow, i);
                        setCurrencyEnkaMoneyFormat(cell, intCellformat);
                    }
                }

                wkRow++; //行加算
            }
        }
        
        return wkRow;
    }
    
    
    /**
     * 通貨コードの埋め込み
     */
    //private int setCurrencyCode(Sheet sheet, int wkRow, List<S004DownloadEntity> currencyList, Sheet styleSheet) {
    private int setCurrencyCode(Sheet sheet, int wkRow, List<S004DownloadEntity> currencyList, Sheet styleSheet, DataFormat intCellformat) {
        Cell cell;

        if (!currencyList.isEmpty()) {
            // 円貨(JPY)通貨のみであるかをチェック
            boolean isEnka = isEnkaOnly(currencyList);
            
            for (int idx=0; idx<currencyList.size(); idx++ ) {
                String currencyCode =  currencyList.get(idx).getCurrencyCode();
                // 2行目以降は行追加（Excel計算式保持のため）
                if (idx!=0) {
                    sheet.shiftRows(wkRow, sheet.getLastRowNum(), 1);
                    sheet.createRow(wkRow);
                    styleSheet.shiftRows(wkRow, styleSheet.getLastRowNum(), 1);
                    styleSheet.createRow(wkRow);
                    
                    // 行のコピー
                    PoiUtil.copyRowStyleValue(sheet.getRow(wkRow), sheet.getRow(wkRow+1), true);
                    PoiUtil.copyRowStyleValue(styleSheet.getRow(wkRow), styleSheet.getRow(wkRow+1), true);
                }
                // 通貨コードをセット
                cell = PoiUtil.getCell(sheet, wkRow, 3);
                PoiUtil.setCellValue(cell, currencyCode);
                
                // (2018A)通貨が円貨(JPY)しか存在しない場合、小数点2桁を表示しないようにセルの書式設定を変更する
                // (スタイル用シート[kikanS_style]のF,G,H列にそれぞれ実績,見込,期(Q)合計要のテンプレートを用意しているため、この列に対してセットする)
                // 5列目:実績月用列 6列目:見込月用列 7列目:4半期/期合計要列
                if (isEnka) {
                    for (int i=5; i<=7; i++) {
                        cell = PoiUtil.getCell(styleSheet, wkRow, i);
                        setCurrencyEnkaMoneyFormat(cell, intCellformat);
                    }
                }

                wkRow++; //行加算
            }
        } else {
            wkRow+=1;
        }
        
        return wkRow;
    }

    /**
     * 通貨コードの埋め込み2
     */
    //private int setCurrencyCode2(Sheet sheet, int wkRow, List<S004DownloadEntity> currencyList, String label, Sheet styleSheet) {
    private int setCurrencyCode2(Sheet sheet, int wkRow, List<S004DownloadEntity> currencyList, String label, Sheet styleSheet, DataFormat intCellformat) {
        Cell cell;
        
        if (!currencyList.isEmpty()) {
            // 円貨(JPY)通貨のみであるかをチェック
            boolean isEnka = isEnkaOnly(currencyList);

            for (int idx=0; idx<currencyList.size(); idx++ ) {
                String currencyCode =  currencyList.get(idx).getCurrencyCode();
                // 2行目以降は行追加（Excel計算式保持のため）
                if (idx!=0) {
                    sheet.shiftRows(wkRow, sheet.getLastRowNum(), 2);
                    sheet.createRow(wkRow);
                    sheet.createRow(wkRow+1);
                    styleSheet.shiftRows(wkRow, styleSheet.getLastRowNum(), 2);
                    styleSheet.createRow(wkRow);
                    styleSheet.createRow(wkRow+1);
                    // 行のコピー
                    PoiUtil.copyRowStyleValue(sheet.getRow(wkRow), sheet.getRow(wkRow+2), true);
                    PoiUtil.copyRowStyleValue(sheet.getRow(wkRow+1), sheet.getRow(wkRow+3), true);
                    PoiUtil.copyRowStyleValue(styleSheet.getRow(wkRow), styleSheet.getRow(wkRow+2), true);
                    PoiUtil.copyRowStyleValue(styleSheet.getRow(wkRow+1), styleSheet.getRow(wkRow+3), true);
                }
                // 通貨コードをセット
                cell = PoiUtil.getCell(sheet, wkRow, 3);
                PoiUtil.setCellValue(cell, currencyCode);

                // (2018A)通貨が円貨(JPY)しか存在しない場合、小数点2桁を表示しないようにセルの書式設定を変更する
                // (スタイル用シート[kikanS_style]のF,G,H列にそれぞれ実績,見込,期(Q)合計要のテンプレートを用意しているため、この列に対してセットする)
                // 5列目:実績月用列 6列目:見込月用列 7列目:4半期/期合計要列
                if (isEnka) {
                    for (int i=5; i<=7; i++) {
                        cell = PoiUtil.getCell(styleSheet, wkRow, i);
                        setCurrencyEnkaMoneyFormat(cell, intCellformat);
                    }
                }

                wkRow++; //行加算
                
                // 通貨JPYの場合、売上高・今回・円貨・為替差調整の行は表示しない(高さ0にしておく)S
                if (label.equals(labelKawase) && jpUnit.equals(currencyCode)) {
// 非表示でなく行削除
//                  Row row = PoiUtil.getRow(sheet, wkRow);
//                  row.setHeightInPoints(0);
                    delTagetRow = wkRow;
                }

                // 通貨コードをセット
                cell = PoiUtil.getCell(sheet, wkRow, 3);
                PoiUtil.setCellValue(cell, currencyCode);
                // labelが補正のときだけセルに名前を設定
                if ("補正".equals(label)) {
                    PoiUtil.setCellName(sheet, "_" + currencyCode, wkRow, 3 );
                }

                cell = PoiUtil.getCell(sheet, wkRow, 4);
                PoiUtil.setCellValue(cell, label);
                
                // (2018A)通貨が円貨(JPY)しか存在しない場合、小数点2桁を表示しないようにセルの書式設定を変更する
                // (スタイル用シート[kikanS_style]のF,G,H列にそれぞれ実績,見込,期(Q)合計要のテンプレートを用意しているため、この列に対してセットする)
                // 5列目:実績月用列 6列目:見込月用列 7列目:4半期/期合計要列
                if (isEnka) {
                    for (int i=5; i<=7; i++) {
                        cell = PoiUtil.getCell(styleSheet, wkRow, i);
                        setCurrencyEnkaMoneyFormat(cell, intCellformat);
                    }
                }

                wkRow++; //行加算
            }
            
        } else {
            wkRow+=2;
        }
        
        return wkRow;
    }
    
    /**
     * 売上原価カテゴリの埋め込み
     */
    private int setCateTitle(Sheet sheet, int wkRow, List<Cost> cateTitleList, Sheet styleSheet) {
        
        Cell cell;
        
        if (!cateTitleList.isEmpty()) {
        
            for (int idx=0; idx<cateTitleList.size(); idx++ ) {
                
                String categoryName1 = cateTitleList.get(idx).getCategoryName1();
                String categoryName2 = cateTitleList.get(idx).getCategoryName2();
                String categoryCode  = cateTitleList.get(idx).getCategoryCode();
                String categoryKbn1  = cateTitleList.get(idx).getCategoryKbn1();
                String categoryKbn2  = cateTitleList.get(idx).getCategoryKbn2();
                
                // 2行目以降は行追加（Excel計算式保持のため）
                if (idx!=0) {
                    sheet.shiftRows(wkRow, sheet.getLastRowNum(), 1);
                    sheet.createRow(wkRow);
                    styleSheet.shiftRows(wkRow, styleSheet.getLastRowNum(), 1);
                    styleSheet.createRow(wkRow);
                    // 行のコピー
                    PoiUtil.copyRowStyleValue(sheet.getRow(wkRow), sheet.getRow(wkRow+1), true);
                    PoiUtil.copyRowStyleValue(styleSheet.getRow(wkRow), styleSheet.getRow(wkRow+1), true);
                }
                // 売上原価カテゴリをセット
                cell = PoiUtil.getCell(sheet, wkRow, 2);
                PoiUtil.setCellValue(cell, categoryName1);
                cell = PoiUtil.getCell(sheet, wkRow, 3);
                PoiUtil.setCellValue(cell, categoryName2);
                // セルに名前を設定
                // 単体だと名前が被るので、各コードを"￥"で繋いでセットする
                PoiUtil.setCellName(sheet, "_" + categoryCode + "￥" + categoryKbn1 + "￥" + categoryKbn2, wkRow, 2 );
                                
                wkRow++; //行加算
            }
        } else {
            wkRow+=1;
        }
        
        return wkRow;
    }

    /**
     * 年月、ラベル（実績or見込or完売））のセット（年月、ラベル（実績or見込or完売））
     * @param sheet
     * @param nengetsu
     * @param label
     * @param col
     * @param boolSetName
     * @throws Exception 
     */
    private void setYmData(Sheet sheet, String nengetsu, String label, int col, boolean boolSetName, String kaisyuStr) throws Exception {
        
        Cell cell;
        
        // 年月をセット
        cell = PoiUtil.getCell(sheet, ymStartRow, col);
        PoiUtil.setCellValue(cell, nengetsu);
        if (boolSetName) {
            // セルに名前を設定
            PoiUtil.setCellName(sheet, "_" + nengetsu.replaceAll("/", "") + kaisyuStr, ymStartRow, col );
        }
        // 実績or見込みをセット
        cell = PoiUtil.getCell(sheet, ymStartRow+1, col);
        PoiUtil.setCellValue(cell, label);
    }
    
    /**
     * 年月を四半期or期のキーに変換
     */
    private String getQuaterKiToKey(String syuekiYm, int kbn) throws ParseException {
        String result = StringUtils.replace(syuekiYm, "/", "");
        if (kbn == 0) {
            // 四半期
            result = result + "Q";
        } else {
            // 期
            Date data = Utils.parseDate(result);
            result = SyuuekiUtils.dateToKikan(data);
        }
        return result;
    }

    /**
     * (2018A)ロスコン開始年月を取得
     * @throws SQLException
     */
    private String getLossStartYm() throws SQLException {

        String lossStartYm = "";
        if (isLossconData()) {
            lossStartYm = downloadFacade.selectLossTargetYm(s004Bean.getAnkenId(), (new Integer(s004Bean.getRirekiId())), kanjoYm, s004Bean.getRirekiFlg());
        // ロスコン判定された月の4半期月をロスコン開始年月とする。→ なし、DBに登録されている年月そのままをロスコン判定月とする(GAIA取込で4半期月にセットはされるはず)
//            String[] ymArray = SyuuekiUtils.getQuarterMonthAry(lossStartYm);
//            if (ymArray != null) {
//                lossStartYm = ymArray[ymArray.length-1];
//            }
        }
        return StringUtils.defaultString(lossStartYm);
    }
    
    /**
     * 年月データの埋め込み
     */
    private void setDitailData(Sheet sheet, Sheet styleSheet) throws Exception {
        
        // 見込初月フラグ
        firstMikomiFlg = 0;
        // 完売月フラグ
        kanbaiFlg = 0;
        // 実績の範囲
        jissekiCol = 0;
        
        // 前月セルの位置補正用（四半期表示有で使用）
        colBefMonth = 0;
        // 過去３ヵ月SUM用の変数を初期化（四半期表示有で使用）
        cntQuarterSum = 0;
        // 過去６ヵ月SUM用の変数を初期化（四半期表示有で使用）
        colQuarterSum1 = null;
        colQuarterSum2 = null;
        colQuarterSum3 = null;
        colQuarterSum4 = null;
        
        String label = "";
        
        // 年月描画開始位置
        int col = YM_START_COL;       
        
        String dataKbn = "";
        
        // 検索条件セット
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("rirekiFlg", (s004Bean.getRirekiFlg()));

        // 年月を取得
        dateilHeader.setAnkenId(s004Bean.getAnkenId());
        dateilHeader.setRirekiId(s004Bean.getRirekiId());
        dateilHeader.setRirekiFlg(s004Bean.getRirekiFlg());
        //List<String> nengetsuList = dateilHeader.findNengetsuList();
        List<String> nengetsuList = dateilHeader.findNengetsuListKikanShinkoDownload();
               
        // 勘定月の取得(履歴を参照している場合は、履歴テーブルの勘定月を取得)
        //kanjoYm = kanjyoMstFacade.getNowKanjoDate();
        kanjoYm = StringUtils.replace(syuuekiUtils.exeFormatYm(dateilHeader.getKanjoDate()), "/", "");

        // ロスコン対象年月の取得(1回だけ取得すればよいので年月ループ外で取得する)
        String lossStartYm = getLossStartYm();
        
        if (!nengetsuList.isEmpty()) {           
            for(int idx=0; idx<nengetsuList.size() - 1; idx++){
                String nengetsu = nengetsuList.get(idx);

                // 実績or見込みの判定
                boolean jissekiFlg = syuuekiUtils.getJissekiFlg(Utils.parseDate(kanjoYm), nengetsu);
                if (jissekiFlg) {
                    label = Label.getValue(Label.jisseki);
                    dataKbn = "J";
                    jissekiCol++;
                } else {
                    label = Label.getValue(Label.mikomi);
                    dataKbn = "M";
                    firstMikomiFlg++;
                }
                
                // 開始年月列からセルスタイル(計算式のみ)をコピー
                // データ描画最初の列の場合はコピーしない
                if (idx > 0) {
                    //PoiUtil.copyColumnStyleValue(sheet, col, sheet, col-1, 2);
                    PoiUtil.copyColumnStyleValue(sheet, col, sheet, YM_START_COL, 2);
                    // 横幅を１つ前の列と同じにする
                    sheet.setColumnWidth(col, sheet.getColumnWidth(col-1));
                }

                // 年月項目をセット
                setYmData(sheet, nengetsu, label, col, true, "");
                // 月別金額をセット
                setMoneyData(sheet, dataKbn, nengetsu, col, 0, lossStartYm, styleSheet);
        
                col++;
                cntQuarterSum++;   // 四半期表示で使用
                
                colBefMonth = 0;
                //// 四半期列作成処理
                // 四半期表示フラグが１かつ四半期月の場合に行う
                if ("1".equals(dateilHeader.getQuarterDispFlg()) && isQuarter(nengetsu.replaceAll("/", ""))) {
                    col = createQuarterCol(sheet, dataKbn, nengetsu, label, col);
                }
                
            }
            
            // セルスタイル(計算式のみ)をコピー
            //PoiUtil.copyColumnStyleValue(sheet, col, sheet, col-1, 2);
            PoiUtil.copyColumnStyleValue(sheet, col, sheet, YM_START_COL, 2);
            // 横幅を１つ前の列と同じにする
            sheet.setColumnWidth(col, sheet.getColumnWidth(col-1));
            // 最終月のラベルは"完売"
            String nengetsu = nengetsuList.get(nengetsuList.size() - 1);
            if(syuuekiUtils.getJissekiFlg(Utils.parseDate(kanjoYm), nengetsu)) {
                dataKbn = "J";
            } else {
                dataKbn = "M";
            }
            
            // 年月項目をセット
            setYmData(sheet, nengetsu, "完売", col, true, "");
            
            kanbaiFlg = 1;
            // 月別金額をセット
            setMoneyData(sheet, dataKbn, nengetsu, col, 1, lossStartYm, styleSheet);
            
            cntQuarterSum++;   // 四半期表示で使用
            col++;
            colBefMonth = 0;
            
            //// 四半期列作成処理
            // 四半期表示フラグが１の場合に行う
            if ("1".equals(dateilHeader.getQuarterDispFlg())) {
                col = createQuarterCol(sheet, dataKbn, nengetsu, label, col);
            }
            
            lastCol = col;
        }
    }
    
    /**
     * 四半期、期のデータをセット
     * @param sheet
     * @param dataKbn
     * @param col
     * @return
     * @throws Exception 
     */
    private int createQuarterCol(Sheet sheet, String dataKbn, String nengetsu, String label, int col) throws Exception {
        // 前列のセルスタイル(計算式のみ)をコピー
        //PoiUtil.copyColumnStyleValue(sheet, col, sheet, col-1, 2);
        PoiUtil.copyColumnStyleValue(sheet, col, sheet, YM_START_COL, 2);
        // 横幅を１つ前の列と同じにする
        sheet.setColumnWidth(col, sheet.getColumnWidth(col-1));

        // 年月項目をセット
        setYmData(sheet, getQuarterLabel(nengetsu, 1), label, col, false, "");

        colBefMonth++;
        // 四半期分金額をセット
        setQuarterData(dataKbn, nengetsu, sheet, col, 0);
        // 過去３ヵ月SUM用変数を初期化
        cntQuarterSum = 0; 

        col++;

        // ９or３月もしくは完売月のときは、期列を追加
        if (isKi(nengetsu.replaceAll("/", "")) || kanbaiFlg == 1) {
            // 前列のセルスタイル(計算式のみ)をコピー
            //PoiUtil.copyColumnStyleValue(sheet, col, sheet, col-1, 2);
            PoiUtil.copyColumnStyleValue(sheet, col, sheet, YM_START_COL, 2);
            // 横幅を１つ前の列と同じにする
            sheet.setColumnWidth(col, sheet.getColumnWidth(col-1));    

            // 年月項目をセット
            setYmData(sheet, getQuarterLabel(nengetsu, 0), label, col, false, "");

            colBefMonth++;
            // 四半期分金額をセット
            setQuarterData(dataKbn, nengetsu, sheet, col, 1);
            // 過去６ヵ月SUM用変数を初期化
            colQuarterSum1 = null;
            colQuarterSum2 = null;
            colQuarterSum3 = null;
            colQuarterSum4 = null;

            col++;           
        }

        return col;
    }
    
    
    /**
     * 月別金額データの埋め込み
     */
    private void setMoneyData(Sheet sheet, String dataKbn, String nengetsu, int col, int kanbaiMonthFlg, String lossStartYm, Sheet styleSheet) throws Exception {
        String targetNengetsu = nengetsu.replace("/", "");
        // 検索条件セット
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("rirekiFlg", s004Bean.getRirekiFlg());
        paramMap.put("dataKbn", dataKbn);
        paramMap.put("syuekiYm", targetNengetsu);
        paramMap.put("kanbaiMonthFlg", kanbaiMonthFlg);
        paramMap.put("lossStartYm", lossStartYm);
        
        // 通貨金額データの埋め込み
        setDitailCurrency(sheet, paramMap, col, styleSheet);

        // 見積総原価データの埋め込み
        setDitailSogenka(sheet, paramMap, col);
        
        // 売上原価カテゴリデータの埋め込み
        setDitailCate(sheet, paramMap, col);
        
        // (2018A)ロスコン補正・引当情報データの埋め込み  ※ロスコン対象の案件の場合のみ実施
        if (isLossconData()) {
            // 原価回収基準の場合
            if (("1").equals(this.dateilHeader.getAnkenEntity().getSalesClassGenka())) {
                setLossconInfoGenkaData(sheet, paramMap, col);
            // 進行基準の場合
            } else {
                setLossconInfoShinkoData(sheet, paramMap, col);
            }
        }
    }
    
    /**
     * (2018A)前回の4半期月のセル位置を取得
     */
    private int getQuarterMonthMinusCount(String _month) {
        if (StringUtils.isEmpty(_month)) {
            return 0;
        }
        
        int minusCount = 0;
        // 必ず3月分の配列(3配列)が取得できるので、どの月と等しいかで、マイナスするセル数を決定する(例えば4月であれば1セル分マイナス 5月であれば2セル分マイナス 6月であれば3セル分マイナスとする)
        String[] quarterMonthAry = SyuuekiUtils.getQuarterMonthAry(_month);
        if (quarterMonthAry == null) {
            return 0;
        }
        for (String targetMonth: quarterMonthAry) {
            minusCount = minusCount + 1;
            if (targetMonth.equals(_month)) {
                break;
            }
        }

        // 4半期合計や期合計を出力するかどうか状況を判断して、更に差し引くセル数を決定
        int qkMinusCount = 0;
        if ("1".equals(dateilHeader.getQuarterDispFlg())) {
            String month = StringUtils.substring(_month, 4);
            if ((month.compareTo("07") >= 0 && month.compareTo("09") <= 0) || (month.compareTo("01") >= 0 && month.compareTo("03") <= 0)) {
                // 9月や3月の場合は、前4半期月は6月や12月になり、間に期合計が挟まっていないため、Qセル分(1セル分)をマイナスする。
                qkMinusCount = 1;
            } else if ((month.compareTo("04") >= 0 && month.compareTo("06") <= 0) || (month.compareTo("10") >= 0 && month.compareTo("12") <= 0)) {
                // 6月や12月の場合は、前4半期月は3月や9月になり、間に期合計が挟まっている。Qセル分に加え、さらに期合計分のセル(1セル分)を更にマイナスする(合計2セル分)。
                qkMinusCount = 2;
            }
            minusCount = minusCount + qkMinusCount;
        }
        
        return minusCount;
    }
    
    /**
     * ロスコンデータ登録の対象行(Cell)を取得
     */
    private Cell getLossStartRowCell(Sheet sheet) throws Exception {
        // ロスコン情報対象行の取得(テンプレートファイル内のセル名称が"_LOSSCON"としているので、その行がロスコン情報開始のセルとなる) → 2018/07/10 セル名称を付与していると、他ダウンロード機能で影響があったのでプログラムからセル行番号取得に修正
        //return PoiUtil.searchCellName(sheet, "_LOSSCON");
        Integer lossStartRow = (Integer)headStartMap.get("uriGenkaKonkai") + 6;
        logger.info("lossStartRow=" + lossStartRow);
        Cell cell = PoiUtil.getCell(sheet, lossStartRow, 0);
        return cell;
    }
    
    /**
     * ロスコン引当(累計)の計算式設定
     */
    private void setFormulaLossRuikeiTotalCell(Sheet sheet, Cell targetCell) throws Exception {
        // 契約金額合計の対象行を取得するためのセル情報(テンプレートファイル内のセル名称が"_KEIYAKU_TOTAL_TITLE"としている) → 2018/07/10 セル名称を付与していると、他ダウンロード機能で影響があったのでプログラムからセル行番号取得に修正
        //Cell keiyakuTotalRowCell = PoiUtil.searchCellName(sheet, "_KEIYAKU_TOTAL_TITLE");
        Integer keiyakuTotalRowIndex = (Integer)headStartMap.get("keiyakuSum");
        Cell keiyakuTotalRowCell = PoiUtil.getCell(sheet, keiyakuTotalRowIndex, 2);
        
        // 見積総原価合計の対象行を取得するためのセル情報(テンプレートファイル内のセル名称が"_SOGENKA_TOTAL_TITLE"としている) → 2018/07/10 セル名称を付与していると、他ダウンロード機能で影響があったのでプログラムからセル行番号取得に修正
        //Cell sogenkaTotalRowCell = PoiUtil.searchCellName(sheet, "_SOGENKA_TOTAL_TITLE");
        Integer sogenkaRowIndex = (Integer)headStartMap.get("mitsumoriSum");
        Cell sogenkaTotalRowCell = PoiUtil.getCell(sheet, sogenkaRowIndex, 2);
        logger.info("keiyakuTotalRowIndex=" + keiyakuTotalRowIndex + " sogenkaRowIndex=" + sogenkaRowIndex);

        int col = targetCell.getColumnIndex();
        String keiyakuCellNo = PoiUtil.getCellReferenceStr(keiyakuTotalRowCell.getRow().getRowNum(), col);
        String sogenkaCellNo = PoiUtil.getCellReferenceStr(sogenkaTotalRowCell.getRow().getRowNum(), col);
        targetCell.setCellFormula(keiyakuCellNo + "-" + sogenkaCellNo);
    }

    /**
     * (2018B)進行基準の場合、ロスコン欄データの設定
     * @param sheet
     * @param paramMap
     * @param col
     * @throws Exception 
     */
    private void setLossconInfoShinkoData(Sheet sheet, Map<String, Object> paramMap, int col) throws Exception {
        // データセット対象の年月、実績(J)/見込(M)区分、ロスコン開始年月を取得
        String targetNengetsu = (String)paramMap.get("syuekiYm");
        String dataKbn = (String)paramMap.get("dataKbn");
        String lossStartYm = (String)paramMap.get("lossStartYm");
        Integer kanbaiMonthFlg = (Integer)paramMap.get("kanbaiMonthFlg");

        // ロスコン情報対象行の取得(テンプレートファイル内のセル名称が"_LOSSCON"としているので、その行がロスコン情報開始のセルとなる)
        Cell lossStartRowCell = getLossStartRowCell(sheet);
        
        // ロスコン補正
        Row lossHoseiRow = lossStartRowCell.getRow();
        Cell lossHoseiCell = lossHoseiRow.getCell(col);
        // ロスコン補正後の売上高
        Row lossHoseiAmountRow = sheet.getRow(lossHoseiRow.getRowNum() + 1);
        Cell lossHoseiAmountCell = lossHoseiAmountRow.getCell(col);
        // ロスコン補正後の売上原価
        Row lossHoseiGenkaRow = sheet.getRow(lossHoseiAmountRow.getRowNum() + 1);
        Cell lossHoseiGenkaCell = lossHoseiGenkaRow.getCell(col);
        // ロスコン粗利
        Row lossHoseiArariRow = sheet.getRow(lossHoseiGenkaRow.getRowNum() + 1);
        // ロスコンM率
        Row lossHoseiMrateRow = sheet.getRow(lossHoseiArariRow.getRowNum() + 1);
        // ロスコン引当(今回)
        Row lossHoseiHikiateNowRow = sheet.getRow(lossHoseiMrateRow.getRowNum() + 1);
        Cell lossHoseiHikiateNowCell = lossHoseiHikiateNowRow.getCell(col);
        // ロスコン引当(累計)
        Row lossHoseiHikiateTotalRow = sheet.getRow(lossHoseiHikiateNowRow.getRowNum() + 1);
        Cell lossHoseiHikiateTotalCell = lossHoseiHikiateTotalRow.getCell(col);

        boolean isLossBeforeMonthFlg = false;
        // 対象年月がロスコン開始年月より前の場合、ロスコン計算はしないため、計算式を全てクリア
        if (targetNengetsu.compareTo(lossStartYm) < 0) {
            lossHoseiCell.setCellFormula(null);
            lossHoseiAmountCell.setCellFormula(null);
            lossHoseiGenkaCell.setCellFormula(null);
            lossHoseiHikiateNowCell.setCellFormula(null);
            lossHoseiHikiateTotalCell.setCellFormula(null);
            isLossBeforeMonthFlg = true;
        }
        
        // 実績月の場合は、ロスコン情報テーブルから補正額や引当額を取得(実績月なので、GAIAから取得した情報をセットする)
        SyuKiLossTbl lossEntity = null;
        if ("J".equals(dataKbn)) {
            lossEntity = syuKiLossTblFacade.findLossTblPk(s004Bean.getAnkenId(), Integer.parseInt(s004Bean.getRirekiId()), dataKbn, targetNengetsu, s004Bean.getRirekiFlg());
            if (lossEntity == null) {
                lossEntity = new SyuKiLossTbl();
            }
        }
        
        ////////////////// 実績月のロスコン関連値を埋め込み(実績はGAIAから取り込みした値をそのまま埋め込む) ////////////////////////
        if (lossEntity != null) {
            ////// 実績月
            // ロスコン補正額をセット
            lossHoseiCell.setCellFormula(null);
            PoiUtil.setCellValue(lossHoseiCell, lossEntity.getLossHosei());
            // ロスコン補正後の売上高をセット
            lossHoseiAmountCell.setCellFormula(null);
            PoiUtil.setCellValue(lossHoseiAmountCell, lossEntity.getLossAmount());
            // 補正後の売上原価をセット
            lossHoseiGenkaCell.setCellFormula(null);
            PoiUtil.setCellValue(lossHoseiGenkaCell, lossEntity.getLossGenka());
            // ロスコン引当(今回)をセット
            lossHoseiHikiateNowCell.setCellFormula(null);
            PoiUtil.setCellValue(lossHoseiHikiateNowCell, lossEntity.getLossHikiate());
            // ロスコン引当(累計)をセット
            lossHoseiHikiateTotalCell.setCellFormula(null);
            PoiUtil.setCellValue(lossHoseiHikiateTotalCell, lossEntity.getLossRuikeiHikiate());
            
            return;
        }

        // 対象年月がロスコン開始年月より前 or 実績月の場合は、以後の処理(計算式埋め込み)は行わない。
        if (isLossBeforeMonthFlg) {
            return;
        }

        ////////////////// 以下、計算式を埋め込み(4半期月判別で計算式を埋め込む) ////////////////////////
        ///// ロスコン補正の計算式設定
        // 完売月の場合
        if (kanbaiMonthFlg == 1) {
            // ロスコン補正行の番号
            int losssHoseiRowIdx = lossHoseiRow.getRowNum();
            // 過去(前月まで)のロスコン補正の総合計
            String strFormula = getRuikeiLast(sheet, losssHoseiRowIdx, DATA_START_COL, col - 1);            
            // ロスコン補正（過去(前月まで)の総合計*-1）
            lossHoseiCell.setCellFormula("(" + strFormula + ")" + "*(-1)");

        // ロスコン開始月の場合      
        } else if (StringUtils.equals(targetNengetsu, lossStartYm)) {
            // 粗利累計の行番号
            Integer arariRuikeiRowIndex = (Integer)headStartMap.get("arari") + 1;
            // ロスコン補正（粗利累計 - ロスコン引当(今回)）
            lossHoseiCell.setCellFormula(PoiUtil.getCellReferenceStr(arariRuikeiRowIndex, col) 
                    + "-" + PoiUtil.getCellReferenceStr(lossHoseiHikiateNowRow.getRowNum(), col));
        
        // ロスコンの通常月もしくはロスコンの4半期最終月の場合
        } else {
            // 粗利今回の行番号
            Integer arariKonkaiRowIndex = (Integer)headStartMap.get("arari");
            // ロスコン補正（粗利今回）
            lossHoseiCell.setCellFormula(PoiUtil.getCellReferenceStr(arariKonkaiRowIndex, col));
        }
       
        ///// 補正後の売上高の計算式設定
        // 売上高合計の行番号
        Integer uriKonkaiSumRowIndex = (Integer)headStartMap.get("uriKonkaiSum");
        lossHoseiAmountCell.setCellFormula(PoiUtil.getCellReferenceStr(uriKonkaiSumRowIndex, col));
        
        ///// 補正後の売上原価の計算式設定
        // 売上原価(今回)の行番号
        Integer uriGenkaKonkaiRowIndex = (Integer)headStartMap.get("uriGenkaKonkai");
        // 完売月もしくはロスコン開始月の場合    
        if (kanbaiMonthFlg == 1 || StringUtils.equals(targetNengetsu, lossStartYm)) {
            // 補正後の売上原価（売上原価(今回) + ロスコン補正）
            lossHoseiGenkaCell.setCellFormula(PoiUtil.getCellReferenceStr(uriGenkaKonkaiRowIndex, col) 
                    + "+" + PoiUtil.getCellReferenceStr(lossHoseiRow.getRowNum(), col));
        
        // ロスコンの通常月もしくはロスコンの4半期最終月の場合         
        } else {
            // 補正後の売上原価（売上原価(今回)）+ ロスコン補正 - ロスコン引当(今回)）
            lossHoseiGenkaCell.setCellFormula(PoiUtil.getCellReferenceStr(uriGenkaKonkaiRowIndex, col) 
                    + "+" + PoiUtil.getCellReferenceStr(lossHoseiRow.getRowNum(), col)
                    + "-" + PoiUtil.getCellReferenceStr(lossHoseiHikiateNowRow.getRowNum(), col));
        }
         
        ///// ロスコン引当(累計),ロスコン引当(今回)の計算式設定
        // 前4半期最終月の年月セル位置(beforeQuaterLossColumnIndex)を取得
        int beforeQuaterLossColumnIndex = col - getQuarterMonthMinusCount(targetNengetsu);
            
        // ロスコン開始月もしくは(見込)四半期月もしくは完売月の場合に設定
        if (StringUtils.equals(targetNengetsu, lossStartYm) || isQuarter(targetNengetsu) || kanbaiMonthFlg == 1) {
            ////// ロスコン引当(今回)に計算式を埋め込む
            String lossconTotalCellCalc;
            // ロスコン引当(累計)のセール引用
            String lossconTotalCellNo = PoiUtil.getCellReferenceStr(lossHoseiHikiateTotalRow.getRowNum(), col);
            if (beforeQuaterLossColumnIndex >= YM_START_COL) {
                // ロスコン引当(今回)（ロスコン引当(累計) - 前4半期の最終月のロスコン引当(累計額)）
                lossconTotalCellCalc = lossconTotalCellNo + "-" + PoiUtil.getCellReferenceStr(lossHoseiHikiateTotalRow.getRowNum(), beforeQuaterLossColumnIndex);
            } else {
                // 前4半期最終月が存在しない場合は ロスコン引当(累計) の値をそのまま参照
                lossconTotalCellCalc = lossconTotalCellNo;
            }
            lossHoseiHikiateNowCell.setCellFormula(lossconTotalCellCalc);

            ////// ロスコン引当(累計)に計算式(契約金額-見積総原価)を埋め込む
            setFormulaLossRuikeiTotalCell(sheet, lossHoseiHikiateTotalCell);
            
        // ロスコンの通常月の場合
        } else {
            // ロスコン引当(今回)をクリアする(テンプレートに予め計算式が設定されているので、それをクリアする処理となる)
            lossHoseiHikiateNowCell.setCellFormula(null);
            // 前4半期最終月が存在した場合
            if (beforeQuaterLossColumnIndex >= YM_START_COL) {
                // ロスコン引当(今回)（前4半期の最終月のロスコン引当(累計額)）
                lossHoseiHikiateTotalCell.setCellFormula(PoiUtil.getCellReferenceStr(lossHoseiHikiateTotalRow.getRowNum(), beforeQuaterLossColumnIndex));
            } else {
                // 前4半期最終月が存在しない場合は ロスコン引当(累計) の値を空白
                lossHoseiHikiateTotalCell.setCellFormula(null);
            }
        }
    }
        
    /**
     * (2018B)原価基準の場合、ロスコン欄データの設定
     */
    private void setLossconInfoGenkaData(Sheet sheet, Map<String, Object> paramMap, int col) throws Exception {
        // データセット対象の年月、実績(J)/見込(M)区分、ロスコン開始年月を取得
        String targetNengetsu = (String)paramMap.get("syuekiYm");
        String dataKbn = (String)paramMap.get("dataKbn");
        String lossStartYm = (String)paramMap.get("lossStartYm");
        Integer kanbaiMonthFlg = (Integer)paramMap.get("kanbaiMonthFlg");

        // ロスコン情報対象行の取得(テンプレートファイル内のセル名称が"_LOSSCON"としているので、その行がロスコン情報開始のセルとなる)
        Cell lossStartRowCell = getLossStartRowCell(sheet);
        
        // ロスコン補正
        Row lossHoseiRow = lossStartRowCell.getRow();
        Cell lossHoseiCell = lossHoseiRow.getCell(col);
        // ロスコン補正後の売上高
        Row lossHoseiAmountRow = sheet.getRow(lossHoseiRow.getRowNum() + 1);
        Cell lossHoseiAmountCell = lossHoseiAmountRow.getCell(col);
        // ロスコン補正後の売上原価
        Row lossHoseiGenkaRow = sheet.getRow(lossHoseiAmountRow.getRowNum() + 1);
        Cell lossHoseiGenkaCell = lossHoseiGenkaRow.getCell(col);
        // ロスコン粗利
        Row lossHoseiArariRow = sheet.getRow(lossHoseiGenkaRow.getRowNum() + 1);
        // ロスコンM率
        Row lossHoseiMrateRow = sheet.getRow(lossHoseiArariRow.getRowNum() + 1);
        // ロスコン引当(今回)
        Row lossHoseiHikiateNowRow = sheet.getRow(lossHoseiMrateRow.getRowNum() + 1);
        Cell lossHoseiHikiateNowCell = lossHoseiHikiateNowRow.getCell(col);
        // ロスコン引当(累計)
        Row lossHoseiHikiateTotalRow = sheet.getRow(lossHoseiHikiateNowRow.getRowNum() + 1);
        Cell lossHoseiHikiateTotalCell = lossHoseiHikiateTotalRow.getCell(col);

        boolean isLossBeforeMonthFlg = false;
        // 対象年月がロスコン開始年月より前の場合、ロスコン計算はしないため、計算式を全てクリア
        if (targetNengetsu.compareTo(lossStartYm) < 0) {
            lossHoseiCell.setCellFormula(null);
            lossHoseiAmountCell.setCellFormula(null);
            lossHoseiGenkaCell.setCellFormula(null);
            lossHoseiHikiateNowCell.setCellFormula(null);
            lossHoseiHikiateTotalCell.setCellFormula(null);
            isLossBeforeMonthFlg = true;
        }
        
        // 実績月の場合は、ロスコン情報テーブルから補正額や引当額を取得(実績月なので、GAIAから取得した情報をセットする)
        SyuKiLossTbl lossEntity = null;
        if ("J".equals(dataKbn)) {
            lossEntity = syuKiLossTblFacade.findLossTblPk(s004Bean.getAnkenId(), Integer.parseInt(s004Bean.getRirekiId()), dataKbn, targetNengetsu, s004Bean.getRirekiFlg());
            if (lossEntity == null) {
                lossEntity = new SyuKiLossTbl();
            }
        }
        
        ////////////////// 実績月のロスコン関連値を埋めfindLossTblPk込み(実績はGAIAから取り込みした値をそのまま埋め込む) ////////////////////////
        if (lossEntity != null) {
            ////// 実績月
            // ロスコン補正額をセット
            lossHoseiCell.setCellFormula(null);
            PoiUtil.setCellValue(lossHoseiCell, lossEntity.getLossHosei());
            // ロスコン補正後の売上高をセット
            lossHoseiAmountCell.setCellFormula(null);
            PoiUtil.setCellValue(lossHoseiAmountCell, lossEntity.getLossAmount());
            // 補正後の売上原価をセット
            lossHoseiGenkaCell.setCellFormula(null);
            PoiUtil.setCellValue(lossHoseiGenkaCell, lossEntity.getLossGenka());
            // ロスコン引当(今回)をセット
            lossHoseiHikiateNowCell.setCellFormula(null);
            PoiUtil.setCellValue(lossHoseiHikiateNowCell, lossEntity.getLossHikiate());
            // ロスコン引当(累計)をセット
            lossHoseiHikiateTotalCell.setCellFormula(null);
            PoiUtil.setCellValue(lossHoseiHikiateTotalCell, lossEntity.getLossRuikeiHikiate());
            
            return;
        }

        // 対象年月がロスコン開始年月より前 or 実績月の場合は、以後の処理(計算式埋め込み)は行わない。
        if (isLossBeforeMonthFlg) {
            return;
        }
        
        ////////////////// 以下、計算式を埋め込み(4半期月判別で計算式を埋め込む) ////////////////////////
        ///// ロスコン補正の計算式設定
        // 完売月の場合
        if (kanbaiMonthFlg == 1) {
            // ロスコン補正行の番号
            int losssHoseiRowIdx = lossHoseiRow.getRowNum();
            // 過去(前月まで)のロスコン補正の総合計
            String strFormula = getRuikeiLast(sheet, losssHoseiRowIdx, DATA_START_COL, col - 1);            
            // ロスコン補正（過去(前月まで)の総合計*-1）
            lossHoseiCell.setCellFormula("(" + strFormula + ")" + "*(-1)");

        // ロスコン開始月の場合      
        } else if (StringUtils.equals(targetNengetsu, lossStartYm)) {
            // 売上高(累計)合計のセール引用
            String strUriRuikeiSumRef = PoiUtil.getCellReferenceStr((Integer)headStartMap.get("uriRuikeiSum"), col);
            // 契約/見積総原価のセール引用
            String strKeiyakuMitsumoriSumRef = PoiUtil.getCellReferenceStr((Integer)headStartMap.get("keiyakuMitsumoriSum"), col);
            // （当月売上高（累計）＊（Ｍ率－１））+ ロスコン引当（今回）
            String nowFormula = "ROUND(" + strUriRuikeiSumRef + "*(" + strKeiyakuMitsumoriSumRef + "-1),0)" 
                     + "+" + PoiUtil.getCellReferenceStr(lossHoseiHikiateNowRow.getRowNum(), col);
            
            // ロスコン補正（上記値を設定）
            lossHoseiCell.setCellFormula(nowFormula);

        // ロスコンの4半期最終月の場合  
        } else if (isQuarter(targetNengetsu)) {
            // 売上高(累計)合計のセール引用
            String uriRuikeiSumRef = PoiUtil.getCellReferenceStr((Integer) headStartMap.get("uriRuikeiSum"), col);
            // 契約/見積総原価合計(M率)のセール引用
            String keiyakuMitsumoriMRef = PoiUtil.getCellReferenceStr((Integer) headStartMap.get("keiyakuMitsumoriSum"), col);
            // （当月売上高（累計）＊（Ｍ率－１））
            String nowFormula = "ROUND(" + uriRuikeiSumRef + "*(" + keiyakuMitsumoriMRef + "-1),0)";

            // 前4半期最終月の年月セル位置
            int beforeQuaterLossColumnIndex = col - getQuarterMonthMinusCount(targetNengetsu);
            // 前4半期最終月が存在した場合
            if (beforeQuaterLossColumnIndex >= YM_START_COL) {
                // 前4半期最終月の売上高(累計)合計のセール引用
                String befUriRuikeiSumRef = PoiUtil.getCellReferenceStr((Integer) headStartMap.get("uriRuikeiSum"), beforeQuaterLossColumnIndex);
                // 前4半期最終月の契約/見積総原価合計(M率)のセール引用
                String befKeiyakuMitsumoriMRef = PoiUtil.getCellReferenceStr((Integer) headStartMap.get("keiyakuMitsumoriSum"), beforeQuaterLossColumnIndex);
                // （当月売上高（累計）＊（Ｍ率－１））
                String befFormula = "ROUND(" + befUriRuikeiSumRef + "*(" + befKeiyakuMitsumoriMRef + "-1),0)";

                // ロスコン補正（（当月売上高（累計）＊（Ｍ率－１））－（前Ｑ売上高（累計）＊（前ＱＭ率－１）））
                lossHoseiCell.setCellFormula(nowFormula + "-" + befFormula);

            // 前4半期最終月が存在しない場合    
            } else {
                // ロスコン補正（当月売上高（累計）＊（Ｍ率－１））
                lossHoseiCell.setCellFormula(nowFormula);
            }
           
        // ロスコンの通常月の場合
        } else {
            // 粗利今回の行番号
            Integer arariKonkaiRowIndex = (Integer)headStartMap.get("arari");
            // ロスコン補正（粗利今回）
            lossHoseiCell.setCellFormula(PoiUtil.getCellReferenceStr(arariKonkaiRowIndex, col));
        }
        
        ///// 補正後の売上高の計算式設定
        // 売上高(今回)の行番号
        Integer uriKonkaiSumRowIndex = (Integer)headStartMap.get("uriKonkaiSum");
        // 完売月もしくはロスコン開始月の場合    
        if (kanbaiMonthFlg == 1 || StringUtils.equals(targetNengetsu, lossStartYm)) {
            // 補正後の売上高（売上高(今回) + ロスコン補正）
            lossHoseiAmountCell.setCellFormula(PoiUtil.getCellReferenceStr(uriKonkaiSumRowIndex, col) 
                    + "+" + PoiUtil.getCellReferenceStr(lossHoseiRow.getRowNum(), col));
        
        // ロスコンの通常月もしくはロスコンの4半期最終月の場合         
        } else {
            // 補正後の売上高（売上高(今回) + ロスコン補正 + ロスコン引当（今回））
            lossHoseiAmountCell.setCellFormula(PoiUtil.getCellReferenceStr(uriKonkaiSumRowIndex, col) 
                    + "+" + PoiUtil.getCellReferenceStr(lossHoseiRow.getRowNum(), col)
                    + "+" + PoiUtil.getCellReferenceStr(lossHoseiHikiateNowRow.getRowNum(), col));
        }
 
        ///// 補正後の売上原価の計算式設定
        // 売上原価合計の行番号
        Integer uriGenkaKonkaiRowIndex = (Integer)headStartMap.get("uriGenkaKonkai");
        lossHoseiGenkaCell.setCellFormula(PoiUtil.getCellReferenceStr(uriGenkaKonkaiRowIndex, col));
        
        ///// ロスコン引当(累計),ロスコン引当(今回)の計算式設定
        // 前4半期最終月の年月セル位置(beforeQuaterLossColumnIndex)を取得
        int beforeQuaterLossColumnIndex = col - getQuarterMonthMinusCount(targetNengetsu);
            
        // ロスコン開始月もしくは(見込)四半期月もしくは完売月の場合に設定
        if (StringUtils.equals(targetNengetsu, lossStartYm) || isQuarter(targetNengetsu) || kanbaiMonthFlg == 1) {

            ////// ロスコン引当(今回)に計算式を埋め込む
            String lossconTotalCellCalc;
            // ロスコン引当(累計)のセール引用
            String lossconTotalCellNo = PoiUtil.getCellReferenceStr(lossHoseiHikiateTotalRow.getRowNum(), col);
            // 前4半期最終月が存在した場合
            if (beforeQuaterLossColumnIndex >= YM_START_COL) {
                // ロスコン引当(今回)（ロスコン引当(累計) - 前4半期の最終月のロスコン引当(累計額)）
                lossconTotalCellCalc = lossconTotalCellNo + "-" + PoiUtil.getCellReferenceStr(lossHoseiHikiateTotalRow.getRowNum(), beforeQuaterLossColumnIndex);
            } else {
                // 前4半期最終月が存在しない場合は ロスコン引当(累計) の値をそのまま参照
                lossconTotalCellCalc = lossconTotalCellNo;
            }
            lossHoseiHikiateNowCell.setCellFormula(lossconTotalCellCalc);


            ////// ロスコン引当(累計)に計算式(契約金額-見積総原価)を埋め込む
            setFormulaLossRuikeiTotalCell(sheet, lossHoseiHikiateTotalCell);
        
        // ロスコンの通常月の場合
        } else {
            // ロスコン引当(今回)の計算式をクリアする(テンプレートに予め計算式が設定されているので、それをクリアする処理となる)
            lossHoseiHikiateNowCell.setCellFormula(null);
            // 前4半期最終月が存在した場合
            if (beforeQuaterLossColumnIndex >= YM_START_COL) {
                // ロスコン引当(今回)（前4半期の最終月のロスコン引当(累計額)）
                lossHoseiHikiateTotalCell.setCellFormula(PoiUtil.getCellReferenceStr(lossHoseiHikiateTotalRow.getRowNum(), beforeQuaterLossColumnIndex));
            } else {
                // 前4半期最終月が存在しない場合は ロスコン引当(累計) の値を空白
                lossHoseiHikiateTotalCell.setCellFormula(null);
            }
        }
    }

    /**
     * 指定列間の項目Xの総合計を求める（4半期合計(Q)や期合計(K)の列は含めないこと）
     * @param sheet 指定したシート
     * @param rowIdx 項目の行番号
     * @param startColIdx 合計の開始列　
     * @param endColIdx 合計の終了列
     * @return 
     */
    private String getRuikeiLast(Sheet sheet, Integer rowIdx, Integer startColIdx, Integer endColIdx) {

        // 過去月まで指定項目の総合計
        String strFormula = "SUM(" + PoiUtil.getCellReferenceStr(rowIdx, startColIdx) + ":"
                + PoiUtil.getCellReferenceStr(rowIdx, endColIdx) + ")";
        
        // 四半期表示フラグが１の場合
        if ("1".equals(dateilHeader.getQuarterDispFlg())) {
            for (int i = startColIdx; i <= endColIdx; i++) {

                // 対象列のタイトル年月を取得
                String strYm = (String) (PoiUtil.getCellValue(PoiUtil.getCell(sheet, YM_START_ROW, i)));
                // 4半期合計(Ｑ)もしくは期合計の場合
                if ("Q".equals(strYm.substring(strYm.length() - 1)) || "期".equals(strYm.substring(strYm.length() - 1))) {
                    // 4半期もしくは期のセール引用
                    String strQKCellRef = PoiUtil.getCellReferenceStr(rowIdx, i);
                    // 4半期もしくは期の値を総合計から除く
                    strFormula = strFormula + "-" + "IF(" + strQKCellRef + "=\"\",0, " + strQKCellRef + ")";
                }
            }
        }
        
// 計算方法２　期ごとデータを合計（バグあり、保留中）
//        String strFormula = "";
//        String start = "";
//        String now = "";
//        String bef = "";
//        for (int i = startColIdx; i <= endColIdx; i++) {
//            String strYm = (String) (PoiUtil.getCellValue(PoiUtil.getCell(sheet, YM_START_ROW, i)));
//            //if ("Q".equals(strYm.substring(strYm.length() - 1)) || "期".equals(strYm.substring(strYm.length() - 1))) {
//            if ("期".equals(strYm.substring(strYm.length() - 1))) {
//                continue;
//            }
//
//            now = PoiUtil.getCellReferenceStr(rowIdx, i);
//            if ("Q".equals(strYm.substring(strYm.length() - 1)) || i == endColIdx) {
//                logger.info("last i:[{}], now:[{}] bef:[{}]", i, now, bef);
//                String calc = StringUtils.isNotEmpty(start) ? "SUM(" + start + ":" + bef +")" : now;
//                strFormula = strFormula + (StringUtils.isNotEmpty(strFormula) ? "+" : "") + calc;
//                start = "";
//
//            } else {
//                if (StringUtils.isEmpty(start)) {
//                    start = now;
//                }
//            }
//            bef = now;
//            logger.info("now:[{}] bef:[{}]", now, bef);
//        }
       
        return strFormula;
    }
    /**
     * 通貨金額データの埋め込み
     */
    private void setDitailCurrency(Sheet sheet, Map<String, Object> paramMap, int col, Sheet styleSheet) throws Exception {
        
        Cell cell;
        int row;
        
        if (!currencyList.isEmpty()) {
            for(int idx=0; idx < currencyList.size(); idx++) {
                String currencyCode = currencyList.get(idx).getCurrencyCode();
                
                paramMap.put("currencyCode", currencyCode);
                //// 通貨毎に取得
                Map<String, Object> tsukiCurrencyItem = downloadFacade.selectKsTsukiCurrency2(paramMap);

                //// 契約金額：建値額
                row = convInteger(headStartMap.get("keiyakuTatene")) + (idx * 2);
                cell = PoiUtil.getCell(sheet, row, col);
                // 実績もしくは見込初月のときはテーブルの値をセット
                if ( "J".equals(paramMap.get("dataKbn")) || ("M".equals(paramMap.get("dataKbn")) && 1 == firstMikomiFlg)) {
                    cell.setCellFormula(null);
                    PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(tsukiCurrencyItem.get("KEIYAKU_AMOUNT"))));
                    
                // 見込初月以降は、(前月)契約金額：建値額＋(前月)契約金額：建値額（補正）をセット
                } else {
                    String cellBefTatene = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("keiyakuTatene"))+(idx * 2)), col-1-colBefMonth);
                    String cellBefTateneHosei = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("keiyakuTatene"))+(idx * 2) + 1), col-1-colBefMonth); 
                    cell.setCellFormula("SUM(" + cellBefTatene + ")+SUM(" + cellBefTateneHosei + ")");
                }
                
                //// 契約金額：建値額（補正）
                // 見込のときだけセット
                row = convInteger(headStartMap.get("keiyakuTatene")) + (idx * 2) + 1;
                cell = PoiUtil.getCell(sheet, row, col);
                cell.setCellFormula(null);
                if ("M".equals(paramMap.get("dataKbn"))) {
                    PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(tsukiCurrencyItem.get("KEIYAKU_HOSEI_AMOUNT"))));
                }
                    
                
                //// 契約金額：契約為替レート
                row = convInteger(headStartMap.get("keiyakuKawase")) + idx;
                cell = PoiUtil.getCell(sheet, row, col);
                cell.setCellFormula(null);
                PoiUtil.setCellValue(cell, currencyList.get(idx).getKeiyakuRate());
                
                //// 契約金額：円価
                row = convInteger(headStartMap.get("keiyakuEnka")) + idx;
                cell = PoiUtil.getCell(sheet, row, col);
                // 見込では、(契約金額：建値額＋建値額（補正）)＊契約金額：為替レートの計算式をセット
                if ("M".equals(paramMap.get("dataKbn"))) {
                    String cellTatene = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("keiyakuTatene"))+(idx * 2)), col);
                    String cellHosei = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("keiyakuTatene"))+(idx * 2) + 1), col);
                    String cellRate = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("keiyakuKawase"))+idx), col); 
                    cell.setCellFormula("ROUND((SUM(" + cellTatene + ")+SUM(" + cellHosei + "))*SUM(" + cellRate + "),0)" );
                // 実績の場合はテーブルの情報をセット
                } else {
                    cell.setCellFormula(null);
                    PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(tsukiCurrencyItem.get("KEIYAKU_ENKA_AMOUNT"))));
                }
                

                // 売上高：今回：建値額
                row = convInteger(headStartMap.get("uriKonkai")) + idx;
                cell = PoiUtil.getCell(sheet, row, col);
                // 見込では、売上高：累計：建値額－(前月)売上高：累計：建値額の計算式をセット
                if ("M".equals(paramMap.get("dataKbn"))) {
                    //2018/03/05 原価回収基準対応　REP START
//                    String cellUriRTatene = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriRuikei"))+idx), col);
//                    String cellBefUriRTatene = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriRuikei"))+idx), col-1-colBefMonth); 
//                    cell.setCellFormula("SUM(" + cellUriRTatene + ")-SUM(" + cellBefUriRTatene + ")");
                    if("1".equals(dateilHeader.getAnkenEntity().getSalesClassGenka()) && kanbaiFlg != 1 ){ //原価回収基準でかつ完売月以外
                        //原価回収基準
                        int rowKeiyakuEnka = convInteger(headStartMap.get("keiyakuEnka")) + idx; //契約金額合計
                        int rowKeiyakuSum = convInteger(headStartMap.get("keiyakuSum")) ;   //契約金額　合計        
                        int rowUriGenkaSum = convInteger(headStartMap.get("uriGenkaKonkai")) ;   //売上原価　今回合計    
                        int rowUriRate = convInteger(headStartMap.get("uriKonkai")) + idx + currencyList.size();   //売上為替レート 
                        
                        String cellKeiyakuEnka = PoiUtil.getCellReferenceStr(rowKeiyakuEnka, col); 
                        String cellKeiyakuSum  = PoiUtil.getCellReferenceStr(rowKeiyakuSum, col); 
                        String cellUriGenkaSum = PoiUtil.getCellReferenceStr(rowUriGenkaSum, col);
                        String cellUriRate     = PoiUtil.getCellReferenceStr(rowUriRate, col);

                        //契約通貨の割合：（通貨別契約 円貨 /契約円貨合計）*売上原価合計 / 見込レート*
                        cell.setCellFormula("ROUND((SUM(" + cellKeiyakuEnka + ")/SUM(" + cellKeiyakuSum + "))*SUM(" + cellUriGenkaSum + ")/SUM(" + cellUriRate + "),3)");

                    }else{
                        //進行基準
                        String cellUriRTatene = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriRuikei"))+idx), col);
                        String cellBefUriRTatene = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriRuikei"))+idx), col-1-colBefMonth); 
                        cell.setCellFormula("SUM(" + cellUriRTatene + ")-SUM(" + cellBefUriRTatene + ")");
                    }
                    //2018/03/05 原価回収基準対応　REP END
                    
                } else {
                    cell.setCellFormula(null);
                    PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(tsukiCurrencyItem.get("URIAGE_AMOUNT"))));
                }

                
                //// 売上高：今回：売上為替レート
                row = convInteger(headStartMap.get("uriKonkai")) + idx + currencyList.size();
                cell = PoiUtil.getCell(sheet, row, col);
                cell.setCellFormula(null);
                PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(tsukiCurrencyItem.get("URI_RATE"))));
                

                //// 売上高：今回：円価
                row = convInteger(headStartMap.get("uriKonkai")) + (idx*2) + (currencyList.size()*2);
                cell = PoiUtil.getCell(sheet, row, col);
                // 見込では、売上高：今回：建値額＊売上高：今回：売上為替レートの計算式をセット
                if ("M".equals(paramMap.get("dataKbn"))) {
                    String cellUriKTatene = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriKonkai"))+idx), col);
                    String cellUriKRate = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriKonkai"))+idx+currencyList.size()), col); 
                    cell.setCellFormula("ROUND(SUM(" + cellUriKTatene + ")*SUM(" + cellUriKRate + "),0)");
                } else {
                    cell.setCellFormula(null);
                    PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(tsukiCurrencyItem.get("URIAGE_ENKA_AMOUNT"))));
                }

                //// 売上高：今回：円価：為替差調整
                row = convInteger(headStartMap.get("uriKonkai")) + (idx*2) + (currencyList.size()*2) + 1;
                cell = PoiUtil.getCell(sheet, row, col);
                boolean isSet = true;
                if (jpUnit.equals(currencyCode)) {  // 通貨JPYは為替差調整セットしない
                    isSet = false;
                }
                //2018/03/05 原価回収基準対応　ADD START
                //原価回収基準の場合は為替差の計算は不必要なしたため、計算しない。
                if("1".equals(dateilHeader.getAnkenEntity().getSalesClassGenka())){
                    isSet = false;
                }
                //2018/03/05 原価回収基準対応　ADD END
                
                // 見込かつ四半期月の場合は計算式をセット
                if (isSet) {
                    if ("M".equals(paramMap.get("dataKbn")) && isQuarter(paramMap.get("syuekiYm").toString()) && (firstMikomiFlg+jissekiCol)!=1) {
                        // （売上高：今回：売上為替レート－(先月)売上高：今回：売上為替レート）＊(先月)売上高：今回：建値額
                        //　＋（売上高：今回：売上為替レート－(先々月)売上高：今回：売上為替レート）＊(先々月)売上高：今回：建値額 の計算式をセット
                        String cellUriKRate = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriKonkai"))+idx+currencyList.size()), col); 
                        String cellBefUriKRate = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriKonkai"))+idx+currencyList.size()), col-1); 
                        String cellBefUriKTatene = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriKonkai"))+idx), col-1);

                        String strFormula = "ROUND((SUM(" + cellUriKRate + ")-SUM(" + cellBefUriKRate + "))*SUM(" + cellBefUriKTatene + "),0)"; 

                        // 先々月の売上為替レートがある
                        if (firstMikomiFlg+jissekiCol >= 3) {
                            String cellBef2UriKRate = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriKonkai"))+idx+currencyList.size()), col-2); 
                            String cellBef2UriKTatene = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriKonkai"))+idx), col-2);

                            cell.setCellFormula(strFormula + "+ROUND((SUM(" + cellUriKRate + ")-SUM(" + cellBef2UriKRate + "))*SUM(" + cellBef2UriKTatene + "),0)");
                        // 先々月分の売上為替レートがない
                        } else if (firstMikomiFlg+jissekiCol == 2) {
                            cell.setCellFormula(strFormula);
                        }

                    // 実績ではDBの値をセット
                    } else if ("J".equals(paramMap.get("dataKbn"))) { 
                        cell.setCellFormula(null);
                        PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(tsukiCurrencyItem.get("URIAGE_KAWASESA"))));
                    } else {
                        isSet = false;
                    }
                }
                    
                // 上記以外は空にする    
                if (!isSet){
                    cell.setCellFormula(null);
                    PoiUtil.setCellValue(cell, Utils.changeBigDecimal(""));
                }
                
                //// 売上高：累計：建値額
                row = convInteger(headStartMap.get("uriRuikei")) + idx;
                cell = PoiUtil.getCell(sheet, row, col);
                // 完売月では、契約金額：建値額＋契約金額：建値額(補正)の計算式をセット
                if (kanbaiFlg == 1) {
                    String cellKeTatebe = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("keiyakuTatene"))+(idx*2)), col); 
                    String cellKeHosei = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("keiyakuTatene"))+(idx*2)+1), col); 
                    cell.setCellFormula("SUM(" + cellKeTatebe + ")+SUM(" + cellKeHosei + ")");
                // 見込では、売上原価：累計：合計／見積総原価：合計＊（契約金額：建値額＋契約金額：建値額(補正)）の計算式をセット
                } else if ("M".equals(paramMap.get("dataKbn"))) {
                    //2018/03/05 原価回収基準対応　REP START
//                    String cellUriGeRui = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriGenkaRuikei"))), col);
//                    String cellMiGeSum = ｓPoiUtil.getCellReferenceStr((convInteger(headStartMap.get("mitsumoriSum"))), col); 
//                    String cellKeTatebe = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("keiyakuTatene"))+(idx*2)), col); 
//                    String cellKeHosei = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("keiyakuTatene"))+(idx*2)+1), col); 
//                    cell.setCellFormula( "IF(" + cellMiGeSum +"=0,0,ROUND(" + cellUriGeRui + "/" + cellMiGeSum + "*(SUM(" + cellKeTatebe + ")+SUM(" + cellKeHosei + ")),2))" );
                    
                    if("1".equals(dateilHeader.getAnkenEntity().getSalesClassGenka())){
                        //原価回収基準
                        int rowUriTatene = convInteger(headStartMap.get("uriKonkai")) + idx; //売上高建値
 
                        String cellUriTatene = PoiUtil.getCellReferenceStr(rowUriTatene, col); 
                        String cellUriSumTatene  = PoiUtil.getCellReferenceStr(row, col-1-colBefMonth); //先月の売上高累計建値

                        //先月分の先月の売上高累計建値+当月売上高建値
                        cell.setCellFormula("SUM(" + cellUriTatene + ")+SUM(" + cellUriSumTatene + ")");

                    }else{
                        //進行基準
                        String cellUriGeRui = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriGenkaRuikei"))), col);
                        String cellMiGeSum = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("mitsumoriSum"))), col); 
                        String cellKeTatebe = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("keiyakuTatene"))+(idx*2)), col); 
                        String cellKeHosei = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("keiyakuTatene"))+(idx*2)+1), col); 
                        cell.setCellFormula( "IF(" + cellMiGeSum +"=0,0,ROUND(" + cellUriGeRui + "/" + cellMiGeSum + "*(SUM(" + cellKeTatebe + ")+SUM(" + cellKeHosei + ")),2))" );
                    }
                    //2018/03/05 原価回収基準対応　REP END
                    
                // 実績ではDBの値をセット
                } else {
                    cell.setCellFormula(null);
                    PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(tsukiCurrencyItem.get("URIAGE_RUIKEI_AMOUNT"))));
                }
                
                //// 売上高：累計：円価
                row = convInteger(headStartMap.get("uriRuikei")) + idx + currencyList.size();
                cell = PoiUtil.getCell(sheet, row, col);
                // 見込では、(前月)売上高：累計：円価＋（売上高：今回：円価＋売上高：今回：為替差調整）の計算式をセット
                if ("M".equals(paramMap.get("dataKbn"))) {
                    String cellBefUriRuiEn = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriRuikei"))+idx+ currencyList.size()), col-1-colBefMonth);
                    String cellUriKEn = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriKonkai"))+(idx*2)+(currencyList.size()*2)), col);
                    String cellUriKKawasesa = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriKonkai"))+(idx*2)+(currencyList.size()*2)+1), col);
                    if(delTagetRow != null && delTagetRow == (convInteger(headStartMap.get("uriKonkai"))+(idx*2)+(currencyList.size()*2)+1)){
                        cellUriKKawasesa = "";
                    }else{
                        cellUriKKawasesa = "+SUM(" +  PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriKonkai"))+(idx*2)+(currencyList.size()*2)+1), col) + ")";
                    }
                    cell.setCellFormula("SUM(" + cellBefUriRuiEn + ")+(SUM(" + cellUriKEn + ")" +  cellUriKKawasesa + ")");
                    
                // 実績ではDBの値をセット
                } else {
                    cell.setCellFormula(null);
                    PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(tsukiCurrencyItem.get("URIAGE_RUIKEI_ENKA_AMOUNT"))));
                }
                
            }
        }
    }

    /**
     * 見積総原価金額データの埋め込み
     */
    private void setDitailSogenka(Sheet sheet, Map<String, Object> paramMap, int col) throws Exception {
        
        Cell cell;
        int row;
        
        String dataKbn = (String)paramMap.get("dataKbn");
        String syuekiYm = (String)paramMap.get("syuekiYm");
        
        Map<String, Object> sogenkaTsukiItem = downloadFacade.selectKnSogenkaTsuki(paramMap);
        
        //// 見積総原価：発番NET
        int hatubanNetRow = convInteger(headStartMap.get("mitsumoriGenka"));
        row =  hatubanNetRow;
        cell = PoiUtil.getCell(sheet, row, col);
        // 見込初月以外の見込月では、前月の見積総原価合計を使用する
        if ("M".equals(dataKbn) && firstMikomiFlg != 1) {
            String cellBefMGenkaSum = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("mitsumoriSum"))), (col-1-colBefMonth));
            cell.setCellFormula(cellBefMGenkaSum);       
        //  実績、見込初月では取得した発番NETを使用する   
        } else {
            cell.setCellFormula(null);
            PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(sogenkaTsukiItem.get("HAT_NET"))));             
        }

        //// 見積総原価：発番NET
        cell = PoiUtil.getCell(sheet, hatubanNetRow, col);
        //if(this.divisionCode.equals("N7")){
        if (divisionComponentPage.isNuclearDivision(this.divisionCode)) {
            cell.setCellFormula(null);
        }
        PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(sogenkaTsukiItem.get("HAT_NET"))));
        
        //// 見積総原価：未発番NET
        int mihatubanNetRow = hatubanNetRow + 1;
        row = mihatubanNetRow;
        cell = PoiUtil.getCell(sheet, row, col);
        cell.setCellFormula(null);
        PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(sogenkaTsukiItem.get("MI_HAT_NET"))));
        
        //// 見積総原価：製番損益
        int seibanSonekiRow = mihatubanNetRow + 1;
        row = seibanSonekiRow;
        cell = PoiUtil.getCell(sheet, row, col);
        cell.setCellFormula(null);
        PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(sogenkaTsukiItem.get("SEIBAN_SONEKI_NET"))));

        //// 見積総原価：為替洗替影響
        int kawaseEikyoRow = seibanSonekiRow + 1;
        row = kawaseEikyoRow;
        cell = PoiUtil.getCell(sheet, row, col);
        cell.setCellFormula(null);
        PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(sogenkaTsukiItem.get("KAWASE_EIKYO"))));

        //// 見積総原価：合計
        setCalcMitSogenkaSum(sheet, col, dataKbn, syuekiYm);
    }
    
    /**
     * 見積総原価:合計の計算式をセット
     */
    private void setCalcMitSogenkaSum(Sheet sheet, int col, String dataKbn, String syuekiYm) throws Exception {
        int hatubanNetRow = convInteger(headStartMap.get("mitsumoriGenka"));
        Cell cell = PoiUtil.getCell(sheet, hatubanNetRow + 4, col);

        if (dataKbn.equals("J")) {

            if(syuekiYm.endsWith("Q") || syuekiYm.endsWith("K")){
                cell.setCellFormula(PoiUtil.getCellReferenceStr( hatubanNetRow + 4, col - 1));
            } else {
                // 実績月のみ値をセット
                cell.setCellFormula(null);
                BigDecimal totalSougenka = downloadFacade.getTotalSougenka(
                        s004Bean.getAnkenId(),
                        new Integer(s004Bean.getRirekiId()),
                        dataKbn,
                        syuekiYm,
                        s004Bean.getRirekiFlg()
                );
                PoiUtil.setCellValue(cell, totalSougenka);
            }

        } else {
            // 見込月は発番NET＋未発番ＮＥＴ-製番損益＋為替洗替影響(計算式セット)
            cell.setCellFormula(PoiUtil.getCellReferenceStr(hatubanNetRow, col) + "+" + PoiUtil.getCellReferenceStr(hatubanNetRow + 1, col) + "-"
            + PoiUtil.getCellReferenceStr(hatubanNetRow + 2, col) + "+" + PoiUtil.getCellReferenceStr(hatubanNetRow + 3, col));

        }

    }
    
    /**
     * 売上原価カテゴリ金額データの埋め込み
     */
    private void setDitailCate(Sheet sheet, Map<String, Object> paramMap, int col) throws Exception {
        
        Cell cell;
        int row;
        
        List<Map<String, Object>> cateTsukiList = downloadFacade.selectCateTsuki(paramMap);
        
        if (cateTsukiList != null) {
            row = convInteger(headStartMap.get("uriGenka"));
            for (int idx=0; idx<cateTsukiList.size(); idx++) {
                // 売上原価：今回：カテゴリ
                cell = PoiUtil.getCell(sheet, row, col);
                cell.setCellFormula(null);
                PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(cateTsukiList.get(idx).get("NET"))));
                row++;
            }
        }
        
        // 完売月では、計算式を再設定する（ﾃﾝﾌﾟﾚｰﾄの計算式を使わない）
        if (kanbaiFlg == 1) {
            //// 売上原価：今回：合計
            // 売上原価：累計：合計 － (前月)売上原価：累計：合計の計算式をセット
            row = convInteger(headStartMap.get("uriGenkaKonkai"));
            cell = PoiUtil.getCell(sheet, row, col);
            String cellUriGeRui = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriGenkaRuikei"))), col);
            String cellBefUriGeRui = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriGenkaRuikei"))), col-1-colBefMonth);
            cell.setCellFormula( cellUriGeRui + "-" + cellBefUriGeRui );
            
            //// 売上原価：累計：合計
            // 見積総原価：合計の値をセット
            row = convInteger(headStartMap.get("uriGenkaKonkai")) + 1;
            cell = PoiUtil.getCell(sheet, row, col);
            String cellMiGeSum = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("mitsumoriSum"))), col); 
            cell.setCellFormula( cellMiGeSum );
        } else {
            //// 売上原価：累計
            row  = convInteger(headStartMap.get("uriGenkaRuikei"));
            cell = PoiUtil.getCell(sheet, row, col);
            // 前月売上原価：累計＋売上原価：今回：合計
            String cellBeflRui = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriGenkaRuikei"))), col-1-colBefMonth);
            String cellGokei = PoiUtil.getCellReferenceStr((convInteger(headStartMap.get("uriGenkaKonkai"))), col);        
            cell.setCellFormula( cellBeflRui + "+" + cellGokei );  
        }
        
        
    }
    
    /**
     * 四半期チェック
     * @param nengetsu
     * @return 
     */
    private boolean isQuarter(String nengetsu) {
        boolean shihankiFlg = false;
        
        if (nengetsu != null && !"".equals(nengetsu)) {
            if (nengetsu.length() == 6) {
                String strTsuki = nengetsu.substring(nengetsu.length()-2, nengetsu.length());
                Integer tsuki = Integer.parseInt(strTsuki);
                
                if (tsuki%3 == 0) {
                    shihankiFlg = true;
                }
            }
        }
        
        return shihankiFlg;
    }
    
    
    /**
     * 上期下期チェック
     * @param nengetsu
     * @return 
     */
    private boolean isKi(String nengetsu) {
        boolean kiFlg = false;
        
        if (nengetsu != null && !"".equals(nengetsu)) {
            if (nengetsu.length() == 6) {
                String strTsuki = nengetsu.substring(nengetsu.length()-2, nengetsu.length());
                Integer tsuki = Integer.parseInt(strTsuki);
                
                // "上期"と"下期"
                if (tsuki==9 || tsuki == 3) {
                    kiFlg = true;
                }
                
            }
        }
        
        return kiFlg;
    }
    
    
    /**
     * 四半期ラベル
     * @param nengetsu
     * @param kbn 0:四半期 1:上期下期
     * @return ex."2010/1Q" or "2010/上期"
     */
    private String getQuarterLabel(String nengetsu, int kbn) throws Exception {
        
        String strLabel = "";
        Date wkDate = Utils.parseDate(nengetsu + "/01", "yyyy/MM/dd");
        
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal = DateUtils.toCalendar(DateUtils.addMonths(wkDate, -3));
        String nendo = Integer.toString(cal.get(Calendar.YEAR));
        
        int m = (int) Math.ceil((cal.get(Calendar.MONTH)+1)/3.0);
        
        switch (m) {
            case 1: strLabel = nendo + (kbn==1?"/1Q":"/上期");
                    break;
            case 2: strLabel = nendo + (kbn==1?"/2Q":"/上期");
                    break;
            case 3: strLabel = nendo + (kbn==1?"/3Q":"/下期");
                    break;
            case 4: strLabel = nendo + (kbn==1?"/4Q":"/下期");
                    break;
            default: break;
        }
        
        return strLabel;
        
    }
    
    /**
     * 四半期データをセット
     * @param sheet
     * @param paramMap
     * @param col
     * @param kbn 0:四半期　1:期
     * @throws Exception 
     */
    private void setQuarterData(String dataKbn, String nengetsu, Sheet sheet, int col, int kbn) throws Exception{
        
        Cell cell;
        int row;

        int startColQ = col-cntQuarterSum;
        int endColQ = col-colBefMonth;
        
        // 四半期のときだけ行う
        // SUM(６ヶ月)用にCOLの位置を保持しておく
        // 下の変数を用いて「SUM(A1:C1,E1:G1)」のように計算式を作る
        if (kbn == 0) {
            if (colQuarterSum1 == null) {
                colQuarterSum1 = startColQ;
                colQuarterSum2 = endColQ;
            } else {
                colQuarterSum3 = startColQ;
                colQuarterSum4 = endColQ;
            }
        }

        // 見積総原価:合計欄の計算式セット
        String quarterSyuekiYm = getQuaterKiToKey(nengetsu, kbn);
        setCalcMitSogenkaSum(sheet, col, dataKbn, quarterSyuekiYm);

        // 通貨単位でループ
        if (!currencyList.isEmpty()) {
            for(int idx=0; idx < currencyList.size(); idx++) {
                String currencyCode = currencyList.get(idx).getCurrencyCode();

                //// 契約金額：建値額
                row = convInteger(headStartMap.get("keiyakuTatene")) + (idx * 2);
                cell = PoiUtil.getCell(sheet, row, col);
                // 前月(四半期月)のセルを参照する計算式をセット
                cell.setCellFormula( PoiUtil.getCellReferenceStr(row, endColQ) );
                
                //// 契約金額：建値額（補正）
                row = convInteger(headStartMap.get("keiyakuTatene")) + (idx * 2) + 1;
                cell = PoiUtil.getCell(sheet, row, col);
                // 前月(四半期月)のセルを参照する計算式をセット
                cell.setCellFormula( PoiUtil.getCellReferenceStr(row, endColQ) );
                
                //// 契約金額：契約為替レート
                row = convInteger(headStartMap.get("keiyakuKawase")) + idx;
                cell = PoiUtil.getCell(sheet, row, col);
                // 前月(四半期月)のセルを参照する計算式をセット
                cell.setCellFormula( PoiUtil.getCellReferenceStr(row, endColQ) );
                
                //// 契約金額：円価
                row = convInteger(headStartMap.get("keiyakuEnka")) + idx;
                cell = PoiUtil.getCell(sheet, row, col);
                // 前月(四半期月)のセルを参照する計算式をセット
                cell.setCellFormula( PoiUtil.getCellReferenceStr(row, endColQ) );
                
                //// 売上高：今回：建値額
                row = convInteger(headStartMap.get("uriKonkai")) + idx;
                cell = PoiUtil.getCell(sheet, row, col);
                // 四半期の場合はSUM(過去３ヶ月の"売上高：今回：建値額")をセット
                // 期の場合はSUM(過去６ヶ月の"売上高：今回：建値額")をセット
                
                // 四半期
                if (kbn == 0) {
                    cell.setCellFormula( getQuarterCellFormula(row, startColQ, endColQ) );
                // 上期or下期
                } else if (kbn == 1) {
                    cell.setCellFormula( getKiCellFormula(row) );
                }
                
                //// 売上高：今回：売上為替レート
                // 表示せず
                
                //// 売上高：今回：円価
                row = convInteger(headStartMap.get("uriKonkai")) + (idx*2) + (currencyList.size()*2);
                cell = PoiUtil.getCell(sheet, row, col);
                // 四半期の場合はSUM(過去３ヶ月の"売上高：今回：円価")をセット
                // 期の場合はSUM(過去６ヶ月の"売上高：今回：円価")をセット
                // 四半期
                if (kbn == 0) {
                    cell.setCellFormula( getQuarterCellFormula(row, startColQ, endColQ) );
                // 上期or下期    
                } else if (kbn == 1) {
                    cell.setCellFormula( getKiCellFormula(row) );
                }
                
                //// 売上高：今回：円価：為替差調整
                row = convInteger(headStartMap.get("uriKonkai")) + (idx*2) + (currencyList.size()*2) + 1;
                cell = PoiUtil.getCell(sheet, row, col);
                boolean isSet = true;
                if (jpUnit.equals(currencyCode)) {  // 通貨JPYは為替差調整セットしない
                    isSet = false;
                }
                
                if (isSet) {
                    // 四半期の場合はSUM(過去３ヶ月の"売上高：今回：為替差調整")をセット
                    // 期の場合はSUM(過去６ヶ月の"売上高：今回：為替差調整")をセット
                    // 四半期
                    if (kbn == 0) {
                        cell.setCellFormula( getQuarterCellFormula(row, startColQ, endColQ) );
                    // 上期or下期    
                    } else if (kbn == 1) {
                        cell.setCellFormula( getKiCellFormula(row) );
                    }
                }
                
                //// 売上高：累計：建値額
                row = convInteger(headStartMap.get("uriRuikei")) + idx;
                cell = PoiUtil.getCell(sheet, row, col);
                // 前月(四半期月)のセルを参照する計算式をセット
                cell.setCellFormula( PoiUtil.getCellReferenceStr(row, col-colBefMonth) );
                
                
                //// 売上高：累計：円価
                row = convInteger(headStartMap.get("uriRuikei")) + idx + currencyList.size();
                cell = PoiUtil.getCell(sheet, row, col);
                // 前月(四半期月)のセルを参照する計算式をセット
                cell.setCellFormula( PoiUtil.getCellReferenceStr(row, col-colBefMonth) );         
                
            }    
        }
        
        
        //// 見積総原価：発番NET
        row  = convInteger(headStartMap.get("mitsumoriGenka"));
        cell = PoiUtil.getCell(sheet, row, col);
        // 前月(四半期月)のセルを参照する計算式をセット
        cell.setCellFormula( PoiUtil.getCellReferenceStr(row, col-colBefMonth) );         
        
        //// 見積総原価：未発番NET
        row  = convInteger(headStartMap.get("mitsumoriGenka")) + 1;
        cell = PoiUtil.getCell(sheet, row, col);
        // 前月(四半期月)のセルを参照する計算式をセット
        cell.setCellFormula( PoiUtil.getCellReferenceStr(row, col-colBefMonth) );         
        
        //// 見積総原価：製番損益
        row  = convInteger(headStartMap.get("mitsumoriGenka")) + 2;
        cell = PoiUtil.getCell(sheet, row, col);
        // 前月(四半期月)のセルを参照する計算式をセット
        cell.setCellFormula( PoiUtil.getCellReferenceStr(row, col-colBefMonth) );
        
        //// 見積総原価：製為替洗替影響
        row  = convInteger(headStartMap.get("mitsumoriGenka")) + 3;
        cell = PoiUtil.getCell(sheet, row, col);
        // 前月(四半期月)のセルを参照する計算式をセット
        cell.setCellFormula( PoiUtil.getCellReferenceStr(row, col-colBefMonth) );
        
        Integer categoryRowNum = null;
        //// 売上原価：今回：カテゴリ
        if (!cateTitleList.isEmpty()) {
            row = convInteger(headStartMap.get("uriGenka"));
            // カテゴリでループ
            for(int idx2=0; idx2<cateTitleList.size(); idx2++ ) {
                categoryRowNum = row + idx2;
                cell = PoiUtil.getCell(sheet, categoryRowNum, col);
                // 四半期の場合はSUM(過去３ヶ月の"売上原価：今回：カテゴリ")をセット
                // 期の場合はSUM(過去６ヶ月の"売上原価：今回：カテゴリ")をセット
                // 四半期
                if (kbn == 0) {
                    cell.setCellFormula( getQuarterCellFormula(categoryRowNum, startColQ, endColQ) );
                // 上期or下期    
                } else if (kbn == 1) {
                    cell.setCellFormula( getKiCellFormula(categoryRowNum) );
                }
            }
        }
        
        //// 売上原価・今回：合計
        if (categoryRowNum != null) {
            categoryRowNum = categoryRowNum + 1;
            cell = PoiUtil.getCell(sheet, categoryRowNum, col);
            // 四半期
            if (kbn == 0) {
                cell.setCellFormula( getQuarterCellFormula(categoryRowNum, startColQ, endColQ) );
            // 上期or下期    
            } else if (kbn == 1) {
                cell.setCellFormula( getKiCellFormula(categoryRowNum) );
            }
        }

        //// 売上原価：累計
        row  = convInteger(headStartMap.get("uriGenkaRuikei"));
        cell = PoiUtil.getCell(sheet, row, col);
        // 前月(四半期月)のセルを参照する計算式をセット
        cell.setCellFormula( PoiUtil.getCellReferenceStr(row, col-colBefMonth) );  
        
        // (2018A)ロスコン補正・引当情報データの埋め込み 
        if (isLossconData()) {
            // ロスコン情報対象行の開始行を取得(lossRowNumがロスコン開始行となる)
            Cell lossStartRowCell = getLossStartRowCell(sheet);
            int lossRowNum = lossStartRowCell.getRow().getRowNum();

            // 要素1: ロスコン補正(四半期 or 上期or下期 合計)の行番号
            // 要素2: 補正後の売上原価(今回)(四半期 or 上期or下期 合計)の行番号
            // 要素3: ロスコン引当(今回)(四半期 or 上期or下期 合計)の行番号
            //// 要素4: ロスコン引当(累計)(四半期 or 上期or下期 合計)の行番号 →　四半期や期合計では不正な値になるため別だし
            // ※ロスコンの粗利,M率はExcelテンプレート内で計算式設定済みなので、いじらないようにする
            //int[] lossRowNumArray = {lossRowNum, lossRowNum + 1, lossRowNum + 4, lossRowNum + 5};
            int[] lossRowNumArray = {lossRowNum, lossRowNum + 1, lossRowNum + 2, lossRowNum + 5};
            // 上記要素がロスコン関連の(四半期 or 上期or下期 合計)の合計行なので、以下でその合計計算式を埋め込む
            for (int i : lossRowNumArray) {
                cell = PoiUtil.getCell(sheet, i, col);
                // ロスコン補正関連はロスコン対象外の年月の粗利/M率計算をやらないようにするために、Q合計,K合計は該当セル全てが空白の場合は計算を行わないようにする(ExcelのCOUNTA関数で対応)
                // kbn:1→上期/下期のセル範囲 kbn:0→四半期のセル範囲
                String countaFunc = (kbn == 1 ?  getKiCellFormula(i, "COUNTA") : getQuarterCellFormula(i, startColQ, endColQ, "COUNTA"));
                String sumFunc = (kbn == 1 ?  getKiCellFormula(i) : getQuarterCellFormula(i, startColQ, endColQ));
                String excelFormula = "IF(" + countaFunc +"=0,\"\"," + sumFunc + ")";
                //cell.setCellFormula((kbn == 1 ?  getKiCellFormula(i) : getQuarterCellFormula(i, startColQ, endColQ)));
                cell.setCellFormula(excelFormula);
            }
            
            // ロスコン引当(累計) の計算式を埋め込み(対象列(4半期列,期合計列)の 最終月をそのまま参照)
            int lossHikiateTotalRow = lossRowNum + 6;
            String lossHikiateTotalCellNo = PoiUtil.getCellReferenceStr(lossHikiateTotalRow, col-colBefMonth);
            cell = PoiUtil.getCell(sheet, lossHikiateTotalRow, col);
            cell.setCellFormula("IF(COUNTA(" + lossHikiateTotalCellNo + ")=0,\"\"," + lossHikiateTotalCellNo + ")");
            //cell.setCellFormula(PoiUtil.getCellReferenceStr(lossHikiateTotalRow, col-colBefMonth));
            //setFormulaLossRuikeiTotalCell(sheet, cell);
        }
 
    }

    /**
     * 四半期の合計式"SUM(過去３ヶ月分)"の設定
     * @param row
     * @param col1
     * @param col2
     * @return ex.SUM(A1:C1)
     * @throws Exception 
     */
    private String getQuarterCellFormula(int row, int col1, int col2) throws Exception {
        return getQuarterCellFormula(row, col1, col2, "SUM");
    }
    private String getQuarterCellFormula(int row, int col1, int col2, String excelFunctionName) throws Exception {
        String strCellFormula;
        
        // SUM(過去３ヶ月の金額)の計算式を取得
        String cell1 = PoiUtil.getCellReferenceStr(row, col1);        
        String cell2 = PoiUtil.getCellReferenceStr(row, col2);

        //strCellFormula = "SUM(" + cell1 + ":" + cell2 + ")";
        strCellFormula = excelFunctionName + "(" + cell1 + ":" + cell2 + ")";
                    
        return strCellFormula;
    }
    
    /**
     * 上期・下期の合計式"SUM(過去６ヶ月分)"の設定
     * @param row
     * @return ex.SUM(A1:C1,E1:G1)
     * @throws Exception 
     */
    private String getKiCellFormula(int row) throws Exception {
        return getKiCellFormula(row, "SUM");
    }
    private String getKiCellFormula(int row, String excelFunctionName) throws Exception {
        String strCellFormula;
        
        // SUM(過去６ヶ月の金額)の計算式を取得
        String cellStartQuarter1 = PoiUtil.getCellReferenceStr(row, colQuarterSum1);        
        String cellEndQuarter1 = PoiUtil.getCellReferenceStr(row, colQuarterSum2);
        //String strQuarter1 = "SUM(" + cellStartQuarter1 + ":" + cellEndQuarter1;
        String strQuarter1 = excelFunctionName + "(" + cellStartQuarter1 + ":" + cellEndQuarter1;
        String strQuarter2 = "";
        if (colQuarterSum3 != null) {
            String cellStartQuarter2 = PoiUtil.getCellReferenceStr(row, colQuarterSum3);
            String cellEndQuarter2 = PoiUtil.getCellReferenceStr(row, colQuarterSum4);
            strQuarter2 = ", " + cellStartQuarter2 + ":" + cellEndQuarter2;
        }
        strCellFormula =  strQuarter1 + strQuarter2 + ")";

        return strCellFormula;
    }
    
    
    /**
     * ObjectをInteger型に変換
     * @param obj
     * @return
     * @throws Exception 
     */
    private Integer convInteger(Object obj) throws Exception {
        
        if (obj==null) {
            return null;
        }
        return new Integer(obj.toString()).intValue();
    }    

    /**
     * @return the divisionCode
     */
    public String getDivisionCode() {
        return divisionCode;
    }

    /**
     * @param divisionCode the divisionCode to set
     */
    public void setDivisionCode(String divisionCode) {
        this.divisionCode = divisionCode;
    }

    /**
     * 左部項目のデータ埋め込み(回収管理用) 
     */
    private void setLeftDataKaisyu(Sheet sheet, Sheet styleSheet) throws Exception {      
        
        int wkRow = sheet.getLastRowNum()-1;

        // 初期化
        headStartMap = new HashMap<>();
        mergedRegionMap = new HashMap<>();
        
        // 検索条件セット
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("rirekiFlg", (s004Bean.getRirekiFlg()));
        
        // 回収管理種類の取得        
        s004Bean.setKaisyuCurrencyList(downloadFacade.selectKaisyuCurrencyList(paramMap));

        //// 回収管理：通貨、税率、金種、前受、円の種類
        headStartMap.put("kaisyuCurrency", wkRow);

        setKaisyuTitle(sheet, wkRow, s004Bean.getKaisyuCurrencyList(), styleSheet);

        // 余分な行を削除(1行分)
        //sheet.removeRow(sheet.getRow(wkRow));
        //styleSheet.removeRow(styleSheet.getRow(wkRow));  
    }
    
    /**
     * 回収額が円貨(JPY)通貨のみであるかをチェック
     */
    private boolean isKaisyuEnkaOnly(List<S004DownloadKaisyuEntity> kaisyuTitleList) {
        boolean isEnka = true;
        for (int idx=0; idx<kaisyuTitleList.size(); idx++ ) {
            String currencyCode =  kaisyuTitleList.get(idx).getCurrencyCode();
            if (!ConstantString.currencyCodeEn.equals(currencyCode)) {
                isEnka = false;
                break;
            }
        }
        return isEnka;
    }

    /**
     * 回収管理の種類の埋め込み
     */
    private int setKaisyuTitle(Sheet sheet, int wkRow, List<S004DownloadKaisyuEntity> kaisyuTitleList, Sheet styleSheet) {
        
        Cell cell;
        String currencyCodeBef = "";
        int  curStartRow = wkRow;
        int  margeStartRow = wkRow;
        
        boolean isKaisyuEmpty = false;
        
        // (2018A)円貨(通貨JPY)のみの場合、契約・売上・回収の各通貨は小数点を表示しないようにする
        // そのために整数用フォーマットを用意する
        DataFormat intCellformat = styleSheet.getWorkbook().createDataFormat();

        if (!kaisyuTitleList.isEmpty()) {
            boolean isKaisyuEnkaOnly = isKaisyuEnkaOnly(kaisyuTitleList);
            
            for (int idx=0; idx<kaisyuTitleList.size(); idx++ ) {
                String currencyCode = kaisyuTitleList.get(idx).getCurrencyCode();
                String zei = kaisyuTitleList.get(idx).getZei();
                String zeiKbn = kaisyuTitleList.get(idx).getZeiKbn();
                String kinsyuKbn  = kaisyuTitleList.get(idx).getKinsyuKbn();
                String kaisyuKbn  = kaisyuTitleList.get(idx).getKaisyuKbn();
                String kbn = kaisyuTitleList.get(idx).getKbn();
                String rnk = kaisyuTitleList.get(idx).getRnk();
                
                // 通貨の結合
                if(!currencyCodeBef.equals(currencyCode)){
                    if(!currencyCodeBef.equals("")){
                        // セルの結合
                        sheet.addMergedRegion(new CellRangeAddress(curStartRow, wkRow-1, 0, 0)); 
                    }
                    curStartRow = wkRow;
                }
                currencyCodeBef = currencyCode;
                // 税率、金種、前受の結合
                //if (!rnk.equals("1")) {
                //    zeiKbn     = "";
                //    kinsyuKbn  = "";
                //    kaisyuKbn  = "";   
                //}else{
                if ("1".equals(rnk)) {
                    if(margeStartRow != wkRow){
                        // セルの結合
                        sheet.addMergedRegion(new CellRangeAddress(margeStartRow, wkRow-1, 1, 1));
                        sheet.addMergedRegion(new CellRangeAddress(margeStartRow, wkRow-1, 2, 2));
                        sheet.addMergedRegion(new CellRangeAddress(margeStartRow, wkRow-1, 3, 3));
                    }
                    margeStartRow = wkRow;
                }
                
                // 2行目以降は行追加（Excel計算式保持のため）
                if (idx!=0) {
                    sheet.shiftRows(wkRow, wkRow+1, 1);
                    sheet.createRow(wkRow);
                    styleSheet.shiftRows(wkRow, wkRow+1, 1);
                    styleSheet.createRow(wkRow);
                    // 行のコピー
                    PoiUtil.copyRowStyleValue(sheet.getRow(wkRow), sheet.getRow(wkRow+1), true);
                    PoiUtil.copyRowStyleValue(styleSheet.getRow(wkRow), styleSheet.getRow(wkRow+1), true);
                }
                // 通貨、税率、金種、前受、円種類をセット
                cell = PoiUtil.getCell(sheet, wkRow, 0);
                PoiUtil.setCellValue(cell, currencyCode);
                cell = PoiUtil.getCell(sheet, wkRow, 1);
                PoiUtil.setCellValue(cell, zeiKbn);
                cell = PoiUtil.getCell(sheet, wkRow, 2);
                PoiUtil.setCellValue(cell, getKaisyuKinsyuTitle(kinsyuKbn));
                cell = PoiUtil.getCell(sheet, wkRow, 3);
                PoiUtil.setCellValue(cell, getKaisyuKaisyuKbnTitle(kaisyuKbn));
                cell = PoiUtil.getCell(sheet, wkRow, 4);
                PoiUtil.setCellValue(cell, getKaisyuKbnTitleName(kbn));
                // セルに名前を設定
                // 単体だと名前が被るので、各コードを"￥"で繋いでセットする
                //PoiUtil.setCellName(sheet, "_" + currencyCode + "￥" + kbn + "￥" + idx, wkRow, 4 );
                PoiUtil.setCellName(sheet, "_" + currencyCode + "￥" + kbn + "￥" + zei + "￥" + kinsyuKbn + "￥" + kaisyuKbn, wkRow, 4);
                
                // (2018A)通貨が円貨(JPY)しか存在しない場合、小数点2桁を表示しないようにセルの書式設定を変更する
                // (スタイル用シート[kikanS_style]のF,G,H列にそれぞれ実績,見込,期(Q)合計要のテンプレートを用意しているため、この列に対してセットする)
                // 5列目:実績月用列 6列目:見込月用列 7列目:4半期/期合計要列
                if (isKaisyuEnkaOnly) {
                    for (int i=5; i<=7; i++) {
                        cell = PoiUtil.getCell(styleSheet, wkRow, i);
                        setCurrencyKaisyuEnkaMoneyFormat(cell, intCellformat);
                    }
                }

                wkRow++; //行加算
            }
            
            // 通貨の結合（最終行）
            if(margeStartRow != wkRow){
                sheet.addMergedRegion(new CellRangeAddress(curStartRow, wkRow-1, 0, 0)); 
            }            
            // 税率、金種、前受の結合（最終行）
            if(margeStartRow != wkRow){
                // セルの結合
                sheet.addMergedRegion(new CellRangeAddress(margeStartRow, wkRow-1, 1, 1));
                sheet.addMergedRegion(new CellRangeAddress(margeStartRow, wkRow-1, 2, 2));
                sheet.addMergedRegion(new CellRangeAddress(margeStartRow, wkRow-1, 3, 3));
            }

        } else {
            wkRow+=1;
            isKaisyuEmpty = true;
        }

        // 余分な行を削除(1行分)
        sheet.removeRow(sheet.getRow(wkRow));
        styleSheet.removeRow(styleSheet.getRow(wkRow));  
        if (isKaisyuEmpty) {
            // 回収データが存在しない場合は、更にもう一行削除しておく(前の行)
            // ↑テンプレートでは、回収データ空行を2行用意しているためです
            int emptyDelRow = wkRow - 1;
            sheet.removeRow(sheet.getRow(emptyDelRow));
            styleSheet.removeRow(styleSheet.getRow(emptyDelRow));  
        }

        return wkRow;
    }    

    /**
     * 回収管理欄 回収金額種別のタイトルを取得
     * @param kbn
     * @return 
     */
    private String getKaisyuKbnTitleName(String kbn) {
        String title = "";
        if ("GG".equals(kbn)) {
            title = "外貨";
        } else if ("GE".equals(kbn)) {
            title = "円貨";
        } else if ("EE".equals(kbn)) {
            title = "本体";
        } else if ("ZZ".equals(kbn)) {
            title = "税額";
        }
        return title;
    }

    /**
     * 回収管理欄 金種区分のタイトルを取得
     * @param kinsyuKbn
     * @return 
     */
    private String getKaisyuKinsyuTitle(String kinsyuKbn) {
        String kinsyuTitle = "";
        if ("1".equals(kinsyuKbn)) {
            kinsyuTitle = "現金";
        } else if ("2".equals(kinsyuKbn)) {
            kinsyuTitle = "手形";
        }
        return kinsyuTitle;
    }

    /**
     * 回収管理欄 回収区分のタイトルを取得
     * @param kaisyuKbn
     * @return 
     */
    private String getKaisyuKaisyuKbnTitle(String kaisyuKbn) {
        String kaisyuKbnTitle = "";
        if ("0".equals(kaisyuKbn)) {
            kaisyuKbnTitle = "通常";
        } else if ("1".equals(kaisyuKbn)) {
            kaisyuKbnTitle = "前受";
        }
        return kaisyuKbnTitle;
    }

    /**
     * 年月データの埋め込み(回収管理用) 
     */
    private void setDitailDataKaisyu(Sheet sheet, Sheet styleSheet) throws Exception {
        
        // 見込初月フラグ
        firstMikomiFlg = 0;
        // 完売月フラグ
        kanbaiFlg = 0;
        // 実績の範囲
        jissekiCol = 0;
        
        // 前月セルの位置補正用（四半期表示有で使用）
        colBefMonth = 0;
        // 過去３ヵ月SUM用の変数を初期化（四半期表示有で使用）
        cntQuarterSum = 0;
        // 過去６ヵ月SUM用の変数を初期化（四半期表示有で使用）
        colQuarterSum1 = null;
        colQuarterSum2 = null;
        colQuarterSum3 = null;
        colQuarterSum4 = null;
        
        String label = "";
        
        // 年月描画開始位置
        int col = YM_START_COL;       
        
        String dataKbn = "";
        
        // 検索条件セット
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("rirekiFlg", (s004Bean.getRirekiFlg()));

        // 年月を取得
        dateilHeader.setAnkenId(s004Bean.getAnkenId());
        dateilHeader.setRirekiId(s004Bean.getRirekiId());
        dateilHeader.setRirekiFlg(s004Bean.getRirekiFlg());
        List<String> nengetsuList = dateilHeader.findNengetsuList();
        
        // 完売年月(最終売上予定月)を取得
        String kanbaiMonth = "";
        if (CollectionUtils.isNotEmpty(nengetsuList)) {
            kanbaiMonth = nengetsuList.get(nengetsuList.size() - 1);
        }

        // 回収年月は完売月以降でも、システムで設定した月数カウント以降の月数分見込を設定可能とする(期間損益進行基準画面の同機能とは月数は別になっている)
        int kaisyuKanbaiMonthCount = Integer.parseInt(Env.Shinko_Excel_Kasyu_Input_KanbaiCount.getValue());
        //for(int idx=0; idx<ADD_YM; idx++){
        for(int idx=0; idx<kaisyuKanbaiMonthCount; idx++){
            Date dateYmFrom = Utils.parseDate(nengetsuList.get(nengetsuList.size()-1));
            Date nextMonthDate = DateUtils.addMonths(dateYmFrom, 1);
            nengetsuList.add(syuuekiUtils.exeFormatYm(nextMonthDate));
        }
               
        // 勘定月の取得(履歴を参照している場合は、履歴テーブルの勘定月を取得)
        //kanjoYm = kanjyoMstFacade.getNowKanjoDate();
        kanjoYm = StringUtils.replace(syuuekiUtils.exeFormatYm(dateilHeader.getKanjoDate()), "/", "");

        if (CollectionUtils.isNotEmpty(nengetsuList)) {           
            for(int idx=0; idx<nengetsuList.size() - 1; idx++){
                String nengetsu = nengetsuList.get(idx);

                // 実績or見込みの判定
                boolean jissekiFlg = syuuekiUtils.getJissekiFlg(Utils.parseDate(kanjoYm), nengetsu);
                if (jissekiFlg) {
                    dataKbn = "J";
                    jissekiCol++;
                } else {
                    dataKbn = "M";
                    firstMikomiFlg++;
                }
                // 年月ラベル(実績/見込/完売)を取得
                label = getKensyuYmLabel(nengetsu, kanbaiMonth, dataKbn);

                // 開始年月列からセルスタイル(計算式のみ)をコピー
                // データ描画最初の列の場合はコピーしない
                if (idx > 0) {
                    //PoiUtil.copyColumnStyleValue(sheet, col, sheet, col-1, 2);
                    PoiUtil.copyColumnStyleValue(sheet, col, sheet, YM_START_COL, ymStartRow, 2);
                    // 横幅を１つ前の列と同じにする
                    sheet.setColumnWidth(col, sheet.getColumnWidth(col-1));
                }

                // 年月項目をセット
                setYmData(sheet, nengetsu, label, col, true, YM_TITLE_K);
                // 月別金額をセット
                setMoneyDataKaisyu(sheet, dataKbn, nengetsu, col, styleSheet);
        
                col++;
                cntQuarterSum++;   // 四半期表示で使用
                
                colBefMonth = 0;
                //// 四半期列作成処理
                // 四半期表示フラグが１かつ四半期月の場合に行う
                if ("1".equals(dateilHeader.getQuarterDispFlg()) && isQuarter(nengetsu.replaceAll("/", ""))) {
                    label = getKensyuYmLabel(nengetsu, "", dataKbn);
                    col = createQuarterColKaisyu(sheet, dataKbn, nengetsu, label, col);
                }
            }
            
            ////////// 以降、最終検収年月を設定 ////////
            // セルスタイル(計算式のみ)をコピー
            //PoiUtil.copyColumnStyleValue(sheet, col, sheet, col-1, 2);
            PoiUtil.copyColumnStyleValue(sheet, col, sheet, YM_START_COL, ymStartRow, 2);
            // 横幅を１つ前の列と同じにする
            sheet.setColumnWidth(col, sheet.getColumnWidth(col-1));
            // 最終月のラベルは"完売"
            String nengetsu = nengetsuList.get(nengetsuList.size() - 1);
            if(syuuekiUtils.getJissekiFlg(Utils.parseDate(kanjoYm), nengetsu)) {
                dataKbn = "J";
            } else {
                dataKbn = "M";
            }
            // 年月ラベル(実績/見込/完売)を取得
            label = getKensyuYmLabel(nengetsu, kanbaiMonth, dataKbn);
            
            // 年月項目をセット
            setYmData(sheet, nengetsu, label, col, true, YM_TITLE_K);
            
            kanbaiFlg = 1;
            // 月別金額をセット
            setMoneyDataKaisyu(sheet, dataKbn, nengetsu, col, styleSheet);
            
            cntQuarterSum++;   // 四半期表示で使用
            col++;
            colBefMonth = 0;
            
            //// 四半期列作成処理
            // 四半期表示フラグが１の場合に行う
            if ("1".equals(dateilHeader.getQuarterDispFlg())) {
                label = getKensyuYmLabel(nengetsu, "", dataKbn);
                col = createQuarterColKaisyu(sheet, dataKbn, nengetsu, label, col);
            }
            
            lastCol = col;
        }
    }

    /**
     * 検収年月のラベルを取得
     */
    private String getKensyuYmLabel(String targetKensyuYm, String kanbaiYm, String dataKbn) {
        String label;
        if (StringUtils.equals(targetKensyuYm, kanbaiYm)) {
            // 完売年月と同一年月であれば"完売"ラベルにする
            label = "完売";
        } else {
            if ("J".equals(dataKbn)) {
                label = Label.getValue(Label.jisseki);
            } else {
                label = Label.getValue(Label.mikomi);
            }
        }
        return label;
    }

    /**
     * 月別金額データの埋め込み(回収管理用)
     */
    private void setMoneyDataKaisyu(Sheet sheet, String dataKbn, String nengetsu, int col, Sheet styleSheet) throws Exception {
       
        // 検索条件セット
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("rirekiFlg", s004Bean.getRirekiFlg());
        paramMap.put("dataKbn", dataKbn);
        paramMap.put("syuekiYm", nengetsu.replace("/", ""));
        paramMap.put("ditailFlg", "Y");
        
        // 回収管理データの埋め込み
        setDitailKaisyu(sheet, styleSheet, paramMap, col);
    }
    
    /**
     * 回収管理 金額データの埋め込み
     */
    private void setDitailKaisyu(Sheet sheet, Sheet styleSheet, Map<String, Object> paramMap, int col) throws Exception {
        
        Cell cell;
        Cell cellZei;
        Cell cellStyle;
        CellStyle orgStyle;
        String cellStr = "";
        int row;
        int rowS;
        
        List<Map<String, Object>> kaisyuTsukiList = downloadFacade.selectKaisyuTsuki(paramMap);
        
        if (kaisyuTsukiList != null) {
            row  = convInteger(headStartMap.get("kaisyuCurrency"));
            rowS = convInteger(headStartMap.get("kaisyuCurrency"))-1;
            for (int idx=0; idx<kaisyuTsukiList.size(); idx++) {
                
                // 回収管理：通貨、税率、金種、前受、円貨毎
                cell = PoiUtil.getCell(sheet, row, col);
                cell.setCellFormula(null);
                PoiUtil.setCellValue(cell, Utils.changeBigDecimal(Utils.getObjToStrValue(kaisyuTsukiList.get(idx).get("KAISYU_AMOUNT"))));

                // 税額の場合(本体(円貨)×税率)
                if ("ZZ".equals(Utils.getObjToStrValue(kaisyuTsukiList.get(idx).get("KBN")))){
                    // 計算式セット
//                    cell.setCellFormula(PoiUtil.getCellReferenceStr(row-1, col)+"*"+Utils.changeBigDecimal(Utils.getObjToStrValue(kaisyuTsukiList.get(idx).get("ZEI_RATE"))));
                    cell.setCellFormula("IF("+PoiUtil.getCellReferenceStr(row-1, col)+"=\"\",0,"+PoiUtil.getCellReferenceStr(row-1, col)+"*"+Utils.changeBigDecimal(Utils.getObjToStrValue(kaisyuTsukiList.get(idx).get("ZEI_RATE")))+")");
                    
                    // スタイルセット（税額は白）
                    cellStyle = PoiUtil.getCell(styleSheet, row, MIKOMI_STYLE_COL);
                    cellZei = PoiUtil.getCell(styleSheet, row, QUARTER_STYLE_COL);
                    orgStyle = cellZei.getCellStyle();
                    if (orgStyle != null) {
                        cellStyle.setCellStyle(orgStyle);
                    }
                }
                
                // 円貨／本体／税額の場合
                if ("GE".equals(Utils.getObjToStrValue(kaisyuTsukiList.get(idx).get("KBN"))) ||
                    "EE".equals(Utils.getObjToStrValue(kaisyuTsukiList.get(idx).get("KBN"))) ||
                    "ZZ".equals(Utils.getObjToStrValue(kaisyuTsukiList.get(idx).get("KBN"))) ){
                    if ("".equals(cellStr)){
                        cellStr = PoiUtil.getCellReferenceStr(row, col);
                    }else{
                        //cellStr = cellStr + ":" + PoiUtil.getCellReferenceStr(row, col);
                        cellStr = cellStr + "," + PoiUtil.getCellReferenceStr(row, col);
                    }
                }
                row++;
            }

            //合計(円貨)の計算式をセットする
            if (StringUtils.isNotEmpty(cellStr)) {
                cellStr = "SUM(" + cellStr  + ")";
                cell = PoiUtil.getCell(sheet, rowS, col);
                cell.setCellFormula(cellStr);
            }
        }
        
    }
    
    /**
     * 四半期、期のデータをセット(回収管理用)
     * @param sheet
     * @param dataKbn
     * @param col
     * @return
     * @throws Exception 
     */
    private int createQuarterColKaisyu(Sheet sheet, String dataKbn, String nengetsu, String label, int col) throws Exception {
        // 前列のセルスタイル(計算式のみ)をコピー
        //PoiUtil.copyColumnStyleValue(sheet, col, sheet, col-1, 2);
        PoiUtil.copyColumnStyleValue(sheet, col, sheet, YM_START_COL, ymStartRow, 2);
        // 横幅を１つ前の列と同じにする
        sheet.setColumnWidth(col, sheet.getColumnWidth(col-1));

        // 年月項目をセット
        setYmData(sheet, getQuarterLabel(nengetsu, 1), label, col, false, YM_TITLE_K);

        colBefMonth++;
        // 四半期分金額をセット
        setQuarterDataKaisyu(dataKbn, nengetsu, sheet, col, 0);
        // 過去３ヵ月SUM用変数を初期化
        cntQuarterSum = 0; 

        col++;

        // ９or３月もしくは完売月のときは、期列を追加
        if (isKi(nengetsu.replaceAll("/", "")) || kanbaiFlg == 1) {
            // 前列のセルスタイル(計算式のみ)をコピー
            //PoiUtil.copyColumnStyleValue(sheet, col, sheet, col-1, 2);
            PoiUtil.copyColumnStyleValue(sheet, col, sheet, YM_START_COL, ymStartRow, 2);
            // 横幅を１つ前の列と同じにする
            sheet.setColumnWidth(col, sheet.getColumnWidth(col-1));    

            // 年月項目をセット
            setYmData(sheet, getQuarterLabel(nengetsu, 0), label, col, false, YM_TITLE_K);

            colBefMonth++;
            // 四半期分金額をセット
            setQuarterDataKaisyu(dataKbn, nengetsu, sheet, col, 1);
            // 過去６ヵ月SUM用変数を初期化
            colQuarterSum1 = null;
            colQuarterSum2 = null;
            colQuarterSum3 = null;
            colQuarterSum4 = null;

            col++;           
        }

        return col;
    }
    
    /**
     * 四半期データをセット(回収管理用)
     * @param sheet
     * @param paramMap
     * @param col
     * @param kbn 0:四半期　1:期
     * @throws Exception 
     */
    private void setQuarterDataKaisyu(String dataKbn, String nengetsu, Sheet sheet, int col, int kbn) throws Exception{
        
        Cell cell;
        int row;

        int startColQ = col-cntQuarterSum;
        int endColQ = col-colBefMonth;
        
        // 四半期のときだけ行う
        // SUM(６ヶ月)用にCOLの位置を保持しておく
        // 下の変数を用いて「SUM(A1:C1,E1:G1)」のように計算式を作る
        if (kbn == 0) {
            if (colQuarterSum1 == null) {
                colQuarterSum1 = startColQ;
                colQuarterSum2 = endColQ;
            } else {
                colQuarterSum3 = startColQ;
                colQuarterSum4 = endColQ;
            }
        }
       
        Integer kaisyuRowNum = null;
        //// 回収管理：通貨、税率、金種、前受、円貨毎
        if (!s004Bean.getKaisyuCurrencyList().isEmpty()) {
            row = convInteger(headStartMap.get("kaisyuCurrency"));
            // カテゴリでループ
            for(int idx2=0; idx2<s004Bean.getKaisyuCurrencyList().size(); idx2++ ) {
                kaisyuRowNum = row + idx2;
                cell = PoiUtil.getCell(sheet, kaisyuRowNum, col);
                // 四半期の場合はSUM(過去３ヶ月)をセット
                // 期の場合はSUM(過去６ヶ月)をセット
                // 四半期
                if (kbn == 0) {
                    cell.setCellFormula( getQuarterCellFormula(kaisyuRowNum, startColQ, endColQ) );
                // 上期or下期    
                } else if (kbn == 1) {
                    cell.setCellFormula( getKiCellFormula(kaisyuRowNum) );
                }
            }
        }
        
//        //// 売上原価・今回：合計
//        if (categoryRowNum != null) {
//            categoryRowNum = categoryRowNum + 1;
//            cell = PoiUtil.getCell(sheet, categoryRowNum, col);
//            // 四半期
//            if (kbn == 0) {
//                cell.setCellFormula( getQuarterCellFormula(categoryRowNum, startColQ, endColQ) );
//            // 上期or下期    
//            } else if (kbn == 1) {
//                cell.setCellFormula( getKiCellFormula(categoryRowNum) );
//            }
//        }
//
//        //// 売上原価：累計
//        row  = convInteger(headStartMap.get("uriGenkaRuikei"));
//        cell = PoiUtil.getCell(sheet, row, col);
//        // 前月(四半期月)のセルを参照する計算式をセット
//        cell.setCellFormula( PoiUtil.getCellReferenceStr(row, col-colBefMonth) );  
    }

}
