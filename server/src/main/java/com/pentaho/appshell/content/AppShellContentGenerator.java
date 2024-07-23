/*!
 * HITACHI VANTARA PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2024 Hitachi Vantara. All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Hitachi Vantara and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Hitachi Vantara and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Hitachi Vantara is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Hitachi Vantara,
 * explicitly covering such access.
 */

package com.pentaho.appshell.content;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.api.repository.IContentItem;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.services.solution.BaseContentGenerator;
import org.pentaho.platform.util.messages.LocaleHelper;

import java.io.OutputStream;
import java.io.Serializable;

public class AppShellContentGenerator extends BaseContentGenerator implements Serializable {
  private static final long serialVersionUID = 7346204723974033745L;
  private static final String MIMETYPE = "text/html";
  private static final Log logger = LogFactory.getLog( AppShellContentGenerator.class );

  @Override
  public void createContent() throws Exception {
    IContentItem contentItem = outputHandler.getOutputContentItem( "response", "content", instanceId, MIMETYPE );
    OutputStream out = contentItem.getOutputStream( itemName );

    try {
      IPluginResourceLoader resLoader = PentahoSystem.get( IPluginResourceLoader.class, null );
      String html = resLoader.getResourceAsString( AppShellContentGenerator.class, "webclient/index.html" );

      out.write( html.getBytes( LocaleHelper.getSystemEncoding() ) );
      out.flush();
    } catch ( Exception e ) {
      // mostly broken pipe exceptions. This is very likely to occur when the user drags a second field into the
      // report before the first field has returned data. The browser will close the connection on the first request
      // which will result in a ClientAbortException
      this.debug( "Error flushing Analyzer OutputStream", e );
    } finally {
      try {
        out.close();
      } catch ( Exception ignored ) {
        logger.debug( ignored.getMessage(), ignored );
      }
    }
  }

  @Override
  public Log getLogger() {
    return LogFactory.getLog( AppShellContentGenerator.class );
  }
}
