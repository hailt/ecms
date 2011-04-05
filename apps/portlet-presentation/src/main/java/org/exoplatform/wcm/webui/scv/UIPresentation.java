/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.wcm.webui.scv;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.exoplatform.ecm.resolver.JCRResourceResolver;
import org.exoplatform.ecm.webui.presentation.AbstractActionComponent;
import org.exoplatform.ecm.webui.presentation.UIBaseNodePresentation;
import org.exoplatform.ecm.webui.presentation.removeattach.RemoveAttachmentComponent;
import org.exoplatform.ecm.webui.presentation.removecomment.RemoveCommentComponent;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.resolver.ResourceResolver;
import org.exoplatform.services.cms.impl.DMSConfiguration;
import org.exoplatform.services.wcm.core.NodeLocation;
import org.exoplatform.services.wcm.friendly.FriendlyService;
import org.exoplatform.services.wcm.publication.WCMComposer;
import org.exoplatform.wcm.webui.Utils;
import org.exoplatform.web.application.Parameter;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.Lifecycle;
import org.exoplatform.webui.ext.UIExtension;
import org.exoplatform.webui.ext.UIExtensionManager;

/**
 * Created by The eXo Platform SAS
 * Author : DANG TAN DUNG
 * dzungdev@gmail.com
 * Jun 9, 2008
 */
@ComponentConfig(
    lifecycle = Lifecycle.class
)

public class UIPresentation extends UIBaseNodePresentation {

  private NodeLocation originalNodeLocation;

  private NodeLocation viewNodeLocation;

  String templatePath = null;

  /* (non-Javadoc)
   * @see org.exoplatform.ecm.webui.presentation.UIBaseNodePresentation#getOriginalNode()
   */
  public Node getOriginalNode() throws Exception {
    return Utils.getViewableNodeByComposer(originalNodeLocation.getRepository(),
                                           originalNodeLocation.getWorkspace(),
                                           originalNodeLocation.getPath(),
                                           WCMComposer.BASE_VERSION);
  }

  /* (non-Javadoc)
   * @see org.exoplatform.ecm.webui.presentation.UIBaseNodePresentation#getNode()
   */
  public void setOriginalNode(Node node) throws Exception{
    originalNodeLocation = NodeLocation.make(node);
  }

  /* (non-Javadoc)
   * @see org.exoplatform.ecm.webui.presentation.UIBaseNodePresentation#getNode()
   */
  public Node getNode() throws Exception {
    return Utils.getViewableNodeByComposer(viewNodeLocation.getRepository(),
                                           viewNodeLocation.getWorkspace(),
                                           viewNodeLocation.getPath());
  }

  /* (non-Javadoc)
   * @see org.exoplatform.ecm.webui.presentation.NodePresentation#setNode(javax.jcr.Node)
   */
  public void setNode(Node node) {
    viewNodeLocation = NodeLocation.make(node);
  }

  /* (non-Javadoc)
   * @see org.exoplatform.ecm.webui.presentation.UIBaseNodePresentation#getRepositoryName()
   */
  public String getRepositoryName() {
    PortletRequestContext portletRequestContext = WebuiRequestContext.getCurrentInstance();
    PortletPreferences portletPreferences = portletRequestContext.getRequest().getPreferences();
    return originalNodeLocation != null ? originalNodeLocation.getRepository()
                                       : portletPreferences.getValue(UISingleContentViewerPortlet.REPOSITORY,
                                                                     "repository");
  }

  /* (non-Javadoc)
   * @see org.exoplatform.portal.webui.portal.UIPortalComponent#getTemplate()
   */
  public String getTemplate() {
    return templatePath;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.ecm.webui.presentation.UIBaseNodePresentation#getTemplatePath()
   */
  public String getTemplatePath() throws Exception {
    return templatePath;
  }

  public void setTemplatePath(String templatePath) {
    this.templatePath = templatePath;
  }



  /*
   * (non-Javadoc)
   * @see
   * org.exoplatform.webui.core.UIComponent#getTemplateResourceResolver(org.
   * exoplatform.webui.application.WebuiRequestContext, java.lang.String)
   */
  @Deprecated
  public ResourceResolver getTemplateResourceResolver(WebuiRequestContext context, String template) {
    DMSConfiguration dmsConfiguration = getApplicationComponent(DMSConfiguration.class);
    String workspace = dmsConfiguration.getConfig().getSystemWorkspace();
    return new JCRResourceResolver(workspace);
  }
  
  public ResourceResolver getTemplateResourceResolver() {
    DMSConfiguration dmsConfiguration = getApplicationComponent(DMSConfiguration.class);
    String workspace = dmsConfiguration.getConfig().getSystemWorkspace();
    return new JCRResourceResolver(workspace);
  }  

  /* (non-Javadoc)
   * @see org.exoplatform.ecm.webui.presentation.NodePresentation#getNodeType()
   */
  public String getNodeType() throws Exception {
    return null;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.ecm.webui.presentation.NodePresentation#isNodeTypeSupported()
   */
  public boolean isNodeTypeSupported() {
    return false;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.ecm.webui.presentation.NodePresentation#getCommentComponent()
   */
  public UIComponent getCommentComponent() {
    return null;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.ecm.webui.presentation.NodePresentation#getCommentComponent()
   */
  public UIComponent getRemoveAttach() throws Exception {
    removeChild(RemoveAttachmentComponent.class);
    UIComponent uicomponent = addChild(RemoveAttachmentComponent.class, null, "PresentationRemoveAttach");
    ((AbstractActionComponent) uicomponent).setLstComponentupdate(Arrays.asList(new Class[] { UIPresentationContainer.class }));
    return uicomponent;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.ecm.webui.presentation.NodePresentation#getCommentComponent()
   */
  public UIComponent getRemoveComment() throws Exception {
    removeChild(RemoveCommentComponent.class);
    UIComponent uicomponent = addChild(RemoveCommentComponent.class, null, "PresentationRemoveComment");
    ((AbstractActionComponent)uicomponent).setLstComponentupdate(Arrays.asList(new Class[] {UIPresentationContainer.class}));
    return uicomponent;
  }

  public UIComponent getUIComponent(String mimeType) throws Exception {
    UIExtensionManager manager = getApplicationComponent(UIExtensionManager.class);
    List<UIExtension> extensions = manager.getUIExtensions(org.exoplatform.ecm.webui.utils.Utils.FILE_VIEWER_EXTENSION_TYPE);
      Map<String, Object> context = new HashMap<String, Object>();
      context.put(org.exoplatform.ecm.webui.utils.Utils.MIME_TYPE, mimeType);
      for (UIExtension extension : extensions) {
        UIComponent uiComponent = manager.addUIExtension(extension, context, this);
        if(uiComponent != null) return uiComponent;
      }
      return null;
  }

  /**
   * Gets the attachment URL.
   *
   * @param node the node
   * @return the attachment URL
   * @throws Exception the exception
   */
  public String getAttachmentURL(Node node, Parameter[] params) throws Exception {
    String link = null;
    PortalRequestContext portalRequestContext = Util.getPortalRequestContext();
    PortletRequestContext portletRequestContext = WebuiRequestContext.getCurrentInstance();
    PortletRequest portletRequest = portletRequestContext.getRequest();
    String portalURI = portalRequestContext.getPortalURI();
    NodeLocation nodeLocation = NodeLocation.getNodeLocationByNode(node);
    String baseURI = portletRequest.getScheme() + "://" + portletRequest.getServerName() + ":"
        + String.format("%s", portletRequest.getServerPort());
    String basePath = Utils.getPortletPreference(UISingleContentViewerPortlet.PREFERENCE_TARGET_PAGE);
    String scvWith = Utils.getPortletPreference(UISingleContentViewerPortlet.PREFERENCE_SHOW_SCV_WITH);
    if (scvWith == null || scvWith.length() == 0)
        scvWith = UISingleContentViewerPortlet.DEFAULT_SHOW_SCV_WITH;
    if (node.isNodeType("nt:frozenNode")) {
      String uuid = node.getProperty("jcr:frozenUuid").getString();
      Node originalNode = node.getSession().getNodeByUUID(uuid);
      link = baseURI + portalURI + basePath + "?" + scvWith + "=/" + nodeLocation.getRepository()
          + "/" + nodeLocation.getWorkspace() + originalNode.getPath();
    } else {
      link = baseURI + portalURI + basePath + "?" + scvWith + "=/" + nodeLocation.getRepository()
          + "/" + nodeLocation.getWorkspace() + node.getPath();
    }
    FriendlyService friendlyService = getApplicationComponent(FriendlyService.class);
    link = friendlyService.getFriendlyUri(link);

    return link;
  }

}
