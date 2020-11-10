package com.example.simplerestapis.service;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.example.simplerestapis.models.SalesforceOrg;
import com.sforce.soap.metadata.*;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Service
public class FileBasedDeployAndRetrieve {

	@Autowired
	private SalesforceService sfService;
	
	private MetadataConnection metadataConnection;

	static BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));

	// one second in milliseconds
	private static final long ONE_SECOND = 1000;
	// maximum number of attempts to retrieve the results
	private static final int MAX_NUM_POLL_REQUESTS = 50;

	// manifest file that controls which components get retrieved
	private static final String MANIFEST_FILE = "package.xml";
	private static final String ZIP_FILE = "retrieveResults.zip";
	private static final double API_VERSION = 29.0;

	public void createMetadataConnection(String type, String orgId) throws RemoteException, Exception {
		final ConnectorConfig metadataConfig = new ConnectorConfig();
		metadataConfig.setServiceEndpoint("https://ap16.salesforce.com/services/Soap/m/49.0/");
		SalesforceOrg sfOrg =  sfService.getOrg(orgId);
		String token;
		try {
			token = sfOrg.getAccessToken();
			System.out.println("token id... " + token);
			metadataConfig.setSessionId(token);
			this.metadataConnection = new MetadataConnection(metadataConfig);
			
			if(type == "retrieve" )
				this.retrieveZip();
			else 
				this.deployZip();
		} catch (Exception e) {
			System.out.println("Token experied...... Generating new token");
			token = sfService.renewAccess(orgId);
			metadataConfig.setSessionId(token);
			this.metadataConnection = new MetadataConnection(metadataConfig);
			if(type == "retrieve" )
				this.retrieveZip();
			else 
				this.deployZip();
		}
	}

	private void retrieveZip() throws RemoteException, Exception {
		RetrieveRequest retrieveRequest = new RetrieveRequest();
		// The version in package.xml overrides the version in RetrieveRequest
		retrieveRequest.setApiVersion(API_VERSION);
		setUnpackaged(retrieveRequest);
		
		
		// Start the retrieve operation
		AsyncResult asyncResult = metadataConnection.retrieve(retrieveRequest);
		String asyncResultId = asyncResult.getId();
		
		// Wait for the retrieve to complete
		int poll = 0;
		long waitTimeMilliSecs = ONE_SECOND;
		RetrieveResult result = null;
		do {
			Thread.sleep(waitTimeMilliSecs);
			// Double the wait time for the next iteration
			waitTimeMilliSecs *= 2;
			if (poll++ > MAX_NUM_POLL_REQUESTS) {
				throw new Exception("Request timed out.  If this is a large set "
						+ "of metadata components, check that the time allowed "
						+ "by MAX_NUM_POLL_REQUESTS is sufficient.");
			}
			result = metadataConnection.checkRetrieveStatus(asyncResultId, true);
			System.out.println("Retrieve Status: " + result.getStatus());
		} while (!result.isDone());

		if (result.getStatus() == RetrieveStatus.Failed) {
			throw new Exception(result.getErrorStatusCode() + " msg: " + result.getErrorMessage());
		} else if (result.getStatus() == RetrieveStatus.Succeeded) {
			// Print out any warning messages
			StringBuilder buf = new StringBuilder();
			if (result.getMessages() != null) {
				for (RetrieveMessage rm : result.getMessages()) {
					buf.append(rm.getFileName() + " - " + rm.getProblem());
				}
			}
			if (buf.length() > 0) {
				System.out.println("Retrieve warnings:\n" + buf);
			}

			// Write the zip to the file system
			System.out.println("Writing results to zip file");
			ByteArrayInputStream bais = new ByteArrayInputStream(result.getZipFile());
			File resultsFile = new File("retrieveResults.zip");
			FileOutputStream os = new FileOutputStream(resultsFile);
			try {
				ReadableByteChannel src = Channels.newChannel(bais);
				FileChannel dest = os.getChannel();
				copy(src, dest);

				System.out.println("Results written to " + resultsFile.getAbsolutePath());
			} finally {
				os.close();
			}
		}
	}

	/**
	 * Helper method to copy from a readable channel to a writable channel, using an
	 * in-memory buffer.
	 */
	private void copy(ReadableByteChannel src, WritableByteChannel dest) throws IOException {
		// Use an in-memory byte buffer
		ByteBuffer buffer = ByteBuffer.allocate(8092);
		while (src.read(buffer) != -1) {
			buffer.flip();
			while (buffer.hasRemaining()) {
				dest.write(buffer);
			}
			buffer.clear();
		}
	}

	private void setUnpackaged(RetrieveRequest request) throws Exception {
		// Edit the path, if necessary, if your package.xml file is located elsewhere
		File unpackedManifest = new File(MANIFEST_FILE);
		System.out.println("Manifest file: " + unpackedManifest.getAbsolutePath());

		if (!unpackedManifest.exists() || !unpackedManifest.isFile())
			throw new Exception("Should provide a valid retrieve manifest " + "for unpackaged content. "
					+ "Looking for " + unpackedManifest.getAbsolutePath());

		// Note that we populate the _package object by parsing a manifest file here.
		// You could populate the _package based on any source for your
		// particular application.
		com.sforce.soap.metadata.Package p = parsePackage(unpackedManifest);
		request.setUnpackaged(p);
	}

	private com.sforce.soap.metadata.Package parsePackage(File file) throws Exception {
		try {
			InputStream is = new FileInputStream(file);
			List<PackageTypeMembers> pd = new ArrayList<PackageTypeMembers>();
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Element d = db.parse(is).getDocumentElement();
			for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
				if (c instanceof Element) {
					Element ce = (Element) c;
					//
					NodeList namee = ce.getElementsByTagName("name");
					if (namee.getLength() == 0) {
						// not
						continue;
					}
					String name = namee.item(0).getTextContent();
					NodeList m = ce.getElementsByTagName("members");
					List<String> members = new ArrayList<String>();
					for (int i = 0; i < m.getLength(); i++) {
						Node mm = m.item(i);
						members.add(mm.getTextContent());
					}
					PackageTypeMembers pdi = new PackageTypeMembers();
					pdi.setName(name);
					pdi.setMembers(members.toArray(new String[members.size()]));
					pd.add(pdi);
				}
			}
			com.sforce.soap.metadata.Package r = new com.sforce.soap.metadata.Package();
			r.setTypes(pd.toArray(new PackageTypeMembers[pd.size()]));
			r.setVersion(API_VERSION + "");
			return r;
		} catch (ParserConfigurationException pce) {
			throw new Exception("Cannot create XML parser", pce);
		} catch (IOException ioe) {
			throw new Exception(ioe);
		} catch (SAXException se) {
			throw new Exception(se);
		}
	}

	// The sample client application retrieves the user's login credentials.
	// Helper function for retrieving user input from the console
	String getUserInput(String prompt) {
		System.out.print(prompt);
		try {
			return rdr.readLine();
		} catch (IOException ex) {
			return null;
		}
	}
	public void deployZip()
	        throws RemoteException, Exception
	    {
	        byte zipBytes[] = readZipFile();
	        DeployOptions deployOptions = new DeployOptions();
	        deployOptions.setPerformRetrieve(false);
	        deployOptions.setRollbackOnError(true);
	        AsyncResult asyncResult = metadataConnection.deploy(zipBytes, deployOptions);
	        String asyncResultId = asyncResult.getId();
	        
	        // Wait for the deploy to complete
	        int poll = 0;
	        long waitTimeMilliSecs = ONE_SECOND;
	        DeployResult deployResult = null;
	        boolean fetchDetails;
	        do {
	            Thread.sleep(waitTimeMilliSecs);
	            // double the wait time for the next iteration
	            waitTimeMilliSecs *= 2;
	            if (poll++ > MAX_NUM_POLL_REQUESTS) {
	                throw new Exception("Request timed out. If this is a large set " +
	                        "of metadata components, check that the time allowed by " +
	                        "MAX_NUM_POLL_REQUESTS is sufficient.");
	            }
	            
	            // Fetch in-progress details once for every 3 polls
	            fetchDetails = (poll % 3 == 0);
	            deployResult = metadataConnection.checkDeployStatus(asyncResultId, fetchDetails);
	            System.out.println("Status is: " + deployResult.getStatus());
	            if (!deployResult.isDone() && fetchDetails) {
	                printErrors(deployResult, "Failures for deployment in progress:\n");
	            }
	        }
	        while (!deployResult.isDone());
	        
	        if (!deployResult.isSuccess() && deployResult.getErrorStatusCode() != null) {
	            throw new Exception(deployResult.getErrorStatusCode() + " msg: " +
	                    deployResult.getErrorMessage());
	        }
	 
	        if (!fetchDetails) {
	            // Get the final result with details if we didn't do it in the last attempt.
	            deployResult = metadataConnection.checkDeployStatus(asyncResultId, true);
	        }
	 
	        if (!deployResult.isSuccess()) {
	            printErrors(deployResult, "Final list of failures:\n");
	            throw new Exception("The files were not successfully deployed");
	        }
	        
	        System.out.println("The file " + ZIP_FILE + " was successfully deployed");
	    }
	    
	    /**
	     * Read the zip file contents into a byte array.
	     * @return byte[]
	     * @throws Exception - if cannot find the zip file to deploy
	     */
	    private byte[] readZipFile()
	        throws Exception
	    {
	        // We assume here that you have a deploy.zip file.
	        // See the retrieve sample for how to retrieve a zip file.
	        File deployZip = new File(ZIP_FILE);
	        if (!deployZip.exists() || !deployZip.isFile())
	            throw new Exception("Cannot find the zip file to deploy. Looking for " +
	                    deployZip.getAbsolutePath());
	        
	        FileInputStream fos = new FileInputStream(deployZip);
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        int readbyte = -1;
	        while ((readbyte = fos.read()) != -1)  {
	            bos.write(readbyte);
	        }
	        fos.close();
	        bos.close();
	        return bos.toByteArray();
	    }
	    
	 
	    /**
	     * Print out any errors, if any, related to the deploy.
	     * @param result - DeployResult
	     */
	    private void printErrors(DeployResult result, String messageHeader)
	    {
	        DeployDetails deployDetails = result.getDetails();
	        
	        StringBuilder errorMessageBuilder = new StringBuilder();
	        if (deployDetails != null) {
	            DeployMessage[] componentFailures = deployDetails.getComponentFailures();
	            for (DeployMessage message : componentFailures) {
	                String loc = (message.getLineNumber() == 0 ? "" :
	                    ("(" + message.getLineNumber() + "," +
	                            message.getColumnNumber() + ")"));
	                if (loc.length() == 0
	                        && !message.getFileName().equals(message.getFullName())) {
	                    loc = "(" + message.getFullName() + ")";
	                }
	                errorMessageBuilder.append(message.getFileName() + loc + ":" +
	                        message.getProblem()).append('\n');
	            }
	            RunTestsResult rtr = deployDetails.getRunTestResult();
	            if (rtr.getFailures() != null) {
	                for (RunTestFailure failure : rtr.getFailures()) {
	                    String n = (failure.getNamespace() == null ? "" :
	                        (failure.getNamespace() + ".")) + failure.getName();
	                    errorMessageBuilder.append("Test failure, method: " + n + "." +
	                            failure.getMethodName() + " -- " +
	                            failure.getMessage() + " stack " +
	                            failure.getStackTrace() + "\n\n");
	                }
	            }
	            if (rtr.getCodeCoverageWarnings() != null) {
	                for (CodeCoverageWarning ccw : rtr.getCodeCoverageWarnings()) {
	                    errorMessageBuilder.append("Code coverage issue");
	                    if (ccw.getName() != null) {
	                        String n = (ccw.getNamespace() == null ? "" :
	                            (ccw.getNamespace() + ".")) + ccw.getName();
	                        errorMessageBuilder.append(", class: " + n);
	                    }
	                    errorMessageBuilder.append(" -- " + ccw.getMessage() + "\n");
	                }
	            }
	        }
	        
	        if (errorMessageBuilder.length() > 0) {
	            errorMessageBuilder.insert(0, messageHeader);
	            System.out.println(errorMessageBuilder.toString());
	        }
	    }

}