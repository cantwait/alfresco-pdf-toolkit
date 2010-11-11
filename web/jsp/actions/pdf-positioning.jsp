										<tr>
											<td class="paddingRow"></td>
										</tr>
										<tr>
											<td><h:outputText value="#{customMsg.pdfstamp_position}" />:</td>
											<td>
												<h:selectOneMenu
													id="WatermarkPosition"
 													value="#{WizardManager.bean.actionProperties.StampPosition}">
  													<f:selectItems value="#{WizardManager.bean.actionProperties.PositionOptions}" />
												</h:selectOneMenu>
											</td>
										</tr>
										<tr>
											<td><h:outputText value="#{customMsg.pdfstamp_location_x}" />:</td>
											<td><h:inputText id="location_x" size="4" 
												value="#{WizardManager.bean.actionProperties.LocationX}"/>
											</td>
										</tr>
										<tr>
											<td class="paddingRow"></td>
										</tr>
										<tr>
											<td><h:outputText value="#{customMsg.pdfstamp_location_y}" />:</td>
											<td><h:inputText id="location_y" size="4" 
												value="#{WizardManager.bean.actionProperties.LocationY}"/>
											</td>
										</tr>
										<tr>
											<td class="paddingRow"></td>
										</tr>
