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

import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFAppendActionExecuter;
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

public class PDFAppendActionHandler extends BaseActionHandler
{
   private static final long serialVersionUID = 8277555214101165061L;
  
   protected static final String PROP_PDF_APPEND_TARGET = "PDFAppendTarget";
   protected static final String PROP_PDF_APPEND_NAME = "PDFAppendName";

   public String getJSPPath()
   {
	   return getJSPPath(PDFAppendActionExecuter.NAME);
   }

   public void prepareForSave(Map<String, Serializable> actionProps,
         Map<String, Serializable> repoProps)
   { 
      //add new nodes name to the action properties
	  String newNodeName = actionProps.get(PROP_PDF_APPEND_NAME).toString();
	  repoProps.put(PDFAppendActionExecuter.PARAM_DESTINATION_NAME, newNodeName);
	   
	  //add the target Node id to the action properties
	  String [] targetNodes = (String [])actionProps.get(PROP_PDF_APPEND_TARGET);
	  NodeRef targetNodeRef = new NodeRef(targetNodes[0]);
	  repoProps.put(PDFAppendActionExecuter.PARAM_TARGET_NODE, targetNodeRef);
	  
	  // add the destination space id to the action properties
      NodeRef destNodeRef = (NodeRef)actionProps.get(PROP_DESTINATION);
      repoProps.put(PDFAppendActionExecuter.PARAM_DESTINATION_FOLDER, destNodeRef);
   }

   public void prepareForEdit(Map<String, Serializable> actionProps,
         Map<String, Serializable> repoProps)
   {
	   repoProps.put(PDFAppendActionExecuter.PARAM_DESTINATION_NAME, PROP_PDF_APPEND_NAME);
		   
	   NodeRef targetNodeRef = (NodeRef)actionProps.get(PROP_PDF_APPEND_TARGET);
	   repoProps.put(PDFAppendActionExecuter.PARAM_TARGET_NODE, targetNodeRef);
	   
	   NodeRef destNodeRef = (NodeRef)repoProps.get(PDFSplitActionExecuter.PARAM_DESTINATION_FOLDER);
	   actionProps.put(PROP_DESTINATION, destNodeRef);
   }

   public String generateSummary(FacesContext context, IWizardBean wizard,
         Map<String, Serializable> actionProps)
   {
      String[] targetNodes = (String [])actionProps.get(PROP_PDF_APPEND_TARGET);
      NodeRef targetNode = new NodeRef(targetNodes[0]);
	  String targetNodeName = Repository.getNameForNode(
			Repository.getServiceRegistry(context).getNodeService(), targetNode);
	  
      String newNodeName = actionProps.get(PROP_PDF_APPEND_NAME).toString();
      
      NodeRef space = (NodeRef)actionProps.get(PROP_DESTINATION);
      String spaceName = Repository.getNameForNode(
            Repository.getServiceRegistry(context).getNodeService(), space);

      return MessageFormat.format(Application.getMessage(context, "action_pdf_append"),
            new Object[] {targetNodeName, newNodeName, spaceName});
   }
}