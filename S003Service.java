package jp.co.toshiba.hby.pspromis.syuueki.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.co.toshiba.hby.pspromis.common.util.CollectionUtil;
import jp.co.toshiba.hby.pspromis.syuueki.bean.S003Bean;
//import jp.co.toshiba.hby.pspromis.syuueki.dto.AnkenRecalIDto;
import jp.co.toshiba.hby.pspromis.syuueki.dto.IppanBalanceMakerDto;
import jp.co.toshiba.hby.pspromis.syuueki.dto.SyuP7ChangeYmItemNetInfoDto;
import jp.co.toshiba.hby.pspromis.syuueki.entity.OperationLog;
import jp.co.toshiba.hby.pspromis.syuueki.entity.S003TargetYm;
import jp.co.toshiba.hby.pspromis.syuueki.entity.S003UriageSpRowInfo;
//import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuCurMst;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuGeBukkenInfoTbl;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuKiBikou;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuKiNetCateTitleTbl;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuKiNetCateTukiTbl;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuKiSpCurTbl;
//import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuZeikbnMst;
//import jp.co.toshiba.hby.pspromis.syuueki.entity.TeamEntity;
import jp.co.toshiba.hby.pspromis.syuueki.enums.Env;
import jp.co.toshiba.hby.pspromis.syuueki.facade.CategoryMapFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.S003TargetYmFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SysdateEntityFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuGeBukenInfoTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiBikouFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiJyuchuNetTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiJyuchuSpTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiNetCateTitleTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiNetCateTukiTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiSpCurTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiSpTukiITblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiKaisyuTblFacade;
//import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuCurMstFacade;
//import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuZeikbnMstFacade;
//import jp.co.toshiba.hby.pspromis.syuueki.facade.TeamMstFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.KanjyoMstFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiNetItemTukiTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.interceptor.TranceInterceptor;
import jp.co.toshiba.hby.pspromis.syuueki.jdbc.DbUtilsExecutor;
import jp.co.toshiba.hby.pspromis.syuueki.jdbc.SqlFile;
import jp.co.toshiba.hby.pspromis.syuueki.pages.DivisonComponentPage;
import jp.co.toshiba.hby.pspromis.syuueki.pages.DetailHeader;
//import jp.co.toshiba.hby.pspromis.syuueki.util.AuthorityCheck;
import jp.co.toshiba.hby.pspromis.syuueki.util.AuthorityUtils;
import jp.co.toshiba.hby.pspromis.syuueki.util.ConstantString;
import jp.co.toshiba.hby.pspromis.syuueki.util.LoginUserInfo;
import jp.co.toshiba.hby.pspromis.syuueki.util.SyuuekiUtils;
import jp.co.toshiba.hby.pspromis.syuueki.util.Utils;
import jp.co.toshiba.hby.pspromis.syuueki.util.DateUtils;
//import jp.co.toshiba.hby.pspromis.common.exception.PspRunTimeExceotion;
//import jp.co.toshiba.hby.pspromis.common.util.StringUtil;
import jp.co.toshiba.hby.pspromis.syuueki.util.NumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PS-Promis収益管理システム
 * 期間損益(一般) Service
 * @author (NPC)K.Sano
 */
@Stateless
@Interceptors({TranceInterceptor.class})
public class S003Service {

    /**
     * ロガー
     */
    public static final Logger logger = LoggerFactory.getLogger(S003Service.class);

    @PersistenceContext(unitName = jp.co.toshiba.hby.pspromis.syuueki.util.ConstantString.syuuekiDataSourceName)
    private EntityManager em;

    /**
     * パラメータ格納クラスをinjection(CDI)<br>
     * InjectアノテーションよりAPサーバー(Glassfish)側で自動的にインスタンス作成(new)される。<br>
     */
    @Inject
    private S003Bean s003Bean;

    /**
     * Injection DetailHeader
     */
    @Inject
    private DetailHeader dateilHeader;

    /**
     * Injection targetYmFacade
     */
    @Inject
    private S003TargetYmFacade targetYmFacade;

    @Inject
    private OperationLogService operationLogService;

    @Inject
    private SyuKiJyuchuSpTblFacade syuKiJyuchuSpTblFacade;

    @Inject
    private SyuKiJyuchuNetTblFacade syuKiJyuchuNetTblFacade;

    @Inject
    private CategoryMapFacade categoryMapFacade;

    @Inject
    private SyuKiNetCateTitleTblFacade syuKiNetCateTitleTblFacade;

    @Inject
    private SyuKiSpCurTblFacade syuKiSpCurTblFacade;

    @Inject
    private SyuKiSpTukiITblFacade syuKiSpTukiITblFacade;

    @Inject
    private SyuKiNetCateTukiTblFacade syuKiNetCateTukiTblFacade;

    @Inject
    private SyuKiKaisyuTblFacade syuKiKaisyuTblFacade;

    @Inject
    private SyuKiBikouFacade syuKiBikouFacade;

    @Inject
    private SyuGeBukenInfoTblFacade geBukenInfoTblFacade;

    @Inject
    private SysdateEntityFacade sysdateEntityFacade;

    @Inject
    private LoginUserInfo loginUserInfo;

    @Inject
    private StoredProceduresService storedProceduresService;

    //@Inject
    //private TeamMstFacade teamMstFacade;

    @Inject
    private SyuuekiCommonService syuuekiCommonService;
    
    @Inject
    private KanjyoMstFacade kanjyoMstFacade;

    @Inject
    private AuthorityUtils authorityUtils;

    @Inject
    private SyuuekiUtils syuuekiUtils;

    @Inject
    private SyuKiNetItemTukiTblFacade syuKiNetItemTukiTblFacade;
    
    @Inject
    private DivisonComponentPage divisionComponentPage;
    
    /**
     * Injection dbUtilsExecutor
     */
    @Inject
    private DbUtilsExecutor dbUtilsExecutor;

//    @Inject
//    private SyuCurMstFacade syuCurMstFacade;

    //@Inject
    //private SyuZeikbnMstFacade syuZeikbnMstFacade;


    /**
     * インスタンス変数を全て初期化
     */
    private void initInstanceValue() {
    }

    private Map<String, Object> getBaseCondition() {
        Map<String, Object> baseCondition = new HashMap<>();
        baseCondition.put("ankenId", s003Bean.getAnkenId());
        baseCondition.put("rirekiId", new Integer(s003Bean.getRirekiId()));
        baseCondition.put("rirekiFlg", s003Bean.getRirekiFlg());
        return baseCondition;
    }

    /**
     * 規程見込月数分、表示月を増やす(編集モード時の対応)。
     * @throws Exception
     */
    private void addTargetMikomiYm(List<S003TargetYm> targetYmList) {
        // 新規見込月をプロパティファイルに設定した個数分の入力欄を設定
        Integer addYmCount = new Integer(Env.getValue(Env.Ippan_New_Mikomi_Count));

        for (int i=0; i<addYmCount; i++) {
            // 月を追加
            S003TargetYm entity = new S003TargetYm();
            entity.setDispAddFlg("1");
            entity.setDataKbn("M");
            if (targetYmList == null) {
                targetYmList = new ArrayList<>();
            }
            targetYmList.add(entity);
        }
    }

    private String formatYm(Date date) throws Exception {
        if (date == null) {
            return "";
        }

        SimpleDateFormat ymSd = new SimpleDateFormat("yyyyMM");
        String returnValue = ymSd.format(date);
        return returnValue;
    }

    /**
     * [回収情報]関連データを取得
     */
    private void findKaisyuInfo() throws Exception {
        int syuekiYmIndex = 0;
        SqlFile sqlFile = new SqlFile();
        String sqlFilePath;
        String sqlString;
        Object[] params;

        StringBuilder selectSyuekiYmKaisyu = new StringBuilder();
        StringBuilder selectSyuekiYmKaisyuTotal = new StringBuilder(); // 2017/11/20 #072 ADD 回収Total行追加

        // SQLパラメータの設定
        Map<String, Object> baseCondition = this.getBaseCondition();
        baseCondition.put("kanjyoYm", formatYm(this.dateilHeader.getKanjoDate()));

        // 各年月の見込/実績情報取得のSELECT句を作成
        for (S003TargetYm entity : s003Bean.getTargetYm()) {
            // 年月書式のバリデーションチェック(年月指定になっていない場合はここでExceptionで落ちる)
            String syuekiYm = entity.getSyuekiYm();
            Utils.parseDate(syuekiYm);

            // 2016A データ区分(J or M)を追加(勘定月の場合は実績,見込を両方出力するようにするため、データ区分で区分けする必要が発生した)
            String dataKbn = entity.getDataKbn();

            syuekiYmIndex++;

            // 2017/11/20 #072 ADD 回収Total行追加
            selectSyuekiYmKaisyuTotal.append(", SUM(CASE WHEN A.SYUEKI_YM = '").append(syuekiYm).append("' AND A.DATA_KBN = '").append(dataKbn).append("' AND (A.KAISYU_ENKA_AMOUNT IS NOT NULL OR A.KAISYU_ENKA_ZEI IS NOT NULL) THEN NVL(A.KAISYU_ENKA_AMOUNT, 0) + NVL(A.KAISYU_ENKA_ZEI, 0) ELSE NULL END) AS KAISYU_ENKA_ZEIKOMI_YM_TOTAL").append(syuekiYmIndex).append(" ");
            selectSyuekiYmKaisyuTotal.append("\n");

            selectSyuekiYmKaisyu.append(", SUM(CASE WHEN A.SYUEKI_YM = '").append(syuekiYm).append("' AND A.DATA_KBN = '").append(dataKbn).append("' THEN A.KAISYU_AMOUNT ELSE NULL END) AS KAISYU_AMOUNT_YM").append(syuekiYmIndex).append(" ");
            selectSyuekiYmKaisyu.append("\n");
            selectSyuekiYmKaisyu.append(", SUM(CASE WHEN A.SYUEKI_YM = '").append(syuekiYm).append("' AND A.DATA_KBN = '").append(dataKbn).append("' THEN A.KAISYU_ENKA_AMOUNT ELSE NULL END) AS KAISYU_ENKA_AMOUNT_YM").append(syuekiYmIndex).append(" ");
            selectSyuekiYmKaisyu.append("\n");
            selectSyuekiYmKaisyu.append(", SUM(CASE WHEN A.SYUEKI_YM = '").append(syuekiYm).append("' AND A.DATA_KBN = '").append(dataKbn).append("' THEN A.KAISYU_ENKA_ZEI ELSE NULL END) AS KAISYU_ENKA_ZEI_YM").append(syuekiYmIndex).append(" ");
            selectSyuekiYmKaisyu.append("\n");
// (2017A)累計回収額/未回収額は非表示となったためSELECT対象から除外
//            selectSyuekiYmKaisyu.append(", SUM(CASE WHEN A.SYUEKI_YM = '").append(syuekiYm).append("' AND A.DATA_KBN = '").append(dataKbn).append("' THEN A.RUIKEI_KAISYU_AMOUNT ELSE NULL END) AS RUIKEI_KAISYU_AMOUNT_YM").append(syuekiYmIndex).append(" ");
//            selectSyuekiYmKaisyu.append(", SUM(CASE WHEN A.SYUEKI_YM = '").append(syuekiYm).append("' AND A.DATA_KBN = '").append(dataKbn).append("' THEN A.MI_KAISYU_AMOUNT ELSE NULL END) AS MI_KAISYU_AMOUNT_YM").append(syuekiYmIndex).append(" ");
        }

        ////// 回収Total情報 取得(S) //////
        // 2017/11/20 #072 ADD 回収Total行追加
        // 取得SQL文
        sqlFilePath = "/sql/S003/selectKaisyuTotalInfo.sql";
        // 検索条件を設定
        Map<String, Object> conditonKaisyuTotalInfo = new HashMap<>(baseCondition);
        // 各実績/見込年月の売上SP取得のSELECT句を挿入
        conditonKaisyuTotalInfo.put("selectSyuekiYmInfo", String.valueOf(selectSyuekiYmKaisyuTotal));
        // SQL文を取得
        sqlString = sqlFile.getSqlString(sqlFilePath, conditonKaisyuTotalInfo);
        params = sqlFile.getSqlParams(sqlFilePath, conditonKaisyuTotalInfo);
        // 実行
        Map<String, Object>kaisyuTotalInfo = dbUtilsExecutor.dbUtilsGetSql(em, sqlString, params);
        s003Bean.setKaisyuTotalInfo(kaisyuTotalInfo);
        ////// 回収Total情報 取得(E) //////

        ////// 回収情報 取得(S) //////
        // 取得SQL文
        sqlFilePath = "/sql/S003/selectKaisyuInfo.sql";
        // 検索条件を設定
        Map<String, Object> conditonKaisyuInfo = new HashMap<>(baseCondition);
        // 各実績/見込年月の売上SP取得のSELECT句を挿入
        conditonKaisyuInfo.put("selectSyuekiYmInfo", String.valueOf(selectSyuekiYmKaisyu));
        // SQL文を取得
        sqlString = sqlFile.getSqlString(sqlFilePath, conditonKaisyuInfo);
        params = sqlFile.getSqlParams(sqlFilePath, conditonKaisyuInfo);
        // 実行
        List<Map<String, Object>> kaisyuInfoList = dbUtilsExecutor.dbUtilsGetSqlList(em, sqlString, params);

        s003Bean.setKaisyuInfoList(kaisyuInfoList);

// (2017A)回収情報の表示内容が変更になったため、以下のロジックは取り消し
//        Map<String, Object> kaisyuCurInfo = new HashMap<>();
//        // 結果を通貨単位をkeyとするMapに詰めなおす(画面で取得しやすいようにする)。
//        for (Map<String, Object> kaisyuInfo : kaisyuInfoList) {
//            String currencyCode = (String)kaisyuInfo.get("CURRENCY_CODE");
//            kaisyuCurInfo.put(currencyCode, kaisyuInfo);
//        }
//        s003Bean.setKaisyuCurInfo(kaisyuCurInfo);

        ////// 回収情報 Total情報 取得(E) //////
    }

    /**
     * [売上管理NET]一括カテゴリNET情報の追加
     */
    private void addIkkatsuUriageCategoryInfo() throws Exception {
        List<SyuKiNetCateTitleTbl> uriageNetCateList = s003Bean.getUriageNetCateList();

        // カテゴリ情報に一括見込NET(CATEGORY_CODE='B0000'が存在するかをチェック)
        boolean isIkkatsuCategory = false;
        if (uriageNetCateList != null) {
            for (SyuKiNetCateTitleTbl cateTitleEntity : uriageNetCateList) {
                if (cateTitleEntity.getCategoryCode().equals(ConstantString.ikkatsuCategoryCode)) {
                    isIkkatsuCategory = true;
                    break;
                }
            }
        }

        // 一括見込カテゴリが存在しない場合は表示対象に追加する。
        if (!isIkkatsuCategory) {
            this.findIkkatsuCategoryInfo();
            Map<String, Object> ikkatsuCategoryInfoMap = s003Bean.getIkkatsuCategoryInfo();
            if (ikkatsuCategoryInfoMap != null) {
                SyuKiNetCateTitleTbl ikkatsuCategoryInfoEntity = new SyuKiNetCateTitleTbl();
                ikkatsuCategoryInfoEntity.setCategoryCode((String)ikkatsuCategoryInfoMap.get("CATEGORY_CODE"));
                ikkatsuCategoryInfoEntity.setCategoryKbn1((String)ikkatsuCategoryInfoMap.get("CATEGORY_KBN1"));
                ikkatsuCategoryInfoEntity.setCategoryKbn2((String)ikkatsuCategoryInfoMap.get("CATEGORY_KBN2"));
                ikkatsuCategoryInfoEntity.setCategoryName1((String)ikkatsuCategoryInfoMap.get("CATEGORY_NAME1"));
                ikkatsuCategoryInfoEntity.setCategoryName2((String)ikkatsuCategoryInfoMap.get("CATEGORY_NAME2"));
                ikkatsuCategoryInfoEntity.setCategorySeq((String)ikkatsuCategoryInfoMap.get("CATEGORY_SEQ"));

                if (uriageNetCateList != null) {
                    uriageNetCateList = CollectionUtil.createAndCopyList(SyuKiNetCateTitleTbl.class, uriageNetCateList);
                } else {
                    uriageNetCateList = new ArrayList<>();
                }
                uriageNetCateList.add(ikkatsuCategoryInfoEntity);

                s003Bean.setUriageNetCateList(uriageNetCateList);
            }
        }
    }

    /**
     * [売上管理NET]関連データを取得
     */
    private void findUriageInfoNet() throws Exception {
        int syuekiYmIndex = 0;
        SqlFile sqlFile = new SqlFile();
        String sqlFilePath;
        String sqlString;
        Object[] params;

        StringBuilder selectSyuekiYmUriageTotal = new StringBuilder();
        StringBuilder selectSyuekiYmUriageNetUti = new StringBuilder();

        // SQLパラメータの設定
        Map<String, Object> baseCondition = this.getBaseCondition();
        baseCondition.put("kanjyoYm", formatYm(this.dateilHeader.getKanjoDate()));

        // 各年月の見込/実績情報取得のSELECT句を作成
        List<Integer> mikomiIndexList = new ArrayList<>();
        for (S003TargetYm entity : s003Bean.getTargetYm()) {
            // 年月書式のバリデーションチェック(年月指定になっていない場合はここでExceptionで落ちる)
            String syuekiYm = entity.getSyuekiYm();
            Utils.parseDate(syuekiYm);

            // 2016A データ区分(J or M)を追加(勘定月の場合は実績,見込を両方出力するようにするため、データ区分で区分けする必要が発生した)
            String dataKbn = entity.getDataKbn();

            syuekiYmIndex++;

            selectSyuekiYmUriageTotal.append(", SUM(CASE WHEN SYUEKI_YM = '").append(syuekiYm).append("' AND DATA_KBN = '").append(dataKbn).append("' THEN URIAGE_GENKA ELSE NULL END) AS URIAGE_GENKA_YM").append(syuekiYmIndex).append(" ");

            // 2017/11/28 #071 ADD 項番優先で計算された値のセルに色付けをする
            selectSyuekiYmUriageNetUti.append(", SUM(CASE WHEN SYUEKI_YM = '").append(syuekiYm).append("' AND DATA_KBN = '").append(dataKbn).append("' THEN NET ELSE NULL END) AS NET_YM").append(syuekiYmIndex).append(" ");
            selectSyuekiYmUriageNetUti.append("\n ");
            if ("J".equals(dataKbn)) {
                selectSyuekiYmUriageNetUti.append(", '0' AS ITEM_FLG_YM").append(syuekiYmIndex).append(" ");
            } else {
                // 見込月のITEM_FLGの加工のために、見込月を表すindexを予めため込んでおく。
                mikomiIndexList.add(syuekiYmIndex);
                selectSyuekiYmUriageNetUti.append(", NVL(SUM(CASE WHEN SYUEKI_YM = '").append(syuekiYm).append("' AND DATA_KBN = '").append("M").append("' THEN ITEM_FLG ELSE NULL END), '0') AS ITEM_FLG_YM").append(syuekiYmIndex).append(" ");
            }
            selectSyuekiYmUriageNetUti.append("\n ");
        }


        ////// 売上NET Total情報 取得(S) //////
        // 取得SQL文
        sqlFilePath = "/sql/S003/selectUriageNetTotalInfo.sql";
        // 検索条件を設定
        Map<String, Object> conditonNetTotalInfo = new HashMap<>(baseCondition);
        // 各実績/見込年月の売上SP取得のSELECT句を挿入
        conditonNetTotalInfo.put("selectSyuekiYmInfo", String.valueOf(selectSyuekiYmUriageTotal));
        // SQL文を取得
        sqlString = sqlFile.getSqlString(sqlFilePath, conditonNetTotalInfo);
        params = sqlFile.getSqlParams(sqlFilePath, conditonNetTotalInfo);
        // 実行
        Map<String, Object> uriageNetTotalInfo = dbUtilsExecutor.dbUtilsGetSql(em, sqlString, params);
        s003Bean.setUriageNetTotalInfo(uriageNetTotalInfo);
        ////// 売上NET Total情報 取得(E) //////


        // 内訳のカテゴリ情報(縦)一覧を取得
        List<SyuKiNetCateTitleTbl> uriageNetCateList = syuKiNetCateTitleTblFacade.getBaseList(baseCondition);
        s003Bean.setUriageNetCateList(uriageNetCateList);
        // [売上管理NET]一括カテゴリNET情報の追加
        this.addIkkatsuUriageCategoryInfo();

        ////// 売上NET 内訳カテゴリ毎のNET情報 取得(S) //////
        // 取得SQL文
        sqlFilePath = "/sql/S003/selectUriageNetCateCellInfo.sql";
        // 検索条件を設定
        Map<String, Object> conditonNetInfo = new HashMap<>(baseCondition);
        // 各実績/見込年月の売上SP取得のSELECT句を挿入
        conditonNetInfo.put("selectSyuekiYmInfo", String.valueOf(selectSyuekiYmUriageNetUti));
        // SQL文を取得
        sqlString = sqlFile.getSqlString(sqlFilePath, conditonNetInfo);
        params = sqlFile.getSqlParams(sqlFilePath, conditonNetInfo);
        // 実行
        List<Map<String, Object>> uriageNetCateInfoList = dbUtilsExecutor.dbUtilsGetSqlList(em, sqlString, params);
        Map<String, Object> uriageNetCateInfo = new HashMap<>();
        if (uriageNetCateInfoList != null) {
            for (Map<String, Object> info : uriageNetCateInfoList) {
                // 2017/12/13 見込月のITEM_FLGを加工する。
                changeCategoryItemFlg(info, mikomiIndexList);
                
                String keyInfo = (String)(info.get("CATEGORY_CODE")) + "_" + (String)(info.get("CATEGORY_KBN1")) + "_" + (String)(info.get("CATEGORY_KBN2"));
                uriageNetCateInfo.put(keyInfo, info);
            }
        }
        s003Bean.setUriageNetCateInfo(uriageNetCateInfo);
        ////// 売上NET 内訳カテゴリ毎のNET情報 取得(S) //////
    }

    /**
     * カテゴリ毎の売上NET内訳のデータ加工
     * 見込月以降で、アイテムからの集約が存在する年月が1件でも存在する場合、そのカテゴリデータの全見込月をアイテムからの集約とみなす。
     *   (再計算処理SYU_P0_KI_NETでその集約処理を行なっている)
     *   (アイテム集約が存在するカテゴリは全見込月の背景色を変えたいが、SYU_KI_NET_CATE_TUKI_TBLにレコードが存在しない月もあるためこのメソッドで全年月のITEM_FLGを立てる)
     */
    private void changeCategoryItemFlg(Map<String, Object> uriageCateInfo, List<Integer> mikomiIndexList) {
        if (CollectionUtils.isEmpty(mikomiIndexList)) {
            return;
        }

        List<S003TargetYm> targetYmList = s003Bean.getTargetYm();
        boolean isAllYmFlgSet = false;
        
        for (Integer mikomiIndex: mikomiIndexList) {
            // 対象年月のITEM_FLGを取得
            String keyName = "ITEM_FLG_YM" + mikomiIndex;
            String itemFlg = uriageCateInfo.get(keyName).toString();
            // 対象カテゴリデータの全見込月にITEM_FLGを立てて背景色をアイテム積み上げ状態として見せる
            if ("1".equals(itemFlg) && !isAllYmFlgSet) {
                for (Integer mikomiIndex2: mikomiIndexList) {
                    String keyName2 = "ITEM_FLG_YM" + mikomiIndex2;
                    uriageCateInfo.put(keyName2, "1");
                }
                isAllYmFlgSet = true;
            }
            
            // 対象年月のNETを取得
            String netKeyName = "NET_YM" + mikomiIndex;
            Object net = uriageCateInfo.get(netKeyName);
            // アイテムからNETを積み上げされた年月は、削除時のメッセージを変更するためにFLGを立てておく
            if ("1".equals(itemFlg) && net != null) {
                S003TargetYm targetYmEntity = targetYmList.get(mikomiIndex - 1);
                targetYmEntity.setUriageItemFlg("1");
            }
        }
    }

    /**
     * [売上管理SP]関連データを取得
     */
    private void findUriageInfoSp() throws Exception {
        int syuekiYmIndex = 0;
        SqlFile sqlFile = new SqlFile();
        String sqlFilePath;
        String sqlString;
        Object[] params;

        StringBuilder selectSyuekiYmUriagetTotalSp = new StringBuilder();
        StringBuilder selectSyuekiYmUriageSp = new StringBuilder();

        // SQLパラメータの設定
        Map<String, Object> baseCondition = this.getBaseCondition();
        baseCondition.put("kanjyoYm", formatYm(this.dateilHeader.getKanjoDate()));

        // 各年月の見込/実績情報取得のSELECT句を作成
        int jissekiIndex = 0;
        for (S003TargetYm entity : s003Bean.getTargetYm()) {
            // 年月書式のバリデーションチェック(年月指定になっていない場合はここでExceptionで落ちる)
            String syuekiYm = entity.getSyuekiYm();
            Utils.parseDate(syuekiYm);

            // 2016A データ区分(J or M)を追加(勘定月の場合は実績,見込を両方出力するようにするため、データ区分で区分けする必要が発生した)
            String dataKbn = entity.getDataKbn();

            syuekiYmIndex++;

            selectSyuekiYmUriagetTotalSp.append(", SUM(CASE WHEN SYUEKI_YM = '").append(syuekiYm).append("' AND DATA_KBN = '").append(dataKbn).append("' THEN URIAGE_AMOUNT ELSE NULL END) AS URIAGE_AMOUNT_YM").append(syuekiYmIndex).append(" ");

            selectSyuekiYmUriageSp.append(", SUM(CASE WHEN SYUEKI_YM = '").append(syuekiYm).append("' AND DATA_KBN = '").append(dataKbn).append("' THEN URIAGE_AMOUNT ELSE NULL END) AS URIAGE_AMOUNT_YM").append(syuekiYmIndex).append(" ");
            // 2017/11/13 MOD #27 売上管理の実績月に売上時レートを出す
            // 実績のときは連番が最大の値を出力するように変更
            if("J".equals(dataKbn)){
                String tableName = "SYU_KI_SP_TUKI_I_TBL";
                if ("R".equals(s003Bean.getRirekiFlg())) {
                    tableName = "SYU_R_KI_SP_TUKI_I_TBL";
                }
  
                selectSyuekiYmUriageSp.append(", (SELECT B.URI_RATE FROM " + tableName + " B WHERE B.SYUEKI_YM = '").append(syuekiYm).append("' AND B.DATA_KBN = '").append(dataKbn).append("' AND B.CURRENCY_CODE = A.CURRENCY_CODE AND B.ANKEN_ID = A.ANKEN_ID AND B.RIREKI_ID = A.RIREKI_ID AND NOT EXISTS (SELECT 'X' FROM " + tableName + " C WHERE C.SYUEKI_YM = B.SYUEKI_YM AND C.DATA_KBN = B.DATA_KBN AND C.CURRENCY_CODE = B.CURRENCY_CODE AND C.ANKEN_ID = B.ANKEN_ID AND C.RIREKI_ID = B.RIREKI_ID AND C.RENBAN > B.RENBAN)) AS URI_RATE_YM").append(syuekiYmIndex).append(" ");
                jissekiIndex = syuekiYmIndex;

            }else{
                selectSyuekiYmUriageSp.append(", SUM(CASE WHEN SYUEKI_YM = '").append(syuekiYm).append("' AND DATA_KBN = '").append(dataKbn).append("' THEN URI_RATE ELSE NULL END) AS URI_RATE_YM").append(syuekiYmIndex).append(" ");
            }
        }

        ////// 売上SP Total行データ 取得(S) //////
        // 取得SQL文
        sqlFilePath = "/sql/S003/selectUriageSpTotalInfo.sql";
        // 検索条件を設定
        Map<String, Object> conditonSpTotalInfo = new HashMap<>(baseCondition);
        // 各実績/見込年月の売上SP取得のSELECT句を挿入
        conditonSpTotalInfo.put("selectSyuekiYmInfo", String.valueOf(selectSyuekiYmUriagetTotalSp));
        // SQL文を取得
        sqlString = sqlFile.getSqlString(sqlFilePath, conditonSpTotalInfo);
        params = sqlFile.getSqlParams(sqlFilePath, conditonSpTotalInfo);
        // 実行
        Map<String, Object> uriageSpTotalInfo = dbUtilsExecutor.dbUtilsGetSql(em, sqlString, params);
        s003Bean.setUriageSpTotalInfo(uriageSpTotalInfo);
        ////// 売上SP Total行データ 取得(E) //////


        ////// 売上SP 売上金額の通貨毎の合計データ 取得(S) //////
        // 取得SQL文
        sqlFilePath = "/sql/S003/selectUriageSpCellInfo.sql";
        // 検索条件を設定
        Map<String, Object> conditonSpInfo = new HashMap<>(baseCondition);
        // 各実績/見込年月の売上SP取得のSELECT句を挿入
        conditonSpInfo.put("selectSyuekiYmInfo", String.valueOf(selectSyuekiYmUriageSp));
        // 条件設定
        conditonSpInfo.put("utiwakeFlg", "0");
        // SQL文を取得
        sqlString = sqlFile.getSqlString(sqlFilePath, conditonSpInfo);
        params = sqlFile.getSqlParams(sqlFilePath, conditonSpInfo);
        // 実行
        List<Map<String, Object>> uriageSpInfoSumList = dbUtilsExecutor.dbUtilsGetSqlList(em, sqlString, params);
        Map<String, Object> uriageSpInfoSum = new HashMap<>();
        if (uriageSpInfoSumList != null) {
            for (Map<String, Object> info : uriageSpInfoSumList) {
                String keyInfo = (String)(info.get("CURRENCY_CODE"));
                uriageSpInfoSum.put(keyInfo, info);
            }
        }
        s003Bean.setUriageSpInfoSum(uriageSpInfoSum);
        ////// 売上SP 売上金額の通貨毎の合計データ 取得(E) //////


        // 受注見込実績一覧(縦)を取得
        Map<String, Object> conditonJyucyuCur = new HashMap<>(baseCondition);
        conditonJyucyuCur.put("noExistsRenban", ConstantString.finalRenban);
        List<S003UriageSpRowInfo> uriageSpRowInfoList = targetYmFacade.findUriageSpRowInfo(conditonJyucyuCur);
        s003Bean.setUriageSpRowInfoList(uriageSpRowInfoList);

        ////// 売上SP 一括見込SP行、受注通知SP行データ 取得(S) //////
        // 条件設定
        conditonSpInfo.put("utiwakeFlg", "1");
        // SQL文を取得
        sqlString = sqlFile.getSqlString(sqlFilePath, conditonSpInfo);
        params = sqlFile.getSqlParams(sqlFilePath, conditonSpInfo);
        // 実行
        List<Map<String, Object>> uriageSpInfoList = dbUtilsExecutor.dbUtilsGetSqlList(em, sqlString, params);

        // 画面表示用パラメータに詰める(通貨をKeyとしたMapにする)
        Map<String, Object> uriageIkkatsuSpInfo = new HashMap<>();
        Map<String, Object> uriageSpInfoJyuchuTuti = new HashMap<>();
        if (uriageSpInfoList != null) {
            for (Map<String, Object> info : uriageSpInfoList) {
                String currencyCode = (String)(info.get("CURRENCY_CODE"));
                if (ConstantString.finalRenban.equals((String)(info.get("RENBAN")))) {
                    // 一括見込SP行データの取得(連番0001のみ)
                    String keyInfo = currencyCode;
                    uriageIkkatsuSpInfo.put(keyInfo, info);

                } else {
                    // 受注通知行データの取得
                    String keyInfo = (String)(info.get("CURRENCY_CODE")) + "_" + (String)(info.get("RENBAN"));
                    uriageSpInfoJyuchuTuti.put(keyInfo, info);

                }

                // 実績月の売上レートを登録
                if (uriageIkkatsuSpInfo.get(currencyCode) == null) {
                    Map<String, Object> ikkatsuSp = new HashMap<>();
                    for (int i=1; i<=jissekiIndex; i++) {
                        String uriRateKey = "URI_RATE_YM" + i;
                        ikkatsuSp.put(uriRateKey, info.get(uriRateKey));
                    }
                    uriageIkkatsuSpInfo.put(currencyCode, ikkatsuSp);
                }
            }
        }
        s003Bean.setIkkatsuSpRenban(ConstantString.finalRenban);
        s003Bean.setIkkatsuSpRenbanSeq(ConstantString.finalRenbanSeq);
        s003Bean.setUriageIkkatsuSpInfo(uriageIkkatsuSpInfo);
        s003Bean.setUriageSpInfoJyuchuTuti(uriageSpInfoJyuchuTuti);
        ////// 売上SP 一括見込SP行、受注通知SP行データ 取得(E) //////
    }

    /**
     * 一括カテゴリNET情報をセット
     */
    private void findIkkatsuCategoryInfo() throws Exception {
        if (s003Bean.getIkkatsuCategoryInfo() != null) {
            return;
        }

        SqlFile sqlFile = new SqlFile();
        String sqlFilePath = "/sql/categoryMst/selectCategoryMstFromCode.sql";
        // 条件設定
        Map<String, Object> conditonCateMst = new HashMap<>();
        conditonCateMst.put("categoryCode", ConstantString.ikkatsuCategoryCode);
        // SQL文を取得
        String sqlString = sqlFile.getSqlString(sqlFilePath, conditonCateMst);
        Object[] params = sqlFile.getSqlParams(sqlFilePath, conditonCateMst);
        // 情報を取得
        List<Map<String, Object>> ikkatsuCategoryInfoList = dbUtilsExecutor.dbUtilsGetSqlList(em, sqlString, params);

        if (ikkatsuCategoryInfoList != null && !ikkatsuCategoryInfoList.isEmpty()) {
            s003Bean.setIkkatsuCategoryInfo(ikkatsuCategoryInfoList.get(0));
        }
    }

    /**
     * [受注管理]関連のデータを取得
     * @throws Exception
     */
    private void findJyuchuInfo() throws Exception {
        SqlFile sqlFile = new SqlFile();
        String sqlFilePath;
        String sqlString;
        Object[] params;

        int syuekiYmIndex = 0;
        StringBuilder selectSyuekiYmTotal = new StringBuilder();
        StringBuilder selectSyuekiYmInfo = new StringBuilder();
        StringBuilder selectSyuekiYmNet = new StringBuilder();

        // SQLパラメータの設定
        Map<String, Object> baseCondition = this.getBaseCondition();
        baseCondition.put("kanjyoYm", formatYm(this.dateilHeader.getKanjoDate()));

        // 各年月の見込/実績情報取得のSELECT句を作成
        for (S003TargetYm entity : s003Bean.getTargetYm()) {
            // 年月書式のバリデーションチェック(年月指定になっていない場合はここでExceptionで落ちる)
            String syuekiYm = entity.getSyuekiYm();
            Utils.parseDate(syuekiYm);

            // 2016A データ区分(J or M)を追加(勘定月の場合は実績,見込を両方出力するようにするため、データ区分で区分けする必要が発生した)
            String dataKbn = entity.getDataKbn();

            syuekiYmIndex++;

            // 受注SP(発番SP)/受注NET(発番NET) 合計行データ 各見込/実績月 取得用SELECT句
            selectSyuekiYmTotal.append(", SUM(CASE WHEN SYUEKI_YM = '").append(syuekiYm).append("' AND DATA_KBN = '").append(dataKbn).append("' THEN JYUCHU_SP  ELSE NULL END) as JYUCHU_SP_YM").append(syuekiYmIndex).append(" ");
            selectSyuekiYmTotal.append(", SUM(CASE WHEN SYUEKI_YM = '").append(syuekiYm).append("' AND DATA_KBN = '").append(dataKbn).append("' THEN JYUCHU_NET ELSE NULL END) as JYUCHU_NET_YM").append(syuekiYmIndex).append(" ");

            // [受注レート/受注金額 データ]各見込/実績月データ取得用のSELECT句
            selectSyuekiYmInfo.append(", SUM(CASE WHEN SYUEKI_YM = '").append(syuekiYm).append("' AND DATA_KBN = '").append(dataKbn).append("' THEN JYUCHU_SP   ELSE NULL END) as JYUCHU_SP_YM").append(syuekiYmIndex).append(" ");
            selectSyuekiYmInfo.append(", SUM(CASE WHEN SYUEKI_YM = '").append(syuekiYm).append("' AND DATA_KBN = '").append(dataKbn).append("' THEN JYUCHU_RATE ELSE NULL END) as JYUCHU_RATE_YM").append(syuekiYmIndex).append(" ");

            // [一括受注NET データ]各見込/実績月データ取得用のSELECT句
            selectSyuekiYmNet.append(", SUM(CASE WHEN SYUEKI_YM = '").append(syuekiYm).append("' AND DATA_KBN = '").append(dataKbn).append("' THEN JYUCHU_NET ELSE NULL END) as JYUCHU_NET_YM").append(syuekiYmIndex).append(" ");
        }

        ////// 受注SP(発番SP)/受注NET(発番NET) 合計行データ 取得(S) //////
        // 取得SQL文
        sqlFilePath = "/sql/S003/selectJyuchuTotalInfo.sql";
        // 検索条件を設定
        Map<String, Object> conditonSpInfoTotal = new HashMap<>(baseCondition);
        // 各実績/見込年月の受注レート/SP取得のSELECT句を挿入
        conditonSpInfoTotal.put("selectSyuekiYmInfo", String.valueOf(selectSyuekiYmTotal));
        // SQL文を取得
        sqlString = sqlFile.getSqlString(sqlFilePath, conditonSpInfoTotal);
        params = sqlFile.getSqlParams(sqlFilePath, conditonSpInfoTotal);
        // 実行
        Map<String, Object> jyuchuSpInfoTotal = dbUtilsExecutor.dbUtilsGetSql(em, sqlString, params);
        // 画面表示用パラメータに詰める
        s003Bean.setJyuchuInfoTotal(jyuchuSpInfoTotal);
        ////// 受注SP(発番SP)/受注NET(発番NET) 合計行データ 取得(E) //////


        ////// 各通貨の受注レート/受注金額データ 取得(S) //////
        // 取得SQL文
        sqlFilePath = "/sql/S003/selectJyuchuSpInfo.sql";
        // 検索条件を設定
        Map<String, Object> conditonSpInfo = new HashMap<>(baseCondition);
        // 各実績/見込年月の受注レート/SP取得のSELECT句を挿入
        conditonSpInfo.put("selectSyuekiYmInfo", String.valueOf(selectSyuekiYmInfo));
        // SQL文を取得
        sqlString = sqlFile.getSqlString(sqlFilePath, conditonSpInfo);
        params = sqlFile.getSqlParams(sqlFilePath, conditonSpInfo);
        // 実行
        List<Map<String, Object>> jyuchuSpInfoList = dbUtilsExecutor.dbUtilsGetSqlList(em, sqlString, params);
        // 画面表示用パラメータに詰める(通貨をKeyとしたMapにする)
        Map<String, Object> jyuchuSpInfo = new HashMap<>();
        if (jyuchuSpInfoList != null) {
            for (Map<String, Object> info : jyuchuSpInfoList) {
                String currencyCode = (String)(info.get("CURRENCY_CODE"));
                jyuchuSpInfo.put(currencyCode, info);
            }
        }
        s003Bean.setJyuchuSpInfo(jyuchuSpInfo);
        ////// 受注レート/受注金額データ 取得(E) //////


        ////// 一括受注NET データ 取得(S) //////
        // 取得SQL文
        sqlFilePath = "/sql/S003/selectJyuchuNetInfo.sql";
        // 検索条件を設定
        Map<String, Object> conditonNetInfo = new HashMap<>(baseCondition);
        // カテゴリコード(一括見込NET)を条件指定
        conditonNetInfo.put("categoryCode", ConstantString.ikkatsuCategoryCode);
        // 各実績/見込年月の受注レート/SP取得のSELECT句を挿入
        conditonNetInfo.put("selectSyuekiYmInfo", String.valueOf(selectSyuekiYmNet));
        // SQL文を取得
        sqlString = sqlFile.getSqlString(sqlFilePath, conditonNetInfo);
        params = sqlFile.getSqlParams(sqlFilePath, conditonNetInfo);
        // 実行
        List<Map<String, Object>> jyuchuNetInfoList = dbUtilsExecutor.dbUtilsGetSqlList(em, sqlString, params);
        Map<String, Object> jyuchuNetInfo = null;

        if (jyuchuNetInfoList != null && !jyuchuNetInfoList.isEmpty()) {
            jyuchuNetInfo = jyuchuNetInfoList.get(0);
        } else {
            // データが存在しない場合、カテゴリマスタから一括見込NET欄を生成
            this.findIkkatsuCategoryInfo();
            jyuchuNetInfo = s003Bean.getIkkatsuCategoryInfo();
        }
        // 画面表示用パラメータに詰める
        s003Bean.setJyuchuNetInfo(jyuchuNetInfo);
        ////// 一括受注NET データ 取得(E) //////
    }

    /**
     * データ編集可否FLGを取得
     * @return 1:編集可能 0:編集不可
     */
    private String getEditAuthFlg() {
        String editAuthFlg = "0";
        SyuGeBukkenInfoTbl geEntity = this.dateilHeader.getAnkenEntity();

        // 案件の事業部コード
        //String divisionCode = StringUtils.defaultString(geEntity.getDivisionCode());

        // 事業部コード:(原子力)であるかを判断
        //boolean isNuclearDivision = divisionComponentPage.isNuclearDivision(divisionCode);

        // ログイン者が権限を保有していれば編集可能
        //List<TeamEntity> teamMstList = teamMstFacade.findUserTeamList(loginUserInfo.getUserId());
        //boolean isEdit = loginUserInfo.isAnkenEdit(geEntity, teamMstList);
        boolean isEdit = syuuekiCommonService.isAnkenEditOk(geEntity);
        if (isEdit) {
            editAuthFlg = "1";
        }
//        //原子力で企画の場合は編集権限ありで対応
//        if (isNuclearDivision) {
//            boolean isKikakuEdit =loginUserInfo.isAnkenEditKikakuGen(geEntity);
//            if (isKikakuEdit) {
//                editAuthFlg = "1";
//            }
//        }
        return editAuthFlg;
    }

    /**
     * 指定月の備考有無チェック(ボタン表示)
     */
    private String getBikoFlg(String syuekiYm) {
        // 備考ボタン表示権限がない場合は、常に非表示
        if (authorityUtils.enableFlg("TUKIBIKO_DISP", dateilHeader.getDivisionCode(), s003Bean.getRirekiFlg()) != 1) {
            return "0";
        }
        // 編集モードでは備考を入力可能にするため、常に表示。
        if ("1".equals(s003Bean.getEditFlg())) {
            return "1";
        }

        // 参照モードでは備考登録済の年月のみ備考参照のボタンを表示。
        Integer rirekiId = Integer.parseInt(s003Bean.getRirekiId());
        SyuKiBikou entity = syuKiBikouFacade.findPk(s003Bean.getAnkenId(), rirekiId, syuekiYm, s003Bean.getRirekiFlg());
        if (entity != null && StringUtils.isNotEmpty(entity.getBikou())) {
            return "1";
        } else {
            return "0";
        }
    }

    /**
     * [受注管理]/[売上管理]両者に関連するデータを取得
     * @throws Exception
     */
    private void findCommonInfo() throws Exception {
        // 物件基本情報を取得(ヘッダ部＋履歴管理・備考部＋勘定月の取得)
        this.dateilHeader.setAnkenId(this.s003Bean.getAnkenId());
        this.dateilHeader.setRirekiId(this.s003Bean.getRirekiId());
        this.dateilHeader.setRirekiFlg(this.s003Bean.getRirekiFlg());
        this.dateilHeader.setEditFlg(s003Bean.getEditFlg());
        this.dateilHeader.setId("S003");
        this.dateilHeader.findPk();

        // データ編集可否FLGの取得(案件とログイン者のチームが一致していること)
        String editAuthFlg = getEditAuthFlg();
        s003Bean.setEditAuthFlg(editAuthFlg);

        Date kanjoYmDate = this.dateilHeader.getKanjoDate();
        String kanjoYmFormat = syuuekiUtils.exeFormatYm(kanjoYmDate);
        int kanjyoYmCount = 0;

        Map<String, Object> paramMap = this.getBaseCondition();
        paramMap.put("syuekiYm", formatYm(this.dateilHeader.getKanjoDate()));

        // 表示対象年月取得
        List<S003TargetYm> targetYm = this.targetYmFacade.findList(paramMap);
        if (targetYm != null) {
            // 各月の備考ボタン表示の判定を行う
            for (S003TargetYm ymEntity : targetYm) {
                String bikoFlg = getBikoFlg(ymEntity.getSyuekiYm());
                ymEntity.setDispBikoFlg(bikoFlg);

                // 2016A 現在の勘定年月のデータの存在数(画面でcolspanを行うかの判定に利用)
                if (kanjoYmFormat.equals(ymEntity.getSyuekiYmDate())) {
                    kanjyoYmCount++;
                }
            }
        }

        // 表示対象の通貨コードを取得(受注管理/売上管理/回収情報)
        List<String> targetCurrencyList = this.targetYmFacade.findCurrencyList(paramMap);

        this.s003Bean.setTargetYm(targetYm);
        this.s003Bean.setTargetCurrencyList(targetCurrencyList);

        // 2016A 勘定年月のセット(勘定年月と同月は、実績/見込欄を両方を出力するので、その判断に用いる)
        this.s003Bean.setKanjyoYm(kanjoYmFormat);
        this.s003Bean.setKanjyoYmCount(kanjyoYmCount);
    }

    /**
     * 画面表示　ビジネスロジック
     * @throws Exception
     */
    public void indexExecute() throws Exception {
        logger.info("S003Service#indexExecute");
        logger.info("jpyUnit=" + s003Bean.getAnkenId());

        initInstanceValue();

        // 円貨はデフォルト1(円)とする。
        if (this.s003Bean.getJpyUnit() == null) {
            this.s003Bean.setJpyUnit(1);
        }

        if (this.s003Bean.getJpyUnit() == 1000) {
            this.s003Bean.setJpyUnitKbn("2");
        } else if (this.s003Bean.getJpyUnit() == 1000000) {
            this.s003Bean.setJpyUnitKbn("3");
        } else {
            this.s003Bean.setJpyUnitKbn("1");
        }

        // 共通情報を取得
        findCommonInfo();

        List<S003TargetYm> targetYm = s003Bean.getTargetYm();
        this.s003Bean.setTargetYm(targetYm);

        // 受注管理情報を取得
        findJyuchuInfo();

        // 売上管理SP情報を取得
        findUriageInfoSp();

        // 回収情報を取得
        findKaisyuInfo();

         // 編集モード時、規程見込月数分の表示月を追加
        List<S003TargetYm> targetYmCopy = new ArrayList<>(targetYm);
        if ("1".equals(s003Bean.getEditFlg())) {
            this.addTargetMikomiYm(targetYmCopy);
        }
        this.s003Bean.setTargetYm(targetYmCopy);
        
        // 売上管理NET情報を取得
        findUriageInfoNet();
    }

    /**
     * 売上レートの取得
     */
    private BigDecimal getRate(String currencyCode) {
        BigDecimal rate = null;
        // 円の場合は1固定としておく。外貨の場合どうするか？確認必要
        if ("JPY".equals(currencyCode)) {
            rate = new BigDecimal(1);
        }
        return rate;
    }

    /**
     * 売上NETの保存
     */
    private void entryUriageNet(String dataKbn, String syuekiYm, String categoryCode, String categoryKbn1, String categoryKbn2, String net, Date now, boolean isForceEntryFlg) {
        int insertFlg = 0;

        SyuKiNetCateTukiTbl en
                = syuKiNetCateTukiTblFacade.getPkInfo(s003Bean.getAnkenId(), Integer.parseInt(s003Bean.getRirekiId()), dataKbn, syuekiYm, categoryCode, categoryKbn1, categoryKbn2);

        if (en == null) {
            // データが存在しない場合は新規登録
            insertFlg = 1;

            en = new SyuKiNetCateTukiTbl();
            en.setAnkenId(s003Bean.getAnkenId());
            en.setRirekiId(Integer.parseInt(s003Bean.getRirekiId()));
            en.setDataKbn(dataKbn);
            en.setSyuekiYm(syuekiYm);
            en.setCategoryCode(categoryCode);
            en.setCategoryKbn1(categoryKbn1);
            en.setCategoryKbn2(categoryKbn2);
            en.setCreatedAt(now);
            en.setCreatedBy(loginUserInfo.getUserId());
        }

        en.setUpdatedAt(now);
        en.setUpdatedBy(loginUserInfo.getUserId());

        // NETを設定
        BigDecimal bigNet = Utils.changeBigDecimal(net);
        if (bigNet != null) {
            en.setNet((bigNet).longValue());
        } else {
            en.setNet(null);
        }

        if (insertFlg == 1) {
            if (bigNet != null || isForceEntryFlg) { //金額なnullならば新規追加しない(ただし強制登録指定された場合は新規追加する)
                syuKiNetCateTukiTblFacade.create(en);  // 新規登録
            }
        } else {
            if (bigNet == null && !isForceEntryFlg) {
                syuKiNetCateTukiTblFacade.remove(en);  // 削除(入力金額がクリアされた場合)
            } else {
                syuKiNetCateTukiTblFacade.edit(en);    // 更新
            }
        }
    }

    /**
     * 売上管理・最終見込/売上レートをMap設定
     * return 通貨毎の売上レート(key:通貨 value:レート)
     */
    private Map<String, Object> getUriageRateFMap() {
        int i = 0;
        Map<String, Object> rateInfo = new HashMap<>();

        for (String uriageRate : s003Bean.getUriageRateF_Currency()) {
            rateInfo.put(s003Bean.getUriageCurF()[i], Utils.changeBigDecimal(uriageRate));
            i++;
        }

        return rateInfo;
    }

    /**
     * 売上管理・各見込月/売上レートをMap設定
     * return 通貨・見込月毎の売上レート(key:通貨_見込月 value:レート)
     */
    private Map<String, Object> getUriageRateMMap() {
        int i = 0;
        int ymIndex = 0;
        Map<String, Object> rateInfo = new HashMap<>();

        for (String uriageRate : s003Bean.getUriageRateM_Currency()) {
            // 次行の処理に移ったら年月indexをはじめに戻す。
            if (s003Bean.getUriageSyuekiYmM().length <= ymIndex) {
                ymIndex = 0;
            }

            String currencyCode = s003Bean.getUriageCurM()[i];
            String uriageSyuekiYm = StringUtils.replace(StringUtils.defaultString(s003Bean.getUriageSyuekiYmM()[ymIndex]), "/", "");
            String key = currencyCode + "_" + uriageSyuekiYm;

            rateInfo.put(key, Utils.changeBigDecimal(uriageRate));

            i++;
            ymIndex++;
        }

        return rateInfo;
    }

    /**
     * 売上管理・保存
     * @throws Exception
     */
    private void saveUriage(SyuGeBukkenInfoTbl geEntity) throws Exception {
        int i;
        int ymIndex;
        Map <String, Object> params;

        Date now = sysdateEntityFacade.getSysdate();

          // GEに登録されている売上予定年月
        String geUriageYm = StringUtils.defaultString(DateUtils.getString(geEntity.getUriageEnd(), DateUtils.getMONTH_MEDIUM_SIMPLE_PATTERN()));

        // 基本パラメータ
        Map<String, Object> baseParams = new HashMap<>();
        baseParams.put("ankenId", s003Bean.getAnkenId());
        baseParams.put("rirekiId", Integer.parseInt(s003Bean.getRirekiId()));

        ////// 最終見込・売上金額の保存
        if (s003Bean.getUriageCurF() != null) {
            i = 0;
            Map<String, Object> uriageRateFInfo = this.getUriageRateFMap();
            for (String uriageCurM : s003Bean.getUriageCurF()) {
                params = new HashMap<>(baseParams);
                params.put("currencyCode", uriageCurM);
                params.put("renban", s003Bean.getUriageRenbanF()[i]);
                params.put("renbanSeq", s003Bean.getUriageRenbanSeqF()[i]);

                // SYU_KI_SP_CUR_TBLの更新(新規登録)  ※縦
                //int count = syuKiSpCurTblFacade.countSyuKiSpCur(params);
                List<SyuKiSpCurTbl> syuKiSpCurTblDataList = syuKiSpCurTblFacade.selectSyuKiSpCur(params);
                //if (count == 0) {
                String orderNo = "";
                if (CollectionUtils.isEmpty(syuKiSpCurTblDataList)) {
                    syuKiSpCurTblFacade.insertSyuKiSpCurTbl(params);
                } else {
                    orderNo = syuKiSpCurTblDataList.get(0).getOrderNo();
                }

                params.put("dataKbn", ConstantString.finalDataKbn);
                params.put("syuekiYm", ConstantString.finalSyuekiYm);
                params.put("uriRate", uriageRateFInfo.get(uriageCurM));
                params.put("uriageAmount", Utils.changeBigDecimal(s003Bean.getUriageSpF()[i]));
                params.put("rateUpdateFlg", "1");
                params.put("orderNo", orderNo);

                // SYU_KI_SP_TUKI_I_TBLの更新(新規登録)   ※横
                syuKiSpTukiITblFacade.entrySyuKiSpTukiITbl(params, true);

                i++;
            }
        }

        ////// 最終見込・カテゴリ毎のNETを保存
        if (s003Bean.getUriageNetF() != null) {
            i = 0;
            for (String uriageNetF : s003Bean.getUriageNetF()) {
                // SYU_KI_NET_CATE_TITLE_TBLの新規登録(一括見込NETカテゴリ(CATEGORY_CODE='B0000')の場合のみ)
                if (ConstantString.ikkatsuCategoryCode.equals(s003Bean.getUriageCategoryCodeF()[i])) {
                    this.findIkkatsuCategoryInfo();
                    Map<String, Object> ikkatsuCategoryInfo = s003Bean.getIkkatsuCategoryInfo();

                    Map<String, Object> cateTitleParams = new HashMap<>();
                    cateTitleParams.put("ankenId", s003Bean.getAnkenId());
                    cateTitleParams.put("rirekiId", new Integer(s003Bean.getRirekiId()));
                    cateTitleParams.put("categoryCode", s003Bean.getUriageCategoryCodeF()[i]);
                    cateTitleParams.put("categoryKbn1", s003Bean.getUriageCategoryKbn1F()[i]);
                    cateTitleParams.put("categoryKbn2", s003Bean.getUriageCategoryKbn2F()[i]);
                    cateTitleParams.put("categoryName1", ikkatsuCategoryInfo.get("CATEGORY_NAME1"));
                    cateTitleParams.put("categoryName2", ikkatsuCategoryInfo.get("CATEGORY_NAME2"));
                    cateTitleParams.put("categorySeq", ikkatsuCategoryInfo.get("CATEGORY_SEQ"));
                    cateTitleParams.put("inputFlg", "1");

                    syuKiNetCateTitleTblFacade.entrySyuKiNetCateTitle(cateTitleParams);
                }

                // SYU_KI_NET_CATE_TUKI_TBLの更新(新規登録)   ※横
                this.entryUriageNet(
                          ConstantString.finalDataKbn
                        , ConstantString.finalSyuekiYm
                        , s003Bean.getUriageCategoryCodeF()[i]
                        , s003Bean.getUriageCategoryKbn1F()[i]
                        , s003Bean.getUriageCategoryKbn2F()[i]
                        , uriageNetF
                        , now
                        , true
                );

                i++;
            }
        }

        ////// 各見込月の削除 + 項番の売上年月の変更
        boolean isChangeItemSyuekiYm = false;
        if (s003Bean.getUriageSyuekiYmMDelFlg() != null) {
            ymIndex = 0;
            for (String delFlg : s003Bean.getUriageSyuekiYmMDelFlg()) {
                String uriageSyuekiYm    = StringUtils.defaultString(s003Bean.getUriageSyuekiYmM()[ymIndex]);
                String orgUriageSyuekiYm = StringUtils.defaultString(s003Bean.getOrgUriageSyuekiYmM()[ymIndex]);
                // 削除対象(見込月の削除ボタン実行) or 見込年月が変更されている場合、指定の見込月を削除
                if ("1".equals(delFlg) || !uriageSyuekiYm.equals(orgUriageSyuekiYm)) {
                    params = new HashMap<>(baseParams);
                    params.put("dataKbn", s003Bean.getUriageDataKbnM()[ymIndex]);
                    params.put("syuekiYm", StringUtils.replace(orgUriageSyuekiYm, "/", ""));   // 編集前の見込月を条件指定
                    // 売上SP情報削除(対象見込月データの通貨情報データ全て)
                    syuKiSpTukiITblFacade.deleteSyuKiSpTukiITbl(params);
                    // 売上NET情報削除(対象見込月データ全て)
                    syuKiNetCateTukiTblFacade.deleteSyuKiNetCateTukiTbl(params);

                    // 回収情報情報削除(対象見込月データの通貨情報データ全て)
                    //syuKiKaisyuTblFacade.deleteSyuKiKaisyuTbl(params);

                }

                // 売上年月そのものが変更された場合、項番の売上年月(SYU_KI_NET_ITEM_TUKI_TBL)のSYUEKI_YMも変更する
                //if ((StringUtils.isNotEmpty(uriageSyuekiYm) && StringUtils.isNotEmpty(orgUriageSyuekiYm)) && (!"1".equals(delFlg) && !uriageSyuekiYm.equals(orgUriageSyuekiYm))) {
                if (StringUtils.isNotEmpty(orgUriageSyuekiYm) && !uriageSyuekiYm.equals(orgUriageSyuekiYm)) {
                    callSyuP7ChangeYmItemNetInfo("M", orgUriageSyuekiYm, uriageSyuekiYm, delFlg);
                    isChangeItemSyuekiYm = true;
                }

                ymIndex++;
            }
        }

        ////// 各見込月・売上SPの更新-新規登録
        if (s003Bean.getUriageCurM() != null && s003Bean.getUriageSyuekiYmM() != null) {
            i = 0;
            ymIndex = 0;
            String orderNo = "";
            Map<String, Object> uriageRateMInfo = this.getUriageRateMMap();
            for (String currency : s003Bean.getUriageCurM()) {
                // 次行(次の連番・通貨)の処理に移ったら年月indexをはじめに戻す
                if (s003Bean.getUriageSyuekiYmM().length <= ymIndex) {
                    ymIndex = 0;
                }
                
                // (次の連番・通貨の処理に移ったら)売上月情報に登録する注番を取得
                if (ymIndex == 0) {
                    orderNo = "";
                    params = new HashMap<>(baseParams);
                    params.put("currencyCode", currency);
                    params.put("renban", s003Bean.getUriageRenbanM()[i]);
                    List<SyuKiSpCurTbl> syuKiSpCurTblDataList = syuKiSpCurTblFacade.selectSyuKiSpCur(params);
                    if (!CollectionUtils.isEmpty(syuKiSpCurTblDataList)) {
                        orderNo = syuKiSpCurTblDataList.get(0).getOrderNo();
                    }
                } 

                String delFlg = s003Bean.getUriageSyuekiYmMDelFlg()[ymIndex];
                String uriageSyuekiYm    = StringUtils.replace(StringUtils.defaultString(s003Bean.getUriageSyuekiYmM()[ymIndex]), "/", "");
                String orgUriageSyuekiYm = StringUtils.replace(StringUtils.defaultString(s003Bean.getOrgUriageSyuekiYmM()[ymIndex]), "/", "");

                // GEに登録されている売上予定と同一月データは売上SPがNULLでもデータを強制的に登録する(そのためのFLGを設定)
                //boolean isForceEntryFlg = (geUriageYm.equals(orgUriageSyuekiYm));
                // ↓期間損益画面の売上情報の入力状態で売上予定年月を決定したいので、未入力になったら削除する
                boolean isForceEntryFlg = false;

                // 見込月が入力されており、未削除の見込月に対して更新-登録を行う。
                if (StringUtils.isNotEmpty(uriageSyuekiYm) && (!"1".equals(delFlg) || !uriageSyuekiYm.equals(orgUriageSyuekiYm))) {
                    String rateKey = currency + "_" + uriageSyuekiYm;
                    params = new HashMap<>(baseParams);
                    params.put("currencyCode", currency);
                    params.put("renban", s003Bean.getUriageRenbanM()[i]);
                    params.put("dataKbn", s003Bean.getUriageDataKbnM()[ymIndex]);
                    params.put("syuekiYm", uriageSyuekiYm);
                    params.put("uriRate", uriageRateMInfo.get(rateKey));
                    params.put("uriageAmount", Utils.changeBigDecimal(s003Bean.getUriageSpM()[i]));
                    params.put("rateUpdateFlg", "1");
                    params.put("orderNo", orderNo);

                    // SYU_KI_SP_TUKI_I_TBLの更新(新規登録)   ※横
                    syuKiSpTukiITblFacade.entrySyuKiSpTukiITbl(params, isForceEntryFlg);
                }

                i++;
                ymIndex++;
            }
        }



        ////// 各見込月・カテゴリ毎のNETの保存
        if (s003Bean.getUriageNetM() != null && s003Bean.getUriageSyuekiYmM() != null) {
            i = 0;
            ymIndex = 0;
            for (String uriageNetM : s003Bean.getUriageNetM()) {
                // 次行の処理に移ったら年月indexをはじめに戻す。
                if (s003Bean.getUriageSyuekiYmM().length <= ymIndex) {
                    ymIndex = 0;
                }

                String delFlg = s003Bean.getUriageSyuekiYmMDelFlg()[ymIndex];
                String uriageSyuekiYm    = StringUtils.defaultString(s003Bean.getUriageSyuekiYmM()[ymIndex]);
                String orgUriageSyuekiYm = StringUtils.defaultString(s003Bean.getOrgUriageSyuekiYmM()[ymIndex]);

                // GEに登録されている売上予定と同一月データは売上NETがNULLでもデータを強制的に登録する(そのためのFLGを設定)
                //boolean isForceEntryFlg = (geUriageYm.equals(orgUriageSyuekiYm));
                // ↓期間損益画面の売上情報の入力状態で売上予定年月を決定したいので、未入力になったら削除する
                boolean isForceEntryFlg = false;
                
                // 見込月が入力されており、未削除の見込月に対して更新-登録を行う。
                if (StringUtils.isNotEmpty(uriageSyuekiYm) && (!"1".equals(delFlg) || !uriageSyuekiYm.equals(orgUriageSyuekiYm))) {
                    // SYU_KI_NET_CATE_TUKI_TBLの更新(新規登録)   ※横
                    this.entryUriageNet(
                          s003Bean.getUriageDataKbnM()[ymIndex]
                        , StringUtils.replace(uriageSyuekiYm, "/", "")
                        , s003Bean.getUriageCategoryCodeM()[i]
                        , s003Bean.getUriageCategoryKbn1M()[i]
                        , s003Bean.getUriageCategoryKbn2M()[i]
                        , uriageNetM
                        , now
                        , isForceEntryFlg
                    );
                }

                i++;
                ymIndex++;
            }
        }
        
        // アイテムの売上年月が変更された場合、変更した売上年月を確定する処理を実施
        if (isChangeItemSyuekiYm) {
            callSyuP7ChangeYmItemNetInfoFix("M");
        }

    }

    /**
     * 受注管理・保存
     * @throws Exception
     */
    private void saveJyuchu(SyuGeBukkenInfoTbl geEntity) throws Exception {
        int i;
        int ymIndex;
        Map <String, Object> params;

        // GEに登録されている受注予定月
        String geJyuchuYm = StringUtils.defaultString(DateUtils.getString(geEntity.getJyuchuEnd(), DateUtils.getMONTH_MEDIUM_SIMPLE_PATTERN()));

        // 基本パラメータ
        Map<String, Object> baseParams = new HashMap<>();
        baseParams.put("ankenId", s003Bean.getAnkenId());
        baseParams.put("rirekiId", Integer.parseInt(s003Bean.getRirekiId()));

        // カテゴリ情報を取得
        Map categoryMap = categoryMapFacade.findCategoryMap(ConstantString.ikkatsuCategoryCode);
        if (categoryMap == null) {
            throw new RuntimeException("SYU_N8_CATEGORY_MAPに一括見込NETのCATEGORY_CODE[" + ConstantString.ikkatsuCategoryCode + "]が設定されていません。");
        }

        ////// 最終見込・受注レート/受注金額の保存
        if (s003Bean.getJyuchuCurF() != null) {
            i = 0;
            for (String jyuchuCurF : s003Bean.getJyuchuCurF()) {
                params = new HashMap<>(baseParams);
                params.put("dataKbn", ConstantString.finalDataKbn);
                params.put("currencyCode", jyuchuCurF);
                params.put("syuekiYm", ConstantString.finalSyuekiYm);
                params.put("jyuchuRate", Utils.changeBigDecimal(s003Bean.getJyuchuSpRateCurF()[i]));
                params.put("jyuchuSp", Utils.changeBigDecimal(s003Bean.getJyuchuSpCurF()[i]));
                params.put("rateUpdateFlg", "1");
                syuKiJyuchuSpTblFacade.entryJyuchuSpRate(params, true);
                i++;
            }
        }

        ////// 最終見込・一括見込NETの保存
        if (s003Bean.getJyuchuNetF() != null) {
            i = 0;
            for (String jyuchuNetF : s003Bean.getJyuchuNetF()) {
                params = new HashMap<>(baseParams);
                params.put("dataKbn", ConstantString.finalDataKbn);
                params.put("syuekiYm", ConstantString.finalSyuekiYm);
                params.put("categoryCode", s003Bean.getJyuchuNetCategoryCodeF()[i]);
                params.put("categoryKbn1", s003Bean.getJyuchuNetCategoryKbn1F()[i]);
                params.put("categoryKbn2", s003Bean.getJyuchuNetCategoryKbn2F()[i]);
                params.put("categoryName1", categoryMap.get("CATEGORY_NAME1"));
                params.put("categoryName2", categoryMap.get("CATEGORY_NAME2"));
                params.put("categorySeq", categoryMap.get("CATEGORY_SEQ"));
                params.put("jyuchuNet", Utils.changeBigDecimal(jyuchuNetF));
                syuKiJyuchuNetTblFacade.entryJyuchuNet(params, true);
                i++;
            }
        }

        ////// 各見込月の削除
        if (s003Bean.getJyuchuSyuekiYmMDelFlg() != null) {
            ymIndex = 0;
            for (String delFlg : s003Bean.getJyuchuSyuekiYmMDelFlg()) {
                String jyuchuSyuekiYm    = StringUtils.defaultString(s003Bean.getJyuchuSyuekiYmM()[ymIndex]);
                String orgJyuchuSyuekiYm = StringUtils.defaultString(s003Bean.getOrgJyuchuSyuekiYmM()[ymIndex]);
                // 削除対象 or 見込年月が変更されている場合、指定の見込月を削除
                if ("1".equals(delFlg) || !jyuchuSyuekiYm.equals(orgJyuchuSyuekiYm)) {
                    params = new HashMap<>(baseParams);
                    params.put("dataKbn", s003Bean.getJyuchuDataKbnM()[ymIndex]);
                    params.put("syuekiYm", StringUtils.replace(orgJyuchuSyuekiYm, "/", ""));   // 編集前の見込月を条件指定
                    // 受注SP情報削除(対象見込月データの通貨情報データ全て)
                    syuKiJyuchuSpTblFacade.deleteJyuchuSpRate(params);
                    // 受注NET情報削除(対象見込月データ)
                    syuKiJyuchuNetTblFacade.deleteJyuchuNet(params);
                }
                ymIndex++;
            }
        }

        ////// 各見込月・受注金額の更新-新規登録
        if (s003Bean.getJyuchuCurM() != null && s003Bean.getJyuchuSyuekiYmM() != null) {
            i = 0;
            ymIndex = 0;
            for (String currency : s003Bean.getJyuchuCurM()) {
                // 次行の処理に移ったら年月indexをはじめに戻す。
                if (s003Bean.getJyuchuSyuekiYmM().length <= ymIndex) {
                    ymIndex = 0;
                }

                String delFlg = s003Bean.getJyuchuSyuekiYmMDelFlg()[ymIndex];
                String jyuchuSyuekiYm    = StringUtils.defaultString(s003Bean.getJyuchuSyuekiYmM()[ymIndex]);
                String orgJyuchuSyuekiYm = StringUtils.defaultString(s003Bean.getOrgJyuchuSyuekiYmM()[ymIndex]);

                // GEに登録されている受注予定年月データは受注SPがNULLでも回収テーブルにデータを強制的に登録する(そのためのFLGを設定)
                //boolean isForceEntryFlg = (geJyuchuYm.equals(StringUtils.replace(orgJyuchuSyuekiYm, "/", "")));
                // ↓期間損益の入力状態で受注予定年月を決定したいので、未入力状態になったらやはり削除する。
                boolean isForceEntryFlg = false;

                // 見込月が入力されており、未削除の見込月に対して更新-登録を行う。
                if (StringUtils.isNotEmpty(jyuchuSyuekiYm) && (!"1".equals(delFlg) || !jyuchuSyuekiYm.equals(orgJyuchuSyuekiYm))) {
                    params = new HashMap<>(baseParams);
                    params.put("dataKbn", s003Bean.getJyuchuDataKbnM()[ymIndex]);
                    params.put("currencyCode", currency);
                    params.put("syuekiYm", StringUtils.replace(jyuchuSyuekiYm, "/", ""));
                    params.put("jyuchuRate", Utils.changeBigDecimal(s003Bean.getJyuchuSpRateCurM()[i]));
                    params.put("jyuchuSp", Utils.changeBigDecimal(s003Bean.getJyuchuSpCurM()[i]));
                    params.put("rateUpdateFlg", "1");
                    syuKiJyuchuSpTblFacade.entryJyuchuSpRate(params, isForceEntryFlg);
                }

                i++;
                ymIndex++;
            }
        }

        ////// 各見込月・一括見込NETの保存
        if (s003Bean.getJyuchuNetM() != null && s003Bean.getJyuchuSyuekiYmM() != null) {
            i = 0;
            ymIndex = 0;
            for (String jyuchuNetM : s003Bean.getJyuchuNetM()) {
                // 次行の処理に移ったら年月indexをはじめに戻す。
                if (s003Bean.getJyuchuSyuekiYmM().length <= ymIndex) {
                    ymIndex = 0;
                }

                String delFlg = s003Bean.getJyuchuSyuekiYmMDelFlg()[ymIndex];
                String jyuchuSyuekiYm    = StringUtils.defaultString(s003Bean.getJyuchuSyuekiYmM()[ymIndex]);
                String orgJyuchuSyuekiYm = StringUtils.defaultString(s003Bean.getOrgJyuchuSyuekiYmM()[ymIndex]);

                // GEに登録されている受注予定年月データは受注SPがNULLでも回収テーブルにデータを強制的に登録する(そのためのFLGを設定)
                //boolean isForceEntryFlg = (geJyuchuYm.equals(StringUtils.replace(orgJyuchuSyuekiYm, "/", "")));
                // ↓期間損益の入力状態で受注予定年月を決定したいので、未入力状態になったらやはり削除する。
                boolean isForceEntryFlg = false;
                
                // 見込月が入力されており、未削除の見込月に対して更新-登録を行う。
                if (StringUtils.isNotEmpty(jyuchuSyuekiYm) && (!"1".equals(delFlg) || !jyuchuSyuekiYm.equals(orgJyuchuSyuekiYm))) {
                    params = new HashMap<>(baseParams);
                    params.put("dataKbn", s003Bean.getJyuchuDataKbnM()[ymIndex]);
                    params.put("syuekiYm", StringUtils.replace(jyuchuSyuekiYm, "/", ""));
                    params.put("categoryCode", s003Bean.getJyuchuNetCategoryCodeM()[i]);
                    params.put("categoryKbn1", s003Bean.getJyuchuNetCategoryKbn1M()[i]);
                    params.put("categoryKbn2", s003Bean.getJyuchuNetCategoryKbn2M()[i]);
                    params.put("categoryName1", categoryMap.get("CATEGORY_NAME1"));
                    params.put("categoryName2", categoryMap.get("CATEGORY_NAME2"));
                    params.put("categorySeq", categoryMap.get("CATEGORY_SEQ"));
                    params.put("jyuchuNet", Utils.changeBigDecimal(jyuchuNetM));
                    syuKiJyuchuNetTblFacade.entryJyuchuNet(params, isForceEntryFlg);
                }

                i++;
                ymIndex++;
            }
        }

    }

    /**
     * 回収管理・保存
     * @throws Exception
     */
    private void saveKaisyu(SyuGeBukkenInfoTbl geEntity) throws Exception {
        int i;
        int ymIndex;
        Map <String, Object> params;

        // GEに登録されている回収月
        String geKaisyuYm = StringUtils.defaultString(DateUtils.getString(geEntity.getKaisyuEnd(), DateUtils.getMONTH_MEDIUM_SIMPLE_PATTERN()));
                
        // 2017/11/14 ADD #5 #12 #22 （収益）受注年月と（収益）売上予定を加える
        String greatestYm = "";

        // 現在の勘定年月取得(一般案件の処理なので、一般案件用勘定年月を取得する)
        Date kanjyoYm = DateUtils.parseDate(kanjyoMstFacade.getNowKanjoDate(ConstantString.salesClassI));

        // 基本パラメータ
        Map<String, Object> baseParams = new HashMap<>();
        baseParams.put("ankenId", s003Bean.getAnkenId());
        baseParams.put("rirekiId", Integer.parseInt(s003Bean.getRirekiId()));

        ////// 「削除」処理された回収月データの削除
        if (s003Bean.getKaisyuSyuekiYmMDelFlg() != null) {
            ymIndex = 0;
            for (String delFlg : s003Bean.getKaisyuSyuekiYmMDelFlg()) {
                String kaisyuSyuekiYm    = StringUtils.defaultString(s003Bean.getKaisyuSyuekiYmM()[ymIndex]);    // 画面上の回収年月入力欄の回収年月
                String orgKaisyuSyuekiYm = StringUtils.defaultString(s003Bean.getOrgKaisyuSyuekiYmM()[ymIndex]); // 画面初期表示時の回収年月
                // 削除対象 or 見込年月が変更されている場合、指定の見込月を削除
                if ("1".equals(delFlg) || !kaisyuSyuekiYm.equals(orgKaisyuSyuekiYm)) {
                    params = new HashMap<>(baseParams);
                    //params.put("dataKbn", s003Bean.getOrgKaisyuDataKbnM()[ymIndex]);    // 2017/11/02 (NPC)S.Ibayashi 回収情報の削除は実績/見込両方を削除
                    params.put("syuekiYm", StringUtils.replace(orgKaisyuSyuekiYm, "/", ""));   // 編集前の年月を条件指定

                    // 回収情報削除(対象データ区分/年月の通貨情報データ全て)
                    syuKiKaisyuTblFacade.deleteSyuKiKaisyuTbl(params);
                }
                ymIndex++;
            }
        }

        ////// 各年月の回収月データの更新/登録
        if (s003Bean.getKaisyuCur()!= null && s003Bean.getKaisyuSyuekiYmM() != null) {
            i = 0;
            ymIndex = 0;
            BigDecimal ngKaisyuRateMini = new BigDecimal(ConstantString.ngKaisyuRateMini);

            //for (String currency : s003Bean.getKaisyuCur()) {
            for (int j=0; j<s003Bean.getKaisyuCur().length; j++) {
                // 対象回収額入力欄の通貨コード/税区分/金種区分/通常・前受区分を取得
                String currency = s003Bean.getKaisyuCur()[j];
                String zeiKbn = s003Bean.getKaisyuZeiKbn()[j];
                String kinsyuKbn = s003Bean.getKaisyuKinsyuKbn()[j];
                String kaisyuKbn = s003Bean.getKaisyuKbn()[j];

                // 次行の処理に移ったら年月indexをはじめに戻す。
                if (s003Bean.getKaisyuSyuekiYmM().length <= ymIndex) {
                    ymIndex = 0;
                }

                String kaisyuSyuekiYm = StringUtils.replace(StringUtils.defaultString(s003Bean.getKaisyuSyuekiYmM()[ymIndex]), "/", "");        // 画面上の回収年月入力欄の回収年月
                String orgKaisyuSyuekiYm = StringUtils.replace(StringUtils.defaultString(s003Bean.getOrgKaisyuSyuekiYmM()[ymIndex]), "/", "");  // 画面初期表示時の回収年月
                
                // 年月が入力されており、未削除の年月に対して回収情報の更新-登録を行う。
                if (StringUtils.isNotEmpty(kaisyuSyuekiYm) ) {
                    // 入力した年月により、実績(J)/見込(M)の判断を行う。
                    String targetDataKbn = (syuuekiUtils.getJissekiFlg(kanjyoYm, kaisyuSyuekiYm) ? "J" : "M");

                    // GEに登録されている回収月データは回収金額がNULLでも回収テーブルにデータを強制的に登録する(そのためのFLGを設定)
                    // (GEの回収月と回収テーブルの最新回収月は同期をとる必要があるための処置)
                    //boolean isForceEntryFlg = (geKaisyuYm.equals(orgKaisyuSyuekiYm));
                    // ↓期間損益画面の回収情報の入力状態で回収予定年月を決定したいので、未入力になったら削除する
                    boolean isForceEntryFlg = false;

                    // 回収円貨額
                    String kaisyuEnka = s003Bean.getKaisyuEnka()[i];
                    if (ConstantString.currencyCodeEn.equals(currency)) {
                        // 円貨通貨(JPY)の場合は外貨・円貨の額は同一とする(入力欄も1つになっている).
                        kaisyuEnka = s003Bean.getKaisyu()[i];
                    }

                    // 回収レート
                    BigDecimal kaisyuRate = NumberUtils.div(Utils.changeBigDecimal(kaisyuEnka), Utils.changeBigDecimal(s003Bean.getKaisyu()[i]), 7);
                    // 回収レートがDBへ格納可能な桁数を超えてしまう場合はnullにする。
                    if (kaisyuRate != null) {
                        if (kaisyuRate.compareTo(ngKaisyuRateMini) >= 0) {
                            kaisyuRate = null;
                        }
                    }

                    params = new HashMap<>(baseParams);
                    params.put("currencyCode", currency);
                    params.put("syukeiYm", kaisyuSyuekiYm);
                    params.put("kinsyuKbn", kinsyuKbn);
                    params.put("kaisyuKbn", kaisyuKbn);
                    params.put("zeiKbn", zeiKbn);
                    params.put("kaisyuAmount", Utils.changeBigDecimal(s003Bean.getKaisyu()[i]));
                    params.put("kaisyuEnkaAmount", Utils.changeBigDecimal(kaisyuEnka));
                    params.put("kaisyuRate", kaisyuRate);

                    // 当月、未来日付は見込を追加・更新
                    if ("M".equals(targetDataKbn)) {
                        params.put("dataKbn", "M");
                        // 2017/11/14 MOD #5 #12 #22 （収益）受注年月と（収益）売上予定を加える
//                        syuKiKaisyuTblFacade.entrySyuKiKaisyuTbl(params);
                        int execCount = syuKiKaisyuTblFacade.entrySyuKiKaisyuTbl(params, isForceEntryFlg);
//                        if (execCount > 0) {
//                            if (greatestYm.compareTo(kaisyuSyuekiYm) < 0) {
//                                greatestYm = kaisyuSyuekiYm;
//                            }
//                        }
                    }

                    // 実績は過去・当月・未来関わらず登録 2017/10/23 (NPC)S.Ibayashi
                    params.put("dataKbn", "J");
                    int execCount = syuKiKaisyuTblFacade.entrySyuKiKaisyuTbl(params, isForceEntryFlg);
                    if (execCount > 0) {
                        if (greatestYm.compareTo(kaisyuSyuekiYm) < 0) {
                            greatestYm = kaisyuSyuekiYm;
                        }
                    }

                }

                i++;
                ymIndex++;
            }
        }

        // 2017/11/14 ADD #5 #12 #22 GEの回収年月を更新する
        //if (StringUtil.isNotEmpty(greatestYm)) {
            params = new HashMap<>(baseParams);
            params.put("kaisyuEnd", greatestYm);
            geBukenInfoTblFacade.updateKaisyuEndMonth(params);
        //}

    }


//    private void saveKaisyu() throws Exception {
//       int i;
//        int ymIndex;
//        Map <String, Object> params;
//
//        Date now = sysdateEntityFacade.getSysdate();
//
//        // 基本パラメータ
//        Map<String, Object> baseParams = new HashMap<>();
//        baseParams.put("ankenId", s003Bean.getAnkenId());
//        baseParams.put("rirekiId", Integer.parseInt(s003Bean.getRirekiId()));
//
//        ////// 各実績月・回収金額の更新-新規登録
//        if (s003Bean.getKaisyuCurJ()!= null &&  s003Bean.getUriageSyuekiYmJ() != null  ) {
//            i = 0;
//            ymIndex = 0;
//            for (String currency : s003Bean.getKaisyuCurJ()) {
//                // 次行の処理に移ったら年月indexをはじめに戻す。
//                if (s003Bean.getUriageSyuekiYmJ().length  <= ymIndex) {
//                    ymIndex = 0;
//                }
//
//                String uriageSyuekiYm    = StringUtils.replace(StringUtils.defaultString(s003Bean.getUriageSyuekiYmJ()[ymIndex]), "/", "");
//
//                // 見込月が入力されており、未削除の見込月に対して更新-登録を行う。
//                if (StringUtils.isNotEmpty(uriageSyuekiYm) ) {
//                    params = new HashMap<>(baseParams);
//                    params.put("currencyCode", currency);
//                    params.put("dataKbn", s003Bean.getUriageDataKbnJ()[ymIndex]);
//                    params.put("syukeiYm", uriageSyuekiYm);
//                    params.put("kaisyuAmount", Utils.changeBigDecimal(s003Bean.getKaisyuJ()[i]));
//
//                    List<SyuCurMst> list = syuCurMstFacade.findAll();
//                    for (SyuCurMst syuCurMst : list) {
//                        if (syuCurMst.getCurrencyCode().equals(currency)) {
//                            params.put("currencyCodeSeq", syuCurMst.getDispSeq());
//                            break;
//                        }
//                    }
//                    // SYU_KI_KAISYU_TBL(新規登録)   ※横
//                    syuKiKaisyuTblFacade.entrySyuKiKaisyuTbl(params);
//                }
//
//                i++;
//                ymIndex++;
//            }
//        }
//
//        ////// 各見込月・売上SPの更新-新規登録
//        if (s003Bean.getKaisyuCurM()!= null &&  s003Bean.getUriageSyuekiYmM() != null ) {
//            i = 0;
//            ymIndex = 0;
//            for (String currency : s003Bean.getKaisyuCurM()) {
//                // 次行の処理に移ったら年月indexをはじめに戻す。
//                if (s003Bean.getUriageSyuekiYmM().length <= ymIndex) {
//                    ymIndex = 0;
//                }
//                //実績時には存在しないため
//                String delFlg = s003Bean.getUriageSyuekiYmMDelFlg()[ymIndex];
//
//                String uriageSyuekiYm    = StringUtils.replace(StringUtils.defaultString(s003Bean.getUriageSyuekiYmM()[ymIndex]), "/", "");
//                String orgUriageSyuekiYm = StringUtils.replace(StringUtils.defaultString(s003Bean.getOrgUriageSyuekiYmM()[ymIndex]), "/", "");
//
//                // 見込月が入力されており、未削除の見込月に対して更新-登録を行う。
//                if (StringUtils.isNotEmpty(uriageSyuekiYm) && (!"1".equals(delFlg) || !uriageSyuekiYm.equals(orgUriageSyuekiYm))) {
//                    params = new HashMap<>(baseParams);
//                    params.put("currencyCode", currency);
//                    params.put("dataKbn", s003Bean.getUriageDataKbnM()[ymIndex]);
//                    params.put("syukeiYm", uriageSyuekiYm);
//                    params.put("kaisyuAmount", Utils.changeBigDecimal(s003Bean.getKaisyuM()[i]));
//
//                    List<SyuCurMst> list = syuCurMstFacade.findAll();
//                    for (SyuCurMst syuCurMst : list) {
//                        if (syuCurMst.getCurrencyCode().equals(currency)) {
//                            params.put("currencyCodeSeq", syuCurMst.getDispSeq());
//                            break;
//                        }
//                    }
//                    // SYU_KI_KAISYU_TBL(新規登録)   ※横
//                    syuKiKaisyuTblFacade.entrySyuKiKaisyuTbl(params);
//                }
//
//                i++;
//                ymIndex++;
//            }
//        }
//
//    }

    /**
     * 金額の更新処理
     * @param processFlg 処理区分(0:通常の保存処理 1:最新値更新)
     * @throws Exception
     */
    public void updateExpectedAmount(int processFlg) throws Exception {
        // 案件の基本情報を取得
        Map<String, Object> baseCondition = getBaseCondition();
        SyuGeBukkenInfoTbl geEntity = geBukenInfoTblFacade.findPk(baseCondition);
        
        // 受注管理情報・保存
        saveJyuchu(geEntity);

        // 売上管理情報・保存
        saveUriage(geEntity);

        // 回収情報・保存
        saveKaisyu(geEntity);

        // 備考を登録
        geBukenInfoTblFacade.setBiko(s003Bean.getAnkenId(), (new Integer(s003Bean.getRirekiId())), s003Bean.getBikou(), "KI");

        // 「受注売上バランス」処理の場合、案分パッケージをCall
        if ("1".equals(s003Bean.getSaveBalanceFlg())) {
            this.callIppanBalanceMaker();
        }

        // 一般案件用の再計算パッケージCall
        //this.callAnken_I_Recal();

        // 操作ログ保存
        if ("1".equals(s003Bean.getSaveBalanceFlg())) {
            // [受注売上バランス]ボタン
            registOperationLog("BALANCE");
        } else {
            // [保存]ボタン
            registOperationLog("SAVE_ONLINE");
        }

        // 一般案件用の再計算パッケージCall(Step4修正)
        callUpdateDataProcedureExecute(processFlg);
        //storedProceduresService.callAnkenRecalAuto(s003Bean.getAnkenId(), s003Bean.getRirekiId(), "0");
        
        // 不要な項番の見込月データ(NET関連が全てNULLのデータ)を削除する
        deketeMikomiTukiEmptyNet(geEntity);
    }

    /**
     * 項番の見込年月変更用のパッケージを実行
     * @param oSyuekiYm 変更前の売上年月
     * @param nSyuekiYm 変更後の売上
     */
    private void callSyuP7ChangeYmItemNetInfo(String dataKbn, String oSyuekiYm, String nSyuekiYm, String delFlg) throws Exception {
        SyuP7ChangeYmItemNetInfoDto dto = new SyuP7ChangeYmItemNetInfoDto();
        dto.setAnkenId(s003Bean.getAnkenId());
        dto.setRirekiId(s003Bean.getRirekiId());
        dto.setDataKbn(dataKbn);
        dto.setOSyuekiYm(StringUtils.replace(oSyuekiYm, "/", ""));
        dto.setNSyuekiYm(StringUtils.replace(nSyuekiYm, "/", ""));
        dto.setDelFlg(delFlg);
        
        storedProceduresService.callSyuP7ChangeYmItemNetInfo(dto);
        logger.info("callSyuP7ChangeYmItemNetInfo errFlg=[{}] errMsg=[{}]", dto.getErrFlg(), dto.getErrMsg());
        
        if (!"0".equals(dto.getErrFlg())) {
            throw new Exception("項番の売上年月変更パッケージ[" + dto.getExeProcedureName() + "]でエラーが発生しました。ankenId=" + s003Bean.getAnkenId() + " rirekiId=" + s003Bean.getRirekiId() + " dataKbn=" + dto.getDataKbn() + " oSyuekiYm=" + oSyuekiYm + " nSyuekiYm=" + nSyuekiYm);
        }
    }
    
    /**
     * 項番の見込年月変更用のパッケージを実行
     * @param oSyuekiYm 変更前の売上年月
     * @param nSyuekiYm 変更後の売上
     */
    private void callSyuP7ChangeYmItemNetInfoFix(String dataKbn) throws Exception {
        SyuP7ChangeYmItemNetInfoDto dto = new SyuP7ChangeYmItemNetInfoDto();
        dto.setAnkenId(s003Bean.getAnkenId());
        dto.setRirekiId(s003Bean.getRirekiId());
        dto.setDataKbn(dataKbn);
        
        storedProceduresService.callSyuP7ChangeYmItemNetInfoFix(dto);
        logger.info("callSyuP7ChangeYmItemNetInfoFix errFlg=[{}] errMsg=[{}]", dto.getErrFlg(), dto.getErrMsg());
        
        if (!"0".equals(dto.getErrFlg())) {
            throw new Exception("項番の売上年月変更確定パッケージ[" + dto.getExeProcedureName() + "]でエラーが発生しました。ankenId=" + s003Bean.getAnkenId() + " rirekiId=" + s003Bean.getRirekiId() + " dataKbn=" + dto.getDataKbn());
        }
    }

    /**
     * 不要な項番の見込月データ(全てのNETがNULLの月データ)を削除
     * ※これを削除しておかないと、案件詳細画面の[(収益)売上年月]が分割売ではないにも関わらず分割売上とみなされてしまい変更できない支障が発生するために処置を行っておく
     */
    private void deketeMikomiTukiEmptyNet(SyuGeBukkenInfoTbl geEntity) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("ankenId", s003Bean.getAnkenId());
        condition.put("rirekiId", s003Bean.getRirekiId());
        condition.put("dataKbn", "M");
        
        String kanjyoYm = kanjyoMstFacade.getNowKanjoDate(geEntity.getSalesClass());
        condition.put("kanjyoYm", kanjyoYm);
        
        syuKiNetItemTukiTblFacade.deleteNetItem(condition);
    }
    
    /**
     * データ更新用のパッケージを実行
     * @param processFlg 処理区分(0:通常の保存処理 1:最新値更新)
     */
    public void callUpdateDataProcedureExecute(int processFlg) throws Exception {
        String recalProcFlg = "0";

        if (processFlg == 1) {
            // 最新値更新パッケージ(TSIS様作成)呼出し
            storedProceduresService.callUpdateNewData(s003Bean.getAnkenId(), s003Bean.getRirekiId(), s003Bean.getUpdateNewDataKbn());
            recalProcFlg = "4";   // 最新値更新パッケージ実行の場合、後続の再計算処理の処理フラグ(PROC_FLG)は"4"にする。
        }

        // 再計算パッケージ(TSIS様作成)呼び出し
        storedProceduresService.callAnkenRecalAuto(s003Bean.getAnkenId(), s003Bean.getRirekiId(), recalProcFlg);
    }


    /**
     * 見込レートの取得
     */
    public void findMikomiRate() throws Exception {
        //List<Map<String, String>> mikomiRateInfoList = new ArrayList<>();
        Map<String, Map<String, String>> rateInfo = new HashMap<>();
        Set<String> syuekiYmSet = new HashSet<>();

        // 物件基本情報を取得
        this.dateilHeader.setAnkenId(this.s003Bean.getAnkenId());
        this.dateilHeader.setRirekiId(this.s003Bean.getRirekiId());
        this.dateilHeader.setRirekiFlg(this.s003Bean.getRirekiFlg());
        this.dateilHeader.findAnkenPk();

        // 受注管理の見込年月を格納
        if (s003Bean.getJyuchuSyuekiYmM() != null) {
            for (String jyuchuSyuekiYm : s003Bean.getJyuchuSyuekiYmM()) {
                if (StringUtils.isNotEmpty(jyuchuSyuekiYm)) {
                    syuekiYmSet.add(StringUtils.replace(jyuchuSyuekiYm, "/", ""));
                }
            }
        }

        // 売上管理の見込年月を格納
        if (s003Bean.getUriageSyuekiYmM() != null) {
            for (String uriageSyuekiYm : s003Bean.getUriageSyuekiYmM()) {
                if (StringUtils.isNotEmpty(uriageSyuekiYm)) {
                    syuekiYmSet.add(StringUtils.replace(uriageSyuekiYm, "/", ""));
                }
            }
        }

        // 受注/売上の各通貨・見込月のレートを取得
        if (s003Bean.getJyuchuCurF() != null) {
            for (String currencyCode : s003Bean.getJyuchuCurF()) {
                Map<String, String> ymRateInfo = new HashMap<>();

                Iterator<String> ymIte = syuekiYmSet.iterator();
                while(ymIte.hasNext()) {
                    String syuekiYm = ymIte.next();
                    BigDecimal rate;
                    String strRate = "";

                    if (ConstantString.currencyCodeEn.equals(currencyCode)) {
                        // 通貨JPYはレート1固定とする。
                        rate = new BigDecimal("1");
                    } else {
                        // 他通貨はレートマスタよりレート検索(FUNCTION利用)
                        rate = storedProceduresService.getMikomiCurrencyRate(syuekiYm, currencyCode, this.dateilHeader.getDivisionCode());
                    }

                    if (rate != null) {
                        strRate = String.valueOf(rate.doubleValue());
                    }
                    ymRateInfo.put(syuekiYm, strRate);
                }

                rateInfo.put(currencyCode, ymRateInfo);
            }
        }

        //s003Bean.setMikomiRateInfoList(mikomiRateInfoList);
        s003Bean.setMikomiRateInfo(rateInfo);
    }

    /**
     * 案分処理をcallする。
     */
    private void callIppanBalanceMaker() throws Exception {
        IppanBalanceMakerDto dto = new IppanBalanceMakerDto();
        dto.setAnkenId(s003Bean.getAnkenId());
        dto.setRirekiId(s003Bean.getRirekiId());
        dto.setSyoriFlg("0");   // バランス調整用パラメータを指定

        storedProceduresService.callIppanBalanceMaker(dto);

        if (!"0".equals(dto.getStatus())) {
            throw new Exception("案分パッケージ[SYU_P0_IPPAN_BALANCE_MAKE.anken_main]でエラーが発生しました。物件Key=" + s003Bean.getAnkenId() + " 履歴ID=" + s003Bean.getRirekiId() + " 処理FLG=" + dto.getSyoriFlg());
        }
    }

    /**
     * 再計算処理をcallする。
     */
//    private void callAnken_I_Recal() throws Exception {
//
//        AnkenRecalIDto dto = new AnkenRecalIDto();
//        dto.setAnkenId(s003Bean.getAnkenId());
//        dto.setRirekiId(Integer.parseInt(s003Bean.getRirekiId()));
//
//        storedProceduresService.callAnken_I_Recal(dto);
//
//        if (!"0".equals(dto.getStatus())) {
//            throw new Exception("再計算処理[SYU_ANKEN_RECAL_I_MAIN.anken_main]でエラーが発生しました。物件Key=" + s003Bean.getAnkenId());
//        }
//    }

    /**
     * 操作ログの登録
     * @param operationCode
     * @throws Exception
     */
    public void registOperationLog(String operationCode) throws Exception{
        OperationLog operationLog = this.operationLogService.getOperationLog();

        operationLog.setOperationCode(operationCode);
        operationLog.setObjectId(this.getObjectId(operationCode));
        operationLog.setObjectType(operationLogService.getObjectType("S003"));
        operationLog.setRemarks(s003Bean.getAnkenId());

        operationLogService.insertOperationLogSearch(operationLog);
    }

    /**
     * 操作対象IDの取得
     * @param operationCode
     * @return
     */
    private Integer getObjectId(String operationCode){
        return 20;
    }

}
