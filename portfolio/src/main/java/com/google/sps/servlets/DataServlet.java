// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public final class DataServlet extends HttpServlet {

  private ArrayList<String> comments;

  @Override
  public void init() {
    comments = new ArrayList<>();
  }


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Convert the comments to JSON
    String json = convertToJsonUsingGson(comments);

    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Converts the comments array  into a JSON string using the Gson library. Note: We first added
   * the Gson library dependency to pom.xml.
   */
  private String convertToJsonUsingGson(List<String> comments) {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String comment = getParameter(request, "user-comment", null);

    // Return error message if the user did not input any comment.
    if (comment == null) {
        response.setContentType("text/html;");
        response.getWriter().println("Enter a comment before submitting.");
        return;
    }
    
    
    // If the user did submit a comment, add it to the datastore
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("content", comment);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    datastore.put(commentEntity);

    // Respond with a success message
    response.setContentType("text/html;");
    response.getWriter().println("Your comment has been registered. Thank you!");
  }


  /** Returns the desired parameter entered by the user, or null if the user input was invalid. */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    // Get the input from the form.
    String value = request.getParameter(name);

    if (value == null) {
        return defaultValue;
    }

    return value;
  }
}
