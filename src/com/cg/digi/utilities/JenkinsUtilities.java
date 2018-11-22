/******************************************************************************
Copyright © 2016 Capgemini Group of companies. All rights reserved
(Subject to Limited Distribution and Restricted Disclosure Only.)
THIS SOURCE FILE MAY CONTAIN INFORMATION WHICH IS THE PROPRIETARY
INFORMATION OF Capgemini GROUP OF COMPANIES AND IS INTENDED FOR USE
ONLY BY THE ENTITY WHO IS ENTITLED TO AND MAY CONTAIN
INFORMATION THAT IS PRIVILEGED, CONFIDENTIAL, OR EXEMPT FROM
DISCLOSURE UNDER APPLICABLE LAW.
YOUR ACCESS TO THIS SOURCE FILE IS GOVERNED BY THE TERMS AND
CONDITIONS OF AN AGREEMENT BETWEEN YOU AND Capgemini GROUP OF COMPANIES.
The USE, DISCLOSURE REPRODUCTION OR TRANSFER OF THIS PROGRAM IS
RESTRICTED AS SET FORTH THEREIN.
******************************************************************************/

package com.cg.digi.utilities;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.cg.digi.logger.DigiLoggerUtils;
import com.cg.digi.logger.DigiLoggerUtils.LEVEL;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class JenkinsUtilities {
	public static void main(String[] args) throws InterruptedException {

	}

	public static String[] listJobs(String url) {
		Client client = Client.create();
		// client.addFilter(new
		// com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME,
		// PASSWORD));
		WebResource webResource = client.resource(url + "/api/xml");
		ClientResponse response = webResource.get(ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
		// System.out.println("Response listJobs:::::"+jsonResponse);

		// Assume jobs returned are in xml format, TODO using an XML Parser
		// would be better here
		// Get name from <job><name>...
		String[] jobs = jsonResponse.split("job>"); // 1, 3, 5, 7, etc will
													// contain jobs

		return jobs;
	}

	public static String deleteJob(String url, String jobName) {
		Client client = Client.create();
		// client.addFilter(new
		// com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME,
		// PASSWORD));
		if (url.endsWith("/")) {
			url = url + "job/" + jobName + "/doDelete";
		} else {
			url = url + "/job/" + jobName + "/doDelete";
		}

		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.post(ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
		// System.out.println("Response deleteJobs:::::"+jsonResponse);
		return jsonResponse;
	}

	public static String copyJob(String url, String newJobName,
			String oldJobName) {
		Client client = Client.create();
		// client.addFilter(new
		// com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME,
		// PASSWORD));
		if (url.endsWith("/")) {
			url = url + "createItem?name=" + newJobName + "&mode=copy&from="
					+ oldJobName;
		} else {
			url = url + "/createItem?name=" + newJobName + "&mode=copy&from="
					+ oldJobName;
		}

		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.type("application/xml").get(
				ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
		// System.out.println("Response copyJob:::::"+jsonResponse);
		return jsonResponse;
	}

	public static String createJob(String url, String newJobName,
			String configXML) {
		String jsonResponse = null;
		try {
			Client client = Client.create();
			// client.addFilter(new
			// com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME,
			// PASSWORD));
			if (url.endsWith("/")) {
				url = url + "createItem?name=" + newJobName;

			} else {
				url = url + "/createItem?name=" + newJobName;
			}
			WebResource webResource = client.resource(url);
			ClientResponse response = webResource.type("application/xml").post(
					ClientResponse.class, configXML);
			jsonResponse = response.getEntity(String.class);
			int statusCode = response.getStatus();
			DigiLoggerUtils
					.log("Status  Creation of Jenkins Job : Job Name :"
							+ newJobName + "   Status Code : " + statusCode,
							LEVEL.info);
			client.destroy();
		} catch (UniformInterfaceException e) {
			DigiLoggerUtils.log(
					"Error while Creating Jenkins Job : Uniform Interface Exception :"
							+ e.getMessage(), LEVEL.error);
		} catch (ClientHandlerException e) {
			DigiLoggerUtils.log(
					"Error while Creating Jenkins Job : Uniform Interface Exception :"
							+ e.getMessage(), LEVEL.error);
		} catch (Exception e) {
			DigiLoggerUtils.log(
					"Error while Creating Jenkins Job : Uniform Interface Exception :"
							+ e.getMessage(), LEVEL.error);
		}
		return jsonResponse;
	}

	public static String readJob(String url, String jobName) {
		Client client = Client.create();
		// client.addFilter(new
		// com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME,
		// PASSWORD));
		if (url.endsWith("/")) {
			url = url + "job/" + jobName + "/config.xml";
		} else {
			url = url + "/job/" + jobName + "/config.xml";
		}
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.get(ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
		// System.out.println("Response readJob:::::"+jsonResponse);
		return jsonResponse;
	}

	public static String readAnyXmlFromJenkins(String url) {
		Client client = Client.create();
		// client.addFilter(new
		// com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME,
		// PASSWORD));
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.get(ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
		// System.out.println("Response readJob:::::"+jsonResponse);
		return jsonResponse;
	}

	public static HttpResponse buildJob(String jenurl, String jobName) {
		HttpResponse responseCode = null;
		try {

			HttpClient client = HttpClientBuilder.create().build();
			String finalUrl = "";
			if (jenurl.endsWith("/")) {
				finalUrl = jenurl + "job/" + jobName + "/build";
			} else {
				finalUrl = jenurl + "/job/" + jobName + "/build";
			}
			HttpGet get = new HttpGet(finalUrl);
			responseCode = client.execute(get);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return responseCode;
	}

	public static HttpResponse buildJob(String jenurl, String jobName,
			long delay) {
		HttpResponse responseCode = null;
		try {
			String finalUrl = "";
			if (jenurl.endsWith("/")) {
				finalUrl = jenurl + "job/" + jobName + "/build";
			} else {
				finalUrl = jenurl + "/job/" + jobName + "/build";
			}

			HttpClient client = HttpClientBuilder.create().build();

			if (delay > 0) {
				finalUrl = finalUrl + "?delay=" + delay;
			}
			HttpGet get = new HttpGet(finalUrl);
			responseCode = client.execute(get);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseCode;
	}

	// added
	public static String buildStatus(String jenurl, String jobName,
			int buildNumber) {
		String Status = null;
		try {
			String finalUrl = "";
			if (jenurl.endsWith("/")) {
				finalUrl = jenurl + "job/" + jobName + "/" + buildNumber
						+ "/api/xml";
			} else {
				finalUrl = jenurl + "/job/" + jobName + "/" + buildNumber
						+ "/api/xml";
			}

			Client client = Client.create();
			WebResource webResource = client.resource(finalUrl);
			ClientResponse response = webResource.get(ClientResponse.class);
			Status = response.getEntity(String.class);
			client.destroy();
			Status = getResult(Status, "result");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Status;
	}

	public static String latestBuildStatus(String jenurl, String jobName) {
		String status = null;
		String buildNo = null;
		try {
			String finalUrl = "";
			if (jenurl.endsWith("/")) {
				finalUrl = jenurl + "job/" + jobName + "/api/xml";
			} else {
				finalUrl = jenurl + "/job/" + jobName + "/api/xml";
			}
			Client client = Client.create();
			WebResource webResource = client.resource(finalUrl);
			ClientResponse response = null;
			try {
				response = webResource.get(ClientResponse.class);
			} catch (Exception e) {
				if(e.getMessage().contains("Connection refused: connect")){
					status="Jenkins Offline";
				}
			}
			status = response.getEntity(String.class);
			client.destroy();
			buildNo = getResult(status, "lastBuild");
			if (jenurl.endsWith("/")) {
				finalUrl = jenurl + "job/" + jobName + "/" + buildNo
						+ "/api/xml";
			} else {
				finalUrl = jenurl + "/job/" + jobName + "/" + buildNo
						+ "/api/xml";
			}
			client = Client.create();
			webResource = client.resource(finalUrl);
			response = webResource.get(ClientResponse.class);
			status = response.getEntity(String.class);
			client.destroy();
			status = getResult(status, "result");
		} catch (Exception e) {
		//	e.printStackTrace();
		}
		return status;
	}

	private static String getResult(String status, String tosearch) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputSource src = new InputSource();
			src.setCharacterStream(new StringReader(status));
			Document doc = builder.parse(src);
			try {
				if (tosearch == "lastBuild") {
					status = doc.getElementsByTagName(tosearch).item(0).getTextContent();
					status = status.split("h")[0];
				} else {
					status = doc.getElementsByTagName(tosearch).item(0)
							.getTextContent();
				}
			} catch (NullPointerException e) {
				 return status="Progress";
			}
		} catch (Exception e) {
			
			DigiLoggerUtils.log("Failed in getting status of the Jon inJenkins Utils "+e.getStackTrace(), LEVEL.error);
		}
		return status;
	}

	public static int newJenkinsJob(String url, String newJobName,
			String configXML) {
		int jsonResponse = 0;
		try {
			Client client = Client.create();
			// client.addFilter(new
			// com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME,
			// PASSWORD));
			if (url.endsWith("/")) {
				url = url + "createItem?name=" + newJobName;

			} else {
				url = url + "/createItem?name=" + newJobName;
			}
			WebResource webResource = client.resource(url);
			ClientResponse response = webResource.type("application/xml").post(
					ClientResponse.class, configXML);
			jsonResponse = response.getStatus();
			int statusCode = response.getStatus();
			DigiLoggerUtils
					.log("Status  Creation of Jenkins Job : Job Name :"
							+ newJobName + "   Status Code : " + statusCode,
							LEVEL.info);
			client.destroy();
		} catch (UniformInterfaceException e) {
			DigiLoggerUtils.log(
					"Error while Creating Jenkins Job : Uniform Interface Exception :"
							+ e.getMessage(), LEVEL.error);
		} catch (ClientHandlerException e) {
			DigiLoggerUtils.log(
					"Error while Creating Jenkins Job : Uniform Interface Exception :"
							+ e.getMessage(), LEVEL.error);
		} catch (Exception e) {
			DigiLoggerUtils.log(
					"Error while Creating Jenkins Job : Uniform Interface Exception :"
							+ e.getMessage(), LEVEL.error);
		}
		return jsonResponse;
	}

}
