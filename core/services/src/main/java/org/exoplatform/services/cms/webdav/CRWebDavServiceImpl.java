/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.services.cms.webdav;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.util.HierarchicalProperty;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ecm.utils.text.Text;
import org.exoplatform.services.cms.link.LinkUtils;
import org.exoplatform.services.cms.link.NodeFinder;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.webdav.util.TextUtil;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.ExtHttpHeaders;
import org.exoplatform.services.rest.ext.webdav.method.CHECKIN;
import org.exoplatform.services.rest.ext.webdav.method.CHECKOUT;
import org.exoplatform.services.rest.ext.webdav.method.COPY;
import org.exoplatform.services.rest.ext.webdav.method.LOCK;
import org.exoplatform.services.rest.ext.webdav.method.OPTIONS;
import org.exoplatform.services.rest.ext.webdav.method.ORDERPATCH;
import org.exoplatform.services.rest.ext.webdav.method.PROPFIND;
import org.exoplatform.services.rest.ext.webdav.method.PROPPATCH;
import org.exoplatform.services.rest.ext.webdav.method.REPORT;
import org.exoplatform.services.rest.ext.webdav.method.SEARCH;
import org.exoplatform.services.rest.ext.webdav.method.UNCHECKOUT;
import org.exoplatform.services.rest.ext.webdav.method.UNLOCK;
import org.exoplatform.services.rest.ext.webdav.method.VERSIONCONTROL;

import java.io.InputStream;

import javax.jcr.Item;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.PathNotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * This class is used to override the default WebDavServiceImpl in order to support symlinks
 * 
 * Created by The eXo Platform SAS
 * Author : peter.nedonosko@exoplatform.com
 * 29 April 2011  
 */
@Path("/dav/")
public class CRWebDavServiceImpl extends org.exoplatform.services.jcr.webdav.CRWebDavServiceImpl {

  /**
   * Logger.
   */
  private static Log log = ExoLogger.getLogger("cms.webdav.CRWebDavServiceImpl");
  
  private final NodeFinder nodeFinder;
  
  public CRWebDavServiceImpl(InitParams params,
                           RepositoryService repositoryService,
                           ThreadLocalSessionProviderService sessionProviderService,
                           NodeFinder nodeFinder) throws Exception {
    super(params, repositoryService, sessionProviderService);
    
    this.nodeFinder = nodeFinder;
  }

  private String getRealDestinationHeader(String baseURI, String destinationHeader) {
    String serverURI = baseURI + "/dav";

    destinationHeader = TextUtil.unescape(destinationHeader, '%');

    if (!destinationHeader.startsWith(serverURI)) {
      return null;
    }

    String destPath = destinationHeader.substring(serverURI.length() + 1);
    
    try {
      Item item = nodeFinder.getItem(null, workspaceName(destPath), LinkUtils.getParentPath(path(destPath)), true);
      return item.getSession().getWorkspace().getName() + LinkUtils.createPath(item.getPath(), LinkUtils.getItemName(path(destPath)));
    } catch (Exception e) {
      log.warn("Cannot find the item at /" + destPath, e);
      return null;
    }
  }

  
  @CHECKIN
  @Path("/{repoPath:.*}/")
  public Response checkin(@PathParam("repoPath") String repoPath,
                          @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader,
                          @HeaderParam(ExtHttpHeaders.IF) String ifHeader) {

    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), path(Text.escapeIllegalJcrChars(repoPath)), true);
      repoPath = item.getSession().getWorkspace().getName() + item.getPath();
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item at /" + repoPath, e);
      return Response.serverError().build();
    }
    return super.checkin(repoPath, lockTokenHeader, ifHeader);
  }

  @CHECKOUT
  @Path("/{repoPath:.*}/")
  public Response checkout(@PathParam("repoPath") String repoPath,
                           @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader,
                           @HeaderParam(ExtHttpHeaders.IF) String ifHeader) {
    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), path(Text.escapeIllegalJcrChars(repoPath)), true);
      repoPath = item.getSession().getWorkspace().getName() + item.getPath();
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item at /" + repoPath, e);
      return Response.serverError().build();
    }
    return super.checkout(repoPath, lockTokenHeader, ifHeader);
  }

  @COPY
  @Path("/{repoPath:.*}/")
  public Response copy(@PathParam("repoPath") String repoPath,
                       @HeaderParam(ExtHttpHeaders.DESTINATION) String destinationHeader,
                       @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader,
                       @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
                       @HeaderParam(ExtHttpHeaders.DEPTH) String depthHeader,
                       @HeaderParam(ExtHttpHeaders.OVERWRITE) String overwriteHeader,
                       @Context UriInfo uriInfo,
                       HierarchicalProperty body) {

    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), path(Text.escapeIllegalJcrChars(repoPath)));
      repoPath = item.getSession().getWorkspace().getName() + item.getPath();
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item at /" + repoPath, e);
      return Response.serverError().build();
    }
    String realDestinationHeader = getRealDestinationHeader(uriInfo.getPath(), destinationHeader);
    if (realDestinationHeader != null) {
      destinationHeader = realDestinationHeader;
    }
    return super.copy(repoPath, destinationHeader, lockTokenHeader, ifHeader, depthHeader, overwriteHeader, uriInfo, body);
  }

  @GET
  @Path("/{repoPath:.*}/")
  public Response get(@PathParam("repoPath") String repoPath,
                      @HeaderParam(ExtHttpHeaders.RANGE) String rangeHeader,
                      @HeaderParam(ExtHttpHeaders.IF_MODIFIED_SINCE) String ifModifiedSince,
                      @QueryParam("version") String version,
                      @Context UriInfo uriInfo) {

    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), path(Text.escapeIllegalJcrChars(repoPath)), true);
      repoPath = item.getSession().getWorkspace().getName() + item.getPath();
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item at /" + repoPath, e);
      return Response.serverError().build();
    }
    return super.get(repoPath, rangeHeader, ifModifiedSince, version, uriInfo);
  }

  @HEAD
  @Path("/{repoPath:.*}/")
  public Response head(@PathParam("repoPath") String repoPath,
                       @Context UriInfo uriInfo) {

    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), path(Text.escapeIllegalJcrChars(repoPath)), true);
      repoPath = item.getSession().getWorkspace().getName() + item.getPath();
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item /" + repoPath, e);
      return Response.serverError().build();
    }
    return super.head(repoPath, uriInfo);
  }

  @LOCK
  @Path("/{repoPath:.*}/")
  public Response lock(@PathParam("repoPath") String repoPath,
                       @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader,
                       @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
                       @HeaderParam(ExtHttpHeaders.DEPTH) String depthHeader,
                       HierarchicalProperty body) {

    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), path(Text.escapeIllegalJcrChars(repoPath)), true);
      repoPath = item.getSession().getWorkspace().getName() + item.getPath();
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item /" + repoPath, e);
      return Response.serverError().build();
    }
    return super.lock(repoPath, lockTokenHeader, ifHeader, depthHeader, body);
  }

  @UNLOCK
  @Path("/{repoPath:.*}/")
  public Response unlock(@PathParam("repoPath") String repoPath,
                         @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader,
                         @HeaderParam(ExtHttpHeaders.IF) String ifHeader) {

    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), path(Text.escapeIllegalJcrChars(repoPath)), true);
      repoPath = item.getSession().getWorkspace().getName() + item.getPath();
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item /" + repoPath, e);
      return Response.serverError().build();
    }
    return super.unlock(repoPath, lockTokenHeader, ifHeader);
  }

  @OPTIONS
  @Path("/{path:.*}/")
  public Response options(@PathParam("path") String path) {
    return super.options(path);
  }

  @ORDERPATCH
  @Path("/{repoPath:.*}/")
  public Response order(@PathParam("repoPath") String repoPath,
                        @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader,
                        @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
                        @Context UriInfo uriInfo,
                        HierarchicalProperty body) {

    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), path(Text.escapeIllegalJcrChars(repoPath)), true);
      repoPath = item.getSession().getWorkspace().getName() + item.getPath();
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item /" + repoPath, e);
      return Response.serverError().build();
    }
    return super.order(repoPath, lockTokenHeader, ifHeader, uriInfo, body);
  }

  @PROPFIND
  @Path("/{repoPath:.*}/")
  public Response propfind(@PathParam("repoPath") String repoPath,
                           @HeaderParam(ExtHttpHeaders.DEPTH) String depthHeader,
                           @Context UriInfo uriInfo,
                           HierarchicalProperty body) {

    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), path(Text.escapeIllegalJcrChars(repoPath)), true);
      repoPath = item.getSession().getWorkspace().getName() + item.getPath();
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item /" + repoPath, e);
      return Response.serverError().build();
    }
    return super.propfind(repoPath, depthHeader, uriInfo, body);
  }

  @PROPPATCH
  @Path("/{repoPath:.*}/")
  public Response proppatch(@PathParam("repoPath") String repoPath,
                            @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader,
                            @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
                            @Context UriInfo uriInfo,
                            HierarchicalProperty body) {

    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), path(Text.escapeIllegalJcrChars(repoPath)), true);
      repoPath = item.getSession().getWorkspace().getName() + item.getPath();
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item /" + repoPath, e);
      return Response.serverError().build();
    }
    return super.proppatch(repoPath, lockTokenHeader, ifHeader, uriInfo, body);
  }

  @PUT
  @Path("/{repoPath:.*}/")
  public Response put(@PathParam("repoPath") String repoPath,
                      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader,
                      @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
                      @HeaderParam(ExtHttpHeaders.FILE_NODETYPE) String fileNodeTypeHeader,
                      @HeaderParam(ExtHttpHeaders.CONTENT_NODETYPE) String nodeTypeHeader,
                      @HeaderParam(ExtHttpHeaders.CONTENT_MIXINTYPES) String mixinTypes,
                      @HeaderParam(ExtHttpHeaders.CONTENTTYPE) MediaType mediaType,
                      InputStream inputStream,
                      @Context UriInfo uriInfo) {

    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), LinkUtils.getParentPath(path(Text.escapeIllegalJcrChars(repoPath))), true);
      repoPath = item.getSession().getWorkspace().getName() + LinkUtils.createPath(item.getPath(), LinkUtils.getItemName(path(repoPath)));
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item /" + repoPath, e);
      return Response.serverError().build();
    }
    return super.put(repoPath, lockTokenHeader, ifHeader, null, nodeTypeHeader, mixinTypes, mediaType, inputStream, uriInfo);
  }

  @REPORT
  @Path("/{repoPath:.*}/")
  public Response report(@PathParam("repoPath") String repoPath,
                         @HeaderParam(ExtHttpHeaders.DEPTH) String depthHeader,
                         @Context UriInfo uriInfo,
                         HierarchicalProperty body) {

    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), path(Text.escapeIllegalJcrChars(repoPath)), true);
      repoPath = item.getSession().getWorkspace().getName() + item.getPath();
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item /" + repoPath, e);
      return Response.serverError().build();
    }
    return super.report(repoPath, depthHeader, uriInfo, body);
  }

  @SEARCH
  @Path("/{repoPath:.*}/")
  public Response search(@PathParam("repoPath") String repoPath,
                         @Context UriInfo uriInfo,
                         HierarchicalProperty body) {

    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), path(Text.escapeIllegalJcrChars(repoPath)), true);
      repoPath = item.getSession().getWorkspace().getName() + item.getPath();
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item at /" + repoPath, e);
      return Response.serverError().build();
    }
    return super.search(repoPath, uriInfo, body);
  }

  @UNCHECKOUT
  @Path("/{repoPath:.*}/")
  public Response uncheckout(@PathParam("repoPath") String repoPath,
                             @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader,
                             @HeaderParam(ExtHttpHeaders.IF) String ifHeader) {

    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), path(Text.escapeIllegalJcrChars(repoPath)), true);
      repoPath = item.getSession().getWorkspace().getName() + item.getPath();
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item at /" + repoPath, e);
      return Response.serverError().build();
    }
    return super.uncheckout(repoPath, lockTokenHeader, ifHeader);
  }

  @VERSIONCONTROL
  @Path("/{repoPath:.*}/")
  public Response versionControl(@PathParam("repoPath") String repoPath,
                                 @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader,
                                 @HeaderParam(ExtHttpHeaders.IF) String ifHeader) {

    try {
      Item item = nodeFinder.getItem(null, workspaceName(repoPath), path(Text.escapeIllegalJcrChars(repoPath)), true);
      repoPath = item.getSession().getWorkspace().getName() + item.getPath();
    } catch (PathNotFoundException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (NoSuchWorkspaceException exc) {
      return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
    } catch (Exception e) {
      log.warn("Cannot find the item at /" + repoPath, e);
      return Response.serverError().build();
    }
    return super.versionControl(repoPath, lockTokenHeader, ifHeader);
  }
}
