/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOAllRevisionsProvider;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil.AllRevisionsDumper;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.Worker;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class CDOServerBrowser extends Worker
{
  private static final String REQUEST_PREFIX = "GET ";

  private static final String REQUEST_SUFFIX = " HTTP/1.1";

  private ThreadLocal<Map<String, String>> params = new InheritableThreadLocal<Map<String, String>>()
  {
    @Override
    protected Map<String, String> initialValue()
    {
      return new HashMap<String, String>();
    }
  };

  private int port = 7777;

  private ServerSocket serverSocket;

  private Map<String, InternalRepository> repositories;

  private List<Page> pages = new ArrayList<Page>();

  public CDOServerBrowser(Map<String, InternalRepository> repositories)
  {
    this.repositories = repositories;
    setDaemon(true);
  }

  public Map<String, InternalRepository> getRepositories()
  {
    return repositories;
  }

  public int getPort()
  {
    return port;
  }

  public void setPort(int port)
  {
    this.port = port;
  }

  @Override
  protected void work(WorkContext context) throws Exception
  {
    Socket socket = null;

    try
    {
      socket = serverSocket.accept();
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      OutputStream out = new BufferedOutputStream(socket.getOutputStream());
      PrintStream pout = new PrintStream(out);
      printHeader(pout);

      String line;
      while ((line = in.readLine()) != null)
      {
        if (line.startsWith(REQUEST_PREFIX) && line.endsWith(REQUEST_SUFFIX))
        {
          String request = line.substring(REQUEST_PREFIX.length(), line.length() - REQUEST_SUFFIX.length()).trim();
          String resource = request;
          String params = "";
          int pos = request.indexOf('?');
          if (pos != -1)
          {
            resource = request.substring(0, pos);
            params = request.substring(pos + 1);
          }

          initParams(params);
          if ("/".equals(resource))
          {
            showMenu(pout);
          }
          else
          {
            String pageName = resource.substring(1);
            for (Page page : pages)
            {
              if (page.getName().equals(pageName))
              {
                showPage(pout, page);
              }
            }
          }
        }

        out.flush();
        return;
      }
    }
    catch (Exception ex)
    {
      if (isActive())
      {
        ex.printStackTrace();
      }
    }
    finally
    {
      params.remove();
      if (socket != null)
      {
        socket.close();
      }
    }
  }

  protected void initParams(String params)
  {
    Map<String, String> map = this.params.get();
    for (String param : params.split("&"))
    {
      if (param.length() != 0)
      {
        String[] keyValue = param.split("=");
        map.put(keyValue[0], keyValue[1]);
      }
    }
  }

  protected void clearParams()
  {
    Map<String, String> map = params.get();
    map.clear();
  }

  public void removeParam(String key)
  {
    Map<String, String> map = params.get();
    map.remove(key);
  }

  public String getParam(String key)
  {
    Map<String, String> map = params.get();
    return map.get(key);
  }

  public String href(String label, String resource, String... params)
  {
    Map<String, String> map = new HashMap<String, String>(this.params.get());
    for (int i = 0; i < params.length;)
    {
      map.put(params[i++], params[i++]);
    }

    List<String> list = new ArrayList<String>(map.keySet());
    Collections.sort(list);

    StringBuilder builder = new StringBuilder();
    for (String key : list)
    {
      String value = map.get(key);
      if (value != null)
      {
        if (builder.length() != 0)
        {
          builder.append("&");
        }

        builder.append(key);
        builder.append("=");
        builder.append(value);
      }
    }

    return "<a href=\"/" + escape(resource) + "?" + escape(builder.toString()) + "\">" + escape(label) + "</a>";
  }

  public String escape(String raw)
  {
    if (raw == null)
    {
      return "null";
    }

    return raw.replace("<", "&lt;");
  }

  protected void printHeader(PrintStream pout)
  {
    pout.print("HTTP/1.1 200 OK\r\n");
    pout.print("Content-Type: text/html\r\n");
    pout.print("Date: " + new Date() + "\r\n");
    pout.print("Server: DBBrowser 3.0\r\n");
    pout.print("\r\n");
  }

  protected void showMenu(PrintStream pout)
  {
    clearParams();
    pout.print("<h1>CDO Server Browser 4.0</h1><hr>\r\n");

    for (Page page : pages)
    {
      pout.println("<h3>" + href(page.getLabel(), page.getName()) + "</h3>");
    }
  }

  protected void showPage(PrintStream pout, Page page)
  {
    String repo = getParam("repo");

    List<String> repoNames = new ArrayList<String>(repositories.keySet());
    Collections.sort(repoNames);

    pout.print("<h3><a href=\"/\">" + page.getLabel() + "</a>:&nbsp;&nbsp;");
    for (String repoName : repoNames)
    {
      InternalRepository repository = repositories.get(repoName);
      if (!page.canDisplay(repository))
      {
        continue;
      }

      if (repo == null)
      {
        repo = repoName;
      }

      if (repoName.equals(repo))
      {
        pout.print("<b>" + escape(repoName) + "</b>&nbsp;&nbsp;");
      }
      else
      {
        pout.print(href(repoName, page.getName(), "repo", repoName) + "&nbsp;&nbsp;");
      }
    }

    pout.print("</h3>");

    InternalRepository repository = repositories.get(repo);
    if (repository != null)
    {
      pout.print("<p>\r\n");
      page.display(this, repository, pout);
    }
  }

  @Override
  protected String getThreadName()
  {
    return "DBBrowser";
  }

  protected void initPages(List<Page> pages)
  {
    pages.add(new PackagesPage());
    pages.add(new RevisionsPage.FromCache());
    pages.add(new RevisionsPage.FromStore());

    IPluginContainer container = IPluginContainer.INSTANCE;
    for (String factoryType : container.getFactoryTypes(Page.PRODUCT_GROUP))
    {
      Page page = (Page)container.getElement(Page.PRODUCT_GROUP, factoryType, null);
      pages.add(page);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    initPages(pages);

    try
    {
      serverSocket = new ServerSocket(port);
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Could not open socket on port " + port, ex);
    }

    super.doActivate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    serverSocket.close();
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  public static class ContainerBased extends CDOServerBrowser
  {
    private IContainer<?> container;

    private IListener containerListener = new ContainerEventAdapter<Object>()
    {
      @Override
      protected void onAdded(IContainer<Object> container, Object element)
      {
        addElement(element);
      }

      @Override
      protected void onRemoved(IContainer<Object> container, Object element)
      {
        removeElement(element);
      }
    };

    public ContainerBased(IContainer<?> container)
    {
      super(new HashMap<String, InternalRepository>());
      this.container = container;
    }

    public ContainerBased()
    {
      this(IPluginContainer.INSTANCE);
    }

    public IContainer<?> getContainer()
    {
      return container;
    }

    @Override
    protected void doActivate() throws Exception
    {
      super.doActivate();
      for (Object element : container.getElements())
      {
        addElement(element);
      }

      container.addListener(containerListener);
    }

    @Override
    protected void doDeactivate() throws Exception
    {
      container.removeListener(containerListener);
      super.doDeactivate();
    }

    private void addElement(Object element)
    {
      if (element instanceof InternalRepository)
      {
        InternalRepository repository = (InternalRepository)element;
        getRepositories().put(repository.getName(), repository);
      }
    }

    private void removeElement(Object element)
    {
      if (element instanceof InternalRepository)
      {
        InternalRepository repository = (InternalRepository)element;
        getRepositories().remove(repository.getName());
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends org.eclipse.net4j.util.factory.Factory
    {
      public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.browsers";

      public static final String TYPE = "default";

      public Factory()
      {
        super(PRODUCT_GROUP, TYPE);
      }

      public CDOServerBrowser.ContainerBased create(String description) throws ProductCreationException
      {
        CDOServerBrowser.ContainerBased browser = new CDOServerBrowser.ContainerBased();

        try
        {
          if (!StringUtil.isEmpty(description))
          {
            browser.setPort(Integer.valueOf(description));
          }
        }
        catch (Exception ex)
        {
          OM.LOG.warn(ex);
        }

        return browser;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static interface Page
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.browserPages";

    public String getName();

    public String getLabel();

    public boolean canDisplay(InternalRepository repository);

    public void display(CDOServerBrowser browser, InternalRepository repository, PrintStream out);
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class AbstractPage implements Page
  {
    private String name;

    private String label;

    public AbstractPage(String name, String label)
    {
      this.name = name;
      this.label = label;
    }

    public String getName()
    {
      return name;
    }

    public String getLabel()
    {
      return label;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class PackagesPage extends AbstractPage
  {
    public PackagesPage()
    {
      super("packages", "Packages and Classes");
    }

    public boolean canDisplay(InternalRepository repository)
    {
      return true;
    }

    public void display(CDOServerBrowser browser, InternalRepository repository, PrintStream out)
    {
      String param = browser.getParam("classifier");
      InternalCDOPackageRegistry packageRegistry = repository.getPackageRegistry(false);
      for (InternalCDOPackageUnit unit : packageRegistry.getPackageUnits())
      {
        param = showPackage(unit.getTopLevelPackageInfo(), packageRegistry, browser, param, out, "&nbsp;&nbsp;");
      }
    }

    protected String showPackage(InternalCDOPackageInfo info, InternalCDOPackageRegistry packageRegistry,
        CDOServerBrowser browser, String param, PrintStream out, String prefix)
    {
      EPackage ePackage = info.getEPackage();
      out.println("<h3>" + prefix + ePackage.getName() + "&nbsp;&nbsp;[" + ePackage.getNsURI() + "]</h3>");

      for (EClassifier classifier : ePackage.getEClassifiers())
      {
        String name = classifier.getName();
        if (param == null)
        {
          param = name;
        }

        String label = name.equals(param) ? name : browser.href(name, getName(), "classifier", name);
        out.print(prefix + "&nbsp;&nbsp;<b>" + label);

        if (classifier instanceof EEnum)
        {
          EEnum eenum = (EEnum)classifier;
          out.print("&nbsp;&nbsp;" + eenum.getELiterals());
        }
        else if (classifier instanceof EDataType)
        {
          EDataType eDataType = (EDataType)classifier;
          out.print("&nbsp;&nbsp;" + eDataType.getInstanceClassName());
        }

        out.println("</b><br>");
      }

      for (EPackage sub : ePackage.getESubpackages())
      {
        InternalCDOPackageInfo subInfo = packageRegistry.getPackageInfo(sub);
        param = showPackage(subInfo, packageRegistry, browser, param, out, prefix + "&nbsp;&nbsp;");
      }

      return param;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class RevisionsPage extends AbstractPage
  {
    public RevisionsPage(String name, String label)
    {
      super(name, label);
    }

    public void display(final CDOServerBrowser browser, InternalRepository repository, PrintStream out)
    {
      Map<CDOBranch, List<CDORevision>> allRevisions = getAllRevisions(repository);
      Map<CDOID, List<CDORevision>> ids = getAllIDs(allRevisions);

      out.print("<table border=\"0\">\r\n");
      out.print("<tr>\r\n");

      out.print("<td valign=\"top\">\r\n");
      out.print("<table border=\"1\" cellpadding=\"2\"><tr><td>\r\n");
      final String[] revision = { browser.getParam("revision") };
      new AllRevisionsDumper.Stream.Html(allRevisions, out)
      {
        private StringBuilder versionsBuilder;

        private CDORevision lastRevision;

        @Override
        protected void dumpEnd(List<CDOBranch> branches)
        {
          dumpLastRevision();
          super.dumpEnd(branches);
        }

        @Override
        protected void dumpBranch(CDOBranch branch)
        {
          dumpLastRevision();
          super.dumpBranch(branch);
        }

        @Override
        protected void dumpRevision(CDORevision rev)
        {
          CDOID id = rev.getID();
          if (lastRevision != null && !id.equals(lastRevision.getID()))
          {
            dumpLastRevision();
          }
          if (versionsBuilder == null)
          {
            versionsBuilder = new StringBuilder();
          }
          else
          {
            versionsBuilder.append(" ");
            if (versionsBuilder.length() > 64)
            {
              versionsBuilder.append("<br>");
            }
          }

          String key = CDORevisionUtil.formatRevisionKey(rev);
          if (revision[0] == null)
          {
            revision[0] = key;
          }

          String version = "v" + rev.getVersion();
          if (key.equals(revision[0]))
          {
            versionsBuilder.append("<b>" + version + "</b>");
          }
          else
          {
            versionsBuilder.append(browser.href(version, getName(), "revision", key));
          }

          lastRevision = rev;
        }

        protected void dumpLastRevision()
        {
          if (versionsBuilder != null)
          {
            PrintStream out = out();
            out.println("<tr>");
            out.println("<td>&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println(getCDOIDLabel(lastRevision));
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;</td>");

            out.println("<td>");
            out.println(versionsBuilder.toString());
            out.println("</td>");
            out.println("</tr>");

            lastRevision = null;
            versionsBuilder = null;
          }
        }
      }.dump();

      out.print("</td></tr></table></td>\r\n");
      out.print("<td>&nbsp;&nbsp;&nbsp;</td>\r\n");

      if (revision[0] != null)
      {
        out.print("<td valign=\"top\">\r\n");
        showRevision(out, browser, allRevisions, ids, revision[0], repository);
        out.print("</td>\r\n");
      }

      out.print("</tr>\r\n");
      out.print("</table>\r\n");
    }

    /**
     * @since 4.0
     */
    protected void showRevision(PrintStream pout, CDOServerBrowser browser,
        Map<CDOBranch, List<CDORevision>> allRevisions, Map<CDOID, List<CDORevision>> ids, String key,
        InternalRepository repository)
    {
      CDORevisionKey revisionKey = CDORevisionUtil.parseRevisionKey(key, repository.getBranchManager());
      for (CDORevision revision : allRevisions.get(revisionKey.getBranch()))
      {
        if (revision.getVersion() == revisionKey.getVersion() && revision.getID().equals(revisionKey.getID()))
        {
          showRevision(pout, browser, ids, (InternalCDORevision)revision);
          return;
        }
      }
    }

    /**
     * @since 4.0
     */
    protected void showRevision(PrintStream pout, CDOServerBrowser browser, Map<CDOID, List<CDORevision>> ids,
        InternalCDORevision revision)
    {
      String className = revision.getEClass().toString();
      className = className.substring(className.indexOf(' '));
      className = StringUtil.replace(className, new String[] { "(", ")", "," }, new String[] { "<br>", "", "<br>" });
      className = className.substring("<br>".length() + 1);

      pout.print("<table border=\"1\" cellpadding=\"2\">\r\n");
      showKeyValue(pout, true, "class", className);
      showKeyValue(pout, true, "id", getRevisionValue(revision.getID(), browser, ids, revision));
      showKeyValue(pout, true, "branch", revision.getBranch().getName() + "[" + revision.getBranch().getID() + "]");
      showKeyValue(pout, true, "version", revision.getVersion());
      showKeyValue(pout, true, "created", CDOCommonUtil.formatTimeStamp(revision.getTimeStamp()));
      showKeyValue(pout, true, "revised", CDOCommonUtil.formatTimeStamp(revision.getRevised()));
      showKeyValue(pout, true, "resource", getRevisionValue(revision.getResourceID(), browser, ids, revision));
      showKeyValue(pout, true, "container", getRevisionValue(revision.getContainerID(), browser, ids, revision));
      showKeyValue(pout, true, "feature", revision.getContainingFeatureID());

      for (EStructuralFeature feature : revision.getClassInfo().getAllPersistentFeatures())
      {
        Object value = revision.getValue(feature);
        showKeyValue(pout, false, feature.getName(), getRevisionValue(value, browser, ids, revision));
      }

      pout.print("</table>\r\n");
    }

    /**
     * @since 4.0
     */
    protected Object getRevisionValue(Object value, CDOServerBrowser browser, Map<CDOID, List<CDORevision>> ids,
        InternalCDORevision context)
    {
      if (value instanceof CDOID)
      {
        List<CDORevision> revisions = ids.get(value);
        if (revisions != null)
        {
          StringBuilder builder = new StringBuilder();
          builder.append(getCDOIDLabel(revisions.get(0)));

          if (browser != null)
          {
            builder.append("&nbsp;&nbsp;");
            for (CDORevision revision : revisions)
            {
              String label = "v" + revision.getVersion();
              String branchName = revision.getBranch().getName();
              if (!CDOBranch.MAIN_BRANCH_NAME.equals(branchName))
              {
                label += "[" + branchName + "]";
              }

              builder.append(" ");
              if (revision == context)
              {
                builder.append(label);
              }
              else
              {
                builder.append(browser.href(label, getName(), "revision", CDORevisionUtil.formatRevisionKey(revision)));
              }
            }
          }

          return builder.toString();
        }
      }

      if (value instanceof Collection)
      {
        StringBuilder builder = new StringBuilder();
        for (Object element : (Collection<?>)value)
        {
          builder.append(builder.length() == 0 ? "" : "<br>");
          builder.append(getRevisionValue(element, browser, ids, context));
        }

        return builder.toString();
      }

      return value;
    }

    /**
     * @since 4.0
     */
    protected void showKeyValue(PrintStream pout, boolean bg, String key, Object value)
    {
      String color = bg ? "EEEEEE" : "FFFFFF";
      pout.print("<tr bgcolor=\"" + color + "\">\r\n");
      pout.print("<td valign=\"top\"><b>" + key + "</b></td>\r\n");
      pout.print("<td valign=\"top\">");
      pout.print(value);
      pout.print("</td>\r\n");
      pout.print("</tr>\r\n");
    }

    protected abstract Map<CDOBranch, List<CDORevision>> getAllRevisions(InternalRepository repository);

    private Map<CDOID, List<CDORevision>> getAllIDs(Map<CDOBranch, List<CDORevision>> allRevisions)
    {
      Map<CDOID, List<CDORevision>> ids = new HashMap<CDOID, List<CDORevision>>();
      for (List<CDORevision> list : allRevisions.values())
      {
        for (CDORevision revision : list)
        {
          CDOID id = revision.getID();
          List<CDORevision> revisions = ids.get(id);
          if (revisions == null)
          {
            revisions = new ArrayList<CDORevision>();
            ids.put(id, revisions);
          }

          revisions.add(revision);
        }
      }

      return ids;
    }

    protected String getCDOIDLabel(CDORevision revision)
    {
      String label = revision.toString();
      return label.substring(0, label.indexOf(':'));
    }

    /**
     * @author Eike Stepper
     */
    public static class FromCache extends RevisionsPage
    {
      public FromCache()
      {
        super("crevisions", "Revisions From Cache");
      }

      public boolean canDisplay(InternalRepository repository)
      {
        return true;
      }

      @Override
      protected Map<CDOBranch, List<CDORevision>> getAllRevisions(InternalRepository repository)
      {
        return repository.getRevisionManager().getCache().getAllRevisions();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class FromStore extends RevisionsPage
    {
      public FromStore()
      {
        super("srevisions", "Revisions From Store");
      }

      public boolean canDisplay(InternalRepository repository)
      {
        return repository.getStore() instanceof CDOAllRevisionsProvider;
      }

      @Override
      protected Map<CDOBranch, List<CDORevision>> getAllRevisions(InternalRepository repository)
      {
        return ((CDOAllRevisionsProvider)repository.getStore()).getAllRevisions();
      }
    }
  }
}
