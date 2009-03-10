package org.eclipse.emf.cdo.common.model;

import org.eclipse.net4j.util.concurrent.Worker;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;

import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class CDOPackageRegistryPopulator extends Worker
{
  public static final int DEFAULT_SOURCE_POLL_INTERVAL = 5000;

  private long sourcePollInterval = DEFAULT_SOURCE_POLL_INTERVAL;

  private EPackage.Registry source;

  private CDOPackageRegistry target;

  public CDOPackageRegistryPopulator(EPackage.Registry source, CDOPackageRegistry target)
  {
    this.source = source;
    this.target = target;
  }

  public CDOPackageRegistryPopulator(CDOPackageRegistry target)
  {
    this(EPackage.Registry.INSTANCE, target);
  }

  public EPackage.Registry getSource()
  {
    return source;
  }

  public CDOPackageRegistry getTarget()
  {
    return target;
  }

  public long getSourcePollInterval()
  {
    return sourcePollInterval;
  }

  public void setSourcePollInterval(long sourcePollInterval)
  {
    this.sourcePollInterval = sourcePollInterval;
  }

  @Override
  protected void work(WorkContext context) throws Exception
  {
    doWork();
    context.nextWork(getSourcePollInterval());
  }

  protected void doWork()
  {
    populate(getSource(), getTarget());
  }

  @Override
  protected void doActivate() throws Exception
  {
    doWork();
    super.doActivate();
  }

  public static boolean populate(EPackage.Registry source, CDOPackageRegistry target)
  {
    boolean populated = false;
    while (populateFirstMatch(source, target))
    {
      populated = true;
    }

    return populated;
  }

  private static boolean populateFirstMatch(EPackage.Registry source, CDOPackageRegistry target)
  {
    for (Entry<String, Object> entry : source.entrySet())
    {
      String nsURI = entry.getKey();
      if (!target.containsKey(nsURI))
      {
        target.put(nsURI, new Descriptor(source, nsURI));
        return true;
      }
    }

    return false;
  }

  /**
   * @author Eike Stepper
   */
  public static class Descriptor implements EPackage.Descriptor
  {
    private EPackage.Registry source;

    private String nsURI;

    public Descriptor(EPackage.Registry source, String nsURI)
    {
      this.source = source;
      this.nsURI = nsURI;
    }

    public EPackage.Registry getSource()
    {
      return source;
    }

    public String getNsURI()
    {
      return nsURI;
    }

    public EFactory getEFactory()
    {
      return source.getEFactory(nsURI);
    }

    public EPackage getEPackage()
    {
      return source.getEPackage(nsURI);
    }
  }
}
