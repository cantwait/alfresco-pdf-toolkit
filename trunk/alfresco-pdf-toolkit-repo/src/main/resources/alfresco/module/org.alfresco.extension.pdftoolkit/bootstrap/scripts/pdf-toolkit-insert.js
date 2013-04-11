function insert()
{
	/*
	 * This script depends on the test data from the bootstrap ACP file being loaded into
	 * the PDF Toolkit Examples folder in the repository.  This can be done by enabling the bootstrap
	 * load of this test data in context/bootstrap-context.xml
	 */
	
	// Get the PDF to be inserted to
	var pdf = companyhome.childByNamePath("PDF Toolkit Examples/Assets/PDF Toolkit Test Document.pdf");
	
	// Get the PDF to insert 
	var insert = companyhome.childByNamePath("PDF Toolkit Examples/Assets/PDF Toolkit Inserted Page.pdf");
	
	// Get the destination folder for the merged document
	var destination = companyhome.childByNamePath("PDF Toolkit Examples");
	
	// Get the insert action
	var insertAction = actions.create("pdf-insert-at-page");
	
	// Set the action parameters
	
	// Execute the insert action
	insertAction.execute(pdf);
}

insert();