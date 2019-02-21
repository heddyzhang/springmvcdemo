/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.co.toshiba.hby.pspromis.syuueki.service.mikomiupload;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuGeBukkenInfoTbl;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuKiNetCateTitleTbl;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuKiNetCateTukiCldTbl;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuKiNetCateTukiTbl;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuKiNetSogenkaTukiTbl;
import jp.co.toshiba.hby.pspromis.syuueki.entity.SyuKiSpTukiSTbl;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SysdateEntityFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuGeBukenInfoTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiNetCateTitleTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiNetCateTukiCldTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiNetCateTukiTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiNetSogenkaTukiTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiSpTukiSTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.interceptor.TranceInterceptor;
import jp.co.toshiba.hby.pspromis.syuueki.jdbc.DbUtilsExecutor;
import jp.co.toshiba.hby.pspromis.common.jdbc.SqlExecutor;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiJyuchuNetTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiJyuchuSpTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.facade.SyuKiKaisyuTblFacade;
import jp.co.toshiba.hby.pspromis.syuueki.jdbc.SqlFile;
import jp.co.toshiba.hby.pspromis.syuueki.util.LoginUserInfo;
import jp.co.toshiba.hby.pspromis.syuueki.util.Utils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author ibayashi
 */
@Stateless
@Interceptors({TranceInterceptor.class})
public class UploadDataAccess {
    
    @PersistenceContext(unitName = jp.co.toshiba.hby.pspromis.syuueki.util.ConstantString.syuuekiDataSourceName)
    private EntityManager em;
    
    @Inject
    private SyuGeBukenInfoTblFacade syuGeBukenInfoTblFacade;

    @Inject
    private SyuKiNetCateTitleTblFacade syuKiNetCateTitleTblFacade;
    
    @Inject
    private SyuKiNetCateTukiTblFacade syuKiNetCateTukiTblFacade;

    @Inject
    private SyuKiNetCateTukiCldTblFacade syuKiNetCateTukiCldTblFacade;

    @Inject
    private SysdateEntityFacade sysdateEntityFacade;
    
    @Inject
    private SyuKiSpTukiSTblFacade syuKiSpTukiSTblFacade;
    
    @Inject
    private SyuKiNetSogenkaTukiTblFacade syuKiNetSogenkaTukiTblFacade;
    
    @Inject
    private LoginUserInfo loginUserInfo;
    
    @Inject
    protected SqlExecutor sqlExecutor;
    
    @Inject
    private DbUtilsExecutor dbUtilsExecutor;

    @Inject
    private SyuKiKaisyuTblFacade syuKiKaisyuTblFacade;
    
    @Inject
    private SyuKiJyuchuSpTblFacade syuKiJyuchuSpTblFacade;
    
    @Inject
    private SyuKiJyuchuNetTblFacade syuKiJyuchuNetTblFacade;  
    
    /**
     * 指定注番より案件情報を取得
     * @param ono
     * @param ankenFlg
     * @return 
     */
    public SyuGeBukkenInfoTbl findOnoBuken(String ono, String ankenFlg) {
        Map<String, Object> condition = new HashMap<>();

        condition.put("ankenFlg", ankenFlg);

        if (StringUtils.length(ono) >= 8) {
            // 指定した注番が8桁以上の場合は、引数の注番はPromis案件番号と見なす。
            condition.put("ankenId", ono);
        } else {
            // 指定した注番が7桁以下の場合は、引数の注番は注番そのものと見なす。
            if ("1".equals(ankenFlg)) {
                condition.put("mainOrderNo", ono);
            } else {
                condition.put("orderNo", ono);
            }
        }

        SyuGeBukkenInfoTbl bukkenEn = syuGeBukenInfoTblFacade.findOnBukken(condition);

        return bukkenEn;
    }
    
    /**
     * 指定案件に再計算FLGを立てる
     */
    public void setSaikeisanFlg(String ankenId, Integer rirekiId) {
        syuGeBukenInfoTblFacade.setSaikeisanFlg(ankenId, rirekiId, "1");
    }
    
    /**
     * SYU_KI_NET_CATE_TITLE_TBLにデータを登録
     * @param data 
     */
    public void insertKiNetCateTitleTbl(Map<String, Object> data) {
        Long count = 
                syuKiNetCateTitleTblFacade.getPkCount(
                        (String)data.get("ankenId"),
                        (Integer)data.get("rirekiId"),
                        (String)data.get("categoryCode"),
                        (String)data.get("categoryKbn1"),
                        (String)data.get("categoryKbn2")
                );

        if (count == null || count == 0) {
            Date now = sysdateEntityFacade.getSysdate();
            SyuKiNetCateTitleTbl en = new SyuKiNetCateTitleTbl();
            en.setAnkenId((String)data.get("ankenId"));
            en.setRirekiId((Integer)data.get("rirekiId"));
            en.setCategoryCode((String)data.get("categoryCode"));
            en.setCategoryKbn1((String)data.get("categoryKbn1"));
            en.setCategoryKbn2((String)data.get("categoryKbn2"));
            en.setCategoryName1((String)data.get("categoryName1"));
            en.setCategoryName2((String)data.get("categoryName2"));
            en.setCategorySeq((String)data.get("categorySeq"));
            en.setCreatedAt(now);
            en.setCreatedBy(loginUserInfo.getUserId());
            en.setUpdatedAt(now);
            en.setUpdatedBy(loginUserInfo.getUserId());
            
            syuKiNetCateTitleTblFacade.create(en);
        }
    }

    /**
     * SYU_KI_NET_CATE_TUKI_TBLにデータを登録
     * @param data 
     */
    public void updateKiNetCateTukiTbl(Map<String, Object> data) throws Exception {
        int insertFlg = 0;
        Date now;
        
        SyuKiNetCateTukiTbl en
                = syuKiNetCateTukiTblFacade.getPkInfo(
                         (String)data.get("ankenId")
                        ,(Integer)data.get("rirekiId")
                        ,(String)data.get("dataKbn")
                        ,(String)data.get("syuekiYm")
                        ,(String)data.get("categoryCode")
                        ,(String)data.get("categoryKbn1")
                        ,(String)data.get("categoryKbn2")
                );
        
        now = sysdateEntityFacade.getSysdate();
        if (en == null) {
            // データが存在しない場合は新規登録
            insertFlg = 1;

            en = new SyuKiNetCateTukiTbl();
            en.setAnkenId((String)data.get("ankenId"));
            en.setRirekiId((Integer)data.get("rirekiId"));
            en.setDataKbn((String)data.get("dataKbn"));
            en.setSyuekiYm((String)data.get("syuekiYm"));
            en.setCategoryCode((String)data.get("categoryCode"));
            en.setCategoryKbn1((String)data.get("categoryKbn1"));
            en.setCategoryKbn2((String)data.get("categoryKbn2"));
            en.setCreatedAt(now);
            en.setCreatedBy(loginUserInfo.getUserId());
        }

        en.setUpdatedAt(now);
        en.setUpdatedBy(loginUserInfo.getUserId());

        // NETを設定
        if (data.get("net") != null) {
            en.setNet(((BigDecimal)data.get("net")).longValue());
        } else {
            en.setNet(null);
        }

        // 製番損益を設定
        // 期間損益進行アップロード時の更新対象はNETのみ
        if (!"KSS".equals((String)data.get("updateKbn"))) {
            if (data.get("seibanSonekiNet") != null) {
                en.setSeibanSonekiNet(((BigDecimal)data.get("seibanSonekiNet")).longValue());
            } else {
                en.setSeibanSonekiNet(null);
            }
        }
        
        if (insertFlg == 1) {
            syuKiNetCateTukiTblFacade.create(en);
        } else {
            syuKiNetCateTukiTblFacade.edit(en);
        }

    }

    /**
     * SYU_KI_NET_CATE_TUKI_CLD_TBLにデータを登録
     * @param data 
     */
    public void updateKiNetCateTukiCdlTbl(Map<String, Object> data) throws Exception {
        int insertFlg = 0;
        Date now;
        
        SyuKiNetCateTukiCldTbl en
                = syuKiNetCateTukiCldTblFacade.getPkInfo(
                         (String)data.get("ankenId")
                        ,(String)data.get("syuekiYm")
                        ,(String)data.get("categoryCode")
                        ,(String)data.get("categoryKbn1")
                        ,(String)data.get("kbn")
                );
        
        now = sysdateEntityFacade.getSysdate();
        if (en == null) {
            // データが存在しない場合は新規登録
            insertFlg = 1;

            en = new SyuKiNetCateTukiCldTbl();
            en.setAnkenId((String)data.get("ankenId"));
            en.setSyuekiYm((String)data.get("syuekiYm"));
            en.setCategoryCode((String)data.get("categoryCode"));
            en.setCategoryKbn1((String)data.get("categoryKbn1"));
            en.setKbn((String)data.get("kbn"));
            en.setCreatedAt(now);
            en.setCreatedBy(loginUserInfo.getUserId());
        }

        en.setUpdatedAt(now);
        en.setUpdatedBy(loginUserInfo.getUserId());

        // NETを設定
        if (data.get("net") != null) {
            en.setNet(((BigDecimal)data.get("net")).longValue());
        } else {
            en.setNet(null);
        }
 
        if (insertFlg == 1) {
            syuKiNetCateTukiCldTblFacade.create(en);
        } else {
            syuKiNetCateTukiCldTblFacade.edit(en);
        }
    }

    /**
     * SYU_KI_NET_CATE_TUKI_CLD_TBLから指定案件、月毎の集計NETを取得
     * @param ankenIdList 検索対象の物件key(複数件指定)
     * @param syuekiYm 集計開始を行う年月(yyyyMM) 
     * @return  
     */
    public List<Map<String, Object>> getSumYmList(String[] ankenIdList, String syuekiYm) throws SQLException {
        Map<String, Object> condition = new HashMap<>();
        condition.put("ankenId", ankenIdList);
        condition.put("syuekiYm", syuekiYm);
        condition.put("rirekiId", 0);

        String sqlFilePath ="/sql/syuKiNetCateTukiCldTbl/selectYmSumNet.sql";
        
        // SQLを取
        SqlFile sqlFile = new SqlFile();
        String sql = sqlFile.getSqlString(sqlFilePath, condition);
        Object[] params = sqlFile.getSqlParams(sqlFilePath, condition);
        
        // SQL実行
        List<Map<String, Object>> list = dbUtilsExecutor.dbUtilsGetSqlList(em, sql, params);
        
        return list;
    }
    
    /**
     * カテゴリマスタ(SYU_N8_CATEGORY_MAP)からデータ取得(CATEGORY_KBN1を条件)
     * @param categoryKbn1
     * @param categoryKbn2
     * @return 
     * @throws java.sql.SQLException 
     */
    public Map<String, Object> categoryMstInfo(String categoryKbn1, String categoryKbn2) throws SQLException {
        Map<String, Object> result = null;
        
        Map<String, Object> condition = new HashMap<>();
        condition.put("categoryKbn1", categoryKbn1);
        if (StringUtils.isNotEmpty(categoryKbn2)) {
            condition.put("categoryKbn2", categoryKbn2);
        }

        String sqlFilePath = "/sql/categoryMst/selectCategoryMst.sql";
        
        // SQLを取得
        SqlFile sqlFile = new SqlFile();
        String sql = sqlFile.getSqlString(sqlFilePath, condition);
        Object[] params = sqlFile.getSqlParams(sqlFilePath, condition);
        
        // SQL実行
        List<Map<String, Object>> list = dbUtilsExecutor.dbUtilsGetSqlList(em, sql, params);
        
        if (list.size() > 0) {
            result = list.get(0);
        }
        
        return result;
    }
    
    /**
     * カテゴリマスタ(SYU_N8_CATEGORY_MAP)からデータ取得(CATEGORY_KBN1を条件)
     * @param categoryKbn1
     * @return 
     * @throws java.sql.SQLException 
     */
    public Map<String, Object> categoryMstInfo(String categoryKbn1) throws SQLException {
        return categoryMstInfo(categoryKbn1, null);
    }
    
    
    /**
     * カテゴリマスタ(SYU_N8_CATEGORY_MAP)からデータ取得(CATEGORY_CODEを条件)
     * @param categoryCode
     * @return 
     * @throws java.sql.SQLException 
     */
    public Map<String, Object> categoryMstInfoFromCateCode(String categoryCode) throws SQLException {
        Map<String, Object> result = null;
        
        Map<String, Object> condition = new HashMap<>();
        condition.put("categoryCode", categoryCode);

        String sqlFilePath = "/sql/categoryMst/selectCategoryMstFromCode.sql";
        
        // SQLを取得
        SqlFile sqlFile = new SqlFile();
        String sql = sqlFile.getSqlString(sqlFilePath, condition);
        Object[] params = sqlFile.getSqlParams(sqlFilePath, condition);
        
        // SQL実行
        List<Map<String, Object>> list = dbUtilsExecutor.dbUtilsGetSqlList(em, sql, params);
        
        if (list.size() > 0) {
            result = list.get(0);
        }
        
        return result;
    }
    
    
    /**
     * SYU_KI_SP_TUKI_S_TBLにデータを登録
     * @param data 
     */
    public void updateKiSpTukiSTbl(Map<String, Object> data) throws Exception {
        int insertFlg = 0;
        Date now;
        
        SyuKiSpTukiSTbl en
                = syuKiSpTukiSTblFacade.getPkInfo(
                         (String)data.get("ankenId")
                        ,(Integer)data.get("rirekiId")
                        ,(String)data.get("dataKbn")
                        ,(String)data.get("syuekiYm")
                        ,(String)data.get("currencyCode")
                );
        
        now = sysdateEntityFacade.getSysdate();
        if (en == null) {
            // データが存在しない場合は新規登録
            insertFlg = 1;

            en = new SyuKiSpTukiSTbl();
            en.setAnkenId((String)data.get("ankenId"));
            en.setRirekiId((Integer)data.get("rirekiId"));
            en.setDataKbn((String)data.get("dataKbn"));
            en.setSyuekiYm((String)data.get("syuekiYm"));
            en.setCurrencyCode((String)data.get("currencyCode"));
            en.setCreatedAt(now);
            en.setCreatedBy(loginUserInfo.getUserId());
        }

        en.setUpdatedAt(now);
        en.setUpdatedBy(loginUserInfo.getUserId());

        // 契約金額:補正を設定
        if (data.get("keiyakuHoseiAmount") != null) {
            en.setKeiyakuHoseiAmount(((BigDecimal)data.get("keiyakuHoseiAmount")));
        } else {
            en.setKeiyakuHoseiAmount(null);
        }

        if (insertFlg == 1) {
            syuKiSpTukiSTblFacade.create(en);
        } else {
            syuKiSpTukiSTblFacade.edit(en);
        }

    }

    
    /**
     * SYU_KI_NET_SOGENKA_TUKI_TBLにデータを登録
     * @param data 
     */
    public void updateKiNetSogenkaTukiTbl(Map<String, Object> data) throws Exception {
        int insertFlg = 0;
        Date now;
        
        SyuKiNetSogenkaTukiTbl en
                = syuKiNetSogenkaTukiTblFacade.getPkInfo(
                         (String)data.get("ankenId")
                        ,(Integer)data.get("rirekiId")
                        ,(String)data.get("dataKbn")
                        ,(String)data.get("syuekiYm")
                );
        
        now = sysdateEntityFacade.getSysdate();
        if (en == null) {
            // データが存在しない場合は新規登録
            insertFlg = 1;

            en = new SyuKiNetSogenkaTukiTbl();
            en.setAnkenId((String)data.get("ankenId"));
            en.setRirekiId((Integer)data.get("rirekiId"));
            en.setDataKbn((String)data.get("dataKbn"));
            en.setSyuekiYm((String)data.get("syuekiYm"));
            en.setCreatedAt(now);
            en.setCreatedBy(loginUserInfo.getUserId());
        }

        en.setUpdatedAt(now);
        en.setUpdatedBy(loginUserInfo.getUserId());

        // 発NETを設定
        if (data.get("hatNet") != null) {
            en.setHatNet(((BigDecimal)data.get("hatNet")).longValue());
        } else {
            en.setHatNet(null);
        }

        // 未発NETを設定
        if (data.get("miHatNet") != null) {
            en.setMiHatNet(((BigDecimal)data.get("miHatNet")).longValue());
        } else {
            en.setMiHatNet(null);
        }

        // 製番損益を設定
        if (data.get("seibanSoneki") != null) {
            en.setSeibanSonekiNet(((BigDecimal)data.get("seibanSoneki")).longValue());
        } else {
            en.setSeibanSonekiNet(null);
        }

        // 為替洗替影響を設定
        if (data.get("kawaseEikyo") != null) {
            en.setKawaseEikyo(((BigDecimal)data.get("kawaseEikyo")).longValue());
        } else {
            en.setKawaseEikyo(null);
        }

        if (insertFlg == 1) {
            syuKiNetSogenkaTukiTblFacade.create(en);
        } else {
            syuKiNetSogenkaTukiTblFacade.edit(en);
        }

    }

    /**
     * SYU_KI_KAISYU_TBLにデータを登録
     * @param data 
     */
    public void updateKiKaisyuTbl(Map<String, Object> data) throws Exception {
        
        data.put("userId", loginUserInfo.getUserId());
        syuKiKaisyuTblFacade.entrySyuKiKaisyuTbl(data);

    }
    
    /**
     * 受注管理情報(SP内訳)を更新
     * @param jyuchuSp 
     */
    public void updateJyuchuSpRate(Map<String, Object> jyuchuSp) {
       
       HashMap params = new HashMap<>();
       
       // 更新条件を作成
        // 物件Key  
        params.put("ankenId", jyuchuSp.get("ankenId"));
        // 履歴Key
        params.put("rirekiId", jyuchuSp.get("rirekiId"));
        // データ種別
        params.put("dataKbn", jyuchuSp.get("dataKbn"));
        // 通貨コード  
        params.put("currencyCode", jyuchuSp.get("currencyCode"));
        // 年月
        params.put("syuekiYm", jyuchuSp.get("syuekiYm"));
        // 受注レート
        params.put("jyuchuRate", jyuchuSp.get("jyuchuRate"));
        // 受注SP金額 
        params.put("jyuchuSp", jyuchuSp.get("jyuchuSp"));
        // 受注レート更新FLG
        params.put("rateUpdateFlg", "1");
                
        // 受注管理情報(SP内訳)を更新
        syuKiJyuchuSpTblFacade.entryJyuchuSpRate(params, false);
    }
    
     /**
     * 受注管理情報(一括見込NET)を更新
     * @param jyuchuNet 
     */
    public void updateJyuchuNet(Map<String, Object> jyuchuNet) {
       
       HashMap params = new HashMap<>();
       
       // 更新条件を作成
        // 物件Key  
        params.put("ankenId", jyuchuNet.get("ankenId"));
        // 履歴Key
        params.put("rirekiId", jyuchuNet.get("rirekiId"));
        // データ種別
        params.put("dataKbn", jyuchuNet.get("dataKbn"));
        // 年月
        params.put("syuekiYm", jyuchuNet.get("syuekiYm"));
        // カテゴリーコード  
        params.put("categoryCode", jyuchuNet.get("categoryCode"));
        // 分類１  
        params.put("categoryKbn1", jyuchuNet.get("categoryKbn1"));
        // 分類2  
        params.put("categoryKbn2", jyuchuNet.get("categoryKbn2"));
        // カテゴリ名１（部課名含む）  
        params.put("categoryName1", jyuchuNet.get("categoryName1"));
        // カテゴリ名２（部課名含む）  
        params.put("categoryName2", jyuchuNet.get("categoryName2"));
        // NET
        params.put("jyuchuNet", jyuchuNet.get("jyuchuNet"));
                
        // 受注管理情報(一括見込NET)を更新
        syuKiJyuchuNetTblFacade.entryJyuchuNet(params, false);
    }
}
