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
package org.exoplatform.services.cms.templates;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by The eXo Platform SAS
 * Author : Hoa Pham
 *          hoa.pham@exoplatform.com
 * Oct 3, 2008
 */
public class ContentTypeFilterPlugin extends BaseComponentPlugin {

  private List<FolderFilterConfig> folderFilterConfigs = new ArrayList<FolderFilterConfig>();
  
  @Deprecated
  private String repository;
  
  public ContentTypeFilterPlugin(InitParams initParams) {
     if (initParams.getValueParam("repository") != null)
     {
        repository = initParams.getValueParam("repository").getValue();
     }
     else
     {
        repository = null;
     }
    for(Iterator<ObjectParameter> iterator = initParams.getObjectParamIterator();iterator.hasNext();) {
      Object object = iterator.next().getObject();
      if(object instanceof FolderFilterConfig ) {
        folderFilterConfigs.add(FolderFilterConfig.class.cast(object));
      }
    }
  }

  public List<FolderFilterConfig> getFolderFilterConfigList() { return folderFilterConfigs; }

  @Deprecated
  public String getRepository() { return this.repository; }
  
  public static class FolderFilterConfig {
    private String folderType;
    private ArrayList<String> contentTypes;
    public String getFolderType() { return folderType; }
    public void setFolderType(String folderType) { this.folderType = folderType; }

    public ArrayList<String> getContentTypes() { return contentTypes;}
    public void setContentTypes(ArrayList<String> contentTypes) { this.contentTypes = contentTypes;}

  }


}
