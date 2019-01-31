package jp.co.toshiba.hby.pspromis.syuueki.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
//import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import jp.co.toshiba.hby.pspromis.common.util.StringUtil;
import jp.co.toshiba.hby.pspromis.syuueki.bean.S004Bean;
//import jp.co.toshiba.hby.pspromis.syuueki.dto.AnkenRecalDto;
import jp.co.toshiba.hby.pspromis.syuueki.entity.ContractAmount;
import jp.co.toshiba.hby.pspromis.syuueki.entity.Cost;
import jp.co.toshiba.hby.pspromis.syuueki.entity.OperationLog;
import jp.co.toshiba.hby.pspromis.syuueki.entity.RecoveryAmount;
import jp.co.toshiba.hby.pspromis.syuueki.entity.S004Cost;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SalesAmount;
//import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuCurMst;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuGeBukkenInfoTbl;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuKiBikou;
import jp.co.toshiba.hby.pspromis.syuueki.entity.KsLossData;
//import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuZeikbnMst;
//import jp.co.toshiba.hby.pspromis.syuueki.entity.TeamEntity;
import jp.co.toshiba.hby.pspromis.syuueki.enums.Label;
import jp.co.toshiba.hby.pspromis.syuueki.facade.ContractAmountListFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.CostListFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.KanjyoMstFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.RecoveryAmountListFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.S004UpdateFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SalesListFacade;
//import jp.co.toshiba.hby.pspromis.syuueki.facade.TeamMstFacade;
//import jp.co.toshiba.hby.pspromis.syuueki.facade.SysdateEntityFacade;
//import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuCurMstFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuGeBukenInfoTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiBikouFacade;
//import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuZeikbnMstFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiKaisyuTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiLossTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.interceptor.TranceInterceptor;
import jp.co.toshiba.hby.pspromis.syuueki.pages.DetailHeader;
import jp.co.toshiba.hby.pspromis.syuueki.pages.DivisonComponentPage;
import jp.co.toshiba.hby.pspromis.syuueki.util.AuthorityUtils;
import jp.co.toshiba.hby.pspromis.syuueki.util.ConstantString;
import jp.co.toshiba.hby.pspromis.syuueki.util.LoginUserInfo;
import jp.co.toshiba.hby.pspromis.syuueki.util.SyuuekiUtils;
import jp.co.toshiba.hby.pspromis.syuueki.util.Utils;
import jp.co.toshiba.hby.pspromis.syuueki.util.NumberUtils;
//import jp.co.toshiba.hby.pspromis.common.exception.PspRunTimeExceotion;
import jp.co.toshiba.hby.pspromis.syuueki.enums.Env;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PS-Promis収益管理システム
 * 期間損益(進行基準) Service
 * @author (NPC)K.Sano
 */
@Stateless
@Interceptors({TranceInterceptor.class})
public class S004Service {


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

    /**
     * ロガー
     */
    public static final Logger logger = LoggerFactory.getLogger(S004Service.class);

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

    /**
     * Injection syuuekiUtils
     */
    @Inject
    private SyuuekiUtils syuuekiUtils;

    /**
     * Injection ContractAmountListFacade
     */
    @Inject
    private ContractAmountListFacade contractAmountListFacade;

    /**
     * Injection costListFacade
     */
    @Inject
    private CostListFacade costListFacade;

    /**
     * Injection salesListFacade
     */
    @Inject
    private SalesListFacade salesListFacade;

    /**
     * Injection salesListFacade
     */
    @Inject
    private RecoveryAmountListFacade recoveryAmountListFacade;

    /**
     * Injection S004UpdateFacade
     */
    @Inject
    private S004UpdateFacade s004UpdateFacade;

    /**
     * Injection OperationLogService
     */
    @Inject
    private OperationLogService operationLogService;

    @Inject
    private KanjyoMstFacade kanjyoMstFacade;

    @Inject
    private SyuGeBukenInfoTblFacade geBukenInfoTblFacade;

    @Inject
    private StoredProceduresService storedProceduresService;

    @Inject
    private SyuKiBikouFacade syuKiBikouFacade;

    @Inject
    private AuthorityUtils authorityUtils;

    //@Inject
    //private SyuCurMstFacade syuCurMstFacade;

    //@Inject
    //private SyuZeikbnMstFacade syuZeikbnMstFacade;

    //@Inject
    //private TeamMstFacade teamMstFacade;

    @Inject
    private SyuKiKaisyuTblFacade syuKiKaisyuTblFacade;

    @Inject
    private DivisonComponentPage divisionComponentPage;
    
    @Inject
    private SyuKiLossTblFacade syuKiLossTblFacade;
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    ////// 注意 以下以外のインスタンス変数はもう定義しないようにお願いします ////////////////////////////
    ////// ステートレスセッションbeanは利用後もサーバー上にインスタンスがメモリ上に残り続けるためです //////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * 契約金額合計
     */
    private ContractAmount totalContractAmount;
    private ContractAmount previousPeriodTotalContractAmount;

    /**
     * 契約金額内訳
     */
    private List<ContractAmount> contractAmount;
    private List<ContractAmount> previousPeriodContractAmount;

    /**
     * 見積総原価合計
     */
    private Cost totalCost;

    /**
     * 見積総原価合計
     */
    private Cost previousPeriodTotalCost;

    /**
     * 見積総原価内訳
     */
    private S004Cost cost2;

    /**
     * 見積総原価内訳
     */
    private S004Cost previousPeriodCost2;

    /**
     * 今回売上高
     */
    private SalesAmount totalSales;

    /**
     * 今回売上高
     */
    private SalesAmount previousPeriodTotalSales;


    /**
     * 今回売上高内訳
     */
    private List<SalesAmount> salesList;

    /**
     * 今回売上高内訳
     */
    private List<SalesAmount> previousPeriodSalesList;

    /**
     * 売上高累計
     */
    private SalesAmount totalRuikeiSales;

   /**
     * 売上高累計
     */
    private SalesAmount previousPeriodTotalRuikeiSales;

    /**
     * 売上高累計内訳
     */
    private List<SalesAmount> ruikeiSalesList;

    /**
     * 売上高累計内訳
     */
    private List<SalesAmount> previousPeriodRuikeiSalesList;

    /**
     * 今回売上原価合計
     */
    private SalesAmount totalSalesCost;

    /**
     * 今回売上原価合計
     */
    private SalesAmount previousPeriodTotalSalesCost;

    /**
     * 今回売上原価内訳
     */
    private List<Cost> salesCostList;

    /**
     * 今回売上原価内訳
     */
    private List<Cost> previousPeriodSalesCostList;

    /**
     * 売上原価累計合計あら
     */
    private SalesAmount totalSalesCostRuikei;

    /**
     * 売上原価累計合計
     */
    private SalesAmount previousPeriodTotalSalesCostRuikei;

    /**
     * 回収金額
     */
    // 2017/11/20 #072 ADD 回収Total行追加
    private RecoveryAmount totalRecoveryAmountData;

    /**
     * 回収金額
     */
    private List<RecoveryAmount> recoveryAmountList;

    /**
     * 回収金額
     */
    //private List<RecoveryAmount> previousPeriodRecoveryAmountList;

    /**
     * 案件基本情報の検索
     */
    private void findAnkenInfo() {
        dateilHeader.setAnkenId(s004Bean.getAnkenId());
        dateilHeader.setRirekiId(s004Bean.getRirekiId());
        dateilHeader.setRirekiFlg(s004Bean.getRirekiFlg());
        dateilHeader.setEditFlg(s004Bean.getEditFlg());
        dateilHeader.setId("S004");
        dateilHeader.findPk();
    }

    /**
     * 表示する期間が設定されていない場合、表示対象期間を設定する
     */
    private void setDefaultKikanFrom(List<String> kikanFromList) throws Exception {
        // 画面に表示する値をbeanにセット
        if (StringUtil.isEmpty(s004Bean.getKikanForm())) {
            /////// 初期表示時(項番一覧から期間が引き継がれなかった場合)
            // 初期表示時は、デフォルトの期間を設定
            String defaultKikan = dateilHeader.getDefaultKikan(kikanFromList);
            s004Bean.setKikanForm(defaultKikan);
        }
        
        // 表示対象の期間が候補から外れてしまっている場合、期間候補の最終期間を表示対象期にする。
        if (CollectionUtils.isNotEmpty(kikanFromList)) {
            if (!kikanFromList.contains(s004Bean.getKikanForm())) {
                s004Bean.setKikanForm(kikanFromList.get(kikanFromList.size() - 1));
            }
        }
    }
    
    /**
     * 表示期間切り替え時
     */
    private void changeKikan() {
        // 期間の変更処理でなければこのメソッドはスル―
        if (StringUtil.isEmpty(s004Bean.getKikanChangeFlg())) {
            return;
        }

        String kikanFrom = s004Bean.getKikanForm();
        if ("B".equals(s004Bean.getKikanChangeFlg())) {
            // 一期前に移動
            kikanFrom = syuuekiUtils.calcKikan(kikanFrom, -1);
        } else if ("A".equals(s004Bean.getKikanChangeFlg())) {
            // 一期後に移動
            kikanFrom = syuuekiUtils.calcKikan(kikanFrom, 1);
        }

        // 変更した期間(from)をbeanに詰め直す(画面再表示時にこの期間が使用される)
        s004Bean.setKikanForm(kikanFrom);
    }

    /**
     * インスタンス変数を全て初期化
     */
    private void initInstanceValue() {
        this.totalContractAmount = null;
        this.previousPeriodTotalContractAmount = null;
        this.contractAmount = null;
        this.previousPeriodContractAmount = null;
        this.totalCost = null;
        this.previousPeriodTotalCost = null;
        this.cost2 = null;
        this.previousPeriodCost2 = null;
        this.totalSales = null;
        this.previousPeriodTotalSales = null;
        this.salesList = null;
        this.previousPeriodSalesList = null;
        this.totalRuikeiSales = null;
        this.previousPeriodTotalRuikeiSales = null;
        this.ruikeiSalesList = null;
        this.previousPeriodRuikeiSalesList = null;
        this.totalSalesCost = null;
        this.previousPeriodTotalSalesCost = null;
        this.salesCostList = null;
        this.previousPeriodSalesCostList = null;
        this.totalSalesCostRuikei = null;
        this.previousPeriodTotalSalesCostRuikei = null;
        this.totalRecoveryAmountData = null;
        this.recoveryAmountList = null;
        //this.previousPeriodRecoveryAmountList = null;
        logger.info("all instance clear");
    }

    /**
     * タイトル部分の取得
     */
    private Map<String, String> getTitleMap(int flg) {
        Map<String, String> titleMap = new HashMap<>();
        titleMap.put("title", "");
        titleMap.put("class", "");
        titleMap.put("bikou", "");
        titleMap.put("syuekiYm", "");

        if (flg == 1) {
            titleMap.put("class", "ki-summary-col");
        }

        return titleMap;
    }

    /**
     * 指定月の備考有無チェック(ボタン表示)
     */
    private String getBikoFlg(String syuekiYm) {
        // 備考ボタン表示権限がない場合は、常に非表示
        if (authorityUtils.enableFlg("TUKIBIKO_DISP", dateilHeader.getDivisionCode(), s004Bean.getRirekiFlg()) != 1) {
            return "0";
        }
        // 編集モードでは備考を入力可能にするため、常に表示。
        if ("1".equals(s004Bean.getEditFlg())) {
            return "1";
        }

        // 参照モードでは備考登録済の年月のみ備考参照のボタンを表示。
        Integer rirekiId = Integer.parseInt(s004Bean.getRirekiId());
        SyuKiBikou entity = syuKiBikouFacade.findPk(s004Bean.getAnkenId(), rirekiId, syuekiYm, s004Bean.getRirekiFlg());
        if (entity != null && StringUtils.isNotEmpty(entity.getBikou())) {
            return "1";
        } else {
            return "0";
        }
    }

    /**
     * 見込-実績を出力する月情報をMapで取得<br>
     * key:tmYm=見込-実績の月<br>
     * key:index=見込-実績を表示する箇所のindex<br>
     * 対象期間内で表示を行わない場合はnullを返す。
     */
    private Map<String, Object> getTmInfo(String[] kikanFromAry, String[] kikanToAry) throws ParseException {
        Date kanjyoDate = dateilHeader.getKanjoDate();
        Date kanjyoBeforeDate = DateUtils.addMonths(kanjyoDate, -1);
        String kanjyoBeforeYm = syuuekiUtils.exeFormatYm(kanjyoBeforeDate);

        int index = 0;
        String tmYm = "";
        int dispIndex = -1;

        for (String ym : kikanFromAry) {
            Date ymDate = Utils.parseDate(ym);
            ymDate = DateUtils.addMonths(ymDate, -1);
            String beforeYm = syuuekiUtils.exeFormatYm(ymDate);

            if (kanjyoBeforeYm.equals(beforeYm)) {
                tmYm = beforeYm;
                dispIndex = index;
            }
            if (dispIndex >= 0) {
                break;
            }

            index++;
        }

        if (dispIndex == -1) {
            for (String ym : kikanToAry) {
                Date ymDate = Utils.parseDate(ym);
                ymDate = DateUtils.addMonths(ymDate, -1);
                String beforeYm = syuuekiUtils.exeFormatYm(ymDate);

                if (kanjyoBeforeYm.equals(beforeYm)) {
                    tmYm = beforeYm;
                    dispIndex = index;
                }
                if (dispIndex >= 0) {
                    break;
                }

                index++;
            }
        }

        if (dispIndex == -1) {
            return null;
        } else {
            Map<String, Object> info = new HashMap<>();
            info.put("tmYm", tmYm);
            info.put("index", dispIndex);
            return info;
        }
    }

    /**
     * 画面表示　ビジネスロジック
     * @throws Exception
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void indexExecute() throws Exception {
        logger.info("S004Service#indexExecute");
        logger.info("jpyUnit=" + s004Bean.getJpyUnit());

        String jyLabel = "";
        String jissekiLabelConst = Label.getValue(Label.jisseki);

        initInstanceValue();

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

        // 円貨はデフォルト1(円)とする。
        if (s004Bean.getJpyUnit() == null) {
            s004Bean.setJpyUnit(1);
        }
        // 物件基本情報を取得(ヘッダ部＋履歴管理・備考部＋勘定月の取得)
        findAnkenInfo();
        this.s004Bean.setSabunHaneiFlg(this.dateilHeader.getAnkenEntity().getSabunHaneiFlg());

        // 期間Fromの選択候補を取得(2014上(Step1前半) add)
        Integer kaisyuKanbaiCount = Integer.parseInt(Env.Shinko_Kasyu_KanbaiCount.getValue());
        List<String> kikanFromList = dateilHeader.findSelectKikanFromList(kaisyuKanbaiCount);  // 回収入力(売上予定(完売)年月＋6ヶ月後(プロパティ設定)まで入力可能)のため、+6ヶ月展開可能にする。
        s004Bean.setKikanFromList(kikanFromList);

        // 勘定月から現在の期を取得
//        String nowKikan = dateilHeader.getDefaultKikan(kikanFromList);
//        // 画面に表示する値をbeanにセット
//        if (StringUtil.isEmpty(s004Bean.getKikanForm())) {
//            s004Bean.setKikanForm(nowKikan);
//        }
        setDefaultKikanFrom(kikanFromList);

        // 期間切り替え時
        changeKikan();

        // 期間と実績見込の見出し
        String kikanFrom = s004Bean.getKikanForm();
        String kikanTo   = syuuekiUtils.calcKikan(kikanFrom, 1);

        String[] kikanFromAry = SyuuekiUtils.getKikanMonthAry(kikanFrom);
        String[] kikanToAry = SyuuekiUtils.getKikanMonthAry(kikanTo);

        ArrayList<String> title1 = new ArrayList<>();
        ArrayList<Map<String, String>> title2 = new ArrayList<>();
        Map<String, String> title2Map;

        // [見込-実績]欄を出力するための情報を取得
        int tmIndex = -1;
        String tmYm = "";
        Map<String, Object> tmInfo = getTmInfo(kikanFromAry, kikanToAry);
        if (tmInfo != null) {
            tmIndex = (Integer)tmInfo.get("index");
            tmYm = (String)tmInfo.get("tmYm");
        }
        s004Bean.setTmIndex(tmIndex);

        //////////////// 1期目の取得欄を作成(S) ////////////
        // 画面出力用に見出しとCSS名を設定
        String[] classNameAry = new String[12];
        for (int i=0; i<kikanFromAry.length; i++){

            // [見込-実績]月の表示判定
            if(i == tmIndex){
                //title1.add(syuuekiUtils.getLabelMonth(tmYm));
                title1.add(tmYm);

                title2Map = getTitleMap(0);
                String tmTitle = Label.getValue(Label.mikomi) + "-" + Label.getValue(Label.jisseki);
                if("1".equals(this.dateilHeader.getAnkenEntity().getSabunHaneiFlg())){
                    tmTitle = Label.getValue(Label.hanei) + " " + tmTitle;
                }
                title2Map.put("title", tmTitle);
                title2.add(title2Map);
            }

            //title1.add(Integer.parseInt(kikanFromAry[i].substring(4, 6)) + Label.getValue(Label.month));
            title1.add(syuuekiUtils.getLabelMonth(kikanFromAry[i]));

            jyLabel = syuuekiUtils.getJYLabel(dateilHeader.getKanjoDate(), kikanFromAry[i]);
            title2Map = getTitleMap(0);
            title2Map.put("title", jyLabel);
            title2Map.put("bikou", getBikoFlg(kikanFromAry[i]));
            title2Map.put("syuekiYm", kikanFromAry[i]);
            title2.add(title2Map);

            if (jyLabel.equals(jissekiLabelConst)) {
                classNameAry[i] = "fix_jisseki";
            }

            // 四半期を挟み込む(ここでは第1Q(i==2) と 第2Q(i==5))  ※物件設定で4半期表示を行える場合のみ追加
            if ("1".equals(dateilHeader.getQuarterDispFlg())) {
                if (i == 2 || i == 5) {
                    String qNo = kikanFrom.substring(6).equals("K") ? (i == 5 ? "2" : "1") : (i == 5 ? "4" : "3");
                    title1.add(kikanFromAry[0].substring(0, 4) + Label.getValue(Label.year) + " " + qNo + "Q");

                    title2Map = getTitleMap(1);
                    title2Map.put("title", syuuekiUtils.getJYLabel(dateilHeader.getKanjoDate(), kikanFromAry[i]));
                    title2.add(title2Map);

                    title2Map = getTitleMap(1);
                    title2Map.put("title", Label.getValue(Label.befDiff));
                    title2.add(title2Map);
                }
            }
        }
        //////////////// 1期目の取得欄を作成(E) ////////////

        String str = kikanFrom.substring(6).equals("K") ? Label.getValue(Label.firstHalf) : Label.getValue(Label.secondHalf);
        str = kikanFromAry[0].substring(0, 4) + str;
        title1.add(str);

        //title2.add(syuuekiUtils.getJYLabel(dateilHeader.getKanjoDate(), kikanFromAry[kikanFromAry.length-1]));
        title2Map = getTitleMap(1);
        title2Map.put("title", syuuekiUtils.getJYLabel(dateilHeader.getKanjoDate(), kikanFromAry[kikanFromAry.length-1]));
        title2.add(title2Map);

        //title2.add(Label.getValue(Label.befDiff));
        title2Map = getTitleMap(1);
        title2Map.put("title", Label.getValue(Label.befDiff));
        title2.add(title2Map);

        //////////////// 2期目の取得欄を作成(S) ////////////
        for (int i=0; i<kikanToAry.length; i++){
            // [見込-実績]月の表示判定
            if(i == (tmIndex - 6)){
                //title1.add(syuuekiUtils.getLabelMonth(tmYm));
                title1.add(tmYm);

                title2Map = getTitleMap(0);
                String tmTitle = Label.getValue(Label.mikomi) + "-" + Label.getValue(Label.jisseki);
                if("1".equals(this.dateilHeader.getAnkenEntity().getSabunHaneiFlg())){
                    tmTitle = Label.getValue(Label.hanei) + " " + tmTitle;
                }
                title2Map.put("title", tmTitle);
                title2.add(title2Map);
            }

            //title1.add(Integer.parseInt(kikanToAry[i].substring(4, 6)) + Label.getValue(Label.month));
            title1.add(syuuekiUtils.getLabelMonth(kikanToAry[i]));

            jyLabel = syuuekiUtils.getJYLabel(dateilHeader.getKanjoDate(), kikanToAry[i]);
            title2Map = getTitleMap(0);
            title2Map.put("title", jyLabel);
            title2Map.put("bikou", getBikoFlg(kikanToAry[i]));
            title2Map.put("syuekiYm", kikanToAry[i]);
            title2.add(title2Map);

            if (jyLabel.equals(jissekiLabelConst)) {
                classNameAry[i+6] = "fix_jisseki";
            }

            // 四半期を挟み込む(ここでは第3Q(i==2) と 第4Q(i==5))  ※物件設定で4半期表示を行える場合のみ追加
            if ("1".equals(dateilHeader.getQuarterDispFlg())) {
                if (i == 2 || i == 5) {
                    String qNo = kikanTo.substring(6).equals("K") ? (i == 5 ? "2" : "1") : (i == 5 ? "4" : "3");
                    title1.add(kikanToAry[0].substring(0, 4) + Label.getValue(Label.year) + " " + qNo + "Q");

                    //title2.add(syuuekiUtils.getJYLabel(dateilHeader.getKanjoDate(), kikanToAry[i]));
                    title2Map = getTitleMap(1);
                    title2Map.put("title", syuuekiUtils.getJYLabel(dateilHeader.getKanjoDate(), kikanToAry[i]));
                    title2.add(title2Map);

                    //title2.add(Label.getValue(Label.befDiff));
                    title2Map = getTitleMap(1);
                    title2Map.put("title", Label.getValue(Label.befDiff));
                    title2.add(title2Map);
                }
            }

        }
        //////////////// 2期目の取得欄を作成(E) ////////////

        str = kikanTo.substring(6).equals("K") ? Label.getValue(Label.firstHalf) : Label.getValue(Label.secondHalf);
        str = kikanTo.substring(6).equals("K") ? kikanToAry[0].substring(0, 4) + str : kikanFromAry[0].substring(0, 4) + str;
        title1.add(str);

        //title2.add(syuuekiUtils.getJYLabel(dateilHeader.getKanjoDate(), kikanToAry[kikanToAry.length-1]));
        title2Map = getTitleMap(1);
        title2Map.put("title", syuuekiUtils.getJYLabel(dateilHeader.getKanjoDate(), kikanToAry[kikanToAry.length-1]));
        title2.add(title2Map);

        //title2.add(Label.getValue(Label.befDiff));
        title2Map = getTitleMap(1);
        title2Map.put("title", Label.getValue(Label.befDiff));
        title2.add(title2Map);

        title1.add(Label.getValue(Label.total));

        //title2.add(Label.getValue(Label.mikomi));
        title2Map = getTitleMap(0);
        title2Map.put("title", Label.getValue(Label.mikomi));
        title2.add(title2Map);

        //title2.add(Label.getValue(Label.befDiff));
        title2Map = getTitleMap(0);
        title2Map.put("title", Label.getValue(Label.befDiff));
        title2.add(title2Map);

//        最終見込列は非表示
//        title1.add(Label.getValue(Label.lastMikomi));

        //title2.add(Label.getValue(Label.mikomi));
//        title2Map = getTitleMap(0);
//        title2Map.put("title", Label.getValue(Label.mikomi));
//        title2.add(title2Map);

        //title2.add(Label.getValue(Label.diff));
//        title2Map = getTitleMap(0);
//        title2Map.put("title", Label.getValue(Label.diff));
//        title2.add(title2Map);

        s004Bean.setClassNameAry(classNameAry);
        s004Bean.setMonthTitle(title1);
        s004Bean.setTitle(title2);

        // 検索条件セット
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("kikanA", kikanFromAry[0]);
        paramMap.put("kikanB", kikanFromAry[1]);
        paramMap.put("kikanC", kikanFromAry[2]);
        paramMap.put("kikanD", kikanFromAry[3]);
        paramMap.put("kikanE", kikanFromAry[4]);
        paramMap.put("kikanF", kikanFromAry[5]);
        paramMap.put("kikanG", kikanToAry[0]);
        paramMap.put("kikanH", kikanToAry[1]);
        paramMap.put("kikanI", kikanToAry[2]);
        paramMap.put("kikanJ", kikanToAry[3]);
        paramMap.put("kikanK", kikanToAry[4]);
        paramMap.put("kikanL", kikanToAry[5]);
        paramMap.put("kiFrom", kikanFrom);
        paramMap.put("kiTo", kikanTo);
        paramMap.put("rirekiFlg", s004Bean.getRirekiFlg());

        paramMap.put("Q1", kikanFromAry[2] + "Q");
        paramMap.put("Q2", kikanFromAry[5] + "Q");
        paramMap.put("Q3", kikanToAry[2] + "Q");
        paramMap.put("Q4", kikanToAry[5] + "Q");
        
        // 2018A 不具合修正 各SP,NETの前回値(前回差の取得利用)は、RテーブルからzenkaiIdを用いて取得
        if (!"R".equals(s004Bean.getRirekiFlg()) && !ConstantString.geRirekiId.equals(StringUtils.defaultString(String.valueOf(this.dateilHeader.getAnkenEntity().getZenkaiId()), ConstantString.geRirekiId))) {
            String zenkaiId = String.valueOf(this.dateilHeader.getAnkenEntity().getZenkaiId());
            paramMap.put("zenkaiRirekiId", zenkaiId);
        }

        /*
        cal.setTime(dateilHeader.getKanjoDate());
        cal.add(Calendar.MONTH, -1);
        paramMap.put("kanjoYM", sdf.format(cal.getTime()));
        */
        // 前期検索条件
        Map<String, Object> paramPreviousPeriodMap = getPreviousPeriodMapCondion(kikanFrom ,false);
        Map<String, Object> paramAllPreviousPeriodMap = getPreviousPeriodMapCondion(kikanFrom ,true);

        Date nowKankyoYm = dateilHeader.getKanjoDate();
        Date beforeKanjoYm = DateUtils.addMonths(nowKankyoYm, -1);
        paramMap.put("nowkanjoYM", StringUtils.replace(syuuekiUtils.exeFormatYm(nowKankyoYm), "/", ""));
        paramMap.put("kanjoYM", StringUtils.replace(syuuekiUtils.exeFormatYm(beforeKanjoYm), "/", ""));

        if (StringUtils.isNotEmpty(tmYm)) {
            paramMap.put("tmYM", StringUtils.replace(tmYm, "/", ""));
        } else {
            paramMap.put("tmYM", "XXXXXX");
        }

        // 契約金額合計
        this.totalContractAmount = contractAmountListFacade.findTotalListKs(paramMap);
        // 前期(契約金額合計)
        this.previousPeriodTotalContractAmount = contractAmountListFacade.findTotalListKs(paramPreviousPeriodMap);

        // 契約金額内訳
        this.contractAmount = contractAmountListFacade.findListKs(paramMap);
        // 前期(契約金額内訳)
        this.previousPeriodContractAmount = contractAmountListFacade.findListKs(paramPreviousPeriodMap);

        // 見積総原価合計
        this.totalCost = costListFacade.findEstimateTotalCost(paramMap);
        // 前期(見積総原価合計)
        this.previousPeriodTotalCost = costListFacade.findEstimateTotalCost(paramPreviousPeriodMap);

        // 見積総原価内訳
        this.cost2 = costListFacade.findEstimateCostDetail(paramMap);
        // 前期(見積総原価内訳)
        this.previousPeriodCost2 = costListFacade.findEstimateCostDetail(paramPreviousPeriodMap);

        // 今回売上高
        this.totalSales = salesListFacade.findTotalSales(paramMap);
        // 前期までの累計(今回売上高)
        this.previousPeriodTotalSales = salesListFacade.findPreviousPeriodTotalSales(paramAllPreviousPeriodMap);

        // 今回売上高内訳
        this.salesList = salesListFacade.findSalesDetail(paramMap);
        // 前期までの累計(今回売上高内訳)
        this.previousPeriodSalesList = salesListFacade.findPreviousPeriodTotalSalesDetail(paramAllPreviousPeriodMap);

        // 売上高累計
        this.totalRuikeiSales = salesListFacade.findTotalSalesRuikei(paramMap);
        // 前期までの累計(売上高累計)
        this.previousPeriodTotalRuikeiSales = salesListFacade.findPreviousPeriodTotalSalesRuikei(paramPreviousPeriodMap);

        // 売上高累計内訳
        this.ruikeiSalesList = salesListFacade.findSalesRuikeiDetail(paramMap);
        // 前期までの累計(売上高累計内訳)
        this.previousPeriodRuikeiSalesList = salesListFacade.findPreviousPeriodSalesRuikeiDetail(paramPreviousPeriodMap);

        // 今回売上原価合計
        this.totalSalesCost = salesListFacade.findTotalSalesCost(paramMap);
        // 前期までの累計(今回売上原価合計)
        this.previousPeriodTotalSalesCost = salesListFacade.findPreviousPeriodTotalSalesCost(paramAllPreviousPeriodMap);

        // 今回売上原価内訳
        this.salesCostList = costListFacade.findSalesCostDetail(paramMap);
        // 前期までの累計(今回売上原価内訳)
        this.previousPeriodSalesCostList = costListFacade.findPreviousPeriodSalesCostDetail(paramAllPreviousPeriodMap);

        // 売上原価累計合計
        this.totalSalesCostRuikei = salesListFacade.findTotalSalesCostRuikei(paramMap);
        // 前期までの累計(売上原価累計合計)
        this.previousPeriodTotalSalesCostRuikei = salesListFacade.findPreviousPeriodTotalSalesCostRuikei(paramPreviousPeriodMap);

        // 2017/11/20 #072 ADD 回収Total行追加
        // 合計回収金額
        this.totalRecoveryAmountData = recoveryAmountListFacade.findTotal(paramMap);
        // 回収金額
        this.recoveryAmountList = recoveryAmountListFacade.findList(paramMap);
        
        // (2018A ロスコン対応)ロスコン関連情報を取得
        if (ConstantString.lossControlTargetFlg.equals(this.dateilHeader.getAnkenEntity().getLossControlFlag())) {
            setLossData(paramMap);
        }
        
// (2017A)回収金額一覧と同時に取得するようにしたため削除
//        List<RecoveryAmount> findPreviousPeriodList = recoveryAmountListFacade.findPreviousPeriodList(paramAllPreviousPeriodMap);
        // 前期までの累計(回収金額)
//        this.previousPeriodRecoveryAmountList = recoveryAmountListFacade.findAllPreviousPeriodList(paramAllPreviousPeriodMap);
//        for(RecoveryAmount previousPeriodRecoveryAmount : this.previousPeriodRecoveryAmountList ){
//            for(RecoveryAmount findPreviousPeriod : findPreviousPeriodList ){
//                if(previousPeriodRecoveryAmount.getCurrencyCode().equals(findPreviousPeriod.getCurrencyCode())){
//                    previousPeriodRecoveryAmount.setRuikeiKaisyuAmountTotal(findPreviousPeriod.getRuikeiKaisyuAmountTotal());
//                    break;
//                }
//            }
//        }

        // (2018A インシデント対応)契約金額や売上額、回収額を外貨表記(小数点2桁まで表記するか？)
        // 1:少数2桁まで表示(外貨表記) 0:整数表記(円貨表記)
        this.checkCurrencyForeig();

        // 粗利今回
        if(totalSales != null && totalSalesCost != null){
            s004Bean.setArari1(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount1(), totalSalesCost.getUriageAmount1()), s004Bean.getJpyUnit())));
            s004Bean.setArari2(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount2(), totalSalesCost.getUriageAmount2()), s004Bean.getJpyUnit())));
            s004Bean.setArari3(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount3(), totalSalesCost.getUriageAmount3()), s004Bean.getJpyUnit())));
            s004Bean.setArari4(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount4(), totalSalesCost.getUriageAmount4()), s004Bean.getJpyUnit())));
            s004Bean.setArari5(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount5(), totalSalesCost.getUriageAmount5()), s004Bean.getJpyUnit())));
            s004Bean.setArari6(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount6(), totalSalesCost.getUriageAmount6()), s004Bean.getJpyUnit())));
            s004Bean.setArari7(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount7(), totalSalesCost.getUriageAmount7()), s004Bean.getJpyUnit())));
            s004Bean.setArari8(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount8(), totalSalesCost.getUriageAmount8()), s004Bean.getJpyUnit())));
            s004Bean.setArari9(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount9(), totalSalesCost.getUriageAmount9()), s004Bean.getJpyUnit())));
            s004Bean.setArari10(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount10(), totalSalesCost.getUriageAmount10()), s004Bean.getJpyUnit())));
            s004Bean.setArari11(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount11(), totalSalesCost.getUriageAmount11()), s004Bean.getJpyUnit())));
            s004Bean.setArari12(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount12(), totalSalesCost.getUriageAmount12()), s004Bean.getJpyUnit())));
            s004Bean.setArariK1(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmountK1(), totalSalesCost.getUriageAmountK1()), s004Bean.getJpyUnit())));
            s004Bean.setArariK2(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmountK2(), totalSalesCost.getUriageAmountK2()), s004Bean.getJpyUnit())));
            s004Bean.setArariG(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmountG(), totalSalesCost.getUriageAmountG()), s004Bean.getJpyUnit())));
            s004Bean.setArariF(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmountF(), totalSalesCost.getUriageAmountF()), s004Bean.getJpyUnit())));
            s004Bean.setArariK1Diff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmountK1Diff(), totalSalesCost.getUriageAmountK1Diff()), s004Bean.getJpyUnit())));
            s004Bean.setArariK2Diff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmountK2Diff(), totalSalesCost.getUriageAmountK2Diff()), s004Bean.getJpyUnit())));
            s004Bean.setArariGDiff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmountGDiff(), totalSalesCost.getUriageAmountGDiff()), s004Bean.getJpyUnit())));
            s004Bean.setArariDiff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmountDiff(), totalSalesCost.getUriageAmountDiff()), s004Bean.getJpyUnit())));
            s004Bean.setArariTm(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmountTm(), totalSalesCost.getUriageAmountTm()), s004Bean.getJpyUnit())));

            s004Bean.setArari1Q(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount1Q(), totalSalesCost.getUriageAmount1Q()), s004Bean.getJpyUnit())));
            s004Bean.setArari2Q(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount2Q(), totalSalesCost.getUriageAmount2Q()), s004Bean.getJpyUnit())));
            s004Bean.setArari3Q(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount3Q(), totalSalesCost.getUriageAmount3Q()), s004Bean.getJpyUnit())));
            s004Bean.setArari4Q(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount4Q(), totalSalesCost.getUriageAmount4Q()), s004Bean.getJpyUnit())));
            s004Bean.setArari1QDiff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount1QDiff(), totalSalesCost.getUriageAmount1QDiff()), s004Bean.getJpyUnit())));
            s004Bean.setArari2QDiff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount2QDiff(), totalSalesCost.getUriageAmount2QDiff()), s004Bean.getJpyUnit())));
            s004Bean.setArari3QDiff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount3QDiff(), totalSalesCost.getUriageAmount3QDiff()), s004Bean.getJpyUnit())));
            s004Bean.setArari4QDiff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalSales.getUriageAmount4QDiff(), totalSalesCost.getUriageAmount4QDiff()), s004Bean.getJpyUnit())));
        }

        // 前期までの累計(粗利今回)
        if(previousPeriodTotalSales != null && previousPeriodTotalSalesCost != null){
            s004Bean.setArariMaeAllTotal(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(previousPeriodTotalSales.getUriageAmountTotal(), previousPeriodTotalSalesCost.getUriageRuikeiAmount()), s004Bean.getJpyUnit())));
        }

        // 粗利累計
        if(totalRuikeiSales != null && totalSalesCostRuikei != null){
            s004Bean.setArariRuikei1(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount1(), totalSalesCostRuikei.getUriageAmount1()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei2(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount2(), totalSalesCostRuikei.getUriageAmount2()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei3(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount3(), totalSalesCostRuikei.getUriageAmount3()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei4(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount4(), totalSalesCostRuikei.getUriageAmount4()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei5(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount5(), totalSalesCostRuikei.getUriageAmount5()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei6(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount6(), totalSalesCostRuikei.getUriageAmount6()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei7(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount7(), totalSalesCostRuikei.getUriageAmount7()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei8(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount8(), totalSalesCostRuikei.getUriageAmount8()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei9(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount9(), totalSalesCostRuikei.getUriageAmount9()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei10(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount10(), totalSalesCostRuikei.getUriageAmount10()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei11(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount11(), totalSalesCostRuikei.getUriageAmount11()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei12(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount12(), totalSalesCostRuikei.getUriageAmount12()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikeiK1(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmountK1(), totalSalesCostRuikei.getUriageAmountK1()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikeiK2(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmountK2(), totalSalesCostRuikei.getUriageAmountK2()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikeiG(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmountG(), totalSalesCostRuikei.getUriageAmountG()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikeiF(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmountF(), totalSalesCostRuikei.getUriageAmountF()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikeiK1Diff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmountK1Diff(), totalSalesCostRuikei.getUriageAmountK1Diff()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikeiK2Diff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmountK2Diff(), totalSalesCostRuikei.getUriageAmountK2Diff()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikeiGDiff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmountGDiff(), totalSalesCostRuikei.getUriageAmountGDiff()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikeiDiff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmountDiff(), totalSalesCostRuikei.getUriageAmountDiff()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikeiTm(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmountTm(), totalSalesCostRuikei.getUriageAmountTm()), s004Bean.getJpyUnit())));

            s004Bean.setArariRuikei1Q(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount1Q(), totalSalesCostRuikei.getUriageAmount1Q()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei2Q(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount2Q(), totalSalesCostRuikei.getUriageAmount2Q()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei3Q(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount3Q(), totalSalesCostRuikei.getUriageAmount3Q()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei4Q(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount4Q(), totalSalesCostRuikei.getUriageAmount4Q()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei1QDiff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount1QDiff(), totalSalesCostRuikei.getUriageAmount1QDiff()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei2QDiff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount2QDiff(), totalSalesCostRuikei.getUriageAmount2QDiff()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei3QDiff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount3QDiff(), totalSalesCostRuikei.getUriageAmount3QDiff()), s004Bean.getJpyUnit())));
            s004Bean.setArariRuikei4QDiff(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(totalRuikeiSales.getUriageAmount4QDiff(), totalSalesCostRuikei.getUriageAmount4QDiff()), s004Bean.getJpyUnit())));
        }

        // 前期までの累計(粗利累計)
        if(previousPeriodTotalRuikeiSales != null && previousPeriodTotalSalesCostRuikei != null){
            s004Bean.setArariRuikeiMaeAllTotal(syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(previousPeriodTotalRuikeiSales.getUriageRuikeiAmount(), previousPeriodTotalSalesCostRuikei.getUriageRuikeiAmount()), s004Bean.getJpyUnit())));
        }

        // M率今回
        if(totalSales != null && totalSalesCost != null){
            s004Bean.setMrate1(syuuekiUtils.mrate(totalSales.getUriageAmount1(), totalSalesCost.getUriageAmount1()));
            s004Bean.setMrate2(syuuekiUtils.mrate(totalSales.getUriageAmount2(), totalSalesCost.getUriageAmount2()));
            s004Bean.setMrate3(syuuekiUtils.mrate(totalSales.getUriageAmount3(), totalSalesCost.getUriageAmount3()));
            s004Bean.setMrate4(syuuekiUtils.mrate(totalSales.getUriageAmount4(), totalSalesCost.getUriageAmount4()));
            s004Bean.setMrate5(syuuekiUtils.mrate(totalSales.getUriageAmount5(), totalSalesCost.getUriageAmount5()));
            s004Bean.setMrate6(syuuekiUtils.mrate(totalSales.getUriageAmount6(), totalSalesCost.getUriageAmount6()));
            s004Bean.setMrate7(syuuekiUtils.mrate(totalSales.getUriageAmount7(), totalSalesCost.getUriageAmount7()));
            s004Bean.setMrate8(syuuekiUtils.mrate(totalSales.getUriageAmount8(), totalSalesCost.getUriageAmount8()));
            s004Bean.setMrate9(syuuekiUtils.mrate(totalSales.getUriageAmount9(), totalSalesCost.getUriageAmount9()));
            s004Bean.setMrate10(syuuekiUtils.mrate(totalSales.getUriageAmount10(), totalSalesCost.getUriageAmount10()));
            s004Bean.setMrate11(syuuekiUtils.mrate(totalSales.getUriageAmount11(), totalSalesCost.getUriageAmount11()));
            s004Bean.setMrate12(syuuekiUtils.mrate(totalSales.getUriageAmount12(), totalSalesCost.getUriageAmount12()));
            s004Bean.setMrateK1(syuuekiUtils.mrate(totalSales.getUriageAmountK1(), totalSalesCost.getUriageAmountK1()));
            s004Bean.setMrateK2(syuuekiUtils.mrate(totalSales.getUriageAmountK2(), totalSalesCost.getUriageAmountK2()));
            s004Bean.setMrateG(syuuekiUtils.mrate(totalSales.getUriageAmountG(), totalSalesCost.getUriageAmountG()));
            s004Bean.setMrateF(syuuekiUtils.mrate(totalSales.getUriageAmountF(), totalSalesCost.getUriageAmountF()));
            s004Bean.setMrateK1Diff(syuuekiUtils.mrate(totalSales.getUriageAmountK1Diff(), totalSalesCost.getUriageAmountK1Diff()));
            s004Bean.setMrateK2Diff(syuuekiUtils.mrate(totalSales.getUriageAmountK2Diff(), totalSalesCost.getUriageAmountK2Diff()));
            s004Bean.setMrateGDiff(syuuekiUtils.mrate(totalSales.getUriageAmountGDiff(), totalSalesCost.getUriageAmountGDiff()));
            s004Bean.setMrateDiff(syuuekiUtils.mrate(totalSales.getUriageAmountDiff(), totalSalesCost.getUriageAmountDiff()));
            s004Bean.setMrateTm(syuuekiUtils.mrate(totalSales.getUriageAmountTm(), totalSalesCost.getUriageAmountTm()));

            s004Bean.setMrate1Q(syuuekiUtils.mrate(totalSales.getUriageAmount1Q(), totalSalesCost.getUriageAmount1Q()));
            s004Bean.setMrate2Q(syuuekiUtils.mrate(totalSales.getUriageAmount2Q(), totalSalesCost.getUriageAmount2Q()));
            s004Bean.setMrate3Q(syuuekiUtils.mrate(totalSales.getUriageAmount3Q(), totalSalesCost.getUriageAmount3Q()));
            s004Bean.setMrate4Q(syuuekiUtils.mrate(totalSales.getUriageAmount4Q(), totalSalesCost.getUriageAmount4Q()));
            s004Bean.setMrate1QDiff(syuuekiUtils.mrate(totalSales.getUriageAmount1QDiff(), totalSalesCost.getUriageAmount1QDiff()));
            s004Bean.setMrate2QDiff(syuuekiUtils.mrate(totalSales.getUriageAmount2QDiff(), totalSalesCost.getUriageAmount2QDiff()));
            s004Bean.setMrate3QDiff(syuuekiUtils.mrate(totalSales.getUriageAmount3QDiff(), totalSalesCost.getUriageAmount3QDiff()));
            s004Bean.setMrate4QDiff(syuuekiUtils.mrate(totalSales.getUriageAmount4QDiff(), totalSalesCost.getUriageAmount4QDiff()));
        }

        // 前期までの累計(M率今回)
        if(previousPeriodTotalSales != null && previousPeriodTotalSalesCost != null){
            s004Bean.setMrateMaeAllTotal(syuuekiUtils.mrate(previousPeriodTotalSales.getUriageAmountTotal(), previousPeriodTotalSalesCost.getUriageRuikeiAmount()));
        }

        // M率累計
        if(totalRuikeiSales != null && totalSalesCostRuikei != null){
            s004Bean.setMrateRuikei1(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount1(), totalSalesCostRuikei.getUriageAmount1()));
            s004Bean.setMrateRuikei2(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount2(), totalSalesCostRuikei.getUriageAmount2()));
            s004Bean.setMrateRuikei3(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount3(), totalSalesCostRuikei.getUriageAmount3()));
            s004Bean.setMrateRuikei4(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount4(), totalSalesCostRuikei.getUriageAmount4()));
            s004Bean.setMrateRuikei5(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount5(), totalSalesCostRuikei.getUriageAmount5()));
            s004Bean.setMrateRuikei6(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount6(), totalSalesCostRuikei.getUriageAmount6()));
            s004Bean.setMrateRuikei7(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount7(), totalSalesCostRuikei.getUriageAmount7()));
            s004Bean.setMrateRuikei8(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount8(), totalSalesCostRuikei.getUriageAmount8()));
            s004Bean.setMrateRuikei9(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount9(), totalSalesCostRuikei.getUriageAmount9()));
            s004Bean.setMrateRuikei10(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount10(), totalSalesCostRuikei.getUriageAmount10()));
            s004Bean.setMrateRuikei11(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount11(), totalSalesCostRuikei.getUriageAmount11()));
            s004Bean.setMrateRuikei12(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount12(), totalSalesCostRuikei.getUriageAmount12()));
            s004Bean.setMrateRuikeiK1(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmountK1(), totalSalesCostRuikei.getUriageAmountK1()));
            s004Bean.setMrateRuikeiK2(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmountK2(), totalSalesCostRuikei.getUriageAmountK2()));
            s004Bean.setMrateRuikeiG(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmountG(), totalSalesCostRuikei.getUriageAmountG()));
            s004Bean.setMrateRuikeiF(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmountF(), totalSalesCostRuikei.getUriageAmountF()));
            s004Bean.setMrateRuikeiK1Diff(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmountK1Diff(), totalSalesCostRuikei.getUriageAmountK1Diff()));
            s004Bean.setMrateRuikeiK2Diff(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmountK2Diff(), totalSalesCostRuikei.getUriageAmountK2Diff()));
            s004Bean.setMrateRuikeiGDiff(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmountGDiff(), totalSalesCostRuikei.getUriageAmountGDiff()));
            s004Bean.setMrateRuikeiDiff(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmountDiff(), totalSalesCostRuikei.getUriageAmountDiff()));
            s004Bean.setMrateRuikeiTm(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmountTm(), totalSalesCostRuikei.getUriageAmountTm()));

            s004Bean.setMrateRuikei1Q(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount1Q(), totalSalesCostRuikei.getUriageAmount1Q()));
            s004Bean.setMrateRuikei2Q(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount2Q(), totalSalesCostRuikei.getUriageAmount2Q()));
            s004Bean.setMrateRuikei3Q(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount3Q(), totalSalesCostRuikei.getUriageAmount3Q()));
            s004Bean.setMrateRuikei4Q(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount4Q(), totalSalesCostRuikei.getUriageAmount4Q()));
            s004Bean.setMrateRuikei1QDiff(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount1QDiff(), totalSalesCostRuikei.getUriageAmount1QDiff()));
            s004Bean.setMrateRuikei2QDiff(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount2QDiff(), totalSalesCostRuikei.getUriageAmount2QDiff()));
            s004Bean.setMrateRuikei3QDiff(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount3QDiff(), totalSalesCostRuikei.getUriageAmount3QDiff()));
            s004Bean.setMrateRuikei4QDiff(syuuekiUtils.mrate(totalRuikeiSales.getUriageAmount4QDiff(), totalSalesCostRuikei.getUriageAmount4QDiff()));
        }

        // 前期までの累計(M率累計)
        if(previousPeriodTotalRuikeiSales != null && previousPeriodTotalSalesCostRuikei != null){
            s004Bean.setMrateRuikeiMaeAllTotal(syuuekiUtils.mrate(previousPeriodTotalRuikeiSales.getUriageRuikeiAmount(), previousPeriodTotalSalesCostRuikei.getUriageRuikeiAmount()));
        }

        this.switchingUnit();

        s004Bean.setKikanFromAry(kikanFromAry);
        s004Bean.setKikanToAry(kikanToAry);

        // 期間Fromの選択候補を取得(2014上(Step1前半) add)
        //List<String> kikanFromList = dateilHeader.findKikanFromList();
        //s004Bean.setKikanFromList(kikanFromList);

        // 対象案件の案件FLG(子案件 or 纏め)と、項目[見込入力の案件単位]が一致しているかをチェック(2015上(Step1前半コメント) add)
        // (等しくない場合は編集ボタン、機能-調整口追加ボタンを表示しない)
        SyuGeBukkenInfoTbl bukkenEntity = dateilHeader.getAnkenEntity();
        if (bukkenEntity != null && "1".equals(bukkenEntity.getInputAnkenFlg())) {
            s004Bean.setAnkenInputFlg(1);
        } else {
            s004Bean.setAnkenInputFlg(0);
        }

        setEditAuthFlg();
    }
    private Map<String, Object> getPreviousPeriodMapCondion(String kikanFrom , boolean allPreviousPeriodFlg){

        Map<String, Object> paramPreviousPeriodMap = new HashMap<>();
        if(allPreviousPeriodFlg){
            List<String> kikan = new ArrayList<>();
            String endYm = syuuekiUtils.calcKikan(kikanFrom, -1);
            if(s004Bean.getKikanFromList().contains(endYm)){
                for(String ym : s004Bean.getKikanFromList()){
                    if(ym.equals(endYm)){
                        kikan.add(ym);
                        break;
                    }
                    kikan.add(ym);
                }
            }else{
                kikan.add(endYm);
            }
            paramPreviousPeriodMap.put("zenki", syuuekiUtils.calcKikan(kikanFrom, -1));
            paramPreviousPeriodMap.put("kikan", kikan);
        }else{
            paramPreviousPeriodMap.put("kiTo", syuuekiUtils.calcKikan(kikanFrom, -1));
            paramPreviousPeriodMap.put("rirekiFlg", s004Bean.getRirekiFlg());
        }

        paramPreviousPeriodMap.put("ankenId", s004Bean.getAnkenId());
        paramPreviousPeriodMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));

        return paramPreviousPeriodMap;
    }

    
    /**
     * 契約/売上と、回収が円貨(JPY)のみか外貨含まれているかを確認する
     * (上記金額を小数点2桁表記するかどうかを確定するための処理)
     */
    private void checkCurrencyForeig() {
        // 契約/売上金額の外貨通貨存在チェック
        for (ContractAmount entity: this.contractAmount) {
            String currencyCode = StringUtils.defaultString(entity.getCurrencyCode());
            if (!ConstantString.currencyCodeEn.equals(currencyCode)) {
                s004Bean.setForeignFlg(1);
            }
        }
        
        // 回収金額の外貨通貨存在チェック
        for (RecoveryAmount entity: this.recoveryAmountList) {
            String currencyCode = StringUtils.defaultString(entity.getCurrencyCode());
            if (!ConstantString.currencyCodeEn.equals(currencyCode)) {
                s004Bean.setKaisyuForeignFlg(1);
            }
        }
    }

    /**
     * 取得した各リストの通貨単位切り替えを行う
     */
    private void switchingUnit(){
        // 契約金額合計
        HashMap<String, String> totalContractAmountMap = new HashMap<>();
        totalContractAmountMap.put("KeiyakuAmount1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount1(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount2(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount3", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount3(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount4", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount4(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount5", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount5(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount6", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount6(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount7", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount7(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount8", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount8(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount9", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount9(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount10", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount10(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount11", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount11(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount12", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount12(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmountK1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmountK1(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmountK2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmountK2(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmountG", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmountG(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmountF", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmountF(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmountK1Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmountK1Diff(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmountK2Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmountK2Diff(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmountGDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmountGDiff(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmountDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmountDiff(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmountTm", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmountTm(), s004Bean.getJpyUnit())));

        totalContractAmountMap.put("KeiyakuAmount1Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount1Q(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount2Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount2Q(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount3Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount3Q(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount4Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount4Q(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount1QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount1QDiff(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount2QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount2QDiff(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount3QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount3QDiff(), s004Bean.getJpyUnit())));
        totalContractAmountMap.put("KeiyakuAmount4QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalContractAmount.getKeiyakuAmount4QDiff(), s004Bean.getJpyUnit())));

        totalContractAmountMap.put("KeiyakuAmountZenkiRuikei", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.previousPeriodTotalContractAmount.getKeiyakuAmountK2(), s004Bean.getJpyUnit())));

        this.s004Bean.setTotalContractAmount(totalContractAmountMap);

        // 契約金額内訳
        List<HashMap<String, String>> contractAmountMapList = new ArrayList<>();
        if(this.contractAmount != null){
            int unit = 1;
            for(int i=0; i<this.contractAmount.size(); i++){
                if(this.contractAmount.get(i).getCurrencyCode().equals("JPY")){
                    unit = s004Bean.getJpyUnit();
                }else{
                    unit = 1;
                }

                Integer seq = this.contractAmount.get(i).getSeq();
                Integer formatFlg = s004Bean.getForeignFlg();   // (2018A)通貨JPYしかない場合は整数部のみ表示。外貨通貨が存在する場合は小数点2桁まで表示
//                Integer formatFlg = 1;   // 1:小数2桁まで表示 0:整数部のみ表示
//                if (seq == 2 && "1".equals(s004Bean.getEditFlg())) {
//                    // 補正額で、編集モードの場合は整数のみのフォーマットにする。
//                    formatFlg = 1;
//                }

                HashMap<String, String> contractAmountMap = new HashMap<>();
                contractAmountMap.put("currencyCode", this.contractAmount.get(i).getCurrencyCode());
                contractAmountMap.put("keiyakuRate", this.contractAmount.get(i).getKeiyakuRate());
                contractAmountMap.put("KeiyakuAmount1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount1(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount2(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount3", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount3(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount4", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount4(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount5", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount5(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount6", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount6(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount7", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount7(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount8", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount8(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount9", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount9(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount10", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount10(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount11", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount11(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount12", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount12(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmountK1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmountK1(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmountK2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmountK2(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmountG", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmountG(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmountF", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmountF(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmountK1Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmountK1Diff(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmountK2Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmountK2Diff(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmountGDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmountGDiff(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmountDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmountDiff(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmountTm", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmountTm(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));

                contractAmountMap.put("KeiyakuAmount1Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount1Q(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount2Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount2Q(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount3Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount3Q(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount4Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount4Q(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount1QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount1QDiff(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount2QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount2QDiff(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount3QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount3QDiff(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));
                contractAmountMap.put("KeiyakuAmount4QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.contractAmount.get(i).getKeiyakuAmount4QDiff(), unit, this.contractAmount.get(i).getCurrencyCode()), formatFlg));

                for(int j=0; j<this.previousPeriodContractAmount.size(); j++){
                   if( this.contractAmount.get(i).getCurrencyCode().equals(this.previousPeriodContractAmount.get(j).getCurrencyCode())
                            && this.contractAmount.get(i).getSeq().equals(this.previousPeriodContractAmount.get(j).getSeq())){
                            contractAmountMap.put("KeiyakuAmountZenkiRuikei", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.previousPeriodContractAmount.get(j).getKeiyakuAmountK2(), unit, this.previousPeriodContractAmount.get(j).getCurrencyCode()), formatFlg));
                            break;

                    }
                }
                contractAmountMapList.add(contractAmountMap);
            }
        }
        this.s004Bean.setContractAmount(contractAmountMapList);

        // 見積総原価合計
        HashMap<String, String> totalCostMap = new HashMap<>();
        totalCostMap.put("Net1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet1(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet2(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net3", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet3(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net4", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet4(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net5", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet5(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net6", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet6(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net7", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet7(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net8", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet8(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net9", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet9(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net10", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet10(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net11", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet11(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net12", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet12(), s004Bean.getJpyUnit())));
        totalCostMap.put("NetK1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNetK1(), s004Bean.getJpyUnit())));
        totalCostMap.put("NetK2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNetK2(), s004Bean.getJpyUnit())));
        totalCostMap.put("NetG", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNetG(), s004Bean.getJpyUnit())));
        totalCostMap.put("NetF", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNetF(), s004Bean.getJpyUnit())));
        totalCostMap.put("NetK1Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNetK1Diff(), s004Bean.getJpyUnit())));
        totalCostMap.put("NetK2Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNetK2Diff(), s004Bean.getJpyUnit())));
        totalCostMap.put("NetGDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNetGDiff(), s004Bean.getJpyUnit())));
        totalCostMap.put("NetDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNetDiff(), s004Bean.getJpyUnit())));
        totalCostMap.put("NetTm", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNetTm(), s004Bean.getJpyUnit())));

        totalCostMap.put("Net1Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet1Q(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net2Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet2Q(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net3Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet3Q(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net4Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet4Q(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net1QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet1QDiff(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net2QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet2QDiff(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net3QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet3QDiff(), s004Bean.getJpyUnit())));
        totalCostMap.put("Net4QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalCost.getNet4QDiff(), s004Bean.getJpyUnit())));

        totalCostMap.put("KeiyakuAmountZenkiRuikei", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.previousPeriodTotalCost.getNetK2(), s004Bean.getJpyUnit())));

        this.s004Bean.setTotalCost(totalCostMap);

        // 見積総原価内訳
        HashMap<String, String> costListMap = new HashMap<>();
        if(this.cost2 != null){
            costListMap.put("HatNet1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet1(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet2(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet3", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet3(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet4", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet4(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet5", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet5(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet6", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet6(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet7", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet7(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet8", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet8(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet9", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet9(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet10", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet10(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet11", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet11(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet12", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet12(), s004Bean.getJpyUnit())));
            costListMap.put("HatNetK1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNetK1(), s004Bean.getJpyUnit())));
            costListMap.put("HatNetK2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNetK2(), s004Bean.getJpyUnit())));

            costListMap.put("KeiyakuAmountZenkiRuikeiHatNetK2" , syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.previousPeriodCost2.getHatNetK2(), s004Bean.getJpyUnit())));

            costListMap.put("HatNetG", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNetG(), s004Bean.getJpyUnit())));
            costListMap.put("HatNetF", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNetF(), s004Bean.getJpyUnit())));
            costListMap.put("HatNetK1Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNetK1Diff(), s004Bean.getJpyUnit())));
            costListMap.put("HatNetK2Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNetK2Diff(), s004Bean.getJpyUnit())));
            costListMap.put("HatNetGDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNetGDiff(), s004Bean.getJpyUnit())));
            costListMap.put("HatNetDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNetDiff(), s004Bean.getJpyUnit())));
            costListMap.put("HatNetTm", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNetTm(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet1(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet2(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet3", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet3(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet4", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet4(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet5", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet5(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet6", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet6(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet7", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet7(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet8", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet8(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet9", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet9(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet10", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet10(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet11", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet11(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet12", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet12(), s004Bean.getJpyUnit())));
            costListMap.put("MiNetK1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNetK1(), s004Bean.getJpyUnit())));
            costListMap.put("MiNetK2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNetK2(), s004Bean.getJpyUnit())));

            costListMap.put("KeiyakuAmountZenkiRuikeiMiNetK2" , syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.previousPeriodCost2.getMiNetK2(), s004Bean.getJpyUnit())));

            costListMap.put("MiNetG", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNetG(), s004Bean.getJpyUnit())));
            costListMap.put("MiNetF", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNetF(), s004Bean.getJpyUnit())));
            costListMap.put("MiNetK1Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNetK1Diff(), s004Bean.getJpyUnit())));
            costListMap.put("MiNetK2Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNetK2Diff(), s004Bean.getJpyUnit())));
            costListMap.put("MiNetGDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNetGDiff(), s004Bean.getJpyUnit())));
            costListMap.put("MiNetDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNetDiff(), s004Bean.getJpyUnit())));
            costListMap.put("MiNetTm", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNetTm(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet1(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet2(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet3", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet3(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet4", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet4(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet5", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet5(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet6", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet6(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet7", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet7(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet8", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet8(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet9", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet9(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet10", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet10(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet11", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet11(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet12", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet12(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNetK1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNetK1(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNetK2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNetK2(), s004Bean.getJpyUnit())));

            costListMap.put("KeiyakuAmountZenkiRuikeiSSNK2" , syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.previousPeriodCost2.getSeibanSonekiNetK2(), s004Bean.getJpyUnit())));

            costListMap.put("SeibanSonekiNetG", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNetG(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNetF", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNetF(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNetK1Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNetK1Diff(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNetK2Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNetK2Diff(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNetGDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNetGDiff(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNetDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNetDiff(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNetTm", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNetTm(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo1(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo2(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo3", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo3(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo4", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo4(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo5", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo5(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo6", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo6(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo7", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo7(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo8", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo8(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo9", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo9(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo10", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo10(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo11", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo11(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo12", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo12(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyoK1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyoK1(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyoK2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyoK2(), s004Bean.getJpyUnit())));

            costListMap.put("KeiyakuAmountZenkiRuikeiKEK2" , syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.previousPeriodCost2.getKawaseEikyoK2(), s004Bean.getJpyUnit())));

            costListMap.put("KawaseEikyoG", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyoG(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyoF", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyoF(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyoK1Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyoK1Diff(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyoK2Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyoK2Diff(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyoGDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyoGDiff(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyoDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyoDiff(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyoTm", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyoTm(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet1Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet1Q(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet2Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet2Q(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet3Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet3Q(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet4Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet4Q(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet1QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet1QDiff(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet2QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet2QDiff(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet3QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet3QDiff(), s004Bean.getJpyUnit())));
            costListMap.put("HatNet4QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getHatNet4QDiff(), s004Bean.getJpyUnit())));

            costListMap.put("MiNet1Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet1Q(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet2Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet2Q(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet3Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet3Q(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet4Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet4Q(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet1QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet1QDiff(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet2QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet2QDiff(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet3QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet3QDiff(), s004Bean.getJpyUnit())));
            costListMap.put("MiNet4QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getMiNet4QDiff(), s004Bean.getJpyUnit())));

            costListMap.put("SeibanSonekiNet1Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet1Q(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet2Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet2Q(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet3Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet3Q(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet4Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet4Q(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet1QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet1QDiff(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet2QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet2QDiff(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet3QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet3QDiff(), s004Bean.getJpyUnit())));
            costListMap.put("SeibanSonekiNet4QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getSeibanSonekiNet4QDiff(), s004Bean.getJpyUnit())));

            costListMap.put("KawaseEikyo1Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo1Q(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo2Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo2Q(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo3Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo3Q(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo4Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo4Q(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo1QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo1QDiff(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo2QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo2QDiff(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo3QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo3QDiff(), s004Bean.getJpyUnit())));
            costListMap.put("KawaseEikyo4QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.cost2.getKawaseEikyo4QDiff(), s004Bean.getJpyUnit())));

        }
        this.s004Bean.setCostList(costListMap);

        // 今回売上高
        HashMap<String, String> totalSalesMap = new HashMap<>();
        totalSalesMap.put("UriageAmount1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount1(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount2(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount3", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount3(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount4", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount4(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount5", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount5(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount6", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount6(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount7", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount7(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount8", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount8(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount9", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount9(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount10", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount10(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount11", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount11(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount12", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount12(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmountK1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmountK1(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmountK2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmountK2(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmountG", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmountG(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmountF", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmountF(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmountK1Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmountK1Diff(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmountK2Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmountK2Diff(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmountGDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmountGDiff(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmountDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmountDiff(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmountTm", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmountTm(), s004Bean.getJpyUnit())));

        totalSalesMap.put("UriageAmount1Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount1Q(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount2Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount2Q(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount3Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount3Q(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount4Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount4Q(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount1QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount1QDiff(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount2QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount2QDiff(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount3QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount3QDiff(), s004Bean.getJpyUnit())));
        totalSalesMap.put("UriageAmount4QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSales.getUriageAmount4QDiff(), s004Bean.getJpyUnit())));

        totalSalesMap.put("UriageAmountTotal", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.previousPeriodTotalSales.getUriageAmountTotal(), s004Bean.getJpyUnit())));

        this.s004Bean.setTotalSales(totalSalesMap);

        // 今回売上高内訳
        List<HashMap<String, String>> salesAmountMapList = new ArrayList<>();
        if(this.salesList != null){
            int unit = 1;
            for(int i=0; i<this.salesList.size(); i++){
                if(this.salesList.get(i).getUriageRate() != null && this.salesList.get(i).getUriageRate().equals("kawaseRate")){
                    HashMap<String, String> salesAmountMap = new HashMap<>();
                    salesAmountMap.put("currencyCode", this.salesList.get(i).getCurrencyCode());
                    salesAmountMap.put("uriageRate", this.salesList.get(i).getUriageRate());
                    salesAmountMap.put("UriageAmount1", this.salesList.get(i).getUriageAmount1() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount1()) : null);
                    salesAmountMap.put("UriageAmount2", this.salesList.get(i).getUriageAmount2() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount2()) : null);
                    salesAmountMap.put("UriageAmount3", this.salesList.get(i).getUriageAmount3() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount3()) : null);
                    salesAmountMap.put("UriageAmount4", this.salesList.get(i).getUriageAmount4() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount4()) : null);
                    salesAmountMap.put("UriageAmount5", this.salesList.get(i).getUriageAmount5() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount5()) : null);
                    salesAmountMap.put("UriageAmount6", this.salesList.get(i).getUriageAmount6() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount6()) : null);
                    salesAmountMap.put("UriageAmount7", this.salesList.get(i).getUriageAmount7() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount7()) : null);
                    salesAmountMap.put("UriageAmount8", this.salesList.get(i).getUriageAmount8() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount8()) : null);
                    salesAmountMap.put("UriageAmount9", this.salesList.get(i).getUriageAmount9() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount9()) : null);
                    salesAmountMap.put("UriageAmount10", this.salesList.get(i).getUriageAmount10() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount10()) : null);
                    salesAmountMap.put("UriageAmount11", this.salesList.get(i).getUriageAmount11() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount11()) : null);
                    salesAmountMap.put("UriageAmount12", this.salesList.get(i).getUriageAmount12() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount12()) : null);
                    salesAmountMap.put("UriageAmountK1", this.salesList.get(i).getUriageAmountK1() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmountK1()) : null);
                    salesAmountMap.put("UriageAmountK2", this.salesList.get(i).getUriageAmountK2() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmountK2()) : null);
                    salesAmountMap.put("UriageAmountG", this.salesList.get(i).getUriageAmountG() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmountG()) : null);
                    salesAmountMap.put("UriageAmountF", this.salesList.get(i).getUriageAmountF() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmountF()) : null);
                    salesAmountMap.put("UriageAmountK1Diff", this.salesList.get(i).getUriageAmountK1Diff() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmountK1Diff()) : null);
                    salesAmountMap.put("UriageAmountK2Diff", this.salesList.get(i).getUriageAmountK2Diff() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmountK2Diff()) : null);
                    salesAmountMap.put("UriageAmountGDiff", this.salesList.get(i).getUriageAmountGDiff() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmountGDiff()) : null);
                    salesAmountMap.put("UriageAmountDiff", this.salesList.get(i).getUriageAmountDiff() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmountDiff()) : null);
                    salesAmountMap.put("UriageAmountTm", this.salesList.get(i).getUriageAmountTm() != null ? syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmountTm()) : null);

                    salesAmountMap.put("UriageAmount1Q", syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount1Q()));
                    salesAmountMap.put("UriageAmount2Q", syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount2Q()));
                    salesAmountMap.put("UriageAmount3Q", syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount3Q()));
                    salesAmountMap.put("UriageAmount4Q", syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount4Q()));
                    salesAmountMap.put("UriageAmount1QDiff", syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount1QDiff()));
                    salesAmountMap.put("UriageAmount2QDiff", syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount2QDiff()));
                    salesAmountMap.put("UriageAmount3QDiff", syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount3QDiff()));
                    salesAmountMap.put("UriageAmount4QDiff", syuuekiUtils.exeFormatRate(this.salesList.get(i).getUriageAmount4QDiff()));

                    salesAmountMapList.add(salesAmountMap);

                } else {
                    if(this.salesList.get(i).getCurrencyCode().equals(ConstantString.currencyCodeEn) || this.salesList.get(i).getSeq().equals(3)){
                        unit = s004Bean.getJpyUnit();
                    }else{
                        unit = 1;
                    }

                    //int formatFlg = 1;
                    int formatFlg = s004Bean.getForeignFlg();   // (2018A)通貨JPYしかない場合は整数部のみ表示。外貨通貨が存在する場合は小数点2桁まで表示
                    if (this.salesList.get(i).getSeq().equals(3)) {
                        // [為替差調整]は必ず整数部のみ表示
                        formatFlg = 0;
                    }

                    HashMap<String, String> salesAmountMap = new HashMap<>();
                    salesAmountMap.put("currencyCode", this.salesList.get(i).getCurrencyCode());
                    salesAmountMap.put("uriageRate", this.salesList.get(i).getUriageRate());
                    salesAmountMap.put("UriageAmount1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount1(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount2(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount3", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount3(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount4", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount4(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount5", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount5(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount6", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount6(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount7", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount7(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount8", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount8(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount9", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount9(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount10", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount10(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount11", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount11(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount12", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount12(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmountK1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmountK1(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmountK2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmountK2(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmountG", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmountG(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmountF", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmountF(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmountK1Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmountK1Diff(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmountK2Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmountK2Diff(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmountGDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmountGDiff(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmountDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmountDiff(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmountTm", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmountTm(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));

                    salesAmountMap.put("UriageAmount1Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount1Q(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount2Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount2Q(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount3Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount3Q(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount4Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount4Q(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount1QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount1QDiff(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount2QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount2QDiff(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount3QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount3QDiff(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));
                    salesAmountMap.put("UriageAmount4QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesList.get(i).getUriageAmount4QDiff(), unit, this.salesList.get(i).getCurrencyCode()), formatFlg));

                    for (SalesAmount salesAmount : previousPeriodSalesList) {
                        if (this.salesList.get(i).getCurrencyCode().equals(salesAmount.getCurrencyCode())) {
                            if (this.salesList.get(i).getCurrencyCode().equals(salesAmount.getCurrencyCode()) && (this.salesList.get(i).getUriageRate() == null || this.salesList.get(i).getUriageRate().isEmpty())) {
                                if (salesAmount.getUriageRate() == null || salesAmount.getUriageRate().isEmpty()) {
                                    salesAmountMap.put("UriageAmountTotal", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(salesAmount.getUriageAmountTotal(), unit, salesAmount.getCurrencyCode()), formatFlg));
                                    break;
                                }
                            } else if (this.salesList.get(i).getCurrencyCode().equals(salesAmount.getCurrencyCode()) && (this.salesList.get(i).getUriageRate().equals(salesAmount.getUriageRate()))) {
                                salesAmountMap.put("UriageAmountTotal", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(salesAmount.getUriageKawaseTotal(), unit, salesAmount.getCurrencyCode()), formatFlg));
                                break;
                            }
                        }
                    }
                    salesAmountMapList.add(salesAmountMap);
                }
            }
        }
        this.s004Bean.setSalesList(salesAmountMapList);

        // 売上高累計
        HashMap<String, String> totalRuikeiSalesMap = new HashMap<>();
        totalRuikeiSalesMap.put("UriageAmount1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount1(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount2(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount3", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount3(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount4", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount4(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount5", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount5(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount6", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount6(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount7", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount7(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount8", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount8(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount9", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount9(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount10", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount10(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount11", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount11(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount12", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount12(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmountK1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmountK1(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmountK2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmountK2(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmountG", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmountG(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmountF", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmountF(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmountK1Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmountK1Diff(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmountK2Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmountK2Diff(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmountGDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmountGDiff(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmountDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmountDiff(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmountTm", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmountTm(), s004Bean.getJpyUnit())));

        totalRuikeiSalesMap.put("UriageAmount1Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount1Q(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount2Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount2Q(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount3Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount3Q(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount4Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount4Q(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount1QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount1QDiff(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount2QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount2QDiff(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount3QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount3QDiff(), s004Bean.getJpyUnit())));
        totalRuikeiSalesMap.put("UriageAmount4QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRuikeiSales.getUriageAmount4QDiff(), s004Bean.getJpyUnit())));

        totalRuikeiSalesMap.put("UriageAmountTotal", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.previousPeriodTotalRuikeiSales.getUriageRuikeiAmount(), s004Bean.getJpyUnit())));

        this.s004Bean.setTotalRuikeiSales(totalRuikeiSalesMap);

        // 売上高累計内訳
        List<HashMap<String, String>> ruikeiSalesAmountMapList = new ArrayList<>();
        if(this.ruikeiSalesList != null){
            int unit = 1;
            for(int i=0; i<this.ruikeiSalesList.size(); i++){
                if(this.ruikeiSalesList.get(i).getCurrencyCode().equals("JPY")){
                    unit = s004Bean.getJpyUnit();
                }else{
                    //unit = 1000;
                    unit = 1;
                }

                //int formatFlg = 1;
                int formatFlg = s004Bean.getForeignFlg();   // (2018A)通貨JPYしかない場合は整数部のみ表示。外貨通貨が存在する場合は小数点2桁まで表示

                HashMap<String, String> ruikeiSalesAmountMap = new HashMap<>();
                ruikeiSalesAmountMap.put("currencyCode", this.ruikeiSalesList.get(i).getCurrencyCode());
                ruikeiSalesAmountMap.put("uriageRate", this.ruikeiSalesList.get(i).getUriageRate());
                ruikeiSalesAmountMap.put("UriageAmount1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount1(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount2(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount3", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount3(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount4", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount4(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount5", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount5(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount6", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount6(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount7", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount7(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount8", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount8(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount9", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount9(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount10", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount10(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount11", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount11(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount12", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount12(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmountK1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmountK1(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmountK2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmountK2(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmountG", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmountG(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmountF", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmountF(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmountK1Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmountK1Diff(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmountK2Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmountK2Diff(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmountGDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmountGDiff(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmountDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmountDiff(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmountTm", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmountTm(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));

                ruikeiSalesAmountMap.put("UriageAmount1Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount1Q(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount2Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount2Q(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount3Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount3Q(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount4Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount4Q(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount1QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount1QDiff(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount2QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount2QDiff(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount3QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount3QDiff(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));
                ruikeiSalesAmountMap.put("UriageAmount4QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.ruikeiSalesList.get(i).getUriageAmount4QDiff(), unit, this.ruikeiSalesList.get(i).getCurrencyCode()), formatFlg));

                for(SalesAmount salesAmount : previousPeriodRuikeiSalesList){
                   if(this.ruikeiSalesList.get(i).getCurrencyCode().equals(salesAmount.getCurrencyCode())
                     && this.ruikeiSalesList.get(i).getUriageRate() == salesAmount.getUriageRate() ){
                        ruikeiSalesAmountMap.put("UriageAmountTotal", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(salesAmount.getUriageRuikeiAmount(), unit, salesAmount.getCurrencyCode()), formatFlg));
                   }
                }

                ruikeiSalesAmountMapList.add(ruikeiSalesAmountMap);
            }
        }
        this.s004Bean.setRuikeiSalesList(ruikeiSalesAmountMapList);

        // 今回売上原価合計
        HashMap<String, String> totalSalesCostMap = new HashMap<>();
        totalSalesCostMap.put("UriageAmount1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount1(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount2(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount3", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount3(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount4", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount4(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount5", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount5(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount6", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount6(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount7", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount7(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount8", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount8(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount9", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount9(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount10", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount10(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount11", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount11(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount12", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount12(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmountK1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmountK1(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmountK2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmountK2(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmountG", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmountG(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmountF", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmountF(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmountK1Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmountK1Diff(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmountK2Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmountK2Diff(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmountGDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmountGDiff(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmountDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmountDiff(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmountTm", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmountTm(), s004Bean.getJpyUnit())));

        totalSalesCostMap.put("UriageAmount1Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount1Q(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount2Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount2Q(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount3Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount3Q(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount4Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount4Q(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount1QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount1QDiff(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount2QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount2QDiff(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount3QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount3QDiff(), s004Bean.getJpyUnit())));
        totalSalesCostMap.put("UriageAmount4QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCost.getUriageAmount4QDiff(), s004Bean.getJpyUnit())));

        totalSalesCostMap.put("UriageRuikeiAmount", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.previousPeriodTotalSalesCost.getUriageRuikeiAmount(), s004Bean.getJpyUnit())));

        this.s004Bean.setTotalSalesCost(totalSalesCostMap);

        // 今回売上原価内訳
        List<HashMap<String, String>> salesCostMapList = new ArrayList<>();
        if(this.salesCostList != null){
            for(int i=0; i<this.salesCostList.size(); i++){
                HashMap<String, String> salesCostMap = new HashMap<>();
                salesCostMap.put("categoryCode", this.salesCostList.get(i).getCategoryCode());
                salesCostMap.put("categoryName1", this.salesCostList.get(i).getCategoryName1());
                salesCostMap.put("categoryName2", this.salesCostList.get(i).getCategoryName2());
                salesCostMap.put("categoryCode", this.salesCostList.get(i).getCategoryCode());
                salesCostMap.put("categoryKbn1", this.salesCostList.get(i).getCategoryKbn1());
                salesCostMap.put("categoryKbn2", this.salesCostList.get(i).getCategoryKbn2());
                salesCostMap.put("inputFlg", this.salesCostList.get(i).getInputFlg());
                salesCostMap.put("Net1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet1(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet2(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net3", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet3(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net4", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet4(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net5", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet5(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net6", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet6(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net7", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet7(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net8", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet8(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net9", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet9(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net10", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet10(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net11", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet11(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net12", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet12(), s004Bean.getJpyUnit())));
                salesCostMap.put("NetK1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNetK1(), s004Bean.getJpyUnit())));
                salesCostMap.put("NetK2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNetK2(), s004Bean.getJpyUnit())));
                salesCostMap.put("NetG", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNetG(), s004Bean.getJpyUnit())));
                salesCostMap.put("NetF", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNetF(), s004Bean.getJpyUnit())));
                salesCostMap.put("NetK1Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNetK1Diff(), s004Bean.getJpyUnit())));
                salesCostMap.put("NetK2Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNetK2Diff(), s004Bean.getJpyUnit())));
                salesCostMap.put("NetGDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNetGDiff(), s004Bean.getJpyUnit())));
                salesCostMap.put("NetDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNetDiff(), s004Bean.getJpyUnit())));
                salesCostMap.put("NetTm", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNetTm(), s004Bean.getJpyUnit())));

                salesCostMap.put("Net1Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet1Q(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net2Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet2Q(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net3Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet3Q(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net4Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet4Q(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net1QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet1QDiff(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net2QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet2QDiff(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net3QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet3QDiff(), s004Bean.getJpyUnit())));
                salesCostMap.put("Net4QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.salesCostList.get(i).getNet4QDiff(), s004Bean.getJpyUnit())));

                for(Cost cost : previousPeriodSalesCostList){
                    if(this.salesCostList.get(i).getCategoryCode().equals(cost.getCategoryCode())){
                        salesCostMap.put("UriageRuikeiNet", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(cost.getUriageRuikeiNet(), s004Bean.getJpyUnit())));
                        break;
                    }
                }
                salesCostMapList.add(salesCostMap);
            }
        }
        this.s004Bean.setSalesCostList(salesCostMapList);

        // 売上原価累計合計
        HashMap<String, String> totalRuikeiSalesCostMap = new HashMap<>();
        totalRuikeiSalesCostMap.put("UriageAmount1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount1(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount2(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount3", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount3(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount4", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount4(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount5", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount5(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount6", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount6(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount7", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount7(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount8", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount8(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount9", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount9(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount10", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount10(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount11", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount11(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount12", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount12(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmountK1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmountK1(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmountK2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmountK2(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmountG", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmountG(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmountF", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmountF(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmountK1Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmountK1Diff(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmountK2Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmountK2Diff(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmountGDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmountGDiff(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmountDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmountDiff(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmountTm", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmountTm(), s004Bean.getJpyUnit())));

        totalRuikeiSalesCostMap.put("UriageAmount1Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount1Q(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount2Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount2Q(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount3Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount3Q(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount4Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount4Q(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount1QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount1QDiff(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount2QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount2QDiff(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount3QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount3QDiff(), s004Bean.getJpyUnit())));
        totalRuikeiSalesCostMap.put("UriageAmount4QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalSalesCostRuikei.getUriageAmount4QDiff(), s004Bean.getJpyUnit())));

        totalRuikeiSalesCostMap.put("UriageRuikeiAmount", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.previousPeriodTotalSalesCostRuikei.getUriageRuikeiAmount(), s004Bean.getJpyUnit())));

        this.s004Bean.setTotalSalesCostRuikei(totalRuikeiSalesCostMap);

        // 2017/11/20 #072 ADD 回収Total行追加
        HashMap<String, String> totalRecoveryAmountMap = new HashMap<>();
        if(this.totalRecoveryAmountData != null){
            // 以下は回収金額(円貨)のため、画面で指定した円貨単位に変換
            int unit = s004Bean.getJpyUnit();
            totalRecoveryAmountMap.put("preKaisyuEnkaAmountTotal", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getPreKaisyuEnkaAmountTotal(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount1(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount2(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount3", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount3(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount4", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount4(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount5", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount5(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount6", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount6(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount7", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount7(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount8", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount8(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount9", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount9(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount10", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount10(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount11", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount11(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount12", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount12(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmountK1", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmountK1(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmountK2", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmountK2(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmountG", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmountG(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmountF", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmountF(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmountK1Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmountK1Diff(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmountK2Diff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmountK2Diff(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmountGDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmountGDiff(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmountDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmountDiff(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmountTm", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmountTm(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount1Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount1Q(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount2Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount2Q(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount3Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount3Q(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount4Q", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount4Q(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount1QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount1QDiff(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount2QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount2QDiff(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount3QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount3QDiff(), unit, s004Bean.getCurrencyCodeEn())));
            totalRecoveryAmountMap.put("KaisyuEnkaAmount4QDiff", syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.totalRecoveryAmountData.getKaisyuEnkaAmount4QDiff(), unit, s004Bean.getCurrencyCodeEn())));
        }
        this.s004Bean.setTotalRecoveryAmountData(totalRecoveryAmountMap);

        // 回収金額
        List<HashMap<String, String>> recoveryAmountMapList = new ArrayList<>();
        if(this.recoveryAmountList != null){
            int unit = 1;
            for(int i=0; i<this.recoveryAmountList.size(); i++){
                if(this.recoveryAmountList.get(i).getCurrencyCode().equals(ConstantString.currencyCodeEn)){
                    unit = s004Bean.getJpyUnit();
                }else{
                    //unit = 1000;
                    unit = 1;
                }

                //int formatFlg = 1;
                int formatFlg = s004Bean.getKaisyuForeignFlg();   // (2018A)回収金額の通貨JPYしかない場合は整数部のみ表示。外貨通貨が存在する場合は小数点2桁まで表示

                HashMap<String, String> recoveryAmountMap = new HashMap<>();
                recoveryAmountMap.put("currencyCode", this.recoveryAmountList.get(i).getCurrencyCode());
                recoveryAmountMap.put("zeiKbn", this.recoveryAmountList.get(i).getZeiKbn());
                recoveryAmountMap.put("kinsyuKbn", this.recoveryAmountList.get(i).getKinsyuKbn());
                recoveryAmountMap.put("kaisyuKbn", this.recoveryAmountList.get(i).getKaisyuKbn());
                recoveryAmountMap.put("currencyCodeCount", String.valueOf(this.recoveryAmountList.get(i).getCurrencyCodeCount()));
                recoveryAmountMap.put("preCurrencyCode", this.recoveryAmountList.get(i).getPreCurrencyCode());
                recoveryAmountMap.put("zeiRnm", this.recoveryAmountList.get(i).getZeiRnm());
                recoveryAmountMap.put("zeiRate", this.recoveryAmountList.get(i).getZeiRate() == null ? "" : this.recoveryAmountList.get(i).getZeiRate().toString());

                recoveryAmountMap.put("preKaisyuAmountTotal", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getPreKaisyuAmountTotal(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount1(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount2(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount3", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount3(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount4", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount4(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount5", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount5(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount6", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount6(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount7", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount7(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount8", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount8(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount9", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount9(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount10", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount10(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount11", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount11(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount12", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount12(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmountK1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmountK1(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmountK2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmountK2(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmountG", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmountG(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmountF", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmountF(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmountK1Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmountK1Diff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmountK2Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmountK2Diff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmountGDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmountGDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmountDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmountDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmountTm", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmountTm(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount1Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount1Q(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount2Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount2Q(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount3Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount3Q(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount4Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount4Q(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount1QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount1QDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount2QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount2QDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount3QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount3QDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
                recoveryAmountMap.put("KaisyuAmount4QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuAmount4QDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));

                // 以下は回収金額(円貨)のため、画面で指定した円貨単位に変換
                unit = s004Bean.getJpyUnit();
                recoveryAmountMap.put("preKaisyuEnkaAmountTotal", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getPreKaisyuEnkaAmountTotal(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount1(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount2(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount3", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount3(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount4", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount4(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount5", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount5(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount6", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount6(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount7", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount7(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount8", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount8(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount9", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount9(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount10", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount10(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount11", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount11(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount12", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount12(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmountK1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmountK1(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmountK2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmountK2(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmountG", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmountG(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmountF", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmountF(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmountK1Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmountK1Diff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmountK2Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmountK2Diff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmountGDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmountGDiff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmountDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmountDiff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmountTm", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmountTm(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount1Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount1Q(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount2Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount2Q(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount3Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount3Q(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount4Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount4Q(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount1QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount1QDiff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount2QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount2QDiff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount3QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount3QDiff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaAmount4QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaAmount4QDiff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));

                recoveryAmountMap.put("preKaisyuEnkaZeiTotal", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getPreKaisyuEnkaZeiTotal(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei1(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei2(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei3", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei3(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei4", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei4(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei5", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei5(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei6", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei6(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei7", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei7(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei8", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei8(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei9", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei9(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei10", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei10(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei11", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei11(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei12", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei12(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZeiK1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZeiK1(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZeiK2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZeiK2(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZeiG", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZeiG(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZeiF", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZeiF(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZeiK1Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZeiK1Diff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZeiK2Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZeiK2Diff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZeiGDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZeiGDiff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZeiDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZeiDiff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZeiTm", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZeiTm(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei1Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei1Q(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei2Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei2Q(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei3Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei3Q(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei4Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei4Q(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei1QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei1QDiff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei2QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei2QDiff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei3QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei3QDiff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));
                recoveryAmountMap.put("KaisyuEnkaZei4QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getKaisyuEnkaZei4QDiff(), unit, s004Bean.getCurrencyCodeEn()), formatFlg));

// (2017A)累計回収額/未回収額は非表示になったためコードからも除外
//                recoveryAmountMap.put("RuikeiKaisyuAmount1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount1(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount2(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount3", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount3(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount4", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount4(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount5", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount5(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount6", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount6(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount7", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount7(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount8", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount8(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount9", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount9(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount10", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount10(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount11", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount11(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount12", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount12(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmountK1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmountK1(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmountK2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmountK2(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmountG", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmountG(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmountF", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmountF(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmountK1Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmountK1Diff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmountK2Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmountK2Diff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmountGDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmountGDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmountDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmountDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmountTm", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmountTm(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount1(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount2(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount3", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount3(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount4", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount4(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount5", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount5(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount6", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount6(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount7", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount7(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount8", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount8(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount9", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount9(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount10", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount10(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount11", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount11(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount12", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount12(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmountK1", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmountK1(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmountK2", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmountK2(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmountG", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmountG(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmountF", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmountF(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmountK1Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmountK1Diff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmountK2Diff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmountK2Diff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmountGDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmountGDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmountDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmountDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmountTm", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmountTm(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));

// (2017A)累計回収額は非表示になったためコードからも除外
//                recoveryAmountMap.put("RuikeiKaisyuAmount1Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount1Q(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount2Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount2Q(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount3Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount3Q(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount4Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount4Q(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount1QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount1QDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount2QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount2QDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount3QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount3QDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("RuikeiKaisyuAmount4QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getRuikeiKaisyuAmount4QDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));

// (2017A)未回収額は非表示になったためコードからも除外
//                recoveryAmountMap.put("MiKaisyuAmount1Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount1Q(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount2Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount2Q(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount3Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount3Q(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount4Q", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount4Q(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount1QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount1QDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount2QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount2QDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount3QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount3QDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));
//                recoveryAmountMap.put("MiKaisyuAmount4QDiff", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(this.recoveryAmountList.get(i).getMiKaisyuAmount4QDiff(), unit, this.recoveryAmountList.get(i).getCurrencyCode()), formatFlg));

// (2017A)累計回収額はまとめて取得するようにしたため削除
//                for(RecoveryAmount recoveryAmount : previousPeriodRecoveryAmountList){
//                    if(this.recoveryAmountList.get(i).getCurrencyCode().equals(recoveryAmount.getCurrencyCode())){
//                        recoveryAmountMap.put("KaisyuAmountTotal", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(recoveryAmount.getKaisyuAmountTotal(), unit, recoveryAmount.getCurrencyCode()), formatFlg));
//                        recoveryAmountMap.put("RuikeiKaisyuAmountTotal", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(recoveryAmount.getRuikeiKaisyuAmountTotal(), unit, recoveryAmount.getCurrencyCode()), formatFlg));
//                        recoveryAmountMap.put("MiKaisyuAmountTotal", syuuekiUtils.exeChangeFormatUnit(syuuekiUtils.switchingCurrencyUnit(recoveryAmount.getMiKaisyuAmountTotal(), unit, recoveryAmount.getCurrencyCode()), formatFlg));
//                        break;
//                    }
//                }

                recoveryAmountMapList.add(recoveryAmountMap);
            }
        }
        this.s004Bean.setRecoveryAmountList(recoveryAmountMapList);

    }


    /**
     * 金額の更新処理
     * @param processFlg 処理区分(0:通常の保存処理 1:最新値更新)
     * @return
     * @throws Exception
     */
    public boolean updateExpectedAmount(int processFlg) throws Exception{
        //boolean retFlg = true;
        boolean registFlg = false;
        //try{
            String kikanFrom = s004Bean.getKikanForm();
            String kikanTo   = syuuekiUtils.calcKikan(kikanFrom, 1);

            String[] kikanFromAry = SyuuekiUtils.getKikanMonthAry(kikanFrom);
            String[] kikanToAry = SyuuekiUtils.getKikanMonthAry(kikanTo);
            // 契約金額の更新
            if(this.s004Bean.getInpTargetKeiyakuCurrencyCode() != null){
                for(int i=0; i<this.s004Bean.getInpTargetKeiyakuCurrencyCode().length; i++){
                    // 更新対象(見込列)かつ、値を変更している場合のみ更新を行う
                    //if(this.s004Bean.getInpTargetKeiyakuAmount1() != null && "1".equals(this.s004Bean.getInpTargetKeiyakuAmountUpdateFlg1()[i])){
                    if("1".equals(s004Bean.getInpTargetKeiyakuAmountUpdateFlg1()[i])) {
                        this.updateKeiyakuAmount(s004Bean.getInpTargetKeiyakuCurrencyCode()[i], kikanFromAry[0], s004Bean.getKeiyakuAmount1()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKeiyakuAmount2() != null && "1".equals(this.s004Bean.getInpTargetKeiyakuAmountUpdateFlg2()[i])){
                    if ("1".equals(s004Bean.getInpTargetKeiyakuAmountUpdateFlg2()[i])){
                        this.updateKeiyakuAmount(this.s004Bean.getInpTargetKeiyakuCurrencyCode()[i], kikanFromAry[1], this.s004Bean.getKeiyakuAmount2()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKeiyakuAmount3() != null && "1".equals(this.s004Bean.getInpTargetKeiyakuAmountUpdateFlg3()[i])){
                    if ("1".equals(s004Bean.getInpTargetKeiyakuAmountUpdateFlg3()[i])) {
                        this.updateKeiyakuAmount(this.s004Bean.getInpTargetKeiyakuCurrencyCode()[i], kikanFromAry[2], this.s004Bean.getKeiyakuAmount3()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKeiyakuAmount4() != null && "1".equals(this.s004Bean.getInpTargetKeiyakuAmountUpdateFlg4()[i])){
                    if ("1".equals(s004Bean.getInpTargetKeiyakuAmountUpdateFlg4()[i])) {
                        this.updateKeiyakuAmount(this.s004Bean.getInpTargetKeiyakuCurrencyCode()[i], kikanFromAry[3], this.s004Bean.getKeiyakuAmount4()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKeiyakuAmount5() != null && "1".equals(this.s004Bean.getInpTargetKeiyakuAmountUpdateFlg5()[i])){
                    if ("1".equals(s004Bean.getInpTargetKeiyakuAmountUpdateFlg5()[i])) {
                        this.updateKeiyakuAmount(this.s004Bean.getInpTargetKeiyakuCurrencyCode()[i], kikanFromAry[4], this.s004Bean.getKeiyakuAmount5()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKeiyakuAmount6() != null && "1".equals(this.s004Bean.getInpTargetKeiyakuAmountUpdateFlg6()[i])){
                    if ("1".equals(s004Bean.getInpTargetKeiyakuAmountUpdateFlg6()[i])) {
                        this.updateKeiyakuAmount(this.s004Bean.getInpTargetKeiyakuCurrencyCode()[i], kikanFromAry[5], this.s004Bean.getKeiyakuAmount6()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKeiyakuAmount7() != null && "1".equals(this.s004Bean.getInpTargetKeiyakuAmountUpdateFlg7()[i])){
                    if ("1".equals(s004Bean.getInpTargetKeiyakuAmountUpdateFlg7()[i])) {
                        this.updateKeiyakuAmount(this.s004Bean.getInpTargetKeiyakuCurrencyCode()[i], kikanToAry[0], this.s004Bean.getKeiyakuAmount7()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKeiyakuAmount8() != null && "1".equals(this.s004Bean.getInpTargetKeiyakuAmountUpdateFlg8()[i])){
                    if ("1".equals(s004Bean.getInpTargetKeiyakuAmountUpdateFlg8()[i])) {
                        this.updateKeiyakuAmount(this.s004Bean.getInpTargetKeiyakuCurrencyCode()[i], kikanToAry[1], this.s004Bean.getKeiyakuAmount8()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKeiyakuAmount9() != null && "1".equals(this.s004Bean.getInpTargetKeiyakuAmountUpdateFlg9()[i])){
                    if ("1".equals(s004Bean.getInpTargetKeiyakuAmountUpdateFlg9()[i])) {
                        this.updateKeiyakuAmount(this.s004Bean.getInpTargetKeiyakuCurrencyCode()[i], kikanToAry[2], this.s004Bean.getKeiyakuAmount9()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKeiyakuAmount10() != null && "1".equals(this.s004Bean.getInpTargetKeiyakuAmountUpdateFlg10()[i])){
                    if ("1".equals(s004Bean.getInpTargetKeiyakuAmountUpdateFlg10()[i])) {
                        this.updateKeiyakuAmount(this.s004Bean.getInpTargetKeiyakuCurrencyCode()[i], kikanToAry[3], this.s004Bean.getKeiyakuAmount10()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKeiyakuAmount11() != null && "1".equals(this.s004Bean.getInpTargetKeiyakuAmountUpdateFlg11()[i])){
                    if ("1".equals(s004Bean.getInpTargetKeiyakuAmountUpdateFlg11()[i])) {
                        this.updateKeiyakuAmount(this.s004Bean.getInpTargetKeiyakuCurrencyCode()[i], kikanToAry[4], this.s004Bean.getKeiyakuAmount11()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKeiyakuAmount12() != null && "1".equals(this.s004Bean.getInpTargetKeiyakuAmountUpdateFlg12()[i])){
                    if ("1".equals(s004Bean.getInpTargetKeiyakuAmountUpdateFlg12()[i])) {
                        this.updateKeiyakuAmount(this.s004Bean.getInpTargetKeiyakuCurrencyCode()[i], kikanToAry[5], this.s004Bean.getKeiyakuAmount12()[i]);
                        registFlg = true;
                    }
                }
            }

            // 未発番NET、製番損益NETの更新
            //if(this.s004Bean.getInpTargetMiNet1() != null && ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg1()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg1()))){
            if ("1".equals(s004Bean.getInpTargetMiNetUpdateFlg1()) || "1".equals(s004Bean.getInpTargetSeibanSonekiNetUpdateFlg1())
                    || "1".equals(s004Bean.getInpTargetKawaseEikyoUpdateFlg1()) || "1".equals(s004Bean.getInpTargetHatNetUpdateFlg1())){
                this.updateSougenka(kikanFromAry[0], this.s004Bean.getHatNet1(), this.s004Bean.getMiNet1(), this.s004Bean.getSeibanSonekiNet1(), this.s004Bean.getKawaseEikyo1());
                registFlg = true;
            }
            //if(this.s004Bean.getInpTargetMiNet2() != null && ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg2()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg2()))){
            if ("1".equals(s004Bean.getInpTargetMiNetUpdateFlg2()) || "1".equals(s004Bean.getInpTargetSeibanSonekiNetUpdateFlg2())
                    || "1".equals(s004Bean.getInpTargetKawaseEikyoUpdateFlg2()) || "1".equals(s004Bean.getInpTargetHatNetUpdateFlg2())){
                this.updateSougenka(kikanFromAry[1], this.s004Bean.getHatNet2(), this.s004Bean.getMiNet2(), this.s004Bean.getSeibanSonekiNet2(), this.s004Bean.getKawaseEikyo2());
                registFlg = true;
            }
            //if(this.s004Bean.getInpTargetMiNet3() != null && ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg3()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg3()))){
            if ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg3()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg3())
                    || "1".equals(s004Bean.getInpTargetKawaseEikyoUpdateFlg3()) || "1".equals(s004Bean.getInpTargetHatNetUpdateFlg3())){
                this.updateSougenka(kikanFromAry[2], this.s004Bean.getHatNet3(), this.s004Bean.getMiNet3(), this.s004Bean.getSeibanSonekiNet3(), this.s004Bean.getKawaseEikyo3());
                registFlg = true;
            }
            //if(this.s004Bean.getInpTargetMiNet4() != null && ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg4()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg4()))){
            if ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg4()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg4())
                    || "1".equals(s004Bean.getInpTargetKawaseEikyoUpdateFlg4()) || "1".equals(s004Bean.getInpTargetHatNetUpdateFlg4())){
                this.updateSougenka(kikanFromAry[3], this.s004Bean.getHatNet4(), this.s004Bean.getMiNet4(), this.s004Bean.getSeibanSonekiNet4(), this.s004Bean.getKawaseEikyo4());
                registFlg = true;
            }
            //if(this.s004Bean.getInpTargetMiNet5() != null && ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg5()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg5()))){
            if ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg5()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg5())
                    || "1".equals(s004Bean.getInpTargetKawaseEikyoUpdateFlg5()) || "1".equals(s004Bean.getInpTargetHatNetUpdateFlg5())){
                this.updateSougenka(kikanFromAry[4], this.s004Bean.getHatNet5(), this.s004Bean.getMiNet5(), this.s004Bean.getSeibanSonekiNet5(), this.s004Bean.getKawaseEikyo5());
                registFlg = true;
            }
            //if(this.s004Bean.getInpTargetMiNet6() != null && ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg6()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg6()))){
            if ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg6()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg6())
                    || "1".equals(s004Bean.getInpTargetKawaseEikyoUpdateFlg6()) || "1".equals(s004Bean.getInpTargetHatNetUpdateFlg6())){
                this.updateSougenka(kikanFromAry[5], this.s004Bean.getHatNet6(), this.s004Bean.getMiNet6(), this.s004Bean.getSeibanSonekiNet6(), this.s004Bean.getKawaseEikyo6());
                registFlg = true;
            }
            //if(this.s004Bean.getInpTargetMiNet7() != null && ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg7()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg7()))){
            if ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg7()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg7())
                    || "1".equals(s004Bean.getInpTargetKawaseEikyoUpdateFlg7()) || "1".equals(s004Bean.getInpTargetHatNetUpdateFlg7())){
                this.updateSougenka(kikanToAry[0], this.s004Bean.getHatNet7(), this.s004Bean.getMiNet7(), this.s004Bean.getSeibanSonekiNet7(), this.s004Bean.getKawaseEikyo7());
                registFlg = true;
            }
            //if(this.s004Bean.getInpTargetMiNet8() != null && ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg8()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg8()))){
            if ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg8()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg8())
                    || "1".equals(s004Bean.getInpTargetKawaseEikyoUpdateFlg8()) || "1".equals(s004Bean.getInpTargetHatNetUpdateFlg8())){
                this.updateSougenka(kikanToAry[1], this.s004Bean.getHatNet8(), this.s004Bean.getMiNet8(), this.s004Bean.getSeibanSonekiNet8(), this.s004Bean.getKawaseEikyo8());
                registFlg = true;
            }
            //if(this.s004Bean.getInpTargetMiNet9() != null && ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg9()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg9()))){
            if ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg9()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg9())
                    || "1".equals(s004Bean.getInpTargetKawaseEikyoUpdateFlg9()) || "1".equals(s004Bean.getInpTargetHatNetUpdateFlg9())){
                this.updateSougenka(kikanToAry[2], this.s004Bean.getHatNet9(), this.s004Bean.getMiNet9(), this.s004Bean.getSeibanSonekiNet9(), this.s004Bean.getKawaseEikyo9());
                registFlg = true;
            }
            //if(this.s004Bean.getInpTargetMiNet10() != null && ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg10()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg10()))){
            if ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg10()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg10())
                    || "1".equals(s004Bean.getInpTargetKawaseEikyoUpdateFlg10()) || "1".equals(s004Bean.getInpTargetHatNetUpdateFlg10())){
                this.updateSougenka(kikanToAry[3], this.s004Bean.getHatNet10(), this.s004Bean.getMiNet10(), this.s004Bean.getSeibanSonekiNet10(), this.s004Bean.getKawaseEikyo10());
                registFlg = true;
            }
            //if(this.s004Bean.getInpTargetMiNet11() != null && ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg11()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg11()))){
            if ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg11()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg11())
                    || "1".equals(s004Bean.getInpTargetKawaseEikyoUpdateFlg11()) || "1".equals(s004Bean.getInpTargetHatNetUpdateFlg11())){
                this.updateSougenka(kikanToAry[4], this.s004Bean.getHatNet11(), this.s004Bean.getMiNet11(), this.s004Bean.getSeibanSonekiNet11(), this.s004Bean.getKawaseEikyo11());
                registFlg = true;
            }
            //if(this.s004Bean.getInpTargetMiNet12() != null && ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg12()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg12()))){
            if ("1".equals(this.s004Bean.getInpTargetMiNetUpdateFlg12()) || "1".equals(this.s004Bean.getInpTargetSeibanSonekiNetUpdateFlg12())
                    || "1".equals(s004Bean.getInpTargetKawaseEikyoUpdateFlg12()) || "1".equals(s004Bean.getInpTargetHatNetUpdateFlg12())){
                this.updateSougenka(kikanToAry[5], this.s004Bean.getHatNet12(), this.s004Bean.getMiNet12(), this.s004Bean.getSeibanSonekiNet12(), this.s004Bean.getKawaseEikyo12());
                registFlg = true;
            }

            // 売上原価の更新
            if (this.s004Bean.getInpTargetNetCategoryCode() != null) {
                for (int i=0; i<this.s004Bean.getInpTargetNetCategoryCode().length; i++) {
                    //if(this.s004Bean.getInpTargetNet1() != null && "1".equals(this.s004Bean.getInpTargetNetUpdateFlg1()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetNetUpdateFlg1()[i])) {
                        this.updateNet(this.s004Bean.getInpTargetNetCategoryCode()[i], this.s004Bean.getInpTargetNetCategoryKbn1()[i], this.s004Bean.getInpTargetNetCategoryKbn2()[i], kikanFromAry[0], this.s004Bean.getNet1()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetNet2() != null && "1".equals(this.s004Bean.getInpTargetNetUpdateFlg2()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetNetUpdateFlg2()[i])) {
                        this.updateNet(this.s004Bean.getInpTargetNetCategoryCode()[i], this.s004Bean.getInpTargetNetCategoryKbn1()[i], this.s004Bean.getInpTargetNetCategoryKbn2()[i], kikanFromAry[1], this.s004Bean.getNet2()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetNet3() != null && "1".equals(this.s004Bean.getInpTargetNetUpdateFlg3()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetNetUpdateFlg3()[i])) {
                        this.updateNet(this.s004Bean.getInpTargetNetCategoryCode()[i], this.s004Bean.getInpTargetNetCategoryKbn1()[i], this.s004Bean.getInpTargetNetCategoryKbn2()[i], kikanFromAry[2], this.s004Bean.getNet3()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetNet4() != null && "1".equals(this.s004Bean.getInpTargetNetUpdateFlg4()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetNetUpdateFlg4()[i])) {
                        this.updateNet(this.s004Bean.getInpTargetNetCategoryCode()[i], this.s004Bean.getInpTargetNetCategoryKbn1()[i], this.s004Bean.getInpTargetNetCategoryKbn2()[i], kikanFromAry[3], this.s004Bean.getNet4()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetNet5() != null && "1".equals(this.s004Bean.getInpTargetNetUpdateFlg5()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetNetUpdateFlg5()[i])) {
                        this.updateNet(this.s004Bean.getInpTargetNetCategoryCode()[i], this.s004Bean.getInpTargetNetCategoryKbn1()[i], this.s004Bean.getInpTargetNetCategoryKbn2()[i], kikanFromAry[4], this.s004Bean.getNet5()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetNet6() != null && "1".equals(this.s004Bean.getInpTargetNetUpdateFlg6()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetNetUpdateFlg6()[i])) {
                        this.updateNet(this.s004Bean.getInpTargetNetCategoryCode()[i], this.s004Bean.getInpTargetNetCategoryKbn1()[i], this.s004Bean.getInpTargetNetCategoryKbn2()[i], kikanFromAry[5], this.s004Bean.getNet6()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetNet7() != null && "1".equals(this.s004Bean.getInpTargetNetUpdateFlg7()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetNetUpdateFlg7()[i])) {
                        this.updateNet(this.s004Bean.getInpTargetNetCategoryCode()[i], this.s004Bean.getInpTargetNetCategoryKbn1()[i], this.s004Bean.getInpTargetNetCategoryKbn2()[i], kikanToAry[0], this.s004Bean.getNet7()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetNet8() != null && "1".equals(this.s004Bean.getInpTargetNetUpdateFlg8()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetNetUpdateFlg8()[i])) {
                        this.updateNet(this.s004Bean.getInpTargetNetCategoryCode()[i], this.s004Bean.getInpTargetNetCategoryKbn1()[i], this.s004Bean.getInpTargetNetCategoryKbn2()[i], kikanToAry[1], this.s004Bean.getNet8()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetNet9() != null && "1".equals(this.s004Bean.getInpTargetNetUpdateFlg9()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetNetUpdateFlg9()[i])) {
                        this.updateNet(this.s004Bean.getInpTargetNetCategoryCode()[i], this.s004Bean.getInpTargetNetCategoryKbn1()[i], this.s004Bean.getInpTargetNetCategoryKbn2()[i], kikanToAry[2], this.s004Bean.getNet9()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetNet10() != null && "1".equals(this.s004Bean.getInpTargetNetUpdateFlg10()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetNetUpdateFlg10()[i])) {
                        this.updateNet(this.s004Bean.getInpTargetNetCategoryCode()[i], this.s004Bean.getInpTargetNetCategoryKbn1()[i], this.s004Bean.getInpTargetNetCategoryKbn2()[i], kikanToAry[3], this.s004Bean.getNet10()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetNet11() != null && "1".equals(this.s004Bean.getInpTargetNetUpdateFlg11()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetNetUpdateFlg11()[i])) {
                        this.updateNet(this.s004Bean.getInpTargetNetCategoryCode()[i], this.s004Bean.getInpTargetNetCategoryKbn1()[i], this.s004Bean.getInpTargetNetCategoryKbn2()[i], kikanToAry[4], this.s004Bean.getNet11()[i]);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetNet12() != null && "1".equals(this.s004Bean.getInpTargetNetUpdateFlg12()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetNetUpdateFlg12()[i])) {
                        this.updateNet(this.s004Bean.getInpTargetNetCategoryCode()[i], this.s004Bean.getInpTargetNetCategoryKbn1()[i], this.s004Bean.getInpTargetNetCategoryKbn2()[i], kikanToAry[5], this.s004Bean.getNet12()[i]);
                        registFlg = true;
                    }
                }
            }

            // 回収金額の更新
            if(this.s004Bean.getInpTargetKaisyuAmountCurrencyCode() != null){
                // 見込-実績取得対象月(現在の勘定月-1ヵ月)
                // (進行基準案件のため、進行基準の勘定月を取得)
                String getSyuekiYm = getDiffTargetYm( kanjyoMstFacade.getNowKanjoDate(ConstantString.salesClassS));

                for(int i=0; i<this.s004Bean.getInpTargetKaisyuAmountCurrencyCode().length; i++){
                    //if(this.s004Bean.getInpTargetKaisyuAmount1() != null && "1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg1()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg1()[i])) {
                        this.updateKaisyuAmount(i, NumberUtils.changeBigDecimal(s004Bean.getKaisyuAmount1()[i]), NumberUtils.changeBigDecimal(s004Bean.getKaisyuEnkaAmount1()[i]), kikanFromAry[0], getSyuekiYm);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKaisyuAmount2() != null && "1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg2()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg2()[i])) {
                        this.updateKaisyuAmount(i, NumberUtils.changeBigDecimal(s004Bean.getKaisyuAmount2()[i]), NumberUtils.changeBigDecimal(s004Bean.getKaisyuEnkaAmount2()[i]), kikanFromAry[1], getSyuekiYm);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKaisyuAmount3() != null && "1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg3()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg3()[i])) {
                        this.updateKaisyuAmount(i, NumberUtils.changeBigDecimal(s004Bean.getKaisyuAmount3()[i]), NumberUtils.changeBigDecimal(s004Bean.getKaisyuEnkaAmount3()[i]), kikanFromAry[2], getSyuekiYm);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKaisyuAmount4() != null && "1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg4()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg4()[i])) {
                        this.updateKaisyuAmount(i, NumberUtils.changeBigDecimal(s004Bean.getKaisyuAmount4()[i]), NumberUtils.changeBigDecimal(s004Bean.getKaisyuEnkaAmount4()[i]), kikanFromAry[3], getSyuekiYm);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKaisyuAmount5() != null && "1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg5()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg5()[i])) {
                        this.updateKaisyuAmount(i, NumberUtils.changeBigDecimal(s004Bean.getKaisyuAmount5()[i]), NumberUtils.changeBigDecimal(s004Bean.getKaisyuEnkaAmount5()[i]), kikanFromAry[4], getSyuekiYm);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKaisyuAmount6() != null && "1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg6()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg6()[i])) {
                        this.updateKaisyuAmount(i, NumberUtils.changeBigDecimal(s004Bean.getKaisyuAmount6()[i]), NumberUtils.changeBigDecimal(s004Bean.getKaisyuEnkaAmount6()[i]), kikanFromAry[5], getSyuekiYm);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKaisyuAmount7() != null && "1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg7()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg7()[i])) {
                        this.updateKaisyuAmount(i, NumberUtils.changeBigDecimal(s004Bean.getKaisyuAmount7()[i]), NumberUtils.changeBigDecimal(s004Bean.getKaisyuEnkaAmount7()[i]), kikanToAry[0], getSyuekiYm);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKaisyuAmount8() != null && "1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg8()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg8()[i])) {
                        this.updateKaisyuAmount(i, NumberUtils.changeBigDecimal(s004Bean.getKaisyuAmount8()[i]), NumberUtils.changeBigDecimal(s004Bean.getKaisyuEnkaAmount8()[i]), kikanToAry[1], getSyuekiYm);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKaisyuAmount9() != null && "1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg9()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg9()[i])) {
                        this.updateKaisyuAmount(i, NumberUtils.changeBigDecimal(s004Bean.getKaisyuAmount9()[i]), NumberUtils.changeBigDecimal(s004Bean.getKaisyuEnkaAmount9()[i]), kikanToAry[2], getSyuekiYm);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKaisyuAmount10() != null && "1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg10()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg10()[i])) {
                        this.updateKaisyuAmount(i, NumberUtils.changeBigDecimal(s004Bean.getKaisyuAmount10()[i]), NumberUtils.changeBigDecimal(s004Bean.getKaisyuEnkaAmount10()[i]), kikanToAry[3], getSyuekiYm);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKaisyuAmount11() != null && "1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg11()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg11()[i])) {
                        this.updateKaisyuAmount(i, NumberUtils.changeBigDecimal(s004Bean.getKaisyuAmount11()[i]), NumberUtils.changeBigDecimal(s004Bean.getKaisyuEnkaAmount11()[i]), kikanToAry[4], getSyuekiYm);
                        registFlg = true;
                    }
                    //if(this.s004Bean.getInpTargetKaisyuAmount12() != null && "1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg12()[i])){
                    if ("1".equals(this.s004Bean.getInpTargetKaisyuAmountUpdateFlg12()[i])) {
                        this.updateKaisyuAmount(i, NumberUtils.changeBigDecimal(s004Bean.getKaisyuAmount12()[i]), NumberUtils.changeBigDecimal(s004Bean.getKaisyuEnkaAmount12()[i]), kikanToAry[5], getSyuekiYm);
                        registFlg = true;
                    }
                }
            }


            // 再計算FLGを設定
            geBukenInfoTblFacade.setSaikeisanFlg(s004Bean.getAnkenId(), (new Integer(s004Bean.getRirekiId())), "1");

            // 再計算処理を実行
            calc(processFlg);

            // 備考を登録
            geBukenInfoTblFacade.setBiko(s004Bean.getAnkenId(), (new Integer(s004Bean.getRirekiId())), s004Bean.getBikou(), "KI");

            // データが登録できた場合
            if (registFlg) {
                // 操作ログ出力
                registOperationLog(SAVE_ONLINE);
            }

        //}catch(Exception e){
        //    throw e;
        //}

        return true;
    }

    /**
     * 再計算処理実行
     */
    public void calc(int processFlg) throws Exception {
        String recalProcFlg = "0";

        if (processFlg == 1) {
            // 最新値更新パッケージ(TSIS様作成)呼出し
            storedProceduresService.callUpdateNewData(s004Bean.getAnkenId(), s004Bean.getRirekiId(), s004Bean.getUpdateNewDataKbn());
            recalProcFlg = "4";   // 最新値更新パッケージ実行の場合、後続の再計算処理の処理フラグ(PROC_FLG)は"4"にする。
        }

        // 再計算処理を実行(STEP4修正)
        //callAnkenRecal();
        storedProceduresService.callAnkenRecalAuto(s004Bean.getAnkenId(), s004Bean.getRirekiId(), recalProcFlg);

        // 再計算FLGを解除
        geBukenInfoTblFacade.setSaikeisanFlg(s004Bean.getAnkenId(), (new Integer(s004Bean.getRirekiId())), "0");
    }

    /**
     * 契約金額の更新
     * @param currencyCode
     * @param syukeiYm
     * @param keiyakuAmount
     * @return
     */
    private boolean updateKeiyakuAmount(String currencyCode, String syukeiYm, String keiyakuAmount){
        BigDecimal amount = Utils.changeBigDecimal(keiyakuAmount);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("userId", loginUserInfo.getUserId());
        paramMap.put("currencyCode", currencyCode);
        paramMap.put("syukeiYm", syukeiYm);
        paramMap.put("keiyakuAmount", amount);

        if(s004UpdateFacade.updateContractAmount(paramMap) == 0){
           s004UpdateFacade.insertContractAmount(paramMap);
        }
        return true;
    }

    /**
     * 未発番NET、製番損益NETの更新
     * @param syukeiYm
     * @param hatNet
     * @param miNet
     * @param seibanSonekiNet
     * @return
     */
    private boolean updateSougenka(String syukeiYm, String hatNet, String miNet, String seibanSonekiNet ,String kawaseEikyo){
        BigDecimal amount0 = null;
        BigDecimal amount1 = null;
        BigDecimal amount2 = null;
        BigDecimal amount3 = null;

        if (!"noUpdate".equals(hatNet)) {
            amount0 = Utils.changeBigDecimal(hatNet);
        }
        if (!"noUpdate".equals(miNet)) {
            amount1 = Utils.changeBigDecimal(miNet);
        }
        if (!"noUpdate".equals(seibanSonekiNet)) {
            amount2 = Utils.changeBigDecimal(seibanSonekiNet);
        }
        if (!"noUpdate".equals(kawaseEikyo)) {
            amount3 = Utils.changeBigDecimal(kawaseEikyo);
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("userId", loginUserInfo.getUserId());
        paramMap.put("syukeiYm", syukeiYm);

        paramMap.put("hatNet", amount0);
        paramMap.put("hatNetUpdateFlg", 1);
        if ("noUpdate".equals(hatNet)) {
            paramMap.put("hatNetUpdateFlg", 0);
        }

        paramMap.put("miNet", amount1);
        paramMap.put("miNetUpdateFlg", 1);
        if ("noUpdate".equals(miNet)) {
            paramMap.put("miNetUpdateFlg", 0);
        }

        paramMap.put("seibanSonekiNet", amount2);
        paramMap.put("seibanSonekiNetUpdateFlg", 1);
        if ("noUpdate".equals(seibanSonekiNet)) {
            paramMap.put("seibanSonekiNetUpdateFlg", 0);
        }

        paramMap.put("kawaseEikyo", amount3);
        paramMap.put("kawaseEikyoUpdateFlg", 1);
        if ("noUpdate".equals(kawaseEikyo)) {
            paramMap.put("kawaseEikyoUpdateFlg", 0);
        }

        if(s004UpdateFacade.updateCurrencyCost(paramMap) == 0){
            s004UpdateFacade.insertCurrencyCost(paramMap);
        }
        return true;
    }

    /**
     * 売上原価内訳の更新
     * @param categoryCode
     * @param categoryName1
     * @param categoryName2
     * @param syukeiYm
     * @param net
     * @return
     */
    private boolean updateNet(String categoryCode, String categoryKbn1, String categoryKbn2, String syukeiYm, String net){
        BigDecimal amount = Utils.changeBigDecimal(net);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("userId", loginUserInfo.getUserId());
        paramMap.put("categoryCode", categoryCode);
        paramMap.put("categoryKbn1", categoryKbn1);
        paramMap.put("categoryKbn2", categoryKbn2);
        paramMap.put("syukeiYm", syukeiYm);
        paramMap.put("net", amount);

        if(s004UpdateFacade.updateSaleCostDetail(paramMap) == 0){
            s004UpdateFacade.insertSaleCostDetail(paramMap);
        }
        return true;
    }

    /**
     * 回収金額の更新
     * @param currencyCode
     * @param syukeiYm
     * @param kaisyuAmount
     * @param kanjoDate
     * @return
     */
    //private boolean updateKaisyuAmount(String currencyCode, String syukeiYm, String kaisyuAmount, String kanjoDate) {
    private boolean updateKaisyuAmount(int kaisyuRowIndex, BigDecimal kaisyuAmount, BigDecimal kaisyuEnkaAmount, String syukeiYm, String kanjoDate) throws Exception {
        //SyuZeikbnMst zeiKbnMstEntity;

        // PK項目
        String currencyCode = s004Bean.getInpTargetKaisyuAmountCurrencyCode()[kaisyuRowIndex];
        String zeiKbn = s004Bean.getInpTargetKaisyuAmountZeiKbn()[kaisyuRowIndex];
        String kinsyuKbn = s004Bean.getInpTargetKaisyuAmountKinsyuKbn()[kaisyuRowIndex];
        String kaisyuKbn = s004Bean.getInpTargetKaisyuAmountKaisyuKbn()[kaisyuRowIndex];

        // 回収金額
        BigDecimal amount = kaisyuAmount;

        // 回収円貨額
        BigDecimal enkaAmount = kaisyuEnkaAmount;
        if (ConstantString.currencyCodeEn.equals(currencyCode)) {
            // 円貨通貨(JPY)の場合は外貨・円貨の額は同一とする(画面上の入力欄も1つになっている).
            enkaAmount = amount;
        }

        // 回収レート
        BigDecimal kaisyuRate = NumberUtils.div(enkaAmount, amount, 7);
        // 回収レートがDBへ格納可能な桁数を超えてしまう場合はnullにする。
        if (kaisyuRate != null) {
            BigDecimal ngKaisyuRateMini = new BigDecimal(ConstantString.ngKaisyuRateMini);
            if (kaisyuRate.compareTo(ngKaisyuRateMini) >= 0) {
                kaisyuRate = null;
            }
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("userId", loginUserInfo.getUserId());
        paramMap.put("currencyCode", currencyCode);
        paramMap.put("syukeiYm", syukeiYm);
        paramMap.put("kinsyuKbn", kinsyuKbn);
        paramMap.put("kaisyuKbn", kaisyuKbn);
        paramMap.put("zeiKbn", zeiKbn);
        paramMap.put("kaisyuAmount", amount);
        paramMap.put("kaisyuEnkaAmount", enkaAmount);
        paramMap.put("kaisyuRate", kaisyuRate);

        //paramMap.put("kinsyuKbn", ConstantString.kaisyuKinsyuGenkin);
        //paramMap.put("kaisyuKbn", ConstantString.kaisyuKbnNormal);

//        if (currencyCode.equals(ConstantString.currencyCodeEn)) {
//            zeiKbnMstEntity = syuZeikbnMstFacade.findDefaultZeiKbnMst();
//        } else {
//            zeiKbnMstEntity = syuZeikbnMstFacade.findPkZeiKbnMst(ConstantString.menzeiZeiKbn);
//        }
//        if (zeiKbnMstEntity == null) {
//            throw new PspRunTimeExceotion("税区分マスタ[SYU_ZEIKBN_MST]にデータが存在しません. CURRENCY_CODE=" + currencyCode);
//        }
//        paramMap.put("zeiKbn", zeiKbnMstEntity.getZeiKbn());

//        List<SyuCurMst> list = syuCurMstFacade.findAll();
//        for (SyuCurMst syuCurMst : list) {
//            if (syuCurMst.getCurrencyCode().equals(currencyCode)) {
//                paramMap.put("currencyCodeSeq", syuCurMst.getDispSeq());
//                break;
//            }
//        }


        // 勘定日付が未設定は見込みのみ追加・更新
        if (StringUtils.isEmpty(kanjoDate)) {
            paramMap.put("dataKbn", "M");
            syuKiKaisyuTblFacade.entrySyuKiKaisyuTbl(paramMap);

//            if (s004UpdateFacade.updateRecoveryAmount(paramMap) == 0) {
//                s004UpdateFacade.insertRecoveryAmount(paramMap);
//            }

        } else {
            // 当月、未来日付は見込を追加・更新
            if (syukeiYm.compareTo(kanjoDate) >= 0) {
                paramMap.put("dataKbn", "M");
                syuKiKaisyuTblFacade.entrySyuKiKaisyuTbl(paramMap);
            }

            // 実績は過去・当月・未来関わらず登録
            paramMap.put("dataKbn", "J");
            syuKiKaisyuTblFacade.entrySyuKiKaisyuTbl(paramMap);

//            if (syukeiYm.compareTo(kanjoDate) >= 0) {
//                if (s004UpdateFacade.updateRecoveryAmount(paramMap) == 0) {
//                    s004UpdateFacade.insertRecoveryAmount(paramMap);
//                }
//                if (s004UpdateFacade.updateRecoveryJissekiAmount(paramMap) == 0) {
//                    s004UpdateFacade.insertRecoveryJissekiAmount(paramMap);
//                }
//            } else {
//                // 過去日付は実績のみ追加・更新
//                if (s004UpdateFacade.updateRecoveryJissekiAmount(paramMap) == 0) {
//                    s004UpdateFacade.insertRecoveryJissekiAmount(paramMap);
//                }
//            }
        }
        return true;
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
        //operationLog.setObjectType("KIKAN_S");
        operationLog.setObjectType(operationLogService.getObjectType("S004"));
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
     * 第1引数と第2引数を加算
     */
    private String mikomiJissekiSum(String val1, String val2) {
        BigDecimal bigVal1 = null;
        BigDecimal bigVal2 = null;
        BigDecimal answerVal = null;
        String anserStr = null;

        bigVal1 = Utils.changeBigDecimal(val1);
        bigVal2 = Utils.changeBigDecimal(val2);

        if (bigVal1 != null && bigVal2 == null) {
            answerVal = bigVal1;
        } else if (bigVal1 == null && bigVal2 != null) {
            answerVal = bigVal2;
        } else if (bigVal1 != null && bigVal2 != null) {
            answerVal = bigVal1.add(bigVal2);
        }

        if (answerVal != null) {
            anserStr = answerVal.toString();
        }

        return anserStr;
    }

    /**
     * 見込-実績取得対象月を取得
     */
    private String getDiffTargetYm(String syuekiYm) throws ParseException {
        Date syuekiDate = Utils.parseDate(syuekiYm);
        Date targetDate = DateUtils.addMonths(syuekiDate, -1);

        String targetYm = DateFormatUtils.format(targetDate, "yyyyMM");

        return targetYm;
    }

    /**
     * 見込差反映処理
     * @return
     * @throws Exception
     */
    public boolean updateExpectedReflect() throws Exception{

        boolean registFlg = false;
        String sum;
        String sumHatsuban;
        String sumMihatsuban;
        String sumSeibanSoneki;
        String sumKawaseEikyo;

        String diffNet = "";
        String mNet = "";
        String jNet = "";

        String diffKawaseEikyo = "";
        String mKawaseEikyo = "";

        try{
            // 更新対象月(現在の勘定月)
            // (進行基準案件のため、進行基準の勘定月を取得)
            //String updateSyuekiYm = this.judgeTm(this.s004Bean.getTmIndex(), 1);
            String updateSyuekiYm = kanjyoMstFacade.getNowKanjoDate(ConstantString.salesClassS);
            // 見込-実績取得対象月(現在の勘定月-1ヵ月)
            //String getSyuekiYm = this.judgeTm(this.s004Bean.getTmIndex(), 0);
            String getSyuekiYm = getDiffTargetYm(updateSyuekiYm);

            // 契約金額の更新
            if (s004Bean.getInpTargetKeiyakuCurrencyCode() != null) {
                for (int i=0; i<this.s004Bean.getInpTargetKeiyakuCurrencyCode().length; i++){
                    String currencyCode = s004Bean.getInpTargetKeiyakuCurrencyCode()[i];
                    diffNet = getTmTargetKeiyakuAmount(getSyuekiYm, currencyCode, "DIFF");
                    mNet    = getTmTargetKeiyakuAmount(updateSyuekiYm, currencyCode, "M");

                    if (StringUtil.isNotEmpty(diffNet)) {
                        sum = mikomiJissekiSum(mNet, diffNet);
                        updateKeiyakuAmount(currencyCode, updateSyuekiYm, sum);
                        registFlg = true;
                    }
                }
            }

            S004Cost diffCostEn = getTmTargetCostNet(getSyuekiYm, "DIFF");
            S004Cost mCostEn = getTmTargetCostNet(updateSyuekiYm, "M");

            // 発番NETの取得
            diffNet = null;
            if (diffCostEn != null && diffCostEn.getHatNetTm()!= null) {
                diffNet = diffCostEn.getHatNetTm().toString();
            }
            mNet = null;
            if (mCostEn != null && mCostEn.getHatNetTm() != null) {
                mNet = mCostEn.getHatNetTm().toString();
            }
            sumHatsuban = StringUtil.isEmpty(diffNet) ? "noUpdate" : mikomiJissekiSum(mNet, diffNet);

            // 未発番NETの取得
            diffNet = null;
            if (diffCostEn != null && diffCostEn.getMiNetTm() != null) {
                diffNet = diffCostEn.getMiNetTm().toString();
            }
            mNet = null;
            if (mCostEn != null && mCostEn.getSeibanSonekiNetTm() != null) {
                mNet = mCostEn.getMiNetTm().toString();
            }
            sumMihatsuban = StringUtil.isEmpty(diffNet) ? "noUpdate" : mikomiJissekiSum(mNet, diffNet);

            // 製番損益NETの取得
            diffNet = null;
            if (diffCostEn != null && diffCostEn.getSeibanSonekiNetTm() != null) {
                diffNet = diffCostEn.getSeibanSonekiNetTm().toString();
            }
            mNet = null;
            if (mCostEn != null && mCostEn.getSeibanSonekiNetTm() != null) {
                mNet = mCostEn.getSeibanSonekiNetTm().toString();
            }
            sumSeibanSoneki = StringUtil.isEmpty(diffNet) ? "noUpdate" : mikomiJissekiSum(mNet, diffNet);

            diffKawaseEikyo = null;
            if (diffCostEn != null && diffCostEn.getKawaseEikyoTm() != null) {
                diffKawaseEikyo = diffCostEn.getKawaseEikyoTm().toString();
            }
            mKawaseEikyo = null;
            if (mCostEn != null && mCostEn.getKawaseEikyoTm() != null) {
                mKawaseEikyo = mCostEn.getKawaseEikyoTm().toString();
            }
            sumKawaseEikyo = StringUtil.isEmpty(diffKawaseEikyo) ? "noUpdate" : mikomiJissekiSum(mKawaseEikyo, diffKawaseEikyo);

            // 未発番NET、製番損益NETの更新
            if (!("noUpdate".equals(sumMihatsuban) && "noUpdate".equals(sumSeibanSoneki))) {
                this.updateSougenka(updateSyuekiYm, sumHatsuban, sumMihatsuban, sumSeibanSoneki, sumKawaseEikyo);
                registFlg = true;
            }

            // 売上原価の更新
            if (s004Bean.getInpTargetNetCategoryCode() != null) {
                for (int i=0; i<this.s004Bean.getInpTargetNetCategoryCode().length; i++) {
                    diffNet = getTmTargetNet(getSyuekiYm, s004Bean.getInpTargetNetCategoryCode()[i], s004Bean.getInpTargetNetCategoryKbn1()[i], s004Bean.getInpTargetNetCategoryKbn2()[i], "DIFF");
                    mNet    = getTmTargetNet(updateSyuekiYm, s004Bean.getInpTargetNetCategoryCode()[i], s004Bean.getInpTargetNetCategoryKbn1()[i], s004Bean.getInpTargetNetCategoryKbn2()[i], "M");

                    if (StringUtil.isNotEmpty(diffNet)) {
                        sum = mikomiJissekiSum(mNet, diffNet);
                        updateNet(s004Bean.getInpTargetNetCategoryCode()[i], s004Bean.getInpTargetNetCategoryKbn1()[i], s004Bean.getInpTargetNetCategoryKbn2()[i], updateSyuekiYm, sum);
                        registFlg = true;
                    }
                }
            }

            // 回収金額の更新
            if (s004Bean.getInpTargetKaisyuAmountCurrencyCode() != null) {
                for (int i=0; i<this.s004Bean.getInpTargetKaisyuAmountCurrencyCode().length; i++){
                    Map<String, BigDecimal> diffNetInfo = getTmTargetKaisyuAmount(getSyuekiYm, i, "DIFF");
                    Map<String, BigDecimal> mNetInfo = getTmTargetKaisyuAmount(getSyuekiYm, i, "M");

                    BigDecimal bigDiffNet = diffNetInfo.get("kaisyuAmountTm");
                    BigDecimal bigMNet    = mNetInfo.get("kaisyuAmountTm");

                    BigDecimal bigEnkaDiffNet = diffNetInfo.get("kaisyuEnkaAmountTm");
                    BigDecimal bigEnkaMNet    = mNetInfo.get("kaisyuEnkaAmountTm");

                    // 外貨項目
                    BigDecimal bigSum = NumberUtils.add(bigMNet, bigDiffNet);
                    // 円貨項目
                    BigDecimal bigEnkaSum = NumberUtils.add(bigEnkaMNet, bigEnkaDiffNet);

                    if (bigSum != null || bigEnkaSum != null) {
                        //updateKaisyuAmount(s004Bean.getInpTargetKaisyuAmountCurrencyCode()[i], updateSyuekiYm, sum, null);
                        updateKaisyuAmount(i, bigSum, bigEnkaSum, updateSyuekiYm, null);
                        registFlg = true;
                    }
                }
            }

            ///////////////////////////////////////////////////////////////
            // 見込-実績取得対象となった月の見込から見込-実績差を減算する
            ///////////////////////////////////////////////////////////////
            // 契約金額の更新
            if (s004Bean.getInpTargetKeiyakuCurrencyCode() != null) {
                for (int i=0; i<s004Bean.getInpTargetKeiyakuCurrencyCode().length; i++){
                    jNet = getTmTargetKeiyakuAmount(getSyuekiYm, s004Bean.getInpTargetKeiyakuCurrencyCode()[i], "J");
                    if (StringUtil.isNotEmpty(jNet)) {
                        updateKeiyakuAmount(s004Bean.getInpTargetKeiyakuCurrencyCode()[i], getSyuekiYm, jNet);
                        registFlg = true;
                    }
                }
            }

            // 未発番NET、製番損益NETの更新
            S004Cost jCostEn = getTmTargetCostNet(getSyuekiYm, "J");

            String jHatNet = null;
            if (jCostEn != null && jCostEn.getHatNetTm() != null) {
                jHatNet = jCostEn.getHatNetTm().toString();
            }
            jHatNet = StringUtil.isEmpty(jHatNet) ? "noUpdate" : jHatNet;

            String jMiNet = null;
            if (jCostEn != null && jCostEn.getMiNetTm() != null) {
                jMiNet = jCostEn.getMiNetTm().toString();
            }
            jMiNet = StringUtil.isEmpty(jMiNet) ? "noUpdate" : jMiNet;

            String jSeibanSonekiNet = null;
            if (jCostEn != null && jCostEn.getSeibanSonekiNetTm() != null) {
                jSeibanSonekiNet = jCostEn.getSeibanSonekiNetTm().toString();
            }
            jSeibanSonekiNet = StringUtil.isEmpty(jSeibanSonekiNet) ? "noUpdate" : jSeibanSonekiNet;

            String jKawaseEikyo = null;
            if (jCostEn != null && jCostEn.getKawaseEikyoTm() != null) {
                jKawaseEikyo = jCostEn.getKawaseEikyoTm().toString();
            }
            jKawaseEikyo = StringUtil.isEmpty(jKawaseEikyo) ? "noUpdate" : jKawaseEikyo;

            if (!("noUpdate".equals(jMiNet) && "noUpdate".equals(jSeibanSonekiNet))) {
                updateSougenka(getSyuekiYm, jHatNet, jMiNet, jSeibanSonekiNet, jKawaseEikyo);
                registFlg = true;
            }

            // 売上原価の更新
            if (s004Bean.getInpTargetNetCategoryCode() != null) {
                for(int i=0; i<this.s004Bean.getInpTargetNetCategoryCode().length; i++){
                    jNet = getTmTargetNet(getSyuekiYm, s004Bean.getInpTargetNetCategoryCode()[i], s004Bean.getInpTargetNetCategoryKbn1()[i], s004Bean.getInpTargetNetCategoryKbn2()[i], "J");
                    if (StringUtil.isNotEmpty(jNet)) {
                        updateNet(s004Bean.getInpTargetNetCategoryCode()[i], s004Bean.getInpTargetNetCategoryKbn1()[i], s004Bean.getInpTargetNetCategoryKbn2()[i], getSyuekiYm, jNet);
                        registFlg = true;
                    }
                }
            }

            // 回収金額の更新
            if (s004Bean.getInpTargetKaisyuAmountCurrencyCode() != null) {
                for (int i=0; i<this.s004Bean.getInpTargetKaisyuAmountCurrencyCode().length; i++) {
                    Map<String, BigDecimal> jNetInfo = getTmTargetKaisyuAmount(getSyuekiYm, i, "J");

                    BigDecimal bigJNet = jNetInfo.get("kaisyuAmountTm");
                    BigDecimal bigEnkaJNet = jNetInfo.get("kaisyuEnkaAmountTm");
                    //jNet = getTmTargetKaisyuAmount(getSyuekiYm, s004Bean.getInpTargetKaisyuAmountCurrencyCode()[i], "J");

                    //if (StringUtil.isNotEmpty(jNet)) {
                    if (bigJNet != null || bigEnkaJNet != null) {
                        //updateKaisyuAmount(s004Bean.getInpTargetKaisyuAmountCurrencyCode()[i], getSyuekiYm, jNet, null);
                        updateKaisyuAmount(i, bigJNet, bigEnkaJNet, getSyuekiYm, null);
                        registFlg = true;
                    }
                }
            }

            // 登録できたら操作ログ出力
            if(registFlg){
                // 再計算FLGを設定
                geBukenInfoTblFacade.setSaikeisanFlg(s004Bean.getAnkenId(), (new Integer(s004Bean.getRirekiId())), "1");

                // 再計算処理を実行
                calc(0);

                this.updateSyuekiBukken();
                this.registOperationLog(COPY_SABUN);
            }

        } catch(Exception e) {
            throw e;
        }

        return true;
    }

    /**
     * 当月の契約金額を取得
     * @param tmIndex
     * @param index
     * @param syukeiYm
     * @param currencyCode
     * @return
     */
    private String getTmTargetKeiyakuAmount(String syukeiYm, String currencyCode, String reflecFlg){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("currencyCode", currencyCode);
        paramMap.put("syukeiYm", syukeiYm);
        //paramMap.put("reflecFlg", "1");
        paramMap.put("reflecFlg", reflecFlg);
        ContractAmount contract = this.contractAmountListFacade.findReflectTarget(paramMap);
        if (contract != null && contract.getKeiyakuAmountTm() != null) {
            return contract.getKeiyakuAmountTm().toString();
        } else {
            return null;
        }
    }

    /**
     * 当月の未発番NETを取得
     * @param tmIndex
     * @param index
     * @param syukeiYm
     * @param currencyCode
     * @return
     */
    private S004Cost getTmTargetCostNet(String syukeiYm, String reflecFlg){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("syukeiYm", syukeiYm);
        //paramMap.put("reflecFlg", "1");
        paramMap.put("reflecFlg", reflecFlg);
        S004Cost cost = this.costListFacade.findEstimateCostDetailReflectTarget(paramMap);

        return cost;
    }

    /**
     * 当月の売上原価を取得
     * @param tmIndex
     * @param index
     * @param syukeiYm
     * @param currencyCode
     * @return
     */
    private String getTmTargetNet(String syukeiYm, String categoryCode, String categoryKbn1, String categoryKbn2, String reflecFlg){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("categoryCode", categoryCode);
        paramMap.put("categoryKbn1", categoryKbn1);
        paramMap.put("categoryKbn2", categoryKbn2);
        paramMap.put("syukeiYm", syukeiYm);
        //paramMap.put("reflecFlg", "1");
        paramMap.put("reflecFlg", reflecFlg);
        Cost cost = this.costListFacade.findSalesCostDetailReflectTarget(paramMap);
        if (cost != null && cost.getNetTm() != null) {
            return cost.getNetTm().toString();
        } else {
            return null;
        }
    }

    /**
     * 当月の回収金額を取得
     * @param tmIndex
     * @param index
     * @param syukeiYm
     * @param currencyCode
     * @return
     */
    //private String getTmTargetKaisyuAmount(String syukeiYm, String currencyCode, String reflecFlg){
    private Map<String, BigDecimal> getTmTargetKaisyuAmount(String syukeiYm, int kaisyuRowIndex, String reflecFlg){
        String currencyCode = s004Bean.getInpTargetKaisyuAmountCurrencyCode()[kaisyuRowIndex];
        String zeiKbn = s004Bean.getInpTargetKaisyuAmountZeiKbn()[kaisyuRowIndex];
        String kinsyuKbn = s004Bean.getInpTargetKaisyuAmountKinsyuKbn()[kaisyuRowIndex];
        String kaisyuKbn = s004Bean.getInpTargetKaisyuAmountKaisyuKbn()[kaisyuRowIndex];

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        paramMap.put("currencyCode", currencyCode);
        paramMap.put("zeiKbn", zeiKbn);
        paramMap.put("kinsyuKbn", kinsyuKbn);
        paramMap.put("kaisyuKbn", kaisyuKbn);
        paramMap.put("syukeiYm", syukeiYm);
        paramMap.put("reflecFlg", reflecFlg);
        RecoveryAmount recoveryAmount = this.recoveryAmountListFacade.findTargetReflect(paramMap);

        Map<String, BigDecimal> resultMap = new HashMap<>();
        if (recoveryAmount != null) {
            resultMap.put("kaisyuAmountTm", recoveryAmount.getKaisyuAmountTm());
            resultMap.put("kaisyuEnkaAmountTm", recoveryAmount.getKaisyuEnkaAmountTm());
        }
        return resultMap;
    }

    /**
     * ロスコン関連情報を取得して設定
     */
    private void setLossData(final Map<String, Object> _condition) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Map<String, Map<String, String>> lossData = new HashMap<>();
        
        // ロスコン情報値を取得する際のテーブル検索条件を作成
        Map<String, Object> condition = new HashMap<>(_condition);
        condition.put("zenki", SyuuekiUtils.calcKikan((String)condition.get("kiFrom"), -1));   // 指定期間の前期表現を取得(ロスコン引当累計の前期累計は、表示期の前期のロスコン引当累計を表示するため)

        // 各ロスコン情報取得項目を定義
        String[] lossColAry = {"LOSS_HOSEI", "LOSS_AMOUNT", "LOSS_GENKA", "LOSS_HIKIATE", "LOSS_RUIKEI_HIKIATE"};
        // 各列(各月や各Q,K,G項目の後ろ項目名)を定義([前期までの累計] 用の項目名は粗利/M率計算でKEYの相違があるためあえて除外している)
        String[] colFooterAry = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "K1", "K2", "G", "1Q", "2Q", "3Q", "4Q", "K1Diff", "K2Diff", "GDiff", "1QDiff", "2QDiff", "3QDiff", "4QDiff", "Tm"};

        KsLossData lossGenka = null;
        KsLossData lossAmount = null;
        // 画面に表示する全ての年月、Q、K、Gのロスコン補正額、ロスコン補正額を含む売上原価(合計)、を取得
        for (String lossColKey: lossColAry) {
            // テーブルからロスコン情報値を取得
            condition.put("lossCol", lossColKey);
            KsLossData lossDBInfo = syuKiLossTblFacade.findKsLossInfoData(condition);
            
            // テーブルから取得した情報がロスコン補正込みの売上原価(今回)である場合、それを変数に格納しておく(後続のロスコン補正の粗利/M率を計算に利用するため)
            if ("LOSS_GENKA".equals(lossColKey)) {
                lossGenka = lossDBInfo;
            }
            // テーブルから取得した情報がロスコン補正込みの売上高である場合、それを変数に格納しておく(後続のロスコン補正の粗利/M率を計算に利用するため)
            if ("LOSS_AMOUNT".equals(lossColKey)) {
                lossAmount = lossDBInfo;
            }
            
            // 取得したロスコン情報の各月や各Q,K,G項目を画面表示用に整形(円貨単位、カンマ編集)
            Map<String, String> lossColData = new HashMap<>();
            for (String colFooter: colFooterAry) {
                String lossKey = "loss" + colFooter;
                Object lossNet = PropertyUtils.getProperty(lossDBInfo, lossKey);
                String formatLoss = syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit((lossNet != null ? (BigDecimal)lossNet : null), s004Bean.getJpyUnit()));
                lossColData.put(lossKey, formatLoss);
            }
            // 前期までの累計
            String formatLoss = syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(lossDBInfo.getLossBRuikei(), s004Bean.getJpyUnit()));
            lossColData.put("lossBRuikei", formatLoss);
                
            lossData.put(lossColKey, lossColData);
        }

        // ロスコン補正の粗利/M率を計算
        Map<String, String> lossArari = new HashMap<>();
        Map<String, String> lossMrate = new HashMap<>();
        if (lossAmount != null && lossGenka != null){
            for (String colFooter: colFooterAry) {
                // ロスコン補正込みの売上高を取得
                Object sp = PropertyUtils.getProperty(lossAmount, "loss" + colFooter);
                BigDecimal bigSp = (sp != null ? (BigDecimal)sp : null);
                // ロスコン補正込みの売上原価(今回) を取得
                Object net = PropertyUtils.getProperty(lossGenka, "loss" + colFooter);
                BigDecimal bigNet = (net != null ? (BigDecimal)net : null);
                
                String arari = null;
                String mrate = null;
                if (bigNet != null) {  // ロスコン補正込の売上原価が登録されている場合のみロスコン粗利/M率を計算する
                    // 粗利を計算
                    arari = syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(bigSp, bigNet), s004Bean.getJpyUnit()));
                    // M率を計算
                    mrate = syuuekiUtils.mrate(bigSp, bigNet);
                }
                
                lossArari.put("loss" + colFooter, arari);
                lossMrate.put("loss" + colFooter, mrate);
            }
            
            String arari = null;
            String mrate = null;
            if (lossGenka.getLossBRuikei() != null) {
                // 前期までの累計 粗利
                arari = syuuekiUtils.exeFormatUnit(syuuekiUtils.switchingCurrencyUnit(syuuekiUtils.arari(lossAmount.getLossBRuikei(), lossGenka.getLossBRuikei()), s004Bean.getJpyUnit()));
                // 前期までの累計 M率
                mrate = syuuekiUtils.mrate(lossAmount.getLossBRuikei(), lossGenka.getLossBRuikei());
            }
            lossArari.put("lossBRuikei", arari);
            lossMrate.put("lossBRuikei", mrate);
        }
        lossData.put("LOSS_ARARI", lossArari);
        lossData.put("LOSS_MRATE", lossMrate);
        
        
        s004Bean.setLossData(lossData);
    }

    /**
     * 収益物件の見込差反映済を更新する
     */
    private void updateSyuekiBukken(){
        this.findAnkenInfo();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ankenId", s004Bean.getAnkenId());
        paramMap.put("ankenRev", dateilHeader.getAnkenEntity().getAnkenRev());
        paramMap.put("rirekiId", (new Integer(s004Bean.getRirekiId())));
        this.s004UpdateFacade.updateSyuekiBukken(paramMap);
    }

    /**
     * データ編集可否FLGを取得
     * @return 1:編集可能 0:編集不可
     */
    private void setEditAuthFlg() {
        String saisyuUpdeteBtnFlg = "0";

        SyuGeBukkenInfoTbl geEntity = dateilHeader.getAnkenEntity();

        // 事業部:原子力か？
        boolean isNuclearDivision = divisionComponentPage.isNuclearDivision(geEntity.getDivisionCode());
        
        if (isNuclearDivision && !"1".equals(geEntity.getAnkenFlg())) {
            saisyuUpdeteBtnFlg = "1";
        }

        s004Bean.setSaisyuUpdeteBtnFlg(saisyuUpdeteBtnFlg);
    }
    
    /**
     * 再計算処理をcallする。
     */
//    private void callAnkenRecal() throws Exception {
//        AnkenRecalDto dto = new AnkenRecalDto();
//        dto.setAnkenId(s004Bean.getAnkenId());
//        dto.setRirekiId(Integer.parseInt(s004Bean.getRirekiId()));
//        //dto.setKbn("0");
//
//        storedProceduresService.callAnkenRecal(dto);
//
//        if (!"0".equals(dto.getStatus())) {
//            throw new Exception("再計算処理[SYU_ANKEN_RECAL_MAIN]でエラーが発生しました。物件Key=" + s004Bean.getAnkenId());
//        }
//    }

    /**
     * @return the previousPeriodTotalContractAmount
     */
    public ContractAmount getPreviousPeriodTotalContractAmount() {
        return previousPeriodTotalContractAmount;
    }

    /**
     * @param previousPeriodTotalContractAmount the previousPeriodTotalContractAmount to set
     */
    public void setPreviousPeriodTotalContractAmount(ContractAmount previousPeriodTotalContractAmount) {
        this.previousPeriodTotalContractAmount = previousPeriodTotalContractAmount;
    }

    /**
     * @return the previousPeriodcontractAmount
     */
    public List<ContractAmount> getPreviousPeriodContractAmount() {
        return previousPeriodContractAmount;
    }

    /**
     * @param previousPeriodcontractAmount the previousPeriodcontractAmount to set
     */
    public void setPreviousPeriodContractAmount(List<ContractAmount> previousPeriodContractAmount) {
        this.previousPeriodContractAmount = previousPeriodContractAmount;
    }

    /**
     * @return the previousPeriodCost2
     */
    public S004Cost getPreviousPeriodCost2() {
        return previousPeriodCost2;
    }

    /**
     * @param previousPeriodCost2 the previousPeriodCost2 to set
     */
    public void setPreviousPeriodCost2(S004Cost previousPeriodCost2) {
        this.previousPeriodCost2 = previousPeriodCost2;
    }

    /**
     * @return the previousPeriodTotalSales
     */
    public SalesAmount getPreviousPeriodTotalSales() {
        return previousPeriodTotalSales;
    }

    /**
     * @param previousPeriodTotalSales the previousPeriodTotalSales to set
     */
    public void setPreviousPeriodTotalSales(SalesAmount previousPeriodTotalSales) {
        this.previousPeriodTotalSales = previousPeriodTotalSales;
    }

    /**
     * @return the previousPeriodsalesList
     */
    public List<SalesAmount> getPreviousPeriodSalesList() {
        return previousPeriodSalesList;
    }

    /**
     * @param previousPeriodSalesList the previousPeriodSalesList to set
     */
    public void setPreviousPeriodSalesList(List<SalesAmount> previousPeriodSalesList) {
        this.previousPeriodSalesList = previousPeriodSalesList;
    }

    /**
     * @return the previousPeriodTotalRuikeiSales
     */
    public SalesAmount getPreviousPeriodTotalRuikeiSales() {
        return previousPeriodTotalRuikeiSales;
    }

    /**
     * @param previousPeriodTotalRuikeiSales the previousPeriodTotalRuikeiSales to set
     */
    public void setPreviousPeriodTotalRuikeiSales(SalesAmount previousPeriodTotalRuikeiSales) {
        this.previousPeriodTotalRuikeiSales = previousPeriodTotalRuikeiSales;
    }

    /**
     * @return the previousPeriodRuikeiSalesList
     */
    public List<SalesAmount> getPreviousPeriodRuikeiSalesList() {
        return previousPeriodRuikeiSalesList;
    }

    /**
     * @param previousPeriodRuikeiSalesList the previousPeriodRuikeiSalesList to set
     */
    public void setPreviousPeriodRuikeiSalesList(List<SalesAmount> previousPeriodRuikeiSalesList) {
        this.previousPeriodRuikeiSalesList = previousPeriodRuikeiSalesList;
    }

    /**
     * @return the previousPeriodTotalSalesCost
     */
    public SalesAmount getPreviousPeriodTotalSalesCost() {
        return previousPeriodTotalSalesCost;
    }

    /**
     * @param previousPeriodTotalSalesCost the previousPeriodTotalSalesCost to set
     */
    public void setPreviousPeriodTotalSalesCost(SalesAmount previousPeriodTotalSalesCost) {
        this.previousPeriodTotalSalesCost = previousPeriodTotalSalesCost;
    }

    /**
     * @return the previousPeriodSalesCostList
     */
    public List<Cost> getPreviousPeriodSalesCostList() {
        return previousPeriodSalesCostList;
    }

    /**
     * @param previousPeriodSalesCostList the previousPeriodSalesCostList to set
     */
    public void setPreviousPeriodSalesCostList(List<Cost> previousPeriodSalesCostList) {
        this.previousPeriodSalesCostList = previousPeriodSalesCostList;
    }

    /**
     * @return the previousPeriodTotalSalesCostRuikei
     */
    public SalesAmount getPreviousPeriodTotalSalesCostRuikei() {
        return previousPeriodTotalSalesCostRuikei;
    }

    /**
     * @param previousPeriodTotalSalesCostRuikei the previousPeriodTotalSalesCostRuikei to set
     */
    public void setPreviousPeriodTotalSalesCostRuikei(SalesAmount previousPeriodTotalSalesCostRuikei) {
        this.previousPeriodTotalSalesCostRuikei = previousPeriodTotalSalesCostRuikei;
    }

    /**
     * @return the previousPeriodRecoveryAmountList
     */
//    public List<RecoveryAmount> getPreviousPeriodRecoveryAmountList() {
//        return previousPeriodRecoveryAmountList;
//    }

    /**
     * @param previousPeriodRecoveryAmountList the previousPeriodRecoveryAmountList to set
     */
//    public void setPreviousPeriodRecoveryAmountList(List<RecoveryAmount> previousPeriodRecoveryAmountList) {
//        this.previousPeriodRecoveryAmountList = previousPeriodRecoveryAmountList;
//    }

}
