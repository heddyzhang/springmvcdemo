<!DOCTYPE html>
<html lang="ja">
    <head>
        <c:import url="${componentPath}metainfo.xhtml" />
        <%-- IE9のバグ？でテーブルが1列ずれる事象が発生しているため、IE8レンダリングモードを利用する --%>
<%--STEP3で主要ブラウザがIE11になるためここはもう不要
        <meta charset="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=8" />
--%>
        <title><fmt:message key="applicationName" bundle="${label}"/></title>
        <c:import url="${componentFullPath}csslink.xhtml" />
         <style type="text/css">
            #head2table{
                table-layout: fixed;
                width: 99%;
            }
            #head2table td{
                white-space: nowrap;
                overflow: hidden;
            }
            #search-result-data tr{
                height:22px;
            }
        </style>
    </head>
    <body>
        <%-- 変数定義 --%>
        <c:set value="${sUtil.exeFormatYm(detailHeader.ankenUriageEndDate)}" var="uriageYm"/>
        <c:set value="${sUtil.exeFormatYm(detailHeader.kanjoDate)}" var="kanjoYm"/>
        <c:set value="width:98px" var="numberStyle"/>
        <%-- 編集ボタンの表示権限(0:非表示 1:表示 --%>
        <c:set value="0" var="editBtnFlg"/>
        <c:if test="${s004Bean.ankenInputFlg == 1 && (authUtl.enableFlg('KIKAN_S_EDITALL', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1 || authUtl.enableFlg('KIKAN_S_EDITNET', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1)}">
            <c:set value="1" var="editBtnFlg"/>
        </c:if>
        <c:set value="0" var="editAllFlg"/>
        <c:if test="${authUtl.enableFlg('KIKAN_S_EDITALL', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}">
            <c:set value="1" var="editAllFlg"/>
        </c:if>

        <!-- Wrap all page content here -->
        <div id="wrap">
        <!-- PS-Promis Header -->
        <c:import url="${componentPath}header.xhtml"/>

        <!-- Begin page content -->
        <div class="container" id="main">
            <div class="search-frame-dateil">

            <form id="mainForm" method="post" target="_self" style="height: 100%;">

            <!-- header component -->
            <c:import url="${componentPath}detailHeader.xhtml">
                <c:param name="id" value="S004" />
            </c:import>

            <!-- 検索結果 -->
            <div class="search-result">
                <input type="hidden" name="editFlg" id="editFlg" value="${s004Bean.editFlg}" />

            <!-- ボタン -->
            <%-- 参照モード時のボタン表示(S) --%>
            <c:if test="${s004Bean.editFlg != '1'}">
            <div class="search-result_btn" style="margin:10px 0px">
                <c:if test="${editBtnFlg == '1'}" >
                <div class="btn-group">
                    <button type="button" class="btn btn-default" onclick="edit()" ><fmt:message key="edit" bundle="${label}"/>&nbsp;</button>
                </div><!-- btn-group -->
                </c:if>
                <div class="btn-group">
                    <button type="button" class="btn btn-default" onclick="openThisSelectHistory();" data-auth-flg="${authUtl.enableFlg("KIKAN_S_RIREKI", detailHeader.divisionCode, s004Bean.rirekiFlg)}"><fmt:message key="history" bundle="${label}"/><fmt:message key="disp" bundle="${label}"/>&nbsp;</button>
                </div><!-- btn-group -->
                <c:if test="${authUtl.enableFlg('KIKAN_S_INFOEDIT', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                <div class="btn-group">
                    <button type="button" class="btn btn-default" onclick="return syuekiInfo('<fmt:message key="forceSave" bundle="${label}"/>')" ><fmt:message key="detailInfo" bundle="${label}"/>&nbsp;</button>
                </div><!-- btn-group -->
                </c:if>
                <div class="btn-group">
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" data-auth-flg="${authUtl.enableFlg("KIKAN_S_FUNC", detailHeader.divisionCode, s004Bean.rirekiFlg)}"><fmt:message key="kino" bundle="${label}"/>&nbsp;<span class="caret"></span></button>
                    <ul class="dropdown-menu" role="menu" style="width:180px;">
                        <li id="jpyUnitSelect">
                            <a href="javascript:void(0)" onclick="return false"><fmt:message key="jpyUnitLabel" bundle="${label}"/><fmt:message key="unitLabel" bundle="${label}"/><fmt:message key="change" bundle="${label}"/></a>
                            <ul>
                                <li><a href="javascript:void(0);" id="jpyUnit1"><fmt:message key="jpyUnit1" bundle="${label}"/></a></li>
                                <li><a href="javascript:void(0);" id="jpyUnit2"><fmt:message key="jpyUnit2" bundle="${label}"/></a></li>
                                <li><a href="javascript:void(0);" id="jpyUnit3"><fmt:message key="jpyUnit3" bundle="${label}"/></a></li>
                            </ul>
                            <input type="hidden" name="jpyUnit" value="${fn:escapeXml(s004Bean.jpyUnit)}" id="jpyUnit" />
                        </li>
                        <%-- NETカテゴリ編集,見込SP通貨編集は案件の編集権限が存在する場合のみ表示 --%>
                        <c:if test="${editBtnFlg == '1'}" >
                        <li class="divider"></li>
                        <li>
                            <ul id="calcDiffSelect">
                                <li><a href="javascript:void(0);" onclick="return openUriageChosei(document.getElementById('mainForm'));" id="UriageChouseiOpen"><fmt:message key="net" bundle="${label}"/><fmt:message key="category" bundle="${label}"/><fmt:message key="edit" bundle="${label}"/></a></li>
                                <li><a href="javascript:void(0);" onclick="return exeOpenCurEdit('<fmt:message key="forceSave" bundle="${label}"/>');" id="CurEditOpen"><fmt:message key="dispMikomiSpCurEdit" bundle="${label}"/></a></li>
                            </ul>
                        </li>
                        </c:if>
                    </ul>
                </div><!-- btn-group -->

                <c:if test="${authUtl.enableFlg('KIKAN_S_UPDATE', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1 && s004Bean.saisyuUpdeteBtnFlg == '1'}">
                <div class="btn-group"><!-- 最新値更新 -->
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" ><fmt:message key="newestValueSave" bundle="${label}"/>&nbsp;<span class="caret"></span></button>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="javascript:void(0)" id="newest1" onclick="updateNewData('<fmt:message key="confirmNewDataHb" bundle="${label}"/>', 1);"><fmt:message key="hatsuban" bundle="${label}"/><fmt:message key="update" bundle="${label}"/></a></li>
                        <%-- [受注通知更新]は進行基準で取り込むとおかしくなるため表示しない --%>
                        <c:if test="${divisonComponentPage.isNuclearDivision(detailHeader.divisionCode)}"><%-- 見積更新は[原子力]のみ利用するため、他事業部では表示されないようにする --%>
                        <li><a href="javascript:void(0)" id="newest3" onclick="updateNewData('<fmt:message key="confirmNewDataMit" bundle="${label}"/>', 3);"><fmt:message key="mit" bundle="${label}"/><fmt:message key="update" bundle="${label}"/></a></li>
                        </c:if>
                    </ul>
                </div><!-- btn-group -->
                </c:if>

                <c:if test="${detailHeader.uriageEndFlg == '2' && detailHeader.ankenFlg == '1' && authUtl.enableFlg('KANBAIJ_DISP', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}">
                <div class="btn-group">
                    <button type="button" class="btn btn-default" onclick="return kanbaiJ('<fmt:message key="forceSave" bundle="${label}"/>', '<fmt:message key="registSuccess" bundle="${label}"/>', '<fmt:message key="editError" bundle="${label}"/>');" id="kanbaiJOpen"><fmt:message key="kanbai" bundle="${label}"/>&nbsp;</button>
                </div><!-- btn-group -->
                </c:if>
                <c:if test="${authUtl.enableFlg('KIKAN_S_DL', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1 || authUtl.enableFlg('KIKAN_S_UP', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                <div class="btn-group">
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"><fmt:message key="excelEdit" bundle="${label}"/>&nbsp;<span class="caret"></span></button>
                    <ul class="dropdown-menu" role="menu">
                        <c:if test="${authUtl.enableFlg('KIKAN_S_DL', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                        <li><a href="javascript:void(0);" onclick="return openDownload('<fmt:message key="alertNoShinkoDownload" bundle="${label}"/>');"><fmt:message key="download" bundle="${label}"/></a></li>
                        </c:if>
                         <c:if test="${authUtl.enableFlg('KIKAN_S_UP', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1 && s004Bean.ankenInputFlg == 1}" >
                        <li><a href="javascript:void(0);" onclick="return openUpload(document.getElementById('mainForm'));"><fmt:message key="upload" bundle="${label}"/></a></li>
                        </c:if>
                    </ul>
                </div><!-- btn-group -->
                </c:if>
<%--
                <c:if test="${authUtl.enableFlg('KIKAN_S_PDF', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                <div class="btn-group">
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"><fmt:message key="output" bundle="${label}"/>&nbsp;<span class="caret"></span></button>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="javascript:void(0);" onclick="return openOutput(document.getElementById('mainForm'));"><fmt:message key="sheetOutput" bundle="${label}"/></a></li>
                    </ul>
                </div><!-- btn-group -->
                </c:if>
--%>
                <c:if test="${authUtl.enableFlg('KIKAN_S_BOOK_ON', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                <div class="btn-group" id="addBookMarkArea" style="display:none">
                    <input type="button" id="addBookMark" class="btn btn-default" value="<fmt:message key="addBookMark" bundle="${label}"/>">
                </div><!-- btn-group -->
                </c:if>
                <c:if test="${authUtl.enableFlg('KIKAN_S_BOOK_OFF', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                <div class="btn-group" id="delBookMarkArea" style="display:none">
                    <input type="button" id="delBookMark" class="btn btn-default" value="<fmt:message key="delBookMark" bundle="${label}"/>">
                </div><!-- btn-group -->
                </c:if>
                <c:if test="${authUtl.enableFlg('KIKAN_S_HELP', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                <div class="btn-group">
                    <c:import url="${componentPath}helplink.xhtml"/>
                </div><!-- btn-group -->
                </c:if>
                <c:if test="${authUtl.enableFlg('KIKAN_S_CLOSE', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                <div class="btn-group">
                    <button type="button" class="btn btn-default" id="close" onclick="windowClose()"><fmt:message key="close" bundle="${label}"/>&nbsp;</button>
                </div><!-- btn-group -->
                </c:if>
                <c:if test="${authUtl.enableFlg('KIKAN_S_RECAL', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1 && detailHeader.ankenEntity.saikeisanFlg == '1'}" >
                <div class="btn-group">
                    <button type="button" class="btn btn-default" id="recal" onclick="calcAction('<fmt:message key="confirmRecal" bundle="${label}"/>')"><fmt:message key="recal" bundle="${label}"/></button>
                </div><!-- btn-group -->
                </c:if>
                <c:if test="${authUtl.enableFlg('KIKAN_S_QLIKVIEW', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                <div class="btn-group">
                    <button type="button" class="btn btn-default" id="recal" onclick="openQlikView('${detailHeader.ankenEntity.mainOrderNo}')"><fmt:message key="qlikView" bundle="${label}"/></button>
                </div><!-- btn-group -->
                </c:if>
                <c:if test="${authUtl.enableFlg('KIKAN_S_ATTACH', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}">
                <div class="btn-group"><%-- 添付資料 --%>
                    <button type="button" class="btn btn-default" onclick="return exeOpenAttachInfo('<fmt:message key="forceSave" bundle="${label}"/>');"><fmt:message key="dispNameAttachment" bundle="${label}"/>&nbsp;</button>
                </div><!-- btn-group -->
                </c:if>

                <%-- (原子力)関連システムへのリンクボタン --%>
                <c:import url="${componentPath}bprSysLinkButton.xhtml" >
                    <c:param name="ankenId" value="${detailHeader.ankenId}"/>
                    <c:param name="rirekiId" value="${detailHeader.rirekiId}"/>
                    <c:param name="rirekiFlg" value="${detailHeader.rirekiFlg}"/>
                    <c:param name="procId" value="S004"/>
                    <c:param name="marginFlg" value="1"/>
                </c:import>
            </div>
            </c:if>
            <%-- 参照モード時のボタン表示(E) --%>

            <%-- 編集モード時のボタン表示(S) --%>
            <c:if test="${s004Bean.editFlg == '1'}">
            <div class="search-result_btn" style="margin:10px 0px">
                <c:if test="${authUtl.enableFlg('KIKAN_S_SAVE', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                <div class="btn-group">
                    <button type="button" class="btn btn-default" onclick="save('<fmt:message key="confirmSave" bundle="${label}"/>', '<fmt:message key="notEdit" bundle="${label}"/>', '<fmt:message key="registSuccess" bundle="${label}"/>', '<fmt:message key="editError" bundle="${label}"/>')" ><fmt:message key="save" bundle="${label}"/>&nbsp;</button>
                </div><!-- btn-group -->
                </c:if>
                <c:if test="${authUtl.enableFlg('KIKAN_S_INFOEDIT', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                <div class="btn-group">
                    <button type="button" class="btn btn-default" onclick="return syuekiInfo('<fmt:message key="forceSave" bundle="${label}"/>')" ><fmt:message key="detailInfo" bundle="${label}"/>&nbsp;</button>
                </div><!-- btn-group -->
                </c:if>
                <div class="btn-group">
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"><fmt:message key="kino" bundle="${label}"/>&nbsp;<span class="caret"></span></button>
                    <ul class="dropdown-menu" role="menu" style="width:160px;">
                        <c:if test="${s004Bean.ankenInputFlg == 1}" >
                        <li><a href="javascript:void(0);" onclick="return uriageChosei('<fmt:message key="forceSave" bundle="${label}"/>', '<fmt:message key="registSuccess" bundle="${label}"/>', '<fmt:message key="editError" bundle="${label}"/>');" id="UriageChouseiOpen"><fmt:message key="net" bundle="${label}"/><fmt:message key="category" bundle="${label}"/><fmt:message key="edit" bundle="${label}"/></a></li>
                        </c:if>
<%--
                        <c:if test="${authUtl.enableFlg('KIKAN_S_INFOEDIT', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                        <li><a href="javascript:void(0);" onclick="return syuekiInfo('<fmt:message key="forceSave" bundle="${label}"/>');" id="SyuekiInfoOpen"><fmt:message key="dispSyuekiInfoEdit" bundle="${label}"/></a></li>
                        </c:if>
--%>
                        <c:if test="${authUtl.enableFlg('KIKAN_S_RELOAD', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                        <li><a href="javascript:void(0);" onclick="return expectReflection('<fmt:message key="expectReflectMsg" bundle="${label}"/>', '<fmt:message key="registSuccess" bundle="${label}"/>', '<fmt:message key="editError" bundle="${label}"/>');" id="expectReflection"><fmt:message key="dispMikomiNextMonth" bundle="${label}"/></a></li>
                        </c:if>
                        <c:if test="${authUtl.enableFlg('KIKAN_S_KAISYU_EDIT', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}">
                        <li><a href="javascript:void(0);" onclick="return exeOpenKaisyuEdit('<fmt:message key="forceSave" bundle="${label}"/>');" id="KaisyuEditOpen"><fmt:message key="dispNameKaisyuEdit" bundle="${label}"/></a></li>
                        </c:if>
                        <li><a href="javascript:void(0);" onclick="return exeOpenCurEdit('<fmt:message key="forceSave" bundle="${label}"/>');" id="CurEditOpen"><fmt:message key="dispMikomiSpCurEdit" bundle="${label}"/></a></li>
                    </ul>
                </div><!-- btn-group -->

                <c:if test="${authUtl.enableFlg('KIKAN_S_UPDATE', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1 &&  s004Bean.saisyuUpdeteBtnFlg == '1'}">
                <div class="btn-group"><!-- 最新値更新 -->
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" ><fmt:message key="newestValueSave" bundle="${label}"/>&nbsp;<span class="caret"></span></button>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="javascript:void(0)" id="newest1" onclick="updateNewData('<fmt:message key="confirmNewDataHb" bundle="${label}"/>', 1);"><fmt:message key="hatsuban" bundle="${label}"/><fmt:message key="update" bundle="${label}"/></a></li>
                        <%-- [受注通知更新]は進行基準で取り込むとおかしくなるため表示しない --%>
                        <c:if test="${divisonComponentPage.isNuclearDivision(detailHeader.divisionCode)}"><%-- 見積更新は[原子力]のみ利用するため、他事業部では表示されないようにする --%>
                        <li><a href="javascript:void(0)" id="newest3" onclick="updateNewData('<fmt:message key="confirmNewDataMit" bundle="${label}"/>', 3);"><fmt:message key="mit" bundle="${label}"/><fmt:message key="update" bundle="${label}"/></a></li>
                        </c:if>
                    </ul>
                </div><!-- btn-group -->
                </c:if>

                <c:if test="${detailHeader.uriageEndFlg == '2' && detailHeader.ankenFlg == '1' && authUtl.enableFlg('KANBAIJ_DISP', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}">
                <div class="btn-group">
                    <button type="button" class="btn btn-default" onclick="return kanbaiJ('<fmt:message key="forceSave" bundle="${label}"/>', '<fmt:message key="registSuccess" bundle="${label}"/>', '<fmt:message key="editError" bundle="${label}"/>');" id="kanbaiJOpen"><fmt:message key="kanbai" bundle="${label}"/>&nbsp;</button>
                </div><!-- btn-group -->
                </c:if>
                <div class="btn-group">
                    <button type="button" class="btn btn-default" onclick="cancel('<fmt:message key="confirmEditCancel" bundle="${label}"/>');"><fmt:message key="cancel" bundle="${label}"/>&nbsp;</button>
                </div><!-- btn-group -->
                <c:if test="${authUtl.enableFlg('KIKAN_S_HELP', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                <div class="btn-group">
                    <c:import url="${componentPath}helplink.xhtml"/>
                </div><!-- btn-group -->
                </c:if>
                <c:if test="${authUtl.enableFlg('KIKAN_S_RECAL', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                <div class="btn-group">
                    <button type="button" class="btn btn-default" id="recal" onclick="calcAction('<fmt:message key="confirmRecalEdit" bundle="${label}"/>')"><fmt:message key="recal" bundle="${label}"/></button>
                </div><!-- btn-group -->
                </c:if>
                <c:if test="${authUtl.enableFlg('KIKAN_S_QLIKVIEW', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}" >
                <div class="btn-group">
                    <button type="button" class="btn btn-default" id="recal" onclick="openQlikView('${detailHeader.ankenEntity.mainOrderNo}')"><fmt:message key="qlikView" bundle="${label}"/></button>
                </div><!-- btn-group -->
                </c:if>
                <c:if test="${authUtl.enableFlg('KIKAN_S_ATTACH', detailHeader.divisionCode, s004Bean.rirekiFlg) == 1}">
                <div class="btn-group"><%-- 添付資料 --%>
                    <button type="button" class="btn btn-default" onclick="return exeOpenAttachInfo('<fmt:message key="forceSave" bundle="${label}"/>');"><fmt:message key="dispNameAttachment" bundle="${label}"/>&nbsp;</button>
                </div><!-- btn-group -->
                </c:if>

                <%-- (原子力)関連システムへのリンクボタン --%>
                <c:import url="${componentPath}bprSysLinkButton.xhtml" >
                    <c:param name="ankenId" value="${detailHeader.ankenId}"/>
                    <c:param name="rirekiId" value="${detailHeader.rirekiId}"/>
                    <c:param name="rirekiFlg" value="${detailHeader.rirekiFlg}"/>
                    <c:param name="procId" value="S004"/>
                    <c:param name="marginFlg" value="1"/>
                </c:import>
            </div>
            </c:if>
            <%-- 編集モード時のボタン表示(E) --%>

            <div align="left" id="data_haed">
                <table id="head2table" style="table-layout: fixed; width:100%">
                    <tr>
                        <td valign="top" width="230px">
                            <table class="table-bordered psprimis-list-table fixed-layout table-grid" >
                                <tr>
                                    <td >
                                        <c:if test="${s004Bean.kikanForm != s004Bean.kikanFromList.get(0)}">
                                        <a href="javascript:void(0);" onclick="return changeKikan('B', '<fmt:message key="forceSave" bundle="${label}"/>');"><fmt:message key="allLeft" bundle="${pspLabel}"/></a>
                                        </c:if>
                                    </td>
                                    <th style="width:80px"><fmt:message key="kikan" bundle="${label}"/></th>
                                    <td >
                                        <select name="kikanForm" id="kikanForm" onchange="return changeKikan('0', '<fmt:message key="forceSave" bundle="${label}"/>');">
                                            <c:forEach items="${s004Bean.kikanFromList}" var="kikanFrom" varStatus="s">
                                                <option value="${fn:escapeXml(kikanFrom)}" ${kikanFrom == s004Bean.kikanForm ? "selected" : ""}>${html.nbsp(fn:escapeXml(sUtil.kikanLabel(kikanFrom)))}</option>
                                            </c:forEach>
                                        </select>
                                        <input type="hidden" name="defaultKikanForm" value="${fn:escapeXml(s004Bean.kikanForm)}" id="defaultKikanForm" />
                                        <!--
                                        ${html.nbsp(fn:escapeXml(sUtil.kikanLabel(s004Bean.kikanForm)))}
                                        <input type="hidden" name="kikanForm" value="${fn:escapeXml(s004Bean.kikanForm)}" id="kikanForm" />
                                        -->
                                    </td>
                                    <td style="width:20px">～</td>
                                    <td>
                                        <c:set value="${sUtil.calcKikan(s004Bean.kikanForm, 1)}" var="kikanTo"/>
                                        ${html.nbsp(fn:escapeXml(sUtil.kikanLabel(kikanTo)))}
                                        <input type="hidden" name="kikanTo" value="${fn:escapeXml(kikanTo)}" id="kikanTo" />
                                    </td>
                                    <td >
                                        <c:if test="${s004Bean.kikanForm != s004Bean.kikanFromList.get(s004Bean.kikanFromList.size()-1)}">
                                        <a href="javascript:void(0);" onclick="return changeKikan('A', '<fmt:message key="forceSave" bundle="${label}"/>');"><fmt:message key="allRight" bundle="${pspLabel}"/></a>
                                        </c:if>
                                    </td>
                                </tr>
                            </table>
                            <input type="hidden" name="kikanChangeFlg" value="" id="kikanChangeFlg" />
                        </td>
                        <td valign="top" width="770px">
                            <c:import url="${componentPath}detailInfoHistory.xhtml"/>
                        </td>
                    </tr>
                </table>
            </div>
            <div style="display: flex; width: 100%;">
                <div style="width: 80%;">
                    <a href="javascript:void(0)" id="ListAllOpen"><span class="tree-text">＋</span>:<fmt:message key="allTenkai" bundle="${label}"/></a>
                    <a href="javascript:void(0)" id="ListAllClose"><span class="tree-text">－</span>:<fmt:message key="allKanjyo" bundle="${label}"/></a>
                </div>
                <div style="width: 20%; text-align: right;">
                    <fmt:message key="jpyUnitLabel" bundle="${label}"/><fmt:message key="unitLabel" bundle="${label}"/>：
                    <c:if test="${s004Bean.jpyUnit == 1}"><fmt:message key="jpyUnit1" bundle="${label}"/></c:if>
                    <c:if test="${s004Bean.jpyUnit == 1000}"><fmt:message key="jpyUnit2" bundle="${label}"/></c:if>
                    <c:if test="${s004Bean.jpyUnit == 1000000}"><fmt:message key="jpyUnit3" bundle="${label}"/></c:if>&nbsp;&nbsp;
                    <!--<fmt:message key="foreignUnitLabel" bundle="${label}"/><fmt:message key="unitLabel" bundle="${label}"/>：<fmt:message key="foreignUnit2" bundle="${label}"/>-->
                </div>
            </div>
            <div id="search-result-div"><!--  class="search-result-divm"-->
                <table border=0 id="search-result-data2" style="height: 100%;">
                    <tr>
                        <td valign="top" >
                            <div align=left style="height:220px;overflow-x:hidden;overflow-y:hidden;" id="all_line">
                                <table  border="0">
                                    <tr>
                                        <td style="vertical-align:top;">
                                            <!-- zhangt Mod(s) -->
                                            <div id="Head1" style="width:180px;overflow-x:hidden;overflow-y:hidden;">
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <tr>
                                                        <th style="width:105px"><fmt:message key="kanjoYm" bundle="${label}"/></th>
                                                        <td style="width:92px"><fmt:formatDate value="${detailHeader.kanjoDate}" pattern="yyyy/MM" /></td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="2" class="subTitle"><fmt:message key="juchuManagement" bundle="${label}"/></td>
                                                    </tr>
                                                </table>
                                            </div>
                                             <!-- zhangt Mod(e) -->
                                            <div id="List1" style="height:253px;width:180px;overflow-x:hidden;overflow-y:hidden;" onMouseWheel="return leftScroll(this);"  onscroll="jsScrollTop(List2,this)">
                                                <!-- zhangt Add(s) -->
                                                <!-- ↓↓↓　受注SP　↓↓↓ -->
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <!-- 受注SP(発番SP) 合計　タイトル Start -->
                                                    <tr style="height:23px;" class="total_disp_field">
                                                        <td style="width:20px">
                                                            <a href="javascript:void(0)" id="juchuSpListOpen" class="tree-text">－</a>
                                                            <input type="hidden" name="juchuSpListOpenFlg" value="${fn:escapeXml(s004Bean.juchuSpListOpenFlg)}" id="juchuSpListOpenFlg"/>
                                                        </td>
                                                        <td style="width:100px" colspan="2">
                                                            <fmt:message key="juchuSp" bundle="${label}"/>(<fmt:message key="hatSp" bundle="${label}"/>)
                                                        </td>
                                                        <td style="width:60px" align="center"><fmt:message key="total" bundle="${label}"/></td>
                                                    </tr>
                                                    <!-- 受注SP(発番SP) 合計　タイトル End -->
                                                    <!-- 受注レート (内訳) タイトル Start -->
                                                    <c:forEach items="${s004Bean.jyuchuRate}" var="jyuchuRate" varStatus="s">
                                                    <tr style="height:23px;">
                                                        <td style="height:23px;" class="total_disp_field"></td>
                                                        <c:if test="${s.index==0}">
                                                        <th colspan="2" rowspan="${s004Bean.jyuchuRate.size()}" valign="top">
                                                            <fmt:message key="juchuRate" bundle="${label}"/>
                                                        </th>
                                                        </c:if>
                                                        <th >${fn:escapeXml(jyuchuRate.CurrencyCode)}</th>
                                                    </tr>
                                                    </c:forEach>
                                                    <!-- 受注レート (内訳) タイトル End -->
                                                    <!-- 受注金額  (内訳) タイトル Start -->
                                                    <c:forEach items="${s004Bean.jyuchuKingaku}" var="jyuchuKingaku" varStatus="s">
                                                    <tr style="height:23px;">
                                                        <td style="height:23px;" class="total_disp_field"></td>
                                                        <c:if test="${s.index==0}">
                                                        <th colspan="2" rowspan="${s004Bean.jyuchuKingaku.size()}" valign="top">
                                                            <fmt:message key="juchuGaku" bundle="${label}"/>
                                                        </th>
                                                        </c:if>
                                                        <th >${fn:escapeXml(jyuchuKingaku.CurrencyCode)}</th>
                                                    </tr>
                                                    </c:forEach>
                                                    <!-- 受注金額  (内訳) タイトル End -->
                                                </table>
                                                <!-- ↓↓↓　受注NET　↓↓↓ -->
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <!-- 受注NET(発番NET) 合計　タイトル Start -->
                                                    <tr style="height:23px;" class="total_disp_field">
                                                        <td style="width:20px">
                                                            <a href="javascript:void(0)" id="juchuNetListOpen" class="tree-text">－</a>
                                                            <input type="hidden" name="juchuNetListOpenFlg" value="${fn:escapeXml(s004Bean.juchuNetListOpenFlg)}" id="juchuNetListOpenFlg"/>
                                                        </td>
                                                        <td style="width:100px" colspan="2">
                                                            <fmt:message key="juchuNet" bundle="${label}"/>(<fmt:message key="hatsubanNet" bundle="${label}"/>)
                                                        </td>
                                                        <td style="width:60px" align="center"><fmt:message key="total" bundle="${label}"/></td>
                                                    </tr>
                                                    <!-- 受注NET(発番NET) 合計　タイトル End -->
                                                    <!-- 一括見込NET タイトル Start -->
                                                    <tr style="height:23px;">
                                                        <th colspan="4" >
                                                            <fmt:message key="ikkatsuMikomiNet" bundle="${label}"/>
                                                        </th>
                                                    </tr>
                                                    <!-- 一括見込NET タイトル End -->
                                                    <!-- 受注粗利 タイトル Start -->
                                                    <tr style="height:23px;" >
                                                        <th colspan="4" class="total_disp_field">
                                                            <fmt:message key="arari" bundle="${label}"/>
                                                        </th>
                                                    </tr>
                                                    <!-- 受注粗利 タイトル End -->
                                                    <!-- 受注M率 タイトル Start -->
                                                    <tr style="height:23px;">
                                                        <th colspan="4" class="total_disp_field">
                                                            <fmt:message key="mrate" bundle="${label}"/>
                                                        </th>
                                                    </tr>
                                                    <!-- 受注M率 タイトル End -->
                                                <table>    
                                                <br>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <tr>
                                                        <th style="width:105px"><fmt:message key="kanjoYm" bundle="${label}"/></th>
                                                        <td style="width:92px"><fmt:formatDate value="${detailHeader.kanjoDate}" pattern="yyyy/MM" /></td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="2" class="subTitle"><fmt:message key="uriageManagement" bundle="${label}"/></td>
                                                    </tr>
                                                </table>
                                                <!-- zhangt Add(e) -->
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <tr style="height:23px;" class="total_disp_field">
                                                        <td style="width:20px">
                                                            <a href="javascript:void(0)" id="spListOpen" class="tree-text">－</a>
                                                            <input type="hidden" name="spListOpenFlg" value="${fn:escapeXml(s004Bean.spListOpenFlg)}" id="spListOpenFlg"/>
                                                        </td>
                                                        <td style="width:100px" colspan="2">
                                                            &nbsp;<fmt:message key="keiyakuSp" bundle="${label}"/>
                                                        </td>
                                                        <td style="width:60px" align="center"> <fmt:message key="total" bundle="${label}"/></td>
                                                    </tr>
                                                    <c:forEach items="${s004Bean.contractAmount}" var="contractAmount" varStatus="s">
                                                    <tr style="height:23px;">
                                                        <td style="height:23px;" class="total_disp_field"></td>
                                                        <c:if test="${s.index==0}">
                                                        <th rowspan="${s004Bean.contractAmount.size()}"></th>
                                                        </c:if>
                                                        <th >${fn:escapeXml(contractAmount.currencyCode)}</th>
                                                        <th >
                                                            <c:choose>
                                                                <c:when test="${contractAmount.keiyakuRate == 'hosei'}">
                                                                    <fmt:message key="hosei" bundle="${label}"/>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <fmt:formatNumber value="${contractAmount.keiyakuRate}" pattern="${sUtil.formatRate()}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </th>
                                                    </tr>
                                                    </c:forEach>
                                                </table>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <tr style="height:23px;" class="total_disp_field">
                                                        <td style="width:20px">
                                                            <a href="javascript:void(0)" id="netListOpen" class="tree-text">－</a>
                                                            <input type="hidden" name="netListOpenFlg" value="${fn:escapeXml(s004Bean.netListOpenFlg)}" id="netListOpenFlg"/>
                                                        </td>
                                                        <td style="width:100px" colspan="2">
                                                            <fmt:message key="estimateCost" bundle="${label}"/>
                                                        </td>
                                                        <td style="width:60px" align="center"> <fmt:message key="total" bundle="${label}"/></td>
                                                    </tr>
                                                    <tr style="height:23px;">
                                                        <td class="total_disp_field"></td>
                                                        <th rowspan="4"></th>
                                                        <th colspan="2"><fmt:message key="hatsubanNet" bundle="${label}"/></th>
                                                    </tr>
                                                    <tr style="height:23px;">
                                                        <td class="total_disp_field"></td>
                                                        <th colspan="2"><fmt:message key="miHatsubanNet" bundle="${label}"/></th>
                                                    </tr>
                                                    <tr style="height:23px;">
                                                        <td class="total_disp_field"></td>
                                                        <th colspan="2"><fmt:message key="seibanSonekiNet" bundle="${label}"/></th>
                                                    </tr>
                                                    <tr style="height:23px;">
                                                        <td class="total_disp_field"></td>
                                                        <th colspan="2"><fmt:message key="kawaseEikyo" bundle="${label}"/></th>
                                                    </tr>
                                                </table>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <tr style="height:23px;" class="total_disp_field">
                                                        <td style="width:20px">
                                                            <a href="javascript:void(0)" id="uriage1ListOpen" class="tree-text">－</a>
                                                            <input type="hidden" name="uriage1ListOpenFlg" value="${fn:escapeXml(s004Bean.uriage1ListOpenFlg)}" id="uriage1ListOpenFlg"/>
                                                        </td>
                                                        <td style="width:100px" colspan="2">
                                                            <fmt:message key="uriageAmount" bundle="${label}"/>
                                                        </td>
                                                        <td style="width:60px" align="center"><fmt:message key="now" bundle="${label}"/><fmt:message key="total" bundle="${label}"/></td>
                                                    </tr>
                                                    <c:forEach items="${s004Bean.salesList}" var="salesList" varStatus="s">
                                                    <tr style="height:23px;">
                                                        <td class="total_disp_field"></td>
                                                        <c:if test="${s.index==0}">
                                                        <th rowspan="${s004Bean.salesList.size()}"></th>
                                                        </c:if>
                                                        <c:choose>
                                                            <c:when test="${salesList.uriageRate==null}">
                                                        <th >${fn:escapeXml(salesList.currencyCode)}</th>
                                                        <th ><fmt:message key="tatene" bundle="${label}"/></th>
                                                            </c:when>
                                                            <c:when test="${salesList.uriageRate!=null}">
                                                                <c:choose>
                                                                    <c:when test="${salesList.uriageRate == 'kawaseRate'}">
                                                        <th colspan="2"><fmt:message key="kawaseRate" bundle="${label}"/></th>
                                                                    </c:when>
                                                                    <c:when test="${salesList.uriageRate == 'kawaseDiff'}">
                                                        <th colspan="2"><fmt:message key="kawaseDiff" bundle="${label}"/></th>
                                                                    </c:when>
                                                                </c:choose>
                                                            </c:when>
                                                        </c:choose>
                                                    </tr>
                                                    </c:forEach>
                                                </table>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <tr style="height:23px;" class="total_disp_field" >
                                                        <td style="width:20px">
                                                            <a href="javascript:void(0)" id="uriage2ListOpen" class="tree-text">－</a>;
                                                            <input type="hidden" name="uriage2ListOpenFlg" value="${fn:escapeXml(s004Bean.uriage2ListOpenFlg)}" id="uriage2ListOpenFlg"/>
                                                        </td>
                                                        <td style="width:100px" colspan="2">
                                                            <fmt:message key="uriageAmount" bundle="${label}"/>
                                                        </td>
                                                        <td style="width:60px" align="center"><fmt:message key="ruikei" bundle="${label}"/><fmt:message key="total" bundle="${label}"/></td>
                                                    </tr>
                                                    <c:forEach items="${s004Bean.ruikeiSalesList}" var="ruikeiSalesList" varStatus="s">
                                                    <tr style="height:23px;">
                                                        <td class="total_disp_field"></td>
                                                        <c:if test="${s.index==0}">
                                                            <th rowspan="${s004Bean.ruikeiSalesList.size()}"></th>
                                                        </c:if>
                                                        <th >${fn:escapeXml(ruikeiSalesList.currencyCode)}</th>
                                                        <th ><fmt:message key="tatene" bundle="${label}"/></th>
                                                    </tr>
                                                    </c:forEach>
                                                </table>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <tr style="height:23px;" class="total_disp_field">
                                                        <td style="width:20px">
                                                            <a href="javascript:void(0)" id="uriGen1ListOpen" class="tree-text">－</a>
                                                            <input type="hidden" name="uriGen1ListOpenFlg" value="${fn:escapeXml(s004Bean.uriGen1ListOpenFlg)}" id="uriGen1ListOpenFlg"/>
                                                        </td>
                                                        <td style="width:100px" colspan="2">
                                                            <fmt:message key="uriageNet" bundle="${label}"/>
                                                        </td>
                                                        <td style="width:60px" align="center"><fmt:message key="now" bundle="${label}"/><fmt:message key="total" bundle="${label}"/></td>
                                                    </tr>
                                                    <c:forEach items="${s004Bean.salesCostList}" var="salesCostList" varStatus="s">
                                                    <tr style="height:23px;">
                                                        <td class="total_disp_field"></td>
                                                        <c:if test="${s.index==0}">
                                                            <th rowspan="${s004Bean.salesCostList.size()}"></th>
                                                        </c:if>
                                                       <th class="disp_character_hid" style="text-align:left" title="${fn:escapeXml(salesCostList.categoryName1)}">
                                                           <c:if test="${salesCostList.inputFlg != '1'}"><a href="javascript:void(0);" class="categoryLink" data-key="${fn:escapeXml(salesCostList.categoryCode)}" data-kbn="ki"></c:if>
                                                               ${fn:escapeXml(salesCostList.categoryName1)}
                                                           <c:if test="${salesCostList.inputFlg != '1'}"></a></c:if>
                                                       </th>
                                                       <th class="disp_character_hid" style="text-align:left" title="${fn:escapeXml(salesCostList.categoryName2)}">
                                                           <c:if test="${salesCostList.inputFlg != '1'}"><a href="javascript:void(0);" class="categoryLink" data-key="${fn:escapeXml(salesCostList.categoryCode)}" data-kbn="ki"></c:if>
                                                               ${fn:escapeXml(salesCostList.categoryName2)}
                                                           <c:if test="${salesCostList.inputFlg != '1'}"></a></c:if>
                                                       </th>
                                                    </tr>
                                                    </c:forEach>
                                                </table>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <tr style="height:23px;" class="total_disp_field">
                                                        <td style="width:120px" colspan="2"><fmt:message key="uriageNet" bundle="${label}"/></td>
                                                        <td style="width:60px" align="center"><fmt:message key="ruikei" bundle="${label}"/></td>
                                                    </tr>
                                                    <tr style="height:23px;">
                                                        <th rowspan="2" colspan="2"><fmt:message key="arari" bundle="${label}"/></th>
                                                        <th ><fmt:message key="now" bundle="${label}"/></th>
                                                    </tr>
                                                    <tr style="height:23px;">
                                                        <th ><fmt:message key="ruikei" bundle="${label}"/></th>
                                                    </tr>
                                                    <tr style="height:23px;">
                                                        <th rowspan="2" colspan="2"><fmt:message key="mrate" bundle="${label}"/></th>
                                                        <th ><fmt:message key="now" bundle="${label}"/></th>
                                                    </tr>
                                                    <tr style="height:23px;">
                                                        <th ><fmt:message key="ruikei" bundle="${label}"/></th>
                                                    </tr>
                                                </table>

                                                <c:if test="${detailHeader.lossControlFlag}">
                                                <!-- ロスコン情報(2018A追加) ※ロスコンd対象案件のみこの欄を表示 -->
                                                <div style="height:5px"></div>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid">
                                                    <colgroup>
                                                        <col style="width:20px">
                                                        <col style="width:40px">
                                                        <col style="width:120px">
                                                        <col style="width:110px">
                                                    </colgroup>
                                                    <tr style="height:23px;" class="total_disp_field">
                                                        <td colspan="2" rowspan="7" style="text-align:center"><fmt:message key="lossControl" bundle="${label}"/></td>
                                                        <td colspan="2" style="text-align:center"><fmt:message key="lossHosei" bundle="${label}"/></td>
                                                    </tr>
                                                    <tr style="height:23px;" class="total_disp_field">
                                                        <td colspan="2" style="text-align:center"><fmt:message key="lossUriageSp" bundle="${label}"/></td>
                                                    </tr>
                                                    <tr style="height:23px;" class="total_disp_field">
                                                        <td colspan="2" style="text-align:center"><fmt:message key="lossUriageNet" bundle="${label}"/></td>
                                                    </tr>
                                                     <tr style="height:23px;" class="total_disp_field">
                                                        <td colspan="2" style="text-align:center"><fmt:message key="arari" bundle="${label}"/></td>
                                                    </tr>
                                                     <tr style="height:23px;" class="total_disp_field">
                                                        <td colspan="2" style="text-align:center"><fmt:message key="mrate" bundle="${label}"/></td>
                                                    </tr>
                                                     <tr style="height:23px;" class="total_disp_field">
                                                        <td colspan="2" style="text-align:center"><fmt:message key="lossHikiate" bundle="${label}"/></td>
                                                    </tr>
                                                     <tr style="height:23px;" class="total_disp_field">
                                                        <td colspan="2" style="text-align:center"><fmt:message key="lossRuikeiHikiate" bundle="${label}"/></td>
                                                    </tr>
                                                </table>
                                                </c:if>
                                                
                                                <br>

                                                <!-- 回収情報 -->
                                                <!-- 回収情報タイトル(固定) -->
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data">
                                                    <colgroup>
                                                        <col style="width:32px">
                                                        <col style="width:45px">
                                                        <col style="width:37px">
                                                        <col style="width:35px">
                                                        <col style="width:33px">
                                                    </colgroup>
                                                    <tr style="height:23px">
                                                        <td colspan="5" style="border-bottom-style:none;" class="subTitle">
                                                            <fmt:message key="kaisyuManagement" bundle="${label}"/>&nbsp;
                                                            <span style="display:inline-block;" title="${fn:escapeXml(sUtil.getToriatsuName(detailHeader.ankenEntity.toriatsuNm))}&nbsp;${fn:escapeXml(detailHeader.ankenEntity.salesRouteNm)}">${fn:escapeXml(sUtil.getToriatsuName(detailHeader.ankenEntity.toriatsuNm))}&nbsp;${fn:escapeXml(detailHeader.ankenEntity.salesRouteNm)}</span>
                                                        </td>
                                                    </tr>
                                                    <tr style="height:23px">
                                                        <th ><fmt:message key="currency" bundle="${label}"/></th>
                                                        <th ><fmt:message key="taxRate" bundle="${label}"/></th>
                                                        <th ><fmt:message key="kinsyu" bundle="${label}"/></th>
                                                        <th ><fmt:message key="maeuke" bundle="${label}"/></th>
                                                        <th ></th>
                                                    </tr>
                                                </table>

                                                <!-- 回収情報 各データの情報(通貨・税率 etc) -->
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <colgroup>
                                                        <col style="width:32px">
                                                        <col style="width:45px">
                                                        <col style="width:37px">
                                                        <col style="width:35px">
                                                        <col style="width:33px">
                                                    </colgroup>
                                                    <tr style="height:23px;" class="total_disp_field"><%-- 2017/11/20 #072 MOD 回収Total行追加 --%>
                                                        <td colspan="5" class="total_disp_field" style="text-align: right;"><fmt:message key="total" bundle="${label}"/>(<fmt:message key="enka" bundle="${label}"/>)</td>
                                                    </tr>
                                                    <c:forEach items="${s004Bean.recoveryAmountList}" var="recoveryAmountList" varStatus="s">
                                                    <c:set value="${recoveryAmountList.currencyCode != recoveryAmountList.preCurrencyCode ? '1' : '0'}" var="dispCurrencyCodeKbn" />
                                                    <%--<c:set value="${(recoveryAmountList.currencyCode == s004Bean.currencyCodeEn ? recoveryAmountList.currencyCodeCount : recoveryAmountList.currencyCodeCount * 2) + (recoveryAmountList.zeiRate != '0' ? 1 : 0)}" var="currencyCodeRowSpanCount" />--%>
                                                    <c:set value="${recoveryAmountList.currencyCodeCount}" var="currencyCodeRowSpanCount" />
                                                    <c:set value="${recoveryAmountList.currencyCode == s004Bean.currencyCodeEn ? (recoveryAmountList.zeiRate != '0' ? '2' : '1') : (recoveryAmountList.zeiRate != '0' ? '3' : '2')}" var="rowSpanCount" />
                                                    <tr style="height:23px;" >
                                                        <c:if test="${dispCurrencyCodeKbn == '1'}">
                                                        <th rowspan="${currencyCodeRowSpanCount}">${fn:escapeXml(recoveryAmountList.currencyCode)}</th>
                                                        </c:if>
                                                        <th rowspan="${rowSpanCount}" >${fn:escapeXml(recoveryAmountList.zeiRnm)}</th>
                                                        <th rowspan="${rowSpanCount}" >${sUtil.getLabelKinsyuKbn(recoveryAmountList.kinsyuKbn)}</th>
                                                        <th rowspan="${rowSpanCount}" >${sUtil.getLabelKaisyuKbn(recoveryAmountList.kaisyuKbn)}</th>
                                                        <th ><c:if test="${recoveryAmountList.currencyCode != s004Bean.currencyCodeEn}"><fmt:message key="foreignUnitLabel" bundle="${label}"/></c:if><c:if test="${recoveryAmountList.currencyCode == s004Bean.currencyCodeEn}"><fmt:message key="hontai" bundle="${label}"/></c:if></th>
                                                    </tr>

                                                    <!-- 回収金額 円通貨以外での円貨入力欄のタイトル -->
                                                    <c:if test="${recoveryAmountList.currencyCode != s004Bean.currencyCodeEn}">
                                                    <tr style="height:23px;" >
                                                        <th ><fmt:message key="jpyUnitLabel" bundle="${label}"/></th>
                                                    </tr>
                                                    </c:if>

                                                    <!-- 回収金額 税額 --><%-- 2017/11/20 #072 MOD 回収Total行追加 --%>
                                                    <c:if test="${recoveryAmountList.zeiRate != '0'}">
                                                    <tr style="height:23px;" >
                                                        <th ><fmt:message key="zeigaku" bundle="${label}"/></th>
                                                    </tr>
                                                    </c:if>
                                                    </c:forEach>
                                                </table>
                                            </div>
                                        </td>
                                        <td align="left">
                                            <!--スクロール部分-->
                                            <div id="Head2" style="width:1024px;overflow-x:hidden;">
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <colgroup>
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                            <col style="width:125px">
                                                        </c:if>
                                                        <c:forEach items="${s004Bean.title}" var="title" varStatus="s">
                                                        <col style="width:105px">
                                                        </c:forEach>
                                                    </colgroup>
                                                    <tr>
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                            <th><fmt:message key="zenkiruikei" bundle="${label}"/></th>
                                                        </c:if>
                                                        <c:forEach items="${s004Bean.monthTitleJyuchu}" var="title" varStatus="s">
                                                            <c:choose>
                                                                <c:when test="${fn:endsWith(title, s004Bean.getKi()) || fn:endsWith(title, 'Q') || fn:contains(title, s004Bean.getTotal())}">
<%--                                                        <th colspan="2" class="${fn:endsWith(title, s004Bean.getKi()) || fn:endsWith(title, 'Q') ? 'ki-summary-col' : ''}">${fn:escapeXml(title)}</th> --%>
                                                        <th colspan="2" class="">${fn:escapeXml(title)}</th>

                                                                </c:when>
                                                                <c:otherwise>
                                                        <th>${fn:escapeXml(title)}</th>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:forEach>
                                                    </tr>
                                                    <tr>
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                            <th><fmt:message key="jisseki" bundle="${label}"/></th>
                                                        </c:if>
                                                        <c:forEach items="${s004Bean.titleJyuchu}" var="title" varStatus="s">
                                                        <%--<th class="${fn:escapeXml(title.class)}">--%>
                                                        <th class="">
                                                            ${fn:escapeXml(title.title)}
                                                            <c:if test="${title.bikou == '1'}"><div class="btn-bikou" onclick="return kiBikou(${fn:escapeXml(title.syuekiYm)},'<fmt:message key="forceSave" bundle="${label}"/>', '<fmt:message key="registSuccess" bundle="${label}"/>', '<fmt:message key="editError" bundle="${label}"/>')">…</div></c:if>
                                                        </th>
                                                        </c:forEach>
                                                    </tr>
                                                </table>
                                            </div>
                                            <div id="List2" style="height:258px;overflow-y:scroll;width:1042px;overflow-x:scroll;"  onscroll="jsScrollLeft(Head2, this);jsScrollTop(List1, this);jsScrollLeft(all_line, this);">
                                                <!-- zhangt Add(s) -->
                                                <!-- ↓↓↓　受注SP　↓↓↓ -->
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <c:set value="${s004Bean.totalJyuchuSp}" var="totalJyuchuSp"/>
                                                    <!--受注SP(合計行) Start-->
                                                    <tr style="height:23px;"  class="fix_tb total_disp_field" id="link_juchuSpListOpen">
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px">${totalJyuchuSp.JyuchuBRuikei}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px">${totalJyuchuSp.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px">${totalJyuchuSp.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px">${totalJyuchuSp.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu1Q}</td>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px">${totalJyuchuSp.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px">${totalJyuchuSp.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px">${totalJyuchuSp.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu2Q}</td>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <%-- 期計(S) --%>
                                                        <td style="width:105px">${totalJyuchuSp.JyuchuK1}</td>
                                                        <td style="width:105px">${totalJyuchuSp.JyuchuK1Diff}</td>
                                                        <%-- 期計(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px">${totalJyuchuSp.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px">${totalJyuchuSp.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px">${totalJyuchuSp.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu3Q}</td>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px">${totalJyuchuSp.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px">${totalJyuchuSp.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px">${totalJyuchuSp.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu12}</td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu4Q}</td>
                                                        <td style="width:105px">${totalJyuchuSp.Jyuchu4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <%-- 期計、最終見込、発番実績(S) --%>
                                                        <td style="width:105px">${totalJyuchuSp.JyuchuK2}</td>
                                                        <td style="width:105px">${totalJyuchuSp.JyuchuK2Diff}</td>
                                                        <td style="width:105px">${totalJyuchuSp.JyuchuF}</td>
                                                        <td style="width:105px">${totalJyuchuSp.JyuchuH}</td>
                                                        
                                                        <%-- 期計、最終見込、発番実績(E) --%>
                                                    </tr>
                                                    <!-- 受注SP(合計行) End-->
                                                    <!--受注レート(通貨詳細行) Start -->
                                                    <c:forEach items="${s004Bean.jyuchuRate}" var="jyuchuRate" varStatus="s">
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuRate.JyuchuBRuikei}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${jyuchuRate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[0] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpRate" class="numberFormat2" style="${numberStyle}" value="${jyuchuRate.Jyuchu1}" onchange="changeEditFlg(this);" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuRate.Jyuchu1}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${jyuchuRate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[1] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpRate" class="numberFormat2" style="${numberStyle}" value="${jyuchuRate.Jyuchu2}" onchange="changeEditFlg(this);" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuRate.Jyuchu2}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${jyuchuRate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[2] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpRate" class="numberFormat2" style="${numberStyle}" value="${jyuchuRate.Jyuchu3}" onchange="changeEditFlg(this);" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuRate.Jyuchu3}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuRate.Jyuchu1Q}</td>
                                                        <td class="ki-summary-col">${jyuchuRate.Jyuchu1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${jyuchuRate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[3] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpRate" class="numberFormat2" style="${numberStyle}" value="${jyuchuRate.Jyuchu4}" onchange="changeEditFlg(this);" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuRate.Jyuchu4}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${jyuchuRate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[4] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpRate" class="numberFormat2" style="${numberStyle}" value="${jyuchuRate.Jyuchu5}" onchange="changeEditFlg(this);" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuRate.Jyuchu5}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${jyuchuRate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[5] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpRate" class="numberFormat2" style="${numberStyle}" value="${jyuchuRate.Jyuchu6}" onchange="changeEditFlg(this);" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuRate.Jyuchu6}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuRate.Jyuchu2Q}</td>
                                                        <td class="ki-summary-col">${jyuchuRate.Jyuchu2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <%-- 期計(S) --%>
                                                        <td class="ki-summary-col">${jyuchuRate.JyuchuK1}</td>
                                                        <td class="ki-summary-col">${jyuchuRate.JyuchuK1Diff}</td>
                                                        <%-- 期計(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${jyuchuRate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[6] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpRate" class="numberFormat2" style="${numberStyle}" value="${jyuchuRate.Jyuchu7}" onchange="changeEditFlg(this);" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuRate.Jyuchu7}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${jyuchuRate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[7] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpRate" class="numberFormat2" style="${numberStyle}" value="${jyuchuRate.Jyuchu8}" onchange="changeEditFlg(this);" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuRate.Jyuchu8}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${jyuchuRate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[8] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpRate" class="numberFormat2" style="${numberStyle}" value="${jyuchuRate.Jyuchu9}" onchange="changeEditFlg(this);" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuRate.Jyuchu9}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuRate.Jyuchu3Q}</td>
                                                        <td class="ki-summary-col">${jyuchuRate.Jyuchu3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${jyuchuRate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[9] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpRate" class="numberFormat2" style="${numberStyle}" value="${jyuchuRate.Jyuchu10}" onchange="changeEditFlg(this);" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuRate.Jyuchu10}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${jyuchuRate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[10] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpRate" class="numberFormat2" style="${numberStyle}" value="${jyuchuRate.Jyuchu11}" onchange="changeEditFlg(this);" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuRate.Jyuchu11}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${jyuchuRate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[11] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpRate" class="numberFormat2" style="${numberStyle}" value="${jyuchuRate.Jyuchu12}" onchange="changeEditFlg(this);" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuRate.Jyuchu12}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuRate.Jyuchu4Q}</td>
                                                        <td class="ki-summary-col">${jyuchuRate.Jyuchu4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <%-- 期計、最終見込、発番実績(S) --%>
                                                        <td class="ki-summary-col">${jyuchuRate.JyuchuK2}</td>
                                                        <td class="ki-summary-col">${jyuchuRate.JyuchuK2Diff}</td>
                                                        <td class="ki-summary-col">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && editAllFlg == '1' }">
                                                                    <input type="text" name="jyuchuSpRate" class="numberFormat2" style="${numberStyle}" value="${jyuchuRate.JyuchuF}" onchange="changeEditFlg(this);" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuRate.JyuchuF}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td class="ki-summary-col">${jyuchuRate.JyuchuH}</td>
                                                        
                                                        <%-- 期計、最終見込、発番実績(E) --%>
                                                    </tr>
                                                    </c:forEach>
                                                    <!--受注レート(通貨詳細行) End-->
                                                    <!--受注金額(通貨詳細行) Start-->
                                                    <c:set var="jyuchuFormatClass" value="${s004Bean.jyuchuForeignFlg == '1' ? 'numberFormat2' : 'numberFormat'}"/><%-- numberFormat:円貨用 numberFormat2:外貨用 --%>							
                                                    <c:forEach items="${s004Bean.jyuchuKingaku}" var="jyuchuKingaku" varStatus="s">
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuKingaku.JyuchuBRuikei}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${jyuchuKingaku.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[0] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpKingaku" class="${jyuchuFormatClass}" style="${numberStyle}" value="${jyuchuKingaku.Jyuchu1}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuDataKbn" value="${empty s004Bean.classNameAry[0] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuCurrencyCode" value="${jyuchuKingaku.CurrencyCode}" />
                                                                    <input type="hidden" name="jyuchuSyuekiYm" value="${s004Bean.kikanFromAry[0]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuKingaku.Jyuchu1}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${jyuchuKingaku.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[1] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpKingaku" class="${jyuchuFormatClass}" style="${numberStyle}" value="${jyuchuKingaku.Jyuchu2}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuDataKbn" value="${empty s004Bean.classNameAry[1] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuCurrencyCode" value="${jyuchuKingaku.CurrencyCode}" />
                                                                    <input type="hidden" name="jyuchuSyuekiYm" value="${s004Bean.kikanFromAry[1]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuKingaku.Jyuchu2}
                                                                </c:otherwise>
                                                            </c:choose>                                                            
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${jyuchuKingaku.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">
                                                             <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[2] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpKingaku" class="${jyuchuFormatClass}" style="${numberStyle}" value="${jyuchuKingaku.Jyuchu3}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuDataKbn" value="${empty s004Bean.classNameAry[2] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuCurrencyCode" value="${jyuchuKingaku.CurrencyCode}" />
                                                                    <input type="hidden" name="jyuchuSyuekiYm" value="${s004Bean.kikanFromAry[2]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuKingaku.Jyuchu3}
                                                                </c:otherwise>
                                                            </c:choose> 
                                                        </td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuKingaku.Jyuchu1Q}</td>
                                                        <td class="ki-summary-col">${jyuchuKingaku.Jyuchu1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${jyuchuKingaku.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[3] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpKingaku" class="${jyuchuFormatClass}" style="${numberStyle}" value="${jyuchuKingaku.Jyuchu4}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuDataKbn" value="${empty s004Bean.classNameAry[3] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuCurrencyCode" value="${jyuchuKingaku.CurrencyCode}" />
                                                                    <input type="hidden" name="jyuchuSyuekiYm" value="${s004Bean.kikanFromAry[3]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuKingaku.Jyuchu4}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${jyuchuKingaku.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[4] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpKingaku" class="${jyuchuFormatClass}" style="${numberStyle}" value="${jyuchuKingaku.Jyuchu5}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuDataKbn" value="${empty s004Bean.classNameAry[4] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuCurrencyCode" value="${jyuchuKingaku.CurrencyCode}" />
                                                                    <input type="hidden" name="jyuchuSyuekiYm" value="${s004Bean.kikanFromAry[4]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuKingaku.Jyuchu5}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${jyuchuKingaku.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[5] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpKingaku" class="${jyuchuFormatClass}" style="${numberStyle}" value="${jyuchuKingaku.Jyuchu6}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuDataKbn" value="${empty s004Bean.classNameAry[5] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuCurrencyCode" value="${jyuchuKingaku.CurrencyCode}" />
                                                                    <input type="hidden" name="jyuchuSyuekiYm" value="${s004Bean.kikanFromAry[5]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuKingaku.Jyuchu6}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuKingaku.Jyuchu2Q}</td>
                                                        <td class="ki-summary-col">${jyuchuKingaku.Jyuchu2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <%-- 期計(S) --%>
                                                        <td class="ki-summary-col">${jyuchuKingaku.JyuchuK1}</td>
                                                        <td class="ki-summary-col">${jyuchuKingaku.JyuchuK1Diff}</td>
                                                        <%-- 期計(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${jyuchuKingaku.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[6] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpKingaku" class="${jyuchuFormatClass}" style="${numberStyle}" value="${jyuchuKingaku.Jyuchu7}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuDataKbn" value="${empty s004Bean.classNameAry[6] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuCurrencyCode" value="${jyuchuKingaku.CurrencyCode}" />
                                                                    <input type="hidden" name="jyuchuSyuekiYm" value="${s004Bean.kikanToAry[0]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuKingaku.Jyuchu7}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${jyuchuKingaku.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[7] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpKingaku" class="${jyuchuFormatClass}" style="${numberStyle}" value="${jyuchuKingaku.Jyuchu8}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuDataKbn" value="${empty s004Bean.classNameAry[7] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuCurrencyCode" value="${jyuchuKingaku.CurrencyCode}" />
                                                                    <input type="hidden" name="jyuchuSyuekiYm" value="${s004Bean.kikanToAry[1]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuKingaku.Jyuchu8}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${jyuchuKingaku.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[8] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpKingaku" class="${jyuchuFormatClass}" style="${numberStyle}" value="${jyuchuKingaku.Jyuchu9}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuDataKbn" value="${empty s004Bean.classNameAry[8] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuCurrencyCode" value="${jyuchuKingaku.CurrencyCode}" />
                                                                    <input type="hidden" name="jyuchuSyuekiYm" value="${s004Bean.kikanToAry[2]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuKingaku.Jyuchu9}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuKingaku.Jyuchu3Q}</td>
                                                        <td class="ki-summary-col">${jyuchuKingaku.Jyuchu3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${jyuchuKingaku.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[9] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpKingaku" class="${jyuchuFormatClass}" style="${numberStyle}" value="${jyuchuKingaku.Jyuchu10}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuDataKbn" value="${empty s004Bean.classNameAry[9] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuCurrencyCode" value="${jyuchuKingaku.CurrencyCode}" />
                                                                    <input type="hidden" name="jyuchuSyuekiYm" value="${s004Bean.kikanToAry[3]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuKingaku.Jyuchu10}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${jyuchuKingaku.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">
                                                             <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[10] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpKingaku" class="${jyuchuFormatClass}" style="${numberStyle}" value="${jyuchuKingaku.Jyuchu11}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuDataKbn" value="${empty s004Bean.classNameAry[10] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuCurrencyCode" value="${jyuchuKingaku.CurrencyCode}" />
                                                                    <input type="hidden" name="jyuchuSyuekiYm" value="${s004Bean.kikanToAry[4]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuKingaku.Jyuchu11}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${jyuchuKingaku.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[11] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuSpKingaku" class="${jyuchuFormatClass}" style="${numberStyle}" value="${jyuchuKingaku.Jyuchu12}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuDataKbn" value="${empty s004Bean.classNameAry[11] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuCurrencyCode" value="${jyuchuKingaku.CurrencyCode}" />
                                                                    <input type="hidden" name="jyuchuSyuekiYm" value="${s004Bean.kikanToAry[5]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuKingaku.Jyuchu12}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuKingaku.Jyuchu4Q}</td>
                                                        <td class="ki-summary-col">${jyuchuKingaku.Jyuchu4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <%-- 期計、最終見込、発番実績(S) --%>
                                                        <td class="ki-summary-col">${jyuchuKingaku.JyuchuK2}</td>
                                                        <td class="ki-summary-col">${jyuchuKingaku.JyuchuK2Diff}</td>
                                                        <td class="ki-summary-col">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && editAllFlg == '1' }">
                                                                    <input type="text" name="jyuchuSpKingaku" class="${jyuchuFormatClass}" style="${numberStyle}" value="${jyuchuKingaku.JyuchuF}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuDataKbn" value="F" />							
                                                                    <input type="hidden" name="jyuchuCurrencyCode" value="${jyuchuKingaku.CurrencyCode}" />
                                                                    <input type="hidden" name="jyuchuSyuekiYm" value="999900F" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuKingaku.JyuchuF}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td class="ki-summary-col">${jyuchuKingaku.JyuchuH}</td>
                                                        
                                                        <%-- 期計、最終見込、発番実績(E) --%>
                                                    </tr>
                                                    </c:forEach>
                                                    <!--受注金額(通貨詳細行) End -->
                                                </table>
                                                <!-- ↓↓↓　受注NET　↓↓↓ -->
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <c:set value="${s004Bean.totalJyuchuNet}" var="totalJyuchuNet"/>
                                                    <!-- 受注Net(合計行) Start -->
                                                    <tr style="height:23px;"  class="fix_tb total_disp_field" id="link_juchuNetListOpen">
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px">${totalJyuchuNet.JyuchuBRuikei}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px">${totalJyuchuNet.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px">${totalJyuchuNet.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px">${totalJyuchuNet.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu1Q}</td>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px">${totalJyuchuNet.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px">${totalJyuchuNet.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px">${totalJyuchuNet.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu2Q}</td>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <%-- 期計(S) --%>
                                                        <td style="width:105px">${totalJyuchuNet.JyuchuK1}</td>
                                                        <td style="width:105px">${totalJyuchuNet.JyuchuK1Diff}</td>
                                                        <%-- 期計(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px">${totalJyuchuNet.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px">${totalJyuchuNet.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px">${totalJyuchuNet.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu3Q}</td>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px">${totalJyuchuNet.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px">${totalJyuchuNet.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px">${totalJyuchuNet.JyuchuTm}</td></c:if>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu12}</td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu4Q}</td>
                                                        <td style="width:105px">${totalJyuchuNet.Jyuchu4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <%-- 期計、最終見込、発番実績(S) --%>
                                                        <td style="width:105px">${totalJyuchuNet.JyuchuK2}</td>
                                                        <td style="width:105px">${totalJyuchuNet.JyuchuK2Diff}</td>
                                                        <td style="width:105px">${totalJyuchuNet.JyuchuF}</td>
                                                        <td style="width:105px">${totalJyuchuNet.JyuchuH}</td>
                                                        
                                                        <%-- 期計、最終見込、発番実績(E) --%>
                                                    </tr>
                                                    <!-- 受注Net(合計行) End -->
                                                    <!-- 一括見込むNET行 Start -->
                                                    <c:set value="${s004Bean.jyuchuMikomuNet}" var="jyuchuMikomuNet"/>
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuMikomuNet.JyuchuBRuikei}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMikomuNet.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[0] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuNetKingaku" class="numberFormat" style="${numberStyle}" value="${jyuchuMikomuNet.Jyuchu1}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuNetDataKbn" value="${empty s004Bean.classNameAry[0] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuNetSyuekiYm" value="${s004Bean.kikanFromAry[0]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuMikomuNet.Jyuchu1}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMikomuNet.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[1] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuNetKingaku" class="numberFormat" style="${numberStyle}" value="${jyuchuMikomuNet.Jyuchu2}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuNetDataKbn" value="${empty s004Bean.classNameAry[1] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuNetSyuekiYm" value="${s004Bean.kikanFromAry[1]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuMikomuNet.Jyuchu2}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMikomuNet.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[2] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuNetKingaku" class="numberFormat" style="${numberStyle}" value="${jyuchuMikomuNet.Jyuchu3}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuNetDataKbn" value="${empty s004Bean.classNameAry[2] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuNetSyuekiYm" value="${s004Bean.kikanFromAry[2]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuMikomuNet.Jyuchu3}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuMikomuNet.Jyuchu1Q}</td>
                                                        <td class="ki-summary-col">${jyuchuMikomuNet.Jyuchu1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMikomuNet.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[3] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuNetKingaku" class="numberFormat" style="${numberStyle}" value="${jyuchuMikomuNet.Jyuchu4}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuNetDataKbn" value="${empty s004Bean.classNameAry[3] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuNetSyuekiYm" value="${s004Bean.kikanFromAry[3]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuMikomuNet.Jyuchu4}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMikomuNet.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[4] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuNetKingaku" class="numberFormat" style="${numberStyle}" value="${jyuchuMikomuNet.Jyuchu5}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuNetDataKbn" value="${empty s004Bean.classNameAry[4] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuNetSyuekiYm" value="${s004Bean.kikanFromAry[4]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuMikomuNet.Jyuchu5}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMikomuNet.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[5] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuNetKingaku" class="numberFormat" style="${numberStyle}" value="${jyuchuMikomuNet.Jyuchu6}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuNetDataKbn" value="${empty s004Bean.classNameAry[5] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuNetSyuekiYm" value="${s004Bean.kikanFromAry[5]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuMikomuNet.Jyuchu6}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuMikomuNet.Jyuchu2Q}</td>
                                                        <td class="ki-summary-col">${jyuchuMikomuNet.Jyuchu2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <%-- 期計(S) --%>
                                                        <td class="ki-summary-col">${jyuchuMikomuNet.JyuchuK1}</td>
                                                        <td class="ki-summary-col">${jyuchuMikomuNet.JyuchuK1Diff}</td>
                                                        <%-- 期計(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMikomuNet.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[6] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuNetKingaku" class="numberFormat" style="${numberStyle}" value="${jyuchuMikomuNet.Jyuchu7}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuNetDataKbn" value="${empty s004Bean.classNameAry[6] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuNetSyuekiYm" value="${s004Bean.kikanToAry[0]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuMikomuNet.Jyuchu7}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMikomuNet.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[7] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuNetKingaku" class="numberFormat" style="${numberStyle}" value="${jyuchuMikomuNet.Jyuchu8}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuNetDataKbn" value="${empty s004Bean.classNameAry[7] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuNetSyuekiYm" value="${s004Bean.kikanToAry[1]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuMikomuNet.Jyuchu8}
                                                                </c:otherwise>
                                                            </c:choose>                                                            
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMikomuNet.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[8] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuNetKingaku" class="numberFormat" style="${numberStyle}" value="${jyuchuMikomuNet.Jyuchu9}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuNetDataKbn" value="${empty s004Bean.classNameAry[8] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuNetSyuekiYm" value="${s004Bean.kikanToAry[2]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuMikomuNet.Jyuchu9}
                                                                </c:otherwise>
                                                            </c:choose>  
                                                        </td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuMikomuNet.Jyuchu3Q}</td>
                                                        <td class="ki-summary-col">${jyuchuMikomuNet.Jyuchu3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMikomuNet.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[9] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuNetKingaku" class="numberFormat" style="${numberStyle}" value="${jyuchuMikomuNet.Jyuchu10}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuNetDataKbn" value="${empty s004Bean.classNameAry[9] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuNetSyuekiYm" value="${s004Bean.kikanToAry[3]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuMikomuNet.Jyuchu10}
                                                                </c:otherwise>
                                                            </c:choose> 
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMikomuNet.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[10] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuNetKingaku" class="numberFormat" style="${numberStyle}" value="${jyuchuMikomuNet.Jyuchu11}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuNetDataKbn" value="${empty s004Bean.classNameAry[10] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuNetSyuekiYm" value="${s004Bean.kikanToAry[4]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuMikomuNet.Jyuchu11}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMikomuNet.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[11] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="jyuchuNetKingaku" class="numberFormat" style="${numberStyle}" value="${jyuchuMikomuNet.Jyuchu12}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuNetDataKbn" value="${empty s004Bean.classNameAry[11] ? 'M' : 'J'}" />							
                                                                    <input type="hidden" name="jyuchuNetSyuekiYm" value="${s004Bean.kikanToAry[5]}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuMikomuNet.Jyuchu12}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuMikomuNet.Jyuchu4Q}</td>
                                                        <td class="ki-summary-col">${jyuchuMikomuNet.Jyuchu4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <%-- 期計、最終見込、発番実績(S) --%>
                                                        <td class="ki-summary-col">${jyuchuMikomuNet.JyuchuK2}</td>
                                                        <td class="ki-summary-col">${jyuchuMikomuNet.JyuchuK2Diff}</td>
                                                        <td class="ki-summary-col">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && editAllFlg == '1'}">
                                                                    <input type="text" name="jyuchuNetKingaku" class="numberFormat" style="${numberStyle}" value="${jyuchuMikomuNet.JyuchuF}" onchange="changeEditFlg(this);" />
                                                                    <input type="hidden" name="jyuchuNetDataKbn" value="F" />							
                                                                    <input type="hidden" name="jyuchuNetSyuekiYm" value="999900F" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${jyuchuMikomuNet.JyuchuF}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td class="ki-summary-col">${jyuchuMikomuNet.JyuchuH}</td>
                                                        
                                                        <%-- 期計、最終見込、発番実績(E) --%>
                                                    </tr>
                                                    <!-- 一括見込むNET行 End -->
                                                    <!-- 受注情報の粗利行 Start-->
                                                    <c:set value="${s004Bean.jyuchuArari}" var="jyuchuArari"/>
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuArari.JyuchuBRuikei}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${jyuchuArari.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">${jyuchuArari.Jyuchu1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${jyuchuArari.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">${jyuchuArari.Jyuchu2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${jyuchuArari.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">${jyuchuArari.Jyuchu3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuArari.Jyuchu1Q}</td>
                                                        <td class="ki-summary-col">${jyuchuArari.Jyuchu1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${jyuchuArari.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">${jyuchuArari.Jyuchu4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${jyuchuArari.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">${jyuchuArari.Jyuchu5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${jyuchuArari.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">${jyuchuArari.Jyuchu6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuArari.Jyuchu2Q}</td>
                                                        <td class="ki-summary-col">${jyuchuArari.Jyuchu2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <%-- 期計(S) --%>
                                                        <td class="ki-summary-col">${jyuchuArari.JyuchuK1}</td>
                                                        <td class="ki-summary-col">${jyuchuArari.JyuchuK1Diff}</td>
                                                        <%-- 期計(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${jyuchuArari.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">${jyuchuArari.Jyuchu7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${jyuchuArari.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">${jyuchuArari.Jyuchu8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${jyuchuArari.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">${jyuchuArari.Jyuchu9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuArari.Jyuchu3Q}</td>
                                                        <td class="ki-summary-col">${jyuchuArari.Jyuchu3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${jyuchuArari.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">${jyuchuArari.Jyuchu10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${jyuchuArari.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">${jyuchuArari.Jyuchu11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${jyuchuArari.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">${jyuchuArari.Jyuchu12}</td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuArari.Jyuchu4Q}</td>
                                                        <td class="ki-summary-col">${jyuchuArari.Jyuchu4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <%-- 期計、最終見込、発番実績(S) --%>
                                                        <td class="ki-summary-col">${jyuchuArari.JyuchuK2}</td>
                                                        <td class="ki-summary-col">${jyuchuArari.JyuchuK2Diff}</td>
                                                        <td class="ki-summary-col">${jyuchuArari.JyuchuF}</td>
                                                        <td class="ki-summary-col">${jyuchuArari.JyuchuH}</td>
                                                        
                                                        <%-- 期計、最終見込、発番実績(E) --%>
                                                    </tr>
                                                    <!-- 受注情報の粗利行 End-->
                                                    <!-- 受注情報のM率行 Start-->
                                                    <c:set value="${s004Bean.jyuchuMrate}" var="jyuchuMrate"/>
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${jyuchuMrate.JyuchuBRuikei}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMrate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">${jyuchuMrate.Jyuchu1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMrate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">${jyuchuMrate.Jyuchu2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMrate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">${jyuchuMrate.Jyuchu3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuMrate.Jyuchu1Q}</td>
                                                        <td class="ki-summary-col">${jyuchuMrate.Jyuchu1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMrate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">${jyuchuMrate.Jyuchu4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMrate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">${jyuchuMrate.Jyuchu5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMrate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">${jyuchuMrate.Jyuchu6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuMrate.Jyuchu2Q}</td>
                                                        <td class="ki-summary-col">${jyuchuMrate.Jyuchu2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <%-- 期計(S) --%>
                                                        <td class="ki-summary-col">${jyuchuMrate.JyuchuK1}</td>
                                                        <td class="ki-summary-col">${jyuchuMrate.JyuchuK1Diff}</td>
                                                        <%-- 期計(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMrate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">${jyuchuMrate.Jyuchu7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMrate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">${jyuchuMrate.Jyuchu8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMrate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">${jyuchuMrate.Jyuchu9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuMrate.Jyuchu3Q}</td>
                                                        <td class="ki-summary-col">${jyuchuMrate.Jyuchu3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMrate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">${jyuchuMrate.Jyuchu10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMrate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">${jyuchuMrate.Jyuchu11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${jyuchuMrate.JyuchuTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">${jyuchuMrate.Jyuchu12}</td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${jyuchuMrate.Jyuchu4Q}</td>
                                                        <td class="ki-summary-col">${jyuchuMrate.Jyuchu4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <%-- 期計、最終見込、発番実績(S) --%>
                                                        <td class="ki-summary-col">${jyuchuMrate.JyuchuK2}</td>
                                                        <td class="ki-summary-col">${jyuchuMrate.JyuchuK2Diff}</td>
                                                        <td class="ki-summary-col">${jyuchuMrate.JyuchuF}</td>
                                                        <td class="ki-summary-col">${jyuchuMrate.JyuchuH}</td>
                                                        
                                                        <%-- 期計、最終見込、発番実績(E) --%>
                                                    </tr>
                                                    <!-- 受注情報のM率行 End-->                                                    
                                                </table>
                                                <br>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <colgroup>
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                            <col style="width:125px">
                                                        </c:if>
                                                        <c:forEach items="${s004Bean.title}" var="title" varStatus="s">
                                                        <col style="width:105px">
                                                        </c:forEach>
                                                    </colgroup>
                                                    <tr style="height:23px">
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                            <th><fmt:message key="zenkiruikei" bundle="${label}"/></th>
                                                        </c:if>
                                                        <c:forEach items="${s004Bean.monthTitle}" var="title" varStatus="s">
                                                            <c:choose>
                                                                <c:when test="${fn:endsWith(title, s004Bean.getKi()) || fn:endsWith(title, 'Q') || fn:contains(title, s004Bean.getTotal()) || fn:contains(title, s004Bean.getLastMikomi()) }">
                                                        <th colspan="2" class="">${fn:escapeXml(title)}</th>
                                                                </c:when>
                                                                <c:otherwise>
                                                        <th>${fn:escapeXml(title)}</th>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:forEach>
                                                    </tr>
                                                    <tr style="height:23px">
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}"><th><fmt:message key="jisseki" bundle="${label}"/></th></c:if>
                                                        <c:forEach items="${s004Bean.title}" var="title" varStatus="s">
                                                        <th class="">${fn:escapeXml(title.title)}</th>
                                                        </c:forEach>
                                                    </tr>
                                                </table>
                                                <!-- zhangt Add(e) -->
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <!--契約金額(total行)-->
                                                    <tr style="height:23px;"  class="fix_tb total_disp_field" id="link_spListOpen">
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px">${s004Bean.totalContractAmount.KeiyakuAmountZenkiRuikei}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount1Q}</td>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount2Q}</td>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <%-- 期計(S) --%>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountK1}</td>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountK1Diff}</td>
                                                        <%-- 期計(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount3Q}</td>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount12}</td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount4Q}</td>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmount4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <%-- 期計、合計(S) --%>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountK2}</td>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountK2Diff}</td>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountG}</td>
                                                        <td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountGDiff}</td>
                                                        <%--<td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountF}</td>--%>
                                                        <%--<td style="width:105px">${s004Bean.totalContractAmount.KeiyakuAmountDiff}</td>--%>
                                                        <%-- 期計、合計(E) --%>
                                                        <input type="hidden" name="tmIndex" value="${s004Bean.tmIndex}" />
                                                    </tr>

                                                    <% int i = 0;%>
                                                    <!--契約金額(通貨詳細行)-->
                                                    <c:forEach items="${s004Bean.contractAmount}" var="contractAmount" varStatus="s">
                                                    <c:set var="keiyakuFormatFlg" value="${s004Bean.getForeignFlg()}"/><%-- 0:円貨表記(小数点以下無し) 1:小数点2桁(外貨通貨が混じっている場合)--%>
                                                    <c:set var="numberFormatClass" value="${s004Bean.getForeignFlg() == '1' ? 'numberFormat2' : 'numberFormat'}"/><%-- numberFormat:円貨用 numberFormat2:外貨用 --%>
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <c:if test="${contractAmount.keiyakuRate == 'hosei'}">
                                                            <input type="hidden" name="inpTargetKeiyakuCurrencyCode" value="${contractAmount.currencyCode}" />
                                                            <input type="hidden" name="keiyakuAmountTm" value="${contractAmount.KeiyakuAmountTm}" />
                                                        </c:if>
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${contractAmount.KeiyakuAmountZenkiRuikei}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${contractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && contractAmount.keiyakuRate == 'hosei' && s004Bean.classNameAry[0] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[0], uriageYm) == 0}">
                                                                    <c:set var="keiyakuAmount1" value="${sUtil.exeChangeFormatUnit(utl.changeBigDecimal(contractAmount.KeiyakuAmount1), keiyakuFormatFlg)}"/>
                                                                    <input type="text" name="keiyakuAmount1" class="${numberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(keiyakuAmount1), keiyakuFormatFlg)}" onblur="inputEdit('inpTargetKeiyakuAmountUpdateFlg1', <%= i %>, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${contractAmount.KeiyakuAmount1}
                                                                    <c:if test="${contractAmount.keiyakuRate == 'hosei'}"><input type="hidden" name="keiyakuAmount1" value="${contractAmount.KeiyakuAmount1}" /></c:if>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKeiyakuAmountUpdateFlg1" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${contractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && contractAmount.keiyakuRate == 'hosei' && s004Bean.classNameAry[1] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[1], uriageYm) == 0}">
                                                                    <c:set var="keiyakuAmount2" value="${sUtil.exeChangeFormatUnit(utl.changeBigDecimal(contractAmount.KeiyakuAmount2), keiyakuFormatFlg)}"/>
                                                                    <input type="text" name="keiyakuAmount2" class="${numberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(keiyakuAmount2), keiyakuFormatFlg)}" onblur="inputEdit('inpTargetKeiyakuAmountUpdateFlg2', <%= i %>, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${contractAmount.KeiyakuAmount2}
                                                                    <c:if test="${contractAmount.keiyakuRate == 'hosei'}"><input type="hidden" name="keiyakuAmount2" value="${contractAmount.KeiyakuAmount2}" /></c:if>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKeiyakuAmountUpdateFlg2" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${contractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && contractAmount.keiyakuRate == 'hosei' && s004Bean.classNameAry[2 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[2], uriageYm) == 0}">
                                                                    <c:set var="keiyakuAmount3" value="${sUtil.exeChangeFormatUnit(utl.changeBigDecimal(contractAmount.KeiyakuAmount3), keiyakuFormatFlg)}"/>
                                                                    <input type="text" name="keiyakuAmount3" class="${numberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(keiyakuAmount3), keiyakuFormatFlg)}" onblur="inputEdit('inpTargetKeiyakuAmountUpdateFlg3', <%= i %>, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${contractAmount.KeiyakuAmount3}
                                                                    <c:if test="${contractAmount.keiyakuRate == 'hosei'}"><input type="hidden" name="keiyakuAmount3" value="${contractAmount.KeiyakuAmount3}" /></c:if>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKeiyakuAmountUpdateFlg3" />
                                                        </td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${contractAmount.KeiyakuAmount1Q}</td>
                                                        <td class="ki-summary-col">${contractAmount.KeiyakuAmount1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${contractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && contractAmount.keiyakuRate == 'hosei' && s004Bean.classNameAry[3 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[3], uriageYm) == 0}">
                                                                    <c:set var="keiyakuAmount4" value="${sUtil.exeChangeFormatUnit(utl.changeBigDecimal(contractAmount.KeiyakuAmount4), keiyakuFormatFlg)}"/>
                                                                    <input type="text" name="keiyakuAmount4" class="${numberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(keiyakuAmount4), keiyakuFormatFlg)}" onblur="inputEdit('inpTargetKeiyakuAmountUpdateFlg4', <%= i %>, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${contractAmount.KeiyakuAmount4}
                                                                    <c:if test="${contractAmount.keiyakuRate == 'hosei'}"><input type="hidden" name="keiyakuAmount4" value="${contractAmount.KeiyakuAmount4}" /></c:if>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKeiyakuAmountUpdateFlg4" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${contractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && contractAmount.keiyakuRate == 'hosei' && s004Bean.classNameAry[4 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[4], uriageYm) == 0}">
                                                                    <c:set var="keiyakuAmount5" value="${sUtil.exeChangeFormatUnit(utl.changeBigDecimal(contractAmount.KeiyakuAmount5), keiyakuFormatFlg)}"/>
                                                                    <input type="text" name="keiyakuAmount5" class="${numberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(keiyakuAmount5), keiyakuFormatFlg)}" onblur="inputEdit('inpTargetKeiyakuAmountUpdateFlg5', <%= i %>, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${contractAmount.KeiyakuAmount5}
                                                                    <c:if test="${contractAmount.keiyakuRate == 'hosei'}"><input type="hidden" name="keiyakuAmount5" value="${contractAmount.KeiyakuAmount5}" /></c:if>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKeiyakuAmountUpdateFlg5" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${contractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && contractAmount.keiyakuRate == 'hosei' && s004Bean.classNameAry[5 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[5], uriageYm) == 0}">
                                                                    <c:set var="keiyakuAmount6" value="${sUtil.exeChangeFormatUnit(utl.changeBigDecimal(contractAmount.KeiyakuAmount6), keiyakuFormatFlg)}"/>
                                                                    <input type="text" name="keiyakuAmount6" class="${numberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(keiyakuAmount6), keiyakuFormatFlg)}" onblur="inputEdit('inpTargetKeiyakuAmountUpdateFlg6', <%= i %>, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${contractAmount.KeiyakuAmount6}
                                                                    <c:if test="${contractAmount.keiyakuRate == 'hosei'}"><input type="hidden" name="keiyakuAmount6" value="${contractAmount.KeiyakuAmount6}" /></c:if>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKeiyakuAmountUpdateFlg6" />
                                                        </td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${contractAmount.KeiyakuAmount2Q}</td>
                                                        <td class="ki-summary-col">${contractAmount.KeiyakuAmount2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <%-- 期計(S) --%>
                                                        <td class="ki-summary-col">${contractAmount.KeiyakuAmountK1}</td>
                                                        <td class="ki-summary-col">${contractAmount.KeiyakuAmountK1Diff}</td>
                                                        <%-- 期計(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${contractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && contractAmount.keiyakuRate == 'hosei' && s004Bean.classNameAry[6 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[0], uriageYm) == 0}">
                                                                    <c:set var="keiyakuAmount7" value="${sUtil.exeChangeFormatUnit(utl.changeBigDecimal(contractAmount.KeiyakuAmount7), keiyakuFormatFlg)}"/>
                                                                    <input type="text" name="keiyakuAmount7" class="${numberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(keiyakuAmount7), keiyakuFormatFlg)}" onblur="inputEdit('inpTargetKeiyakuAmountUpdateFlg7', <%= i %>, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${contractAmount.KeiyakuAmount7}
                                                                    <c:if test="${contractAmount.keiyakuRate == 'hosei'}"><input type="hidden" name="keiyakuAmount7" value="${contractAmount.KeiyakuAmount7}" /></c:if>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKeiyakuAmountUpdateFlg7" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${contractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && contractAmount.keiyakuRate == 'hosei' && s004Bean.classNameAry[7 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[1], uriageYm) == 0}">
                                                                    <c:set var="keiyakuAmount8" value="${sUtil.exeChangeFormatUnit(utl.changeBigDecimal(contractAmount.KeiyakuAmount8), keiyakuFormatFlg)}"/>
                                                                    <input type="text" name="keiyakuAmount8" class="${numberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(keiyakuAmount8), keiyakuFormatFlg)}" onblur="inputEdit('inpTargetKeiyakuAmountUpdateFlg8', <%= i %>, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${contractAmount.KeiyakuAmount8}
                                                                    <c:if test="${contractAmount.keiyakuRate == 'hosei'}"><input type="hidden" name="keiyakuAmount8" value="${contractAmount.KeiyakuAmount8}" /></c:if>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKeiyakuAmountUpdateFlg8" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${contractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && contractAmount.keiyakuRate == 'hosei' && s004Bean.classNameAry[8 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[2], uriageYm) == 0}">
                                                                    <c:set var="keiyakuAmount9" value="${sUtil.exeChangeFormatUnit(utl.changeBigDecimal(contractAmount.KeiyakuAmount9), keiyakuFormatFlg)}"/>
                                                                    <input type="text" name="keiyakuAmount9" class="${numberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(keiyakuAmount9), keiyakuFormatFlg)}" onblur="inputEdit('inpTargetKeiyakuAmountUpdateFlg9', <%= i %>, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${contractAmount.KeiyakuAmount9}
                                                                    <c:if test="${contractAmount.keiyakuRate == 'hosei'}"><input type="hidden" name="keiyakuAmount9" value="${contractAmount.KeiyakuAmount9}" /></c:if>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKeiyakuAmountUpdateFlg9" />
                                                        </td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${contractAmount.KeiyakuAmount3Q}</td>
                                                        <td class="ki-summary-col">${contractAmount.KeiyakuAmount3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${contractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && contractAmount.keiyakuRate == 'hosei' && s004Bean.classNameAry[9] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[3], uriageYm) == 0}">
                                                                    <c:set var="keiyakuAmount10" value="${sUtil.exeChangeFormatUnit(utl.changeBigDecimal(contractAmount.KeiyakuAmount10), keiyakuFormatFlg)}"/>
                                                                    <input type="text" name="keiyakuAmount10" class="${numberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(keiyakuAmount10), keiyakuFormatFlg)}" onblur="inputEdit('inpTargetKeiyakuAmountUpdateFlg10', <%= i %>, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${contractAmount.KeiyakuAmount10}
                                                                    <c:if test="${contractAmount.keiyakuRate == 'hosei'}"><input type="hidden" name="keiyakuAmount10" value="${contractAmount.KeiyakuAmount10}" /></c:if>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKeiyakuAmountUpdateFlg10" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${contractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && contractAmount.keiyakuRate == 'hosei' && s004Bean.classNameAry[10] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[4], uriageYm) == 0}">
                                                                    <c:set var="keiyakuAmount11" value="${sUtil.exeChangeFormatUnit(utl.changeBigDecimal(contractAmount.KeiyakuAmount11), keiyakuFormatFlg)}"/>
                                                                    <input type="text" name="keiyakuAmount11" class="${numberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(keiyakuAmount11), keiyakuFormatFlg)}" onblur="inputEdit('inpTargetKeiyakuAmountUpdateFlg11', <%= i %>, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${contractAmount.KeiyakuAmount11}
                                                                    <c:if test="${contractAmount.keiyakuRate == 'hosei'}"><input type="hidden" name="keiyakuAmount11" value="${contractAmount.KeiyakuAmount11}" /></c:if>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKeiyakuAmountUpdateFlg11" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${contractAmount.KeiyakuAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && contractAmount.keiyakuRate == 'hosei' && s004Bean.classNameAry[11] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[5], uriageYm) == 0}">
                                                                    <c:set var="keiyakuAmount12" value="${sUtil.exeChangeFormatUnit(utl.changeBigDecimal(contractAmount.KeiyakuAmount12), keiyakuFormatFlg)}"/>
                                                                    <input type="text" name="keiyakuAmount12" class="${numberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(keiyakuAmount12), keiyakuFormatFlg)}" onblur="inputEdit('inpTargetKeiyakuAmountUpdateFlg12', <%= i %>, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${contractAmount.KeiyakuAmount12}
                                                                    <c:if test="${contractAmount.keiyakuRate == 'hosei'}"><input type="hidden" name="keiyakuAmount12" value="${contractAmount.KeiyakuAmount12}" /></c:if>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKeiyakuAmountUpdateFlg12" />
                                                        </td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td class="kikan_shinko_kanjyo_diff">${contractAmount.KeiyakuAmountTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${contractAmount.KeiyakuAmount4Q}</td>
                                                        <td class="ki-summary-col">${contractAmount.KeiyakuAmount4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col">${contractAmount.KeiyakuAmountK2}</td>
                                                        <td class="ki-summary-col">${contractAmount.KeiyakuAmountK2Diff}</td>
                                                        <td class="ki-summary-col">${contractAmount.KeiyakuAmountG}</td>
                                                        <td class="ki-summary-col">${contractAmount.KeiyakuAmountGDiff}</td>
                                                        <%--<td class="kikan_total_disp_field">${contractAmount.KeiyakuAmountF}</td>--%>
                                                        <%--<td class="kikan_total_disp_field">${contractAmount.KeiyakuAmountDiff}</td>--%>
                                                    </tr>
                                                    <c:if test="${contractAmount.keiyakuRate == 'hosei'}"><% i++; %></c:if>
                                                    </c:forEach>
                                                </table>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <!--見積総原価(合計行)-->
                                                    <tr style="height:23px;"  class="fix_tb total_disp_field" id="link_netListOpen">
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px">${s004Bean.totalCost.KeiyakuAmountZenkiRuikei}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px">${s004Bean.totalCost.NetTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalCost.Net1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px">${s004Bean.totalCost.NetTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalCost.Net2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px">${s004Bean.totalCost.NetTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalCost.Net3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalCost.Net1Q}</td>
                                                        <td style="width:105px">${s004Bean.totalCost.Net1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px">${s004Bean.totalCost.NetTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalCost.Net4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px">${s004Bean.totalCost.NetTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalCost.Net5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px">${s004Bean.totalCost.NetTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalCost.Net6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalCost.Net2Q}</td>
                                                        <td style="width:105px">${s004Bean.totalCost.Net2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td style="width:105px">${s004Bean.totalCost.NetK1}</td>
                                                        <td style="width:105px">${s004Bean.totalCost.NetK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px">${s004Bean.totalCost.NetTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalCost.Net7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px">${s004Bean.totalCost.NetTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalCost.Net8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px">${s004Bean.totalCost.NetTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalCost.Net9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalCost.Net3Q}</td>
                                                        <td style="width:105px">${s004Bean.totalCost.Net3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px">${s004Bean.totalCost.NetTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalCost.Net10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px">${s004Bean.totalCost.NetTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalCost.Net11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px">${s004Bean.totalCost.NetTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalCost.Net12}</td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td style="width:105px">${s004Bean.totalCost.NetTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalCost.Net4Q}</td>
                                                        <td style="width:105px">${s004Bean.totalCost.Net4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>
                                                        <td style="width:105px">${s004Bean.totalCost.NetK2}</td>
                                                        <td style="width:105px">${s004Bean.totalCost.NetK2Diff}</td>
                                                        <td style="width:105px">${s004Bean.totalCost.NetG}</td>
                                                        <td style="width:105px">${s004Bean.totalCost.NetGDiff}</td>
                                                        <%--<td style="width:105px">${s004Bean.totalCost.NetF}</td>--%>
                                                        <%--<td style="width:105px">${s004Bean.totalCost.NetDiff}</td>--%>
                                                    </tr>
                                                    <!--見積総原価・発番NET-->
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${s004Bean.costList.KeiyakuAmountZenkiRuikeiHatNetK2}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.HatNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">
                                                            <input type="hidden" name="hatNetTm" value="${s004Bean.costList.HatNetTm}" />
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[0 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="hatNet1" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.HatNet1}" onblur="inputEdit('inpTargetHatNetUpdateFlg1', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.HatNet1}<input type="hidden" name="hatNet1" value="${s004Bean.costList.HatNet1}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetHatNetUpdateFlg1" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.HatNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[1 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="hatNet2" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.HatNet2}" onblur="inputEdit('inpTargetHatNetUpdateFlg2', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.HatNet2}<input type="hidden" name="hatNet2" value="${s004Bean.costList.HatNet2}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetHatNetUpdateFlg2" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.HatNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[2 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="hatNet3" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.HatNet3}" onblur="inputEdit('inpTargetHatNetUpdateFlg3', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.HatNet3}<input type="hidden" name="hatNet3" value="${s004Bean.costList.HatNet3}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetHatNetUpdateFlg3" />
                                                        </td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.HatNet1Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.HatNet1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.HatNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[3 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="hatNet4" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.HatNet4}" onblur="inputEdit('inpTargetHatNetUpdateFlg4', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.HatNet4}<input type="hidden" name="hatNet4" value="${s004Bean.costList.HatNet4}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetHatNetUpdateFlg4" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.HatNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[4 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="hatNet5" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.HatNet5}" onblur="inputEdit('inpTargetHatNetUpdateFlg5', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.HatNet5}<input type="hidden" name="hatNet5" value="${s004Bean.costList.HatNet5}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetHatNetUpdateFlg5" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.HatNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[5 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="hatNet6" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.HatNet6}" onblur="inputEdit('inpTargetHatNetUpdateFlg6', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.HatNet6}<input type="hidden" name="hatNet6" value="${s004Bean.costList.HatNet6}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetHatNetUpdateFlg6" />
                                                        </td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.HatNet2Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.HatNet2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>
                                                        <td class="ki-summary-col">${s004Bean.costList.HatNetK1}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.HatNetK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.HatNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[6 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="hatNet7" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.HatNet7}" onblur="inputEdit('inpTargetHatNetUpdateFlg7', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.HatNet7}<input type="hidden" name="hatNet7" value="${s004Bean.costList.HatNet7}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetHatNetUpdateFlg7" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.HatNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[7] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="hatNet8" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.HatNet8}" onblur="inputEdit('inpTargetHatNetUpdateFlg8', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.HatNet8}<input type="hidden" name="hatNet8" value="${s004Bean.costList.HatNet8}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetHatNetUpdateFlg8" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.HatNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[8 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="hatNet9" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.HatNet9}" onblur="inputEdit('inpTargetHatNetUpdateFlg9', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.HatNet9}<input type="hidden" name="hatNet9" value="${s004Bean.costList.HatNet9}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetHatNetUpdateFlg9" />
                                                        </td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.HatNet3Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.HatNet3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.HatNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[9 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="hatNet10" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.HatNet10}" onblur="inputEdit('inpTargetHatNetUpdateFlg10', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.HatNet10}<input type="hidden" name="hatNet10" value="${s004Bean.costList.HatNet10}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetHatNetUpdateFlg10" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.HatNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[10 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="hatNet11" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.HatNet11}" onblur="inputEdit('inpTargetHatNetUpdateFlg11', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.HatNet11}<input type="hidden" name="hatNet11" value="${s004Bean.costList.HatNet11}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetHatNetUpdateFlg11" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.HatNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[11 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="hatNet12" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.HatNet12}" onblur="inputEdit('inpTargetHatNetUpdateFlg12', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.HatNet12}<input type="hidden" name="hatNet12" value="${s004Bean.costList.HatNet12}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetHatNetUpdateFlg12" />
                                                        </td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.HatNetTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.HatNet4Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.HatNet4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col">${s004Bean.costList.HatNetK2}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.HatNetK2Diff}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.HatNetG}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.HatNetGDiff}</td>
                                                        <%--<td class="kikan_total_disp_field">${s004Bean.costList.HatNetF}</td>--%>
                                                        <%--<td class="kikan_total_disp_field">${s004Bean.costList.HatNetDiff}</td>--%>
                                                    </tr>
                                                    <!--見積総原価・未発番NET-->
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${s004Bean.costList.KeiyakuAmountZenkiRuikeiMiNetK2}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.MiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">
                                                            <input type="hidden" name="miNetTm" value="${s004Bean.costList.MiNetTm}" />
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[0 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="miNet1" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.MiNet1}" onblur="inputEdit('inpTargetMiNetUpdateFlg1', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.MiNet1}<input type="hidden" name="miNet1" value="${s004Bean.costList.MiNet1}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetMiNetUpdateFlg1" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.MiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[1 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="miNet2" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.MiNet2}" onblur="inputEdit('inpTargetMiNetUpdateFlg2', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.MiNet2}<input type="hidden" name="miNet2" value="${s004Bean.costList.MiNet2}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetMiNetUpdateFlg2" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.MiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[2 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="miNet3" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.MiNet3}" onblur="inputEdit('inpTargetMiNetUpdateFlg3', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.MiNet3}<input type="hidden" name="miNet3" value="${s004Bean.costList.MiNet3}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetMiNetUpdateFlg3" />
                                                        </td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.MiNet1Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.MiNet1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.MiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[3 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="miNet4" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.MiNet4}" onblur="inputEdit('inpTargetMiNetUpdateFlg4', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.MiNet4}<input type="hidden" name="miNet4" value="${s004Bean.costList.MiNet4}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetMiNetUpdateFlg4" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.MiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[4 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="miNet5" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.MiNet5}" onblur="inputEdit('inpTargetMiNetUpdateFlg5', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.MiNet5}<input type="hidden" name="miNet5" value="${s004Bean.costList.MiNet5}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetMiNetUpdateFlg5" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.MiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[5 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="miNet6" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.MiNet6}" onblur="inputEdit('inpTargetMiNetUpdateFlg6', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.MiNet6}<input type="hidden" name="miNet6" value="${s004Bean.costList.MiNet6}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetMiNetUpdateFlg6" />
                                                        </td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.MiNet2Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.MiNet2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>
                                                        <td class="ki-summary-col">${s004Bean.costList.MiNetK1}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.MiNetK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.MiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[6 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="miNet7" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.MiNet7}" onblur="inputEdit('inpTargetMiNetUpdateFlg7', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.MiNet7}<input type="hidden" name="miNet7" value="${s004Bean.costList.MiNet7}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetMiNetUpdateFlg7" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.MiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[7] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="miNet8" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.MiNet8}" onblur="inputEdit('inpTargetMiNetUpdateFlg8', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.MiNet8}<input type="hidden" name="miNet8" value="${s004Bean.costList.MiNet8}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetMiNetUpdateFlg8" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.MiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[8 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="miNet9" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.MiNet9}" onblur="inputEdit('inpTargetMiNetUpdateFlg9', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.MiNet9}<input type="hidden" name="miNet9" value="${s004Bean.costList.MiNet9}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetMiNetUpdateFlg9" />
                                                        </td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.MiNet3Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.MiNet3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.MiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[9 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="miNet10" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.MiNet10}" onblur="inputEdit('inpTargetMiNetUpdateFlg10', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.MiNet10}<input type="hidden" name="miNet10" value="${s004Bean.costList.MiNet10}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetMiNetUpdateFlg10" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.MiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[10 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="miNet11" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.MiNet11}" onblur="inputEdit('inpTargetMiNetUpdateFlg11', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.MiNet11}<input type="hidden" name="miNet11" value="${s004Bean.costList.MiNet11}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetMiNetUpdateFlg11" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.MiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[11 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="miNet12" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.MiNet12}" onblur="inputEdit('inpTargetMiNetUpdateFlg12', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.MiNet12}<input type="hidden" name="miNet12" value="${s004Bean.costList.MiNet12}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetMiNetUpdateFlg12" />
                                                        </td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.MiNetTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.MiNet4Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.MiNet4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col">${s004Bean.costList.MiNetK2}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.MiNetK2Diff}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.MiNetG}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.MiNetGDiff}</td>
                                                        <%-- <td class="kikan_total_disp_field">${s004Bean.costList.MiNetF}</td>--%>
                                                        <%--<td class="kikan_total_disp_field">${s004Bean.costList.MiNetDiff}</td>--%>
                                                    </tr>
                                                    <!--見積総原価・製番損益NET-->
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${s004Bean.costList.KeiyakuAmountZenkiRuikeiSSNK2}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.SeibanSonekiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">
                                                            <input type="hidden" name="seibanSonekiNetTm" value="${s004Bean.costList.SeibanSonekiNetTm}" />
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[0] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="seibanSonekiNet1" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.SeibanSonekiNet1}" onblur="inputEdit('inpTargetSeibanSonekiNetUpdateFlg1', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.SeibanSonekiNet1}<input type="hidden" name="seibanSonekiNet1" value="${s004Bean.costList.SeibanSonekiNet1}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetSeibanSonekiNetUpdateFlg1" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.SeibanSonekiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[1 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="seibanSonekiNet2" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.SeibanSonekiNet2}" onblur="inputEdit('inpTargetSeibanSonekiNetUpdateFlg2', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.SeibanSonekiNet2}<input type="hidden" name="seibanSonekiNet2" value="${s004Bean.costList.SeibanSonekiNet2}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetSeibanSonekiNetUpdateFlg2" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.SeibanSonekiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[2 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="seibanSonekiNet3" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.SeibanSonekiNet3}" onblur="inputEdit('inpTargetSeibanSonekiNetUpdateFlg3', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.SeibanSonekiNet3}<input type="hidden" name="seibanSonekiNet3" value="${s004Bean.costList.SeibanSonekiNet3}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetSeibanSonekiNetUpdateFlg3" />
                                                        </td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.SeibanSonekiNet1Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.SeibanSonekiNet1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.SeibanSonekiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[3 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="seibanSonekiNet4" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.SeibanSonekiNet4}" onblur="inputEdit('inpTargetSeibanSonekiNetUpdateFlg4', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.SeibanSonekiNet4}<input type="hidden" name="seibanSonekiNet4" value="${s004Bean.costList.SeibanSonekiNet4}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetSeibanSonekiNetUpdateFlg4" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.SeibanSonekiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[4 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="seibanSonekiNet5" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.SeibanSonekiNet5}" onblur="inputEdit('inpTargetSeibanSonekiNetUpdateFlg5', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.SeibanSonekiNet5}<input type="hidden" name="seibanSonekiNet5" value="${s004Bean.costList.SeibanSonekiNet5}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetSeibanSonekiNetUpdateFlg5" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.SeibanSonekiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[5] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="seibanSonekiNet6" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.SeibanSonekiNet6}" onblur="inputEdit('inpTargetSeibanSonekiNetUpdateFlg6', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.SeibanSonekiNet6}<input type="hidden" name="seibanSonekiNet6" value="${s004Bean.costList.SeibanSonekiNet6}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetSeibanSonekiNetUpdateFlg6" />
                                                        </td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.SeibanSonekiNet2Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.SeibanSonekiNet2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td class="ki-summary-col">${s004Bean.costList.SeibanSonekiNetK1}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.SeibanSonekiNetK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.SeibanSonekiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[6] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="seibanSonekiNet7" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.SeibanSonekiNet7}" onblur="inputEdit('inpTargetSeibanSonekiNetUpdateFlg7', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.SeibanSonekiNet7}<input type="hidden" name="seibanSonekiNet7" value="${s004Bean.costList.SeibanSonekiNet7}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetSeibanSonekiNetUpdateFlg7" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.SeibanSonekiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[7 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="seibanSonekiNet8" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.SeibanSonekiNet8}" onblur="inputEdit('inpTargetSeibanSonekiNetUpdateFlg8', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.SeibanSonekiNet8}<input type="hidden" name="seibanSonekiNet8" value="${s004Bean.costList.SeibanSonekiNet8}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetSeibanSonekiNetUpdateFlg8" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.SeibanSonekiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[8 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="seibanSonekiNet9" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.SeibanSonekiNet9}" onblur="inputEdit('inpTargetSeibanSonekiNetUpdateFlg9', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.SeibanSonekiNet9}<input type="hidden" name="seibanSonekiNet9" value="${s004Bean.costList.SeibanSonekiNet9}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetSeibanSonekiNetUpdateFlg9" />
                                                        </td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.SeibanSonekiNet3Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.SeibanSonekiNet3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.SeibanSonekiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[9 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="seibanSonekiNet10" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.SeibanSonekiNet10}" onblur="inputEdit('inpTargetSeibanSonekiNetUpdateFlg10', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.SeibanSonekiNet10}<input type="hidden" name="seibanSonekiNet10" value="${s004Bean.costList.SeibanSonekiNet10}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetSeibanSonekiNetUpdateFlg10" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.SeibanSonekiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[10 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="seibanSonekiNet11" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.SeibanSonekiNet11}" onblur="inputEdit('inpTargetSeibanSonekiNetUpdateFlg11', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.SeibanSonekiNet11}<input type="hidden" name="seibanSonekiNet11" value="${s004Bean.costList.SeibanSonekiNet11}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetSeibanSonekiNetUpdateFlg11" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.SeibanSonekiNetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[11 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="seibanSonekiNet12" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.SeibanSonekiNet12}" onblur="inputEdit('inpTargetSeibanSonekiNetUpdateFlg12', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.SeibanSonekiNet12}<input type="hidden" name="seibanSonekiNet12" value="${s004Bean.costList.SeibanSonekiNet12}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetSeibanSonekiNetUpdateFlg12" />
                                                        </td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.SeibanSonekiNetTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.SeibanSonekiNet4Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.SeibanSonekiNet4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col">${s004Bean.costList.SeibanSonekiNetK2}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.SeibanSonekiNetK2Diff}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.SeibanSonekiNetG}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.SeibanSonekiNetGDiff}</td>
                                                        <%--<td class="kikan_total_disp_field">${s004Bean.costList.SeibanSonekiNetF}</td>--%>
                                                        <%--<td class="kikan_total_disp_field">${s004Bean.costList.SeibanSonekiNetDiff}</td>--%>
                                                    </tr>
                                                    <!--見積総原価・為替洗替影響-->
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${s004Bean.costList.KeiyakuAmountZenkiRuikeiKEK2}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.KawaseEikyoTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">
                                                            <input type="hidden" name="kawaseEikyoTm" value="${s004Bean.costList.KawaseEikyoTm}" />
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[0] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="kawaseEikyo1" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.KawaseEikyo1}" onblur="inputEdit('inpTargetKawaseEikyoUpdateFlg1', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.KawaseEikyo1}<input type="hidden" name="kawaseEikyo1" value="${s004Bean.costList.KawaseEikyo1}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKawaseEikyoUpdateFlg1" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.KawaseEikyoTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[1 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="kawaseEikyo2" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.KawaseEikyo2}" onblur="inputEdit('inpTargetKawaseEikyoUpdateFlg2', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.KawaseEikyo2}<input type="hidden" name="kawaseEikyo2" value="${s004Bean.costList.KawaseEikyo2}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKawaseEikyoUpdateFlg2" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.KawaseEikyoTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[2 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="kawaseEikyo3" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.KawaseEikyo3}" onblur="inputEdit('inpTargetKawaseEikyoUpdateFlg3', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.KawaseEikyo3}<input type="hidden" name="kawaseEikyo3" value="${s004Bean.costList.KawaseEikyo3}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKawaseEikyoUpdateFlg3" />
                                                        </td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.KawaseEikyo1Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.KawaseEikyo1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.KawaseEikyoTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[3 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="kawaseEikyo4" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.KawaseEikyo4}" onblur="inputEdit('inpTargetKawaseEikyoUpdateFlg4', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.KawaseEikyo4}<input type="hidden" name="kawaseEikyo4" value="${s004Bean.costList.KawaseEikyo4}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKawaseEikyoUpdateFlg4" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.KawaseEikyoTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[4 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="kawaseEikyo5" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.KawaseEikyo5}" onblur="inputEdit('inpTargetKawaseEikyoUpdateFlg5', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.KawaseEikyo5}<input type="hidden" name="kawaseEikyo5" value="${s004Bean.costList.KawaseEikyo5}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKawaseEikyoUpdateFlg5" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.KawaseEikyoTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[5] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="kawaseEikyo6" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.KawaseEikyo6}" onblur="inputEdit('inpTargetKawaseEikyoUpdateFlg6', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.KawaseEikyo6}<input type="hidden" name="kawaseEikyo6" value="${s004Bean.costList.KawaseEikyo6}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKawaseEikyoUpdateFlg6" />
                                                        </td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.KawaseEikyo2Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.KawaseEikyo2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td class="ki-summary-col">${s004Bean.costList.KawaseEikyoK1}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.KawaseEikyoK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.KawaseEikyoTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[6] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="kawaseEikyo7" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.KawaseEikyo7}" onblur="inputEdit('inpTargetKawaseEikyoUpdateFlg7', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.KawaseEikyo7}<input type="hidden" name="kawaseEikyo7" value="${s004Bean.costList.KawaseEikyo7}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKawaseEikyoUpdateFlg7" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.KawaseEikyoTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[7 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="kawaseEikyo8" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.KawaseEikyo8}" onblur="inputEdit('inpTargetKawaseEikyoUpdateFlg8', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.KawaseEikyo8}<input type="hidden" name="kawaseEikyo8" value="${s004Bean.costList.KawaseEikyo8}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKawaseEikyoUpdateFlg8" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.KawaseEikyoTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[8 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="kawaseEikyo9" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.KawaseEikyo9}" onblur="inputEdit('inpTargetKawaseEikyoUpdateFlg9', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.KawaseEikyo9}<input type="hidden" name="kawaseEikyo9" value="${s004Bean.costList.KawaseEikyo9}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKawaseEikyoUpdateFlg9" />
                                                        </td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.KawaseEikyo3Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.KawaseEikyo3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.KawaseEikyoTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[9 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="kawaseEikyo10" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.KawaseEikyo10}" onblur="inputEdit('inpTargetKawaseEikyoUpdateFlg10', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.KawaseEikyo10}<input type="hidden" name="kawaseEikyo10" value="${s004Bean.costList.KawaseEikyo10}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKawaseEikyoUpdateFlg10" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.KawaseEikyoTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[10 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="kawaseEikyo11" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.KawaseEikyo11}" onblur="inputEdit('inpTargetKawaseEikyoUpdateFlg11', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.KawaseEikyo11}<input type="hidden" name="kawaseEikyo11" value="${s004Bean.costList.KawaseEikyo11}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKawaseEikyoUpdateFlg11" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.KawaseEikyoTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[11 ] == null && editAllFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="kawaseEikyo12" class="numberFormat" style="${numberStyle}" value="${s004Bean.costList.KawaseEikyo12}" onblur="inputEdit('inpTargetKawaseEikyoUpdateFlg12', 0, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${s004Bean.costList.KawaseEikyo12}<input type="hidden" name="kawaseEikyo12" value="${s004Bean.costList.KawaseEikyo12}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetKawaseEikyoUpdateFlg12" />
                                                        </td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td class="kikan_shinko_kanjyo_diff">${s004Bean.costList.KawaseEikyoTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.costList.KawaseEikyo4Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.KawaseEikyo4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col">${s004Bean.costList.KawaseEikyoK2}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.KawaseEikyoK2Diff}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.KawaseEikyoG}</td>
                                                        <td class="ki-summary-col">${s004Bean.costList.KawaseEikyoGDiff}</td>
                                                        <%--<td class="kikan_total_disp_field">${s004Bean.costList.KawaseEikyoF}</td>--%>
                                                        <%--<td class="kikan_total_disp_field">${s004Bean.costList.KawaseEikyoDiff}</td>--%>
                                                    </tr>

                                                </table>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <!--売上高(今回合計)・TOTAL-->
                                                    <tr style="height:23px;"  class="fix_tb total_disp_field" id="link_uriage1ListOpen">
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px">${s004Bean.totalSales.UriageAmountTotal}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px">${s004Bean.totalSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px">${s004Bean.totalSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px">${s004Bean.totalSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount1Q}</td>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px">${s004Bean.totalSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px">${s004Bean.totalSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px">${s004Bean.totalSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount2Q}</td>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmountK1}</td>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmountK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px">${s004Bean.totalSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px">${s004Bean.totalSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px">${s004Bean.totalSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount3Q}</td>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px">${s004Bean.totalSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px">${s004Bean.totalSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px">${s004Bean.totalSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount12}</td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td style="width:105px">${s004Bean.totalSales.UriageAmountTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount4Q}</td>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmount4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmountK2}</td>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmountK2Diff}</td>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmountG}</td>
                                                        <td style="width:105px">${s004Bean.totalSales.UriageAmountGDiff}</td>
                                                        <%--<td style="width:105px">${s004Bean.totalSales.UriageAmountF}</td>--%>
                                                        <%--<td style="width:105px">${s004Bean.totalSales.UriageAmountDiff}</td>--%>
                                                    </tr>
                                                    <!--売上高(今回合計)・内訳-->
                                                    <c:forEach items="${s004Bean.salesList}" var="salesList" varStatus="s">
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${salesList.UriageAmountTotal}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${salesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">${salesList.UriageAmount1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${salesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">${salesList.UriageAmount2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${salesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">${salesList.UriageAmount3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${salesList.UriageAmount1Q}</td>
                                                        <td class="ki-summary-col">${salesList.UriageAmount1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${salesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">${salesList.UriageAmount4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${salesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">${salesList.UriageAmount5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${salesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">${salesList.UriageAmount6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${salesList.UriageAmount2Q}</td>
                                                        <td class="ki-summary-col">${salesList.UriageAmount2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td class="ki-summary-col">${salesList.UriageAmountK1}</td>
                                                        <td class="ki-summary-col">${salesList.UriageAmountK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${salesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">${salesList.UriageAmount7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${salesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">${salesList.UriageAmount8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${salesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">${salesList.UriageAmount9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${salesList.UriageAmount3Q}</td>
                                                        <td class="ki-summary-col">${salesList.UriageAmount3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${salesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">${salesList.UriageAmount10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${salesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">${salesList.UriageAmount11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${salesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">${salesList.UriageAmount12}</td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td class="kikan_shinko_kanjyo_diff">${salesList.UriageAmountTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${salesList.UriageAmount4Q}</td>
                                                        <td class="ki-summary-col">${salesList.UriageAmount4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col">${salesList.UriageAmountK2}</td>
                                                        <td class="ki-summary-col">${salesList.UriageAmountK2Diff}</td>
                                                        <td class="ki-summary-col">${salesList.UriageAmountG}</td>
                                                        <td class="ki-summary-col">${salesList.UriageAmountGDiff}</td>
                                                        <%--<td class="kikan_total_disp_field">${salesList.UriageAmountF}</td>--%>
                                                        <%--<td class="kikan_total_disp_field">${salesList.UriageAmountDiff}</td>--%>
                                                    </tr>
                                                    </c:forEach>
                                                </table>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <!--売上高(累計合計)・TOTAL-->
                                                    <tr style="height:23px;"  class="fix_tb total_disp_field" id="link_uriage2ListOpen">
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px">${s004Bean.totalRuikeiSales.UriageAmountTotal}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount1Q}</td>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount2Q}</td>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountK1}</td>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount3Q}</td>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount12}</td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount4Q}</td>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmount4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountK2}</td>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountK2Diff}</td>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountG}</td>
                                                        <td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountGDiff}</td>
                                                        <%--<td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountF}</td>--%>
                                                        <%--<td style="width:105px">${s004Bean.totalRuikeiSales.UriageAmountDiff}</td>--%>
                                                    </tr>
                                                    <!--売上高(累計合計)・通貨別の内訳-->
                                                    <c:forEach items="${s004Bean.ruikeiSalesList}" var="ruikeiSalesList" varStatus="s">
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${ruikeiSalesList.UriageAmountTotal}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${ruikeiSalesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">${ruikeiSalesList.UriageAmount1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${ruikeiSalesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">${ruikeiSalesList.UriageAmount2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${ruikeiSalesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">${ruikeiSalesList.UriageAmount3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${ruikeiSalesList.UriageAmount1Q}</td>
                                                        <td class="ki-summary-col">${ruikeiSalesList.UriageAmount1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${ruikeiSalesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">${ruikeiSalesList.UriageAmount4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${ruikeiSalesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">${ruikeiSalesList.UriageAmount5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${ruikeiSalesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">${ruikeiSalesList.UriageAmount6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${ruikeiSalesList.UriageAmount2Q}</td>
                                                        <td class="ki-summary-col">${ruikeiSalesList.UriageAmount2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td class="ki-summary-col">${ruikeiSalesList.UriageAmountK1}</td>
                                                        <td class="ki-summary-col">${ruikeiSalesList.UriageAmountK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${ruikeiSalesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">${ruikeiSalesList.UriageAmount7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${ruikeiSalesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">${ruikeiSalesList.UriageAmount8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${ruikeiSalesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">${ruikeiSalesList.UriageAmount9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${ruikeiSalesList.UriageAmount3Q}</td>
                                                        <td class="ki-summary-col">${ruikeiSalesList.UriageAmount3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${ruikeiSalesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">${ruikeiSalesList.UriageAmount10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${ruikeiSalesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">${ruikeiSalesList.UriageAmount11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${ruikeiSalesList.UriageAmountTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">${ruikeiSalesList.UriageAmount12}</td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td class="kikan_shinko_kanjyo_diff">${ruikeiSalesList.UriageAmountTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${ruikeiSalesList.UriageAmount4Q}</td>
                                                        <td class="ki-summary-col">${ruikeiSalesList.UriageAmount4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col">${ruikeiSalesList.UriageAmountK2}</td>
                                                        <td class="ki-summary-col">${ruikeiSalesList.UriageAmountK2Diff}</td>
                                                        <td class="ki-summary-col">${ruikeiSalesList.UriageAmountG}</td>
                                                        <td class="ki-summary-col">${ruikeiSalesList.UriageAmountGDiff}</td>
                                                        <%--<td class="kikan_total_disp_field">${ruikeiSalesList.UriageAmountF}</td>--%>
                                                        <%--<td class="kikan_total_disp_field">${ruikeiSalesList.UriageAmountDiff}</td>--%>
                                                    </tr>
                                                    </c:forEach>
                                                </table>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <!--売上原価(今回合計)・TOTAL-->
                                                    <tr style="height:23px;"  class="fix_tb total_disp_field" id="link_uriGen1ListOpen">
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px">${s004Bean.totalSalesCost.UriageRuikeiAmount}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px">${s004Bean.totalSalesCost.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px">${s004Bean.totalSalesCost.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px">${s004Bean.totalSalesCost.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount1Q}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px">${s004Bean.totalSalesCost.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px">${s004Bean.totalSalesCost.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px">${s004Bean.totalSalesCost.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount2Q}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmountK1}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmountK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px">${s004Bean.totalSalesCost.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px">${s004Bean.totalSalesCost.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px">${s004Bean.totalSalesCost.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount3Q}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px">${s004Bean.totalSalesCost.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px">${s004Bean.totalSalesCost.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px">${s004Bean.totalSalesCost.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount12}</td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td style="width:105px">${s004Bean.totalSalesCost.UriageAmountTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount4Q}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmount4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmountK2}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmountK2Diff}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmountG}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCost.UriageAmountGDiff}</td>
                                                        <%--<td style="width:105px">${s004Bean.totalSalesCost.UriageAmountF}</td>--%>
                                                        <%--<td style="width:105px">${s004Bean.totalSalesCost.UriageAmountDiff}</td>--%>
                                                    </tr>
                                                    <!--売上原価(今回)・内訳-->
                                                    <c:forEach items="${s004Bean.salesCostList}" var="salesCostList" varStatus="s">
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${salesCostList.UriageRuikeiNet}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${salesCostList.NetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">
                                                            <input type="hidden" name="netTm" value="${salesCostList.NetTm}" />
                                                            <input type="hidden" name="inpTargetNetCategoryKbn1" value="${salesCostList.categoryKbn1}" />
                                                            <input type="hidden" name="inpTargetNetCategoryKbn2" value="${salesCostList.categoryKbn2}" />
                                                            <input type="hidden" name="inpTargetNetCategoryCode" value="${salesCostList.categoryCode}" />

                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[0] == null && sUtil.uriageYmCompare(s004Bean.kikanFromAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="net1" class="numberFormat" style="${numberStyle}" value="${salesCostList.Net1}" onblur="inputEdit('inpTargetNetUpdateFlg1', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${salesCostList.Net1}<input type="hidden" name="net1" value="${salesCostList.Net1}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetNetUpdateFlg1" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${salesCostList.NetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[1] == null && sUtil.uriageYmCompare(s004Bean.kikanFromAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="net2" class="numberFormat" style="${numberStyle}" value="${salesCostList.Net2}" onblur="inputEdit('inpTargetNetUpdateFlg2', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${salesCostList.Net2}<input type="hidden" name="net2" value="${salesCostList.Net2}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetNetUpdateFlg2" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${salesCostList.NetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[2] == null && sUtil.uriageYmCompare(s004Bean.kikanFromAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="net3" class="numberFormat" style="${numberStyle}" value="${salesCostList.Net3}" onblur="inputEdit('inpTargetNetUpdateFlg3', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${salesCostList.Net3}<input type="hidden" name="net3" value="${salesCostList.Net3}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetNetUpdateFlg3" />
                                                        </td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${salesCostList.Net1Q}</td>
                                                        <td class="ki-summary-col">${salesCostList.Net1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${salesCostList.NetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[3] == null && sUtil.uriageYmCompare(s004Bean.kikanFromAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="net4" class="numberFormat" style="${numberStyle}" value="${salesCostList.Net4}" onblur="inputEdit('inpTargetNetUpdateFlg4', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${salesCostList.Net4}<input type="hidden" name="net4" value="${salesCostList.Net4}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetNetUpdateFlg4" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${salesCostList.NetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[4] == null && sUtil.uriageYmCompare(s004Bean.kikanFromAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="net5" class="numberFormat" style="${numberStyle}" value="${salesCostList.Net5}" onblur="inputEdit('inpTargetNetUpdateFlg5', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${salesCostList.Net5}<input type="hidden" name="net5" value="${salesCostList.Net5}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetNetUpdateFlg5" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${salesCostList.NetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[5] == null && sUtil.uriageYmCompare(s004Bean.kikanFromAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="net6" class="numberFormat" style="${numberStyle}" value="${salesCostList.Net6}" onblur="inputEdit('inpTargetNetUpdateFlg6', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${salesCostList.Net6}<input type="hidden" name="net6" value="${salesCostList.Net6}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetNetUpdateFlg6" />
                                                        </td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${salesCostList.Net2Q}</td>
                                                        <td class="ki-summary-col">${salesCostList.Net2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td class="ki-summary-col">${salesCostList.NetK1}</td>
                                                        <td class="ki-summary-col">${salesCostList.NetK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${salesCostList.NetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[6] == null && sUtil.uriageYmCompare(s004Bean.kikanToAry[0], uriageYm) == 0}">
                                                                    <input type="text" name="net7" class="numberFormat" style="${numberStyle}" value="${salesCostList.Net7}" onblur="inputEdit('inpTargetNetUpdateFlg7', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${salesCostList.Net7}<input type="hidden" name="net7" value="${salesCostList.Net7}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetNetUpdateFlg7" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${salesCostList.NetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[7] == null && sUtil.uriageYmCompare(s004Bean.kikanToAry[1], uriageYm) == 0}">
                                                                    <input type="text" name="net8" class="numberFormat" style="${numberStyle}" value="${salesCostList.Net8}" onblur="inputEdit('inpTargetNetUpdateFlg8', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${salesCostList.Net8}<input type="hidden" name="net8" value="${salesCostList.Net8}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetNetUpdateFlg8" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${salesCostList.NetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[8] == null && sUtil.uriageYmCompare(s004Bean.kikanToAry[2], uriageYm) == 0}">
                                                                    <input type="text" name="net9" class="numberFormat" style="${numberStyle}" value="${salesCostList.Net9}" onblur="inputEdit('inpTargetNetUpdateFlg9', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${salesCostList.Net9}<input type="hidden" name="net9" value="${salesCostList.Net9}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetNetUpdateFlg9" />
                                                        </td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${salesCostList.Net3Q}</td>
                                                        <td class="ki-summary-col">${salesCostList.Net3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${salesCostList.NetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[9] == null && sUtil.uriageYmCompare(s004Bean.kikanToAry[3], uriageYm) == 0}">
                                                                    <input type="text" name="net10" class="numberFormat" style="${numberStyle}" value="${salesCostList.Net10}" onblur="inputEdit('inpTargetNetUpdateFlg10', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${salesCostList.Net10}<input type="hidden" name="net10" value="${salesCostList.Net10}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetNetUpdateFlg10" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${salesCostList.NetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[10] == null && sUtil.uriageYmCompare(s004Bean.kikanToAry[4], uriageYm) == 0}">
                                                                    <input type="text" name="net11" class="numberFormat" style="${numberStyle}" value="${salesCostList.Net11}" onblur="inputEdit('inpTargetNetUpdateFlg11', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${salesCostList.Net11}<input type="hidden" name="net11" value="${salesCostList.Net11}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetNetUpdateFlg11" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${salesCostList.NetTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">
                                                            <c:choose>
                                                                <c:when test="${s004Bean.editFlg == '1' && s004Bean.classNameAry[11] == null && sUtil.uriageYmCompare(s004Bean.kikanToAry[5], uriageYm) == 0}">
                                                                    <input type="text" name="net12" class="numberFormat" style="${numberStyle}" value="${salesCostList.Net12}" onblur="inputEdit('inpTargetNetUpdateFlg12', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${salesCostList.Net12}<input type="hidden" name="net12" value="${salesCostList.Net12}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <input type="hidden" name="inpTargetNetUpdateFlg12" />
                                                        </td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td class="kikan_shinko_kanjyo_diff">${salesCostList.NetTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${salesCostList.Net4Q}</td>
                                                        <td class="ki-summary-col">${salesCostList.Net4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col">${salesCostList.NetK2}</td>
                                                        <td class="ki-summary-col">${salesCostList.NetK2Diff}</td>
                                                        <td class="ki-summary-col">${salesCostList.NetG}</td>
                                                        <td class="ki-summary-col">${salesCostList.NetGDiff}</td>
                                                        <%--<td class="kikan_total_disp_field">${salesCostList.NetF}</td>--%>
                                                        <%--<td class="kikan_total_disp_field">${salesCostList.NetDiff}</td>--%>
                                                    </tr>
                                                    </c:forEach>
                                                </table>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <!--売上原価(累計)・total-->
                                                    <tr style="height:23px;"  class="fix_tb total_disp_field">
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px">${s004Bean.totalSalesCostRuikei.UriageRuikeiAmount}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount1Q}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount2Q}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountK1}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount3Q}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount12}</td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount4Q}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmount4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountK2}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountK2Diff}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountG}</td>
                                                        <td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountGDiff}</td>
                                                        <%--<td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountF}</td>--%>
                                                        <%--<td style="width:105px">${s004Bean.totalSalesCostRuikei.UriageAmountDiff}</td>--%>
                                                    </tr>
                                                </table>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <!--粗利(今回)-->
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${s004Bean.arariMaeAllTotal}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[0]}">${s004Bean.arari1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[1]}">${s004Bean.arari2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[2]}">${s004Bean.arari3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${s004Bean.arari1Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${s004Bean.arari1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[3]}">${s004Bean.arari4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[4]}">${s004Bean.arari5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[5]}">${s004Bean.arari6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${s004Bean.arari2Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${s004Bean.arari2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td style="width:105px" class="ki-summary-col">${s004Bean.arariK1}</td>
                                                        <td style="width:105px" class="ki-summary-col">${s004Bean.arariK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[6]}">${s004Bean.arari7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[7]}">${s004Bean.arari8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[8]}">${s004Bean.arari9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${s004Bean.arari3Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${s004Bean.arari3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[9]}">${s004Bean.arari10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[10]}">${s004Bean.arari11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[11]}">${s004Bean.arari12}</td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${s004Bean.arari4Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${s004Bean.arari4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td style="width:105px" class="ki-summary-col">${s004Bean.arariK2}</td>
                                                        <td style="width:105px" class="ki-summary-col">${s004Bean.arariK2Diff}</td>
                                                        <td style="width:105px" class="ki-summary-col">${s004Bean.arariG}</td>
                                                        <td style="width:105px" class="ki-summary-col">${s004Bean.arariGDiff}</td>
                                                        <%--<td style="width:105px" class="kikan_total_disp_field">${s004Bean.arariF}</td>--%>
                                                        <%--<td style="width:105px" class="kikan_total_disp_field">${s004Bean.arariDiff}</td>--%>
                                                    </tr>
                                                    <!--粗利(累計)-->
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${s004Bean.arariRuikeiMaeAllTotal}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariRuikeiTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">${s004Bean.arariRuikei1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariRuikeiTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">${s004Bean.arariRuikei2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariRuikeiTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">${s004Bean.arariRuikei3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.arariRuikei1Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.arariRuikei1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariRuikeiTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">${s004Bean.arariRuikei4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariRuikeiTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">${s004Bean.arariRuikei5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariRuikeiTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">${s004Bean.arariRuikei6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.arariRuikei2Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.arariRuikei2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td class="ki-summary-col">${s004Bean.arariRuikeiK1}</td>
                                                        <td class="ki-summary-col">${s004Bean.arariRuikeiK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariRuikeiTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">${s004Bean.arariRuikei7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariRuikeiTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">${s004Bean.arariRuikei8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariRuikeiTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">${s004Bean.arariRuikei9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.arariRuikei3Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.arariRuikei3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariRuikeiTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">${s004Bean.arariRuikei10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariRuikeiTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">${s004Bean.arariRuikei11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariRuikeiTm}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">${s004Bean.arariRuikei12}</td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${s004Bean.arariRuikeiTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.arariRuikei4Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.arariRuikei4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col">${s004Bean.arariRuikeiK2}</td>
                                                        <td class="ki-summary-col">${s004Bean.arariRuikeiK2Diff}</td>
                                                        <td class="ki-summary-col">${s004Bean.arariRuikeiG}</td>
                                                        <td class="ki-summary-col">${s004Bean.arariRuikeiGDiff}</td>
                                                        <%--<td class="kikan_total_disp_field">${s004Bean.arariRuikeiF}</td>--%>
                                                        <%--<td class="kikan_total_disp_field">${s004Bean.arariRuikeiDiff}</td>--%>
                                                    </tr>
                                                    <!--M率(今回)-->
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${s004Bean.mrateMaeAllTotal}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}"><c:out value="${s004Bean.mrate1}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}"><c:out value="${s004Bean.mrate2}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}"><c:out value="${s004Bean.mrate3}" /></td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.mrate1Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.mrate1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}"><c:out value="${s004Bean.mrate4}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}"><c:out value="${s004Bean.mrate5}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}"><c:out value="${s004Bean.mrate6}" /></td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.mrate2Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.mrate2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td class="ki-summary-col"><c:out value="${s004Bean.mrateK1}" /></td>
                                                        <td class="ki-summary-col"><c:out value="${s004Bean.mrateK1Diff}" /></td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}"><c:out value="${s004Bean.mrate7}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}"><c:out value="${s004Bean.mrate8}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}"><c:out value="${s004Bean.mrate9}" /></td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.mrate3Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.mrate3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}"><c:out value="${s004Bean.mrate10}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}"><c:out value="${s004Bean.mrate11}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}"><c:out value="${s004Bean.mrate12}" /></td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateTm}" /></td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.mrate4Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.mrate4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col"><c:out value="${s004Bean.mrateK2}" /></td>
                                                        <td class="ki-summary-col"><c:out value="${s004Bean.mrateK2Diff}" /></td>
                                                        <td class="ki-summary-col"><c:out value="${s004Bean.mrateG}" /></td>
                                                        <td class="ki-summary-col"><c:out value="${s004Bean.mrateGDiff}" /></td>
                                                        <%--<td class="kikan_total_disp_field"><c:out value="${s004Bean.mrateF}" /></td>--%>
                                                        <%--<td class="kikan_total_disp_field"><c:out value="${s004Bean.mrateDiff}" /></td>--%>
                                                    </tr>
                                                    <!--M率(累計)-->
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${s004Bean.mrateRuikeiMaeAllTotal}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateRuikeiTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}"><c:out value="${s004Bean.mrateRuikei1}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateRuikeiTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}"><c:out value="${s004Bean.mrateRuikei2}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateRuikeiTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}"><c:out value="${s004Bean.mrateRuikei3}" /></td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.mrateRuikei1Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.mrateRuikei1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateRuikeiTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}"><c:out value="${s004Bean.mrateRuikei4}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateRuikeiTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}"><c:out value="${s004Bean.mrateRuikei5}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateRuikeiTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}"><c:out value="${s004Bean.mrateRuikei6}" /></td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.mrateRuikei2Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.mrateRuikei2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td class="ki-summary-col"><c:out value="${s004Bean.mrateRuikeiK1}" /></td>
                                                        <td class="ki-summary-col"><c:out value="${s004Bean.mrateRuikeiK1Diff}" /></td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateRuikeiTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}"><c:out value="${s004Bean.mrateRuikei7}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateRuikeiTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}"><c:out value="${s004Bean.mrateRuikei8}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateRuikeiTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}"><c:out value="${s004Bean.mrateRuikei9}" /></td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.mrateRuikei3Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.mrateRuikei3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateRuikeiTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}"><c:out value="${s004Bean.mrateRuikei10}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateRuikeiTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}"><c:out value="${s004Bean.mrateRuikei11}" /></td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateRuikeiTm}" /></td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}"><c:out value="${s004Bean.mrateRuikei12}" /></td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td style="width:105px" class="kikan_shinko_kanjyo_diff"><c:out value="${s004Bean.mrateRuikeiTm}" /></td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${s004Bean.mrateRuikei4Q}</td>
                                                        <td class="ki-summary-col">${s004Bean.mrateRuikei4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col"><c:out value="${s004Bean.mrateRuikeiK2}" /></td>
                                                        <td class="ki-summary-col"><c:out value="${s004Bean.mrateRuikeiK2Diff}" /></td>
                                                        <td class="ki-summary-col"><c:out value="${s004Bean.mrateRuikeiG}" /></td>
                                                        <td class="ki-summary-col"><c:out value="${s004Bean.mrateRuikeiGDiff}" /></td>
                                                        <%--<td class="kikan_total_disp_field"><c:out value="${s004Bean.mrateRuikeiF}" /></td>--%>
                                                        <%--<td class="kikan_total_disp_field"><c:out value="${s004Bean.mrateRuikeiDiff}" /></td>--%>
                                                    </tr>
                                                </table>

                                                <c:if test="${detailHeader.lossControlFlag}">
                                                <div style="height:5px"></div>
                                                
                                                <!-- ロスコン情報(2018A追加) ※ロスコン対象案件のみこの欄を表示 -->
                                                <c:set value="${s004Bean.lossData}" var="lossData"/>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <!-- ロスコン補正 -->
                                                    <c:set value="${lossData['LOSS_HOSEI']}" var="lossHosei"/>
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${lossHosei["lossBRuikei"]}</td>
                                                        </c:if>

                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${lossHosei["lossTm"]}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[0]}">${lossHosei["loss1"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${lossHosei["lossTm"]}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[1]}">${lossHosei["loss2"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${lossHosei["lossTm"]}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[2]}">${lossHosei["loss3"]}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${lossHosei["loss1Q"]}</td>
                                                        <td style="width:105px" class="ki-summary-col">${lossHosei["loss1QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${lossHosei["lossTm"]}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[3]}">${lossHosei["loss4"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${lossHosei["lossTm"]}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[4]}">${lossHosei["loss5"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${lossHosei["lossTm"]}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[5]}">${lossHosei["loss6"]}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${lossHosei["loss2Q"]}</td>
                                                        <td style="width:105px" class="ki-summary-col">${lossHosei["loss2QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td style="width:105px" class="ki-summary-col">${lossHosei["lossK1"]}</td>
                                                        <td style="width:105px" class="ki-summary-col">${lossHosei["lossK1Diff"]}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${lossHosei["lossTm"]}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[6]}">${lossHosei["loss7"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${lossHosei["lossTm"]}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[7]}">${lossHosei["loss8"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${lossHosei["lossTm"]}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[8]}">${lossHosei["loss9"]}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${lossHosei["loss3Q"]}</td>
                                                        <td style="width:105px" class="ki-summary-col">${lossHosei["loss3QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${lossHosei["lossTm"]}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[9]}">${lossHosei["loss10"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${lossHosei["lossTm"]}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[10]}">${lossHosei["loss11"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${lossHosei["lossTm"]}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[11]}">${lossHosei["loss12"]}</td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${lossHosei["loss4Q"]}</td>
                                                        <td style="width:105px" class="ki-summary-col">${lossHosei["loss4QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td style="width:105px" class="ki-summary-col">${lossHosei["lossK2"]}</td>
                                                        <td style="width:105px" class="ki-summary-col">${lossHosei["lossK2Diff"]}</td>
                                                        <td style="width:105px" class="ki-summary-col">${lossHosei["lossG"]}</td>
                                                        <td style="width:105px" class="ki-summary-col">${lossHosei["lossGDiff"]}</td>
                                                    </tr>

                                                    <!-- ロスコン補正後の売上高 -->
                                                    <c:set value="${lossData['LOSS_AMOUNT']}" var="lossAmount"/>
                                                    <tr style="height:23px;" class="fix_tb fix_tb total_disp_field">
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td>${lossAmount["lossBRuikei"]}</td>
                                                        </c:if>
                                                        
                                                        <c:if test="${s004Bean.tmIndex==0}"><td >${lossAmount["lossTm"]}</td></c:if>
                                                        <td >${lossAmount["loss1"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td >${lossAmount["lossTm"]}</td></c:if>
                                                        <td >${lossAmount["loss2"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td >${lossAmount["lossTm"]}</td></c:if>
                                                        <td >${lossAmount["loss3"]}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td >${lossAmount["loss1Q"]}</td>
                                                        <td >${lossAmount["loss1QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td >${lossAmount["lossTm"]}</td></c:if>
                                                        <td >${lossAmount["loss4"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td >${lossAmount["lossTm"]}</td></c:if>
                                                        <td >${lossAmount["loss5"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td >${lossAmount["lossTm"]}</td></c:if>
                                                        <td >${lossAmount["loss6"]}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td >${lossAmount["loss2Q"]}</td>
                                                        <td >${lossAmount["loss2QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td >${lossAmount["lossK1"]}</td>
                                                        <td >${lossAmount["lossK1Diff"]}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td >${lossAmount["lossTm"]}</td></c:if>
                                                        <td >${lossAmount["loss7"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td >${lossAmount["lossTm"]}</td></c:if>
                                                        <td >${lossAmount["loss8"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td >${lossAmount["lossTm"]}</td></c:if>
                                                        <td >${lossAmount["loss9"]}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td >${lossAmount["loss3Q"]}</td>
                                                        <td >${lossAmount["loss3QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td >${lossAmount["lossTm"]}</td></c:if>
                                                        <td >${lossAmount["loss10"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td >${lossAmount["lossTm"]}</td></c:if>
                                                        <td >${lossAmount["loss11"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td >${lossAmount["lossTm"]}</td></c:if>
                                                        <td >${lossAmount["loss12"]}</td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td >${lossAmount["loss4Q"]}</td>
                                                        <td >${lossAmount["loss4QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td >${lossAmount["lossK2"]}</td>
                                                        <td >${lossAmount["lossK2Diff"]}</td>
                                                        <td >${lossAmount["lossG"]}</td>
                                                        <td >${lossAmount["lossGDiff"]}</td>
                                                    </tr>
                                                    
                                                    <tr style="height:23px;" class="fix_tb fix_tb total_disp_field">
                                                        <!-- ロスコン補正後の売上原価(今回) -->
                                                        <c:set value="${lossData['LOSS_GENKA']}" var="lossGenka"/>
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td>${lossGenka["lossBRuikei"]}</td>
                                                        </c:if>
                                                        
                                                        <c:if test="${s004Bean.tmIndex==0}"><td >${lossGenka["lossTm"]}</td></c:if>
                                                        <td >${lossGenka["loss1"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td >${lossGenka["lossTm"]}</td></c:if>
                                                        <td >${lossGenka["loss2"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td >${lossGenka["lossTm"]}</td></c:if>
                                                        <td >${lossGenka["loss3"]}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td >${lossGenka["loss1Q"]}</td>
                                                        <td >${lossGenka["loss1QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td >${lossGenka["lossTm"]}</td></c:if>
                                                        <td >${lossGenka["loss4"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td >${lossGenka["lossTm"]}</td></c:if>
                                                        <td >${lossGenka["loss5"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td >${lossGenka["lossTm"]}</td></c:if>
                                                        <td >${lossGenka["loss6"]}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td >${lossGenka["loss2Q"]}</td>
                                                        <td >${lossGenka["loss2QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td >${lossGenka["lossK1"]}</td>
                                                        <td >${lossGenka["lossK1Diff"]}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td >${lossGenka["lossTm"]}</td></c:if>
                                                        <td >${lossGenka["loss7"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td >${lossGenka["lossTm"]}</td></c:if>
                                                        <td >${lossGenka["loss8"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td >${lossGenka["lossTm"]}</td></c:if>
                                                        <td >${lossGenka["loss9"]}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td >${lossGenka["loss3Q"]}</td>
                                                        <td >${lossGenka["loss3QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td >${lossGenka["lossTm"]}</td></c:if>
                                                        <td >${lossGenka["loss10"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td >${lossGenka["lossTm"]}</td></c:if>
                                                        <td >${lossGenka["loss11"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td >${lossGenka["lossTm"]}</td></c:if>
                                                        <td >${lossGenka["loss12"]}</td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td >${lossGenka["loss4Q"]}</td>
                                                        <td >${lossGenka["loss4QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td >${lossGenka["lossK2"]}</td>
                                                        <td >${lossGenka["lossK2Diff"]}</td>
                                                        <td >${lossGenka["lossG"]}</td>
                                                        <td >${lossGenka["lossGDiff"]}</td>
                                                    </tr>

                                                    <!-- ロスコン補正後の粗利 -->
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <c:set value="${lossData['LOSS_ARARI']}" var="lossArari"/>
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossArari["lossBRuikei"]}</td>
                                                        </c:if>

                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${lossArari["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">${lossArari["loss1"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${lossArari["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">${lossArari["loss2"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${lossArari["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">${lossArari["loss3"]}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${lossArari["loss1Q"]}</td>
                                                        <td style="width:105px" class="ki-summary-col">${lossArari["loss1QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${lossArari["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">${lossArari["loss4"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${lossArari["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">${lossArari["loss5"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${lossArari["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">${lossArari["loss6"]}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossArari["loss2Q"]}</td>
                                                        <td class="ki-summary-col">${lossArari["loss2QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td class="ki-summary-col">${lossArari["lossK1"]}</td>
                                                        <td class="ki-summary-col">${lossArari["lossK1Diff"]}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${lossArari["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">${lossArari["loss7"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${lossArari["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">${lossArari["loss8"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${lossArari["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">${lossArari["loss9"]}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossArari["loss3Q"]}</td>
                                                        <td class="ki-summary-col">${lossArari["loss3QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${lossArari["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">${lossArari["loss10"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${lossArari["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">${lossArari["loss11"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${lossArari["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">${lossArari["loss12"]}</td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossArari["loss4Q"]}</td>
                                                        <td class="ki-summary-col">${lossArari["loss4QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col">${lossArari["lossK2"]}</td>
                                                        <td class="ki-summary-col">${lossArari["lossK2Diff"]}</td>
                                                        <td class="ki-summary-col">${lossArari["lossG"]}</td>
                                                        <td class="ki-summary-col">${lossArari["lossGDiff"]}</td>
                                                    </tr>

                                                    <!-- ロスコン補正後のM率 -->
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <c:set value="${lossData['LOSS_MRATE']}" var="lossMrate"/>
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossMrate["lossBRuikei"]}</td>
                                                        </c:if>

                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${lossMrate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">${lossMrate["loss1"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${lossMrate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">${lossMrate["loss2"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${lossMrate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">${lossMrate["loss3"]}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${lossMrate["loss1Q"]}</td>
                                                        <td style="width:105px" class="ki-summary-col">${lossMrate["loss1QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${lossMrate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">${lossMrate["loss4"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${lossMrate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">${lossMrate["loss5"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${lossMrate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">${lossMrate["loss6"]}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossMrate["loss2Q"]}</td>
                                                        <td class="ki-summary-col">${lossMrate["loss2QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td class="ki-summary-col">${lossMrate["lossK1"]}</td>
                                                        <td class="ki-summary-col">${lossMrate["lossK1Diff"]}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${lossMrate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">${lossMrate["loss7"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${lossMrate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">${lossMrate["loss8"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${lossMrate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">${lossMrate["loss9"]}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossMrate["loss3Q"]}</td>
                                                        <td class="ki-summary-col">${lossMrate["loss3QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${lossMrate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">${lossMrate["loss10"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${lossMrate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">${lossMrate["loss11"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${lossMrate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">${lossMrate["loss12"]}</td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossMrate["loss4Q"]}</td>
                                                        <td class="ki-summary-col">${lossMrate["loss4QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col">${lossMrate["lossK2"]}</td>
                                                        <td class="ki-summary-col">${lossMrate["lossK2Diff"]}</td>
                                                        <td class="ki-summary-col">${lossMrate["lossG"]}</td>
                                                        <td class="ki-summary-col">${lossMrate["lossGDiff"]}</td>
                                                    </tr>

                                                    <!-- ロスコン引当(今回) -->
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <c:set value="${lossData['LOSS_HIKIATE']}" var="lossHikiate"/>
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossHikiate["lossBRuikei"]}</td>
                                                        </c:if>

                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${lossHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">${lossHikiate["loss1"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${lossHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">${lossHikiate["loss2"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${lossHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">${lossHikiate["loss3"]}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${lossHikiate["loss1Q"]}</td>
                                                        <td style="width:105px" class="ki-summary-col">${lossHikiate["loss1QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${lossHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">${lossHikiate["loss4"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${lossHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">${lossHikiate["loss5"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${lossHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">${lossHikiate["loss6"]}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossHikiate["loss2Q"]}</td>
                                                        <td class="ki-summary-col">${lossHikiate["loss2QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td class="ki-summary-col">${lossHikiate["lossK1"]}</td>
                                                        <td class="ki-summary-col">${lossHikiate["lossK1Diff"]}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${lossHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">${lossHikiate["loss7"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${lossHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">${lossHikiate["loss8"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${lossHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">${lossHikiate["loss9"]}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossHikiate["loss3Q"]}</td>
                                                        <td class="ki-summary-col">${lossHikiate["loss3QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${lossHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">${lossHikiate["loss10"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${lossHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">${lossHikiate["loss11"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${lossHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">${lossHikiate["loss12"]}</td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossHikiate["loss4Q"]}</td>
                                                        <td class="ki-summary-col">${lossHikiate["loss4QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col">${lossHikiate["lossK2"]}</td>
                                                        <td class="ki-summary-col">${lossHikiate["lossK2Diff"]}</td>
                                                        <td class="ki-summary-col">${lossHikiate["lossG"]}</td>
                                                        <td class="ki-summary-col">${lossHikiate["lossGDiff"]}</td>
                                                    </tr>

                                                    <!-- ロスコン引当(累計) -->
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <c:set value="${lossData['LOSS_RUIKEI_HIKIATE']}" var="lossRuikeiHikiate"/>
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossRuikeiHikiate["lossBRuikei"]}</td>
                                                        </c:if>

                                                        <c:if test="${s004Bean.tmIndex==0}"><td class="kikan_shinko_kanjyo_diff">${lossRuikeiHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[0]}">${lossRuikeiHikiate["loss1"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td class="kikan_shinko_kanjyo_diff">${lossRuikeiHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[1]}">${lossRuikeiHikiate["loss2"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td class="kikan_shinko_kanjyo_diff">${lossRuikeiHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[2]}">${lossRuikeiHikiate["loss3"]}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${lossRuikeiHikiate["loss1Q"]}</td>
                                                        <td style="width:105px" class="ki-summary-col">${lossRuikeiHikiate["loss1QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td class="kikan_shinko_kanjyo_diff">${lossRuikeiHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[3]}">${lossRuikeiHikiate["loss4"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td class="kikan_shinko_kanjyo_diff">${lossRuikeiHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[4]}">${lossRuikeiHikiate["loss5"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td class="kikan_shinko_kanjyo_diff">${lossRuikeiHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[5]}">${lossRuikeiHikiate["loss6"]}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossRuikeiHikiate["loss2Q"]}</td>
                                                        <td class="ki-summary-col">${lossRuikeiHikiate["loss2QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td class="ki-summary-col">${lossRuikeiHikiate["lossK1"]}</td>
                                                        <td class="ki-summary-col">${lossRuikeiHikiate["lossK1Diff"]}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td class="kikan_shinko_kanjyo_diff">${lossRuikeiHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[6]}">${lossRuikeiHikiate["loss7"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td class="kikan_shinko_kanjyo_diff">${lossRuikeiHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[7]}">${lossRuikeiHikiate["loss8"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td class="kikan_shinko_kanjyo_diff">${lossRuikeiHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[8]}">${lossRuikeiHikiate["loss9"]}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossRuikeiHikiate["loss3Q"]}</td>
                                                        <td class="ki-summary-col">${lossRuikeiHikiate["loss3QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td class="kikan_shinko_kanjyo_diff">${lossRuikeiHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[9]}">${lossRuikeiHikiate["loss10"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td class="kikan_shinko_kanjyo_diff">${lossRuikeiHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[10]}">${lossRuikeiHikiate["loss11"]}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td class="kikan_shinko_kanjyo_diff">${lossRuikeiHikiate["lossTm"]}</td></c:if>
                                                        <td class="${s004Bean.classNameAry[11]}">${lossRuikeiHikiate["loss12"]}</td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td class="ki-summary-col">${lossRuikeiHikiate["loss4Q"]}</td>
                                                        <td class="ki-summary-col">${lossRuikeiHikiate["loss4QDiff"]}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td class="ki-summary-col">${lossRuikeiHikiate["lossK2"]}</td>
                                                        <td class="ki-summary-col">${lossRuikeiHikiate["lossK2Diff"]}</td>
                                                        <td class="ki-summary-col">${lossRuikeiHikiate["lossG"]}</td>
                                                        <td class="ki-summary-col">${lossRuikeiHikiate["lossGDiff"]}</td>
                                                    </tr>
                                                </table>
                                                </c:if>

                                                <br>

                                                <!-- 回収情報 -->
                                                <!-- 回収情報タイトル部(横スクロール) -->
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <colgroup>
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                            <col style="width:125px">
                                                        </c:if>
                                                        <c:forEach items="${s004Bean.title}" var="title" varStatus="s">
                                                        <col style="width:105px">
                                                        </c:forEach>
                                                    </colgroup>
                                                    <tr style="height:23px">
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                            <th><fmt:message key="zenkiruikei" bundle="${label}"/></th>
                                                        </c:if>
                                                        <c:forEach items="${s004Bean.monthTitle}" var="title" varStatus="s">
                                                            <c:choose>
                                                                <c:when test="${fn:endsWith(title, s004Bean.getKi()) || fn:endsWith(title, 'Q') || fn:contains(title, s004Bean.getTotal()) || fn:contains(title, s004Bean.getLastMikomi()) }">
                                                        <th colspan="2" class="">${fn:escapeXml(title)}</th>
                                                                </c:when>
                                                                <c:otherwise>
                                                        <th>${fn:escapeXml(title)}</th>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:forEach>
                                                    </tr>
                                                    <tr style="height:23px">
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}"><th><fmt:message key="jisseki" bundle="${label}"/></th></c:if>
                                                        <c:forEach items="${s004Bean.title}" var="title" varStatus="s">
                                                        <th class="">${fn:escapeXml(title.title)}</th>
                                                        </c:forEach>
                                                    </tr>
                                                </table>
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <!--回収管理 合計(円貨)--><%-- 2017/11/20 #072 ADD 回収Total行追加 --%>
                                                    <tr style="height:23px;"  class="fix_tb total_disp_field">
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px">${s004Bean.totalRecoveryAmountData.preKaisyuEnkaAmountTotal}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount1}</td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount2}</td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount3}</td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount1Q}</td>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount4}</td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount5}</td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount6}</td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount2Q}</td>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountK1}</td>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount7}</td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount8}</td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount9}</td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount3Q}</td>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount10}</td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount11}</td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount12}</td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount4Q}</td>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmount4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountK2}</td>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountK2Diff}</td>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountG}</td>
                                                        <td style="width:105px">${s004Bean.totalRecoveryAmountData.KaisyuEnkaAmountGDiff}</td>
                                                    </tr>
                                                </table>
                                                <!-- 回収情報 外貨額-->
                                                <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                    <c:forEach items="${s004Bean.recoveryAmountList}" var="recoveryAmountList" varStatus="s">
                                                    <c:set var="kaisyuFormatFlg" value="${s004Bean.getKaisyuForeignFlg()}"/><%-- 0:円貨表記(小数点以下無し) 1:小数点2桁(外貨通貨が混じっている場合)--%>
                                                    <c:set var="kaisyuNumberFormatClass" value="${s004Bean.getKaisyuForeignFlg() == '1' ? 'numberFormat2' : 'numberFormat'}"/><%-- numberFormat:円貨用 numberFormat2:外貨用 --%>

                                                    <input type="hidden" name="kaisyuAmountTm" value="${recoveryAmountList.KaisyuAmountTm}" />
                                                    <input type="hidden" name="inpTargetKaisyuAmountCurrencyCode" value="${recoveryAmountList.currencyCode}" />
                                                    <input type="hidden" name="inpTargetKaisyuAmountZeiKbn" value="${recoveryAmountList.zeiKbn}" />
                                                    <input type="hidden" name="inpTargetKaisyuAmountKinsyuKbn" value="${recoveryAmountList.kinsyuKbn}" />
                                                    <input type="hidden" name="inpTargetKaisyuAmountKaisyuKbn" value="${recoveryAmountList.kaisyuKbn}" />

                                                    <!--回収金額-->
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${recoveryAmountList.preKaisyuAmountTotal}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[0]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[0], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuAmount1" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuAmount1), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg1', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuAmount1}<input type="hidden" name="kaisyuAmount1" value="${recoveryAmountList.KaisyuAmount1}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:if test="${recoveryAmountList.currencyCode == s004Bean.currencyCodeEn}"><input type="hidden" name="kaisyuEnkaAmount1" value="" /></c:if>
                                                            <input type="hidden" name="inpTargetKaisyuAmountUpdateFlg1" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[1]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[1], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuAmount2" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuAmount2), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg2', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuAmount2}<input type="hidden" name="kaisyuAmount2" value="${recoveryAmountList.KaisyuAmount2}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:if test="${recoveryAmountList.currencyCode == s004Bean.currencyCodeEn}"><input type="hidden" name="kaisyuEnkaAmount2" value="" /></c:if>
                                                            <input type="hidden" name="inpTargetKaisyuAmountUpdateFlg2" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[2]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[2], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuAmount3" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuAmount3), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg3', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuAmount3}<input type="hidden" name="kaisyuAmount3" value="${recoveryAmountList.KaisyuAmount3}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:if test="${recoveryAmountList.currencyCode == s004Bean.currencyCodeEn}"><input type="hidden" name="kaisyuEnkaAmount3" value="" /></c:if>
                                                            <input type="hidden" name="inpTargetKaisyuAmountUpdateFlg3" />
                                                        </td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuAmount1Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuAmount1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[3]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[3], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuAmount4" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuAmount4), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg4', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuAmount4}<input type="hidden" name="kaisyuAmount4" value="${recoveryAmountList.KaisyuAmount4}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:if test="${recoveryAmountList.currencyCode == s004Bean.currencyCodeEn}"><input type="hidden" name="kaisyuEnkaAmount4" value="" /></c:if>
                                                            <input type="hidden" name="inpTargetKaisyuAmountUpdateFlg4" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[4]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[4], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuAmount5" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuAmount5), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg5', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuAmount5}<input type="hidden" name="kaisyuAmount5" value="${recoveryAmountList.KaisyuAmount5}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:if test="${recoveryAmountList.currencyCode == s004Bean.currencyCodeEn}"><input type="hidden" name="kaisyuEnkaAmount5" value="" /></c:if>
                                                            <input type="hidden" name="inpTargetKaisyuAmountUpdateFlg5" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[5]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[5], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuAmount6" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuAmount6), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg6', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuAmount6}<input type="hidden" name="kaisyuAmount6" value="${recoveryAmountList.KaisyuAmount6}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:if test="${recoveryAmountList.currencyCode == s004Bean.currencyCodeEn}"><input type="hidden" name="kaisyuEnkaAmount6" value="" /></c:if>
                                                            <input type="hidden" name="inpTargetKaisyuAmountUpdateFlg6" />
                                                        </td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuAmount2Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuAmount2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuAmountK1}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuAmountK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[6]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[0], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuAmount7" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuAmount7), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg7', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuAmount7}<input type="hidden" name="kaisyuAmount7" value="${recoveryAmountList.KaisyuAmount7}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:if test="${recoveryAmountList.currencyCode == s004Bean.currencyCodeEn}"><input type="hidden" name="kaisyuEnkaAmount7" value="" /></c:if>
                                                            <input type="hidden" name="inpTargetKaisyuAmountUpdateFlg7" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[7]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[1], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuAmount8" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuAmount8), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg8', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuAmount8}<input type="hidden" name="kaisyuAmount8" value="${recoveryAmountList.KaisyuAmount8}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:if test="${recoveryAmountList.currencyCode == s004Bean.currencyCodeEn}"><input type="hidden" name="kaisyuEnkaAmount8" value="" /></c:if>
                                                            <input type="hidden" name="inpTargetKaisyuAmountUpdateFlg8" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[8]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[2], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuAmount9" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuAmount9), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg9', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuAmount9}<input type="hidden" name="kaisyuAmount9" value="${recoveryAmountList.KaisyuAmount9}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:if test="${recoveryAmountList.currencyCode == s004Bean.currencyCodeEn}"><input type="hidden" name="kaisyuEnkaAmount9" value="" /></c:if>
                                                            <input type="hidden" name="inpTargetKaisyuAmountUpdateFlg9" />
                                                        </td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuAmount3Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuAmount3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[9]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[3], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuAmount10" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuAmount10), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg10', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuAmount10}<input type="hidden" name="kaisyuAmount10" value="${recoveryAmountList.KaisyuAmount10}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:if test="${recoveryAmountList.currencyCode == s004Bean.currencyCodeEn}"><input type="hidden" name="kaisyuEnkaAmount10" value="" /></c:if>
                                                            <input type="hidden" name="inpTargetKaisyuAmountUpdateFlg10" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[10]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[4], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuAmount11" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuAmount11), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg11', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuAmount11}<input type="hidden" name="kaisyuAmount11" value="${recoveryAmountList.KaisyuAmount11}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:if test="${recoveryAmountList.currencyCode == s004Bean.currencyCodeEn}"><input type="hidden" name="kaisyuEnkaAmount11" value="" /></c:if>
                                                            <input type="hidden" name="inpTargetKaisyuAmountUpdateFlg11" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[11]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[5], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuAmount12" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuAmount12), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg12', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuAmount12}<input type="hidden" name="kaisyuAmount12" value="${recoveryAmountList.KaisyuAmount12}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:if test="${recoveryAmountList.currencyCode == s004Bean.currencyCodeEn}"><input type="hidden" name="kaisyuEnkaAmount12" value="" /></c:if>
                                                            <input type="hidden" name="inpTargetKaisyuAmountUpdateFlg12" />
                                                        </td>
                                                        <%--<c:if test="${s004Bean.tmIndex==12}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuAmountTm}</td></c:if>--%>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuAmount4Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuAmount4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuAmountK2}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuAmountK2Diff}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuAmountG}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuAmountGDiff}</td>
                                                    </tr>

                                                    <!-- 回収金額 円通貨以外での円貨入力 -->
                                                    <c:if test="${recoveryAmountList.currencyCode != s004Bean.currencyCodeEn}">
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${recoveryAmountList.preKaisyuEnkaAmountTotal}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[0]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[0], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuEnkaAmount1" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuEnkaAmount1), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg1', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuEnkaAmount1}<input type="hidden" name="kaisyuEnkaAmount1" value="${recoveryAmountList.KaisyuEnkaAmount1}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[1]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[1], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuEnkaAmount2" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuEnkaAmount2), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg2', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuEnkaAmount2}<input type="hidden" name="kaisyuEnkaAmount2" value="${recoveryAmountList.KaisyuEnkaAmount2}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[2]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[2], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuEnkaAmount3" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuEnkaAmount3), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg3', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuEnkaAmount3}<input type="hidden" name="kaisyuEnkaAmount3" value="${recoveryAmountList.KaisyuEnkaAmount3}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaAmount1Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaAmount1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[3]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[3], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuEnkaAmount4" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuEnkaAmount4), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg4', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuEnkaAmount4}<input type="hidden" name="kaisyuEnkaAmount4" value="${recoveryAmountList.KaisyuEnkaAmount4}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[4]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[4], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuEnkaAmount5" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuEnkaAmount5), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg5', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuEnkaAmount5}<input type="hidden" name="kaisyuEnkaAmount5" value="${recoveryAmountList.KaisyuEnkaAmount5}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[5]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanFromAry[5], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuEnkaAmount6" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuEnkaAmount6), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg6', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuEnkaAmount6}<input type="hidden" name="kaisyuEnkaAmount6" value="${recoveryAmountList.KaisyuEnkaAmount6}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaAmount2Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaAmount2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaAmountK1}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaAmountK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[6]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[0], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuEnkaAmount7" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuEnkaAmount7), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg7', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuEnkaAmount7}<input type="hidden" name="kaisyuEnkaAmount7" value="${recoveryAmountList.KaisyuEnkaAmount7}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[7]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[1], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuEnkaAmount8" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuEnkaAmount8), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg8', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuEnkaAmount8}<input type="hidden" name="kaisyuEnkaAmount8" value="${recoveryAmountList.KaisyuEnkaAmount8}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[8]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[2], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuEnkaAmount9" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuEnkaAmount9), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg9', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuEnkaAmount9}<input type="hidden" name="kaisyuEnkaAmount9" value="${recoveryAmountList.KaisyuEnkaAmount9}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaAmount3Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaAmount3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[9]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[3], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuEnkaAmount10" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuEnkaAmount10), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg10', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuEnkaAmount10}<input type="hidden" name="kaisyuEnkaAmount10" value="${recoveryAmountList.KaisyuEnkaAmount10}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[10]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[4], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuEnkaAmount11" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuEnkaAmount11), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg11', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuEnkaAmount11}<input type="hidden" name="kaisyuEnkaAmount11" value="${recoveryAmountList.KaisyuEnkaAmount11}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaAmountTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.editFlg == '1' ? "" : s004Bean.classNameAry[11]}">
                                                            <c:choose>
                                                                <%--<c:when test="${s004Bean.editFlg == '1' && sUtil.uriageYmCompare(s004Bean.kikanToAry[5], uriageYm , 1) == 0}">--%>
                                                                <c:when test="${s004Bean.editFlg == '1'}">
                                                                    <input type="text" name="kaisyuEnkaAmount12" class="${kaisyuNumberFormatClass}" style="${numberStyle}" value="${sUtil.exeChangeDispFormatUnit(utl.changeBigDecimal(recoveryAmountList.KaisyuEnkaAmount12), kaisyuFormatFlg)}" onblur="inputEdit('inpTargetKaisyuAmountUpdateFlg12', ${s.index}, this)" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${recoveryAmountList.KaisyuEnkaAmount12}<input type="hidden" name="kaisyuEnkaAmount12" value="${recoveryAmountList.KaisyuEnkaAmount12}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaAmount4Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaAmount4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaAmountK2}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaAmountK2Diff}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaAmountG}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaAmountGDiff}</td>
                                                    </tr>
                                                    </c:if>

                                                    <!-- 回収金額 税額 --><%-- 2017/11/20 #072 ADD 回収Total行追加 --%>
                                                    <c:if test="${recoveryAmountList.zeiRate != '0'}">
                                                    <tr style="height:23px;" class="fix_tb" >
                                                        <!--期間損益画面の累計-->
                                                        <c:if test="${detailHeader.maeruikeiDispFlg == '1'}">
                                                        <td style="width:125px" class="ki-summary-col">${recoveryAmountList.preKaisyuEnkaZeiTotal}</td>
                                                        </c:if>
                                                        <c:if test="${s004Bean.tmIndex==0}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaZeiTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[0]}">
                                                            ${recoveryAmountList.KaisyuEnkaZei1}<input type="hidden" name="kaisyuEnkaZei1" value="${recoveryAmountList.KaisyuEnkaZei1}" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==1}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaZeiTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[1]}">
                                                            ${recoveryAmountList.KaisyuEnkaZei2}<input type="hidden" name="kaisyuEnkaZei2" value="${recoveryAmountList.KaisyuEnkaZei2}" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==2}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaZeiTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[2]}">
                                                            ${recoveryAmountList.KaisyuEnkaZei3}<input type="hidden" name="kaisyuEnkaZei3" value="${recoveryAmountList.KaisyuEnkaZei3}" />
                                                        </td>

                                                        <%-- 1Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaZei1Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaZei1QDiff}</td>
                                                        </c:if>
                                                        <%-- 1Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==3}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaZeiTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[3]}">
                                                            ${recoveryAmountList.KaisyuEnkaZei4}<input type="hidden" name="kaisyuEnkaZei4" value="${recoveryAmountList.KaisyuEnkaZei4}" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==4}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaZeiTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[4]}">
                                                            ${recoveryAmountList.KaisyuEnkaZei5}<input type="hidden" name="kaisyuEnkaZei5" value="${recoveryAmountList.KaisyuEnkaZei5}" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==5}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaZeiTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[5]}">
                                                            ${recoveryAmountList.KaisyuEnkaZei6}<input type="hidden" name="kaisyuEnkaZei6" value="${recoveryAmountList.KaisyuEnkaZei6}" />
                                                        </td>

                                                        <%-- 2Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaZei2Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaZei2QDiff}</td>
                                                        </c:if>
                                                        <%-- 2Q(E) --%>

                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaZeiK1}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaZeiK1Diff}</td>

                                                        <c:if test="${s004Bean.tmIndex==6}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaZeiTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[6]}">
                                                            ${recoveryAmountList.KaisyuEnkaZei7}<input type="hidden" name="kaisyuEnkaZei7" value="${recoveryAmountList.KaisyuEnkaZei7}" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==7}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaZeiTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[7]}">
                                                            ${recoveryAmountList.KaisyuEnkaZei8}<input type="hidden" name="kaisyuEnkaZei8" value="${recoveryAmountList.KaisyuEnkaZei8}" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==8}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaZeiTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[8]}">
                                                            ${recoveryAmountList.KaisyuEnkaZei9}<input type="hidden" name="kaisyuEnkaZei9" value="${recoveryAmountList.KaisyuEnkaZei9}" />
                                                        </td>

                                                        <%-- 3Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaZei3Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaZei3QDiff}</td>
                                                        </c:if>
                                                        <%-- 3Q(E) --%>

                                                        <c:if test="${s004Bean.tmIndex==9}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaZeiTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[9]}">
                                                            ${recoveryAmountList.KaisyuEnkaZei10}<input type="hidden" name="kaisyuEnkaZei10" value="${recoveryAmountList.KaisyuEnkaZei10}" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==10}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaZeiTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[10]}">
                                                            ${recoveryAmountList.KaisyuEnkaZei11}<input type="hidden" name="kaisyuEnkaZei11" value="${recoveryAmountList.KaisyuEnkaZei11}" />
                                                        </td>
                                                        <c:if test="${s004Bean.tmIndex==11}"><td style="width:105px" class="kikan_shinko_kanjyo_diff">${recoveryAmountList.KaisyuEnkaZeiTm}</td></c:if>
                                                        <td style="width:105px" class="${s004Bean.classNameAry[11]}">
                                                            ${recoveryAmountList.KaisyuEnkaZei12}<input type="hidden" name="kaisyuEnkaZei12" value="${recoveryAmountList.KaisyuEnkaZei12}" />
                                                        </td>

                                                        <%-- 4Q(S) --%>
                                                        <c:if test="${detailHeader.quarterDispFlg == '1'}">
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaZei4Q}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaZei4QDiff}</td>
                                                        </c:if>
                                                        <%-- 4Q(E) --%>

                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaZeiK2}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaZeiK2Diff}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaZeiG}</td>
                                                        <td style="width:105px" class="ki-summary-col">${recoveryAmountList.KaisyuEnkaZeiGDiff}</td>
                                                    </tr>
                                                    </c:if>
                                                    </c:forEach>
                                                </table>
                                            </div>
                                        </td>
                                    </tr>
                                </table>
                            </TD>
                        </tr>
                    </table>
                </div>
            </div>  <!-- search-result -->

            <input type="hidden" name="uriageRangeNormalFlg" id="uriageRangeNormalFlg" value="${fn:escapeXml(detailHeader.uriageRangeNormalFlg)}"/>
            <input type="hidden" name="updateNewDataKbn" value="" id="updateNewDataKbn" />
            <input type="hidden" name="kanbaiYm" value="${uriageYm}" /> <!-- 完売月 -->
            </form>
            </div>
        </div>  <!-- container -->
    </div>  <!-- wrap -->

    <c:import url="${componentFullPath}jslink.xhtml" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/view/s004.js?var=${utl.random()}"></script>

    </body>
</html>
