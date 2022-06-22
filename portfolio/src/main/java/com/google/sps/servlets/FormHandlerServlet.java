package com.google.sps.servlets;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.util.Date;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/form-handler")
@MultipartConfig
public class FormHandlerServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
    String name = request.getParameter("name-input");
    String email = request.getParameter("email-input");
    String message = request.getParameter("message-input");

    // Get date from server
    Date date = new Date();
    String dateTime = date.toString();

     // Get the file chosen by the user.
     Part filePart = request.getPart("image");
     String uploadedFileUrl = "—";
 
     //Check if there is a file uploaded
     if(filePart.getSize()>0){
        String fileName = filePart.getSubmittedFileName();
        InputStream fileInputStream = filePart.getInputStream();
 
        // Upload the file and get its URL
        uploadedFileUrl = uploadToCloudStorage(fileName, fileInputStream);
     }

    System.out.println("name: " + name);
    System.out.println("email: " + email);
    System.out.println("message: " + message);

    //  Writing to Datastore
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("ContactInfo");
    FullEntity taskEntity = 
                            Entity.newBuilder(keyFactory.newKey())
                            .set("Name", name)
                            .set("Email", email)
                            .set("Message", message)
                            .set("File", uploadedFileUrl)
                            .set("Date", dateTime)
                            .build();
    datastore.put(taskEntity);

    // Redirecting the website 
    response.sendRedirect("https://swu-sps-summer22.appspot.com/#contact");
  }

  /** Uploads a file to Cloud Storage and returns the uploaded file's URL. */
  private static String uploadToCloudStorage(String fileName, InputStream fileInputStream) {
    String projectId = "swu-sps-summer22";
    String bucketName = "swu-sps-summer22.appspot.com";
    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    BlobId blobId = BlobId.of(bucketName, fileName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    // Upload the file to Cloud Storage.
    Blob blob = storage.create(blobInfo, fileInputStream);

    // Return the uploaded file's URL.
    return blob.getMediaLink();
  }
}

