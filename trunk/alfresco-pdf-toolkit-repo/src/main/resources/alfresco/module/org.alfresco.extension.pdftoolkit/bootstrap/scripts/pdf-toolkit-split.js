function split()
{
	/*
	 * This script depends on the test data from the bootstrap ACP file being loaded into
	 * the PDF Toolkit Examples folder in the repository.  This can be done by enabling the bootstrap
	 * load of this test data in context/bootstrap-context.xml
	 */
	
	// Get the PDF to be split
	var pdf = companyhome.childByNamePath("PDF Toolkit Examples/Assets/PDF Toolkit Test Document.pdf");
	
	// Get the destination folder for the split documents
	var destination = companyhome.childByNamePath("PDF Toolkit Examples");
	
	// Get the split action
	var splitAction = actions.create("pdf-split");
	
	// Set the action parameters
	
	// Execute the split action
	splitAction.execute(pdf);
}

split();