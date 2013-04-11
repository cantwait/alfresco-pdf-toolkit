function deletepage()
{
	/*
	 * This script depends on the test data from the bootstrap ACP file being loaded into
	 * the PDF Toolkit Examples folder in the repository.  This can be done by enabling the bootstrap
	 * load of this test data in context/bootstrap-context.xml
	 */
	
	// Get the PDF to be modified
	var pdf = companyhome.childByNamePath("PDF Toolkit Examples/Assets/PDF Toolkit Test Document.pdf");
	
	// Get the destination folder for the modified document
	var destination = companyhome.childByNamePath("PDF Toolkit Examples");
	
	// Get the append action
	var deleteAction = actions.create("pdf-delete-page");
	
	// Set the action parameters
	deleteAction.parameters["delete-pages"] = "2";
	deleteAction.parameters["destination-name"] = "pages-deleted";
	
	// Execute the append action
	deleteAction.execute(pdf);
}

deletepage();