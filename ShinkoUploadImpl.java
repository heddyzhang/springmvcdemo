
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.co.toshiba.hby.pspromis.syuueki.service.mikomiupload;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.co.toshiba.hby.pspromis.common.util.StringUtil;
import jp.co.toshiba.hby.pspromis.syuueki.bean.MikomiUploadBean;
import jp.co.toshiba.hby.pspromis.syuueki.bean.MikomiUploadErrorBean;
//import jp.co.toshiba.hby.pspromis.syuueki.dto.AnkenRecalDto;
import jp.co.toshiba.hby.pspromis.syuueki.entity.Cost;
import jp.co.toshiba.hby.pspromis.syuueki.entity.S004DownloadEntity;
import jp.co.toshiba.hby.pspromis.syuueki.entity.S004DownloadKaisyuEntity;
import jp.co.toshiba.hby.pspromis.syuueki.enums.Env;
import jp.co.toshiba.hby.pspromis.syuueki.enums.Label;
import jp.co.toshiba.hby.pspromis.syuueki.enums.MikomiUploadLabel;
import jp.co.toshiba.hby.pspromis.syuueki.facade.CategoryMapFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.KanjyoMstFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.S004DownloadFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuGeBukenInfoTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.interceptor.TranceInterceptor;
import jp.co.toshiba.hby.pspromis.syuueki.pages.DetailHeader;
import jp.co.toshiba.hby.pspromis.syuueki.service.StoredProceduresService;
import jp.co.toshiba.hby.pspromis.syuueki.util.AuthorityUtils;
import jp.co.toshiba.hby.pspromis.syuueki.util.ConstantString;
import jp.co.toshiba.hby.pspromis.syuueki.util.NumberUtils;
import jp.co.toshiba.hby.pspromis.syuueki.util.PoiUtil;
import jp.co.toshiba.hby.pspromis.syuueki.util.SyuuekiUtils;
import jp.co.toshiba.hby.pspromis.syuueki.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PS-Promis収益管理システム
 * 期間損益_進行アップロード処理
 * @author (NPC)kitajima
 */
@Stateless
@Interceptors({TranceInterceptor.class})
public class ShinkoUploadImpl implements UploadComponent {
 
    @PersistenceContext(unitName = jp.co.toshiba.hby.pspromis.syuueki.util.ConstantString.syuuekiDataSourceName)
    private EntityManager em;

    // タイトル
    private final String THIS_SHEET_TITLE = "期間損益_進行";
    // アップロード区分
    private final String THIS_UPLOAD_KBN = "KSS";
    // 強制アップロードフラグ
    private final String UPLOAD_FLG = "ON";
    
    // 日付フォーマット
    private final String DATE_PATTERN = "yyyy/MM/dd";
    
    // データの開始列
    private final int DATA_START_ROW = 10;
    // データの開始行
    private final int DATA_START_COL = 6;
    
    // 見出し行
    private final int HEADLINE_COL = 5;
    
    // 見積総原価：発番NETのセル名称
    private final String CELL_NAME_HAT_NET = "HAT_NET";
    // 見積総原価：未発番NETのセル名称
    private final String CELL_NAME_MI_HAT_NET = "MI_HAT_NET";
    // 見積総原価：製番損益のセル名称
    private final String CELL_NAME_SEIBAN_SONEKI = "SEIBAN_SONEKI";
    // 見積総原価：為替洗替影響
    private final String CELL_NAME_KAWASE_EIKYO = "KAWASE_EIKYO";
    
    // 税額
    //private final String ZEIGAKU = "税額";
    private final String ZEIGAKU = "ZZ";
    // 外貨
    //private final String GAIKA = "外貨";
    private final String GAIKA = "GG'";
    // 円貨
    //private final String ENKA = "円貨";
    private final String ENKA = "GE";
    // 回収管理 年月表示の加算分
    //private final int ADD_YM = 3;

    /**
     * ロガー
     */
    public static final Logger logger = LoggerFactory.getLogger(ShinkoUploadImpl.class);
    
    @Inject
    private MikomiUploadBean mikomiUploadBean;
    
    @Inject
    private MikomiUploadErrorBean mikomiUploadErrorBean;
    
    
    @Inject
    private UploadDataAccess uploadDataAccess;
    
    @Inject
    private KanjyoMstFacade kanjyoMstFacade;
    
    /**
     * Injection DetailHeader
     */
    @Inject
    private DetailHeader dateilHeader;
    
    /**
     * Injection syuuekiUtils
     */
    @Inject
    private SyuuekiUtils syuuekiUtils;
    
    @Inject
    private S004DownloadFacade downloadFacade;
    
    @Inject
    private SyuGeBukenInfoTblFacade geBukenInfoTblFacade;
    
    @Inject
    private StoredProceduresService storedProceduresService;
    
    @Inject
    private CategoryMapFacade categoryMapFacade;
        
    @Inject
    private AuthorityUtils authorityUtils;
    
    /**
     * 処理対象ワークシートの取得
     */
    private Sheet getTargetSheet() {
        Sheet sheet = (mikomiUploadBean.getWorkBook()).getSheetAt(0);
        return sheet;
    }
    
    /**
     * 入力内容のデータチェック
     * @return 
     * @throws java.lang.Exception
     */
    @Override
    public boolean isDataCheck() throws Exception {
        logger.info("ShinkoUploadImpl isDataCheck");
        
        boolean isCheck = false; 
        
        // 作業対象シートを取得
        Sheet targetSheet = getTargetSheet();
        
        // Excelタイトルチェック
        isCheck = isCheckTitile(targetSheet);
        
        // 案件番号チェック
        if (isCheck) {
            isCheck = isCheckAnken(targetSheet);
        }
        
        // Excelの出力日チェック
        if (isCheck) {
            isCheck = isCheckDate(targetSheet);
        }
        
        // Excel上の一覧を読み取って、DBに登録する値の取得/エラーチェックを行う。
        if (isCheck) {
            isCheck = exeGetData(targetSheet);
        }

        // beanに成功/失敗FLGをセット
        mikomiUploadErrorBean.setIsSuccess(isCheck);

        
        return isCheck;
    }

    /**
     * Excelのタイトル
     */
    private boolean isCheckTitile(Sheet sheet) {
        String errorMessageTitle = MikomiUploadLabel.getValue(MikomiUploadLabel.titleFormatError);
        Cell cellTitle = PoiUtil.getCell(sheet, 0, 0);
        String title = (String)PoiUtil.getCellValue(cellTitle);
        
        boolean isSuccess = false;
        
        String kbn = StringUtils.defaultString(mikomiUploadBean.getUploadKbn());
        
        // タイトルチェック
        if (!StringUtil.isEmpty(title)) {
            // 一致するか
            if (title.equals(this.THIS_SHEET_TITLE) && kbn.equals(this.THIS_UPLOAD_KBN)) {
                isSuccess = true;
            }
        }
        
        if (!isSuccess) {
            mikomiUploadErrorBean.addErrorMessage(errorMessageTitle);
        }
        
        return isSuccess;
        
    }
    
    /**
     * 案件番号チェック
     */
     private boolean isCheckAnken(Sheet sheet) {
        String errorMessageAnken = MikomiUploadLabel.getValue(MikomiUploadLabel.ankenFormatError);
        // 案件番号
        Cell cellAnken = PoiUtil.getCell(sheet, 1, 1);
        String ankenId = (String)PoiUtil.getCellValue(cellAnken);
        // 注番
        /*
        Cell cellOrderNo = PoiUtil.getCell(sheet, 2, 0);
        String orderNo = (String)PoiUtil.getCellValue(cellOrderNo);
        orderNo = orderNo.replaceAll("注番:", "");
        */
        
        boolean isSuccess = false;
        
        String kbn = StringUtils.defaultString(mikomiUploadBean.getUploadKbn());
        // 画面の案件番号
        String dispAnken = StringUtils.defaultString(mikomiUploadBean.getAnkenId());

        // 案件番号チェック
        if (!StringUtil.isEmpty(ankenId)) {
            // 画面の案件番号と等しいか
            if (ankenId.equals(dispAnken) && kbn.equals(this.THIS_UPLOAD_KBN)) {
                isSuccess = true;
            }
        }
        
        if (!isSuccess) {
            mikomiUploadErrorBean.addErrorMessage(errorMessageAnken + "(" + ankenId + ")");
        }
        
        return isSuccess;
        
    }
    
    
    /**
     * Excelの出力日時チェック
     */
    private boolean isCheckDate(Sheet sheet) throws Exception {
        String errorMessageFormat = MikomiUploadLabel.getValue(MikomiUploadLabel.dataFormatError);
        String errorMessageDate = MikomiUploadLabel.getValue(MikomiUploadLabel.outputDateError);
        // 強制フラグ
        Cell cellFlg = PoiUtil.getCell(sheet, 1, 4);
        String flg = (String)PoiUtil.getCellValue(cellFlg);
        // 案件番号
        Cell cellAnken = PoiUtil.getCell(sheet, 1, 1);
        String ankenId = (String)PoiUtil.getCellValue(cellAnken);
        // 出力日時
        Cell cellOutputDate = PoiUtil.getCell(sheet, 0, 4);
        String outputDate = (String)PoiUtil.getCellValue(cellOutputDate);
        
        String strUpdatedAt = "";
        String strOutputDateYmd = "";

        boolean isSuccess = false;
        
        dateilHeader.setAnkenId(ankenId);
        dateilHeader.setRirekiId(mikomiUploadBean.getRirekiId());
        dateilHeader.findAnkenPk();
        Date updatedAt = dateilHeader.getAnkenEntity().getUpdatedAt();
        if (updatedAt == null) {
            // 2018A不具合修正 更新日時が未登録の場合、バッチ更新日時を利用
            updatedAt = dateilHeader.getAnkenEntity().getUpdatedBatchAt();
        }

        String orderNo = "";
        if ("1".equals(dateilHeader.getAnkenEntity().getAnkenFlg())) {
            orderNo = dateilHeader.getAnkenEntity().getMainOrderNo();
        } else {
            orderNo = dateilHeader.getAnkenEntity().getOrderNo();
        }
        
        // 正常終了時のため、結果子画面に出力するメッセージ(案件番号/注番)を登録    
        mikomiUploadErrorBean.addMessage("■" + ankenId + "/" + orderNo);
        
        
        // 強制フラグのチェック
        // 強制フラグがONの場合は、出力日時のチェックをスルー
        if (!flg.isEmpty()) {
            // 一致するか
            if (this.UPLOAD_FLG.equals(StringUtils.upperCase(flg))) {
                isSuccess = true;
                return isSuccess;
            }
        }
        
        if (!outputDate.isEmpty()) {
            try{
                Date outputDateYmd = Utils.parseDate(outputDate, DATE_PATTERN);
                strOutputDateYmd = new SimpleDateFormat(DATE_PATTERN).format(outputDateYmd);
            }catch(Exception e){
                mikomiUploadErrorBean.addErrorMessage(errorMessageFormat);
                return isSuccess;
            }
        }
        
        // 2018A不具合修正 更新日時が未登録の場合はアップロードOKとする
        if (updatedAt == null) {
            return true;
        }

        // 出力日時のチェック
        if (!isSuccess) {
            logger.info("updatedAt=[{}], strOutputDateYmd={[]}", updatedAt, strOutputDateYmd);
            if (updatedAt != null && !strOutputDateYmd.isEmpty()) {
                strUpdatedAt = new SimpleDateFormat(DATE_PATTERN).format(updatedAt);
                if (strUpdatedAt.compareTo(strOutputDateYmd) <= 0) {
                    isSuccess = true;
                } else {
                    mikomiUploadErrorBean.addErrorMessage(errorMessageDate + "(" + strOutputDateYmd + ")");
                }
            }
        }
        
        return isSuccess;
    }
    
    
    /**
     * Excel上の一覧を読み取って、DBに登録する値の取得/エラーチェックを行う
     */
    private boolean exeGetData(Sheet sheet) throws Exception {
        
        boolean isSuccess = true;
        
        // 案件番号
        Cell ankenCell = PoiUtil.getCell(sheet, 1, 1);
        String ankenId = (String)PoiUtil.getCellValue(ankenCell);
        
        String amount = "";
        
        // 勘定月
        String kanjoYm = "";
        
        // 行位置保持用
        ArrayList<Map<String, Object>> currencyRowList = new ArrayList<>();
        ArrayList<Map<String, Object>> cateRowList = new ArrayList<>();
        ArrayList<Map<String, Object>> kaisyuRowList = new ArrayList<>();

        Integer hatNetRow = null;
        Integer miHatNetRow = null;
        Integer seibanSonekiRow = null;
        Integer kawaseEikyoRow = null;

        // 検索条件のセット
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", ankenId);
        paramMap.put("rirekiId", Integer.parseInt(mikomiUploadBean.getRirekiId()));    
        
        // 勘定月の取得
        kanjoYm = kanjyoMstFacade.getNowKanjoDate(dateilHeader.getAnkenEntity().getSalesClass());
        
        // 通貨種類(受注用)の取得        
        List<Map<String, Object>> jyuchuCurrencyList = downloadFacade.selectJyuchuCurrencyList(paramMap);
        // カテゴリ種類を取得(受注NetのKEY検索用)
        Map categoryMap = categoryMapFacade.findCategoryMap(ConstantString.ikkatsuCategoryCode);
        
        // 対象案件が持つ、通貨種類を取得  
        List<S004DownloadEntity> currencyList = downloadFacade.selectCurrencyList(paramMap);
        
        // 対象案件が持つ、売上原価種類を取得
        List<Cost> cateTitleList = downloadFacade.selectCateTitleList(paramMap);

        // 対象案件が持つ、回収管理種類を取得
        List<S004DownloadKaisyuEntity> kaisyuCurrencyList = (downloadFacade.selectKaisyuCurrencyList(paramMap));
        
        // 年月リストを取得
        dateilHeader.setAnkenId(ankenId);
        dateilHeader.setRirekiId(mikomiUploadBean.getRirekiId());
        List<String> nengetsuList = dateilHeader.findNengetsuList();
        List<String> nengetsuList_K = new ArrayList<>(nengetsuList);
        
        // 回収年月は完売月以降でも、システムで設定した月数カウント以降の月数分見込を設定可能とする(期間損益進行基準画面の同機能とは月数は別になっている)
        int kaisyuKanbaiMonthCount = Integer.parseInt(Env.Shinko_Excel_Kasyu_Input_KanbaiCount.getValue());
        //for(int idx=0; idx<ADD_YM; idx++){
        for (int idx=0; idx<kaisyuKanbaiMonthCount; idx++) {
            Date dateYmFrom = Utils.parseDate(nengetsuList_K.get(nengetsuList_K.size()-1));
            Date nextMonthDate = DateUtils.addMonths(dateYmFrom, 1);
            nengetsuList_K.add(syuuekiUtils.exeFormatYm(nextMonthDate));
        }
        
        //// 各キー項目の行位置を取得
        // 各通貨の行位置
        if (!currencyList.isEmpty()) {
            for (int idx=0; idx<currencyList.size(); idx++) {
                String currencyCode = currencyList.get(idx).getCurrencyCode();
                // 名称からセルを取得
                Cell cellCurrencyKey = searchCellName(sheet, "_" + currencyCode);
                // セルが取得できた場合、行位置をリストに保持
                if (cellCurrencyKey!=null) {
                    int rowIdx = cellCurrencyKey.getRowIndex();
                    Map<String, Object> currencyRowItem = new HashMap<>();
                    currencyRowItem.put("currencyCode", cellCurrencyKey);
                    currencyRowItem.put("rowIdx", rowIdx);
                    currencyRowList.add(currencyRowItem);
                }
            }
        }
        
        // 売上原価種類の行位置
        if (!cateTitleList.isEmpty()) {
            for (int idx=0; idx<cateTitleList.size(); idx++) {
                String cateCode = cateTitleList.get(idx).getCategoryCode();
                String cateKbn1 = cateTitleList.get(idx).getCategoryKbn1();
                String cateKbn2 = cateTitleList.get(idx).getCategoryKbn2();
                
                // 名称からセルを取得
                Cell cellCateKey = searchCellName(sheet, "_"+cateCode+"￥"+cateKbn1+"￥"+cateKbn2);
                // セルが取得できた場合、行位置をリストに保持
                if (cellCateKey!=null) {
                    int rowIdx = cellCateKey.getRowIndex();
                    Map<String, Object> cateRowItem = new HashMap<>();
                    
                    cateRowItem.put("categoryCode", cateCode);
                    cateRowItem.put("categoryKbn1", cateKbn1);
                    cateRowItem.put("categoryKbn2", cateKbn2);
                    cateRowItem.put("rowIdx", rowIdx);
                    cateRowList.add(cateRowItem);
                }
            }
        }
        
        //// 見積総原価：発番NET＆製番損益の行位置
        // 名称からセルを取得
        Cell cellHatNetKey = searchCellName(sheet, "_"+CELL_NAME_HAT_NET);
        // セルが取得できた場合、行位置をリストに保持
        if (cellHatNetKey!=null) {
            hatNetRow = cellHatNetKey.getRowIndex();
        }
        //// 見積総原価：未発番NET＆製番損益の行位置
        // 名称からセルを取得
        Cell cellMiHatNetKey = searchCellName(sheet, "_"+CELL_NAME_MI_HAT_NET);
        // セルが取得できた場合、行位置をリストに保持
        if (cellMiHatNetKey!=null) {
            miHatNetRow = cellMiHatNetKey.getRowIndex();
        }
        // 名称からセルを取得
        Cell cellSeiSonKey = searchCellName(sheet, "_"+CELL_NAME_SEIBAN_SONEKI);
        // セルが取得できた場合、行位置をリストに保持
        if (cellSeiSonKey!=null) {
            seibanSonekiRow = cellSeiSonKey.getRowIndex();
        }
        
        // 名称からセルを取得
        Cell kawaseEikyoKey = searchCellName(sheet, "_"+CELL_NAME_KAWASE_EIKYO);
        // セルが取得できた場合、行位置をリストに保持
        if (kawaseEikyoKey!=null) {
            kawaseEikyoRow = kawaseEikyoKey.getRowIndex();
        }

        // 回収管理種類の行位置
        if (!kaisyuCurrencyList.isEmpty()) {
            for (int idx=0; idx<kaisyuCurrencyList.size(); idx++) {
                String currencyCode = kaisyuCurrencyList.get(idx).getCurrencyCode();
                //String zei = kaisyuCurrencyList.get(idx).getZei();
                //String zeiKbn = kaisyuCurrencyList.get(idx).getZeiKbn();
                String zeiKbn = kaisyuCurrencyList.get(idx).getZei();
                String kinsyuKbn  = kaisyuCurrencyList.get(idx).getKinsyuKbn();
                String kaisyuKbn  = kaisyuCurrencyList.get(idx).getKaisyuKbn();
                String kbn = kaisyuCurrencyList.get(idx).getKbn();
                String rnk = kaisyuCurrencyList.get(idx).getRnk();
                
                // 名称からセルを取得
                //Cell cellKaisyuKey = searchCellName(sheet, "_" + currencyCode + "￥" + kbn + "￥" + idx);
                Cell cellKaisyuKey = searchCellName(sheet, "_" + currencyCode + "￥" + kbn + "￥" + zeiKbn + "￥" + kinsyuKbn + "￥" + kaisyuKbn);
                // セルが取得できた場合、行位置をリストに保持
                if (cellKaisyuKey!=null) {
                    int rowIdx = cellKaisyuKey.getRowIndex();
                    Map<String, Object> kaisyuRowItem = new HashMap<>();
                    
                    kaisyuRowItem.put("currencyCode", currencyCode);
                    kaisyuRowItem.put("zei", zeiKbn);
                    kaisyuRowItem.put("kinsyu", kinsyuKbn);
                    kaisyuRowItem.put("kaisyu", kaisyuKbn);
                    kaisyuRowItem.put("kbn", kbn);
                    kaisyuRowItem.put("rnk", rnk); 
                    kaisyuRowItem.put("rowIdx", rowIdx);
                    kaisyuRowList.add(kaisyuRowItem);                    
                    
                }
            }
        }
        
        // 権限チェック
        // editAllFlg ⇒ 契約金額：通貨：補正、見積総原価：未発番NET＆製番損益、売上原価カテゴリ
        // editNetFlg ⇒ 売上原価カテゴリ
        boolean editAllFlg = authorityUtils.enableFlg("KIKAN_S_EDITALL", dateilHeader.getDivisionCode(), "0")==1?true:false;
        boolean editNetFlg = authorityUtils.enableFlg("KIKAN_S_EDITNET", dateilHeader.getDivisionCode(), "0")==1?true:false;        

        if (!nengetsuList.isEmpty()) {
            for (int idx=0;idx<nengetsuList.size(); idx++) {
                
                String nengetsu = nengetsuList.get(idx);
                // 実績or見込みの判定
                boolean jissekiFlg = syuuekiUtils.getJissekiFlg(Utils.parseDate(kanjoYm), nengetsu);
        
                // 見込の場合、データを取得する（範囲：勘定月～完売月）
                if (!jissekiFlg) {
                    // 年月からセルの行位置を取得
                    Cell cellYm = searchCellName(sheet, "_"+nengetsu.replaceAll("/", ""));
                    if (cellYm!=null) {
                        int colIdx = cellYm.getColumnIndex();

                        //// 上記で取得した行位置と事前に取得していた列位置で、セルの値を取得する
                        if (editAllFlg) {
                            
                            // アップロード用の受注情報のチェックと設定
                            if (!setJyuchuDataForUpd(sheet, colIdx, ankenId, "M", nengetsu, jyuchuCurrencyList, categoryMap)) {
                                 isSuccess = false;
                            }
                            
                            // 通貨
                            for (int currencyIdx=0; currencyIdx<currencyRowList.size(); currencyIdx++) {
                                int rowCurrencyIdx = convInteger(currencyRowList.get(currencyIdx).get("rowIdx"));
                                Cell cellCurrency = PoiUtil.getCell(sheet,rowCurrencyIdx, colIdx);
                                String currencyCode = currencyRowList.get(currencyIdx).get("currencyCode").toString();
                                String hosei = Utils.getObjToStrValue(PoiUtil.getCellValue(cellCurrency));

                                // 注入金額の入力チェック
                                amount = changeAmountStr(hosei, 2);

                                boolean currencyFlg = chkAmountError(amount, hosei, rowCurrencyIdx, nengetsu);
                                if (currencyFlg) {
                                    Map<String, Object> updCurrencyItem = new HashMap<>();
                                    updCurrencyItem.put("ankenId", ankenId);
                                    updCurrencyItem.put("rirekiId", Integer.parseInt(mikomiUploadBean.getRirekiId()));
                                    updCurrencyItem.put("dataKbn", "M");
                                    updCurrencyItem.put("syuekiYm", nengetsu.replaceAll("/", ""));
                                    updCurrencyItem.put("currencyCode", currencyCode);
                                    updCurrencyItem.put("keiyakuHoseiAmount", Utils.changeBigDecimal((String)amount));
                                    //updCurrencyList.add(updCurrencyItem);
                                    mikomiUploadBean.addDataList(updCurrencyItem);
                                } else {
                                    isSuccess = false;
                                }   
                            }
                        
                        
                            // 売上総原価
                            boolean hatNetFlg = true;
                            boolean miHatNetFlg = true;
                            boolean seibanSonekiFlg = true;
                            boolean kawaseEikyoFlg = true;
                            String amountHatNet = null;
                            String amountMiHatNet = null;
                            String amountSeibanSoneki = null;
                            String amountkawaseEikyo = null;

                            if (hatNetRow != null) {
                                Cell cellHatNet = PoiUtil.getCell(sheet,hatNetRow, colIdx);
                                String hatNet = Utils.getObjToStrValue(PoiUtil.getCellValue(cellHatNet));

                                // 注入金額の入力チェック
                                amountHatNet = changeAmountStr(hatNet);
                                hatNetFlg = chkAmountError(amountHatNet, hatNet, hatNetRow, nengetsu);
                            }
                            if (miHatNetRow != null) {
                                Cell cellMiHatNet = PoiUtil.getCell(sheet,miHatNetRow, colIdx);
                                String miHatNet = Utils.getObjToStrValue(PoiUtil.getCellValue(cellMiHatNet));

                                // 注入金額の入力チェック
                                amountMiHatNet = changeAmountStr(miHatNet);
                                miHatNetFlg = chkAmountError(amountMiHatNet, miHatNet, miHatNetRow, nengetsu);
                            }
                            if (seibanSonekiRow != null) {
                                Cell cellSeibanSoneki = PoiUtil.getCell(sheet,seibanSonekiRow, colIdx);
                                String seibanSoneki = Utils.getObjToStrValue(PoiUtil.getCellValue(cellSeibanSoneki));

                                // 注入金額の入力チェック
                                amountSeibanSoneki = changeAmountStr(seibanSoneki);
                                seibanSonekiFlg = chkAmountError(amountSeibanSoneki, seibanSoneki, seibanSonekiRow, nengetsu);
                            }
                            if (kawaseEikyoRow != null) {
                                Cell cellkawaseEikyo = PoiUtil.getCell(sheet,kawaseEikyoRow, colIdx);
                                String kawaseEikyo = Utils.getObjToStrValue(PoiUtil.getCellValue(cellkawaseEikyo));

                                // 注入金額の入力チェック
                                amountkawaseEikyo = changeAmountStr(kawaseEikyo);
                                kawaseEikyoFlg = chkAmountError(amountkawaseEikyo, kawaseEikyo, kawaseEikyoRow, nengetsu);
                            }

                            if (hatNetFlg && miHatNetFlg && seibanSonekiFlg && kawaseEikyoFlg) {
                                Map<String, Object> updSogenkaItem = new HashMap<>();
                                updSogenkaItem.put("ankenId", ankenId);
                                updSogenkaItem.put("rirekiId", Integer.parseInt(mikomiUploadBean.getRirekiId()));
                                updSogenkaItem.put("dataKbn", "M");
                                updSogenkaItem.put("syuekiYm", nengetsu.replaceAll("/", ""));
                                updSogenkaItem.put("hatNet", Utils.changeBigDecimal((String)amountHatNet));
                                updSogenkaItem.put("miHatNet", Utils.changeBigDecimal((String)amountMiHatNet));
                                updSogenkaItem.put("seibanSoneki", Utils.changeBigDecimal((String)amountSeibanSoneki));
                                updSogenkaItem.put("kawaseEikyo", Utils.changeBigDecimal((String)amountkawaseEikyo));

                                //updSogenkaList.add(updSogenkaItem);
                                mikomiUploadBean.addDataList2(updSogenkaItem);
                            } else {
                                isSuccess = false;
                            }
                            
                        }
                        
                        
                        if (editNetFlg || editAllFlg) {
                            // 売上原価種類
                            for (int cateIdx=0; cateIdx<cateRowList.size(); cateIdx++) {
                                int rowCateIdx = convInteger(cateRowList.get(cateIdx).get("rowIdx"));
                                Cell cellCate = PoiUtil.getCell(sheet,rowCateIdx, colIdx);
                                String cateCode = cateRowList.get(cateIdx).get("categoryCode").toString();
                                String cateKbn1 = cateRowList.get(cateIdx).get("categoryKbn1").toString();
                                String cateKbn2 = cateRowList.get(cateIdx).get("categoryKbn2").toString();

                                String cateNet = Utils.getObjToStrValue(PoiUtil.getCellValue(cellCate));

                                // 注入金額の入力チェック
                                amount = changeAmountStr(cateNet);
                                boolean cateFlg = chkAmountError(amount, cateNet, rowCateIdx, nengetsu);
                                if (cateFlg) {
                                    Map<String, Object> updCateItem = new HashMap<>();
                                    updCateItem.put("ankenId", ankenId);
                                    updCateItem.put("rirekiId", Integer.parseInt(mikomiUploadBean.getRirekiId()));
                                    updCateItem.put("dataKbn", "M");
                                    updCateItem.put("syuekiYm", nengetsu.replaceAll("/", ""));
                                    updCateItem.put("categoryCode", cateCode);
                                    updCateItem.put("categoryKbn1", cateKbn1);
                                    updCateItem.put("categoryKbn2", cateKbn2);
                                    updCateItem.put("net", Utils.changeBigDecimal((String)amount));
                                    updCateItem.put("updateKbn", THIS_UPLOAD_KBN);
                                    //updCateList.add(updCateItem);
                                    mikomiUploadBean.addDataList3(updCateItem);
                                } else {
                                    isSuccess = false;
                                }    
                            }
                        }
                          
                    }
                }
            }
        }
        // 最終見込の列の位置を取得
        Cell cellLastMikomi = searchCellName(sheet, "_999900F");
        // セールを取得できた場合
        if (cellLastMikomi != null) {
            // 列の位置を取得
            int colIdx = cellLastMikomi.getColumnIndex();
            
            // アップロード用の受注情報のチェックと設定
            if (!setJyuchuDataForUpd(sheet, colIdx, ankenId, "F", "999900F", jyuchuCurrencyList, categoryMap)) {
                 isSuccess = false;
            }
        }
        // 回収管理用
        if (!nengetsuList_K.isEmpty()) {
            for (int idx=0;idx<nengetsuList_K.size(); idx++) {
                
                String nengetsu = nengetsuList_K.get(idx);
                // 実績or見込みの判定
                boolean jissekiFlg = syuuekiUtils.getJissekiFlg(Utils.parseDate(kanjoYm), nengetsu);
        
                // 見込の場合、データを取得する（範囲：勘定月～完売月）
                if (!jissekiFlg) {
                    // 年月からセルの行位置を取得
                    Cell cellYm_K = searchCellName(sheet, "_"+nengetsu.replaceAll("/", "")+"_K");
                    if (cellYm_K!=null) {
                        int colIdx = cellYm_K.getColumnIndex();
                        
//                        if (editNetFlg || editAllFlg) {
                            // 外貨円貨保持用
                            BigDecimal gaiAmount = null;
                            BigDecimal enkaAmount = null;
                            // 回収管理種類
                            for (int kaiIdx=0; kaiIdx<kaisyuRowList.size(); kaiIdx++) {
                                int rowKaiIdx = convInteger(kaisyuRowList.get(kaiIdx).get("rowIdx"));
                                Cell cellKaisyu = PoiUtil.getCell(sheet,rowKaiIdx, colIdx);
                                String currencyCode = kaisyuRowList.get(kaiIdx).get("currencyCode").toString();
                                String zei = kaisyuRowList.get(kaiIdx).get("zei").toString();
                                String kinsyu = kaisyuRowList.get(kaiIdx).get("kinsyu").toString();
                                String kaisyu = kaisyuRowList.get(kaiIdx).get("kaisyu").toString();
                                String kbn = kaisyuRowList.get(kaiIdx).get("kbn").toString();

                                String kaisyuNet = Utils.getObjToStrValue(PoiUtil.getCellValue(cellKaisyu));                                
                                
                                // 注入金額の入力チェック
                                amount = changeAmountStr(kaisyuNet, 2);
                                boolean kaisyuFlg = chkAmountError(amount, kaisyuNet, rowKaiIdx, nengetsu);
                                if (kaisyuFlg) {

                                    // 税額も場合、登録しない
                                    if(ZEIGAKU.equals(kbn)){
                                        continue;
                                    }
                                    // JPY以外の場合                                  
                                    if(ENKA.equals(kbn)){                                     
                                        enkaAmount = Utils.changeBigDecimal(amount);
                                    }else{
                                        gaiAmount = Utils.changeBigDecimal(amount);
                                    }
                                    // 外貨の場合はセットのみで次のセル(円貨)へ
                                    if(GAIKA.equals(kbn)){                                        
                                        continue;
                                    }
                                    // 回収円貨額                                    
                                    if (ConstantString.currencyCodeEn.equals(currencyCode)) {
                                        // 円貨通貨(JPY)の場合は外貨・円貨の額は同一とする(画面上の入力欄も1つになっている).
                                        enkaAmount = Utils.changeBigDecimal(amount);
                                    }
                                    // 回収レート
                                    BigDecimal kaisyuRate = NumberUtils.div(enkaAmount, gaiAmount, 7);
                                    // 回収レートがDBへ格納可能な桁数を超えてしまう場合はnullにする。
                                    if (kaisyuRate != null) {
                                        BigDecimal ngKaisyuRateMini = new BigDecimal(ConstantString.ngKaisyuRateMini);
                                        if (kaisyuRate.compareTo(ngKaisyuRateMini) >= 0) {
                                            kaisyuRate = null;
                                        }
                                    }

                                    Map<String, Object> updKaisyuItem = new HashMap<>();
                                    updKaisyuItem.put("ankenId", ankenId);
                                    updKaisyuItem.put("rirekiId", Integer.parseInt(mikomiUploadBean.getRirekiId()));
                                    updKaisyuItem.put("dataKbn", "M");
                                    updKaisyuItem.put("syukeiYm", nengetsu.replaceAll("/", ""));
                                    updKaisyuItem.put("currencyCode", currencyCode);
                                    updKaisyuItem.put("kinsyuKbn", kinsyu);
                                    updKaisyuItem.put("kaisyuKbn", kaisyu);
                                    updKaisyuItem.put("zeiKbn", zei);
                                    updKaisyuItem.put("kaisyuAmount", gaiAmount);
                                    updKaisyuItem.put("kaisyuEnkaAmount", enkaAmount);
                                    updKaisyuItem.put("kaisyuRate", kaisyuRate);
                                    mikomiUploadBean.addDataList4(updKaisyuItem);
                                    
                                    // 回収情報は実績もセットで登録する
                                    Map<String, Object> updKaisyuItemJis = new HashMap<>(updKaisyuItem);
                                    updKaisyuItemJis.put("dataKbn", "J");
                                    mikomiUploadBean.addDataList4(updKaisyuItemJis);
                                    
                                    // データセット後、円貨の場合は外貨をクリア
                                    if(ENKA.equals(kbn)){
                                        gaiAmount = null;
                                    }
                                } else {
                                    isSuccess = false;
                                }    
                            }
//                        }
                          
                    }
                }
            }
        }
        
        return isSuccess;
    }

    /**
     * アップロード用の受注情報のチェックと設定
     * @param sheet
     * @param colIdx
     * @param ankenId
     * @param dataKbn
     * @param syuekiYm
     * @param jyuchuCurrencyList
     * @param categoryMap
     * @return
     * @throws Exception 
     */
    private boolean setJyuchuDataForUpd(Sheet sheet, int colIdx, String ankenId, String dataKbn, String syuekiYm, 
            List<Map<String, Object>> jyuchuCurrencyList, Map categoryMap) throws Exception {
        
        boolean isSuccess = true;
        String jyuchuRate;
        String jyuchuSp;
        String jyuchuNet;
        // 列名を設定（エラー出力用）
        String colTitleMsg = "M".equals(dataKbn) ? syuekiYm : Label.getValue(Label.lastMikomi);;
        
        // 受注用の通貨種類情報分を繰り返し
        for (Map<String, Object> jyuchuCurrency : jyuchuCurrencyList) {
            // 通貨コード
            String currencyCode = String.valueOf(jyuchuCurrency.get("CURRENCY_CODE"));
            // 受注レートの通貨名前付けの名称からセールを取得
            Cell cellCurrencyRate = searchCellName(sheet, "JYUCHURATE_" + currencyCode);
            // 受注SPの通貨名前付けの名称からセールを取得
            Cell cellCurrencySP = searchCellName(sheet, "JYUCHUSP_" + currencyCode);
            
            // セール取得できない場合、下記の処理を飛ばす
            if (cellCurrencyRate == null || cellCurrencySP == null) {
                continue;
            }
            // 受注レートの行番号を取得
            int rowIdxRate = cellCurrencyRate.getRowIndex();

            // 受注SP金額の行番号を取得
            int rowIdxSP = cellCurrencySP.getRowIndex();

            // 日本円の場合
            if (ConstantString.currencyCodeEn.equals(currencyCode)) {
                // 受注レートが１（固定）
                jyuchuRate = "1";
                // 外貨の場合    
            } else {
                // 該当通貨の受注レートを取得
                jyuchuRate = Utils.getObjToStrValue(PoiUtil.getCellValue(PoiUtil.getCell(sheet, rowIdxRate, colIdx)));
            }
            // 受注レートを小数点2桁までを変換し、入力チェックを行う
            String rate = changeAmountStr(jyuchuRate, 2);
            boolean chkRateFlg = chkAmountError(rate, jyuchuRate, rowIdxRate, colTitleMsg);

            //　該当通貨の受注SP金額を取得
            jyuchuSp = Utils.getObjToStrValue(PoiUtil.getCellValue(PoiUtil.getCell(sheet, rowIdxSP, colIdx)));
            // 受注SP金額を小数点2桁まで変換し、入力チェックを行う
            String sp = changeAmountStr(jyuchuSp, 2);
            boolean chkSPFlg = chkAmountError(sp, jyuchuSp, rowIdxSP, colTitleMsg);

            // チェックエラーがないの場合
            if (chkRateFlg && chkSPFlg) {
                // アップロード用の受注ＳＰ情報を作成
                Map<String, Object> jyucyuSpData = new HashMap<>();
                jyucyuSpData.put("ankenId", ankenId);
                jyucyuSpData.put("rirekiId", Integer.parseInt(mikomiUploadBean.getRirekiId()));
                jyucyuSpData.put("dataKbn", dataKbn);
                jyucyuSpData.put("syuekiYm", syuekiYm.replaceAll("/", ""));
                jyucyuSpData.put("currencyCode", currencyCode);
                jyucyuSpData.put("jyuchuRate", Utils.changeBigDecimal(rate));
                jyucyuSpData.put("jyuchuSp", Utils.changeBigDecimal(sp));

                // アップロード用の受注ＳＰリストに追加
                mikomiUploadBean.addJyuchuSpList(jyucyuSpData);
            } else {
                isSuccess = false;
            }   
        }

        // 受注NETのＫＥＹ名前付けの名称からセルを取得
        Cell cellJyuchuNet = searchCellName(sheet, "JYUCHUNET_" + "B0000" +"￥" + categoryMap.get("CATEGORY_KBN1")
               + "￥" + categoryMap.get("CATEGORY_KBN2"));
        
        // セール取得できない場合、下記の処理を飛ばす
        if (cellJyuchuNet == null) {
            return isSuccess;
        }
                    
        // 受注NETの行番号を取得
        int rowIdxJyuchuNet = cellJyuchuNet.getRowIndex();
        // 受注NET金額を取得
        jyuchuNet = Utils.getObjToStrValue(PoiUtil.getCellValue(PoiUtil.getCell(sheet, rowIdxJyuchuNet, colIdx)));
        // 受注NET金額を整数まで変換し、入力チェックを行う
        String net = changeAmountStr(jyuchuNet);
        boolean chkNetFlg = chkAmountError(net, jyuchuNet, rowIdxJyuchuNet, colTitleMsg);

        // チェックエラーがないの場合
        if (chkNetFlg) {
            // アップロード用の受注Net情報を作成
            Map<String, Object> jyucyuNetData = new HashMap<>();
            jyucyuNetData.put("ankenId", ankenId);
            jyucyuNetData.put("rirekiId", Integer.parseInt(mikomiUploadBean.getRirekiId()));
            jyucyuNetData.put("dataKbn", dataKbn);
            jyucyuNetData.put("syuekiYm", syuekiYm.replaceAll("/", ""));
            jyucyuNetData.put("categoryCode", ConstantString.ikkatsuCategoryCode);
            jyucyuNetData.put("categoryKbn1", String.valueOf(categoryMap.get("CATEGORY_KBN1")));
            jyucyuNetData.put("categoryKbn2", String.valueOf(categoryMap.get("CATEGORY_KBN2")));
            jyucyuNetData.put("categoryName1", String.valueOf(categoryMap.get("CATEGORY_NAME1")));
            jyucyuNetData.put("categoryName2", String.valueOf(categoryMap.get("CATEGORY_NAME2")));
            jyucyuNetData.put("jyuchuNet", Utils.changeBigDecimal(net));

            // アップロード用の受注Netに追加
            mikomiUploadBean.addJyuchuNetList(jyucyuNetData);
        } else {
            isSuccess = false;
        }
        
        return isSuccess;
    }
    
    private String changeAmountStr(String str) {
        return changeAmountStr(str, 0);
    }

    private String changeAmountStr(String str, int round) {
        String amount = str;
        String errorFormat = "";
        //logger.info("amount=" + amount + " round=" + round + " isNum=" + Utils.isNumeric(amount));
        
        if (!Utils.isNumeric(amount)) {
            errorFormat = "formatError";
            //return "formatError";
        } else {
            BigDecimal dAmount = Utils.changeBigDecimal(amount);
            if (dAmount != null) {
                //amount = dAmount.setScale(0, BigDecimal.ROUND_DOWN).toString();
                amount = dAmount.setScale(round, BigDecimal.ROUND_HALF_UP).toString();
                String amountSub = UploadUtil.changeAmountStrSub(amount);
                if (StringUtils.length(amountSub) > 12) {
                    errorFormat = "ketaOverError";
                    //return "ketaOverError";
                }
            }
        }
        
        logger.info("amount=" + amount + " round=" + round + " isNum=" + Utils.isNumeric(amount) + " errorFormat=" + errorFormat);
        if (StringUtils.isNotEmpty(errorFormat)) {
            return errorFormat;
        } else {
            return amount;
        }
        
        //return amount;
    }

    /**
     * アップロード処理の実行
     * @throws java.lang.Exception
     */
    @Override
    public void executeUpload() throws Exception {
        logger.info("ShinkoUploadImpl executeUpload");
        
        // 再計算実行フラグ
        boolean saikeisanFlg = false;
        
        //// データの登録
        // 受注SPデータリスト
        List<Map<String, Object>> jyuchuSpList = mikomiUploadBean.getJyuchuSpList();
        // 受注Netデータリスト
        List<Map<String, Object>> jyuchuNetList = mikomiUploadBean.getJyuchuNetList();
        // 契約金額：建値額：通貨：補正
        List<Map<String, Object>> hoseiList = mikomiUploadBean.getDataList();
        // 見積総原価：未発番NET＆製番損益
        List<Map<String, Object>> sogenkaList = mikomiUploadBean.getDataList2();
        // 売上原価：今回：カテゴリ
        List<Map<String, Object>> cateList = mikomiUploadBean.getDataList3();
        // 回収管理
        List<Map<String, Object>> kaisyuList = mikomiUploadBean.getDataList4();
        
        /*
        if (hoseiList == null && sogenkaList == null && cateList == null) {
            return;
        }
*/
        
        //// データを登録
        // 受注ＳＰを更新
        for (Map<String, Object> jyuchuData: jyuchuSpList) {
            // 受注管理情報(SP内訳)を更新
            uploadDataAccess.updateJyuchuSpRate(jyuchuData);
            // 再計算フラグを設定
            saikeisanFlg = true;
        }

        // 受注NETを更新
        for (Map<String, Object> jyuchuNet: jyuchuNetList) {
            // 受注管理情報(一括見込NET)
            uploadDataAccess.updateJyuchuNet(jyuchuNet);
            // 再計算フラグを設定
            saikeisanFlg = true;
        }
        
        // 契約金額：建値額：通貨：補正
        if (hoseiList != null) {
            for (Map<String, Object> hoseiItem : hoseiList) {
                // SYU_KI_SP_TUKI_S_TBLの更新(or新規登録)
                uploadDataAccess.updateKiSpTukiSTbl(hoseiItem);
            }
            saikeisanFlg = true;
        }
        
        // 見積総原価
        if (sogenkaList != null) {
            for (Map<String, Object> sogenkaItem : sogenkaList) {
                // SYU_KI_NET_SOGENKA_TUKI_TBLの更新(or新規登録)
                uploadDataAccess.updateKiNetSogenkaTukiTbl(sogenkaItem);
            }
            saikeisanFlg = true;
        }
        
        // 売上原価：今回：カテゴリ
        if (cateList != null) {
            for (Map<String, Object> cateItem : cateList) {
                // SYU_KI_NET_CATE_TUKI_TBLの更新(or新規登録)
                uploadDataAccess.updateKiNetCateTukiTbl(cateItem);
            }
            saikeisanFlg = true;
        }
        
        // 回収管理
        if (kaisyuList != null) {
            for (Map<String, Object> cateItem : kaisyuList) {
                // SYU_KI_KAISYU_TBLの更新(or新規登録)
                uploadDataAccess.updateKiKaisyuTbl(cateItem);
            }
            saikeisanFlg = true;
        }
        
        if (saikeisanFlg) {
            // 再計算FLGを設定
            geBukenInfoTblFacade.setSaikeisanFlg(mikomiUploadBean.getAnkenId(), 0, "1");

            // 再計算処理を実行
            calc();
            
            // GE_BUKKEN_INFO_TBL 更新者と更新日を登録
            // 備考登録処理を利用
            geBukenInfoTblFacade.setBiko(mikomiUploadBean.getAnkenId(), 0, null, null);
        }
    }
    
    
    /**
     * セルに定義された名前を用いてセルを取得する
     * @param cellName
     * @return
     * @throws Exception 
     */
    private Cell searchCellName(Sheet sheet, String cellName) throws Exception {
        
        Cell retCell = null;
        
        Name name = sheet.getWorkbook().getName(cellName);
        if (name != null) {
            String strRef = name.getRefersToFormula();
            if (strRef.indexOf("#REF!") == -1) {
                AreaReference areaRef = new AreaReference(strRef);
                CellReference cellRef = areaRef.getFirstCell();
                Row row = sheet.getRow(cellRef.getRow());
                retCell =  row.getCell(cellRef.getCol());
            }
        }
        return retCell;
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
     * 金額セルの値をチェック
     * @param amount
     * @param orgData
     * @param rowIdx
     * @param title
     * @param nengetsu
     * @return 
     */
    private boolean chkAmountError(String amount, String orgData, int rowIdx, /*String title,*/ String nengetsu) {
        boolean bool = true;
        int row = rowIdx + 1;
        
        if ("formatError".equals(amount)) {
            bool = false;
            mikomiUploadErrorBean.addErrorMessage("(" + row + ")行目：" + nengetsu + "：" + MikomiUploadLabel.getValue(MikomiUploadLabel.mikomiAmountFormatError) + "(" + orgData + ")");
        } else if ("ketaOverError".equals(amount)) {
            bool = false;
            mikomiUploadErrorBean.addErrorMessage("(" + row + ")行目：" + nengetsu + "：" + MikomiUploadLabel.getValue(MikomiUploadLabel.mikomiAmountKetaFormatError) + "(" + orgData + ")");
        }
        
        return bool;
    }

    
    
    /**
     * 再計算処理実行
     */
    public void calc() throws Exception {
        // 再計算処理を実行
        callAnkenRecal();

        // 再計算FLGを解除
        geBukenInfoTblFacade.setSaikeisanFlg(mikomiUploadBean.getAnkenId(), 0, "0");
    }
    
    /**
     * 再計算処理をcallする(Step4修正)。
     */
    private void callAnkenRecal() throws Exception {
        storedProceduresService.callAnkenRecalAuto(mikomiUploadBean.getAnkenId(), mikomiUploadBean.getRirekiId(), "0");
//        AnkenRecalDto dto = new AnkenRecalDto();
//        dto.setAnkenId(mikomiUploadBean.getAnkenId());
//        dto.setRirekiId(Integer.parseInt(mikomiUploadBean.getRirekiId()));
//        //dto.setKbn("0");
//
//        storedProceduresService.callAnkenRecal(dto);
//        
//        if (!"0".equals(dto.getStatus())) {
//            throw new Exception("再計算処理[SYU_ANKEN_RECAL_MAIN]でエラーが発生しました。物件Key=" + mikomiUploadBean.getAnkenId());
//        }
    }    
    
}
