/**
 *  Copyright (C) 2003-2010 eXo Platform SAS.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation; either version 3
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.ecms.xcmis.sp;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.cms.drives.DriveData;
import org.exoplatform.services.cms.drives.ManageDriveService;
import org.exoplatform.services.cms.impl.DMSConfiguration;
import org.exoplatform.services.document.DocumentReaderService;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.SystemIdentity;
import org.exoplatform.services.jcr.impl.RepositoryServiceImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.Connection;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.PermissionService;
import org.xcmis.spi.StorageProvider;
import org.xcmis.spi.model.RepositoryShortInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class DriveCmisRegistry extends JcrCmisRegistry
{

   private static final Log LOG = ExoLogger.getLogger(DriveCmisRegistry.class);

   private final RepositoryService repositoryService;

   private final DocumentReaderService documentReaderService;

   private final ManageDriveService driveService;

   private final String defRepository = "repository";

   private String repository;

   private final PermissionService permissionService;

   private boolean persistRenditions;

   private final DMSConfiguration dmsConfiguration;

   public DriveCmisRegistry(RepositoryServiceImpl repositoryService, DocumentReaderService documentReaderService,
      InitParams initParams, ManageDriveService driveService, DMSConfiguration dmsConfiguration)
   {
      super(repositoryService, documentReaderService, initParams);
      this.repositoryService = repositoryService;
      this.documentReaderService = documentReaderService;
      this.dmsConfiguration = dmsConfiguration;
      this.permissionService = new PermissionService();
      this.driveService = driveService;
   }

   public DriveCmisRegistry(RepositoryServiceImpl repositoryService, InitParams initParams,
      ManageDriveService driveService, DMSConfiguration dmsConfiguration)
   {
      this(repositoryService, null, initParams, driveService, dmsConfiguration);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Connection getConnection(String storageId)
   {
      StorageProvider provider = storageProviders.get(storageId);
      if (provider == null)
      {
         DriveData drive = null;
         try
         {
            drive = driveService.getDriveByName(storageId, repository);
         }
         catch (Exception e)
         {
            throw new CmisRuntimeException(e.getMessage(), e);
         }
         if (drive != null)
         {
            // TODO : No indexing on drive which met after start.
            StorageProviderImpl provider0 = createStorageProvider(drive);
            try
            {
               provider0.init();
               provider = provider0;
            }
            catch (Exception e)
            {
               throw new CmisRuntimeException("Initializing of storage provider " + storageId + " failed. "
                  + e.getMessage(), e);
            }
         }
      }
      if (provider != null)
      {
         return provider.getConnection();
      }
      throw new InvalidArgumentException("Storage '" + storageId + "' does not exist.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<RepositoryShortInfo> getStorageInfos()
   {
      List<String> memberships = null;
      String userId = null;
      ConversationState state = ConversationState.getCurrent();
      if (state != null)
      {
         Identity identity = state.getIdentity();
         if (identity != null)
         {
            userId = identity.getUserId();
            memberships = new ArrayList<String>(identity.getMemberships().size());
            for (MembershipEntry membership : identity.getMemberships())
            {
               memberships.add(membership.toString());
            }
         }
      }
      if (userId == null)
      {
         userId = SystemIdentity.ANONIM;
      }
      if (memberships == null)
      {
         memberships = Collections.emptyList();
      }

      SortedSet<RepositoryShortInfo> repositories = new TreeSet<RepositoryShortInfo>();
      List<DriveData> availableDrives = null;
      try
      {
         availableDrives = driveService.getDriveByUserRoles(repository, userId, memberships);
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
      }

      if (availableDrives != null)
      {
         for (DriveData drive : availableDrives)
         {
            String driveName = drive.getName();
            StorageProvider provider = storageProviders.get(driveName);
            if (provider == null)
            {
               // TODO : No indexing on drive which met after start.
               StorageProviderImpl provider0 = createStorageProvider(drive);
               try
               {
                  provider0.init();
                  provider = provider0;
               }
               catch (Exception e)
               {
                  LOG.error("Initializing of storage provider " + driveName + " failed. ", e);
               }
               // XXX NOTE New storage is not add in registry.
               // At the moment met only private/public user's storage.
               // It can be a lot storage if user's database is large.
               // Probably will need create specific storage provider for this.
            }

            if (provider != null)
            {
               Connection connection = null;
               try
               {
                  String storageID = provider.getStorageID();
                  RepositoryShortInfo info = new RepositoryShortInfo(storageID, storageID);
                  connection = provider.getConnection();
                  info.setRootFolderId(connection.getStorage().getRepositoryInfo().getRootFolderId());
                  repositories.add(info);
               }
               catch (Exception e)
               {
                  LOG.error(e.getMessage());
               }
               finally
               {
                  if (connection != null)
                  {
                     connection.close();
                  }
               }
            }
         }
      }
      return Collections.unmodifiableSortedSet(repositories);
   }

   /**
    * @see org.exoplatform.ecms.xcmis.sp.JcrCmisRegistry#getAffectedWorkspaceNames()
    */
   @Override
   public String[] getAffectedWorkspaceNames() throws RepositoryException
   {

      List<DriveData> drives;
      try
      {
         drives = driveService.getAllDrives(repositoryService.getCurrentRepository().getConfiguration().getName());
      }
      catch (Exception e)
      {
         throw new RepositoryException(e.getLocalizedMessage(), e);
      }
      Set<String> wsNames = new HashSet<String>();
      if (drives != null)
      {
         for (DriveData driveData : drives)
         {
            wsNames.add(driveData.getWorkspace());
         }
      }
      return wsNames.toArray(new String[wsNames.size()]);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void start()
   {

      repository = getValueParameter("repository", defRepository);
      persistRenditions = Boolean.parseBoolean(getValueParameter("exo.cmis.renditions.persistent", "false"));
      List<DriveData> allDrives = null;
      try
      {
         allDrives = driveService.getAllDrives(repository);
      }
      catch (Exception e)
      {
         throw new CmisRuntimeException("Unable get list of drives. " + e.getMessage(), e);
      }
      for (DriveData drive : allDrives)
      {
         if (isPrivateDrive(drive))
         {
            // Do nothing with private drive since we don't know user's name
            // and can't determine root path.
            continue;
         }
         String driveName = drive.getName();
         //         String indexDir =
         //            rootIndexDir != null ? (rootIndexDir.charAt(rootIndexDir.length() - 1) == File.separatorChar ? rootIndexDir
         //               + driveName : rootIndexDir + File.separatorChar + driveName) : null;
         //         IndexConfiguration indexConfiguration = null;
         //         if (indexDir != null)
         //         {
         //            indexConfiguration = new IndexConfiguration();
         //            indexConfiguration.setIndexDir(indexDir);
         //         }
         StorageProviderImpl sp = createStorageProvider(drive);
         try
         {
            sp.init();
            addStorage(sp);
         }
         catch (Exception e)
         {
            LOG.error("Initializing of storage provider " + driveName + " failed. " + e.getMessage());
         }
      }
      super.start();
   }

   protected boolean isPrivateDrive(DriveData drive)
   {
      // TODO is there something better in WCM API ?
      return drive.getHomePath().contains("${userId}");
   }

   //   @Override
   //   private String getValueParameter(String name, String defaultValue)
   //   {
   //      String value = null;
   //      if (initParams != null)
   //      {
   //         ValueParam valueParam = initParams.getValueParam(name);
   //         if (valueParam != null)
   //         {
   //            value = valueParam.getValue();
   //         }
   //      }
   //      return value != null ? value : defaultValue;
   //   }

   private StorageProviderImpl createStorageProvider(DriveData drive)
   {
      String driveName = drive.getName();
      String driveRootPath = drive.getHomePath();
      String driveWorkspace = drive.getWorkspace();
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put("exo.cmis.renditions.persistent", persistRenditions);
      StorageConfiguration configuration =
         new StorageConfiguration(driveName, repository, driveWorkspace, driveRootPath, properties, null);
      StorageProviderImpl sp =
         new StorageProviderImpl(repositoryService, documentReaderService, permissionService, this, getSearchService(
            repository, driveWorkspace), configuration);
      return sp;
   }
}