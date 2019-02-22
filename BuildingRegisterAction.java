/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.co.toshiba.cs.mainte.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.co.toshiba.cs.mainte.common.CellDateFormat;
import jp.co.toshiba.cs.mainte.common.GetGeocodingApiContext;
import jp.co.toshiba.cs.mainte.dto.PBuldingInfoUpdateDto;
import jp.co.toshiba.cs.mainte.dto.PDownloadDto;
import jp.co.toshiba.cs.mainte.entity.BuildingEntity;
import jp.co.toshiba.cs.mainte.entity.BuildingInfoEntity;
import jp.co.toshiba.cs.mainte.entity.BuildingRegisterEntity;
import jp.co.toshiba.cs.mainte.entity.ChildTdsEntity;
import jp.co.toshiba.cs.mainte.entity.CommodityEntity;
import jp.co.toshiba.cs.mainte.form.BuildingRegisterForm;
import jp.co.toshiba.cs.mainte.service.BuildingRegisterService;
import jp.co.toshiba.cs.mainte.service.CommodityService;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.annotation.Required;
import org.seasar.struts.upload.S2MultipartRequestHandler;
import org.seasar.struts.util.ActionMessagesUtil;
import org.seasar.struts.util.ResponseUtil;

import com.google.maps.GeocodingApi.ComponentFilter;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.GeocodingResult;

public class BuildingRegisterAction {

    /**
     * ActionForm
     */
    @ActionForm
    @Resource
    protected BuildingRegisterForm buildingRegisterForm;

    /**
     * Service
     */
    @Resource
    private BuildingRegisterService buildingRegisterService;

    /** リクエスト */
    private HttpServletRequest httpServletRequest;

    /** レスポンス */
    public HttpServletResponse response;

    @Required
    @Binding(bindingType = BindingType.NONE)
    public FormFile formFile;

    @Binding(bindingType = BindingType.NONE)
    public FormFile[] formFiles;

    public int eCount = 0;
    public int cCount = 0;

    @Resource
    protected CommodityService commodityService;

    final int READ_COL_AMOUNT = 60;
    final String COUNTRY_FILTER = "JP";
    final String COUNTRY_LANG   = "ja";

    /**
     * セッションの判定
     */
     public String session(HttpSession session){
         String ret = "OK";
         //セッションの判定
         if (session.getAttribute("userId") == null || session.getAttribute("userId").equals("") ) {
             //セッションが切れている
             session.invalidate();
             ret = "NG";
         }
         return ret;
     }
    /**
     * 初期表示
     */
    @Execute(validator = false)
    public String index() {
        String returnURL = "/buildingRegister/buildingRegister.jsp";
        //セッション情報の取得
        HttpSession session = httpServletRequest.getSession();
        String sessionChk = this.session(session);
        //セッションの判定
        if ( sessionChk != null && sessionChk.equals("OK") ) {
            buildingRegisterForm.insert03Flg = (String)session.getAttribute("insert03Flg");
            SizeLimitExceededException e = (SizeLimitExceededException) httpServletRequest.getAttribute(S2MultipartRequestHandler.SIZE_EXCEPTION_KEY);
                if (e != null) {
                    ActionMessages errors = new ActionMessages();
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "errors.upload.size",
                        new Object[] { e.getPermittedSize(), e.getActualSize() }));
                    ActionMessagesUtil.addErrors(httpServletRequest, errors);
                }
        } else {
            //セッションが切れているのでログイン画面へ
            returnURL = "/login/login.jsp";
        }
        return returnURL;
    }
    /**
     * Upload時処理
     */
    @Execute(validator = false)
    public String upload() {
        //EXCELの内容をチェック
        this.checkUploadFile(buildingRegisterForm.formFile);
        buildingRegisterForm.uploadFileName = buildingRegisterForm.formFile.getFileName();

        //チェック結果のセット
        StringBuilder buf = new StringBuilder(100);
        buf.append("{\"error\" : " + buildingRegisterForm.errorCount+ ",\"cell\" : " + buildingRegisterForm.cellCount + ",\"cellAll\" : " + buildingRegisterForm.cellAllCount +",\"duplicationCount\" : " + buildingRegisterForm.duplicationCount + "}");
        ResponseUtil.write(buf.toString(), "application/json", "UTF-8");
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute("BuildingRegisterForm",buildingRegisterForm);

        return null;
    }
    /**
     * EXCELの内容をチェック
     * @param file
     */
    private void checkUploadFile(FormFile file) {
        if(file==null){return;}
        if(file.getFileSize() == 0){return;}

        InputStream is = null;
        Workbook wbf = null;

        List<BuildingRegisterEntity> cellList = new ArrayList<BuildingRegisterEntity>();
        List<BuildingRegisterEntity> errorList = new ArrayList<BuildingRegisterEntity>();
        int errorCount = 0 ;
        int successCount = 0 ;
        int duplicationCount = 0;
        // チェックフラグ
        boolean sFlg = true;
        boolean nFlg = true;
        boolean lFlg = true;
        // 現在行
        Row curRow = null;
        // 開始行
        int i=2;
        // Excel終了フラグ
        boolean exeiFlg=true;

        try {
            is = file.getInputStream();
            wbf = WorkbookFactory.create(is);
            //データテンプレートの確認
            Sheet sheetTo = wbf.getSheet("建物一覧");

            while (exeiFlg) {
                // 現在行を取得
                curRow = sheetTo.getRow(i);

                // Cell is null then BREAK;
                if(this.yomikomiEndCh(curRow)){exeiFlg = false;break;}

                List<Integer> errCellList = new ArrayList<Integer>();

                // 必須チェック
                nFlg = this.checkNotNull(curRow, errCellList);

                // 桁数チェック
                lFlg = this.checkColunmLen(curRow, errCellList);

                // 重複チェック
                sFlg = buildingCheck(this.getCellValue(curRow.getCell(2)));

                // 重複件数の累加
                if (!sFlg) {
                    errCellList.add(2);
                    duplicationCount++;
                }
                // チェックエラーの場合
                if(!nFlg || !lFlg){
                    BuildingRegisterEntity errorEntity = this.getListValue(sheetTo, i);
                    // エラーセール位置リストを追加
                    errorEntity.errCellList = errCellList;
                    errorList.add(errorEntity);
                    errorCount++;
                }else{
                    cellList.add(this.getListValue(sheetTo,i));
                    successCount++;
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptedDocumentException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        buildingRegisterForm.cellList         = cellList;
        buildingRegisterForm.errorList        = errorList;
        buildingRegisterForm.cellCount        = successCount;     // 成功件数
        buildingRegisterForm.errorCount       = errorCount;       // エラー件数
        buildingRegisterForm.duplicationCount = duplicationCount; // 重複件数
        buildingRegisterForm.cellAllCount     = (i - 2) < 0 ? 0: i - 2; // 総件数
    }
    /**
     * 読み込み終了判定 true:読み込み終了
     */
    private boolean yomikomiEndCh(Row curRow){
        boolean bool = true;
        if(curRow!=null){
            for(int i=0; i<=READ_COL_AMOUNT; i++){
                if(this.getCellValue(curRow.getCell(i))!=null){
                    if(!this.getCellValue(curRow.getCell(i)).equals("")){
                        bool = false;
                        break;
                    }
                }
            }
        }
        return bool;
    }
    /**
     * 必須チェック
     * @param ckRow 現在行
     * @param errCellList エラー位置リスト
     * @return
     */
    private boolean checkNotNull(Row ckRow,  List<Integer> errCellList){
         boolean ret = true;
        int cellIndex;
        String str = "";
        // 建物名
        cellIndex = 1;
        str = this.getCellValue(ckRow.getCell(cellIndex));
        if ("".equals(str)) {
            ret = false;
            errCellList.add(cellIndex);
        }
        // 管理番号
        cellIndex = 27;
        str = this.getCellValue(ckRow.getCell(cellIndex));

        // 「(TELC)延床」、「(TELC)フロア数」、「(TELC)エレベータ台数」、「(TELC)エスカレータ台数」のいずれかをいっていたら必須とする。
		if (!"".equals(getCellValue(ckRow.getCell(28))) || !"".equals(getCellValue(ckRow.getCell(29)))
				|| !"".equals(getCellValue(ckRow.getCell(30))) || !"".equals(getCellValue(ckRow.getCell(31)))) {
            // （TELC）管理番号は必須とする
			if ("".equals(str)) {
                ret = false;
                errCellList.add(cellIndex);
            }
        }

        // 建物用途
        cellIndex = 23;
        str = this.getCellValue(ckRow.getCell(cellIndex));
        if ("".equals(str)) {
            ret = false;
            errCellList.add(cellIndex);
        }
        return ret;
    }

    /**
     * 桁数チェック
     * @param ckRow
     * @return
     */
    private boolean checkColunmLen(Row ckRow, List<Integer> errCellList){

        boolean ret = true;
        int i;
        i = 1;
        if (!this.lenCheck(300, this.getCellValue(ckRow.getCell(i)))) {
            ret = false;
        } // 建物名
        i=2;  if(!this.lenCheck(300,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //住所
/*
        i=3;  if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //昇降機
        i=4;  if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //空調
        i=5;  if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //電源
        i=6;  if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //照明制御
        i=7;  if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //EMS 2017/08/17 Add
        i=8;  if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //その他
*/
        i=9;  if(!this.isValidDate(ckRow.getCell(i))){errCellList.add(i); ret = false;}  //完成時期

        i=10; if(!this.lenCheck(300,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //施主
        i=11; if(!this.lenCheck(300,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //管理会社
//      i=12; if(this.lenck(300,this.getCellValue(ckRow.getCell(i)))){ret="1";}  //昇降機納入有無/保守管理有無
//      i=13; if(this.lenck(300,this.getCellValue(ckRow.getCell(i)))){ret="1";}  //空調納入有無/保守管理有無
//      i=14; if(this.lenck(300,this.getCellValue(ckRow.getCell(i)))){ret="1";}  //電源納入有無/保守管理有無
//      i=15; if(this.lenck(300,this.getCellValue(ckRow.getCell(i)))){ret="1";}  //照明制御納入有無/保守管理有無
//      i=16; if(this.lenck(300,this.getCellValue(ckRow.getCell(i)))){ret="1";}  //EMSの納入有無/保守管理有無 2017/08/17 Add
//      i=17; if(this.lenck(300,this.getCellValue(ckRow.getCell(i)))){ret="1";}  //その他納入有無/保守管理有無
        i=12; if(!this.lenCheck(300,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //その他付帯情報
        i=13; if(!this.lenCheck(150,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //A/C
        i=14; if(!this.lenCheck(150,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //G/C
        i=15; if(!this.lenCheck(150,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //S/C
//      i=16; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //東芝テナント有
        i=17; if(!this.lenCheck(300,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //昇降機保守契約先
        i=18; if(!this.lenCheck(300,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //空調保守契約先
        i=19; if(!this.lenCheck(300,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //電気設備保守契約先
        i=20; if(!this.lenCheck(300,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //照明制御保守契約先
        i=21; if(!this.lenCheck(300,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //EMSの保守契約先 2017/08/17 Add
        i=22; if(!this.lenCheck(300,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //その他保守契約先
        i=23; if(!this.lenCheck(300,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //建物用途
        i=24; if(!this.lenCheck( 30,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TCC)MJ数
        i=25; if(!this.lenCheck( 10,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TCC)製品区分
        i=26; if(!this.lenCheck(  6,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TCC)出荷年月
        i=27; if(!this.lenCheck( 20,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TELC)管理番号
        i=28; if(!this.lenCheck( 30,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TELC)延床
        i=29; if(!this.lenCheck( 30,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TELC)フロア数
        i=30; if(!this.lenCheck(300,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TELC)エレベータ台数
        i=31; if(!this.lenCheck(300,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TELC)エスカレータ台数
//      i=32; if(!this.isValidDate(ckRow.getCell(i))){errCellList.add(i); ret = false;}                      //(TELC)FS開始年月
/*
        i=32; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)特高
        i=33; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)高圧
        i=34; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)低圧
        i=35; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)モータ
        i=36; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)発電機
        i=37; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)中央監視
        i=38; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)計装
        i=39; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)ﾃﾚﾒｰﾀ
        i=40; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)水質計器
        i=41; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)ﾘﾌﾄ･ﾛｰﾌﾟｳｪｲ
        i=42; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)汚泥乾燥機（車）
        i=43; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)太陽光発電
        i=44; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)UPS
        i=45; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)ドライブ
        i=46; if(!this.lenCheck(  2,this.getCellValue(ckRow.getCell(i)))){errCellList.add(i); ret = false;}  //(TDS)その他
*/
//        i=46; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //テナントグループ会社名
//        i=56; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //脈有無
//        i=57; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //進捗
//        i=58; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //概要
//        i=55; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //昇降機の最終更新者
//        i=56; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //昇降機の最終更新日
//        i=57; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //空調の最終更新者
//        i=58; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //空調の最終更新日
//        i=59; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //電源の最終更新者
//        i=60; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //電源の最終更新日
//        i=61; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //照明の最終更新者
//        i=62; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //照明の最終更新日
//        i=63; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //EMSの最終更新者
//        i=64; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //EMSの最終更新日
//        i=65; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //その他の最終更新者
//        i=66; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //その他の最終更新日
//        i=67; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //システム連動の更新者
//        i=68; if(this.lenck(300,getCellValue(ckRow.getCell(i)))){ret="1";}  //システム連動の更新日
        return ret;
    }
    /**
     * 長さチェック
     * @param LenLimit 最大長さ
     * @param s チェック文字列
     * @return
     */
    private boolean lenCheck(int LenLimit,String s){
        int objlen = this.getLenB(s);
        if(objlen<=LenLimit) return true;
        return false;
    }
/*
    private int getLen(String s){
        if(s==null || s.equals("")){return 0;}
        return s.length();
    }
*/
    private int getLenB(String s){
        if(s==null || s.equals("")){return 0;}
        return s.getBytes(Charset.forName("UTF-8")).length;
    }

    private BuildingRegisterEntity getListValue(Sheet sheetTo,int RowCount){
        BuildingRegisterEntity  getList = new BuildingRegisterEntity();
        Row originalRow = sheetTo.getRow(RowCount);
        Cell originalCell = null;

        try {
            int maxCell = 47;
            for(int i = 0; i < maxCell;i++){
                originalCell = originalRow.getCell(i);

                if(originalCell == null){
                    break;
//                    originalCell.setCellValue("");
                }

                switch(i){
                    case 0:  getList.seq                       = this.getCellValue(originalCell);break; //No
                    case 1:  getList.buildingName              = this.getCellValue(originalCell);break; //建物名
                    case 2:  getList.address                   = this.getCellValue(originalCell);
                             getList.addressDef                = this.getCellValue(originalCell);
                             getList = this.geocode(getList);
                    break; //住所
                    case 3:  getList.elevatorDisp              = this.getCellValue(originalCell);break; //昇降機
                    case 4:  getList.airConditioningDisp       = this.getCellValue(originalCell);break; //空調
                    case 5:  getList.powerSupplyDisp           = this.getCellValue(originalCell);break; //電源
                    case 6:  getList.illuminationDisp          = this.getCellValue(originalCell);break; //照明制御
                    case 7:  getList.emsDisp                   = this.getCellValue(originalCell);break; //EMS 2017/08/17 Add
                    case 8:  getList.otherDisp                 = this.getCellValue(originalCell);break; //その他
                    case 9:  getList.completionDate            = this.getCellValue(originalCell);break; //完成時期
                    case 10: getList.owner                     = this.getCellValue(originalCell);break; //施主
                    case 11: getList.managementCompany         = this.getCellValue(originalCell);break; //管理会社
//                  case 12: getList.elevatorDisp              = this.getCellValue(originalCell);break; //昇降機納入有無/保守管理有無
//                  case 13: getList.airConditioningDisp       = this.getCellValue(originalCell);break; //空調納入有無/保守管理有無
//                  case 14: getList.powerSupplyDisp           = this.getCellValue(originalCell);break; //電源納入有無/保守管理有無
//                  case 15: getList.illuminationDisp          = this.getCellValue(originalCell);break; //照明制御納入有無/保守管理有無
//                  case 16: getList.emsDisp                   = this.getCellValue(originalCell);break; //EMSの納入有無/保守管理有無 2017/08/17 Add
//                  case 17: getList.otherDisp                 = this.getCellValue(originalCell);break; //その他納入有無/保守管理有無
                    case 12: getList.supplementaryInfo         = this.getCellValue(originalCell);break; //その他付帯情報
                    case 13: getList.doiAo                     = this.getCellValue(originalCell);break; //A/C
                    case 14: getList.doiGc                     = this.getCellValue(originalCell);break; //G/C
                    case 15: getList.doiSc                     = this.getCellValue(originalCell);break; //S/C
//                  this.setCellvalue(chkTenant(data.tenantFlg) = this.getCellValue(originalCell);break; //東芝テナント有
                    case 16: getList.tenantFlg                 = this.getCellValue(originalCell);break; //東芝テナント有
                    case 17: getList.elevatorMainte            = this.getCellValue(originalCell);break; //昇降機保守契約先
                    case 18: getList.airConditioningMainte     = this.getCellValue(originalCell);break; //空調保守契約先
                    case 19: getList.powerSupplyMainte         = this.getCellValue(originalCell);break; //電気設備保守契約先
                    case 20: getList.illuminationMainte        = this.getCellValue(originalCell);break; //照明制御保守契約先
                    case 21: getList.emsMainte                 = this.getCellValue(originalCell);break; //EMSの保守契約先 2017/08/17 Add
                    case 22: getList.otherMainte               = this.getCellValue(originalCell);break; //その他保守契約先
//                  this.setCellvalue(targetListService.callBuildingInfoProcedure(data.buildingUsesCode) = getCellValue(originalCell);break; //建物用途
                    case 23: getList.buildingUses              = this.getCellValue(originalCell);break; //建物用途
//                  case 29: getList.buildingUsesName          = this.getCellValue(originalCell);break; //各社建物用途
                    case 24: getList.floorScale                = this.getCellValue(originalCell);break; //(TCC)MJ数
                    case 25: getList.productKbn                = this.getCellValue(originalCell);break; //(TCC)製品区分
                    case 26: getList.shippingDate              = this.getCellValue(originalCell);break; //(TCC)出荷年月
                    case 27: getList.doiControlNumber          = this.getCellValue(originalCell);break; //(TELC)管理番号
                    case 28: getList.doiTotalFloorSpace        = this.getCellValue(originalCell);break; //(TELC)延床
                    case 29: getList.floorScale2               = this.getCellValue(originalCell);break; //(TELC)フロア数
                    case 30: getList.doiElevator               = this.getCellValue(originalCell);break; //(TELC)エレベータ台数
                    case 31: getList.escalator                 = this.getCellValue(originalCell);break; //(TELC)エスカレータ台数
//                  case 32: getList.doiCompletionDate         = this.getCellValue(originalCell);break; //(TELC)FS開始年月
                    case 32: getList.doiExhighVoltage          = this.getCellValue(originalCell);break; //(TDS)特高
                    case 33: getList.doiHighPressure           = this.getCellValue(originalCell);break; //(TDS)高圧
                    case 34: getList.doiLowPressure            = this.getCellValue(originalCell);break; //(TDS)低圧
                    case 35: getList.doiMotor                  = this.getCellValue(originalCell);break; //(TDS)モータ
                    case 36: getList.doiPrivatePowerGeneration = this.getCellValue(originalCell);break; //(TDS)発電機
                    case 37: getList.doiCentralMonitoring      = this.getCellValue(originalCell);break; //(TDS)中央監視
                    case 38: getList.doiInstrumentation        = this.getCellValue(originalCell);break; //(TDS)計装
                    case 39: getList.doiTelemetry              = this.getCellValue(originalCell);break; //(TDS)ﾃﾚﾒｰﾀ
                    case 40: getList.doiWaterQualityMeter      = this.getCellValue(originalCell);break; //(TDS)水質計器
                    case 41: getList.doiLiftRopeway            = this.getCellValue(originalCell);break; //(TDS)ﾘﾌﾄ･ﾛｰﾌﾟｳｪｲ
                    case 42: getList.doiSludgeDryingMachine    = this.getCellValue(originalCell);break; //(TDS)汚泥乾燥機（車）
                    case 43: getList.doiSolarPower             = this.getCellValue(originalCell);break; //(TDS)太陽光発電
                    case 44: getList.doiUps                    = this.getCellValue(originalCell);break; //(TDS)UPS
                    case 45: getList.doiDrive                  = this.getCellValue(originalCell);break; //(TDS)ドライブ
                    case 46: getList.doiOther                  = this.getCellValue(originalCell);break; //(TDS)その他
//                  case 48: getList.doiCompanyName            = this.getCellValue(originalCell);break; //テナントグループ会社名
//                  case 56: getList.hopeFlg                   = this.getCellValue(originalCell);break; //脈有無
//                  case 57: getList.progressFlg               = this.getCellValue(originalCell);break; //進捗
//                  case 58: getList.overView                  = this.getCellValue(originalCell);break; //概要
//                  case 55: getList.doiElevatorUpdateId       = this.getCellValue(originalCell);break; //昇降機の最終更新者
//                  case 56: getList.doiElevatorUpdateDate     = this.getCellValue(originalCell);break; //昇降機の最終更新日
//                  case 57: getList.doiAirConUpdateId         = this.getCellValue(originalCell);break; //空調の最終更新者
//                  case 58: getList.doiAirConUpdateDate       = this.getCellValue(originalCell);break; //空調の最終更新日
//                  case 59: getList.doiPowerSupplyUpdateId    = this.getCellValue(originalCell);break; //電源の最終更新者
//                  case 60: getList.doiPowerSupplyUpdateDate  = this.getCellValue(originalCell);break; //電源の最終更新日
//                  case 61: getList.doiIlluminationUpdateId   = this.getCellValue(originalCell);break; //照明の最終更新者
//                  case 62: getList.doiIlluminationUpdateDate = this.getCellValue(originalCell);break; //照明の最終更新日
//                  case 63: getList.doiEmsUpdateId            = this.getCellValue(originalCell);break; //EMSの最終更新者
//                  case 64: getList.doiEmsUpdateDate          = this.getCellValue(originalCell);break; //EMSの最終更新日
//                  case 65: getList.doiOtherUpdateId          = this.getCellValue(originalCell);break; //その他の最終更新者
//                  case 66: getList.doiOtherUpdateDate        = this.getCellValue(originalCell);break; //その他の最終更新日
//                  case 67: getList.doiRelationId             = this.getCellValue(originalCell);break; //システム連動の更新者
//                  case 68: getList.doiRelationDate           = this.getCellValue(originalCell);break; //システム連動の更新日
                }
            }
        } catch (Exception e) {
            // 自動生成された catch ブロック
            e.printStackTrace();
        }
        return getList;
    }
    private String msterCk(String ckStr,LinkedHashMap<String, String> ckList){
        String ret = "";
        for(Iterator<Map.Entry<String, String>> iterator = ckList.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> entry = iterator.next();
            if(entry.getValue().equals(ckStr)){
                ret = entry.getKey();
                break;
            }
        }
        return ret;
    }
    /**
     * セールの内容を取得
     * @param c セール
     * @return
     */
    private String getCellValue(Cell c){
        String str = "";
        if(c == null)return str;

        switch(c.getCellType()){
            // 文字列の場合
            case Cell.CELL_TYPE_STRING:
                str = c.getStringCellValue();
                break;
            // 数字の場合
            case Cell.CELL_TYPE_NUMERIC:
                // Excel標準の日付書式を使っている場合
                if (DateUtil.isCellDateFormatted(c)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    str = sdf.format(c.getDateCellValue());
                } else {
                    if (CellDateFormat.contains(c.getCellStyle().getDataFormat())) {
                        Date theDate = c.getDateCellValue();
                        DateFormat dateFormat = CellDateFormat.getFormt(c.getCellStyle().getDataFormat()).getDateFormat();
                        str = dateFormat.format(theDate);
                    }else{
                        DecimalFormat decimalFormat = new DecimalFormat("#");
                        str = decimalFormat.format(c.getNumericCellValue());
                    }
                }
                break;
            // 空白の場合
            case Cell.CELL_TYPE_BLANK:
                str = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                str = Boolean.toString(c.getBooleanCellValue());
                break;
            // 上記以外の場合
            default:
                str = "";
                // case Cell.CELL_TYPE_FORMULA:
                // System.out.println("Formula:" + c.getCellFormula());
                // break;
                // case Cell.CELL_TYPE_ERROR :
                // System.out.println("Error:" + c.getErrorCellValue());
                // break;
        }
        return str;
    }
    /**
     *  アップロード用Excelシートダウンロード<br>
     *  流用元：TKMNNL226Excl
     *  @return view()
     *  @throws exception
     */
    @Execute(validator = false)
    public String excelFileDownload() throws Exception{
        try{
            FileInputStream is = null;
            Workbook wbf = null;

            try{
                //テンプレートファイル読込
                String filePath = "";
                String fileName = "UploadListSheet.xlsx";
                ServletContext application = httpServletRequest.getSession().getServletContext();
                filePath = application.getRealPath("/WEB-INF/data/" + fileName);

                File f = new File(filePath);
                if(!f.exists()){ //ファイルやディレクトリがあるか調べる
                    throw new FileNotFoundException();
                }

                is = new FileInputStream(filePath);
                wbf = WorkbookFactory.create(is);

                //データテンプレートの確認
//              Sheet sheetTo = wbf.getSheet("建物一覧");

                //EXCELダウンロード
                ResponseUtil.getResponse().setContentType("application/vnd.ms-excel");
                ResponseUtil.getResponse().setHeader("Content-Disposition","attachment;filename=" + new String(fileName.getBytes("Shift_JIS"), "ISO8859_1" ));

                try{
                    wbf.write(ResponseUtil.getResponse().getOutputStream());
                }catch (IOException e) {
                    e.printStackTrace();
                    ResponseUtil.getResponse().reset();
                    ResponseUtil.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    System.out.println("EXCELデータの取込に失敗しました。");
                }

            }catch(FileNotFoundException e){
                e.printStackTrace();
                ResponseUtil.getResponse().reset();
                ResponseUtil.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                System.out.println("データファイルが存在しませんでした。");

            }catch(IOException e){
                e.printStackTrace();
                ResponseUtil.getResponse().reset();
                ResponseUtil.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                System.out.println("EXCELデータの取込に失敗しました。");

            }finally{
                try {
                    if (is != null) {
                        is.close();
                    }
                    wbf.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            ResponseUtil.getResponse().reset();
            ResponseUtil.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.out.println(e.getLocalizedMessage());
            System.out.println("処理が失敗しました。");
        }
        return null;
    }
    /**
     * セルに値をセットする（文字列）
     * @param value
     * @param dataRow
     * @param cellNo
     * @return
     */
    private Cell setCellvalue(String value, Row dataRow, int cellNo, Row origin){
        if (dataRow == null){
            return null;
        }
        Cell dataCell = dataRow.getCell((short)cellNo);
        if (dataCell == null){
            dataCell = dataRow.createCell((short)cellNo);
        }
        //dataCell.setEncoding(HSSFCell.ENCODING_UTF_16);
        //dataCell.setCellType(Cell.CELL_TYPE_STRING);

        // テンプレート行の同一列の書式をコピー
        dataCell.setCellType(origin.getCell(cellNo).getCellType());
        dataCell.setCellStyle(origin.getCell(cellNo).getCellStyle());

        // 値を設定
        dataCell.setCellValue(value);
        return dataCell;
    }
    /**
     * HttpServlet getter.
     * @return requestオブジェクト
     */
    public HttpServletRequest getRequest(){
        return httpServletRequest;
    }
    /**
     * HttpServlet setter.
     * @param request requestオブジェクト
     */
    public void setRequest(HttpServletRequest httpServletRequest){
        this.httpServletRequest = httpServletRequest;
    }
    /**
     * データ登録処理
     * @return
     */
    @Execute(validator = false)
    public String dataRegister() {
        HttpSession session = httpServletRequest.getSession();
        buildingRegisterForm = (BuildingRegisterForm) session.getAttribute("BuildingRegisterForm");
        String userId = (String) session.getAttribute("userId");// ユーザＩＤ
        List<BuildingRegisterEntity> cellList   = buildingRegisterForm.cellList;
        LinkedHashMap<String,String> tdsList    = buildingRegisterService.getTdsCommodityList();
        LinkedHashMap<String,String> shozaiList = buildingRegisterService.getShozaiBunruiList();
        LinkedHashMap<String,String> TYotoList  = null;
        LinkedHashMap<String,String> KTYotoList = null;
        try {
            TYotoList = buildingRegisterService.getBuildUseList();
            KTYotoList = buildingRegisterService.getBuildUseCompList();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        //Calendarクラスのオブジェクトを生成する
//      Calendar cl = Calendar.getInstance();
        //SimpleDateFormatクラスでフォーマットパターンを設定する
//      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//      String Sdate = sdf.format(cl.getTime());

        for(int i=0; i < cellList.size();i++){
            try {
                // ユーザIDを設定
                buildingRegisterForm.userId = userId;

                // 建物ID
                String buildingId = buildingRegisterService.getNewBuildingId();

                // 建物(CSS_T_BUILDING)登録
                BuildingEntity buildingEntity = this.convFormToBuilding(cellList.get(i));
                buildingEntity.bdgBuildingId = buildingId;
                buildingRegisterService.insertBuilding(buildingEntity);

                // 建物詳細(CSS_T_BUILDING_INFO)を登録
                BuildingInfoEntity buildingInfoEntity = this.convFormToBuildingInfo(cellList.get(i),shozaiList);
                buildingInfoEntity.bdiBuildingId = buildingId;
                buildingRegisterService.insertBuildingInfo(buildingInfoEntity);

                // 登録用の製品別契約区分を作成
                ChildTdsEntity childTdsEntity = this.convFormChildTds(cellList.get(i),tdsList);
                childTdsEntity.pcdBuildingId = buildingId;

                String cmdFloorScale = "";
                String cmdFloorScale2 = "";
                boolean CommodityInsBool = true;

                CommodityEntity CustomerCommodityEntity = this.convFormToCommodity(cellList.get(i), "00",TYotoList,KTYotoList);
                if( "".equals(CustomerCommodityEntity.cmdOwner)
                &&  "".equals(CustomerCommodityEntity.cmdAo)
                &&  "".equals(CustomerCommodityEntity.cmdGc)
                &&  "".equals(CustomerCommodityEntity.cmdSc)
                ){
                    // すべて空白の場合、登録なし
                }else{
                    CustomerCommodityEntity.buildingId = buildingId;
                    buildingRegisterService.insertCommodityCustomer(CustomerCommodityEntity, userId);
                }
//              if (!getComCodeBySign(cellList.get(i).elevatorDisp).equals("00") ) {
                if (!buildingInfoEntity.bdiElevator.equals("") && !buildingInfoEntity.bdiElevator.equals("00") ) {
                    // 昇降機情報を登録
                    CommodityEntity eleCommodityEntity = this.convFormToCommodity(cellList.get(i), "01",TYotoList,KTYotoList);
                    eleCommodityEntity.buildingId = buildingId;
                    cmdFloorScale2 = eleCommodityEntity.cmdFloorScale;
                    if(buildingInfoEntity.bdiElevator.equals("12")
                    || buildingInfoEntity.bdiElevator.equals("13")
                    || buildingInfoEntity.bdiElevator.equals("23")
                    ){
                        eleCommodityEntity.makerCode = "01";
                        eleCommodityEntity.maintenanceCode = "";
                        buildingRegisterService.insertCommodity(eleCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiElevator.equals("11")
                    || buildingInfoEntity.bdiElevator.equals("12")
                    || buildingInfoEntity.bdiElevator.equals("21")
                    || buildingInfoEntity.bdiElevator.equals("22")
                    ){
                        eleCommodityEntity.makerCode = "01";
                        eleCommodityEntity.maintenanceCode = "01";
                        buildingRegisterService.insertCommodity(eleCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiElevator.equals("22")
                    || buildingInfoEntity.bdiElevator.equals("23")
                    || buildingInfoEntity.bdiElevator.equals("32")
                    || buildingInfoEntity.bdiElevator.equals("33")
                    ){
                        eleCommodityEntity.makerCode = "";
                        eleCommodityEntity.maintenanceCode = "";
                        eleCommodityEntity.cmdSpecifications      = ""; // 仕様・数量(Elevator)
//                      eleCommodityEntity.cmdElevatorMainte      = ""; // 昇降機の保守契約先
                        eleCommodityEntity.cmdControlNumber       = ""; // 管理番号
                        eleCommodityEntity.cmdTotalFloorSpace     = ""; // 建物延床面積
//                      eleCommodityEntity.cmdMaintenanceContract = ""; // 保守契約先
                        eleCommodityEntity.cmdFloorScale          = ""; // フロア数・規模
                        buildingRegisterService.insertCommodity(eleCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiElevator.equals("21")
                    || buildingInfoEntity.bdiElevator.equals("31")
                    || buildingInfoEntity.bdiElevator.equals("32")
                    ){
                        eleCommodityEntity.makerCode = "";
                        eleCommodityEntity.maintenanceCode = "01";
                        eleCommodityEntity.cmdSpecifications      = ""; // 仕様・数量(Elevator)
//                      eleCommodityEntity.cmdElevatorMainte      = ""; // 昇降機の保守契約先
                        eleCommodityEntity.cmdControlNumber       = ""; // 管理番号
                        eleCommodityEntity.cmdTotalFloorSpace     = ""; // 建物延床面積
//                      eleCommodityEntity.cmdMaintenanceContract = ""; // 保守契約先
                        eleCommodityEntity.cmdFloorScale          = ""; // フロア数・規模
                        buildingRegisterService.insertCommodity(eleCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                }
//              if (!getComCodeBySign(cellList.get(i).airConditioningDisp).equals("00") ) {
                if (!buildingInfoEntity.bdiAirCondition.equals("") && !buildingInfoEntity.bdiAirCondition.equals("00") ) {
                    // 空調情報を登録
                    CommodityEntity airCommodityEntity = this.convFormToCommodity(cellList.get(i), "02",TYotoList,KTYotoList);
                    airCommodityEntity.buildingId = buildingId;
                    cmdFloorScale = airCommodityEntity.cmdFloorScale;
                    if(buildingInfoEntity.bdiAirCondition.equals("12")
                    || buildingInfoEntity.bdiAirCondition.equals("13")
                    || buildingInfoEntity.bdiAirCondition.equals("23")
                    ){
                        airCommodityEntity.makerCode = "01";
                        airCommodityEntity.maintenanceCode = "";
                        buildingRegisterService.insertCommodity(airCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiAirCondition.equals("11")
                    || buildingInfoEntity.bdiAirCondition.equals("12")
                    || buildingInfoEntity.bdiAirCondition.equals("21")
                    || buildingInfoEntity.bdiAirCondition.equals("22")
                    ){
                        airCommodityEntity.makerCode = "01";
                        airCommodityEntity.maintenanceCode = "01";
                        buildingRegisterService.insertCommodity(airCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiAirCondition.equals("22")
                    || buildingInfoEntity.bdiAirCondition.equals("23")
                    || buildingInfoEntity.bdiAirCondition.equals("32")
                    || buildingInfoEntity.bdiAirCondition.equals("33")
                    ){
                        airCommodityEntity.makerCode = "";
                        airCommodityEntity.maintenanceCode = "";
//                      airCommodityEntity.cmdAirConditionrMainte = ""; // 空調の保守契約先
                        airCommodityEntity.cmdShippingDate        = ""; // 出荷年月
                        airCommodityEntity.cmdProductKbn          = ""; // 製品区分
                        airCommodityEntity.cmdFloorScale          = ""; // フロア数・規模
                        buildingRegisterService.insertCommodity(airCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiAirCondition.equals("21")
                    || buildingInfoEntity.bdiAirCondition.equals("31")
                    || buildingInfoEntity.bdiAirCondition.equals("32")
                    ){
                        airCommodityEntity.makerCode = "";
                        airCommodityEntity.maintenanceCode = "01";
//                      airCommodityEntity.cmdAirConditionrMainte = ""; // 空調の保守契約先
                        airCommodityEntity.cmdShippingDate        = ""; // 出荷年月
                        airCommodityEntity.cmdProductKbn          = ""; // 製品区分
                        airCommodityEntity.cmdFloorScale          = ""; // フロア数・規模
                        buildingRegisterService.insertCommodity(airCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }

                }
//              if (!getComCodeBySign(cellList.get(i).powerSupplyDisp).equals("00") ) {
                if (!buildingInfoEntity.bdiPowerSupply.equals("") && !buildingInfoEntity.bdiPowerSupply.equals("00") ) {
                    // 電気設備情報を登録
                    CommodityEntity powerCommodityEntity = this.convFormToCommodity(cellList.get(i), "03",TYotoList,KTYotoList);
                    powerCommodityEntity.buildingId = buildingId;
                    if(buildingInfoEntity.bdiPowerSupply.equals("22")
                    || buildingInfoEntity.bdiPowerSupply.equals("23")
                    || buildingInfoEntity.bdiPowerSupply.equals("32")
                    || buildingInfoEntity.bdiPowerSupply.equals("33")
                    ){
                        powerCommodityEntity.makerCode = "";
                        powerCommodityEntity.maintenanceCode = "";
                        buildingRegisterService.insertCommodity(powerCommodityEntity, userId);
                        buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiPowerSupply.equals("21")
                    || buildingInfoEntity.bdiPowerSupply.equals("31")
                    || buildingInfoEntity.bdiPowerSupply.equals("32")
                    ){
                        powerCommodityEntity.makerCode = "";
                        powerCommodityEntity.maintenanceCode = "01";
                        buildingRegisterService.insertCommodity(powerCommodityEntity, userId);
                        buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiPowerSupply.equals("12")
                    || buildingInfoEntity.bdiPowerSupply.equals("13")
                    || buildingInfoEntity.bdiPowerSupply.equals("23")
                    ){
                        powerCommodityEntity.makerCode = "01";
                        powerCommodityEntity.maintenanceCode = "";
                        buildingRegisterService.insertCommodity(powerCommodityEntity, userId);
                        buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiPowerSupply.equals("11")
                    || buildingInfoEntity.bdiPowerSupply.equals("12")
                    || buildingInfoEntity.bdiPowerSupply.equals("21")
                    || buildingInfoEntity.bdiPowerSupply.equals("22")
                    ){
                        powerCommodityEntity.makerCode = "01";
                        powerCommodityEntity.maintenanceCode = "01";
                        buildingRegisterService.insertCommodity(powerCommodityEntity, userId);
                        buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                }
//              if (!getComCodeBySign(cellList.get(i).illuminationDisp).equals("00") ) {
                if (!buildingInfoEntity.bdiIllumination.equals("") && !buildingInfoEntity.bdiIllumination.equals("00")) {
                    // 照明情報を登録
                    CommodityEntity illuCommodityEntity = this.convFormToCommodity(cellList.get(i), "04",TYotoList,KTYotoList);
                    illuCommodityEntity.buildingId = buildingId;
                    if(buildingInfoEntity.bdiIllumination.equals("22")
                    || buildingInfoEntity.bdiIllumination.equals("23")
                    || buildingInfoEntity.bdiIllumination.equals("32")
                    || buildingInfoEntity.bdiIllumination.equals("33")
                    ){
                        illuCommodityEntity.makerCode = "";
                        illuCommodityEntity.maintenanceCode = "";
                        buildingRegisterService.insertCommodity(illuCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiIllumination.equals("21")
                    || buildingInfoEntity.bdiIllumination.equals("31")
                    || buildingInfoEntity.bdiIllumination.equals("32")
                    ){
                        illuCommodityEntity.makerCode = "";
                        illuCommodityEntity.maintenanceCode = "01";
                        buildingRegisterService.insertCommodity(illuCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiIllumination.equals("12")
                    || buildingInfoEntity.bdiIllumination.equals("13")
                    || buildingInfoEntity.bdiIllumination.equals("23")
                    ){
                        illuCommodityEntity.makerCode = "01";
                        illuCommodityEntity.maintenanceCode = "";
                        buildingRegisterService.insertCommodity(illuCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiIllumination.equals("11")
                    || buildingInfoEntity.bdiIllumination.equals("12")
                    || buildingInfoEntity.bdiIllumination.equals("21")
                    || buildingInfoEntity.bdiIllumination.equals("22")
                    ){
                        illuCommodityEntity.makerCode = "01";
                        illuCommodityEntity.maintenanceCode = "01";
                        buildingRegisterService.insertCommodity(illuCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                }
//              if (!getComCodeBySign(cellList.get(i).emsDisp).equals("00") ) {
                if (!buildingInfoEntity.bdiEms.equals("") && !buildingInfoEntity.bdiEms.equals("00")) {
                    // EMS情報を登録
                    CommodityEntity emsCommodityEntity = this.convFormToCommodity(cellList.get(i), "05",TYotoList,KTYotoList);
                    emsCommodityEntity.buildingId = buildingId;
                    if(buildingInfoEntity.bdiEms.equals("22")
                    || buildingInfoEntity.bdiEms.equals("23")
                    || buildingInfoEntity.bdiEms.equals("32")
                    || buildingInfoEntity.bdiEms.equals("33")
                    ){
                        emsCommodityEntity.makerCode = "";
                        emsCommodityEntity.maintenanceCode = "";
                        buildingRegisterService.insertCommodity(emsCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiEms.equals("21")
                    || buildingInfoEntity.bdiEms.equals("31")
                    || buildingInfoEntity.bdiEms.equals("32")
                    ){
                        emsCommodityEntity.makerCode = "";
                        emsCommodityEntity.maintenanceCode = "01";
                        buildingRegisterService.insertCommodity(emsCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiEms.equals("12")
                    || buildingInfoEntity.bdiEms.equals("13")
                    || buildingInfoEntity.bdiEms.equals("23")
                    ){
                        emsCommodityEntity.makerCode = "01";
                        emsCommodityEntity.maintenanceCode = "";
                        buildingRegisterService.insertCommodity(emsCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiEms.equals("11")
                    || buildingInfoEntity.bdiEms.equals("12")
                    || buildingInfoEntity.bdiEms.equals("21")
                    || buildingInfoEntity.bdiEms.equals("22")
                    ){
                        emsCommodityEntity.makerCode = "01";
                        emsCommodityEntity.maintenanceCode = "01";
                        buildingRegisterService.insertCommodity(emsCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                }
//              if (!getComCodeBySign(cellList.get(i).otherDisp).equals("00") ) {
                if (!buildingInfoEntity.bdiOther.equals("") && !buildingInfoEntity.bdiOther.equals("00")) {
                    // その他情報を登録
                    CommodityEntity otherCommodityEntity = this.convFormToCommodity(cellList.get(i), "06",TYotoList,KTYotoList);
                    otherCommodityEntity.buildingId = buildingId;
                    if(buildingInfoEntity.bdiOther.equals("22")
                    || buildingInfoEntity.bdiOther.equals("23")
                    || buildingInfoEntity.bdiOther.equals("32")
                    || buildingInfoEntity.bdiOther.equals("33")
                    ){
                        otherCommodityEntity.makerCode = "";
                        otherCommodityEntity.maintenanceCode = "";
                        buildingRegisterService.insertCommodity(otherCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiOther.equals("21")
                    || buildingInfoEntity.bdiOther.equals("31")
                    || buildingInfoEntity.bdiOther.equals("32")
                    ){
                        otherCommodityEntity.makerCode = "";
                        otherCommodityEntity.maintenanceCode = "01";
                        buildingRegisterService.insertCommodity(otherCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiOther.equals("12")
                    || buildingInfoEntity.bdiOther.equals("13")
                    || buildingInfoEntity.bdiOther.equals("23")
                    ){
                        otherCommodityEntity.makerCode = "01";
                        otherCommodityEntity.maintenanceCode = "";
                        buildingRegisterService.insertCommodity(otherCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                    if(buildingInfoEntity.bdiOther.equals("11")
                    || buildingInfoEntity.bdiOther.equals("12")
                    || buildingInfoEntity.bdiOther.equals("21")
                    || buildingInfoEntity.bdiOther.equals("22")
                    ){
                        otherCommodityEntity.makerCode = "01";
                        otherCommodityEntity.maintenanceCode = "01";
                        buildingRegisterService.insertCommodity(otherCommodityEntity, userId);
//                      buildingRegisterService.insertProductContract(childTdsEntity,userId);
                        CommodityInsBool = false;
                    }
                }
                if(CommodityInsBool){
                    CommodityEntity noneCommodityEntity = this.convFormToCommodity(cellList.get(i), "07",TYotoList,KTYotoList);
                    noneCommodityEntity.buildingId = buildingId;
                    buildingRegisterService.insertCommodityDmy(noneCommodityEntity,userId);
                }
                // 商材処理後、建物情報の商材を再計算するプロシージャをコール
                PBuldingInfoUpdateDto retDto = commodityService.callBuildingInfoProcedure(buildingId);
                if(!"0".equals(retDto.OutErrbuf)){
//                    commodityForm.errorMessage = "建物情報の再取得でエラーが発生しました。\n：" + retDto.OutErrbuf;
                }

                // 商材処理後、ダウンロードデータを更新するプロシージャをコール
                PDownloadDto retDownloadDto = commodityService.callDownLoadDataProcedure(buildingId);
                if(!"0".equals(retDownloadDto.OutErrbuf)){
//                    commodityForm.errorMessage = "ダウンロードデータ更新でエラーが発生しました。\n：" + retDownloadDto.OutErrbuf;
                }
                buildingRegisterService.uploadDL(cmdFloorScale,cmdFloorScale2,buildingId);

            } catch (Exception e) {
                buildingRegisterForm.errorMessage = e.getMessage();
            }
        }
        StringBuilder buf = new StringBuilder(100);
        buf.append("{\"error\" : " + buildingRegisterForm.errorCount+ ",\"cell\" : " + buildingRegisterForm.cellAllCount + "}");
        ResponseUtil.write(buf.toString(), "application/json", "UTF-8");
        session.setAttribute("BuildingRegisterForm",buildingRegisterForm);
        return null;
    }

    /**
     * 建物登録用エンティティを作成
     * @param userId
     */
    private BuildingEntity convFormToBuilding(BuildingRegisterEntity buildingRegisterEntity){
        BuildingEntity buildingEntity = new BuildingEntity();
        buildingEntity.bdgBuildingName   = buildingRegisterEntity.buildingName;     // ビル名を設定
        buildingEntity.bdgAddressDefault = buildingRegisterEntity.addressDef;       // 住所(初期)を設定
        buildingEntity.bdgAdress         = buildingRegisterEntity.address;          // 住所を設定
        buildingEntity.bdgPrefectures    = buildingRegisterEntity.Prefectures;      // 都道府県を設定
        buildingEntity.bdgCity           = buildingRegisterEntity.City;             // 市区郡を設定
        buildingEntity.bdgVillage        = buildingRegisterEntity.Village;          // 大字を設定
        buildingEntity.bdgItem1          = buildingRegisterEntity.Item1;            // 明細１を設定
        buildingEntity.bdgItem2          = buildingRegisterEntity.Item2;            // 明細２を設定
        buildingEntity.bdgItem3          = buildingRegisterEntity.Item3;            // 明細３を設定
        buildingEntity.bdgItem4          = buildingRegisterEntity.Item4;            // 明細４を設定
        buildingEntity.bdgItem5          = buildingRegisterEntity.Item5;            // 明細５を設定
        buildingEntity.bdgLatitude       = buildingRegisterEntity.Latitude;         // 緯度を設定
        buildingEntity.bdgLongitude      = buildingRegisterEntity.Longitude;        // 経度を設定
        buildingEntity.bdgManagementNo   = buildingRegisterEntity.doiControlNumber; //（TELC）管理番号
        buildingEntity.userId            = buildingRegisterForm.userId;             // ユーザIDを設定
        return buildingEntity;
    }
    /**
     * 建物詳細情報登録用のエンティティを作成
     * @param newBuildingMapForm
     * @return
     */
    private BuildingInfoEntity convFormToBuildingInfo(BuildingRegisterEntity buildingRegisterEntity,LinkedHashMap<String, String> shozaiList){
        BuildingInfoEntity buildingInfoEntity = new BuildingInfoEntity();
        buildingInfoEntity.bdiElevator           = this.msterCk(buildingRegisterEntity.elevatorDisp,shozaiList);        // 昇降機
        buildingInfoEntity.bdiAirCondition       = this.msterCk(buildingRegisterEntity.airConditioningDisp,shozaiList); // 空調
        buildingInfoEntity.bdiPowerSupply        = this.msterCk(buildingRegisterEntity.powerSupplyDisp,shozaiList);     // 電源
        buildingInfoEntity.bdiIllumination       = this.msterCk(buildingRegisterEntity.illuminationDisp,shozaiList);    // 照明
        buildingInfoEntity.bdiEms                = this.msterCk(buildingRegisterEntity.emsDisp,shozaiList);             // EMS
        buildingInfoEntity.bdiOther              = this.msterCk(buildingRegisterEntity.otherDisp,shozaiList);           // その他
        buildingInfoEntity.bdiCompletionDate     = buildingRegisterEntity.completionDate;                               // 完成時期
        buildingInfoEntity.bdiOwner              = buildingRegisterEntity.owner;                                        // 施主
        buildingInfoEntity.bdiManagementCompany  = buildingRegisterEntity.managementCompany;                            // 管理会社
        buildingInfoEntity.bdiSupplementaryInfo  = buildingRegisterEntity.supplementaryInfo;                            // その他付帯情報
        buildingInfoEntity.bdiTenantFlg          = this.chkTenant(buildingRegisterEntity.tenantFlg);                    // 東芝テナントフラグ

        buildingInfoEntity.bdiElevatorMainte     = buildingRegisterEntity.elevatorMainte;                               // 昇降機の保守契約先
        buildingInfoEntity.bdiAirConditionMainte = buildingRegisterEntity.airConditioningMainte;                        // 空調の保守契約先
        buildingInfoEntity.bdiPowerSupplyMainte  = buildingRegisterEntity.powerSupplyMainte;                            // 電源の保守契約先
        buildingInfoEntity.bdiIlluminationMainte = buildingRegisterEntity.illuminationMainte;                           // 照明の保守契約先
        buildingInfoEntity.bdiEmsMainte          = buildingRegisterEntity.emsMainte;                                    // EMSの保守契約先
        buildingInfoEntity.bdiOtherMainte        = buildingRegisterEntity.otherMainte;                                  // その他の保守契約先
        buildingInfoEntity.userId                = buildingRegisterForm.userId;                                         // ユーザIDを設定
        return buildingInfoEntity;
    }

    /**
     * 製品別契約区分登録用のエンティティを作成
     * @param newBuildingMapForm
     * @return
     */
    private ChildTdsEntity convFormChildTds(BuildingRegisterEntity buildingRegisterEntity,LinkedHashMap<String, String> tdsList){
        ChildTdsEntity childTdsEntity = new ChildTdsEntity();
        childTdsEntity.pcdExhighVoltage          =this.msterCk(buildingRegisterEntity.doiExhighVoltage,tdsList);          //特高
        childTdsEntity.pcdHighPressure           =this.msterCk(buildingRegisterEntity.doiHighPressure,tdsList);           //高圧
        childTdsEntity.pcdLowPressure            =this.msterCk(buildingRegisterEntity.doiLowPressure,tdsList);            //低圧
        childTdsEntity.pcdMotor                  =this.msterCk(buildingRegisterEntity.doiMotor,tdsList);                  //モータ
        childTdsEntity.pcdPrivatePowerGeneration =this.msterCk(buildingRegisterEntity.doiPrivatePowerGeneration,tdsList); //自家発
        childTdsEntity.pcdCentralMonitoring      =this.msterCk(buildingRegisterEntity.doiCentralMonitoring,tdsList);      //中央監視
        childTdsEntity.pcdInstrumentation        =this.msterCk(buildingRegisterEntity.doiInstrumentation,tdsList);        //計装
        childTdsEntity.pcdTelemetry              =this.msterCk(buildingRegisterEntity.doiTelemetry,tdsList);              //テレメータ
        childTdsEntity.pcdWaterQualityMeter      =this.msterCk(buildingRegisterEntity.doiWaterQualityMeter,tdsList);      //水質計器
        childTdsEntity.pcdLiftRopeway            =this.msterCk(buildingRegisterEntity.doiLiftRopeway,tdsList);            // ﾘﾌﾄ･ﾛｰﾌﾟｳｪｲ
        childTdsEntity.pcdSludgeDryingMachine    =this.msterCk(buildingRegisterEntity.doiSludgeDryingMachine,tdsList);    //汚泥乾燥機（車）
        childTdsEntity.pcdSolarPower             =this.msterCk(buildingRegisterEntity.doiSolarPower,tdsList);             //太陽光発電
        childTdsEntity.pcdUps                    =this.msterCk(buildingRegisterEntity.doiUps,tdsList);                    //ＵＰＳ
        childTdsEntity.pcdDrive                  =this.msterCk(buildingRegisterEntity.doiDrive,tdsList);                  //ドライブ
        childTdsEntity.pcdOther                  =this.msterCk(buildingRegisterEntity.doiOther,tdsList);                  //その他
        return childTdsEntity;
    }
    /**
     * 商材情報登録用のエンティティを作成
     * @param newBuildingMapForm
     * @return
     */
    private CommodityEntity convFormToCommodity(BuildingRegisterEntity buildingRegisterEntity, String comCode,LinkedHashMap<String, String> TYotoList,LinkedHashMap<String, String> KTYotoList){
        CommodityEntity commodityEntity = new CommodityEntity();
        commodityEntity.commodityCode = comCode;
        commodityEntity.cmdComUsesCode = this.msterCk(buildingRegisterEntity.buildingUses,TYotoList);
        commodityEntity.cmdBuildingUsesCode = this.msterCk(commodityEntity.cmdComUsesCode,KTYotoList);

        commodityEntity.cmdOwner           = ""; // 施主
        commodityEntity.cmdAo              = ""; // Ａ／Ｏ
        commodityEntity.cmdGc              = ""; // Ｇ／Ｃ
        commodityEntity.cmdSc              = ""; // Ｓ／Ｃ

        commodityEntity.cmdFloorScale      = ""; // フロア数・規模
        commodityEntity.cmdShippingDate    = ""; // 出荷年月
        commodityEntity.cmdProductKbn      = ""; // 製品区分

        commodityEntity.cmdControlNumber   = ""; // 管理番号
        commodityEntity.cmdTotalFloorSpace = ""; // 建物延床面積
        commodityEntity.cmdSpecifications  = ""; // 仕様・数量(Elevator)

        commodityEntity.cmdMaintenanceContract = buildingRegisterEntity.managementCompany;  // 保守契約先

        // Customerの場合
        if("00".equals(comCode)){
            commodityEntity.cmdOwner               = buildingRegisterEntity.owner;              // 施主
            commodityEntity.cmdAo                  = buildingRegisterEntity.doiAo;              // Ａ／Ｏ
            commodityEntity.cmdGc                  = buildingRegisterEntity.doiGc;              // Ｇ／Ｃ
            commodityEntity.cmdSc                  = buildingRegisterEntity.doiSc;              // Ｓ／Ｃ
        // 昇降機の場合
        } else if ("01".equals(comCode)) {
            commodityEntity.cmdSpecifications      = buildingRegisterEntity.doiElevator;        // 仕様・数量(Elevator)
            commodityEntity.cmdElevatorMainte      = buildingRegisterEntity.elevatorMainte;     // 昇降機の保守契約先
            commodityEntity.cmdControlNumber       = buildingRegisterEntity.doiControlNumber;   // 管理番号
            commodityEntity.cmdTotalFloorSpace     = buildingRegisterEntity.doiTotalFloorSpace; // 建物延床面積
            commodityEntity.cmdMaintenanceContract = buildingRegisterEntity.managementCompany;  // 保守契約先
            commodityEntity.cmdFloorScale          = buildingRegisterEntity.floorScale2;        // フロア数・規模

        // 空調の場合
        } else if ("02".equals(comCode)) {
            commodityEntity.cmdAirConditionrMainte = buildingRegisterEntity.airConditioningMainte; // 空調の保守契約先
            commodityEntity.cmdShippingDate        = buildingRegisterEntity.shippingDate;       // 出荷年月
            commodityEntity.cmdProductKbn          = buildingRegisterEntity.productKbn;         // 製品区分
            commodityEntity.cmdFloorScale          = buildingRegisterEntity.floorScale;         // フロア数・規模

        // 電気設備の場合
        } else if ("03".equals(comCode)) {
            commodityEntity.cmdSpecifications      = buildingRegisterEntity.escalator;          // 仕様・数量(Escalator)
            commodityEntity.cmdPowerSupplyMainte   = buildingRegisterEntity.powerSupplyMainte;  // 電気設備の保守契約先

        // 照明設備の場合
        } else if ("04".equals(comCode)) {
            commodityEntity.cmdIlluminationMainte  = buildingRegisterEntity.illuminationMainte; // 照明設備の保守契約先

        // EMSの場合
        } else if ("05".equals(comCode)) {
            commodityEntity.cmdEmsMainte          = buildingRegisterEntity.emsMainte;          // EMSの保守契約先

        // その他の場合
        } else if ("06".equals(comCode)) {
            commodityEntity.cmdOtherMainte        = buildingRegisterEntity.otherMainte;        // その他の保守契約先

        } else if ("07".equals(comCode)) {
            commodityEntity.cmdOtherMainte        = buildingRegisterEntity.otherMainte;        // その他の保守契約先
            commodityEntity.cmdMaintenanceContract= buildingRegisterEntity.managementCompany;  // 保守契約先
        }
        // 保守契約先コード
        if(!"".equals(commodityEntity.keiyakuKikanFrom) || !"".equals(commodityEntity.keiyakuKikanTo)) {
            commodityEntity.maintenanceCode = "01";    // 東芝保守
        }
        return commodityEntity;
    }
    /**
     *  建物登録エラーExcelシートダウンロード
     *  流用元：TKMNNL226Excl
     *  @return view()
     *  @throws exception
     */
    @Execute(validator = false)
    public String errorFileDownload() throws Exception{
        // 各種基準Index
        int workerRowIdx   = 2;
        int templateRowIdx = 2; // データ部は全て3行目のフォーマットを使用
        try{
            //データ取得
            HttpSession session = httpServletRequest.getSession();
            buildingRegisterForm = (BuildingRegisterForm) session.getAttribute("BuildingRegisterForm");
            List<BuildingRegisterEntity> errorList =  buildingRegisterForm.errorList;
            //データがなかったらエラー表示
            if(errorList==null || errorList.size()==0){
                return null;
            }
            FileInputStream is = null;
            Workbook wbf = null;
            try{
                //テンプレートファイル読込
                String fileName = "ErrorListSheet.xlsx";
                ServletContext application = httpServletRequest.getSession().getServletContext();
                String filePath = application.getRealPath("/WEB-INF/data/" + fileName);

                File f = new File(filePath);
                if(!f.exists()){ //ファイルやディレクトリがあるか調べる
                    throw new FileNotFoundException();
                }

                is = new FileInputStream(filePath);
                wbf = WorkbookFactory.create(is);

                //データテンプレートの確認
                Sheet sheetTo = wbf.getSheet("建物一覧");

                CellStyle style1 = wbf.createCellStyle();
                style1.setFillPattern(CellStyle.SOLID_FOREGROUND);
                style1.setFillForegroundColor(IndexedColors.RED.getIndex());
                style1.setBorderLeft(CellStyle.BORDER_THIN );
                style1.setBorderBottom(CellStyle.BORDER_THIN );

                Row originalRow = sheetTo.getRow(templateRowIdx);

                //データ出力開始////////////////////////////////
                for(BuildingRegisterEntity data : errorList){
                //開始行設定
                    Row rowDetail = sheetTo.getRow(workerRowIdx);
                    if(rowDetail == null){
                        rowDetail = sheetTo.createRow(workerRowIdx);
                    }
                    // 出力
                    this.setCellvalue(data.seq,                       rowDetail, 0,  originalRow);  //No
                    this.setCellvalue(data.buildingName,              rowDetail, 1,  originalRow);  //建物名
                    this.setCellvalue(data.addressDef,                rowDetail, 2,  originalRow);  //住所
                    this.setCellvalue(data.elevatorDisp,              rowDetail, 3,  originalRow);  //昇降機
                    this.setCellvalue(data.airConditioningDisp,       rowDetail, 4,  originalRow);  //空調
                    this.setCellvalue(data.powerSupplyDisp,           rowDetail, 5,  originalRow);  //電源
                    this.setCellvalue(data.illuminationDisp,          rowDetail, 6,  originalRow);  //照明制御
                    this.setCellvalue(data.emsDisp,                   rowDetail, 7,  originalRow);  //EMS
                    this.setCellvalue(data.otherDisp,                 rowDetail, 8,  originalRow);  //その他
                    this.setCellvalue(data.completionDate,            rowDetail, 9,  originalRow);  //完成時期
                    this.setCellvalue(data.owner,                     rowDetail, 10, originalRow);  //施主
                    this.setCellvalue(data.managementCompany,         rowDetail, 11, originalRow);  //管理会社
//                  this.setCellvalue(data.elevatorDisp,              rowDetail, 12, originalRow);  //昇降機納入有無/保守管理有無
//                  this.setCellvalue(data.airConditioningDisp,       rowDetail, 13, originalRow);  //空調納入有無/保守管理有無
//                  this.setCellvalue(data.powerSupplyDisp,           rowDetail, 14, originalRow);  //電源納入有無/保守管理有無
//                  this.setCellvalue(data.illuminationDisp,          rowDetail, 15, originalRow);  //照明制御納入有無/保守管理有無
//                  this.setCellvalue(data.emsDisp,                   rowDetail, 16, originalRow);  //EMSの納入有無/保守管理有無
//                  this.setCellvalue(data.otherDisp,                 rowDetail, 17, originalRow);  //その他納入有無/保守管理有無
                    this.setCellvalue(data.supplementaryInfo,         rowDetail, 12, originalRow);  //その他付帯情報
                    this.setCellvalue(data.doiAo,                     rowDetail, 13, originalRow);  //A/C
                    this.setCellvalue(data.doiGc,                     rowDetail, 14, originalRow);  //G/C
                    this.setCellvalue(data.doiSc,                     rowDetail, 15, originalRow);  //S/C
//                  this.setCellvalue(chkTenant(data.tenantFlg),      rowDetail, 22, originalRow);  //東芝テナント有
                    this.setCellvalue(data.tenantFlg,                 rowDetail, 16, originalRow);  //東芝テナント有
                    this.setCellvalue(data.elevatorMainte,            rowDetail, 17, originalRow);  //昇降機保守契約先
                    this.setCellvalue(data.airConditioningMainte,     rowDetail, 18, originalRow);  //空調保守契約先
                    this.setCellvalue(data.powerSupplyMainte,         rowDetail, 19, originalRow);  //電気設備保守契約先
                    this.setCellvalue(data.illuminationMainte,        rowDetail, 20, originalRow);  //照明制御保守契約先
                    this.setCellvalue(data.emsMainte,                 rowDetail, 21, originalRow);  //EMSの保守契約先
                    this.setCellvalue(data.otherMainte,               rowDetail, 22, originalRow);  //その他保守契約先
//                  this.setCellvalue(targetListService.callBuildingInfoProcedure(data.buildingUsesCode), rowDetail, 29, originalRow);  //建物用途
                    this.setCellvalue(data.buildingUses,              rowDetail, 23, originalRow);  //建物用途
//                  this.setCellvalue(data.buildingUsesName,          rowDetail, 29, originalRow);  //各社建物用途
                    this.setCellvalue(data.floorScale,                rowDetail, 24, originalRow);  //(TCC)MJ数
                    this.setCellvalue(data.productKbn,                rowDetail, 25, originalRow);  //(TCC)製品区分
                    this.setCellvalue(data.shippingDate,              rowDetail, 26, originalRow);  //(TCC)出荷年月
                    this.setCellvalue(data.doiControlNumber,          rowDetail, 27, originalRow);  //(TELC)管理番号
                    this.setCellvalue(data.doiTotalFloorSpace,        rowDetail, 28, originalRow);  //(TELC)延床
                    this.setCellvalue(data.floorScale2,               rowDetail, 29, originalRow);  //(TELC)フロア数
                    this.setCellvalue(data.doiElevator,               rowDetail, 30, originalRow);  //(TELC)エレベータ台数
                    this.setCellvalue(data.escalator,                 rowDetail, 31, originalRow);  //(TELC)エスカレータ台数
//                  this.setCellvalue(data.doiCompletionDate,         rowDetail, 38, originalRow);  //(TELC)FS開始年月
                    this.setCellvalue(data.doiExhighVoltage,          rowDetail, 32, originalRow);  //(TDS)特高
                    this.setCellvalue(data.doiHighPressure,           rowDetail, 33, originalRow);  //(TDS)高圧
                    this.setCellvalue(data.doiLowPressure,            rowDetail, 34, originalRow);  //(TDS)低圧
                    this.setCellvalue(data.doiMotor,                  rowDetail, 35, originalRow);  //(TDS)モータ
                    this.setCellvalue(data.doiPrivatePowerGeneration, rowDetail, 36, originalRow);  //(TDS)発電機
                    this.setCellvalue(data.doiCentralMonitoring,      rowDetail, 37, originalRow);  //(TDS)中央監視
                    this.setCellvalue(data.doiInstrumentation,        rowDetail, 38, originalRow);  //(TDS)計装
                    this.setCellvalue(data.doiTelemetry,              rowDetail, 39, originalRow);  //(TDS)ﾃﾚﾒｰﾀ
                    this.setCellvalue(data.doiWaterQualityMeter,      rowDetail, 40, originalRow);  //(TDS)水質計器
                    this.setCellvalue(data.doiLiftRopeway,            rowDetail, 41, originalRow);  //(TDS)ﾘﾌﾄ･ﾛｰﾌﾟｳｪｲ
                    this.setCellvalue(data.doiSludgeDryingMachine,    rowDetail, 42, originalRow);  //(TDS)汚泥乾燥機（車）
                    this.setCellvalue(data.doiSolarPower,             rowDetail, 43, originalRow);  //(TDS)太陽光発電
                    this.setCellvalue(data.doiUps,                    rowDetail, 44, originalRow);  //(TDS)UPS
                    this.setCellvalue(data.doiDrive,                  rowDetail, 45, originalRow);  //(TDS)ドライブ
                    this.setCellvalue(data.doiOther,                  rowDetail, 46, originalRow);  //(TDS)その他
//                  this.setCellvalue(data.doiCompanyName,            rowDetail, 54, originalRow);  //テナントグループ会社名
//                  this.setCellvalue(data.hopeFlg,                   rowDetail, 56  originalRow);  //脈有無
//                  this.setCellvalue(data.progressFlg,               rowDetail, 57, originalRow);  //進捗
//                  this.setCellvalue(data.overView,                  rowDetail, 58, originalRow);  //概要
//                  this.setCellvalue(data.doiElevatorUpdateId,       rowDetail, 55, originalRow);  //昇降機の最終更新者
//                  this.setCellvalue(data.doiElevatorUpdateDate,     rowDetail, 56, originalRow);  //昇降機の最終更新日
//                  this.setCellvalue(data.doiAirConUpdateId,         rowDetail, 57, originalRow);  //空調の最終更新者
//                  this.setCellvalue(data.doiAirConUpdateDate,       rowDetail, 58, originalRow);  //空調の最終更新日
//                  this.setCellvalue(data.doiPowerSupplyUpdateId,    rowDetail, 59, originalRow);  //電源の最終更新者
//                  this.setCellvalue(data.doiPowerSupplyUpdateDate,  rowDetail, 60, originalRow);  //電源の最終更新日
//                  this.setCellvalue(data.doiIlluminationUpdateId,   rowDetail, 61, originalRow);  //照明の最終更新者
//                  this.setCellvalue(data.doiIlluminationUpdateDate, rowDetail, 62, originalRow);  //照明の最終更新日
//                  this.setCellvalue(data.doiEmsUpdateId,            rowDetail, 63, originalRow);  //EMSの最終更新者
//                  this.setCellvalue(data.doiEmsUpdateDate,          rowDetail, 64, originalRow);  //EMSの最終更新日
//                  this.setCellvalue(data.doiOtherUpdateId,          rowDetail, 65, originalRow);  //その他の最終更新者
//                  this.setCellvalue(data.doiOtherUpdateDate,        rowDetail, 66, originalRow);  //その他の最終更新日
//                  this.setCellvalue(data.doiRelationId,             rowDetail, 67, originalRow);  //システム連動の更新者
//                  this.setCellvalue(data.doiRelationDate,           rowDetail, 68, originalRow);  //システム連動の更新日
                    workerRowIdx++;
                }
                Row  errorRow  = null ;
                Cell errorCell = null;
                int j = 0;
                // エラーになったエクセルの箇所を赤く表示する
                for(int i = 2; i < workerRowIdx; i++){
                    errorRow = sheetTo.getRow(i);
                    List<Integer> errCellList = errorList.get(j++).errCellList;
                    for (int errPos: errCellList) {
                        // エラーセールを取得
                        errorCell= errorRow.getCell(errPos);
                        errorCell.setCellStyle(style1);
                    }
                }
                //EXCELダウンロード
                ResponseUtil.getResponse().setContentType("application/vnd.ms-excel");
                ResponseUtil.getResponse().setHeader("Content-Disposition","attachment;filename=" + new String(fileName.getBytes("Shift_JIS"), "ISO8859_1" ));
                try{
                    wbf.write(ResponseUtil.getResponse().getOutputStream());
                }catch (IOException e) {
                    e.printStackTrace();
                    ResponseUtil.getResponse().reset();
                    ResponseUtil.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    System.out.println("EXCELデータの取込に失敗しました。");
                }
            }catch(FileNotFoundException e){
                e.printStackTrace();
                ResponseUtil.getResponse().reset();
                ResponseUtil.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                System.out.println("データファイルが存在しませんでした。");
            }catch(IOException e){
                e.printStackTrace();
                ResponseUtil.getResponse().reset();
                ResponseUtil.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                System.out.println("EXCELデータの取込に失敗しました。");
            }finally{
                try {
                    if (is != null) {is.close();}
                    wbf.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            ResponseUtil.getResponse().reset();
            ResponseUtil.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.out.println(e.getLocalizedMessage());
            System.out.println("処理が失敗しました。");
        }
        return null;
    }
    /**
     * Google APIより住所を検索
     * @param getList
     * @return
     * @throws Exception
     */
    private BuildingRegisterEntity geocode(BuildingRegisterEntity getList) throws Exception{
        //ジオコーディング実行
        GetGeocodingApiContext GGAP = new GetGeocodingApiContext();
        GeocodingApiRequest request = GGAP.GetGCApiRq();
        request.components(ComponentFilter.country(COUNTRY_FILTER)).language(COUNTRY_LANG).address(getList.addressDef);

        //結果セット
        GeocodingResult[] results = request.await();

        //取得結果の件数チェック
        if( results.length == 0 ){
            //処理なし
        }else{
            if( results[0].partialMatch ){
                getList.Attribute2 = "部分一致";
            }else{
                getList.Attribute2 = "完全一致";
            }

            //住所セット
            String[] ar_address = null;
            ar_address = results[0].formattedAddress.split(" ");
            if( ar_address.length == 2 ){
                getList.address=ar_address[1];
            }else if( ar_address.length >= 3 ){
                getList.address=ar_address[2];
            }

            //住所詳細セット
            for (AddressComponent addressComponent : results[0].addressComponents) {
                // テナントジオコード変換時にGoogleMapsApiの仕様が変更されていたため、対応 2016/08/05 Mod
                // GoogleMapsApiがどのような結果を返すかは、https://maps.googleapis.com/maps/api/geocode/json?components=country%3AJP&address=任意の住所&language=jaで確認可（2016/08/08現在）
                // Google公式ドキュメントはhttps://developers.google.com/maps/documentation/geocoding/intro?hl=jaを参照（2016/08/08現在）
                for(int i = 0; i < addressComponent.types.length ; i++) {
                    switch (addressComponent.types[i].name().toLowerCase()) {
                    case "administrative_area_level_1":
                        getList.Prefectures = addressComponent.longName; // 都道府県
                        break;
                    case "colloquial_area":
                        getList.City=addressComponent.longName; // 郡
                        break;
                    case "locality":
                        getList.City=addressComponent.longName; // 市区
                        break;
                    case "ward":
                    case "unknown":
                        getList.Village=addressComponent.longName; // XX市ZZ区のような場合の区
                        break;
                    case "sublocality_level_1":
                        getList.Item1=addressComponent.longName; // 明細１（町、大字など）
                        break;
                    case "sublocality_level_2":
                        getList.Item2=addressComponent.longName; // 明細２
                        break;
                    case "sublocality_level_3":
                        getList.Item3=addressComponent.longName; // 明細３
                        break;
                    case "sublocality_level_4":
                        getList.Item4=addressComponent.longName; // 明細４
                        break;
                    case "sublocality_level_5":
                        getList.Item5=addressComponent.longName; // 明細５
                        break;
                    }
                }
            }
            // データが取得できている場合には、緯度・経度セット
            getList.Latitude=results[0].geometry.location.lat;  // 緯度
            getList.Longitude=results[0].geometry.location.lng; // 経度
        }
        return getList;
    }
    /**
     *
     * @param sign
     * @return
     */
/*
    private String getComCodeBySign(String sign){
        String result = "";
        String nounyu = "";
        String hosyu  = "";
        if(sign != "" && sign != null && sign.length() != 0){
            nounyu = sign.substring(0,1);
            hosyu  = sign.substring(sign.length()-1);
        }

        if(nounyu == hosyu){
            switch(nounyu)
            {
               case "*":
                   result = "0";break;
               case "○":
                   result = "13";break;
               case "△":
                   result = "23";break;
               case "●":
                   result = "31";break;
               case "▲":
                   result = "32";break;
               default:
                   result = "0";break;
            }
        }else{

            switch(nounyu+"+"+hosyu)
            {
               case "○+●":
                   result = "11";break;
               case "○+▲":
                   result = "12";break;
               case "△+●":
                   result = "21";break;
               case "△+▲":
                   result = "22";break;
               case "×+×":
                   result = "33";break;
               default:
                   result = "0";break;
            }
        }
        return result;
    }
*/
    /**
     * テナント有無の表示
     * @param value
     * @return
     */
    private String chkTenant(String value){
        String result = "";
        if(value == null){
            value = "0";
        }
        switch (value){
            case "テナント有" :
                result = "T";
                break;
            case "テナント無" :
                result = "F";
                break;
            default :
                result = "F";
                break;
        }
        return result;
    }
    /**
     * 日付チェック
     * @param inDate
     * @return
     */
    private boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        dateFormat.setLenient(false);
        try {
            // 入力日付が空白以外場合
            if (!"".equals(inDate)) {
                dateFormat.parse(inDate.trim());
            }
        } catch (ParseException pe) {
            try {
                dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                // 入力日付が空白以外場合
                if (!"".equals(inDate)) {
                    dateFormat.parse(inDate.trim());
                }
            } catch (ParseException pee) {
                try {
                    dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                    // 入力日付が空白以外場合
                    if (!"".equals(inDate)) {
                        dateFormat.parse(inDate.trim());
                    }
                } catch (ParseException peee) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean isValidDate(Cell c) {
//        String str = "";
        // Excel標準の日付書式を使っている場合
        try{
            if (DateUtil.isCellDateFormatted(c)|| CellDateFormat.contains(c.getCellStyle().getDataFormat())) {
                c.getDateCellValue();//Date theDate = c.getDateCellValue();
                CellDateFormat.getFormt(c.getCellStyle().getDataFormat()).getDateFormat();//DateFormat dateFormat = CellDateFormat.getFormt(c.getCellStyle().getDataFormat()).getDateFormat();
            }else{
                return this.isValidDate(this.getCellValue(c));
            }
        }catch (Exception e){
            return this.isValidDate(this.getCellValue(c));
        }
        return true;
    }
    /**
     * 住所チェック
     * @param newBuildingMapForm
     * @return
     */
    private boolean buildingCheck(String buildingAddressDef){
        try {
            // 入力した住所を重複チェック
            if (!buildingRegisterService.buildingCheck(buildingAddressDef)) {
                return false;
            }
            // GoogleAPIで正規化した住所をチェック
            String formalAddress = getAddressByGeoApi(buildingAddressDef);
            if (!buildingRegisterService.buildingCheck(formalAddress)) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * GoogleAPIで正規化した住所を取得
     * @param address
     * @return
     * @throws Exception
     */
    private String getAddressByGeoApi(String address) throws Exception{
        String apiAddress = "";
        //ジオコーディング実行
        GetGeocodingApiContext GGAP = new GetGeocodingApiContext();
        GeocodingApiRequest request = GGAP.GetGCApiRq();
        request.components(ComponentFilter.country(COUNTRY_FILTER)).language(COUNTRY_LANG).address(address);
        //結果セット
        GeocodingResult[] results = request.await();
        //取得結果の件数チェック
        if( results.length == 0 ){
            return "";
        }else{
            //住所セット
            String[] ar_address = null;
            ar_address = results[0].formattedAddress.split(" ");
            if (ar_address.length == 2) {
                apiAddress = ar_address[1];
            } else if (ar_address.length >= 3) {
                apiAddress = ar_address[2];
            }
        }
        return apiAddress;
    }
}
