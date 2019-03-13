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
  spt����   ���
  package   spt.kessai.kessai.print
  class     KessaiPrintSvlt
  @author   M.MASUKO@NPC
  @version  1.00 , 2003/06/17
                  , 2006/05/11 S.SHIMA@NPC DB������vrq����ł͂Ȃ�java������悤�ɂ������ߑ啝�ύX
****************************************/
public class KessaiPrintSvlt extends OrgSvlt {

    private String pdfPath;
    private String svfPath;//2010/09/09 frm�p�p�X��݂��邱�Ƃɂ���
    private String PathDelimiter;

    /** log4j�p
     */
    private static Category log = Category.getInstance( "KessaiPrintSvlt" );

    /**
     * ���l���[�̍s���̒�`
     * ���l���[�̍s����ύX����Ƃ��͂�����ύX���Ă��������B
     */
    private final static int M_HUTAI_ROW = 5;   // ���ώ��t�я󋵍s��
    private final static int E_HUTAI_ROW = 5;   // ���I�������t�я󋵍s��
    private final static int M_GAIYO_ROW = 30;  // ���ϊT�v�s��
    private final static int N_GAIYO_ROW = 30;  // �l�S�T�v�s��

    /**
     * ���l���I�[�o�[�����Ƃ��Ɉꖇ�ڂɕ\�����镶��
     */
    private final static String BIKO_OVER = "�����ɂ��ʎ��Q�Ɗ肢�܂��B";

    /**
     * initialize pdf�p�X�ƃp�X��؂蕶�����v���p�e�B�t�@�C�����擾
     */
    public void init( ServletConfig config ) throws ServletException {
        ResourceBundle bundle = ResourceBundle.getBundle(EcasEnv.properties_file);
        this.pdfPath = bundle.getString("PdfPath");
        this.svfPath = bundle.getString("SvfPath");//2010/09/09 frm�p�p�X��݂��邱�Ƃɂ���
        this.PathDelimiter = bundle.getString("PathDelimiter");
        super.init( config );
    }


    /* (�� Javadoc)
    * @see spt.kessai.OrgSvlt#setBeans(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.sql.Connection)
    */
    protected String setBeans(
        HttpServletRequest request
      , HttpServletResponse response
      , Connection conn
    ) throws sptException, SQLException {

    	log.debug("KessaiPrintSvlt start");

    	File cur = new File(".");
    	log.debug("�J�����g�p�X�F" + cur.getAbsolutePath());

        //2010/09/09 frm�p�p�X��݂��邱�Ƃɂ��� sptCookie cookie  = new sptCookie( request );

        String PdfName    = this.getFileName();
        //String path       = pdfPath + PathDelimiter + "kessai" + PathDelimiter;//�쐬���ꂽPDF�t�@�C���̒u���ꏊ
        String path       = pdfPath + PathDelimiter;//�쐬���ꂽPDF�t�@�C���̒u���ꏊ(PDF�t�@�C�������ɍ쐬����B���t�H���_�쐬���Y�ꂽ�ꍇ���l���j
        String frmPath    = svfPath + PathDelimiter;//2010/09/09 add frm�̒u���ꏊ
        //2010/09/09 �g�p���Ă��Ȃ��̂ŃR�����g String RealSvfPdf = pdfPath + PathDelimiter + "kessai" + PathDelimiter;

        // �\�Z����FLG
        String Ysflg = getParam( request, "YSFLG");

        String  formname;   // �t�H�[����

        // ���[������Bean���擾
        KessaiPrintBean printBean = this.makePrintBean( conn, request );

        if( Ysflg.equals("Y") ){
            formname   = "yosan.frm";
        } else {
    		formname   = "seisiki.frm";
        }
    	log.debug("form���擾�F" + formname);

        // SVF����I�u�W�F�N�g�̍쐬
        Vrw32   svf = new Vrw32();
        int ret = svf.VrInit( "MS932" );
        log.debug("SVF����I�u�W�F�N�g�쐬�@ret=" + ret);
        if( ret != 0 )  throw new sptException("SVF����I�u�W�F�N�g�쐬���G���[");

        // PDF�t�@�C�������Z�b�g
        /*ret = svf.VrSetSpoolFileName2( path + "files" + PathDelimiter + PdfName );
        if( ret != 0 )  throw new sptException("PDF�t�@�C�����w�莞�G���[ "
                                              + "file�p�X�y" + path + "files" + PathDelimiter + PdfName + "�z");*/
        ret = svf.VrSetSpoolFileName2( path + PdfName );
        log.debug("PDF�t�@�C�����w��@ret=" + ret + "  file�p�X�y" + path + PdfName + "�z");
        if( ret != 0 )  throw new sptException("PDF�t�@�C�����w�莞�G���[ "
                                              + "file�p�X�y" + path + PdfName + "�z");

        /**
         * ���ْ��[�������Z�b�g
         */
        // �l���t�@�C���Əo�͂̃��[�h�̎w��
        //2010/09/09 del ret = svf.VrSetForm( path + formname, 5 );
        //if( ret != 0 )  throw new sptException("�t�H�[���w�莞�G���[ formname�y" + path + formname + "�z");
        ret = svf.VrSetForm( frmPath + "kessai" + PathDelimiter + formname, 5 );//2010/09/09 add
        log.debug("�t�H�[���w��(���ْ��[����)�@ret=" + ret + "  form�p�X�y" + frmPath + "kessai" + PathDelimiter + formname + "�z");
        if( ret != 0 )  throw new sptException("�t�H�[���w�莞�G���[ formname�y" + frmPath + "kessai" + PathDelimiter + formname + "�z");

        //SVF�I�u�W�F�N�g�Ƀ��X�g�̒l���Z�b�g
        for( int i = 0; i < printBean.getBukkenlist().getSize(); i++ ) {

            if( Ysflg.equals("Y") ){
                ret = setToSvfYosan( svf, printBean, i );
            } else {
                ret = setToSvfSeisiki( svf, printBean, i );
            }
            log.debug("�f�[�^�Z�b�g(���ْ��[����)�@ret=" + ret );
            if( ret != 0 )  throw new sptException("�f�[�^�Z�b�g���G���[");
            //  ��s�o��
            ret = svf.VrEndRecord();
            log.debug("�f�[�^�Z�b�g�I��(���ْ��[����)�@ret=" + ret );
            if( ret != 0 )  throw new sptException("�f�[�^�Z�b�g�I�����G���[");
        }

        ret = svf.VrPrint();
        log.debug("���|�[�g���C�^�[���[�̈���̎��s(���ْ��[����)�@ret=" + ret );
        if( ret != 0 )  throw new sptException("���|�[�g���C�^�[���[�̈���̎��s���G���[");
        int kousenpage= 0;


        /**
         * ���l���[�������Z�b�g
         */
        for( int i = 0; i < printBean.getBikoPageCnt(); i++ ) {
            //2010/09/09 del �l���t�@�C���Əo�͂̃��[�h�̎w��
            //ret = svf.VrSetForm( path + "bikou.frm", 5 );
        	//if( ret != 0 )  throw new sptException("�t�H�[���w�莞�G���[ formname�y" + path + "bikou.frm" + "�z");
        	ret = svf.VrSetForm( frmPath + "kessai" + PathDelimiter + "bikou.frm", 5 );//2010/09/09 add
        	log.debug("�t�H�[���w��(���l���[����)�@ret=" + ret + "  form�p�X�y" + frmPath + "kessai" + PathDelimiter + "bikou.frm" + "�z");
            if( ret != 0 )  throw new sptException("�t�H�[���w�莞�G���[ formname�y" + frmPath + "kessai" + PathDelimiter + "bikou.frm" + "�z");

            ret = setToSvfBiko( svf, printBean, i );
            log.debug("�f�[�^�Z�b�g(���l���[����)�@ret=" + ret );
            if( ret != 0 )  throw new sptException("�f�[�^�Z�b�g���G���[");

            ret = svf.VrEndRecord();
            log.debug("�f�[�^�Z�b�g�I��(���l���[����)�@ret=" + ret );
            if( ret != 0 )  throw new sptException("�f�[�^�Z�b�g�I�����G���[");

            ret = svf.VrPrint();
            log.debug("���|�[�g���C�^�[���[�̈���̎��s(���l���[����)�@ret=" + ret );
            if( ret != 0 )  throw new sptException("���|�[�g���C�^�[���[�̈���̎��s���G���[");
            kousenpage = i + 1;

        }

        if( Ysflg.equals("S") ) {
	        /**
	         * 2009/02/23  add
	         * ���K���[�������Z�b�g
	         */
	        //2010/09/09 del ret = svf.VrSetForm( pdfPath + PathDelimiter + "kousen" + PathDelimiter + "kousen.frm", 1 );
        	//if( ret != 0 )  throw new sptException("�t�H�[���w�莞�G���[ formname�y" + pdfPath + PathDelimiter + "kousen" + PathDelimiter + "kousen.frm" + "�z");
        	ret = svf.VrSetForm( frmPath + "kousen" + PathDelimiter + "kousen.frm", 1 );//2010/09/09 add
        	log.debug("�t�H�[���w��(���K���[����)�@ret=" + ret + "  form�p�X�y" + frmPath + "kousen" + PathDelimiter + "kousen.frm" + "�z");
	        if( ret != 0 )  throw new sptException("�t�H�[���w�莞�G���[ formname�y" + frmPath + "kousen" + PathDelimiter + "kousen.frm" + "�z");

	        ret = setKousenSvf(conn,request,svf,printBean,kousenpage);
	        log.debug("�f�[�^�Z�b�g(���K���[����)�@ret=" + ret );
	        if( ret != 0 )  throw new sptException("�f�[�^�Z�b�g���G���[");

	        ret = svf.VrEndPage();
	        log.debug("�f�[�^�Z�b�g�I��(���K���[����)�@ret=" + ret );
	        if( ret != 0 )  throw new sptException("�f�[�^�Z�b�g�I�����G���[");

	        ret = svf.VrPrint();
	        log.debug("���|�[�g���C�^�[���[�̈���̎��s(���K���[����)�@ret=" + ret );
	        if( ret != 0 )  throw new sptException("���|�[�g���C�^�[���[�̈���̎��s���G���[");

        }

        ret = svf.VrQuit();
        log.debug("VrQuit�@ret=" + ret );

        // �f�[�^�Z�b�g�A�g���r���[�g
        //2010/09/13 PDF�o�͕��@��ύX(���X�|���X�ɓf���o�����Ƃɂ����j
        //request.setAttribute( "PdfName" , PdfName );
        request.setAttribute( "PdfName" , path + PdfName);
        log.debug("KessaiPrintSvlt end");
        return "kessai/kessai/print/print.jsp";
    }


    /**
     * ���[����ɕK�v�ȏ��̓�����Bean���쐬����
     * @param conn Connection�I�u�W�F�N�g
     * @param request HttpServletRequest
     * @return ���[������Bean
     * @throws sptException
     */
    private KessaiPrintBean makePrintBean( Connection conn, HttpServletRequest request ) throws sptException {

        // Q/#, REV, �\�Z����FLG
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

        //���������̖�莖�����擾
        String mShomuMondai = psver.getShomuCheckStr( Q_NO, Revition, Ysflg, "M" );
        String eShomuMondai = psver.getShomuCheckStr( Q_NO, Revition, Ysflg, "E" );

        int colsize;
        int colsize2;
        int rowsize;
        String svfFieldNm;

        /**
         * ���ϕt�я����𕪉���Vector�ɃZ�b�g
         */
        colsize = 54;
        colsize2 = 100;
        Vector vecMhutai = this.getTokened( bukkenlist.getVal(0, "M_HUTAI_JYOKEN"), colsize );
        Vector vecMhutai2 = this.getTokened( bukkenlist.getVal(0, "M_HUTAI_JYOKEN"), colsize2 ); //���l���[�p��100�s���ɐ؂�������
        // �P���ڂ͂Q�s�A����ȍ~��M_HUTAI_ROW�s�\������
        int pageCntMhutai;
        if( vecMhutai.size() <= 2 ) {
            pageCntMhutai = 0;
        } else {
            pageCntMhutai = ( ( vecMhutai2.size() - 1 ) / M_HUTAI_ROW ) + 1;
        }

        /**
         * ���ϕt�я����𕪉���Vector�ɃZ�b�g
         */
        colsize = 54;
        colsize2 = 100;
        Vector vecEhutai = this.getTokened( bukkenlist.getVal(0, "K_HUTAI_JYOKEN"), colsize );
        Vector vecEhutai2 = this.getTokened( bukkenlist.getVal(0, "K_HUTAI_JYOKEN"), colsize2 ); //���l���[�p��100�s���ɐ؂�������
        // �P���ڂ͂Q�s�A����ȍ~��E_HUTAI_ROW�s�\������
        int pageCntEhutai;
        if( vecEhutai.size() <= 2 ) {
            pageCntEhutai = 0;
        } else {
            pageCntEhutai = ( ( vecEhutai2.size() - 1 ) / E_HUTAI_ROW ) + 1;
        }

        /**
         * ���ϊT�v�𕪉���Vector�ɃZ�b�g
         */
        colsize = 100;
        Vector vecGaiyo = this.getTokened( bukkenlist.getVal(0, "MITUMORI_GAIYOU"), colsize );
        // �P���ڂ͂T�s�A����ȍ~��M_GAIYO_ROW�s�\������
        int pageCntMGaiyo;
        if( vecGaiyo.size() <= 5 ) {
            pageCntMGaiyo = 0;
        } else {
            pageCntMGaiyo = ( ( vecGaiyo.size() - 1 ) / M_GAIYO_ROW ) + 1;
        }

        /**
         * �l�S�T�v�𕪉���Vector�ɃZ�b�g
         */
        colsize = 100;
        Vector vecNego = this.getTokened( bukkenlist.getVal(0, "NEGO"), colsize );
        // �P���ڂ͂T�s�A����ȍ~��N_GAIYO_ROW�s�\������
        int pageCntNGaiyo = ( vecNego.size() - 1 ) / 5;
        if( vecNego.size() <= 5 ) {
            pageCntNGaiyo = 0;
        } else {
            pageCntNGaiyo = ( ( vecNego.size() - 1 ) / N_GAIYO_ROW ) + 1;
        }

        for( int index = 0; index < bukkenlist.getSize(); index++ ) {

            // ���ϕt�я����Z�b�g
            for( int vecindex = 0; vecindex < vecMhutai.size(); vecindex++ ) {
                bukkenlist.putVal( index, "���ϕt�я���" + String.valueOf( vecindex + 1 ), (String) vecMhutai.get(vecindex) );
            }

            // ���ϕt�я���(���l���[�p��100�s����)�Z�b�g
            for( int vecindex = 0; vecindex < vecMhutai2.size(); vecindex++ ) {
                bukkenlist.putVal( index, "���ϔ��l�p�t�я���" + String.valueOf( vecindex + 1 ), (String) vecMhutai2.get(vecindex) );
            }


            // ���ϕt�я����Z�b�g
            for( int vecindex = 0; vecindex < vecEhutai.size(); vecindex++ ) {
                bukkenlist.putVal( index, "���ϕt�я���" + String.valueOf( vecindex + 1 ), (String) vecEhutai.get(vecindex) );
            }

            // ���ϕt�я���(���l���[�p��100�s����)�Z�b�g
            for( int vecindex = 0; vecindex < vecEhutai2.size(); vecindex++ ) {
                bukkenlist.putVal( index, "���ϔ��l�p�t�я���" + String.valueOf( vecindex + 1 ), (String) vecEhutai2.get(vecindex) );
            }

            // ���ϊT�v�Z�b�g
            for( int vecindex = 0; vecindex < vecGaiyo.size(); vecindex++ ) {
                bukkenlist.putVal( index, "���ϊT�v" + String.valueOf( vecindex + 1 ), (String) vecGaiyo.get(vecindex) );
            }

            // �l�S�T�v�Z�b�g
            for( int vecindex = 0; vecindex < vecNego.size(); vecindex++ ) {
                bukkenlist.putVal( index, "�l�S�T�v" + String.valueOf( vecindex + 1 ), (String) vecNego.get(vecindex) );
            }

            // ���ώ�����������莖�����Z�b�g
            bukkenlist.putVal( index, "���ώ�����������莖��", mShomuMondai );

            // ���I��������������莖�����Z�b�g
            bukkenlist.putVal( index, "���I��������������莖��", eShomuMondai );
        }

        // ���ْ��[�y�[�W��
        int kessaiPageCnt = ( ( bukkenlist.getSize() - 1 )  / 11 ) + 1;

        // ���l���[�y�[�W�� ���ϕt�я����A���ٕt�я����A���ϊT�v�A�l�S�T�v��Max�����l�̃y�[�W���ɂȂ�
        int bikoPageCnt = this.max( new int[]{ pageCntMhutai, pageCntEhutai, pageCntMGaiyo, pageCntNGaiyo } );

        //2009/02/24 ���K��񂪂���΂P�y�[�W���₷
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
         * Bean�ɃZ�b�g��RETURN
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
     * ���s�R�[�h���邢�͎w�肳�ꂽ�������܂ōs�����當���𕪉���Vector��ɓ����B
     * ��ʏ�̃e�L�X�g�G���A�̌����ڂƒ��[��̌����ڂ����킹�邽�߂ɍs���Ă���B
     * @param strOrg ���̕�����
     * @param LINEBREAK ���s���镶����
     * @return ���s�܂���colsize�����ŕ�����ꂽVector
     * �� LINEBREAK=10
     * strOrg ="01234567890123[���s]
     *          4567890123456789"
     * Vector(0) = 0123456789 ��10�����ŕ���
     * Vector(1) = 0123       ��123��456�̊Ԃɂ�����s���������m������
     * Vector(2) = 4567890123 �����s����x�������炳���10������ɂȂ����番��
     * Vector(3) = 89         �����s����x�������炳���10������ɂȂ����番��
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
                lineBreak = true;       // �������s
                index++;                // "\r\n"�ƂȂ��Ă���̂�\n�̕��C���f�b�N�X���P�i�߂�
            } else {
                // �S�p
                if( nowChr.getBytes().length == 2 ) {
                    nowline += 2;
                    sb.append( nowChr );
                // ���p
                } else {
                    // �^�u�������� �^�u�������X�y�[�X�ɕϊ����ăZ�b�g 2006/04/06 add
                    if( nowChr.equals("\t") ) {
                        nowChr = this.adjTab( nowline, 8 );
                        nowline += nowChr.length();
                    // �^�u�ȊO�̒ʏ핶��
                    } else {
                        nowline++;
                    }
                    sb.append( nowChr );
                }
                // �Ō�ɉ��s���Ȃ��ꍇ�łȂ��̂�
                if( index == strOrg.length() - 1 ) {
                    lineBreak = true;
                }
            }
            // �s�̍Ōオ�S�p�����ł܂����ł��܂��Ƃ��̑΍� 2006/03/31 add
            boolean overNext = false;
            if( index != strOrg.length() - 1 ) {
                String nextChr = strOrg.substring(index + 1, index + 2);
                if( nowline == LINEBREAK - 1 && nextChr.getBytes().length == 2 ) {
                    overNext = true;
                }
                //���݂�LINEBREAK�s�ڂł��̎��ɉ��s�������Ă���Ƃ���d���s�ɂȂ��Ă��܂��̂�
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
     * SVF�ɒl���Z�b�g �\�Z
     * @param svf Svf�I�u�W�F�N�g
     * @param printlist ����ŏo���f�[�^
     * @exception 0:����I�� 1:�ُ�I��
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

                // ���ϊT�v1,���ϊT�v2,���ϊT�v3����ƃZ�b�g
                keybiko = "���ϊT�v";
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
                keybiko = "�l�S�T�v";
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if(  Integer.parseInt( tmpstr.substring( keybiko.length() ) ) >= ( N_GAIYO_ROW * bikoPageIndex ) + 1
                       && Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= N_GAIYO_ROW * ( bikoPageIndex + 1 )
                    ) {
                        fieldCnt = Integer.parseInt( tmpstr.substring( keybiko.length() ) ) - ( N_GAIYO_ROW * bikoPageIndex );
                        ret = svf.VrsOut( keybiko + String.valueOf( fieldCnt ), printlist.getVal( 0, keybiko + tmpstr.substring( keybiko.length() ) ) );
                        if( ret < 0 ) return ret;
                    }
                }
                keybiko = "���ϔ��l�p�t�я���";
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if(  Integer.parseInt( tmpstr.substring( keybiko.length() ) ) >= ( M_HUTAI_ROW * bikoPageIndex ) + 1
                      && Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= M_HUTAI_ROW * ( bikoPageIndex + 1 )
                    ) {
                        fieldCnt = Integer.parseInt( tmpstr.substring( keybiko.length() ) ) - ( M_HUTAI_ROW * bikoPageIndex );
                        ret = svf.VrsOut( "���ϕt�я���" + String.valueOf( fieldCnt ), printlist.getVal( 0, keybiko + tmpstr.substring( keybiko.length() ) ) );
                        if( ret < 0 ) return ret;
                    }
                }
                keybiko = "���ϔ��l�p�t�я���";
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if(  Integer.parseInt( tmpstr.substring( keybiko.length() ) ) >= ( E_HUTAI_ROW * bikoPageIndex ) + 1
                      && Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= E_HUTAI_ROW * ( bikoPageIndex + 1 )
                    ) {
                        fieldCnt = Integer.parseInt( tmpstr.substring( keybiko.length() ) ) - ( E_HUTAI_ROW * bikoPageIndex );
                        ret = svf.VrsOut( "���ϕt�я���" + String.valueOf( fieldCnt ), printlist.getVal( 0, keybiko + tmpstr.substring( keybiko.length() ) ) );
                        if( ret < 0 ) return ret;
                    }
                }
            }
            ret = svf.VrsOut( "�y�[�W��", String.valueOf( bean.getKessaiPageCnt() + ( bikoPageIndex + 1 ) ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���y�[�W��", String.valueOf( bean.getKessaiPageCnt() + bean.getBikoPageCnt() + bean.getKousenPageCnt()) );
            if( ret < 0 ) return ret;
        } catch( Exception e ){
            ret = -1;
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * SVF�ɒl���Z�b�g �\�Z
     * @param svf Svf�I�u�W�F�N�g
     * @param bean ������Bean
     * @exception 0:����I�� 1:�ُ�I��
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
            ret = svf.VrsOut( "Spurt�ԍ�", printlist.getVal( i, "SPURT_NO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�쐬��", "[" + printlist.getVal( i, "SAKUSEIBU" ) + "]" );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�쐬��", "[" + printlist.getVal( i, "SAKUSEIKA" ) + "]" );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "BuCode", printlist.getVal( i, "BU_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���", printlist.getVal( i, "SYURUI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "JobGr", printlist.getVal( i, "JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ω񓚓���", printlist.getVal( i, "MT_KAITO_NITIGEN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���σt�H�[��", printlist.getVal( i, "MI_FORM" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�_��`��", printlist.getVal( i, "KEIYAKU_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�S����", printlist.getVal( i, "TANTO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�v�����g", printlist.getVal( i, "PLANT_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�\���쐬��", printlist.getVal( i, "SAKUSEIKA" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�q��S����", printlist.getVal( i, "KYAKU_TAN_KA_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�q��S����", printlist.getVal( i, "KYAKU_TAN_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "Q/#�\���p", printlist.getVal( i, "QNO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "Q/#", printlist.getVal( i, "KEY_QNO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�v���[��", printlist.getVal( i, "YOKYU_NOKI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���{����", printlist.getVal( i, "JISSI_JIKI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�q�挩�ϔԍ�", printlist.getVal( i, "KYAKU_KEIYAKU_NO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�������t", printlist.getVal( i, "CHUMONBI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�茟��", printlist.getVal( i, "TEIKEN_KAISU" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "O/#", printlist.getVal( i, "ONO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�v���H��FROM", printlist.getVal( i, "KOKI_FROM" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�q��", printlist.getVal( i, "KYAKU_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����", printlist.getVal( i, "KENMEI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���σX�e�[�^�X", printlist.getVal( i, "M_STATUS" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϋ��zSP", printlist.getVal( i, "SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�\�Z�l���ڕW", printlist.getVal( i, "MOKUHYO_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���Ϗ��F��", printlist.getVal( i, "M_SYOUNIN_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϒ�����", printlist.getVal( i, "M_CHOSA_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϋN����", printlist.getVal( i, "M_TANTO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "SP����", printlist.getVal( i, "KAKU_MIKOMI_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "SP����NET", printlist.getVal( i, "MIKOMI_NET_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "SP����M��", printlist.getVal( i, "MRITU_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�������\�Z", printlist.getVal( i, "CHUCHO_YOS_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���Ϗ��������m�F", printlist.getVal( i, "M_SHOMUJIKO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϓ��L���ׂ�", printlist.getVal( i, "M_HUTAI_JYOKEN_FLG" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϕt�я����Y�t�t�@�C��", printlist.getVal( i, "M_HUTAI_TEMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���JOBGr", printlist.getVal( i, "SHUKAN_JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���l", printlist.getVal( i, "BIKO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�֌WJobGr", printlist.getVal( i, "KAIKEI_JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�H�ꕔ�ۃx���_", printlist.getVal( i, "SHOSHO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET", printlist.getVal( i, "SHU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET", printlist.getVal( i, "MIKO_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�ڕWNET", printlist.getVal( i, "MOKUHYO_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�݌v����NET", printlist.getVal( i, "SEKKEISATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϓY�t�t�@�C��", printlist.getVal( i, "M_TEMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�d�͌v��\�Z", printlist.getVal( i, "DEN_KEIKAKU_YOSAN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�R�����g1", printlist.getVal( i, "KAKUNIN_COMMENT_1" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�m�F��", printlist.getVal( i, "KAKUNINBI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�m�F��S����", printlist.getVal( i, "KAKUNIN_TAN_KA_1" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�m�F��S����", printlist.getVal( i, "KAKUNIN_TAN_1" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�_��\�zSP", printlist.getVal( i, "YOSO_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�_��\�z����NET", printlist.getVal( i, "K_MIKOMINET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�_��\�zM��", printlist.getVal( i, "K_MRITU" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�_��\�z�e��", printlist.getVal( i, "K_SORI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���Ϗ��F��", printlist.getVal( i, "K_SYOUNIN_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϒ�����", printlist.getVal( i, "K_CHOSA_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϋN����", printlist.getVal( i, "K_TANTO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���Ϗ��������m�F", printlist.getVal( i, "K_SHOMUJIKO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϓ��L���ׂ�", printlist.getVal( i, "K_HUTAI_JYOKEN_FLG" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϕt�я����Y�t�t�@�C��", printlist.getVal( i, "K_HUTAI_TMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϓY�t�t�@�C��", printlist.getVal( i, "K_TMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "��JOBGR", printlist.getVal( i, "N_JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�񎟏����R�[�h", printlist.getVal( i, "N_SHOSHO_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�v���H��TO", printlist.getVal( i, "KOKI_TO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ώ�STS", printlist.getVal( i, "STATUSCODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���X�e�[�^�X", printlist.getVal( i, "K_STATUS" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET����", printlist.getVal( i, "TSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET�ڕW", printlist.getVal( i, "TSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET�݌v����", printlist.getVal( i, "TSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET����", printlist.getVal( i, "TSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڑe������", printlist.getVal( i, "ASS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڑe���ڕW", printlist.getVal( i, "ASMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڑe���݌v����", printlist.getVal( i, "ASSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڑe������", printlist.getVal( i, "ASM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڂl������", printlist.getVal( i, "MSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڂl���ڕW", printlist.getVal( i, "MSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڂl���݌v����", printlist.getVal( i, "MSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڂl������", printlist.getVal( i, "MSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�l�����", printlist.getVal( i, "JSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�l����ڕW", printlist.getVal( i, "JSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�l����݌v����", printlist.getVal( i, "JSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�l�����", printlist.getVal( i, "JSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�J�Б��o���", printlist.getVal( i, "RSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�J�Б��o��ڕW", printlist.getVal( i, "RSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�J�Б��o��݌v����", printlist.getVal( i, "RSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�J�Б��o���", printlist.getVal( i, "RSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET���v", printlist.getVal( i, "GSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�ڕWNET���v", printlist.getVal( i, "GMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�݌v����NET���v", printlist.getVal( i, "GSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET���v", printlist.getVal( i, "GSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�񎟏����敪", printlist.getVal( i, "N_SHOSHO_KBN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���y�[�W��", String.valueOf( bean.getKessaiPageCnt() + bean.getBikoPageCnt()) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "M_SHOMU_KOMOKU", printlist.getVal( i, "���ώ�����������莖��" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "E_SHOMU_KOMOKU", printlist.getVal( i, "���I��������������莖��" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "M_hinagata", printlist.getVal( i, "M_HINAGATA" ) );		//2009/02/24 add
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "E_hinagata", printlist.getVal( i, "E_HINAGATA" ) );		//2009/02/24 add
            if( ret < 0 ) return ret;
            ret = setKessaiBikoSvf( svf, bean, i );
            if( ret < 0 ) return ret;

            // 2016B ADD(S)
            ret = svf.VrsOut( "MIT_SHONIN_DATE", printlist.getVal( i, "M_SYOUNIN_DATE" ) );     // ���ώ� ���F��
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "KESSAI_SHONIN_DATE", printlist.getVal( i, "K_SYOUNIN_DATE" ) );  // ���I���� ���F��
            if( ret < 0 ) return ret;
            // 2016B ADD(E)

        } catch( Exception e ){
            ret = -1;
        }
        return ret;
    }

    /**
     * SVF�ɒl���Z�b�g ����
     * @param svf Svf�I�u�W�F�N�g
     * @param bean ������Bean
     * @exception 0:����I�� 1:�ُ�I��
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
            ret = svf.VrsOut( "Spurt�ԍ�", printlist.getVal( i, "SPURT_NO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�쐬��", "[" + printlist.getVal( i, "SAKUSEIBU" ) + "]" );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�쐬��", "[" + printlist.getVal( i, "SAKUSEIKA" ) + "]" );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "BuCode", printlist.getVal( i, "BU_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���", printlist.getVal( i, "SYURUI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "JobGr", printlist.getVal( i, "JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ω񓚓���", printlist.getVal( i, "MT_KAITO_NITIGEN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���σt�H�[��", printlist.getVal( i, "MI_FORM" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�_��`��", printlist.getVal( i, "KEIYAKU_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�S����", printlist.getVal( i, "TANTO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�v�����g", printlist.getVal( i, "PLANT_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�\���쐬��", printlist.getVal( i, "SAKUSEIKA" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�q��S����", printlist.getVal( i, "KYAKU_TAN_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "Q/#", printlist.getVal( i, "KEY_QNO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "Q/#�\���p", printlist.getVal( i, "QNO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�q��S����", printlist.getVal( i, "KYAKU_TAN_KA_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�v���[��", printlist.getVal( i, "YOKYU_NOKI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���{����", printlist.getVal( i, "JISSI_JIKI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�q�挩�ϔԍ�", printlist.getVal( i, "KYAKU_KEIYAKU_NO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�������t", printlist.getVal( i, "CHUMONBI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�茟��", printlist.getVal( i, "TEIKEN_KAISU" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "O/#", printlist.getVal( i, "ONO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�v���H��FROM", printlist.getVal( i, "KOKI_FROM" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�q��", printlist.getVal( i, "KYAKU_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����", printlist.getVal( i, "KENMEI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���σX�e�[�^�X", printlist.getVal( i, "M_STATUS" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϋ��zSP", printlist.getVal( i, "SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�_��\�zSP", printlist.getVal( i, "KEIYAKU_YOSO_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�_��\�z����NET", printlist.getVal( i, "MIKOMI_NET_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�_��\�zM��", printlist.getVal( i, "MRITU_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���Ϗ��F��", printlist.getVal( i, "M_SYOUNIN_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����2��������", printlist.getVal( i, "M_CHOSA2_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϒ�����", printlist.getVal( i, "M_CHOSA_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϋN����", printlist.getVal( i, "M_TANTO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���{�\�Z", printlist.getVal( i, "JISSI_YOSAN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�Η\�Z��", printlist.getVal( i, "TAIYOSAN_HI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���Ϗ��������m�F", printlist.getVal( i, "M_SHOMUJIKO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϓ��L���ׂ�", printlist.getVal( i, "M_HUTAI_JYOKEN_FLG" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϕt�я����Y�t�t�@�C��", printlist.getVal( i, "M_HUTAI_TEMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���JOBGr", printlist.getVal( i, "SHUKAN_JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "��JOBGR", printlist.getVal( i, "N_JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���l", printlist.getVal( i, "BIKO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�֌WJobGr", printlist.getVal( i, "KAIKEI_JOBGR_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�H�ꕔ�ۃx���_", printlist.getVal( i, "SHOSHO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET", printlist.getVal( i, "SHU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET", printlist.getVal( i, "MIKO_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�ڕWNET", printlist.getVal( i, "MOKUHYO_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�݌v����NET", printlist.getVal( i, "SEKKEISATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�񎟏����R�[�h", printlist.getVal( i, "N_SHOSHO_CODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϓY�t�t�@�C��", printlist.getVal( i, "M_TEMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���X�e�[�^�X", printlist.getVal( i, "K_STATUS" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���vSP", printlist.getVal( i, "G_S_YOSO_SP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���v����NET", printlist.getVal( i, "G_MIKOMINET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���vM��", printlist.getVal( i, "G_MRITU" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���v�e��", printlist.getVal( i, "G_SORI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����2��������", printlist.getVal( i, "K_CHOSA2_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�΋q��\�Z��", printlist.getVal( i, "TAI_KYAKU_YOSAN_HI" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���Ϗ��F��", printlist.getVal( i, "K_SYOUNIN_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϒ�����", printlist.getVal( i, "K_CHOSA_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϋN����", printlist.getVal( i, "K_TANTO_NAME" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���Ϗ��������m�F", printlist.getVal( i, "K_SHOMUJIKO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϓ��L���ׂ�", printlist.getVal( i, "K_HUTAI_JYOKEN_FLG" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϕt�я����Y�t�t�@�C��", printlist.getVal( i, "K_HUTAI_TMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ϓY�t�t�@�C��", printlist.getVal( i, "K_TMP" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�v���H��TO", printlist.getVal( i, "KOKI_TO" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ώ�STS", printlist.getVal( i, "STATUSCODE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ώ��X�e�[�^�X", printlist.getVal( i, "MSTATUS" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ώ��戵", printlist.getVal( i, "MTORIATUKAIN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ώ����F��", printlist.getVal( i, "MSHONIN_DATE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET����", printlist.getVal( i, "TSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET�ڕW", printlist.getVal( i, "TSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET�݌v����", printlist.getVal( i, "TSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET����", printlist.getVal( i, "TSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڑe������", printlist.getVal( i, "ASS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڑe���ڕW", printlist.getVal( i, "ASMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڑe���݌v����", printlist.getVal( i, "ASSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڑe������", printlist.getVal( i, "ASM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڂl������", printlist.getVal( i, "MSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڂl���ڕW", printlist.getVal( i, "MSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڂl���݌v����", printlist.getVal( i, "MSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���ڂl������", printlist.getVal( i, "MSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�l�����", printlist.getVal( i, "JSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�l����ڕW", printlist.getVal( i, "JSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�l����݌v����", printlist.getVal( i, "JSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�l�����", printlist.getVal( i, "JSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�J�Б��o���", printlist.getVal( i, "RSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�J�Б��o��ڕW", printlist.getVal( i, "RSMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�J�Б��o��݌v����", printlist.getVal( i, "RSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�J�Б��o���", printlist.getVal( i, "RSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET���v", printlist.getVal( i, "GSS_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�ڕWNET���v", printlist.getVal( i, "GMOKU_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�݌v����NET���v", printlist.getVal( i, "GSSATEI_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "����NET���v", printlist.getVal( i, "GSM_NET" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�����X�e�[�^�X", printlist.getVal( i, "KSTATUS" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�����戵", printlist.getVal( i, "KTORIATUKAIN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�������F��", printlist.getVal( i, "KSHONIN_DATE" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "�񎟏����敪", printlist.getVal( i, "N_SHOSHO_KBN" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "���y�[�W��", String.valueOf( bean.getKessaiPageCnt() + bean.getBikoPageCnt() + bean.getKousenPageCnt()) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "M_SHOMU_KOMOKU", printlist.getVal( i, "���ώ�����������莖��" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "E_SHOMU_KOMOKU", printlist.getVal( i, "���I��������������莖��" ) );
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "M_hinagata", printlist.getVal( i, "M_HINAGATA" ) );		//2009/02/24 add
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "E_hinagata", printlist.getVal( i, "E_HINAGATA" ) );		//2009/02/24 add
            if( ret < 0 ) return ret;
            if(bean.getKousenPageCnt() > 0){
            	ret = svf.VrsOut( "KOUSEN_EXIST", "���K�L(�ʎ��Q��)" );		//2009/03/09 add
            }
            if( ret < 0 ) return ret;
            ret = setKessaiBikoSvf( svf, bean, i );
            if( ret < 0 ) return ret;

            // 2016B ADD(S)
            ret = svf.VrsOut( "MIT_SHONIN_DATE", printlist.getVal( i, "M_SYOUNIN_DATE" ) );     // ���ώ� ���F��
            if( ret < 0 ) return ret;
            ret = svf.VrsOut( "KESSAI_SHONIN_DATE", printlist.getVal( i, "K_SYOUNIN_DATE" ) );  // ���I���� ���F��
            if( ret < 0 ) return ret;
            // 2016B ADD(E)

        } catch( Exception e ){
            ret = -1;
        }
        return ret;
    }

    /**
     * SVF�̌��ْ��[�̔��l�������Z�b�g
     * @param svf Svf�I�u�W�F�N�g
     * @param bean ������Bean
     * @exception 0:����I�� 1:�ُ�I��
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

                // ���ϊT�v1,���ϊT�v2,���ϊT�v3����ƃZ�b�g
                keybiko = "���ϊT�v";
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if( Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= 5 ) {
                        // ���l���I�[�o�[���Ă����烁�b�Z�[�W���O����B
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
                keybiko = "�l�S�T�v";
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if( Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= 5 ) {
                        // ���l���I�[�o�[���Ă����烁�b�Z�[�W���O����B
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
                keybiko = "���ϕt�я���";
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if( Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= 2 ) {
                        if( bean.isMHutaiOver() ) {
                            // ���l���I�[�o�[���Ă����烁�b�Z�[�W���O����B
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
                keybiko = "���ϕt�я���";
                if( tmpstr.indexOf( keybiko ) != -1 ) {
                    if( Integer.parseInt( tmpstr.substring( keybiko.length() ) ) <= 2 ) {
                        if( bean.isEHutaiOver() ) {
                            // ���l���I�[�o�[���Ă����烁�b�Z�[�W���O����B
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
     * ���K�Z�o�\���o��
     * @param conn
     * @param request
     * @param svf
     * @param printBean
     * @return
     */
    private int setKousenSvf(Connection conn ,HttpServletRequest request ,Vrw32 svf,KessaiPrintBean printBean,int page){
    	int ret = 0;
        // Q/#, REV, �\�Z����FLG
        String Q_NO = getParam( request, "QNO");
        String Rev = getParam( request, "REV");
        String Ysflg = getParam( request, "YSFLG");
        KousenPrintBean bean = new KousenPrintBean();
    	try{
        	KousenPrintSver sver = new KousenPrintSver();
    		sver.init(conn);
            bean = sver.makePrintBean(Q_NO,Rev,Ysflg);
    		//�y�[�W�����Z�b�g
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
     * PDF�̃t�@�C������Ԃ�
     * @return ���ݎ����𗘗p�������j�[�N��PDF�t�@�C����
     */
    private String getFileName() {

        // PDF�̃t�@�C������`
        long FileTime = System.currentTimeMillis();

        // �������g�p���ăt�@�C��������胆�j�[�N�ɂ���
        Random Uni  = new Random();
        Double DUni = new Double( Uni.nextDouble()*1000000 );
        DecimalFormat DForm = new DecimalFormat("0000000");
        String SUni = DForm.format( DUni );

        // PDF�̃t�@�C�����쐬
        return "kessai" + String.valueOf( FileTime ) + SUni + ".pdf";
    }

    /**
     * array��max��Ԃ�
     * @param array int[]
     * @return array��max
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
     * tab�����Ő��`���ꂽ�������X�y�[�X�ɒu�������Ă����`�����悤�X�y�[�X��}��
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

