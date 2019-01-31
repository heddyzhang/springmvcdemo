<!DOCTYPE html>
<html lang="ja">
    <head>
        <c:import url="${componentPath}metainfo.xhtml" />
        <title><fmt:message key="applicationName" bundle="${label}"/></title>
        <c:import url="${componentFullPath}csslink.xhtml" />
        <style>
            .btn-M-Delete, .btn-M-Add {
                font-size:10px;
                height:18px;
                width:30px;
                vertical-align:middle;
                display:inline-block;
                margin:0px;
                background: #fff;
                color: #000;
                cursor: pointer;
                border: solid 1px #d3d3d3;
                text-align:center;
            }

            .btn-M-Delete:hover {
                background: gainsboro;
            }

            .btn-M-Delete:active {
                background: gainsboro;
                border: inset 1px #d3d3d3;
            }

            input[readonly] {
                background-color: #c0c0c0;
            }

            tr[data-row-disp-Flg='0'] {
                display: none !important;
            }
        </style>
    </head>
    <body>
        <%-- 変数定義 --%>
        <c:set value="width:211px" var="tdWidth2Span"/>
        <c:set value="width:105px" var="tdWidth"/>
        <c:set value="width:100px" var="numberStyle"/>
        <c:set value="" var="bRowSpanKeyInfo" />
        <c:if test="${s003Bean.editFlg != '1'}">
            <c:set value="height:22px;" var="tdHeight" />
        </c:if>
        <c:if test="${s003Bean.editFlg == '1'}">
            <c:set value="height:23px;" var="tdHeight" />
        </c:if>
        <%-- 編集ボタンの表示権限 --%>
        <c:set value="${s003Bean.editAuthFlg == '1' && authUtl.enableFlg('KIKAN_I_EDIT', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}" var="dispEditButtonFlg" />

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
                <c:param name="id" value="S003" />
            </c:import>

            <!-- 検索結果 -->
            <div class="search-result">
            <input type="hidden" name="editFlg" id="editFlg" value="${s003Bean.editFlg}" />

        <!-- ボタン -->
        <%-- 参照モード時のボタン表示(S) --%>
        <c:if test="${s003Bean.editFlg != '1'}">
        <div class="search-result_btn" style="margin:10px 0px">
            <c:if test="${dispEditButtonFlg}"><%-- 編集ボタンは案件チームがログイン者チームと一致している場合のみ --%>
            <div class="btn-group" id="hensyuArea" style="${detailHeader.deleteFlg == '2' ? 'display:none' : ''}">
                <button type="button" class="btn btn-default" onclick="edit()" ><fmt:message key="edit" bundle="${label}"/>&nbsp;</button>
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${authUtl.enableFlg('KIKAN_I_RIREKI', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group">
                <button type="button" class="btn btn-default" onclick="return openSelectHistory(document.getElementById('mainForm'));"><fmt:message key="history" bundle="${label}"/><fmt:message key="disp" bundle="${label}"/>&nbsp;</button>
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${authUtl.enableFlg('KIKAN_I_INFOEDIT', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group">
                <button type="button" class="btn btn-default" onclick="return syuekiInfo('<fmt:message key="forceSave" bundle="${label}"/>')" ><fmt:message key="detailInfo" bundle="${label}"/>&nbsp;</button>
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${authUtl.enableFlg('KIKAN_I_FUNC', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group">
                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"><fmt:message key="kino" bundle="${label}"/>&nbsp;<span class="caret"></span></button>
                <ul class="dropdown-menu" role="menu" style="width:180px;">

                    <li id="jpyUnitSelect">
                        <a href="javascript:void(0)" onclick="return false"><fmt:message key="jpyUnitLabel" bundle="${label}"/><fmt:message key="unitLabel" bundle="${label}"/><fmt:message key="change" bundle="${label}"/></a>
                        <ul>
                            <li><a href="javascript:void(0);" id="jpyUnit1"><fmt:message key="jpyUnit1" bundle="${label}"/></a></li>
                            <li><a href="javascript:void(0);" id="jpyUnit2"><fmt:message key="jpyUnit2" bundle="${label}"/></a></li>
                            <li><a href="javascript:void(0);" id="jpyUnit3"><fmt:message key="jpyUnit3" bundle="${label}"/></a></li>
                        </ul>
                        <input type="hidden" name="jpyUnit" value="${fn:escapeXml(s003Bean.jpyUnit)}" id="jpyUnit" />
                    </li>
                    <%-- 見込SP(通貨)、NETカテゴリ編集は案件の編集権限が存在する場合のみ表示 --%>
                    <%--<c:if test="${s003Bean.editAuthFlg == '1' && (empty s003Bean.rirekiFlg)}">--%>
                    <c:if test="${dispEditButtonFlg}">
                    <div class="btn-group" id="kinouArea" style="${detailHeader.deleteFlg == '2' ? 'display:none' : ''}">
                    <li class="divider"></li>
                    <li>
                        <ul>
                            <li><a href="javascript:void(0);" onclick="return exeOpenCurEdit('<fmt:message key="forceSave" bundle="${label}"/>');" id="CurEditOpen"><fmt:message key="dispMikomiSpCurEdit" bundle="${label}"/></a></li>
                            <li><a href="javascript:void(0);" onclick="return exeOpenUriageChosei('<fmt:message key="forceSave" bundle="${label}"/>');" id="UriageChouseiOpen"><fmt:message key="net" bundle="${label}"/><fmt:message key="category" bundle="${label}"/><fmt:message key="edit" bundle="${label}"/></a></li>
                        </ul>
                    </li>
                    </div><!-- btn-group -->
                    </c:if>
                </ul>
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${s003Bean.editAuthFlg == '1' && authUtl.enableFlg('KIKAN_I_UPDATE', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group"><%-- 最新値更新 --%>
                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" ><fmt:message key="newestValueSave" bundle="${label}"/>&nbsp;<span class="caret"></span></button>
                <ul class="dropdown-menu" role="menu">
                    <li><a href="javascript:void(0)" id="newest1" onclick="updateNewData('<fmt:message key="confirmNewDataHb" bundle="${label}"/>', 1);"><fmt:message key="hatsuban" bundle="${label}"/><fmt:message key="update" bundle="${label}"/></a></li>
                    <li><a href="javascript:void(0)" id="newest2" onclick="updateNewData('<fmt:message key="confirmNewDataKeiyaku" bundle="${label}"/>', 2);"><fmt:message key="juchuTuti" bundle="${label}"/><fmt:message key="update" bundle="${label}"/></a></li>
                    <c:if test="${divisonComponentPage.isNuclearDivision(detailHeader.divisionCode)}"><%-- 見積更新は[原子力]のみ利用するため、他事業部では表示されないようにする --%>
                    <li><a href="javascript:void(0)" id="newest3" onclick="updateNewData('<fmt:message key="confirmNewDataMit" bundle="${label}"/>', 3);"><fmt:message key="mit" bundle="${label}"/><fmt:message key="update" bundle="${label}"/></a></li>
                    </c:if>
                </ul>
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${s003Bean.editAuthFlg == '1' && detailHeader.ankenEntity.ispKbn != '0' && authUtl.enableFlg('KIKAN_I_ISP', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group">
                <button type="button" class="btn btn-default" onclick="return ispJisseki('<fmt:message key="confirmSave" bundle="${label}"/>');"><fmt:message key="ispParformance" bundle="${label}"/></button>
            </div><!-- btn-group -->
            </c:if>
            <%-- 白地調整案件 取消/復活ボタン(S) ※該当案件が白地調整案件の場合のみ表示 --%>
            <c:if test="${detailHeader.shirajiFlg == '1' && s003Bean.editAuthFlg == '1'}">
            <c:if test="${authUtl.enableFlg('KIKAN_I_FUKKATSU', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group" id="shirajiFukkatsuArea" style="${detailHeader.deleteFlg == '2' ? '' : 'display:none'}">
                <input type="button" id="shirajiFukkatsu" class="btn btn-default" value="<fmt:message key="fukkatsu" bundle="${label}"/>">
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${authUtl.enableFlg('KIKAN_I_TORIKESHI', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group" id="shirajiTorikeshiArea" style="${detailHeader.deleteFlg == '2' ? 'display:none' : ''}">
                <input type="button" id="shirajiTorikeshi" class="btn btn-default" value="<fmt:message key="torikeshi" bundle="${label}"/>">
            </div><!-- btn-group -->
            </c:if>
            </c:if>
            <%-- 白地調整案件 取消/復活ボタン(E) --%>
            <c:if test="${authUtl.enableFlg('KIKAN_I_BOOK_ON', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group" id="addBookMarkArea" style="display:none">
                <input type="button" id="addBookMark" class="btn btn-default" value="<fmt:message key="addBookMark" bundle="${label}"/>">
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${authUtl.enableFlg('KIKAN_I_BOOK_OFF', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group" id="delBookMarkArea" style="display:none">
                <input type="button" id="delBookMark" class="btn btn-default" value="<fmt:message key="delBookMark" bundle="${label}"/>">
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${authUtl.enableFlg('KIKAN_I_HELP', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group">
                <c:import url="${componentPath}helplink.xhtml"/>
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${authUtl.enableFlg('KIKAN_I_CLOSE', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group">
                <button type="button" class="btn btn-default" id="close" onclick="window.close()" ><fmt:message key="close" bundle="${label}"/>&nbsp;</button>
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${authUtl.enableFlg('KIKAN_I_ATTACH', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group"><%-- 添付資料 --%>
                <button type="button" class="btn btn-default" onclick="return exeOpenAttachInfo('<fmt:message key="forceSave" bundle="${label}"/>');"><fmt:message key="dispNameAttachment" bundle="${label}"/>&nbsp;</button>
            </div><!-- btn-group -->
            </c:if>

            <%-- (原子力)関連システムへのリンクボタン --%>
            <c:import url="${componentPath}bprSysLinkButton.xhtml" >
                <c:param name="ankenId" value="${detailHeader.ankenId}"/>
                <c:param name="rirekiId" value="${detailHeader.rirekiId}"/>
                <c:param name="rirekiFlg" value="${detailHeader.rirekiFlg}"/>
                <c:param name="procId" value="S003"/>
                <c:param name="marginFlg" value="1"/>
            </c:import>
        </div>
        </c:if>

        <%-- 編集モード時のボタン表示(S) --%>
        <c:if test="${s003Bean.editFlg == '1'}">
        <div class="search-result_btn" style="margin:10px 0px">
            <c:if test="${authUtl.enableFlg('KIKAN_I_SAVE', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group">
                <button type="button" class="btn btn-default" onclick="save('<fmt:message key="confirmSave" bundle="${label}"/>', '<fmt:message key="notEdit" bundle="${label}"/>')" ><fmt:message key="save" bundle="${label}"/>&nbsp;</button>
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${authUtl.enableFlg('KIKAN_I_BALANCE', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group">
                <button type="button" class="btn btn-default" onclick="return exeJuchuUriBalance('<fmt:message key="confirmJuchuUriBarance" bundle="${label}"/>')" ><fmt:message key="juchuUriBalance" bundle="${label}"/>&nbsp;</button>
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${authUtl.enableFlg('MRATE_UPDATE', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group">
                <button type="button" class="btn btn-default" onclick="return exeRateReflection('<fmt:message key="confirmRateReflection" bundle="${label}"/>')" ><fmt:message key="rateReflection" bundle="${label}"/>&nbsp;</button>
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${authUtl.enableFlg('KIKAN_I_INFOEDIT', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group">
                <button type="button" class="btn btn-default" onclick="return syuekiInfo('<fmt:message key="forceSave" bundle="${label}"/>')" ><fmt:message key="detailInfo" bundle="${label}"/>&nbsp;</button>
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${authUtl.enableFlg('KIKAN_I_FUNC', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1 && s003Bean.editAuthFlg == '1' && (empty s003Bean.rirekiFlg)}"><%-- 見込SP(通貨)、調整口追加は案件チームがログイン者チームと一致している場合のみ --%>
            <div class="btn-group">
                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" ><fmt:message key="kino" bundle="${label}"/>&nbsp;<span class="caret"></span></button>
                <ul class="dropdown-menu" role="menu" style="width:180px;">
                    <li class="divider"></li>
                    <li>
                        <ul>
                            <li><a href="javascript:void(0);" onclick="return exeOpenCurEdit('<fmt:message key="forceSave" bundle="${label}"/>');" id="CurEditOpen"><fmt:message key="dispMikomiSpCurEdit" bundle="${label}"/></a></li>
                            <li><a href="javascript:void(0);" onclick="return exeOpenUriageChosei('<fmt:message key="forceSave" bundle="${label}"/>');" id="UriageChouseiOpen"><fmt:message key="net" bundle="${label}"/><fmt:message key="category" bundle="${label}"/><fmt:message key="edit" bundle="${label}"/></a></li>
                            <c:if test="${authUtl.enableFlg('KIKAN_I_KAISYU_EDIT', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
                            <li><a href="javascript:void(0);" onclick="return exeOpenKaisyuEdit('<fmt:message key="forceSave" bundle="${label}"/>');" id="KaisyuEditOpen"><fmt:message key="dispNameKaisyuEdit" bundle="${label}"/></a></li>
                            </c:if>
                        </ul>
                    </li>
                </ul>
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${authUtl.enableFlg('KIKAN_I_UPDATE', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group"><%-- 最新値更新 --%>
                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" ><fmt:message key="newestValueSave" bundle="${label}"/>&nbsp;<span class="caret"></span></button>
                <ul class="dropdown-menu" role="menu">
                    <li><a href="javascript:void(0)" id="newest1" onclick="updateNewData('<fmt:message key="confirmNewDataHb" bundle="${label}"/>', 1);"><fmt:message key="hatsuban" bundle="${label}"/><fmt:message key="update" bundle="${label}"/></a></li>
                    <li><a href="javascript:void(0)" id="newest2" onclick="updateNewData('<fmt:message key="confirmNewDataKeiyaku" bundle="${label}"/>', 2);"><fmt:message key="juchuTuti" bundle="${label}"/><fmt:message key="update" bundle="${label}"/></a></li>
                    <c:if test="${divisonComponentPage.isNuclearDivision(detailHeader.divisionCode)}"><%-- 見積更新は[原子力]のみ利用するため、他事業部では表示されないようにする --%>
                    <li><a href="javascript:void(0)" id="newest3" onclick="updateNewData('<fmt:message key="confirmNewDataMit" bundle="${label}"/>', 3);"><fmt:message key="mit" bundle="${label}"/><fmt:message key="update" bundle="${label}"/></a></li>
                    </c:if>
                </ul>
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${s003Bean.editAuthFlg == '1' && detailHeader.ankenEntity.ispKbn != '0' && authUtl.enableFlg('KIKAN_I_ISP', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group">
                <button type="button" class="btn btn-default" onclick="return ispJisseki('<fmt:message key="confirmSave" bundle="${label}"/>');"><fmt:message key="ispParformance" bundle="${label}"/></button>
            </div><!-- btn-group -->
            </c:if>
            <div class="btn-group">
                <button type="button" class="btn btn-default" onclick="cancel('<fmt:message key="confirmEditCancel" bundle="${label}"/>');"><fmt:message key="cancel" bundle="${label}"/>&nbsp;</button>
            </div><!-- btn-group -->
            <c:if test="${authUtl.enableFlg('KIKAN_I_HELP', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group">
                <c:import url="${componentPath}helplink.xhtml"/>
            </div><!-- btn-group -->
            </c:if>
            <c:if test="${authUtl.enableFlg('KIKAN_I_ATTACH', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}">
            <div class="btn-group"><%-- 添付資料 --%>
                <button type="button" class="btn btn-default" onclick="return exeOpenAttachInfo('<fmt:message key="forceSave" bundle="${label}"/>');"><fmt:message key="dispNameAttachment" bundle="${label}"/>&nbsp;</button>
            </div><!-- btn-group -->
            </c:if>

            <%-- (原子力)関連システムへのリンクボタン --%>
            <c:import url="${componentPath}bprSysLinkButton.xhtml" >
                <c:param name="ankenId" value="${detailHeader.ankenId}"/>
                <c:param name="rirekiId" value="${detailHeader.rirekiId}"/>
                <c:param name="rirekiFlg" value="${detailHeader.rirekiFlg}"/>
                <c:param name="procId" value="S003"/>
                <c:param name="marginFlg" value="1"/>
            </c:import>
        </div>
        </c:if>

        <div align="left" id="data_haed">
            <table id="head2table" style="width:100%">
                <tr>
                    <td style="width:90%">
                        <c:import url="${componentPath}detailInfoHistory.xhtml"/>
                    </td>
                    <td style="width:10%; text-align:right" valign="bottom">
                        <fmt:message key="jpyUnitLabel" bundle="${label}"/><fmt:message key="unitLabel" bundle="${label}"/>：
                        <c:if test="${s003Bean.jpyUnit == 1}"><fmt:message key="jpyUnit1" bundle="${label}"/></c:if>
                        <c:if test="${s003Bean.jpyUnit == 1000}"><fmt:message key="jpyUnit2" bundle="${label}"/></c:if>
                        <c:if test="${s003Bean.jpyUnit == 1000000}"><fmt:message key="jpyUnit3" bundle="${label}"/></c:if>&nbsp;&nbsp;
                    </td>
                </tr>
            </table>
        </div><!-- data_haed -->

 <%--
        <div style="text-align:right">
            <fmt:message key="jpyUnitLabel" bundle="${label}"/><fmt:message key="unitLabel" bundle="${label}"/>：
            <c:if test="${s003Bean.jpyUnit == 1}"><fmt:message key="jpyUnit1" bundle="${label}"/></c:if>
            <c:if test="${s003Bean.jpyUnit == 1000}"><fmt:message key="jpyUnit2" bundle="${label}"/></c:if>
            <c:if test="${s003Bean.jpyUnit == 1000000}"><fmt:message key="jpyUnit3" bundle="${label}"/></c:if>&nbsp;&nbsp;
        </div>
--%>

<%--
        <div style="text-align:left">
            <a href="javascript:void(0)" id="ListAllOpen"><span class="tree-text">＋</span>:<fmt:message key="allTenkai" bundle="${label}"/></a>
            <a href="javascript:void(0)" id="ListAllClose"><span class="tree-text">－</span>:<fmt:message key="allKanjyo" bundle="${label}"/></a>
        </div>
--%>
        <div id="search-result-div">
            <table border=0 id="search-result-data2">
		<tr>
                    <td valign="top" >
                        <div align=left style="height:200px;overflow-x:hidden;overflow-y:hidden;" id="all_line">
                            <table  border="0">
                                <tr>
                                    <td style="vertical-align:top;">
<%--
                                        <div id="Head1" style="width:185px;overflow-x:hidden;overflow-y:hidden;">
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid" id="search-result-data">
                                                <tr>
                                                    <td colspan="2" style="border-style: none ;" class="subTitle"><fmt:message key="uriageManagement" bundle="${label}"/></td>
                                                </tr>
                                                <tr>
                                                    <th style="width:90px"><fmt:message key="kanjoYm" bundle="${label}"/></th>
                                                    <td style="width:92px" ><fmt:formatDate value="${detailHeader.kanjoDate}" pattern="yyyy/MM" /></td>
                                                </tr>
                                            </table>
                                        </div>
--%>
                                        <div id="Head1" style="width:185px;overflow-x:hidden;overflow-y:hidden; height:21px;">
                                            <a href="javascript:void(0)" id="ListAllOpen"><span class="tree-text">＋</span>:<fmt:message key="allTenkai" bundle="${label}"/></a>
                                            <a href="javascript:void(0)" id="ListAllClose"><span class="tree-text">－</span>:<fmt:message key="allKanjyo" bundle="${label}"/></a>
                                        </div>
                                        <div id="List1" style="height:213px;width:516px;overflow-x:hidden;overflow-y:hidden;" onMouseWheel="return leftScroll(this);"  onscroll="List2.scrollTop=this.scrollTop;">
                                            <%-- 受注管理情報(左)　レンダリングStart --%>
                                            <!-- 受注管理Table(左上) -->
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" data-table-kanri="juchu">
                                                <tr style="${tdHeight};">
                                                    <th style="width:90px"><fmt:message key="kanjoYm" bundle="${label}"/></th>
                                                    <td style="width:92px" ><span id="kanjoDate"><fmt:formatDate value="${detailHeader.kanjoDate}" pattern="yyyy/MM" /></span></td>
                                                    <!-- 固定項目 -->
                                                    <th style="${tdWidth}"><fmt:message key="lastMikomi" bundle="${label}"/></th>
                                                    <th style="${tdWidth}"><fmt:message key="hatsubanJisseki" bundle="${label}"/></th>
                                                    <th style="${tdWidth}"><fmt:message key="keiyakuJisseki" bundle="${label}"/></th>
                                                </tr>
                                                <tr>
                                                    <td colspan="2" class="subTitle"><fmt:message key="juchuManagement" bundle="${label}"/></td>
                                                    <!-- 固定項目 -->
                                                    <th style="${tdWidth}"><%-- 受注管理のSPとNET連動する場合は文言変更 --%>
                                                        <c:if test="${detailHeader.hatLinkSpNetFlg == '1'}"><span style="color:yellow"><fmt:message key="hatAutoRef" bundle="${label}"/></span></c:if>
                                                        <c:if test="${detailHeader.hatLinkSpNetFlg != '1'}"><fmt:message key="mikomi" bundle="${label}"/></c:if>
                                                    </th>
                                                    <th style="${tdWidth}"><fmt:message key="jisseki" bundle="${label}"/></th>
                                                    <th style="${tdWidth}"><fmt:message key="jisseki" bundle="${label}"/></th>
                                                </tr>
                                            </table>
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" data-table-kanri="juchu">
                                                <colgroup>
                                                    <col style="width:60px">
                                                    <col style="width:30px">
                                                    <col style="width:30px">
                                                    <col style="width:62px">
                                                    <!-- 固定項目 -->
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                </colgroup>
                                                <!-- 受注SP 左 合計 -->
                                                <tr style="height:20px;" class="total_disp_field" data-total-row="1" data-total-type="sp">
                                                    <th colspan="3" class="total_disp_field" style="text-align:left;">
                                                        <a href="javascript:void(0)" id="juchuSpListOpen" class="tree-text">－</a>&nbsp;<fmt:message key="juchuSp" bundle="${label}"/>(<fmt:message key="hatSp" bundle="${label}"/>)
                                                        <input type="hidden" name="juchuSpListOpenFlg" value="${fn:escapeXml(s003Bean.juchuSpListOpenFlg)}" id="juchuSpListOpenFlg"/>
                                                    </th>
                                                    <th align="center" class="total_disp_field"><fmt:message key="total" bundle="${label}"/>(<fmt:message key="enka" bundle="${label}"/>)</th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb">
                                                        <span class="jyuchuSpCurF_Total">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_SP_F"], s003Bean.jpyUnitKbn)}</span>
                                                        <input type="hidden" name="jyuchuSpCurF_Total" data-type-name="finalMikomi" value="${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_SP_F"], s003Bean.jpyUnitKbn)}" />
                                                    </td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="fix_tb">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_SP_H"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 発番実績(K) -->
                                                    <td class="fix_tb">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_SP_K"], s003Bean.jpyUnitKbn)}</td>
                                                </tr>

                                                <!-- 受注レート 左(通貨毎にloopでレンダリング) -->
                                                <c:set value="1" var="rateTitleDispFlg" />
                                                <c:forEach items="${s003Bean.targetCurrencyList}" var="currencyCode" varStatus="s">
                                                <c:set value="${currencyCode == s003Bean.currencyCodeEn ? '0' : '1'}" var="rowDispFlg" /><%-- 画面見やすくするため、通貨JPYの場合は行を非表示にする --%>
                                                <c:set value="${s003Bean.jyuchuSpInfo[currencyCode]}" var="jyuchuSpInfo" />
                                                <tr style="${tdHeight}" data-currency-code="${fn:escapeXml(currencyCode)}" data-rate="1" data-row-disp-Flg="${rowDispFlg}">
                                                    <th colspan ="2" style="border-bottom-style:hidden;border-top-style:hidden;" valign="top">
                                                        <c:if test="${rateTitleDispFlg == '1' and rowDispFlg == '1'}">
                                                        <fmt:message key="juchuRate" bundle="${label}"/>
                                                        <c:set value="0" var="rateTitleDispFlg" />
                                                        </c:if>
                                                    </th>
                                                    <th colspan ="2">
                                                        ${fn:escapeXml(currencyCode)}<input type="hidden" name="jyuchuCurF" value="${fn:escapeXml(currencyCode)}" />
                                                    </th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb">
                                                        <c:if test="${s003Bean.editFlg == '1'}"><%--編集モード時(入力可能)--%>
                                                        <input type="text" name="jyuchuSpRateCurF" data-currency-code="${fn:escapeXml(currencyCode)}" data-type-name="finalRate" data-rate="1" data-total-name="jyuchuSpCurF_Total" class="rateFormat" style="${numberStyle}" value="${sUtil.exeFormatRateString(jyuchuSpInfo["JYUCHU_RATE_F"], currencyCode)}" />
                                                        </c:if>
                                                        <c:if test="${s003Bean.editFlg != '1'}"><%--参照モード時--%>
                                                        ${sUtil.exeFormatRateString(jyuchuSpInfo["JYUCHU_RATE_F"], currencyCode)}
                                                        </c:if>
                                                    </td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="fix_tb summary-col">${sUtil.exeFormatRateString(jyuchuSpInfo["JYUCHU_RATE_H"], currencyCode)}</td>
                                                    <!-- 発番実績(K) -->
                                                    <td class="fix_tb summary-col">${sUtil.exeFormatRateString(jyuchuSpInfo["JYUCHU_RATE_K"], currencyCode)}</td>
                                                </tr>
                                                </c:forEach>

                                                <!-- 受注金額 左(通貨毎にloopでレンダリング) -->
                                                <c:forEach items="${s003Bean.targetCurrencyList}" var="currencyCode" varStatus="s">
                                                <c:set value="${s003Bean.jyuchuSpInfo[currencyCode]}" var="jyuchuSpInfo" />
                                                <tr style="${tdHeight}" data-currency-code="${fn:escapeXml(currencyCode)}" data-barance-check-kbn="sp-total" data-sp-net-utiwake="1">
                                                    <c:if test="${s.first}">
                                                    <th colspan ="2" rowspan="${s003Bean.targetCurrencyList.size()}" valign="top"><fmt:message key="juchuGaku" bundle="${label}"/></th>
                                                    </c:if>
                                                    <th colspan ="2">${fn:escapeXml(currencyCode)}</th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb">
                                                        <c:if test="${s003Bean.editFlg == '1'}"><%--編集モード時(入力可能)--%>
                                                            <input type="text" name="jyuchuSpCurF" data-type-name="finalMikomi" data-sp-net-utiwake="1" data-currency-code="${fn:escapeXml(currencyCode)}" data-total-name="jyuchuSpCurF_Total" class="numberFormat" style="${numberStyle}" value="${sUtil.changeDispCurrencyFormat(jyuchuSpInfo["JYUCHU_SP_F"], s003Bean.jpyUnitKbn, currencyCode)}" />
                                                        </c:if>
                                                        <c:if test="${s003Bean.editFlg != '1'}"><%--参照モード時--%>
                                                            ${sUtil.changeDispCurrencyFormat(jyuchuSpInfo["JYUCHU_SP_F"], s003Bean.jpyUnitKbn, currencyCode)}
                                                        </c:if>
                                                    </td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="fix_tb summary-col">${sUtil.changeDispCurrencyFormat(jyuchuSpInfo["JYUCHU_SP_H"], s003Bean.jpyUnitKbn, currencyCode)}</td>
                                                    <!-- 契約実績(K) -->
                                                    <td class="fix_tb summary-col">${sUtil.changeDispCurrencyFormat(jyuchuSpInfo["JYUCHU_SP_K"], s003Bean.jpyUnitKbn, currencyCode)}</td>
                                                </tr>
                                                </c:forEach>
                                            </table>
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" data-table-kanri="juchu">
                                                <colgroup>
                                                    <col style="width:60px">
                                                    <col style="width:60px">
                                                    <col style="width:62px">
                                                    <!-- 固定項目 -->
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                </colgroup>
                                                <!-- 受注NET 左 合計 -->
                                                <tr style="height:20px;" class="total_disp_field" data-total-row="1" data-total-type="net" data-barance-check-kbn="net-total">
                                                    <th colspan="2" class="total_disp_field">
                                                        <a href="javascript:void(0)" id="juchuNetListOpen" class="tree-text">－</a>&nbsp;<fmt:message key="juchuNet" bundle="${label}"/>(<fmt:message key="hatsubanNet" bundle="${label}"/>)
                                                        <input type="hidden" name="juchuNetListOpenFlg" value="${fn:escapeXml(s003Bean.juchuNetListOpenFlg)}" id="juchuNetListOpenFlg"/>
                                                    </th>
                                                    <th align="center" class="total_disp_field"><fmt:message key="total" bundle="${label}"/>(<fmt:message key="enka" bundle="${label}"/>)</th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb">
                                                        <span class="jyuchuNetF_Total">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_NET_F"], s003Bean.jpyUnitKbn)}</span>
                                                        <input type="hidden" name="jyuchuNetF_Total" data-type-name="finalMikomi" value="${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_NET_F"], s003Bean.jpyUnitKbn)}" />
                                                    </td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="fix_tb">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_NET_H"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 契約実績(K) -->
                                                    <td class="fix_tb">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_NET_K"], s003Bean.jpyUnitKbn)}</td>
                                                </tr>
                                                <!-- 受注NET・内訳(一括見込NET) -->
                                                <tr style="${tdHeight}" data-sp-net-utiwake="1">
                                                    <th colspan="3">
                                                        ${fn:escapeXml(s003Bean.jyuchuNetInfo["CATEGORY_NAME1"])}<%--<fmt:message key="ikkatsuMikomiNet" bundle="${label}"/>--%>
                                                        <input type="hidden" name="jyuchuNetCategoryCodeF" value="${fn:escapeXml(s003Bean.jyuchuNetInfo["CATEGORY_CODE"])}"/>
                                                        <input type="hidden" name="jyuchuNetCategoryKbn1F" value="${fn:escapeXml(s003Bean.jyuchuNetInfo["CATEGORY_KBN1"])}"/>
                                                        <input type="hidden" name="jyuchuNetCategoryKbn2F" value="${fn:escapeXml(s003Bean.jyuchuNetInfo["CATEGORY_KBN2"])}"/>
                                                    </th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb">
                                                        <c:if test="${s003Bean.editFlg == '1'}"><%--編集モード時(入力可能)--%>
                                                            <input type="text" name="jyuchuNetF" data-type-name="finalMikomi" data-sp-net-utiwake="1" data-total-name="jyuchuNetF_Total" class="numberFormat" style="${numberStyle}" value="${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuNetInfo["JYUCHU_NET_F"], s003Bean.jpyUnitKbn)}" />
                                                        </c:if>
                                                        <c:if test="${s003Bean.editFlg != '1'}"><%--参照モード時--%>
                                                            ${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuNetInfo["JYUCHU_NET_F"], s003Bean.jpyUnitKbn)}
                                                        </c:if>
                                                    </td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="fix_tb summary-col">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuNetInfo["JYUCHU_NET_H"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 契約実績(K) -->
                                                    <td class="fix_tb summary-col">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuNetInfo["JYUCHU_NET_K"], s003Bean.jpyUnitKbn)}</td>
                                                </tr>
                                            </table>
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" data-table-kanri="juchu">
                                                <colgroup>
                                                    <col style="width:60px">
                                                    <col style="width:60px">
                                                    <col style="width:62px">
                                                    <!-- 固定項目 -->
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                </colgroup>
                                                <!-- 受注管理・粗利 -->
                                                <tr style="height:20px;" class="total_disp_field" data-arari="juchu">
                                                    <th colspan="2" class="total_disp_field"><fmt:message key="arari" bundle="${label}"/></th>
                                                    <th align="center" class="total_disp_field"></th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb">${sUtil.changeDispCurrencyFormat(sUtil.arariString(s003Bean.jyuchuInfoTotal["JYUCHU_SP_F"], s003Bean.jyuchuInfoTotal["JYUCHU_NET_F"]), s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="fix_tb">${sUtil.changeDispCurrencyFormat(sUtil.arariString(s003Bean.jyuchuInfoTotal["JYUCHU_SP_H"], s003Bean.jyuchuInfoTotal["JYUCHU_NET_H"]), s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 発番実績(K) -->
                                                    <td class="fix_tb"></td>
                                                </tr>
                                                <!-- 受注管理・Ｍ率 -->
                                                <tr style="height:20px;" class="total_disp_field" data-mrate="juchu">
                                                    <th colspan="2" class="total_disp_field"><fmt:message key="mrate" bundle="${label}"/></th>
                                                    <th align="center" class="total_disp_field"></th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb">${sUtil.mrate(s003Bean.jyuchuInfoTotal["JYUCHU_SP_F"], s003Bean.jyuchuInfoTotal["JYUCHU_NET_F"])}</td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="fix_tb">${sUtil.mrate(s003Bean.jyuchuInfoTotal["JYUCHU_SP_H"], s003Bean.jyuchuInfoTotal["JYUCHU_NET_H"])}</td>
                                                    <!-- 発番実績(K) -->
                                                    <td class="fix_tb"></td>
                                                </tr>
                                            </table>

                                            <table class="search-result-data">
                                                <tr style="height:21px">
                                                    <td></td>
                                                </tr>
                                            </table>

                                            <%-- 売上管理情報(左)　レンダリングStart --%>
                                            <!-- 売上管理Table(左上) -->
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" data-table-kanri="uriage">
                                                <tr style="${tdHeight};">
                                                    <th style="width:90px"><fmt:message key="kanjoYm" bundle="${label}"/></th>
                                                    <td style="width:92px" ><fmt:formatDate value="${detailHeader.kanjoDate}" pattern="yyyy/MM" /></td>
                                                    <!-- 固定項目 -->
                                                    <th style="${tdWidth}"><fmt:message key="lastMikomi" bundle="${label}"/></th>
                                                    <th style="${tdWidth}"><fmt:message key="hatsubanJisseki" bundle="${label}"/></th>
                                                    <th style="${tdWidth}"><fmt:message key="keiyakuJisseki" bundle="${label}"/></th>
                                                </tr>
                                                <tr>
                                                    <td colspan="2" class="subTitle"><fmt:message key="uriageManagement" bundle="${label}"/></td>
                                                    <!-- 固定項目 -->
                                                    <th style="${tdWidth}"><%-- 受注管理のSPとNET連動する場合は文言変更 --%>
                                                        <c:if test="${detailHeader.hatLinkSpNetFlg == '1'}"><span style="color:yellow"><fmt:message key="hatAutoRef" bundle="${label}"/></span></c:if>
                                                        <c:if test="${detailHeader.hatLinkSpNetFlg != '1'}"><fmt:message key="mikomi" bundle="${label}"/></c:if>
                                                    </th>
                                                    <th style="${tdWidth}"><fmt:message key="jisseki" bundle="${label}"/></th>
                                                    <th style="${tdWidth}"><fmt:message key="jisseki" bundle="${label}"/></th>
                                                </tr>
<%--
                                                <tr>
                                                    <th style="width:90px"><fmt:message key="kanjoYm" bundle="${label}"/></th>
                                                    <td style="width:92px" ><fmt:formatDate value="${detailHeader.kanjoDate}" pattern="yyyy/MM" /></td>
                                                </tr>
--%>
                                            </table>
                                            <!-- 売上管理TBL(左下) -->
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" data-table-kanri="uriage">
                                                <colgroup>
                                                    <col style="width:60px">
                                                    <col style="width:30px">
                                                    <col style="width:30px">
                                                    <col style="width:62px">
                                                    <!-- 固定項目 -->
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                </colgroup>
                                                <!-- 売上高 合計 -->
                                                <c:set value="${s003Bean.uriageSpTotalInfo}" var="uriageSpTotalInfo" />
                                                <tr style="height:20px;" class="total_disp_field" data-total-row="1" data-total-type="sp">
                                                    <th colspan="3" class="total_disp_field" style="text-align:left;">
                                                        <a href="javascript:void(0)" id="spListOpen" class="tree-text">－</a>&nbsp;<fmt:message key="uriage" bundle="${label}"/><fmt:message key="sp" bundle="${label}"/>
                                                        <input type="hidden" name="spListOpenFlg" value="${fn:escapeXml(s003Bean.spListOpenFlg)}" id="spListOpenFlg"/>
                                                    </th>
                                                    <th align="center" class="total_disp_field"> <fmt:message key="total" bundle="${label}"/>(<fmt:message key="enka" bundle="${label}"/>)</th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb">
                                                        <span class="uriageSpCurF_Total">${sUtil.changeDispCurrencyFormat(uriageSpTotalInfo["URIAGE_AMOUNT_F"], s003Bean.jpyUnitKbn)}</span>
                                                        <input type="hidden" name="uriageSpCurF_Total" data-type-name="finalMikomi" value="${sUtil.changeDispCurrencyFormat(uriageSpTotalInfo["URIAGE_AMOUNT_F"], s003Bean.jpyUnitKbn)}" />
                                                    </td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="fix_tb">${sUtil.changeDispCurrencyFormat(uriageSpTotalInfo["URIAGE_AMOUNT_H"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 発番実績(K) -->
                                                    <td class="fix_tb">${sUtil.changeDispCurrencyFormat(uriageSpTotalInfo["URIAGE_AMOUNT_K"], s003Bean.jpyUnitKbn)}</td>
                                                </tr>

                                                <!-- 売上レート(通貨毎にレンダリング) -->
                                                <c:set value="1" var="rateTitleDispFlg" />
                                                <c:forEach items="${s003Bean.targetCurrencyList}" var="currencyCode" varStatus="s">
                                                <c:set value="${currencyCode == s003Bean.currencyCodeEn ? '0' : '1'}" var="rowDispFlg" /><%-- 画面見やすくするため、通貨JPYの場合は行を非表示にする --%>
                                                <c:set value="${s003Bean.uriageIkkatsuSpInfo[currencyCode]}" var="uriageIkkatsuSpInfo" />
                                                <tr style="${tdHeight}" class="fix_tb" data-currency-code="${fn:escapeXml(currencyCode)}" data-rate="1" data-row-disp-Flg="${rowDispFlg}">
                                                    <th colspan ="2" style="border-bottom-style:hidden;border-top-style:hidden;" valign="top">
                                                        <c:if test="${rateTitleDispFlg == '1' and rowDispFlg == '1'}">
                                                        <fmt:message key="uriageRate" bundle="${label}"/>
                                                        <c:set value="0" var="rateTitleDispFlg" />
                                                        </c:if>
                                                    </th>
                                                    <th colspan ="2">${fn:escapeXml(currencyCode)}</th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="">
                                                        <span class="uriageRateF_Currency" data-currency-code="${fn:escapeXml(currencyCode)}">${sUtil.exeFormatRateString(uriageIkkatsuSpInfo["URI_RATE_F"], currencyCode)}</span><input type="hidden" name="uriageRateF_Currency" data-currency-code="${fn:escapeXml(currencyCode)}" data-type-name="finalRate" data-rate="1" data-total-name="uriageSpCurF_Total" value="${sUtil.exeFormatRateString(uriageIkkatsuSpInfo["URI_RATE_F"], currencyCode)}" />
                                                    </td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="summary-col">${sUtil.exeFormatRateString(uriageIkkatsuSpInfo["URI_RATE_H"], currencyCode)}</td>
                                                    <!-- 発番実績(K) -->
                                                    <td class="summary-col"></td>
                                                </tr>
                                                </c:forEach>

                                                <!-- 売上金額(通貨毎にレンダリング) -->
                                                <c:forEach items="${s003Bean.targetCurrencyList}" var="currencyCode" varStatus="s">
                                                <c:set value="${s003Bean.uriageSpInfoSum[currencyCode]}" var="uriageSpInfoSum" />
                                                <tr style="${tdHeight}" class="fix_tb" data-currency-code="${fn:escapeXml(currencyCode)}" data-barance-check-kbn="sp-total" data-sp-net-utiwake="1">
                                                    <c:if test="${s.first}">
                                                    <th colspan ="2" rowspan="${s003Bean.targetCurrencyList.size()}" valign="top"><fmt:message key="uriageGaku" bundle="${label}"/></th>
                                                    </c:if>
                                                    <th colspan ="2">${fn:escapeXml(currencyCode)}</th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="">
                                                        <span data-uriage-curreny-sp="1">${sUtil.changeDispCurrencyFormat(uriageSpInfoSum["URIAGE_AMOUNT_F"], s003Bean.jpyUnitKbn, currencyCode)}</span>
                                                        <input type="hidden" name="uriageSpF_CurrencyTotal" data-type-name="finalMikomi" data-sp-net-utiwake="1" data-currency-code="${fn:escapeXml(currencyCode)}" data-total-name="uriageSpCurF_Total" value="${sUtil.changeDispCurrencyFormat(uriageSpInfoSum["URIAGE_AMOUNT_F"], s003Bean.jpyUnitKbn, currencyCode)}" />
                                                    </td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="summary-col">${sUtil.changeDispCurrencyFormat(uriageSpInfoSum["URIAGE_AMOUNT_H"], s003Bean.jpyUnitKbn, currencyCode)}</td>
                                                    <!-- 契約実績(K) -->
                                                    <td class="summary-col">${sUtil.changeDispCurrencyFormat(uriageSpInfoSum["URIAGE_AMOUNT_K"], s003Bean.jpyUnitKbn, currencyCode)}</td>
                                                </tr>
                                                </c:forEach>

                                                <!-- 売上金額・一括見込SP(通貨毎にレンダリング) -->
                                                <c:forEach items="${s003Bean.targetCurrencyList}" var="currencyCode" varStatus="s">
                                                <c:set value="${s003Bean.uriageIkkatsuSpInfo[currencyCode]}" var="uriageIkkatsuSpInfo" />
                                                <tr style="${tdHeight}" class="fix_tb" data-currency-code="${fn:escapeXml(currencyCode)}" data-sp-net-utiwake="2">
                                                    <c:if test="${s.first}">
                                                    <th colspan ="2" rowspan="${s003Bean.targetCurrencyList.size()}" valign="top"><fmt:message key="ikkatsuMikomiSp" bundle="${label}"/></th>
                                                    </c:if>
                                                    <th colspan ="2">${fn:escapeXml(currencyCode)}</th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb ">
                                                        <c:if test="${s003Bean.editFlg == '1'}"><%--編集モード時(入力可能)--%>
                                                        <input type="hidden" name="uriageCurF" value="${fn:escapeXml(currencyCode)}" />
                                                        <input type="hidden" name="uriageRenbanF" value="${fn:escapeXml(s003Bean.ikkatsuSpRenban)}" />
                                                        <input type="hidden" name="uriageRenbanSeqF" value="${fn:escapeXml(s003Bean.ikkatsuSpRenbanSeq)}" />
                                                        <input type="text" name="uriageSpF" data-type-name="finalMikomi" data-sp-net-utiwake="2" data-currency-code="${fn:escapeXml(currencyCode)}" data-total-name="uriageSpF_CurrencyTotal" class="numberFormat" style="${numberStyle}" value="${sUtil.changeDispCurrencyFormat(uriageIkkatsuSpInfo["URIAGE_AMOUNT_F"], s003Bean.jpyUnitKbn, currencyCode)}" />
                                                        </c:if>
                                                        <c:if test="${s003Bean.editFlg != '1'}"><%--参照モード時--%>
                                                        ${sUtil.changeDispCurrencyFormat(uriageIkkatsuSpInfo["URIAGE_AMOUNT_F"], s003Bean.jpyUnitKbn, currencyCode)}
                                                        </c:if>
                                                    </td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="summary-col">${sUtil.changeDispCurrencyFormat(uriageIkkatsuSpInfo["URIAGE_AMOUNT_H"], s003Bean.jpyUnitKbn, currencyCode)}</td>
                                                    <!-- 契約実績(K) -->
                                                    <td class="summary-col">${sUtil.changeDispCurrencyFormat(uriageIkkatsuSpInfo["URIAGE_AMOUNT_K"], s003Bean.jpyUnitKbn, currencyCode)}</td>
                                                </tr>
                                                </c:forEach>

                                                <!-- 売上高 受注通知一覧・開閉の設定行 -->
                                                <tr style="height:20px;" class="sub-total-jTuti">
                                                    <td colspan="3" style="text-align:left;">
                                                        &nbsp;&nbsp;<a href="javascript:void(0)" id="juchuTuchiListOpen" class="tree-text">－</a>&nbsp;受注通知内訳<input type="hidden" name="juchuTuchiListOpenFlg" value="${fn:escapeXml(s003Bean.juchuTuchiListOpenFlg)}" id="juchuTuchiListOpenFlg"/>
                                                    </td>
                                                    <td ></td>
                                                    <td ></td>
                                                    <td ></td>
                                                    <td ></td>
                                                </tr>

                                                <!-- 売上高 受注通知一覧(縦) -->
                                                <c:forEach items="${s003Bean.uriageSpRowInfoList}" var="rowList" varStatus="s">
                                                <c:set value="${(rowList.currencyCode).concat('_').concat(rowList.renban)}" var="colKeyInfo" />
                                                <c:set value="${s003Bean.uriageSpInfoJyuchuTuti[colKeyInfo]}" var="uriageSpJyuchuTuti" />
                                                <tr style="${tdHeight}" data-currency-code="${fn:escapeXml(rowList.currencyCode)}" data-sp-net-utiwake="2" data-barance-check-kbn="sp-utiwake">
                                                    <c:if test="${rowList.rowspanKeyInfo != bRowSpanKeyInfo}">
                                                    <th rowspan="${fn:escapeXml(rowList.rowspanCount)}" colspan ="2" valign="top">${fn:escapeXml(rowList.renbanHead)}(<fmt:formatDate value="${utl.elParseDate(rowList.uriageYm)}" pattern="${sUtil.formatYm()}"/>)</th>
                                                    </c:if>
                                                    <th colspan ="2">${fn:escapeXml(rowList.currencyCode)}<fmt:formatNumber value="${rowList.keiyakuRate}" pattern="${sUtil.formatRate()}" /></th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb ">
                                                        <c:if test="${s003Bean.editFlg == '1'}"><%--編集モード時(入力可能)--%>
                                                        <input type="hidden" name="uriageCurF" value="${fn:escapeXml(rowList.currencyCode)}" />
                                                        <input type="hidden" name="uriageRenbanF" value="${fn:escapeXml(rowList.renban)}" />
                                                        <input type="hidden" name="uriageRenbanSeqF" value="${fn:escapeXml(rowList.renbanSeq)}" />
                                                        <input type="text" name="uriageSpF" data-type-name="finalMikomi" data-sp-net-utiwake="2" data-currency-code="${fn:escapeXml(rowList.currencyCode)}" data-total-name="uriageSpF_CurrencyTotal" class="numberFormat" style="${numberStyle}" value="${sUtil.changeDispCurrencyFormat(uriageSpJyuchuTuti["URIAGE_AMOUNT_F"], s003Bean.jpyUnitKbn, rowList.currencyCode)}" />
                                                        </c:if>
                                                        <c:if test="${s003Bean.editFlg != '1'}"><%--参照モード時--%>
                                                        ${sUtil.changeDispCurrencyFormat(uriageSpJyuchuTuti["URIAGE_AMOUNT_F"], s003Bean.jpyUnitKbn, rowList.currencyCode)}
                                                        </c:if>
                                                    </td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="fix_tb summary-col">${sUtil.changeDispCurrencyFormat(uriageSpJyuchuTuti["URIAGE_AMOUNT_H"], s003Bean.jpyUnitKbn, rowList.currencyCode)}</td>
                                                    <!-- 契約実績(K) -->
                                                    <td class="fix_tb summary-col">${sUtil.changeDispCurrencyFormat(uriageSpJyuchuTuti["URIAGE_AMOUNT_K"], s003Bean.jpyUnitKbn, rowList.currencyCode)}</td>
                                                </tr>
                                                <c:set value="${rowList.rowspanKeyInfo}" var="bRowSpanKeyInfo" />
                                                </c:forEach>
<%--
                                                <c:forEach items="${s003Bean.salesList}" var="salesList" varStatus="s">
                                                <tr style="${tdHeight}">
                                                    <c:if test="${salesList.LAG_ORDER_NO != salesList.LAG_CATEGORY}">
                                                    <th rowspan="${salesList.CURRENCY_COUNT}" colspan ="2" valign="top"><c:out value="${fn:escapeXml(salesList.RENBAN)}" />(<c:out value="${fn:escapeXml(salesList.URIAGE_YM_DATE)}" />)</th>
                                                    </c:if>
                                                    <th colspan ="2">${fn:escapeXml(salesList.CURRENCY_CODE)}<fmt:formatNumber value="${salesList.KEIYAKU_RATE}" pattern="${sUtil.formatRate()}" /></th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb ">
                                                        <c:if test="${s003Bean.editFlg == '1'}">
                                                        <input type="text" name="" class="numberFormat" style="${numberStyle}" value="" />
                                                        </c:if>
                                                        <c:if test="${s003Bean.editFlg != '1'}">
                                                            参照
                                                        </c:if>
                                                    </td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="fix_tb summary-col"></td>
                                                    <!-- 発番実績(K) -->
                                                    <td class="fix_tb summary-col"></td>
                                                    <!-- 受注残(Z) -->
                                                    <td class="fix_tb summary-col"></td>
                                                </tr>
                                                </c:forEach>
--%>
                                            </table>

                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" data-table-kanri="uriage">
                                                <colgroup>
                                                    <col style="width:60px">
                                                    <col style="width:60px">
                                                    <col style="width:62px">
                                                    <!-- 固定項目 -->
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                </colgroup>
                                                <!-- 売上原価 合計 -->
                                                <tr style="height:20px;" class="total_disp_field" data-total-row="1" data-total-type="net" data-barance-check-kbn="net-total">
                                                    <th colspan="2" class="total_disp_field" style="text-align:left;">
                                                        <a href="javascript:void(0)" id="netListOpen" class="tree-text">－</a>&nbsp;<fmt:message key="uriage" bundle="${label}"/><fmt:message key="net" bundle="${label}"/>
                                                        <input type="hidden" name="netListOpenFlg" value="${fn:escapeXml(s003Bean.netListOpenFlg)}" id="netListOpenFlg"/>
                                                    </th>
                                                    <th align="center" class="total_disp_field"><fmt:message key="total" bundle="${label}"/>(<fmt:message key="enka" bundle="${label}"/>)</th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb">
                                                        <span class="uriageNetF_Total">${sUtil.changeDispCurrencyFormat(s003Bean.uriageNetTotalInfo["URIAGE_GENKA_F"], s003Bean.jpyUnitKbn)}</span>
                                                        <input type="hidden" name="uriageNetF_Total" data-type-name="finalMikomi" value="${sUtil.changeDispCurrencyFormat(s003Bean.uriageNetTotalInfo["URIAGE_GENKA_F"], s003Bean.jpyUnitKbn)}" />
                                                    </td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="fix_tb">${sUtil.changeDispCurrencyFormat(s003Bean.uriageNetTotalInfo["URIAGE_GENKA_H"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 契約実績(K) -->
                                                    <td class="fix_tb">${sUtil.changeDispCurrencyFormat(s003Bean.uriageNetTotalInfo["URIAGE_GENKA_K"], s003Bean.jpyUnitKbn)}</td>
                                                </tr>
                                                <!-- 売上原価 内訳一覧 -->
                                                <c:forEach items="${s003Bean.uriageNetCateList}" var="uriageNetCateList" varStatus="s">
                                                <c:set value="${(uriageNetCateList.categoryCode).concat('_').concat(uriageNetCateList.categoryKbn1).concat('_').concat(uriageNetCateList.categoryKbn2)}" var="cateKey"/>
                                                <c:set value="${s003Bean.uriageNetCateInfo[cateKey]}" var="uriageNetCateInfo"/>
                                                <tr style="${tdHeight}" data-sp-net-utiwake="1" ${!sUtil.isIkkatsuCategoryCode(uriageNetCateList.categoryCode) ? "data-barance-check-kbn='net-utiwake'" : ""}>
                                                    <c:if test="${s.index==0}">
                                                    <th rowspan="${s003Bean.uriageNetCateList.size()}"></th>
                                                    </c:if>
                                                    <th class="disp_character_hid" style="text-align:left" title="${fn:escapeXml(uriageNetCateList.categoryName1)}">
                                                        <a href="javascript:void(0);" class="categoryLink" data-key="${fn:escapeXml(uriageNetCateList.categoryCode)}" data-barance-check-kbn="ki">${fn:escapeXml(uriageNetCateList.categoryName1)}</a>
                                                    </th>
                                                    <th class="disp_character_hid" style="text-align:left" title="${fn:escapeXml(uriageNetCateList.categoryName2)}"><a href="javascript:void(0);" class="categoryLink" data-key="${fn:escapeXml(uriageNetCateList.categoryCode)}" data-barance-check-kbn="ki">${fn:escapeXml(uriageNetCateList.categoryName2)}</a></th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb ${uriageNetCateInfo["ITEM_FLG_F"] == '1' ? 'itemFlg' : ''}"><%-- 2017/11/28 #071 MOD 項番優先で計算された値のセルに色付けをする --%>
                                                        <c:if test="${s003Bean.editFlg == '1'}"><%--編集モード時(入力可能)--%>
                                                        <input type="hidden" name="uriageCategoryCodeF" value="${fn:escapeXml(uriageNetCateList.categoryCode)}" />
                                                        <input type="hidden" name="uriageCategoryKbn1F" value="${fn:escapeXml(uriageNetCateList.categoryKbn1)}" />
                                                        <input type="hidden" name="uriageCategoryKbn2F" value="${fn:escapeXml(uriageNetCateList.categoryKbn2)}" />
                                                        <input type="text" name="uriageNetF" data-type-name="finalMikomi" data-sp-net-utiwake="1" data-total-name="uriageNetF_Total" class="numberFormat" style="${numberStyle}" value="${sUtil.changeDispCurrencyFormat(uriageNetCateInfo["NET_F"], s003Bean.jpyUnitKbn)}" />
                                                        </c:if>
                                                        <c:if test="${s003Bean.editFlg != '1'}"><%--参照モード時--%>
                                                        ${sUtil.changeDispCurrencyFormat(uriageNetCateInfo["NET_F"], s003Bean.jpyUnitKbn)}
                                                        </c:if>
                                                    </td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="fix_tb summary-col">${sUtil.changeDispCurrencyFormat(uriageNetCateInfo["NET_H"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 契約実績(K) -->
                                                    <td class="fix_tb summary-col">${sUtil.changeDispCurrencyFormat(uriageNetCateInfo["NET_K"], s003Bean.jpyUnitKbn)}</td>
                                                </tr>
                                                </c:forEach>
                                            </table>
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" data-table-kanri="uriage">
                                                <colgroup>
                                                    <col style="width:60px">
                                                    <col style="width:62px">
                                                    <col style="width:62px">
                                                    <!-- 固定項目 -->
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                </colgroup>
                                                <!-- 売上管理・粗利 -->
                                                <tr style="height:20px;" class="total_disp_field" data-arari="uriage">
                                                    <th colspan="2" class="total_disp_field"><fmt:message key="arari" bundle="${label}"/></th>
                                                    <th align="center" class="total_disp_field"></th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb">${sUtil.changeDispCurrencyFormat(sUtil.arariString(s003Bean.uriageSpTotalInfo["URIAGE_AMOUNT_F"], s003Bean.uriageNetTotalInfo["URIAGE_GENKA_F"]), s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="fix_tb">${sUtil.changeDispCurrencyFormat(sUtil.arariString(s003Bean.uriageSpTotalInfo["URIAGE_AMOUNT_H"], s003Bean.uriageNetTotalInfo["URIAGE_GENKA_H"]), s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 契約実績(K) -->
                                                    <td class="fix_tb"><%--${sUtil.changeDispCurrencyFormat(sUtil.arariString(s003Bean.uriageSpTotalInfo["URIAGE_AMOUNT_K"], s003Bean.uriageNetTotalInfo["URIAGE_GENKA_K"]), s003Bean.jpyUnitKbn)}--%></td>
                                                </tr>
                                                <!-- 売上管理・Ｍ率 -->
                                                <tr style="height:20px;" class="total_disp_field" data-mrate="uriage">
                                                    <th colspan="2" class="total_disp_field"><fmt:message key="mrate" bundle="${label}"/></th>
                                                    <th align="center" class="total_disp_field"></th>
                                                    <!-- 最終見込(F) -->
                                                    <td class="fix_tb">${sUtil.mrate(s003Bean.uriageSpTotalInfo["URIAGE_AMOUNT_F"], s003Bean.uriageNetTotalInfo["URIAGE_GENKA_F"])}</td>
                                                    <!-- 発番実績(H) -->
                                                    <td class="fix_tb">${sUtil.mrate(s003Bean.uriageSpTotalInfo["URIAGE_AMOUNT_H"], s003Bean.uriageNetTotalInfo["URIAGE_GENKA_H"])}</td>
                                                    <!-- 契約実績(K) -->
                                                    <td class="fix_tb"><%--${sUtil.mrate(s003Bean.uriageSpTotalInfo["URIAGE_AMOUNT_K"], s003Bean.uriageNetTotalInfo["URIAGE_GENKA_K"])}--%></td>
                                                </tr>
                                            </table>

                                            <%--<c:if test="${!empty s003Bean.kaisyuInfoList}">--%>
                                            <table class="search-result-data">
                                                <tr style="height:21px">
                                                    <td></td>
                                                </tr>
                                            </table>

                                            <%-- 回収情報(左)　レンダリングStart --%>
                                            <!-- 回収情報Table -->
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data">
                                                <colgroup>
                                                    <col style="width:32px">
                                                    <col style="width:45px">
                                                    <col style="width:38px">
                                                    <col style="width:38px">
                                                    <col style="width:32px">
                                                    <!-- 固定項目 -->
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                </colgroup>
                                                <tr >
                                                    <td colspan="5" style="border-bottom-style:none;" class="subTitle">
                                                        <fmt:message key="kaisyuManagement" bundle="${label}"/>&nbsp;
                                                        <span style="display:inline-block;" title="${fn:escapeXml(sUtil.getToriatsuName(detailHeader.ankenEntity.toriatsuNm))}">${fn:escapeXml(sUtil.getToriatsuName(detailHeader.ankenEntity.toriatsuNm))}</span>
                                                    </td>
                                                    <!-- 固定項目 -->
                                                    <th rowspan="2" style="border-bottom-style:none;"><fmt:message key="jissekiTotal" bundle="${label}"/></th>
                                                    <th rowspan="2" style="border-bottom-style:none;"><fmt:message key="zangaku" bundle="${label}"/></th>
                                                    <th rowspan="2" style="border-bottom-style:none;"></th>
                                                </tr>
                                                <tr style="${tdHeight};">
                                                    <th ><fmt:message key="currency" bundle="${label}"/></th>
                                                    <th ><fmt:message key="taxRate" bundle="${label}"/></th>
                                                    <th ><fmt:message key="kinsyu" bundle="${label}"/></th>
                                                    <th ><fmt:message key="maeuke" bundle="${label}"/></th>
                                                    <th ></th>
                                                </tr>
                                            </table>
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" id="kaisyuDataLeft">
                                                <colgroup>
                                                    <col style="width:32px">
                                                    <col style="width:45px">
                                                    <col style="width:38px">
                                                    <col style="width:38px">
                                                    <col style="width:32px">
                                                    <!-- 固定項目 -->
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                    <col style="${tdWidth}">
                                                </colgroup>
                                                <!-- 回収管理 合計(円貨) --><%-- 2017/11/20 #072 ADD 回収Total行追加 --%>
                                                <tr style="height:20px;" class="total_disp_field" data-mrate="kaisyu">
                                                    <th colspan="5" class="total_disp_field" style="text-align: right;"><fmt:message key="total" bundle="${label}"/>(<fmt:message key="enka" bundle="${label}"/>)</th>
                                                    <!-- 実績合計 -->
                                                    <td class="fix_tb" id="kaisyu-total-j">${sUtil.changeDispCurrencyFormat(s003Bean.kaisyuTotalInfo["KAISYU_ENKA_ZEIKOMI_J_TOTAL"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 残額 -->
                                                    <td class="fix_tb" id="kaisyu-total-m">${sUtil.changeDispCurrencyFormat(s003Bean.kaisyuTotalInfo["KAISYU_ENKA_ZEIKOMI_M_TOTAL"], s003Bean.jpyUnitKbn)}</td>
                                                    <!--  -->
                                                    <td class="fix_tb"></td>
                                                </tr>
                                                <!-- 回収金額(通貨毎) --><%-- 2017/11/20 #072 ADD 回収Total行追加 --%>
                                                <c:forEach items="${s003Bean.kaisyuInfoList}" var="kaisyuCurInfo" varStatus="s">
                                                <c:set value="${kaisyuCurInfo['CURRENCY_CODE']}" var="currencyCode" />
                                                <c:set value="${currencyCode != kaisyuCurInfo['PRE_CURRENCY_CODE'] ? '1' : '0'}" var="dispCurrencyCodeKbn" />
                                                <c:set value="${kaisyuCurInfo['CURRENCY_CODE_COUNT']}" var="currencyCodeRowSpanCount" />
                                                <c:set value="${currencyCode == s003Bean.currencyCodeEn ? (kaisyuCurInfo['ZEI_RATE'] > 0 ? '2' : '1') : (kaisyuCurInfo['ZEI_RATE'] > 0 ? '3' : '2')}" var="rowSpanCount" />
                                                <tr style="${tdHeight}" data-currency-code="${fn:escapeXml(currencyCode)}" data-sp-net-utiwake="1">
                                                    <c:if test="${dispCurrencyCodeKbn == '1'}">
                                                    <th rowspan="${currencyCodeRowSpanCount}" style="text-align:center">${fn:escapeXml(currencyCode)}</th>
                                                    </c:if>
                                                    <th rowspan="${rowSpanCount}">${fn:escapeXml(kaisyuCurInfo["ZEI_RNM"])}</th>
                                                    <th rowspan="${rowSpanCount}">${sUtil.getLabelKinsyuKbn(kaisyuCurInfo["KINSYU_KBN"])}</th>
                                                    <th rowspan="${rowSpanCount}">${sUtil.getLabelKaisyuKbn(kaisyuCurInfo["KAISYU_KBN"])}</th>
                                                    <th ><c:if test="${currencyCode != s003Bean.currencyCodeEn}"><fmt:message key="foreignUnitLabel" bundle="${label}"/></c:if><c:if test="${currencyCode == s003Bean.currencyCodeEn}"><fmt:message key="hontai" bundle="${label}"/></c:if></th>
                                                    <!-- 固定項目 -->
                                                    <td class="fix_noSpNet" data-kaisyu-j-total-cell="1">
                                                        <span data-kaisyu-amount-j-total="1" data-target-calc="${currencyCode == s003Bean.currencyCodeEn ? '1' : '0'}" data-currency-code="${fn:escapeXml(currencyCode)}" data-zei-rate="${fn:escapeXml(kaisyuCurInfo["ZEI_RATE"])}">${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_AMOUNT_J_TOTAL"], s003Bean.jpyUnitKbn, currencyCode)}</span>
                                                    </td>
                                                    <td class="fix_noSpNet" data-kaisyu-m-total-cell="1">
                                                        <span data-kaisyu-amount-m-total="1" data-target-calc="${currencyCode == s003Bean.currencyCodeEn ? '1' : '0'}" data-currency-code="${fn:escapeXml(currencyCode)}" data-zei-rate="${fn:escapeXml(kaisyuCurInfo["ZEI_RATE"])}">${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_AMOUNT_M_TOTAL"], s003Bean.jpyUnitKbn, currencyCode)}</span>
                                                    </td>
                                                    <td class="fix_noSpNet"></td>
                                                </tr>

                                                <!-- 回収金額 円通貨以外での円貨入力 -->
                                                <c:if test="${currencyCode != s003Bean.currencyCodeEn}">
                                                <tr style="${tdHeight}" data-currency-code="${fn:escapeXml(s003Bean.currencyCodeEn)}"  data-sp-net-utiwake="1">
                                                    <th ><fmt:message key="jpyUnitLabel" bundle="${label}"/></th>
                                                    <!-- 固定項目 -->
                                                    <td class="fix_noSpNet" data-kaisyu-j-total-cell="1">
                                                        <span data-kaisyu-amount-j-total="1" data-target-calc="1" data-currency-code="${fn:escapeXml(s003Bean.currencyCodeEn)}" data-zei-rate="${fn:escapeXml(kaisyuCurInfo["ZEI_RATE"])}">${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_ENKA_AMOUNT_J_TOTAL"], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}</span>
                                                    </td>
                                                    <td class="fix_noSpNet" data-kaisyu-m-total-cell="1">
                                                        <span data-kaisyu-amount-m-total="1" data-target-calc="1" data-currency-code="${fn:escapeXml(s003Bean.currencyCodeEn)}" data-zei-rate="${fn:escapeXml(kaisyuCurInfo["ZEI_RATE"])}">${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_ENKA_AMOUNT_M_TOTAL"], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}</span>
                                                    </td>
                                                    <td class="fix_noSpNet"></td>
                                                </tr>
                                                </c:if>

                                                <!-- 回収金額 税額 --><%-- 2017/11/20 #072 ADD 回収Total行追加 --%>
                                                <c:if test="${kaisyuCurInfo['ZEI_RATE'] > 0}">
                                                <tr style="${tdHeight}" data-currency-code="${fn:escapeXml(s003Bean.currencyCodeEn)}" data-sp-net-utiwake="1">
                                                    <th ><fmt:message key="zeigaku" bundle="${label}"/></th>
                                                    <!-- 固定項目 -->
                                                    <td class="fix_noSpNet" data-kaisyu-j-total-cell="1">
                                                        <span data-kaisyu-amount-j-total="1" data-target-calc="2" class="enkaZei">${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_ENKA_ZEI_J_TOTAL"], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}</span>
                                                    </td>
                                                    <td class="fix_noSpNet" data-kaisyu-m-total-cell="1">
                                                        <span data-kaisyu-amount-m-total="1" data-target-calc="2" class="enkaZei">${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_ENKA_ZEI_M_TOTAL"], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}</span>
                                                    </td>
                                                    <td class="fix_noSpNet"></td>
                                                </tr>
                                                </c:if>
                                                </c:forEach>
                                            </table>
                                            <%--</c:if>--%>

                                        </div>
                                    </td>

                                    <td align="left">
                                        <!--スクロール部分-->
                                        <div id="Head2" style="width:1019px;overflow-x:hidden;">
                                            <table class="search-result-data">
                                                <tr style="height:21px">
                                                    <td style="display:none;${tdWidth}"></td>
                                                    <td style="${tdWidth}"></td>
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}; text-align:right"><%-- 受注の見込月削除ボタン表示 --%>
                                                        <c:set value="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M' && !empty targetYm.syuekiYm && authUtl.enableFlg('MTUKI_DELETE', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}" var="ymDeleteFlg"/>
                                                        <c:if test="${ymDeleteFlg}"><div class="btn-M-Delete" data-button-name="juchuMDelete" onclick="deleteMikomi(this)"><fmt:message key="delete" bundle="${label}"/></div></c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <td style="${tdWidth}">
                                                        <c:if test="${s003Bean.editFlg == '1'}"><div class="btn-M-Add" onclick="addMikomiInputCol()"><fmt:message key="addition" bundle="${label}"/></div></c:if><%-- 見込入力月の追加ボタン表示(編集モード) --%>
                                                    </td>
                                                    <td style="${tdWidth}"></td>
                                                </tr>
                                            </table>
                                        </div>
                                        <div id="List2" style="height:218px;overflow-y:scroll;width:1042px;overflow-x:scroll;" onscroll="jsScrollTop(List1, this);jsScrollLeft(Head2, this)">
                                            <%-- 受注管理情報(右) レンダリングStart --%>
                                            <!-- 受注管理Table(右上)・タイトル -->
                                            <table class="table-bordered psprimis-list-table table-grid search-result-data" data-table-kanri="juchu">
                                                <tr style="${tdHeight};" data-ym-title="1">
                                                    <!-- 為替差 -->
                                                    <th style="display:none;${tdWidth}"> </th>
                                                    <!-- 受注残 -->
                                                    <th style="${tdWidth}"><fmt:message key="juchuZan" bundle="${label}"/></th>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="s">
                                                    <th style="${tdWidth}">
                                                        <c:set value="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M'}" var="ymEditFlg"/>
                                                        <c:if test="${ymEditFlg}"><%--編集モード時(入力可能)--%>
                                                            <input type="text" name="jyuchuSyuekiYmM" data-disp-add-flg="${fn:escapeXml(targetYm.dispAddFlg)}" class="ymFormat" value="${fn:escapeXml(targetYm.syuekiYmDate)}" maxlength="7" />
                                                            <input type="hidden" name="orgJyuchuSyuekiYmM" value="${fn:escapeXml(targetYm.syuekiYmDate)}" />
                                                            <input type="hidden" name="jyuchuSyuekiYmMDelFlg" value="0" />
                                                            <input type="hidden" name="jyuchuDataKbnM" value="${fn:escapeXml(targetYm.dataKbn)}" />
                                                        </c:if>
                                                        <c:if test="${!ymEditFlg}"><%--参照モード時(入力可能)--%>
                                                            ${fn:escapeXml(targetYm.syuekiYmDate)}
                                                        </c:if>
                                                    </th>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <th style="display:none"></th>
                                                    <th style="${tdWidth2Span}" colspan="2"><fmt:message key="total" bundle="${label}"/></th>
                                                </tr>
                                                <tr id="baseWidthTr">
                                                    <!-- 為替差 -->
                                                    <th style="display:none;${tdWidth}"> </th>
                                                    <!-- 発番-売上済 -->
                                                    <th style="${tdWidth}"><fmt:message key="hatUriage" bundle="${label}"/></th>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="s">
                                                    <th style="${tdWidth}">
                                                        <c:set value="${targetYm.dataKbn == 'J' && targetYm.syuekiYmDate == sUtil.exeFormatYm(detailHeader.kanjoDate)}" var="nowMonthFlg"/>
                                                        <c:if test="${nowMonthFlg}"><fmt:message key="nowMonthHassei" bundle="${label}"/></c:if>
                                                        <c:if test="${!nowMonthFlg}">${fn:escapeXml(sUtil.getJYLabel(targetYm.dataKbn))}</c:if>
                                                        <c:if test="${targetYm.dispBikoFlg == '1'}"><div class="btn-bikou" onclick="return kiBikou(${fn:escapeXml(targetYm.syuekiYm)},'<fmt:message key="forceSave" bundle="${label}"/>')">…</div></c:if>
                                                    </th>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <th style="${tdWidth}"><fmt:message key="jissekiMikomi" bundle="${label}"/></th>
                                                    <th style="${tdWidth}"><fmt:message key="diff3" bundle="${label}"/></th>
                                                </tr>
                                            </table>
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" id="jyuchuSpInfoTableRight" data-table-kanri="juchu">
                                                <!-- 受注SP 合計 -->
                                                <tr style="height:20px;" class="fix_tb total_disp_field" id="link_juchuSpListOpen" data-total-row="1" data-total-type="sp">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_SP_E"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 受注残(Z) -->
                                                    <td style="${tdWidth}" class="fix_tb">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_SP_Z"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}">
                                                        <span class="jyuchuSpCurM_Total">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_SP_YM".concat(t.count)], s003Bean.jpyUnitKbn)}</span>
                                                        <c:if test="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M'}"><input type="hidden" name="jyuchuSpCurM_Total" data-type-name="syuekiMikomi" value="${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_SP_YM".concat(t.count)], s003Bean.jpyUnitKbn)}" /></c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}">
                                                        <span class="gTotal">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_SP_G"], s003Bean.jpyUnitKbn)}</span>
                                                        <input type="hidden" name="gTotal" value="${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_SP_JM"], s003Bean.jpyUnitKbn)}" />
                                                    </td>
                                                    <td style="${tdWidth}">
                                                        <c:set value="${sUtil.arariString(s003Bean.jyuchuInfoTotal['JYUCHU_SP_F'], s003Bean.jyuchuInfoTotal['JYUCHU_SP_G'])}" var="jyuchuTotalSpDiff" />
                                                        <span class="gDiff">${sUtil.changeDispCurrencyFormat(jyuchuTotalSpDiff, s003Bean.jpyUnitKbn)}</span>
                                                    </td>
                                                </tr>

                                                <!-- 受注レート 右(通貨毎にloopでレンダリング) -->
                                                <c:set value="1" var="rateTitleDispFlg" />
                                                <c:forEach items="${s003Bean.targetCurrencyList}" var="currencyCode" varStatus="s">
                                                <c:set value="${currencyCode == s003Bean.currencyCodeEn ? '0' : '1'}" var="rowDispFlg" /><%-- 画面見やすくするため、通貨JPYの場合は行を非表示にする --%>
                                                <c:set value="${s003Bean.jyuchuSpInfo[currencyCode]}" var="jyuchuSpInfo" />
                                                <tr style="${tdHeight}" class="fix_tb" data-currency-code="${fn:escapeXml(currencyCode)}" data-rate="1" data-row-disp-Flg="${rowDispFlg}">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}" class="summary-col">${sUtil.exeFormatRateString(jyuchuSpInfo["JYUCHU_RATE_E"], currencyCode)}</td>
                                                    <!-- 受注残(Z) -->
                                                    <td style="${tdWidth}" class="fix_tb summary-col">${sUtil.exeFormatRateString(jyuchuSpInfo["JYUCHU_RATE_Z"], currencyCode)}</td>
                                                    <!-- 各月の実績/見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}" class="${targetYm.className}">
                                                        <span class="jyuchuSpRateCurM">${sUtil.exeFormatRateString(jyuchuSpInfo["JYUCHU_RATE_YM".concat(t.count)], currencyCode)}</span>
                                                        <c:if test="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M'}"><input type="hidden" name="jyuchuSpRateCurM" data-currency-code="${fn:escapeXml(currencyCode)}" data-total-name="jyuchuSpCurM_Total" data-type-name="syuekiRate" data-rate="1" value="${sUtil.exeFormatRateString(jyuchuSpInfo["JYUCHU_RATE_YM".concat(t.count)], currencyCode)}" /></c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}" class="ki-summary-col"></td>
                                                    <td style="${tdWidth}" class="kikan_ikkan_diff"></td>
                                                </tr>
                                                </c:forEach>

                                                <!-- 受注金額 右(通貨毎にloopでレンダリング) -->
                                                <c:forEach items="${s003Bean.targetCurrencyList}" var="currencyCode" varStatus="s">
                                                <c:set value="${s003Bean.jyuchuSpInfo[currencyCode]}" var="jyuchuSpInfo" />
                                                <tr style="${tdHeight}" class="fix_tb" data-currency-code="${fn:escapeXml(currencyCode)}" data-barance-check-kbn="sp-total" data-sp-net-utiwake="1">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}" class="summary-col">${sUtil.changeDispCurrencyFormat(jyuchuSpInfo["JYUCHU_SP_E"], s003Bean.jpyUnitKbn, currencyCode)}</td>
                                                    <!-- 受注残(Z) -->
                                                    <td style="${tdWidth}" class="fix_tb summary-col">${sUtil.changeDispCurrencyFormat(jyuchuSpInfo["JYUCHU_SP_Z"], s003Bean.jpyUnitKbn, currencyCode)}</td>
                                                    <!-- 各月の実績/見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}" class="${targetYm.className}">
                                                        <c:set value="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M'}" var="ymEditFlg"/>
                                                        <c:if test="${ymEditFlg}"><%--編集モード時(入力可能)--%>
                                                            <input type="hidden" name="jyuchuCurM" value="${fn:escapeXml(currencyCode)}" />
                                                            <input type="text" name="jyuchuSpCurM" data-type-name="syuekiMikomi" data-sp-net-utiwake="1" data-currency-code="${fn:escapeXml(currencyCode)}" data-total-name="jyuchuSpCurM_Total" class="numberFormat" style="${numberStyle}" value="${sUtil.changeDispCurrencyFormat(jyuchuSpInfo["JYUCHU_SP_YM".concat(t.count)], s003Bean.jpyUnitKbn, currencyCode)}" />
                                                        </c:if>
                                                        <c:if test="${!ymEditFlg}"><%--参照モード時--%>
                                                            ${sUtil.changeDispCurrencyFormat(jyuchuSpInfo["JYUCHU_SP_YM".concat(t.count)], s003Bean.jpyUnitKbn, currencyCode)}
                                                        </c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}" class="fix_tb ki-summary-col">
                                                        <span class="gTotal">${sUtil.changeDispCurrencyFormat(jyuchuSpInfo["JYUCHU_SP_G"], s003Bean.jpyUnitKbn, currencyCode)}</span>
                                                        <input type="hidden" name="gTotal" value="${sUtil.changeDispCurrencyFormat(jyuchuSpInfo["JYUCHU_SP_JM"], s003Bean.jpyUnitKbn, currencyCode)}" />
                                                    </td>
                                                    <td style="${tdWidth}" class="fix_tb kikan_ikkan_diff"><span class="gDiff" data-g-diff-check="1">${sUtil.changeDispCurrencyFormat(sUtil.arariString(jyuchuSpInfo["JYUCHU_SP_F"], jyuchuSpInfo["JYUCHU_SP_G"]), s003Bean.jpyUnitKbn, currencyCode)}</span></td>
                                                </tr>
                                                </c:forEach>
                                            </table>
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" id="jyuchuSpInfoTableRight" data-table-kanri="juchu">
                                                <!-- 受注NET・合計 -->
                                                <tr style="height:20px;" class="fix_tb total_disp_field" data-total-row="1" id="link_juchuNetListOpen" data-total-type="net" data-barance-check-kbn="net-total">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_NET_E"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 受注残(Z) -->
                                                    <td style="${tdWidth}" class="fix_tb">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_NET_Z"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}" >
                                                        <span class="jyuchuNetM_Total">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_NET_YM".concat(t.count)], s003Bean.jpyUnitKbn)}</span>
                                                        <c:if test="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M'}"><input type="hidden" name="jyuchuNetM_Total" data-type-name="syuekiMikomi" value="${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_NET_YM".concat(t.count)], s003Bean.jpyUnitKbn)}" /></c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}" >
                                                        <span class="gTotal">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_NET_G"], s003Bean.jpyUnitKbn)}</span>
                                                        <input type="hidden" name="gTotal" value="${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuInfoTotal["JYUCHU_NET_JM"], s003Bean.jpyUnitKbn)}" />
                                                    </td>
                                                    <td style="${tdWidth}" >
                                                        <c:set value="${sUtil.arariString(s003Bean.jyuchuInfoTotal['JYUCHU_NET_F'], s003Bean.jyuchuInfoTotal['JYUCHU_NET_G'])}" var="jyuchuTotalNetDiff" />
                                                        <span class="gDiff">${sUtil.changeDispCurrencyFormat(jyuchuTotalNetDiff, s003Bean.jpyUnitKbn)}</span>
                                                    </td>
                                                </tr>
                                                <!-- 受注NET・内訳(一括受注NET) -->
                                                <tr style="${tdHeight};" class="fix_tb" data-sp-net-utiwake="1">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}" class="summary-col">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuNetInfo["JYUCHU_NET_E"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 受注残(Z) -->
                                                    <td style="${tdWidth}" class="fix_tb summary-col">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuNetInfo["JYUCHU_NET_Z"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}" class="${targetYm.className}">
                                                        <c:set value="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M'}" var="ymEditFlg"/>
                                                        <c:if test="${ymEditFlg}"><%--編集モード時(入力可能)--%>
                                                            <input type="hidden" name="jyuchuNetCategoryCodeM" value="${fn:escapeXml(s003Bean.jyuchuNetInfo["CATEGORY_CODE"])}"/>
                                                            <input type="hidden" name="jyuchuNetCategoryKbn1M" value="${fn:escapeXml(s003Bean.jyuchuNetInfo["CATEGORY_KBN1"])}"/>
                                                            <input type="hidden" name="jyuchuNetCategoryKbn2M" value="${fn:escapeXml(s003Bean.jyuchuNetInfo["CATEGORY_KBN2"])}"/>
                                                            <input type="text" name="jyuchuNetM" data-type-name="syuekiMikomi" data-sp-net-utiwake="1" data-total-name="jyuchuNetM_Total" class="numberFormat" style="${numberStyle}" value="${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuNetInfo["JYUCHU_NET_YM".concat(t.count)], s003Bean.jpyUnitKbn)}" />
                                                        </c:if>
                                                        <c:if test="${!ymEditFlg}"><%--参照モード時--%>
                                                            ${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuNetInfo["JYUCHU_NET_YM".concat(t.count)], s003Bean.jpyUnitKbn)}
                                                        </c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td class="ki-summary-col">
                                                        <span class="gTotal">${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuNetInfo["JYUCHU_NET_G"], s003Bean.jpyUnitKbn)}</span>
                                                        <input type="hidden" name="gTotal" value="${sUtil.changeDispCurrencyFormat(s003Bean.jyuchuNetInfo["JYUCHU_NET_JM"], s003Bean.jpyUnitKbn)}" />
                                                    </td>
                                                    <td class="kikan_ikkan_diff"><span class="gDiff" data-g-diff-check="1">${sUtil.changeDispCurrencyFormat(sUtil.arariString(s003Bean.jyuchuNetInfo["JYUCHU_NET_F"], s003Bean.jyuchuNetInfo["JYUCHU_NET_G"]), s003Bean.jpyUnitKbn)}</span></td>
                                                </tr>
                                            </table>
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" data-table-kanri="juchu">
                                                <!-- 受注管理・粗利 -->
                                                <tr style="height:20px;" class="fix_tb total_disp_field" data-arari="juchu">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}"></td>
                                                    <!-- 受注残(Z) -->
                                                    <td style="${tdWidth}" class="fix_tb">${sUtil.changeDispCurrencyFormat(sUtil.arariString(s003Bean.jyuchuInfoTotal["JYUCHU_SP_Z"], s003Bean.jyuchuInfoTotal["JYUCHU_NET_Z"]), s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}" >${sUtil.changeDispCurrencyFormat(sUtil.arariString(s003Bean.jyuchuInfoTotal["JYUCHU_SP_YM".concat(t.count)], s003Bean.jyuchuInfoTotal["JYUCHU_NET_YM".concat(t.count)]), s003Bean.jpyUnitKbn)}</td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}" ><span class="gTotal">${sUtil.changeDispCurrencyFormat(sUtil.arariString(s003Bean.jyuchuInfoTotal["JYUCHU_SP_G"], s003Bean.jyuchuInfoTotal["JYUCHU_NET_G"]), s003Bean.jpyUnitKbn)}</span></td>
                                                    <td style="${tdWidth}" ><span class="gDiff">${sUtil.changeDispCurrencyFormat(sUtil.arariString(jyuchuTotalSpDiff, jyuchuTotalNetDiff), s003Bean.jpyUnitKbn)}</span></td>
                                                </tr>
                                                <!-- 受注管理・Ｍ率 -->
                                                <tr style="height:20px;" class="fix_tb total_disp_field" data-mrate="juchu">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;"></td>
                                                    <!-- 受注残(Z) -->
                                                    <td class="fix_tb">${sUtil.mrate(s003Bean.jyuchuInfoTotal["JYUCHU_SP_Z"], s003Bean.jyuchuInfoTotal["JYUCHU_NET_Z"])}</td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td >${sUtil.mrate(s003Bean.jyuchuInfoTotal["JYUCHU_SP_YM".concat(t.count)], s003Bean.jyuchuInfoTotal["JYUCHU_NET_YM".concat(t.count)])}</td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td ><span class="gTotal">${sUtil.mrate(s003Bean.jyuchuInfoTotal["JYUCHU_SP_G"], s003Bean.jyuchuInfoTotal["JYUCHU_NET_G"])}</span></td>
                                                    <td ><span class="gDiff">${sUtil.mrate(jyuchuTotalSpDiff, jyuchuTotalNetDiff)}</span></td>
                                                </tr>
                                            </table>

                                            <table class="search-result-data">
                                                <tr style="height:21px">
                                                    <td style="display:none;${tdWidth}"></td>
                                                    <td style="${tdWidth}"></td>
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}; text-align:right"><%-- 売上の見込月削除ボタン表示 --%>
                                                        <c:set value="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M' && !empty targetYm.syuekiYm && authUtl.enableFlg('MTUKI_DELETE', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}" var="ymDeleteFlg"/>
                                                        <c:if test="${ymDeleteFlg}"><div class="btn-M-Delete" data-button-name="uriageMDelete" data-uriage-item-flg="${fn:escapeXml(targetYm.uriageItemFlg)}" onclick="deleteMikomi(this)">削除</div></c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <td style="${tdWidth}"></td>
                                                    <td style="${tdWidth}"></td>
                                                </tr>
                                            </table>

                                            <%-- 売上管理情報(右) レンダリングStart --%>
                                            <!-- 売上管理Table(右上)・タイトル -->
                                            <table class="table-bordered psprimis-list-table table-grid search-result-data" data-table-kanri="uriage">
                                                <tr style="${tdHeight};" data-ym-title="1">
                                                    <!-- 為替差 -->
                                                    <th style="display:none;${tdWidth}"><fmt:message key="kawaseDiff2" bundle="${label}"/></th>
                                                    <!-- 売上実績 -->
                                                    <th style="${tdWidth}"><fmt:message key="uriageJisseki" bundle="${label}"/></th>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <th style="${tdWidth}">
                                                        <c:set value="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M'}" var="ymEditFlg"/>
                                                        <c:if test="${ymEditFlg}"><%--編集モード時(入力可能)--%>
                                                            <input type="text" name="uriageSyuekiYmM" data-disp-add-flg="${fn:escapeXml(targetYm.dispAddFlg)}" class="ymFormat" value="${fn:escapeXml(targetYm.syuekiYmDate)}" maxlength="7" />
                                                            <input type="hidden" name="orgUriageSyuekiYmM" value="${fn:escapeXml(targetYm.syuekiYmDate)}" />
                                                            <input type="hidden" name="uriageSyuekiYmMDelFlg" value="0" />
                                                            <input type="hidden" name="uriageDataKbnM" value="${fn:escapeXml(targetYm.dataKbn)}" />
                                                        </c:if>
                                                        <c:if test="${!ymEditFlg}"><%--参照モード時--%>
                                                            ${fn:escapeXml(targetYm.syuekiYmDate)}
                                                            <input type="hidden" name="uriageSyuekiYmJ" data-disp-add-flg="${fn:escapeXml(targetYm.dispAddFlg)}" class="ymFormat" value="${fn:escapeXml(targetYm.syuekiYmDate)}" maxlength="7" />
                                                            <input type="hidden" name="uriageDataKbnJ" value="${fn:escapeXml(targetYm.dataKbn)}" />
                                                        </c:if>
                                                    </th>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <th style="display:none"></th>
                                                    <th style="${tdWidth2Span}" colspan="2"><fmt:message key="total" bundle="${label}"/></th>
                                                </tr>
                                                <tr>
                                                    <!-- 為替差 -->
                                                    <th style="display:none;${tdWidth}"><fmt:message key="keiyakuUriage" bundle="${label}"/></th>
                                                    <!-- 累計 -->
                                                    <th style="${tdWidth}"><fmt:message key="ruikei" bundle="${label}"/></th>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="s">
                                                    <th style="${tdWidth}">
                                                        <c:set value="${targetYm.dataKbn == 'J' && targetYm.syuekiYmDate == sUtil.exeFormatYm(detailHeader.kanjoDate)}" var="nowMonthFlg"/>
                                                        <c:if test="${nowMonthFlg}"><fmt:message key="nowMonthHassei" bundle="${label}"/></c:if>
                                                        <c:if test="${!nowMonthFlg}">${fn:escapeXml(sUtil.getJYLabel(targetYm.dataKbn))}</c:if>
                                                    </th>
                                                    </c:forEach>

                                                    <!-- 合計 -->
                                                    <th style="${tdWidth}"><fmt:message key="jissekiMikomi" bundle="${label}"/></th>
                                                    <th style="${tdWidth}"><fmt:message key="diff3" bundle="${label}"/></th>
                                                </tr>
                                            </table>

                                            <!-- 売上管理Table(右下)・SP,NET値 -->
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" data-table-kanri="uriage">
                                                <!-- 売上高 合計 -->
                                                <c:set value="${s003Bean.uriageSpTotalInfo}" var="uriageSpTotalInfo" />
                                                <tr style="height:20px;" class="fix_tb total_disp_field" id="link_spListOpen" data-total-row="1" data-total-type="sp">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}">${sUtil.changeDispCurrencyFormat(uriageSpTotalInfo["URIAGE_AMOUNT_E"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 売上実績(U) -->
                                                    <td style="${tdWidth}" class="fix_tb">${sUtil.changeDispCurrencyFormat(uriageSpTotalInfo["URIAGE_AMOUNT_U"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}">
                                                        <c:set value="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M'}" var="ymEditFlg"/>
                                                        <span class="uriageSpM_Total">${sUtil.changeDispCurrencyFormat(uriageSpTotalInfo["URIAGE_AMOUNT_YM".concat(t.count)], s003Bean.jpyUnitKbn)}</span>
                                                        <c:if test="${ymEditFlg}"><input type="hidden" name="uriageSpM_Total" data-type-name="syuekiMikomi" value="${sUtil.changeDispCurrencyFormat(uriageSpTotalInfo["URIAGE_AMOUNT_YM".concat(t.count)], s003Bean.jpyUnitKbn)}" /></c:if>
                                                    </td>
                                                    </c:forEach>

                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}">
                                                        <span class="gTotal">${sUtil.changeDispCurrencyFormat(uriageSpTotalInfo["URIAGE_AMOUNT_G"], s003Bean.jpyUnitKbn)}</span>
                                                        <input type="hidden" name="gTotal" value="${sUtil.changeDispCurrencyFormat(uriageSpTotalInfo["URIAGE_AMOUNT_JM"], s003Bean.jpyUnitKbn)}" />
                                                    </td>
                                                    <td style="${tdWidth}">
                                                        <c:set value="${sUtil.arariString(uriageSpTotalInfo['URIAGE_AMOUNT_F'], uriageSpTotalInfo['URIAGE_AMOUNT_G'])}" var="uriageSpTotalValDiff" />
                                                        <span class="gDiff">${sUtil.changeDispCurrencyFormat(uriageSpTotalValDiff, s003Bean.jpyUnitKbn)}</span>
                                                    </td>
                                                </tr>

                                                <!-- 売上レート(通貨毎にレンダリング) -->
                                                <c:set value="1" var="rateTitleDispFlg" />
                                                <c:forEach items="${s003Bean.targetCurrencyList}" var="currencyCode" varStatus="s">
                                                <c:set value="${currencyCode == s003Bean.currencyCodeEn ? '0' : '1'}" var="rowDispFlg" /><%-- 画面見やすくするため、通貨JPYの場合は行を非表示にする --%>
                                                <c:set value="${s003Bean.uriageIkkatsuSpInfo[currencyCode]}" var="uriageIkkatsuSpInfo" />
                                                <tr style="${tdHeight}" class="fix_tb" data-currency-code="${fn:escapeXml(currencyCode)}" data-rate="1" data-row-disp-Flg="${rowDispFlg}">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}" class="summary-col"></td>
                                                    <!-- 受注残(Z) -->
                                                    <td class="summary-col"></td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}" class="${targetYm.className}">
                                                        <span class="uriageRateM_Currency">${sUtil.exeFormatRateString(uriageIkkatsuSpInfo["URI_RATE_YM".concat(t.count)], currencyCode)}</span>
                                                        <c:if test="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M'}"><input type="hidden" name="uriageRateM_Currency" data-currency-code="${fn:escapeXml(currencyCode)}" data-type-name="syuekiRate" data-rate="1" data-total-name="uriageSpM_Total" value="${sUtil.exeFormatRateString(uriageIkkatsuSpInfo["URI_RATE_YM".concat(t.count)], currencyCode)}" /></c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}" class="ki-summary-col"></td>
                                                    <td style="${tdWidth}" class="kikan_ikkan_diff"></td>
                                                </tr>
                                                </c:forEach>

                                                <!-- 売上金額(通貨毎にレンダリング) -->
                                                <c:forEach items="${s003Bean.targetCurrencyList}" var="currencyCode" varStatus="s">
                                                <c:set value="${s003Bean.uriageSpInfoSum[currencyCode]}" var="uriageSpInfoSum" />
                                                <tr style="${tdHeight}" class="fix_tb" data-currency-code="${fn:escapeXml(currencyCode)}" data-barance-check-kbn="sp-total" data-sp-net-utiwake="1">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}" class="summary-col">${sUtil.changeDispCurrencyFormat(uriageSpInfoSum["URIAGE_AMOUNT_E"], s003Bean.jpyUnitKbn, currencyCode)}</td>
                                                    <!-- 売上実績(Z) -->
                                                    <td style="${tdWidth}" class="summary-col">${sUtil.changeDispCurrencyFormat(uriageSpInfoSum["URIAGE_AMOUNT_U"], s003Bean.jpyUnitKbn, currencyCode)}</td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}" class="${targetYm.className}">
                                                        <span data-uriage-curreny-sp="1">${sUtil.changeDispCurrencyFormat(uriageSpInfoSum['URIAGE_AMOUNT_YM'.concat(t.count)], s003Bean.jpyUnitKbn, currencyCode)}</span>
                                                        <c:if test="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M'}"><input type="hidden" name="uriageSpM_CurrencyTotal" data-type-name="syuekiMikomi" data-sp-net-utiwake="1" data-currency-code="${fn:escapeXml(currencyCode)}" data-total-name="uriageSpM_Total" value="${sUtil.changeDispCurrencyFormat(uriageSpInfoSum['URIAGE_AMOUNT_YM'.concat(t.count)], s003Bean.jpyUnitKbn, currencyCode)}" /></c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}" class="ki-summary-col">
                                                        <span class="gTotal">${sUtil.changeDispCurrencyFormat(uriageSpInfoSum['URIAGE_AMOUNT_G'.concat(t.count)], s003Bean.jpyUnitKbn, currencyCode)}</span>
                                                        <input type="hidden" name="gTotal" value="${sUtil.changeDispCurrencyFormat(uriageSpInfoSum['URIAGE_AMOUNT_JM'.concat(t.count)], s003Bean.jpyUnitKbn, currencyCode)}" />
                                                    </td>
                                                    <td style="${tdWidth}" class="kikan_ikkan_diff"><span class="gDiff" data-g-diff-check="1">${sUtil.changeDispCurrencyFormat(sUtil.arariString(uriageSpInfoSum["URIAGE_AMOUNT_F"], uriageSpInfoSum["URIAGE_AMOUNT_G"]), s003Bean.jpyUnitKbn, currencyCode)}</span></td>
                                                </tr>
                                                </c:forEach>

                                                <!-- 売上金額・一括見込SP(通貨毎にレンダリング) -->
                                                <c:forEach items="${s003Bean.targetCurrencyList}" var="currencyCode" varStatus="s">
                                                <c:set value="${s003Bean.uriageIkkatsuSpInfo[currencyCode]}" var="uriageIkkatsuSpInfo" />
                                                <tr style="${tdHeight}" class="fix_tb" data-currency-code="${fn:escapeXml(currencyCode)}" data-sp-net-utiwake="2">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}" class="summary-col">${sUtil.changeDispCurrencyFormat(uriageIkkatsuSpInfo["URIAGE_AMOUNT_E"], s003Bean.jpyUnitKbn, currencyCode)}</td>
                                                    <!-- 売上実績(U) -->
                                                    <td style="${tdWidth}" class="summary-col">${sUtil.changeDispCurrencyFormat(uriageIkkatsuSpInfo["URIAGE_AMOUNT_U"], s003Bean.jpyUnitKbn, currencyCode)}</td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}" class="${targetYm.className}">
                                                        <c:set value="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M'}" var="ymEditFlg"/>
                                                        <c:if test="${ymEditFlg}"><%--編集モード時(入力可能)--%>
                                                        <input type="hidden" name="uriageCurM" value="${fn:escapeXml(currencyCode)}" />
                                                        <input type="hidden" name="uriageRenbanM" value="${fn:escapeXml(s003Bean.ikkatsuSpRenban)}" />
                                                        <input type="text" name="uriageSpM" data-type-name="syuekiMikomi" data-sp-net-utiwake="2" data-currency-code="${fn:escapeXml(currencyCode)}" data-total-name="uriageSpM_CurrencyTotal" class="numberFormat" style="${numberStyle}" value="${sUtil.changeDispCurrencyFormat(uriageIkkatsuSpInfo['URIAGE_AMOUNT_YM'.concat(t.count)], s003Bean.jpyUnitKbn, currencyCode)}" />
                                                        </c:if>
                                                        <c:if test="${!ymEditFlg}"><%--参照モード時--%>
                                                        ${sUtil.changeDispCurrencyFormat(uriageIkkatsuSpInfo['URIAGE_AMOUNT_YM'.concat(t.count)], s003Bean.jpyUnitKbn, currencyCode)}
                                                        </c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}" class="ki-summary-col">
                                                        <span class="gTotal">${sUtil.changeDispCurrencyFormat(uriageIkkatsuSpInfo["URIAGE_AMOUNT_G"], s003Bean.jpyUnitKbn, currencyCode)}</span>
                                                        <input type="hidden" name="gTotal" value="${sUtil.changeDispCurrencyFormat(uriageIkkatsuSpInfo["URIAGE_AMOUNT_JM"], s003Bean.jpyUnitKbn, currencyCode)}" />
                                                    </td>
                                                    <td style="${tdWidth}" class="kikan_ikkan_diff"><span class="gDiff" data-g-diff-check="1">${sUtil.changeDispCurrencyFormat(sUtil.arariString(uriageIkkatsuSpInfo["URIAGE_AMOUNT_F"], uriageIkkatsuSpInfo["URIAGE_AMOUNT_G"]), s003Bean.jpyUnitKbn, currencyCode)}</span></td>
                                                </tr>
                                                </c:forEach>

                                                <!-- 売上高 受注通知一覧・開閉の設定行 -->
                                                <tr style="height:20px;" class="sub-total-jTuti" id="link_juchuTuchiListOpen" data-parent-link-id="link_spListOpen">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;"></td>
                                                    <td ></td>
                                                    <!-- 各月の実績・見込(売上高・年月・通貨毎のSP表示(横)) -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td></td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td ></td>
                                                    <td ></td>
                                                </tr>

                                                <!-- 売上高・年月・受注通知一覧(縦) -->
                                                <c:forEach items="${s003Bean.uriageSpRowInfoList}" var="rowList" varStatus="s">
                                                <c:set value="${(rowList.currencyCode).concat('_').concat(rowList.renban)}" var="colKeyInfo" />
                                                <c:set value="${s003Bean.uriageSpInfoJyuchuTuti[colKeyInfo]}" var="uriageSpJyuchuTuti" />
                                                <tr style="${tdHeight}" class="fix_tb" data-currency-code="${fn:escapeXml(rowList.currencyCode)}" data-sp-net-utiwake="2" data-barance-check-kbn="sp-utiwake">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}" class="summary-col">${sUtil.changeDispCurrencyFormat(uriageSpJyuchuTuti["URIAGE_AMOUNT_E"], s003Bean.jpyUnitKbn, rowList.currencyCode)}</td>
                                                    <!-- 売上実績(U) -->
                                                    <td style="${tdWidth}" class="fix_tb summary-col">${sUtil.changeDispCurrencyFormat(uriageSpJyuchuTuti["URIAGE_AMOUNT_U"], s003Bean.jpyUnitKbn, rowList.currencyCode)}</td>
                                                    <!-- 各月の実績・見込(売上高・年月・通貨毎のSP表示(横)) -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td class="${targetYm.className}">
                                                        <c:set value="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M'}" var="ymEditFlg"/>
                                                        <c:if test="${ymEditFlg}"><%--編集モード時(入力可能)--%>
                                                        <input type="hidden" name="uriageCurM" value="${fn:escapeXml(rowList.currencyCode)}" />
                                                        <input type="hidden" name="uriageRenbanM" value="${fn:escapeXml(rowList.renban)}" />
                                                        <input type="text" name="uriageSpM" data-type-name="syuekiMikomi" data-sp-net-utiwake="2" data-currency-code="${fn:escapeXml(rowList.currencyCode)}" data-total-name="uriageSpM_CurrencyTotal" class="numberFormat" style="${numberStyle}" value="${sUtil.changeDispCurrencyFormat(uriageSpJyuchuTuti["URIAGE_AMOUNT_YM".concat(t.count)], s003Bean.jpyUnitKbn, rowList.currencyCode)}" />
                                                        </c:if>
                                                        <c:if test="${!ymEditFlg}"><%--参照モード時--%>
                                                        ${sUtil.changeDispCurrencyFormat(uriageSpJyuchuTuti["URIAGE_AMOUNT_YM".concat(t.count)], s003Bean.jpyUnitKbn, rowList.currencyCode)}
                                                        </c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td class="ki-summary-col">
                                                        <span class="gTotal">${sUtil.changeDispCurrencyFormat(uriageSpJyuchuTuti["URIAGE_AMOUNT_G"], s003Bean.jpyUnitKbn, rowList.currencyCode)}</span>
                                                        <input type="hidden" name="gTotal" value="${sUtil.changeDispCurrencyFormat(uriageSpJyuchuTuti["URIAGE_AMOUNT_JM"], s003Bean.jpyUnitKbn, rowList.currencyCode)}" />
                                                    </td>
                                                    <td class="kikan_ikkan_diff"><span class="gDiff" data-g-diff-check="1">${sUtil.changeDispCurrencyFormat(sUtil.arariString(uriageSpJyuchuTuti["URIAGE_AMOUNT_F"], uriageSpJyuchuTuti["URIAGE_AMOUNT_G"]), s003Bean.jpyUnitKbn, rowList.currencyCode)}</span></td>
                                                </tr>
                                                </c:forEach>
                                            </table>

                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" data-table-kanri="uriage">
                                                <!-- 売上原価 合計 -->
                                                <tr style="height:20px;" class="fix_tb total_disp_field" id="link_netListOpen" data-total-row="1" data-total-type="net" data-barance-check-kbn="net-total">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}">${sUtil.changeDispCurrencyFormat(s003Bean.uriageNetTotalInfo["URIAGE_GENKA_E"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 売上実績(U) -->
                                                    <td style="${tdWidth}" class="fix_tb">${sUtil.changeDispCurrencyFormat(s003Bean.uriageNetTotalInfo["URIAGE_GENKA_U"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}" >
                                                        <c:set value="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M'}" var="ymEditFlg"/>
                                                        <span class="uriageNetM_Total">${sUtil.changeDispCurrencyFormat(s003Bean.uriageNetTotalInfo["URIAGE_GENKA_YM".concat(t.count)], s003Bean.jpyUnitKbn)}</span>
                                                        <c:if test="${ymEditFlg}"><input type="hidden" name="uriageNetM_Total" data-type-name="syuekiMikomi" value="${sUtil.changeDispCurrencyFormat(s003Bean.uriageNetTotalInfo["URIAGE_GENKA_YM".concat(t.count)], s003Bean.jpyUnitKbn)}" /></c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}" >
                                                        <span class="gTotal">${sUtil.changeDispCurrencyFormat(s003Bean.uriageNetTotalInfo["URIAGE_GENKA_G"], s003Bean.jpyUnitKbn)}</span>
                                                        <input type="hidden" name="gTotal" value="${sUtil.changeDispCurrencyFormat(s003Bean.uriageNetTotalInfo["URIAGE_GENKA_JM"], s003Bean.jpyUnitKbn)}" />
                                                    </td>
                                                    <td style="${tdWidth}" >
                                                        <c:set value="${sUtil.arariString(s003Bean.uriageNetTotalInfo['URIAGE_GENKA_F'], s003Bean.uriageNetTotalInfo['URIAGE_GENKA_G'])}" var="uriageNetTotalValDiff" />
                                                        <span class="gDiff">${sUtil.changeDispCurrencyFormat(uriageNetTotalValDiff, s003Bean.jpyUnitKbn)}</span>
                                                    </td>
                                                </tr>

                                                <!-- 売上原価 内訳タイトル(縦) -->
                                                <c:forEach items="${s003Bean.uriageNetCateList}" var="uriageNetCateList" varStatus="s">
                                                <c:set value="${(uriageNetCateList.categoryCode).concat('_').concat(uriageNetCateList.categoryKbn1).concat('_').concat(uriageNetCateList.categoryKbn2)}" var="cateKey"/>
                                                <c:set value="${s003Bean.uriageNetCateInfo[cateKey]}" var="uriageNetCateInfo"/>
                                                <tr style="${tdHeight};" class="fix_tb" data-sp-net-utiwake="1" ${!sUtil.isIkkatsuCategoryCode(uriageNetCateList.categoryCode) ? "data-barance-check-kbn='net-utiwake'" : ""}><%--data-barance-check-kbn='net-utiwake'のカスタム属性が付与されている行は保存時のバランスチェックを行う--%>
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}" class="summary-col">${sUtil.changeDispCurrencyFormat(uriageNetCateInfo["NET_E"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 売上実績(U) -->
                                                    <td style="${tdWidth}" class="fix_tb summary-col">${sUtil.changeDispCurrencyFormat(uriageNetCateInfo["NET_U"], s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 各月の実績・見込(売上原価 各月の見込・実績 内訳(横)) -->
                                                    <!-- 各月の実績・見込(売上高・年月・通貨毎のSP表示(横)) -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td class="${targetYm.className} ${uriageNetCateInfo["ITEM_FLG_YM".concat(t.count)] == '1' ? 'itemFlg' : ''}"><%-- 2017/11/28 #071 MOD 項番優先で計算された値のセルに色付けをする --%>
                                                        <c:set value="${s003Bean.editFlg == '1' && targetYm.dataKbn == 'M'}" var="ymEditFlg"/>
                                                        <c:if test="${ymEditFlg}"><%--編集モード時(入力可能)--%>
                                                        <input type="hidden" name="uriageCategoryCodeM" value="${fn:escapeXml(uriageNetCateList.categoryCode)}" />
                                                        <input type="hidden" name="uriageCategoryKbn1M" value="${fn:escapeXml(uriageNetCateList.categoryKbn1)}" />
                                                        <input type="hidden" name="uriageCategoryKbn2M" value="${fn:escapeXml(uriageNetCateList.categoryKbn2)}" />
                                                        <input type="text" name="uriageNetM" data-type-name="syuekiMikomi" data-sp-net-utiwake="1" data-total-name="uriageNetM_Total" class="numberFormat" style="${numberStyle}" value="${sUtil.changeDispCurrencyFormat(uriageNetCateInfo["NET_YM".concat(t.count)], s003Bean.jpyUnitKbn)}" />
                                                        </c:if>
                                                        <c:if test="${!ymEditFlg}"><%--参照モード時--%>
                                                        ${sUtil.changeDispCurrencyFormat(uriageNetCateInfo["NET_YM".concat(t.count)], s003Bean.jpyUnitKbn)}
                                                        </c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td class="ki-summary-col">
                                                        <span class="gTotal">${sUtil.changeDispCurrencyFormat(uriageNetCateInfo["NET_G"], s003Bean.jpyUnitKbn)}</span>
                                                        <input type="hidden" name="gTotal" value="${sUtil.changeDispCurrencyFormat(uriageNetCateInfo["NET_JM"], s003Bean.jpyUnitKbn)}" />
                                                    </td>
                                                    <td class="kikan_ikkan_diff"><span class="gDiff" data-g-diff-check="1">${sUtil.changeDispCurrencyFormat(sUtil.arariString(uriageNetCateInfo["NET_F"], uriageNetCateInfo["NET_G"]), s003Bean.jpyUnitKbn)}</span></td>
                                                </tr>
                                                </c:forEach>

                                            </table>
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" data-table-kanri="uriage">
                                                <!-- 売上管理・粗利 -->
                                                <tr style="height:20px;" class="fix_tb total_disp_field" data-arari="uriage">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}"></td>
                                                    <!-- 売上実績(U) -->
                                                    <td style="${tdWidth}" class="fix_tb">${sUtil.changeDispCurrencyFormat(sUtil.arariString(s003Bean.uriageSpTotalInfo["URIAGE_AMOUNT_U"], s003Bean.uriageNetTotalInfo["URIAGE_GENKA_U"]), s003Bean.jpyUnitKbn)}</td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}" >${sUtil.changeDispCurrencyFormat(sUtil.arariString(s003Bean.uriageSpTotalInfo["URIAGE_AMOUNT_YM".concat(t.count)], s003Bean.uriageNetTotalInfo["URIAGE_GENKA_YM".concat(t.count)]), s003Bean.jpyUnitKbn)}</td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}" ><span class="gTotal">${sUtil.changeDispCurrencyFormat(sUtil.arariString(s003Bean.uriageSpTotalInfo["URIAGE_AMOUNT_G"], s003Bean.uriageNetTotalInfo["URIAGE_GENKA_G"]), s003Bean.jpyUnitKbn)}</span></td>
                                                    <td style="${tdWidth}" ><span class="gDiff">${sUtil.changeDispCurrencyFormat(sUtil.arariString(uriageSpTotalValDiff, uriageNetTotalValDiff), s003Bean.jpyUnitKbn)}</span></td>
                                                </tr>

                                                <%--</tr>--%>
                                                <!-- 売上管理・Ｍ率 -->
                                                <tr style="height:20px;" class="fix_tb total_disp_field" data-mrate="uriage">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}"></td>
                                                    <!-- 売上実績(U) -->
                                                    <td style="${tdWidth}" class="fix_tb">${sUtil.mrate(s003Bean.uriageSpTotalInfo["URIAGE_AMOUNT_U"], s003Bean.uriageNetTotalInfo["URIAGE_GENKA_U"])}</td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td >${sUtil.mrate(s003Bean.uriageSpTotalInfo["URIAGE_AMOUNT_YM".concat(t.count)], s003Bean.uriageNetTotalInfo["URIAGE_GENKA_YM".concat(t.count)])}</td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td ><span class="gTotal">${sUtil.mrate(s003Bean.uriageSpTotalInfo["URIAGE_AMOUNT_G"], s003Bean.uriageNetTotalInfo["URIAGE_GENKA_G"])}</span></td>
                                                    <td ><span class="gDiff">${sUtil.mrate(uriageSpTotalValDiff, uriageNetTotalValDiff)}</span></td>
                                                </tr>

                                            </table>

                                            <%-- 回収情報(右) レンダリングStart --%>
                                            <%--<c:if test="${!empty s003Bean.kaisyuInfoList}">--%>
                                            <table class="search-result-data">
                                                <tr style="height:21px">
                                                    <td style="display:none;${tdWidth}"></td>
                                                    <td style="${tdWidth}"></td>
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}; text-align:right"><%-- 売上の見込月削除ボタン表示 --%>
                                                        <c:set value="${targetYm.dataKbn == 'J' && targetYm.syuekiYmDate == sUtil.exeFormatYm(detailHeader.kanjoDate)}" var="nowMonthFlg"/>
                                                        <c:set value="${s003Bean.editFlg == '1' && !nowMonthFlg && !empty targetYm.syuekiYm && authUtl.enableFlg('MTUKI_DELETE', detailHeader.divisionCode, s003Bean.rirekiFlg) == 1}" var="ymDeleteFlg"/>
                                                        <c:if test="${ymDeleteFlg}"><div class="btn-M-Delete" data-button-name="kaisyuDelete" onclick="deleteMikomi(this)">削除</div></c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <td style="${tdWidth}"></td>
                                                    <td style="${tdWidth}"></td>
                                                </tr>
                                            </table>

                                            <!-- 回収情報Table -->
                                            <table class="table-bordered psprimis-list-table table-grid search-result-data" data-table-kanri="kaisyu" id="kaisyuTitleRight">
                                                <tr style="${tdHeight}" data-ym-title="1">
                                                    <!-- 為替差 -->
                                                    <th style="display:none;${tdWidth}; border-bottom-style:none;"></th>
                                                    <th style="border-bottom-style:none;${tdWidth}"></th>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="s">
                                                    <th style="${tdWidth}">
                                                        <c:set value="${s003Bean.editFlg == '1' && !(targetYm.dataKbn == 'J' && targetYm.syuekiYmDate == sUtil.exeFormatYm(detailHeader.kanjoDate))}" var="ymEditFlg"/>
                                                        <c:if test="${ymEditFlg}"><%--編集モード時(入力可能)--%>
                                                            <input type="text" name="kaisyuSyuekiYmM" data-disp-add-flg="${fn:escapeXml(targetYm.dispAddFlg)}" class="ymFormat" value="${fn:escapeXml(targetYm.syuekiYmDate)}" maxlength="7" />
                                                            <input type="hidden" name="orgKaisyuSyuekiYmM" value="${fn:escapeXml(targetYm.syuekiYmDate)}" />
                                                            <input type="hidden" name="orgKaisyuDataKbnM" value="${fn:escapeXml(targetYm.dataKbn)}" />
                                                            <input type="hidden" name="kaisyuSyuekiYmMDelFlg" value="0" />
                                                        </c:if>
                                                        <c:if test="${!ymEditFlg}"><%--参照モード時(表示のみ)--%>
                                                            ${fn:escapeXml(targetYm.syuekiYmDate)}
                                                        </c:if>
                                                        <span data-kaisyu-data-kbn="1" style="display:none">${fn:escapeXml(targetYm.dataKbn)}</span>
                                                    </th>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <th style="display:none"></th>
                                                    <th style="${tdWidth2Span}" colspan="2"><fmt:message key="total" bundle="${label}"/></th>
                                                </tr>
                                                <tr data-ym-title="2" id="kaisyuDataKbnRow">
                                                    <!-- 為替差 -->
                                                    <th style="display:none;${tdWidth}; border-top-style:none;"> </th>
                                                    <th style="border-top-style:none;border-bottom-style:none;${tdWidth}"></th>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="s">
                                                    <th style="${tdWidth}">
                                                        <c:set value="${targetYm.dataKbn == 'J' && targetYm.syuekiYmDate == sUtil.exeFormatYm(detailHeader.kanjoDate)}" var="nowMonthFlg"/>
                                                        <c:if test="${nowMonthFlg}"><fmt:message key="nowMonthHassei" bundle="${label}"/></c:if>
                                                        <c:if test="${!nowMonthFlg}">
                                                            <span class="jissekiLabel noAddClear" style="${targetYm.dataKbn == 'J' ? '' : 'display:none'}"><fmt:message key="jisseki" bundle="${label}"/></span>
                                                            <span class="mikomiLabel noAddClear" style="${targetYm.dataKbn == 'J' ? 'display:none' : ''}"><fmt:message key="mikomi" bundle="${label}"/></span>
                                                        </c:if>
                                                        <!--<input type="hidden" name="kaisyuDataKbn" value="${fn:escapeXml(targetYm.dataKbn)}" style="width:20px" />-->
                                                    </th>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <th style="${tdWidth}"><fmt:message key="jissekiMikomi" bundle="${label}"/></th>
                                                    <th style="${tdWidth}"><fmt:message key="diff3" bundle="${label}"/></th>
                                                </tr>
                                            </table>
                                            <table class="table-bordered psprimis-list-table fixed-layout table-grid search-result-data" data-table-kanri="kaisyu" id="kaisyuDataRight">
                                                <!-- 回収金額(合計) -->
                                                <tr style="height:20px;" class="fix_tb total_disp_field" data-total-row="1">
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}"></td>
                                                    <td style="${tdWidth}"></td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <td style="${tdWidth}">
                                                        <span class="kaisyu_Total">${sUtil.changeDispCurrencyFormat(s003Bean.kaisyuTotalInfo["KAISYU_ENKA_ZEIKOMI_YM_TOTAL".concat(t.count)], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}</span>
                                                        <c:if test="${ymEditFlg}"><input type="hidden" name="kaisyu_Total" data-type-name="syuekiMikomi" value="${sUtil.changeDispCurrencyFormat(s003Bean.kaisyuTotalInfo["KAISYU_ENKA_ZEIKOMI_YM_TOTAL".concat(t.count)], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}" /></c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}">
                                                        <span class="gTotal">${sUtil.changeDispCurrencyFormat(s003Bean.kaisyuTotalInfo["KAISYU_ENKA_ZEIKOMI_G_TOTAL"], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}</span>
                                                        <input type="hidden" name="gTotal" value="${sUtil.changeDispCurrencyFormat(s003Bean.kaisyuTotalInfo["KAISYU_ENKA_ZEIKOMI_G_TOTAL"], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}" />
                                                    </td>
                                                    <td style="${tdWidth}"></td>
                                                </tr>

                                                <%-- 2017/11/20 #072 MOD 回収Total行追加 --%>
                                                <c:forEach items="${s003Bean.kaisyuInfoList}" var="kaisyuCurInfo" varStatus="s">
                                                <c:set value="${kaisyuCurInfo['CURRENCY_CODE']}" var="currencyCode" />
                                                <c:set value="${currencyCode != kaisyuCurInfo['PRE_CURRENCY_CODE'] ? '1' : '0'}" var="dispCurrencyCodeKbn" />
                                                <c:set value="${kaisyuCurInfo['CURRENCY_CODE_COUNT']}" var="currencyCodeRowSpanCount" />
                                                <c:set value="${currencyCode == s003Bean.currencyCodeEn ? (kaisyuCurInfo['ZEI_RATE'] > 0 ? '2' : '1') : (kaisyuCurInfo['ZEI_RATE'] > 0 ? '3' : '2')}" var="rowSpanCount" />

                                                <!-- 回収金額(通貨毎) -->
                                                <tr style="${tdHeight};" class="fix_tb" data-currency-code="${fn:escapeXml(currencyCode)}" data-sp-net-utiwake="1" >
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}" class="fix_noSpNet"></td>
                                                    <td style="${tdWidth}" class="fix_noSpNet"></td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <c:set value="${targetYm.dataKbn == 'J' && targetYm.syuekiYmDate == sUtil.exeFormatYm(detailHeader.kanjoDate)}" var="nowMonthFlg"/>
                                                    <td style="${tdWidth}" class="${nowMonthFlg ? 'kikan_ikkan_diff' : ''}">
                                                        <c:set value="${s003Bean.editFlg == '1' && !nowMonthFlg}" var="ymEditFlg"/>
                                                        <c:if test="${ymEditFlg}"><%--編集モード時(入力可能)--%>
                                                        <input type="hidden" name="kaisyuCur" value="${fn:escapeXml(currencyCode)}" />
                                                        <input type="hidden" name="kaisyuZeiKbn" value="${fn:escapeXml(kaisyuCurInfo["ZEI_KBN"])}" />
                                                        <input type="hidden" name="kaisyuKinsyuKbn" value="${fn:escapeXml(kaisyuCurInfo["KINSYU_KBN"])}" />
                                                        <input type="hidden" name="kaisyuKbn" value="${fn:escapeXml(kaisyuCurInfo["KAISYU_KBN"])}" />
                                                        <input type="text" name="kaisyu" data-type-name="syuekiMikomi"  data-total-name="kaisyu_Total" data-target-calc="${currencyCode == s003Bean.currencyCodeEn ? '1' : '0'}" data-currency-code="${fn:escapeXml(currencyCode)}" data-zei-rate="${fn:escapeXml(kaisyuCurInfo["ZEI_RATE"])}" class="numberFormat" style="${numberStyle}" value="${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_AMOUNT_YM".concat(t.count)], s003Bean.jpyUnitKbn, currencyCode)}" />
                                                        <c:if test="${currencyCode == s003Bean.currencyCodeEn}">
                                                            <input type="hidden" name="kaisyuEnka" value="" /><%-- 円貨(通貨JPY)の場合、項目の数を合わせるための隠し項目 --%>
                                                        </c:if>
                                                        </c:if>
                                                        <c:if test="${!ymEditFlg}"><%--参照モード時--%>
                                                        ${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_AMOUNT_YM".concat(t.count)], s003Bean.jpyUnitKbn, currencyCode)}
                                                        </c:if>
                                                    </td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}" class="ki-summary-col">
                                                        <span class="gTotal">${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_AMOUNT_G"], s003Bean.jpyUnitKbn, currencyCode)}</span>
                                                        <input type="hidden" name="gTotal" value="${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_AMOUNT_G"], s003Bean.jpyUnitKbn, currencyCode)}" />
                                                    </td>
                                                    <td style="${tdWidth}" class="kikan_ikkan_diff"></td>
                                                </tr>

                                                <!-- 回収金額 円通貨以外での円貨入力 -->
                                                <c:if test="${currencyCode != s003Bean.currencyCodeEn}">
                                                <tr style="${tdHeight};" class="fix_tb" data-currency-code="${fn:escapeXml(currencyCode)}" data-sp-net-utiwake="1" >
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}" class="fix_noSpNet"></td>
                                                    <td style="${tdWidth}" class="fix_noSpNet"></td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <c:set value="${targetYm.dataKbn == 'J' && targetYm.syuekiYmDate == sUtil.exeFormatYm(detailHeader.kanjoDate)}" var="nowMonthFlg"/>
                                                    <td style="${tdWidth}" class="${nowMonthFlg ? 'kikan_ikkan_diff' : ''}">
                                                        <c:set value="${s003Bean.editFlg == '1' && !nowMonthFlg}" var="ymEditFlg"/>
                                                        <c:if test="${ymEditFlg}"><%--編集モード時(入力可能)--%>
                                                        <input type="text" name="kaisyuEnka" data-type-name="syuekiMikomi" data-total-name="kaisyu_Total" data-target-calc="1" data-currency-code="${fn:escapeXml(s003Bean.currencyCodeEn)}" data-zei-rate="${fn:escapeXml(kaisyuCurInfo["ZEI_RATE"])}" class="numberFormat" style="${numberStyle}" value="${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_ENKA_AMOUNT_YM".concat(t.count)], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}" />
                                                        </c:if>
                                                        <c:if test="${!ymEditFlg}"><%--参照モード時--%>
                                                        ${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_ENKA_AMOUNT_YM".concat(t.count)], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}
                                                        </c:if>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}" class="ki-summary-col">
                                                        <span class="gTotal">${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_ENKA_AMOUNT_G"], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}</span>
                                                        <input type="hidden" name="gTotal" value="${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_ENKA_AMOUNT_G"], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}" />
                                                    </td>
                                                    <td style="${tdWidth}" class="kikan_ikkan_diff"></td>
                                                </tr>
                                                </c:if>

                                                <!-- 回収金額 税額 --><%-- 2017/11/20 #072 ADD 回収Total行追加 --%>
                                                <c:if test="${kaisyuCurInfo['ZEI_RATE'] > 0}">
                                                <tr style="${tdHeight};" class="fix_tb"   data-currency-code="${fn:escapeXml(currencyCode)}" data-sp-net-utiwake="1" >
                                                    <!-- 為替差 -->
                                                    <td style="display:none;${tdWidth}" class="fix_noSpNet"></td>
                                                    <td style="${tdWidth}" class="fix_noSpNet"></td>
                                                    <!-- 各月の実績・見込 -->
                                                    <c:forEach items="${s003Bean.targetYm}" var="targetYm" varStatus="t">
                                                    <c:set value="${targetYm.dataKbn == 'J' && targetYm.syuekiYmDate == sUtil.exeFormatYm(detailHeader.kanjoDate)}" var="nowMonthFlg"/>
                                                    <td style="${tdWidth}" class="${nowMonthFlg ? 'kikan_ikkan_diff' : ''}">
                                                        <span class="enkaZei" data-type-name="syuekiMikomi">${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_ENKA_ZEI_YM".concat(t.count)], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}</span>
                                                        <input type="hidden" data-type-name="syuekiMikomi" data-total-name="kaisyu_Total" data-target-calc="2" value="" />
                                                    </td>
                                                    </c:forEach>
                                                    <!-- 合計 -->
                                                    <td style="${tdWidth}" class="ki-summary-col">
                                                        <span class="gTotal">${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_ENKA_ZEI_G"], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}</span>
                                                        <input type="hidden" name="gTotal" value="${sUtil.changeDispCurrencyFormat(kaisyuCurInfo["KAISYU_ENKA_ZEI_G"], s003Bean.jpyUnitKbn, s003Bean.currencyCodeEn)}" />
                                                    </td>
                                                    <td style="${tdWidth}" class="kikan_ikkan_diff"></td>
                                                </tr>
                                                </c:if>

                                                </c:forEach>
                                            </table>
                                            <%--</c:if>--%>

                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>  <!-- search-result -->

    <input type="hidden" id="saveBalanceFlg" name="saveBalanceFlg" value="0" />
    <input type="hidden" name="updateNewDataKbn" value="" id="updateNewDataKbn" />

    </form>

    </div> <!-- search frame -->

    </div>  <!-- container -->
    </div>  <!-- wrap -->

    <c:import url="${componentFullPath}jslink.xhtml" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/view/s003.js?var=${utl.random()}"></script>

    </body>
</html>
