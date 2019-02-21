package jp.co.toshiba.hby.pspromis.syuueki.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.apache.commons.fileupload.FileItem;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author ibayashi
 */
@Named(value = "mikomiUploadBean")
@RequestScoped
public class MikomiUploadBean {

    /**
     * アップロードファイル
     */
    private FileItem uploadFile;
   
    /**
     * アップロードファイル(PoiのWorkbook)
     */
    private Workbook workBook;

    /**
     * アップロード処理FLG
     */
    private String uploadFlg;
    
    /**
     * 処理区分(画面から取得)
     */
    private String uploadKbn;

    /**
     * 画面から取得した処理開始年月をDateにしたもの
     */
    private Date startYm;

    /**
     * 処理開始年(画面から取得)
     */
    private String startYear;
    
    /**
     * 処理開始月(画面から取得)
     */
    private String startMonth;

    /**
     * 対象の事業部コード(画面から取得)
     */
    private String uploadDivisionCode;
        
    /**
     * アップロードしたExcelファイルから取得した受注ＳＰリスト
     * エラーチェック中に取得しておく。
     */
    private List<Map<String, Object>> jyuchuSpList;
    
    /**
     * アップロードしたExcelファイルから取得した受注NETデータ
     * エラーチェック中に取得しておく。
     */
    private List<Map<String, Object>> jyuchuNetList;
    
    /**
     * アップロードしたExcelファイルから取得したデータ
     * エラーチェック中に取得しておく。
     */
    private List<Map<String, Object>> dataList;

    /**
     * アップロードしたExcelファイルから取得したデータ
     * エラーチェック中に取得しておく。
     */
    private List<Map<String, Object>> dataList2;

    /**
     * アップロードしたExcelファイルから取得したデータ
     * エラーチェック中に取得しておく。
     */
    private List<Map<String, Object>> dataList3;

    /**
     * アップロードしたExcelファイルから取得したデータ
     * エラーチェック中に取得しておく。
     */
    private List<Map<String, Object>> dataList4;
    
    /**
     * 案件番号 期間損益_進行基準で使用
     */
    private String ankenId;
    
    /**
     * 履歴ID 期間損益_進行基準で使用
     */
    private String rirekiId;
    
    
    public FileItem getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(FileItem uploadFile) {
        this.uploadFile = uploadFile;
    }

    public String getUploadKbn() {
        return uploadKbn;
    }

    public void setUploadKbn(String uploadKbn) {
        this.uploadKbn = uploadKbn;
    }

    public String getUploadFlg() {
        return uploadFlg;
    }

    public void setUploadFlg(String uploadFlg) {
        this.uploadFlg = uploadFlg;
    }

    public Workbook getWorkBook() {
        return workBook;
    }

    public void setWorkBook(Workbook workBook) {
        this.workBook = workBook;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public List<Map<String, Object>> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map<String, Object>> dataList) {
        this.dataList = dataList;
    }
    
    public void addDataList(Map<String, Object> data) {
        if (this.dataList == null) {
            this.dataList = new ArrayList<>();
        }
        this.dataList.add(data);
    }
    
    public List<Map<String, Object>> getDataList2() {
        return dataList2;
    }

    public void setDataList2(List<Map<String, Object>> dataList2) {
        this.dataList2 = dataList2;
    }
    
    public void addDataList2(Map<String, Object> data2) {
        if (this.dataList2 == null) {
            this.dataList2 = new ArrayList<>();
        }
        this.dataList2.add(data2);
    }
    
    public List<Map<String, Object>> getDataList3() {
        return dataList3;
    }

    public void setDataList3(List<Map<String, Object>> dataList3) {
        this.dataList3 = dataList3;
    }
    
    public void addDataList3(Map<String, Object> data3) {
        if (this.dataList3 == null) {
            this.dataList3 = new ArrayList<>();
        }
        this.dataList3.add(data3);
    }

        public List<Map<String, Object>> getDataList4() {
        return dataList4;
    }

    public void setDataList4(List<Map<String, Object>> dataList4) {
        this.dataList4 = dataList4;
    }
    
    public void addDataList4(Map<String, Object> data4) {
        if (this.dataList4 == null) {
            this.dataList4 = new ArrayList<>();
        }
        this.dataList4.add(data4);
    }
    
    public Date getStartYm() {
        return startYm;
    }

    public void setStartYm(Date startYm) {
        this.startYm = startYm;
    }

    public String getUploadDivisionCode() {
        return uploadDivisionCode;
    }

    public void setUploadDivisionCode(String uploadDivisionCode) {
        this.uploadDivisionCode = uploadDivisionCode;
    }

    public String getAnkenId() {
        return ankenId;
    }

    public void setAnkenId(String ankenId) {
        this.ankenId = ankenId;
    }

    public String getRirekiId() {
        return rirekiId;
    }

    public void setRirekiId(String rirekiId) {
        this.rirekiId = rirekiId;
    }
       
    /**
     * @return the jyuchuSpList
     */
    public List<Map<String, Object>> getJyuchuSpList() {
        return jyuchuSpList;
    }

    /**
     * @param jyuchuSpList the jyuchuSpList to set
     */
    public void setJyuchuSpList(List<Map<String, Object>> jyuchuSpList) {
        this.jyuchuSpList = jyuchuSpList;
    }
    
    public void addJyuchuSpList(Map<String, Object> data) {
        if (this.jyuchuSpList == null) {
            this.jyuchuSpList = new ArrayList<>();
        }
        this.jyuchuSpList.add(data);
    }

    /**
     * @return the jyuchuNetList
     */
    public List<Map<String, Object>> getJyuchuNetList() {
        return jyuchuNetList;
    }

    /**
     * @param jyuchuNetList the jyuchuNetList to set
     */
    public void setJyuchuNetList(List<Map<String, Object>> jyuchuNetList) {
        this.jyuchuNetList = jyuchuNetList;
    }

    public void addJyuchuNetList(Map<String, Object> data) {
        if (this.jyuchuNetList == null) {
            this.jyuchuNetList = new ArrayList<>();
        }
        this.jyuchuNetList.add(data);
    }
        
}
