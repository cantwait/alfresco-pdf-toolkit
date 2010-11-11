<%--
* Copyright (C) 2005-2007 Alfresco Software Limited.

* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

* As a special exception to the terms and conditions of version 2.0 of
* the GPL, you may redistribute this Program in connection with Free/Libre
* and Open Source Software ("FLOSS") applications as described in Alfresco's
* FLOSS exception.  You should have recieved a copy of the text describing
* the FLOSS exception, and it is also available here:
* http://www.alfresco.com/legal/licensing"
--%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a"%>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r"%>

<%@ page buffer="32kb" contentType="text/html;charset=UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ page import="org.alfresco.web.ui.common.PanelGenerator"%>

<r:page titleId="title_action_pdf_watermark">

	<f:view>

		<%-- load a bundle of properties with I18N strings --%>
		<f:loadBundle basename="alfresco.messages.webclient" var="msg" />
		<f:loadBundle basename="alfresco.messages.pdf-toolkit"
			var="customMsg" />

		<h:form acceptcharset="UTF-8" id="pdf-watermark-action">

			<%-- Main outer table --%>
			<table cellspacing="0" cellpadding="2">

				<%-- Title bar --%>
				<tr>
					<td colspan="2"><%@ include file="../parts/titlebar.jsp"%>
					</td>
				</tr>

				<%-- Main area --%>
				<tr valign="top">
					<%-- Shelf --%>
					<td><%@ include file="../parts/shelf.jsp"%>
					</td>

					<%-- Work Area --%>
					<td width="100%">
					<table cellspacing="0" cellpadding="0" width="100%">
						<%-- Breadcrumb --%>
						<%@ include file="../parts/breadcrumb.jsp"%>

						<%-- Status and Actions --%>
						<tr>
							<td
								style="background-image: url(<%=request.getContextPath()%>/ images/ parts/ statuspanel_4 . gif )"
								width="4"></td>
							<td bgcolor="#dfe6ed"><%-- Status and Actions inner contents table --%>
							<%-- Generally this consists of an icon, textual summary and actions for the current object --%>
							<table cellspacing="4" cellpadding="0" width="100%">
								<tr>
									<td width="32"><h:graphicImage id="wizard-logo"
										url="/images/icons/new_rule_large.gif" /></td>
									<td>
									<div class="mainTitle"><h:outputText
										value="#{WizardManager.title}" /></div>
									<div class="mainSubText"><h:outputText
										value="#{WizardManager.description}" /></div>
									</td>
								</tr>
							</table>

							</td>
							<td
								style="background-image: url(<%=request.getContextPath()%>/ images/ parts/ statuspanel_6 . gif )"
								width="4"></td>
						</tr>

						<%-- separator row with gradient shadow --%>
						<tr>
							<td><img
								src="<%=request.getContextPath()%>/images/parts/statuspanel_7.gif"
								width="4" height="9"></td>
							<td
								style="background-image: url(<%=request.getContextPath()%>/ images/ parts/ statuspanel_8 . gif )"></td>
							<td><img
								src="<%=request.getContextPath()%>/images/parts/statuspanel_9.gif"
								width="4" height="9"></td>
						</tr>

						<%-- Details --%>
						<tr valign=top>
							<td
								style="background-image: url(<%=request.getContextPath()%>/ images/ parts/ whitepanel_4 . gif )"
								width="4"></td>
							<td>
							<table cellspacing="0" cellpadding="3" border="0" width="100%">
								<tr>
									<td width="100%" valign="top"><a:errors
										message="#{msg.error_wizard}" styleClass="errorMessage" /> <%
     PanelGenerator.generatePanelStart(out, request
                         .getContextPath(), "white", "white");
 %>
									<table cellpadding="2" cellspacing="2" border="0" width="100%">
										<tr>
											<td colspan="2" class="mainSubTitle"><h:outputText
												value="#{msg.set_action_values}" /></td>
										</tr>
										<tr>
											<td class="paddingRow"></td>
										</tr>
										<tr>
											<td><h:outputText value="#{customMsg.pdfwatermark_type}" /></td>
											<td>
												<h:selectOneRadio id="watermarkType"
													value="#{WizardManager.bean.actionProperties.WatermarkType}">
													<f:selectItems value="#{WizardManager.bean.actionProperties.TypeOptions}" />
												</h:selectOneRadio>
											</td>
										</tr>
										<tr>
											<td colspan="2" class="paddingRow"></td>
										</tr>
										<tr>
											<td><h:outputText value="#{customMsg.pdfwatermark_image}" />:</td>
											<td><r:ajaxFileSelector id="fileSelector"
												label="#{customMsg.pdfwatermark_image_selector}"
												value="#{WizardManager.bean.actionProperties.WatermarkImage}"
												initialSelection="#{NavigationBean.currentNode.nodeRefAsString}"
												styleClass="selector" /></td>
										</tr>
										<tr>
											<td colspan="2" class="paddingRow"></td>
										</tr>
										<tr>
											<td><h:outputText value="#{customMsg.pdfwatermark_text}" />:</td>
											<td><h:inputTextarea id="textArea" rows="4" cols="30" 
												value="#{WizardManager.bean.actionProperties.WatermarkText}"/>
											</td>
										</tr>
										<tr>
											<td class="paddingRow"></td>
										</tr>
										<tr>
											<td><h:outputText value="#{customMsg.pdfwatermark_font}" />:</td>
											<td>
												<h:selectOneMenu
													id="WatermarkFont"
 													value="#{WizardManager.bean.actionProperties.WatermarkFont}">
  													<f:selectItems value="#{WizardManager.bean.actionProperties.FontOptions}" />
												</h:selectOneMenu>
											</td>
										</tr>
										<tr>
											<td class="paddingRow"></td>
										</tr>
										<tr>
											<td><h:outputText value="#{customMsg.pdfwatermark_size}" />:</td>
											<td>
												<h:selectOneMenu
													id="WatermarkSize"
 													value="#{WizardManager.bean.actionProperties.WatermarkSize}">
  													<f:selectItems value="#{WizardManager.bean.actionProperties.SizeOptions}" />
												</h:selectOneMenu>
											</td>
										</tr>
										<tr>
											<td class="paddingRow"></td>
										</tr>
										<tr>
											<td><h:outputText value="#{customMsg.pdfwatermark_pages}" />:</td>
											<td>
												<h:selectOneMenu
													id="WatermarkPages"
 													value="#{WizardManager.bean.actionProperties.WatermarkPages}">
  													<f:selectItems value="#{WizardManager.bean.actionProperties.PageOptions}" />
												</h:selectOneMenu>
											</td>
										</tr>
										<!--  include the positioning elements -->
										<%@ include file="pdf-positioning.jsp" %>
										<tr>
											<td class="paddingRow"></td>
										</tr>
										<tr>
											<td><h:outputText value="#{customMsg.pdfwatermark_depth}" />:</td>
											<td>
												<h:selectOneRadio
 													value="#{WizardManager.bean.actionProperties.WatermarkDepth}">
  													<f:selectItems value="#{WizardManager.bean.actionProperties.DepthOptions}" />
												</h:selectOneRadio>
											</td>
										</tr>
										<tr>
											<td colspan="2" class="paddingRow"></td>
										</tr>
										<tr>
											<td><h:outputText value="#{msg.destination}" />:</td>
											<td><r:ajaxFolderSelector id="spaceSelector"
												label="#{msg.select_destination_prompt}"
												value="#{WizardManager.bean.actionProperties.destinationLocation}"
												initialSelection="#{NavigationBean.currentNode.nodeRefAsString}"
												styleClass="selector" /></td>
										</tr>
										<tr>
											<td class="paddingRow"></td>
										</tr>
										<tr>
											<td class="paddingRow"></td>
										</tr>
									</table>
									<%
									    PanelGenerator.generatePanelEnd(out, request
									                        .getContextPath(), "white");
									%>
									</td>

									<td valign="top">
									<%
									    PanelGenerator.generatePanelStart(out, request
									                        .getContextPath(), "greyround", "#F5F5F5");
									%>
									<table cellpadding="1" cellspacing="1" border="0">
										<tr>
											<td align="center"><h:commandButton value="#{msg.ok}"
												action="#{WizardManager.bean.addAction}"
												styleClass="wizardButton"
												disabled="#{WizardManager.bean.actionProperties.destinationLocation == null}" />
											</td>
										</tr>
										<tr>
											<td align="center"><h:commandButton
												value="#{msg.cancel_button}"
												action="#{WizardManager.bean.cancelAddAction}"
												styleClass="wizardButton" /></td>
										</tr>
									</table>
									<%
									    PanelGenerator.generatePanelEnd(out, request
									                        .getContextPath(), "greyround");
									%>
									</td>
								</tr>
							</table>
							</td>
							<td
								style="background-image: url(<%=request.getContextPath()%>/ images/ parts/ whitepanel_6 . gif )"
								width="4"></td>
						</tr>

						<%-- separator row with bottom panel graphics --%>
						<tr>
							<td><img
								src="<%=request.getContextPath()%>/images/parts/whitepanel_7.gif"
								width="4" height="4"></td>
							<td width="100%" align="center"
								style="background-image: url(<%=request.getContextPath()%>/ images/ parts/ whitepanel_8 . gif )"></td>
							<td><img
								src="<%=request.getContextPath()%>/images/parts/whitepanel_9.gif"
								width="4" height="4"></td>
						</tr>

					</table>
					</td>
				</tr>
			</table>

		</h:form>

	</f:view>

</r:page>
