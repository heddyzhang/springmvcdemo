package spt.kessai.kessai.print;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.fit.vfreport.Vrw32;

import org.apache.log4j.Category;

import spt.kessai.OrgSvlt;
import spt.kessai.subDisp.kousen.print.KousenPrintBean;
import spt.kessai.subDisp.kousen.print.KousenPrintSver;
import spt.sptComn.EcasEnv;
import spt.sptComn.sptException;
import spt.sptComn.sptList;

/****************************************
  spt共通   印刷
  package   spt.kessai.kessai.print
  class     KessaiPrintSvlt
  @author   M.MASUKO@NPC
  @version  1.00 , 2003/06/17
                  , 2006/05/11 S.SHIMA@NPC DB検索をvrqからではなくjavaからやるようにしたため大幅変更
****************************************/
public class KessaiPrintSvlt extends OrgSvlt {

    private String pdfPath;
    private String svfPath;//2010/09/09 frm用パスを設けることにした
    private String PathDelimiter;

    /** log4j用
     */
    private static Category log = Category.getInstance( "KessaiPrintSvlt" );

    /**
     * 備考帳票の行数の定義
     * 備考帳票の行数を変更するときはここを変更してください。
     */
    private final static int M_HUTAI_ROW = 5;   // 見積時付帯状況行数
    private final static int E_HUTAI_ROW = 5;   // 交渉終了時時付帯状況行数
    private final static int M_GAIYO_ROW = 30;  // 見積概要行数
    private final static int N_GAIYO_ROW = 30;  // ネゴ概要行数

    /**
     * 備考がオーバーしたときに一枚目に表示する文言
     */
    private final static String BIKO_OVER = "長文につき別紙参照願います。";

    /**
     * initialize pdfパスとパス区切り文字をプロパティファイルより取得
     */
    public void init( ServletConfig config ) throws ServletException {
        ResourceBundle bundle = ResourceBundle.getBundle(EcasEnv.properties_file);
        this.pdfPath = bundle.getString("PdfPath");
        this.svfPath = bundle.getString("SvfPath");//2010/09/09 frm用パスを設けることにした
        this.PathDelimiter = bundle.getString("PathDelimiter");
        super.init( config );
    }


    /* (非 Javadoc)
    * @see spt.kessai.OrgSvlt#setBeans(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.sql.Connection)
    */
    protected String setBeans(
        HttpServletRequest request
      , HttpServletResponse response
      , Connection conn
    ) throws sptException, SQLException {

    	log.debug("KessaiPrintSvlt start");

    	File cur = new File(".");
    	log.debug("カレントパス：" + cur.getAbsolutePath());

        //2010/09/09 frm用パスを設けることにした sptCookie cookie  = new sptCookie( request );

        String PdfName    = this.getFileName();
        //String path       = pdfPath + PathDelimiter + "kessai" + PathDelimiter;//作成されたPDFファイルの置き場所
        String path       = pdfPath + PathDelimiter;//作成されたPDFファイルの置き場所(PDFファイル直下に作成する。※フォルダ作成し忘れた場合を考慮）
        String frmPath    = svfPath + PathDelimiter;//2010/09/09 add frmの置き場所
        //2010/09/09 使用していないのでコメント String RealSvfPdf = pdfPath + PathDelimiter + "kessai" + PathDelimiter;

        // 予算正式FLG
        String Ysflg = getParam( request, "YSFLG");

        String  formname;   // フォーム名

        // 帳票印刷情報Beanを取得
        KessaiPrintBean printBean = this.makePrintBean( conn, request );

        if( Ysflg.equals("Y") ){
            formname   = "yosan.frm";
        } else {
    		formname   = "seisiki.frm";
        }
    	log.debug("form名取得：" + formname);

        // SVF操作オブジェクトの作成
        Vrw32   svf = new Vrw32();
        int ret = svf.VrInit( "MS932" );
        log.debug("SVF操作オブジェクト作成　ret=" + ret);
        if( ret != 0 )  throw new sptException("SVF操作オブジェクト作成時エラー");

        // PDFファイル名をセット
        /*ret = svf.VrSetSpoolFileName2( path + "files" + PathDelimiter + PdfName );
        if( ret != 0 )  throw new sptException("PDFファイル名指定時エラー "
                                              + "fileパス【" + path + "files" + PathDelimiter + PdfName + "】");*/
        ret = svf.VrSetSpoolFileName2( path + PdfName );
        log.debug("PDFファイル名指定　ret=" + ret + "  fileパス【" + path + PdfName + "】");
        if( ret != 0 )  throw new sptException("PDFファイル名指定時エラー "
                                              + "fileパス【" + path + PdfName + "】");

        /**
         * 決裁帳票部分をセット
         */
        // 様式ファイルと出力のモードの指定
        //2010/09/09 del ret = svf.VrSetForm( path + formname, 5 );
        //if( ret != 0 )  throw new sptException("フォーム指定時エラー formname【" + path + formname + "】");
        ret = svf.VrSetForm( frmPath + "kessai" + PathDelimiter + formname, 5 );//2010/09/09 add
        log.debug("フォーム指定(決裁帳票部分)　ret=" + ret + "  formパス【" + frmPath + "kessai" + PathDelimiter + formname + "】");
        if( ret != 0 )  throw new sptException("フォーム指定時エラー formname【" + frmPath + "kessai" + PathDelimiter + formname + "】");

        //SVFオブジェクトにリストの値をセット
        for( int i = 0; i < printBean.getBukkenlist().getSize(); i++ ) {

            if( Ysflg.equals("Y") ){
                ret = setToSvfYosan( svf, printBean, i );
            } else {
                ret = setToSvfSeisiki( svf, printBean, i );
            }
            log.debug("データセット(決裁帳票部分)　ret=" + ret );
            if( ret != 0 )  throw new sptException("データセット時エラー");
            //  一行出力
            ret = svf.VrEndRecord();
            log.debug("データセット終了(決裁帳票部分)　ret=" + ret );
            if( ret != 0 )  throw new sptException("データセット終了時エラー");
        }

        ret = svf.VrPrint();
        log.debug("レポートライター帳票の印刷の実行(決裁帳票部分)　ret=" + ret );
        if( ret != 0 )  throw new sptException("レポートライター帳票の印刷の実行時エラー");
        int kousenpage= 0;


        /**
         * 備考帳票部分をセット
         */
        for( int i = 0; i < printBean.getBikoPageCnt(); i++ ) {
            //2010/09/09 del 様式ファイルと出力のモードの指定
            //ret = svf.VrSetForm( path + "bikou.frm", 5 );
        	//if( ret != 0 )  throw new sptException("フォーム指定時エラー formname【" + path + "bikou.frm" + "】");
        	ret = svf.VrSetForm( frmPath + "kessai" + PathDelimiter + "bikou.frm", 5 );//2010/09/09 add
        	log.debug("フォーム指定(備考帳票部分)　ret=" + ret + "  formパス【" + frmPath + "kessai" + PathDelimiter + "bikou.frm" + "】");
            if( ret != 0 )  throw new sptException("フォーム指定時エラー formname【" + frmPath + "kessai" + PathDelimiter + "bikou.frm" + "】");

            ret = setToSvfBiko( svf, printBean, i );
            log.debug("データセット(備考帳票部分)　ret=" + ret );
            if( ret != 0 )  throw new sptException("データセット時エラー");

            ret = svf.VrEndRecord();
            log.debug("データセット終了(備考帳票部分)　ret=" + ret );
            if( ret != 0 )  throw new sptException("データセット終了時エラー");

            ret = svf.VrPrint();
            log.debug("レポートライター帳票の印刷の実行(備考帳票部分)　ret=" + ret );
            if( ret != 0 )  throw new sptException("レポートライター帳票の印刷の実行時エラー");
            kousenpage = i + 1;

        }

        if( Ysflg.equals("S") ) {
	        /**
	         * 2009/02/23  add
	         * 口銭帳票部分をセット
	         */
	        //2010/09/09 del ret = svf.VrSetForm( pdfPath + PathDelimiter + "kousen" + PathDelimiter + "kousen.frm", 1 );
        	//if( ret != 0 )  throw new sptException("フォーム指定時エラー formname【" + pdfPath + PathDelimiter + "kousen" + PathDelimiter + "kousen.frm" + "】");
        	ret = svf.VrSetForm( frmPath + "kousen" + PathDelimiter + "kousen.frm", 1 );//2010/09/09 add
        	log.debug("フォーム指定(口銭帳票部分)　ret=" + ret + "  formパス【" + frmPath + "kousen" + PathDelimiter + "kousen.frm" + "】");
	        if( ret != 0 )  throw new sptException("フォーム指定時エラー formname【" + frmPath + "kousen" + PathDelimiter + "kousen.frm" + "】");

	        ret = setKousenSvf(conn,request,svf,printBean,kousenpage);
	        log.debug("データセット(口銭帳票部分)　ret=" + ret );
	        if( ret != 0 )  throw new sptException("データセット時エラー");

	        ret = svf.VrEndPage();
	        log.debug("データセット終了(口銭帳票部分)　ret=" + ret );
	        if( ret != 0 )  throw new sptException("データセット終了時エラー");

	        ret = svf.VrPrint();
	        log.debug("レポートライター帳票の印刷の実行(口銭帳票部分)　ret=" + ret );
	        if( ret != 0 )  throw new sptException("レポートライター帳票の印刷の実行時エラー");

        }

        ret = svf.VrQuit();
        log.debug("VrQuit　ret=" + ret );

        // データセットアトレビュート
        //2010/09/13 PDF出力方法を変更(レスポンスに吐き出すことにした）
        //request.setAttribute( "PdfName" , PdfName );
        request.setAttribute( "PdfName" , path + PdfName);
        log.debug("KessaiPrintSvlt end");
        return "kessai/kessai/print/print.jsp";
    }


    /**
     * 帳票印刷に必要な情報の入ったBeanを作成する
     * @param conn Connectionオブジェクト
     * @param request HttpServletRequest
     * @return 帳票印刷情報Bean
     * @throws sptException
     */
    private KessaiPrintBean makePrintBean( Connection conn, HttpServletRequest request ) throws sptException {

        // Q/#, REV, 予算正式FLG
        String Q_NO = getParam( request, "QNO");
        String Revition = getParam( request, "REV");
        String Ysflg = getParam( request, "YSFLG");

        KessaiPrintSver psver = new KessaiPrintSver();
        psver.init( conn );
        //2009/02/24 add
    	KousenPrintSver sver = new KousenPrintSver();
		sver.init(conn);

        sptList bukkenlist;
        sptList kosenlist;		//2009/02/24 add
        if( Ysflg.equals("Y") ) {
            bukkenlist = psver.getPrintLstYosan( Q_NO, Revition, Ysflg );
        } else {
            bukkenlist = psver.getPrintLstSeisiki( Q_NO, Revition, Ysflg );
        }

        //商務事項の問題事項を取得
        String mShomuMondai = psver.getShomuCheckStr( Q_NO, Revition, Ysflg, "M" );
        String eShomuMondai = psver.getShomuCheckStr( Q_NO, Revition, Ysflg, "E" );

        int colsize;
        int colsize2;
        int rowsize;
        String svfFieldNm;

        /**
         * 見積付帯条件を分解しVectorにセット
         */
        colsize = 54;
        colsize2 = 100;
        Vector vecMhutai = this.getTokened( bukkenlist.getVal(0, "M_HUTAI_JYOKEN"), colsize );
        Vector vecMhutai2 = this.getTokened( bukkenlist.getVal(0, "M_HUTAI_JYOKEN"), colsize2 ); //備考帳票用に100行毎に切ったもの
        // １枚目は２行、それ以降はM_HUTAI_ROW行表示する
        int pageCntMhutai;
        if( vecMhutai.size() <= 2 ) {
            pageCntMhutai = 0;
        } else {
            pageCntMhutai = ( ( vecMhutai2.size() - 1 ) / M_HUTAI_ROW ) + 1;
        }

        /**
         * 決済付帯条件を分解しVectorにセット
         */
        colsize = 54;
        colsize2 = 100;
        Vector vecEhutai = this.getTokened( bukkenlist.getVal(0, "K_HUTAI_JYOKEN"), colsize );
        Vector vecEhutai2 = this.getTokened( bukkenlist.getVal(0, "K_HUTAI_JYOKEN"), colsize2 ); //備考帳票用に100行毎に切ったもの
        // １枚目は２行、それ以降はE_HUTAI_ROW行表示する
        int pageCntEhutai;
        if( vecEhutai.size() <= 2 ) {
            pageCntEhutai = 0;
        } else {
            pageCntEhutai = ( ( vecEhutai2.size() - 1 ) / E_HUTAI_ROW ) + 1;
        }

        /**
         * 見積概要を分解しVectorにセット
         */
        colsize = 100;
        Vector vecGaiyo = this.getTokened( bukkenlist.getVal(0, "MITUMORI_GAIYOU"), colsize );
        // １枚目は５行、それ以降はM_GAIYO_ROW行表示する
        int pageCntMGaiyo;
        if( vecGaiyo.size() <= 5 ) {
            pageCntMGaiyo = 0;
        } else {
            pageCntMGaiyo = ( ( vecGaiyo.size() - 1 ) / M_GAIYO_ROW ) + 1;
        }

        /**
         * ネゴ概要を分解しVectorにセット
         */
        colsize = 100;
        Vector vecNego = this.getTokened( bukkenlist.getVal(0, "NEGO"), colsize );
        // １枚目は５行、それ以降はN_GAIYO_ROW行表示する
        int pageCntNGaiyo = ( vecNego.size() - 1 ) / 5;
        if( vecNego.size() <= 5 ) {
            pageCntNGaiyo = 0;
        } else {
            pageCntNGaiyo = ( ( vecNego.size() - 1 ) / N_GAIYO_ROW ) + 1;
        }

        for( int index = 0; index < bukkenlist.getSize(); index++ ) {

            // 見積付帯条件セット
            for( int vecindex = 0; vecindex < vecMhutai.size(); vecindex++ ) {
                bukkenlist.putVal( index, "見積付帯条件" + String.valueOf( vecindex + 1 ), (String) vecMhutai.get(vecindex) );
            }

            // 見積付帯条件(備考帳票用に100行毎に)セット
            for( int vecindex = 0; vecindex < vecMhutai2.size(); vecindex++ ) {
                bukkenlist.putVal( index, "見積備考用付帯条件" + String.valueOf( vecindex + 1 ), (String) vecMhutai2.get(vecindex) );
            }


            // 決済付帯条件セット
            for( int vecindex = 0; vecindex < vecEhutai.size(); vecindex++ ) {
                bukkenlist.putVal( index, "決済付帯条件" + String.valueOf( vecindex + 1 ), (String) vecEhutai.get(vecindex) );
            }

            // 決済付帯条件(備考帳票用に100行毎に)セット
            for( int vecindex = 0; vecindex < vecEhutai2.size(); vecindex++ ) {
                bukkenlist.putVal( index, "決済備考用付帯条件" + String.valueOf( vecindex + 1 ), (String) vecEhutai2.get(vecindex) );
            }

            // 見積概要セット
            for( int vecindex = 0; vecindex < vecGaiyo.size(); vecindex++ ) {
                bukkenlist.putVal( index, "見積概要" + String.valueOf( vecindex + 1 ), (String) vecGaiyo.get(vecindex) );
            }

            // ネゴ概要セット
            for( int vecindex = 0; vecindex < vecNego.size(); vecindex++ ) {
                bukkenlist.putVal( index, "ネゴ概要" + String.valueOf( vecindex + 1 ), (String) vecNego.get(vecindex) );
            }

            // 見積時商務事項問題事項をセット
            bukkenlist.putVal( index, "見積時商務事項問題事項", mShomuMondai );

            // 交渉終了時商務事項問題事項をセット
            bukkenlist.putVal( index, "交渉終了時商務事項問題事項", eShomuMondai );
        }

        // 決裁帳票ページ数
        int kessaiPageCnt = ( ( bukkenlist.getSize() - 1 )  / 11 ) + 1;

        // 備考帳票ページ数 見積付帯条件、決裁付帯条件、見積概要、ネゴ概要のMaxが備考のページ数になる
        int bikoPageCnt = this.max( new int[]{ pageCntMhutai, pageCntEhutai, pageCntMGaiyo, pageCntNGaiyo } );

        //2009/02/24 口銭情報があれば１ページ増やす
        int kousenPageCnt = 0;
        if( Ysflg.equals("S") ) {
	        kosenlist = sver.getPrintLstKosen(Q_NO, Revition, Ysflg);
	        if(kosenlist.getSize() > 0){
	        	if("1".equals(kosenlist.getVal(0,"KOUSEN_FLG"))){
	        		kousenPageCnt = 1;
	        	}
	        }
        }


        /**
         * BeanにセットしRETURN
         */
        KessaiPrintBean bean = new KessaiPrintBean();
        bean.setKessaiPageCnt( kessaiPageCnt );
        bean.setBikoPageCnt( bikoPageCnt );
        bean.setBukkenlist( bukkenlist );
        bean.setKousenPageCnt(kousenPageCnt);		//2009/02/24 add
        bean.setMHutaiOver( pageCntMhutai > 0 );
        bean.setEHutaiOver( pageCntEhutai > 0 );
        bean.setMGaiyoOver( pageCntMGaiyo > 0 );
        bean.setNGaiyoOver( pageCntNGaiyo > 0 );
        return bean;
    }


    /**
     * 改行コードあるいは指定された文字数まで行ったら文字を分解しVector列に入れる。
     * 画面上のテキストエリアの見た目と帳票上の見た目をあわせるために行っている。
     * @param strOrg 元の文字列
     * @param LINEBREAK 改行する文字数
     * @return 改行またはcolsize部分で分けられたVector
     * 例 LINEBREAK=10
     * strOrg ="01234567890123[改行]
     *          4567890123456789"
     * Vector(0) = 0123456789 ←10文字で分解
     * Vector(1) = 0123       ←123と456の間にある改行文字を検知し分解
     * Vector(2) = 4567890123 ←改行が一度入ったらさらに10文字後になったら分解
     * Vector(3) = 89         ←改行が一度入ったらさらに10文字後になったら分解
     */
    private Vector getTokened( String strOrg, final int LINEBREAK ) {

        Vector vec = new Vector();
        StringBuffer sb = new StringBuffer("");
        int nowline = 0;
        int nowrow  = 0;
        int tabSpace = 0;

        for( int index = 0; index < strOrg.length(); index++ ) {
            boolean lineBreak = false;
            String nowChr = strOrg.substring(index, index + 1);
            if( nowChr.equals("\r") ) {
                lineBreak = true;       // 強制改行
                index++;                // "\r\n"となっているので\nの分インデックスを１進める
            } else {
                // 全角
                if( nowChr.getBytes().length == 2 ) {
                    nowline += 2;
                    sb.append( nowChr );
                // 半角
                } else {
                    // タブ文字制御 タブ文字をスペースに変換してセット 2006/04/06 add
                    if( nowChr.equals("\t") ) {
                        nowChr = this.adjTab( nowline, 8 );
                        nowline += nowChr.length();
                    // タブ以外の通常文字
                    } else {
                        nowline++;
                    }
                    sb.append( nowChr );
                }
                // 最後に改行がない場合でないので
                if( index == strOrg.length() - 1 ) {
                    lineBreak = true;
                }
            }
            // 行の最後が全角文字でまたいでしまうときの対策 2006/03/31 add
            boolean overNext = false;
            if( index != strOrg.length() - 1 ) {
                String nextChr = strOrg.substring(index + 1, index + 2);
                if( nowline == LINEBREAK - 1 && nextChr.getBytes().length == 2 ) {
                    overNext = true;
                }
                //現在がLINEBREAK行目でその次に改行が入っているとき二重改行になってしまうので
                if( nowline == LINEBREAK && nextChr.equals("\r") ) {
                    nowline = 0;
                    continue;
                }
            }
            if( nowline == LINEBREAK || overNext || lineBreak ) {
                vec.add(nowrow, sb.toString() + "\r\n");
                sb = new StringBuffer("");
                nowline = 0;
                nowrow++;
            }
        }
        return vec;
    }

    /**
     * SVFに値をセット 予算
     * @param svf Svfオブジェクト
     * @param printlist 印刷で出すデータ
     * @exception 0:正常終了 1:異常終了
     */
    private int setToSvfBiko( Vrw32 svf, KessaiPrintBean bean, int bikoPageIndex ) {
        int ret = 0;
        sptList printlist = bean.getBukkenlist();
        try{
            Hashtable hash = printlist.getList( 0 );
            Enumeration keys = hash.keys();
            while (keys.hasMoreElements()) {

                String tmpstr = (String)keys.nextElement();
                String keybiko;

                // 見積概要1,見積概要2,見積概要3･･･とセット
                keybiko = "見積概要";
                int fieldCnt;
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if(  Integer.parseInt( tmpstr.substring( keybiko.length() ) ) >= ( M_GAIYO_ROW * bikoPageIndex ) + 1
                      && Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= M_GAIYO_ROW * ( bikoPageIndex + 1 )
                    ) {
                        fieldCnt = Integer.parseInt( tmpstr.substring( keybiko.length() ) ) - ( M_GAIYO_ROW * bikoPageIndex );
                        ret = svf.VrsOut( keybiko + String.valueOf( fieldCnt ), printlist.getVal( 0, keybiko + tmpstr.substring( keybiko.length() ) ) );
                        if( ret < 0 ) return ret;
                    }
                }
                keybiko = "ネゴ概要";
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if(  Integer.parseInt( tmpstr.substring( keybiko.length() ) ) >= ( N_GAIYO_ROW * bikoPageIndex ) + 1
                       && Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= N_GAIYO_ROW * ( bikoPageIndex + 1 )
                    ) {
                        fieldCnt = Integer.parseInt( tmpstr.substring( keybiko.length() ) ) - ( N_GAIYO_ROW * bikoPageIndex );
                        ret = svf.VrsOut( keybiko + String.valueOf( fieldCnt ), printlist.getVal( 0, keybiko + tmpstr.substring( keybiko.length() ) ) );
                        if( ret < 0 ) return ret;
                    }
                }
                keybiko = "見積備考用付帯条件";
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if(  Integer.parseInt( tmpstr.substring( keybiko.length() ) ) >= ( M_HUTAI_ROW * bikoPageIndex ) + 1
                      && Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= M_HUTAI_ROW * ( bikoPageIndex + 1 )
                    ) {
                        fieldCnt = Integer.parseInt( tmpstr.substring( keybiko.length() ) ) - ( M_HUTAI_ROW * bikoPageIndex );
                        ret = svf.VrsOut( "見積付帯条件" + String.valueOf( fieldCnt ), printlist.getVal( 0, keybiko + tmpstr.substring( keybiko.length() ) ) );
                        if( ret < 0 ) return ret;
                    }
                }
                keybiko = "決済備考用付帯条件";
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if(  Integer.parseInt( tmpstr.substring( keybiko.length() ) ) >= ( E_HUTAI_ROW * bikoPageIndex ) + 1
                      && Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= E_HUTAI_ROW * ( bikoPageIndex + 1 )
                    ) {
                        fieldCnt = Integer.parseInt( tmpstr.substring( keybiko.length() ) ) - ( E_HUTAI_ROW * bikoPageIndex );
                        ret = svf.VrsOut( "決済付帯条件" + String.valueOf( fieldCnt ), printlist.getVal( 0, keybiko + tmpstr.substring( keybiko.length() ) ) );
                        if( ret < 0 ) return ret;
                    }
                }
            }
            ret = svf.VrsOut( "ページ数", String.valueOf( bean.getKessaiPageCnt() + ( bikoPageIndex + 1 ) ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "総ページ数", String.valueOf( bean.getKessaiPageCnt() + bean.getBikoPageCnt() + bean.getKousenPageCnt()) );
            if( ret < 0 ) return ret;
        } catch( Exception e ){
            ret = -1;
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * SVFに値をセット 予算
     * @param svf Svfオブジェクト
     * @param bean 印刷情報Bean
     * @exception 0:正常終了 1:異常終了
     */
    private int setToSvfYosan( Vrw32 svf, KessaiPrintBean bean, int i ){
        int ret = 0;
        sptList printlist = bean.getBukkenlist();
        try{
            ret = svf.VrsOut( "REV", printlist.getVal( i, "REV" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "Rev2", printlist.getVal( i, "REV" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "YSFLG", printlist.getVal( i, "YS_FLG" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "Spurt番号", printlist.getVal( i, "SPURT_NO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "作成部", "[" + printlist.getVal( i, "SAKUSEIBU" ) + "]" );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "作成課", "[" + printlist.getVal( i, "SAKUSEIKA" ) + "]" );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "BuCode", printlist.getVal( i, "BU_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "種類", printlist.getVal( i, "SYURUI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "JobGr", printlist.getVal( i, "JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積回答日限", printlist.getVal( i, "MT_KAITO_NITIGEN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積フォーム", printlist.getVal( i, "MI_FORM" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "契約形態", printlist.getVal( i, "KEIYAKU_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "担当者", printlist.getVal( i, "TANTO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "プラント", printlist.getVal( i, "PLANT_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "表内作成課", printlist.getVal( i, "SAKUSEIKA" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "客先担当課", printlist.getVal( i, "KYAKU_TAN_KA_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "客先担当者", printlist.getVal( i, "KYAKU_TAN_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "Q/#表示用", printlist.getVal( i, "QNO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "Q/#", printlist.getVal( i, "KEY_QNO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "要求納期", printlist.getVal( i, "YOKYU_NOKI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "実施時期", printlist.getVal( i, "JISSI_JIKI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "客先見積番号", printlist.getVal( i, "KYAKU_KEIYAKU_NO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "注文日付", printlist.getVal( i, "CHUMONBI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "定検回数", printlist.getVal( i, "TEIKEN_KAISU" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "O/#", printlist.getVal( i, "ONO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "要求工期FROM", printlist.getVal( i, "KOKI_FROM" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "客先", printlist.getVal( i, "KYAKU_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "件名", printlist.getVal( i, "KENMEI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積ステータス", printlist.getVal( i, "M_STATUS" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積金額SP", printlist.getVal( i, "SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "予算獲得目標", printlist.getVal( i, "MOKUHYO_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積承認者", printlist.getVal( i, "M_SYOUNIN_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積調査者", printlist.getVal( i, "M_CHOSA_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積起草者", printlist.getVal( i, "M_TANTO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "SP見込", printlist.getVal( i, "KAKU_MIKOMI_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "SP見込NET", printlist.getVal( i, "MIKOMI_NET_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "SP見込M率", printlist.getVal( i, "MRITU_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "中長期予算", printlist.getVal( i, "CHUCHO_YOS_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積商務事項確認", printlist.getVal( i, "M_SHOMUJIKO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積特記すべき", printlist.getVal( i, "M_HUTAI_JYOKEN_FLG" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積付帯条件添付ファイル", printlist.getVal( i, "M_HUTAI_TEMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "主管JOBGr", printlist.getVal( i, "SHUKAN_JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "備考", printlist.getVal( i, "BIKO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "関係JobGr", printlist.getVal( i, "KAIKEI_JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "工場部課ベンダ", printlist.getVal( i, "SHOSHO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積NET", printlist.getVal( i, "SHU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見込NET", printlist.getVal( i, "MIKO_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "目標NET", printlist.getVal( i, "MOKUHYO_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "設計査定NET", printlist.getVal( i, "SEKKEISATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積添付ファイル", printlist.getVal( i, "M_TEMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "電力計画予算", printlist.getVal( i, "DEN_KEIKAKU_YOSAN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "コメント1", printlist.getVal( i, "KAKUNIN_COMMENT_1" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "確認日", printlist.getVal( i, "KAKUNINBI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "確認先担当課", printlist.getVal( i, "KAKUNIN_TAN_KA_1" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "確認先担当者", printlist.getVal( i, "KAKUNIN_TAN_1" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "契約予想SP", printlist.getVal( i, "YOSO_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "契約予想見込NET", printlist.getVal( i, "K_MIKOMINET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "契約予想M率", printlist.getVal( i, "K_MRITU" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "契約予想粗利", printlist.getVal( i, "K_SORI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済承認者", printlist.getVal( i, "K_SYOUNIN_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済調査者", printlist.getVal( i, "K_CHOSA_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済起草者", printlist.getVal( i, "K_TANTO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済商務事項確認", printlist.getVal( i, "K_SHOMUJIKO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済特記すべき", printlist.getVal( i, "K_HUTAI_JYOKEN_FLG" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済付帯条件添付ファイル", printlist.getVal( i, "K_HUTAI_TMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済添付ファイル", printlist.getVal( i, "K_TMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "二次JOBGR", printlist.getVal( i, "N_JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "二次所掌コード", printlist.getVal( i, "N_SHOSHO_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "要求工期TO", printlist.getVal( i, "KOKI_TO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済時STS", printlist.getVal( i, "STATUSCODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "交渉ステータス", printlist.getVal( i, "K_STATUS" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接NET見積", printlist.getVal( i, "TSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接NET目標", printlist.getVal( i, "TSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接NET設計査定", printlist.getVal( i, "TSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接NET見込", printlist.getVal( i, "TSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接粗利見積", printlist.getVal( i, "ASS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接粗利目標", printlist.getVal( i, "ASMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接粗利設計査定", printlist.getVal( i, "ASSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接粗利見込", printlist.getVal( i, "ASM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接Ｍ率見積", printlist.getVal( i, "MSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接Ｍ率目標", printlist.getVal( i, "MSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接Ｍ率設計査定", printlist.getVal( i, "MSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接Ｍ率見込", printlist.getVal( i, "MSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "人件費見積", printlist.getVal( i, "JSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "人件費目標", printlist.getVal( i, "JSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "人件費設計査定", printlist.getVal( i, "JSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "人件費見込", printlist.getVal( i, "JSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "労災他経費見積", printlist.getVal( i, "RSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "労災他経費目標", printlist.getVal( i, "RSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "労災他経費設計査定", printlist.getVal( i, "RSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "労災他経費見込", printlist.getVal( i, "RSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積NET合計", printlist.getVal( i, "GSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "目標NET合計", printlist.getVal( i, "GMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "設計査定NET合計", printlist.getVal( i, "GSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見込NET合計", printlist.getVal( i, "GSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "二次所掌区分", printlist.getVal( i, "N_SHOSHO_KBN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "総ページ数", String.valueOf( bean.getKessaiPageCnt() + bean.getBikoPageCnt()) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "M_SHOMU_KOMOKU", printlist.getVal( i, "見積時商務事項問題事項" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "E_SHOMU_KOMOKU", printlist.getVal( i, "交渉終了時商務事項問題事項" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "M_hinagata", printlist.getVal( i, "M_HINAGATA" ) );		//2009/02/24 add
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "E_hinagata", printlist.getVal( i, "E_HINAGATA" ) );		//2009/02/24 add
            if( ret < 0 ) return ret;
            ret = setKessaiBikoSvf( svf, bean, i );
            if( ret < 0 ) return ret;

            // 2016B ADD(S)
            ret = svf.VrsOut( "MIT_SHONIN_DATE", printlist.getVal( i, "M_SYOUNIN_DATE" ) );     // 見積時 承認日
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "KESSAI_SHONIN_DATE", printlist.getVal( i, "K_SYOUNIN_DATE" ) );  // 交渉終了時 承認日
            if( ret < 0 ) return ret;
            // 2016B ADD(E)

        } catch( Exception e ){
            ret = -1;
        }
        return ret;
    }

    /**
     * SVFに値をセット 正式
     * @param svf Svfオブジェクト
     * @param bean 印刷情報Bean
     * @exception 0:正常終了 1:異常終了
     */
    private int setToSvfSeisiki( Vrw32 svf, KessaiPrintBean bean, int i ){
        int ret = 0;
        sptList printlist = bean.getBukkenlist();
        try{
            ret = svf.VrsOut( "REV", printlist.getVal( i, "REV" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "Rev2", printlist.getVal( i, "REV" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "YSFLG", printlist.getVal( i, "YS_FLG" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "Spurt番号", printlist.getVal( i, "SPURT_NO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "作成部", "[" + printlist.getVal( i, "SAKUSEIBU" ) + "]" );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "作成課", "[" + printlist.getVal( i, "SAKUSEIKA" ) + "]" );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "BuCode", printlist.getVal( i, "BU_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "種類", printlist.getVal( i, "SYURUI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "JobGr", printlist.getVal( i, "JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積回答日限", printlist.getVal( i, "MT_KAITO_NITIGEN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積フォーム", printlist.getVal( i, "MI_FORM" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "契約形態", printlist.getVal( i, "KEIYAKU_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "担当者", printlist.getVal( i, "TANTO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "プラント", printlist.getVal( i, "PLANT_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "表内作成課", printlist.getVal( i, "SAKUSEIKA" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "客先担当者", printlist.getVal( i, "KYAKU_TAN_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "Q/#", printlist.getVal( i, "KEY_QNO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "Q/#表示用", printlist.getVal( i, "QNO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "客先担当課", printlist.getVal( i, "KYAKU_TAN_KA_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "要求納期", printlist.getVal( i, "YOKYU_NOKI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "実施時期", printlist.getVal( i, "JISSI_JIKI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "客先見積番号", printlist.getVal( i, "KYAKU_KEIYAKU_NO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "注文日付", printlist.getVal( i, "CHUMONBI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "定検回数", printlist.getVal( i, "TEIKEN_KAISU" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "O/#", printlist.getVal( i, "ONO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "要求工期FROM", printlist.getVal( i, "KOKI_FROM" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "客先", printlist.getVal( i, "KYAKU_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "件名", printlist.getVal( i, "KENMEI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積ステータス", printlist.getVal( i, "M_STATUS" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積金額SP", printlist.getVal( i, "SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "契約予想SP", printlist.getVal( i, "KEIYAKU_YOSO_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "契約予想見込NET", printlist.getVal( i, "MIKOMI_NET_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "契約予想M率", printlist.getVal( i, "MRITU_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積承認者", printlist.getVal( i, "M_SYOUNIN_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積2次調査者", printlist.getVal( i, "M_CHOSA2_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積調査者", printlist.getVal( i, "M_CHOSA_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積起草者", printlist.getVal( i, "M_TANTO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "実施予算", printlist.getVal( i, "JISSI_YOSAN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "対予算比", printlist.getVal( i, "TAIYOSAN_HI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積商務事項確認", printlist.getVal( i, "M_SHOMUJIKO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積特記すべき", printlist.getVal( i, "M_HUTAI_JYOKEN_FLG" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積付帯条件添付ファイル", printlist.getVal( i, "M_HUTAI_TEMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "主管JOBGr", printlist.getVal( i, "SHUKAN_JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "二次JOBGR", printlist.getVal( i, "N_JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "備考", printlist.getVal( i, "BIKO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "関係JobGr", printlist.getVal( i, "KAIKEI_JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "工場部課ベンダ", printlist.getVal( i, "SHOSHO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積NET", printlist.getVal( i, "SHU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見込NET", printlist.getVal( i, "MIKO_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "目標NET", printlist.getVal( i, "MOKUHYO_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "設計査定NET", printlist.getVal( i, "SEKKEISATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "二次所掌コード", printlist.getVal( i, "N_SHOSHO_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積添付ファイル", printlist.getVal( i, "M_TEMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "交渉ステータス", printlist.getVal( i, "K_STATUS" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "合計SP", printlist.getVal( i, "G_S_YOSO_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "合計見込NET", printlist.getVal( i, "G_MIKOMINET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "合計M率", printlist.getVal( i, "G_MRITU" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "合計粗利", printlist.getVal( i, "G_SORI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済2次調査者", printlist.getVal( i, "K_CHOSA2_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "対客先予算比", printlist.getVal( i, "TAI_KYAKU_YOSAN_HI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済承認者", printlist.getVal( i, "K_SYOUNIN_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済調査者", printlist.getVal( i, "K_CHOSA_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済起草者", printlist.getVal( i, "K_TANTO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済商務事項確認", printlist.getVal( i, "K_SHOMUJIKO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済特記すべき", printlist.getVal( i, "K_HUTAI_JYOKEN_FLG" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済付帯条件添付ファイル", printlist.getVal( i, "K_HUTAI_TMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済添付ファイル", printlist.getVal( i, "K_TMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "要求工期TO", printlist.getVal( i, "KOKI_TO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "決済時STS", printlist.getVal( i, "STATUSCODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積時ステータス", printlist.getVal( i, "MSTATUS" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積時取扱", printlist.getVal( i, "MTORIATUKAIN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積時承認日", printlist.getVal( i, "MSHONIN_DATE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接NET見積", printlist.getVal( i, "TSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接NET目標", printlist.getVal( i, "TSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接NET設計査定", printlist.getVal( i, "TSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接NET見込", printlist.getVal( i, "TSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接粗利見積", printlist.getVal( i, "ASS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接粗利目標", printlist.getVal( i, "ASMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接粗利設計査定", printlist.getVal( i, "ASSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接粗利見込", printlist.getVal( i, "ASM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接Ｍ率見積", printlist.getVal( i, "MSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接Ｍ率目標", printlist.getVal( i, "MSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接Ｍ率設計査定", printlist.getVal( i, "MSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "直接Ｍ率見込", printlist.getVal( i, "MSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "人件費見積", printlist.getVal( i, "JSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "人件費目標", printlist.getVal( i, "JSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "人件費設計査定", printlist.getVal( i, "JSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "人件費見込", printlist.getVal( i, "JSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "労災他経費見積", printlist.getVal( i, "RSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "労災他経費目標", printlist.getVal( i, "RSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "労災他経費設計査定", printlist.getVal( i, "RSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "労災他経費見込", printlist.getVal( i, "RSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見積NET合計", printlist.getVal( i, "GSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "目標NET合計", printlist.getVal( i, "GMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "設計査定NET合計", printlist.getVal( i, "GSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "見込NET合計", printlist.getVal( i, "GSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "交渉時ステータス", printlist.getVal( i, "KSTATUS" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "交渉時取扱", printlist.getVal( i, "KTORIATUKAIN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "交渉時承認日", printlist.getVal( i, "KSHONIN_DATE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "二次所掌区分", printlist.getVal( i, "N_SHOSHO_KBN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "総ページ数", String.valueOf( bean.getKessaiPageCnt() + bean.getBikoPageCnt() + bean.getKousenPageCnt()) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "M_SHOMU_KOMOKU", printlist.getVal( i, "見積時商務事項問題事項" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "E_SHOMU_KOMOKU", printlist.getVal( i, "交渉終了時商務事項問題事項" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "M_hinagata", printlist.getVal( i, "M_HINAGATA" ) );		//2009/02/24 add
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "E_hinagata", printlist.getVal( i, "E_HINAGATA" ) );		//2009/02/24 add
            if( ret < 0 ) return ret;
            if(bean.getKousenPageCnt() > 0){
            	ret = svf.VrsOut( "KOUSEN_EXIST", "口銭有(別紙参照)" );		//2009/03/09 add
            }
            if( ret < 0 ) return ret;
            ret = setKessaiBikoSvf( svf, bean, i );
            if( ret < 0 ) return ret;

            // 2016B ADD(S)
            ret = svf.VrsOut( "MIT_SHONIN_DATE", printlist.getVal( i, "M_SYOUNIN_DATE" ) );     // 見積時 承認日
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "KESSAI_SHONIN_DATE", printlist.getVal( i, "K_SYOUNIN_DATE" ) );  // 交渉終了時 承認日
            if( ret < 0 ) return ret;
            // 2016B ADD(E)

        } catch( Exception e ){
            ret = -1;
        }
        return ret;
    }

    /**
     * SVFの決裁帳票の備考部分をセット
     * @param svf Svfオブジェクト
     * @param bean 印刷情報Bean
     * @exception 0:正常終了 1:異常終了
     */
    private int setKessaiBikoSvf( Vrw32 svf, KessaiPrintBean bean, int i ) {
        int ret = 0;
        sptList printlist = bean.getBukkenlist();
        try {
            Hashtable hash = printlist.getList( i );
            Enumeration keys = hash.keys();
            while (keys.hasMoreElements()) {

                String tmpstr = (String)keys.nextElement();
                String keybiko;

                // 見積概要1,見積概要2,見積概要3･･･とセット
                keybiko = "見積概要";
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if( Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= 5 ) {
                        // 備考がオーバーしていたらメッセージングする。
                        if( bean.isMGaiyoOver() && Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= 5 ) {
                            if( tmpstr.substring( keybiko.length() ).equals("1") ) {
                                ret = svf.VrsOut( keybiko + tmpstr.substring( keybiko.length() ), BIKO_OVER );
                            } else {
                                ret = svf.VrsOut( keybiko + tmpstr.substring( keybiko.length() ), "" );
                            }
                        } else {
                            ret = svf.VrsOut( keybiko + tmpstr.substring( keybiko.length() ), printlist.getVal( i, keybiko + tmpstr.substring( keybiko.length() ) ) );
                        }
                        if( ret < 0 ) return ret;
                    }
                }
                keybiko = "ネゴ概要";
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if( Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= 5 ) {
                        // 備考がオーバーしていたらメッセージングする。
                        if( bean.isNGaiyoOver() && Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= 5 ) {
                            if( tmpstr.substring( keybiko.length() ).equals("1") ) {
                                ret = svf.VrsOut( keybiko + tmpstr.substring( keybiko.length() ), BIKO_OVER );
                            } else {
                                ret = svf.VrsOut( keybiko + tmpstr.substring( keybiko.length() ), "" );
                            }
                        } else {
                            ret = svf.VrsOut( keybiko + tmpstr.substring( keybiko.length() ), printlist.getVal( i, keybiko + tmpstr.substring( keybiko.length() ) ) );
                        }
                        if( ret < 0 ) return ret;
                    }
                }
                keybiko = "見積付帯条件";
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if( Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= 2 ) {
                        if( bean.isMHutaiOver() ) {
                            // 備考がオーバーしていたらメッセージングする。
                            if( tmpstr.substring( keybiko.length() ).equals("1") ) {
                                ret = svf.VrsOut( keybiko + tmpstr.substring( keybiko.length() ), BIKO_OVER );
                            } else {
                                ret = svf.VrsOut( keybiko + tmpstr.substring( keybiko.length() ), "" );
                            }
                        } else {
                            ret = svf.VrsOut( keybiko + tmpstr.substring( keybiko.length() ), printlist.getVal( i, keybiko + tmpstr.substring( keybiko.length() ) ) );
                        }
                        if( ret < 0 ) return ret;
                    }
                }
                keybiko = "決済付帯条件";
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if( Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= 2 ) {
                        if( bean.isEHutaiOver() ) {
                            // 備考がオーバーしていたらメッセージングする。
                            if( tmpstr.substring( keybiko.length() ).equals("1") ) {
                                ret = svf.VrsOut( keybiko + tmpstr.substring( keybiko.length() ), BIKO_OVER );
                            } else {
                                ret = svf.VrsOut( keybiko + tmpstr.substring( keybiko.length() ), "" );
                            }
                        } else {
                            ret = svf.VrsOut( keybiko + tmpstr.substring( keybiko.length() ), printlist.getVal( i, keybiko + tmpstr.substring( keybiko.length() ) ) );
                        }
                        if( ret < 0 ) return ret;
                    }
                }
            }
        } catch( Exception e ) {
            ret = -1;
        }
        return ret;
    }
    /**
     * 口銭算出表を出力
     * @param conn
     * @param request
     * @param svf
     * @param printBean
     * @return
     */
    private int setKousenSvf(Connection conn ,HttpServletRequest request ,Vrw32 svf,KessaiPrintBean printBean,int page){
    	int ret = 0;
        // Q/#, REV, 予算正式FLG
        String Q_NO = getParam( request, "QNO");
        String Rev = getParam( request, "REV");
        String Ysflg = getParam( request, "YSFLG");
        KousenPrintBean bean = new KousenPrintBean();
    	try{
        	KousenPrintSver sver = new KousenPrintSver();
    		sver.init(conn);
            bean = sver.makePrintBean(Q_NO,Rev,Ysflg);
    		//ページ数をセット
    		bean.setPage(String.valueOf(printBean.getKessaiPageCnt() + page + 1));
    		bean.setTotalPage(String.valueOf(printBean.getKessaiPageCnt() + printBean.getBikoPageCnt() + 1));
            if("1".equals(bean.getKosenFlg())){
            	ret = sver.setSvfkousen(svf,bean);
            }
    	}catch(Exception e){
    		ret = -1;
    	}


    	return ret;
    }

    /**
     * PDFのファイル名を返す
     * @return 現在時刻を利用したユニークなPDFファイル名
     */
    private String getFileName() {

        // PDFのファイル名定義
        long FileTime = System.currentTimeMillis();

        // 乱数を使用してファイル名をよりユニークにする
        Random Uni  = new Random();
        Double DUni = new Double( Uni.nextDouble()*1000000 );
        DecimalFormat DForm = new DecimalFormat("0000000");
        String SUni = DForm.format( DUni );

        // PDFのファイル名作成
        return "kessai" + String.valueOf( FileTime ) + SUni + ".pdf";
    }

    /**
     * arrayのmaxを返す
     * @param array int[]
     * @return arrayのmax
     */
    private int max( int[] array ) {
        int tmp = array[0];
        for( int i = 1; i < array.length; i++ ) {
            if( tmp < array[i] ) {
                tmp = array[i];
            }
        }
        return tmp;
    }

    /**
     * tab文字で成形された文書をスペースに置き換えても成形されるようスペースを挿入
     * @param line
     * @return
     */
    private String adjTab(int line, int tabSize ) {
        StringBuffer sbtab = new StringBuffer("");
        int spaceSize = tabSize - line % tabSize;
        for( int i = 0; i < spaceSize; i++ ) {
            sbtab.append(" ");
        }
        return sbtab.toString();
    }

}

