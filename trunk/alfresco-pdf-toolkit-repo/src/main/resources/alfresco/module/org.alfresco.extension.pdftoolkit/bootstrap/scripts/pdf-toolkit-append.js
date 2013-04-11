function append()
{
	/*
	 * This script depends on the test data from the bootstrap ACP file being loaded into
	 * the PDF Toolkit Examples folder in the repository.  This can be done by enabling the bootstrap
	 * load of this test data in context/bootstrap-context.xml
	 */
	
	// Get the PDF to be appended to
	var pdf = companyhome.childByNamePath("PDF Toolkit Examples/Assets/PDF Toolkit Test Document.pdf");
	
	// Get the PDF to append
	var append = companyhome.childByNamePath("PDF Toolkit Examples/Assets/PDF Toolkit Inserted Page.pdf");
	
	// Get the destination folder for the appended document
	var destination = companyhome.childByNamePath("PDF Toolkit Examples");
	
	// Get the append action
	var appendAction = actions.create("pdf-append");
	
	// Set the action parameters
	appendAction.parameters["destination-folder"] = destination;
	appendAction.parameters["target-node"] = append;
	appendAction.parameters["destination-name"] = "appended";
	
	// Execute the append action
	appendAction.execute(pdf);
}

append();