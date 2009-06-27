/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.views;

import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.jface.viewers.TreePath;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ContainerItemProvider<CONTAINER extends IContainer<Object>> extends ItemProvider<CONTAINER>
{
  private Map<Object, Node> nodes = new HashMap<Object, Node>();

  private Node root;

  private IElementFilter rootElementFilter;

  public ContainerItemProvider()
  {
  }

  public ContainerItemProvider(IElementFilter rootElementFilter)
  {
    this.rootElementFilter = rootElementFilter;
  }

  public IElementFilter getRootElementFilter()
  {
    return rootElementFilter;
  }

  @Override
  public boolean hasChildren(Object element)
  {
    try
    {
      Node node = getNode(element);
      return !node.getChildren().isEmpty();
    }
    catch (Exception ex)
    {
      return false;
    }
  }

  public Object[] getChildren(Object element)
  {
    try
    {
      Node node = getNode(element);
      List<Node> children = node.getChildren();
      for (Iterator<Node> it = children.iterator(); it.hasNext();)
      {
        Node child = it.next();
        if (child.isDisposed())
        {
          it.remove();
        }
        else
        {
          Object childElement = child.getElement();
          LifecycleState lifecycleState = LifecycleUtil.getLifecycleState(childElement);
          if (lifecycleState == LifecycleState.INACTIVE || lifecycleState == LifecycleState.DEACTIVATING)
          {
            handleInactiveElement(it, child);
          }
        }
      }

      Object[] result = new Object[children.size()];
      for (int i = 0; i < result.length; i++)
      {
        result[i] = children.get(i).getElement();
      }

      return result;
    }
    catch (RuntimeException ex)
    {
      return NO_ELEMENTS;
    }
  }

  public Object getParent(Object element)
  {
    try
    {
      Node node = getNode(element);
      Node parentNode = node.getParent();
      return parentNode == null ? null : parentNode.getElement();
    }
    catch (RuntimeException ex)
    {
      return null;
    }
  }

  @Override
  public void dispose()
  {
    super.dispose();
  }

  @Override
  protected void connectInput(CONTAINER input)
  {
    root = createNode(null, input);
    if (root != null)
    {
      addNode(input, root);
    }
  }

  @Override
  protected void disconnectInput(CONTAINER input)
  {
    root.dispose();
    root = null;
    nodes.clear();
  }

  /**
   * @since 2.0
   */
  protected void handleInactiveElement(Iterator<Node> it, Node child)
  {
    it.remove();
    child.dispose();
  }

  protected void elementAdded(Object element, Object parent)
  {
  }

  protected void elementRemoved(Object element, Object parent)
  {
  }

  protected Node getRoot()
  {
    return root;
  }

  protected Map<Object, Node> getNodes()
  {
    return nodes;
  }

  protected Node getNode(Object element)
  {
    Node node = root;
    if (element != getInput())
    {
      node = nodes.get(element);
    }

    if (node == null)
    {
      throw new IllegalStateException("No node for " + element); //$NON-NLS-1$
    }

    return node;
  }

  protected Node createNode(Node parent, Object element)
  {
    if (element instanceof IContainer<?>)
    {
      return createContaineNode(parent, element);
    }

    return createLeafNode(parent, element);
  }

  protected LeafNode createLeafNode(Node parent, Object element)
  {
    return new LeafNode(parent, element);
  }

  @SuppressWarnings("unchecked")
  protected ContainerNode createContaineNode(Node parent, Object element)
  {
    return new ContainerNode(parent, (IContainer<Object>)element);
  }

  protected void addNode(Object element, Node node)
  {
    nodes.put(element, node);
  }

  protected Node removeNode(Object element)
  {
    return nodes.remove(element);
  }

  protected boolean filterRootElement(Object element)
  {
    if (rootElementFilter != null)
    {
      return rootElementFilter.filter(element);
    }

    return true;
  }

  /**
   * @author Eike Stepper
   */
  public interface Node
  {
    public void dispose();

    public boolean isDisposed();

    public Object getElement();

    public Node getParent();

    public List<Node> getChildren();

    public TreePath getTreePath();
  }

  /**
   * @author Eike Stepper
   */
  public abstract class AbstractNode implements Node
  {
    private Node parent;

    private boolean disposed;

    public AbstractNode(Node parent)
    {
      this.parent = parent;
    }

    public void dispose()
    {
      if (!disposed)
      {
        removeNode(getElement());
        parent = null;
        disposed = true;
      }
    }

    public boolean isDisposed()
    {
      return disposed;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("{0}[{1}]", getClass().getSimpleName(), getElement()); //$NON-NLS-1$
    }

    public final Node getParent()
    {
      checkNotDisposed();
      return parent;
    }

    public TreePath getTreePath()
    {
      TreePath parentPath = parent == null ? TreePath.EMPTY : parent.getTreePath();
      return parentPath.createChildPath(getElement());
    }

    protected void checkNotDisposed()
    {
      if (disposed)
      {
        throw new IllegalStateException("Node is already disposed of"); //$NON-NLS-1$
      }
    }

    protected Node addChild(Collection<Node> children, Object element)
    {
      if (nodes.containsKey(element))
      {
        return null;
      }

      if (this != root || filterRootElement(element))
      {
        Node node = createNode(this, element);
        if (node != null)
        {
          addNode(element, node);
          children.add(node);
          return node;
        }
      }

      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public abstract class AbstractContainerNode extends AbstractNode
  {
    private List<Node> children;

    protected IListener containerListener = new ContainerEventAdapter<Object>()
    {
      @Override
      protected void onAdded(IContainer<Object> container, Object element)
      {
        AbstractContainerNode.this.onAdded(container, element);
      }

      @Override
      protected void onRemoved(IContainer<Object> container, Object element)
      {
        AbstractContainerNode.this.onRemoved(container, element);
      }

      @Override
      protected void notifyOtherEvent(IEvent event)
      {
        updateLabels(event.getSource());
      }
    };

    public AbstractContainerNode(Node parent)
    {
      super(parent);
    }

    @Override
    public void dispose()
    {
      if (!isDisposed())
      {
        if (children != null)
        {
          for (Node child : children)
          {
            child.dispose();
          }

          children.clear();
          children = null;
          containerListener = null;
        }

        super.dispose();
      }
    }

    public final List<Node> getChildren()
    {
      checkNotDisposed();
      if (children == null)
      {
        children = createChildren();
      }

      return children;
    }

    @SuppressWarnings("unchecked")
    public IContainer<Object> getContainer()
    {
      return (IContainer<Object>)getElement();
    }

    protected List<Node> createChildren()
    {
      Object[] elements = getContainer().getElements();
      List<Node> children = new ArrayList<Node>(elements.length);
      for (int i = 0; i < elements.length; i++)
      {
        Object element = elements[i];
        addChild(children, element);
      }

      getContainer().addListener(containerListener);
      return children;
    }

    protected void onAdded(IContainer<Object> container, Object element)
    {
      Node node = addChild(getChildren(), element);
      if (node != null)
      {
        refreshElement(container, true);
        revealElement(element);
        elementAdded(element, container);
      }
    }

    protected void onRemoved(IContainer<Object> container, Object element)
    {
      Node node = removeNode(element);
      if (node != null)
      {
        getChildren().remove(node);
        elementRemoved(element, container);

        Object rootElement = root.getElement();
        Object refreshElement = container == rootElement ? null : container;
        refreshElement(refreshElement, true);
        node.dispose();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public class ContainerNode extends AbstractContainerNode
  {
    private IContainer<Object> container;

    public ContainerNode(Node parent, IContainer<Object> container)
    {
      super(parent);
      this.container = container;
      if (container == null)
      {
        throw new IllegalArgumentException("container == null"); //$NON-NLS-1$
      }
    }

    @Override
    public void dispose()
    {
      if (!isDisposed())
      {
        container.removeListener(containerListener);
        container = null;
        super.dispose();
      }
    }

    public Object getElement()
    {
      return container;
    }
  }

  /**
   * @author Eike Stepper
   */
  public class LeafNode extends AbstractNode implements IListener
  {
    private Object element;

    public LeafNode(Node parent, Object element)
    {
      super(parent);
      this.element = element;
      EventUtil.addListener(element, this);
    }

    @Override
    public void dispose()
    {
      if (!isDisposed())
      {
        EventUtil.removeListener(element, this);
        element = null;
        super.dispose();
      }
    }

    public Object getElement()
    {
      checkNotDisposed();
      return element;
    }

    public List<Node> getChildren()
    {
      checkNotDisposed();
      return Collections.emptyList();
    }

    public void notifyEvent(IEvent event)
    {
      updateLabels(event.getSource());
    }
  }
}
