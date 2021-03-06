package net.sourceforge.pldoc.mojo;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.Locale;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.ResourceBundle;
import net.sourceforge.pldoc.Settings;
import net.sourceforge.pldoc.ant.PLDocTask;

import org.apache.tools.ant.BuildException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.plexus.util.StringUtils;

/**
 * Goal which touches a timestamp file.
 *
 * Here is a sample configuration for the plugin with the defaults:
 *             <plugin>
<groupId>net.sourceforge.pldoc</groupId>
<artifactId>maven-pldoc-plugin</artifactId>
<version>2.1-SNAPSHOT</version>
<configuration>
<applicationTitle>project-name</applicationTitle>
<sourceDirectory>src/sql</sourceDirectory>
<includes>*.sql</includes>
<namesCase>default</namesCase>
<inputEncoding>ISO-8859-15</inputEncoding>
<reportOutputDirectory>target/site/apidocs</reportOutputDirectory>
<destDir>sql-apidocs<destDir>
<showSkippedPackages>true</showSkippedPackages>
<ignoreInformalComments>true</ignoreInformalComments>
<driverName>oracle.jdbc.OracleDriver</driverName>
<getMetadataStatement>CallableStatement text</getMetadataStatement>
<getMetadataStatementReturnType>2005</getMetadataStatementReturnType>
<dbUrl>jdbc:oracle:thin:@//192.168.100.22:1521/orcl</dbUrl>
<dbUser>system</dbUser>
<dbPassword>oracle</dbPassword>
<inputTypes>PROCEDURE,FUNCTION,TRIGGER,PACKAGE,TYPE,PACKAGE BODY,TYPE BODY</inputTypes>
<inputObjects>ANONYMOUS.%,APEX_040000.%,APEX_PUBLIC_USER.%,APPQOSSYS.%,BI.%,CACHEADM.%,CTXSYS.%,DBSNMP.%,DEMO.%,DIP.%,EXFSYS.%,FLOWS_FILES.%,HR.%,HR1.%,HR_TRIG.%,IX.%,MDDATA.%,MDSYS.%,MGMT_VIEW.%,OBE.%,OE.%,OE1.%,OLAPSYS.%,ORACLE_OCM.%,ORDDATA.%,ORDPLUGINS.%,ORDSYS.%,OUTLN.%,OWBSYS.%,OWBSYS_AUDIT.%,PHPDEMO.%,PLS.%,PM.%,SCOTT.%,SH.%,SI_INFORMTN_SCHEMA.%,SPATIAL_CSW_ADMIN_USR.%,SPATIAL_WFS_ADMIN_USR.%,SYS.%,SYSMAN.%,SYSTEM.%,TIMESTEN.%,TTHR.%,WMSYS.%,XDB.%,XDBMETADATA.%,XDBPM.%,XFILES.%,XS$NULL.%</inputObjects>
</configuration>                    
</plugin>

 *
 * @goal pldoc
 * @phase pldoc
 * @execute phase="generate-sources"
 *
 */
public class PLDoc
        extends AbstractMojo
implements MavenReport{

    /**
     * Specifies the application title
     *
     * @parameter expression="${application.title}" default-value="${project.name}"
     * @required
     */
    private String applicationTitle;

    /**
     * The name of the destination subdirectory.
     * <br/>
     *
     * @since 2.1
     * @parameter expression="${destDir}" default-value="sql-apidocs"
     */
    private String destDir;

    /**
     * Specifies the File summarising the documented application.
     *
     *
     * @parameter expression="${overviewFile}"
     * @since 2.17 
     */
    protected File overviewFile;

    /**
     * Specifies the CSS stylesheet file to reference in the generated HTML files.
     *
     * @parameter expression="${stylesheet}"
     * @since 2.17
     */
    private String stylesheet;
    

    /**
     * Specifies the destination directory where pldoc saves the generated HTML files.
     *
     *
     * @parameter expression="${destDir}" alias="destDir" default-value="${project.build.directory}/sql-apidocs"
     * @required
     */
    protected File outputDirectory;

    /**
     * Specifies the destination directory where pldoc saves the generated HTML files.
     *
     * @parameter expression="${project.reporting.outputDirectory}/sql-apidocs"
     * @required
     */
    private File reportOutputDirectory;
    
    /**
     * Specifies the source directory
     *
     * @parameter expression="${sourceDirectory}" 
     */
    private File sourceDirectory;

    /**
     * Specifies the included files
     *
     * @parameter expression="${includes}" 
     */
    private String includes;

    /**
     * Specifies the character encoding of the input files
     *
     * @since 2.6
     * @parameter expression="${inputEncoding}" 
     */
    private String inputEncoding = System.getProperty("file.encoding");

    /**
     * Specifies the desired case of names 
     *
     * @since 2.10
     * @parameter expression="${namesCase}" 
     */
    private String namesCase = "default";

    /**
     * JDBC URL
     *
     * @since 2.1
     * @parameter expression="${dbUrl}" 
     */
    private String dbUrl ;
    /**
     * Database user name 
     *
     * @since 2.1
     * @parameter expression="${dbUser}" 
     */
    private String dbUser ;
    /**
     * Database user password
     *
     * @since 2.1
     * @parameter expression="${dbPassword}" 
     */
    private String dbPassword ;
    /**
     * Comma-separated list of input Object Types to process, for example: "PACKAGE,TYPE,FUNCTION,PROCEDURE,TRIGGER"
     *
     * @since 2.1
     * @parameter expression="${inputTypes}" default-value="PACKAGE,TYPE,FUNCTION,PROCEDURE,TRIGGER"
     */
    private String inputTypes ;
    /**
     * Comma-separated list of input Objects to process, for example "SCOTT.%,HR.%,SH.%"
     *
     * @since 2.1
     * @parameter expression="${inputObjects}" 
     */
    private String inputObjects ;
    /**
     * Display parsing errors for failed packages in Generator.html.
     *
     * @since 2.1
     * @parameter expression="${showSkippedPackages}" default-value="false"
     */
    private boolean showSkippedPackages ;

    /**
     * Class name of JDBC driver.
     *
     * @since 2.15
     * @parameter expression="${driverName}" 
     */
    private String driverName ;

    /**
     * Callable statement to retrieve object source.
     *
     * @since 2.15
     * @parameter expression="${getMetadataStatement}" 
     */
    private String getMetadataStatement ;

    /**
     * Return Type (see java.sql.Types).
     *
     * @since 2.15
     * @parameter expression="${getMetadataStatementReturnType}" 
     */
    private Integer getMetadataStatementReturnType ;


    /**
     * Ignore informal PL/SQL comments (--) when searching for PLDoc comments.
     *
     * @since 2.16
     * @parameter expression="${ignoreInformalComments}" default-value="false"
     */
    private boolean ignoreInformalComments ;

    /**
     * The Maven Project Object
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;


    /**
     * The name of the Javadoc report.
     *
     * @since 2.1
     * @parameter expression="${name}"
     */
    private String name;

    /**
     * The description of the Javadoc report.
     *
     * @since 2.1
     * @parameter expression="${description}"
     */
    private String description;


    /** {@inheritDoc} */

    public void execute()
            throws MojoExecutionException {
	
        try {
	    //RenderingContext = new RenderingContext (outputDirectory, getOutPutName + ".html" ); 
	    //SiteRendererSink = new SiteRendererSink (context);
	    Sink sink = null; 
	    Locale locale = Locale.getDefault();
            generate(sink, locale );
        } 
	catch (MavenReportException ex) {
            //throw new MavenReportException("Failed generating pldoc report",ex);
	    throw new MojoExecutionException( "An error has occurred in " + getName (Locale.ENGLISH ) + " report generation" , ex);
        }
	catch (RuntimeException ex) {
            //throw new MavenReportException("Failed generating pldoc report",ex);
	    throw new MojoExecutionException( "An error has occurred in " + getName (Locale.ENGLISH ) + " report generation" , ex);
        }


    }

    /** {@inheritDoc} 
        This implementation current ignore ignores both parameters  
    */

    public void generate(Sink sink, Locale locale) throws MavenReportException {

	outputDirectory = getReportOutputDirectory();
	getLog().debug( "outputDirectory=" + outputDirectory  ) ;
	getLog().debug( "destDir=" + destDir  ) ;
	getLog().debug( "reportOutputDirectory=" + reportOutputDirectory  ) ;
	getLog().debug( "applicationTitle=" + applicationTitle  ) ;
	getLog().debug( "sourceDirectory=" + sourceDirectory  ) ;
	getLog().debug( "includes=" + includes  ) ;
	getLog().debug( "inputEncoding=" + inputEncoding  ) ;
        getLog().debug( "overviewFile=" + overviewFile ) ;
        getLog().debug( "stylesheet=" + stylesheet  ) ;
	getLog().debug( "namesCase=" + namesCase  ) ;
	getLog().debug( "dbUrl=" + dbUrl  ) ;
	getLog().debug( "dbUser=" + dbUser  ) ;
	getLog().debug( "dbPassword=" + ((null == dbPassword) ? "undefined" : "defined" )   ) ;
	getLog().debug( "inputObjects=" + inputObjects  ) ;
	getLog().debug( "inputTypes=" + inputTypes  ) ;
	getLog().debug( "showSkippedPackages=" + showSkippedPackages ) ;
	getLog().debug( "ignoreInformalComments=" + ignoreInformalComments ) ;
	getLog().debug( "driverName=" + driverName ) ;
	getLog().debug( "getMetadataStatement=" + getMetadataStatement ) ;
	getLog().debug( "getMetadataStatementReturnType=" + getMetadataStatementReturnType ) ;

        try {
	    if (!outputDirectory.exists()) 
	    {
	      getLog().info( "Creating directory " + outputDirectory.toString()  ) ;
	      outputDirectory.mkdirs();
	    }
	    PLDocTask task = new PLDocTask();
	    task.init();
	    task.setDestdir(outputDirectory);
	    task.setDoctitle(applicationTitle);
	    task.setDbUrl(dbUrl); 
	    task.setDbUser(dbUser);
	    task.setDbPassword(dbPassword);
	    task.setInputObjects(inputObjects);
	    task.setInputTypes(inputTypes);
	    task.setInputEncoding(inputEncoding);
	    task.setShowSkippedPackages(showSkippedPackages);
	    task.setIgnoreInformalComments(ignoreInformalComments);
	    PLDocTask.NamesCase antNamesCase = new PLDocTask.NamesCase();
	    antNamesCase.setValue(namesCase);
	    task.setNamesCase(antNamesCase);

	    /* Set non-Oracle settings only if they are not null;
	     * otherwise, rely on the defaults  
	     */
            if (null != overviewFile) task.setOverview(overviewFile);
            if (null != stylesheet) task.setStylesheet(stylesheet);
	    if (null != driverName) task.setDriverName(driverName);
	    if (null != getMetadataStatement) task.setGetMetadataStatement(getMetadataStatement);
	    if (null != getMetadataStatementReturnType) task.setReturnType(getMetadataStatementReturnType);

	    if (null != sourceDirectory && null != includes)
	    {
	      FileSet fset = new FileSet();
	      fset.setDir(sourceDirectory);
	      fset.setIncludes(includes);
	      task.addFileset(fset);
	    }

	    Project proj = new Project();
	    proj.setBaseDir(outputDirectory);
	    proj.setName(applicationTitle);
	    task.setProject(proj);
	    task.execute();
        } 
	catch (BuildException ex) {
	  //Convert Ant Build Exception into expected Maven Exception 
	  throw new MavenReportException("Failed generating pldoc report",ex);
        }
	catch (RuntimeException ex) {
	  throw new MavenReportException("Failed generating pldoc report",ex);
        }
    }

    /** {@inheritDoc} */

    public String getOutputName() {
        return destDir + "/index";
    }

    /** {@inheritDoc} */

    public String getName(Locale locale) {
        if ( StringUtils.isEmpty( name ) )
        {
            return getBundle( locale ).getString( "report.pldoc.name" );
        }

        return name;
    }

    /** {@inheritDoc} */

    public String getCategoryName() {
        return CATEGORY_PROJECT_REPORTS;
    }

    /** {@inheritDoc} */

    public String getDescription(Locale locale) {
        if ( StringUtils.isEmpty( description ) )
        {
            return getBundle( locale ).getString( "report.pldoc.description" );
        }

        return description;
    }

    public void setDestDir(String destDir) {
      this.destDir = destDir; 

	getLog().debug( "setDestDir: param destDir=" + destDir  ) ;
	getLog().debug( "setDestDir: outputDirectory=" + this.outputDirectory  ) ;
	getLog().debug( "setDestDir: destDir=" + this.destDir  ) ;
	getLog().debug( "setDestDir: reportOutputDirectory=" + this.reportOutputDirectory  ) ;
      updateReportOutputDirectory(reportOutputDirectory, destDir) ;
    }


    public void setReportOutputDirectory(File reportOutputDirectory) {

	getLog().debug( "setReportOutPutDirectory: param reportOutputDirectory=" + reportOutputDirectory  ) ;
	getLog().debug( "setReportOutPutDirectory: outputDirectory=" + this.outputDirectory  ) ;
	getLog().debug( "setReportOutPutDirectory: destDir=" + this.destDir  ) ;
	getLog().debug( "setReportOutPutDirectory: reportOutputDirectory=" + this.reportOutputDirectory  ) ;
      updateReportOutputDirectory(reportOutputDirectory, destDir) ;
    }

    private void updateReportOutputDirectory(File reportOutputDirectory, String destDir) {
        if ( ( reportOutputDirectory != null ) 
	     && ( destDir != null ) 
	     && ( !reportOutputDirectory.getAbsolutePath().endsWith( destDir ) ) 
	   )
        {
            this.reportOutputDirectory = new File( reportOutputDirectory, destDir );
        }
        else
        {
            this.reportOutputDirectory = reportOutputDirectory;
        }
	getLog().debug( "updateReportOutPutDirectory: reportOutputDirectory=" + this.reportOutputDirectory  ) ;
    }

    /** {@inheritDoc} */

    public File getReportOutputDirectory() {
        if ( reportOutputDirectory == null )
        {
            return outputDirectory;
        }
        return reportOutputDirectory;
    }

    /** {@inheritDoc} */

    public boolean isExternalReport() {
         return true;
    }

    /** {@inheritDoc} */

    public boolean canGenerateReport() {
        return true;
    }

     /**
     * Gets the resource bundle for the specified locale.
     *
     * @param locale The locale of the currently generated report.
     * @return The resource bundle for the requested locale.
     */
    private ResourceBundle getBundle( Locale locale )
    {
        return ResourceBundle.getBundle( "pldoc-report", locale, getClass().getClassLoader() );
    }


}
