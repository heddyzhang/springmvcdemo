package jp.co.toshiba.hby.pspromis.syuueki.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.co.toshiba.hby.pspromis.syuueki.entity.KsLossData;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
//import jp.co.toshiba.hby.pspromis.syuueki.entity.S004DownloadEntity;
import jp.co.toshiba.hby.pspromis.syuueki.entity.S004DownloadKaisyuEntity;
import jp.co.toshiba.hby.pspromis.syuueki.enums.Label;

/**
 *
 * @author sano
 */
@Named(value = "s004Bean")
@RequestScoped
public class S004Bean extends AbstractBean {

    private final String ki         = Label.getValue(Label.ki);
    private final String total      = Label.getValue(Label.total);
    private final String lastMikomi = Label.getValue(Label.lastMikomi);

    /**
     * 一括ダウンロード出力FLG
     * (案件一覧の一括ダウンロード処理から指定された場合はtrueとなる)
     * (この場合は特定処理(この内部での操作ログ書き込み等)を行わない)
     */
    private boolean isIkkatsuFlg = false;

    /**
     * 円貨表示単位
     */
    private Integer jpyUnit = 1;
    
    /**
     * 受注管理・受注SP・内訳ツリー展開openフラグ
     */
    private String juchuSpListOpenFlg = "1";
    
    /**
     * 受注管理・受注NET・内訳ツリー展開openフラグ
     */
    private String juchuNetListOpenFlg = "1";
    
    /**
     * 契約金額・内訳ツリー展開openフラグ
     */
    private String spListOpenFlg = "1";

    /**
     * 見積総原価・内訳ツリー展開openフラグ
     */
    private String netListOpenFlg = "1";

    /**
     * 売上高(その1)・内訳ツリー展開openフラグ
     */
    private String uriage1ListOpenFlg = "1";

    /**
     * 売上高(その2)・内訳ツリー展開openフラグ
     */
    private String uriage2ListOpenFlg = "1";

    /**
     * 売上原価(その1)・内訳ツリー展開openフラグ
     */
    private String uriGen1ListOpenFlg = "1";

    /**
     * 期間変更FLG("B":一期前に移動 "A":一期先に移動)
     */
    private String kikanChangeFlg;

    /**
     * 纏め・一般　見込入力の案件単位 が等しいかどうかのFLG<br>
     * (等しくない場合は編集ボタン、機能-調整口追加ボタンを表示しない)
     */
    private int ankenInputFlg = 0;

    /**
     * 見込-実績を表示するindex
     */
    private int tmIndex = -1;

    /**
     * 一覧画面のclass名格納配列
     */
    private String[] classNameAry;

    /**
     * 見出し(月)
     */
    private ArrayList<String> monthTitle;

    /**
     * 見出し(見込、実績)＋cssスタイル名
     */
    //private ArrayList<String> title;
    private ArrayList<Map<String, String>> title;

    /**
     * 受注の見出し(月)
     */
    private ArrayList<String> monthTitleJyuchu;
    
    /**
     * 受注の見出し(見込、実績)＋cssスタイル名
     */
    private ArrayList<Map<String, String>> titleJyuchu;
    
    /**
     * 見出し(見込、実績) のcssスタイル
     */
    //private ArrayList<String> titleCssClass;

    /**
     * 受注SP合計
     */
    private HashMap<String, String> totalJyuchuSp;
    
    /**
     * 受注レート内訳
     */
    private List<HashMap<String, String>> jyuchuRate;
    
    /**
     * 受注金額内訳
     */
    private List<HashMap<String, String>> jyuchuKingaku;
    
    /**
     * 受注NET合計
     */
    private HashMap<String, String> totalJyuchuNet;
    
    /**
     * 一括見込NET
     */
    private HashMap<String, String> jyuchuMikomuNet;
    
    /**
     * 粗利
     */
    private HashMap<String, String> jyuchuArari;
    
    /**
     * M率
     */
    private HashMap<String, String> jyuchuMrate;
    
    /**
     * 契約金額合計
     */
    private HashMap<String, String> totalContractAmount;

    /**
     * 契約金額内訳
     */
    private List<HashMap<String, String>> contractAmount;

    /**
     * 見積総原価合計
     */
    private HashMap<String, String> totalCost;

    /**
     * 見積総原価内訳
     */
    private HashMap<String, String> costList;

    /**
     * 今回売上高
     */
    private HashMap<String, String> totalSales;

    /**
     * 今回売上高内訳
     */
    private List<HashMap<String, String>> salesList;

    /**
     * 売上高累計
     */
    private HashMap<String, String> totalRuikeiSales;

    /**
     * 売上高累計内訳
     */
    private List<HashMap<String, String>> ruikeiSalesList;

    /**
     * 今回売上原価合計
     */
    private HashMap<String, String> totalSalesCost;

    /**
     * 今回売上原価内訳
     */
    private List<HashMap<String, String>> salesCostList;

    /**
     * 売上原価累計合計
     */
    private HashMap<String, String> totalSalesCostRuikei;

    /**
     * 回収金額
     */
    // 2017/11/20 #072 ADD 回収Total行追加
    private HashMap<String, String> totalRecoveryAmountData;

    /**
     * 回収金額
     */
    private List<HashMap<String, String>> recoveryAmountList;

    /**
     * 粗利
     */
    private String arari1;

    /**
     * 粗利
     */
    private String arari2;

    /**
     * 粗利
     */
    private String arari3;

    /**
     * 粗利
     */
    private String arari4;

    /**
     * 粗利
     */
    private String arari5;

    /**
     * 粗利
     */
    private String arari6;

    /**
     * 粗利
     */
    private String arari7;

    /**
     * 粗利
     */
    private String arari8;

    /**
     * 粗利
     */
    private String arari9;

    /**
     * 粗利
     */
    private String arari10;

    /**
     * 粗利
     */
    private String arari11;

    /**
     * 粗利
     */
    private String arari12;

    /**
     * 粗利(期)
     */
    private String arariK1;

    /**
     * 粗利(期)
     */
    private String arariK2;

    /**
     * 粗利(合計)
     */
    private String arariG;

    /**
     * 粗利(最終見込)
     */
    private String arariF;

    /**
     * 粗利(期前回差)
     */
    private String arariK1Diff;

    /**
     * 粗利(期前回差)
     */
    private String arariK2Diff;

    /**
     * 粗利(合計前回差)
     */
    private String arariGDiff;

    /**
     * 粗利(最終見込差分)
     */
    private String arariDiff;

    /**
     * 粗利(見込-実績)
     */
    private String arariTm;

    /**
     * 粗利(1Q目)
     */
    private String arari1Q;

    /**
     * 粗利(1Q目前回差)
     */
    private String arari1QDiff;

    /**
     * 粗利(2Q目)
     */
    private String arari2Q;

    /**
     * 粗利(2Q目前回差)
     */
    private String arari2QDiff;

    /**
     * 粗利(3Q目)
     */
    private String arari3Q;

    /**
     * 粗利(3Q目前回差)
     */
    private String arari3QDiff;

    /**
     * 粗利(4Q目)
     */
    private String arari4Q;

    /**
     * 粗利(4Q目前回差)
     */
    private String arari4QDiff;


    /**
     * 粗利累計
     */
    private String arariRuikei1;

    /**
     * 粗利累計
     */
    private String arariRuikei2;

    /**
     * 粗利累計
     */
    private String arariRuikei3;

    /**
     * 粗利累計
     */
    private String arariRuikei4;

    /**
     * 粗利累計
     */
    private String arariRuikei5;

    /**
     * 粗利累計
     */
    private String arariRuikei6;

    /**
     * 粗利累計
     */
    private String arariRuikei7;

    /**
     * 粗利累計
     */
    private String arariRuikei8;

    /**
     * 粗利累計
     */
    private String arariRuikei9;

    /**
     * 粗利累計
     */
    private String arariRuikei10;

    /**
     * 粗利累計
     */
    private String arariRuikei11;

    /**
     * 粗利累計
     */
    private String arariRuikei12;

    /**
     * 粗利累計(期)
     */
    private String arariRuikeiK1;

    /**
     * 粗利累計(期)
     */
    private String arariRuikeiK2;

    /**
     * 粗利累計(合計)
     */
    private String arariRuikeiG;

    /**
     * 粗利累計(最終見込)
     */
    private String arariRuikeiF;

    /**
     * 粗利累計(期前回差)
     */
    private String arariRuikeiK1Diff;

    /**
     * 粗利累計(期前回差)
     */
    private String arariRuikeiK2Diff;

    /**
     * 粗利累計(合計前回差)
     */
    private String arariRuikeiGDiff;

    /**
     * 粗利累計(最終見込差分)
     */
    private String arariRuikeiDiff;

    /**
     * 粗利累計(見込-実績)
     */
    private String arariRuikeiTm;


    /**
     * 粗利累計(1Q目)
     */
    private String arariRuikei1Q;

    /**
     * 粗利累計(1Q目前回差)
     */
    private String arariRuikei1QDiff;

    /**
     * 粗利累計(2Q目)
     */
    private String arariRuikei2Q;

    /**
     * 粗利累計(2Q目前回差)
     */
    private String arariRuikei2QDiff;

    /**
     * 粗利累計(3Q目)
     */
    private String arariRuikei3Q;

    /**
     * 粗利累計(3Q目前回差)
     */
    private String arariRuikei3QDiff;

    /**
     * 粗利累計(4Q目)
     */
    private String arariRuikei4Q;

    /**
     * 粗利累計(4Q目前回差)
     */
    private String arariRuikei4QDiff;

    /**
     * 粗利今回(前期までの累計)
     */
    private String arariMaeAllTotal;

    /**
     * 粗利累計(前期までの累計)
     */
    private String arariRuikeiMaeAllTotal;

    /**
     * M率
     */
    private String mrate1;

    /**
     * M率
     */
    private String mrate2;

    /**
     * M率
     */
    private String mrate3;

    /**
     * M率
     */
    private String mrate4;

    /**
     * M率
     */
    private String mrate5;

    /**
     * M率
     */
    private String mrate6;

    /**
     * M率
     */
    private String mrate7;

    /**
     * M率
     */
    private String mrate8;

    /**
     * M率
     */
    private String mrate9;

    /**
     * M率
     */
    private String mrate10;

    /**
     * M率
     */
    private String mrate11;

    /**
     * M率
     */
    private String mrate12;

    /**
     * M率(期)
     */
    private String mrateK1;

    /**
     * M率(期)
     */
    private String mrateK2;

    /**
     * M率(合計)
     */
    private String mrateG;

    /**
     * M率(最終見込)
     */
    private String mrateF;

    /**
     * M率(期前回差)
     */
    private String mrateK1Diff;

    /**
     * M率(期前回差)
     */
    private String mrateK2Diff;

    /**
     * M率(合計前回差)
     */
    private String mrateGDiff;

    /**
     * M率(最終見込差分)
     */
    private String mrateDiff;

    /**
     * M率(見込-実績)
     */
    private String mrateTm;


    /**
     * M率(1Q目)
     */
    private String mrate1Q;

    /**
     * M率(2Q目)
     */
    private String mrate2Q;

    /**
     * M率(3Q目)
     */
    private String mrate3Q;

    /**
     * M率(4Q目)
     */
    private String mrate4Q;

    /**
     * M率(1Q目) 前回差分
     */
    private String mrate1QDiff;

    /**
     * M率(2Q目) 前回差分
     */
    private String mrate2QDiff;

    /**
     * M率(3Q目) 前回差分
     */
    private String mrate3QDiff;

    /**
     * M率(4Q目) 前回差分
     */
    private String mrate4QDiff;


    /**
     * M率累計
     */
    private String mrateRuikei1;

    /**
     * M率累計
     */
    private String mrateRuikei2;

    /**
     * M率累計
     */
    private String mrateRuikei3;

    /**
     * M率累計
     */
    private String mrateRuikei4;

    /**
     * M率累計
     */
    private String mrateRuikei5;

    /**
     * M率累計
     */
    private String mrateRuikei6;

    /**
     * M率累計
     */
    private String mrateRuikei7;

    /**
     * M率累計
     */
    private String mrateRuikei8;

    /**
     * M率累計
     */
    private String mrateRuikei9;

    /**
     * M率累計
     */
    private String mrateRuikei10;

    /**
     * M率累計
     */
    private String mrateRuikei11;

    /**
     * M率累計
     */
    private String mrateRuikei12;

    /**
     * M率累計(期)
     */
    private String mrateRuikeiK1;

    /**
     * M率累計(期)
     */
    private String mrateRuikeiK2;

    /**
     * M率累計(合計)
     */
    private String mrateRuikeiG;

    /**
     * M率累計(最終見込)
     */
    private String mrateRuikeiF;

    /**
     * M率累計(期前回差)
     */
    private String mrateRuikeiK1Diff;

    /**
     * M率累計(期前回差)
     */
    private String mrateRuikeiK2Diff;

    /**
     * M率累計(合計前回差)
     */
    private String mrateRuikeiGDiff;

    /**
     * M率累計(最終見込差分)
     */
    private String mrateRuikeiDiff;

    /**
     * M率累計(見込-実績)
     */
    private String mrateRuikeiTm;


    /**
     * M率(1Q目)
     */
    private String mrateRuikei1Q;

    /**
     * M率(2Q目)
     */
    private String mrateRuikei2Q;

    /**
     * M率(3Q目)
     */
    private String mrateRuikei3Q;

    /**
     * M率(4Q目)
     */
    private String mrateRuikei4Q;

    /**
     * M率(1Q目) 前回差分
     */
    private String mrateRuikei1QDiff;

    /**
     * M率(2Q目) 前回差分
     */
    private String mrateRuikei2QDiff;

    /**
     * M率(3Q目) 前回差分
     */
    private String mrateRuikei3QDiff;

    /**
     * M率(4Q目) 前回差分
     */
    private String mrateRuikei4QDiff;

    /**
     * M率(前期までの累計(M率今回))
     */
    private String mrateMaeAllTotal;

    /**
     * M率(前期までの累計)
     */
    private String mrateRuikeiMaeAllTotal;

    /**
     * 期間Fromの選択候補(2014下add)
     */
    private List<String> kikanFromList;

    /**
     * 完売月
     */
    private String kanbaiYm;
    
    /**
     * 【編集（非表示）】データ種別
     */
    private String[] jyuchuDataKbn;
    
    /**
     * 【編集（非表示）】通貨コード
     */
    private String[] jyuchuCurrencyCode;
    
    /**
     * 【編集（非表示）】年月
     */
    private String[] jyuchuSyuekiYm;
    
    /**
     * 【編集】受注金額
     */
    private String[] jyuchuSpKingaku;
    /**
     * 【編集】受注レート
     */
    private String[] jyuchuSpRate;
    
    /**
     * 【編集（非表示）】データ種別
     */
    private String[] jyuchuNetDataKbn;
    /**
     * 【編集（非表示）】年月
     */
    private String[] jyuchuNetSyuekiYm;
    /**
     * 【編集】NET金額
     */
    private String[] jyuchuNetKingaku;

    
    /**
     * 【編集】契約金額
     */
    private String[] keiyakuAmount1;
    /**
     * 【編集】契約金額
     */
    private String[] keiyakuAmount2;
    /**
     * 【編集】契約金額
     */
    private String[] keiyakuAmount3;
    /**
     * 【編集】契約金額
     */
    private String[] keiyakuAmount4;
    /**
     * 【編集】契約金額
     */
    private String[] keiyakuAmount5;
    /**
     * 【編集】契約金額
     */
    private String[] keiyakuAmount6;
    /**
     * 【編集】契約金額
     */
    private String[] keiyakuAmount7;
    /**
     * 【編集】契約金額
     */
    private String[] keiyakuAmount8;
    /**
     * 【編集】契約金額
     */
    private String[] keiyakuAmount9;
    /**
     * 【編集】契約金額
     */
    private String[] keiyakuAmount10;
    /**
     * 【編集】契約金額
     */
    private String[] keiyakuAmount11;
    /**
     * 【編集】契約金額
     */
    private String[] keiyakuAmount12;
    /**
     * 【編集】契約金額
     */
    private String[] keiyakuAmountTm;
    
    /**
     * 【編集】発番NET
     */
    private String hatNet1;
    /**
     * 【編集】発番NET
     */
    private String hatNet2;
    /**
     * 【編集】発番NET
     */
    private String hatNet3;
    /**
     * 【編集】発番NET
     */
    private String hatNet4;
    /**
     * 【編集】発番NET
     */
    private String hatNet5;
    /**
     * 【編集】発番NET
     */
    private String hatNet6;
    /**
     * 【編集】発番NET
     */
    private String hatNet7;
    /**
     * 【編集】発番NET
     */
    private String hatNet8;
    /**
     * 【編集】発番NET
     */
    private String hatNet9;
    /**
     * 【編集】発番NET
     */
    private String hatNet10;
    /**
     * 【編集】発番NET
     */
    private String hatNet11;
    /**
     * 【編集】発番NET
     */
    private String hatNet12;
    /**
     * 【編集】発番NET
     */
    private String hatNetTm;

    /**
     * 【編集】未発番NET
     */
    private String miNet1;
    /**
     * 【編集】未発番NET
     */
    private String miNet2;
    /**
     * 【編集】未発番NET
     */
    private String miNet3;
    /**
     * 【編集】未発番NET
     */
    private String miNet4;
    /**
     * 【編集】未発番NET
     */
    private String miNet5;
    /**
     * 【編集】未発番NET
     */
    private String miNet6;
    /**
     * 【編集】未発番NET
     */
    private String miNet7;
    /**
     * 【編集】未発番NET
     */
    private String miNet8;
    /**
     * 【編集】未発番NET
     */
    private String miNet9;
    /**
     * 【編集】未発番NET
     */
    private String miNet10;
    /**
     * 【編集】未発番NET
     */
    private String miNet11;
    /**
     * 【編集】未発番NET
     */
    private String miNet12;
    /**
     * 【編集】未発番NET
     */
    private String miNetTm;

    /**
     * 【編集】製番損益NET
     */
    private String seibanSonekiNet1;
    /**
     * 【編集】製番損益NET
     */
    private String seibanSonekiNet2;
    /**
     * 【編集】製番損益NET
     */
    private String seibanSonekiNet3;
    /**
     * 【編集】製番損益NET
     */
    private String seibanSonekiNet4;
    /**
     * 【編集】製番損益NET
     */
    private String seibanSonekiNet5;
    /**
     * 【編集】製番損益NET
     */
    private String seibanSonekiNet6;
    /**
     * 【編集】製番損益NET
     */
    private String seibanSonekiNet7;
    /**
     * 【編集】製番損益NET
     */
    private String seibanSonekiNet8;
    /**
     * 【編集】製番損益NET
     */
    private String seibanSonekiNet9;
    /**
     * 【編集】製番損益NET
     */
    private String seibanSonekiNet10;
    /**
     * 【編集】製番損益NET
     */
    private String seibanSonekiNet11;
    /**
     * 【編集】製番損益NET
     */
    private String seibanSonekiNet12;
    /**
     * 【編集】製番損益NET
     */
    private String seibanSonekiNetTm;
    /**
     * 【編集】為替洗替影響
     */
    private String kawaseEikyo1;
    /**
     * 【編集】為替洗替影響
     */
    private String kawaseEikyo2;
    /**
     * 【編集】為替洗替影響
     */
    private String kawaseEikyo3;
    /**
     * 【編集】為替洗替影響
     */
    private String kawaseEikyo4;
    /**
     * 【編集】為替洗替影響
     */
    private String kawaseEikyo5;
    /**
     * 【編集】為替洗替影響
     */
    private String kawaseEikyo6;
    /**
     * 【編集】為替洗替影響
     */
    private String kawaseEikyo7;
    /**
     * 【編集】為替洗替影響
     */
    private String kawaseEikyo8;
    /**
     * 【編集】為替洗替影響
     */
    private String kawaseEikyo9;
    /**
     * 【編集】為替洗替影響
     */
    private String kawaseEikyo10;
    /**
     * 【編集】為替洗替影響
     */
    private String kawaseEikyo11;
    /**
     * 【編集】為替洗替影響
     */
    private String kawaseEikyo12;
    /**
     * 【編集】為替洗替影響
     */
    private String kawaseEikyoTm;
    /**
     * 【編集】売上原価
     */
    private String[] net1;
    /**
     * 【編集】売上原価
     */
    private String[] net2;
    /**
     * 【編集】売上原価
     */
    private String[] net3;
    /**
     * 【編集】売上原価
     */
    private String[] net4;
    /**
     * 【編集】売上原価
     */
    private String[] net5;
    /**
     * 【編集】売上原価
     */
    private String[] net6;
    /**
     * 【編集】売上原価
     */
    private String[] net7;
    /**
     * 【編集】売上原価
     */
    private String[] net8;
    /**
     * 【編集】売上原価
     */
    private String[] net9;
    /**
     * 【編集】売上原価
     */
    private String[] net10;
    /**
     * 【編集】売上原価
     */
    private String[] net11;
    /**
     * 【編集】売上原価
     */
    private String[] net12;
    /**
     * 【編集】売上原価
     */
    private String[] netTm;

    /**
     * 【編集】回収金額
     */
    private String[] kaisyuAmount1;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuAmount2;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuAmount3;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuAmount4;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuAmount5;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuAmount6;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuAmount7;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuAmount8;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuAmount9;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuAmount10;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuAmount11;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuAmount12;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuAmountTm;


    /**
     * 【編集】回収金額
     */
    private String[] kaisyuEnkaAmount1;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuEnkaAmount2;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuEnkaAmount3;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuEnkaAmount4;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuEnkaAmount5;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuEnkaAmount6;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuEnkaAmount7;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuEnkaAmount8;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuEnkaAmount9;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuEnkaAmount10;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuEnkaAmount11;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuEnkaAmount12;
    /**
     * 【編集】回収金額
     */
    private String[] kaisyuEnkaAmountTm;


    /**
     * 【編集】契約金額更新フラグ
     */
    private String[] inpTargetKeiyakuAmountUpdateFlg1;
    /**
     * 【編集】契約金額更新フラグ
     */
    private String[] inpTargetKeiyakuAmountUpdateFlg2;
    /**
     * 【編集】契約金額更新フラグ
     */
    private String[] inpTargetKeiyakuAmountUpdateFlg3;
    /**
     * 【編集】契約金額更新フラグ
     */
    private String[] inpTargetKeiyakuAmountUpdateFlg4;
    /**
     * 【編集】契約金額更新フラグ
     */
    private String[] inpTargetKeiyakuAmountUpdateFlg5;
    /**
     * 【編集】契約金額更新フラグ
     */
    private String[] inpTargetKeiyakuAmountUpdateFlg6;
    /**
     * 【編集】契約金額更新フラグ
     */
    private String[] inpTargetKeiyakuAmountUpdateFlg7;
    /**
     * 【編集】契約金額更新フラグ
     */
    private String[] inpTargetKeiyakuAmountUpdateFlg8;
    /**
     * 【編集】契約金額更新フラグ
     */
    private String[] inpTargetKeiyakuAmountUpdateFlg9;
    /**
     * 【編集】契約金額更新フラグ
     */
    private String[] inpTargetKeiyakuAmountUpdateFlg10;
    /**
     * 【編集】契約金額更新フラグ
     */
    private String[] inpTargetKeiyakuAmountUpdateFlg11;
    /**
     * 【編集】契約金額更新フラグ
     */
    private String[] inpTargetKeiyakuAmountUpdateFlg12;

    /**
     * 【編集】発番NET更新フラグ
     */
    private String   inpTargetHatNetUpdateFlg1;
    /**
     * 【編集】発番NET更新フラグ
     */
    private String   inpTargetHatNetUpdateFlg2;
    /**
     * 【編集】発番NET更新フラグ
     */
    private String   inpTargetHatNetUpdateFlg3;
    /**
     * 【編集】発番NET更新フラグ
     */
    private String   inpTargetHatNetUpdateFlg4;
    /**
     * 【編集】発番NET更新フラグ
     */
    private String   inpTargetHatNetUpdateFlg5;
    /**
     * 【編集】発番NET更新フラグ
     */
    private String   inpTargetHatNetUpdateFlg6;
    /**
     * 【編集】発番NET更新フラグ
     */
    private String   inpTargetHatNetUpdateFlg7;
    /**
     * 【編集】発番NET更新フラグ
     */
    private String   inpTargetHatNetUpdateFlg8;
    /**
     * 【編集】発番NET更新フラグ
     */
    private String   inpTargetHatNetUpdateFlg9;
    /**
     * 【編集】発番NET更新フラグ
     */
    private String   inpTargetHatNetUpdateFlg10;
    /**
     * 【編集】発番NET更新フラグ
     */
    private String   inpTargetHatNetUpdateFlg11;
    /**
     * 【編集】発番NET更新フラグ
     */
    private String   inpTargetHatNetUpdateFlg12;

    /**
     * 【編集】未発番NET更新フラグ
     */
    private String   inpTargetMiNetUpdateFlg1;
    /**
     * 【編集】未発番NET更新フラグ
     */
    private String   inpTargetMiNetUpdateFlg2;
    /**
     * 【編集】未発番NET更新フラグ
     */
    private String   inpTargetMiNetUpdateFlg3;
    /**
     * 【編集】未発番NET更新フラグ
     */
    private String   inpTargetMiNetUpdateFlg4;
    /**
     * 【編集】未発番NET更新フラグ
     */
    private String   inpTargetMiNetUpdateFlg5;
    /**
     * 【編集】未発番NET更新フラグ
     */
    private String   inpTargetMiNetUpdateFlg6;
    /**
     * 【編集】未発番NET更新フラグ
     */
    private String   inpTargetMiNetUpdateFlg7;
    /**
     * 【編集】未発番NET更新フラグ
     */
    private String   inpTargetMiNetUpdateFlg8;
    /**
     * 【編集】未発番NET更新フラグ
     */
    private String   inpTargetMiNetUpdateFlg9;
    /**
     * 【編集】未発番NET更新フラグ
     */
    private String   inpTargetMiNetUpdateFlg10;
    /**
     * 【編集】未発番NET更新フラグ
     */
    private String   inpTargetMiNetUpdateFlg11;
    /**
     * 【編集】未発番NET更新フラグ
     */
    private String   inpTargetMiNetUpdateFlg12;

    /**
     * 【編集】製番損益NET更新フラグ
     */
    private String   inpTargetSeibanSonekiNetUpdateFlg1;
    /**
     * 【編集】製番損益NET更新フラグ
     */
    private String   inpTargetSeibanSonekiNetUpdateFlg2;
    /**
     * 【編集】製番損益NET更新フラグ
     */
    private String   inpTargetSeibanSonekiNetUpdateFlg3;
    /**
     * 【編集】製番損益NET更新フラグ
     */
    private String   inpTargetSeibanSonekiNetUpdateFlg4;
    /**
     * 【編集】製番損益NET更新フラグ
     */
    private String   inpTargetSeibanSonekiNetUpdateFlg5;
    /**
     * 【編集】製番損益NET更新フラグ
     */
    private String   inpTargetSeibanSonekiNetUpdateFlg6;
    /**
     * 【編集】製番損益NET更新フラグ
     */
    private String   inpTargetSeibanSonekiNetUpdateFlg7;
    /**
     * 【編集】製番損益NET更新フラグ
     */
    private String   inpTargetSeibanSonekiNetUpdateFlg8;
    /**
     * 【編集】製番損益NET更新フラグ
     */
    private String   inpTargetSeibanSonekiNetUpdateFlg9;
    /**
     * 【編集】製番損益NET更新フラグ
     */
    private String   inpTargetSeibanSonekiNetUpdateFlg10;
    /**
     * 【編集】製番損益NET更新フラグ
     */
    private String   inpTargetSeibanSonekiNetUpdateFlg11;
    /**
     * 【編集】製番損益NET更新フラグ
     */
    private String   inpTargetSeibanSonekiNetUpdateFlg12;

    /**
     * 【編集】為替洗替影響更新フラグ
     */
    private String   inpTargetKawaseEikyoUpdateFlg1;
    /**
     * 【編集】為替洗替影響更新フラグ
     */
    private String   inpTargetKawaseEikyoUpdateFlg2;
    /**
     * 【編集】為替洗替影響更新フラグ
     */
    private String   inpTargetKawaseEikyoUpdateFlg3;
    /**
     * 【編集】為替洗替影響更新フラグ
     */
    private String   inpTargetKawaseEikyoUpdateFlg4;
    /**
     * 【編集】為替洗替影響更新フラグ
     */
    private String   inpTargetKawaseEikyoUpdateFlg5;
    /**
     * 【編集】為替洗替影響更新フラグ
     */
    private String   inpTargetKawaseEikyoUpdateFlg6;
    /**
     * 【編集】為替洗替影響更新フラグ
     */
    private String   inpTargetKawaseEikyoUpdateFlg7;
    /**
     * 【編集】為替洗替影響更新フラグ
     */
    private String   inpTargetKawaseEikyoUpdateFlg8;
    /**
     * 【編集】為替洗替影響更新フラグ
     */
    private String   inpTargetKawaseEikyoUpdateFlg9;
    /**
     * 【編集】為替洗替影響更新フラグ
     */
    private String   inpTargetKawaseEikyoUpdateFlg10;
    /**
     * 【編集】為替洗替影響更新フラグ
     */
    private String   inpTargetKawaseEikyoUpdateFlg11;
    /**
     * 【編集】為替洗替影響更新フラグ
     */
    private String   inpTargetKawaseEikyoUpdateFlg12;

    /**
     * 【編集】売上原価更新フラグ
     */
    private String[] inpTargetNetUpdateFlg1;
    /**
     * 【編集】売上原価更新フラグ
     */
    private String[] inpTargetNetUpdateFlg2;
    /**
     * 【編集】売上原価更新フラグ
     */
    private String[] inpTargetNetUpdateFlg3;
    /**
     * 【編集】売上原価更新フラグ
     */
    private String[] inpTargetNetUpdateFlg4;
    /**
     * 【編集】売上原価更新フラグ
     */
    private String[] inpTargetNetUpdateFlg5;
    /**
     * 【編集】売上原価更新フラグ
     */
    private String[] inpTargetNetUpdateFlg6;
    /**
     * 【編集】売上原価更新フラグ
     */
    private String[] inpTargetNetUpdateFlg7;
    /**
     * 【編集】売上原価更新フラグ
     */
    private String[] inpTargetNetUpdateFlg8;
    /**
     * 【編集】売上原価更新フラグ
     */
    private String[] inpTargetNetUpdateFlg9;
    /**
     * 【編集】売上原価更新フラグ
     */
    private String[] inpTargetNetUpdateFlg10;
    /**
     * 【編集】売上原価更新フラグ
     */
    private String[] inpTargetNetUpdateFlg11;
    /**
     * 【編集】売上原価更新フラグ
     */
    private String[] inpTargetNetUpdateFlg12;

    /**
     * 【編集】回収金額更新フラグ
     */
    private String[] inpTargetKaisyuAmountUpdateFlg1;
    /**
     * 【編集】回収金額更新フラグ
     */
    private String[] inpTargetKaisyuAmountUpdateFlg2;
    /**
     * 【編集】回収金額更新フラグ
     */
    private String[] inpTargetKaisyuAmountUpdateFlg3;
    /**
     * 【編集】回収金額更新フラグ
     */
    private String[] inpTargetKaisyuAmountUpdateFlg4;
    /**
     * 【編集】回収金額更新フラグ
     */
    private String[] inpTargetKaisyuAmountUpdateFlg5;
    /**
     * 【編集】回収金額更新フラグ
     */
    private String[] inpTargetKaisyuAmountUpdateFlg6;
    /**
     * 【編集】回収金額更新フラグ
     */
    private String[] inpTargetKaisyuAmountUpdateFlg7;
    /**
     * 【編集】回収金額更新フラグ
     */
    private String[] inpTargetKaisyuAmountUpdateFlg8;
    /**
     * 【編集】回収金額更新フラグ
     */
    private String[] inpTargetKaisyuAmountUpdateFlg9;
    /**
     * 【編集】回収金額更新フラグ
     */
    private String[] inpTargetKaisyuAmountUpdateFlg10;
    /**
     * 【編集】回収金額更新フラグ
     */
    private String[] inpTargetKaisyuAmountUpdateFlg11;
    /**
     * 【編集】回収金額更新フラグ
     */
    private String[] inpTargetKaisyuAmountUpdateFlg12;

    /**
     * 【編集】契約金額通貨コード
     */
    private String[] inpTargetKeiyakuCurrencyCode;

    /**
     * 【編集】売上原価カテゴリ区分1
     */
    private String[] inpTargetNetCategoryKbn1;

    /**
     * 【編集】売上原価カテゴリ区分2
     */
    private String[] inpTargetNetCategoryKbn2;

    /**
     * 【編集】売上原価カテゴリコード
     */
    private String[] inpTargetNetCategoryCode;


    /**
     * 【編集】回収金額通貨コード
     */
    private String[] inpTargetKaisyuAmountCurrencyCode;

    /**
     * 【編集】回収金額税区分
     */
    private String[] inpTargetKaisyuAmountZeiKbn;

    /**
     * 【編集】回収金額金種区分
     */
    private String[] inpTargetKaisyuAmountKinsyuKbn;

    /**
     * 【編集】回収金額回収区分
     */
    private String[] inpTargetKaisyuAmountKaisyuKbn;


    /**
     * 契約金額の入力エラーリスト
     */
    private List<boolean[]> keiyakuAmountErrorList;
    /**
     * 未発番NETの入力エラーリスト
     */
    private List<Boolean> miNetErrorList;
    /**
     * 製番損益NETの入力エラーリスト
     */
    private List<Boolean> seibanSonekiErrorList;
    /**
     * 売上原価の入力エラーリスト
     */
    private List<boolean[]> netErrorList;
    /**
     * 回収金額の入力エラーリスト
     */
    private List<boolean[]> kaisyuAmountErrorList;

    /**
     * 差分反映済みフラグ
     */
    private String sabunHaneiFlg;

    /**
     * 編集フラグ
     */
    private String editFlg;

    /**
     * 期間Fromの各年月(yyyyMM)を格納した配列
     */
    private String[] kikanFromAry;

    /**
     * 期間Toの各年月(yyyyMM)を格納した配列
     */
    private String[] kikanToAry;

    /**
     * 備考
     */
    private String bikou;

    /**
     * 注番
     */
    private String orderNo;

    /**
     * 最新値更新区分(1:発番取込 2:契約取込 3:見積取込)
     */
    private String updateNewDataKbn;
    
    /**
     * [最新値更新]ボタンがりようできるか？(1:可能 0:不可)
     */
    private String saisyuUpdeteBtnFlg = "0";
    
    /**
     * 編集可能権限FLG
     */
//    private String editAuthFlg = "0";

    /**
     * ダウンロード　回収管理の種類
     */
    private List<S004DownloadKaisyuEntity> kaisyuCurrencyList;
    
    /**
     * ロスコン情報
     */
    private Map<String, Map<String, String>> lossData;
    
    /**
     * 契約金額や売上額を外貨表記(小数点2桁まで表記するか？)
     * 1:少数2桁まで表示(外貨表記) 0:整数表記(円貨表記)
     */
    private Integer foreignFlg = 0;

    /**
     * 回収額を外貨表記(小数点2桁まで表記するか？)
     * 1:少数2桁まで表示(外貨表記) 0:整数表記(円貨表記)
     */
    private Integer kaisyuForeignFlg = 0;
    
    /**
     * 受注を外貨表記(小数点2桁まで表記するか？)
     * 1:少数2桁まで表示(外貨表記) 0:整数表記(円貨表記)
     */
    private Integer jyuchuForeignFlg = 0;
    
    /**
     * Creates a new instance of S004Bean
     */
    public S004Bean() {
    }

    public String getKi() {
        return this.ki;
    }

    public String getTotal() {
        return this.total;
    }

    public String getLastMikomi() {
        return this.lastMikomi;
    }

    public Integer getJpyUnit() {
        return jpyUnit;
    }

    public void setJpyUnit(Integer jpyUnit) {
        this.jpyUnit = jpyUnit;
    }

    public String getSpListOpenFlg() {
        return spListOpenFlg;
    }

    public void setSpListOpenFlg(String spListOpenFlg) {
        this.spListOpenFlg = spListOpenFlg;
    }

    public String getNetListOpenFlg() {
        return netListOpenFlg;
    }

    public void setNetListOpenFlg(String netListOpenFlg) {
        this.netListOpenFlg = netListOpenFlg;
    }

    public String getUriage1ListOpenFlg() {
        return uriage1ListOpenFlg;
    }

    public void setUriage1ListOpenFlg(String uriage1ListOpenFlg) {
        this.uriage1ListOpenFlg = uriage1ListOpenFlg;
    }

    public String getUriage2ListOpenFlg() {
        return uriage2ListOpenFlg;
    }

    public void setUriage2ListOpenFlg(String uriage2ListOpenFlg) {
        this.uriage2ListOpenFlg = uriage2ListOpenFlg;
    }

    public String getUriGen1ListOpenFlg() {
        return uriGen1ListOpenFlg;
    }

    public void setUriGen1ListOpenFlg(String uriGen1ListOpenFlg) {
        this.uriGen1ListOpenFlg = uriGen1ListOpenFlg;
    }

    public String getKikanChangeFlg() {
        return kikanChangeFlg;
    }

    public void setKikanChangeFlg(String kikanChangeFlg) {
        this.kikanChangeFlg = kikanChangeFlg;
    }

    public int getTmIndex() {
        return tmIndex;
    }

    public void setTmIndex(int tmIndex) {
        this.tmIndex = tmIndex;
    }

    public String[] getClassNameAry() {
        return classNameAry;
    }

    public void setClassNameAry(String[] classNameAry) {
        this.classNameAry = classNameAry;
    }

    public ArrayList<String> getMonthTitle() {
        return this.monthTitle;
    }

    public void setMonthTitle(ArrayList<String> monthTitle) {
        this.monthTitle = monthTitle;
    }
/*
    public ArrayList<String> getTitle() {
        return this.title;
    }

    public void setTitle(ArrayList<String> title) {
        this.title = title;
    }
*/
    public HashMap<String, String> getTotalContractAmount() {
        return totalContractAmount;
    }

    public void setTotalContractAmount(HashMap<String, String> totalContractAmount) {
        this.totalContractAmount = totalContractAmount;
    }

    public  List<HashMap<String, String>> getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount( List<HashMap<String, String>> contractAmount) {
        this.contractAmount = contractAmount;
    }

    public HashMap<String, String> getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(HashMap<String, String> totalCost) {
        this.totalCost = totalCost;
    }

    public HashMap<String, String> getCostList() {
        return costList;
    }

    public void setCostList(HashMap<String, String> costList) {
        this.costList = costList;
    }

    public HashMap<String, String> getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(HashMap<String, String> totalSales) {
        this.totalSales = totalSales;
    }

    public List<HashMap<String, String>> getSalesList() {
        return salesList;
    }

    public void setSalesList(List<HashMap<String, String>> salesList) {
        this.salesList = salesList;
    }

    public HashMap<String, String> getTotalRuikeiSales() {
        return totalRuikeiSales;
    }

    public void setTotalRuikeiSales(HashMap<String, String> totalRuikeiSales) {
        this.totalRuikeiSales = totalRuikeiSales;
    }

    public List<HashMap<String, String>> getRuikeiSalesList() {
        return ruikeiSalesList;
    }

    public void setRuikeiSalesList(List<HashMap<String, String>> ruikeiSalesList) {
        this.ruikeiSalesList = ruikeiSalesList;
    }

    public HashMap<String, String> getTotalSalesCost() {
        return totalSalesCost;
    }

    public void setTotalSalesCost(HashMap<String, String> totalSalesCost) {
        this.totalSalesCost = totalSalesCost;
    }

    public List<HashMap<String, String>> getSalesCostList() {
        return salesCostList;
    }

    public void setSalesCostList(List<HashMap<String, String>> salesCostList) {
        this.salesCostList = salesCostList;
    }

    public HashMap<String, String> getTotalSalesCostRuikei() {
        return totalSalesCostRuikei;
    }

    public void setTotalSalesCostRuikei(HashMap<String, String> totalSalesCostRuikei) {
        this.totalSalesCostRuikei = totalSalesCostRuikei;
    }

    public HashMap<String, String> getTotalRecoveryAmountData() {
        return totalRecoveryAmountData;
    }

    public void setTotalRecoveryAmountData(HashMap<String, String> totalRecoveryAmountData) {
        this.totalRecoveryAmountData = totalRecoveryAmountData;
    }

    public List<HashMap<String, String>> getRecoveryAmountList() {
        return recoveryAmountList;
    }

    public void setRecoveryAmountList(List<HashMap<String, String>> recoveryAmountList) {
        this.recoveryAmountList = recoveryAmountList;
    }


    public String getArari1() {
        return arari1;
    }

    public void setArari1(String arari1) {
        this.arari1 = arari1;
    }

    public String getArari2() {
        return arari2;
    }

    public void setArari2(String arari2) {
        this.arari2 = arari2;
    }

    public String getArari3() {
        return arari3;
    }

    public void setArari3(String arari3) {
        this.arari3 = arari3;
    }

    public String getArari4() {
        return arari4;
    }

    public void setArari4(String arari4) {
        this.arari4 = arari4;
    }

    public String getArari5() {
        return arari5;
    }

    public void setArari5(String arari5) {
        this.arari5 = arari5;
    }

    public String getArari6() {
        return arari6;
    }

    public void setArari6(String arari6) {
        this.arari6 = arari6;
    }

    public String getArari7() {
        return arari7;
    }

    public void setArari7(String arari7) {
        this.arari7 = arari7;
    }

    public String getArari8() {
        return arari8;
    }

    public void setArari8(String arari8) {
        this.arari8 = arari8;
    }

    public String getArari9() {
        return arari9;
    }

    public void setArari9(String arari9) {
        this.arari9 = arari9;
    }

    public String getArari10() {
        return arari10;
    }

    public void setArari10(String arari10) {
        this.arari10 = arari10;
    }

    public String getArari11() {
        return arari11;
    }

    public void setArari11(String arari11) {
        this.arari11 = arari11;
    }

    public String getArari12() {
        return arari12;
    }

    public void setArari12(String arari12) {
        this.arari12 = arari12;
    }

    public String getArariK1() {
        return arariK1;
    }

    public void setArariK1(String arariK1) {
        this.arariK1 = arariK1;
    }

    public String getArariK2() {
        return arariK2;
    }

    public void setArariK2(String arariK2) {
        this.arariK2 = arariK2;
    }

    public String getArariG() {
        return arariG;
    }

    public void setArariG(String arariG) {
        this.arariG = arariG;
    }

    public String getArariF() {
        return arariF;
    }

    public void setArariF(String arariF) {
        this.arariF = arariF;
    }

    public String getArariK1Diff() {
        return arariK1Diff;
    }

    public void setArariK1Diff(String arariK1Diff) {
        this.arariK1Diff = arariK1Diff;
    }

    public String getArariK2Diff() {
        return arariK2Diff;
    }

    public void setArariK2Diff(String arariK2Diff) {
        this.arariK2Diff = arariK2Diff;
    }

    public String getArariGDiff() {
        return arariGDiff;
    }

    public void setArariGDiff(String arariGDiff) {
        this.arariGDiff = arariGDiff;
    }

    public String getArariDiff() {
        return arariDiff;
    }

    public void setArariDiff(String arariDiff) {
        this.arariDiff = arariDiff;
    }

    public String getArariTm() {
        return arariTm;
    }

    public void setArariTm(String arariTm) {
        this.arariTm = arariTm;
    }

    public String getArariRuikei1() {
        return arariRuikei1;
    }

    public void setArariRuikei1(String arariRuikei1) {
        this.arariRuikei1 = arariRuikei1;
    }

    public String getArariRuikei2() {
        return arariRuikei2;
    }

    public void setArariRuikei2(String arariRuikei2) {
        this.arariRuikei2 = arariRuikei2;
    }

    public String getArariRuikei3() {
        return arariRuikei3;
    }

    public void setArariRuikei3(String arariRuikei3) {
        this.arariRuikei3 = arariRuikei3;
    }

    public String getArariRuikei4() {
        return arariRuikei4;
    }

    public void setArariRuikei4(String arariRuikei4) {
        this.arariRuikei4 = arariRuikei4;
    }

    public String getArariRuikei5() {
        return arariRuikei5;
    }

    public void setArariRuikei5(String arariRuikei5) {
        this.arariRuikei5 = arariRuikei5;
    }

    public String getArariRuikei6() {
        return arariRuikei6;
    }

    public void setArariRuikei6(String arariRuikei6) {
        this.arariRuikei6 = arariRuikei6;
    }

    public String getArariRuikei7() {
        return arariRuikei7;
    }

    public void setArariRuikei7(String arariRuikei7) {
        this.arariRuikei7 = arariRuikei7;
    }

    public String getArariRuikei8() {
        return arariRuikei8;
    }

    public void setArariRuikei8(String arariRuikei8) {
        this.arariRuikei8 = arariRuikei8;
    }

    public String getArariRuikei9() {
        return arariRuikei9;
    }

    public void setArariRuikei9(String arariRuikei9) {
        this.arariRuikei9 = arariRuikei9;
    }

    public String getArariRuikei10() {
        return arariRuikei10;
    }

    public void setArariRuikei10(String arariRuikei10) {
        this.arariRuikei10 = arariRuikei10;
    }

    public String getArariRuikei11() {
        return arariRuikei11;
    }

    public void setArariRuikei11(String arariRuikei11) {
        this.arariRuikei11 = arariRuikei11;
    }

    public String getArariRuikei12() {
        return arariRuikei12;
    }

    public void setArariRuikei12(String arariRuikei12) {
        this.arariRuikei12 = arariRuikei12;
    }

    public String getArariRuikeiK1() {
        return arariRuikeiK1;
    }

    public void setArariRuikeiK1(String arariRuikeiK1) {
        this.arariRuikeiK1 = arariRuikeiK1;
    }

    public String getArariRuikeiK2() {
        return arariRuikeiK2;
    }

    public void setArariRuikeiK2(String arariRuikeiK2) {
        this.arariRuikeiK2 = arariRuikeiK2;
    }

    public String getArariRuikeiG() {
        return arariRuikeiG;
    }

    public void setArariRuikeiG(String arariRuikeiG) {
        this.arariRuikeiG = arariRuikeiG;
    }

    public String getArariRuikeiF() {
        return arariRuikeiF;
    }

    public void setArariRuikeiF(String arariRuikeiF) {
        this.arariRuikeiF = arariRuikeiF;
    }

    public String getArariRuikeiK1Diff() {
        return arariRuikeiK1Diff;
    }

    public void setArariRuikeiK1Diff(String arariRuikeiK1Diff) {
        this.arariRuikeiK1Diff = arariRuikeiK1Diff;
    }

    public String getArariRuikeiK2Diff() {
        return arariRuikeiK2Diff;
    }

    public void setArariRuikeiK2Diff(String arariRuikeiK2Diff) {
        this.arariRuikeiK2Diff = arariRuikeiK2Diff;
    }

    public String getArariRuikeiGDiff() {
        return arariRuikeiGDiff;
    }

    public void setArariRuikeiGDiff(String arariRuikeiGDiff) {
        this.arariRuikeiGDiff = arariRuikeiGDiff;
    }

    public String getArariRuikeiDiff() {
        return arariRuikeiDiff;
    }

    public void setArariRuikeiDiff(String arariRuikeiDiff) {
        this.arariRuikeiDiff = arariRuikeiDiff;
    }

    public String getArariRuikeiTm() {
        return arariRuikeiTm;
    }

    public void setArariRuikeiTm(String arariRuikeiTm) {
        this.arariRuikeiTm = arariRuikeiTm;
    }

    public String getMrate1() {
        return mrate1;
    }

    public void setMrate1(String mrate1) {
        this.mrate1 = mrate1;
    }

    public String getMrate2() {
        return mrate2;
    }

    public void setMrate2(String mrate2) {
        this.mrate2 = mrate2;
    }

    public String getMrate3() {
        return mrate3;
    }

    public void setMrate3(String mrate3) {
        this.mrate3 = mrate3;
    }

    public String getMrate4() {
        return mrate4;
    }

    public void setMrate4(String mrate4) {
        this.mrate4 = mrate4;
    }

    public String getMrate5() {
        return mrate5;
    }

    public void setMrate5(String mrate5) {
        this.mrate5 = mrate5;
    }

    public String getMrate6() {
        return mrate6;
    }

    public void setMrate6(String mrate6) {
        this.mrate6 = mrate6;
    }

    public String getMrate7() {
        return mrate7;
    }

    public void setMrate7(String mrate7) {
        this.mrate7 = mrate7;
    }

    public String getMrate8() {
        return mrate8;
    }

    public void setMrate8(String mrate8) {
        this.mrate8 = mrate8;
    }

    public String getMrate9() {
        return mrate9;
    }

    public void setMrate9(String mrate9) {
        this.mrate9 = mrate9;
    }

    public String getMrate10() {
        return mrate10;
    }

    public void setMrate10(String mrate10) {
        this.mrate10 = mrate10;
    }

    public String getMrate11() {
        return mrate11;
    }

    public void setMrate11(String mrate11) {
        this.mrate11 = mrate11;
    }

    public String getMrate12() {
        return mrate12;
    }

    public void setMrate12(String mrate12) {
        this.mrate12 = mrate12;
    }

    public String getMrateK1() {
        return mrateK1;
    }

    public void setMrateK1(String mrateK1) {
        this.mrateK1 = mrateK1;
    }

    public String getMrateK2() {
        return mrateK2;
    }

    public void setMrateK2(String mrateK2) {
        this.mrateK2 = mrateK2;
    }

    public String getMrateG() {
        return mrateG;
    }

    public void setMrateG(String mrateG) {
        this.mrateG = mrateG;
    }

    public String getMrateF() {
        return mrateF;
    }

    public void setMrateF(String mrateF) {
        this.mrateF = mrateF;
    }

    public String getMrateK1Diff() {
        return mrateK1Diff;
    }

    public void setMrateK1Diff(String mrateK1Diff) {
        this.mrateK1Diff = mrateK1Diff;
    }

    public String getMrateK2Diff() {
        return mrateK2Diff;
    }

    public void setMrateK2Diff(String mrateK2Diff) {
        this.mrateK2Diff = mrateK2Diff;
    }

    public String getMrateGDiff() {
        return mrateGDiff;
    }

    public void setMrateGDiff(String mrateGDiff) {
        this.mrateGDiff = mrateGDiff;
    }

    public String getMrateDiff() {
        return mrateDiff;
    }

    public void setMrateDiff(String mrateDiff) {
        this.mrateDiff = mrateDiff;
    }

    public String getMrateTm() {
        return mrateTm;
    }

    public void setMrateTm(String mrateTm) {
        this.mrateTm = mrateTm;
    }

    public String getMrateRuikei1() {
        return mrateRuikei1;
    }

    public void setMrateRuikei1(String mrateRuikei1) {
        this.mrateRuikei1 = mrateRuikei1;
    }

    public String getMrateRuikei2() {
        return mrateRuikei2;
    }

    public void setMrateRuikei2(String mrateRuikei2) {
        this.mrateRuikei2 = mrateRuikei2;
    }

    public String getMrateRuikei3() {
        return mrateRuikei3;
    }

    public void setMrateRuikei3(String mrateRuikei3) {
        this.mrateRuikei3 = mrateRuikei3;
    }

    public String getMrateRuikei4() {
        return mrateRuikei4;
    }

    public void setMrateRuikei4(String mrateRuikei4) {
        this.mrateRuikei4 = mrateRuikei4;
    }

    public String getMrateRuikei5() {
        return mrateRuikei5;
    }

    public void setMrateRuikei5(String mrateRuikei5) {
        this.mrateRuikei5 = mrateRuikei5;
    }

    public String getMrateRuikei6() {
        return mrateRuikei6;
    }

    public void setMrateRuikei6(String mrateRuikei6) {
        this.mrateRuikei6 = mrateRuikei6;
    }

    public String getMrateRuikei7() {
        return mrateRuikei7;
    }

    public void setMrateRuikei7(String mrateRuikei7) {
        this.mrateRuikei7 = mrateRuikei7;
    }

    public String getMrateRuikei8() {
        return mrateRuikei8;
    }

    public void setMrateRuikei8(String mrateRuikei8) {
        this.mrateRuikei8 = mrateRuikei8;
    }

    public String getMrateRuikei9() {
        return mrateRuikei9;
    }

    public void setMrateRuikei9(String mrateRuikei9) {
        this.mrateRuikei9 = mrateRuikei9;
    }

    public String getMrateRuikei10() {
        return mrateRuikei10;
    }

    public void setMrateRuikei10(String mrateRuikei10) {
        this.mrateRuikei10 = mrateRuikei10;
    }

    public String getMrateRuikei11() {
        return mrateRuikei11;
    }

    public void setMrateRuikei11(String mrateRuikei11) {
        this.mrateRuikei11 = mrateRuikei11;
    }

    public String getMrateRuikei12() {
        return mrateRuikei12;
    }

    public void setMrateRuikei12(String mrateRuikei12) {
        this.mrateRuikei12 = mrateRuikei12;
    }

    public String getMrateRuikeiK1() {
        return mrateRuikeiK1;
    }

    public void setMrateRuikeiK1(String mrateRuikeiK1) {
        this.mrateRuikeiK1 = mrateRuikeiK1;
    }

    public String getMrateRuikeiK2() {
        return mrateRuikeiK2;
    }

    public void setMrateRuikeiK2(String mrateRuikeiK2) {
        this.mrateRuikeiK2 = mrateRuikeiK2;
    }

    public String getMrateRuikeiG() {
        return mrateRuikeiG;
    }

    public void setMrateRuikeiG(String mrateRuikeiG) {
        this.mrateRuikeiG = mrateRuikeiG;
    }

    public String getMrateRuikeiF() {
        return mrateRuikeiF;
    }

    public void setMrateRuikeiF(String mrateRuikeiF) {
        this.mrateRuikeiF = mrateRuikeiF;
    }

    public String getMrateRuikeiK1Diff() {
        return mrateRuikeiK1Diff;
    }

    public void setMrateRuikeiK1Diff(String mrateRuikeiK1Diff) {
        this.mrateRuikeiK1Diff = mrateRuikeiK1Diff;
    }

    public String getMrateRuikeiK2Diff() {
        return mrateRuikeiK2Diff;
    }

    public void setMrateRuikeiK2Diff(String mrateRuikeiK2Diff) {
        this.mrateRuikeiK2Diff = mrateRuikeiK2Diff;
    }

    public String getMrateRuikeiGDiff() {
        return mrateRuikeiGDiff;
    }

    public void setMrateRuikeiGDiff(String mrateRuikeiGDiff) {
        this.mrateRuikeiGDiff = mrateRuikeiGDiff;
    }

    public String getMrateRuikeiDiff() {
        return mrateRuikeiDiff;
    }

    public void setMrateRuikeiDiff(String mrateRuikeiDiff) {
        this.mrateRuikeiDiff = mrateRuikeiDiff;
    }

    public String getMrateRuikeiTm() {
        return mrateRuikeiTm;
    }

    public void setMrateRuikeiTm(String mrateRuikeiTm) {
        this.mrateRuikeiTm = mrateRuikeiTm;
    }

    public List<String> getKikanFromList() {
        return kikanFromList;
    }

    public void setKikanFromList(List<String> kikanFromList) {
        this.kikanFromList = kikanFromList;
    }

    public String[] getKeiyakuAmount1() {
        return this.keiyakuAmount1;
    }

    public void setKeiyakuAmount1(String[] keiyakuAmount1) {
        this.keiyakuAmount1 = keiyakuAmount1;
    }

    public String[] getKeiyakuAmount2() {
        return this.keiyakuAmount2;
    }

    public void setKeiyakuAmount2(String[] keiyakuAmount2) {
        this.keiyakuAmount2 = keiyakuAmount2;
    }

    public String[] getKeiyakuAmount3() {
        return this.keiyakuAmount3;
    }

    public void setKeiyakuAmount3(String[] keiyakuAmount3) {
        this.keiyakuAmount3 = keiyakuAmount3;
    }

    public String[] getKeiyakuAmount4() {
        return this.keiyakuAmount4;
    }

    public void setKeiyakuAmount4(String[] keiyakuAmount4) {
        this.keiyakuAmount4 = keiyakuAmount4;
    }

    public String[] getKeiyakuAmount5() {
        return this.keiyakuAmount5;
    }

    public void setKeiyakuAmount5(String[] keiyakuAmount5) {
        this.keiyakuAmount5 = keiyakuAmount5;
    }

    public String[] getKeiyakuAmount6() {
        return this.keiyakuAmount6;
    }

    public void setKeiyakuAmount6(String[] keiyakuAmount6) {
        this.keiyakuAmount6 = keiyakuAmount6;
    }

    public String[] getKeiyakuAmount7() {
        return this.keiyakuAmount7;
    }

    public void setKeiyakuAmount7(String[] keiyakuAmount7) {
        this.keiyakuAmount7 = keiyakuAmount7;
    }

    public String[] getKeiyakuAmount8() {
        return this.keiyakuAmount8;
    }

    public void setKeiyakuAmount8(String[] keiyakuAmount8) {
        this.keiyakuAmount8 = keiyakuAmount8;
    }

    public String[] getKeiyakuAmount9() {
        return this.keiyakuAmount9;
    }

    public void setKeiyakuAmount9(String[] keiyakuAmount9) {
        this.keiyakuAmount9 = keiyakuAmount9;
    }

    public String[] getKeiyakuAmount10() {
        return this.keiyakuAmount10;
    }

    public void setKeiyakuAmount10(String[] keiyakuAmount10) {
        this.keiyakuAmount10 = keiyakuAmount10;
    }

    public String[] getKeiyakuAmount11() {
        return this.keiyakuAmount11;
    }

    public void setKeiyakuAmount11(String[] keiyakuAmount11) {
        this.keiyakuAmount11 = keiyakuAmount11;
    }

    public String[] getKeiyakuAmount12() {
        return this.keiyakuAmount12;
    }

    public void setKeiyakuAmount12(String[] keiyakuAmount12) {
        this.keiyakuAmount12 = keiyakuAmount12;
    }

    public String[] getKeiyakuAmountTm() {
        return this.keiyakuAmountTm;
    }

    public void setKeiyakuAmountTm(String[] keiyakuAmountTm) {
        this.keiyakuAmountTm = keiyakuAmountTm;
    }

    public String getMiNet1() {
        return this.miNet1;
    }

    public void setMiNet1(String miNet1) {
        this.miNet1 = miNet1;
    }

    public String getMiNet2() {
        return this.miNet2;
    }

    public void setMiNet2(String miNet2) {
        this.miNet2 = miNet2;
    }

    public String getMiNet3() {
        return this.miNet3;
    }

    public void setMiNet3(String miNet3) {
        this.miNet3 = miNet3;
    }

    public String getMiNet4() {
        return this.miNet4;
    }

    public void setMiNet4(String miNet4) {
        this.miNet4 = miNet4;
    }

    public String getMiNet5() {
        return this.miNet5;
    }

    public void setMiNet5(String miNet5) {
        this.miNet5 = miNet5;
    }

    public String getMiNet6() {
        return this.miNet6;
    }

    public void setMiNet6(String miNet6) {
        this.miNet6 = miNet6;
    }

    public String getMiNet7() {
        return this.miNet7;
    }

    public void setMiNet7(String miNet7) {
        this.miNet7 = miNet7;
    }

    public String getMiNet8() {
        return this.miNet8;
    }

    public void setMiNet8(String miNet8) {
        this.miNet8 = miNet8;
    }

    public String getMiNet9() {
        return this.miNet9;
    }

    public void setMiNet9(String miNet9) {
        this.miNet9 = miNet9;
    }

    public String getMiNet10() {
        return this.miNet10;
    }

    public void setMiNet10(String miNet10) {
        this.miNet10 = miNet10;
    }

    public String getMiNet11() {
        return this.miNet11;
    }

    public void setMiNet11(String miNet11) {
        this.miNet11 = miNet11;
    }

    public String getMiNet12() {
        return this.miNet12;
    }

    public void setMiNet12(String miNet12) {
        this.miNet12 = miNet12;
    }

    public String getMiNetTm() {
        return this.miNetTm;
    }

    public void setMiNetTm(String miNetTm) {
        this.miNetTm = miNetTm;
    }

    public String getSeibanSonekiNet1() {
        return this.seibanSonekiNet1;
    }

    public void setSeibanSonekiNet1(String seibanSonekiNet1) {
        this.seibanSonekiNet1 = seibanSonekiNet1;
    }

    public String getSeibanSonekiNet2() {
        return this.seibanSonekiNet2;
    }

    public void setSeibanSonekiNet2(String seibanSonekiNet2) {
        this.seibanSonekiNet2 = seibanSonekiNet2;
    }

    public String getSeibanSonekiNet3() {
        return this.seibanSonekiNet3;
    }

    public void setSeibanSonekiNet3(String seibanSonekiNet3) {
        this.seibanSonekiNet3 = seibanSonekiNet3;
    }

    public String getSeibanSonekiNet4() {
        return this.seibanSonekiNet4;
    }

    public void setSeibanSonekiNet4(String seibanSonekiNet4) {
        this.seibanSonekiNet4 = seibanSonekiNet4;
    }

    public String getSeibanSonekiNet5() {
        return this.seibanSonekiNet5;
    }

    public void setSeibanSonekiNet5(String seibanSonekiNet5) {
        this.seibanSonekiNet5 = seibanSonekiNet5;
    }

    public String getSeibanSonekiNet6() {
        return this.seibanSonekiNet6;
    }

    public void setSeibanSonekiNet6(String seibanSonekiNet6) {
        this.seibanSonekiNet6 = seibanSonekiNet6;
    }

    public String getSeibanSonekiNet7() {
        return this.seibanSonekiNet7;
    }

    public void setSeibanSonekiNet7(String seibanSonekiNet7) {
        this.seibanSonekiNet7 = seibanSonekiNet7;
    }

    public String getSeibanSonekiNet8() {
        return this.seibanSonekiNet8;
    }

    public void setSeibanSonekiNet8(String seibanSonekiNet8) {
        this.seibanSonekiNet8 = seibanSonekiNet8;
    }

    public String getSeibanSonekiNet9() {
        return this.seibanSonekiNet9;
    }

    public void setSeibanSonekiNet9(String seibanSonekiNet9) {
        this.seibanSonekiNet9 = seibanSonekiNet9;
    }

    public String getSeibanSonekiNet10() {
        return this.seibanSonekiNet10;
    }

    public void setSeibanSonekiNet10(String seibanSonekiNet10) {
        this.seibanSonekiNet10 = seibanSonekiNet10;
    }

    public String getSeibanSonekiNet11() {
        return this.seibanSonekiNet11;
    }

    public void setSeibanSonekiNet11(String seibanSonekiNet11) {
        this.seibanSonekiNet11 = seibanSonekiNet11;
    }

    public String getSeibanSonekiNet12() {
        return this.seibanSonekiNet12;
    }

    public void setSeibanSonekiNet12(String seibanSonekiNet12) {
        this.seibanSonekiNet12 = seibanSonekiNet12;
    }

    public String getSeibanSonekiNetTm() {
        return this.seibanSonekiNetTm;
    }

    public void setSeibanSonekiNetTm(String seibanSonekiNetTm) {
        this.seibanSonekiNetTm = seibanSonekiNetTm;
    }

    public String[] getNet1() {
        return this.net1;
    }

    public void setNet1(String[] net1) {
        this.net1 = net1;
    }

    public String[] getNet2() {
        return this.net2;
    }

    public void setNet2(String[] net2) {
        this.net2 = net2;
    }

    public String[] getNet3() {
        return this.net3;
    }

    public void setNet3(String[] net3) {
        this.net3 = net3;
    }

    public String[] getNet4() {
        return this.net4;
    }

    public void setNet4(String[] net4) {
        this.net4 = net4;
    }

    public String[] getNet5() {
        return this.net5;
    }

    public void setNet5(String[] net5) {
        this.net5 = net5;
    }

    public String[] getNet6() {
        return this.net6;
    }

    public void setNet6(String[] net6) {
        this.net6 = net6;
    }

    public String[] getNet7() {
        return this.net7;
    }

    public void setNet7(String[] net7) {
        this.net7 = net7;
    }

    public String[] getNet8() {
        return this.net8;
    }

    public void setNet8(String[] net8) {
        this.net8 = net8;
    }

    public String[] getNet9() {
        return this.net9;
    }

    public void setNet9(String[] net9) {
        this.net9 = net9;
    }

    public String[] getNet10() {
        return this.net10;
    }

    public void setNet10(String[] net10) {
        this.net10 = net10;
    }

    public String[] getNet11() {
        return this.net11;
    }

    public void setNet11(String[] net11) {
        this.net11 = net11;
    }

    public String[] getNet12() {
        return this.net12;
    }

    public void setNet12(String[] net12) {
        this.net12 = net12;
    }

    public String[] getNetTm() {
        return this.netTm;
    }

    public void setNetTm(String[] netTm) {
        this.netTm = netTm;
    }

    public String[] getKaisyuAmount1() {
        return this.kaisyuAmount1;
    }

    public void setKaisyuAmount1(String[] kaisyuAmount1) {
        this.kaisyuAmount1 = kaisyuAmount1;
    }

    public String[] getKaisyuAmount2() {
        return this.kaisyuAmount2;
    }

    public void setKaisyuAmount2(String[] kaisyuAmount2) {
        this.kaisyuAmount2 = kaisyuAmount2;
    }

    public String[] getKaisyuAmount3() {
        return this.kaisyuAmount3;
    }

    public void setKaisyuAmount3(String[] kaisyuAmount3) {
        this.kaisyuAmount3 = kaisyuAmount3;
    }

    public String[] getKaisyuAmount4() {
        return this.kaisyuAmount4;
    }

    public void setKaisyuAmount4(String[] kaisyuAmount4) {
        this.kaisyuAmount4 = kaisyuAmount4;
    }

    public String[] getKaisyuAmount5() {
        return this.kaisyuAmount5;
    }

    public void setKaisyuAmount5(String[] kaisyuAmount5) {
        this.kaisyuAmount5 = kaisyuAmount5;
    }

    public String[] getKaisyuAmount6() {
        return this.kaisyuAmount6;
    }

    public void setKaisyuAmount6(String[] kaisyuAmount6) {
        this.kaisyuAmount6 = kaisyuAmount6;
    }

    public String[] getKaisyuAmount7() {
        return this.kaisyuAmount7;
    }

    public void setKaisyuAmount7(String[] kaisyuAmount7) {
        this.kaisyuAmount7 = kaisyuAmount7;
    }

    public String[] getKaisyuAmount8() {
        return this.kaisyuAmount8;
    }

    public void setKaisyuAmount8(String[] kaisyuAmount8) {
        this.kaisyuAmount8 = kaisyuAmount8;
    }

    public String[] getKaisyuAmount9() {
        return this.kaisyuAmount9;
    }

    public void setKaisyuAmount9(String[] kaisyuAmount9) {
        this.kaisyuAmount9 = kaisyuAmount9;
    }

    public String[] getKaisyuAmount10() {
        return this.kaisyuAmount10;
    }

    public void setKaisyuAmount10(String[] kaisyuAmount10) {
        this.kaisyuAmount10 = kaisyuAmount10;
    }

    public String[] getKaisyuAmount11() {
        return this.kaisyuAmount11;
    }

    public void setKaisyuAmount11(String[] kaisyuAmount11) {
        this.kaisyuAmount11 = kaisyuAmount11;
    }

    public String[] getKaisyuAmount12() {
        return this.kaisyuAmount12;
    }

    public void setKaisyuAmount12(String[] kaisyuAmount12) {
        this.kaisyuAmount12 = kaisyuAmount12;
    }

    public String[] getKaisyuAmountTm() {
        return this.kaisyuAmountTm;
    }

    public void setKaisyuAmountTm(String[] kaisyuAmountTm) {
        this.kaisyuAmountTm = kaisyuAmountTm;
    }

    public String[] getInpTargetKeiyakuAmountUpdateFlg1() {
        return this.inpTargetKeiyakuAmountUpdateFlg1;
    }

    public void setInpTargetKeiyakuAmountUpdateFlg1(String[] inpTargetKeiyakuAmountUpdateFlg1) {
        this.inpTargetKeiyakuAmountUpdateFlg1 = inpTargetKeiyakuAmountUpdateFlg1;
    }

    public String[] getInpTargetKeiyakuAmountUpdateFlg2() {
        return this.inpTargetKeiyakuAmountUpdateFlg2;
    }

    public void setInpTargetKeiyakuAmountUpdateFlg2(String[] inpTargetKeiyakuAmountUpdateFlg2) {
        this.inpTargetKeiyakuAmountUpdateFlg2 = inpTargetKeiyakuAmountUpdateFlg2;
    }

    public String[] getInpTargetKeiyakuAmountUpdateFlg3() {
        return this.inpTargetKeiyakuAmountUpdateFlg3;
    }

    public void setInpTargetKeiyakuAmountUpdateFlg3(String[] inpTargetKeiyakuAmountUpdateFlg3) {
        this.inpTargetKeiyakuAmountUpdateFlg3 = inpTargetKeiyakuAmountUpdateFlg3;
    }

    public String[] getInpTargetKeiyakuAmountUpdateFlg4() {
        return this.inpTargetKeiyakuAmountUpdateFlg4;
    }

    public void setInpTargetKeiyakuAmountUpdateFlg4(String[] inpTargetKeiyakuAmountUpdateFlg4) {
        this.inpTargetKeiyakuAmountUpdateFlg4 = inpTargetKeiyakuAmountUpdateFlg4;
    }

    public String[] getInpTargetKeiyakuAmountUpdateFlg5() {
        return this.inpTargetKeiyakuAmountUpdateFlg5;
    }

    public void setInpTargetKeiyakuAmountUpdateFlg5(String[] inpTargetKeiyakuAmountUpdateFlg5) {
        this.inpTargetKeiyakuAmountUpdateFlg5 = inpTargetKeiyakuAmountUpdateFlg5;
    }

    public String[] getInpTargetKeiyakuAmountUpdateFlg6() {
        return this.inpTargetKeiyakuAmountUpdateFlg6;
    }

    public void setInpTargetKeiyakuAmountUpdateFlg6(String[] inpTargetKeiyakuAmountUpdateFlg6) {
        this.inpTargetKeiyakuAmountUpdateFlg6 = inpTargetKeiyakuAmountUpdateFlg6;
    }

    public String[] getInpTargetKeiyakuAmountUpdateFlg7() {
        return this.inpTargetKeiyakuAmountUpdateFlg7;
    }

    public void setInpTargetKeiyakuAmountUpdateFlg7(String[] inpTargetKeiyakuAmountUpdateFlg7) {
        this.inpTargetKeiyakuAmountUpdateFlg7 = inpTargetKeiyakuAmountUpdateFlg7;
    }

    public String[] getInpTargetKeiyakuAmountUpdateFlg8() {
        return this.inpTargetKeiyakuAmountUpdateFlg8;
    }

    public void setInpTargetKeiyakuAmountUpdateFlg8(String[] inpTargetKeiyakuAmountUpdateFlg8) {
        this.inpTargetKeiyakuAmountUpdateFlg8 = inpTargetKeiyakuAmountUpdateFlg8;
    }

    public String[] getInpTargetKeiyakuAmountUpdateFlg9() {
        return this.inpTargetKeiyakuAmountUpdateFlg9;
    }

    public void setInpTargetKeiyakuAmountUpdateFlg9(String[] inpTargetKeiyakuAmountUpdateFlg9) {
        this.inpTargetKeiyakuAmountUpdateFlg9 = inpTargetKeiyakuAmountUpdateFlg9;
    }

    public String[] getInpTargetKeiyakuAmountUpdateFlg10() {
        return this.inpTargetKeiyakuAmountUpdateFlg10;
    }

    public void setInpTargetKeiyakuAmountUpdateFlg10(String[] inpTargetKeiyakuAmountUpdateFlg10) {
        this.inpTargetKeiyakuAmountUpdateFlg10 = inpTargetKeiyakuAmountUpdateFlg10;
    }

    public String[] getInpTargetKeiyakuAmountUpdateFlg11() {
        return this.inpTargetKeiyakuAmountUpdateFlg11;
    }

    public void setInpTargetKeiyakuAmountUpdateFlg11(String[] inpTargetKeiyakuAmountUpdateFlg11) {
        this.inpTargetKeiyakuAmountUpdateFlg11 = inpTargetKeiyakuAmountUpdateFlg11;
    }

    public String[] getInpTargetKeiyakuAmountUpdateFlg12() {
        return this.inpTargetKeiyakuAmountUpdateFlg12;
    }

    public void setInpTargetKeiyakuAmountUpdateFlg12(String[] inpTargetKeiyakuAmountUpdateFlg12) {
        this.inpTargetKeiyakuAmountUpdateFlg12 = inpTargetKeiyakuAmountUpdateFlg12;
    }

    public String getInpTargetMiNetUpdateFlg1() {
        return this.inpTargetMiNetUpdateFlg1;
    }

    public void setInpTargetMiNetUpdateFlg1(String inpTargetMiNetUpdateFlg1) {
        this.inpTargetMiNetUpdateFlg1 = inpTargetMiNetUpdateFlg1;
    }

    public String getInpTargetMiNetUpdateFlg2() {
        return this.inpTargetMiNetUpdateFlg2;
    }

    public void setInpTargetMiNetUpdateFlg2(String inpTargetMiNetUpdateFlg2) {
        this.inpTargetMiNetUpdateFlg2 = inpTargetMiNetUpdateFlg2;
    }

    public String getInpTargetMiNetUpdateFlg3() {
        return this.inpTargetMiNetUpdateFlg3;
    }

    public void setInpTargetMiNetUpdateFlg3(String inpTargetMiNetUpdateFlg3) {
        this.inpTargetMiNetUpdateFlg3 = inpTargetMiNetUpdateFlg3;
    }

    public String getInpTargetMiNetUpdateFlg4() {
        return this.inpTargetMiNetUpdateFlg4;
    }

    public void setInpTargetMiNetUpdateFlg4(String inpTargetMiNetUpdateFlg4) {
        this.inpTargetMiNetUpdateFlg4 = inpTargetMiNetUpdateFlg4;
    }

    public String getInpTargetMiNetUpdateFlg5() {
        return this.inpTargetMiNetUpdateFlg5;
    }

    public void setInpTargetMiNetUpdateFlg5(String inpTargetMiNetUpdateFlg5) {
        this.inpTargetMiNetUpdateFlg5 = inpTargetMiNetUpdateFlg5;
    }

    public String getInpTargetMiNetUpdateFlg6() {
        return this.inpTargetMiNetUpdateFlg6;
    }

    public void setInpTargetMiNetUpdateFlg6(String inpTargetMiNetUpdateFlg6) {
        this.inpTargetMiNetUpdateFlg6 = inpTargetMiNetUpdateFlg6;
    }

    public String getInpTargetMiNetUpdateFlg7() {
        return this.inpTargetMiNetUpdateFlg7;
    }

    public void setInpTargetMiNetUpdateFlg7(String inpTargetMiNetUpdateFlg7) {
        this.inpTargetMiNetUpdateFlg7 = inpTargetMiNetUpdateFlg7;
    }

    public String getInpTargetMiNetUpdateFlg8() {
        return this.inpTargetMiNetUpdateFlg8;
    }

    public void setInpTargetMiNetUpdateFlg8(String inpTargetMiNetUpdateFlg8) {
        this.inpTargetMiNetUpdateFlg8 = inpTargetMiNetUpdateFlg8;
    }

    public String getInpTargetMiNetUpdateFlg9() {
        return this.inpTargetMiNetUpdateFlg9;
    }

    public void setInpTargetMiNetUpdateFlg9(String inpTargetMiNetUpdateFlg9) {
        this.inpTargetMiNetUpdateFlg9 = inpTargetMiNetUpdateFlg9;
    }

    public String getInpTargetMiNetUpdateFlg10() {
        return this.inpTargetMiNetUpdateFlg10;
    }

    public void setInpTargetMiNetUpdateFlg10(String inpTargetMiNetUpdateFlg10) {
        this.inpTargetMiNetUpdateFlg10 = inpTargetMiNetUpdateFlg10;
    }

    public String getInpTargetMiNetUpdateFlg11() {
        return this.inpTargetMiNetUpdateFlg11;
    }

    public void setInpTargetMiNetUpdateFlg11(String inpTargetMiNetUpdateFlg11) {
        this.inpTargetMiNetUpdateFlg11 = inpTargetMiNetUpdateFlg11;
    }

    public String getInpTargetMiNetUpdateFlg12() {
        return this.inpTargetMiNetUpdateFlg12;
    }

    public void setInpTargetMiNetUpdateFlg12(String inpTargetMiNetUpdateFlg12) {
        this.inpTargetMiNetUpdateFlg12 = inpTargetMiNetUpdateFlg12;
    }

    public String getInpTargetSeibanSonekiNetUpdateFlg1() {
        return this.inpTargetSeibanSonekiNetUpdateFlg1;
    }

    public void setInpTargetSeibanSonekiNetUpdateFlg1(String inpTargetSeibanSonekiNetUpdateFlg1) {
        this.inpTargetSeibanSonekiNetUpdateFlg1 = inpTargetSeibanSonekiNetUpdateFlg1;
    }

    public String getInpTargetSeibanSonekiNetUpdateFlg2() {
        return this.inpTargetSeibanSonekiNetUpdateFlg2;
    }

    public void setInpTargetSeibanSonekiNetUpdateFlg2(String inpTargetSeibanSonekiNetUpdateFlg2) {
        this.inpTargetSeibanSonekiNetUpdateFlg2 = inpTargetSeibanSonekiNetUpdateFlg2;
    }

    public String getInpTargetSeibanSonekiNetUpdateFlg3() {
        return this.inpTargetSeibanSonekiNetUpdateFlg3;
    }

    public void setInpTargetSeibanSonekiNetUpdateFlg3(String inpTargetSeibanSonekiNetUpdateFlg3) {
        this.inpTargetSeibanSonekiNetUpdateFlg3 = inpTargetSeibanSonekiNetUpdateFlg3;
    }

    public String getInpTargetSeibanSonekiNetUpdateFlg4() {
        return this.inpTargetSeibanSonekiNetUpdateFlg4;
    }

    public void setInpTargetSeibanSonekiNetUpdateFlg4(String inpTargetSeibanSonekiNetUpdateFlg4) {
        this.inpTargetSeibanSonekiNetUpdateFlg4 = inpTargetSeibanSonekiNetUpdateFlg4;
    }

    public String getInpTargetSeibanSonekiNetUpdateFlg5() {
        return this.inpTargetSeibanSonekiNetUpdateFlg5;
    }

    public void setInpTargetSeibanSonekiNetUpdateFlg5(String inpTargetSeibanSonekiNetUpdateFlg5) {
        this.inpTargetSeibanSonekiNetUpdateFlg5 = inpTargetSeibanSonekiNetUpdateFlg5;
    }

    public String getInpTargetSeibanSonekiNetUpdateFlg6() {
        return this.inpTargetSeibanSonekiNetUpdateFlg6;
    }

    public void setInpTargetSeibanSonekiNetUpdateFlg6(String inpTargetSeibanSonekiNetUpdateFlg6) {
        this.inpTargetSeibanSonekiNetUpdateFlg6 = inpTargetSeibanSonekiNetUpdateFlg6;
    }

    public String getInpTargetSeibanSonekiNetUpdateFlg7() {
        return this.inpTargetSeibanSonekiNetUpdateFlg7;
    }

    public void setInpTargetSeibanSonekiNetUpdateFlg7(String inpTargetSeibanSonekiNetUpdateFlg7) {
        this.inpTargetSeibanSonekiNetUpdateFlg7 = inpTargetSeibanSonekiNetUpdateFlg7;
    }

    public String getInpTargetSeibanSonekiNetUpdateFlg8() {
        return this.inpTargetSeibanSonekiNetUpdateFlg8;
    }

    public void setInpTargetSeibanSonekiNetUpdateFlg8(String inpTargetSeibanSonekiNetUpdateFlg8) {
        this.inpTargetSeibanSonekiNetUpdateFlg8 = inpTargetSeibanSonekiNetUpdateFlg8;
    }

    public String getInpTargetSeibanSonekiNetUpdateFlg9() {
        return this.inpTargetSeibanSonekiNetUpdateFlg9;
    }

    public void setInpTargetSeibanSonekiNetUpdateFlg9(String inpTargetSeibanSonekiNetUpdateFlg9) {
        this.inpTargetSeibanSonekiNetUpdateFlg9 = inpTargetSeibanSonekiNetUpdateFlg9;
    }

    public String getInpTargetSeibanSonekiNetUpdateFlg10() {
        return this.inpTargetSeibanSonekiNetUpdateFlg10;
    }

    public void setInpTargetSeibanSonekiNetUpdateFlg10(String inpTargetSeibanSonekiNetUpdateFlg10) {
        this.inpTargetSeibanSonekiNetUpdateFlg10 = inpTargetSeibanSonekiNetUpdateFlg10;
    }

    public String getInpTargetSeibanSonekiNetUpdateFlg11() {
        return this.inpTargetSeibanSonekiNetUpdateFlg11;
    }

    public void setInpTargetSeibanSonekiNetUpdateFlg11(String inpTargetSeibanSonekiNetUpdateFlg11) {
        this.inpTargetSeibanSonekiNetUpdateFlg11 = inpTargetSeibanSonekiNetUpdateFlg11;
    }

    public String getInpTargetSeibanSonekiNetUpdateFlg12() {
        return this.inpTargetSeibanSonekiNetUpdateFlg12;
    }

    public void setInpTargetSeibanSonekiNetUpdateFlg12(String inpTargetSeibanSonekiNetUpdateFlg12) {
        this.inpTargetSeibanSonekiNetUpdateFlg12 = inpTargetSeibanSonekiNetUpdateFlg12;
    }

    public String[] getInpTargetNetUpdateFlg1() {
        return this.inpTargetNetUpdateFlg1;
    }

    public void setInpTargetNetUpdateFlg1(String[] inpTargetNetUpdateFlg1) {
        this.inpTargetNetUpdateFlg1 = inpTargetNetUpdateFlg1;
    }

    public String[] getInpTargetNetUpdateFlg2() {
        return this.inpTargetNetUpdateFlg2;
    }

    public void setInpTargetNetUpdateFlg2(String[] inpTargetNetUpdateFlg2) {
        this.inpTargetNetUpdateFlg2 = inpTargetNetUpdateFlg2;
    }

    public String[] getInpTargetNetUpdateFlg3() {
        return this.inpTargetNetUpdateFlg3;
    }

    public void setInpTargetNetUpdateFlg3(String[] inpTargetNetUpdateFlg3) {
        this.inpTargetNetUpdateFlg3 = inpTargetNetUpdateFlg3;
    }

    public String[] getInpTargetNetUpdateFlg4() {
        return this.inpTargetNetUpdateFlg4;
    }

    public void setInpTargetNetUpdateFlg4(String[] inpTargetNetUpdateFlg4) {
        this.inpTargetNetUpdateFlg4 = inpTargetNetUpdateFlg4;
    }

    public String[] getInpTargetNetUpdateFlg5() {
        return this.inpTargetNetUpdateFlg5;
    }

    public void setInpTargetNetUpdateFlg5(String[] inpTargetNetUpdateFlg5) {
        this.inpTargetNetUpdateFlg5 = inpTargetNetUpdateFlg5;
    }

    public String[] getInpTargetNetUpdateFlg6() {
        return this.inpTargetNetUpdateFlg6;
    }

    public void setInpTargetNetUpdateFlg6(String[] inpTargetNetUpdateFlg6) {
        this.inpTargetNetUpdateFlg6 = inpTargetNetUpdateFlg6;
    }

    public String[] getInpTargetNetUpdateFlg7() {
        return this.inpTargetNetUpdateFlg7;
    }

    public void setInpTargetNetUpdateFlg7(String[] inpTargetNetUpdateFlg7) {
        this.inpTargetNetUpdateFlg7 = inpTargetNetUpdateFlg7;
    }

    public String[] getInpTargetNetUpdateFlg8() {
        return this.inpTargetNetUpdateFlg8;
    }

    public void setInpTargetNetUpdateFlg8(String[] inpTargetNetUpdateFlg8) {
        this.inpTargetNetUpdateFlg8 = inpTargetNetUpdateFlg8;
    }

    public String[] getInpTargetNetUpdateFlg9() {
        return this.inpTargetNetUpdateFlg9;
    }

    public void setInpTargetNetUpdateFlg9(String[] inpTargetNetUpdateFlg9) {
        this.inpTargetNetUpdateFlg9 = inpTargetNetUpdateFlg9;
    }

    public String[] getInpTargetNetUpdateFlg10() {
        return this.inpTargetNetUpdateFlg10;
    }

    public void setInpTargetNetUpdateFlg10(String[] inpTargetNetUpdateFlg10) {
        this.inpTargetNetUpdateFlg10 = inpTargetNetUpdateFlg10;
    }

    public String[] getInpTargetNetUpdateFlg11() {
        return this.inpTargetNetUpdateFlg11;
    }

    public void setInpTargetNetUpdateFlg11(String[] inpTargetNetUpdateFlg11) {
        this.inpTargetNetUpdateFlg11 = inpTargetNetUpdateFlg11;
    }

    public String[] getInpTargetNetUpdateFlg12() {
        return this.inpTargetNetUpdateFlg12;
    }

    public void setInpTargetNetUpdateFlg12(String[] inpTargetNetUpdateFlg12) {
        this.inpTargetNetUpdateFlg12 = inpTargetNetUpdateFlg12;
    }

    public String[] getInpTargetKaisyuAmountUpdateFlg1() {
        return this.inpTargetKaisyuAmountUpdateFlg1;
    }

    public void setInpTargetKaisyuAmountUpdateFlg1(String[] inpTargetKaisyuAmountUpdateFlg1) {
        this.inpTargetKaisyuAmountUpdateFlg1 = inpTargetKaisyuAmountUpdateFlg1;
    }

    public String[] getInpTargetKaisyuAmountUpdateFlg2() {
        return this.inpTargetKaisyuAmountUpdateFlg2;
    }

    public void setInpTargetKaisyuAmountUpdateFlg2(String[] inpTargetKaisyuAmountUpdateFlg2) {
        this.inpTargetKaisyuAmountUpdateFlg2 = inpTargetKaisyuAmountUpdateFlg2;
    }

    public String[] getInpTargetKaisyuAmountUpdateFlg3() {
        return this.inpTargetKaisyuAmountUpdateFlg3;
    }

    public void setInpTargetKaisyuAmountUpdateFlg3(String[] inpTargetKaisyuAmountUpdateFlg3) {
        this.inpTargetKaisyuAmountUpdateFlg3 = inpTargetKaisyuAmountUpdateFlg3;
    }

    public String[] getInpTargetKaisyuAmountUpdateFlg4() {
        return this.inpTargetKaisyuAmountUpdateFlg4;
    }

    public void setInpTargetKaisyuAmountUpdateFlg4(String[] inpTargetKaisyuAmountUpdateFlg4) {
        this.inpTargetKaisyuAmountUpdateFlg4 = inpTargetKaisyuAmountUpdateFlg4;
    }

    public String[] getInpTargetKaisyuAmountUpdateFlg5() {
        return this.inpTargetKaisyuAmountUpdateFlg5;
    }

    public void setInpTargetKaisyuAmountUpdateFlg5(String[] inpTargetKaisyuAmountUpdateFlg5) {
        this.inpTargetKaisyuAmountUpdateFlg5 = inpTargetKaisyuAmountUpdateFlg5;
    }

    public String[] getInpTargetKaisyuAmountUpdateFlg6() {
        return this.inpTargetKaisyuAmountUpdateFlg6;
    }

    public void setInpTargetKaisyuAmountUpdateFlg6(String[] inpTargetKaisyuAmountUpdateFlg6) {
        this.inpTargetKaisyuAmountUpdateFlg6 = inpTargetKaisyuAmountUpdateFlg6;
    }

    public String[] getInpTargetKaisyuAmountUpdateFlg7() {
        return this.inpTargetKaisyuAmountUpdateFlg7;
    }

    public void setInpTargetKaisyuAmountUpdateFlg7(String[] inpTargetKaisyuAmountUpdateFlg7) {
        this.inpTargetKaisyuAmountUpdateFlg7 = inpTargetKaisyuAmountUpdateFlg7;
    }

    public String[] getInpTargetKaisyuAmountUpdateFlg8() {
        return this.inpTargetKaisyuAmountUpdateFlg8;
    }

    public void setInpTargetKaisyuAmountUpdateFlg8(String[] inpTargetKaisyuAmountUpdateFlg8) {
        this.inpTargetKaisyuAmountUpdateFlg8 = inpTargetKaisyuAmountUpdateFlg8;
    }

    public String[] getInpTargetKaisyuAmountUpdateFlg9() {
        return this.inpTargetKaisyuAmountUpdateFlg9;
    }

    public void setInpTargetKaisyuAmountUpdateFlg9(String[] inpTargetKaisyuAmountUpdateFlg9) {
        this.inpTargetKaisyuAmountUpdateFlg9 = inpTargetKaisyuAmountUpdateFlg9;
    }

    public String[] getInpTargetKaisyuAmountUpdateFlg10() {
        return this.inpTargetKaisyuAmountUpdateFlg10;
    }

    public void setInpTargetKaisyuAmountUpdateFlg10(String[] inpTargetKaisyuAmountUpdateFlg10) {
        this.inpTargetKaisyuAmountUpdateFlg10 = inpTargetKaisyuAmountUpdateFlg10;
    }

    public String[] getInpTargetKaisyuAmountUpdateFlg11() {
        return this.inpTargetKaisyuAmountUpdateFlg11;
    }

    public void setInpTargetKaisyuAmountUpdateFlg11(String[] inpTargetKaisyuAmountUpdateFlg11) {
        this.inpTargetKaisyuAmountUpdateFlg11 = inpTargetKaisyuAmountUpdateFlg11;
    }

    public String[] getInpTargetKaisyuAmountUpdateFlg12() {
        return this.inpTargetKaisyuAmountUpdateFlg12;
    }

    public void setInpTargetKaisyuAmountUpdateFlg12(String[] inpTargetKaisyuAmountUpdateFlg12) {
        this.inpTargetKaisyuAmountUpdateFlg12 = inpTargetKaisyuAmountUpdateFlg12;
    }

    public String[] getInpTargetKeiyakuCurrencyCode() {
        return this.inpTargetKeiyakuCurrencyCode;
    }

    public void setInpTargetKeiyakuCurrencyCode(String[] inpTargetKeiyakuCurrencyCode) {
        this.inpTargetKeiyakuCurrencyCode = inpTargetKeiyakuCurrencyCode;
    }

    public String[] getInpTargetNetCategoryKbn1() {
        return this.inpTargetNetCategoryKbn1;
    }

    public void setInpTargetNetCategoryKbn1(String[] inpTargetNetCategoryKbn1) {
        this.inpTargetNetCategoryKbn1 = inpTargetNetCategoryKbn1;
    }

    public String[] getInpTargetNetCategoryKbn2() {
        return this.inpTargetNetCategoryKbn2;
    }

    public void setInpTargetNetCategoryKbn2(String[] inpTargetNetCategoryKbn2) {
        this.inpTargetNetCategoryKbn2 = inpTargetNetCategoryKbn2;
    }

    public String[] getInpTargetNetCategoryCode() {
        return this.inpTargetNetCategoryCode;
    }

    public void setInpTargetNetCategoryCode(String[] inpTargetNetCategoryCode) {
        this.inpTargetNetCategoryCode = inpTargetNetCategoryCode;
    }

    public String[] getInpTargetKaisyuAmountCurrencyCode() {
        return this.inpTargetKaisyuAmountCurrencyCode;
    }

    public void setInpTargetKaisyuAmountCurrencyCode(String[] inpTargetKaisyuAmountCurrencyCode) {
        this.inpTargetKaisyuAmountCurrencyCode = inpTargetKaisyuAmountCurrencyCode;
    }

    public List<boolean[]> getKeiyakuAmountErrorList() {
        return this.keiyakuAmountErrorList;
    }

    public void setKeiyakuAmountErrorList(List<boolean[]> keiyakuAmountErrorList) {
        this.keiyakuAmountErrorList = keiyakuAmountErrorList;
    }

    public List<Boolean> getMiNetErrorList() {
        return this.miNetErrorList;
    }

    public void setMiNetErrorList(List<Boolean> miNetErrorList) {
        this.miNetErrorList = miNetErrorList;
    }

    public List<Boolean> getSeibanSonekiErrorList() {
        return this.seibanSonekiErrorList;
    }

    public void setSeibanSonekiErrorList(List<Boolean> seibanSonekiErrorList) {
        this.seibanSonekiErrorList = seibanSonekiErrorList;
    }

    public List<boolean[]> getNetErrorList() {
        return this.netErrorList;
    }

    public void setNetErrorList(List<boolean[]> netErrorList) {
        this.netErrorList = netErrorList;
    }

    public List<boolean[]> getKaisyuAmountErrorList() {
        return this.kaisyuAmountErrorList;
    }

    public void setKaisyuAmountErrorList(List<boolean[]> kaisyuAmountErrorList) {
        this.kaisyuAmountErrorList = kaisyuAmountErrorList;
    }

    public void setSabunHaneiFlg(String sabunHaneiFlg) {
        this.sabunHaneiFlg = sabunHaneiFlg;
    }

    public String getSabunHaneiFlg() {
        return this.sabunHaneiFlg;
    }

    public void setEditFlg(String editFlg) {
        this.editFlg = editFlg;
    }

    public String getEditFlg() {
        return this.editFlg;
    }

    public String[] getKikanFromAry() {
        return kikanFromAry;
    }

    public void setKikanFromAry(String[] kikanFromAry) {
        this.kikanFromAry = kikanFromAry;
    }

    public String[] getKikanToAry() {
        return kikanToAry;
    }

    public void setKikanToAry(String[] kikanToAry) {
        this.kikanToAry = kikanToAry;
    }

    public int getAnkenInputFlg() {
        return ankenInputFlg;
    }

    public void setAnkenInputFlg(int ankenInputFlg) {
        this.ankenInputFlg = ankenInputFlg;
    }

    public ArrayList<Map<String, String>> getTitle() {
        return title;
    }

    public void setTitle(ArrayList<Map<String, String>> title) {
        this.title = title;
    }

    public String getArari1Q() {
        return arari1Q;
    }

    public void setArari1Q(String arari1Q) {
        this.arari1Q = arari1Q;
    }

    public String getArari1QDiff() {
        return arari1QDiff;
    }

    public void setArari1QDiff(String arari1QDiff) {
        this.arari1QDiff = arari1QDiff;
    }

    public String getArari2Q() {
        return arari2Q;
    }

    public void setArari2Q(String arari2Q) {
        this.arari2Q = arari2Q;
    }

    public String getArari2QDiff() {
        return arari2QDiff;
    }

    public void setArari2QDiff(String arari2QDiff) {
        this.arari2QDiff = arari2QDiff;
    }

    public String getArari3Q() {
        return arari3Q;
    }

    public void setArari3Q(String arari3Q) {
        this.arari3Q = arari3Q;
    }

    public String getArari3QDiff() {
        return arari3QDiff;
    }

    public void setArari3QDiff(String arari3QDiff) {
        this.arari3QDiff = arari3QDiff;
    }

    public String getArari4Q() {
        return arari4Q;
    }

    public void setArari4Q(String arari4Q) {
        this.arari4Q = arari4Q;
    }

    public String getArari4QDiff() {
        return arari4QDiff;
    }

    public void setArari4QDiff(String arari4QDiff) {
        this.arari4QDiff = arari4QDiff;
    }

    public String getArariRuikei1Q() {
        return arariRuikei1Q;
    }

    public void setArariRuikei1Q(String arariRuikei1Q) {
        this.arariRuikei1Q = arariRuikei1Q;
    }

    public String getArariRuikei1QDiff() {
        return arariRuikei1QDiff;
    }

    public void setArariRuikei1QDiff(String arariRuikei1QDiff) {
        this.arariRuikei1QDiff = arariRuikei1QDiff;
    }

    public String getArariRuikei2Q() {
        return arariRuikei2Q;
    }

    public void setArariRuikei2Q(String arariRuikei2Q) {
        this.arariRuikei2Q = arariRuikei2Q;
    }

    public String getArariRuikei2QDiff() {
        return arariRuikei2QDiff;
    }

    public void setArariRuikei2QDiff(String arariRuikei2QDiff) {
        this.arariRuikei2QDiff = arariRuikei2QDiff;
    }

    public String getArariRuikei3Q() {
        return arariRuikei3Q;
    }

    public void setArariRuikei3Q(String arariRuikei3Q) {
        this.arariRuikei3Q = arariRuikei3Q;
    }

    public String getArariRuikei3QDiff() {
        return arariRuikei3QDiff;
    }

    public void setArariRuikei3QDiff(String arariRuikei3QDiff) {
        this.arariRuikei3QDiff = arariRuikei3QDiff;
    }

    public String getArariRuikei4Q() {
        return arariRuikei4Q;
    }

    public void setArariRuikei4Q(String arariRuikei4Q) {
        this.arariRuikei4Q = arariRuikei4Q;
    }

    public String getArariRuikei4QDiff() {
        return arariRuikei4QDiff;
    }

    public void setArariRuikei4QDiff(String arariRuikei4QDiff) {
        this.arariRuikei4QDiff = arariRuikei4QDiff;
    }

    public String getMrate1Q() {
        return mrate1Q;
    }

    public void setMrate1Q(String mrate1Q) {
        this.mrate1Q = mrate1Q;
    }

    public String getMrate2Q() {
        return mrate2Q;
    }

    public void setMrate2Q(String mrate2Q) {
        this.mrate2Q = mrate2Q;
    }

    public String getMrate3Q() {
        return mrate3Q;
    }

    public void setMrate3Q(String mrate3Q) {
        this.mrate3Q = mrate3Q;
    }

    public String getMrate4Q() {
        return mrate4Q;
    }

    public void setMrate4Q(String mrate4Q) {
        this.mrate4Q = mrate4Q;
    }

    public String getMrate1QDiff() {
        return mrate1QDiff;
    }

    public void setMrate1QDiff(String mrate1QDiff) {
        this.mrate1QDiff = mrate1QDiff;
    }

    public String getMrate2QDiff() {
        return mrate2QDiff;
    }

    public void setMrate2QDiff(String mrate2QDiff) {
        this.mrate2QDiff = mrate2QDiff;
    }

    public String getMrate3QDiff() {
        return mrate3QDiff;
    }

    public void setMrate3QDiff(String mrate3QDiff) {
        this.mrate3QDiff = mrate3QDiff;
    }

    public String getMrate4QDiff() {
        return mrate4QDiff;
    }

    public void setMrate4QDiff(String mrate4QDiff) {
        this.mrate4QDiff = mrate4QDiff;
    }

    public String getMrateRuikei1Q() {
        return mrateRuikei1Q;
    }

    public void setMrateRuikei1Q(String mrateRuikei1Q) {
        this.mrateRuikei1Q = mrateRuikei1Q;
    }

    public String getMrateRuikei2Q() {
        return mrateRuikei2Q;
    }

    public void setMrateRuikei2Q(String mrateRuikei2Q) {
        this.mrateRuikei2Q = mrateRuikei2Q;
    }

    public String getMrateRuikei3Q() {
        return mrateRuikei3Q;
    }

    public void setMrateRuikei3Q(String mrateRuikei3Q) {
        this.mrateRuikei3Q = mrateRuikei3Q;
    }

    public String getMrateRuikei4Q() {
        return mrateRuikei4Q;
    }

    public void setMrateRuikei4Q(String mrateRuikei4Q) {
        this.mrateRuikei4Q = mrateRuikei4Q;
    }

    public String getMrateRuikei1QDiff() {
        return mrateRuikei1QDiff;
    }

    public void setMrateRuikei1QDiff(String mrateRuikei1QDiff) {
        this.mrateRuikei1QDiff = mrateRuikei1QDiff;
    }

    public String getMrateRuikei2QDiff() {
        return mrateRuikei2QDiff;
    }

    public void setMrateRuikei2QDiff(String mrateRuikei2QDiff) {
        this.mrateRuikei2QDiff = mrateRuikei2QDiff;
    }

    public String getMrateRuikei3QDiff() {
        return mrateRuikei3QDiff;
    }

    public void setMrateRuikei3QDiff(String mrateRuikei3QDiff) {
        this.mrateRuikei3QDiff = mrateRuikei3QDiff;
    }

    public String getMrateRuikei4QDiff() {
        return mrateRuikei4QDiff;
    }

    public void setMrateRuikei4QDiff(String mrateRuikei4QDiff) {
        this.mrateRuikei4QDiff = mrateRuikei4QDiff;
    }

    public String getBikou() {
        return bikou;
    }

    public void setBikou(String bikou) {
        this.bikou = bikou;
    }

    /**
     * @return the kawaseEikyo1
     */
    public String getKawaseEikyo1() {
        return kawaseEikyo1;
    }

    /**
     * @param kawaseEikyo1 the kawaseEikyo1 to set
     */
    public void setKawaseEikyo1(String kawaseEikyo1) {
        this.kawaseEikyo1 = kawaseEikyo1;
    }

    /**
     * @return the kawaseEikyo2
     */
    public String getKawaseEikyo2() {
        return kawaseEikyo2;
    }

    /**
     * @param kawaseEikyo2 the kawaseEikyo2 to set
     */
    public void setKawaseEikyo2(String kawaseEikyo2) {
        this.kawaseEikyo2 = kawaseEikyo2;
    }

    /**
     * @return the kawaseEikyo3
     */
    public String getKawaseEikyo3() {
        return kawaseEikyo3;
    }

    /**
     * @param kawaseEikyo3 the kawaseEikyo3 to set
     */
    public void setKawaseEikyo3(String kawaseEikyo3) {
        this.kawaseEikyo3 = kawaseEikyo3;
    }

    /**
     * @return the kawaseEikyo4
     */
    public String getKawaseEikyo4() {
        return kawaseEikyo4;
    }

    /**
     * @param kawaseEikyo4 the kawaseEikyo4 to set
     */
    public void setKawaseEikyo4(String kawaseEikyo4) {
        this.kawaseEikyo4 = kawaseEikyo4;
    }

    /**
     * @return the kawaseEikyo5
     */
    public String getKawaseEikyo5() {
        return kawaseEikyo5;
    }

    /**
     * @param kawaseEikyo5 the kawaseEikyo5 to set
     */
    public void setKawaseEikyo5(String kawaseEikyo5) {
        this.kawaseEikyo5 = kawaseEikyo5;
    }

    /**
     * @return the kawaseEikyo6
     */
    public String getKawaseEikyo6() {
        return kawaseEikyo6;
    }

    /**
     * @param kawaseEikyo6 the kawaseEikyo6 to set
     */
    public void setKawaseEikyo6(String kawaseEikyo6) {
        this.kawaseEikyo6 = kawaseEikyo6;
    }

    /**
     * @return the kawaseEikyo7
     */
    public String getKawaseEikyo7() {
        return kawaseEikyo7;
    }

    /**
     * @param kawaseEikyo7 the kawaseEikyo7 to set
     */
    public void setKawaseEikyo7(String kawaseEikyo7) {
        this.kawaseEikyo7 = kawaseEikyo7;
    }

    /**
     * @return the kawaseEikyo8
     */
    public String getKawaseEikyo8() {
        return kawaseEikyo8;
    }

    /**
     * @param kawaseEikyo8 the kawaseEikyo8 to set
     */
    public void setKawaseEikyo8(String kawaseEikyo8) {
        this.kawaseEikyo8 = kawaseEikyo8;
    }

    /**
     * @return the kawaseEikyo9
     */
    public String getKawaseEikyo9() {
        return kawaseEikyo9;
    }

    /**
     * @param kawaseEikyo9 the kawaseEikyo9 to set
     */
    public void setKawaseEikyo9(String kawaseEikyo9) {
        this.kawaseEikyo9 = kawaseEikyo9;
    }

    /**
     * @return the kawaseEikyo10
     */
    public String getKawaseEikyo10() {
        return kawaseEikyo10;
    }

    /**
     * @param kawaseEikyo10 the kawaseEikyo10 to set
     */
    public void setKawaseEikyo10(String kawaseEikyo10) {
        this.kawaseEikyo10 = kawaseEikyo10;
    }

    /**
     * @return the kawaseEikyo11
     */
    public String getKawaseEikyo11() {
        return kawaseEikyo11;
    }

    /**
     * @param kawaseEikyo11 the kawaseEikyo11 to set
     */
    public void setKawaseEikyo11(String kawaseEikyo11) {
        this.kawaseEikyo11 = kawaseEikyo11;
    }

    /**
     * @return the kawaseEikyo12
     */
    public String getKawaseEikyo12() {
        return kawaseEikyo12;
    }

    /**
     * @param kawaseEikyo12 the kawaseEikyo12 to set
     */
    public void setKawaseEikyo12(String kawaseEikyo12) {
        this.kawaseEikyo12 = kawaseEikyo12;
    }

    /**
     * @return the kawaseEikyoTm
     */
    public String getKawaseEikyoTm() {
        return kawaseEikyoTm;
    }

    /**
     * @param kawaseEikyoTm the kawaseEikyoTm to set
     */
    public void setKawaseEikyoTm(String kawaseEikyoTm) {
        this.kawaseEikyoTm = kawaseEikyoTm;
    }

    /**
     * @return the inpTargetKawaseEikyoUpdateFlg1
     */
    public String getInpTargetKawaseEikyoUpdateFlg1() {
        return inpTargetKawaseEikyoUpdateFlg1;
    }

    /**
     * @param inpTargetKawaseEikyoUpdateFlg1 the inpTargetKawaseEikyoUpdateFlg1 to set
     */
    public void setInpTargetKawaseEikyoUpdateFlg1(String inpTargetKawaseEikyoUpdateFlg1) {
        this.inpTargetKawaseEikyoUpdateFlg1 = inpTargetKawaseEikyoUpdateFlg1;
    }

    /**
     * @return the inpTargetKawaseEikyoUpdateFlg2
     */
    public String getInpTargetKawaseEikyoUpdateFlg2() {
        return inpTargetKawaseEikyoUpdateFlg2;
    }

    /**
     * @param inpTargetKawaseEikyoUpdateFlg2 the inpTargetKawaseEikyoUpdateFlg2 to set
     */
    public void setInpTargetKawaseEikyoUpdateFlg2(String inpTargetKawaseEikyoUpdateFlg2) {
        this.inpTargetKawaseEikyoUpdateFlg2 = inpTargetKawaseEikyoUpdateFlg2;
    }

    /**
     * @return the inpTargetKawaseEikyoUpdateFlg3
     */
    public String getInpTargetKawaseEikyoUpdateFlg3() {
        return inpTargetKawaseEikyoUpdateFlg3;
    }

    /**
     * @param inpTargetKawaseEikyoUpdateFlg3 the inpTargetKawaseEikyoUpdateFlg3 to set
     */
    public void setInpTargetKawaseEikyoUpdateFlg3(String inpTargetKawaseEikyoUpdateFlg3) {
        this.inpTargetKawaseEikyoUpdateFlg3 = inpTargetKawaseEikyoUpdateFlg3;
    }

    /**
     * @return the inpTargetKawaseEikyoUpdateFlg4
     */
    public String getInpTargetKawaseEikyoUpdateFlg4() {
        return inpTargetKawaseEikyoUpdateFlg4;
    }

    /**
     * @param inpTargetKawaseEikyoUpdateFlg4 the inpTargetKawaseEikyoUpdateFlg4 to set
     */
    public void setInpTargetKawaseEikyoUpdateFlg4(String inpTargetKawaseEikyoUpdateFlg4) {
        this.inpTargetKawaseEikyoUpdateFlg4 = inpTargetKawaseEikyoUpdateFlg4;
    }

    /**
     * @return the inpTargetKawaseEikyoUpdateFlg5
     */
    public String getInpTargetKawaseEikyoUpdateFlg5() {
        return inpTargetKawaseEikyoUpdateFlg5;
    }

    /**
     * @param inpTargetKawaseEikyoUpdateFlg5 the inpTargetKawaseEikyoUpdateFlg5 to set
     */
    public void setInpTargetKawaseEikyoUpdateFlg5(String inpTargetKawaseEikyoUpdateFlg5) {
        this.inpTargetKawaseEikyoUpdateFlg5 = inpTargetKawaseEikyoUpdateFlg5;
    }

    /**
     * @return the inpTargetKawaseEikyoUpdateFlg6
     */
    public String getInpTargetKawaseEikyoUpdateFlg6() {
        return inpTargetKawaseEikyoUpdateFlg6;
    }

    /**
     * @param inpTargetKawaseEikyoUpdateFlg6 the inpTargetKawaseEikyoUpdateFlg6 to set
     */
    public void setInpTargetKawaseEikyoUpdateFlg6(String inpTargetKawaseEikyoUpdateFlg6) {
        this.inpTargetKawaseEikyoUpdateFlg6 = inpTargetKawaseEikyoUpdateFlg6;
    }

    /**
     * @return the inpTargetKawaseEikyoUpdateFlg7
     */
    public String getInpTargetKawaseEikyoUpdateFlg7() {
        return inpTargetKawaseEikyoUpdateFlg7;
    }

    /**
     * @param inpTargetKawaseEikyoUpdateFlg7 the inpTargetKawaseEikyoUpdateFlg7 to set
     */
    public void setInpTargetKawaseEikyoUpdateFlg7(String inpTargetKawaseEikyoUpdateFlg7) {
        this.inpTargetKawaseEikyoUpdateFlg7 = inpTargetKawaseEikyoUpdateFlg7;
    }

    /**
     * @return the inpTargetKawaseEikyoUpdateFlg8
     */
    public String getInpTargetKawaseEikyoUpdateFlg8() {
        return inpTargetKawaseEikyoUpdateFlg8;
    }

    /**
     * @param inpTargetKawaseEikyoUpdateFlg8 the inpTargetKawaseEikyoUpdateFlg8 to set
     */
    public void setInpTargetKawaseEikyoUpdateFlg8(String inpTargetKawaseEikyoUpdateFlg8) {
        this.inpTargetKawaseEikyoUpdateFlg8 = inpTargetKawaseEikyoUpdateFlg8;
    }

    /**
     * @return the inpTargetKawaseEikyoUpdateFlg9
     */
    public String getInpTargetKawaseEikyoUpdateFlg9() {
        return inpTargetKawaseEikyoUpdateFlg9;
    }

    /**
     * @param inpTargetKawaseEikyoUpdateFlg9 the inpTargetKawaseEikyoUpdateFlg9 to set
     */
    public void setInpTargetKawaseEikyoUpdateFlg9(String inpTargetKawaseEikyoUpdateFlg9) {
        this.inpTargetKawaseEikyoUpdateFlg9 = inpTargetKawaseEikyoUpdateFlg9;
    }

    /**
     * @return the inpTargetKawaseEikyoUpdateFlg10
     */
    public String getInpTargetKawaseEikyoUpdateFlg10() {
        return inpTargetKawaseEikyoUpdateFlg10;
    }

    /**
     * @param inpTargetKawaseEikyoUpdateFlg10 the inpTargetKawaseEikyoUpdateFlg10 to set
     */
    public void setInpTargetKawaseEikyoUpdateFlg10(String inpTargetKawaseEikyoUpdateFlg10) {
        this.inpTargetKawaseEikyoUpdateFlg10 = inpTargetKawaseEikyoUpdateFlg10;
    }

    /**
     * @return the inpTargetKawaseEikyoUpdateFlg11
     */
    public String getInpTargetKawaseEikyoUpdateFlg11() {
        return inpTargetKawaseEikyoUpdateFlg11;
    }

    /**
     * @param inpTargetKawaseEikyoUpdateFlg11 the inpTargetKawaseEikyoUpdateFlg11 to set
     */
    public void setInpTargetKawaseEikyoUpdateFlg11(String inpTargetKawaseEikyoUpdateFlg11) {
        this.inpTargetKawaseEikyoUpdateFlg11 = inpTargetKawaseEikyoUpdateFlg11;
    }

    /**
     * @return the inpTargetKawaseEikyoUpdateFlg12
     */
    public String getInpTargetKawaseEikyoUpdateFlg12() {
        return inpTargetKawaseEikyoUpdateFlg12;
    }

    /**
     * @param inpTargetKawaseEikyoUpdateFlg12 the inpTargetKawaseEikyoUpdateFlg12 to set
     */
    public void setInpTargetKawaseEikyoUpdateFlg12(String inpTargetKawaseEikyoUpdateFlg12) {
        this.inpTargetKawaseEikyoUpdateFlg12 = inpTargetKawaseEikyoUpdateFlg12;
    }

    /**
     * @return the hatNet1
     */
    public String getHatNet1() {
        return hatNet1;
    }

    /**
     * @param hatNet1 the hatNet1 to set
     */
    public void setHatNet1(String hatNet1) {
        this.hatNet1 = hatNet1;
    }

    /**
     * @return the hatNet2
     */
    public String getHatNet2() {
        return hatNet2;
    }

    /**
     * @param hatNet2 the hatNet2 to set
     */
    public void setHatNet2(String hatNet2) {
        this.hatNet2 = hatNet2;
    }

    /**
     * @return the hatNet3
     */
    public String getHatNet3() {
        return hatNet3;
    }

    /**
     * @param hatNet3 the hatNet3 to set
     */
    public void setHatNet3(String hatNet3) {
        this.hatNet3 = hatNet3;
    }

    /**
     * @return the hatNet4
     */
    public String getHatNet4() {
        return hatNet4;
    }

    /**
     * @param hatNet4 the hatNet4 to set
     */
    public void setHatNet4(String hatNet4) {
        this.hatNet4 = hatNet4;
    }

    /**
     * @return the hatNet5
     */
    public String getHatNet5() {
        return hatNet5;
    }

    /**
     * @param hatNet5 the hatNet5 to set
     */
    public void setHatNet5(String hatNet5) {
        this.hatNet5 = hatNet5;
    }

    /**
     * @return the hatNet6
     */
    public String getHatNet6() {
        return hatNet6;
    }

    /**
     * @param hatNet6 the hatNet6 to set
     */
    public void setHatNet6(String hatNet6) {
        this.hatNet6 = hatNet6;
    }

    /**
     * @return the hatNet7
     */
    public String getHatNet7() {
        return hatNet7;
    }

    /**
     * @param hatNet7 the hatNet7 to set
     */
    public void setHatNet7(String hatNet7) {
        this.hatNet7 = hatNet7;
    }

    /**
     * @return the hatNet8
     */
    public String getHatNet8() {
        return hatNet8;
    }

    /**
     * @param hatNet8 the hatNet8 to set
     */
    public void setHatNet8(String hatNet8) {
        this.hatNet8 = hatNet8;
    }

    /**
     * @return the hatNet9
     */
    public String getHatNet9() {
        return hatNet9;
    }

    /**
     * @param hatNet9 the hatNet9 to set
     */
    public void setHatNet9(String hatNet9) {
        this.hatNet9 = hatNet9;
    }

    /**
     * @return the hatNet10
     */
    public String getHatNet10() {
        return hatNet10;
    }

    /**
     * @param hatNet10 the hatNet10 to set
     */
    public void setHatNet10(String hatNet10) {
        this.hatNet10 = hatNet10;
    }

    /**
     * @return the hatNet11
     */
    public String getHatNet11() {
        return hatNet11;
    }

    /**
     * @param hatNet11 the hatNet11 to set
     */
    public void setHatNet11(String hatNet11) {
        this.hatNet11 = hatNet11;
    }

    /**
     * @return the hatNet12
     */
    public String getHatNet12() {
        return hatNet12;
    }

    /**
     * @param hatNet12 the hatNet12 to set
     */
    public void setHatNet12(String hatNet12) {
        this.hatNet12 = hatNet12;
    }

    /**
     * @return the hatNetTm
     */
    public String getHatNetTm() {
        return hatNetTm;
    }

    /**
     * @param hatNetTm the hatNetTm to set
     */
    public void setHatNetTm(String hatNetTm) {
        this.hatNetTm = hatNetTm;
    }

    /**
     * @return the inpTargetHatNetUpdateFlg1
     */
    public String getInpTargetHatNetUpdateFlg1() {
        return inpTargetHatNetUpdateFlg1;
    }

    /**
     * @param inpTargetHatNetUpdateFlg1 the inpTargetHatNetUpdateFlg1 to set
     */
    public void setInpTargetHatNetUpdateFlg1(String inpTargetHatNetUpdateFlg1) {
        this.inpTargetHatNetUpdateFlg1 = inpTargetHatNetUpdateFlg1;
    }

    /**
     * @return the inpTargetHatNetUpdateFlg2
     */
    public String getInpTargetHatNetUpdateFlg2() {
        return inpTargetHatNetUpdateFlg2;
    }

    /**
     * @param inpTargetHatNetUpdateFlg2 the inpTargetHatNetUpdateFlg2 to set
     */
    public void setInpTargetHatNetUpdateFlg2(String inpTargetHatNetUpdateFlg2) {
        this.inpTargetHatNetUpdateFlg2 = inpTargetHatNetUpdateFlg2;
    }

    /**
     * @return the inpTargetHatNetUpdateFlg3
     */
    public String getInpTargetHatNetUpdateFlg3() {
        return inpTargetHatNetUpdateFlg3;
    }

    /**
     * @param inpTargetHatNetUpdateFlg3 the inpTargetHatNetUpdateFlg3 to set
     */
    public void setInpTargetHatNetUpdateFlg3(String inpTargetHatNetUpdateFlg3) {
        this.inpTargetHatNetUpdateFlg3 = inpTargetHatNetUpdateFlg3;
    }

    /**
     * @return the inpTargetHatNetUpdateFlg4
     */
    public String getInpTargetHatNetUpdateFlg4() {
        return inpTargetHatNetUpdateFlg4;
    }

    /**
     * @param inpTargetHatNetUpdateFlg4 the inpTargetHatNetUpdateFlg4 to set
     */
    public void setInpTargetHatNetUpdateFlg4(String inpTargetHatNetUpdateFlg4) {
        this.inpTargetHatNetUpdateFlg4 = inpTargetHatNetUpdateFlg4;
    }

    /**
     * @return the inpTargetHatNetUpdateFlg5
     */
    public String getInpTargetHatNetUpdateFlg5() {
        return inpTargetHatNetUpdateFlg5;
    }

    /**
     * @param inpTargetHatNetUpdateFlg5 the inpTargetHatNetUpdateFlg5 to set
     */
    public void setInpTargetHatNetUpdateFlg5(String inpTargetHatNetUpdateFlg5) {
        this.inpTargetHatNetUpdateFlg5 = inpTargetHatNetUpdateFlg5;
    }

    /**
     * @return the inpTargetHatNetUpdateFlg6
     */
    public String getInpTargetHatNetUpdateFlg6() {
        return inpTargetHatNetUpdateFlg6;
    }

    /**
     * @param inpTargetHatNetUpdateFlg6 the inpTargetHatNetUpdateFlg6 to set
     */
    public void setInpTargetHatNetUpdateFlg6(String inpTargetHatNetUpdateFlg6) {
        this.inpTargetHatNetUpdateFlg6 = inpTargetHatNetUpdateFlg6;
    }

    /**
     * @return the inpTargetHatNetUpdateFlg7
     */
    public String getInpTargetHatNetUpdateFlg7() {
        return inpTargetHatNetUpdateFlg7;
    }

    /**
     * @param inpTargetHatNetUpdateFlg7 the inpTargetHatNetUpdateFlg7 to set
     */
    public void setInpTargetHatNetUpdateFlg7(String inpTargetHatNetUpdateFlg7) {
        this.inpTargetHatNetUpdateFlg7 = inpTargetHatNetUpdateFlg7;
    }

    /**
     * @return the inpTargetHatNetUpdateFlg8
     */
    public String getInpTargetHatNetUpdateFlg8() {
        return inpTargetHatNetUpdateFlg8;
    }

    /**
     * @param inpTargetHatNetUpdateFlg8 the inpTargetHatNetUpdateFlg8 to set
     */
    public void setInpTargetHatNetUpdateFlg8(String inpTargetHatNetUpdateFlg8) {
        this.inpTargetHatNetUpdateFlg8 = inpTargetHatNetUpdateFlg8;
    }

    /**
     * @return the inpTargetHatNetUpdateFlg9
     */
    public String getInpTargetHatNetUpdateFlg9() {
        return inpTargetHatNetUpdateFlg9;
    }

    /**
     * @param inpTargetHatNetUpdateFlg9 the inpTargetHatNetUpdateFlg9 to set
     */
    public void setInpTargetHatNetUpdateFlg9(String inpTargetHatNetUpdateFlg9) {
        this.inpTargetHatNetUpdateFlg9 = inpTargetHatNetUpdateFlg9;
    }

    /**
     * @return the inpTargetHatNetUpdateFlg10
     */
    public String getInpTargetHatNetUpdateFlg10() {
        return inpTargetHatNetUpdateFlg10;
    }

    /**
     * @param inpTargetHatNetUpdateFlg10 the inpTargetHatNetUpdateFlg10 to set
     */
    public void setInpTargetHatNetUpdateFlg10(String inpTargetHatNetUpdateFlg10) {
        this.inpTargetHatNetUpdateFlg10 = inpTargetHatNetUpdateFlg10;
    }

    /**
     * @return the inpTargetHatNetUpdateFlg11
     */
    public String getInpTargetHatNetUpdateFlg11() {
        return inpTargetHatNetUpdateFlg11;
    }

    /**
     * @param inpTargetHatNetUpdateFlg11 the inpTargetHatNetUpdateFlg11 to set
     */
    public void setInpTargetHatNetUpdateFlg11(String inpTargetHatNetUpdateFlg11) {
        this.inpTargetHatNetUpdateFlg11 = inpTargetHatNetUpdateFlg11;
    }

    /**
     * @return the inpTargetHatNetUpdateFlg12
     */
    public String getInpTargetHatNetUpdateFlg12() {
        return inpTargetHatNetUpdateFlg12;
    }

    /**
     * @param inpTargetHatNetUpdateFlg12 the inpTargetHatNetUpdateFlg12 to set
     */
    public void setInpTargetHatNetUpdateFlg12(String inpTargetHatNetUpdateFlg12) {
        this.inpTargetHatNetUpdateFlg12 = inpTargetHatNetUpdateFlg12;
    }

    /**
     * @return the arariRuikeiMaeAllTotal
     */
    public String getArariRuikeiMaeAllTotal() {
        return arariRuikeiMaeAllTotal;
    }

    /**
     * @param arariRuikeiMaeAllTotal the arariRuikeiMaeAllTotal to set
     */
    public void setArariRuikeiMaeAllTotal(String arariRuikeiMaeAllTotal) {
        this.arariRuikeiMaeAllTotal = arariRuikeiMaeAllTotal;
    }

    /**
     * @return the mrateRuikeiMaeAllTotal
     */
    public String getMrateRuikeiMaeAllTotal() {
        return mrateRuikeiMaeAllTotal;
    }

    /**
     * @param mrateRuikeiMaeAllTotal the mrateRuikeiMaeAllTotal to set
     */
    public void setMrateRuikeiMaeAllTotal(String mrateRuikeiMaeAllTotal) {
        this.mrateRuikeiMaeAllTotal = mrateRuikeiMaeAllTotal;
    }

    /**
     * @return the arariMaeAllTotal
     */
    public String getArariMaeAllTotal() {
        return arariMaeAllTotal;
    }

    /**
     * @param arariMaeAllTotal the arariMaeAllTotal to set
     */
    public void setArariMaeAllTotal(String arariMaeAllTotal) {
        this.arariMaeAllTotal = arariMaeAllTotal;
    }

    /**
     * @return the mrateMaeAllTotal
     */
    public String getMrateMaeAllTotal() {
        return mrateMaeAllTotal;
    }

    /**
     * @param mrateMaeAllTotal the mrateMaeAllTotal to set
     */
    public void setMrateMaeAllTotal(String mrateMaeAllTotal) {
        this.mrateMaeAllTotal = mrateMaeAllTotal;
    }

    public boolean isIsIkkatsuFlg() {
        return isIkkatsuFlg;
    }

    public void setIsIkkatsuFlg(boolean isIkkatsuFlg) {
        this.isIkkatsuFlg = isIkkatsuFlg;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

//    public String getEditAuthFlg() {
//        return editAuthFlg;
//    }
//
//    public void setEditAuthFlg(String editAuthFlg) {
//        this.editAuthFlg = editAuthFlg;
//    }

    public String getUpdateNewDataKbn() {
        return updateNewDataKbn;
    }

    public void setUpdateNewDataKbn(String updateNewDataKbn) {
        this.updateNewDataKbn = updateNewDataKbn;
    }

    public String[] getKaisyuEnkaAmount1() {
        return kaisyuEnkaAmount1;
    }

    public void setKaisyuEnkaAmount1(String[] kaisyuEnkaAmount1) {
        this.kaisyuEnkaAmount1 = kaisyuEnkaAmount1;
    }

    public String[] getKaisyuEnkaAmount2() {
        return kaisyuEnkaAmount2;
    }

    public void setKaisyuEnkaAmount2(String[] kaisyuEnkaAmount2) {
        this.kaisyuEnkaAmount2 = kaisyuEnkaAmount2;
    }

    public String[] getKaisyuEnkaAmount3() {
        return kaisyuEnkaAmount3;
    }

    public void setKaisyuEnkaAmount3(String[] kaisyuEnkaAmount3) {
        this.kaisyuEnkaAmount3 = kaisyuEnkaAmount3;
    }

    public String[] getKaisyuEnkaAmount4() {
        return kaisyuEnkaAmount4;
    }

    public void setKaisyuEnkaAmount4(String[] kaisyuEnkaAmount4) {
        this.kaisyuEnkaAmount4 = kaisyuEnkaAmount4;
    }

    public String[] getKaisyuEnkaAmount5() {
        return kaisyuEnkaAmount5;
    }

    public void setKaisyuEnkaAmount5(String[] kaisyuEnkaAmount5) {
        this.kaisyuEnkaAmount5 = kaisyuEnkaAmount5;
    }

    public String[] getKaisyuEnkaAmount6() {
        return kaisyuEnkaAmount6;
    }

    public void setKaisyuEnkaAmount6(String[] kaisyuEnkaAmount6) {
        this.kaisyuEnkaAmount6 = kaisyuEnkaAmount6;
    }

    public String[] getKaisyuEnkaAmount7() {
        return kaisyuEnkaAmount7;
    }

    public void setKaisyuEnkaAmount7(String[] kaisyuEnkaAmount7) {
        this.kaisyuEnkaAmount7 = kaisyuEnkaAmount7;
    }

    public String[] getKaisyuEnkaAmount8() {
        return kaisyuEnkaAmount8;
    }

    public void setKaisyuEnkaAmount8(String[] kaisyuEnkaAmount8) {
        this.kaisyuEnkaAmount8 = kaisyuEnkaAmount8;
    }

    public String[] getKaisyuEnkaAmount9() {
        return kaisyuEnkaAmount9;
    }

    public void setKaisyuEnkaAmount9(String[] kaisyuEnkaAmount9) {
        this.kaisyuEnkaAmount9 = kaisyuEnkaAmount9;
    }

    public String[] getKaisyuEnkaAmount10() {
        return kaisyuEnkaAmount10;
    }

    public void setKaisyuEnkaAmount10(String[] kaisyuEnkaAmount10) {
        this.kaisyuEnkaAmount10 = kaisyuEnkaAmount10;
    }

    public String[] getKaisyuEnkaAmount11() {
        return kaisyuEnkaAmount11;
    }

    public void setKaisyuEnkaAmount11(String[] kaisyuEnkaAmount11) {
        this.kaisyuEnkaAmount11 = kaisyuEnkaAmount11;
    }

    public String[] getKaisyuEnkaAmount12() {
        return kaisyuEnkaAmount12;
    }

    public void setKaisyuEnkaAmount12(String[] kaisyuEnkaAmount12) {
        this.kaisyuEnkaAmount12 = kaisyuEnkaAmount12;
    }

    public String[] getKaisyuEnkaAmountTm() {
        return kaisyuEnkaAmountTm;
    }

    public void setKaisyuEnkaAmountTm(String[] kaisyuEnkaAmountTm) {
        this.kaisyuEnkaAmountTm = kaisyuEnkaAmountTm;
    }

    public String[] getInpTargetKaisyuAmountZeiKbn() {
        return inpTargetKaisyuAmountZeiKbn;
    }

    public void setInpTargetKaisyuAmountZeiKbn(String[] inpTargetKaisyuAmountZeiKbn) {
        this.inpTargetKaisyuAmountZeiKbn = inpTargetKaisyuAmountZeiKbn;
    }

    public String[] getInpTargetKaisyuAmountKinsyuKbn() {
        return inpTargetKaisyuAmountKinsyuKbn;
    }

    public void setInpTargetKaisyuAmountKinsyuKbn(String[] inpTargetKaisyuAmountKinsyuKbn) {
        this.inpTargetKaisyuAmountKinsyuKbn = inpTargetKaisyuAmountKinsyuKbn;
    }

    public String[] getInpTargetKaisyuAmountKaisyuKbn() {
        return inpTargetKaisyuAmountKaisyuKbn;
    }

    public void setInpTargetKaisyuAmountKaisyuKbn(String[] inpTargetKaisyuAmountKaisyuKbn) {
        this.inpTargetKaisyuAmountKaisyuKbn = inpTargetKaisyuAmountKaisyuKbn;
    }

    public String getSaisyuUpdeteBtnFlg() {
        return saisyuUpdeteBtnFlg;
    }

    public void setSaisyuUpdeteBtnFlg(String saisyuUpdeteBtnFlg) {
        this.saisyuUpdeteBtnFlg = saisyuUpdeteBtnFlg;
    }

    public List<S004DownloadKaisyuEntity> getKaisyuCurrencyList() {
        return kaisyuCurrencyList;
    }

    public void setKaisyuCurrencyList(List<S004DownloadKaisyuEntity> kaisyuCurrencyList) {
        this.kaisyuCurrencyList = kaisyuCurrencyList;
    }

    public Map<String, Map<String, String>> getLossData() {
        return lossData;
    }

    public void setLossData(Map<String, Map<String, String>> lossData) {
        this.lossData = lossData;
    }

    public Integer getForeignFlg() {
        return foreignFlg;
    }

    public void setForeignFlg(Integer foreignFlg) {
        this.foreignFlg = foreignFlg;
    }

    public Integer getKaisyuForeignFlg() {
        return kaisyuForeignFlg;
    }

    public void setKaisyuForeignFlg(Integer kaisyuForeignFlg) {
        this.kaisyuForeignFlg = kaisyuForeignFlg;
    }

    /**
     * @return the totalJyuchuSp
     */
    public HashMap<String, String> getTotalJyuchuSp() {
        return totalJyuchuSp;
    }

    /**
     * @param totalJyuchuSp the totalJyuchuSp to set
     */
    public void setTotalJyuchuSp(HashMap<String, String> totalJyuchuSp) {
        this.totalJyuchuSp = totalJyuchuSp;
    }

    /**
     * @return the jyuchuRate
     */
    public List<HashMap<String, String>> getJyuchuRate() {
        return jyuchuRate;
    }

    /**
     * @param jyuchuRate the jyuchuRate to set
     */
    public void setJyuchuRate(List<HashMap<String, String>> jyuchuRate) {
        this.jyuchuRate = jyuchuRate;
    }

    /**
     * @return the juchuSpListOpenFlg
     */
    public String getJuchuSpListOpenFlg() {
        return juchuSpListOpenFlg;
    }

    /**
     * @param juchuSpListOpenFlg the juchuSpListOpenFlg to set
     */
    public void setJuchuSpListOpenFlg(String juchuSpListOpenFlg) {
        this.juchuSpListOpenFlg = juchuSpListOpenFlg;
    }

    /**
     * @return the jyuchuKingaku
     */
    public List<HashMap<String, String>> getJyuchuKingaku() {
        return jyuchuKingaku;
    }

    /**
     * @param jyuchuKingaku the jyuchuKingaku to set
     */
    public void setJyuchuKingaku(List<HashMap<String, String>> jyuchuKingaku) {
        this.jyuchuKingaku = jyuchuKingaku;
    }

    /**
     * @return the totalJyuchuNet
     */
    public HashMap<String, String> getTotalJyuchuNet() {
        return totalJyuchuNet;
    }

    /**
     * @param totalJyuchuNet the totalJyuchuNet to set
     */
    public void setTotalJyuchuNet(HashMap<String, String> totalJyuchuNet) {
        this.totalJyuchuNet = totalJyuchuNet;
    }

    /**
     * @return the jyuchuMikomuNet
     */
    public HashMap<String, String> getJyuchuMikomuNet() {
        return jyuchuMikomuNet;
    }

    /**
     * @param jyuchuMikomuNet the jyuchuMikomuNet to set
     */
    public void setJyuchuMikomuNet(HashMap<String, String> jyuchuMikomuNet) {
        this.jyuchuMikomuNet = jyuchuMikomuNet;
    }

    /**
     * @return the juchuNetListOpenFlg
     */
    public String getJuchuNetListOpenFlg() {
        return juchuNetListOpenFlg;
    }

    /**
     * @param juchuNetListOpenFlg the juchuNetListOpenFlg to set
     */
    public void setJuchuNetListOpenFlg(String juchuNetListOpenFlg) {
        this.juchuNetListOpenFlg = juchuNetListOpenFlg;
    }

    /**
     * @return the jyuchuArari
     */
    public HashMap<String, String> getJyuchuArari() {
        return jyuchuArari;
    }

    /**
     * @param jyuchuArari the jyuchuArari to set
     */
    public void setJyuchuArari(HashMap<String, String> jyuchuArari) {
        this.jyuchuArari = jyuchuArari;
    }

    /**
     * @return the jyuchuMrate
     */
    public HashMap<String, String> getJyuchuMrate() {
        return jyuchuMrate;
    }

    /**
     * @param jyuchuMrate the jyuchuMrate to set
     */
    public void setJyuchuMrate(HashMap<String, String> jyuchuMrate) {
        this.jyuchuMrate = jyuchuMrate;
    }

    /**
     * @return the monthTitleJyuchu
     */
    public ArrayList<String> getMonthTitleJyuchu() {
        return monthTitleJyuchu;
    }

    /**
     * @param monthTitleJyuchu the monthTitleJyuchu to set
     */
    public void setMonthTitleJyuchu(ArrayList<String> monthTitleJyuchu) {
        this.monthTitleJyuchu = monthTitleJyuchu;
    }

    /**
     * @return the titleJyuchu
     */
    public ArrayList<Map<String, String>> getTitleJyuchu() {
        return titleJyuchu;
    }

    /**
     * @param titleJyuchu the titleJyuchu to set
     */
    public void setTitleJyuchu(ArrayList<Map<String, String>> titleJyuchu) {
        this.titleJyuchu = titleJyuchu;
    }

    /**
     * @return the jyuchuForeignFlg
     */
    public Integer getJyuchuForeignFlg() {
        return jyuchuForeignFlg;
    }

    /**
     * @param jyuchuForeignFlg the jyuchuForeignFlg to set
     */
    public void setJyuchuForeignFlg(Integer jyuchuForeignFlg) {
        this.jyuchuForeignFlg = jyuchuForeignFlg;
    }

    /**
     * @return the jyuchuDataKbn
     */
    public String[] getJyuchuDataKbn() {
        return jyuchuDataKbn;
    }

    /**
     * @param jyuchuDataKbn the jyuchuDataKbn to set
     */
    public void setJyuchuDataKbn(String[] jyuchuDataKbn) {
        this.jyuchuDataKbn = jyuchuDataKbn;
    }

    /**
     * @return the jyuchuCurrencyCode
     */
    public String[] getJyuchuCurrencyCode() {
        return jyuchuCurrencyCode;
    }

    /**
     * @param jyuchuCurrencyCode the jyuchuCurrencyCode to set
     */
    public void setJyuchuCurrencyCode(String[] jyuchuCurrencyCode) {
        this.jyuchuCurrencyCode = jyuchuCurrencyCode;
    }

    /**
     * @return the jyuchuSyuekiYm
     */
    public String[] getJyuchuSyuekiYm() {
        return jyuchuSyuekiYm;
    }

    /**
     * @param jyuchuSyuekiYm the jyuchuSyuekiYm to set
     */
    public void setJyuchuSyuekiYm(String[] jyuchuSyuekiYm) {
        this.jyuchuSyuekiYm = jyuchuSyuekiYm;
    }

    /**
     * @return the jyuchuSpKingaku
     */
    public String[] getJyuchuSpKingaku() {
        return jyuchuSpKingaku;
    }

    /**
     * @param jyuchuSpKingaku the jyuchuSpKingaku to set
     */
    public void setJyuchuSpKingaku(String[] jyuchuSpKingaku) {
        this.jyuchuSpKingaku = jyuchuSpKingaku;
    }

    /**
     * @return the jyuchuSpRate
     */
    public String[] getJyuchuSpRate() {
        return jyuchuSpRate;
    }

    /**
     * @param jyuchuSpRate the jyuchuSpRate to set
     */
    public void setJyuchuSpRate(String[] jyuchuSpRate) {
        this.jyuchuSpRate = jyuchuSpRate;
    }

    /**
     * @return the kanbaiYm
     */
    public String getKanbaiYm() {
        return kanbaiYm;
    }

    /**
     * @param kanbaiYm the kanbaiYm to set
     */
    public void setKanbaiYm(String kanbaiYm) {
        this.kanbaiYm = kanbaiYm;
    }

    /**
     * @return the jyuchuNetDataKbn
     */
    public String[] getJyuchuNetDataKbn() {
        return jyuchuNetDataKbn;
    }

    /**
     * @param jyuchuNetDataKbn the jyuchuNetDataKbn to set
     */
    public void setJyuchuNetDataKbn(String[] jyuchuNetDataKbn) {
        this.jyuchuNetDataKbn = jyuchuNetDataKbn;
    }

    /**
     * @return the jyuchuNetSyuekiYm
     */
    public String[] getJyuchuNetSyuekiYm() {
        return jyuchuNetSyuekiYm;
    }

    /**
     * @param jyuchuNetSyuekiYm the jyuchuNetSyuekiYm to set
     */
    public void setJyuchuNetSyuekiYm(String[] jyuchuNetSyuekiYm) {
        this.jyuchuNetSyuekiYm = jyuchuNetSyuekiYm;
    }

    /**
     * @return the jyuchuNetKingaku
     */
    public String[] getJyuchuNetKingaku() {
        return jyuchuNetKingaku;
    }

    /**
     * @param jyuchuNetKingaku the jyuchuNetKingaku to set
     */
    public void setJyuchuNetKingaku(String[] jyuchuNetKingaku) {
        this.jyuchuNetKingaku = jyuchuNetKingaku;
    }



}
