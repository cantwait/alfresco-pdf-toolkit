/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
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
 */

package org.alfresco.extension.pdftoolkit.web.bean.actions.handlers;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFSplitActionExecuter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.wizard.IWizardBean;
import org.alfresco.web.bean.actions.handlers.BaseActionHandler;

/**
 * Action handler for the "pdf-split" action.
 * 
 * @author Jared Ottley
 */

public class PDFSplitActionHandler extends BaseActionHandler
{
   private static final long serialVersionUID = -8277555214101165061L;
  
   protected static final String PROP_PDF_SPLIT_FREQUENCY = "PDFSplitFrequency";

   public String getJSPPath()
   {
	   return getJSPPath(PDFSplitActionExecuter.NAME);
   }

   public void prepareForSave(Map<String, Serializable> actionProps,
         Map<String, Serializable> repoProps)
   { 
      // add the destination space id to the action properties
      NodeRef destNodeRef = (NodeRef)actionProps.get(PROP_DESTINATION);
      repoProps.put(PDFSplitActionExecuter.PARAM_DESTINATION_FOLDER, destNodeRef);
      
      //add frequency of split to the action properties
	  String frequencyNodeName = actionProps.get(PROP_PDF_SPLIT_FREQUENCY).toString();
	  repoProps.put(PDFSplitActionExecuter.PARAM_SPLIT_FREQUENCY, frequencyNodeName);
   }

   public void prepareForEdit(Map<String, Serializable> actionProps,
         Map<String, Serializable> repoProps)
   {
	   NodeRef destNodeRef = (NodeRef)repoProps.get(PDFSplitActionExecuter.PARAM_DESTINATION_FOLDER);
	   actionProps.put(PROP_DESTINATION, destNodeRef);
	   
	  String newNodeName = actionProps.get(PROP_PDF_SPLIT_FREQUENCY).toString();
	  repoProps.put(PDFSplitActionExecuter.PARAM_SPLIT_FREQUENCY, newNodeName);
   }

   public String generateSummary(FacesContext context, IWizardBean wizard,
         Map<String, Serializable> actionProps)
   {
      NodeRef space = (NodeRef)actionProps.get(PROP_DESTINATION);
      String name = Repository.getNameForNode(
            Repository.getServiceRegistry(context).getNodeService(), space);
      
      String frequencyNodeName = actionProps.get(PROP_PDF_SPLIT_FREQUENCY).toString();
      
      String messageFormat = null;
	  
	  if ((!frequencyNodeName.equals("")) && (frequencyNodeName != null))
	  {
		  messageFormat = MessageFormat.format(Application.getMessage(context, "action_pdf_split_frequency"),
		            new Object[] {frequencyNodeName, name}); 
	  }
	  else
	  {
		  messageFormat = MessageFormat.format(Application.getMessage(context, "action_pdf_split"),
		            new Object[] {name});  
	  }
	  
	  return messageFormat;

      
   }
}